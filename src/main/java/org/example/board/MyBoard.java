package src.main.java.org.example.board;

import src.main.java.org.example.game.HitStatus;

public class MyBoard {
    char [][] board;
    private static final char SHIP = '#';
    private static final char MISS = '~';
    private static final char HIT = '@';
    private static final int[][] STRAIGHT_DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };

    public MyBoard(char[][] initialBoard){
        this.board = initialBoard;
    }

    public HitStatus hit(String coordinate) {
        int[] coords = parseCoordinate(coordinate);
        if (coords == null) {
            return HitStatus.MISS;
        }
        return hit(coords[0], coords[1]);
    }

    private int[] parseCoordinate(String coord) {
        if (coord == null || coord.length() < 2) {
            return null;
        }

        coord = coord.trim().toUpperCase();
        char rowChar = coord.charAt(0);
        String colStr = coord.substring(1);

        try {
            int row = rowChar - 'A';
            int col = Integer.parseInt(colStr) - 1;

            if (row < 0 || row >= board.length || col < 0 || col >= board[0].length) {
                return null;
            }

            return new int[]{row, col};
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public HitStatus hit (int row, int column){
        if (board[row][column] == SHIP){
            board[row][column] = HIT;
            if (isShipSunk(row, column)) {

                if (areAllShipsSunk()) {
                    return HitStatus.LAST_SUNK;
                }
                return HitStatus.SUNK;
            }
            return HitStatus.HIT;
        }
        board[row][column] = MISS;
        return HitStatus.MISS;
    }

    private boolean isShipSunk(int row, int column) {
        boolean[][] shipSegments = findAllShipSegments(row, column);

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (shipSegments[i][j] && board[i][j] == SHIP) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean[][] findAllShipSegments(int startRow, int startCol) {
        boolean[][] segments = new boolean[board.length][board[0].length];
        boolean[][] visited = new boolean[board.length][board[0].length];

        dfsShipSegments(startRow, startCol, segments, visited);

        return segments;
    }


    private void dfsShipSegments(int row, int col, boolean[][] segments, boolean[][] visited) {

        if (!isInBounds(row, col)) {
            return;
        }

        if (visited[row][col]) {
            return;
        }

        if (board[row][col] != SHIP && board[row][col] != HIT) {
            return;
        }

        visited[row][col] = true;
        segments[row][col] = true;

        for (int[] dir : STRAIGHT_DIRECTIONS) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            dfsShipSegments(newRow, newCol, segments, visited);
        }
    }

    private boolean areAllShipsSunk() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == SHIP) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < board.length &&
                col >= 0 && col < board[0].length;
    }


    public void display() {
        int size = board.length;


        System.out.print("  ");
        for (int i = 0; i < size; i++) {
            System.out.print((i + 1) + " ");
        }
        System.out.println();


        for (int i = 0; i < size; i++) {
            System.out.print((char)('A' + i) + " ");
            for (int j = 0; j < size; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

}
