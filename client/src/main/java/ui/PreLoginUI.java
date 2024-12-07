package ui;

import model.login.LoginResult;
import model.register.RegisterResult;
import server.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreLoginUI {
  private final ServerFacade server;
  private final String url;

  public PreLoginUI(String serverUrl) {
    server = new ServerFacade(serverUrl);
    url = serverUrl;
  }

  public void run() throws IOException, URISyntaxException {
    displayWelcomeMessage();
    help();

    try {
      Scanner scan = new Scanner(System.in);
      String command = " ";
      do {
        System.out.print("\n" + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
        command = scan.nextLine();
        processCommand(command);
      } while (!command.equalsIgnoreCase("quit"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    System.out.println();
  }

  private void displayWelcomeMessage() {
    System.out.println("♕ Welcome to 240 chess! Type 'help' to get started. ♕");
  }

  private void processCommand(String input) throws IOException, URISyntaxException {
    try {
      String[] commandParts=input.toLowerCase().split(" ");
      String command=commandParts[0];
      String[] params=Arrays.copyOfRange(commandParts, 1, commandParts.length);

      switch (command) {
        case "login":
          login(params);
          break;
        case "register":
          register(params);
          break;
        case "quit":
          System.out.println("Exiting the application.");
          break;
        case "help":
          help();
          break;
        default:
          badCommandMessage();
          break;
      }
    } catch (Exception e) {
      System.out.println(getMessage(e));
      System.out.println("You are not yet logged in. Try again.");
    }
  }

  private void badCommandMessage() {
    System.out.println("Invalid command. Type 'help' for a list of valid commands.");
  }

  private String getMessage(Exception e) {
    if (e.getMessage().equals("Username is already taken.")) {
      return "Username already taken. Please try again.";
    } else if (e.getMessage().equals("Error: unauthorized")) {
      return "Invalid username or password. Please try again.";
    } else if (e instanceof IllegalArgumentException) {
      return "Invalid argument provided. Please check your input.";
    } else if (e instanceof NumberFormatException) {
      return "Please enter a valid number. It seems you entered something that isn't a number.";
    } else {
      return e.getMessage();
    }
  }

  private void register(String[] params) throws Exception {
    if (params.length != 3) {
      throw new IOException("Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }
      String username=params[0];
      String password=params[1];
      String email=params[2];
      try {
        RegisterResult registerData=server.registerUser(username, password, email);
        new PostLoginUI(url, registerData.authToken(), username).run(username);
      } catch (IOException e) {
          if (e.getMessage().equals("Username is already taken.")) {
            System.out.println("Error: " + e.getMessage());
          }
        }
    }

  private void login(String[] params) throws Exception {
    if (params.length != 2) {
      throw new IOException("Expected: <USERNAME> <PASSWORD>");
    }
    String username = params[0];
    String password = params[1];
    LoginResult loginData = server.loginUser(username, password);
    new PostLoginUI(url, loginData.authToken(), username).run(username);
  }


  private void help() {
    System.out.println("""
                    - register <USERNAME> <PASSWORD> <EMAIL>
                    - login <USERNAME> <PASSWORD>
                    - help
                    - quit
                    """);
  }
}
