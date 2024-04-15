package webSocketMessages.serverMessages;

public class Error extends ServerMessage {
    private String errorMessage;
    public Error(String error) {
        super(ServerMessageType.ERROR);
        errorMessage = error;
    }

    public String getErrorMessage() { return errorMessage; }
}
