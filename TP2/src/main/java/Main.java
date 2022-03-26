import evolutionRules.EvolutionRule;
import evolutionRules.lifeGameRules.B1S12Rule;
import evolutionRules.lifeGameRules.B36S23Rule;
import evolutionRules.lifeGameRules.B3S23D5Rule;
import evolutionRules.lifeGameRules.B3S23Rule;
import cell.Cell;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        // Read input parameters

        InputStream is = new FileInputStream(new File("src/main/resources/config.json"));
        JSONObject json = new JSONObject(new JSONTokener(is));

        int maxIterations = json.getInt("maxIterations");

        // Parse evolution rule

        String ruleStr = json.getString("evolutionRule");

        EvolutionRule rule;
        switch (ruleStr) {
            case "B1/S12":
                rule = new B1S12Rule(); break;
            case "B3/S23/D5":
                rule = new B3S23D5Rule(); break;
            case "B3/S23":
                rule = new B3S23Rule(); break;
            case "B36/S23":
                rule = new B36S23Rule(); break;
            default:
                throw new RuntimeException("Unknown rule name");
        }

        // Parse grid parameters

        JSONObject grid = (JSONObject) json.get("grid");

        String type = grid.getString("type");
        int L = grid.getInt("size");

        if(grid.has("aliveParticles")) {
            JSONArray array = grid.getJSONArray("aliveParticles");

            if(type.equals("2D"))
                Automata.run(parsedGrid2D(L, array), rule, maxIterations);
            else
                Automata.run(parsedGrid3D(L, array), rule, maxIterations);
        } else {
            int p = grid.getInt("aliveProportion");

            if(type.equals("2D"))
                Automata.run(randomGrid2D(L, p), rule, maxIterations);
            else
                Automata.run(randomGrid3D(L, p), rule, maxIterations);
        }
    }

    public static Cell[][] parsedGrid2D(int L, JSONArray points) {
        Cell[][] grid = new Cell[L][L];

        for(int i=0; i<points.length(); i++) {
            JSONArray point = points.getJSONArray(i);
            grid[point.getInt(0)][point.getInt(1)] = new Cell(true);
        }

        for(int x=0; x<L; x++) {
            for(int y=0; y<L; y++) {
                if(grid[x][y] == null)
                    grid[x][y] = new Cell(false);
            }
        }

        return grid;
    }

    public static Cell[][][] parsedGrid3D(int L, JSONArray points) {
        Cell[][][] grid = new Cell[L][L][L];

        for(int i=0; i<points.length(); i++) {
            JSONArray point = points.getJSONArray(i);
            grid[point.getInt(0)][point.getInt(1)][point.getInt(2)] = new Cell(true);
        }

        for(int x=0; x<L; x++) {
            for(int y=0; y<L; y++) {
                for(int z=0; z<L; z++) {
                    if(grid[x][y][z] == null)
                        grid[x][y][z] = new Cell(false);
                }
            }
        }

        return grid;
    }

    private static Cell[][] randomGrid2D(int L, int p) {
        throw new RuntimeException();
    }

    private static Cell[][][] randomGrid3D(int L, int p) {
        throw new RuntimeException();
    }

}