package dataaccess.database;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO {



  public AuthData createAuth(String username) throws DataAccessException {
    String authToken = UUID.randomUUID().toString();
    String insertSQL = "INSERT INTO auth (authToken, username) VALUES (?, ?)";

    try (var connection = DatabaseManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
      preparedStatement.setString(1, authToken);
      preparedStatement.setString(2, username);
      preparedStatement.executeUpdate();
      return new AuthData(authToken, username);
    } catch (SQLException e) {
      throw new DataAccessException("Failed to create auth: " + e.getMessage());
    }
  }

  public AuthData getAuth(String authToken) throws DataAccessException{
    try (var connection = DatabaseManager.getConnection()) {
      var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
      try (var preparedStatement = connection.prepareStatement(statement)) {
        preparedStatement.setString(1, authToken);
        try (var resultSet = preparedStatement.executeQuery()) {
          if (resultSet.next()) {
            var newToken = resultSet.getString(1);
            var username = resultSet.getString(2);
            return new AuthData(newToken, username);
          }
        }
      }
    } catch (Exception e) {
      throw new DataAccessException(e.toString());
    }
    return null;
  }
}
