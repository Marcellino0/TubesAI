// Individual.java
package yinyang;

public class Individual {
    private Board board;
    private int fitness;
    
    public Individual(int size) {
        this.board = new Board(size);
    }
    
    public Individual(Board board) {
        this.board = board.clone();
    }
    
    public Board getBoard() {
        return board;
    }
    
    public void setBoard(Board board) {
        this.board = board;
    }
    
    public int getFitness() {
        return fitness;
    }
    
    public void setFitness(int fitness) {
        this.fitness = fitness;
    }
}