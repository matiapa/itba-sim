package evolutionRules;

import cell.Cell;

public class StandardRule implements EvolutionRule {

    @Override
    public boolean apply(int t, int x, int y, Cell[][] grid) {
        int aliveNeighbours = 0;
        for(int i = x - 1; i <= x + 1; i++) {
            for(int j = y - 1; j <= y + 1; j++) {
                if(i < 0 || i >= grid.length || j < 0 || j >= grid.length)
                    continue;
                aliveNeighbours += grid[i][j].isAlive() && (i != x || j != y)  ? 1 : 0;
            }
        }
        
        changeState(t, grid[x][y], aliveNeighbours);

        return grid[x][y].isAlive() && (x == 0 || y == 0 || x == grid.length-1 || y == grid.length-1);
    }

    @Override
    public boolean apply(int t, int x, int y, int z, Cell[][][] grid) {
        int aliveNeighbours = 0;
        for(int i = x - 1; i <= x + 1; i++) {
            for(int j = y - 1; j <= y + 1; j++) {
                for(int k = z - 1; k <= z + 1; k++) {
                    if (i < 0 || i >= grid.length || j < 0 || j >= grid.length || k < 0 || z >= grid.length)
                        continue;
                    aliveNeighbours += grid[i][j][k].isAlive() && i != x && j != y && k != z ? 1 : 0;
                }
            }
        }

        changeState(t, grid[x][y][z], aliveNeighbours);

        return grid[x][y][z].isAlive() && (x == 0 || y == 0 || z == 0 || x == grid.length-1
                || y == grid.length-1 || z == grid.length-1);
    }
    
    private void changeState(int t, Cell cell, int aliveNeighbours) {
        if(cell.isAlive() && (aliveNeighbours < 2 || aliveNeighbours > 3)) {
            cell.setAlive(false);
            cell.setBornIteration(null);
        } else if(!cell.isAlive() && aliveNeighbours == 3) {
            cell.setAlive(true);
            cell.setBornIteration(t);
        }
    }

}