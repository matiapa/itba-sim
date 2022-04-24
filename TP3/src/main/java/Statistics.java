import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class Statistics {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        bigParticle();
//        collisionTime();
    }


    public static void collisionTime() throws FileNotFoundException, UnsupportedEncodingException {
        int N = 110;
        int T = 100;

        while (N < 150) {
            System.out.println("Writing output file...");
            PrintWriter writer = new PrintWriter("collision_time_"+N+".csv", String.valueOf(StandardCharsets.UTF_8));

            Result result = Simulation.run(N, T, false);
            writer.println(String.format("%d %g", result.getStates().size() - 1, result.getT())); // borrar el primer estado
            writer.println("d");
            result.getStates().removeIf(s -> s.collision == null);
            result.getStates().forEach(state -> {
                writer.printf("%g\n", state.collision.getTimeTaken());
//                state.particles.forEach(p -> writer.printf("%g,%s,%g\n", state.t, p, state.collision.getTimeTaken()));
            });

            System.out.println("Finished writing sample for N: "+N);

            N += 15;

            writer.close();
        }
    }


    public static void bigParticle() throws FileNotFoundException, UnsupportedEncodingException {

        int N = 110;
        int T = 100;
        float increaseAmount = 0.2f;
        Result result = null;

        double Vm = 1;

        List<Particle> particles = Simulation.createParticles(N, Vm);

        System.out.println("Writing output file...");
        PrintWriter writer = new PrintWriter("big_particle.csv", String.valueOf(StandardCharsets.UTF_8));
        writer.println("t,id,x,y,K");


        while (Vm <= 2) {
            List<Particle> particlesClone = new ArrayList<>();

            particles.forEach(p -> particlesClone.add(new Particle(p.id, p.x, p.y, p.vx, p.vy, p.m, p.r)));

            System.out.println(particles.size());
            float finalK = (float) particlesClone.stream().mapToDouble(p -> 0.5*p.m*Math.pow(Math.hypot(p.vx, p.vy), 2)).average().orElse(0);

            result = Simulation.run(N, T, new ArrayList<>(particlesClone), Vm == 1 || Vm + increaseAmount > 2, "big_particle"+Math.round(Vm)+".csv");
            System.out.println("Finishes running...");

            result.getStates().forEach(state -> {
                state.particles.stream().filter(p -> p.id == 0).forEach(p -> writer.printf("%g,%d,%g,%g,%g\n", state.t, p.id, p.x, p.y, finalK));
            });
            System.out.println("Finished writing...");
            particles.forEach(p -> {
                if (p.vx < 0)
                    p.vx -= increaseAmount;
                else
                    p.vx += increaseAmount;

                if (p.vy < 0)
                    p.vy -= increaseAmount;
                else
                    p.vy += increaseAmount;
            });
            System.out.println(Vm);

            Vm += Math.sqrt(2*Math.pow(increaseAmount, 2));
        }

        writer.close();

    }
}
