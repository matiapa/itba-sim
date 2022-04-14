public abstract class Collision implements Comparable<Collision> {

    protected Float t;

    public Collision(Float t) {
        this.t = t;
    }

    @Override
    public int compareTo(Collision o) {
        return t.compareTo(o.t);
    }

    public abstract boolean involves(Particle p);

    public abstract void operate();

}
