// BoardPrinter.java
package yinyang;

import java.util.Collections;

public class BoardPrinter {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_WHITE_BG = "\u001B[47m";
    private static final String ANSI_BLACK_BG = "\u001B[40m";
    private static final String ANSI_BLACK_TEXT = "\u001B[30m";
    private static final String ANSI_WHITE_TEXT = "\u001B[37m";
    
    public static void printBoard(Board board) {
        int size = board.getSize();
        
        // Header
        System.out.print("    ");
        for(int j = 0; j < size; j++) {
            System.out.printf("%2d ", j+1);
        }
        System.out.println("\n   " + String.join("", Collections.nCopies(size * 3 + 1, "-")));
        
        // Board content
        for(int i = 0; i < size; i++) {
            System.out.printf("%2d |", i+1);  // Row numbers
            for(int j = 0; j < size; j++) {
                String symbol;
                switch(board.getCell(i, j)) {
                    case Board.WHITE:
                        symbol = "W ";
                        System.out.print(ANSI_WHITE_BG + ANSI_BLACK_TEXT);
                        break;
                    case Board.BLACK:
                        symbol = "B ";
                        System.out.print(ANSI_BLACK_BG + ANSI_WHITE_TEXT);
                        break;
                    default:
                        symbol = "· ";
                        System.out.print(ANSI_RESET);
                        break;
                }
                System.out.print(symbol);
                System.out.print(ANSI_RESET);
            }
            System.out.println("|");
        }
        
        // Footer
        System.out.println("   " + String.join("", Collections.nCopies(size * 3 + 1, "-")));
        System.out.println("Legend: W = White (1), B = Black (-1), · = Empty (0)");
    }
}