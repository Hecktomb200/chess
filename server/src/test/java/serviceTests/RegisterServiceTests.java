package serviceTests;

import dataAccess.*;
import dataAccess.AuthDAO.SQLAuthDAO;
import dataAccess.UserDAO.MemoryUserDAO;
import dataAccess.UserDAO.SQLUserDAO;
import dataAccess.AuthDAO.MemoryAuthDAO;
import model.register.RegisterRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.UserService;

public class RegisterServiceTests {

    @Test
    void registerServiceSuccess() throws DataAccessException {
        SQLUserDAO userDAO = new MemoryUserDAO();
        SQLAuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);

        RegisterRequest reg = new RegisterRequest("TestUsernameReg", "TestPasswordReg", "Test@EmailReg");
        var res = userService.registerUser(reg);
        Assertions.assertEquals("TestUsernameReg", userDAO.getUser("TestUsernameReg").username());
        Assertions.assertEquals("TestPasswordReg", userDAO.getUser("TestUsernameReg").password());
        Assertions.assertEquals("Test@EmailReg", userDAO.getUser("TestUsernameReg").email());
        Assertions.assertEquals("TestUsernameReg", res.username());
        Assertions.assertNotEquals("", res.authToken());


    }

    @Test
    void registerServiceErrors() throws DataAccessException {
        SQLUserDAO userDAO = new MemoryUserDAO();
        SQLAuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);

        RegisterRequest reg = new RegisterRequest("TestUsername1", "TestPassword1", "Test@Email1");
        userService.registerUser(reg);

        RegisterRequest sameReg = new RegisterRequest("TestUsername1", "TestPassword1", "Test@Email1");
        Assertions.assertThrows(DataAccessException.class, () -> userService.registerUser(sameReg));

        RegisterRequest newReg = new RegisterRequest("", "TestPassword", "Test@Email");
        Assertions.assertThrows(DataAccessException.class, () -> userService.registerUser(newReg));    }

}