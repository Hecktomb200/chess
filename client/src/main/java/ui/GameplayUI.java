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
  private final Scanner scanner;
  public GameplayUI(GameData game, String sURL, String authToken, String username) {
    serverFacade = new ServerFacade(sURL);
    this.authToken = authToken;
    gameData = game;
    this.username = username;
    this.scanner = new Scanner(System.in);
  }

  public void run() throws IOException, URISyntaxException {
    System.out.println(draw());
    help();
    String command=" ";
    do {
      System.out.print("\n" + SET_TEXT_COLOR_YELLOW + "[IN_GAME] >>> " + SET_TEXT_COLOR_GREEN);
      command=scanner.nextLine();
      processCommand(command);
      System.out.print(SET_TEXT_COLOR_YELLOW + command);
    } while (!command.equals("leave"));
    System.out.println();
  }

  public void processCommand(String input) throws IOException, URISyntaxException {
    try {
      String[] integers=input.toLowerCase().split(" ");
      String command=(integers.length > 0) ? integers[0] : "help";
      String[] params=Arrays.copyOfRange(integers, 1, integers.length);
      switch (command) {
        case "redraw":
          System.out.println(redraw());
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
    } catch (Exception e) {
      System.out.println("An error occurred: " + e.getMessage());
      System.out.println("Please try again.");
    }
  }

  private void help() {
    System.out.println(SET_TEXT_COLOR_GREEN + """
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
    if(Objects.equals(gameData.blackUsername(), username)) {
      return drawBlackBoard();
    }
    return drawWhiteBoard();
  }

  public String drawBlackBoard() {
    return drawBoard(true);
  }

  public String drawWhiteBoard() {
    return drawBoard(false);
  }

  private String drawBoard(boolean isBlack) {
    ChessGame currentGame = gameData.game();
    ChessBoard chessboard = currentGame.getBoard();
    StringBuilder boardRepresentation = new StringBuilder();

    if (isBlack) {
      boardRepresentation.append(SET_BG_COLOR_WHITE)
              .append(SET_TEXT_COLOR_BLACK)
              .append("    H  G  F  E  D  C  B  A    ")
              .append(RESET_BG_COLOR)
              .append("\n");
    } else {
      boardRepresentation.append(SET_BG_COLOR_WHITE)
              .append(SET_TEXT_COLOR_BLACK)
              .append("    A  B  C  D  E  F  G  H    ")
              .append(RESET_BG_COLOR)
              .append("\n");
    }

    boolean isSquareBlack = true;

    for (int rank = isBlack ? 8 : 1; isBlack ? rank >= 1 : rank <= 8; rank += isBlack ? -1 : 1) {
      StringBuilder row = new StringBuilder(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + String.format(" %s ", rank));

      for (int file = isBlack ? 8 : 1; isBlack ? file >= 1 : file <= 8; file += isBlack ? -1 : 1) {
        row.append(getSquareColor(isSquareBlack));
        isSquareBlack = !isSquareBlack;

        ChessPiece chessPiece = chessboard.getPiece(new ChessPosition(rank, file));
        if (chessPiece == null) {
          row.append("   ");
        } else {
          row.append(String.format(" %s ", getPieceRepresentation(chessPiece)));
        }
      }

      isSquareBlack = !isSquareBlack;
      row.append(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + String.format(" %s ", rank) + RESET_BG_COLOR).append("\n");
      boardRepresentation.insert(0, row.toString());
    }

    if (isBlack) {
      boardRepresentation.insert(0, SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + "    H  G  F  E  D  C  B  A    " + RESET_BG_COLOR + "\n");
    } else {
      boardRepresentation.insert(0, SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + "    A  B  C  D  E  F  G  H    " + RESET_BG_COLOR + "\n");
    }

    return boardRepresentation.toString();
  }

  private String getSquareColor(boolean isBlack) {
    return isBlack ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
  }

  private String getPieceRepresentation(ChessPiece piece) {
    return getPiece(piece);
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
