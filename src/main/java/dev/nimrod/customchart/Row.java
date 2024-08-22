package dev.nimrod.customchart;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Row {
    private static final int DEFAULT_HEIGHT = 0;

    private List<Cell> cells;
    private boolean highlighted;
    private int height;

    public Row(List<Cell> cells) {
        this.cells = new ArrayList<>(cells);
        this.highlighted = false;
        this.height = DEFAULT_HEIGHT;
    }

    public List<Cell> getCells() {
        return new ArrayList<>(cells);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getCellCount() {
        return cells.size();
    }

    public Cell getCell(int index) {
        return cells.get(index);
    }

    public void setCell(int index, Cell cell) {
        cells.set(index, cell);
    }

    public void addCell(Cell cell) {
        cells.add(cell);
    }

    public void addCell(int index, Cell cell) {
        cells.add(index, cell);
    }

    public void removeCell(int index) {
        cells.remove(index);
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Row that = (Row) obj;
        return cells.equals(that.cells) && highlighted == that.highlighted && height == that.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cells, highlighted, height);
    }
}