package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

public class UserGameCommandParams {
  private ChessGame.TeamColor playerColor;
  private String playerName;
  private boolean isObserver;
  private ChessMove move;

  public UserGameCommandParams(ChessGame.TeamColor playerColor, String playerName, boolean isObserver) {
    this.playerColor = playerColor;
    this.playerName = playerName;
    this.isObserver = isObserver;
  }

  public UserGameCommandParams(ChessMove move) {
    this.move = move;
  }

  public ChessGame.TeamColor getPlayerColor() {
    return playerColor;
  }

  public String getPlayerName() {
    return playerName;
  }

  public boolean isObserver() {
    return isObserver;
  }

  public ChessMove getMove() {
    return move;
  }
}
