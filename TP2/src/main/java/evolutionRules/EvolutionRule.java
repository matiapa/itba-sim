package evolutionRules;

import state.State;

public interface EvolutionRule {

    void apply(int x, int y, State[][] grid);

    void apply(int x, int y, int z, State[][][] grid);

}
