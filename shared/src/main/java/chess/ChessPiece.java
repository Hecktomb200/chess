package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

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
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        switch (getPieceType()) {
            case KING:
                addKingMoves(validMoves, board, myPosition);
                break;
            case QUEEN:
                addDiagonalMoves(validMoves, board, myPosition);
                addLinearMoves(validMoves, board, myPosition);
                break;
            case BISHOP:
                addDiagonalMoves(validMoves, board, myPosition);
                break;
            case KNIGHT:
                addKnightMoves(validMoves, board, myPosition);
                break;
            case ROOK:
                addLinearMoves(validMoves, board, myPosition);
                break;
            case PAWN:
                addPawnMoves(validMoves, board, myPosition);
        }
        //moves are going to go here. Modularize this!
        return validMoves;
    }

    private void addDiagonalMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int i = -1; i <= 1; i += 2) {
            for (int j = -1; j <= 1; j += 2) {
                int newRow = row + i;
                int newCol = col + j;

                while (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessPiece piece = board.getPiece(newPosition);

                    if (piece == null) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    } else if (piece.getTeamColor() != getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                        break;
                    } else {
                        break;
                    }

                    newRow += i;
                    newCol += j;
                }
            }
        }
    }

    private void addKingMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }

                int newRow = row + i;
                int newCol = col + j;

                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessPiece piece = board.getPiece(newPosition);

                    if (piece == null || piece.getTeamColor() != getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
        }
    }

    private void addLinearMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int i = -1; i <= 1; i += 2) {
            int newRow = row + i;
            int newCol = col;

            while (newRow >= 1 && newRow <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                } else if (piece.getTeamColor() != getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                    break;
                } else {
                    break;
                }

                newRow += i;
            }
        }

        for (int i = -1; i <= 1; i += 2) {
            int newRow = row;
            int newCol = col + i;

            while (newCol >= 1 && newCol <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                } else if (piece.getTeamColor() != getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                    break;
                } else {
                    break;
                }

                newCol += i;
            }
        }
    }

    private void addKnightMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int[] rows = {-2, -2, -1, -1, 1, 1, 2, 2};
        int[] cols = {-1, 1, -2, 2, -2, 2, -1, 1};

        for (int i = 0; i < 8; i++) {
            int newRow = row + rows[i];
            int newCol = col + cols[i];

            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null || piece.getTeamColor() != getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
    }

    private void addPawnMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {
        int row=myPosition.getRow();
        int col=myPosition.getColumn();

        if (getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (row == 2) {
                ChessPosition newPosition=new ChessPosition(row + 2, col);
                if (board.getPiece(newPosition) == null) {
                    ChessPosition confirmPosition=new ChessPosition(row + 1, col);
                    if (board.getPiece(confirmPosition) == null) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }

            ChessPosition newPosition=new ChessPosition(row + 1, col);
            if (isValidPosition(newPosition, board)) {
                if (board.getPiece(newPosition) == null && row == 7) {
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), PieceType.ROOK));
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), PieceType.KNIGHT));
                } else if (board.getPiece(newPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), null));
                }
            }

            newPosition=new ChessPosition(row + 1, col + 1);
            if (isValidPosition(newPosition, board)) {
                ChessPiece piece=board.getPiece(newPosition);
                if (piece != null && piece.getTeamColor() != getTeamColor() && row == 7) {
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                } else if (piece != null && piece.getTeamColor() != getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }

            newPosition=new ChessPosition(row + 1, col - 1);
            if (isValidPosition(newPosition, board)) {
                ChessPiece piece=board.getPiece(newPosition);
                if (piece != null && piece.getTeamColor() != getTeamColor() && row == 7) {
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                } else if (piece != null && piece.getTeamColor() != getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        } else {
            if (row == 7) {
                ChessPosition newPosition=new ChessPosition(row - 2, col);
                if (board.getPiece(newPosition) == null) {
                    ChessPosition confirmPosition=new ChessPosition(row - 1, col);
                    if (board.getPiece(confirmPosition) == null) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }

            ChessPosition newPosition=new ChessPosition(row - 1, col);
            if (isValidPosition(newPosition, board)) {
                if (board.getPiece(newPosition) == null && row == 2) {
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                } else if (board.getPiece(newPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }

            newPosition=new ChessPosition(row - 1, col + 1);
            if (isValidPosition(newPosition, board)) {
                ChessPiece piece = board.getPiece(newPosition);
                if (piece != null && piece.getTeamColor() != getTeamColor() && row == 2) {
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                } else if (piece != null && piece.getTeamColor() != getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }

            newPosition=new ChessPosition(row - 1, col - 1);
            if (isValidPosition(newPosition, board)) {
                ChessPiece piece = board.getPiece(newPosition);
                if (piece != null && piece.getTeamColor() != getTeamColor() && row == 2) {
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                } else if (piece != null && piece.getTeamColor() != getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
    }

    private boolean isValidPosition(ChessPosition position, ChessBoard board) {
        int row = position.getRow();
        int col = position.getColumn();

        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece piece=(ChessPiece) o;
        return pieceColor == piece.pieceColor && type == piece.type;
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
