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

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebsocketHandler {
    private WebsocketSession sessions;
    private GamesService gamesService;
    
    public WebsocketHandler(WebsocketSession socket, GamesService game) {
        sessions = socket;
        gamesService = game;
    }
    
    @OnWebSocketMessage
    public void onScreen(Session session, String mess) throws IOException, DataAccessException {
        UserGameCommand comm = new Gson().fromJson(mess, UserGameCommand.class);
        switch (comm.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(new Gson().fromJson(mess, JoinPlayerCommand.class), session);
            case JOIN_OBSERVER -> joinObserver(new Gson().fromJson(mess, JoinObserverCommand.class), session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(mess, MoveCommand.class), session);
            case RESIGN -> resignPlayer(new Gson().fromJson(mess, ResignCommand.class), session);
            case LEAVE -> leave(new Gson().fromJson(mess, LeaveCommand.class), session);
        }
    }

    private void resignPlayer(ResignCommand resignData, Session session) throws DataAccessException, IOException {
        String response = gamesService.resignPlayer(resignData);
        if(response.contains("Error")) {
            sendMessage(new ErrorMessage(response), session);
            return;
        }
        if(sessions.checkResigned(resignData.getGameID())) {
            sendMessage(new ErrorMessage("Another player has already resigned"), session);
            return;
        }
        sessions.addGameToResigned(resignData.getGameID());
        var mess = String.format("%s has resigned", response);
        var notify = new NotificationMessage(mess);
        broadcastMessage(resignData.getGameID(), notify, null);
    }

    private void makeMove(MoveCommand moveData, Session session) throws IOException, DataAccessException{
        if(sessions.checkResigned(moveData.getGameID())) {
            sendMessage(new ErrorMessage("A player has already resigned"), session);
            return;
        }
        String resp = gamesService.makeMove(moveData);
        if (resp.contains("Error")) {
            sendMessage(new ErrorMessage(resp), session);
            return;
        }
        sendMessage(new LoadMessage(moveData.getGameID()), session);
        broadcastMessage(moveData.getGameID(), new LoadMessage(moveData.getGameID()), moveData.getAuthString());
        String[] responseArray = resp.split(",");
        if(Objects.equals(responseArray[1], "checkmate")) {
            broadcastMessage(moveData.getGameID(), new NotificationMessage(String.format("%s is in checkmate! Game over!", responseArray[2])), "null");
        } else if(Objects.equals(responseArray[1], "check")) {
            broadcastMessage(moveData.getGameID(), new NotificationMessage(String.format("%s is in check!", responseArray[2])), "null");
        }
        var mess = String.format("%s moved the piece at %s to %s", responseArray[0],
                moveData.getMove().getStartPosition().toString(), moveData.getMove().getEndPosition().toString());
        var notify = new NotificationMessage(mess);
        broadcastMessage(moveData.getGameID(), notify, moveData.getAuthString());
    }

    private void joinObserver(JoinObserverCommand playerData, Session session) throws IOException, DataAccessException {
        String resp = gamesService.joinObserver(playerData);
        if (resp.contains("Error")) {
            sendMessage(new ErrorMessage(resp), session);
            return;
        }
        sessions.addSessionToGame(playerData.getGameID(), playerData.getAuthString(), session);
        sendMessage(new LoadMessage(playerData.getGameID()), session);
        var mess = String.format("%s joined as an observer", resp);
        var notify = new NotificationMessage(mess);
        broadcastMessage(playerData.getGameID(), notify, playerData.getAuthString());
    }

    private void joinPlayer(JoinPlayerCommand player, Session session) throws IOException, DataAccessException {
        String resp = gamesService.joinPlayer(player);

        if (resp.contains("Error")) {
            sendMessage(new ErrorMessage(resp), session);
            return;
        }
        sessions.addSessionToGame(player.getGameID(), player.getAuthString(), session);
        sendMessage(new LoadMessage(player.getGameID()), session);
        String color;
        if(player.getPlayerColor() == ChessGame.TeamColor.WHITE) {
            color = "white";
        } else {
            color = "black";
        }
        var mess = String.format("%s is joining as %s", player.getPlayerName(),color);
        var notify = new NotificationMessage(mess);
        broadcastMessage(player.getGameID(), notify, player.getAuthString());
    }

    private void sendMessage(ServerMessage mess, Session session) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(new Gson().toJson(mess));
        }
    }

    private void broadcastMessage(Integer gameID, ServerMessage mess, String exceptThisAuthToken) throws IOException {
        ConcurrentHashMap<String, Session> gameSessions = sessions.getSessions(gameID);
        for (Map.Entry<String, Session> authMapBreak : gameSessions.entrySet()) {
            if (!(Objects.equals(authMapBreak.getKey(), exceptThisAuthToken))) {
                if (authMapBreak.getValue().isOpen()) {
                    authMapBreak.getValue().getRemote().sendString(new Gson().toJson(mess));
                }
            }
        }
    }

    private void leave(LeaveCommand leaveData, Session session) throws DataAccessException, IOException {
        String response = gamesService.leavePlayer(leaveData);
        sessions.removeSessions(leaveData.getGameID(), leaveData.getAuthString(), session);
        var mess = String.format("%s has left the game", response);
        var notify = new NotificationMessage(mess);
        broadcastMessage(leaveData.getGameID(), notify, leaveData.getAuthString());
    }

    public void clearResignedIDs () {
        sessions.clearResignedIDs();
    }

}
