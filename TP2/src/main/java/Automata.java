import cell.Cell;
import evolutionRules.EvolutionRule;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Automata {

    public static int run(Cell[][] grid, EvolutionRule rule, int maxIterations) {
        int L = grid.length;
        PrintWriter writer;

        try {
            writer = new PrintWriter("output.csv", "UTF-8");
        } catch (IOException e) {
            System.out.println("Couldn't create output file 'output.csv'");
            return 0;
        }

        writer.println("t x y ".concat(grid[0][0].stateHeader()));

        boolean finalState = false; int t=0;
        for(t=0; !finalState && t<maxIterations; t++) {
            for(int x=0; x<L; x++) {
                for(int y=0; y<L; y++) {
                    Cell cell = grid[x][y];
                    finalState = rule.apply(t, x, y, grid);
                    writer.println(String.format("%d %d %d %s", t, x, y, cell.stateString()));
                }
            }
        }

        writer.close();
        return t;
    }

    public static void run(Cell[][][] grid, EvolutionRule rule, int maxIterations) throws FileNotFoundException, UnsupportedEncodingException {
        int L = grid.length;
        PrintWriter writer = new PrintWriter("output.csv", "UTF-8");
        writer.println("t x y z ".concat(grid[0][0][0].stateHeader()));

        boolean finalState = false;
        for(int t=0; !finalState && t<maxIterations; t++) {
            for(int x=0; x<L; x++) {
                for(int y=0; y<L; y++) {
                    for(int z=0; z<L; z++) {
                        Cell cell = grid[x][y][z];
                        finalState = rule.apply(t, x, y, z, grid);
                        writer.println(String.format("%d %d %d %d %s", t, x, y, z, cell.stateString()));
                    }
                }
            }
        }

        writer.close();
    }

}