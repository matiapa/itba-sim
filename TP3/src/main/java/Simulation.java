import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Simulation {

    private static final String OUT_FILE_NAME = "out.csv";
    private static final int L = 6, Vm = 2;
    private static final float R1 = 0.2f, M1 = 0.9f;
    private static final float R2 = 0.7f, M2 = 2.0f;

    private static float t = 0;

    public static void run(int N, float T) throws IOException {
        List<Particle> particles = createParticles(N);
        Queue<Collision> collisions = new PriorityQueue<>();

        // Calculate initial collisions
        for(int i=0; i<particles.size()-1; i++)
            addParticleCollisions(particles.get(i), particles.subList(i + 1, particles.size()), collisions);
        for (Particle particle : particles)
            addWallCollisions(particle, collisions);

        while(t < T) {
            // Get the nearest in time collision
            Collision collision = collisions.poll();
            if(collision == null)
                break;

            // Evolve particles to collision time
            for(Particle p : particles) {
                p.x += p.vx * (collision.t - t);
                p.y += p.vy * (collision.t - t);
            }
            t = collision.t;

            // Save the state of the system
            PrintWriter writer = new PrintWriter(OUT_FILE_NAME, StandardCharsets.UTF_8);
            writer.println("t,id,x,y,vx,vy,m,r");
            particles.forEach(p -> writer.printf("%g,%s\n", t, p));

            // Invalidate future collisions that have the colliding particles involved
            if(collision instanceof ParticleCollision c) {
                collisions = collisions.stream().filter(fc -> fc.involves(c.p1) || fc.involves(c.p2))
                    .collect(Collectors.toCollection(PriorityQueue::new));
            } else if(collision instanceof WallCollision c) {
                collisions = collisions.stream().filter(fc -> fc.involves(c.particle))
                    .collect(Collectors.toCollection(PriorityQueue::new));
            }

            // Operate the current collision
            collision.operate();
        }
    }

    private static List<Particle> createParticles(int N) {
        List<Particle> particles = new ArrayList<>();

        particles.add(new Particle(0, (float) L/2, (float) L/2, 0, 0, M2, R2));

        for(int n=1; n<N; n++) {
            float x, y; boolean superposition;
            do {
                x = (float) Math.random() * L;
                y = (float) Math.random() * L;
                superposition = checkSuperposition(particles, x, y, R1);
            } while(!superposition);

            float vx = (float) Math.random() * 2*Vm - Vm;
            float vy = (float) Math.random() * 2*Vm - Vm;

            particles.add(new Particle(n, x, y, vx, vy, M1, R1));
        }

        return particles;
    }

    private static boolean checkSuperposition(List<Particle> particles, float x, float y, float r) {
        // Check superposition with walls
        if(x<r || (L-x)<r || y<r || (L-y)<r)
            return true;

        // Check superposition with other particles
        for(Particle p : particles)
            if(Math.hypot(x-p.x, y-p.y) <= r + p.r)
                return true;

        return false;
    }

    private static void addParticleCollisions(Particle p1, List<Particle> particles, Queue<Collision> collisions) {
        // Add collisions with other particles
        for(Particle p2 : particles) {
            float dv_dr = (p1.vx - p2.vx) * (p1.x - p2.x) + (p1.vy - p2.vy) * (p1.y - p2.y);
            float dv_dv = (p1.vx - p2.vx) * (p1.vx - p2.vx) + (p1.vy - p2.vy) * (p1.vy - p2.vy);
            float dr_dr = (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
            float d = dv_dr * dv_dr - dv_dv * (dr_dr - (p1.r + p2.r)*(p1.r + p2.r));

            if(dv_dr < 0 && d>=0) {
                float tc = (float) - (dv_dr + Math.sqrt(d)) / dv_dv;
                collisions.add(new ParticleCollision(p1, p2, tc));
            }
        }
    }

    private static void addWallCollisions(Particle p, Queue<Collision> collisions) {
        // Add collisions with the walls
        if(p.vy > 0)
            collisions.add(new WallCollision(p, WallCollision.Wall.UPPER, (L-p.y-p.r)/p.vy));
        if(p.vy < 0)
            collisions.add(new WallCollision(p, WallCollision.Wall.LOWER, -(p.y-p.r)/p.vy));
        if(p.vx > 0)
            collisions.add(new WallCollision(p, WallCollision.Wall.RIGHT, (L-p.x-p.r)/p.vx));
        if(p.vx < 0)
            collisions.add(new WallCollision(p, WallCollision.Wall.LEFT, -(p.x-p.r)/p.vx));
    }

}
