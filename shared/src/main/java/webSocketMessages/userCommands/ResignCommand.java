package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand{
    private Integer gameID;

    public ResignCommand(String authToken, Integer gameIdentifier) {
        super(authToken);
        gameID = gameIdentifier;
        this.commandType = CommandType.RESIGN;
    }

    public Integer getGameID() { return gameID; }
}
