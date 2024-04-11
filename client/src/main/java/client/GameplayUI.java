package client;

import chess.*;
import server.ResponseException;
import client.websocket.ClientHandler;
import client.websocket.WebsocketFacade;
import model.GameData;
import model.listGames.ListGamesResult;
import server.ServerFacade;
import webSocketMessages.serverMessages.LoadMessage;

import java.util.*;

import static chess.ChessGame.TeamColor.*;
import static ui.EscapeSequences.*;

public class GameplayUI implements ClientHandler {
    private final ServerFacade serverFacade;
    private final String authToken;
    private final String username;
    private boolean isBlack = true;
    private final HashMap<String, Integer> lettersToNumbers = new HashMap<>();
    private final WebsocketFacade webSocketFacade;

    private GameData gameData;
    //private final GameplayUI client;

    public GameplayUI(GameData display, String serverURL, String auth, String user) throws server.ResponseException {
        gameData = display;
        authToken = auth;
        serverFacade = new ServerFacade(serverURL);
        username = user;
        //client = new GameplayUI(gameData);
        lettersToNumbers();
        webSocketFacade = new WebsocketFacade(serverURL, this);
        if(Objects.equals(gameData.blackUsername(), username)) {
            webSocketFacade.joinPlayer(authToken, gameData.gameID(), BLACK, username);
        } else if(Objects.equals(gameData.whiteUsername(), username)) {
            webSocketFacade.joinPlayer(authToken, gameData.gameID(), WHITE, username);
        } else {
            webSocketFacade.joinObserver(authToken, gameData.gameID());
        }
    }

    private void lettersToNumbers() {
        lettersToNumbers.put("a", 1);
        lettersToNumbers.put("b", 2);
        lettersToNumbers.put("c", 3);
        lettersToNumbers.put("d", 4);
        lettersToNumbers.put("e", 5);
        lettersToNumbers.put("f", 6);
        lettersToNumbers.put("g", 7);
        lettersToNumbers.put("h", 8);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redraw();
                case "leave" -> "leave";
                case "move" -> makeMove(params);
                case "resign" -> resignGame();
                case "highlight" -> highlightMoves(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        } catch (client.ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    public String help() {
        return """
                - redraw
                - move ROW,COLUMN(starting position) ROW,COLUMN(ending position)
                - highlight ROW,COLUMN(piece position)
                - resign
                - leave
                """;
    }

    public String redraw() throws server.ResponseException, client.ResponseException {
        ListGamesResult listedGames = serverFacade.listGames(authToken);
        var games = listedGames.games();
        for(GameData game: games) {
            if(game.gameID() == gameData.gameID()) {
                gameData = game;
                return "\n" + draw();
            }
        }
        throw new ResponseException("Game not found");
    }

    private String draw() {
        if(Objects.equals(gameData.blackUsername(), username)) {
            return displayBlackGame();
        }
        return displayWhiteGame();
    }

    public void leave() throws ResponseException {
        webSocketFacade.leaveGame(authToken, gameData.gameID());
    }

    public String makeMove(String... params) throws ResponseException {
        if(params.length == 2) {
            String[] preMove = params[0].split(",");
            String[] postMove = params[1].split(",");
            ChessPosition startingPosition =
                    new ChessPosition(Integer.parseInt(preMove[0]), lettersToNumbers.get(preMove[1]));
            ChessPosition endPosition =
                    new ChessPosition(Integer.parseInt(postMove[0]), lettersToNumbers.get(postMove[1]));
            ChessMove chessMove = new ChessMove(startingPosition, endPosition, null);
            webSocketFacade.makeMove(authToken, gameData.gameID(), chessMove);
            return "";
        }
        throw new ResponseException("Expected: ROW,COLUMN(starting position) ROW,COLUMN(ending position)");
    }

    public String resignGame() throws ResponseException {
        webSocketFacade.resignGame(authToken, gameData.gameID());
        return "";
    }

    private String highlightMoves(String[] params) throws ResponseException {
        if(params.length == 1) {
            ChessGame chessGame = gameData.game();
            String[] positionArray = params[0].split(",");
            ChessPosition piecePosition =
                    new ChessPosition(Integer.parseInt(positionArray[0]), lettersToNumbers.get(positionArray[1]));
            Collection<ChessMove> validMoves = chessGame.validMoves(piecePosition);
            if(Objects.equals(gameData.blackUsername(), username)) {
                return displayBlackHighlightedGame(validMoves, piecePosition);
            } else {
                return displayWhiteHighlightedGame(validMoves, piecePosition);
            }
        }
        throw new ResponseException("Expected: highlight ROW,COLUMN(piece position)");
    }


    public void run() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                System.out.print(SET_TEXT_COLOR_GREEN + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_YELLOW + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
    }

    public String displayWhiteGame() {
        ChessGame game = gameData.game();
        ChessBoard board = game.getBoard();
        String returnString = SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + "    A  B  C  D  E  F  G  H    " + RESET_BG_COLOR + "\n";
        boolean isBlack = true;

        for(int i = 1; i <= 8; i++) {
            String line = SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + String.format(" %s ", i);
            for(int j = 1; j <= 8; j++) {
                line += alternateColors(isBlack);
                isBlack = !isBlack;
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if(piece == null) {
                    line += "   ";
                } else {
                    line += String.format(" %s ", returnCorrectPiece(piece));
                }
            }
            isBlack = !isBlack;
            line += SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + String.format(" %s ", i) + RESET_BG_COLOR + "\n";
            returnString = line + returnString;
        }
        returnString = SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + "    A  B  C  D  E  F  G  H    " +
                RESET_BG_COLOR + "\n" + returnString;
        return returnString;
    }

    public String displayBlackGame() {
        ChessGame game = gameData.game();
        ChessBoard board = game.getBoard();
        String returnString = SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + "    H  G  F  E  D  C  B  A    " + RESET_BG_COLOR + "\n";
        boolean isBlack = true;

        for(int i = 8; i >= 1; i--) {
            String line = SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + String.format(" %s ", i);
            for(int j = 8; j >= 1; j--) {
                line += alternateColors(isBlack);
                isBlack = !isBlack;
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if(piece == null) {
                    line += "   ";
                } else {
                    line += String.format(" %s ", returnCorrectPiece(piece));
                }
            }
            isBlack = !isBlack;
            line += SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + String.format(" %s ", i) + RESET_BG_COLOR + "\n";
            returnString = line + returnString;
        }
        returnString = SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + "    H  G  F  E  D  C  B  A    " +
                RESET_BG_COLOR + "\n" + returnString;
        return returnString;
    }

    public String displayWhiteHighlightedGame(Collection<ChessMove> validMoves, ChessPosition currentPosition) {
        ChessGame game = gameData.game();
        ChessBoard board = game.getBoard();
        String returnString = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR + "\n";
        isBlack = true;

        for(int i = 1; i <= 8; i++) {
            String line = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + String.format(" %s ", i);
            for(int j = 1; j <= 8; j++) {
                line += alternateHighlightedColors(isBlack, validMoves,
                        new ChessMove(currentPosition, new ChessPosition(i, j), null));
                line = displayLoop(line, board, i, j);
            }
            isBlack = !isBlack;
            line += SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + String.format(" %s ", i) + RESET_BG_COLOR + "\n";
            returnString = line + returnString;
        }
        returnString = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "    a  b  c  d  e  f  g  h    " +
                RESET_BG_COLOR + "\n" + returnString;
        return returnString;
    }

    public String displayBlackHighlightedGame(Collection<ChessMove> validMoves, ChessPosition currentPosition) {
        ChessGame game = gameData.game();
        ChessBoard board = game.getBoard();
        String returnString = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR + "\n";
        isBlack = true;

        for(int i = 8; i >= 1; i--) {
            String line = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + String.format(" %s ", i);
            for(int j = 8; j >= 1; j--) {
                line += alternateHighlightedColors(isBlack, validMoves,
                        new ChessMove(currentPosition, new ChessPosition(i, j), null));
                line = displayLoop(line, board, i, j);
            }
            isBlack = !isBlack;
            line += SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + String.format(" %s ", i) + RESET_BG_COLOR + "\n";
            returnString = line + returnString;
        }
        returnString = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "    h  g  f  e  d  c  b  a    " +
                RESET_BG_COLOR + "\n" + returnString;
        return returnString;
    }

    private String alternateHighlightedColors (boolean isBlack, Collection<ChessMove> validMoves, ChessMove currentPosition) {
        if(isBlack && validMoves.contains(currentPosition)) {
            return SET_BG_COLOR_DARK_GREEN;
        }
        if(!isBlack && validMoves.contains(currentPosition)) {
            return SET_BG_COLOR_GREEN;
        }
        if(isBlack) {
            return SET_BG_COLOR_BLACK;
        }
        return SET_BG_COLOR_WHITE;
    }

    private String returnCorrectPiece (ChessPiece piece) {
        if(piece.getTeamColor() == WHITE) {
            return SET_TEXT_COLOR_RED + pieceToLetter(piece);
        } else if(piece.getTeamColor() == BLACK) {
            return SET_TEXT_COLOR_BLUE + pieceToLetter(piece);
        }
        else return "   ";
    }

    private String pieceToLetter (ChessPiece piece) {
        if(piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return "P";
        }
        if(piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            return "R";
        }
        if(piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return "N";
        }
        if(piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return "B";
        }
        if(piece.getPieceType() == ChessPiece.PieceType.KING) {
            return "K";
        }
        if(piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return "Q";
        }
        return "?";
    }

    private String alternateColors (boolean isBlack) {
        if(isBlack) {
            return SET_BG_COLOR_BLACK;
        } else {
            return SET_BG_COLOR_WHITE;
        }
    }

    private String displayLoop(String line, ChessBoard board, int i, int j) {
        isBlack = !isBlack;
        ChessPiece piece = board.getPiece(new ChessPosition(i, j));
        if(piece == null) {
            line += "   ";
        } else {
            line += String.format(" %s ", returnCorrectPiece(piece));
        }
        return line;
    }

    @Override
    public void updateGame(LoadMessage loadGame) throws ResponseException, client.ResponseException {
        if(loadGame.getGame() == gameData.gameID()) {
            System.out.println(redraw());
            System.out.print("\n" + SET_TEXT_COLOR_YELLOW + "[GAMEPLAY]>>> " + SET_TEXT_COLOR_GREEN);
        }
    }

    @Override
    public void printMessage(String message) {
        System.out.println(SET_TEXT_COLOR_RED + message);
        System.out.print("\n" + SET_TEXT_COLOR_YELLOW + "[GAMEPLAY]>>> " + SET_TEXT_COLOR_GREEN);
    }
}
