package server;

import com.google.gson.Gson;
import model.register.RegisterResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {
  private final String baseUrl;
  private final Gson gson;

  private static final String USER_ENDPOINT = "/user";
  private static final String SESSION_ENDPOINT = "/session";
  private static final String GAME_ENDPOINT = "/game";

  public ServerFacade(String baseUrl) {
    this.baseUrl = baseUrl;
    this.gson = new Gson();
  }

  private HttpURLConnection createConnection(String endpoint, String method, String authToken) throws IOException, URISyntaxException {
    URL url = (new URI(this.baseUrl + endpoint)).toURL();
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(method);
    connection.setDoOutput(true);
    connection.addRequestProperty("Content-Type", "application/json");
    if (authToken != null) {
      connection.addRequestProperty("authorization", authToken);
    }
    return connection;
  }

  private void handleResponse(HttpURLConnection connection) throws IOException {
    int statusCode=connection.getResponseCode();
    if (!isSuccessful(statusCode)) {
      throw new IOException("Error: " + statusCode + " - " + connection.getResponseMessage());
    }
  }

  private boolean isSuccessful(int status) {
    return status >= 200 && status < 300;
  }

  private <T> T parseResponse(HttpURLConnection connection, Class<T> responseClass) throws IOException {
    try (InputStream inputStream = connection.getInputStream();
         InputStreamReader reader = new InputStreamReader(inputStream)) {
      return gson.fromJson(reader, responseClass);
    }
  }

  private void writeRequestBody(HttpURLConnection connection, Map<String, Object> body) throws IOException {
    try (OutputStream outputStream = connection.getOutputStream()) {
      String jsonBody = gson.toJson(body);
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

  public RegisterResult register(String username, String password, String email) throws IOException, URISyntaxException {
    validateUserInput(username, password, email);
    HttpURLConnection connection = createConnection(USER_ENDPOINT, "POST", null);
    Map<String, Object> body = new HashMap<>();
    body.put("username", username);
    body.put("password", password);
    body.put("email", email);

    writeRequestBody(connection, body);
    handleResponse(connection);
    return parseResponse(connection, RegisterResult.class);
  }
}
