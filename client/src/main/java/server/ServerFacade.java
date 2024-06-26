package server;

import client.ResponseException;
import com.google.gson.Gson;
import model.createGame.CreateGameResult;
import model.listGames.ListGamesResult;
import model.login.LoginResult;
import model.register.RegisterResult;

import java.io.*;
import java.net.*;
import java.util.Map;

public class ServerFacade {
    private String url;

    public ServerFacade(String url) {
        this.url = url;
    }

    private boolean success(int status) {
        return status / 100 == 2;
    }

    private void throwError(HttpURLConnection http) throws IOException, client.ResponseException {
            var status = http.getResponseCode();
            if (!success(status)) {
                throw new client.ResponseException(status, "failure: " + status);
            }
        }

    private static <T> T getBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }
    public RegisterResult register(String username, String password, String email) throws client.ResponseException {
        try {
            URL url = (new URI(this.url + "/user")).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            Map body;

            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");

            body = Map.of("username", username,
                    "password", password,
                    "email", email);
            try (var outputStream = http.getOutputStream()) {
                var jsonBody = new Gson().toJson(body);
                outputStream.write(jsonBody.getBytes());
            }
            http.connect();
            throwError(http);
            return getBody(http, RegisterResult.class);
        } catch (Exception ex) {
            throw new client.ResponseException(500, ex.getMessage());
        }
    }

    public LoginResult login(String username, String password) throws client.ResponseException {
        try {
            URL url = (new URI(this.url + "/session")).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            Map body;

            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");

            body = Map.of("username", username,
                    "password", password);

            try (var outputStream = http.getOutputStream()) {
                var jsonBody = new Gson().toJson(body);
                outputStream.write(jsonBody.getBytes());
            }
            http.connect();
            throwError(http);
            return getBody(http, LoginResult.class);
        } catch (Exception ex) {
            throw new client.ResponseException(500, ex.getMessage());
        }
    }

    public void logout(String authToken) throws client.ResponseException {
        try {
            URL url = (new URI(this.url + "/session")).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("DELETE");
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            http.addRequestProperty("authorization", authToken);

            http.connect();
            throwError(http);
        } catch (Exception ex) {
            throw new client.ResponseException(500, ex.getMessage());
        }
    }

    public ListGamesResult listGames(String authToken) throws client.ResponseException {
        try {
            URL url = (new URI(this.url + "/game")).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            http.addRequestProperty("authorization", authToken);

            http.connect();
            throwError(http);
            return getBody(http, ListGamesResult.class);
        } catch (Exception ex) {
            throw new client.ResponseException(500, ex.getMessage());
        }
    }

    public CreateGameResult createGame(String gameName, String authToken) throws client.ResponseException {
        try {
            URL url = (new URI(this.url + "/game")).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            Map body;

            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            http.addRequestProperty("authorization", authToken);

            body = Map.of("gameName", gameName);
            try (var outputStream = http.getOutputStream()) {
                var jsonBody = new Gson().toJson(body);
                outputStream.write(jsonBody.getBytes());
            }
            http.connect();
            throwError(http);
            return getBody(http, CreateGameResult.class);
        } catch (Exception ex) {
            throw new client.ResponseException(500, ex.getMessage());
        }
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws client.ResponseException {
        try {
            URL url = (new URI(this.url + "/game")).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            Map body;

            http.setRequestMethod("PUT");
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            http.addRequestProperty("authorization", authToken);
            if (playerColor == null) {
                body = Map.of("gameID", gameID);
            } else {
                body = Map.of("playerColor", playerColor, "gameID", gameID);
            }
            try (var outputStream = http.getOutputStream()) {
                var jsonBody = new Gson().toJson(body);
                outputStream.write(jsonBody.getBytes());
            }
            http.connect();
            throwError(http);
        } catch (Exception ex) {
            throw new client.ResponseException(500, ex.getMessage());
        }
    }

    public void delete() throws client.ResponseException {
        try {
            URL url = (new URI(this.url + "/db")).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("DELETE");
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            http.connect();
            throwError(http);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

}
