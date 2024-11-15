import chess.*;
import ui.PreLoginUI;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        var sUrl = "http://localhost:8080";
        if(args.length == 1) {
            sUrl = args[0];
        }
        new PreLoginUI(sUrl).run();
    }
}