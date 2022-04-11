public abstract class Collision implements Comparable<ParticleCollision> {

    protected Float t;

    public Collision(Float t) {
        this.t = t;
    }

    @Override
    public int compareTo(ParticleCollision o) {
        return t.compareTo(o.t);
    }

    public abstract boolean involves(Particle p);

    public abstract void operate();

}
