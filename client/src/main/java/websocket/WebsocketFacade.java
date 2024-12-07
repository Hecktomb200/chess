package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.DataAccessException;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommandParams;
import websocket.messages.LoadMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocket.messages.Error;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebsocketFacade extends Endpoint{
  private Session session;
  private static final Logger logger = Logger.getLogger(WebsocketFacade.class.getName());

  public WebsocketFacade(String url) throws IOException {
    try {
      URI socketURI=new URI(url.replace("http", "ws") + "/ws");
      WebSocketContainer container=ContainerProvider.getWebSocketContainer();
      this.session=container.connectToServer(this, socketURI);
    } catch (DeploymentException | IOException | URISyntaxException ex) {
      throw new IOException(ex.getMessage(), ex);
    }
  }

  @Override
  public void onOpen(Session session, EndpointConfig endpointConfig) {
    logger.info("WebSocket connection opened.");
  }

  @OnMessage
  public void onMessage(String message) {
    ServerMessage serverMessage = parseServerMessage(message);
    if (serverMessage != null) {
      handleServerMessage(serverMessage);
    }
  }

  private void handleServerMessage(ServerMessage serverMessage) {
    switch (serverMessage.getServerMessageType()) {
      case LOAD_GAME -> handleLoadGame((LoadMessage) serverMessage);
      case NOTIFICATION -> handleNotification(((NotificationMessage) serverMessage).getMessage());
      case ERROR -> handleError(((Error)serverMessage).getErrorMessage());
    }
  }

  private void handleLoadGame(LoadMessage loadMessage) {
    logger.info("Game loaded: " + loadMessage);
  }

  private void handleNotification(String message) {
    logger.info("Notification: " + message);
  }

  private void handleError(String errorMessage) {
    logger.severe("Error: " + errorMessage);
  }

  public void connect(String authToken, Integer gameID) throws IOException {
    sendCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, null);
  }

  public void makeMove(String authToken, Integer gameID, ChessMove move) throws IOException, DataAccessException {
    sendCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken,gameID,move);
  }

  public void leave(String authToken, Integer gameID) throws IOException {
    sendCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID, null);
  }

  public void resign(String authToken, Integer gameID) throws IOException {
    sendCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID, null);
  }

  private void sendCommand(UserGameCommand.CommandType commandType, String authToken, Integer gameID, ChessMove move) throws IOException {
    try {
      UserGameCommand command = new UserGameCommand(commandType, authToken, gameID);

      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("commandType", command.getCommandType().name());
      jsonObject.addProperty("authToken", command.getAuthToken());
      jsonObject.addProperty("gameID", command.getGameID());

      if (commandType == UserGameCommand.CommandType.MAKE_MOVE && move != null) {
        jsonObject.addProperty("move", move.toString());
      }

      this.session.getBasicRemote().sendText(new Gson().toJson(jsonObject));
    } catch (IOException ex) {
      handleException(ex);
    }
  }

  private void handleException(IOException ex) throws IOException {
    logger.log(Level.SEVERE, "IOException occurred: " + ex.getMessage(), ex);
    throw new IOException("Error during WebSocket operation: " + ex.getMessage(), ex);
  }


  private ServerMessage parseServerMessage(String jsonMessage) {
    JsonObject jsonObject = JsonParser.parseString(jsonMessage).getAsJsonObject();
    String type = jsonObject.get("serverMessageType").getAsString();
    return switch (type) {
      case "LOAD_GAME" -> new Gson().fromJson(jsonMessage, LoadMessage.class);
      case "NOTIFICATION" -> new Gson().fromJson(jsonMessage, NotificationMessage.class);
      case "ERROR" -> new Gson().fromJson(jsonMessage, Error.class);
      default -> null;
    };
  }
}