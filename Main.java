import java.util.*;

class YinYangSolver {
    private static final int WHITE = 1;
    private static final int BLACK = -1;
    private static final int EMPTY = 0;
    
    // Parameter genetik algoritma
    private static final int POPULATION_SIZE = 100;
    private static final int MAX_GENERATIONS = 1000;
    private static final double MUTATION_RATE = 0.1;
    private static final double CROSSOVER_RATE = 0.8;
    
    static class Individual {
        int[][] board;
        int fitness;
        
        Individual(int size) {
            board = new int[size][size];
        }
        
        Individual(int[][] board) {
            this.board = new int[board.length][board.length];
            for(int i = 0; i < board.length; i++) {
                this.board[i] = board[i].clone();
            }
        }
    }

    private static int[][] initialBoard;

    public static int[][] solve(int[][] board) {
        initialBoard = new int[board.length][board.length];

        for(int i = 0; i < board.length; i++) {
            initialBoard[i] = board[i].clone();
        }

        int size = board.length;
        List<Individual> population = initializePopulation(board);
        
        for(int generation = 0; generation < MAX_GENERATIONS; generation++) {
            evaluatePopulation(population);
            
            // Cek apakah sudah menemukan solusi
            Individual bestIndividual = getBestIndividual(population);
            if(bestIndividual.fitness == 0) {
                return bestIndividual.board;
            }
            
            // Buat populasi baru
            List<Individual> newPopulation = new ArrayList<>();
            
            while(newPopulation.size() < POPULATION_SIZE) {
                Individual parent1 = tournamentSelection(population);
                Individual parent2 = tournamentSelection(population);
                
                if(Math.random() < CROSSOVER_RATE) {
                    Individual[] children = crossover(parent1, parent2);
                    mutate(children[0]);
                    mutate(children[1]);
                    newPopulation.add(children[0]);
                    if(newPopulation.size() < POPULATION_SIZE) {
                        newPopulation.add(children[1]);
                    }
                } else {
                    newPopulation.add(parent1);
                    if(newPopulation.size() < POPULATION_SIZE) {
                        newPopulation.add(parent2);
                    }
                }
            }
            
            population = newPopulation;
        }
        
        return getBestIndividual(population).board;
    }
    
    private static List<Individual> initializePopulation(int[][] initialBoard) {
        List<Individual> population = new ArrayList<>();
        for(int i = 0; i < POPULATION_SIZE; i++) {
            Individual individual = new Individual(initialBoard.length);
            // Salin nilai-nilai yang sudah tetap
            for(int row = 0; row < initialBoard.length; row++) {
                for(int col = 0; col < initialBoard.length; col++) {
                    if(initialBoard[row][col] != EMPTY) {
                        individual.board[row][col] = initialBoard[row][col];
                    } else {
                        // Isi secara random untuk sel kosong
                        individual.board[row][col] = Math.random() < 0.5 ? WHITE : BLACK;
                    }
                }
            }
            population.add(individual);
        }
        return population;
    }
    
    private static void evaluatePopulation(List<Individual> population) {
        for(Individual individual : population) {
            individual.fitness = calculateFitness(individual.board);
        }
    }
    
    private static int calculateFitness(int[][] board) {
        int violations = 0;
        int size = board.length;
        
        // Cek aturan konektivitas
        violations += checkConnectivity(board, WHITE);
        violations += checkConnectivity(board, BLACK);
        
        // Cek aturan 2x2
        for(int i = 0; i < size-1; i++) {
            for(int j = 0; j < size-1; j++) {
                if(board[i][j] == board[i+1][j] && 
                   board[i][j] == board[i][j+1] && 
                   board[i][j] == board[i+1][j+1]) {
                    violations++;
                }
            }
        }
        
        return violations;
    }
    
    private static int checkConnectivity(int[][] board, int color) {
        int size = board.length;
        boolean[][] visited = new boolean[size][size];
        boolean foundFirst = false;
        int components = 0;
        
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(board[i][j] == color && !visited[i][j]) {
                    if(!foundFirst) {
                        foundFirst = true;
                    } else {
                        components++;
                    }
                    dfs(board, visited, i, j, color);
                }
            }
        }
        
        return components;
    }
    
    private static void dfs(int[][] board, boolean[][] visited, int row, int col, int color) {
        if(row < 0 || row >= board.length || col < 0 || col >= board.length ||
           visited[row][col] || board[row][col] != color) {
            return;
        }
        
        visited[row][col] = true;
        
        // Cek 4 arah
        dfs(board, visited, row+1, col, color);
        dfs(board, visited, row-1, col, color);
        dfs(board, visited, row, col+1, color);
        dfs(board, visited, row, col-1, color);
    }
    
    private static Individual tournamentSelection(List<Individual> population) {
        int tournamentSize = 5;
        Individual best = null;
        
        for(int i = 0; i < tournamentSize; i++) {
            Individual contestant = population.get((int)(Math.random() * population.size()));
            if(best == null || contestant.fitness < best.fitness) {
                best = contestant;
            }
        }
        
        return best;
    }
    
    private static Individual[] crossover(Individual parent1, Individual parent2) {
        int size = parent1.board.length;
        Individual child1 = new Individual(size);
        Individual child2 = new Individual(size);
        
        // Crossover point
        int point = (int)(Math.random() * (size * size));
        
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                // Salin nilai dari input awal jika ada
                if(initialBoard[i][j] != EMPTY) {
                    child1.board[i][j] = initialBoard[i][j];
                    child2.board[i][j] = initialBoard[i][j];
                } else {
                    // Hanya lakukan crossover pada sel yang kosong di input awal
                    int index = i * size + j;
                    if(index < point) {
                        child1.board[i][j] = parent1.board[i][j];
                        child2.board[i][j] = parent2.board[i][j];
                    } else {
                        child1.board[i][j] = parent2.board[i][j];
                        child2.board[i][j] = parent1.board[i][j];
                    }
                }
            }
        }
        
        return new Individual[]{child1, child2};
    }
    
    private static void mutate(Individual individual) {
        int size = individual.board.length;
        
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                // Hanya mutasi sel yang kosong di input awal
                if(initialBoard[i][j] == EMPTY && Math.random() < MUTATION_RATE) {
                    individual.board[i][j] *= -1;  // Flip antara WHITE dan BLACK
                }
            }
        }
    }
    
    private static Individual getBestIndividual(List<Individual> population) {
        return Collections.min(population, (a, b) -> Integer.compare(a.fitness, b.fitness));
    }
}

public class Main {
    private static final int EMPTY = 0;
    private static final int WHITE = 1;
    private static final int BLACK = -1;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Input ukuran puzzle
        System.out.print("Masukkan ukuran puzzle (n x n): ");
        int size = scanner.nextInt();
        
        int[][] initialBoard = new int[size][size];
        
        // Input initial board
        System.out.println("\nMasukkan kondisi awal puzzle:");
        System.out.println("Gunakan:");
        System.out.println("0 untuk sel kosong");
        System.out.println("1 untuk sel putih");
        System.out.println("-1 untuk sel hitam");
        
        System.out.println("\nMasukkan nilai untuk setiap sel:");
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                System.out.printf("Baris %d Kolom %d: ", i+1, j+1);
                initialBoard[i][j] = scanner.nextInt();
                while(initialBoard[i][j] != EMPTY && initialBoard[i][j] != WHITE && initialBoard[i][j] != BLACK) {
                    System.out.println("Nilai tidak valid! Gunakan 0, 1, atau -1");
                    System.out.printf("Baris %d Kolom %d: ", i+1, j+1);
                    initialBoard[i][j] = scanner.nextInt();
                }
            }
        }
        
        // Tampilkan initial board
        System.out.println("\nInitial Board:");
        printBoard(initialBoard);
        
        // Solve puzzle
        System.out.println("\nMencari solusi...");
        long startTime = System.currentTimeMillis();
        
        int[][] solution = YinYangSolver.solve(initialBoard);
        
        long endTime = System.currentTimeMillis();
        System.out.println("\nSolusi ditemukan dalam " + (endTime - startTime) + "ms");
        
        // Tampilkan solusi
        System.out.println("\nSolusi:");
        printBoard(solution);
        
        // Validasi solusi
        boolean isValid = validateSolution(solution);
        System.out.println("\nSolusi " + (isValid ? "valid" : "tidak valid"));
        
        scanner.close();
    }
    
    private static void printBoard(int[][] board) {
        // Header
        System.out.print("    ");
        for(int j = 0; j < board.length; j++) {
            System.out.printf("%2d ", j+1);
        }
        System.out.println("\n   " + String.join("", Collections.nCopies(board.length * 3 + 1, "-")));
        
        for(int i = 0; i < board.length; i++) {
            System.out.printf("%2d |", i+1);  // Row numbers
            for(int j = 0; j < board.length; j++) {
                String symbol;
                switch(board[i][j]) {
                    case WHITE: 
                        symbol = "W "; // White cell
                        System.out.print("\u001B[47m\u001B[30m"); // White background, black text
                        break;
                    case BLACK: 
                        symbol = "B "; // Black cell
                        System.out.print("\u001B[40m\u001B[37m"); // Black background, white text
                        break;
                    default: 
                        symbol = "· "; // Empty cell
                        System.out.print("\u001B[0m"); // Reset colors
                        break;
                }
                System.out.print(symbol);
                System.out.print("\u001B[0m"); // Reset colors after each cell
            }
            System.out.println("|");
        }
        
        // Footer
        System.out.println("   " + String.join("", Collections.nCopies(board.length * 3 + 1, "-")));
        System.out.println("Legend: W = White (1), B = Black (-1), · = Empty (0)");
    }
    
    private static boolean validateSolution(int[][] board) {
        // Cek sel kosong
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board.length; j++) {
                if(board[i][j] == EMPTY) {
                    System.out.println("Error: Ditemukan sel kosong pada posisi (" + (i+1) + "," + (j+1) + ")");
                    return false;
                }
            }
        }
        
        // Cek blok 2x2
        for(int i = 0; i < board.length-1; i++) {
            for(int j = 0; j < board.length-1; j++) {
                if(board[i][j] == board[i+1][j] && 
                   board[i][j] == board[i][j+1] && 
                   board[i][j] == board[i+1][j+1]) {
                    System.out.println("Error: Ditemukan blok 2x2 dengan warna sama pada posisi (" + (i+1) + "," + (j+1) + ")");
                    return false;
                }
            }
        }
        
        // Cek konektivitas
        if(!checkConnectivity(board, WHITE)) {
            System.out.println("Error: Sel putih tidak terhubung semua");
            return false;
        }
        if(!checkConnectivity(board, BLACK)) {
            System.out.println("Error: Sel hitam tidak terhubung semua");
            return false;
        }
        
        return true;
    }
    
    private static boolean checkConnectivity(int[][] board, int color) {
        int size = board.length;
        boolean[][] visited = new boolean[size][size];
        int startRow = -1, startCol = -1;
        
        // Cari sel pertama dengan warna yang dicari
        outerLoop:
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(board[i][j] == color) {
                    startRow = i;
                    startCol = j;
                    break outerLoop;
                }
            }
        }
        
        if(startRow == -1) return true; // Tidak ada sel dengan warna tersebut
        
        // DFS dari sel pertama
        dfs(board, visited, startRow, startCol, color);
        
        // Cek apakah semua sel dengan warna yang sama telah dikunjungi
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(board[i][j] == color && !visited[i][j]) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private static void dfs(int[][] board, boolean[][] visited, int row, int col, int color) {
        if(row < 0 || row >= board.length || col < 0 || col >= board.length ||
           visited[row][col] || board[row][col] != color) {
            return;
        }
        
        visited[row][col] = true;
        
        // Cek 4 arah
        dfs(board, visited, row+1, col, color);
        dfs(board, visited, row-1, col, color);
        dfs(board, visited, row, col+1, color);
        dfs(board, visited, row, col-1, color);
    }
}