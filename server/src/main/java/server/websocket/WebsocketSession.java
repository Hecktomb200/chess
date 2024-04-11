package server.websocket;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class WebsocketSession {
    static private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Session>> sessionMap = new ConcurrentHashMap<>();
    static private final ArrayList<Integer> resignedGames = new ArrayList<>();

    public void addSessionToGame(Integer gameID, String authToken, Session session) {
        ConcurrentHashMap<String, Session> existing = sessionMap.get(gameID);
        if(existing != null) {
            existing.put(authToken, session);
        } else {
            ConcurrentHashMap<String, Session> authMap = new ConcurrentHashMap<>();
            authMap.put(authToken, session);
            sessionMap.put(gameID, authMap);
        }
    }

    public void addGameToResigned(Integer gameID) {
        resignedGames.add(gameID);
    }

    public void removeSessions(Integer gameID, String authToken, Session session) {
        ConcurrentHashMap<String, Session> authMap = sessionMap.get(gameID);
        authMap.remove(authToken, session);
    }

    public boolean checkResigned(Integer gameID) {
        return resignedGames.contains(gameID);
    }

    public ConcurrentHashMap<String, Session> getSessions(Integer gameID) {
        return sessionMap.get(gameID);
    }

    public void clearResignedIDs () {
        resignedGames.clear();
    }
}
