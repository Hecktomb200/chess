package ui;

import java.io.IOException;
import java.util.Arrays;
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

  private void processCommand(String input) {
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

  private boolean list() {
    return false;
  }

  private void help() {
  }

  private void logout() {
  }
}
