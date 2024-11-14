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
  public PostLoginUI(String url, String authToken, String username) {
    server = new ServerFacade(url);
    sURL = url;
    this.authToken = authToken;
    gameList = new HashMap<>();

  }

  public void run(String username) {
    System.out.println("Logged in as " + username + ".");
    help();

    try (Scanner scanner = new Scanner(System.in)) {
      String command = " ";
      do {
        System.out.print("\n" + SET_TEXT_COLOR_GREEN + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_BLUE);
        command = scanner.nextLine();
        processCommand(command);
        System.out.print(SET_TEXT_COLOR_YELLOW + command);
      } while (!command.equals("logout"));
      System.out.println();
      logout();
    } catch (Throwable e) {
      System.out.print(e.toString());
    }
  }

  private void processCommand(String input) throws IOException, URISyntaxException {
    var integers=input.toLowerCase().split(" ");
    var command=(integers.length > 0) ? integers[0] : "help";
    var params=Arrays.copyOfRange(integers, 1, integers.length);

    switch (command) {
      case "list":
        System.out.print(list());
        break;
      case "create":
        System.out.print(create(params));
        break;
      case "join":
        System.out.print(join(params));
        break;
      case "observe":
        System.out.print(observe(params));
        break;
      case "logout":
        System.out.println("logout");
        break;
      case "quit":
        System.out.println("quit");
        break;
      default:
        help();
        break;
    }
  }

  private String observe(String[] params) {
    return null;
  }

  private String join(String[] params) {
    return null;
  }

  private boolean create(String[] params) {
    return false;
  }

  private String list() throws IOException, URISyntaxException {
    gameList.clear();
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
