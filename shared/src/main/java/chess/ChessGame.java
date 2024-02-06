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
        this.teamColor = team;
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
        ArrayList<ChessMove> finalMoves = new ArrayList<>();
        ArrayList<ChessMove> allMoves = new ArrayList<>(chessPiece.pieceMoves(board,startPosition));

        if (chessPiece == null) {
            return null;
        }
        for (ChessMove moves : allMoves) {
            previousBoard = board.clone();
            try {
                getValids(moves);
                if (!isInCheck(chessPiece.getTeamColor())) {
                    finalMoves.add(moves);
                }
                board = previousBoard;
            } catch (Exception e) {
                throw new RuntimeException(e);
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
        ChessPosition checkKing=null;
        for (int row=1; row < 9; row++) {
            for (int col=1; col < 9; col++) {
                ChessPiece piece=board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    checkKing=new ChessPosition(row, col);
                }
            }
        }
        if (checkKing == null) {
            return false;
        }
        ArrayList<ChessPosition> teamPositions=new ArrayList<>();
        for (int row=1; row < 9; row++) {
            for (int col=1; col < 9; col++) {
                if (board.getPiece(new ChessPosition(row, col)).getTeamColor() != teamColor) {
                    if ((board.getPiece(new ChessPosition(row, col)) != null)) {
                        //ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                        teamPositions.add(new ChessPosition(row, col));
                    }
                    //board.getPiece(new ChessPosition(row, col)).pieceMoves(board, new ChessPosition(row, col));

                }
            }
        }
        for (ChessPosition position : teamPositions) {
            ChessPiece enemyPiece = board.getPiece(position);
            ArrayList<ChessMove> enemyMoves = new ArrayList<>(enemyPiece.pieceMoves(board, position));
            for (ChessMove move : enemyMoves) {
                if (move.getEndPosition().equals(checkKing)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (getTeamTurn() != teamColor) {
            return false;
        }
        if (!isInCheck(teamColor)) {
            return false;
        }
        ArrayList<ChessPosition> enemyPositions = new ArrayList<>();
        for (int row=1; row < 9; row++) {
            for (int col=1; col < 9; col++) {
                ChessPiece piece=board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    enemyPositions.add(new ChessPosition(row, col));
                }
            }
        }
        for (ChessPosition position : enemyPositions) {
            if (!validMoves(position).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor) == true) {
            return false;
        }
        if (isInCheck(teamColor) == false) {
            ArrayList<ChessPosition> enemyPositions = new ArrayList<>();
            for (int row=1; row < 9; row++) {
                for (int col=1; col < 9; col++) {
                    ChessPiece piece=board.getPiece(new ChessPosition(row, col));
                    if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                        enemyPositions.add(new ChessPosition(row, col));
                    }
                }
            }
            for (ChessPosition position : enemyPositions) {
                if(!validMoves(position).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
