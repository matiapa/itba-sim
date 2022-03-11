import java.util.Objects;

public class Particle {

    private int id;
    private int x;
    private int y;
    private int r;

    public Particle(int id, int x, int y, int r) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public double distanceTo(Particle o) {
        double dist = Math.hypot((getX() - o.getX()), (getY() - o.getY())) - (r + o.getR());
        return dist > 0 ? dist : 0;
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
        return ""+id;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getR() {
        return r;
    }

}