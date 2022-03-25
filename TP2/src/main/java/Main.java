import evolutionRules.EvolutionRule;
import evolutionRules.StandardRule;
import state.State;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Main {

    static int MAX_ITER = 1;

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

//        int L = Integer.parseInt(args[0]);
//        State[][] grid = new State[L][L];

        int L = 4;
        State[][] grid = {
            {new State(false), new State(false), new State(false), new State(false)},
            {new State(false), new State(true), new State(true), new State(false)},
            {new State(false), new State(true), new State(true), new State(false)},
            {new State(false), new State(false), new State(false), new State(false)}
        };

        EvolutionRule rule = new StandardRule();

        PrintWriter writer = new PrintWriter("output.csv", "UTF-8");
        writer.println("t x y alive bornIteration");

        boolean finished = false;
        for(int t=0; !finished && t<MAX_ITER; t++) {
            for(int i=0; i<L; i++) {
                for(int j=0; j<L; j++) {
                    State cellState = grid[i][j];

                    boolean wasAlive = cellState.isAlive();
                    rule.apply(i, j, grid);
                    boolean isAlive = cellState.isAlive();

                    if(!wasAlive && isAlive)
                        cellState.setBornIteration(t);

                    finished = isAlive && (i == 0 || j == 0 || i == L-1 || j == L-1);

                    writer.println(String.format("%d %d %d %d %d", t, i, j, isAlive ? 1 : 0, cellState.getBornIteration()));
                }
            }
        }

        writer.close();

    }

    // t x y alive age
    // 0 0 0 1 50
    // ...
    // 0 L L 1 50
    // ...
    // T 0 0 1 50
    // ...
    // T L L 1 50

}