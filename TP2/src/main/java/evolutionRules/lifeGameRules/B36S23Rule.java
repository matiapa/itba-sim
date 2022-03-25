package evolutionRules.lifeGameRules;

import cell.Cell;

public class B36S23Rule extends LifeGameRule {

    @Override
    protected void updateAliveProperty(int t, Cell cell, int aliveNeighbours) {
        if(cell.isAlive() && (aliveNeighbours < 2 || aliveNeighbours > 3)) {
            cell.setAlive(false);
        } else if(!cell.isAlive() && (aliveNeighbours == 3 || aliveNeighbours == 6)) {
            cell.setAlive(true);
        }
    }

}