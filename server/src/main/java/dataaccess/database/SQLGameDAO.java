package dataaccess.database;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

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
  
}
