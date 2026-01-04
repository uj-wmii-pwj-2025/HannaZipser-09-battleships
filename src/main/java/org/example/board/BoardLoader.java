package src.main.java.org.example.board;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoardLoader {
    private static final int EXPECTED_SIZE = 10;

    public static char[][] loadFromFile(String filepath) throws IOException {
        List<String> lines = new ArrayList<>();


        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        }

        if (lines.size() != EXPECTED_SIZE) {
            throw new IllegalArgumentException(
                    "Invalid map format: expected " + EXPECTED_SIZE + " rows, got " + lines.size()
            );
        }

        char[][] board = new char[EXPECTED_SIZE][EXPECTED_SIZE];

        for (int i = 0; i < EXPECTED_SIZE; i++) {
            String line = lines.get(i);


            if (line.length() != EXPECTED_SIZE) {
                throw new IllegalArgumentException(
                        "Invalid map format: row " + (i + 1) + " has " + line.length() +
                                " characters, expected " + EXPECTED_SIZE
                );
            }

            for (int j = 0; j < EXPECTED_SIZE; j++) {
                char c = line.charAt(j);

                if (c != '.' && c != '#') {
                    throw new IllegalArgumentException(
                            "Invalid character '" + c + "' at position (" + (i + 1) + "," + (j + 1) +
                                    "). Only '.' (water) and '#' (ship) are allowed."
                    );
                }

                board[i][j] = c;
            }
        }

        return board;
    }

}