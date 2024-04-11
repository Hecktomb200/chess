package webSocketMessages.serverMessages;

public class LoadMessage extends ServerMessage{
    private final Integer load;

    public LoadMessage(Integer gameID) {
        super(ServerMessageType.LOAD_GAME);
        load = gameID;
    }

    public Integer getGame() {
        return load;
    }
}
