package webSocketMessages.userCommands;
import chess.ChessGame;
public class JoinPlayerCommand extends UserGameCommand {
    private final Integer gameID;
    private final ChessGame.TeamColor playerColor;
    private final String playerName;

    public JoinPlayerCommand(String authToken, Integer gI, ChessGame.TeamColor currentPlayerColor, String n) {
        super(authToken);
        gameID = gI;
        playerColor = currentPlayerColor;
        playerName = n;
        this.commandType = CommandType.JOIN_PLAYER;
    }

    public Integer getGameID() {return gameID;}

    public ChessGame.TeamColor getPlayerColor() {return playerColor;}

    public String getPlayerName() {
        return playerName;
    }
}
