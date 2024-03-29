package server;

import dataAccess.AuthDAO.MemoryAuthDAO;
import dataAccess.AuthDAO.AuthDAO;
import dataAccess.AuthDAO.SQLAuthDatabase;
import dataAccess.DataAccessException;
import dataAccess.GameDAO.MemoryGameDAO;
import dataAccess.GameDAO.GameDAO;
import dataAccess.GameDAO.SQLGameDatabase;
import dataAccess.UserDAO.MemoryUserDAO;
import dataAccess.UserDAO.SQLUserDatabase;
import dataAccess.UserDAO.UserDAO;
import model.GameData;
import model.createGame.CreateGameRequest;
import model.joinGame.JoinGameRequest;
import model.listGames.ListGamesRequest;
import model.login.LoginRequest;
import model.logout.LogoutRequest;
import model.register.RegisterRequest;
import service.GamesService;
import service.RemoveService;
import service.UserService;
import com.google.gson.Gson;
import model.ErrorMessageResult;
import spark.*;

import java.util.Objects;

public class ServerHandler {
  UserDAO userDAO;
  AuthDAO authDAO;
  GameDAO gameDAO;

  public ServerHandler() {
    try {
      this.userDAO = new SQLUserDatabase();
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
    try {
      this.authDAO = new SQLAuthDatabase();
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
    try {
      this.gameDAO = new SQLGameDatabase();
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }
  public Object logoutHandler(Request logoutRequest, Response logoutResponse) {
    UserService userService = new UserService(authDAO, userDAO);

    try {
      LogoutRequest request = new LogoutRequest(logoutRequest.headers("authorization"));
      userService.logoutUser(request);
      logoutResponse.status(200);
      return "";
    } catch(Exception e) {
      logoutResponse.status(401);
      return new Gson().toJson(new ErrorMessageResult("Error: unauthorized"));
    }
  }
  public Object loginHandler(Request loginRequest, Response loginResponse) {
    UserService userService = new UserService(authDAO, userDAO);

    try {
      var request = new Gson().fromJson(loginRequest.body(), LoginRequest.class);
      var response = userService.loginUser(request);
      loginResponse.status(200);
      return new Gson().toJson(response);
    } catch(Exception e) {
      loginResponse.status(401);
      return new Gson().toJson(new ErrorMessageResult("Error: unauthorized"));
    }
  }
  public Object registerHandler(Request registerRequest, Response registerResponse) {
    UserService userService = new UserService(authDAO, userDAO);

    try {
      var request = new Gson().fromJson(registerRequest.body(), RegisterRequest.class);
      var response = userService.registerUser(request);
      registerResponse.status(200);
      return new Gson().toJson(response);
    } catch(DataAccessException e) {
      if(Objects.equals(e.getMessage(), "Username already exists")) {
        registerResponse.status(403);
        return new Gson().toJson(new ErrorMessageResult("Error: already taken"));
      }
      if(Objects.equals(e.getMessage(), "Bad request")) {
        registerResponse.status(400);
        return new Gson().toJson(new ErrorMessageResult("Error: bad request"));
      }
      registerResponse.status(500);
      return new Gson().toJson(new ErrorMessageResult("Error: DataAccessException thrown but not caught correctly"));
    } catch(Exception e) {
      registerResponse.status(500);
      return new Gson().toJson(new ErrorMessageResult(e.getMessage()));
    }
  }
  public Object listGamesHandler(Request listRequest, Response listResponse) {
    GamesService gamesService=new GamesService(authDAO, gameDAO);

    try {
      ListGamesRequest request=new ListGamesRequest(listRequest.headers("authorization"));
      var response=gamesService.listGames(request);
      listResponse.status(200);
      return new Gson().toJson(response);
    } catch (Exception e) {
      listResponse.status(401);
      return new Gson().toJson(new ErrorMessageResult("Error: unauthorized"));
    }
  }

  public Object createGameHandler(Request createRequest, Response createResponse) {
    GamesService gamesService = new GamesService(authDAO, gameDAO);

    try {
      String authToken = createRequest.headers("authorization");
      var request = new Gson().fromJson(createRequest.body(), CreateGameRequest.class);
      var response = gamesService.createGame(request, authToken);
      createResponse.status(200);
      return new Gson().toJson(response);
    } catch(Exception e) {
      if(Objects.equals(e.getMessage(), "Bad request")) {
        createResponse.status(400);
        return new Gson().toJson(new ErrorMessageResult("Error: bad request"));
      }
      if(Objects.equals(e.getMessage(), "Unauthorized")) {
        createResponse.status(401);
        return new Gson().toJson(new ErrorMessageResult("Error: unauthorized"));
      }
      createResponse.status(500);
      return new Gson().toJson(new ErrorMessageResult("Error: DataAccessException thrown but not caught correctly"));
    }
  }
  public Object joinGameHandler(Request joinRequest, Response joinResponse) {
    GamesService gamesService = new GamesService(authDAO, gameDAO);

    try {
      String authToken = joinRequest.headers("authorization");
      var request = new Gson().fromJson(joinRequest.body(), JoinGameRequest.class);
      gamesService.joinGame(request, authToken);
      joinResponse.status(200);
      return "{}";
    } catch(Exception e) {
      if(Objects.equals(e.getMessage(), "Unauthorized")) {
        joinResponse.status(401);
        return new Gson().toJson(new ErrorMessageResult("Error: Unauthorized"));
      }
      if(Objects.equals(e.getMessage(), "Bad Request")) {
        joinResponse.status(400);
        return new Gson().toJson(new ErrorMessageResult("Error: Bad Request"));
      }
      if(Objects.equals(e.getMessage(), "Color Already Taken")) {
        joinResponse.status(403);
        return new Gson().toJson(new ErrorMessageResult("Error: Forbidden"));
      }
      joinResponse.status(500);
      return new Gson().toJson(new ErrorMessageResult("Error: DataAccessException thrown but not caught correctly"));
    }
  }

  public Object clear(Request requestClear, Response responseClear) {
    RemoveService removeService = new RemoveService(authDAO, userDAO, gameDAO);

    try {
      removeService.removeAllServices();
      responseClear.status(200);
      return "{}";
    } catch (Exception e) {
      responseClear.status(500);
      return new Gson().toJson(new ErrorMessageResult(e.toString()));
    }
  }
  record games(GameData[] games) {

  }
}
