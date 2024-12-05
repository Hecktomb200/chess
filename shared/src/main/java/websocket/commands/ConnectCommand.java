package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand {
  private final ChessGame.TeamColor playerColor;
  private final String playerName;

  public ConnectCommand(String authToken, Integer gameID, ChessGame.TeamColor playerColor, String playerName) {
    super(CommandType.CONNECT, authToken, gameID);
    this.playerColor = playerColor;
    this.playerName = playerName;
  }

  public ConnectCommand(String authToken, Integer gameID) {
    super(CommandType.CONNECT, authToken, gameID);
    this.playerColor = null;
    this.playerName = null;
  }

  public Integer getGameID() {
    return super.getGameID();
  }

  public ChessGame.TeamColor getPlayerColor() {
    return playerColor;
  }

  public String getPlayerName() {
    return playerName;
  }

  public boolean isObserver() {
    return playerColor == null;
  }
}