import chess.*;
import client.PreLoginUI;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        var url = "http://localhost:8080";
        if(args.length == 1) {
            url = args[0];
        }

        new PreLoginUI(url).run();
    }
}