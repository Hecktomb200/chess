package websocket.commands;

public class ResignCommand extends UserGameCommand {
  public ResignCommand(String authToken, Integer gameID) {
    super(CommandType.RESIGN, authToken, gameID);
  }

  public Integer getGameID() {
    return super.getGameID();
  }

  public String getAuthString() {
    return getAuthString();
  }
}