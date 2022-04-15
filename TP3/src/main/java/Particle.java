import java.util.Objects;

public class Particle {

    int id;
    float x, y;
    float vx, vy;
    float m, r;

    public Particle(int id, float x, float y, float vx, float vy, float m, float r) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.m = m;
        this.r = r;
    }

    public Particle (int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return id == particle.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%d,%g,%g,%g,%g,%g", id, x, y, vx, vy, r);
    }
}