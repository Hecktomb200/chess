package websocket.messages;

import websocket.messages.ServerMessage;

public class NotificationMessage extends ServerMessage {
  private final String message;
  public NotificationMessage(String notification) {
    super(ServerMessageType.NOTIFICATION);
    message = notification;
  }

  public String getMessage() {
    return message;
  }
}
