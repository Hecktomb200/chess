package websocket.messages;

public class Error extends ServerMessage {
  private String errorMessage;
  public Error(String error) {
    super(ServerMessageType.ERROR);
    errorMessage = error;
  }

  public String getErrorMessage() { return errorMessage; }
}
