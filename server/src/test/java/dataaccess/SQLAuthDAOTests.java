package dataaccess;

import dataaccess.database.SQLAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTests {

  private AuthDAO authDAO;

  @BeforeEach
  public void setup() throws DataAccessException {
    this.authDAO = new SQLAuthDAO();
    authDAO.deleteAllAuth();
  }

  @Test
  public void createAuthSuccess() throws DataAccessException {
    String authToken = authDAO.createAuth("User");
    AuthData retrieved = authDAO.getAuth(authToken);
    assertEquals(new AuthData(authToken, "User"), retrieved);
  }

  @Test
  public void createAuthFail() {
    assertThrows(DataAccessException.class, () -> authDAO.createAuth(null));
    assertThrows(DataAccessException.class, () -> authDAO.createAuth(""));
  }

  @Test
  public void getAuthSuccess() throws DataAccessException {
    String authToken = authDAO.createAuth("User");
    AuthData retrieved = authDAO.getAuth(authToken);
    assertEquals(new AuthData(authToken, "User"), retrieved);
  }

  @Test
  public void getAuthFail() throws DataAccessException {
    assertThrows(DataAccessException.class, () -> authDAO.getAuth(null));
    assertNull(authDAO.getAuth("nonExistentToken"));
  }

  @Test
  public void deleteAuthSuccess() throws DataAccessException {
    String authToken = authDAO.createAuth("User");
    authDAO.deleteAuth(authToken);
    assertNull(authDAO.getAuth(authToken));
  }

  @Test
  public void deleteAuthFail() {
    assertThrows(DataAccessException.class, () -> authDAO.deleteAuth(null));
    assertThrows(DataAccessException.class, () -> authDAO.deleteAuth(""));
  }

  @Test
  public void deleteAllAuthSuccess() throws DataAccessException {
    authDAO.createAuth("User");
    authDAO.deleteAllAuth();
    assertNull(authDAO.getAuth("User"));
  }
}