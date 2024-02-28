package service;
import dataAccess.AuthDAO.SQLAuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO.SQLGameDAO;
import model.AuthData;
import model.GameData;
import model.createGame.CreateGameRequest;
import model.createGame.CreateGameResult;
import model.joinGame.JoinGameRequest;
import model.listGames.ListGamesRequest;
import model.listGames.ListGamesResult;

public class GamesService {
  private final SQLAuthDAO authDAO;
  private final SQLGameDAO gameDAO;

  public GamesService (SQLAuthDAO authDAO, SQLGameDAO gameDAO) {
    this.authDAO = authDAO;
    this.gameDAO = gameDAO;
  }

  public CreateGameResult createGame(CreateGameRequest gameRequest, String authToken) throws DataAccessException {
    AuthData auth = authDAO.getAuth(authToken);

    if (auth == null) {
      throw new DataAccessException("Invalid");
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
      return;
    }
    if (auth == null) {
      throw new DataAccessException("Invalid");
    }
    if (game == null) {
      throw new DataAccessException("Not found");
    }

    String whiteUsername = game.whiteUsername();
    String blackUsername =game.blackUsername();

    if (joinRequest.playerColor().equals("WHITE") && game.whiteUsername() == null) {
      whiteUsername = auth.username();
    }
    else if (joinRequest.playerColor().equals("BLACK") && game.whiteUsername() == null) {
      blackUsername = auth.username();
    }
    else {
      throw new DataAccessException("Color already taken");
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
