package client;


import model.login.LoginResult;
import model.register.RegisterResult;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServerFacadeTests {

    private Server server;
    private ServerFacade serverFacade;
    private String authToken;

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
        var authData=serverFacade.registerUser("TestUsername", "TestPassword", "Email@email.com");
        Assertions.assertNotNull(authData);
        Assertions.assertTrue(authData.authToken().length() > 10);
        serverFacade.delete();
    }

    @Test
    void registerFail() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> serverFacade.registerUser("TestUsername", null, "Email@email.com"));
        serverFacade.delete();
    }

    @Test
    void loginSuccess() throws Exception {
        serverFacade.registerUser("TestUsername", "TestPassword", "Email@email.com");
        var loginData = serverFacade.loginUser("TestUsername", "TestPassword");
        Assertions.assertNotNull(loginData);
        Assertions.assertTrue(loginData.authToken().length() > 10);
        Assertions.assertEquals("TestUsername", loginData.username());
        serverFacade.delete();
    }

    @Test
    void loginFail() throws Exception {
        serverFacade.registerUser("TestUsername", "TestPassword", "Email@email.com");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> serverFacade.loginUser(null, "TestPassword"));
        serverFacade.delete();
    }

    @Test
    void logoutSuccess() throws Exception {
        RegisterResult registerData = serverFacade.registerUser("TestUsername", "TestPassword", "Email@email.com");
        serverFacade.logoutUser(registerData.authToken());
        LoginResult loginData = serverFacade.loginUser("TestUsername", "TestPassword");
        Assertions.assertNotNull(loginData.authToken());
        Assertions.assertNotEquals(registerData.authToken(), loginData.authToken());
        Assertions.assertEquals("TestUsername", loginData.username());
        serverFacade.delete();
    }

    @Test
    void logoutFail() throws Exception {
        serverFacade.registerUser("TestUsername", "TestPassword", "EmailO@email.com");
        Assertions.assertThrows(IOException.class,
                () -> serverFacade.logoutUser(UUID.randomUUID().toString()));
        serverFacade.delete();
    }

}