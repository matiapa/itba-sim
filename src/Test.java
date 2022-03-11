import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Test {

    // L=6, M=3, rc=1

    // X 2 X X X    0 0 1 1 2 2
    // 0 3 X X X    0 0 1 1 2 2
    // 1 4 X X X    3 3 4 4 5 5
    // X X 5 X X    3 3 4 4 5 5
    // X X X X X    6 6 7 7 8 8
    // X X X X X    6 6 7 7 8 8

    // 0: 1,3,4    0 1 2 3 4
    // 5: 4

    private static Particle p(int id) {
        return new Particle(id, 0, 0, 0);
    }

    public static void main(String[] args) {
        Set<Particle> particles = new HashSet<>();

        particles.add(new Particle(0, 0, 1, 0));
        particles.add(new Particle(1, 0, 2, 0));
        particles.add(new Particle(2, 1, 0, 0));
        particles.add(new Particle(3, 1, 1, 0));
        particles.add(new Particle(4, 1, 2, 0));
        particles.add(new Particle(5, 2, 3, 0));

        Set<Particle> expectedN0 = new HashSet<>();
        expectedN0.add(p(1)); expectedN0.add(p(3)); expectedN0.add(p(4));

        Set<Particle> expectedN5 = new HashSet<>();
        expectedN5.add(p(4));

        Map<Particle, Set<Particle>> neighbours = CIM.findNeighbours(particles, 1, 6, 3);

        Set<Particle> n0 = neighbours.get(p(0));
        Set<Particle> n5 = neighbours.get(p(5));

        if(n0.equals(expectedN0) && n5.equals(expectedN5))
            System.out.println("Passed!");
        else
            System.out.println("Failed");

        System.out.println(n0);
        System.out.println(n5);
    }

}
