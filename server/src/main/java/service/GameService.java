package service;

import chess.ChessGame;
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
import websocket.commands.*;

import java.util.Objects;

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

  public String connect(ConnectCommand command) throws DataAccessException {
    AuthData authData = authDAO.getAuth(command.getAuthToken());
    GameData gameData = gameDAO.getGame(command.getGameID());

    if (authData == null) {
      return "Error: bad auth token";
    }

    if (gameData == null) {
      return "Error: incorrect gameID";
    }

    if (command.isObserver()) {
      return authData.username();
    } else {
      return handleJoinPlayer(command, authData, gameData);
    }
  }

  private String handleJoinPlayer(ConnectCommand command, AuthData authData, GameData gameData) {
    if (isGameEmpty(command.getPlayerColor(), gameData)) {
      return "Error: game empty";
    }

    if (isColorTaken(command.getPlayerColor(), authData.username(), gameData)) {
      return "Error: spot already taken";
    }

    //TODO
    // Additional logic to update game with player's information might be necessary.

    return "";
  }

  private boolean isGameEmpty(ChessGame.TeamColor playerColor, GameData gameData) {
    return (playerColor == ChessGame.TeamColor.WHITE && gameData.whiteUsername() == null) ||
            (playerColor == ChessGame.TeamColor.BLACK && gameData.blackUsername() == null);
  }

  private boolean isColorTaken(ChessGame.TeamColor playerColor, String username, GameData gameData) {
    return (playerColor == ChessGame.TeamColor.WHITE && Objects.equals(username, gameData.blackUsername())) ||
            (playerColor == ChessGame.TeamColor.BLACK && Objects.equals(username, gameData.whiteUsername()));
  }

  public String leave(LeaveCommand leaveCommand) {
    return null;
  }

  //TODO Make sure to fill this out!
  public String resign(ResignCommand resignCommand) {
    return null;
  }


  //TODO This one to fill out also!
  public String makeMove(MoveCommand moveCommand) {
    return null;
  }
}
