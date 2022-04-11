package evolutionRules.lifeGameRules.r3D;

import cell.Cell;
import evolutionRules.lifeGameRules.LifeGameRule;

public class Rule6657 extends LifeGameRule {

    @Override
    protected boolean cellLives(int t, Cell cell, int aliveNeighbours) {
        if(cell.isAlive() && (aliveNeighbours < 5 || aliveNeighbours > 7)) {
            return false;
        } else if(!cell.isAlive() && aliveNeighbours == 6) {
            return true;
        }
        return cell.isAlive();
    }

    @Override
    public String toString() {
        return "Rule6657";
    }
}