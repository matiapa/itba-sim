package evolutionRules.lifeGameRules.r2D;

import cell.Cell;
import evolutionRules.lifeGameRules.LifeGameRule;

public class Rule3623 extends LifeGameRule {

    @Override
    protected boolean cellLives(int t, Cell cell, int aliveNeighbours) {
        if(cell.isAlive() && (aliveNeighbours < 2 || aliveNeighbours > 3)) {
            return false;
        } else if(!cell.isAlive() && (aliveNeighbours == 3 || aliveNeighbours == 6)) {
            return true;
        }
        return cell.isAlive();
    }

    @Override
    public String toString() {
        return "Rule3623";
    }
}