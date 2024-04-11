package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GamesService;
//import webSocketMessages.serverMessages.Error;
//import webSocketMessages.serverMessages.LoadGame;
//import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

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

    private void joinPlayer(Object fromJson, Session session) {
        
    }


}
