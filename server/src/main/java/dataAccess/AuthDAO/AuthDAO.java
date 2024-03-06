package dataAccess.AuthDAO;
import model.AuthData;
public interface AuthDAO {

  String createAuth (String username);
  AuthData getAuth (String authToken);
  void deleteAuth (String authToken);

  void deleteAuthTotal();
}
