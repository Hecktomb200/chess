package serviceTests;

import dataAccess.*;
import dataAccess.AuthDAO.MemoryAuthDAO;
import dataAccess.AuthDAO.SQLAuthDAO;
import dataAccess.GameDAO.MemoryGameDAO;
import dataAccess.GameDAO.SQLGameDAO;
import model.GameData;
import model.createGame.CreateGameRequest;
import model.createGame.CreateGameResult;
import model.joinGame.JoinGameRequest;
import model.listGames.ListGamesRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.GamesService;

import java.util.Random;
import java.util.UUID;


public class GameServiceTests {

    @Test
    void createGamesTestPositive() throws DataAccessException {
        SQLGameDAO gameDAO = new MemoryGameDAO();
        SQLAuthDAO authDAO = new MemoryAuthDAO();
        GamesService gamesService = new GamesService(authDAO, gameDAO);

        String authToken = authDAO.createAuth("GoodUsername");

        CreateGameRequest req = new CreateGameRequest("GoodGameName");
        CreateGameResult res = gamesService.createGame(req, authToken);
        Assertions.assertNotNull(gameDAO.getGame(res.gameID()));

        gameDAO.deleteGames();
    }

    @Test
    void createGamesTestNegative() {
        SQLGameDAO gameDAO = new MemoryGameDAO();
        SQLAuthDAO authDAO = new MemoryAuthDAO();
        GamesService gamesService = new GamesService(authDAO, gameDAO);

        String authToken = authDAO.createAuth("BadUsername");

        String badAuthToken = UUID.randomUUID().toString();
        CreateGameRequest req = new CreateGameRequest("BadGameName");
        Assertions.assertThrows(DataAccessException.class, () -> gamesService.createGame(req, badAuthToken));

        CreateGameRequest newReq = new CreateGameRequest(null);
        Assertions.assertThrows(DataAccessException.class, () -> gamesService.createGame(newReq, authToken));

        authDAO.deleteAuthTotal();
    }

    @Test
    void listGamesTestPositive() throws DataAccessException {
        SQLGameDAO gameDAO = new MemoryGameDAO();
        SQLAuthDAO authDAO = new MemoryAuthDAO();
        GamesService gamesService = new GamesService(authDAO, gameDAO);

        String authToken = authDAO.createAuth("GoodUsername");
        gameDAO.createGame("GoodGame1");
        gameDAO.createGame("GoodGame2");
        gameDAO.createGame("GoodGame3");
        gameDAO.createGame("GoodGame4");

        ListGamesRequest request = new ListGamesRequest(authToken);
        var response = gamesService.listGames(request);
        Assertions.assertEquals(4, response.games().size());

        gameDAO.deleteGames();
    }

    @Test
    void listGamesServiceNegative() {
        SQLGameDAO gameDAO = new MemoryGameDAO();
        SQLAuthDAO authDAO = new MemoryAuthDAO();
        GamesService gamesService = new GamesService(authDAO, gameDAO);

        authDAO.createAuth("BadUsername");

        ListGamesRequest req = new ListGamesRequest(UUID.randomUUID().toString());
        Assertions.assertThrows(DataAccessException.class, () -> gamesService.listGames(req));

        gameDAO.deleteGames();
    }

    @Test
    void joinGameServicePositive() throws DataAccessException {
        SQLGameDAO gameDAO = new MemoryGameDAO();
        SQLAuthDAO authDAO = new MemoryAuthDAO();
        GamesService gamesService = new GamesService(authDAO, gameDAO);

        String authToken = authDAO.createAuth("GoodUsername");
        int gameID = gameDAO.createGame("GoodGame");

        JoinGameRequest req = new JoinGameRequest("WHITE", gameID);
        gamesService.joinGame(req, authToken);
        Assertions.assertEquals(new GameData(gameID, "GoodUsername", null,
                "GoodGame", gameDAO.getGame(gameID).game()), gameDAO.getGame(gameID));

        gameDAO.deleteGames();
    }

    @Test
    void joinGameServiceNegative() throws DataAccessException {
        SQLGameDAO gameDAO = new MemoryGameDAO();
        SQLAuthDAO authDAO = new MemoryAuthDAO();
        GamesService gamesService = new GamesService(authDAO, gameDAO);

        String authToken = authDAO.createAuth("BadUsername");
        int gameID = gameDAO.createGame("BadGame");

        JoinGameRequest req = new JoinGameRequest("WHITE", gameID);
        gamesService.joinGame(req, authToken);
        Assertions.assertThrows(DataAccessException.class, () -> gamesService.joinGame(req, authToken));

        Random random = new Random();
        JoinGameRequest newReq = new JoinGameRequest("WHITE", random.nextInt(10000));
        Assertions.assertThrows(DataAccessException.class, () -> gamesService.joinGame(newReq, authToken));

        gameDAO.deleteGames();
    }




}
