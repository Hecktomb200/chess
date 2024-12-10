package websocket;

import websocket.messages.LoadMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.Error;
public interface NotificationHandler {
  void updateGame(LoadMessage loadMessage) throws Exception;
  void notify(NotificationMessage notificationMessage);
  void error(Error errorMessage);


}
