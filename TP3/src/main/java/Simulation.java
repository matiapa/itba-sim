import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Simulation {

    private static final int L = 6, Vm = 2;
    private static final float R1 = 0.2f, M1 = 0.9f;
    private static final float R2 = 0.7f, M2 = 2.0f;
    private static final float dT = 0.05f;
    private static Particle bigParticle = new Particle(0, (float) L/2, (float) L/2, 0, 0, M2, R2);

    private static float t = 0;

    public static Result run(int N, float T, boolean animate) throws FileNotFoundException, UnsupportedEncodingException {
        return run(N, T, null, animate, "out.txt");
    }

    public static Result run(int N, float T, List<Particle> initialParticles, boolean animate, String fileName) throws FileNotFoundException, UnsupportedEncodingException {
        t = 0;
        float writeT = 0;
        bigParticle = new Particle(0, (float) L/2, (float) L/2, 0, 0, M2, R2);
        Queue<Collision> collisions = new PriorityQueue<>();
        List<State> states = new ArrayList<>();

        List<Particle> particles = initialParticles != null ? initialParticles : createParticles(N);
        saveState(states, particles, null);

        PrintWriter writer = null;
        if (animate) {
            writer = new PrintWriter(fileName, String.valueOf(StandardCharsets.UTF_8));
            writer.printf("%d %d %d %g", N, L, Vm, dT);
            writeState(writer, particles);
            writeT += dT;
        }

        // Calculate initial collisions
        for(int i=0; i<particles.size()-1; i++)
            addParticleCollisions(particles.get(i), particles.subList(i + 1, particles.size()), collisions);
        for (Particle particle : particles)
            addWallCollisions(particle, collisions);

        while(t < T && !collisions.isEmpty()) {
            // Get the nearest in time collision
            Collision collision = collisions.poll();
            if(collision == null)
                break;

            // Evolve particles to collision time
            for(Particle p : particles) {
                p.x += p.vx * (collision.t - t);
                p.y += p.vy * (collision.t - t);
            }
            collision.setTimeTaken(collision.t - t);
            t = collision.t;

            // Invalidate future collisions that have the colliding particles involved
            if(collision instanceof ParticleCollision) {
                ParticleCollision c = (ParticleCollision) collision;
                collisions.removeIf(fc -> fc.involves(c.p1) || fc.involves(c.p2));
            } else if(collision instanceof WallCollision) {
                WallCollision c = (WallCollision) collision;

                // Check if big particle has collided with wall
                if (c.particle.equals(bigParticle))
                    return new Result(t, states);

                collisions.removeIf(fc -> fc.involves(c.particle));
            }

            // Save state to file
            if (t >= writeT && animate) {
                writeT += dT;
                writeState(writer, particles);
            }

            // Operate the current collision
            collision.operate();

            // Add new collisions
            if(collision instanceof ParticleCollision) {
                ParticleCollision c = (ParticleCollision) collision;
                addParticleCollisions(c.p1, particles, collisions);
                addParticleCollisions(c.p2, particles, collisions);
                addWallCollisions(c.p1, collisions);
                addWallCollisions(c.p2, collisions);
            } else if(collision instanceof WallCollision) {
                WallCollision c = (WallCollision) collision;
                addParticleCollisions(c.particle, particles, collisions);
                addWallCollisions(c.particle, collisions);
            }

            // Save the state of the system
            if(collisions.peek() != null && collisions.peek().t != t)
                saveState(states, particles, collision);
        }

        if (writer != null)
            writer.close();
        return new Result(t, states);
    }

    public static List<Particle> createParticles(int N) {
        return createParticles(N, Vm);
    }

    public static List<Particle> createParticles(int N, double vMax) {
        List<Particle> particles = new ArrayList<>();
        Random rand = new Random();

        particles.add(bigParticle);

        for(int n=1; n<N; n++) {
            float x, y; boolean superposition;
            do {
                x = (float) Math.random() * L;
                y = (float) Math.random() * L;

                superposition = checkSuperposition(particles, x, y, R1);
            } while(superposition);

            double angle = Math.PI*rand.nextDouble();
            double vm = vMax*rand.nextDouble();
            float vx = (float) (Math.cos(angle)*vm);
            float vy = (float) (Math.sin(angle)*vm);

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
            if (p1.equals(p2)) continue;

            float dx = p2.x-p1.x,    dy = p2.y-p1.y;
            float dvx = p2.vx-p1.vx, dvy = p2.vy-p1.vy;
            float s = p1.r+p2.r;

            float dv_dr = dvx * dx + dvy * dy;
            float dv_dv = dvx*dvx + dvy*dvy;
            float dr_dr = dx*dx + dy*dy;
            float d = dv_dr * dv_dr - dv_dv * (dr_dr - s*s);

            if(dv_dr < 0 && d>=0) {
                float tc = (float) - (dv_dr + Math.sqrt(d)) / dv_dv;
                collisions.add(new ParticleCollision(p1, p2, t+tc));
            }
        }
    }

    private static void addWallCollisions(Particle p, Queue<Collision> collisions) {
        // Add collisions with the walls
        if(p.vy > 0)
            collisions.add(new WallCollision(p, WallCollision.Wall.UPPER, t+(L-p.y-p.r)/p.vy));
        if(p.vy < 0)
            collisions.add(new WallCollision(p, WallCollision.Wall.LOWER, t-(p.y-p.r)/p.vy));
        if(p.vx > 0)
            collisions.add(new WallCollision(p, WallCollision.Wall.RIGHT, t+(L-p.x-p.r)/p.vx));
        if(p.vx < 0)
            collisions.add(new WallCollision(p, WallCollision.Wall.LEFT, t-(p.x-p.r)/p.vx));
    }

    private static void saveState(List<State> states, List<Particle> particles, Collision collision) {
        particles = particles.stream().map(p -> new Particle(p.id, p.x, p.y, p.vx, p.vy, p.m, p.r))
                .collect(Collectors.toList());
        states.add(new State(t, particles, collision));
    }

    private static void writeState(PrintWriter writer, List<Particle> particles) {
        StringBuilder str = new StringBuilder();
        str.append(String.format("\n~\n%.7E", t));
        particles.forEach(p -> str.append(String.format("\n%d %.7E %.7E %.7E %.7E %.7E", p.id, p.x, p.y, p.vx, p.vy, p.r)));
        writer.write(str.toString());
        writer.flush();
    }

}