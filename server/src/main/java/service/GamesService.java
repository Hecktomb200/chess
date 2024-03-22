package service;
import dataAccess.AuthDAO.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO.GameDAO;
import model.AuthData;
import model.GameData;
import model.createGame.CreateGameRequest;
import model.createGame.CreateGameResult;
import model.joinGame.JoinGameRequest;
import model.listGames.ListGamesRequest;
import model.listGames.ListGamesResult;

public class GamesService {
  private final AuthDAO authDAO;
  private final GameDAO gameDAO;

  public GamesService (AuthDAO authDAO, GameDAO gameDAO) {
    this.authDAO = authDAO;
    this.gameDAO = gameDAO;
  }

  public CreateGameResult createGame(CreateGameRequest gameRequest, String authToken) throws DataAccessException {
    AuthData auth = authDAO.getAuth(authToken);

    if (auth == null) {
      throw new DataAccessException("Unauthorized");
    }
    if (gameRequest.gameName() == null) {
      throw new DataAccessException("Not found");
    }
    if (gameRequest.gameName().isEmpty()) {
      throw new DataAccessException("Not found");
    }

    Integer gameID =gameDAO.createGame(gameRequest.gameName());
    return new CreateGameResult(gameID);
  }

  public void joinGame(JoinGameRequest joinRequest, String authToken) throws DataAccessException {
    AuthData auth = authDAO.getAuth(authToken);
    GameData game = gameDAO.getGame(joinRequest.gameID());

    if (joinRequest.playerColor() == null) {
      if (game == null) {
        throw new DataAccessException("Bad Request");
      }
      if (auth == null) {
        throw new DataAccessException("Unauthorized");
      }
      return;
    }
    if (auth == null) {
      throw new DataAccessException("Unauthorized");
    }
    if (game == null) {
      throw new DataAccessException("Bad Request");
    }

    String whiteUsername = game.whiteUsername();
    String blackUsername =game.blackUsername();

    if (joinRequest.playerColor().equals("white") && game.whiteUsername() == null) {
      whiteUsername = auth.username();
    }
    else if (joinRequest.playerColor().equals("black") && game.blackUsername() == null) {
      blackUsername = auth.username();
    }
    else {
      throw new DataAccessException("Color Already Taken");
    }

    gameDAO.updateGame(new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName(), game.game()));
  }

  public ListGamesResult listGames(ListGamesRequest listRequest) throws DataAccessException {
    AuthData auth = authDAO.getAuth(listRequest.authToken());

    if (auth == null) {
      throw new DataAccessException("Invalid");
    }
    else {
      return new ListGamesResult(gameDAO.listGames());
    }
  }

  public void clearGame() {
    gameDAO.deleteGames();
  }




}
