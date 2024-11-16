package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.database.SQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTests {

  private GameDAO gameDAO;

  @BeforeEach
  public void setup() throws DataAccessException {
    gameDAO = new SQLGameDAO();
    gameDAO.deleteGames();
  }

  @Test
  public void deleteGamesSuccess() throws DataAccessException {
    int game = gameDAO.createGame("Game");
    gameDAO.deleteGames();
    assertNull(gameDAO.getGame(game));
  }

  @Test
  public void createGameSuccess() throws DataAccessException {
    int game = gameDAO.createGame("Game");
    GameData gameData = gameDAO.getGame(game);
    GameData newGameData = new GameData(game, null, null, "Game", new ChessGame());
    Assertions.assertEquals(newGameData, gameData);
  }

  @Test
  public void createGameFail() {
    assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
  }

  @Test
  public void getGameSuccess() throws DataAccessException {
    int game = gameDAO.createGame("Game");
    GameData gameData = gameDAO.getGame(game);
    GameData newGameData = new GameData(game, null, null, "Game", new ChessGame());
    Assertions.assertNotNull(gameData);
    Assertions.assertNotNull(newGameData);
    Assertions.assertEquals(newGameData, gameData);
  }

  @Test
  public void getGameFail() throws DataAccessException {
    assertNull(gameDAO.getGame(3));
  }

  @Test
  public void listGameSuccess() throws DataAccessException {
    gameDAO.createGame("Game1");
    gameDAO.createGame("Game2");
    gameDAO.createGame("Game3");
    Collection<GameData> gameList = gameDAO.listGames();
    Assertions.assertEquals(3, gameList.size());
  }

  @Test
  public void updateGameSuccess() throws DataAccessException {
    int game = gameDAO.createGame("Game");
    ChessGame newChessGame = new ChessGame();
    newChessGame.setBoard(new ChessBoard());

    gameDAO.updateGame(new GameData(game, null, null, "Game", newChessGame));
    GameData newGameData = new GameData(game, null, null, "Game", newChessGame);
    GameData gameData = gameDAO.getGame(game);

    Assertions.assertEquals(newGameData, gameData);
  }

  @Test
  public void updateGameFail() {
    assertThrows(DataAccessException.class, () -> gameDAO.updateGame(null));
  }
}