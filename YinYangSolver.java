import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class YinYangSolver {
    private static final int WHITE = 1;
    private static final int BLACK = -1;
    private static final int EMPTY = 0;
    
    // Parameter genetik algoritma
    private static final int POPULATION_SIZE = 1000;
    private static final int MAX_GENERATIONS = 100000;
    private static final double MUTATION_RATE = 0.1;
    private static final double CROSSOVER_RATE = 0.8;
    private static final int STAGNATION_LIMIT = 1000;
    
    private static int stagnationCounter = 0;
    private static int lastBestFitness = Integer.MAX_VALUE;

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
            
            Individual bestIndividual = getBestIndividual(population);
            System.out.println("Generasi: " + generation + ", Fitness terbaik: " + bestIndividual.fitness);
            
            if(bestIndividual.fitness == 0) {
                System.out.println("Solusi ditemukan pada generasi: " + generation);
                return bestIndividual.board;
            }
            
            // Cek stagnasi fitness
            if(bestIndividual.fitness == lastBestFitness) {
                stagnationCounter++;
                if(stagnationCounter >= STAGNATION_LIMIT) {
                    System.out.println("Stagnasi terjadi, menghentikan program.");
                    return bestIndividual.board;
                }
            } else {
                stagnationCounter = 0; // reset stagnasi jika ada perbaikan
                lastBestFitness = bestIndividual.fitness;
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
        
        System.out.println("Mencapai generasi maksimum tanpa menemukan solusi sempurna.");
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
                if(initialBoard[i][j] == EMPTY && Math.random() < MUTATION_RATE) {
                    individual.board[i][j] *= -1; // Flip antara WHITE dan BLACK
                }
            }
        }
    }
    
    private static Individual getBestIndividual(List<Individual> population) {
        return Collections.min(population, (a, b) -> Integer.compare(a.fitness, b.fitness));
    }
}