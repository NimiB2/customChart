package dev.nimrod.customchart;

import java.util.ArrayList;
import java.util.List;

public class ChartRow {
    private List<ChartCell> cells;

    public ChartRow() {
        cells = new ArrayList<>();
    }

    public List<ChartCell> getCells() {
        return cells;
    }

    public void addCell(ChartCell cell) {
        cells.add(cell);
    }

    public void addCell(int index, ChartCell cell) {
        cells.add(index, cell);
    }

    public void removeCell(int index) {
        cells.remove(index);
    }
}
