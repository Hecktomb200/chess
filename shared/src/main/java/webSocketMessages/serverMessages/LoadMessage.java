package webSocketMessages.serverMessages;

public class LoadMessage extends ServerMessage{
    private final Integer game;

    public LoadMessage(Integer gameID) {
        super(ServerMessageType.LOAD_GAME);
        game = gameID;
    }

    public Integer getGame() {
        return game;
    }
}
