import java.util.*;

public class CIM {

    static void addZoneNeighbours(
        ArrayList<Set<Particle>> particleZones, Map<Particle, Set<Particle>> particleNeighbours,
        Particle p, int rc, int M, int zone
    ) {
        if(zone >=0 && zone < M*M) {
            for (Particle c : particleZones.get(zone)) {
                if (p.distanceTo(c) <= rc)
                    particleNeighbours.get(p).add(c);
            }
        }
    }

    static Map<Particle, Set<Particle>> findNeighbours(Set<Particle> particles, int rc, int L, int M) {

        ArrayList<Set<Particle>> particleZones = new ArrayList<>();
        Map<Particle, Set<Particle>> particleNeighbours = new HashMap<>();

        // Add empty sets to particle zones
        for(int i=0; i<M*M; i++)
            particleZones.add(new HashSet<>());

        float zoneSize = (float) L / M;

        for (Particle p : particles) {
            // Create a set for storing particle neighbours
            particleNeighbours.put(p, new HashSet<>());

            // Get the zone number of the particle
            int x_zone = (int) (p.getX() / zoneSize);
            int y_zone = (int) (p.getY() / zoneSize);
            int z = y_zone * M + x_zone;

            // Check if particles on the near zones are neighbours
            List<Integer> nearZones = Arrays.asList(z+1, z-1, z+M, z-M, z+(M+1), z-(M+1), z+(M-1), z-(M-1));
            nearZones.forEach(nz -> {
                addZoneNeighbours(particleZones, particleNeighbours, p, rc, M, nz);
            });

            // Add particle to its corresponding zone
            particleZones.get(z).add(p);
        }

        return particleNeighbours;

    }

}
