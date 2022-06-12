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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Statistics {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
//        stats2D();
//        stats3D();
        stats();
    }

    public static void stats() throws FileNotFoundException, UnsupportedEncodingException {
        int L2D = 100;
        int L3D = 20;
        int maxIterations = 100;
        int samples = 1;

        EvolutionRule[] rules = new EvolutionRule[]{new Rule3323(), new Rule5556(), new Rule6657()};
        double[] proportions = new double[]{0.15, 0.3, 0.45, 0.6, 0.75, 0.9};

        PrintWriter writer = new PrintWriter("stats.csv", "UTF-8");
        writer.println("rule,p,s,t,x,y,z");
        Locale.setDefault(Locale.US);

        for (int s = 0; s < samples; s++) {
            for (double proportion : proportions) {
                Cell[][] grid2D = Main.randomGrid2D(L2D, proportion);
                Cell[][][] grid3D = Main.randomGrid3D(L3D, proportion);

                for (EvolutionRule rule : rules) {
                    if (Objects.equals(rule.ruleType(), "2D")) {
                        List<Cell[][]> grid = Automata.run(grid2D, rule, maxIterations);
                        write2D(writer, grid, rule, proportion, s);
                    } else {
                        List<Cell[][][]> grid = Automata.run(grid3D, rule, maxIterations);
                        System.out.println(grid.size());
                        write3D(writer, grid, rule, proportion, s);
                    }
                }
            }

//            if (s % 10 == 0)
//                System.out.println("Sample "+s);
        }

        writer.close();
    }

    private static void write2D(PrintWriter writer, List<Cell[][]> grid, EvolutionRule rule, double p, int s) {
        for (int t = 0; t < grid.size(); t++) {
            for (int i = 0; i < grid.get(t).length; i++) {
                for (int j = 0; j < grid.get(t)[i].length; j++) {
                    if (grid.get(t)[i][j].isAlive())
                        writer.printf("%s,%.2f,%d,%d,%d,%d,1\n", rule, p, s, t, i, j);
                }
            }
        }
    }

    private static void write3D(PrintWriter writer, List<Cell[][][]> grid, EvolutionRule rule, double p, int s) {
        for (int t = 0; t < grid.size(); t++) {
            for (int i = 0; i < grid.get(t).length; i++) {
                for (int j = 0; j < grid.get(t)[i].length; j++) {
                    for (int z = 0; z < grid.get(t)[i][j].length; z++) {
                        if (grid.get(t)[i][j][z].isAlive())
                            writer.printf("%s,%.2f,%d,%d,%d,%d,%d\n", rule, p, s, t, i, j, z);
                    }
                }
            }
        }
    }

    public static void stats3D() throws FileNotFoundException, UnsupportedEncodingException {
        int L = 100;
        int maxIterations = 100;
        int samples = 100;

        // Perform {samples} amount of simulations with each studied rule

        EvolutionRule[] rules = new EvolutionRule[]{new Rule2645(), new Rule5556(), new Rule6657()};
        double[] proportions = new double[]{0.15, 0.3, 0.45, 0.6, 0.75, 0.9};

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
        double[] proportions = new double[]{0.15, 0.3, 0.45, 0.6, 0.75, 0.9};

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
