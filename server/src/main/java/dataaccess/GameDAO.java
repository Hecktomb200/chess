package dataaccess;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class GameDAO {
  private final HashMap<Integer, GameData> games = new HashMap<>();
  private AtomicInteger gameIdGenerator = new AtomicInteger(0);

  public Integer createGame(String gameName) throws DataAccessException {
    if (gameName == null || gameName.isEmpty()) {
      throw new DataAccessException("Game name cannot be null or empty");
    }
    Integer gameID = Math.abs(UUID.randomUUID().hashCode());
    games.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame()));
    return gameID;
  }

  public GameData getGame(int gameID) throws DataAccessException {
    return games.get(gameID);
  }

  public Collection<GameData> listGames() throws DataAccessException {
    var name = games.values().toArray(new GameData[0]);
    return games.values();
  }

  public void updateGame(GameData game) throws DataAccessException {
    if (game == null) {
      throw new DataAccessException("Game data cannot be null");
    }
    if (!games.containsKey(game.gameID())) {
      throw new DataAccessException("Game not found");
    }
    games.put(game.gameID(), game);
  }

  public void deleteGames() throws DataAccessException {
    games.clear();
  }
}