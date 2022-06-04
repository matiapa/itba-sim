import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.*;

public class Beeman {

    // Fixed parameters

    static float L = 1;
    static float W = 0.4f;
    static float min_y = -L/10;
    static float m = 0.01f;
    static float kn = 100000;
    static float g = 9.81f;

    static float min_rad = 0.01f;
    static float max_rad = 0.015f;

    // Variable parameters

    static float Ap = 0.0f;
    static float kt = 2*kn;
    static float dt = (float) (0.1 * sqrt(m/kn));
    static float tf = 0.2f;
    static int N = 200;

    static int fps = 48*4;
    static int anim_step = (int) (((float) 1/fps) / dt);

    // Derived parameters

    static float grid_size = max(L-min_y, W);
    static int zy_count = (int) ceil((L-min_y)/(2*max_rad));
    static int zx_count = (int) ceil( W/(2*max_rad));

    static List<Map<Particle, Set<Particle>>> neighbours_step = new ArrayList<>();

    public static Map<Particle, Set<Particle>> get_neighbours_at(List<Particle> particles, int step) {
        if (step == -1)
            return CIM.findNeighbours(new HashSet<>(particles), 0.0f, grid_size, max(zy_count, zx_count), false);

        if (step == neighbours_step.size())
            neighbours_step.add(CIM.findNeighbours(new HashSet<>(particles), 0.0f, grid_size, max(zy_count, zx_count), false));

        return neighbours_step.get(step);
    }


    public static void reallocate_particle(Particle p, List<Particle> particles) {
        float x, y;
        while(true) {
            x = (float) random() * (W - 2*p.r) + p.r;
            y = (float) random() * (L/2 - p.r) + L/2;

            float finalX = x, finalY = y;
            boolean valid = particles.stream().allMatch((p2) -> hypot(finalX - p2.x, finalY - p2.y) > p.r + p2.r);

            if (valid)
                break;
        }
        p.x = x;
        p.y = y;
        p.vx = 0;
        p.vy = 0;
    }


    public static Pair<Float> collision_force(float zeta_ij, float v_rel_x, float v_rel_y, float en_x, float en_y) {
        float et_x = -en_y;
        float et_y = et_x;

        float fn = -kn * zeta_ij;
        float ft = -kt * zeta_ij * (v_rel_x * et_x + v_rel_y * et_y);

        float fx = fn * en_x - ft * en_y;
        float fy = fn * en_y + ft * en_x;

        return new Pair<>(fx, fy);
    }


    public static Pair<Float> net_force(List<Particle> particles, int i, Map<Particle, Set<Particle>> neighbours){
        float force_x = 0;
        float force_y = -m*g;

        // Force due to contact with particles

        Particle p = particles.get(i);

        for(Particle n : neighbours.get(p)) {
            float dist = (float) hypot(p.x - n.x, p.y - n.y);
            float zeta_ij = p.r + n.r - dist;

            float v_rel_x = p.vx - n.vx;
            float v_rel_y = p.vy - n.vy;

            float en_x = (n.x - p.x) / dist;
            float en_y = (n.y - p.y) / dist;

            Pair<Float> force = collision_force(zeta_ij, v_rel_x, v_rel_y, en_x, en_y);

            force_x += force.x;
            force_y += force.y;
        }

        // Force due to contact with walls

        Pair<Float> force = null;

        if(p.x <= p.r && p.y >= 0)
            force = collision_force(p.r-p.x, p.vx, p.vy, -1, 0);
        else if(W-p.x <= p.r && p.y >= 0)
            force = collision_force(p.r-(W-p.x), p.vx, p.vy, 1, 0);
        else if(L - p.y <= p.r)
            force = collision_force(p.r-(L-p.y), p.vx, p.vy, 0, 1);
        else if(p.y <= p.r  && (p.x <= (W-Ap)/2 || p.x >= (W+Ap)/2))
            force = collision_force(p.r-(p.y), p.vx, p.vy, 0, -1);

        if(force != null) {
            force_x += force.x;
            force_y += force.y;
        }

        return new Pair<>(force_x, force_y);
    }


    public static List<List<Particle>> simulate(List<Particle> initial_particles) {
        List<List<Particle>> p = new ArrayList<>();
        p.add(initial_particles);

        int unexpected_escapes = 0;

        for(int step=0; step < tf/dt-1; step++) {
            if(step % 100 == 0)
                System.out.printf("Progress: %d%%%n", (int) (step/(tf/dt) * 100));

            p.add(new ArrayList<>());

            Map<Particle, Set<Particle>> neighbours = get_neighbours_at(p.get(step), step);
            List<Integer> reallocated_particles = new ArrayList<>();

            for(int i=0; i<N; i++) {
                p.get(step+1).add(new Particle(p.get(step).get(i)));

                // Get current acceleration

                Pair<Float> force = net_force(p.get(step), i, neighbours);
                p.get(step).get(i).ax = force.x / m;
                p.get(step).get(i).ay = force.y / m;

                // Get previous acceleration

                float a_prev_x = step>=1 ? p.get(step-1).get(i).ax : 0;
                float a_prev_y = step>=1 ? p.get(step-1).get(i).ay : -g;

                // Get next position

                p.get(step+1).get(i).x = p.get(step).get(i).x + p.get(step).get(i).vx*dt + (float) 2/3 * p.get(step).get(i).ax * dt*dt - (float) 1/6 * a_prev_x * dt*dt;
                p.get(step+1).get(i).y = p.get(step).get(i).y + p.get(step).get(i).vy*dt + (float) 2/3 * p.get(step).get(i).ay * dt*dt - (float) 1/6 * a_prev_y * dt*dt;

                // Get next speed (first approx)

                p.get(step+1).get(i).vx = p.get(step).get(i).vx + (float) 3/2 * p.get(step).get(i).ax * dt - (float) 1/2 * a_prev_x * dt;
                p.get(step+1).get(i).vy = p.get(step).get(i).vy + (float) 3/2 * p.get(step).get(i).ay * dt - (float) 1/2 * a_prev_y * dt;

                // Reallocate escaped particles

                boolean expected_escape = p.get(step+1).get(i).y <= min_y
                        || (p.get(step+1).get(i).y > min_y && p.get(step+1).get(i).y < 0
                        && (p.get(step+1).get(i).x > W || p.get(step+1).get(i).x < 0));

                boolean unexpected_escape = !expected_escape &&
                        (p.get(step+1).get(i).y > L || p.get(step+1).get(i).x > W || p.get(step+1).get(i).x < 0);

                if(expected_escape || unexpected_escape) {
                    reallocate_particle(p.get(step+1).get(i), p.get(step+1));
                    reallocated_particles.add(i);
                    unexpected_escapes += unexpected_escape ? 1 : 0;
                }
            }

            neighbours = get_neighbours_at(p.get(step+1), step+1);

            for(int i=0; i<N; i++) {
                // Previous acceleration

                float a_prev_x = step>=1 ? p.get(step-1).get(i).ax : 0;
                float a_prev_y = step>=1 ? p.get(step-1).get(i).ay : -g;

                // Next acceleration (approx)

                Pair<Float> force = net_force(p.get(step+1), i, neighbours);

                float a_next_x = force.x / m;
                float a_next_y = force.y / m;

                // Next speed (second approx)

                p.get(step+1).get(i).vx = p.get(step).get(i).vx + (float) 1/3 * a_next_x * dt + (float) 5/6 * p.get(step).get(i).ax * dt - 1/6 * a_prev_x * dt;
                p.get(step+1).get(i).vy = p.get(step).get(i).vy + (float) 1/3 * a_next_y * dt + (float) 5/6 * p.get(step).get(i).ay * dt - 1/6 * a_prev_y * dt;

                // Reallocated particles have zero speed

                for(Integer j : reallocated_particles) {
                    p.get(step).get(j).ax = 0;
                    p.get(step).get(j).ay = -g;
                    p.get(step+1).get(j).vx = 0;
                    p.get(step+1).get(j).vy = 0;
                }
            }
        }

        return p;
    }


    public static List<Particle> random_init(int N) {
        List<Particle> particles = new ArrayList<>();

        while (particles.size() < N) {
            // Create new particles

            for(int i=0; i<N; i++) {
                float d = (float) Math.random() * (max_rad-min_rad) + min_rad;
                float x = (float) Math.random() * (W-2*d) + d;
                float y = (float) Math.random() * (L-2*d) + d;
                particles.add(new Particle(i, x, y, d));
            }

            // Remove overlapping ones

            List<Particle> new_particles = new ArrayList<>();
            Set<Particle> avoid = new HashSet<>();

            Map<Particle, Set<Particle>> neighbours = CIM.findNeighbours(new HashSet<>(particles), 0.0f, grid_size, max(zy_count, zx_count), false);

            for(Particle p1 : neighbours.keySet()) {
                if(!avoid.contains(p1))
                    new_particles.add(p1);
                avoid.addAll(neighbours.get(p1));
            }

            particles = new_particles;
        }

        if(particles.size() > N)
            particles = particles.subList(0, N);

        return particles;
    }


    public static void save_simulation(List<List<Particle>> particles) throws IOException {
        System.out.println("Writing files");

        FileWriter csv_fw = new FileWriter("out.csv");
        FileWriter xyz_fw = new FileWriter("out.xyz");
        Locale.setDefault(Locale.US);

        csv_fw.write("t,id,x,y,vx,vy,r\n");

        for(int s=0; s<particles.size(); s+=anim_step) {
            xyz_fw.write(String.format("%d\n\n", N+6));
            xyz_fw.write(String.format("%d 0 %g 0 0 1e-15 255 255 255\n", N+1, min_y));
            xyz_fw.write(String.format("%d 0 %g 0 0 1e-15 255 255 255\n", N+1, L));
            xyz_fw.write(String.format("%d %g %g 0 0 1e-15 255 255 255\n", N+1, W, min_y));
            xyz_fw.write(String.format("%d %g %g 0 0 1e-15 255 255 255\n", N+1, W, L));
            xyz_fw.write(String.format("%d %g 0 0 0 0.01 255 0 0\n", N+1, (W-Ap)/2));
            xyz_fw.write(String.format("%d %g 0 0 0 0.01 255 0 0\n", N+1, (W+Ap)/2));

            for(Particle p : particles.get(s)) {
                xyz_fw.write(String.format("%d %g %g %g %g %g 0 0 0\n", p.id, p.x, p.y, p.vx, p.vy, p.r));
                csv_fw.write(String.format("%g %d %g %g %g %g %g\n", s*dt, p.id, p.x, p.y, p.vx, p.vy, p.r));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        List<Particle> initial_particles = random_init(N);

        List<List<Particle>> particles = simulate(initial_particles);

        save_simulation(particles);
    }

}