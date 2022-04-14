public class WallCollision extends Collision {

    Particle particle;
    Wall wall;

    public WallCollision(Particle particle, Wall wall, float t) {
        super(t);
        this.particle = particle;
        this.wall = wall;
    }

    @Override
    public boolean involves(Particle p) {
        return particle == p;
    }

    @Override
    public void operate() {
        if(wall == WallCollision.Wall.UPPER || wall == WallCollision.Wall.LOWER)
            particle.vy *= -1;
        else
            particle.vx *= -1;
    }

    public enum Wall{UPPER, LOWER, LEFT, RIGHT}

    @Override
    public String toString() {
        return String.format("P%d %s", particle.id, wall);
    }

}
