import evolutionRules.EvolutionRule;
import evolutionRules.StandardRule;
import cell.Cell;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Main {

    static int MAX_ITER = 1;

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

//        int L = Integer.parseInt(args[0]);
//        Cell[][] grid = new Cell[L][L];

        int L = 4;
        Cell[][] grid = testGrid();

        EvolutionRule rule = new StandardRule();

        PrintWriter writer = new PrintWriter("output.csv", "UTF-8");

        boolean finalState = false;
        for(int t=0; !finalState && t<MAX_ITER; t++) {
            for(int x=0; x<L; x++) {
                for(int y=0; y<L; y++) {
                    Cell cell = grid[x][y];

                    finalState = rule.apply(t, x, y, grid);

                    writer.println(String.format("%d %d %d %s", t, x, y, cell));
                }
            }
        }

        writer.close();

    }
    
    private static Cell[][] testGrid() {
        return new Cell[][]{
            {new Cell(false), new Cell(false), new Cell(false), new Cell(false)},
            {new Cell(false), new Cell(true), new Cell(true), new Cell(false)},
            {new Cell(false), new Cell(true), new Cell(true), new Cell(false)},
            {new Cell(false), new Cell(false), new Cell(false), new Cell(false)}
        };
    }

}