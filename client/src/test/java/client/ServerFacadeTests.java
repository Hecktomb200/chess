package client;


import model.GameData;
import model.creategame.CreateGameResult;
import model.listgames.ListGamesResult;
import model.login.LoginResult;
import model.register.RegisterResult;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServerFacadeTests {

    private Server server;
    private ServerFacade serverFacade;
    private String authToken;
    private static final String USERNAME = "TestUser";
    private static final String PASSWORD = "TestPassword123";
    private static final String EMAIL = "Email@example.com";
    private static final String GAMENAME = "Game";

    @BeforeAll
    void init() {
        server=new Server();
        var port=server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade=new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    void clear() throws URISyntaxException, IOException {
        serverFacade.delete();
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
    void registerSuccess() throws Exception {
        var authData=serverFacade.registerUser(USERNAME, PASSWORD, EMAIL);
        Assertions.assertNotNull(authData);
        Assertions.assertTrue(authData.authToken().length() > 10);
        serverFacade.delete();
    }

    @Test
    void registerFail() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> serverFacade.registerUser(USERNAME, null, EMAIL));
        serverFacade.delete();
    }

    @Test
    void loginSuccess() throws Exception {
        serverFacade.registerUser(USERNAME, PASSWORD, EMAIL);
        var loginData = serverFacade.loginUser(USERNAME, PASSWORD);
        Assertions.assertNotNull(loginData);
        Assertions.assertTrue(loginData.authToken().length() > 10);
        Assertions.assertEquals(USERNAME, loginData.username());
        serverFacade.delete();
    }

    @Test
    void loginFail() throws Exception {
        serverFacade.registerUser(USERNAME, PASSWORD, EMAIL);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> serverFacade.loginUser(null, PASSWORD));
        serverFacade.delete();
    }

    @Test
    void logoutSuccess() throws Exception {
        RegisterResult registerData = serverFacade.registerUser(USERNAME, PASSWORD, EMAIL);
        serverFacade.logoutUser(registerData.authToken());
        LoginResult loginData = serverFacade.loginUser(USERNAME, PASSWORD);
        Assertions.assertNotNull(loginData.authToken());
        Assertions.assertNotEquals(registerData.authToken(), loginData.authToken());
        Assertions.assertEquals(USERNAME, loginData.username());
        serverFacade.delete();
    }

    @Test
    void logoutFail() throws Exception {
        serverFacade.registerUser(USERNAME, PASSWORD, "EmailO@email.com");
        Assertions.assertThrows(IOException.class,
                () -> serverFacade.logoutUser(UUID.randomUUID().toString()));
        serverFacade.delete();
    }

    @Test
    void listGamesSuccess() throws Exception {
        RegisterResult registerData = serverFacade.registerUser(USERNAME, PASSWORD, EMAIL);
        serverFacade.createGame(GAMENAME, registerData.authToken());
        serverFacade.createGame("Game1", registerData.authToken());
        ListGamesResult listGamesData = serverFacade.listGames(registerData.authToken());
        Assertions.assertNotNull(listGamesData.games().size());
        Assertions.assertEquals(2, listGamesData.games().size());
        serverFacade.delete();
    }

    @Test
    void listGamesFail() throws Exception {
        RegisterResult registerData = serverFacade.registerUser(USERNAME, PASSWORD, EMAIL);
        serverFacade.createGame(GAMENAME, registerData.authToken());
        serverFacade.createGame("Game1", registerData.authToken());
        Assertions.assertThrows(IOException.class, () -> serverFacade.listGames(UUID.randomUUID().toString()));
        serverFacade.delete();
    }

    @Test
    void createGameSuccess() throws Exception {
        RegisterResult registerData = serverFacade.registerUser(USERNAME, PASSWORD, EMAIL);
        CreateGameResult createGameData = serverFacade.createGame(GAMENAME, registerData.authToken());
        Assertions.assertTrue(createGameData.gameID() > 0);
        Assertions.assertNotNull(createGameData.gameID());
        serverFacade.delete();
    }

    @Test
    void createGameFail() throws Exception {
        RegisterResult registerData = serverFacade.registerUser(USERNAME, PASSWORD, EMAIL);
        Assertions.assertThrows(IllegalArgumentException.class, () -> serverFacade.createGame(null, registerData.authToken()));
        serverFacade.delete();
    }

    @Test
    void joinGameSuccess() throws Exception {
        RegisterResult registerData = serverFacade.registerUser(USERNAME, PASSWORD, EMAIL);
        CreateGameResult createGameData = serverFacade.createGame(GAMENAME, registerData.authToken());
        serverFacade.joinGame(registerData.authToken(), "white", createGameData.gameID());
        ListGamesResult listGamesData = serverFacade.listGames(registerData.authToken());
        Collection<GameData> games = listGamesData.games();
        for(GameData game : games) {
            Assertions.assertEquals(USERNAME, game.whiteUsername());
        }
        serverFacade.delete();
    }

    @Test
    void joinGameFail() throws Exception {
        RegisterResult registerData = serverFacade.registerUser(USERNAME, PASSWORD, EMAIL);
        CreateGameResult createGameData = serverFacade.createGame(GAMENAME, registerData.authToken());
        Assertions.assertThrows(IOException.class, () -> serverFacade.joinGame(UUID.randomUUID().toString(),
                "WHITE", createGameData.gameID()));
        serverFacade.delete();
    }

}