import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Main {

    private static final String OUT_FILE_NAME = "stats.txt";
    private static int N = 100;
    private static int T = 10;

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

//         List<Particle> particles = Arrays.asList(
//             new Particle(0, 3, 3.5f, 2.5f, 0, 1, 0.5f),    // tc = 1, 3, 5, 7
//             new Particle(1, 2, 1.5f, -2, 0, 1, 1)          // tc = 0.5, 2.5, 4.5, 6.5
//         );

//        List<Particle> particles = Arrays.asList(
//            new Particle(0, 0.5f, 0.5f, 2.5f, 2.5f, 1, 0.5f)    // tc = 0, 2, 4, 6...
//        );

//        List<Particle> particles = Arrays.asList(
//                new Particle(0, 3, 2, 0, 1, 1, 0.5f),
//                new Particle(1, 3, 4, 0, -1, 1, 0.5f)
//        );

//         List<Particle> particles = Arrays.asList(
//             new Particle(0, 2, 2, 1, 1, 1, 0.1f),
//             new Particle(1, 4, 4, -1, -1, 1, 0.1f)
//         );

        System.out.println("Running simulation...");
        Result result = Simulation.run(N, T, true);

        System.out.println("Writing output file...");
        PrintWriter writer = new PrintWriter(OUT_FILE_NAME, String.valueOf(StandardCharsets.UTF_8));
        writer.println(String.format("%d %g", result.getStates().size() - 1, result.getT())); // borrar el primer estado
        writer.println("t,id,x,y,vx,vy,r,d");
        result.getStates().forEach(state -> {
            state.particles.forEach(p -> writer.printf("%g,%s,%g\n", state.t, p, state.collision == null ? 0 : state.collision.getTimeTaken()));
        });
        writer.close();
    }

}
