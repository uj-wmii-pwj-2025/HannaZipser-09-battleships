package src.main.java.org.example.board;

public interface BoardGenerator {

        char[][] generateMap();

        static BoardGenerator defaultInstance() {
            return new BoardGeneratorImpl();
        }


}
