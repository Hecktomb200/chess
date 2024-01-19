package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor color;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        //return color;
        throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        //return type;
        throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * First, start with Bishop moves. What tiles they can move to, If they can take a piece or are blocked by it
     *  Index to move [+1,+1], [+1,-1], [-1,+1], [-1,-1]?
     *  Some kind of check each tile it wants to move to see if there's another piece already holding that position.
     *      This will need a database of sorts to store the information, right? Or perhaps a for loop to run through all current pieces.
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = null;
        ChessPiece chessPiece = board.getPiece(myPosition);
        if (chessPiece.getPieceType() == PieceType.BISHOP) {

        }
        if (chessPiece.getPieceType() == PieceType.KING) {
            ChessPosition lastPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1 );
            validMoves.add(new ChessMove(myPosition ,lastPosition, null));
            lastPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
            validMoves.add(new ChessMove(myPosition, lastPosition, null));
            lastPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1 );
            validMoves.add(new ChessMove(myPosition, lastPosition, null));
            lastPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1 );
            validMoves.add(new ChessMove(myPosition, lastPosition, null));
            lastPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() );
            validMoves.add(new ChessMove(myPosition, lastPosition, null));
            lastPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1 );
            validMoves.add(new ChessMove(myPosition, lastPosition, null));
            lastPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1 );
            validMoves.add(new ChessMove(myPosition, lastPosition, null));
            lastPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1 );
            validMoves.add(new ChessMove(myPosition, lastPosition, null));

            return validMoves;
        }
        if (chessPiece.getPieceType() == PieceType.QUEEN) {

        }
        if (chessPiece.getPieceType() == PieceType.KNIGHT) {

        }
        if (chessPiece.getPieceType() == PieceType.ROOK) {

        }
        if (chessPiece.getPieceType() == PieceType.PAWN) {

        }

        return new ArrayList<>();

    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "color=" + color +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that=(ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }
}
