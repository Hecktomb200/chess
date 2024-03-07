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
import service.GamesService;
import service.UserService;
import spark.*;

public class Server {

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;
    UserService userService;
    GamesService gamesService;
    ServerHandler serverHandler;

    public Server() {
        userDAO = new SQLUserDatabase();
        try {
            authDAO = new SQLAuthDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        gameDAO = new SQLGameDatabase();
        userService = new UserService(authDAO, userDAO);
        serverHandler = new ServerHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", serverHandler::clear);
        Spark.post("/user", serverHandler::registerHandler);
        Spark.post("/session", serverHandler::loginHandler);
        Spark.delete("/session", serverHandler::logoutHandler);
        Spark.get("/game", serverHandler::listGamesHandler);
        Spark.post("/game", serverHandler::createGameHandler);
        Spark.put("/game", serverHandler::joinGameHandler);


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
