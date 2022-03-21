import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class CIM {

    static private int f(int x, int y) {
        return Math.floorMod(x,y);
    }

    static private void addZoneNeighbours(
            ArrayList<Set<Particle>> particleZones, Map<Particle, Set<Particle>> particleNeighbours,
            Particle p, float rc, int L, int M, int zone, boolean periodic
    ) {
        if(zone >=0 && zone < M*M) {
            particleNeighbours.get(p).addAll(particleZones.get(zone).stream().parallel().filter(c -> !p.equals(c) && p.distanceTo(c, L, periodic) <= rc).collect(Collectors.toList()));
        }
    }

    static public Map<Particle, Set<Particle>> findNeighbours(Set<Particle> particles, float rc, int L, int M, boolean periodic) {

        ArrayList<Set<Particle>> particleZones = new ArrayList<>();
        Map<Particle, Set<Particle>> particleNeighbours = new HashMap<>();

        // Add empty sets to particle zones

        for(int i=0; i<M*M; i++)
            particleZones.add(new HashSet<>());

        float zoneSize = (float) L / M;

        // Add particle to its corresponding zone

        for (Particle p : particles) {
            // Get the zone number of the particle
            int x_zone = (int) (p.getX() / zoneSize);
            int y_zone = (int) (p.getY() / zoneSize);
            int z = y_zone * M + x_zone;

            particleZones.get(z).add(p);
        }

        // Find neighbours of all particles

        for (Particle p : particles) {
            // Create a set for storing particle neighbours
            particleNeighbours.put(p, new HashSet<>());

            // Get the zone number of the particle
            int x_zone = (int) (p.getX() / zoneSize);
            int y_zone = (int) (p.getY() / zoneSize);
            int z = y_zone * M + x_zone;

            // Check if particles on the near zones are neighbours

            List<Integer> nearZonesNonPeriodic = Arrays.asList(
                    z-(M+1), 	z-M,   z-(M-1),
                    z-1,      	z,     z+1,
                    z+(M-1), 	z+M,   z+(M+1)
            );

            List<Integer> nearZonesPeriodic = Arrays.asList(
                    z % M == 0 ? f((z-1), (M*M)) : z-(M+1),          f(z+(M-1)*M, (M*M)),    z % M == M-1 ? f((z+1-2*M), (M*M)) : z-(M-1),
                    z % M == 0 ? z-1+M : z-1,                        z,                         z % M == M-1 ? z+1-M : z+1,
                    z % M == 0 ? f((z-1+2*M), (M*M)) : z+(M-1),      f(z-(M-1)*M, (M*M)),    z % M == M-1 ? f((z+1), (M*M)) : z+(M+1)
            );

            (periodic ? nearZonesPeriodic : nearZonesNonPeriodic).forEach(nz -> {
                addZoneNeighbours(particleZones, particleNeighbours, p, rc, L, M, nz, periodic);
            });
        }

        return particleNeighbours;

    }

    public static void outputResult(Set<Particle> particles, Map<Particle, Set<Particle>> neighbours, int L) throws IOException {
        FileWriter writer = new FileWriter("output.csv");

        writer.write("ID,x,y,r,neighbours\n");

        Particle chosen = neighbours.entrySet().iterator().next().getKey();

        for (Particle particle : particles) {
            writer.write(particle.getId()+","+particle.getX()+","+particle.getY()+","+particle.getR()+",\""+Arrays.deepToString(neighbours.get(particle).toArray())+"\"");
//            if (neighbours.get(chosen).contains(particle)) {
//                writer.write('g');
//            } else if (particle.equals(chosen)) {
//                writer.write('b');
//            } else {
//                writer.write('r');
//            }
            writer.write('\n');
//            writer.write(particle.getId()+" 1 "+particle.getX()+" "+particle.getY()+" 0\n");
        }


        writer.close();
    }

}