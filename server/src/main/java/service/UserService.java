package service;
import dataAccess.AuthDAO.SQLAuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO.SQLUserDAO;
import model.AuthData;
import model.UserData;
import model.login.LoginRequest;
import model.login.LoginResult;
import model.logout.LogoutRequest;
import model.register.RegisterRequest;
import model.register.RegisterResult;

import java.util.Objects;

public class UserService {
  private final SQLAuthDAO authDAO;
  private final SQLUserDAO userDAO;

  public UserService(SQLAuthDAO authDAO, SQLUserDAO userDAO) {
    this.authDAO = authDAO;
    this.userDAO = userDAO;
  }

  public RegisterResult registerUser(RegisterRequest register) throws DataAccessException {
    if(register.username() == null || register.username().isEmpty() ||
            register.password() == null || register.password().isEmpty() ||
            register.email() == null || register.email().isEmpty()) {
      throw new DataAccessException("Bad request");
    }

    UserData username = userDAO.getUser(register.username());
    if (username != null) {
      throw new DataAccessException("Username already exists");
    }

    userDAO.createUser(register.username(), register.password(), register.email());
    String authToken = authDAO.createAuth(register.username());

    return new RegisterResult(register.username(), authToken);
  }

  public LoginResult loginUser(LoginRequest requestLogin) throws DataAccessException {
      UserData username = userDAO.getUser(requestLogin.username());
      if (username == null || !Objects.equals(username.password(), requestLogin.password())) {
        throw new DataAccessException("Invalid");
      }

      String authToken = authDAO.createAuth(requestLogin.username());

      return new LoginResult(requestLogin.username(), authToken);
  }

  public void logoutUser(LogoutRequest requestLogout) throws DataAccessException {
    AuthData authData = authDAO.getAuth(requestLogout.authToken());
    if (authData == null) {
      throw new DataAccessException("Invalid");
    }

    authDAO.deleteAuth(requestLogout.authToken());
  }
}
