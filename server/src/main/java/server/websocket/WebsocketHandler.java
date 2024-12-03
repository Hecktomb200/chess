package server.websocket;

import chess.ChessGame;
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

@WebSocket
public class WebsocketHandler {
  private final ConnectionManager connectionManager;
  private final AuthDAO authDAO;
  private final GameDAO gameDAO;
  private final GameService gameService;

  public WebsocketHandler(ConnectionManager connectionManager, GameService gameService, AuthDAO authDAO, GameDAO gameDAO) {
    this.connectionManager=connectionManager;
    this.gameService=gameService;
    this.authDAO = authDAO;
    this.gameDAO = gameDAO;
  }

  @OnWebSocketMessage
  public void onMessage(Session session, String message) throws IOException, DataAccessException {
    UserGameCommand command=new Gson().fromJson(message, UserGameCommand.class);
    switch (command.getCommandType()) {
      case CONNECT -> handleConnect(command, session);
      case MAKE_MOVE -> handleMakeMove(new Gson().fromJson(message, MoveCommand.class), session);
      case RESIGN -> handleResignPlayer(new Gson().fromJson(message, ResignCommand.class), session);
      case LEAVE -> handleLeaveGame(new Gson().fromJson(message, LeaveCommand.class), session);
    }
  }

  private void handleLeaveGame(LeaveCommand fromJson, Session session) {

  }

  private void handleResignPlayer(ResignCommand fromJson, Session session) {

  }

  private void handleMakeMove(MoveCommand fromJson, Session session) {

  }

  private void handleConnect(UserGameCommand command, Session session) throws IOException, DataAccessException {
    String role = command.getAuthToken();
    String playerColor = role.equals("player") ? "WHITE" : "BLACK";

    JoinGameRequest joinRequest = new JoinGameRequest(playerColor, command.getGameID());

    try {
      gameService.joinGame(joinRequest, command.getAuthToken());
    } catch (DataAccessException e) {
      sendResponse(new Error(e.getMessage()), session);
      return;
    }

    addSessionToGame(command.getGameID(), command.getAuthToken(), session);
    sendResponse(new LoadMessage(command.getGameID()), session);
    String notificationMessage = String.format("%s joined as a %s", command.getAuthToken(), role);
    notifyAllPlayers(command.getGameID(), new NotificationMessage(notificationMessage), command.getAuthToken());
  }

  private void notifyAllPlayers(Integer gameID, NotificationMessage notificationMessage, String authToken) {

  }

  private void addSessionToGame(Integer gameID, String authToken, Session session) {

  }

  private void sendResponse(ServerMessage message, Session session) throws IOException {
    if (session.isOpen()) {
      session.getRemote().sendString(new Gson().toJson(message));
    }
  }

}