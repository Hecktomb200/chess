package ui;

import model.GameData;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayUI {
  public GameplayUI(GameData game, String sURL, String authToken, String username) {

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

  public void processCommand(String input) {
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
        case "move":
          makeMove(params);
        case "resign":
          resignGame();
        case "highlight":
          highlightMoves(params);
        default:
          invalidCommandMessage();
          break;
      }
  }

  private void highlightMoves(String[] params) {

  }

  private void resignGame() {

  }

  private void makeMove(String[] params) {

  }

  private void redraw() {

  }

  private void invalidCommandMessage() {
    System.out.println("Invalid command. Type 'help' for a list of valid commands.");
  }
}
