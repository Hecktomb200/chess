package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.Login.LoginRequest;
import model.Login.LoginResult;
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
}
