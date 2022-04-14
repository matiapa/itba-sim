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
        float dv_dr = (p1.vx - p2.vx) * (p1.x - p2.x) + (p1.vy - p2.vy) * (p1.y - p2.y);
        float s = p1.r + p2.r;

        float J = 2 * p1.m * p2.m * dv_dr / (s * (p1.m + p2.m));
        float Jx = J * (p1.x-p2.x) / s;
        float Jy = J * (p1.y-p2.y) / s;

        p1.x += Jx/p1.m; p1.y += Jy/p1.m;
        p2.x += Jx/p2.m; p2.y += Jy/p2.m;
    }

}
