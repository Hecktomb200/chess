package serviceTests;

import dataAccess.*;
import dataAccess.AuthDAO.SQLAuthDAO;
import dataAccess.UserDAO.SQLUserDAO;
import dataAccess.AuthDAO.MemoryAuthDAO;
import dataAccess.UserDAO.MemoryUserDAO;
import model.login.LoginRequest;
import model.login.LoginResult;
import model.logout.LogoutRequest;
import model.register.RegisterRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.UserService;

import java.util.UUID;

public class UserServiceTests {
    @Test
    void registerTestPositive() throws DataAccessException {
        SQLUserDAO userDAO = new MemoryUserDAO();
        SQLAuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);

        RegisterRequest request = new RegisterRequest("GoodUsername", "GoodPassword", "GoodEmail@Email");
        var response = userService.registerUser(request);
        Assertions.assertEquals("GoodUsername", userDAO.getUser("GoodUsername").username());
        Assertions.assertEquals("GoodPassword", userDAO.getUser("GoodUsername").password());
        Assertions.assertEquals("GoodEmail@Email", userDAO.getUser("GoodUsername").email());
        Assertions.assertEquals("GoodUsername", response.username());
        Assertions.assertNotEquals("", response.authToken());


    }

    @Test
    void registerTestNegative() throws DataAccessException {
        SQLUserDAO userDAO = new MemoryUserDAO();
        SQLAuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);

        RegisterRequest request = new RegisterRequest("BadUsername1", "BadPassword1", "BadEmail@Email1");
        userService.registerUser(request);

        RegisterRequest sameRequest = new RegisterRequest("BadUsername1", "BadPassword1", "BadEmail@Email1");
        Assertions.assertThrows(DataAccessException.class, () -> userService.registerUser(sameRequest));

        RegisterRequest newRequest = new RegisterRequest("", "TestPassword", "Test@Email");
        Assertions.assertThrows(DataAccessException.class, () -> userService.registerUser(newRequest));
    }

    @Test
    void loginTestPositive() throws DataAccessException {
        SQLUserDAO userDAO = new MemoryUserDAO();
        SQLAuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);

        userDAO.createUser("GoodUsername", "GoodPassword", "GoodEmail@Email");

        LoginRequest reg = new LoginRequest("GoodUsername", "GoodPassword");
        var response = userService.loginUser(reg);
        Assertions.assertEquals("GoodUsername", response.username());
        Assertions.assertEquals(LoginResult.class, response.getClass());
        Assertions.assertNotEquals("", response.authToken());
        Assertions.assertNotEquals(null, response.authToken());
    }

    @Test
    void loginTestNegative() {
        SQLUserDAO userDAO = new MemoryUserDAO();
        SQLAuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);

        userDAO.createUser("BadUsername", "BadPassword", "BadEmail@Email");

        LoginRequest request = new LoginRequest("BadUsername", "BadWrongPassword");
        Assertions.assertThrows(DataAccessException.class, () -> userService.loginUser(request));

        LoginRequest newRequest = new LoginRequest("BadWrongUsername", "BadPassword");
        Assertions.assertThrows(DataAccessException.class, () -> userService.loginUser(newRequest));
    }

    @Test
    void LogoutTestPositive() throws DataAccessException {
        SQLUserDAO userDAO = new MemoryUserDAO();
        SQLAuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);

        String authToken = authDAO.createAuth("GoodUsername");

        LogoutRequest request = new LogoutRequest(authToken);
        userService.logoutUser(request);
        Assertions.assertNull(authDAO.getAuth(authToken));
    }

    @Test
    void LogoutTestNegative() {
        SQLUserDAO userDAO = new MemoryUserDAO();
        SQLAuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);

        authDAO.createAuth("BadUsername");

        LogoutRequest request = new LogoutRequest(UUID.randomUUID().toString());
        Assertions.assertThrows(DataAccessException.class, () -> userService.logoutUser(request));
    }
}
