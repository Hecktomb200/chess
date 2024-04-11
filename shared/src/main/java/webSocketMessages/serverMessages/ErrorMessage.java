package webSocketMessages.serverMessages;

public class ErrorMessage extends ServerMessage {
    private String errorMessage;
    public ErrorMessage(String error) {
        super(ServerMessageType.ERROR);
        errorMessage = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
