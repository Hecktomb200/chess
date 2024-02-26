package service;
import dataAccess.AuthDAO.SQLAuthDAO;
import dataAccess.UserDAO.SQLUserDAO;
import dataAccess.GameDAO.SQLGameDAO;



public class RemoveService {
  private final SQLAuthDAO authDAO;
  private final SQLUserDAO userDAO;
  private final SQLGameDAO gameDAO;

  public RemoveService(SQLAuthDAO authDAO,SQLUserDAO userDAO,SQLGameDAO gameDAO) {
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
