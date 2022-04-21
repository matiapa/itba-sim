import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class Statistics {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        bigParticle();
    }


    public static void collisionTime() throws FileNotFoundException, UnsupportedEncodingException {
        int N = 110;
        int T = 100;
        float M2 = 2.0f;

        System.out.println("Writing output file...");
        PrintWriter writer = new PrintWriter("big_particle.csv", String.valueOf(StandardCharsets.UTF_8));
        writer.println("t,id,x,y,vx,vy");

        while (N < 150) {

        }
    }


    public static void bigParticle() throws FileNotFoundException, UnsupportedEncodingException {

        int N = 140;
        int T = 100;
        float M2 = 2.0f;
        float K;
        float increaseAmount = 0.2f;

        double Vm = 1;

        List<Particle> particles = Simulation.createParticles(N, Vm);

        System.out.println("Writing output file...");
        PrintWriter writer = new PrintWriter("big_particle.csv", String.valueOf(StandardCharsets.UTF_8));
        writer.println("t,id,x,y,K");


        while (Vm <= 2) {
            K = (float) (0.5 * M2 * Math.pow(Vm, 2));
            List<Particle> particlesClone = new ArrayList<>();
            particles.forEach(p -> particlesClone.add(new Particle(p.id, p.x, p.y, p.vx, p.vy, p.m, p.r)));
            Result result = Simulation.run(N, T, new ArrayList<>(particlesClone));
            System.out.println("Finishes running...");
            float finalK = K;
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
