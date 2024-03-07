package dataAccess.AuthDAO;

import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLAuthDatabase implements AuthDAO {

    public SQLAuthDatabase() throws DataAccessException {
            String[] createTestTable = {"""            
                    CREATE TABLE if NOT EXISTS auth (
                                    authToken VARCHAR(255) NOT NULL,
                                    username VARCHAR(255) NOT NULL,
                                    PRIMARY KEY (authToken)
                                    )"""
            };
            configureDatabase(createTestTable);
        }

    @Override
    public String createAuth(String username) throws DataAccessException{
        try (var connection = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
            String authToken = UUID.randomUUID().toString();
            executeUpdate(statement, authToken, username);
            return authToken;
        } catch (SQLException e) {
            throw new DataAccessException(e.toString());
        } catch (DataAccessException e) {
            throw new DataAccessException(e.toString());
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

    @Override
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

    @Override
    public void deleteAuth(String authToken) {
        try (var connection = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auth WHERE authToken=?";
            executeUpdate(statement, authToken);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAuthTotal() {
        try (var connection = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE auth";
            executeUpdate(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
