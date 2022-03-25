import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {

    private static Particle p(int id) {
        return new Particle(id, 0, 0, 0);
    }

    // L=6, M=3, rc=1

    // X 2 X X X X    0 0 1 1 2 2
    // 0 3 X X X X    0 0 1 1 2 2
    // 1 4 X X X X    3 3 4 4 5 5
    // X X 5 X X X    3 3 4 4 5 5
    // X X X X X X    6 6 7 7 8 8
    // X X X X X X    6 6 7 7 8 8

    // 0: 1, 3, 4 - 5: 4

    @Test
    void nonPeriodicTest() {
        Set<Particle> particles = new HashSet<>();

        particles.add(new Particle(0, 0, 1, 0));
        particles.add(new Particle(1, 0, 2, 0));
        particles.add(new Particle(2, 1, 0, 0));
        particles.add(new Particle(3, 1, 1, 0));
        particles.add(new Particle(4, 1, 2, 1));
        particles.add(new Particle(5, 2, 3, 0));

        Set<Particle> expectedN0 = new HashSet<>();
        expectedN0.add(p(1)); expectedN0.add(p(3)); expectedN0.add(p(4));

        Set<Particle> expectedN5 = new HashSet<>();
        expectedN5.add(p(4));

        Map<Particle, Set<Particle>> neighbours = CIM.findNeighbours(particles, 1, 6, 3, false);

        Set<Particle> n0 = neighbours.get(p(0));
        Set<Particle> n5 = neighbours.get(p(5));

        assertEquals(expectedN0, n0);
        assertEquals(expectedN5, n5);
    }

    // L=6, M=3, rc=2

    // 0 X 1 X X 2    0 0 1 1 2 2
    // X X X X X X    0 0 1 1 2 2
    // X 3 4 5 X X    3 3 4 4 5 5
    // X X X X X X    3 3 4 4 5 5
    // X X X X X X    6 6 7 7 8 8
    // X 6 7 X X 8    6 6 7 7 8 8

    // Neighbours - 0: 1, 2, 6, 8 - 2: 0, 8 - 4: 1, 3, 5 - 6: 0, 1, 7, 8 - 8: 0, 2, 6, 7

    // R=0: 0,1,2,3,4,5,6,7 - R=1: 8

    @Test
    void periodicTest() {
        Set<Particle> particles = new HashSet<>();

        particles.add(new Particle(0, 0, 0, 0));
        particles.add(new Particle(1, 2, 0, 0));
        particles.add(new Particle(2, 5, 0, 0));

        particles.add(new Particle(3, 1, 2, 0));
        particles.add(new Particle(4, 2, 2, 0));
        particles.add(new Particle(5, 3, 2, 0));

        particles.add(new Particle(6, 1, 5, 0));
        particles.add(new Particle(7, 2, 5, 0));
        particles.add(new Particle(8, 5, 5, 1));

        Set<Particle> expectedN0 = new HashSet<>();
        expectedN0.add(p(1)); expectedN0.add(p(2)); expectedN0.add(p(6)); expectedN0.add(p(8));

        Set<Particle> expectedN2 = new HashSet<>();
        expectedN2.add(p(0)); expectedN2.add(p(8));

        Set<Particle> expectedN4 = new HashSet<>();
        expectedN4.add(p(1)); expectedN4.add(p(3)); expectedN4.add(p(5));

        Set<Particle> expectedN6 = new HashSet<>();
        expectedN6.add(p(0)); expectedN6.add(p(1)); expectedN6.add(p(7)); expectedN6.add(p(8));

        Set<Particle> expectedN8 = new HashSet<>();
        expectedN8.add(p(0)); expectedN8.add(p(2)); expectedN8.add(p(6)); expectedN8.add(p(7));

        Map<Particle, Set<Particle>> neighbours = CIM.findNeighbours(particles, 2, 6, 3, true);

        Set<Particle> n0 = neighbours.get(p(0));
        Set<Particle> n2 = neighbours.get(p(2));
        Set<Particle> n4 = neighbours.get(p(4));
        Set<Particle> n6 = neighbours.get(p(6));
        Set<Particle> n8 = neighbours.get(p(8));

        assertEquals(expectedN0, n0);
        assertEquals(expectedN2, n2);
        assertEquals(expectedN4, n4);
        assertEquals(expectedN6, n6);
        assertEquals(expectedN8, n8);
    }

}
