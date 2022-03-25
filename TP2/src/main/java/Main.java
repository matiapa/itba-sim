import evolutionRules.EvolutionRule;
import evolutionRules.StandardRule;
import state.Cell;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Main {

    static int MAX_ITER = 1;

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

        int L = Integer.parseInt(args[0]);
        Cell[][] grid = new Cell[L][L];

        EvolutionRule rule = new StandardRule();

        PrintWriter writer = new PrintWriter("output.csv", "UTF-8");
        writer.println("t x y alive bornIteration");

        boolean finished = false;
        for(int t=0; !finished && t<MAX_ITER; t++) {
            for(int i=0; i<L; i++) {
                for(int j=0; j<L; j++) {
                    Cell cell = grid[i][j];

                    boolean wasAlive = cell.isAlive();
                    rule.apply(i, j, grid);
                    boolean isAlive = cell.isAlive();

                    if(!wasAlive && isAlive)
                        cell.setBornIteration(t);

                    finished = isAlive && (i == 0 || j == 0 || i == L-1 || j == L-1);

                    writer.println(String.format("%d %d %d %d %d", t, i, j, isAlive ? 1 : 0, cell.getBornIteration()));
                }
            }
        }

        writer.close();

    }

}