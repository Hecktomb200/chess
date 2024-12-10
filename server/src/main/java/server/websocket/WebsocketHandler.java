package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
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

import javax.management.Notification;
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
  private ChessGame chessGame;
  private static final Logger logger = Logger.getLogger(WebsocketHandler.class.getName());
  public WebsocketHandler(ConnectionManager connectionManager, GameService gameService, AuthDAO authDAO, GameDAO gameDAO) {
    this.connectionManager=connectionManager;
    this.gameService=gameService;
    this.chessGame = new ChessGame();
    this.authDAO = authDAO;
    this.gameDAO = gameDAO;
  }

  @OnWebSocketMessage
  public void onMessage(Session session, String message) throws IOException, DataAccessException, InvalidMoveException {
    //logger.info("Received message: " + message);
    Map<String, Object> commandData = new Gson().fromJson(message, Map.class);
    String commandType = (String) commandData.get("commandType");

    switch (commandType) {
      case "CONNECT":
        handleConnect(commandData, session);
        break;
      case "MAKE_MOVE":
        handleMakeMove(message, session);
        break;
      case "RESIGN":
        handleResign(commandData, session);
        break;
      case "LEAVE":
        handleLeaveGame(commandData, session);
        break;
      default:
        logger.warning("Unknown command type: " + commandType);
        break;
    }
  }

  private void handleLeaveGame(Map<String, Object> leaveCommand, Session session) throws IOException, DataAccessException {
    Integer gameID = ((Double) leaveCommand.get("gameID")).intValue();
    String authToken = (String) leaveCommand.get("authToken");

    try {
      String response=gameService.leave(leaveCommand);

      removeSessionFromGame(gameID, authToken, session);
      String notificationMessage=String.format("%s has left the game.", response);
      notifyAllPlayers(gameID, new NotificationMessage(notificationMessage), authToken);
    } catch (IOException e) {
      sendResponse(new Error(e.getMessage()), session);
    }
  }

  private void removeSessionFromGame(Integer gameID, String authToken, Session session) {
    ConcurrentHashMap<String, Session> authMap = sessionRegistry.get(gameID);
    if (authMap != null) {
      authMap.remove(authToken, session);
    }
  }

  private void handleResign(Map<String, Object> resignCommand, Session session) throws IOException, DataAccessException {
    Integer gameID = ((Double) resignCommand.get("gameID")).intValue();
    String authToken = (String) resignCommand.get("authToken");

    try {
      if (isGameResigned(gameID)) {
        throw new IOException("Another player has already resigned");
      }
      gameService.resign(resignCommand);
      markGameAsResigned(gameID);

      String notificationMessage = String.format("%s has resigned", authToken);
      notifyAllPlayers(gameID, new NotificationMessage(notificationMessage), null);
    } catch (IOException e) {
      sendResponse(new Error(e.getMessage()), session);
    }
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

  @Override
  public String toString() {
    return super.toString();
  }

  private void handleMakeMove(String move, Session session) throws IOException, DataAccessException {
    MoveCommand moveCommand = new Gson().fromJson(move, MoveCommand.class);
    Integer gameID = moveCommand.getGameID();
    String authToken = moveCommand.getAuthToken();
    ChessPosition startPosition = moveCommand.getMove().getStartPosition();
    ChessPosition endPosition = moveCommand.getMove().getEndPosition();
    ChessPiece.PieceType promotionPiece = moveCommand.getMove().getPromotionPiece();

    try {
      AuthData authData = authDAO.getAuth(authToken);
      GameData gameData = gameDAO.getGame(gameID);

      validateAuth(authData, gameData, authToken, gameID);

      if (isGameResigned(gameID)) {
        throw new InvalidMoveException("Error: a player has already resigned");
      }

      chessGame = gameData.game();
      if((chessGame.getTeamTurn() == ChessGame.TeamColor.WHITE
              && !Objects.equals(gameData.whiteUsername(), authData.username())) ||
              (chessGame.getTeamTurn() == ChessGame.TeamColor.BLACK
                      && !Objects.equals(gameData.blackUsername(), authData.username()))) {
        throw new InvalidMoveException("Error: not current player's turn");
      }
      ChessMove chessMove = new ChessMove(startPosition, endPosition, promotionPiece);
      chessGame.makeMove(chessMove);

      GameData newGame = new GameData(gameData.gameID(), gameData.whiteUsername(),
              gameData.blackUsername(), gameData.gameName(), chessGame);
      gameDAO.updateGame(newGame);

      String checkResponse = gameService.doCheck(newGame, chessGame);
      if (!checkResponse.equals("null")) {
        String[] responseParts = checkResponse.split(",");
        if (Objects.equals(responseParts[0], "checkmate")) {
          notifyAllPlayers(gameID, new NotificationMessage(String.format("%s is in checkmate! Game over!", responseParts[1])), "null");
        } else if (Objects.equals(responseParts[0], "check")) {
          notifyAllPlayers(gameID, new NotificationMessage(String.format("%s is in check!", responseParts[1])), "null");
        }
      }

      String moveNotification = String.format("%s moved the piece from %s to %s",
              authData.username(), chessMove.getStartPosition(), chessMove.getEndPosition());
      notifyAllPlayers(gameID , new NotificationMessage(moveNotification), authToken);
      notifyAllPlayers(gameID, new LoadMessage(chessGame), authToken);
      sendResponse(new LoadMessage(chessGame), session);
    } catch (InvalidMoveException e) {
      sendResponse(new Error(e.getMessage()), session);
    } catch (DataAccessException e) {
      sendResponse(new Error(e.getMessage()), session);
    }
  }

  private void validateAuth(AuthData authData, GameData gameData, String authToken, int gameID) throws DataAccessException {
    if (authData == null) {
      throw new DataAccessException("Error: bad auth token");
    }

    if (!authData.authToken().equals(authToken)) {
      throw new DataAccessException("Error: bad auth token");
    }

    if (gameData == null) {
      throw new DataAccessException("Error: incorrect gameID");
    }

  }

  private void handleConnect(Map<String, Object> command, Session session) throws IOException {
    try {
      String playerColor = (String) command.get("playerColor");
      gameService.connect(command);

      Integer gameID = ((Double) command.get("gameID")).intValue();
      String authToken = (String) command.get("authToken");
      addSessionToGame(gameID, authToken, session);
      String notificationMessage = (playerColor == null)
              ? String.format("%s joined as an observer", command.get("playerName"))
              : String.format("%s joined as %s", command.get("playerName"), playerColor);
      notifyAllPlayers(gameID, new NotificationMessage(notificationMessage), authToken);
      //notifyAllPlayers(gameID, new LoadMessage(chessGame), authToken);
      sendResponse(new LoadMessage(chessGame), session);
    } catch (DataAccessException e) {
      sendResponse(new Error(e.getMessage()), session);
    }
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