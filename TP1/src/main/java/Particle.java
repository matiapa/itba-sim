import java.util.Objects;

public class Particle {

    public final int id;
    public float x;
    public float y;
    public final float r;
    public float vx, vy, ax, ay;

    public Particle(int id, float x, float y, float r) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public Particle(Particle p) {
        this.id = p.id;
        this.x = p.x;
        this.y = p.y;
        this.r = p.r;
        this.vx = p.vx;
        this.vy = p.vy;
        this.ax = p.ax;
        this.ay = p.ay;
    }

    public double distanceTo(Particle o, float L, boolean usePeriodic) {
        double distX = Math.abs(x - o.getX());

        if(usePeriodic) {
            double pDistX = 0;
            if(x < o.getX())
                pDistX = x + L - o.getX();
            else if(x > o.getX())
                pDistX = o.getX() + L - x;
            if(pDistX < distX)
                distX = pDistX;
        }

        double distY = Math.abs(y - o.getY());

        if(usePeriodic) {
            double pDistY = 0;
            if(y < o.getY())
                pDistY = y + L - o.getY();
            else if(y > o.getY())
                pDistY = o.getY() + L - y;
            if(pDistY < distY)
                distY = pDistY;
        }

        double dist = Math.hypot(distX, distY) - (r + o.getR());

        return Math.max(dist, 0);
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
        return String.format("(x=%g, y=%g, vx=%g, vy=%g)", x, y, vx, vy);
    }

    public int getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getR() {
        return r;
    }

}