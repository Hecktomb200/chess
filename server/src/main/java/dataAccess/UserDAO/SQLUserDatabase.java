package dataAccess.UserDAO;

import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import model.UserData;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLUserDatabase implements UserDAO {

    public SQLUserDatabase() {
        try (var connection = DatabaseManager.getConnection()) {
            String[] createTestTable = {"""            
                    CREATE TABLE if NOT EXISTS user (
                                    username VARCHAR(255) NOT NULL,
                                    password VARCHAR(255) NOT NULL,
                                    email VARCHAR(255),
                                    PRIMARY KEY (username)
                                    )"""
            };
            configureDatabase(createTestTable);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createUser(String username, String password, String email) {
        try (var connection = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO user (username, password, email) VALUES(?, ?, ?)";
                executeUpdate(statement, username, password, email);
            } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE username=?";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                try (var resultSet = preparedStatement.executeQuery()) {
                    resultSet.next();
                    var password = resultSet.getString("password");
                    var email = resultSet.getString("email");
                    return new UserData(username, password, email);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.toString());
        }
        //return null;
    }

    @Override
    public void deleteUsers() {
        try (var connection = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE user";
            executeUpdate(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private int executeUpdate(String statement, Object... parameters) {
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
                    return resultSet.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void configureDatabase(String[] statements) throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var connection = DatabaseManager.getConnection()) {
            for (var statement : statements) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.toString());
        }
    }
}
