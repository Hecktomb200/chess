package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import model.listgames.ListGamesResult;
import server.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class GameplayUI {
  private final ServerFacade serverFacade;
  private final String username;

  private final String authToken;
  private GameData gameData;
  public GameplayUI(GameData game, String sURL, String authToken, String username) {
    serverFacade = new ServerFacade(sURL);
    this.authToken = authToken;
    gameData = game;
    this.username = username;
  }

  public void run() {
    try (Scanner scanner=new Scanner(System.in)) {
      String command=" ";
      do {
        System.out.print("\n" + SET_TEXT_COLOR_YELLOW + "[IN_GAME] >>> " + SET_TEXT_COLOR_GREEN);
        command=scanner.nextLine();
        processCommand(command);
        System.out.print(SET_TEXT_COLOR_YELLOW + command);
      } while (!command.equals("leave"));
      System.out.println();
    } catch (Throwable e) {
      System.out.print(e.toString());
    }
  }

  public void processCommand(String input) throws IOException, URISyntaxException {
      String[] integers=input.toLowerCase().split(" ");
      String command=(integers.length > 0) ? integers[0] : "help";
      String[] params=Arrays.copyOfRange(integers, 1, integers.length);
      switch (command) {
        case "redraw":
          redraw();
          break;
        case "leave":
          System.out.println("Leaving game");
          break;
        case "help":
          help();
          break;
        default:
          invalidCommandMessage();
          break;
      }
  }

  private void help() {
    System.out.println("""
                - redraw
                - leave
                """);
  }

  public String redraw() throws IOException, URISyntaxException {
    ListGamesResult gamesResult = serverFacade.listGames(authToken);
    GameData updatedGame = findGameById(gamesResult.games(), gameData.gameID());

    if (updatedGame != null) {
      gameData = updatedGame;
      return "\n" + draw();
    } else {
      throw new IOException("No game found.");
    }
  }

  private String draw() {
    if(Objects.equals(gameData.whiteUsername(), username)) {
      return drawWhiteBoard();
    }
    return drawBlackBoard();
  }

  public String drawBlackBoard() {
    ChessGame currentGame = gameData.game();
    ChessBoard chessBoard = currentGame.getBoard();
    StringBuilder display = new StringBuilder();

    display.append(createHeaderRow()).append("\n");

    for (int row = 8; row >= 1; row--) {
      display.append(createInvertedRow(row, chessBoard)).append("\n");
    }

    display.append(createHeaderRow());

    return display.toString();
  }

  private String createInvertedRow(int row, ChessBoard chessBoard) {
    StringBuilder rowString = new StringBuilder();
    boolean isBlackSquare = (row % 2 != 0);

    rowString.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + String.format(" %s ", row));

    for (int col = 1; col <= 8; col++) {
      rowString.append(swapColors(isBlackSquare));
      isBlackSquare = !isBlackSquare; // Alternate square color

      ChessPiece piece = chessBoard.getPiece(new ChessPosition(row, col));
      rowString.append(piece == null ? "   " : String.format(" %s ", getPiece(piece)));
    }

    rowString.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + String.format(" %s ", row) + RESET_BG_COLOR);

    return rowString.toString();
  }

  public String drawWhiteBoard() {
    ChessGame currentGame = gameData.game();
    ChessBoard chessBoard = currentGame.getBoard();
    StringBuilder display = new StringBuilder();

    display.append(createHeaderRow()).append("\n");

    for (int row = 1; row <= 8; row++) {
      display.append(createRow(row, chessBoard)).append("\n");
    }

    display.append(createHeaderRow());

    return display.toString();
  }

  private String createHeaderRow() {
    return SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + "    A  B  C  D  E  F  G  H    " + RESET_BG_COLOR;
  }

  private String createRow(int row, ChessBoard chessBoard) {
    StringBuilder rowString = new StringBuilder();
    boolean isBlackSquare = (row % 2 == 0);

    rowString.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + String.format(" %s ", row));

    for (int col = 1; col <= 8; col++) {
      rowString.append(swapColors(isBlackSquare));
      isBlackSquare = !isBlackSquare;

      ChessPiece piece = chessBoard.getPiece(new ChessPosition(row, col));
      rowString.append(piece == null ? "   " : String.format(" %s ", getPiece(piece)));
    }

    rowString.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + String.format(" %s ", row) + RESET_BG_COLOR);

    return rowString.toString();
  }

  private String getPiece(ChessPiece piece) {
    if(piece.getTeamColor() == WHITE) {
      return SET_TEXT_COLOR_RED + pieceLetter(piece);
    } else if(piece.getTeamColor() == BLACK) {
      return SET_TEXT_COLOR_BLUE + pieceLetter(piece);
    }
    else {
      return "   ";
    }
  }

  private String pieceLetter(ChessPiece piece) {
    Map<ChessPiece.PieceType, String> pieceMap = new HashMap<>();
    pieceMap.put(ChessPiece.PieceType.PAWN, "P");
    pieceMap.put(ChessPiece.PieceType.ROOK, "R");
    pieceMap.put(ChessPiece.PieceType.KNIGHT, "N");
    pieceMap.put(ChessPiece.PieceType.BISHOP, "B");
    pieceMap.put(ChessPiece.PieceType.KING, "K");
    pieceMap.put(ChessPiece.PieceType.QUEEN, "Q");

    return pieceMap.getOrDefault(piece.getPieceType(), "?");
  }

  private String swapColors (boolean isBlackSquare) {
    if(isBlackSquare) {
      return SET_BG_COLOR_BLACK;
    } else {
      return SET_BG_COLOR_WHITE;
    }
  }

  private GameData findGameById(Collection<GameData> games, int gameId) {
    for (GameData game : games) {
      if (game.gameID() == gameId) {
        return game;
      }
    }
    return null;
  }



  private void invalidCommandMessage() {
    System.out.println("Invalid command. Type 'help' for a list of valid commands.");
  }
}
