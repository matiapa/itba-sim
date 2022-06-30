import cell.Cell;
import cell.CellState;
import evolutionRules.EvolutionRule;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

            for(int x=0; x<L; x++) {
                for(int y=0; y<L; y++) {
                    move(newGrid, x, y);
                }
            }

            if(t != (iterations-1)) {
                grid = newGrid;
                grids.add(newGrid);
            }
        }

        return grids;
    }

    private static void move(Cell[][] grid, int x, int y) {
        CellState state = grid[x][y].getCellState();
        if(state == CellState.EMPTY || state == CellState.QUARANTINED || state == CellState.DEAD)
            return;

        Map<Pair<Integer, Integer>, Integer> potentials = new HashMap<>();

        for(int x1=Math.max(x-1, 0); x1<Math.min(x+1, grid.length); x1++) {
            for(int y1=Math.max(y-1, 0); y1<Math.min(y+1, grid.length); y1++) {

                if(grid[x1][y1].getCellState() == CellState.EMPTY || (x1==x && y1==y)) {
                    Pair<Integer, Integer> pos = new Pair<>(x1,y1);
                    potentials.put(pos, 0);

                    for(int x2=Math.max(x1-1, 0); x2<Math.min(x1+1, grid.length); x2++)
                        for(int y2=Math.max(y1-1, 0); y2<Math.min(y1+1, grid.length); y2++)

                            if(grid[x2][y2].isInfected() && x2!=x && y2!=y)
                                potentials.put(pos, potentials.get(pos) + 1);
                }
            }
        }

        List<Pair<Pair<Integer, Integer>, Double>> cellWeights = potentials.entrySet().stream().map(e -> new Pair<>(e.getKey(), 1.0/(e.getValue()+1)))
                .collect(Collectors.toList());

        Pair<Integer, Integer> chosenPos = new EnumeratedDistribution<>(cellWeights).sample();

        Cell emptyCell = grid[chosenPos.getFirst()][chosenPos.getSecond()];
        grid[chosenPos.getFirst()][chosenPos.getSecond()] = grid[x][y];
        grid[x][y] = emptyCell;
    }

}