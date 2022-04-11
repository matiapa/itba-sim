import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Statistic {

    static Set<Particle> createParticleSet(int N, int L, float r) {
        Set<Particle> particles = new HashSet<>();

        for(int id=0; id<N; id++) {
            float x = (float) Math.random() * L;
            float y = (float) Math.random() * L;
            particles.add(new Particle(id++, x, y, r));
        }

        return particles;
    }

    public static String runExperiment(int L, float rc, float r, int N, int M) {

        float avgCIMTime = 0, avgBFTime = 0;
        int samples = 100;

        for(int i=0; i<samples; i++) {
            Set<Particle> particles = createParticleSet(N, L, r);

            long startTime = System.currentTimeMillis();
            CIM.findNeighbours(particles, rc, L, M, false);
            long endTime = System.currentTimeMillis();

            avgCIMTime += endTime - startTime;

            startTime = System.currentTimeMillis();
            CIM.findNeighbours(particles, rc, L, 1, false);
            endTime = System.currentTimeMillis();

            avgBFTime += endTime - startTime;
        }

        return String.format(Locale.US, "%d %d %f %f", N, M, avgCIMTime/samples, avgBFTime/samples);
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        int L = 20;
        int rc = 1;
        float r = (float) 0.25;

        int minN = Integer.parseInt(args[0]);
        int stepN = Integer.parseInt(args[1]);
        int maxN = Integer.parseInt(args[2]);
        String filename = args[3];

        PrintWriter writer = new PrintWriter(filename, "UTF-8");

        writer.println("N M cimTime bfTime");
        for(int N = minN; N <= maxN; N += stepN) {
            for(int M=2; M < L/rc; M++) {
                String line = runExperiment(L, rc, r, N, M);
                writer.println(line);
            }
        }
        writer.close();
    }

}
