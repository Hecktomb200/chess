package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import java.util.Scanner;

import static chess.ChessGame.TeamColor.*;
import static ui.EscapeSequences.*;

public class GameplayUI {

    private final GameData gameData;
    private final GameplayUI client;

    public GameplayUI(GameData display) {
        gameData = display;
        client = new GameplayUI(gameData);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                System.out.print(SET_TEXT_COLOR_BLUE + result);
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
        String returnString = SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR + "\n";
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
        returnString = SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + "    a  b  c  d  e  f  g  h    " +
                RESET_BG_COLOR + "\n" + returnString;
        return returnString;
    }

    public String displayBlackGame() {
        ChessGame game = gameData.game();
        ChessBoard board = game.getBoard();
        String returnString = SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR + "\n";
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
        returnString = SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + "    h  g  f  e  d  c  b  a    " +
                RESET_BG_COLOR + "\n" + returnString;
        return returnString;
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
}
