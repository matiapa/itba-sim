import cell.Cell;
import evolutionRules.EvolutionRule;
import evolutionRules.lifeGameRules.r2D.Rule1112;
import evolutionRules.lifeGameRules.r2D.Rule3323;
import evolutionRules.lifeGameRules.r2D.Rule3623;
import evolutionRules.lifeGameRules.r3D.Rule2645;
import evolutionRules.lifeGameRules.r3D.Rule5556;
import evolutionRules.lifeGameRules.r3D.Rule6657;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Statistics {


    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        stats2D();
//        stats3D();
    }

    public static void stats3D() throws FileNotFoundException, UnsupportedEncodingException {
        int L = 20;
        double p = 0.1;
        int maxIterations = 100;
        int samples = 10;

        // Perform {samples} amount of simulations with each studied rule

        EvolutionRule[] rules = new EvolutionRule[]{new Rule2645(), new Rule5556(), new Rule6657()};
        int[][][] aliveCells = new int[rules.length][maxIterations][samples];
        double[][][] maxRadius = new double[rules.length][maxIterations][samples];

        for(int s=0; s<samples; s++) {
            Cell[][][] grid3D = Main.randomGrid3D(L, p);

            for (int r=0; r<rules.length; r++) {
                List<Cell[][][]> grids = Automata.run(grid3D, rules[r], maxIterations);

                for(int t=0; t<grids.size(); t++) {
                    Cell[][][] grid = grids.get(t);

                    for(int x=0; x<L; x++) {
                        for(int y=0; y<L; y++) {
                            for (int z = 0; z < L; z++) {
                                if (grid[x][y][z].isAlive()) {
                                    aliveCells[r][t][s] += 1;
                                    double radius = Math.pow(Math.pow(x - (double) L / 2, 2) + Math.pow(y - (double) L / 2, 2) + Math.pow(z - (double) L / 2, 2), 0.5);
                                    maxRadius[r][t][s] = Math.max(maxRadius[r][t][s], radius);
                                }
                            }
                        }
                    }

                }
            }
        }

        // Calculate averages and standard deviations for each rule and time

        double[][] avgAliveCells = new double[rules.length][maxIterations];
        double[][] avgMaxRadius = new double[rules.length][maxIterations];

        for (int r=0; r<rules.length; r++) {
            for(int t=0; t<maxIterations; t++) {
                int amount = samples;
                for(int s=0; s<samples; s++) {
                    if (aliveCells[r][t][s] == 0) {
                        amount--;
                        continue;
                    }
                    avgAliveCells[r][t] += aliveCells[r][t][s];
                    avgMaxRadius[r][t] += maxRadius[r][t][s];
                }
                avgAliveCells[r][t] /= amount;
                avgMaxRadius[r][t] /= amount;
            }
        }

        PrintWriter writer = new PrintWriter("stats_by_t.csv", "UTF-8");
        writer.println("rule,t,avgMaxRadius,stdMaxRadius,avgAliveCells,stdAliveCells");
        Locale.setDefault(Locale.US);

        for (int r=0; r<rules.length; r++) {
            for(int t=0; t<maxIterations; t++) {

                if (Double.isNaN(avgAliveCells[r][t])) {
                    break;
                }

                double stdAliveCells = 0, stdMaxRadius = 0;
                for(int s=0; s<samples; s++) {
                    stdAliveCells += Math.pow(aliveCells[r][t][s] - avgAliveCells[r][t], 2);
                    stdMaxRadius += Math.pow(maxRadius[r][t][s] - avgMaxRadius[r][t], 2);
                }
                stdAliveCells = Math.sqrt(stdAliveCells / (samples));
                stdMaxRadius = Math.sqrt(stdMaxRadius / (samples));

                writer.printf("%s,%d,%g,%g,%g,%g\n", rules[r], t, avgMaxRadius[r][t], stdMaxRadius, avgAliveCells[r][t], stdAliveCells);
            }
        }

        writer.close();
    }

    public static void stats2D() throws FileNotFoundException, UnsupportedEncodingException {

        int L = 100;
        double p = 0.01;
        int maxIterations = 100;
        int samples = 100;

        // Perform {samples} amount of simulations with each studied rule

        EvolutionRule[] rules = new EvolutionRule[]{new Rule1112(), new Rule3323(), new Rule3623()};
        int[][][] aliveCells = new int[rules.length][maxIterations][samples];
        double[][][] maxRadius = new double[rules.length][maxIterations][samples];

        for(int s=0; s<samples; s++) {
            Cell[][] grid2D = Main.randomGrid2D(L, p);

            for (int r=0; r<rules.length; r++) {
                List<Cell[][]> grids = Automata.run(grid2D, rules[r], maxIterations);

                for(int t=0; t<grids.size(); t++) {
                    Cell[][] grid = grids.get(t);

                    for(int x=0; x<L; x++) {
                        for(int y=0; y<L; y++) {
                            if(grid[x][y].isAlive()) {
                                aliveCells[r][t][s] += 1;
                                maxRadius[r][t][s] = Math.max(maxRadius[r][t][s], Math.hypot(x-(double) L/2, y-(double) L/2));
                            }
                        }
                    }

                }
            }
        }

        // Calculate averages and standard deviations for each rule and time

        double[][] avgAliveCells = new double[rules.length][maxIterations];
        double[][] avgMaxRadius = new double[rules.length][maxIterations];

        for (int r=0; r<rules.length; r++) {
            for(int t=0; t<maxIterations; t++) {
                int amount = samples;
                for(int s=0; s<samples; s++) {
                    if (aliveCells[r][t][s] == 0) {
                        amount--;
                        continue;
                    }
                    avgAliveCells[r][t] += aliveCells[r][t][s];
                    avgMaxRadius[r][t] += maxRadius[r][t][s];
                }
                avgAliveCells[r][t] /= amount;
                avgMaxRadius[r][t] /= amount;
            }
        }

        PrintWriter writer = new PrintWriter("stats_by_t.csv", "UTF-8");
        writer.println("rule,t,avgMaxRadius,stdMaxRadius,avgAliveCells,stdAliveCells");
        Locale.setDefault(Locale.US);

        for (int r=0; r<rules.length; r++) {
            for(int t=0; t<maxIterations; t++) {

                if (Double.isNaN(avgAliveCells[r][t])) break;

                double stdAliveCells = 0, stdMaxRadius = 0;
                for(int s=0; s<samples; s++) {
                    stdAliveCells += Math.pow(aliveCells[r][t][s] - avgAliveCells[r][t], 2);
                    stdMaxRadius += Math.pow(maxRadius[r][t][s] - avgMaxRadius[r][t], 2);
                }
                stdAliveCells = Math.sqrt(stdAliveCells / (samples));
                stdMaxRadius = Math.sqrt(stdMaxRadius / (samples));

                writer.printf("%s,%d,%g,%g,%g,%g\n", rules[r], t, avgMaxRadius[r][t], stdMaxRadius, avgAliveCells[r][t], stdAliveCells);
            }
        }

        writer.close();
    }

}
