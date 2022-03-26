import cell.Cell;
import evolutionRules.lifeGameRules.B3S23Rule;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.*;

public class AutomataTest {

    @Test
    public void test_B3S23_2D() {
        int L = 10;
        int maxIterations = 15;

        Integer[][][] initials = new Integer[][][]{
            {{1,1}, {1,2}, {2,1}, {2,2}},           // Static block,
            {{0,4}, {1,5}, {2,3}, {2,4}, {2,5}},    // Glider
        };

        Integer[][][] expectedFinals = new Integer[][][]{
            {{1,1}, {1,2}, {2,1}, {2,2}},           // Static block,
            {{4,7}, {5,8}, {5,9}, {6,4}, {6,5}},    // Glider
        };

        for(int i=0; i<initials.length; i++) {
            System.out.printf("Testing configuration %d%n", i);

            List<Integer[]> expectedFinal = Arrays.asList(expectedFinals[i]);
            System.out.printf("Expecting: %s%n", Arrays.deepToString(expectedFinal.toArray()));

            Cell[][] grid = Main.parsedGrid2D(L, new JSONArray(initials[i]));
            int iterations = Automata.run(grid, new B3S23Rule(), maxIterations);

            List<Integer[]> finals = getAlivePoints(grid);
            System.out.printf("Obtained:  %s%n", Arrays.deepToString(finals.toArray()));
            System.out.printf("Iterations: %d%n", iterations);

            assertEquals(finals.size(), expectedFinal.size());
            for(int j=0; j<finals.size(); j++) {
                assertEquals(finals.get(j)[0], expectedFinal.get(j)[0]);
                assertEquals(finals.get(j)[1], expectedFinal.get(j)[1]);
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

}