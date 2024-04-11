package client.websocket;
import server.ResponseException;
import webSocketMessages.serverMessages.LoadMessage;
public interface ClientHandler {
    void updateGame(LoadMessage loadGame) throws ResponseException, client.ResponseException;

    void printMessage(String message);
}
