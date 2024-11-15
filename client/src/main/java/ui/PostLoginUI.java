package ui;

import model.GameData;
import model.listgames.ListGamesResult;
import server.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostLoginUI {
  private final ServerFacade server;
  private final String sURL;
  private final String authToken;
  private final HashMap<Integer, GameData> gameList;
  private final String username;
  private final Scanner scanner;
  public PostLoginUI(String url, String authToken, String username) {
    server = new ServerFacade(url);
    sURL = url;
    this.authToken = authToken;
    gameList = new HashMap<>();
    this.username = username;
    this.scanner = new Scanner(System.in);
  }

  public void run(String username) throws IOException, URISyntaxException {
    System.out.println("Logged in as " + username + ".");
    help();

    String command = " ";
    do {
      System.out.print("\n" + SET_TEXT_COLOR_GREEN + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_BLUE);
      command = scanner.nextLine();
      processCommand(command);
      System.out.print(SET_TEXT_COLOR_YELLOW + command);
    } while (!command.equals("logout"));
    System.out.println();
  }

  private void processCommand(String input) throws IOException, URISyntaxException {
    var integers=input.toLowerCase().split(" ");
    var command=(integers.length > 0) ? integers[0] : "help";
    var params=Arrays.copyOfRange(integers, 1, integers.length);

    switch (command) {
      case "list":
        list();
        break;
      case "create":
        create(params);
        break;
      case "join":
        join(params);
        break;
      case "observe":
        observe(params);
        break;
      case "logout":
        logout();
        break;
      case "quit":
        System.out.println("Exiting");
        break;
      case "help":
        help();
        break;
      default:
        invalidCommandMessage();
        break;
    }
  }

  private void invalidCommandMessage() {
    System.out.println("Invalid command. Type 'help' for a list of valid commands.");
  }

  private String observe(String[] params) throws IOException, URISyntaxException {
    if (params.length != 1) {
      throw new IOException("Expected: <ID>");
    }
    int gameID = Integer.parseInt(params[0]);
    GameData game = getGameById(gameID);
    joinGame(game, null);
    new GameplayUI(game, sURL, authToken, username).run();
    return String.format("Chess game %s left.", params[0]);
  }

  private GameData getGameById(int gameID) {
    return gameList.get(gameID);
  }

  private void joinGame(GameData game, String playerColor) throws IOException, URISyntaxException {
    System.out.println("Joining game...");
    server.joinGame(authToken, playerColor, game.gameID());
    list();
  }

  private String join(String[] params) throws IOException, URISyntaxException {
    if (params.length != 2) {
      throw new IOException("Expected: <ID> [WHITE | BLACK]");
    }
    int gameId = Integer.parseInt(params[0]);
    String playerColor = params[1];

    GameData game = getGameById(gameId);
    joinGame(game, playerColor);
    new GameplayUI(game, sURL, authToken, username).run();
    return String.format("Chess game %s left.", params[0]);
  }

  private String create(String[] params) throws IOException, URISyntaxException {
    if (params.length != 1) {
      throw new IOException("Expected: <GAME NAME>");
    }
    createGame(params[0]);
    return String.format("Chess game %s created.", params[0]);
  }

  private void createGame(String gameName) throws IOException, URISyntaxException {
    server.createGame(gameName, authToken);
  }

  private String list() throws IOException, URISyntaxException {
    ListGamesResult gamesListed = getGames();
    return buildGamesList(gamesListed);
  }

  private ListGamesResult getGames() throws IOException, URISyntaxException {
    return server.listGames(authToken);
  }

  private String buildGamesList(ListGamesResult gamesListed) {
    StringBuilder formattedList = new StringBuilder();
    var games = gamesListed.games();
    int index = 0;
    for (GameData game : games) {
      formattedList.append(formatGameEntry(index, game));
      gameList.put(index, game);
      index++;
    }
    return formattedList.toString();
  }

  private String formatGameEntry(int index, GameData game) {
    return String.format("%d: %s -- White Player: %s, Black Player: %s%n",
            index, game.gameName(), game.whiteUsername(), game.blackUsername());
  }

  private void help() {
    System.out.println("""
                - list
                - create <GAME NAME>
                - join <ID> [WHITE | BLACK]
                - observe <ID>
                - logout
                - quit
                - help
                """);
  }

  private void logout() throws IOException, URISyntaxException {
    server.logoutUser(authToken);
  }
}
