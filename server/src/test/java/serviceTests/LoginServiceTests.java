package serviceTests;

import dataAccess.*;
import dataAccess.AuthDAO.MemoryAuthDAO;
import dataAccess.AuthDAO.SQLAuthDAO;
import dataAccess.UserDAO.MemoryUserDAO;
import dataAccess.UserDAO.SQLUserDAO;
import model.login.LoginRequest;
import model.login.LoginResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.UserService;

public class LoginServiceTests {

    @Test
    void loginServiceSuccess() throws DataAccessException {
        SQLUserDAO userDAO = new MemoryUserDAO();
        SQLAuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);

        userDAO.createUser("TestUsername", "TestPassword", "Test@Email");

        LoginRequest register = new LoginRequest("TestUsername", "TestPassword");
        var result = userService.loginUser(register);
        Assertions.assertEquals("TestUsername", result.username());
        Assertions.assertEquals(LoginResult.class, result.getClass());
        Assertions.assertNotEquals("", result.authToken());
        Assertions.assertNotEquals(null, result.authToken());
    }

    @Test
    void loginServiceErrors() {
        SQLUserDAO userDAO = new MemoryUserDAO();
        SQLAuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);

        userDAO.createUser("TestUsername", "TestPassword", "Test@Email");

        LoginRequest reg = new LoginRequest("TestUsername", "TestWrongPassword");
        Assertions.assertThrows(DataAccessException.class, () -> userService.loginUser(reg));

        LoginRequest newReg = new LoginRequest("TestWrongUsername", "TestPassword");
        Assertions.assertThrows(DataAccessException.class, () -> userService.loginUser(newReg));
    }
}