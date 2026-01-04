package src.main.java.org.example.board;

public class EnemyBoard {
    private char[][] board;
    private static final int SIZE = 10;

    private static final char WATER = '.';
    private static final char SHIP = '#';
    private  static final char UNKNOWN ='?';
    private static final int[][] STRAIGHT_DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };

        public EnemyBoard() {
            board = new char[SIZE][SIZE];
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    board[i][j] = UNKNOWN;
                }
            }
        }

        public void recordShot(String coordinate, String result) {
            int[] coords = parseCoordinate(coordinate);
            if (coords == null) return;

            int row = coords[0];
            int col = coords[1];

            if (result.equals("pudło")) {
                board[row][col] = WATER;
            } else if (result.equals("trafiony")) {
                board[row][col] = SHIP;
            } else if (result.equals("trafiony zatopiony")) {
                board[row][col] = SHIP;
                markAroundSunkShip(row, col);
            } else if (result.equals("ostatni zatopiony")) {
                board[row][col] = '#';
                markAroundSunkShip(row, col);
            }
        }


        private void markAroundSunkShip(int row, int col) {
            boolean[][] shipSegments = findShipSegments(row, col);

            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (shipSegments[i][j]) {
                        markAround(i, j);
                    }
                }
            }
        }

    private boolean[][] findShipSegments(int startRow, int startCol) {
        boolean[][] segments = new boolean[SIZE][SIZE];
        boolean[][] visited = new boolean[SIZE][SIZE];

        dfsShip(startRow, startCol, segments, visited);

        return segments;
    }


    private void dfsShip(int row, int col, boolean[][] segments, boolean[][] visited) {
        if (!isInBounds(row, col)) {
            return;
        }

        if (visited[row][col]) {
            return;
        }

        if (board[row][col] != '#') {
            return;
        }

        visited[row][col] = true;
        segments[row][col] = true;

        for (int[] dir : STRAIGHT_DIRECTIONS) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            dfsShip(newRow, newCol, segments, visited);
        }

    }


        private void markAround(int row, int col) {
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int r = row + dr;
                    int c = col + dc;
                    if (isInBounds(r, c) && board[r][c] == UNKNOWN) {
                        board[r][c] = WATER;
                    }
                }
            }
        }

        private boolean isInBounds(int row, int col) {
            return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
        }

        private int[] parseCoordinate(String coord) {
            if (coord == null || coord.length() < 2) return null;

            coord = coord.trim().toUpperCase();
            char rowChar = coord.charAt(0);
            String colStr = coord.substring(1);

            try {
                int row = rowChar - 'A';
                int col = Integer.parseInt(colStr) - 1;

                if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
                    return null;
                }

                return new int[]{row, col};
            } catch (NumberFormatException e) {
                return null;
            }
        }

        public void displayPartial() {
            System.out.println("Enemy board:");
            displayBoardArray(board);
        }


        public void displayFull() {
            System.out.println("Enemy board:");
            char[][] temp = new char[SIZE][SIZE];
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    temp[i][j] = board[i][j] == '?' ? '.' : board[i][j];
                }
            }
            displayBoardArray(temp);
        }


    private void displayBoardArray(char[][] arr) {
        int size = board.length;


        System.out.print("  ");
        for (int i = 0; i < size; i++) {
            System.out.print((i + 1) + " ");
        }
        System.out.println();


        for (int i = 0; i < size; i++) {
            System.out.print((char)('A' + i) + " ");
            for (int j = 0; j < size; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }
}
