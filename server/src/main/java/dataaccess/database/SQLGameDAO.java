package dataaccess.database;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.GameData;

import java.sql.*;

public class SQLGameDAO {

  public int createGame(String gameName) throws DataAccessException {
    String insertSQL = "INSERT INTO game (whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?)";
    ChessGame newChess = new ChessGame();
    String gameJson = new Gson().toJson(newChess);

    try (Connection connection = DatabaseManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setNull(1, Types.VARCHAR);
      preparedStatement.setNull(2, Types.VARCHAR);
      preparedStatement.setString(3, gameName);
      preparedStatement.setString(4, gameJson);
      preparedStatement.executeUpdate();

      try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          return generatedKeys.getInt(1);
        } else {
          throw new DataAccessException("Creating game failed, no ID obtained.");
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException("Failed to create game: " + e.getMessage());
    }
  }

  public GameData getGame(int gameID) throws DataAccessException {
    String selectSQL = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID=?";

    try (Connection connection = DatabaseManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
      preparedStatement.setInt(1, gameID);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          int id = resultSet.getInt("gameID");
          String whiteUsername = resultSet.getString("whiteUsername");
          String blackUsername = resultSet.getString("blackUsername");
          String gameName = resultSet.getString("gameName");
          String chessGameJson = resultSet.getString("chessGame");
          ChessGame chessGame = new Gson().fromJson(chessGameJson, ChessGame.class);
          return new GameData(id, whiteUsername, blackUsername, gameName, chessGame);
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException("Failed to get game: " + e.getMessage());
    }
    return null;
  }

  public void updateGame(GameData game) throws DataAccessException {
    String updateSQL = "UPDATE game SET whiteUsername=?, blackUsername=?, gameName=?, chessGame=? WHERE gameID=?";

    try (Connection connection = DatabaseManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
      preparedStatement.setString(1, game.whiteUsername());
      preparedStatement.setString(2, game.blackUsername());
      preparedStatement.setString(3, game.gameName());
      preparedStatement.setString(4, new Gson().toJson(game.game()));
      preparedStatement.setInt(5, game.gameID());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("Failed to update game: " + e.getMessage());
    }
  }

  public void clear() throws DataAccessException {
    String truncateSQL = "TRUNCATE TABLE game";

    try (Connection connection = DatabaseManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(truncateSQL)) {
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException("Failed to clear games: " + e.getMessage());
    }
  }
}
