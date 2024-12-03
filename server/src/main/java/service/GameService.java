package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import model.joingame.JoinGameRequest;
import model.listgames.ListGamesRequest;
import model.listgames.ListGamesResult;
import model.creategame.CreateGameRequest;
import model.creategame.CreateGameResult;
import websocket.commands.UserGameCommand;

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

  public void joinGame(JoinGameRequest joinRequest, String authToken) throws DataAccessException {
    AuthData auth = authDAO.getAuth(authToken);
    GameData game = gameDAO.getGame(joinRequest.gameID());

    if (auth == null) {
      throw new DataAccessException("Unauthorized");
    }
    if (game == null) {
      throw new DataAccessException("Bad Request");
    }
    if (joinRequest.playerColor() == null) {
      throw new DataAccessException("Bad Request");
    }

    GameData updatedGame = assignPlayerToGame(joinRequest, auth, game);
    gameDAO.updateGame(updatedGame);
  }

  private GameData assignPlayerToGame(JoinGameRequest joinRequest, AuthData auth, GameData game) throws DataAccessException {
    String whiteUsername = game.whiteUsername();
    String blackUsername = game.blackUsername();
    String requestedColor = joinRequest.playerColor().toUpperCase();

    switch (requestedColor) {
      case "WHITE":
        if (game.whiteUsername() == null || game.whiteUsername().equals(auth.username())) {
          whiteUsername = auth.username();
        } else {
          throw new DataAccessException("Already Taken");//"Color Already Taken"
        }
        break;
      case "BLACK":
        if (game.blackUsername() == null || game.blackUsername().equals(auth.username())) {
          blackUsername = auth.username();
        } else {
          throw new DataAccessException("Already Taken");
        }
        break;
      default:
        throw new DataAccessException("Bad Request");
    }

    return new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName(), game.game());
  }

  public ListGamesResult listGames(ListGamesRequest listRequest) throws DataAccessException {
    AuthData auth = authDAO.getAuth(listRequest.authToken());

    if (auth == null) {
      throw new DataAccessException("Unauthorized");
    }

    return new ListGamesResult(gameDAO.listGames());
  }

  public String leave(UserGameCommand command) {
    return null;
  }
}
