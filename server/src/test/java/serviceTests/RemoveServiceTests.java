package serviceTests;

import dataAccess.*;
import dataAccess.AuthDAO.MemoryAuthDAO;
import dataAccess.AuthDAO.SQLAuthDAO;
import dataAccess.GameDAO.MemoryGameDAO;
import dataAccess.GameDAO.SQLGameDAO;
import dataAccess.UserDAO.MemoryUserDAO;
import dataAccess.UserDAO.SQLUserDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.RemoveService;

public class RemoveServiceTests {
    @Test
    void clearAllTests() throws DataAccessException {
        SQLGameDAO gameDAO = new MemoryGameDAO();
        SQLUserDAO userDAO = new MemoryUserDAO();
        SQLAuthDAO authDAO = new MemoryAuthDAO();
        RemoveService deleteService = new RemoveService(authDAO, userDAO, gameDAO);

        userDAO.createUser("Username1", "Password1","Email@Email1");
        userDAO.createUser("Username2", "Password2","Email@Email2");
        userDAO.createUser("Username3", "Password3","Email@Email3");
        userDAO.createUser("Username4", "Password4","Email@Email4");
        int gameID_1 = gameDAO.createGame("Game1");
        int gameID_2 = gameDAO.createGame("Game2");
        int gameID_3 = gameDAO.createGame("Game3");
        int gameID_4 = gameDAO.createGame("Game4");
        String authToken_1 = authDAO.createAuth("Username1");
        String authToken_2 =authDAO.createAuth("Username2");
        String authToken_3 =authDAO.createAuth("Username3");
        String authToken_4 =authDAO.createAuth("Username4");

        deleteService.removeAllServices();
        Assertions.assertNull(userDAO.getUser("Username1"));
        Assertions.assertNull(userDAO.getUser("Username2"));
        Assertions.assertNull(userDAO.getUser("Username3"));
        Assertions.assertNull(userDAO.getUser("Username4"));
        Assertions.assertNull(gameDAO.getGame(gameID_1));
        Assertions.assertNull(gameDAO.getGame(gameID_2));
        Assertions.assertNull(gameDAO.getGame(gameID_3));
        Assertions.assertNull(gameDAO.getGame(gameID_4));
        Assertions.assertNull(authDAO.getAuth(authToken_1));
        Assertions.assertNull(authDAO.getAuth(authToken_2));
        Assertions.assertNull(authDAO.getAuth(authToken_3));
        Assertions.assertNull(authDAO.getAuth(authToken_4));
    }
}
