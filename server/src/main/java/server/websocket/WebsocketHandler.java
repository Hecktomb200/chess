package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.joingame.JoinGameRequest;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.commands.ConnectCommand;
import websocket.commands.MoveCommand;
import websocket.commands.ResignCommand;
import websocket.commands.LeaveCommand;
import websocket.messages.LoadMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocket.messages.Error;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@WebSocket
public class WebsocketHandler {
  private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Session>> sessionRegistry = new ConcurrentHashMap<>();
  private final ArrayList<Integer> resignedGameIDs = new ArrayList<>();
  private final ConnectionManager connectionManager;
  private final AuthDAO authDAO;
  private final GameDAO gameDAO;
  private final GameService gameService;
  private static final Logger logger = Logger.getLogger(WebsocketHandler.class.getName());
  public WebsocketHandler(ConnectionManager connectionManager, GameService gameService, AuthDAO authDAO, GameDAO gameDAO) {
    this.connectionManager=connectionManager;
    this.gameService=gameService;
    this.authDAO = authDAO;
    this.gameDAO = gameDAO;
  }

  @OnWebSocketMessage
  public void onMessage(Session session, String message) throws IOException, DataAccessException, InvalidMoveException {
    logger.info("Received message: " + message);
    UserGameCommand command=new Gson().fromJson(message, UserGameCommand.class);
    switch (command.getCommandType()) {
      case CONNECT -> handleConnect(new Gson().fromJson(message, ConnectCommand.class), session);
      case MAKE_MOVE -> handleMakeMove(new Gson().fromJson(message, MoveCommand.class), session);
      case RESIGN -> handleResign(new Gson().fromJson(message, ResignCommand.class), session);
      case LEAVE -> handleLeaveGame(new Gson().fromJson(message, LeaveCommand.class), session);
    }
  }

  private void handleLeaveGame(LeaveCommand leaveCommand, Session session) throws IOException, DataAccessException {
    String response = gameService.leave(leaveCommand);
    if (handleError(response, session)) {
      return;
    }
    removeSessionFromGame(leaveCommand.getGameID(), leaveCommand.getAuthString(), session);
    String notificationMessage = String.format("%s has left the game.", response);
    notifyAllPlayers(leaveCommand.getGameID(), new NotificationMessage(notificationMessage), leaveCommand.getAuthString());
  }

  private boolean handleError(String result, Session session) throws IOException {
    if (result.contains("Error")) {
      sendResponse(new Error(result), session);
      return true;
    }
    return false;
  }

  private void removeSessionFromGame(Integer gameID, String authToken, Session session) {
    ConcurrentHashMap<String, Session> authMap = sessionRegistry.get(gameID);
    if (authMap != null) {
      authMap.remove(authToken, session);
    }
  }

  private void handleResign(ResignCommand resignCommand, Session session) throws IOException, DataAccessException {
    String result = gameService.resign(resignCommand);
    if (handleError(result, session)) {
      return;
    }
    if (isGameResigned(resignCommand.getGameID())) {
      sendResponse(new Error("Another player has already resigned"), session);
      return;
    }
    markGameAsResigned(resignCommand.getGameID());
    String notificationMessage = String.format("%s has resigned", result);
    notifyAllPlayers(resignCommand.getGameID(), new NotificationMessage(notificationMessage), null);
  }

  private boolean isGameResigned(Integer gameID) {
    return resignedGameIDs.contains(gameID);
  }

  private void markGameAsResigned(Integer gameID) {
    resignedGameIDs.add(gameID);
  }

  public void clearResigned() {
    resignedGameIDs.clear();
  }

  private void handleMakeMove(MoveCommand moveCommand, Session session) throws IOException, DataAccessException {
    if (isGameResigned(moveCommand.getGameID())) {
      sendResponse(new Error("A player has already resigned"), session);
      return;
    }
    String response = gameService.makeMove(moveCommand);
    if (response.contains("Error")) {
      sendResponse(new Error(response), session);
      return;
    }
    sendResponse(new LoadMessage(moveCommand.getGameID()), session);
    notifyAllPlayers(moveCommand.getGameID(), new LoadMessage(moveCommand.getGameID()), moveCommand.getAuthString());

    String[] responseParts = response.split(",");
    if (Objects.equals(responseParts[1], "checkmate")) {
      notifyAllPlayers(moveCommand.getGameID(), new NotificationMessage(String.format("%s is in checkmate! Game over!", responseParts[2])), "null");
    } else if (Objects.equals(responseParts[1], "check")) {
      notifyAllPlayers(moveCommand.getGameID(), new NotificationMessage(String.format("%s is in check!", responseParts[2])), "null");
    }
    String moveNotification = String.format("%s moved the piece from %s to %s", responseParts[0],
            moveCommand.getMove().getStartPosition().toString(), moveCommand.getMove().getEndPosition().toString());
    notifyAllPlayers(moveCommand.getGameID(), new NotificationMessage(moveNotification), moveCommand.getAuthString());
  }

  private void handleConnect(ConnectCommand command, Session session) throws IOException {

    try {
      gameService.connect(command);
    } catch (DataAccessException e) {
      sendResponse(new Error(e.getMessage()), session);
      return;
    }

    var playerColor = command.getPlayerColor().toString();
    if (playerColor == null) {
      playerColor = "observer";
    }

    addSessionToGame(command.getGameID(), command.getAuthToken(), session);
    sendResponse(new LoadMessage(command.getGameID()), session);
    String notificationMessage = String.format("%s joined as %s", command.getPlayerName(), command.getPlayerColor());
    notifyAllPlayers(command.getGameID(), new NotificationMessage(notificationMessage), command.getAuthToken());
  }

  private void notifyAllPlayers(Integer gameID, ServerMessage message, String exceptThisAuthToken) throws IOException {
    ConcurrentHashMap<String, Session> gameSessions = sessionRegistry.get(gameID);
    for (Map.Entry<String, Session> entry : gameSessions.entrySet()) {
      if (!Objects.equals(entry.getKey(), exceptThisAuthToken) && entry.getValue().isOpen()) {
        entry.getValue().getRemote().sendString(new Gson().toJson(message));
      }
    }
  }

  private void addSessionToGame(Integer gameID, String authToken, Session session) {
    sessionRegistry.computeIfAbsent(gameID, k -> new ConcurrentHashMap<>()).put(authToken, session);
  }

  private void sendResponse(ServerMessage message, Session session) throws IOException {
    if (session.isOpen()) {
      String jsonMessage = new Gson().toJson(message);
      logger.info("Sending message: " + jsonMessage);
      session.getRemote().sendString(jsonMessage);
    }
  }

}