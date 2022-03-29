package evolutionRules.lifeGameRules;

import cell.Cell;
import evolutionRules.EvolutionRule;

// B3/S23 -> Standard ruleset.
// B1/S12 -> Generates Sierpinski triangles.
// B36/S23 -> Has frequent replicators.

public abstract class LifeGameRule implements EvolutionRule {

    @Override
    public Cell evaluate(int t, int x, int y, Cell[][] grid) {
        int aliveNeighbours = 0;
        for(int i = x - 1; i <= x + 1; i++) {
            for(int j = y - 1; j <= y + 1; j++) {
                if(i < 0 || i >= grid.length || j < 0 || j >= grid.length)
                    continue;
                aliveNeighbours += grid[i][j].isAlive() && (i != x || j != y)  ? 1 : 0;
            }
        }

        return getNewState(t, grid[x][y], aliveNeighbours);
    }

    @Override
    public Cell evaluate(int t, int x, int y, int z, Cell[][][] grid) {
        int aliveNeighbours = 0;
        for(int i = x - 1; i <= x + 1; i++) {
            for(int j = y - 1; j <= y + 1; j++) {
                for(int k = z - 1; k <= z + 1; k++) {
                    if (i < 0 || i >= grid.length || j < 0 || j >= grid.length || k < 0 || k >= grid.length)
                        continue;
                    aliveNeighbours += grid[i][j][k].isAlive() && (i != x || j != y || k != z) ? 1 : 0;
                }
            }
        }

        return getNewState(t, grid[x][y][z], aliveNeighbours);
    }

    private Cell getNewState(int t, Cell cell, int aliveNeighbours) {
        boolean cellLives = cellLives(t, cell, aliveNeighbours);
        Cell newCell = new Cell(cellLives);

        if(!cell.isAlive() && newCell.isAlive())
            newCell.setBornIteration(t);

        return newCell;
    }

    private boolean isFinalState(int x, int y, Cell[][] grid) {
        return grid[x][y].isAlive() && (x == 0 || y == 0 || x == grid.length-1 || y == grid.length-1);
    }

    private boolean isFinalState(int x, int y, int z, Cell[][][] grid) {
        return grid[x][y][z].isAlive() && (x == 0 || y == 0 || z == 0 || x == grid.length-1
            || y == grid.length-1 || z == grid.length-1);
    }

    protected abstract boolean cellLives(int t, Cell cell, int aliveNeighbours);

}
