package client;

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

    private void throwError(HttpURLConnection http) throws IOException, ResponseException {
            var status = http.getResponseCode();
            if (!success(status)) {
                throw new ResponseException(status, "failure: " + status);
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
    public RegisterResult register(String username, String password, String email) throws ResponseException {
        try {
            URL url = (new URI(this.url + "/user")).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);

            http.addRequestProperty("Content-Type", "application/json");
            var body = Map.of("username", username,
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
            throw new ResponseException(500, ex.getMessage());
        }
    }

}
