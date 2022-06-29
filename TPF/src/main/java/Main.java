import cell.CellState;
import evolutionRules.EvolutionRule;
import evolutionRules.InfectionRule;

import cell.Cell;
import org.apache.commons.math3.util.Pair;

import java.io.*;
import java.util.*;

import org.json.JSONObject;
import org.json.JSONTokener;


public class Main {

    public static void main(String[] args) throws IOException {
        if(args.length != 1) {
            System.out.println("Usage: ./run.jar config.json");
            return;
        }

        InputStream is = new FileInputStream(args[0]);
        JSONObject json = new JSONObject(new JSONTokener(is));

        // Read initial grid parameters

        int maxIterations = json.getInt("maxIterations");
        int gridSize = json.getInt("gridSize");
        int susceptible = json.getInt("susceptible");
        int infected = json.getInt("infected");
        float cautiousness = json.getFloat("cautiousness");
        boolean sparse = json.getString("distribution").equals("sparse");

        Cell[][] initialGrid = randomGrid(gridSize, susceptible, infected, cautiousness, sparse);

        // Read evolution rule parameters

        JSONObject probabilities = (JSONObject) json.get("probabilities");

        float pe = probabilities.getFloat("exposure");
        float pi = probabilities.getFloat("infection");
        float pq = probabilities.getFloat("quarantine");
        float pr = probabilities.getFloat("recovery");
        float pd = probabilities.getFloat("death");

        JSONObject periods = (JSONObject) json.get("periods");

        int ti = periods.getInt("infection");
        int tq = periods.getInt("quarantine");
        int tr = periods.getInt("recovery");
        int td = periods.getInt("death");

        JSONObject effectiveness = (JSONObject) json.get("effectiveness");

        float em = effectiveness.getFloat("mask");
        float eq = effectiveness.getFloat("hospitalization");

        EvolutionRule rule = new InfectionRule(pe, pq, pi, pr, pd, em, eq, ti, tq, tr, td);

        // Run the simulation

        List<Cell[][]> results = Automata.run(initialGrid, rule, maxIterations);

        // Write results to output

        PrintWriter writer;
        try {
            writer = new PrintWriter("output.csv", "UTF-8");
        } catch (IOException e) {
            System.out.println("Couldn't create output file 'output.csv'");
            return;
        }

        writer.println("t,x,y,state");
        for (int t = 0; t < results.size(); t++) {
            Cell[][] grid = results.get(t);
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    if (grid[i][j].getCellState() != CellState.EMPTY) {
                        String cellState = grid[i][j].getCellState().toString().split("\\.")[1];
                        writer.println(String.format("%d,%d,%d,%s", t, i, j, cellState));
                    }
                }
            }
        }

        writer.close();
    }

    static Cell[][] randomGrid(int L, int susceptible, int infected, float cautiousness, boolean sparse) {
        double u = (double) L / 2;
        double sd = (double) (L / 4) / 3;

        Random r = new Random();
        Map<Pair<Integer, Integer>, CellState> usedCoordinates = new HashMap<>();
        
        int x, y;
        for(int i=0; i<susceptible + infected; i++){
            do{
                x = (int) Math.round(sparse ? r.nextFloat() * L : r.nextGaussian(u, sd));
                y = (int) Math.round(sparse ? r.nextFloat() * L : r.nextGaussian(u, sd));
            } while(usedCoordinates.containsKey(new Pair<>(x, y)));
            usedCoordinates.put(new Pair<>(x, y), i<susceptible ? CellState.SUSCEPTIBLE : CellState.INFECTED);
        }

        Cell[][] randomGrid = new Cell[L][L];
        for(int row=0; row < L; row++){
            for(int col=0; col<L; col++){
                Pair<Integer, Integer> point = new Pair<>(row, col);
                if(usedCoordinates.containsKey(point)){
                    randomGrid[row][col] = new Cell(usedCoordinates.get(point), Math.random() < cautiousness);
                } else {
                    randomGrid[row][col] = new Cell(CellState.EMPTY, false);
                }
            }
        }

        return randomGrid;
    }

}