package dev.nimrod.customchart.Util;

import android.util.TypedValue;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TableViewMemento {
    private final List<TableRow> state;

    public TableViewMemento(List<TableRow> state) {
        // Deep copy of the state
        this.state = new ArrayList<>();
        for (TableRow row : state) {
            TableRow rowCopy = new TableRow(row.getContext());
            for (int j = 0; j < row.getChildCount(); j++) {
                TextView cell = (TextView) row.getChildAt(j);
                TextView cellCopy = new TextView(row.getContext());
                cellCopy.setText(cell.getText());
                cellCopy.setLayoutParams(cell.getLayoutParams());
                cellCopy.setBackground(cell.getBackground());
                cellCopy.setTextColor(cell.getTextColors());
                cellCopy.setTypeface(cell.getTypeface());
                cellCopy.setTextSize(TypedValue.COMPLEX_UNIT_PX, cell.getTextSize());
                rowCopy.addView(cellCopy);
            }
            this.state.add(rowCopy);
        }
    }

    public List<TableRow> getState() {
        return state;
    }

}
