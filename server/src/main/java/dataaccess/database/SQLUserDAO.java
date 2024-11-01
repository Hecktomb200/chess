package dataaccess.database;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLUserDAO extends UserDAO {

  public SQLUserDAO() throws DataAccessException {
    DatabaseManager.createDatabase();
    DatabaseManager.createUserTable();
  }

  @Override
  public void createUser(String username, String password, String email) throws DataAccessException {
    String insertStatement="INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
    executeUpdate(insertStatement, username, password, email);
  }

  @Override
  public UserData getUser(String username) throws DataAccessException {
    String selectStatement="SELECT username, password, email FROM user WHERE username = ?";
    try (Connection connection=DatabaseManager.getConnection();
         PreparedStatement preparedStatement=connection.prepareStatement(selectStatement)) {

      preparedStatement.setString(1, username);
      try (ResultSet resultSet=preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          String password=resultSet.getString("password");
          String email=resultSet.getString("email");
          return new UserData(username, password, email);
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException("Error retrieving user: " + e.getMessage());
    }
    return null;
  }

  @Override
  public void clearUsers() throws DataAccessException {
    String truncateStatement="TRUNCATE user";
    executeUpdate(truncateStatement);
  }

  private int executeUpdate(String statement, Object... parameters) throws DataAccessException {
    try (Connection connection=DatabaseManager.getConnection();
         PreparedStatement preparedStatement=connection.prepareStatement(statement, RETURN_GENERATED_KEYS)) {

      setParameters(preparedStatement, parameters);
      preparedStatement.executeUpdate();

      try (ResultSet resultSet=preparedStatement.getGeneratedKeys()) {
        if (resultSet.next()) {
          return resultSet.getInt(1);
        }
      }
      return 0;
    } catch (SQLException e) {
      throw new DataAccessException("Error executing update: " + e.getMessage());
    }
  }

  private void setParameters(PreparedStatement preparedStatement, Object... parameters) throws SQLException {
    for (int i=0; i < parameters.length; i++) {
      Object param=parameters[i];
      if (param instanceof String) {
        preparedStatement.setString(i + 1, (String) param);
      } else if (param == null) {
        preparedStatement.setNull(i + 1, java.sql.Types.NULL);
      }
    }
  }
}