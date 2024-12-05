package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

public class UserGameCommandParams {
  private ChessMove move;

  public UserGameCommandParams(ChessMove move) {
    this.move = move;
  }

  public ChessMove getMove() {
    return move;
  }
}
