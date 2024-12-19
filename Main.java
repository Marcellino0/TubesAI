import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Masukkan ukuran puzzle (n x n): ");
        int size = scanner.nextInt();
        scanner.nextLine(); // Membersihkan buffer scanner

        int[][] initialBoard = new int[size][size];

        System.out.println("\nMasukkan papan puzzle dalam bentuk array 2 dimensi (gunakan spasi untuk pemisah nilai, dan enter untuk baris baru):");


        for (int i = 0; i < size; i++) {
            String[] row = scanner.nextLine().split(" ");
            for (int j = 0; j < size; j++) {
                initialBoard[i][j] = Integer.parseInt(row[j]);
            }
        }

        PuzzleBoard puzzleBoard = new PuzzleBoard(initialBoard);
        System.out.println("\nPapan Awal:");
        puzzleBoard.printBoard();

        System.out.println("\nMencari solusi...");
        YinYangSolver solver = new YinYangSolver();
        int[][] solution = solver.solve(initialBoard);

        System.out.println("\nPapan Solusi:");
        PuzzleBoard solutionBoard = new PuzzleBoard(solution);
        solutionBoard.printBoard();
    }
}