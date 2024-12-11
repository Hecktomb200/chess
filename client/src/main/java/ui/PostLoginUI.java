package ui;

import model.GameData;
import model.listgames.ListGamesResult;
import server.ServerFacade;
import websocket.WebsocketFacade;

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
  private static final String INVALID_GAME_NUMBER_MESSAGE = "Invalid game number. Please enter a valid number for the game.";
  public PostLoginUI(String url, String authToken, String username) throws IOException {
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
    try {
      var integers=input.toLowerCase().split(" ");
      var command=(integers.length > 0) ? integers[0] : "help";
      var params=Arrays.copyOfRange(integers, 1, integers.length);

      switch (command) {
        case "list":
          System.out.println(list());
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
        case "help":
          help();
          break;
        default:
          invalidCommandMessage();
          break;
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.out.println("You are still logged in. Try again.");
    }
  }

  private void invalidCommandMessage() {
    System.out.println("Invalid command. Type 'help' for a list of valid commands.");
  }

  private String observe(String[] params) throws Exception {
    if (params.length != 1) {
      throw new IOException("Expected: <GAME#>");
    }
    try {
      int gameID = Integer.parseInt(params[0]);
      GameData game = getGameById(gameID);
      boolean observer = true;
      if (game == null) {
        throw new IOException(INVALID_GAME_NUMBER_MESSAGE);
      }
      //webSocketFacade.connect(authToken, game.gameID(), null, username);
      String playerColor = null;
      server.joinGame(authToken, playerColor, game.gameID());
      new GameplayUI(game, sURL, authToken, username, observer, playerColor).run();
      return String.format("Chess game %s left.", params[0]);
    } catch (NumberFormatException e) {
      throw new IOException(INVALID_GAME_NUMBER_MESSAGE);
    }
  }

  private GameData getGameById(int gameID) {
    return gameList.get(gameID);
  }

  private void joinGame(GameData game, String playerColor) throws Exception {
    try {
      System.out.println("Attempting to join game...");
      if (game == null) {
        throw new IOException(INVALID_GAME_NUMBER_MESSAGE);
      }
      if (game.whiteUsername() != null && game.blackUsername() != null) {
        throw new IOException("Game is already full. You cannot join.");
      }
      if ("white".equalsIgnoreCase(playerColor) && game.whiteUsername() != null) {
        throw new IOException("The white team is already taken. Please choose black.");
      } else if ("black".equalsIgnoreCase(playerColor) && game.blackUsername() != null) {
        throw new IOException("The black team is already taken. Please choose white.");
      }
      server.joinGame(authToken, playerColor, game.gameID());
      //webSocketFacade.connect(authToken, game.gameID(), playerColor, username);
      list();
    } catch (NumberFormatException e) {
      throw new IOException(INVALID_GAME_NUMBER_MESSAGE);
    }
  }

  private String join(String[] params) throws Exception {
    if (params.length != 2) {
      throw new IOException("Expected: <GAME#> [WHITE | BLACK]");
    }
    try {
      int gameId = Integer.parseInt(params[0]);
      String playerColor = params[1];
      if (gameId <= 0) {
        throw new IOException(INVALID_GAME_NUMBER_MESSAGE);
      }

      GameData game = getGameById(gameId);
      joinGame(game, playerColor);
      boolean observer = false;
      new GameplayUI(game, sURL, authToken, username, observer, playerColor).run();
      return String.format("Chess game %s left.", params[0]);
    } catch (NumberFormatException e) {
      throw new IOException(INVALID_GAME_NUMBER_MESSAGE);
    }
  }

  private String create(String[] params) throws Exception {
    if (params.length != 1) {
      throw new IOException("Expected: <GAME NAME>");
    }
    createGame(params[0]);
    list();
    return String.format("Chess game %s created.", params[0]);
  }

  private void createGame(String gameName) throws Exception {
    server.createGame(gameName, authToken);
    ListGamesResult gamesListed = getGames();
    buildGamesList(gamesListed);
  }

  private String list() throws Exception {
    ListGamesResult gamesListed = getGames();
    return buildGamesList(gamesListed);
  }

  private ListGamesResult getGames() throws Exception {
    return server.listGames(authToken);
  }

  private String buildGamesList(ListGamesResult gamesListed) {
    StringBuilder formattedList = new StringBuilder();
    var games = gamesListed.games();
    int index = 1;
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
                - join <GAME#> [WHITE | BLACK]
                - observe <GAME#>
                - logout
                - help
                """);
  }

  private void logout() throws Exception {
    server.logoutUser(authToken);
  }
}
