package clientTests;

import client.ResponseException;
import model.GameData;
import model.createGame.CreateGameResult;
import model.listGames.ListGamesResult;
import model.login.LoginResult;
import model.register.RegisterResult;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import java.util.Collection;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServerFacadeTests {

    private Server server;
    private ServerFacade serverFacade;

    @BeforeAll
    void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    void registerPositive() throws Exception {
        var authData = serverFacade.register("GoodUsername", "GoodPassword", "GoodEmail@email.com");
        Assertions.assertTrue(authData.authToken().length() > 10);
        serverFacade.delete();
    }

    @Test
    void registerNegative() throws Exception {
        Assertions.assertThrows(ResponseException.class,
                () -> serverFacade.register("BadUsername", null, "BadEmail@email.com"));
        serverFacade.delete();
    }

    @Test
    void loginPositive() throws Exception {
        serverFacade.register("GoodUsername", "GoodPassword", "GoodEmail@email.com");
        var loginData = serverFacade.login("goodUsername", "GoodPassword");
        Assertions.assertTrue(loginData.authToken().length() > 10);
        serverFacade.delete();
    }

    @Test
    void loginNegative() throws Exception {
        serverFacade.register("BadUsername", "BadPassword", "pL@email.com");
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(null, "BadPassword"));
        serverFacade.delete();
    }

    @Test
    void logoutPositive() throws Exception {
        RegisterResult registerData = serverFacade.register("GoodUsername", "GoodPassword", "GoodEmail@email.com");
        serverFacade.logout(registerData.authToken());
        LoginResult loginData = serverFacade.login("GoodUsername", "GoodPassword");
        Assertions.assertNotEquals(registerData.authToken(), loginData.authToken());
        serverFacade.delete();
    }

    @Test
    void logoutNegative() throws Exception {
        serverFacade.register("BadUsername", "BadPassword", "BadEmailO@email.com");
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.logout(UUID.randomUUID().toString()));
        serverFacade.delete();
    }

    @Test
    void listGamesPositive() throws Exception {
        RegisterResult registerData = serverFacade.register("GoodUsername", "GoodPassword", "GoodEmail@email.com");
        serverFacade.createGame("listGames0", registerData.authToken());
        serverFacade.createGame("listGames1", registerData.authToken());
        serverFacade.createGame("listGames2", registerData.authToken());
        ListGamesResult listGamesData = serverFacade.listGames(registerData.authToken());
        Assertions.assertEquals(3, listGamesData.games().size());
        serverFacade.delete();
    }

    @Test
    void listGamesNegative() throws Exception {
        RegisterResult registerData = serverFacade.register("playerListGames", "passwordListGames", "pLGs@email.com");
        serverFacade.createGame("gameName0", registerData.authToken());
        serverFacade.createGame("gameName1", registerData.authToken());
        serverFacade.createGame("gameName2", registerData.authToken());
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.listGames(UUID.randomUUID().toString()));
        serverFacade.delete();
    }


    @Test
    void createGamePositive() throws Exception {
        RegisterResult registerData = serverFacade.register("GoodUsername", "GoodPassword", "GoodEmail@email.com");
        CreateGameResult createGameData = serverFacade.createGame("GoodGame", registerData.authToken());
        Assertions.assertTrue(createGameData.gameID() > 0);
        serverFacade.delete();
    }

    @Test
    void createGameNegative() throws Exception {
        RegisterResult registerData = serverFacade.register("BadUsername", "BadPassword", "BadEmail@email.com");
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.createGame(null, registerData.authToken()));
        serverFacade.delete();
    }

    @Test
    void joinGamePositive() throws Exception {
        RegisterResult registerData = serverFacade.register("GoodUsername", "GoodPassword", "GoodEmail@email.com");
        CreateGameResult createGameData = serverFacade.createGame("joinGame", registerData.authToken());
        serverFacade.joinGame(registerData.authToken(), "white", createGameData.gameID());
        ListGamesResult listGamesData = serverFacade.listGames(registerData.authToken());
        Collection<GameData> games = listGamesData.games();
        for(GameData game : games) {
            Assertions.assertEquals("GoodUsername", game.whiteUsername());
        }
        serverFacade.delete();
    }

    @Test
    void joinGameNegative() throws Exception {
        RegisterResult registerData = serverFacade.register("BadUsername", "BadPassword", "BadEmail@email.com");
        CreateGameResult createGameData = serverFacade.createGame("gameName", registerData.authToken());
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.joinGame(UUID.randomUUID().toString(),
                "WHITE", createGameData.gameID()));
        serverFacade.delete();
    }

}
