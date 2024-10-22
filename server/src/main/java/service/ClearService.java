package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {
  private AuthDAO authDAO;
  private UserDAO userDAO;
  private GameDAO gameDAO;

  public ClearService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
    this.authDAO = authDAO;
    this.userDAO = userDAO;
    this.gameDAO = gameDAO;
  }

  public void removeAllServices() {
    authDAO.deleteAllAuth();
    userDAO.clearUsers();
    gameDAO.deleteGames();
  }
}
