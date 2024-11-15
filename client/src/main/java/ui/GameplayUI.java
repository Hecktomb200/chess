package ui;

import model.GameData;
import model.listgames.ListGamesResult;
import server.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayUI {
  private final ServerFacade serverFacade;
  private final String username;

  private final String authToken;
  private GameData gameData;
  public GameplayUI(GameData game, String sURL, String authToken, String username) {
    serverFacade = new ServerFacade(sURL);
    this.authToken = authToken;
    gameData = game;
    this.username = username;
  }

  public void run() {
    try (Scanner scanner=new Scanner(System.in)) {
      String command=" ";
      do {
        System.out.print("\n" + SET_TEXT_COLOR_YELLOW + "[IN_GAME] >>> " + SET_TEXT_COLOR_GREEN);
        command=scanner.nextLine();
        processCommand(command);
        System.out.print(SET_TEXT_COLOR_YELLOW + command);
      } while (!command.equals("leave"));
      System.out.println();
    } catch (Throwable e) {
      System.out.print(e.toString());
    }
  }

  public void processCommand(String input) throws IOException, URISyntaxException {
      String[] integers=input.toLowerCase().split(" ");
      String command=(integers.length > 0) ? integers[0] : "help";
      String[] params=Arrays.copyOfRange(integers, 1, integers.length);
      switch (command) {
        case "redraw":
          redraw();
          break;
        case "leave":
          System.out.println("Leaving game");
          break;
        case "help":
          help();
          break;
        default:
          invalidCommandMessage();
          break;
      }
  }

  private void help() {
    System.out.println("""
                - redraw
                - leave
                """);
  }

  public String redraw() throws IOException, URISyntaxException {
    ListGamesResult gamesResult = serverFacade.listGames(authToken);
    GameData updatedGame = findGameById(gamesResult.games(), gameData.gameID());

    if (updatedGame != null) {
      gameData = updatedGame;
      return "\n" + draw();
    } else {
      throw new IOException("No game found.");
    }
  }

  private String draw() {
    if(Objects.equals(gameData.whiteUsername(), username)) {
      return drawWhiteBoard();
    }
    return drawBlackBoard();
  }

  private String drawBlackBoard() {
    return null;
  }

  private String drawWhiteBoard() {
    return null;
  }

  private GameData findGameById(Collection<GameData> games, int gameId) {
    for (GameData game : games) {
      if (game.gameID() == gameId) {
        return game;
      }
    }
    return null;
  }



  private void invalidCommandMessage() {
    System.out.println("Invalid command. Type 'help' for a list of valid commands.");
  }
}
