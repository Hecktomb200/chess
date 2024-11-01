package dataaccess.database;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO extends GameDAO {

  public SQLGameDAO() throws DataAccessException {
    DatabaseManager.createDatabase();
    DatabaseManager.createGameTable();
  }

  @Override
  public Integer createGame(String gameName) throws DataAccessException {
    if (gameName == null || gameName.isEmpty()) {
      throw new DataAccessException("Error: bad request");
    }
    String insertSQL="INSERT INTO game (whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?)";
    ChessGame newChess=new ChessGame();
    String jString=new Gson().toJson(newChess);

    try (Connection connection=DatabaseManager.getConnection();
         PreparedStatement preparedStatement=connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setNull(1, Types.VARCHAR); // whiteUsername
      preparedStatement.setNull(2, Types.VARCHAR); // blackUsername
      preparedStatement.setString(3, gameName);
      preparedStatement.setString(4, jString);
      preparedStatement.executeUpdate();

      try (ResultSet generatedKeys=preparedStatement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          return generatedKeys.getInt(1);
        } else {
          throw new DataAccessException("Failed to create game, no ID obtained.");
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException(e.getMessage());
    }
  }

  @Override
  public void updateGame(GameData game) throws DataAccessException {
    if (game == null) {
      throw new DataAccessException("Error: bad request");
    }
    String updateSQL="UPDATE game SET whiteUsername=?, blackUsername=?, gameName=?, chessGame=? WHERE gameID=?";
    try (Connection connection=DatabaseManager.getConnection();
         PreparedStatement preparedStatement=connection.prepareStatement(updateSQL)) {
      preparedStatement.setString(1, game.whiteUsername());
      preparedStatement.setString(2, game.blackUsername());
      preparedStatement.setString(3, game.gameName());
      preparedStatement.setString(4, new Gson().toJson(game.game()));
      preparedStatement.setInt(5, game.gameID());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException(e.getMessage());
    }
  }

  @Override
  public void deleteGames() throws DataAccessException {
    String deleteSQL="TRUNCATE TABLE game";
    try (Connection connection=DatabaseManager.getConnection();
         PreparedStatement preparedStatement=connection.prepareStatement(deleteSQL)) {
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new DataAccessException(e.getMessage());
    }
  }

  @Override
  public GameData getGame(int gameID) throws DataAccessException {
    String selectSQL="SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID=?";
    try (Connection connection=DatabaseManager.getConnection();
         PreparedStatement preparedStatement=connection.prepareStatement(selectSQL)) {
      preparedStatement.setInt(1, gameID);
      try (ResultSet resultSet=preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          int newGameID=resultSet.getInt("gameID");
          String whiteUsername=resultSet.getString("whiteUsername");
          String blackUsername=resultSet.getString("blackUsername");
          String gameName=resultSet.getString("gameName");
          String jString=resultSet.getString("chessGame");
          ChessGame chessGame=new Gson().fromJson(jString, ChessGame.class);
          return new GameData(newGameID, whiteUsername, blackUsername, gameName, chessGame);
        } else {
          throw new DataAccessException("Game not found.");
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException(e.getMessage());
    }
  }

  @Override
  public Collection<GameData> listGames() throws DataAccessException {
    Collection<GameData> games=new ArrayList<>();
    String selectSQL="SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game";
    try (Connection connection=DatabaseManager.getConnection();
         PreparedStatement preparedStatement=connection.prepareStatement(selectSQL);
         ResultSet resultSet=preparedStatement.executeQuery()) {
      while (resultSet.next()) {
        int newGameID=resultSet.getInt("gameID");
        String whiteUsername=resultSet.getString("whiteUsername");
        String blackUsername=resultSet.getString("blackUsername");
        String gameName=resultSet.getString("gameName");
        String jString=resultSet.getString("chessGame");
        ChessGame chessGame=new Gson().fromJson(jString, ChessGame.class);
        games.add(new GameData(newGameID, whiteUsername, blackUsername, gameName, chessGame));
      }
    } catch (SQLException e) {
      throw new DataAccessException(e.getMessage());
    }
    return games;
  }
}