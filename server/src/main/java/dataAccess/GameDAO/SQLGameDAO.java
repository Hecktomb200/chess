package dataAccess.GameDAO;

import model.GameData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public interface SQLGameDAO {
  Integer createGame(String gameName);
  GameData getGame(int gameID);
  Collection<GameData> listGames();
  void updateGame(GameData game);

  void deleteGames();
}
