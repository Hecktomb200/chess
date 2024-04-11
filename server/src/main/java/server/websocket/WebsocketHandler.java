package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GamesService;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.management.Notification;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebsocketHandler {
    private WebsocketSession session;
    private GamesService gamesService;
    
    public WebsocketHandler(WebsocketSession socket, GamesService game) {
        session = socket;
        gamesService = game;
    }
    
    @OnWebSocketMessage
    public void onScreen(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand comm = new Gson().fromJson(message, UserGameCommand.class);
        if (comm.getCommandType() == UserGameCommand.CommandType.JOIN_PLAYER) {
            joinPlayer(new Gson().fromJson(message, JoinPlayer.class), session);
        }
        if (comm.getCommandType() == UserGameCommand.CommandType.JOIN_OBSERVER) {
            joinObserver(new Gson().fromJson(message, JoinObserver.class), session);
        }
        if (comm.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            makeMove(new Gson().fromJson(message, MakeMove.class), session);
        }
        if (comm.getCommandType() == UserGameCommand.CommandType.RESIGN) {
            resignPlayer(new Gson().fromJson(message, Resign.class), session);
        }
    }

    private void resignPlayer(Object fromJson, Session session) {
    }

    private void makeMove(Object fromJson, Session session) {
        
    }

    private void joinObserver(Object fromJson, Session session) {
        
    }

    private void joinPlayer(JoinPlayer player, Session session) throws IOException, DataAccessException {
        String resp = gamesService.joinPlayer(player);

        if (resp.contains("Error")) {
            sendMessage(new Error(resp), session);
            return;
        }
        session.addSessionToGame(player.getGameID(), player.getAuthString(), session);
        sendMessage(new LoadMessage(player.getGameID()), session);
        String color;
        if(player.getPlayerColor() == ChessGame.TeamColor.WHITE) {
            color = "white";
        } else {
            color = "black";
        }
        var message = String.format("%s is joining as %s", player.getPlayerName(),color);
        var notification = new Notification(message);
        broadcastMessage(player.getGameID(), notification, player.getAuthString());
    }
}
