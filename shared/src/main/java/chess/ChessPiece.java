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
    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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
        return pieceColor;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
        //throw new RuntimeException("Not implemented");
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
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ChessPiece chessPiece = board.getPiece(myPosition);
        if (chessPiece.getPieceType() == PieceType.BISHOP) {
            int onRow =myPosition.getRow();
            int onCol =myPosition.getColumn();
            ChessPosition lastPosition = new ChessPosition(onRow, onCol);
            while (onRow <= 8 && onRow >= 1 && onCol <= 8 && onCol >= 1) {
                onRow += 1;
                onCol += 1;
                lastPosition = new ChessPosition(onRow, onCol);
                if (onRow == 9 || onCol == 9) {
                    break;
                }
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition, lastPosition, null));
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    break;
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() == pieceColor) {
                    break;
                }
            }
            onRow =myPosition.getRow();
            onCol =myPosition.getColumn();
            lastPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
            while (onRow <= 8 && onRow >= 1 && onCol <= 8 && onCol >= 1) {
                onRow -= 1;
                onCol -= 1;
                lastPosition = new ChessPosition(onRow,onCol);
                if (onRow == 0 || onCol == 0) {
                    break;
                }
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition, lastPosition, null));
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    break;
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() == pieceColor) {
                    break;
                }
            }
            onRow =myPosition.getRow();
            onCol =myPosition.getColumn();
            lastPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
            while (onRow <= 8 && onRow >= 1 && onCol <= 8 && onCol >= 1) {
                onRow -= 1;
                onCol += 1;
                lastPosition = new ChessPosition(onRow,onCol);
                if (onCol == 9 || onRow == 0) {
                    break;
                }
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition, lastPosition, null));
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    break;
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() == pieceColor) {
                    break;
                }
            }
            onRow =myPosition.getRow();
            onCol =myPosition.getColumn();
            lastPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
            while (onRow <= 8 && onRow > 1 && onCol <= 8 && onCol >= 1) {
                onRow += 1;
                onCol -= 1;
                lastPosition = new ChessPosition(onRow,onCol);
                if (onCol == 0 || onRow == 9) {
                    break;
                }
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition, lastPosition, null));
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    break;
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() == pieceColor) {
                    break;
                }
            }

        }
        if (chessPiece.getPieceType() == PieceType.KING) {
            ChessPosition lastPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1 );
            if ( lastPosition.getRow() <= 8 && lastPosition.getRow() >= 1 && lastPosition.getColumn() <= 8 && lastPosition.getColumn() >= 1) {
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition ,lastPosition, null));
                }
            }
            lastPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
            if ( lastPosition.getRow() <= 8 && lastPosition.getRow() >= 1 && lastPosition.getColumn() <= 8 && lastPosition.getColumn() >= 1) {
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition ,lastPosition, null));
                }
            }
            lastPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1 );
            if ( lastPosition.getRow() <= 8 && lastPosition.getRow() >= 1 && lastPosition.getColumn() <= 8 && lastPosition.getColumn() >= 1) {
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition ,lastPosition, null));
                }
            }
            lastPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1 );
            if ( lastPosition.getRow() <= 8 && lastPosition.getRow() >= 1 && lastPosition.getColumn() <= 8 && lastPosition.getColumn() >= 1) {
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition ,lastPosition, null));
                }
            }
            lastPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() );
            if ( lastPosition.getRow() <= 8 && lastPosition.getRow() >= 1 && lastPosition.getColumn() <= 8 && lastPosition.getColumn() >= 1) {
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition ,lastPosition, null));
                }
            }
            lastPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1 );
            if ( lastPosition.getRow() <= 8 && lastPosition.getRow() >= 1 && lastPosition.getColumn() <= 8 && lastPosition.getColumn() >= 1) {
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition ,lastPosition, null));
                }
            }
            lastPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1 );
            if ( lastPosition.getRow() <= 8 && lastPosition.getRow() >= 1 && lastPosition.getColumn() <= 8 && lastPosition.getColumn() >= 1) {
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition ,lastPosition, null));
                }
            }
            lastPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1 );
            if ( lastPosition.getRow() <= 8 && lastPosition.getRow() >= 1 && lastPosition.getColumn() <= 8 && lastPosition.getColumn() >= 1) {
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition ,lastPosition, null));
                }
            }


        }
        if (chessPiece.getPieceType() == PieceType.QUEEN) {
            int onRow =myPosition.getRow();
            int onCol =myPosition.getColumn();
            ChessPosition lastPosition = new ChessPosition(onRow, onCol);
            while (onRow <= 8 && onRow >= 1 && onCol <= 8 && onCol >= 1) {
                onRow += 1;
                onCol += 1;
                lastPosition = new ChessPosition(onRow, onCol);
                if (onRow == 9 || onCol == 9) {
                    break;
                }
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition, lastPosition, null));
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    break;
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() == pieceColor) {
                    break;
                }
            }
            onRow =myPosition.getRow();
            onCol =myPosition.getColumn();
            lastPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
            while (onRow <= 8 && onRow >= 1 && onCol <= 8 && onCol >= 1) {
                onRow -= 1;
                onCol -= 1;
                lastPosition = new ChessPosition(onRow,onCol);
                if (onRow == 0 || onCol == 0) {
                    break;
                }
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition, lastPosition, null));
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    break;
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() == pieceColor) {
                    break;
                }
            }
            onRow =myPosition.getRow();
            onCol =myPosition.getColumn();
            lastPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
            while (onRow <= 8 && onRow >= 1 && onCol <= 8 && onCol >= 1) {
                onRow -= 1;
                onCol += 1;
                lastPosition = new ChessPosition(onRow,onCol);
                if (onCol == 9 || onRow == 0) {
                    break;
                }
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition, lastPosition, null));
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    break;
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() == pieceColor) {
                    break;
                }
            }
            onRow =myPosition.getRow();
            onCol =myPosition.getColumn();
            lastPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
            while (onRow <= 8 && onRow > 1 && onCol <= 8 && onCol >= 1) {
                onRow += 1;
                onCol -= 1;
                lastPosition = new ChessPosition(onRow,onCol);
                if (onCol == 0 || onRow == 9) {
                    break;
                }
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition, lastPosition, null));
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    break;
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() == pieceColor) {
                    break;
                }
            }
            onRow =myPosition.getRow();
            onCol =myPosition.getColumn();
            lastPosition = new ChessPosition(onRow, onCol);
            while (onRow <= 8 && onRow >= 1 && onCol <= 8 && onCol >= 1) {
                onRow += 1;
                lastPosition = new ChessPosition(onRow, onCol);
                if (onRow == 9) {
                    break;
                }
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition, lastPosition, null));
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    break;
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() == pieceColor) {
                    break;
                }
            }
            onRow =myPosition.getRow();
            onCol =myPosition.getColumn();
            lastPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
            while (onRow <= 8 && onRow >= 1 && onCol <= 8 && onCol >= 1) {
                onRow -= 1;
                lastPosition = new ChessPosition(onRow,onCol);
                if (onRow == 0) {
                    break;
                }
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition, lastPosition, null));
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    break;
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() == pieceColor) {
                    break;
                }
            }
            onRow =myPosition.getRow();
            onCol =myPosition.getColumn();
            lastPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
            while (onRow <= 8 && onRow >= 1 && onCol <= 8 && onCol >= 1) {
                onCol += 1;
                lastPosition = new ChessPosition(onRow,onCol);
                if (onCol == 9) {
                    break;
                }
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition, lastPosition, null));
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    break;
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() == pieceColor) {
                    break;
                }
            }
            onRow =myPosition.getRow();
            onCol =myPosition.getColumn();
            lastPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
            while (onRow <= 8 && onRow > 1 && onCol <= 8 && onCol >= 1) {
                onCol -= 1;
                lastPosition = new ChessPosition(onRow,onCol);
                if (onCol == 0) {
                    break;
                }
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition, lastPosition, null));
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    break;
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() == pieceColor) {
                    break;
                }
            }


        }
        if (chessPiece.getPieceType() == PieceType.KNIGHT) {
            ChessPosition lastPosition = new ChessPosition(myPosition.getRow() + 2 , myPosition.getColumn() + 1 );
            if ( lastPosition.getRow() <= 8 && lastPosition.getRow() >= 1 && lastPosition.getColumn() <= 8 && lastPosition.getColumn() >= 1) {
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition ,lastPosition, null));
                }
            }
            lastPosition = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1 );
            if ( lastPosition.getRow() <= 8 && lastPosition.getRow() >= 1 && lastPosition.getColumn() <= 8 && lastPosition.getColumn() >= 1) {
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition ,lastPosition, null));
                }
            }
            lastPosition = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1 );
            if ( lastPosition.getRow() <= 8 && lastPosition.getRow() >= 1 && lastPosition.getColumn() <= 8 && lastPosition.getColumn() >= 1) {
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition ,lastPosition, null));
                }
            }
            lastPosition = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1 );
            if ( lastPosition.getRow() <= 8 && lastPosition.getRow() >= 1 && lastPosition.getColumn() <= 8 && lastPosition.getColumn() >= 1) {
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition ,lastPosition, null));
                }
            }
            lastPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2 );
            if ( lastPosition.getRow() <= 8 && lastPosition.getRow() >= 1 && lastPosition.getColumn() <= 8 && lastPosition.getColumn() >= 1) {
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition ,lastPosition, null));
                }
            }
            lastPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2 );
            if ( lastPosition.getRow() <= 8 && lastPosition.getRow() >= 1 && lastPosition.getColumn() <= 8 && lastPosition.getColumn() >= 1) {
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition ,lastPosition, null));
                }
            }
            lastPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2 );
            if ( lastPosition.getRow() <= 8 && lastPosition.getRow() >= 1 && lastPosition.getColumn() <= 8 && lastPosition.getColumn() >= 1) {
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition ,lastPosition, null));
                }
            }
            lastPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2 );
            if ( lastPosition.getRow() <= 8 && lastPosition.getRow() >= 1 && lastPosition.getColumn() <= 8 && lastPosition.getColumn() >= 1) {
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition ,lastPosition, null));
                }
            }

        }
        if (chessPiece.getPieceType() == PieceType.ROOK) {
            int onRow =myPosition.getRow();
            int onCol =myPosition.getColumn();
            ChessPosition lastPosition = new ChessPosition(onRow, onCol);
            while (onRow <= 8 && onRow >= 1 && onCol <= 8 && onCol >= 1) {
                onRow += 1;
                lastPosition = new ChessPosition(onRow, onCol);
                if (onRow == 9) {
                    break;
                }
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition, lastPosition, null));
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    break;
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() == pieceColor) {
                    break;
                }
            }
            onRow =myPosition.getRow();
            onCol =myPosition.getColumn();
            lastPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
            while (onRow <= 8 && onRow >= 1 && onCol <= 8 && onCol >= 1) {
                onRow -= 1;
                lastPosition = new ChessPosition(onRow,onCol);
                if (onRow == 0) {
                    break;
                }
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition, lastPosition, null));
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    break;
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() == pieceColor) {
                    break;
                }
            }
            onRow =myPosition.getRow();
            onCol =myPosition.getColumn();
            lastPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
            while (onRow <= 8 && onRow >= 1 && onCol <= 8 && onCol >= 1) {
                onCol += 1;
                lastPosition = new ChessPosition(onRow,onCol);
                if (onCol == 9) {
                    break;
                }
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition, lastPosition, null));
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    break;
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() == pieceColor) {
                    break;
                }
            }
            onRow =myPosition.getRow();
            onCol =myPosition.getColumn();
            lastPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn());
            while (onRow <= 8 && onRow > 1 && onCol <= 8 && onCol >= 1) {
                onCol -= 1;
                lastPosition = new ChessPosition(onRow,onCol);
                if (onCol == 0) {
                    break;
                }
                if (board.getPiece(lastPosition) == null || board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    validMoves.add(new ChessMove(myPosition, lastPosition, null));
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() != pieceColor) {
                    break;
                }
                if (board.getPiece(lastPosition) != null && board.getPiece(lastPosition).getTeamColor() == pieceColor) {
                    break;
                }
            }

        }
        if (chessPiece.getPieceType() == PieceType.PAWN) {

        }

        return validMoves;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that=(ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

}
