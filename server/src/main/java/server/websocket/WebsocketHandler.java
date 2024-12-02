package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebsocketHandler {
  private static final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Session>> sessionMap=new ConcurrentHashMap<>();
  private static final ArrayList<Integer> resignedGames=new ArrayList<>();
  private final GameService gameService;

  public WebsocketHandler(GameService game) {
    this.gameService=game;
  }

  @OnWebSocketMessage
  public void onMessage(Session session, String message) throws IOException, DataAccessException {
    UserGameCommand command=new Gson().fromJson(message, UserGameCommand.class);
    handleCommand(command, session);
  }

  private void handleCommand(UserGameCommand command, Session session) throws IOException, DataAccessException {
    switch (command.getCommandType()) {
      case CONNECT -> processConnect(command, session);
      case MAKE_MOVE -> processMakeMove(command, session);
      case RESIGN -> processResign(command, session);
      case LEAVE -> processLeave(command, session);
      default -> sendError("Unknown command", session);
    }
  }

  private void sendError(String unknown_command, Session session) {

  }

  private void processLeave(UserGameCommand command, Session session) {

  }

  private void processResign(UserGameCommand command, Session session) {

  }

  private void processMakeMove(UserGameCommand command, Session session) {

  }

  private void processConnect(UserGameCommand command, Session session) {

  }
}