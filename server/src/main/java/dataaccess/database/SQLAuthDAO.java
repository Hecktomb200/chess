package dataaccess.database;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

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

  public void deleteAuth(String authToken) {
    try (var connection = DatabaseManager.getConnection()) {
      var statement = "DELETE FROM auth WHERE authToken=?";
      executeUpdate(statement, authToken);
    } catch (DataAccessException | SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void deleteAllAuth() {
    try (var connection = DatabaseManager.getConnection()) {
      var statement = "TRUNCATE auth";
      executeUpdate(statement);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private void executeUpdate(String statement, Object... parameters) throws DataAccessException {
    try (var connection = DatabaseManager.getConnection()) {
      try (var preparedStatement = connection.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
        for (var i = 0; i < parameters.length; i++) {
          var param = parameters[i];
          if (param instanceof String p) preparedStatement.setString(i + 1, p);
          else if (param == null) preparedStatement.setNull(i + 1, NULL);
        }
        preparedStatement.executeUpdate();

        var resultSet = preparedStatement.getGeneratedKeys();
        if (resultSet.next()) {
          resultSet.getInt(1);
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException(e.toString());
    } catch (DataAccessException e) {
      throw new DataAccessException(e.toString());
    }
  }
}
