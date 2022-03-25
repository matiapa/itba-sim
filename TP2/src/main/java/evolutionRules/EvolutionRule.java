package evolutionRules;

import state.Cell;

public interface EvolutionRule {

    void apply(int x, int y, Cell[][] grid);

    void apply(int x, int y, int z, Cell[][][] grid);

}
