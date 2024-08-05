package dev.nimrod.customchart.Util;

import java.util.List;
import dev.nimrod.customchart.Row;

public class TableViewMemento {
    private final List<Row> state;
    private final boolean hasHeader;
    private final boolean isRowNumberingEnabled;

    public TableViewMemento(List<Row> state, boolean hasHeader, boolean isRowNumberingEnabled) {
        this.state = state;
        this.hasHeader = hasHeader;
        this.isRowNumberingEnabled = isRowNumberingEnabled;
    }

    public List<Row> getState() {
        return state;
    }

    public boolean isHasHeader() {
        return hasHeader;
    }

    public boolean isRowNumberingEnabled() {
        return isRowNumberingEnabled;
    }
}