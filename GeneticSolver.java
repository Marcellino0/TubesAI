package yinyang;

import java.util.*;

public class GeneticSolver {
    private static final int POPULATION_SIZE = 200;
    private static final int MAX_GENERATIONS = 10000;
    private static final double MUTATION_RATE = 0.5;
    private static final double CROSSOVER_RATE = 0.5;
    
    private final Board initialBoard;
    private List<Individual> population;
    
    public GeneticSolver(Board initialBoard) {
        this.initialBoard = initialBoard;
        this.population = initializePopulation();
    }
    
    public Board solve() {
        for(int generation = 0; generation < MAX_GENERATIONS; generation++) {
            evaluatePopulation();
            
            Individual bestIndividual = getBestIndividual();
            if(bestIndividual.getFitness() == 0) {
                return bestIndividual.getBoard();
            }
            
            List<Individual> newPopulation = new ArrayList<>();
            
            while(newPopulation.size() < POPULATION_SIZE) {
                Individual parent1 = tournamentSelection();
                Individual parent2 = tournamentSelection();
                
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
        
        return getBestIndividual().getBoard();
    }
    
    private List<Individual> initializePopulation() {
        List<Individual> population = new ArrayList<>();
        int size = initialBoard.getSize();
        
        for(int i = 0; i < POPULATION_SIZE; i++) {
            Individual individual = new Individual(size);
            Board board = individual.getBoard();
            
            for(int row = 0; row < size; row++) {
                for(int col = 0; col < size; col++) {
                    int initialValue = initialBoard.getCell(row, col);
                    if(initialValue != Board.EMPTY) {
                        board.setCell(row, col, initialValue);
                    } else {
                        board.setCell(row, col, Math.random() < 0.5 ? Board.WHITE : Board.BLACK);
                    }
                }
            }
            population.add(individual);
        }
        
        return population;
    }
    
    private void evaluatePopulation() {
        for(Individual individual : population) {
            individual.setFitness(calculateFitness(individual.getBoard()));
        }
    }
    
    private int calculateFitness(Board board) {
        int violations = 0;
        
        // Cek konektivitas
        if(!checkConnectivity(board, Board.WHITE)) violations++;
        if(!checkConnectivity(board, Board.BLACK)) violations++;
        
        // Cek aturan 2x2
        int size = board.getSize();
        for(int i = 0; i < size-1; i++) {
            for(int j = 0; j < size-1; j++) {
                if(board.getCell(i, j) == board.getCell(i+1, j) && 
                   board.getCell(i, j) == board.getCell(i, j+1) && 
                   board.getCell(i, j) == board.getCell(i+1, j+1)) {
                    violations++;
                }
            }
        }
        
        return violations;
    }
    
    private boolean checkConnectivity(Board board, int color) {
        int size = board.getSize();
        boolean[][] visited = new boolean[size][size];
        boolean foundFirst = false;
        int components = 0;
        
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(board.getCell(i, j) == color && !visited[i][j]) {
                    if(!foundFirst) {
                        foundFirst = true;
                    } else {
                        components++;
                    }
                    dfs(board, visited, i, j, color);
                }
            }
        }
        
        return components == 0;
    }
    
    private void dfs(Board board, boolean[][] visited, int row, int col, int color) {
        if(!board.isValidPosition(row, col) || 
           visited[row][col] || 
           board.getCell(row, col) != color) {
            return;
        }
        
        visited[row][col] = true;
        
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        for(int[] dir : directions) {
            dfs(board, visited, row + dir[0], col + dir[1], color);
        }
    }
    
    private Individual tournamentSelection() {
        int tournamentSize = 5;
        Individual best = null;
        
        for(int i = 0; i < tournamentSize; i++) {
            Individual contestant = population.get((int)(Math.random() * population.size()));
            if(best == null || contestant.getFitness() < best.getFitness()) {
                best = contestant;
            }
        }
        
        return best;
    }
    
    private Individual[] crossover(Individual parent1, Individual parent2) {
        int size = initialBoard.getSize();
        Individual child1 = new Individual(size);
        Individual child2 = new Individual(size);
        
        int point = (int)(Math.random() * (size * size));
        
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                int initialValue = initialBoard.getCell(i, j);
                if(initialValue != Board.EMPTY) {
                    child1.getBoard().setCell(i, j, initialValue);
                    child2.getBoard().setCell(i, j, initialValue);
                } else {
                    int index = i * size + j;
                    if(index < point) {
                        child1.getBoard().setCell(i, j, parent1.getBoard().getCell(i, j));
                        child2.getBoard().setCell(i, j, parent2.getBoard().getCell(i, j));
                    } else {
                        child1.getBoard().setCell(i, j, parent2.getBoard().getCell(i, j));
                        child2.getBoard().setCell(i, j, parent1.getBoard().getCell(i, j));
                    }
                }
            }
        }
        
        return new Individual[]{child1, child2};
    }
    private void mutate(Individual individual) {
        int size = initialBoard.getSize();
        
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(initialBoard.getCell(i, j) == Board.EMPTY && Math.random() < MUTATION_RATE) {
                    individual.getBoard().setCell(i, j, individual.getBoard().getCell(i, j) * -1);  // Flip antara WHITE dan BLACK
                }
            }
        }
    }
    
    private Individual getBestIndividual() {
        return Collections.min(population, Comparator.comparingInt(Individual::getFitness));
    }
}