package websocket.commands;
import chess.ChessMove;

public class MoveCommand extends UserGameCommand {
  private ChessMove move;

  public MoveCommand(String authToken, Integer gameID, ChessMove chessMove) {
    super(CommandType.MAKE_MOVE, authToken, gameID);
    this.move = chessMove;
  }

  public String getAuthString() {
    return getAuthToken();
  }

  public Integer getGameID() {
    return super.getGameID();
  }

  public ChessMove getMove() {
    return move;
  }

}