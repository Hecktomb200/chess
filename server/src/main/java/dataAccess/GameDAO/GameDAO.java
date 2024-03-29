package dataAccess.GameDAO;

import dataAccess.DataAccessException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
  Integer createGame(String gameName) throws DataAccessException;
  GameData getGame(int gameID) throws DataAccessException;
  Collection<GameData> listGames() throws DataAccessException;
  void updateGame(GameData game) throws DataAccessException;

  void deleteGames();
}
