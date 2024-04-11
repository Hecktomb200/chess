package client.websocket;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import server.ResponseException;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
public class WebsocketFacade extends Endpoint implements MessageHandler.Whole<String>{
    ClientHandler clientHandler;
    Session session;
    public WebsocketFacade(String url, ClientHandler clientHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI sURI = new URI(url + "/connect");
            this.clientHandler = clientHandler;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, sURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String mes) {
                    ServerMessage message = new Gson().fromJson(mes, ServerMessage.class);
                    switch (message.getServerMessageType()) {
                        case LOAD_GAME -> { try { clientHandler.updateGame(new Gson().fromJson(mes, LoadMessage.class));
                            } catch (ResponseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        case NOTIFICATION -> clientHandler.printMessage(new Gson().fromJson(mes, NotificationMessage.class).getMessage());
                        case ERROR -> clientHandler.printMessage(new Gson().fromJson(mes, ErrorMessage.class).getErrorMessage());
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void joinPlayer(String authToken, Integer gameID, ChessGame.TeamColor playerColor, String playerName) throws ResponseException {
        sendToServer(new JoinPlayerCommand(authToken, gameID, playerColor, playerName));
    }

    public void joinObserver(String authToken, Integer gameID) throws ResponseException {
        sendToServer(new JoinObserverCommand(authToken, gameID));
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move) throws ResponseException {
        sendToServer(new MoveCommand(authToken, gameID, move));
    }

    public void resignGame(String authToken, Integer gameID) throws ResponseException {
        sendToServer(new ResignCommand(authToken, gameID));
    }

    public void leaveGame(String authToken, Integer gameID) throws ResponseException {
        sendToServer(new LeaveCommand(authToken, gameID));
    }

    private void sendToServer(UserGameCommand command) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}



    @Override
    public void onMessage(String s) {}
}
