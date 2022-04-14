import java.util.List;

public class State {

    float t;
    List<Particle> particles;

    public State(float t, List<Particle> particles) {
        this.t = t;
        this.particles = particles;
    }

}
