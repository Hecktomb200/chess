package service;

import dataaccess.*;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.login.LoginRequest;
import model.login.LoginResult;
import model.logout.LogoutRequest;
import model.register.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class UserServiceTests {
  private UserDAO userDAO;
  private AuthDAO authDAO;
  private UserService userService;

  @BeforeEach
  void setUp() {
    userDAO=new UserDAO();
    authDAO=new AuthDAO();
    userService=new UserService(authDAO, userDAO);
  }

  @Test
  void registerTest() throws DataAccessException {
    RegisterRequest request=new RegisterRequest("Username", "Password", "Email@Email");
    var response=userService.registerUser(request);

    Assertions.assertEquals("Username", userDAO.getUser("Username").username());
    Assertions.assertEquals("Password", userDAO.getUser("Username").password());
    Assertions.assertEquals("Email@Email", userDAO.getUser("Username").email());
    Assertions.assertEquals("Username", response.username());
    Assertions.assertNotEquals("", response.authToken());
  }

  @Test
  void registerFailsTest() throws DataAccessException {
    RegisterRequest request=new RegisterRequest("Username1", "Password1", "Email@Email1");
    userService.registerUser(request);

    Assertions.assertThrows(DataAccessException.class, () -> userService.registerUser(request));

    RegisterRequest newRequest=new RegisterRequest("", "TestPassword", "Test@Email");
    Assertions.assertThrows(DataAccessException.class, () -> userService.registerUser(newRequest));
  }

  @Test
  void loginTest() throws DataAccessException {
    userDAO.createUser("Username", "Password", "Email@Email");

    LoginRequest loginRequest=new LoginRequest("Username", "Password");
    var response=userService.loginUser(loginRequest);

    Assertions.assertEquals("Username", response.username());
    Assertions.assertEquals(LoginResult.class, response.getClass());
    Assertions.assertNotEquals("", response.authToken());
    Assertions.assertNotNull(response.authToken());
  }

  @Test
  void loginWrongTest() throws DataAccessException {
    userDAO.createUser("Username", "Password", "Email@Email");

    LoginRequest wrongPasswordRequest=new LoginRequest("Username", "WrongPassword");
    Assertions.assertThrows(DataAccessException.class, () -> userService.loginUser(wrongPasswordRequest));

    LoginRequest nonExistentUserRequest=new LoginRequest("WrongUsername", "Password");
    Assertions.assertThrows(DataAccessException.class, () -> userService.loginUser(nonExistentUserRequest));
  }

  @Test
  void logoutTest() throws DataAccessException {
    String authToken=authDAO.createAuth("Username");
    LogoutRequest request=new LogoutRequest(authToken);

    userService.logoutUser(request);
    Assertions.assertNull(authDAO.getAuth(authToken));
  }

  @Test
  void logoutNothingTest() throws DataAccessException {
    authDAO.createAuth("Username");

    LogoutRequest request=new LogoutRequest(UUID.randomUUID().toString());
    Assertions.assertThrows(DataAccessException.class, () -> userService.logoutUser(request));
  }
}