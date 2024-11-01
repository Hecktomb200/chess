package dataaccess;

import dataaccess.DataAccessException;
import model.UserData;

import java.util.HashMap;
import java.util.Optional;

public class UserDAO {
  private final HashMap<String, UserData> users = new HashMap<>();

  public void createUser(String username, String password, String email) throws DataAccessException {
    if (username == null || username.isEmpty()) {
      throw new DataAccessException("Username cannot be null or empty");
    }
    if (password == null || password.isEmpty()) {
      throw new DataAccessException("Password cannot be null or empty");
    }
    if (email == null || email.isEmpty()) {
      throw new DataAccessException("Email cannot be null or empty");
    }
    if (users.containsKey(username)) {
      throw new DataAccessException("Username already exists");
    }

    UserData user = new UserData(username, password, email);
    users.put(username, user);
  }

  public UserData getUser(String username) throws DataAccessException {
    if (username == null || username.isEmpty()) {
      throw new DataAccessException("Username cannot be null or empty");
    }
    return users.get(username);
  }

  public void clearUsers() throws DataAccessException {
    users.clear();
  }
}