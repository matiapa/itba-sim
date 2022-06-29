package evolutionRules;

import cell.Cell;
import cell.CellState;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;


public class InfectionRule implements EvolutionRule {

    private final float pe, pq, pi, pr, pd, em, eq;
    private final int ti, tq, tr, td;

    public InfectionRule(float pe, float pq, float pi, float pr, float pd, float em, float eq, int ti, int tq, int tr, int td) {
        this.pe = pe;
        this.pq = pq;
        this.pi = pi;
        this.pr = pr;
        this.pd = pd;
        this.em = em;
        this.eq = eq;
        this.ti = ti;
        this.tq = tq;
        this.tr = tr;
        this.td = td;
    }

    @Override
    public Cell evaluate(int t, int x, int y, Cell[][] grid) {

        Cell cell = grid[x][y];
        CellState newState = null;

        if(cell.getCellState() == CellState.SUSCEPTIBLE) {
            int infectedNeighbours = 0;
            for(int i = x - 1; i <= x + 1; i++) {
                for(int j = y - 1; j <= y + 1; j++) {
                    if(i < 0 || i >= grid.length || j < 0 || j >= grid.length)
                        continue;
                    infectedNeighbours += grid[i][j].isInfected() && (i != x || j != y)  ? 1 : 0;
                }
            }

            float per = pe * infectedNeighbours * (cell.isCautious() ? (1-em) : 1);

            if(Math.random() < per)
                newState = CellState.EXPOSED;

        } else if (cell.getCellState() == CellState.EXPOSED && t >= cell.getLastT() + ti && Math.random() < pi) {
            newState = CellState.INFECTED;

        } else if (cell.getCellState() == CellState.INFECTED) {
            final List<Pair<CellState, Double>> stateWeights = new ArrayList<>();
            float pqr = t >= cell.getLastT() + tq ? pq : 0;
            float prr = t >= cell.getLastT() + tr ? pr : 0;
            float pdr = t >= cell.getLastT() + td ? pd : 0;
            float pir = 1 - pqr - prr - pdr;

            stateWeights.add(new Pair<>(CellState.QUARANTINED, (double) pqr));
            stateWeights.add(new Pair<>(CellState.RECOVERED, (double) prr));
            stateWeights.add(new Pair<>(CellState.DEAD, (double) pdr));
            stateWeights.add(new Pair<>(CellState.INFECTED, (double) pir));

            newState = new EnumeratedDistribution<>(stateWeights).sample();

        } else if (cell.getCellState() == CellState.QUARANTINED) {
            final List<Pair<CellState, Double>> stateWeights = new ArrayList<>();
            float prr = t >= cell.getLastT() + tr ? pr : 0;
            float pdr = t >= cell.getLastT() + td ? pd * (1-eq) : 0;
            float pqr = 1 - prr - pdr;

            stateWeights.add(new Pair<>(CellState.RECOVERED, (double) prr));
            stateWeights.add(new Pair<>(CellState.DEAD, (double) pdr));
            stateWeights.add(new Pair<>(CellState.QUARANTINED, (double) pqr));

            newState = new EnumeratedDistribution<>(stateWeights).sample();
        }

        boolean stateChanged = newState != null;
        Cell newCell =  new Cell(stateChanged ? newState : cell.getCellState(), cell.isCautious());

        if(stateChanged)
            newCell.setLastT(t);

        return newCell;
    }

    @Override
    public String ruleType() {
        return "InfectionRule";
    }

}
