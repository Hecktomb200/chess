package server;

import dataAccess.AuthDAO.MemoryAuthDAO;
import dataAccess.AuthDAO.AuthDAO;
import dataAccess.GameDAO.MemoryGameDAO;
import dataAccess.GameDAO.GameDAO;
import dataAccess.UserDAO.MemoryUserDAO;
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
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
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
