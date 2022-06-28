package evolutionRules.lifeGameRules;

import cell.Cell;
import cell.CellState;
import evolutionRules.EvolutionRule;

// B3/S23 -> Standard ruleset.
// B1/S12 -> Generates Sierpinski triangles.
// B36/S23 -> Has frequent replicators.

public abstract class LifeGameRule implements EvolutionRule {

    @Override
    public Cell evaluate(int t, int x, int y, Cell[][] grid) {

//        if (isFinalState(x, y, grid)) return null;

        int infectedNeighbours = 0;
        for(int i = x - 1; i <= x + 1; i++) {
            for(int j = y - 1; j <= y + 1; j++) {
                if(i < 0 || i >= grid.length || j < 0 || j >= grid.length)
                    continue;
                infectedNeighbours += grid[i][j].isInfected() && (i != x || j != y)  ? 1 : 0;
            }
        }

        return getNewState(t, grid[x][y], infectedNeighbours);
    }

    private Cell getNewState(int t, Cell cell, int infectedNeighbours) {
        CellState newState = cellNewState(t, cell, infectedNeighbours);
        Cell newCell = new Cell(newState, cell.getCautiousLevel());

        if (newState == CellState.REMOVED)
            newCell.setDeadIteration(t);

        return newCell;
    }

//    private boolean isFinalState(int x, int y, Cell[][] grid) {
//
//        return grid[x][y].isAlive() && (x == 0 || y == 0 || x == grid.length-1 || y == grid.length-1);
//    }

    protected abstract CellState cellNewState(int t, Cell cell, int aliveNeighbours);

}
