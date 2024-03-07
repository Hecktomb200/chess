package dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;
public interface AuthDAO {

  String createAuth (String username) throws DataAccessException;
  AuthData getAuth (String authToken) throws DataAccessException;
  void deleteAuth (String authToken);

  void deleteAuthTotal();
}
