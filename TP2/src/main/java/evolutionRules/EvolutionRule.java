package evolutionRules;

import cell.Cell;

public interface EvolutionRule {

    Cell evaluate(int t, int x, int y, Cell[][] grid);

    Cell evaluate(int t, int x, int y, int z, Cell[][][] grid);

}
