package websocket;

import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketFacade extends Endpoint{
  private Session session;

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
  public void onOpen(Session session, EndpointConfig endpointConfig) {

  }

  @OnMessage
  public void onMessage(String message) {
    ServerMessage serverMessage = parseServerMessage(message);
    if (serverMessage != null) {
      handleServerMessage(serverMessage);
    }
  }

  private void handleServerMessage(ServerMessage serverMessage) {

  }

  private ServerMessage parseServerMessage(String message) {
    return null;
  }
}