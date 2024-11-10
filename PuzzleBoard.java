class PuzzleBoard {
    public static final int WHITE = 1;
    public static final int BLACK = -1;
    public static final int EMPTY = 0;

    private int[][] board;

    public PuzzleBoard(int[][] initialBoard) {
        this.board = new int[initialBoard.length][initialBoard.length];
        for (int i = 0; i < initialBoard.length; i++) {
            this.board[i] = initialBoard[i].clone();
        }
    }

    public int[][] getBoard() {
        return board;
    }

    public void printBoard() {
        for (int[] row : board) {
            for (int cell : row) {
                String symbol = switch (cell) {
                    case WHITE -> "W ";
                    case BLACK -> "B ";
                    default -> "· ";
                };
                System.out.print(symbol);
            }
            System.out.println();
        }
    }
}