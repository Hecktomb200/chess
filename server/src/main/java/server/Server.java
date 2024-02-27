package server;

import dataAccess.AuthDAO.MemoryAuthDAO;
import dataAccess.AuthDAO.SQLAuthDAO;
import dataAccess.GameDAO.MemoryGameDAO;
import dataAccess.GameDAO.SQLGameDAO;
import dataAccess.UserDAO.MemoryUserDAO;
import dataAccess.UserDAO.SQLUserDAO;
import dataAccess.*;
import org.w3c.dom.UserDataHandler;
import service.GamesService;
import service.UserService;
import service.RemoveService;
import model.*;
import com.google.gson.Gson;
import spark.*;

public class Server {

    SQLUserDAO userDAO;
    SQLAuthDAO authDAO;
    SQLGameDAO gameDAO;
    UserService userService;
    GamesService gamesService;
    UserDataHandler userHandler;

    public Server() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(authDAO, userDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clear);
        Spark.post("/user", userHandler::registerHandler);
        Spark.post("/session", userHandler::loginHandler);
        Spark.delete("/session", userHandler::logoutHandler);
        Spark.get("/game", gameHandler::listGamesHandler);
        Spark.post("/game", gameHandler::createGameHandler);
        Spark.put("/game", gameHandler::joinGameHandler);


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clear(Request requestClear, Response responseClear) {

    }
}
