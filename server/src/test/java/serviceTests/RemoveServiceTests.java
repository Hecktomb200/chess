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
        String authToken1 = authDAO.createAuth("Username1");
        String authToken2 =authDAO.createAuth("Username2");
        String authToken3 =authDAO.createAuth("Username3");
        String authToken4 =authDAO.createAuth("Username4");
        int gameID1 = gameDAO.createGame("Game1");
        int gameID2 = gameDAO.createGame("Game2");
        int gameID3 = gameDAO.createGame("Game3");
        int gameID4 = gameDAO.createGame("Game4");

        deleteService.removeAllServices();
        Assertions.assertNull(userDAO.getUser("Username1"));
        Assertions.assertNull(userDAO.getUser("Username2"));
        Assertions.assertNull(userDAO.getUser("Username3"));
        Assertions.assertNull(userDAO.getUser("Username4"));
        Assertions.assertNull(authDAO.getAuth(authToken1));
        Assertions.assertNull(authDAO.getAuth(authToken2));
        Assertions.assertNull(authDAO.getAuth(authToken3));
        Assertions.assertNull(authDAO.getAuth(authToken4));
        Assertions.assertNull(gameDAO.getGame(gameID1));
        Assertions.assertNull(gameDAO.getGame(gameID2));
        Assertions.assertNull(gameDAO.getGame(gameID3));
        Assertions.assertNull(gameDAO.getGame(gameID4));
    }
}
