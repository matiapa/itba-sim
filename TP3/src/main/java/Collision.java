public abstract class Collision implements Comparable<Collision> {

    protected Float t;
    private float timeTaken = 0;

    public Collision(Float t) {
        this.t = t;
    }

    @Override
    public int compareTo(Collision o) {
        return t.compareTo(o.t);
    }

    public abstract boolean involves(Particle p);

    public abstract void operate();

    public void setTimeTaken(float timeTaken) {
        this.timeTaken = timeTaken;
    }

    public float getTimeTaken() {
        return timeTaken;
    }
}
