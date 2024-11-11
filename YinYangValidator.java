// YinYangValidator.java
package yinyang;

public class YinYangValidator {
    public static boolean isValid(Board board) {
        return !hasEmptyCells(board) && 
               !has2x2Blocks(board) && 
               hasConnectedColors(board);
    }
    
    private static boolean hasEmptyCells(Board board) {
        int size = board.getSize();
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(board.getCell(i, j) == Board.EMPTY) {
                    System.out.println("Error: Ditemukan sel kosong pada posisi (" + (i+1) + "," + (j+1) + ")");
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean has2x2Blocks(Board board) {
        int size = board.getSize();
        for(int i = 0; i < size-1; i++) {
            for(int j = 0; j < size-1; j++) {
                int current = board.getCell(i, j);
                if(current == board.getCell(i+1, j) && 
                   current == board.getCell(i, j+1) && 
                   current == board.getCell(i+1, j+1)) {
                    System.out.println("Error: Ditemukan blok 2x2 dengan warna sama pada posisi (" + (i+1) + "," + (j+1) + ")");
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean hasConnectedColors(Board board) {
        return checkConnectivity(board, Board.WHITE) && 
               checkConnectivity(board, Board.BLACK);
    }
    
    private static boolean checkConnectivity(Board board, int color) {
        int size = board.getSize();
        boolean[][] visited = new boolean[size][size];
        int[] start = findFirstCell(board, color);
        
        if(start == null) return true; // Tidak ada sel dengan warna tersebut
        
        dfs(board, visited, start[0], start[1], color);
        
        // Cek apakah semua sel dengan warna yang sama telah dikunjungi
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(board.getCell(i, j) == color && !visited[i][j]) {
                    System.out.println("Error: Sel " + (color == Board.WHITE ? "putih" : "hitam") + " tidak terhubung semua");
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private static int[] findFirstCell(Board board, int color) {
        int size = board.getSize();
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(board.getCell(i, j) == color) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
    
    private static void dfs(Board board, boolean[][] visited, int row, int col, int color) {
        if(!board.isValidPosition(row, col) || 
           visited[row][col] || 
           board.getCell(row, col) != color) {
            return;
        }
        
        visited[row][col] = true;
        
        // Cek 4 arah
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        for(int[] dir : directions) {
            dfs(board, visited, row + dir[0], col + dir[1], color);
        }
    }
}