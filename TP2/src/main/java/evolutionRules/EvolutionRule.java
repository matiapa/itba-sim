package evolutionRules;

import cell.Cell;

public interface EvolutionRule {

    boolean apply(int t, int x, int y, Cell[][] grid);

    boolean apply(int t, int x, int y, int z, Cell[][][] grid);

}
