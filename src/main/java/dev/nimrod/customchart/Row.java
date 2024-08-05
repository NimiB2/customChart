package dev.nimrod.customchart;

import java.util.List;

public class Row {
    private List<Cell> cells;
    private int height;

    public Row(List<Cell> cells) {
        this.cells = cells;
        this.height = 0;
    }

    public List<Cell> getCells() {
        return cells;
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
}