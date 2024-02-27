package server;

import dataAccess.AuthDAO.SQLAuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO.SQLGameDAO;
import dataAccess.UserDAO.SQLUserDAO;
import model.createGame.CreateGameRequest;
import model.joinGame.JoinGameRequest;
import model.listGames.ListGamesRequest;
import model.login.LoginRequest;
import model.logout.LogoutRequest;
import model.register.RegisterRequest;
import service.GamesService;
import service.UserService;
import com.google.gson.Gson;
import spark.*;

import java.util.Objects;

public class ServerHandler {
  SQLUserDAO userDAO;
  SQLAuthDAO authDAO;
  SQLGameDAO gameDAO;

  public Object logoutHandler(Request logoutRequest, Response logoutResponse) {
    UserService userService = new UserService(authDAO, userDAO);

    try {
      LogoutRequest request = new LogoutRequest(logoutRequest.headers("authorization"));
      userService.logoutUser(request);
      return "";
    } catch(Exception e) {
      logoutResponse.status(500);
      return new Gson().toJson(new ErrorMessage(e.getMessage()));
    } catch(DataAccessException e) {
      logoutResponse.status(401);
      return new Gson().toJson(new ErrorMessage("Error: unauthorized"));
    }
  }
  public Object loginHandler(Request loginRequest, Response loginResponse) {
    UserService userService = new UserService(authDAO, userDAO);

    try {
      var request = new Gson().fromJson(loginRequest.body(), LoginRequest.class);
      var response = userService.loginUser(request);
      return new Gson().toJson(res);
    } catch(Exception e) {
      loginResponse.status(500);
      return new Gson().toJson(new ErrorMessage(e.getMessage()));
    } catch(DataAccessException e) {
      loginResponse.status(401);
      return new Gson().toJson(new ErrorMessage("Error: unauthorized"));
    }
  }
  public Object registerHandler(Request registerRequest, Response registerResponse) {
    UserService userService = new UserService(authDAO, userDAO);

    try {
      var request = new Gson().fromJson(registerRequest.body(), RegisterRequest.class);
      var result = userService.registerUser(request);
      return new Gson().toJson(result);
    } catch(Exception e) {
      registerResponse.status(500);
      return new Gson().toJson(new ErrorMessage(e.getMessage()));
    } catch(DataAccessException e) {
      if(Objects.equals(e.getMessage(), "User already exists")) {
        registerResponse.status(403);
        return new Gson().toJson(new ErrorMessage("Error: already taken"));
      }
      if(Objects.equals(e.getMessage(), "Bad request")) {
        registerResponse.status(400);
        return new Gson().toJson(new ErrorMessage("Error: bad request"));
      }
      registerResponse.status(500);
      return new Gson().toJson(new ErrorMessage("Error: DataAccessException thrown but not caught correctly"));
    }
  }
  public Object listGamesHandler(Request listRequest, Response listResponse) {
    GamesService gamesService=new GamesService(authDAO, gameDAO);

    try {
      ListGamesRequest request=new ListGamesRequest(listRequest.headers("authorization"));
      var result=gamesService.listGames(request);
      return new Gson().toJson(result);
    } catch (Exception e) {
      listResponse.status(500);
      return new Gson().toJson(new ErrorMessage(e.getMessage()));
    } catch (DataAccessException e) {
      listResponse.status(401);
      return new Gson().toJson(new ErrorMessage("Error: unauthorized"));
    }
  }

  public Object createGameHandler(Request createRequest, Response createResponse) {
    GamesService gamesService = new GamesService(authDAO, gameDAO);

    try {
      String authToken = createRequest.headers("authorization");
      var request = new Gson().fromJson(createRequest.body(), CreateGameRequest.class);
      var result = gamesService.createGame(request, authToken);
      return new Gson().toJson(result);
    } catch(Exception e) {
      createResponse.status(500);
      return new Gson().toJson(new ErrorMessage(e.getMessage()));
    } catch(DataAccessException e) {
      if(Objects.equals(e.getMessage(), "Bad request")) {
        createResponse.status(400);
        return new Gson().toJson(new ErrorMessage("Error: bad request"));
      }
      if(Objects.equals(e.getMessage(), "Unauthorized")) {
        createResponse.status(401);
        return new Gson().toJson(new ErrorMessage("Error: unauthorized"));
      }
      createResponse.status(500);
      return new Gson().toJson(new ErrorMessage("Error: DataAccessException thrown but not caught correctly"));
    }
  }
  public Object joinGameHandler(Request joinRequest, Response joinResponse) {
    GamesService gamesService = new GamesService(authDAO, gameDAO);

    try {
      String authToken = joinRequest.headers("authorization");
      var request = new Gson().fromJson(joinRequest.body(), JoinGameRequest.class);
      gamesService.joinGame(request, authToken);
      return "";
    } catch(Exception e) {
      joinResponse.status(500);
      return new Gson().toJson(new ErrorMessage(e.getMessage()));
    } catch(DataAccessException e) {
      if(Objects.equals(e.getMessage(), "Unauthorized")) {
        joinResponse.status(401);
        return new Gson().toJson(new ErrorMessage("Error: unauthorized"));
      }
      if(Objects.equals(e.getMessage(), "Bad request")) {
        joinResponse.status(400);
        return new Gson().toJson(new ErrorMessage("Error: bad request"));
      }
      if(Objects.equals(e.getMessage(), "Already taken")) {
        joinResponse.status(403);
        return new Gson().toJson(new ErrorMessage("Error: already taken"));
      }
      joinResponse.status(500);
      return new Gson().toJson(new ErrorMessage("Error: DataAccessException thrown but not caught correctly"));
    }
  }
}
