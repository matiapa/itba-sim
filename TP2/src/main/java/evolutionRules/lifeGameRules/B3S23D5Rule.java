package evolutionRules.lifeGameRules;

import cell.Cell;

public class B3S23D5Rule extends LifeGameRule {

    @Override
    protected boolean cellLives(int t, Cell cell, int aliveNeighbours) {
        if(cell.isAlive() && (aliveNeighbours < 2 || aliveNeighbours > 3 || (t - cell.getBornIteration()) > 5)) {
            return false;
        } else if(!cell.isAlive() && aliveNeighbours == 3) {
            return true;
        }
        return cell.isAlive();
    }

}