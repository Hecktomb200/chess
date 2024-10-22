package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.Register.RegisterRequest;
import model.Register.RegisterResult;
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

        Spark.awaitInitialization();
        return Spark.port();
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
