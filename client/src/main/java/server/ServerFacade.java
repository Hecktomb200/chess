package server;

import com.google.gson.Gson;
import model.creategame.CreateGameResult;
import model.listgames.ListGamesResult;
import model.login.LoginResult;
import model.register.RegisterResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {
  private final String baseUrl;
  private final Gson gson;

  private static final String USER_ENDPOINT="/user";
  private static final String SESSION_ENDPOINT="/session";
  private static final String GAME_ENDPOINT="/game";

  public ServerFacade(String baseUrl) {
    this.baseUrl=baseUrl;
    this.gson=new Gson();
  }

  private HttpURLConnection createConnection(String endpoint, String method, String authToken) throws IOException, URISyntaxException {
    URL url=(new URI(this.baseUrl + endpoint)).toURL();
    HttpURLConnection connection=(HttpURLConnection) url.openConnection();
    connection.setRequestMethod(method);
    connection.setDoOutput(true);
    connection.addRequestProperty("Content-Type", "application/json");
    if (authToken != null) {
      connection.addRequestProperty("authorization", authToken);
    }
    return connection;
  }

  private void handleResponse(HttpURLConnection connection) throws Exception {
    int statusCode=connection.getResponseCode();
    if (!isSuccessful(statusCode)) {
      String sBody = new String(connection.getErrorStream().readAllBytes());
      String message = "";
      Map body = null;
     try {
        body=gson.fromJson(sBody, Map.class);
        message=(String) body.get("message");
      } catch (Exception e) {
       throw new Exception("Received unrecognized response from server: " + sBody);
     }
      if (statusCode == 403 && "Error: already taken".equals(message)) {
        throw new IOException("Username is already taken.");
      }
      if (statusCode == 401 && "Error: unauthorized".equals(message)) {
        throw new IOException("Incorrect password.");
      }
      throw new IOException("Error: " + statusCode + " - " + body.get("message"));
    }
  }

  private boolean isSuccessful(int status) {
    return status >= 200 && status < 300;
  }

  private <T> T parseResponse(HttpURLConnection connection, Class<T> responseClass) throws IOException {
    try (InputStream inputStream=connection.getInputStream();
         InputStreamReader reader=new InputStreamReader(inputStream)) {
      return gson.fromJson(reader, responseClass);
    }
  }

  private void writeRequestBody(HttpURLConnection connection, Map<String, Object> body) throws IOException {
    try (OutputStream outputStream=connection.getOutputStream()) {
      String jsonBody=gson.toJson(body);
      outputStream.write(jsonBody.getBytes());
    }
  }

  private void validateUserInput(String username, String password, String email) {
    if (username == null || username.isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty.");
    }
    if (password == null || password.isEmpty()) {
      throw new IllegalArgumentException("Password cannot be null or empty.");
    }
    if (email != null && email.isEmpty()) {
      throw new IllegalArgumentException("Email cannot be empty if provided.");
    }
  }

  public RegisterResult registerUser(String username, String password, String email) throws Exception {
    validateUserInput(username, password, email);
    HttpURLConnection connection=createConnection(USER_ENDPOINT, "POST", null);
    Map<String, Object> body=new HashMap<>();
    body.put("username", username);
    body.put("password", password);
    body.put("email", email);

    writeRequestBody(connection, body);
    handleResponse(connection);
    return parseResponse(connection, RegisterResult.class);
  }

  public LoginResult loginUser(String username, String password) throws Exception {
    validateUserInput(username, password, null);
    HttpURLConnection connection=createConnection(SESSION_ENDPOINT, "POST", null);
    Map<String, Object> body=new HashMap<>();
    body.put("username", username);
    body.put("password", password);

    writeRequestBody(connection, body);
    handleResponse(connection);
    return parseResponse(connection, LoginResult.class);
  }

  public void logoutUser(String authToken) throws Exception {
    if (authToken == null) {
      throw new IllegalArgumentException("Authorization token cannot be null.");
    }
    HttpURLConnection connection = createConnection(SESSION_ENDPOINT, "DELETE", authToken);
    handleResponse(connection);
  }

  public ListGamesResult listGames(String authToken) throws Exception {
    if (authToken == null) {
      throw new IllegalArgumentException("Authorization token cannot be null.");
    }
    HttpURLConnection connection = createConnection(GAME_ENDPOINT, "GET", authToken);
    handleResponse(connection);
    return parseResponse(connection, ListGamesResult.class);
  }

  public CreateGameResult createGame(String gameName, String authToken) throws Exception {
    if (gameName == null || authToken == null) {
      throw new IllegalArgumentException("Game name and authorization token cannot be null.");
    }
    HttpURLConnection connection = createConnection(GAME_ENDPOINT, "POST", authToken);
    Map<String, Object> body = new HashMap<>();
    body.put("gameName", gameName);

    writeRequestBody(connection, body);
    handleResponse(connection);
    return parseResponse(connection, CreateGameResult.class);
  }

  public void joinGame(String authToken, String playerColor, int gameID) throws Exception {
    if (authToken == null) {
      throw new IllegalArgumentException("Authorization token cannot be null.");
    }
    HttpURLConnection connection = createConnection(GAME_ENDPOINT, "PUT", authToken);
    Map<String, Object> body = new HashMap<>();
    body.put("gameID", gameID);
    if (playerColor != null) {
      body.put("playerColor", playerColor);
    }

    writeRequestBody(connection, body);
    handleResponse(connection);
  }

  public void delete() throws URISyntaxException, IOException {
    HttpURLConnection connection = null;
    try {
      URI resourceUri = new URI(this.baseUrl + "/db");
      URL endpoint = resourceUri.toURL();
      connection = (HttpURLConnection) endpoint.openConnection();

      connection.setRequestMethod("DELETE");
      connection.setDoOutput(true);
      connection.addRequestProperty("Content-Type", "application/json");
      connection.connect();
      handleResponse(connection);
    } catch (Exception ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }
}
