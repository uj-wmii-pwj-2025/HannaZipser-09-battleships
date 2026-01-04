package src.main.java.org.example;

import src.main.java.org.example.board.BoardGenerator;
import src.main.java.org.example.board.BoardLoader;
import src.main.java.org.example.board.EnemyBoard;
import src.main.java.org.example.board.MyBoard;
import src.main.java.org.example.game.Game;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {

            Map<String, String> params = parseArguments(args);

            if (!params.containsKey("mode")) {
                System.err.println("Error: Missing -mode [server|client]");
                System.exit(1);
            }

            if (!params.containsKey("port")) {
                System.err.println("Error: Missing -port");
                System.exit(1);
            }

            String mode = params.get("mode");
            if (!mode.equals("server") && !mode.equals("client")) {
                System.err.println("Error: Invalid mode'");
                System.exit(1);
            }

            int port;
            try {
                port = Integer.parseInt(params.get("port"));
                if (port < 1 || port > 65535) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Invalid port exception");
                System.exit(1);
                return;
            }

            if (mode.equals("client") && !params.containsKey("host")) {
                System.err.println("Error: Missing -host");
                System.exit(1);
            }

            String host = params.get("host");
            String mapFile = params.get("map");

            char[][] initialBoard;
            if (mapFile != null) {
                initialBoard = BoardLoader.loadFromFile(mapFile) ;
            } else {
                initialBoard = BoardGenerator.defaultInstance().generateMap();
            }

            MyBoard board = new MyBoard(initialBoard);

            System.out.println("\nYour board:");
            board.display();
            System.out.println();

            EnemyBoard enemyBoard = new EnemyBoard();
            Game game = new Game(board, enemyBoard);

            if (mode.equals("server")) {
                game.startAsServer(port);
            } else {
                game.startAsClient(host, port);
            }

        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }


    private static Map<String, String> parseArguments(String[] args) {
        Map<String, String> params = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                String key = args[i].substring(1);

                if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    params.put(key, args[i + 1]);
                    i++;
                } else {
                    System.err.println("Error: No value for parameter " + args[i]);
                    System.exit(1);
                }
            }
        }

        return params;
    }

}