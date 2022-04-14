import java.util.List;

public class State {

    float t;
    List<Particle> particles;
    Collision collision;

    public State(float t, List<Particle> particles, Collision collision) {
        this.t = t;
        this.particles = particles;
        this.collision = collision;
    }

}
