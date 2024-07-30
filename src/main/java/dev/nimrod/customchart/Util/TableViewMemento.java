package dev.nimrod.customchart.Util;

import android.util.TypedValue;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TableViewMemento {
    private final List<List<String>> state;
    private final boolean hasHeader;
    private final boolean isRowNumberingEnabled;

    public TableViewMemento(List<List<String>> state, boolean hasHeader, boolean isRowNumberingEnabled) {
        this.state = new ArrayList<>();
        for (List<String> row : state) {
            this.state.add(new ArrayList<>(row));
        }
        this.hasHeader = hasHeader;
        this.isRowNumberingEnabled = isRowNumberingEnabled;
    }

    public List<List<String>> getState() {
        List<List<String>> copiedState = new ArrayList<>();
        for (List<String> row : state) {
            copiedState.add(new ArrayList<>(row));
        }
        return copiedState;
    }

    public boolean isHasHeader() {
        return hasHeader;
    }

    public boolean isRowNumberingEnabled() {
        return isRowNumberingEnabled;
    }
}