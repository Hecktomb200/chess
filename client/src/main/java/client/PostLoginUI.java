package client;

import model.GameData;
import model.listGames.ListGamesResult;
import client.ResponseException;
import client.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
public class PostLoginUI {
    private final ServerFacade server;
    private final HashMap<Integer, GameData> gameList;
    private final String authToken;

    public PostLoginUI(String auth, String url) {
        server = new ServerFacade(url);
        gameList = new HashMap<>();
        authToken = auth;
    }

    public String getCommands(String input) {
        try {
            var integers = input.toLowerCase().split(" ");
            var prompt = (integers.length > 0) ? integers[0] : "help";
            var params = Arrays.copyOfRange(integers, 1, integers.length);
            return switch (prompt) {
                case "list" -> listGames();
                //case "create" -> createGame(params);
                //default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String listGames() throws ResponseException {
        ListGamesResult gamesListed = server.listGames(authToken);
        String list = "";
        int i = 0;
        var games = gamesListed.games();

        for(GameData game: games) {
            list += i + ": " + game.gameName() + "\n";
            i++;
            gameList.put(i, game);
        }
        return list;
    }
}
