package service;

import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClearServiceTests {

  @Test
  void clearAllEntitiesSuccessfully() throws DataAccessException {
    // Initialize the DAOs and the ClearService
    GameDAO gameDAO = new GameDAO();
    UserDAO userDAO = new UserDAO();
    AuthDAO authDAO = new AuthDAO();
    ClearService clearService = new ClearService(authDAO, userDAO, gameDAO);

    // Create some users, games, and authentication tokens
    userDAO.createUser ("User 1", "Password1", "user1@example.com");
    userDAO.createUser ("User 2", "Password2", "user2@example.com");
    userDAO.createUser ("User 3", "Password3", "user3@example.com");
    userDAO.createUser ("User 4", "Password4", "user4@example.com");

    int gameID1 = gameDAO.createGame("Game1");
    int gameID2 = gameDAO.createGame("Game2");

    String authToken1 = authDAO.createAuth("User 1");
    String authToken2 = authDAO.createAuth("User 2");

    // Invoke the method to clear all entities
    clearService.removeAllServices();

    // Assert that all users, games, and auth tokens have been cleared
    Assertions.assertNull(userDAO.getUser ("User 1"));
    Assertions.assertNull(userDAO.getUser ("User 2"));
    Assertions.assertNull(userDAO.getUser ("User 3"));
    Assertions.assertNull(userDAO.getUser ("User 4"));

    Assertions.assertNull(gameDAO.getGame(gameID1));
    Assertions.assertNull(gameDAO.getGame(gameID2));

    Assertions.assertNull(authDAO.getAuth(authToken1));
    Assertions.assertNull(authDAO.getAuth(authToken2));
  }
}