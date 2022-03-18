import java.util.Objects;

public class Particle {

    private final int id;
    private final float x;
    private final float y;
    private final float r;

    public Particle(int id, float x, float y, float r) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public double distanceTo(Particle o) {
        double dist = Math.hypot((getX() - o.getX()), (getY() - o.getY())) - (r + o.getR());
        return dist > 0 ? dist : 0;
    }

    public double periodicDistanceTo(Particle o, int L) {
        double normalDist = distanceTo(o);

        double periodicDistX, periodicDistY;
        if(x < o.getX())
            periodicDistX = x + L - o.getX();
        else if(x > o.getX())
            periodicDistX = o.getX() + L - x;
        else
            periodicDistX = 0;

        if(y < o.getY())
            periodicDistY = y + L - o.getY();
        else if(y > o.getY())
            periodicDistY = o.getY() + L - y;
        else
            periodicDistY = 0;

        double periodicDist = Math.hypot(periodicDistX, periodicDistY) - (r + o.getR());
        periodicDist = periodicDist > 0 ? periodicDist : 0;

        return Math.min(normalDist, periodicDist);
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
        return String.valueOf(id);
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