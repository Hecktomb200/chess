package dataAccess.GameDAO;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
  Integer createGame(String gameName);
  GameData getGame(int gameID);
  Collection<GameData> listGames();
  void updateGame(GameData game);

  void deleteGames();
}
