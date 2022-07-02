import cell.Cell;
import cell.CellState;
import evolutionRules.EvolutionRule;
import evolutionRules.InfectionRule;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Statistics {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
//        obs1_pc();
//        obs1_barbijo();
//        obs2K();
//        obs2Pq();
//        obs2Pc();
//        obs3();
        obs4();
    }

    public static void obs1_pc() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer;
        writer = new PrintWriter("new_infected_pc.csv", "UTF-8");
        writer.println("pc,s,t,infected,newInfected");

        int samples = 50;
        float[] pc = new float[]{0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f};

        for (float v : pc) {
            System.out.println("Running for: "+v);
            EvolutionRule rule = new InfectionRule(v, 0.1f, 0.5f, 0.12f, 0.04f, 0.8f, 0.6f, 8, 2, 18, 18, 1);
            for (int s = 0; s < samples; s++) {
                Cell[][] initialGrid = Main.randomGrid(100, 0.1f, 0.05f, 0.5f, true);
                Result result = Automata.run(initialGrid, rule, 100);

                for (int i = 0; i < result.getNewInfectedPerIteration().size(); i++) {
                    writer.println(String.format("%g,%d,%d,%d,%d",v,s,i, result.getInfectedAmountPerIteration().get(i), result.getNewInfectedPerIteration().get(i)));
                }
            }
        }
        writer.close();
    }

    public static void obs1_barbijo() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer;
        writer = new PrintWriter("new_infected_barbijo.csv", "UTF-8");
        writer.println("pc,s,t,infected,newInfected");

        int samples = 50;
        float[] pc = new float[]{0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f};

        for (float v : pc) {
            System.out.println("Running for: "+v);
            EvolutionRule rule = new InfectionRule(0.5f, 0.1f, 0.5f, 0.12f, 0.04f, v, 0.6f, 8, 2, 18, 18, 1);
            for (int s = 0; s < samples; s++) {
                Cell[][] initialGrid = Main.randomGrid(100, 0.1f, 0.05f, 0.5f, true);
                Result result = Automata.run(initialGrid, rule, 100);

                for (int i = 0; i < result.getNewInfectedPerIteration().size(); i++) {
                    writer.println(String.format("%g,%d,%d,%d,%d",v,s,i, result.getInfectedAmountPerIteration().get(i), result.getNewInfectedPerIteration().get(i)));
                }
            }
        }
        writer.close();
    }

    public static void obs2K() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("obs2_k.csv");
        writer.println("k,i,s");

        float[] cautiousness = new float[]{0.1f, 0.3f, 0.5f, 0.7f, 0.9f, 1};
        int samples = 50;

        for (float v : cautiousness) {
            System.out.println("Begin with k: "+v);
            EvolutionRule rule = new InfectionRule(0.5f, 0.1f, 0.5f, 0.12f, 0.04f, 0.8f, 0.6f, 8, 2, 18, 18, 1);

            for (int s = 0; s < samples; s++) {
                Cell[][] initialGrid = Main.randomGrid(100, 0.1f, 0.05f, v, true);
                List<Cell[][]> results = Automata.run(initialGrid, rule, 50).getGrid();

                Cell[][] grid = results.get(results.size() - 1);
                int iCount = 0;
                int sCount = 0;
                for (Cell[] cells : grid) {
                    for (Cell cell : cells) {
                        if (cell.getCellState() == CellState.INFECTED || cell.getCellState() == CellState.EXPOSED || cell.getCellState() == CellState.QUARANTINED)
                            iCount++;
                        if (cell.getCellState() == CellState.SUSCEPTIBLE)
                            sCount++;
                    }
                }

                writer.println(String.format("%g,%d,%d",v,iCount,sCount));
            }
        }

        writer.close();
    }

    public static void obs2Pq() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("obs2_pq.csv");
        writer.println("pq,i,s");

        float[] pq = new float[]{0.025f, 0.05f, 0.1f, 0.15f, 0.2f};
        int samples = 50;

        for (float v : pq) {
            System.out.println("Begin with k: "+v);
            EvolutionRule rule = new InfectionRule(0.5f, v, 0.5f, 0.12f, 0.04f, 0.8f, 0.6f, 8, 2, 18, 18, 1);

            for (int s = 0; s < samples; s++) {
                Cell[][] initialGrid = Main.randomGrid(100, 0.1f, 0.05f, 0.5f, true);
                List<Cell[][]> results = Automata.run(initialGrid, rule, 50).getGrid();

                Cell[][] grid = results.get(results.size() - 1);
                int iCount = 0;
                int sCount = 0;
                for (Cell[] cells : grid) {
                    for (Cell cell : cells) {
                        if (cell.getCellState() == CellState.INFECTED || cell.getCellState() == CellState.EXPOSED || cell.getCellState() == CellState.QUARANTINED)
                            iCount++;
                        if (cell.getCellState() == CellState.SUSCEPTIBLE)
                            sCount++;
                    }
                }

                writer.println(String.format("%g,%d,%d",v,iCount,sCount));
            }
        }

        writer.close();
    }

    public static void obs2Pc() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("obs2_pc.csv");
        writer.println("pc,i,s");

        float[] pc = new float[]{0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f};
        int samples = 50;

        for (float v : pc) {
            System.out.println("Begin with k: "+v);
            EvolutionRule rule = new InfectionRule(v, 0.1f, 0.5f, 0.12f, 0.04f, 0.8f, 0.6f, 8, 2, 18, 18, 1);

            for (int s = 0; s < samples; s++) {
                Cell[][] initialGrid = Main.randomGrid(100, 0.1f, 0.05f, 0.5f, true);
                List<Cell[][]> results = Automata.run(initialGrid, rule, 50).getGrid();

                Cell[][] grid = results.get(results.size() - 1);
                int iCount = 0;
                int sCount = 0;
                for (Cell[] cells : grid) {
                    for (Cell cell : cells) {
                        if (cell.getCellState() == CellState.INFECTED || cell.getCellState() == CellState.EXPOSED || cell.getCellState() == CellState.QUARANTINED)
                            iCount++;
                        if (cell.getCellState() == CellState.SUSCEPTIBLE)
                            sCount++;
                    }
                }

                writer.println(String.format("%g,%d,%d",v,iCount,sCount));
            }
        }

        writer.close();
    }

    public static void obs3() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("obs3.csv");
        writer.println("p_infected,max_infected,t_max");

        float[] values = new float[]{0.01f, 0.05f, 0.1f, 0.15f, 0.2f, 0.3f, 0.4f, 0.5f};
        int samples = 50;

        for (float v : values) {
            System.out.println("Begin with k: "+v);
            EvolutionRule rule = new InfectionRule(0.5f, 0.1f, 0.5f, 0.12f, 0.04f, 0.8f, 0.6f, 8, 2, 18, 18, 1);

            for (int s = 0; s < samples; s++) {
                Cell[][] initialGrid = Main.randomGrid(100, 0.1f, v, 0.5f, true);
                List<Cell[][]> results = Automata.run(initialGrid, rule, 50).getGrid();

                int maxInfected = 0;
                int maxT = 0;
                for (int t = 0; t < results.size(); t++) {
                    int iCount = 0;
                    for (Cell[] cells : results.get(t)) {
                        for (Cell cell : cells) {
                            if (cell.getCellState() == CellState.INFECTED || cell.getCellState() == CellState.EXPOSED || cell.getCellState() == CellState.QUARANTINED)
                                iCount++;
                        }
                    }

                    if (iCount > maxInfected) {
                        maxInfected = iCount;
                        maxT = t;
                    }
                }

                writer.println(String.format("%g,%d,%d",v,maxInfected,maxT));
            }
        }

        writer.close();
    }

    public static void obs4() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("t_vs_k.csv");
        writer.println("k,t,d,r");

        int samples = 50;
        float[] values = new float[]{0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f};
        EvolutionRule rule = new InfectionRule(0.5f, 0.1f, 0.5f, 0.12f, 0.04f, 0.8f, 0.6f, 8, 2, 18, 18, 1);
        Cell[][] originalGrid = Main.randomGrid(100, 0.1f, 0.05f, 0.1f, true);

        for (float v : values) {
            System.out.println("Running for: "+v);

            for (int s = 0; s < samples; s++) {
                // Make a copy of the grid
                int cautiousnessAmount = (int) (originalGrid.length * originalGrid.length * v);
//                System.out.println(cautiousnessAmount);
                Cell[][] initialGrid = new Cell[originalGrid.length][originalGrid[0].length];
                for (int i = 0; i  < originalGrid.length; i++) {
                    for (int j = 0; j < originalGrid.length; j++) {
                        if (cautiousnessAmount > 0) {
                            initialGrid[i][j] = new Cell(originalGrid[i][j].getCellState(), true);
                            cautiousnessAmount--;
                        } else
                            initialGrid[i][j] = new Cell(originalGrid[i][j].getCellState(), false);
                    }
                }

                Result result = Automata.run(initialGrid, rule, 100);
                int dead = 0;
                int recovered = 0;
                int lastIndex = result.getGrid().size() - 1;
                for (int i = 0; i < result.getGrid().get(lastIndex).length; i++) {
                    for (int j = 0; j < result.getGrid().get(lastIndex)[i].length; j++) {
                        if (result.getGrid().get(lastIndex)[i][j].getCellState() == CellState.DEAD) {
                            dead++;
                        }
                        if (result.getGrid().get(lastIndex)[i][j].getCellState() == CellState.RECOVERED) {
                            recovered++;
                        }
                    }
                }

//                System.out.println(result.getGrid().size());
                writer.println(String.format("%g,%d,%d,%d", v, result.getGrid().size(), dead, recovered));
            }
        }
        writer.close();
    }
}
