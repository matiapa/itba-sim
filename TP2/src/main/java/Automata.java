import cell.Cell;
import evolutionRules.EvolutionRule;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Automata {

    public static List<Cell[][]> run(Cell[][] grid, EvolutionRule rule, int maxT) {
        int L = grid.length;
        List<Cell[][]> grids = new ArrayList<>();

        Cell[][] newGrid;
        for(int t=0; t<=maxT; t++) {
            newGrid = new Cell[L][L];

            for(int x=0; x<L; x++) {
                for(int y=0; y<L; y++) {
                    newGrid[x][y] = rule.evaluate(t, x, y, grid);
                    if(newGrid[x][y] == null)
                        break;
                }
            }

            if(t != maxT) {
                grid = newGrid;
                grids.add(newGrid);
            }
        }

        return grids;
    }

    public static Cell[][][] run(Cell[][][] grid, EvolutionRule rule, int maxT) {
        int L = grid.length;

        PrintWriter writer;
        try {
            writer = new PrintWriter("output.csv", "UTF-8");
        } catch (IOException e) {
            System.out.println("Couldn't create output file 'output.csv'");
            return null;
        }
        writer.println("rule,t,x,y,z,".concat(grid[0][0][0].stateHeader()));

        Cell[][][] newGrid;
        for(int t=0; t<=maxT; t++) {
            newGrid = new Cell[L][L][L];

            for(int x=0; x<L; x++) {
                for(int y=0; y<L; y++) {
                    for(int z=0; z<L; z++) {
                        if(grid[x][y][z].isAlive())
                            writer.println(String.format("%s,%d,%d,%d,%d,%s", rule.toString(), t, x, y, z, grid[x][y][z].stateString()));
                        newGrid[x][y][z] = rule.evaluate(t, x, y, z, grid);
                    }
                }
            }

            if(t != maxT)
                grid = newGrid;
        }

        writer.close();
        return grid;
    }

}