package ui;

import model.GameData;

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

  private void processCommand(String command) {
  }
}
