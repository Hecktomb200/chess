package databaseTests;

import chess.ChessBoard;
import chess.ChessGame;
import dataAccess.GameDAO.GameDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO.SQLGameDatabase;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class SQLGameDatabaseTests {

    @Test
    void createGameSuccess() throws DataAccessException {
        GameDAO gameDAO = new SQLGameDatabase();

        int gameID = gameDAO.createGame("GoodGame");
        int gameID1 = gameDAO.createGame("GoodGame1");

        GameData retrievedData = gameDAO.getGame(gameID);
        GameData retrievedData1 = gameDAO.getGame(gameID1);

        GameData newGameData = new GameData(gameID, null, null, "GoodGame", new ChessGame());
        GameData newGameData1 = new GameData(gameID1, null, null, "GoodGame1", new ChessGame());

        Assertions.assertEquals(newGameData, retrievedData);
        Assertions.assertEquals(newGameData1, retrievedData1);

        gameDAO.deleteGames();
    }

    @Test
    void createGameFail() throws DataAccessException {
        GameDAO gameDAO = new SQLGameDatabase();

        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    @Test
    void getGameSuccess() throws DataAccessException {
        GameDAO gameDAO = new SQLGameDatabase();

        int gameID = gameDAO.createGame("GoodGame");
        int gameID1 = gameDAO.createGame("GoodGame1");

        GameData retrievedData = gameDAO.getGame(gameID);
        GameData retrievedData1 = gameDAO.getGame(gameID1);

        GameData newGameData = new GameData(gameID, null, null, "GoodGame", new ChessGame());
        GameData newGameData1 = new GameData(gameID1, null, null, "GoodGame1", new ChessGame());

        Assertions.assertEquals(newGameData, retrievedData);
        Assertions.assertEquals(newGameData1, retrievedData1);

        gameDAO.deleteGames();
    }

    @Test
    void getGameFail() throws DataAccessException {
        GameDAO gameDAO = new SQLGameDatabase();

        Assertions.assertNull(gameDAO.getGame(12345));
    }

    @Test
    void updateGameSuccess() throws DataAccessException {
        GameDAO gameDAO = new SQLGameDatabase();

        int gameID = gameDAO.createGame("GoodGame");
        int gameID1 = gameDAO.createGame("GoodGame1");

        ChessGame newChessGame = new ChessGame();
        newChessGame.setBoard(new ChessBoard());

        gameDAO.updateGame(new GameData(gameID, null, null, "GoodGame", newChessGame));

        GameData newGameData = new GameData(gameID, null, null, "GoodGame", newChessGame);
        GameData newGameData1 = new GameData(gameID1, null, null, "GoodGame1", new ChessGame());

        GameData retrievedData = gameDAO.getGame(gameID);
        GameData retrievedData1 = gameDAO.getGame(gameID1);

        Assertions.assertEquals(newGameData, retrievedData);
        Assertions.assertEquals(newGameData1, retrievedData1);

        gameDAO.deleteGames();
    }

    @Test
    void updateGameFail() throws DataAccessException {
        GameDAO gameDAO = new SQLGameDatabase();

        gameDAO.updateGame(new GameData(12345, null, null, "game", new ChessGame()));

        Assertions.assertNull(gameDAO.getGame(12345));
    }

    @Test
    void listGamesSuccess() throws DataAccessException {
        GameDAO gameDAO = new SQLGameDatabase();

        gameDAO.createGame("GoodGame");
        gameDAO.createGame("GoodGame1");
        gameDAO.createGame("GoodGame2");

        Collection<GameData> gameList = gameDAO.listGames();

        Assertions.assertEquals(3, gameList.size());

        gameDAO.deleteGames();
    }

    @Test
    void listGamesFail() throws DataAccessException {
        GameDAO gameDAO = new SQLGameDatabase();

        Assertions.assertEquals(0, gameDAO.listGames().size());
    }

    @Test
    void deleteAllGamesSuccess() throws DataAccessException {
        GameDAO gameDAO = new SQLGameDatabase();

        int gameID = gameDAO.createGame("GoodGame");
        int gameID1 = gameDAO.createGame("GoodGame1");

        gameDAO.deleteGames();

        Assertions.assertNull(gameDAO.getGame(gameID));
        Assertions.assertNull(gameDAO.getGame(gameID1));
    }
}
