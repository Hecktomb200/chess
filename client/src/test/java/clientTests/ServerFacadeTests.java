package clientTests;

import model.createGame.CreateGameResult;
import model.listGames.ListGamesResult;
import model.login.LoginResult;
import model.register.RegisterResult;
import org.junit.jupiter.api.*;
import server.Server;
import client.ServerFacade;

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
    void loginPositive() throws Exception {
        serverFacade.register("GoodUsername", "GoodPassword", "GoodEmail@email.com");
        var loginData = serverFacade.login("goodUsername", "GoodPassword");
        Assertions.assertTrue(loginData.authToken().length() > 10);
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
    void listGamesSuccess() throws Exception {
        RegisterResult registerData = serverFacade.register("GoodUsername", "GoodPassword", "GoodEmail@email.com");
        serverFacade.createGame("listGames0", registerData.authToken());
        serverFacade.createGame("listGames1", registerData.authToken());
        serverFacade.createGame("listGames2", registerData.authToken());
        ListGamesResult listGamesData = serverFacade.listGames(registerData.authToken());
        Assertions.assertEquals(3, listGamesData.games().size());
        serverFacade.delete();
    }


    @Test
    void createGameSuccess() throws Exception {
        RegisterResult registerData = serverFacade.register("GoodUsername", "GoodPassword", "GoodEmail@email.com");
        CreateGameResult createGameData = serverFacade.createGame("GoodGame", registerData.authToken());
        Assertions.assertTrue(createGameData.gameID() > 0);
        serverFacade.delete();
    }

}
