package client;

import model.GameData;
import model.listGames.ListGamesResult;
import server.ServerFacade;
import server.ResponseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostLoginUI {
    private final ServerFacade server;
    private final String sURL;
    private final String username;
    private final HashMap<Integer, GameData> gameList;
    private final String authToken;

    public PostLoginUI(String serverUrl, String auth, String user) {
        server = new ServerFacade(serverUrl);
        sURL = serverUrl;
        authToken = auth;
        gameList = new HashMap<>();
        username = user;
    }

    public String getCommands(String input) {
        try {
            var integers = input.toLowerCase().split(" ");
            var prompt = (integers.length > 0) ? integers[0] : "help";
            var params = Arrays.copyOfRange(integers, 1, integers.length);
            return switch (prompt) {
                case "list" -> list();
                case "create" -> create(params);
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> "logout";
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        } catch (client.ResponseException e) {
            throw new RuntimeException(e);
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

    public void logout() throws ResponseException, client.ResponseException {
        server.logout(authToken);
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_GREEN + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_BLUE);
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

    public String list() throws ResponseException, client.ResponseException {
        gameList.clear();
        ListGamesResult gamesListed = server.listGames(authToken);
        String list = "";
        int i = 0;
        var games = gamesListed.games();

        for(GameData game: games) {
            list += i + ": " + game.gameName() + " --White Player: " + game.whiteUsername()
                    + ", Black Player: " + game.blackUsername() + "\n";
            gameList.put(i, game);
            i++;
        }
        return list;
    }

    public String create(String... params) throws ResponseException, client.ResponseException {
        if (params.length == 1) {
            server.createGame(params[0], authToken);
            return String.format("Chess game %s created.", params[0]);
        }
        throw new ResponseException("Expected: <GAME NAME>");
    }

    public String join(String... params) throws server.ResponseException, client.ResponseException {
        if (params.length == 2) {
            GameData game = gameList.get(Integer.parseInt(params[0]));
            System.out.println("Attempting to join game...");
            server.joinGame(authToken, params[1], game.gameID());
            list();
            game = gameList.get(Integer.parseInt(params[0]));
            System.out.println("Game joined!");
            new GameplayUI(game, sURL, authToken, username).run();
            return String.format("Chess game %s left.", params[0]);
        }
        throw new ResponseException("Expected: <ID> [WHITE | BLACK]");
    }

    public String observe(String... params) throws server.ResponseException, client.ResponseException {
        if (params.length == 1) {
            GameData game = gameList.get(Integer.parseInt(params[0]));
            System.out.println("Attempting to join game...");
            server.joinGame(authToken, null, game.gameID());
            list();
            game = gameList.get(Integer.parseInt(params[0]));
            System.out.println("Game joined!");
            new GameplayUI(game, sURL, authToken, username).run();
            return String.format("Chess game %s left.", params[0]);
        }
        throw new ResponseException("Expected: <ID>");
    }
}
