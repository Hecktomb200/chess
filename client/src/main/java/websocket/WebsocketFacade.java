package websocket;

import websocket.messages.LoadMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocket.messages.Error;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

  private ServerMessage parseServerMessage(String message) {
    return null;
  }
}