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

import java.util.Objects;

public class UserService {
  private UserDAO userDAO;
  private AuthDAO authDAO;

  public UserService(AuthDAO authDAO, UserDAO userDAO) {
    this.authDAO = authDAO;
    this.userDAO = userDAO;
  }

  public LoginResult loginUser(LoginRequest requestLogin) throws DataAccessException {
    UserData username = userDAO.getUser(requestLogin.username());
    if (username == null || !Objects.equals(username.password(), requestLogin.password())) {
      throw new DataAccessException("Invalid");
    }

    String authToken = authDAO.createAuth(requestLogin.username());

    return new LoginResult(requestLogin.username(), authToken);
  }

  public RegisterResult registerUser(RegisterRequest register) throws DataAccessException {
    UserData username = userDAO.getUser(register.username());
    if(register.username() == null || register.username().isEmpty()) {
      throw new DataAccessException("A Username is required");
    }
    if(register.password() == null || register.password().isEmpty()) {
      throw new DataAccessException("A Password is required");
    }
    if(register.email() == null || register.email().isEmpty()) {
      throw new DataAccessException("An Email is required");
    }
    if (username != null) {
      throw new DataAccessException("Username already exists");
    }

    userDAO.createUser(register.username(), register.password(), register.email());
    String authToken = authDAO.createAuth(register.username());

    return new RegisterResult(register.username(), authToken);
  }

  public void logoutUser(LogoutRequest requestLogout) throws DataAccessException {
    AuthData authData = authDAO.getAuth(requestLogout.authToken());
    if (authData == null) {
      throw new DataAccessException("Invalid Request");
    }

    authDAO.deleteAuth(requestLogout.authToken());
  }
}