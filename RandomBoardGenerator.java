// RandomBoardGenerator.java
package yinyang;

import java.util.Random;

public class RandomBoardGenerator {
    private static final double FILL_PROBABILITY = 0.3;
    private static final Random random = new Random();
    
    public static Board generate(int size) {
        Board board = new Board(size);
        
        // Isi beberapa sel secara random
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(random.nextDouble() < FILL_PROBABILITY) {
                    if(!board.wouldCreateBlock(i, j, Board.WHITE)) {
                        board.setCell(i, j, random.nextDouble() < 0.5 ? Board.WHITE : Board.BLACK);
                    }
                }
            }
        }
        
        return board;
    }
}