import evolutionRules.EvolutionRule;
import evolutionRules.lifeGameRules.r2D.Rule1112;
import evolutionRules.lifeGameRules.r2D.Rule3623;
import evolutionRules.lifeGameRules.r2D.Rule3323;
import evolutionRules.lifeGameRules.r3D.Rule2645;
import evolutionRules.lifeGameRules.r3D.Rule5556;
import evolutionRules.lifeGameRules.r3D.Rule6657;

import cell.Cell;
import points.Point2D;
import points.Point3D;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;


public class Main {

    public static void main(String[] args) throws IOException {
        if(args.length != 1) {
            System.out.println("Usage: ./run.jar config.json");
            return;
        }

        // Read input parameters

        InputStream is = new FileInputStream(args[0]);
        JSONObject json = new JSONObject(new JSONTokener(is));

        int maxIterations = json.getInt("maxIterations");

        // Parse evolution rule

        String ruleStr = json.getString("evolutionRule");

        EvolutionRule rule;
        switch (ruleStr) {
            case "1112":
                rule = new Rule1112(); break;
            case "3323":
                rule = new Rule3323(); break;
            case "3623":
                rule = new Rule3623(); break;
            case "5556":
                rule = new Rule5556(); break;
            case "6657":
                rule = new Rule6657(); break;
            case "2645":
                rule = new Rule2645(); break;
            default:
                throw new RuntimeException("Unknown rule name");
        }

        // Parse grid parameters

        JSONObject grid = (JSONObject) json.get("grid");

        String method = grid.getString("method");
        String type = grid.getString("type");
        int L = grid.getInt("size");
        float p = grid.getJSONObject("random").getFloat("aliveProportion");

        Cell[][] grid2D = null;
        Cell[][][] grid3D = null;

        switch (method) {
            case "random":

                if (type.equals("2D"))
                    grid2D = randomGrid2D(L, p);
                else
                    grid3D = randomGrid3D(L, p);
                break;
            case "array": {
                JSONArray array = grid.getJSONArray("particles");

                if (type.equals("2D"))
                    grid2D = parsedGrid2D(L, array, true);
                break;
            }
            case "alive_coordinates": {
                JSONArray array = grid.getJSONArray("particles");

                if (type.equals("2D"))
                    grid2D = parsedGrid2D(L, array, false);
                else
                    grid3D = parsedGrid3D(L, array);
                break;
            }
            default:
                throw new RuntimeException("Invalid parameters");
        }

        PrintWriter writer;
        try {
            writer = new PrintWriter(String.format("output_%s_%g.csv", ruleStr, p), "UTF-8");
//            writer = new PrintWriter("output.csv", "UTF-8");
        } catch (IOException e) {
            System.out.println("Couldn't create output file 'output.csv'");
            return;
        }

        if (grid2D != null) {
            writer.println("t,x,y,a,b");
            List<Cell[][]> results = Automata.run(grid2D, rule, maxIterations);
            for (int t = 0; t < results.size(); t++) {
                Cell[][] aux = results.get(t);
                for (int i = 0; i < aux.length; i++) {
                    for (int j = 0; j < aux[i].length; j++) {
                        if (aux[i][j].isAlive()) {
                            writer.println(String.format("%d,%d,%d,%s",t,i,j,aux[i][j].toString()));
                        }
                    }
                }
            }

        } else if(grid3D != null) {
            writer.println("t,x,y,z,a,b");
            List<Cell[][][]> results = Automata.run(grid3D, rule, maxIterations);
            for (int t = 0; t < results.size(); t++) {
                for (int i = 0; i < results.get(t).length; i++) {
                    for (int j = 0; j < results.get(t)[i].length; j++) {
                        for (int k = 0; k < results.get(t)[i][j].length; k++) {
                            if (results.get(t)[i][j][k].isAlive()) {
                                writer.println(String.format("%d,%d,%d,%d,%s",t,i,j,k,results.get(t)[i][j][k].toString()));
                            }
                        }
                    }
                }
            }
        }

        writer.close();

    }

    public static Cell[][] parsedGrid2D(int L, JSONArray points, boolean isArray) {
        Cell[][] grid = new Cell[L][L];

        for(int i=0; i<points.length(); i++) {
            JSONArray point = points.getJSONArray(i);
            if (isArray) {
                for (int j = 0; j < point.length(); j++) {
                    grid[i][j] = new Cell(point.getInt(j) == 1);
                }
            } else {
                grid[point.getInt(0)][point.getInt(1)] = new Cell(true);
            }
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

        for (int x=0; x<L; x++) {
            for (int y=0; y < L; y++) {
                for (int z=0; z < L; z++) {
                    if (grid[x][y][z] == null)
                        grid[x][y][z] = new Cell(false);
                }
            }
        }

        return grid;
    }

    static Cell[][] randomGrid2D(int L, double p) {
        long N = Math.round(p * L/2 * L/2);
        double u = (double) L/2;
        double sd = (double) (L / 4) / 3;

        Random r = new Random();
        Set<Point2D> aliveCellsCoordinates = new HashSet<>();
        int x, y;
        for(int i=0; i<N; i++){
            do{
                x = (int) Math.round(r.nextGaussian() * sd + u);
                y = (int) Math.round(r.nextGaussian() * sd + u);
            } while(aliveCellsCoordinates.contains(new Point2D(x, y)));
            aliveCellsCoordinates.add(new Point2D(x, y));
        }

        Cell[][] randomGrid2D = new Cell[L][L];
        for(int row=0; row < L; row++){
            for(int col=0; col<L; col++){
                if(aliveCellsCoordinates.contains(new Point2D(row, col))){
                    randomGrid2D[row][col] = new Cell(true);
                } else {
                    randomGrid2D[row][col] = new Cell(false);
                }
            }
        }

        return randomGrid2D;
    }

    static Cell[][][] randomGrid3D(int L, double p) {
        long N = Math.round(p * L/2 * L/2 * L/2);
        double u = (double) L / 2;
        double sd = (double) (L / 4) / 3;



        Random r = new Random();
        Set<Point3D> aliveCellsCoordinates = new HashSet<>();
        int x, y, z;
        for(int i=0; i<N; i++){
            do{
                x = (int) Math.round(r.nextGaussian() * sd + u);
                y = (int) Math.round(r.nextGaussian() * sd + u);
                z = (int) Math.round(r.nextGaussian() * sd + u);
            } while(aliveCellsCoordinates.contains(new Point3D(x, y, z)));
            aliveCellsCoordinates.add(new Point3D(x, y, z));
        }

        Cell[][][] randomGrid3D = new Cell[L][L][L];
        for(int row=0; row < L; row++){
            for(int col=0; col<L; col++){
                for(int depth=0; depth<L; depth++){
                    if(aliveCellsCoordinates.contains(new Point3D(row, col, depth))){
                        randomGrid3D[row][col][depth] = new Cell(true);
                    } else {
                        randomGrid3D[row][col][depth] = new Cell(false);
                    }
                }
            }
        }

        return randomGrid3D;
    }

}