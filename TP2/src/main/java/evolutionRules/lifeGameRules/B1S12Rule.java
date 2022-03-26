package evolutionRules.lifeGameRules;

import cell.Cell;

public class B1S12Rule extends LifeGameRule {

    @Override
    protected boolean cellLives(int t, Cell cell, int aliveNeighbours) {
        if(cell.isAlive() && (aliveNeighbours < 1 || aliveNeighbours > 2)) {
            return false;
        } else if(!cell.isAlive() && aliveNeighbours == 1) {
            return true;
        }
        return cell.isAlive();
    }

}