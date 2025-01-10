import java.util.*;

public class YinYangSolver {
    private static final int WHITE = 1;
    private static final int BLACK = -1;
    private static final int EMPTY = 0;
    
    private static int POPULATION_SIZE;
    private static int MAX_GENERATIONS;
    private static double MUTATION_RATE;
    private static double CROSSOVER_RATE;
    private static int TOURNAMENT_SIZE;
    private static int ELITISM_COUNT;
    private static final int RESTART_THRESHOLD = 200; // New constant for restart threshold
    private static final int MAX_TIME_SECONDS = 30000; // 5 minutes maximum runtime
    
    private static Random random = new Random();

    static class Individual implements Comparable<Individual> {
        int[][] board;
        int fitness;
        
        Individual(int size) {
            board = new int[size][size];
        }
        
        Individual(int[][] board) {
            this.board = new int[board.length][board.length];
            for (int i = 0; i < board.length; i++) {
                this.board[i] = board[i].clone();
            }
        }

        @Override
        public int compareTo(Individual other) {
            return Integer.compare(this.fitness, other.fitness);
        }
    }

    private static int[][] initialBoard;

    public static int[][] solve(int[][] board) {
        adjustParameters(board.length);
        initialBoard = new int[board.length][board.length];
        for (int i = 0; i < board.length; i++) {
            initialBoard[i] = board[i].clone();
        }

        Individual bestSolutionEver = null;
        long startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) / 1000 < MAX_TIME_SECONDS) {
            Individual solution = runGeneticAlgorithm(startTime);
            
            if (solution.fitness == 0) {
                return solution.board;
            }
            
            if (bestSolutionEver == null || solution.fitness < bestSolutionEver.fitness) {
                bestSolutionEver = new Individual(solution.board);
                bestSolutionEver.fitness = solution.fitness;
            }
        }

        System.out.println("Time limit reached. Returning best solution found.");
        return bestSolutionEver.board;
    }

    private static Individual runGeneticAlgorithm(long startTime) {
        List<Individual> population = initializePopulation();
        Individual bestSolution = null;
        int stagnantGenerations = 0;
        int currentBestFitness = Integer.MAX_VALUE;
        
        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            // Check time limit
            if ((System.currentTimeMillis() - startTime) / 1000 >= MAX_TIME_SECONDS) {
                break;
            }

            evaluatePopulation(population);
            Individual bestOfGeneration = Collections.min(population);

            // Update best solution
            if (bestSolution == null || bestOfGeneration.fitness < bestSolution.fitness) {
                bestSolution = new Individual(bestOfGeneration.board);
                bestSolution.fitness = bestOfGeneration.fitness;
                currentBestFitness = bestSolution.fitness;
                stagnantGenerations = 0;
            } else if (bestOfGeneration.fitness >= currentBestFitness) {
                stagnantGenerations++;
            }

            System.out.println("Generation: " + generation + ", Best Fitness: " + bestOfGeneration.fitness);

            if (bestOfGeneration.fitness == 0) {
                return bestOfGeneration;
            }

            // Handle stagnation
            if (stagnantGenerations >= RESTART_THRESHOLD) {
                System.out.println("Stagnation detected. Performing aggressive restart...");
                population = performAggressiveRestart(population, bestSolution);
                stagnantGenerations = 0;
                continue;
            }

            population = evolvePopulation(population);
        }

        return bestSolution;
    }

    private static List<Individual> performAggressiveRestart(List<Individual> population, Individual bestSolution) {
        List<Individual> newPopulation = new ArrayList<>();
        
        // Keep the best solution
        if (bestSolution != null) {
            newPopulation.add(new Individual(bestSolution.board));
        }
        
        // Add completely new random solutions
        while (newPopulation.size() < POPULATION_SIZE) {
            Individual newIndividual = generateRandomSolution();
            if (random.nextDouble() < 0.3) { // 30% chance to apply local search
                localSearch(newIndividual);
            }
            newPopulation.add(newIndividual);
        }
        
        return newPopulation;
    }

    private static Individual generateRandomSolution() {
        Individual individual = new Individual(initialBoard.length);
        for (int i = 0; i < initialBoard.length; i++) {
            for (int j = 0; j < initialBoard.length; j++) {
                if (initialBoard[i][j] != EMPTY) {
                    individual.board[i][j] = initialBoard[i][j];
                } else {
                    individual.board[i][j] = random.nextBoolean() ? WHITE : BLACK;
                }
            }
        }
        return individual;
    }

    private static void adjustParameters(int size) {
        POPULATION_SIZE = size * size * 50;
        MAX_GENERATIONS = 2000;
        MUTATION_RATE = 0.01;
        CROSSOVER_RATE = 0.53;
        TOURNAMENT_SIZE = Math.max(3, POPULATION_SIZE / 100);
        ELITISM_COUNT = POPULATION_SIZE / 20;
    }
    
    private static List<Individual> initializePopulation() {
        List<Individual> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            Individual individual = generateRandomSolution();
            if (i < POPULATION_SIZE / 10) { // Apply local search to 10% of initial population
                localSearch(individual);
            }
            population.add(individual);
        }
        return population;
    }
    
    private static void evaluatePopulation(List<Individual> population) {
        population.parallelStream().forEach(individual -> {
            individual.fitness = calculateFitness(individual.board);
        });
    }
    
    private static int calculateFitness(int[][] board) {
        int violations = 0;
        violations += checkConnectivity(board, WHITE);
        violations += checkConnectivity(board, BLACK);
        violations += check2x2Violations(board);
        return violations;
    }
    
    private static int checkConnectivity(int[][] board, int color) {
        int size = board.length;
        boolean[][] visited = new boolean[size][size];
        int components = 0;
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == color && !visited[i][j]) {
                    dfs(board, visited, i, j, color);
                    components++;
                }
            }
        }
        
        return components - 1;
    }
    
    private static void dfs(int[][] board, boolean[][] visited, int row, int col, int color) {
        if (row < 0 || row >= board.length || col < 0 || col >= board.length ||
            visited[row][col] || board[row][col] != color) {
            return;
        }
        
        visited[row][col] = true;
        
        dfs(board, visited, row + 1, col, color);
        dfs(board, visited, row - 1, col, color);
        dfs(board, visited, row, col + 1, color);
        dfs(board, visited, row, col - 1, color);
    }
    
    private static int check2x2Violations(int[][] board) {
        int violations = 0;
        for (int i = 0; i < board.length - 1; i++) {
            for (int j = 0; j < board.length - 1; j++) {
                if (board[i][j] == board[i + 1][j] &&
                    board[i][j] == board[i][j + 1] &&
                    board[i][j] == board[i + 1][j + 1]) {
                    violations++;
                }
            }
        }
        return violations;
    }
    
    private static List<Individual> evolvePopulation(List<Individual> population) {
        List<Individual> newPopulation = new ArrayList<>();
        
        // Elitism
        population.sort(null);
        for (int i = 0; i < ELITISM_COUNT; i++) {
            newPopulation.add(new Individual(population.get(i).board));
        }
        
        // Crossover and Mutation
        while (newPopulation.size() < POPULATION_SIZE) {
            Individual parent1 = tournamentSelection(population);
            Individual parent2 = tournamentSelection(population);
            
            if (random.nextDouble() < CROSSOVER_RATE) {
                Individual[] children = crossover(parent1, parent2);
                mutate(children[0]);
                mutate(children[1]);
                
                // Apply local search occasionally
                if (random.nextDouble() < 0.1) { // 10% chance
                    localSearch(children[0]);
                    localSearch(children[1]);
                }
                
                newPopulation.add(children[0]);
                if (newPopulation.size() < POPULATION_SIZE) {
                    newPopulation.add(children[1]);
                }
            } else {
                newPopulation.add(new Individual(parent1.board));
                if (newPopulation.size() < POPULATION_SIZE) {
                    newPopulation.add(new Individual(parent2.board));
                }
            }
        }
        
        return newPopulation;
    }
    
    private static Individual tournamentSelection(List<Individual> population) {
        Individual best = null;
        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            Individual contestant = population.get(random.nextInt(population.size()));
            if (best == null || contestant.fitness < best.fitness) {
                best = contestant;
            }
        }
        return best;
    }
    
    private static Individual[] crossover(Individual parent1, Individual parent2) {
        int size = parent1.board.length;
        Individual child1 = new Individual(size);
        Individual child2 = new Individual(size);
        
        // Two-point crossover
        int point1 = random.nextInt(size * size);
        int point2 = random.nextInt(size * size);
        if (point1 > point2) {
            int temp = point1;
            point1 = point2;
            point2 = temp;
        }
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int index = i * size + j;
                if (initialBoard[i][j] != EMPTY) {
                    child1.board[i][j] = initialBoard[i][j];
                    child2.board[i][j] = initialBoard[i][j];
                } else {
                    if (index < point1 || index >= point2) {
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
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (initialBoard[i][j] == EMPTY && random.nextDouble() < MUTATION_RATE) {
                    individual.board[i][j] *= -1;
                }
            }
        }
    }
    
    private static void localSearch(Individual individual) {
        int size = individual.board.length;
        boolean improved;
        
        do {
            improved = false;
            int currentFitness = calculateFitness(individual.board);
            
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (initialBoard[i][j] == EMPTY) {
                        individual.board[i][j] *= -1;  // Flip color
                        int newFitness = calculateFitness(individual.board);
                        
                        if (newFitness < currentFitness) {
                            currentFitness = newFitness;
                            improved = true;
                        } else {
                            individual.board[i][j] *= -1;  // Flip back
                        }
                    }
                }
            }
        } while (improved);
        
        individual.fitness = calculateFitness(individual.board);
    }
   
}

