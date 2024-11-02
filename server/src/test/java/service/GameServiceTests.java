package service;

import dataaccess.*;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.GameData;
import model.creategame.CreateGameRequest;
import model.creategame.CreateGameResult;
import model.joingame.JoinGameRequest;
import model.listgames.ListGamesRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;


public class GameServiceTests {

  @Test
  void createGameSuccess() throws DataAccessException {
    GameDAO gameDAO = new GameDAO();
    AuthDAO authDAO = new AuthDAO();
    GameService gameService = new GameService(authDAO, gameDAO);

    String authToken = authDAO.createAuth("Username");

    CreateGameRequest req = new CreateGameRequest("Game");
    CreateGameResult res = gameService.createGame(req, authToken);
    Assertions.assertNotNull(gameDAO.getGame(res.gameID()));

    gameDAO.deleteGames();
  }

  @Test
  void createGameFail() throws DataAccessException {
    GameDAO gameDAO = new GameDAO();
    AuthDAO authDAO = new AuthDAO();
    GameService gameService = new GameService(authDAO, gameDAO);

    String authToken = authDAO.createAuth("Username");

    String badAuthToken = UUID.randomUUID().toString();
    CreateGameRequest req = new CreateGameRequest("Game");
    Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(req, badAuthToken));

    CreateGameRequest newReq = new CreateGameRequest(null);
    Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(newReq, authToken));

    authDAO.deleteAllAuth();
  }

  @Test
  void listGamesTest() throws DataAccessException {
    GameDAO gameDAO = new GameDAO();
    AuthDAO authDAO = new AuthDAO();
    GameService gameService = new GameService(authDAO, gameDAO);

    String authToken = authDAO.createAuth("Username");
    gameDAO.createGame("Game1");
    gameDAO.createGame("Game2");
    gameDAO.createGame("Game3");

    ListGamesRequest request = new ListGamesRequest(authToken);
    var response = gameService.listGames(request);
    Assertions.assertEquals(3, response.games().size());

    gameDAO.deleteGames();
  }

  @Test
  void listGamesFail() throws DataAccessException {
    GameDAO gameDAO = new GameDAO();
    AuthDAO authDAO = new AuthDAO();
    GameService gameService = new GameService(authDAO, gameDAO);

    authDAO.createAuth("Username");

    ListGamesRequest req = new ListGamesRequest(UUID.randomUUID().toString());
    Assertions.assertThrows(DataAccessException.class, () -> gameService.listGames(req));

    gameDAO.deleteGames();
  }

  @Test
  void joinActualGame() throws DataAccessException {
    GameDAO gameDAO = new GameDAO();
    AuthDAO authDAO = new AuthDAO();
    GameService gameService = new GameService(authDAO, gameDAO);

    String authToken = authDAO.createAuth("Username");
    int gameID = gameDAO.createGame("Game");

    JoinGameRequest req = new JoinGameRequest("WHITE", gameID);
    gameService.joinGame(req, authToken);
    Assertions.assertEquals(new GameData(gameID, "Username", null,
            "Game", gameDAO.getGame(gameID).game()), gameDAO.getGame(gameID));

    gameDAO.deleteGames();
  }

  @Test
  void joinNonExistentGame() throws DataAccessException {
    GameDAO gameDAO = new GameDAO();
    AuthDAO authDAO = new AuthDAO();
    GameService gameService = new GameService(authDAO, gameDAO);

    String authToken = authDAO.createAuth("Username");

    int nonExistentGameID = 999999;

    JoinGameRequest req = new JoinGameRequest("WHITE", nonExistentGameID);

    Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(req, authToken));

    gameDAO.deleteGames();
  }
}
