package evolutionRules.lifeGameRules.r3D;

import cell.Cell;
import evolutionRules.lifeGameRules.LifeGameRule;

public class Rule2645 extends LifeGameRule {

    @Override
    protected boolean cellLives(int t, Cell cell, int aliveNeighbours) {
        if(cell.isAlive() && (aliveNeighbours < 4 || aliveNeighbours > 5)) {
            return false;
        } else if(!cell.isAlive() && aliveNeighbours >=2 && aliveNeighbours <=6) {
            return true;
        }
        return cell.isAlive();
    }

    @Override
    public String toString() {
        return "Rule2645";
    }
}