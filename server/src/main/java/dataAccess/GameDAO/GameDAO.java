package dataAccess.GameDAO;

import dataAccess.DataAccessException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
  Integer createGame(String gameName);
  GameData getGame(int gameID) throws DataAccessException;
  Collection<GameData> listGames() throws DataAccessException;
  void updateGame(GameData game);

  void deleteGames();
}
