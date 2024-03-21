package client;

import model.GameData;
import model.listGames.ListGamesResult;
import client.ResponseException;
import client.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import static ui.EscapeSequences.*;

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
                case "create" -> createGame(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public void run(String username) {
        System.out.println("Logged in as " + username + ".");
        System.out.print(this.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("logout")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = this.getCommands(line);
                System.out.print(SET_TEXT_COLOR_YELLOW + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        try {
            System.out.println();
            this.logout();
        } catch (Throwable e) {
            var msg = e.toString();
            System.out.print(msg);
        }

    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_GREEN + "[LOGGED IN]>>> " + SET_TEXT_COLOR_BLUE);
    }

    public String help() {
        return """
                    - create <GAME NAME>
                    - list
                    - join <ID> [WHITE | BLACK]
                    - observe <ID>
                    - logout
                    - quit
                    - help
                    """;
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

    public String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            server.createGame(params[0], authToken);
            return String.format("Chess game %s created.", params[0]);
        }
        throw new ResponseException(400, "Expected: <GAME NAME>");
    }
}
