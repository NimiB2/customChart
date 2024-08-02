package dev.nimrod.customchart.Util;

import android.util.TypedValue;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dev.nimrod.customchart.Cell;

public class TableViewMemento {
    private final List<List<Cell>> state;
    private final boolean hasHeader;
    private final boolean isRowNumberingEnabled;

    public TableViewMemento(List<List<Cell>> state, boolean hasHeader, boolean isRowNumberingEnabled) {
        this.state = state;
        this.hasHeader = hasHeader;
        this.isRowNumberingEnabled = isRowNumberingEnabled;
    }

    public List<List<Cell>> getState() {
        return state;
    }

    public boolean isHasHeader() {
        return hasHeader;
    }

    public boolean isRowNumberingEnabled() {
        return isRowNumberingEnabled;
    }
}
