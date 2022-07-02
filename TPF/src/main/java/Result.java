import cell.Cell;

import java.util.List;

public class Result {

    List<Cell[][]> grid;
    List<Integer> newInfectedPerIteration;
    List<Integer> infectedAmountPerIteration;

    public Result(List<Cell[][]> grid, List<Integer> newInfectedPerIteration, List<Integer> infectedAmountPerIteration) {
        this.grid = grid;
        this.newInfectedPerIteration = newInfectedPerIteration;
        this.infectedAmountPerIteration = infectedAmountPerIteration;
    }

    public List<Cell[][]> getGrid() {
        return grid;
    }

    public void setGrid(List<Cell[][]> grid) {
        this.grid = grid;
    }

    public List<Integer> getNewInfectedPerIteration() {
        return newInfectedPerIteration;
    }

    public void setNewInfectedPerIteration(List<Integer> newInfectedPerIteration) {
        this.newInfectedPerIteration = newInfectedPerIteration;
    }

    public List<Integer> getInfectedAmountPerIteration() {
        return infectedAmountPerIteration;
    }

    public void setInfectedAmountPerIteration(List<Integer> infectedAmountPerIteration) {
        this.infectedAmountPerIteration = infectedAmountPerIteration;
    }
}
