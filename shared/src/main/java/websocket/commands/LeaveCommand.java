package websocket.commands;

public class LeaveCommand extends UserGameCommand{
  private Integer gameID;

  public LeaveCommand(String authToken, Integer gI) {
    super(CommandType.LEAVE, authToken, gI);
    this.gameID = gI;
  }

  public Integer getGameID() { return gameID; }

  public String getAuthString() {
    return getAuthToken();
  }
}
