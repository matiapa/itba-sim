package evolutionRules;

import cell.Cell;

public interface EvolutionRule {

    Cell evaluate(int t, int x, int y, Cell[][] grid);

    String ruleType();

}
