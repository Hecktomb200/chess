package chess;

import java.util.Collection;
import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamColor;
    private ChessBoard board;

    public ChessGame() {
        this.teamColor = TeamColor.WHITE;
        this.board = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamColor = teamTurn;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public void getValids(ChessMove moves) {
        ChessPiece piece = board.getPiece(moves.getStartPosition());
        if (moves.getPromotionPiece() == null) {
            this.board.addPiece(moves.getEndPosition(), piece);
        }
        else {
            this.board.addPiece(moves.getEndPosition(), new ChessPiece(piece.getTeamColor(), moves.getPromotionPiece()));
        }
        this.board.addPiece(moves.getStartPosition(), null);
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece chessPiece = board.getPiece(startPosition);
        ChessBoard previousBoard = board.clone();

        if (chessPiece == null) {
            return null;
        }
        ArrayList<ChessMove> finalMoves = new ArrayList<>();
        ArrayList<ChessMove> allMoves = new ArrayList<>(chessPiece.pieceMoves(board,startPosition));
        for (ChessMove moves : allMoves) {
            previousBoard = board.clone();
            try {
                getValids(moves);
                if (!isInCheck(ChessPiece.getTeamColor())) {
                    finalMoves.add(moves);
                }
                board = previousBoard;
            }

        }

        return finalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException();
        }
        ArrayList<ChessMove> finalMoves = new ArrayList<>();
        if (move.getStartPosition().getRow() > 8 || move.getStartPosition().getRow() < 1 || move.getStartPosition().getColumn() > 8 || move.getStartPosition().getColumn() < 1
        || move.getEndPosition().getRow() > 8 || move.getEndPosition().getRow() < 1 || move.getEndPosition().getColumn() > 8 || move.getEndPosition().getColumn() < 1) {
            throw new InvalidMoveException();
        }
        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException();
        }
        if (piece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException();
        }
        if (move.getPromotionPiece() == null) {
            board.addPiece(move.getEndPosition(), piece);
        }
        else if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
        if (getTeamTurn() == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        }
        else if (getTeamTurn() == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}
