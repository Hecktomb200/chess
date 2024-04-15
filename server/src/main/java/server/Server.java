package server;

import com.google.gson.Gson;
import dataAccess.AuthDAO.AuthDAO;
import dataAccess.AuthDAO.SQLAuthDatabase;
import dataAccess.DataAccessException;
import dataAccess.GameDAO.GameDAO;
import dataAccess.GameDAO.SQLGameDatabase;
import dataAccess.UserDAO.SQLUserDatabase;
import dataAccess.UserDAO.UserDAO;
import model.createGame.CreateGameRequest;
import model.joinGame.JoinGameRequest;
import model.listGames.ListGamesRequest;
import model.login.LoginRequest;
import model.logout.LogoutRequest;
import model.register.RegisterRequest;
import server.websocket.WebsocketHandler;
import service.GamesService;
import service.RemoveService;
import service.UserService;
import server.websocket.*;
import spark.*;
import webSocketMessages.serverMessages.Error;

import java.util.Objects;

public class Server {

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;
    private WebsocketHandler websocketHandler;
    UserService userService;
    GamesService gamesService;
    ServerHandler serverHandler;

//    public Server() {
//        try {
//            userDAO = new SQLUserDatabase();
//        } catch (DataAccessException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            authDAO = new SQLAuthDatabase();
//        } catch (DataAccessException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            gameDAO = new SQLGameDatabase();
//        } catch (DataAccessException e) {
//            throw new RuntimeException(e);
//        }
//        userService = new UserService(authDAO, userDAO);
//        serverHandler = new ServerHandler();
//    }

    public int run(int desiredPort) {
        try {
            userDAO = new SQLUserDatabase();
            authDAO = new SQLAuthDatabase();
            gameDAO = new SQLGameDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        websocketHandler = new WebsocketHandler(new WebsocketSession(), new GamesService(authDAO, gameDAO));

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/connect",websocketHandler);
        Spark.delete("/db", this::deleteHandler);
        Spark.post("/user", this::registerHandler);
        Spark.post("/session", this::loginHandler);
        Spark.delete("/session", this::logoutHandler);
        Spark.get("/game", this::listGamesHandler);
        Spark.post("/game", this::createGameHandler);
        Spark.put("/game", this::joinGameHandler);


        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object registerHandler(Request request, Response response) {
        UserService userService = new UserService(authDAO, userDAO);

        try {
            var req = new Gson().fromJson(request.body(), RegisterRequest.class);
            var res = userService.registerUser(req);
            return new Gson().toJson(res);
        } catch(DataAccessException e) {
            if(Objects.equals(e.getMessage(), "User already exists")) {
                response.status(403);
                return new Gson().toJson(new Error("Error: already taken"));
            }
            if(Objects.equals(e.getMessage(), "Bad request")) {
                response.status(400);
                return new Gson().toJson(new Error("Error: bad request"));
            }
            response.status(500);
            return new Gson().toJson(new Error("Error: DataAccessException thrown but not caught correctly"));
        } catch(Exception e) {
            response.status(500);
            return new Gson().toJson(new Error(e.getMessage()));
        }
    }

    private Object loginHandler(Request request, Response response) {
        UserService userService = new UserService(authDAO, userDAO);

        try {
            var req = new Gson().fromJson(request.body(), LoginRequest.class);
            var res = userService.loginUser(req);
            return new Gson().toJson(res);
        } catch(DataAccessException e) {
            response.status(401);
            return new Gson().toJson(new Error("Error: unauthorized"));
        } catch(Exception e) {
            response.status(500);
            return new Gson().toJson(new Error(e.getMessage()));
        }
    }

    private Object logoutHandler(Request request, Response response) {
        UserService userService = new UserService(authDAO, userDAO);

        try {
            LogoutRequest req = new LogoutRequest(request.headers("authorization"));
            userService.logoutUser(req);
            return "";
        } catch(DataAccessException e) {
            response.status(401);
            return new Gson().toJson(new Error("Error: unauthorized"));
        } catch(Exception e) {
            response.status(500);
            return new Gson().toJson(new Error(e.getMessage()));
        }
    }

    private Object createGameHandler(Request request, Response response) {
        GamesService gameService = new GamesService(authDAO, gameDAO);

        try {
            String authToken = request.headers("authorization");
            var req = new Gson().fromJson(request.body(), CreateGameRequest.class);
            var res = gameService.createGame(req, authToken);
            return new Gson().toJson(res);
        } catch(DataAccessException e) {
            if(Objects.equals(e.getMessage(), "Bad request")) {
                response.status(400);
                return new Gson().toJson(new Error("Error: bad request"));
            }
            if(Objects.equals(e.getMessage(), "Unauthorized")) {
                response.status(401);
                return new Gson().toJson(new Error("Error: unauthorized"));
            }
            response.status(500);
            return new Gson().toJson(new Error("Error: DataAccessException thrown but not caught correctly"));
        } catch(Exception e) {
            response.status(500);
            return new Gson().toJson(new Error(e.getMessage()));
        }
    }

    private Object joinGameHandler(Request request, Response response) {
        GamesService gameService = new GamesService(authDAO, gameDAO);

        try {
            String authToken = request.headers("authorization");
            var req = new Gson().fromJson(request.body(), JoinGameRequest.class);
            gameService.joinGame(req, authToken);
            return "";
        } catch(DataAccessException e) {
            if(Objects.equals(e.getMessage(), "Unauthorized")) {
                response.status(401);
                return new Gson().toJson(new Error("Error: unauthorized"));
            }
            if(Objects.equals(e.getMessage(), "Bad request")) {
                response.status(400);
                return new Gson().toJson(new Error("Error: bad request"));
            }
            if(Objects.equals(e.getMessage(), "Already taken")) {
                response.status(403);
                return new Gson().toJson(new Error("Error: already taken"));
            }
            response.status(500);
            return new Gson().toJson(new Error("Error: DataAccessException thrown but not caught correctly"));
        } catch(Exception e) {
            response.status(500);
            return new Gson().toJson(new Error(e.getMessage()));
        }
    }

    private Object listGamesHandler(Request request, Response response) {
        GamesService gameService = new GamesService(authDAO, gameDAO);

        try {
            ListGamesRequest req = new ListGamesRequest(request.headers("authorization"));
            var res = gameService.listGames(req);
            return new Gson().toJson(res);
        } catch(DataAccessException e) {
            response.status(401);
            return new Gson().toJson(new Error("Error: unauthorized"));
        } catch(Exception e) {
            response.status(500);
            return new Gson().toJson(new Error(e.getMessage()));
        }
    }

    private Object deleteHandler(Request request, Response response) {
        RemoveService removeService = new RemoveService(authDAO, userDAO, gameDAO);

        try {
            removeService.removeAllServices();
            websocketHandler.clearResignedIDs();
        } catch (Exception e) {
            response.status(500);
            return new Gson().toJson(new Error(e.toString()));
        }
        return "";
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
