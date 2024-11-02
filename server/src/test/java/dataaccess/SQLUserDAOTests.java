package dataaccess;

import dataaccess.database.SQLAuthDAO;
import dataaccess.database.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SQLUserDAOTests {

  private UserDAO userDAO;

  public SQLUserDAOTests() throws DataAccessException {
  }

  @BeforeEach
    public void setup() throws DataAccessException {
      this.userDAO = new SQLUserDAO();
      userDAO.clearUsers();
    }

  @Test
  void createUserSuccess() throws DataAccessException {
    UserData testUser  = new UserData("User", "Password", "Email");
    userDAO.createUser("User", "Password", "Email");

    UserData newUser  = userDAO.getUser ("User");
    Assertions.assertEquals(testUser , newUser);
  }

  @Test
  void createUserFail() {
    Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser (null, null, null));
  }

  @Test
  void getUserSuccess() throws DataAccessException {
    UserData testUser  = new UserData("User", "Password", "Email");
    userDAO.createUser ("User", "Password", "Email");

    UserData newUser  = userDAO.getUser ("User");
    Assertions.assertEquals(testUser, newUser);
  }

  @Test
  void getUserFail() throws DataAccessException {
    Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser (null));
    Assertions.assertNull(userDAO.getUser ("nonExistentUser "));
  }

  @Test
  void clearUsersSuccess() throws DataAccessException {
    userDAO.createUser("testUser 1", "testPassword1", "user1@email.com");
    userDAO.createUser("testUser 2", "testPassword2", "user2@email.com");

    Assertions.assertNotNull(userDAO.getUser ("testUser 1"));
    Assertions.assertNotNull(userDAO.getUser ("testUser 2"));

    userDAO.clearUsers();

    // Verify that both users can no longer be retrieved
    Assertions.assertNull(userDAO.getUser ("testUser 1"));
    Assertions.assertNull(userDAO.getUser ("testUser 2"));
  }
}