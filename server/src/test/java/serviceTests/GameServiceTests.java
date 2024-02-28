package serviceTests;

import dataAccess.*;
import dataAccess.AuthDAO.MemoryAuthDAO;
import dataAccess.AuthDAO.SQLAuthDAO;
import dataAccess.GameDAO.MemoryGameDAO;
import dataAccess.GameDAO.SQLGameDAO;
import model.createGame.CreateGameRequest;
import model.createGame.CreateGameResult;
import model.listGames.ListGamesRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.GamesService;

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
    void listGamesServiceErrors() {
        SQLGameDAO gameDAO = new MemoryGameDAO();
        SQLAuthDAO authDAO = new MemoryAuthDAO();
        GamesService gamesService = new GamesService(authDAO, gameDAO);

        authDAO.createAuth("BadUsername");

        ListGamesRequest req = new ListGamesRequest(UUID.randomUUID().toString());
        Assertions.assertThrows(DataAccessException.class, () -> gamesService.listGames(req));

        gameDAO.deleteGames();
    }



}
