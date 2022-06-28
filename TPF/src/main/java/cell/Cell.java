package cell;

public class Cell {

    private CellState cellState;
    private CautiousLevel cautiousLevel;
    private Integer deadIteration;

    public Cell(CellState cellState, CautiousLevel cautiousLevel) {
        this.cellState = cellState;
        this.deadIteration = 0;
        this.cautiousLevel = cautiousLevel;
    }

    public CautiousLevel getCautiousLevel() {
        return cautiousLevel;
    }

    public void setCautiousLevel(CautiousLevel cautiousLevel) {
        this.cautiousLevel = cautiousLevel;
    }

    public CellState getCellState() {
        return cellState;
    }

    public void setCellState(CellState cellState) {
        this.cellState = cellState;
    }

    public Integer getDeadIteration() {
        return deadIteration;
    }

    public void setDeadIteration(Integer deadIteration) {
        this.deadIteration = deadIteration;
    }

    public boolean isInfected() {
        return cellState == CellState.INFECTED || cellState == CellState.EXPOSED;
    }

    public boolean isQuarantined() {
        return cellState == CellState.QUARANTINED;
    }
}