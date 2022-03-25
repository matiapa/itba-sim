package evolutionRules;

import state.Cell;

public class StandardRule implements EvolutionRule {

    @Override
    public void apply(int x, int y, Cell[][] grid) {
        int aliveNeighbours = 0;
        for(int i = x - 1; i <= x + 1; i++) {
            for(int j = y - 1; j <= y + 1; j++) {
                if(i < 0 || i >= grid.length || j < 0 || j >= grid.length)
                    continue;
                aliveNeighbours += grid[i][j].isAlive() && (i != x || j != y)  ? 1 : 0;
            }
        }
        
        changeState(grid[x][y], aliveNeighbours);
    }

    @Override
    public void apply(int x, int y, int z, Cell[][][] grid) {
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

        changeState(grid[x][y][z], aliveNeighbours);
    }
    
    private void changeState(Cell cell, int aliveNeighbours) {
        if(cell.isAlive() && (aliveNeighbours < 2 || aliveNeighbours > 3)) {
            cell.setAlive(false);
        } else if(!cell.isAlive() && aliveNeighbours == 3) {
            cell.setAlive(true);
        }
    }

}