import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Main {

    private static final String OUT_FILE_NAME = "out.csv";
    private static int N = 100;
    private static int T = 6;

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

        // List<Particle> particles = Arrays.asList(
        //     new Particle(0, 3, 3.5f, 2.5f, 0, 1, 0.5f),    // tc = 1, 3, 5, 7
        //     new Particle(1, 2, 1.5f, -2, 0, 1, 1)          // tc = 0.5, 2.5, 4.5, 6.5
        // );

        // List<Particle> particles = Arrays.asList(
        //     new Particle(0, 3, 3.5f, 2.5f, 0, 1, 0.5f),    // tc = 1, 3, 5, 7
        //     new Particle(1, 2, 1.5f, -2, 0, 1, 1)          // tc = 0.5, 2.5, 4.5, 6.5
        // );

        List<Particle> particles = Arrays.asList(
            new Particle(0, 0.5f, 0.5f, 2.5f, 2.5f, 1, 0.5f)    // tc = 1, 3, 5, 7
        );

        System.out.println("Running simulation...");
        List<State> states = Simulation.run(particles.size(), T, particles);

        System.out.println("Writing output file...");
        PrintWriter writer = new PrintWriter(OUT_FILE_NAME, StandardCharsets.UTF_8);
        writer.println("t,id,x,y,vx,vy");
        states.forEach(state -> {
            state.particles.forEach(p -> writer.printf("%g,%s\n", state.t, p));
        });
        writer.close();
    }

}
