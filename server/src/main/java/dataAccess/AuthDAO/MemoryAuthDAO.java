package dataAccess.AuthDAO;
import dataAccess.AuthDAO.SQLAuthDAO;
import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements SQLAuthDAO{
  private final HashMap<String, AuthData> authentication = new HashMap<>();

  @Override
  public String createAuth(String username) {
    String authToken = UUID.randomUUID().toString();
    AuthData auth = new AuthData(authToken, username);
    authentication.put(authToken, auth);
    return authToken;
  }

  @Override
  public AuthData getAuth(String authToken) {
    return authentication.get(authToken);
  }

  @Override
  public void deleteAuth(String authToken) {
    authentication.remove(authToken);
  }

  @Override
  public void deleteAuthTotal() {
    authentication.clear();
  }
}
