package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import websocket.commands.UserGameCommand;
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

      this.session.addMessageHandler(new MessageHandler.Whole<String>() {
        @Override
        public void onMessage (String message){
        logger.info("Received message: " + message);
        ServerMessage serverMessage=parseServerMessage(message);
        if (serverMessage != null) {
          handleServerMessage(serverMessage);
        }
      }
      });
    } catch (DeploymentException | IOException | URISyntaxException ex) {
      throw new IOException(ex.getMessage(), ex);
    }
  }

  @Override
  public void onOpen(Session session, EndpointConfig endpointConfig) {
    //logger.info("WebSocket connection opened.");
  }

  private void handleServerMessage(ServerMessage serverMessage) {
    switch (serverMessage.getServerMessageType()) {
      case LOAD_GAME -> handleLoadGame((LoadMessage) serverMessage);
      case NOTIFICATION -> handleNotification(((NotificationMessage) serverMessage).getMessage());
      case ERROR -> handleError(((Error)serverMessage).getErrorMessage());
    }
  }

  private void handleLoadGame(LoadMessage loadMessage) {
    //System.out.println("Game loaded: " + loadMessage);
    System.out.println("Game has been updated");
  }

  private void handleNotification(String message) {
    System.out.println("Notification: " + message);
  }

  private void handleError(String errorMessage) {
    System.out.println(errorMessage);
  }

  public void connect(String authToken, Integer gameID, String playerColor, String playerName) throws IOException {
    sendCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, playerColor, playerName, null);
  }

  public void makeMove(String authToken, Integer gameID, ChessMove move) throws IOException {
    sendCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken,gameID, null, null, move);
  }

  public void leave(String authToken, Integer gameID) throws IOException {
    sendCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID, null, null, null);
  }

  public void resign(String authToken, Integer gameID) throws IOException {
    sendCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID, null, null, null);
  }

  private void sendCommand(UserGameCommand.CommandType commandType, String authToken, Integer gameID,
                           String playerColor, String playerName, ChessMove move) throws IOException {
    try {
      UserGameCommand command = new UserGameCommand(commandType, authToken, gameID);

      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("commandType", command.getCommandType().name());
      jsonObject.addProperty("authToken", command.getAuthToken());
      jsonObject.addProperty("gameID", command.getGameID());
      jsonObject.addProperty("playerColor", playerColor);
      jsonObject.addProperty("playerName", playerName);

      if (commandType == UserGameCommand.CommandType.MAKE_MOVE && move != null) {
        JsonObject moveJson = new JsonObject();
        moveJson.addProperty("startRow", move.getStartPosition().getRow());
        moveJson.addProperty("startColumn", move.getStartPosition().getColumn());
        moveJson.addProperty("endRow", move.getEndPosition().getRow());
        moveJson.addProperty("endColumn", move.getEndPosition().getColumn());
        jsonObject.add("move", moveJson);
      }

      String messageToSend = new Gson().toJson(jsonObject);
      //logger.info("Sending message: " + messageToSend);
      if (this.session.isOpen()) {
        this.session.getBasicRemote().sendText(new Gson().toJson(jsonObject));
      } else {
        logger.severe("WebSocket session is not open.");
      }
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