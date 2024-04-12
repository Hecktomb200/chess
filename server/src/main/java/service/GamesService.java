package service;
import chess.ChessGame;
import chess.InvalidMoveException;
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
import webSocketMessages.userCommands.*;

import java.util.Objects;

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

  public String joinPlayer(JoinPlayerCommand pData) throws DataAccessException {
    AuthData authData = authDAO.getAuth(pData.getAuthString());
    GameData gameData = gameDAO.getGame(pData.getGameID());

    if(authData == null) {
      return "Error: bad auth token";
    }

    if(gameData == null) {
      return "Error: incorrect gameID";
    }


    if((pData.getPlayerColor() == ChessGame.TeamColor.WHITE
            && gameData.whiteUsername() == null ||
            (pData.getPlayerColor() == ChessGame.TeamColor.BLACK
                    && gameData.blackUsername() == null))) {
      return "Error: game empty";
    }

    if((pData.getPlayerColor() == ChessGame.TeamColor.WHITE
            && Objects.equals(authData.username(), gameData.blackUsername())) ||
            (pData.getPlayerColor() == ChessGame.TeamColor.BLACK
                    && Objects.equals(authData.username(), gameData.whiteUsername()))) {
      return "Error: spot already taken";
    }

    return "";
  }

  public String joinObserver(JoinObserverCommand pData) throws DataAccessException {
    AuthData authData = authDAO.getAuth(pData.getAuthString());
    GameData gameData = gameDAO.getGame(pData.getGameID());

    if(authData == null) {
      return "Error: bad auth token";
    }

    if(gameData == null) {
      return "Error: incorrect gameID";
    }

    return authData.username();
  }

  public String makeMove(MoveCommand mData) throws DataAccessException {
    AuthData authData = authDAO.getAuth(mData.getAuthString());
    GameData gameData = gameDAO.getGame(mData.getGameID());

    ChessGame currentGame = gameData.game();
    if((currentGame.getTeamTurn() == ChessGame.TeamColor.WHITE
            && !Objects.equals(gameData.whiteUsername(), authData.username())) ||
            (currentGame.getTeamTurn() == ChessGame.TeamColor.BLACK
                    && !Objects.equals(gameData.blackUsername(), authData.username()))) {
      return "Error: not current player's turn";
    }
    try {
      currentGame.makeMove(mData.getMove());
      GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(),
              gameData.blackUsername(), gameData.gameName(), currentGame);
      gameDAO.updateGame(newGameData);
      return authData.username() + "," + checkIfCheck(gameData, currentGame);
    } catch (InvalidMoveException e) {
      return "Error: invalid move";
    }
  }

  private String checkIfCheck(GameData gameData, ChessGame currentGame) {
    if(currentGame.isInCheckmate(ChessGame.TeamColor.WHITE)) {
      return "checkmate," + gameData.whiteUsername();
    } else if(currentGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
      return "checkmate," + gameData.blackUsername();
    } else if(currentGame.isInCheck(ChessGame.TeamColor.WHITE)) {
      return "check," + gameData.whiteUsername();
    } else if(currentGame.isInCheck(ChessGame.TeamColor.BLACK)) {
      return "check," + gameData.blackUsername();
    }
    return "null";
  }

  public String resignPlayer(ResignCommand resignData) throws DataAccessException {
    AuthData authData = authDAO.getAuth(resignData.getAuthString());
    GameData gameData = gameDAO.getGame(resignData.getGameID());

    if(!Objects.equals(authData.username(), gameData.whiteUsername())
            && !Objects.equals(authData.username(), gameData.blackUsername())) {
      return "Error: observers not allowed to resign";
    }
    return authData.username();
  }

  public String leavePlayer(LeaveCommand leaveData) throws DataAccessException {
    AuthData authData = authDAO.getAuth(leaveData.getAuthString());
    GameData gameData = gameDAO.getGame(leaveData.getGameID());
    return authData.username();
  }


}
