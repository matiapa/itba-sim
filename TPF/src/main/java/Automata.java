import cell.Cell;
import evolutionRules.EvolutionRule;

import java.util.ArrayList;
import java.util.List;

public class Automata {

    public static List<Cell[][]> run(Cell[][] grid, EvolutionRule rule, int iterations) {
        int L = grid.length;
        List<Cell[][]> grids = new ArrayList<>();
        grids.add(grid);

        Cell[][] newGrid;
        for(int t=0; t<iterations; t++) {
            newGrid = new Cell[L][L];

            for(int x=0; x<L; x++) {
                for(int y=0; y<L; y++) {
                    newGrid[x][y] = rule.evaluate(t, x, y, grid);
                    if(newGrid[x][y] == null)
                        return grids;
                }
            }

            if(t != (iterations-1)) {
                grid = newGrid;
                grids.add(newGrid);
            }
        }

        return grids;
    }

}