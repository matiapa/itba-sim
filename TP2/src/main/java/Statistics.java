import cell.Cell;
import evolutionRules.EvolutionRule;
import evolutionRules.lifeGameRules.r2D.Rule1112;
import evolutionRules.lifeGameRules.r2D.Rule3323;
import evolutionRules.lifeGameRules.r2D.Rule3623;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class Statistics {

    public static void main(String[] args) {

        int L = 100;
        double p = 0.01;
        int maxIterations = 1000;
        int reps = 100;

        Cell[][] grid2D = Main.randomGrid2D(L, p);

        List<EvolutionRule> rules = Arrays.asList(
                new Rule1112(),
                new Rule3323(),
                new Rule3623()
        );

        PrintWriter writer;
        try {
            writer = new PrintWriter("output.csv", "UTF-8");
            writer.println("rule,t,x,y,".concat(grid2D[0][0].stateHeader()));
        } catch (IOException e) {
            System.out.println("Couldn't create output file 'output.csv'");
            return;
        }

//        for (EvolutionRule rule : rules) {
//            System.out.println("Running rule: "+rule);
//            for (int i = 0; i < reps; i++)
//                Automata.run(Arrays.copyOf(grid2D,grid2D.length), rule, maxIterations, writer);
//        }

        for (int i = 0; i < reps; i++) {
            System.out.println(i);
            grid2D = Main.randomGrid2D(L, p);
            Automata.run(grid2D, rules.get(0), maxIterations, writer);
        }

        writer.close();
    }
}
