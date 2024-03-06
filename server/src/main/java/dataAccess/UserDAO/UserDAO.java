package dataAccess.UserDAO;
import model.UserData;
import dataAccess.DataAccessException;

public interface UserDAO {
  void createUser(String username, String password, String email);
  UserData getUser(String username) throws DataAccessException;
  void deleteUsers();
}
