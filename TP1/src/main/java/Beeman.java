import java.io.*;
import java.util.*;

import static java.lang.Math.*;

public class Beeman {

    // Fixed parameters

    static float L = 1f;
    static float W = 0.4f;
    static float min_y = L/10;
    static float m = 0.01f;
    static float kn = 100000;
    static float g = 9.81f;

    static float min_rad = 0.01f;
    static float max_rad = 0.015f;

    // Variable parameters

    static float Ap = 0;
    static float kt = 2*kn;
    static float dt = (float) (0.1 * sqrt(m/kn));
    static float tf = 4f;
    static int N = 200;

    static int fps = 48*4;
    static int anim_step = (int) (((float) 1/fps) / dt);

    // Derived parameters

    static float grid_size = max(L+min_y, W);
    static int zy_count = (int) ceil((L+min_y)/(2*max_rad));
    static int zx_count = (int) ceil(W/(2*max_rad));

    static List<Map<Particle, Set<Particle>>> neighbours_step = new ArrayList<>();

    public static Map<Particle, Set<Particle>> get_neighbours_at(List<Particle> particles, int step, boolean force) {

        if (step == -1 || force)
            return CIM.findNeighbours(new HashSet<>(particles), 0.0f, grid_size, max(zy_count, zx_count), false);

        if (step == neighbours_step.size())
            neighbours_step.add(CIM.findNeighbours(new HashSet<>(particles), 0.0f, grid_size, max(zy_count, zx_count), false));

        return neighbours_step.get(step);
    }


    public static void reallocate_particle(Particle p, List<Particle> particles, int step) {

        float x, y;

        particles.add(p);

        while (true) {
            y = (float) (L + min_y - L/2 + Math.random()*(L/2-p.r));
            x = (float) (p.r + Math.random()*(W-2*p.r));

            p.x = x;
            p.y = y;

//            boolean valid = true;
//            for (Particle p2 : particles) {
//                if (p.distanceTo(p2, L, false) <= 0 && !p.equals(p2)) {
//                    valid = false;
//                    break;
//                }
//            }
//
//            if (valid)
//                break;

            Map<Particle, Set<Particle>> neighbours = get_neighbours_at(particles, step, true);
            if (neighbours.get(p).size() == 0)
                break;
        }
        p.x = x;
        p.y = y;
        p.vx = 0;
        p.vy = 0;
    }


    public static Pair<Float> collision_force(float zeta_ij, float v_rel_x, float v_rel_y, float en_x, float en_y) {
        float et_x = -en_y;
        float et_y = en_x;

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
            float dist = (float) (Math.sqrt(Math.pow(p.x - n.x, 2) + Math.pow(p.y - n.y, 2)));
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

        if(p.x <= p.r && p.y >= 0) // collision with left wall
            force = collision_force(p.r-p.x, p.vx, p.vy, -1, 0);
        else if(W-p.x <= p.r && p.y >= 0) // collision with right wall
            force = collision_force(p.r-(W-p.x), p.vx, p.vy, 1, 0);
        else if(L + min_y - p.y <= p.r) // collision with upper wall
            force = collision_force(p.r-(L + min_y-p.y), p.vx, p.vy, 0, 1);
        else if(p.y - min_y <= p.r  && (p.x <= (W-Ap)/2 || p.x >= (W+Ap)/2)) // collision with bottom wall
            force = collision_force(p.r - (p.y - min_y), p.vx, p.vy, 0, -1);

        if(force != null) {
            force_x += force.x;
            force_y += force.y;
        }

        return new Pair<>(force_x, force_y);
    }


    public static List<List<Particle>> simulate(List<Particle> initial_particles) throws IOException {
        List<List<Particle>> p = new ArrayList<>();
        p.add(initial_particles);

        int unexpected_escapes = 0;

        List<Integer> rellocationQuantity = new ArrayList<>();

        for(int step=0; step < tf/dt-1; step++) {
            if(step % 4000 == 0)
                System.out.printf("Progress: %d%%%n", (int) (step/(tf/dt) * 100));

            p.add(new ArrayList<>());

            Map<Particle, Set<Particle>> neighbours = get_neighbours_at(p.get(step), step, false);

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


                // Reallocate escaped particles

                Particle particle = p.get(step+1).get(i);
                boolean expected_escape = particle.y <= particle.r || (particle.y < min_y && (particle.x > W || particle.x < 0));

                boolean unexpected_escape = !expected_escape && (particle.y > L + min_y || particle.x > W || particle.x < 0 || particle.y < 0);

                if (expected_escape || unexpected_escape) {
                    reallocated_particles.add(i);
                    unexpected_escapes += unexpected_escape ? 1 : 0;
                }


            }

            List<Particle> particlesCopy = new ArrayList<>(p.get(step + 1));
            for (Integer i : reallocated_particles) {
                particlesCopy.remove(p.get(step+1).get(i));
            }


            for (Integer i : reallocated_particles) {
                reallocate_particle(p.get(step+1).get(i), particlesCopy, step+1);
            }
            rellocationQuantity.add(reallocated_particles.size());

            neighbours = get_neighbours_at(p.get(step+1), step+1, true);

            for(int i=0; i<N; i++) {
                // Previous acceleration

                float a_prev_x = step>=1 ? p.get(step-1).get(i).ax : 0;
                float a_prev_y = step>=1 ? p.get(step-1).get(i).ay : -g;

                p.get(step+1).get(i).vx = p.get(step).get(i).vx + (float) 3/2 * p.get(step).get(i).ax * dt - (float) 1/2 * a_prev_x * dt;
                p.get(step+1).get(i).vy = p.get(step).get(i).vy + (float) 3/2 * p.get(step).get(i).ay * dt - (float) 1/2 * a_prev_y * dt;

                // Next acceleration (approx)

                Pair<Float> force = net_force(p.get(step+1), i, neighbours);

                float a_next_x = force.x / m;
                float a_next_y = force.y / m;

                // Next speed (second approx)

                p.get(step+1).get(i).vx = p.get(step).get(i).vx + (float) 1/3 * a_next_x * dt + (float) 5/6 * p.get(step).get(i).ax * dt - 1.f/6 * a_prev_x * dt;
                p.get(step+1).get(i).vy = p.get(step).get(i).vy + (float) 1/3 * a_next_y * dt + (float) 5/6 * p.get(step).get(i).ay * dt - 1.f/6 * a_prev_y * dt;

                // Reallocated particles have zero speed

                for(Integer j : reallocated_particles) {
                    p.get(step).get(j).ax = 0;
                    p.get(step).get(j).ay = -g;
                    p.get(step+1).get(j).vx = 0;
                    p.get(step+1).get(j).vy = 0;
                }
            }
        }

        System.out.println("Unexpected reinsertions: "+unexpected_escapes);

        FileWriter csv_fw = new FileWriter(String.format("reallocations_N%d_Ap%g_tf%g.csv", N, Ap, tf));
        csv_fw.write("t,q");
        for (int s = 0; s < p.size() - 1; s++) {
            if (rellocationQuantity.get(s) > 0)
                csv_fw.write(String.format("%g,%d\n", s*dt, rellocationQuantity.get(s)));
        }
        csv_fw.close();

        return p;
    }


    public static List<Particle> random_init(int N) {
        List<Particle> particles = new ArrayList<>();

        while (particles.size() < N) {
            // Create new particles

            for(int i=0; i<N; i++) {
                float d = (float) Math.random() * (max_rad-min_rad) + min_rad;
                float y = (float) (min_y + d + Math.random() * (L - 2*d));
                float x = (float) (d + Math.random()*(W-2*d));



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

        FileWriter csv_fw = new FileWriter(String.format("out_N%d_Ap%g_tf%g.csv", N, Ap, tf));
        FileWriter xyz_fw = new FileWriter(String.format("out_N%d_Ap%g_tf%g.xyz", N, Ap, tf));
        Locale.setDefault(Locale.US);

        csv_fw.write("t,id,x,y,vx,vy,r\n");

        for(int s=0; s<particles.size(); s+=anim_step) {
//        for(int s=0; s<particles.size(); s++) {
            xyz_fw.write(String.format("%d\n\n", N+6));
            xyz_fw.write(String.format("%d 0 %g 0 0 1e-15 255 255 255\n", N+1, 0.0));
            xyz_fw.write(String.format("%d 0 %g 0 0 1e-15 255 255 255\n", N+1, L+min_y));
            xyz_fw.write(String.format("%d %g %g 0 0 1e-15 255 255 255\n", N+1, W, 0.0));
            xyz_fw.write(String.format("%d %g %g 0 0 1e-15 255 255 255\n", N+1, W, L+min_y));
            xyz_fw.write(String.format("%d %g %g 0 0 0.01 255 0 0\n", N+1, (W-Ap)/2, min_y));
            xyz_fw.write(String.format("%d %g %g 0 0 0.01 255 0 0\n", N+1, (W+Ap)/2, min_y));

            for(Particle p : particles.get(s)) {
                xyz_fw.write(String.format("%d %g %g %g %g %g 0 0 0\n", p.id, p.x, p.y, p.vx, p.vy, p.r));
            }
        }
        xyz_fw.close();

        for (int s = 0; s < particles.size(); s++) {
            for(Particle p : particles.get(s)) {
                csv_fw.write(String.format("%g %d %g %g %g %g %g\n", s*dt, p.id, p.x, p.y, p.vx, p.vy, p.r));
            }
        }
        csv_fw.close();
    }

    public static void main(String[] args) throws IOException {
        List<Particle> initial_particles = random_init(N);

        List<List<Particle>> particles = simulate(initial_particles);

        save_simulation(particles);
    }

}