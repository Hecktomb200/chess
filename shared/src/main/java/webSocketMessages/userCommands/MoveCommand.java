package webSocketMessages.userCommands;
import chess.ChessMove;

public class MoveCommand extends UserGameCommand{
    private Integer gameID;
    private ChessMove move;

    public MoveCommand(String authToken, Integer gI, ChessMove chessMove) {
        super(authToken);
        gameID = gI;
        move = chessMove;
        this.commandType = CommandType.MAKE_MOVE;
    }

    public Integer getGameID() { return gameID; }

    public ChessMove getMove() { return move; }
}
