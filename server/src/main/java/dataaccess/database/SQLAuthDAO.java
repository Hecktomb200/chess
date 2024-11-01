package dataaccess.database;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO extends AuthDAO {

  public SQLAuthDAO() throws DataAccessException {
    DatabaseManager.createDatabase();
    DatabaseManager.createAuthTable();
  }

  @Override
  public String createAuth(String username) throws DataAccessException {
    if (username == null || username.isEmpty()) {
      throw new DataAccessException("Error: unauthorized");
    }
    String authToken = UUID.randomUUID().toString();
    String insertSQL = "INSERT INTO auth (authToken, username) VALUES (?, ?)";

    try (Connection connection = DatabaseManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
      preparedStatement.setString(1, authToken);
      preparedStatement.setString(2, username);
      preparedStatement.executeUpdate();
      return authToken;
    } catch (SQLException e) {
      throw new DataAccessException("Failed to create auth: " + e.getMessage());
    }
  }

  @Override
  public AuthData getAuth(String authToken) throws DataAccessException {
    if (authToken == null || authToken.isEmpty()) {
      throw new DataAccessException("Error: bad request");
    }
    String selectSQL = "SELECT authToken, username FROM auth WHERE authToken=?";

    try (Connection connection = DatabaseManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
      preparedStatement.setString(1, authToken);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          String token = resultSet.getString("authToken");
          String username = resultSet.getString("username");
          return new AuthData(token, username);
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException("Failed to get auth: " + e.getMessage());
    }
    return null;
  }

  @Override
  public void deleteAuth(String authToken) throws DataAccessException {
    if (authToken == null || authToken.isEmpty()) {
      throw new DataAccessException("Error: bad request");
    }
    String deleteSQL = "DELETE FROM auth WHERE authToken=?";

    try (Connection connection = DatabaseManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
      preparedStatement.setString(1, authToken);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("Failed to delete auth: " + e.getMessage());
    }
  }

  @Override
  public void deleteAllAuth() throws DataAccessException {
    String truncateSQL = "TRUNCATE TABLE auth";

    try (Connection connection = DatabaseManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(truncateSQL)) {
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("Failed to truncate auth: " + e.getMessage());
    }
  }
}