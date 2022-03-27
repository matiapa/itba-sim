import evolutionRules.EvolutionRule;
import evolutionRules.lifeGameRules.B1S12Rule;
import evolutionRules.lifeGameRules.B36S23Rule;
import evolutionRules.lifeGameRules.B3S23D5Rule;
import evolutionRules.lifeGameRules.B3S23Rule;
import cell.Cell;
import points.Point2D;
import points.Point3D;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


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

        String method = grid.getString("method");
        String type = grid.getString("type");
        int L = grid.getInt("size");

        Cell[][] grid2D = null;
        Cell[][][] grid3D = null;

        switch (method) {
            case "random":
                int p = grid.getJSONObject("random").getInt("aliveProportion");

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

        if (grid2D != null)
            Automata.run(grid2D, rule, maxIterations);
        else if(grid3D != null)
            Automata.run(grid3D, rule, maxIterations);

        // Generate random grids using Normal Distribution

        Cell[][] randomGrid = randomGrid2D(6, 0.4);
//        int count=0;
//        for(int i=0; i<6; i++){
//            for(int j=0; j<6; j++){
//               if(randomGrid[i][j].isAlive()){
//                   count++;
//               }
//            }
//        }
//
//        System.out.println(count);

        Cell[][][] randomGrid2 = randomGrid3D(6, 0.4);
//        int count2=0;
//
//        for(int i=0; i<6; i++){
//            for(int j=0; j<6; j++){
//                for(int k=0; k<6; k++){
//                    if(randomGrid2[i][j][k].isAlive()){
//                        count2++;
//                    }
//                }
//            }
//        }
//
//        System.out.println(count2);
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

    private static Cell[][] randomGrid2D(int L, double p) {
        double N = Math.round(p * Math.pow(L, 2));
        double u = L/2;
        double sd = L/4;
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

//        System.out.println(aliveCellsCoordinates);

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

    private static Cell[][][] randomGrid3D(int L, double p) {
        double N = Math.round(p * Math.pow(L, 2));
        double u = L/2;
        double sd = L/4;
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

//        System.out.println(aliveCellsCoordinates);

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