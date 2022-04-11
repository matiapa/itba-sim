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
//        stats2D();
        stats3D();
    }

    public static void stats3D() throws FileNotFoundException, UnsupportedEncodingException {
        int L = 20;
        int maxIterations = 100;
        int samples = 100;

        // Perform {samples} amount of simulations with each studied rule

        EvolutionRule[] rules = new EvolutionRule[]{new Rule2645(), new Rule5556(), new Rule6657()};
        double[] proportions = new double[]{0.05, 0.10, 0.15, 0.20, 0.25, 0.30};

        int[][][][] aliveCells = new int[proportions.length][rules.length][maxIterations][samples];
        double[][][][] maxRadius = new double[proportions.length][rules.length][maxIterations][samples];

        for (int s = 0; s < samples; s++) {
            System.out.printf("Running sample: %d\n", s);
            for(int p=0; p < proportions.length; p++) {
                Cell[][][] grid3D = Main.randomGrid3D(L, proportions[p]);

                for (int r = 0; r < rules.length; r++) {
                    List<Cell[][][]> grids = Automata.run(grid3D, rules[r], maxIterations);

                    for (int t = 0; t < grids.size(); t++) {
                        Cell[][][] grid = grids.get(t);

                        for (int x = 0; x < L; x++) {
                            for (int y = 0; y < L; y++) {
                                for (int z = 0; z < L; z++) {
                                    if (grid[x][y][z].isAlive()) {
                                        aliveCells[p][r][t][s] += 1;
                                        double radius = Math.pow(Math.pow(x - (double) L / 2, 2) + Math.pow(y - (double) L / 2, 2) + Math.pow(z - (double) L / 2, 2), 0.5);
                                        maxRadius[p][r][t][s] = Math.max(maxRadius[p][r][t][s], radius);
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        // Calculate averages and standard deviations for each rule and time

        double[][][] avgAliveCells = new double[proportions.length][rules.length][maxIterations];
        double[][][] avgMaxRadius = new double[proportions.length][rules.length][maxIterations];

        for(int p=0; p < proportions.length; p++) {
            for (int r = 0; r < rules.length; r++) {
                for (int t = 0; t < maxIterations; t++) {
                    int amount = samples;
                    for (int s = 0; s < samples; s++) {
                        if (aliveCells[p][r][t][s] == 0) {
                            amount--;
                            continue;
                        }
                        avgAliveCells[p][r][t] += aliveCells[p][r][t][s];
                        avgMaxRadius[p][r][t] += maxRadius[p][r][t][s];
                    }
                    avgAliveCells[p][r][t] /= amount;
                    avgMaxRadius[p][r][t] /= amount;
                }
            }
        }

        PrintWriter writer = new PrintWriter("stats_by_t.csv", "UTF-8");
        writer.println("rule,p,t,avgMaxRadius,stdMaxRadius,avgAliveCells,stdAliveCells");
        Locale.setDefault(Locale.US);

        for(int p=0; p < proportions.length; p++) {
            for (int r = 0; r < rules.length; r++) {
                for (int t = 0; t < maxIterations; t++) {
                    if (Double.isNaN(avgAliveCells[p][r][t]))
                        break;

                    double stdAliveCells = 0, stdMaxRadius = 0;
                    for (int s = 0; s < samples; s++) {
                        stdAliveCells += Math.pow(aliveCells[p][r][t][s] - avgAliveCells[p][r][t], 2);
                        stdMaxRadius += Math.pow(maxRadius[p][r][t][s] - avgMaxRadius[p][r][t], 2);
                    }
                    stdAliveCells = Math.sqrt(stdAliveCells / (samples));
                    stdMaxRadius = Math.sqrt(stdMaxRadius / (samples));

                    writer.printf("%s,%g,%d,%g,%g,%g,%g\n", rules[r], proportions[p], t, avgMaxRadius[p][r][t], stdMaxRadius, avgAliveCells[p][r][t], stdAliveCells);
                }
            }
        }

        writer.close();
    }

    public static void stats2D() throws FileNotFoundException, UnsupportedEncodingException {

        int L = 100;
        int maxIterations = 100;
        int samples = 100;

        // Perform {samples} amount of simulations with each (rule, proportion)

        EvolutionRule[] rules = new EvolutionRule[]{new Rule1112(), new Rule3323(), new Rule3623()};
        double[] proportions = new double[]{0.05, 0.10, 0.15, 0.20, 0.25, 0.30};

        int[][][][] aliveCells = new int[proportions.length][rules.length][maxIterations][samples];
        double[][][][] maxRadius = new double[proportions.length][rules.length][maxIterations][samples];

        for(int s=0; s<samples; s++) {
            System.out.printf("Running sample: %d\n", s);
            for(int p=0; p<proportions.length; p++) {
                Cell[][] grid2D = Main.randomGrid2D(L, proportions[p]);

                for (int r=0; r<rules.length; r++) {
                    List<Cell[][]> grids = Automata.run(grid2D, rules[r], maxIterations);

                    for(int t=0; t<grids.size(); t++) {
                        Cell[][] grid = grids.get(t);

                        for(int x=0; x<L; x++) {
                            for(int y=0; y<L; y++) {
                                if(grid[x][y].isAlive()) {
                                    aliveCells[p][r][t][s] += 1;
                                    maxRadius[p][r][t][s] = Math.max(maxRadius[p][r][t][s], Math.hypot(x-(double) L/2, y-(double) L/2));
                                }
                            }
                        }

                    }
                }
            }
        }

        // Calculate averages and standard deviations for each rule and time

        double[][][] avgAliveCells = new double[proportions.length][rules.length][maxIterations];
        double[][][] avgMaxRadius = new double[proportions.length][rules.length][maxIterations];

        for(int p=0; p<proportions.length; p++) {
            for (int r = 0; r < rules.length; r++) {
                for (int t = 0; t < maxIterations; t++) {
                    int amount = samples;
                    for (int s = 0; s < samples; s++) {
                        if (aliveCells[p][r][t][s] == 0) {
                            amount--;
                            continue;
                        }
                        avgAliveCells[p][r][t] += aliveCells[p][r][t][s];
                        avgMaxRadius[p][r][t] += maxRadius[p][r][t][s];
                    }
                    avgAliveCells[p][r][t] /= amount;
                    avgMaxRadius[p][r][t] /= amount;
                }
            }
        }

        PrintWriter writer = new PrintWriter("stats_by_t.csv", "UTF-8");
        writer.println("rule,p,t,avgMaxRadius,stdMaxRadius,avgAliveCells,stdAliveCells");
        Locale.setDefault(Locale.US);

        for(int p = 0; p<proportions.length; p++) {
            for (int r = 0; r < rules.length; r++) {
                for (int t = 0; t < maxIterations; t++) {
                    if (Double.isNaN(avgAliveCells[p][r][t]))
                        break;

                    double stdAliveCells = 0, stdMaxRadius = 0;
                    for (int s = 0; s < samples; s++) {
                        stdAliveCells += Math.pow(aliveCells[p][r][t][s] - avgAliveCells[p][r][t], 2);
                        stdMaxRadius += Math.pow(maxRadius[p][r][t][s] - avgMaxRadius[p][r][t], 2);
                    }
                    stdAliveCells = Math.sqrt(stdAliveCells / (samples));
                    stdMaxRadius = Math.sqrt(stdMaxRadius / (samples));

                    writer.printf("%s,%g,%d,%g,%g,%g,%g\n", rules[r], proportions[p], t, avgMaxRadius[p][r][t], stdMaxRadius, avgAliveCells[p][r][t], stdAliveCells);
                }
            }
        }

        writer.close();
    }

}
