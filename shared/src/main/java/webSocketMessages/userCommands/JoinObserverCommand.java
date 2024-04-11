package webSocketMessages.userCommands;

public class JoinObserverCommand extends UserGameCommand{
    private Integer gameID;

    public JoinObserverCommand(String authToken, Integer gameIdentifier) {
        super(authToken);
        gameID = gameIdentifier;
        this.commandType = CommandType.JOIN_OBSERVER;
    }

    public Integer getGameID() {return gameID;}
}
