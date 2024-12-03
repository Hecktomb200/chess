package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.commands.ConnectCommand;
import websocket.commands.MoveCommand;
import websocket.commands.ResignCommand;
import websocket.commands.LeaveCommand;

import java.io.IOException;

@WebSocket
public class WebsocketHandler {
  private final ConnectionManager connectionManager;
  private final GameService gameService;

  public WebsocketHandler(ConnectionManager connectionManager, GameService gameService) {
    this.connectionManager=connectionManager;
    this.gameService=gameService;
  }

  @OnWebSocketMessage
  public void onMessage(Session session, String message) throws IOException, DataAccessException {
    UserGameCommand command=new Gson().fromJson(message, UserGameCommand.class);
    switch (command.getCommandType()) {
      case CONNECT -> handleConnect(new Gson().fromJson(message, ConnectCommand.class), session);
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

  private void handleConnect(ConnectCommand fromJson, Session session) {

  }

}