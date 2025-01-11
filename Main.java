import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Pilih mode:");
        System.out.println("1. Solve puzzle baru");
        System.out.println("2. Load solusi dari seed");
        int mode = scanner.nextInt();
        scanner.nextLine(); // Membersihkan buffer

        if (mode == 1) {
            solvePuzzle(scanner);
        } else if (mode == 2) {
            loadSolution(scanner);
        }
    }

    private static void solvePuzzle(Scanner scanner) {
        System.out.print("Masukkan ukuran puzzle (n x n): ");
        int size = scanner.nextInt();
        scanner.nextLine();

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

    private static void loadSolution(Scanner scanner) {
        try {
            System.out.print("Masukkan seed: ");
            long seed = scanner.nextLong();
            
            File solutionFile = new File("solutions.txt");
            if (!solutionFile.exists()) {
                System.out.println("File solusi tidak ditemukan.");
                return;
            }

            Scanner fileScanner = new Scanner(solutionFile);
            boolean found = false;

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) continue;

                try {
                    // Split berdasarkan : untuk memisahkan seed dan data
                    String[] mainParts = line.split(":");
                    if (mainParts.length != 2) continue;

                    long fileSeed = Long.parseLong(mainParts[0]);
                    if (fileSeed == seed) {
                        // Split data menjadi size dan nilai board
                        String[] dataParts = mainParts[1].split(",");
                        if (dataParts.length < 1) continue;

                        int size = Integer.parseInt(dataParts[0]);
                        int[][] solution = new int[size][size];
                        
                        // Memastikan jumlah data sesuai dengan ukuran board
                        if (dataParts.length != (size * size) + 1) {
                            System.out.println("Format data tidak valid untuk seed: " + seed);
                            continue;
                        }

                        // Mengisi array solution
                        int dataIndex = 1; // Mulai dari index 1 karena index 0 adalah size
                        for (int i = 0; i < size; i++) {
                            for (int j = 0; j < size; j++) {
                                solution[i][j] = Integer.parseInt(dataParts[dataIndex++]);
                            }
                        }

                        System.out.println("\nSolusi ditemukan untuk seed: " + seed);
                        System.out.println("Ukuran papan: " + size + "x" + size);
                        System.out.println("\nPapan Solusi:");
                        PuzzleBoard solutionBoard = new PuzzleBoard(solution);
                        solutionBoard.printBoard();
                        found = true;
                        break;
                    }
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    System.out.println("Terdapat data yang tidak valid dalam file solusi: " + e.getMessage());
                    continue;
                }
            }
            
            if (!found) {
                System.out.println("Solusi tidak ditemukan untuk seed: " + seed);
            }
            
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File solusi tidak ditemukan.");
        } catch (Exception e) {
            System.out.println("Terjadi error: " + e.getMessage());
        }
    }
}