package cell;

public class Cell {

    private boolean alive;
    private Integer bornIteration;

    public Cell(boolean alive) {
        this.alive = alive;
        this.bornIteration = 0;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Integer getBornIteration() {
        return bornIteration;
    }

    public void setBornIteration(Integer bornIteration) {
        this.bornIteration = bornIteration;
    }

    @Override
    public String toString() {
        return String.format("%d %d", alive ? 1 : 0, bornIteration);
    }

}