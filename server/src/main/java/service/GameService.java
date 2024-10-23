package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.createGame.CreateGameRequest;
import model.createGame.CreateGameResult;

public class GameService {
  private AuthDAO authDAO;
  private GameDAO gameDAO;

  public GameService (AuthDAO authDAO, GameDAO gameDAO) {
    this.authDAO = authDAO;
    this.gameDAO = gameDAO;
  }

  public CreateGameResult createGame(CreateGameRequest gameRequest, String authToken) throws DataAccessException {
    AuthData auth = authDAO.getAuth(authToken);

    if (auth == null) {
      throw new DataAccessException("Unauthorized");
    }
    verifyGame(gameRequest.gameName());

    int gameID = gameDAO.createGame(gameRequest.gameName());
    return new CreateGameResult(gameID);
  }

  public void verifyGame(String gameName) throws DataAccessException {
    if (gameName == null || gameName.isEmpty()) {
      throw new DataAccessException("Not found");
    }
  }

}
