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
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TableViewMemento that = (TableViewMemento) obj;
        if (hasHeader != that.hasHeader || isRowNumberingEnabled != that.isRowNumberingEnabled) return false;
        if (state.size() != that.state.size()) return false;
        for (int i = 0; i < state.size(); i++) {
            if (!state.get(i).equals(that.state.get(i))) return false;
        }
        return true;
    }
}