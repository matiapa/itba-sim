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
