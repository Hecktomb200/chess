package dataAccess.GameDAO;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDatabase implements GameDAO {

    public SQLGameDatabase() {
        try (var connection = DatabaseManager.getConnection()) {
            String[] createTestTable = { """            
                    CREATE TABLE if NOT EXISTS game (
                                    gameID INT NOT NULL,
                                    whiteUsername VARCHAR(255),
                                    blackUsername VARCHAR(255),
                                    gameName VARCHAR(255),
                                    chessGame TEXT,
                                    PRIMARY KEY (gameID)
                                    )"""
        };
        configureDatabase(createTestTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Integer createGame(String gameName) {
        try (var connection = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO game (whiteUserName, blackUserName, gameName, gameData) VALUES (?, ?, ?, ?)";
            ChessGame newChess = new ChessGame();
            var jString = new Gson().toJson(newChess);
            return executeUpdate(statement, null, null, gameName, jString);
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

    @Override
    public void updateGame(GameData game) throws DataAccessException{
        int gameID = game.gameID();
        try (var connection = DatabaseManager.getConnection()) {
            var statement = "UPDATE game SET whiteUserName = ?, blackUserName = ?, gameName = ?, gameData = ? WHERE gameID=?";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, game.whiteUsername());
                preparedStatement.setString(2, game.blackUsername());
                preparedStatement.setString(3, game.gameName());
                preparedStatement.setString(4, new Gson().toJson(game.game()));
                preparedStatement.setInt(5, gameID);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.toString());
        }
    }

    @Override
    public void deleteGames() {
        try (var connection = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE game";
            executeUpdate(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUserName, blackUserName, gameName, gameData FROM game WHERE gameID=?";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        var newgameID = resultSet.getInt("gameID");
                        var whiteUsername = resultSet.getString("whiteUsername");
                        var blackUsername = resultSet.getString("blackUsername");
                        var gameName = resultSet.getString("gameName");
                        var jString = resultSet.getString("gameData");
                        var chessGame = new Gson().fromJson(jString, ChessGame.class);
                        return new GameData(newgameID, whiteUsername, blackUsername, gameName, chessGame);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.toString());
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException{
        var result = new ArrayList<GameData>();
        try (var connection = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUserName, blackUserName, gameName, gameData FROM game";
            try (var preparedStatement = connection.prepareStatement(statement)) {
                try (var resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        var gameID = resultSet.getInt("gameID");
                        var whiteUsername = resultSet.getString("whiteUsername");
                        var blackUsername = resultSet.getString("blackUsername");
                        var gameName = resultSet.getString("gameName");
                        var jString = resultSet.getString("gameData");
                        var chessGame = new Gson().fromJson(jString, ChessGame.class);
                        result.add(new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.toString());
        }
        return result;
    }
}