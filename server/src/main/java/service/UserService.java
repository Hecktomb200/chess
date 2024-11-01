package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.Login.LoginRequest;
import model.Login.LoginResult;
import model.Logout.LogoutRequest;
import model.Register.RegisterRequest;
import model.Register.RegisterResult;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public class UserService {
  private UserDAO userDAO;
  private AuthDAO authDAO;

  public UserService(AuthDAO authDAO, UserDAO userDAO) {
    this.authDAO = authDAO;
    this.userDAO = userDAO;
  }

  public LoginResult loginUser(LoginRequest requestLogin) throws DataAccessException {
    UserData user = userDAO.getUser(requestLogin.username());
    if (user == null || !BCrypt.checkpw(requestLogin.password(), user.password())) {
      throw new DataAccessException("Unauthorized");
    }

    String authToken = authDAO.createAuth(requestLogin.username());

    return new LoginResult(requestLogin.username(), authToken);
  }

  public RegisterResult registerUser(RegisterRequest register) throws DataAccessException {
    UserData username = userDAO.getUser(register.username());
    if(register.username() == null || register.username().isEmpty()) {
      throw new DataAccessException("Bad Request");
    }
    if(register.password() == null || register.password().isEmpty()) {
      throw new DataAccessException("Bad Request");
    }
    if(register.email() == null || register.email().isEmpty()) {
      throw new DataAccessException("Bad Request");
    }
    if (username != null) {
      throw new DataAccessException("Already Taken");
    }

    String hashedPassword = BCrypt.hashpw(register.password(), BCrypt.gensalt());
    userDAO.createUser(register.username(), hashedPassword, register.email());
    String authToken = authDAO.createAuth(register.username());


    return new RegisterResult(register.username(), authToken);
  }

  public void logoutUser(LogoutRequest requestLogout) throws DataAccessException {
    AuthData authData = authDAO.getAuth(requestLogout.authToken());
    if (authData == null) {
      throw new DataAccessException("Unauthorized");
    }

    authDAO.deleteAuth(requestLogout.authToken());
  }
}
