package yinyang;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Masukkan ukuran puzzle (n x n): ");
        int size = scanner.nextInt();
        
        // Generate kondisi awal secara random
        System.out.println("\nMenghasilkan kondisi awal puzzle secara random...");
        Board initialBoard = RandomBoardGenerator.generate(size);
        
        System.out.println("\nKondisi awal puzzle (generated):");
        BoardPrinter.printBoard(initialBoard);
        
        // Solve puzzle
        System.out.println("\nMencari solusi...");
        long startTime = System.currentTimeMillis();
        
        GeneticSolver solver = new GeneticSolver(initialBoard);
        Board solution = solver.solve();
        
        long endTime = System.currentTimeMillis();
        System.out.println("\nSolusi ditemukan dalam " + (endTime - startTime) + "ms");
        
        // Tampilkan solusi
        System.out.println("\nSolusi:");
        BoardPrinter.printBoard(solution);
        
        // Validasi solusi
        boolean isValid = YinYangValidator.isValid(solution);
        System.out.println("\nSolusi " + (isValid ? "valid" : "tidak valid"));
        
        scanner.close();
    }
}