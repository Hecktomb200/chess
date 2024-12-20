package dataaccess;

import dataaccess.DataAccessException;
import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class AuthDAO {
  private final HashMap<String, AuthData> authentication = new HashMap<>();

  public String createAuth(String username) throws DataAccessException {
    if (username == null || username.isEmpty()) {
      throw new DataAccessException("Username cannot be null or empty");
    }
    String authToken = UUID.randomUUID().toString();
    AuthData auth = new AuthData(authToken, username);
    authentication.put(authToken, auth);
    return authToken;
  }

  public AuthData getAuth(String authToken) throws DataAccessException {
    return authentication.get(authToken);
  }

  public void deleteAuth(String authToken) throws DataAccessException {
    authentication.remove(authToken);
  }

  public void deleteAllAuth() throws DataAccessException {
    authentication.clear();
  }
}