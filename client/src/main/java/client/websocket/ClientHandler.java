package client.websocket;
import client.ResponseException;
import webSocketMessages.serverMessages.LoadMessage;
public interface ClientHandler {
    void updateGame(LoadMessage loadGame) throws ResponseException;

    void printMessage(String message);
}
