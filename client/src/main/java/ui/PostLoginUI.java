package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostLoginUI {
  public PostLoginUI(String url, String authToken, String username) {
    
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

  private void processCommand(String command) {

  }

  private void help() {
  }

  private void logout() {
  }
}
