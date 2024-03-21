package clientTests;

import org.junit.jupiter.api.*;
import server.Server;
import client.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    void registerPositive() throws Exception {
        var authData = serverFacade.register("playerRegister", "passwordRegister", "pR@email.com");
        Assertions.assertTrue(authData.authToken().length() > 10);
        serverFacade.delete();
    }

    @Test
    void loginPositive() throws Exception {
        serverFacade.register("playerLogin", "passwordLogin", "pL@email.com");
        var loginData = serverFacade.login("playerLogin", "passwordLogin");
        Assertions.assertTrue(loginData.authToken().length() > 10);
        serverFacade.delete();
    }

}
