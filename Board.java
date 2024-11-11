// Board.java
package yinyang;

public class Board {
    private int[][] grid;
    private final int size;
    
    public static final int WHITE = 1;
    public static final int BLACK = -1;
    public static final int EMPTY = 0;
    
    public Board(int size) {
        this.size = size;
        this.grid = new int[size][size];
    }
    
    public Board(int[][] grid) {
        this.size = grid.length;
        this.grid = new int[size][size];
        for(int i = 0; i < size; i++) {
            this.grid[i] = grid[i].clone();
        }
    }
    
    public Board clone() {
        return new Board(this.grid);
    }
    
    public int getSize() {
        return size;
    }
    
    public int[][] getGrid() {
        return grid;
    }
    
    public void setCell(int row, int col, int value) {
        grid[row][col] = value;
    }
    
    public int getCell(int row, int col) {
        return grid[row][col];
    }
    
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }
    
    public boolean wouldCreateBlock(int row, int col, int color) {
        // Cek 4 kemungkinan posisi blok 2x2
        
        // Blok kiri atas
        if(row > 0 && col > 0) {
            if(grid[row-1][col-1] == color && 
               grid[row-1][col] == color && 
               grid[row][col-1] == color) {
                return true;
            }
        }
        
        // Blok kanan atas
        if(row > 0 && col < size-1) {
            if(grid[row-1][col] == color && 
               grid[row-1][col+1] == color && 
               grid[row][col+1] == color) {
                return true;
            }
        }
        
        // Blok kiri bawah
        if(row < size-1 && col > 0) {
            if(grid[row][col-1] == color && 
               grid[row+1][col-1] == color && 
               grid[row+1][col] == color) {
                return true;
            }
        }
        
        // Blok kanan bawah
        if(row < size-1 && col < size-1) {
            if(grid[row][col+1] == color && 
               grid[row+1][col] == color && 
               grid[row+1][col+1] == color) {
                return true;
            }
        }
        
        return false;
    }
}