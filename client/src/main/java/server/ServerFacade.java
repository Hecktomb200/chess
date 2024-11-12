package server;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ServerFacade {
  private final String baseUrl;
  private final Gson gson;

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
}
