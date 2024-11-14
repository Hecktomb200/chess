package ui;

import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreLoginUI {
  private final ServerFacade server;
  private final String url;

  public PreLoginUI(String serverUrl) {
    server = new ServerFacade(serverUrl);
    url = serverUrl;
  }

  public void run() {
    displayWelcomeMessage();
    help();

    try (Scanner scanner = new Scanner(System.in)) {
      String command;
      do {
        System.out.print("\n" + SET_TEXT_COLOR_YELLOW + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
        command = scanner.nextLine();
        processCommand(command);
      } while (!command.equalsIgnoreCase("quit"));
    }
    System.out.println();
  }

  private void displayWelcomeMessage() {
    System.out.println("♕ Welcome to 240 chess! Type 'help' to get started. ♕");
  }

  private void processCommand(String line) {
  }

  private void help() {
  }
}
