import java.util.List;

public class Result {

    private float t;
    private List<State> states;

    public Result(float t, List<State> states) {
        this.t = t;
        this.states = states;
    }

    public float getT() {
        return t;
    }

    public List<State> getStates() {
        return states;
    }
}
