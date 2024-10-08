package chess;

import java.util.ArrayList;
import java.util.Collection;

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
        this.board=new ChessBoard();
        this.teamColor=TeamColor.WHITE;
        this.board.resetBoard();
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
        this.teamColor=team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece chessPiece=board.getPiece(startPosition);
        if (chessPiece == null) {
            return null;
        }
        Collection<ChessMove> possibleMoves=chessPiece.pieceMoves(board, startPosition);
        ArrayList<ChessMove> validMoves=new ArrayList<>();

        for (ChessMove move : possibleMoves) {
            try {
                if (moveIsValid(move)) {
                    validMoves.add(move);
                }
            } catch (InvalidMoveException e) {
                throw new RuntimeException(e);
            }
        }

        if (chessPiece.getPieceType() == ChessPiece.PieceType.KING) {
            ChessMove queenSide=new ChessMove(startPosition, new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 2), null);
            ChessMove kingSide=new ChessMove(startPosition, new ChessPosition(startPosition.getRow(), startPosition.getColumn() + 2), null);
            if (canDoCastling(queenSide)) {
                validMoves.add(queenSide);
            }
            if (canDoCastling(kingSide)) {
                validMoves.add(kingSide);
            }
        }

        return validMoves;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param move the chess move being tested
     * @return If the move being tested is valid
     * @throws RuntimeException if an error occurs while testing
     */
    private boolean moveIsValid(ChessMove move) throws InvalidMoveException {
        ChessBoard oldBoard=board.clone();
        try {
            ChessPiece piece=board.getPiece(move.getStartPosition());
            //System.out.println(board.toString());
            //System.out.println();
            if (move.getPromotionPiece() == null) {
                board.addPiece(move.getEndPosition(), piece);
            } else {
                board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
            }

            board.addPiece(move.getStartPosition(), null);
            if (!isInCheck(piece.getTeamColor())) {
                return true;
            }

            //this.board = oldBoard;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            this.board=oldBoard;
        }
        return false;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece=board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("No piece at the start position");
        }

        if (!isValidStartPosition(move.getStartPosition()) || !isValidEndPosition(move.getEndPosition())) {
            throw new InvalidMoveException("Invalid move position");
        }

        if (!isValidMove(move)) {
            throw new InvalidMoveException("Invalid move");
        }

        if (piece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("It's not your turn");
        }

        makeMoveInternal(move, piece);
        switchTurn();
    }

    /**
     * Checks if it's a valid start position
     *
     * @param position
     * @return
     */
    private boolean isValidStartPosition(ChessPosition position) {
        return isValidPosition(position);
    }

    /**
     * Checks if it's a valid end position
     *
     * @param position
     * @return
     */
    private boolean isValidEndPosition(ChessPosition position) {
        return isValidPosition(position);
    }

    /**
     * Checks if it's a valid position
     *
     * @param position
     * @return
     */
    private boolean isValidPosition(ChessPosition position) {
        int row=position.getRow();
        int column=position.getColumn();
        return row >= 1 && row <= 8 && column >= 1 && column <= 8;
    }

    /**
     * Checks if it's a valid move
     *
     * @param move
     * @return
     */
    private boolean isValidMove(ChessMove move) throws InvalidMoveException {
        return validMoves(move.getStartPosition()).contains(move);
    }

    /**
     * Makes the desired move
     *
     * @param move
     * @param piece
     */
    private void makeMoveInternal(ChessMove move, ChessPiece piece) {
        if (piece.getPieceType() == ChessPiece.PieceType.KING && Math.abs(move.getEndPosition().getColumn() - move.getStartPosition().getColumn()) == 2) {
            int row=piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : 8;
            ChessPosition rookPosition=move.getEndPosition().getColumn() < move.getStartPosition().getColumn() ? new ChessPosition(row, 1) : new ChessPosition(row, 8);
            ChessPosition newRookPosition=move.getEndPosition().getColumn() < move.getStartPosition().getColumn() ? new ChessPosition(row, 4) : new ChessPosition(row, 6);
            board.addPiece(newRookPosition, board.getPiece(rookPosition));
            board.addPiece(rookPosition, null);
            ChessPosition newKingPosition=move.getEndPosition().getColumn() < move.getStartPosition().getColumn() ? new ChessPosition(row, 3) : new ChessPosition(row, 7);
            board.addPiece(newKingPosition, piece);
            board.addPiece(move.getStartPosition(), null);
        } else {
            if (move.getPromotionPiece() == null) {
                board.addPiece(move.getEndPosition(), piece);
            } else {
                board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
            }
            board.addPiece(move.getStartPosition(), null);
        }
    }



    /**
     * Switches current turn to other color
     */
    private void switchTurn() {
        if (getTeamTurn() == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
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
        ChessPosition checkKing = checkKingPosition(teamColor);
        if (checkKing == null) {
            return false;
        }
        Collection<ChessPosition> enemyPositions = findEnemyPositions(teamColor);
        for (ChessPosition position : enemyPositions) {
            ChessPiece enemyPiece = board.getPiece(position);
            Collection<ChessMove> enemyMoves = enemyPiece.pieceMoves(board, position);
            if (canCheckKing(enemyMoves, checkKing)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param teamColor
     * @return
     */
    private ChessPosition checkKingPosition(TeamColor teamColor) {
        for (int row=1; row < 9; row++) {
            for (int col=1; col < 9; col++) {
                ChessPiece piece=board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return new ChessPosition(row, col);
                }
            }
        }
        return null;
    }

    /**
     *
     * @param teamColor
     * @return
     */
    private Collection<ChessPosition> findEnemyPositions(TeamColor teamColor) {
        ArrayList<ChessPosition> enemyPositions = new ArrayList<>();
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() != teamColor) {
                    enemyPositions.add(new ChessPosition(row, col));
                }
            }
        }
        return enemyPositions;
    }

    private boolean canCheckKing(Collection<ChessMove> enemyMoves, ChessPosition checkKing) {
        for (ChessMove move : enemyMoves) {
            if (move.getEndPosition().equals(checkKing)) {
                return true;
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
        ChessPosition checkKing = checkKingPosition(teamColor);
        if (checkKing == null) {
            return false;
        }
        if (!isInCheck(teamColor)) {
            return false;
        }
        ChessPosition kingPosition = checkKingPosition(teamColor);
        if (!validMoves(kingPosition).isEmpty()) {
            return false;
        }
        ArrayList<ChessPosition> teamPositions = new ArrayList<>();
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() == teamColor) {
                    teamPositions.add(new ChessPosition(row, col));
                }
            }
        }
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (ChessPosition position : teamPositions) {
            moves.addAll((validMoves(position)));
        }
        if (!moves.isEmpty()) {
            return false;
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
        if (isInCheck(teamColor)) {
            return false;
        }
        if (isInCheckmate(teamColor)) {
            return false;
        }
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(position).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean canDoCastling(ChessMove move) {
        if (move.getEndPosition().getColumn() - move.getStartPosition().getColumn() != 2 &&
                move.getEndPosition().getColumn() - move.getStartPosition().getColumn() != -2) {
            return false;
        }

        ChessPiece piece = board.getPiece(move.getStartPosition());
        ChessPosition kingPosition=checkKingPosition(piece.getTeamColor());
        if (kingPosition == null) {
            return false;
        }
        if (kingPosition.getRow() != 1 && kingPosition.getColumn() != 5) {
            if (kingPosition.getRow() != 8 && kingPosition.getColumn() != 5) {
                return false;
            }
        }

        ChessPosition rookPosition;
        if (piece.getTeamColor() == TeamColor.WHITE) {
            if (move.getEndPosition().getColumn() < move.getStartPosition().getColumn()) {
                rookPosition = new ChessPosition(1, 1); // queenSide
            } else {
                rookPosition = new ChessPosition(1, 8); // kingSide
            }
        } else {
            if (move.getEndPosition().getColumn() < move.getStartPosition().getColumn()) {
                rookPosition = new ChessPosition(8, 1); // queenSide
            } else {
                rookPosition = new ChessPosition(8, 8); // kingSide
            }
        }

        if (board.getPiece(rookPosition) == null || board.getPiece(rookPosition).getPieceType() != ChessPiece.PieceType.ROOK) {
            return false;
        }

        if (isInCheck(getTeamTurn())) {
            return false;
        }

        int startColumn =rookPosition.getColumn();
        int endColumn =kingPosition.getColumn();
        if (startColumn < endColumn) {
            for (int column = startColumn + 1; column < endColumn; column++) {
                ChessPosition position = new ChessPosition(move.getStartPosition().getRow(), column);
                piece = board.getPiece(position);
                if (piece != null) {
                    return false; // Enemy piece is in the way
                }
            }
        } else {
            for (int column=startColumn - 1; column > endColumn; column--) {
                ChessPosition position=new ChessPosition(move.getStartPosition().getRow(), column);
                piece=board.getPiece(position);
                if (piece != null) {
                    return false; // Enemy piece is in the way
                }
            }
        }

        ChessBoard oldBoard = board.clone();
        piece = board.getPiece(move.getStartPosition());
        try {
            makeMoveInternal(move, board.getPiece(move.getStartPosition()));
            if (isInCheck(piece.getTeamColor())) {
                return false;
            }
            if (castleInCheck(move, board)) {
                return false;
            }
        } finally {
            board = oldBoard;
        }

        return true;
    }

    private boolean castleInCheck(ChessMove move, ChessBoard board) {
        ChessPosition rookNewPosition = move.getEndPosition().getColumn() < move.getStartPosition().getColumn() ?
                new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn() + 1) :
                new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn() - 1);

        Collection<ChessPosition> enemyPositions = findEnemyPositions(teamColor);
        for (ChessPosition position : enemyPositions) {
            ChessPiece enemyPiece=board.getPiece(position);
            Collection<ChessMove> enemyMoves=enemyPiece.pieceMoves(board, position);
            for (ChessMove enemyMove : enemyMoves) {
                if (enemyMove.getEndPosition().equals(rookNewPosition)) {
                    return true;
                }
            }
        }

        int startColumn = Math.min(move.getStartPosition().getColumn(), move.getEndPosition().getColumn());
        int endColumn = Math.max(move.getStartPosition().getColumn(), move.getEndPosition().getColumn());
        for (int column = startColumn + 1; column < endColumn; column++) {
            ChessPosition position = new ChessPosition(move.getStartPosition().getRow(), column);
            Collection<ChessPosition> enemyPositionsThrough = findEnemyPositions(teamColor);
            for (ChessPosition enemyPositionThrough : enemyPositionsThrough) {
                ChessPiece enemyPieceThrough = board.getPiece(enemyPositionThrough);
                Collection<ChessMove> enemyMovesThrough = enemyPieceThrough.pieceMoves(board, enemyPositionThrough);
                for (ChessMove enemyMoveThrough : enemyMovesThrough) {
                    if (enemyMoveThrough.getEndPosition().equals(position)) {
                        return true;
                    }
                }
            }
        }

        return false;
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
