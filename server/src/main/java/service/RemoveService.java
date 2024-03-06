package service;
import dataAccess.AuthDAO.AuthDAO;
import dataAccess.UserDAO.UserDAO;
import dataAccess.GameDAO.GameDAO;



public class RemoveService {
  private final AuthDAO authDAO;
  private final UserDAO userDAO;
  private final GameDAO gameDAO;

  public RemoveService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
    this.authDAO = authDAO;
    this.userDAO = userDAO;
    this.gameDAO = gameDAO;
  }

  public void removeAllServices() {
    authDAO.deleteAuthTotal();
    userDAO.deleteUsers();
    gameDAO.deleteGames();
  }
}
