package dataAccess.UserDAO;

import dataAccess.DataAccessException;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements SQLUserDAO{
  private final HashMap<String, UserData> users = new HashMap<>();

  @Override
  public void createUser(String username, String password, String email) {
    UserData user = new UserData(username, password, email);
    users.put(username, user);
  }

  @Override
  public UserData getUser(String username) throws DataAccessException {
    return users.get(username);
  }

  @Override
  public void deleteUsers() {
    users.clear();
  }
}
