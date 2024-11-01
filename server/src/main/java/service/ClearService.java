package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
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

  public void removeAllServices() throws DataAccessException {
    authDAO.deleteAllAuth();
    userDAO.clearUsers();
    gameDAO.deleteGames();
  }
}
