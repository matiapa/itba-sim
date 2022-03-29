import cell.Cell;
import evolutionRules.lifeGameRules.r2D.Rule3323;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.*;

public class AutomataTest {

    @Test
    public void test_B3S23_2D() {
        int L = 10;
        int maxT = 15;

        Integer[][][] initials = new Integer[][][]{
            {{1,1}, {1,2}, {2,1}, {2,2}},           // Static block,
            {{1,2},{2,2},{3,2}},                    // Blinker
            {{1,4}, {2,5}, {3,3}, {3,4}, {3,5}}     // Glider
        };

        Integer[][][] expectedFinals = new Integer[][][]{
            {{1,1}, {1,2}, {2,1}, {2,2}},           // Static block,
            {{2,1}, {2,2}, {2,3}},                  // Blinker
            {{5,7}, {6,8}, {6,9}, {7,7}, {7,8}}     // Glider
        };

        for(int i=0; i<initials.length; i++) {
            System.out.printf("Testing configuration %d%n", i);

            List<Integer[]> expectedFinal = Arrays.asList(expectedFinals[i]);
            System.out.printf("Expecting: %s%n", Arrays.deepToString(expectedFinal.toArray()));

            Cell[][] grid = Main.parsedGrid2D(L, new JSONArray(initials[i]), false);
            Cell[][] finalGrid = Automata.run(grid, new Rule3323(), maxT);
            assertNotNull(finalGrid);

            List<Integer[]> finals = getAlivePoints(finalGrid);
            System.out.printf("Obtained:  %s%n", Arrays.deepToString(finals.toArray()));

            assertEquals(finals.size(), expectedFinal.size());
            for(int j=0; j<finals.size(); j++) {
                assertEquals(finals.get(j)[0], expectedFinal.get(j)[0]);
                assertEquals(finals.get(j)[1], expectedFinal.get(j)[1]);
            }
            System.out.printf("Configuration %d passed!%n", i);
            System.out.println("---------------------------");
        }
    }

    @Test
    public void test_B3S23_3D() {
        int L = 25;
        int maxT = 15;

        Integer[][][] initials = new Integer[][][]{
            {{13,13,13},{13,13,14},{13,13,12},{13,14,13},{13,12,13},{14,13,13},{12,13,13}},                    // Blinker
        };

        Integer[][][] expectedFinals = new Integer[][][]{
            {{2,1,4}, {2,2,4}, {2,3,4}},                  // Blinker
        };

        for(int i=0; i<initials.length; i++) {
            System.out.printf("Testing configuration %d%n", i);

            List<Integer[]> expectedFinal = Arrays.asList(expectedFinals[i]);
            System.out.printf("Expecting: %s%n", Arrays.deepToString(expectedFinal.toArray()));

            Cell[][][] grid = Main.parsedGrid3D(L, new JSONArray(initials[i]));
            Cell[][][] finalGrid = Automata.run(grid, new Rule3323(), maxT);
            assertNotNull(finalGrid);

            List<Integer[]> finals = getAlivePoints(finalGrid);
            System.out.printf("Obtained:  %s%n", Arrays.deepToString(finals.toArray()));

            assertEquals(finals.size(), expectedFinal.size());
            for(int j=0; j<finals.size(); j++) {
                assertEquals(finals.get(j)[0], expectedFinal.get(j)[0]);
                assertEquals(finals.get(j)[1], expectedFinal.get(j)[1]);
                assertEquals(finals.get(j)[2], expectedFinal.get(j)[2]);
            }
            System.out.printf("Configuration %d passed!%n", i);
            System.out.println("---------------------------");
        }
    }

    private List<Integer[]> getAlivePoints(Cell[][] grid) {
        List<Integer[]> points = new ArrayList<>();

        for(int x=0; x<grid.length; x++) {
            for(int y=0; y<grid.length; y++) {
                if(grid[x][y].isAlive())
                    points.add(new Integer[]{x, y});
            }
        }

        return points;
    }

    private List<Integer[]> getAlivePoints(Cell[][][] grid) {
        List<Integer[]> points = new ArrayList<>();

        for(int x=0; x<grid.length; x++) {
            for(int y=0; y<grid.length; y++) {
                for(int z=0; z<grid.length; z++) {
                    if(grid[x][y][z].isAlive())
                        points.add(new Integer[]{x, y, z});
                }
            }
        }

        return points;
    }

}