package evolutionRules.lifeGameRules.r2D;

import cell.Cell;
import evolutionRules.lifeGameRules.LifeGameRule;

public class Rule1112 extends LifeGameRule {

    @Override
    protected boolean cellLives(int t, Cell cell, int aliveNeighbours) {
        if(cell.isAlive() && (aliveNeighbours < 1 || aliveNeighbours > 2)) {
            return false;
        } else if(!cell.isAlive() && aliveNeighbours == 1) {
            return true;
        }
        return cell.isAlive();
    }

    @Override
    public String toString() {
        return "Rule1112";
    }
}