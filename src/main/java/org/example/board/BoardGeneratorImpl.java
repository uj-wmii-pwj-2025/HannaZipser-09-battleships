package src.main.java.org.example.board;


import src.main.java.org.example.board.BoardGenerator;
import src.main.java.org.example.exception.ShipPlacementException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BoardGeneratorImpl implements BoardGenerator {
    private static final int[] SHIP_SIZES = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
    private static final int MAX_BOARD_ATTEMPTS = 1000;
    private static final int MAX_SHIP_ATTEMPTS = 100;
    private static final char WATER = '.';
    private static final char SHIP = '#';
    private static final int SIZE = 10;
    private static final int[][] DIRECTIONS = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1},
            {-1, -1},
            {1, 1},
            {-1, 1},
            {1, -1}
    };
    private static final int[][] STRAIGHT_DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };
    private final Random randomGenerator;
    private char[][] board;

    public BoardGeneratorImpl(){
        randomGenerator = new Random();
    }
    @Override
    public char[][] generateMap() {
        for (int boardAttempt = 0; boardAttempt < MAX_BOARD_ATTEMPTS; boardAttempt++) {
            try {
                initializeBoard();
                placeAllShips();
                return board;

            } catch (ShipPlacementException e) {
                System.out.println("Board attempt " + boardAttempt + " failed");
            }
        }

        throw new RuntimeException("Failed to generate valid board after " + MAX_BOARD_ATTEMPTS + " attempts");
    }

    private void initializeBoard() {
        board = new char[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = WATER;
            }
        }
    }
    private void placeAllShips() throws ShipPlacementException {
        for (int shipSize : SHIP_SIZES){
            placeShip(shipSize);
        }
    }

    private int[][] shuffleDirections(int[][] dirs) {
        List<int[]> list = new ArrayList<>(Arrays.asList(dirs));
        Collections.shuffle(list, randomGenerator);
        return list.toArray(new int[0][]);
    }

    private void placeShip(int shipSize) throws ShipPlacementException {
        for (int attempt = 0; attempt < MAX_SHIP_ATTEMPTS; attempt++) {
            int row = randomGenerator.nextInt(SIZE);
            int col = randomGenerator.nextInt(SIZE);
            int[][] newPosition = new int [shipSize][2];
            boolean[][] visited = new boolean[SIZE][SIZE];
            if (tryPlaceShip(row, col, shipSize, newPosition, visited)) {
                for (int[] position : newPosition) {
                    board[position[0]][position[1]] = SHIP;
                }
                return;
            }
        }
        throw new ShipPlacementException("Could not place ship " + shipSize+ " after " + MAX_SHIP_ATTEMPTS + " attempts");
    }

    private boolean  tryPlaceShip(int row, int column, int shipSize, int[][] newPosition, boolean[][] visited){
        if (shipSize == 0){
            return true;
        }

        if (row < 0 || row >= SIZE || column < 0 || column >= SIZE || visited[row][column]) {
            return false;
        }

        if (!isAreaClear(row, column)) {
            return false;
        }

        visited[row][ column] = true;
        newPosition[shipSize - 1] = new int[]{row, column};

        for (int[] dir : shuffleDirections(STRAIGHT_DIRECTIONS)) {
            int newRow = row + dir[0];
            int newCol = column + dir[1];
            if (tryPlaceShip(newRow, newCol, shipSize - 1, newPosition, visited)){
                return true;
            }
        }

        visited[row][column] = false;
        return false;
    }

    private boolean isAreaClear(int row, int column) {
        for (int[] direction : shuffleDirections(DIRECTIONS)) {
            int newRow = row + direction[0];
            int newCol = column + direction[1];

            if (newRow < 0 || newRow >= SIZE || newCol < 0 || newCol >= SIZE) {
                continue;
            }

            if (board[newRow][newCol] == SHIP) {
                return false;
            }
        }

        return board[row][column] != SHIP;
    }
    private String boardToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                sb.append(board[i][j]);
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
        System.out.println();
        return sb.toString();
    }
}
