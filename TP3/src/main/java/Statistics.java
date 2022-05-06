import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Statistics {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        Locale.setDefault(Locale.US);
        dcm();
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
            Result result = Simulation.run(N, T, new ArrayList<>(particlesClone),false);
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


    public static void dcm() throws FileNotFoundException, UnsupportedEncodingException {
        int N = 130;
        int I = 100;
        float T = (float) 0.6;

        double dt = 0.024;
        ArrayList<Double>[] smallPartDCMs = new ArrayList[(int) (T/dt) + 1];
        ArrayList<Double>[] bigPartDCMs = new ArrayList[(int) (T/dt) + 1];

        for(int s=0; s<T/dt; s++) {
            smallPartDCMs[s] = new ArrayList<>();
            bigPartDCMs[s] = new ArrayList<>();
        }

        for(int i=0; i<I; i++) {
            System.out.printf("Iteration %d\n", i);
            Result result = Simulation.run(N, T, true);

            List<State> states = result.getStates();
            State initState = states.get(0);

            for(State state : states) {
                int step = (int) (state.t / dt);
                if(state.t > T)
                    continue;

                double bigPartDCM = Math.pow(state.particles.get(0).x - initState.particles.get(0).x, 2)
                    + Math.pow(state.particles.get(0).y - initState.particles.get(0).y, 2);
                bigPartDCMs[step].add(bigPartDCM);

                double smallPartDCMAvg = 0;
                for(int p=1; p<state.particles.size(); p++) {
                    double smallPartDCM = Math.pow(state.particles.get(p).x - initState.particles.get(p).x, 2)
                        + Math.pow(state.particles.get(p).y - initState.particles.get(p).y, 2);
                    smallPartDCMAvg += smallPartDCM;
                }
                smallPartDCMs[step].add(smallPartDCMAvg / (N-1));
            }
        }

        PrintWriter writer = new PrintWriter("dcm.csv", String.valueOf(StandardCharsets.UTF_8));
        writer.println("t,b_avg,b_dev,s_avg,s_dev");

        for(int s=0; s<T/dt; s++) {
            double bAvg = bigPartDCMs[s].stream().reduce(Double::sum).orElse(0.0) / bigPartDCMs[s].size();
            double bDev = bigPartDCMs[s].stream().map(d -> Math.pow(d-bAvg, 2)).reduce(Double::sum).orElse(0.0);
            bDev = Math.sqrt(bDev / (bigPartDCMs[s].size() - 1));

            double sAvg = smallPartDCMs[s].stream().reduce(Double::sum).orElse(0.0) / smallPartDCMs[s].size();
            double sDev = smallPartDCMs[s].stream().map(d -> Math.pow(d-sAvg, 2)).reduce(Double::sum).orElse(0.0);
            sDev = Math.sqrt(sDev / (smallPartDCMs[s].size() - 1));

            writer.printf("%g,%g,%g,%g,%g\n", s*dt, bAvg, bDev, sAvg, sDev);
        }

        writer.close();
    }

}