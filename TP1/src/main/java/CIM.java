import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class CIM {

    static private int f(int x, int y) {
        return Math.floorMod(x,y);
    }

    static private void addZoneNeighbours(
        Set<Particle> particleZones, Map<Particle, Set<Particle>> particleNeighbours,
        Particle p, float rc, float L, int M, boolean periodic
    ) {
        if (particleZones != null)
            particleNeighbours.get(p).addAll(particleZones.stream().parallel().filter(c -> !p.equals(c) && p.distanceTo(c, L, periodic) <= rc).collect(Collectors.toList()));
    }

    static public Map<Particle, Set<Particle>> findNeighbours(Set<Particle> particles, float rc, float L, int M, boolean periodic) {

        Map<Particle, Set<Particle>> particleNeighbours = new HashMap<>();
        ArrayList<ArrayList<Set<Particle>>> zones = new ArrayList<>();

        // Add empty sets to particle zones

        for (int i = 0; i < M; i++) {
            zones.add(new ArrayList<>());
            ArrayList<Set<Particle>> column = zones.get(i);
            for (int j = 0; j < M; j++) {
                column.add(new HashSet<>());
            }
        }

        float zoneSize = (float) L / M;

        // Add particle to its corresponding zone

        for (Particle p : particles) {
            // Get the zone coordinates of the particle
            int x = (int) (Math.abs(p.getX()) / zoneSize);
            int y = (int) (Math.abs(p.getY()) / zoneSize);

            zones.get(x).get(y).add(p);
        }

        // Find neighbours of all particles

        for (Particle p : particles) {
            // Create a set for storing particle neighbours
            particleNeighbours.put(p, new HashSet<>());

            // Get the zone coordinates of the particle
            int x = (int) (Math.abs(p.getX()) / zoneSize);
            int y = (int) (Math.abs(p.getY()) / zoneSize);

            // Check if particles on the near zones are neighbours
            List<Set<Particle>> nearZonesNonPeriodic = Arrays.asList(
                    x == 0 || y == 0 ? null : zones.get(x-1).get(y-1),                            // NO
                    y == 0 ? null : zones.get(x).get(y-1),                                        // N
                    x == M - 1 || y == 0 ? null : zones.get(x+1).get(y-1),                        // NE
                    x == 0 ? null : zones.get(f(x-1, M)).get(y),                               // O
                    zones.get(x).get(y),
                    x == M - 1 ? null : zones.get(f(x+1,M)).get(y),                            // E
                    x == 0 || y == M - 1 ? null : zones.get(f(x-1, M)).get(f(y+1, M)),      // SO
                    y == M - 1 ? null : zones.get(x).get(f(y+1,M)),                            // S
                    x == M - 1 || y == M - 1 ? null : zones.get(f(x+1,M)).get(f(y+1,M))     // SE

            );

            List<Set<Particle>> nearZonesPeriodic = Arrays.asList(
                    zones.get(f(x-1, M)).get(f(y-1, M)),     zones.get(x).get(f(y-1,M)),          zones.get(f(x+1,M)).get(f(y-1,M)),
                    zones.get(f(x-1, M)).get(y),                zones.get(x).get(y),                    zones.get(f(x+1,M)).get(y),
                    zones.get(f(x-1, M)).get(f(y+1, M)),     zones.get(x).get(f(y+1,M)),          zones.get(f(x+1,M)).get(f(y+1,M))

            );



            (periodic ? nearZonesPeriodic : nearZonesNonPeriodic).forEach(nz -> {
                addZoneNeighbours(nz, particleNeighbours, p, rc, L, M, periodic);
            });
        }

        return particleNeighbours;

    }

    public static void outputResult(Set<Particle> particles, Map<Particle, Set<Particle>> neighbours, int L) throws IOException {
        FileWriter writer = new FileWriter("output.csv");

        writer.write("ID,x,y,r,neighbours\n");

        for (Particle particle : particles) {
            writer.write(particle.getId()+","+particle.getX()+","+particle.getY()+","+particle.getR()+",\""+Arrays.deepToString(neighbours.get(particle).toArray())+"\"");

            writer.write('\n');
        }

        writer.close();
    }

}