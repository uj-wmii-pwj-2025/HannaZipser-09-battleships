package src.main.java.org.example.game;

import src.main.java.org.example.board.EnemyBoard;
import src.main.java.org.example.board.MyBoard;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Game {
    private EnemyBoard enemyBoard;
    private MyBoard myBoard;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;
    private boolean isMyTurn;
    private boolean isFirstMove;
    private String lastResponse;
    private String lastCoord;
    private String lastSentMessage;

    public Game(MyBoard myBoard, EnemyBoard enemyBoard) {
        this.myBoard = myBoard;
        this.scanner = new Scanner(System.in);
        this.lastResponse = null;
        this.lastCoord = null;
        this.enemyBoard = enemyBoard;
    }

    public void startAsServer(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Waiting for connection...");

        socket = serverSocket.accept();
        System.out.println("Connected with client: " + socket.getInetAddress());

        setupStreams();
        isMyTurn = false;
        isFirstMove = false;

        play();

        cleanup(serverSocket);
    }

    public void startAsClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        System.out.println("Connected with server");

        setupStreams();
        isMyTurn = true;
        isFirstMove = true;
        play();

        cleanup(null);
    }

    private void setupStreams() throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
    }

    private void play() {
        try {
            System.out.println("\n=== BATTLESHIP ===\n");

            while (true) {
                if (isMyTurn) {
                    System.out.println("============================");
                    enemyBoard.displayPartial();
                    System.out.println();
                    System.out.print("Your turn! Enter coordinates: ");

                    String coord = scanner.nextLine().trim().toUpperCase();

                    String command;
                    if (isFirstMove) {
                        command = "start";
                        isFirstMove = false;
                    } else {
                        command = lastResponse;
                    }

                    String message = command + ";" + coord;
                    lastCoord = coord;
                    sendMessage(message);

                    isMyTurn = false;

                } else {
                    String message = receiveMessage();
                    if (message == null) break;

                    String[] parts = message.split(";");


                    String myResult = parts[0];
                    if (!myResult.equals("start")) {
                        System.out.println("Your shot result: " + myResult);
                        enemyBoard.recordShot(lastCoord, myResult);
                    }


                    if (myResult.equals("ostatni zatopiony")) {
                        System.out.println("\n=== WIN ===");
                        enemyBoard.displayFull();
                        break;
                    }

                    if (parts.length < 2) {
                        System.err.println("Invalid message format!");
                        continue;
                    }

                    String coord = parts[1];
                    System.out.println("============================");
                    System.out.println("\nOpponent shot at: " + coord);

                    HitStatus status = myBoard.hit(coord);

                    myBoard.display();

                    String result = hitStatusToString(status);
                    System.out.println("Result: " + result);

                    lastResponse = result;

                    if (status == HitStatus.LAST_SUNK) {
                        sendMessage(result);
                        System.out.println("\n=== LOSS ===");
                        enemyBoard.displayPartial();
                        break;
                    }

                    isMyTurn = true;
                }
            }

        } catch (IOException e) {
            System.err.println("Communication error: " + e.getMessage());
        }
    }

    private String hitStatusToString(HitStatus status) {
        switch (status) {
            case MISS: return "pudło";
            case HIT: return "trafiony";
            case SUNK: return "trafiony zatopiony";
            case LAST_SUNK: return "ostatni zatopiony";
            default: return "pudło";
        }
    }

    private void sendMessage(String message) {
        out.println(message);
        lastSentMessage = message;
    }

    private String receiveMessage() throws IOException {
        int retries = 0;

        while (retries < 3) {
            try {
                String message = in.readLine();
                return message;

            } catch (java.net.SocketTimeoutException e) {
                retries++;
                System.err.println("Timeout: attempt " + retries + "/" + 3);

                if (retries < 3) {
                    if (lastSentMessage != null) {
                        out.println(lastSentMessage);
                    }
                } else {
                    System.err.println("Communication error");
                    System.exit(1);
                }
            }
        }

        return null;
    }

    private void cleanup(ServerSocket serverSocket) {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            if (serverSocket != null) serverSocket.close();
            scanner.close();
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}