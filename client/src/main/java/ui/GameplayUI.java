package ui;

import chess.*;
import model.GameData;
import model.listgames.ListGamesResult;
import server.ServerFacade;
import ui.ChessFile;
import websocket.WebsocketFacade;

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
  private final Boolean observer;
  private final WebsocketFacade webSocketFacade;
  public GameplayUI(GameData game, String sURL, String authToken, String username, boolean observer) throws IOException {
    serverFacade = new ServerFacade(sURL);
    this.authToken = authToken;
    gameData = game;
    this.username = username;
    this.scanner = new Scanner(System.in);
    this.observer = observer;
    webSocketFacade = new WebsocketFacade(sURL);
  }

  public void run() throws Exception {
    System.out.println(redraw());
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
          handleLeave();
          System.out.println("Leaving game");
          break;
        case "help":
          help();
          break;
        case "move":
          makeMove(params);
          break;
        case "resign":
          handleResign();
          break;
        case "highlight":
          handleHighlight(params);
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

  private String handleLeave() throws IOException {
    webSocketFacade.leave(authToken, gameData.gameID());
    return "";
  }

  private String handleHighlight(String[] params) {
    if (params.length != 1) {
      throw new IllegalArgumentException("Expected: highlight [ROW],[COLUMN]");
    }

    ChessGame chessGame = gameData.game();
    String[] positionArray = params[0].split(",");
    ChessPosition piecePosition = new ChessPosition(
            Integer.parseInt(positionArray[0]),
            ChessFile.letterToNumber(positionArray[1])
    );

    Collection<ChessMove> validMoves = chessGame.validMoves(piecePosition);
    return displayHighlightedGame(validMoves, piecePosition);
  }

  private String displayHighlightedGame(Collection<ChessMove> validMoves, ChessPosition piecePosition) {
    ChessGame currentGame = gameData.game();
    ChessBoard chessboard = currentGame.getBoard();
    StringBuilder boardRepresentation = new StringBuilder();

    boolean isBlack = Objects.equals(gameData.blackUsername(), username);
    String header = getBoardHeader(isBlack);
    boardRepresentation.append(header);

    boolean isSquareBlack = true;

    for (int rank = isBlack ? 8 : 1; isBlack ? rank >= 1 : rank <= 8; rank += isBlack ? -1 : 1) {
      StringBuilder row = new StringBuilder(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + String.format(" %s ", rank));
      for (int file = isBlack ? 8 : 1; isBlack ? file >= 1 : file <= 8; file += isBlack ? -1 : 1) {
        ChessPosition targetPosition = new ChessPosition(rank, file);
        row.append(getSquareDisplay(isSquareBlack, validMoves, piecePosition, targetPosition));
        isSquareBlack = !isSquareBlack;
      }
      row.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + String.format(" %s ", rank) + RESET_BG_COLOR + "\n");
      boardRepresentation.insert(0, row.toString());
    }

    boardRepresentation.append(header);
    return boardRepresentation.toString();
  }

  private String getSquareDisplay(boolean isSquareBlack, Collection<ChessMove> validMoves,
                                  ChessPosition piecePosition, ChessPosition targetPosition) {
    String squareColor = isSquareBlack ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
    String highlight = validMoves.contains(new ChessMove(piecePosition, targetPosition, null)) ? SET_BG_COLOR_YELLOW : "";
    return squareColor + highlight + "   " + RESET_BG_COLOR;
  }

  private String getBoardHeader(boolean isBlack) {
    return SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK +
            (isBlack ? "    H  G  F  E  D  C  B  A    " : "    A  B  C  D  E  F  G  H    ") + RESET_BG_COLOR + "\n";
  }

  private String handleResign() throws IOException {
    webSocketFacade.resign(authToken, gameData.gameID());
    return "";
  }

  private String makeMove(String[] params) throws IOException {
    if (params.length != 2) {
      throw new IllegalArgumentException("Expected [ROW],[COLUMN] [ROW],[COLUMN]");
    }

    String[] pre = params[0].split(",");
    String[] post = params[1].split(",");
    if (pre.length != 2 || post.length != 2) {
      throw new IllegalArgumentException("Invalid format. Expected: [ROW],[COLUMN] for both positions.");
    }

    ChessPosition startPos = new ChessPosition(
            Integer.parseInt(pre[0]),
            ChessFile.letterToNumber(pre[1])
    );
    ChessPosition endPos = new ChessPosition(
            Integer.parseInt(post[0]),
            ChessFile.letterToNumber(post[1])
    );

    try {
      ChessMove chessMove = new ChessMove(startPos, endPos, null);
      webSocketFacade.makeMove(authToken, gameData.gameID(), chessMove);

      return "";
    } catch (IllegalArgumentException e) {
      return "Error: " + e.getMessage();
    }
  }

  private void help() {
    System.out.println(SET_TEXT_COLOR_GREEN + """
                - help
                - redraw
                - leave
                - move
                - highlight
                - resign
                """);
  }

  public String redraw() throws Exception {
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
      if (observer) {
        return drawWhiteBoard();
      }
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
