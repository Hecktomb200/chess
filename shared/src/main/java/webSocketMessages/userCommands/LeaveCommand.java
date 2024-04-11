package webSocketMessages.userCommands;

public class LeaveCommand extends UserGameCommand{
    private final Integer gameID;

    public LeaveCommand(String authToken, Integer gI) {
        super(authToken);
        gameID = gI;
        this.commandType = CommandType.LEAVE;
    }

    public Integer getGameID() { return gameID; }
}
