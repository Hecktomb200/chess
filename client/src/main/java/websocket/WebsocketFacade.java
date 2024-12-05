package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
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
      URI socketURI=new URI(url.replace("http", "ws") + "/connect");
      WebSocketContainer container=ContainerProvider.getWebSocketContainer();
      this.session=container.connectToServer(this, socketURI);
    } catch (DeploymentException | IOException | URISyntaxException ex) {
      throw new IOException(ex.getMessage(), ex);
    }
  }

  @Override
  public void onOpen(Session session, EndpointConfig endpointConfig) {}

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
    sendCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
  }
  private void sendCommand(UserGameCommand.CommandType commandType, String authToken, Integer gameID) throws IOException {
    try {
      UserGameCommand command = new UserGameCommand(commandType, authToken, gameID);
      this.session.getBasicRemote().sendText(new Gson().toJson(command));
    } catch (IOException ex) {
      handleException(ex);
    }
  }

  private void handleException(IOException ex) throws IOException {
    logger.log(Level.SEVERE, "IOException occurred: " + ex.getMessage(), ex);
    throw new IOException("Error during WebSocket operation: " + ex.getMessage(), ex);
  }


  private ServerMessage parseServerMessage(String message) {
    return null;
  }
}