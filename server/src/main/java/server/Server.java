package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.JoinGame.JoinGameRequest;
import model.ListGames.ListGamesRequest;
import model.ListGames.ListGamesResult;
import model.Login.LoginRequest;
import model.Login.LoginResult;
import model.Logout.LogoutRequest;
import model.Register.RegisterRequest;
import model.Register.RegisterResult;
import model.createGame.CreateGameRequest;
import model.createGame.CreateGameResult;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.Objects;

public class Server {
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

    public int run(int desiredPort) {
        authDAO = new AuthDAO();
        gameDAO = new GameDAO();
        userDAO = new UserDAO();

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();
        Spark.post("/user", this::registerHandler);
        Spark.post("/session", this::loginHandler);
        Spark.delete("/session", this::logoutHandler);
        Spark.delete("/db", this::deleteHandler);
        Spark.post("/game", this::createGameHandler);
        Spark.put("/game", this::joinGameHandler);
        Spark.get("/game", this::listGamesHandler);



        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object listGamesHandler(Request request, Response response) {
        Gson gson = new Gson();
        GameService gameService = new GameService(authDAO, gameDAO);

        try {
            ListGamesRequest req = new ListGamesRequest(request.headers("authorization"));
            ListGamesResult res = gameService.listGames(req);
            response.status(200);
            return new Gson().toJson(res);
        } catch(DataAccessException e) {
            return handleDataAccessError(response, e);
        } catch(Exception e) {
            response.status(500);
            return new Gson().toJson(new Error(e.getMessage()));
        }
    }

    private Object joinGameHandler(Request request, Response response) {
        Gson gson = new Gson();
        GameService gameService = new GameService(authDAO, gameDAO);

        try {
            String authToken = request.headers("authorization");
            JoinGameRequest req = gson.fromJson(request.body(), JoinGameRequest.class);
            response.status(200);
            gameService.joinGame(req, authToken);
            return "";
        } catch(DataAccessException e) {
            return handleDataAccessError(response, e);
        } catch(Exception e) {
            response.status(500);
            return new Gson().toJson(new Error(e.getMessage()));
        }
    }

    private Object createGameHandler(Request request, Response response) {
        Gson gson = new Gson();
        GameService gameService = new GameService(authDAO, gameDAO);

        try {
            String authToken = request.headers("authorization");
            CreateGameRequest req = gson.fromJson(request.body(), CreateGameRequest.class);
            CreateGameResult res = gameService.createGame(req, authToken);
            response.status(200);
            return new Gson().toJson(res);
        } catch(DataAccessException e) {
            return handleDataAccessError(response, e);
        } catch(Exception e) {
            response.status(500);
            return new Gson().toJson(new Error(e.getMessage()));
        }
    }

    private Object deleteHandler(Request request, Response response) {
        return "";
    }

    private Object logoutHandler(Request request, Response response) {
        UserService userService = new UserService(authDAO, userDAO);

        try {
            LogoutRequest req = new LogoutRequest(request.headers("authorization"));
            userService.logoutUser(req);
            return "";
        } catch(DataAccessException e) {
            return handleDataAccessError(response, e);
        } catch(Exception e) {
            return errorResponse(response, 500, "Error: unexpected server error");
        }
    }

    private Object loginHandler(Request request, Response response) {
        Gson gson = new Gson();
        UserService userService = new UserService(authDAO, userDAO);

        try {
            LoginRequest req = gson.fromJson(request.body(), LoginRequest.class);
            LoginResult res = userService.loginUser(req);
            response.status(200);
            return new Gson().toJson(res);
        } catch(DataAccessException e) {
            return handleDataAccessError(response, e);
        } catch(Exception e) {
            return errorResponse(response, 500, "Error: unexpected server error");
        }
    }

    private Object registerHandler(Request request, Response response) {
        Gson gson = new Gson();
        UserService userService = new UserService(authDAO, userDAO);

        try {
            RegisterRequest req = gson.fromJson(request.body(), RegisterRequest.class);
            RegisterResult res = userService.registerUser(req);
            response.status(200);
            return new Gson().toJson(res);
        } catch(DataAccessException e) {
            return handleDataAccessError(response, e);
        } catch(Exception e) {
            return errorResponse(response, 500, "Error: unexpected server error");
        }
    }

    private Object handleDataAccessError(Response response, DataAccessException e) {
        switch (e.getMessage()) {
            case "Already taken":
                return errorResponse(response, 403, "Error: already taken");
            case "Bad request":
                return errorResponse(response, 400, "Error: bad request");
            case "Unauthorized":
                return errorResponse(response, 401, "Error: unauthorized");
            default:
                return errorResponse(response, 500, "Error: unexpected server error");
        }
    }

    private Object errorResponse(Response response, int status, String message) {
        response.status(status);
        return new Gson().toJson(new Error(message));
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
