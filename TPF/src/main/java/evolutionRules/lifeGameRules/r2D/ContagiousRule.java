package evolutionRules.lifeGameRules.r2D;

import cell.Cell;
import cell.CellState;
import evolutionRules.lifeGameRules.LifeGameRule;

public class ContagiousRule extends LifeGameRule {

    @Override
    public String ruleType() {
        return "Contagious";
    }

    @Override
    protected CellState cellNewState(int t, Cell cell, int aliveNeighbours) {
        return cell.getCellState();
    }
}
