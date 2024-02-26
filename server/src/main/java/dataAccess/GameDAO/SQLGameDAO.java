package dataAccess.GameDAO;

import model.GameData;

import java.lang.reflect.Array;
import java.util.ArrayList;

public interface SQLGameDAO {
  Integer createGame(String gameName);
  GameData getGame(int gameID);
  ArrayList<GameData> listGames();
  void updateGame(GameData game);

  void deleteGames();
}
