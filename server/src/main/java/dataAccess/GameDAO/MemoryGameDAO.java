package dataAccess.GameDAO;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class MemoryGameDAO implements GameDAO {
  private final HashMap<Integer, GameData> games = new HashMap<>();
  private AtomicInteger gameIdGenerator = new AtomicInteger(0);


  @Override
  public Integer createGame(String gameName) {
    Integer gameID = (int) (System.currentTimeMillis() % 1000000) * 1000 + gameIdGenerator.incrementAndGet();
    games.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame()));
    return gameID;
  }

  @Override
  public GameData getGame(int gameID) {
    return games.get(gameID);
  }

  @Override
  public Collection<GameData> listGames() {
    var name = games.values().toArray(new GameData[0]);
    return games.values();
  }

  @Override
  public void updateGame(GameData game) {
    games.put(game.gameID(), game);
  }

  @Override
  public void deleteGames() {
    games.clear();
  }
}
