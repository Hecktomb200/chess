package client;
import model.login.LoginResult;
import model.register.RegisterResult;
import server.ServerFacade;

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

    public String getCommands(String input) {
        try {
            var integers = input.toLowerCase().split(" ");
            var prompt = (integers.length > 0) ? integers[0] : "help";
            var params = Arrays.copyOfRange(integers, 1, integers.length);
            return switch (prompt) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public void run() {
        System.out.println("♕ Welcome to chess! Type 'help' to get started. ♕");
        System.out.print(this.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = this.getCommands(line);
                System.out.print(SET_TEXT_COLOR_GREEN + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_YELLOW + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
    }

    public String help() {
        return """
                    - register <USERNAME> <PASSWORD> <EMAIL>
                    - login <USERNAME> <PASSWORD>
                    - quit
                    """;
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            LoginResult loginData = server.login(params[0], params[1]);
            new PostLoginUI(loginData.authToken(), url).run(params[0]);
            return "You have been logged out.\n";
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            RegisterResult registerData = server.register(params[0], params[1], params[2]);
            new PostLoginUI(registerData.authToken(), url).run(params[0]);
            return "You have been logged out.";
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

}
