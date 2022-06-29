package cell;

public class Cell {

    private CellState cellState;
    private boolean cautious;
    private Integer lastT;

    public Cell(CellState cellState, boolean cautious) {
        this.cellState = cellState;
        this.lastT = 0;
        this.cautious = cautious;
    }

    public boolean isCautious() {
        return cautious;
    }

    public CellState getCellState() {
        return cellState;
    }

    public Integer getLastT() {
        return lastT;
    }

    public void setLastT(Integer lastT) {
        this.lastT = lastT;
    }

    public boolean isInfected() {
        return cellState == CellState.INFECTED || cellState == CellState.EXPOSED;
    }

}