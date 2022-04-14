public class ParticleCollision extends Collision {

    Particle p1, p2;

    public ParticleCollision(Particle p1, Particle p2, float t) {
        super(t);

        if(t < 0) {
            System.out.printf("t: %g\n", t);
            System.out.printf("P1: %s\n", p1);
            System.out.printf("P2: %s\n", p2);
        }

        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public boolean involves(Particle p) {
        return p1 == p || p2 == p;
    }

    @Override
    public void operate() {
        float dx = p2.x-p1.x,    dy = p2.y-p1.y;
        float dvx = p2.vx-p1.vx, dvy = p2.vy-p1.vy;

        float dv_dr = dvx * dx + dvy * dy;
        float s = p1.r + p2.r;

        float J = 2 * p1.m * p2.m * dv_dr / (s * (p1.m + p2.m));
        float Jx = J * dx / s;
        float Jy = J * dy / s;

        p1.vx += Jx/p1.m; p1.vy += Jy/p1.m;
        p2.vx -= Jx/p2.m; p2.vy -= Jy/p2.m;

        // If numbers are not limited in precision, numerical errors lead to undesired behaviour
        // For example, a particle moving in y=x, may end up moving in y=x+n*eps.
        p1.vx = (float) Math.round(p1.vx * 1000) / 1000;
        p1.vy = (float) Math.round(p1.vy * 1000) / 1000;
        p2.vx = (float) Math.round(p2.vx * 1000) / 1000;
        p2.vy = (float) Math.round(p2.vy * 1000) / 1000;
    }

    @Override
    public String toString() {
        return String.format("P%d P%d", p1.id, p2.id);
    }
}
