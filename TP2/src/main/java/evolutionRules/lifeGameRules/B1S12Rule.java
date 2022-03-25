package evolutionRules.lifeGameRules;

import cell.Cell;

public class B1S12Rule extends LifeGameRule {

    @Override
    protected void updateAliveProperty(int t, Cell cell, int aliveNeighbours) {
        if(cell.isAlive() && (aliveNeighbours < 1 || aliveNeighbours > 2)) {
            cell.setAlive(false);
        } else if(!cell.isAlive() && aliveNeighbours == 1) {
            cell.setAlive(true);
        }
    }

}