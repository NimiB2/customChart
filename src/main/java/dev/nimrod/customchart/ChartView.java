package dev.nimrod.customchart;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChartView extends LinearLayout {
    private List<ChartRow> rows;

    public ChartView(Context context) {
        super(context);
        init();
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        rows = new ArrayList<>();
    }

    public void addRow(ChartRow row) {
        rows.add(row);
        addView(createRowView(row));
    }

    public void removeRow(int index) {
        rows.remove(index);
        removeViewAt(index);
    }

    public void updateRow(int index, ChartRow newRow) {
        rows.set(index, newRow);
        removeViewAt(index);
        addView(createRowView(newRow), index);
    }

    private LinearLayout createRowView(ChartRow row) {
        LinearLayout rowView = new LinearLayout(getContext());
        rowView.setOrientation(HORIZONTAL);
        for (ChartCell cell : row.getCells()) {
            TextView cellView = new TextView(getContext());
            cellView.setText(cell.getData());
            rowView.addView(cellView);
        }
        return rowView;
    }

    public void setRowColor(int rowIndex, int color) {
        LinearLayout rowView = (LinearLayout) getChildAt(rowIndex);
        rowView.setBackgroundColor(color);
    }

    public void setCellColor(int rowIndex, int cellIndex, int color) {
        LinearLayout rowView = (LinearLayout) getChildAt(rowIndex);
        TextView cellView = (TextView) rowView.getChildAt(cellIndex);
        cellView.setBackgroundColor(color);
    }

    public void setRowTextSize(int rowIndex, float size) {
        LinearLayout rowView = (LinearLayout) getChildAt(rowIndex);
        for (int i = 0; i < rowView.getChildCount(); i++) {
            TextView cellView = (TextView) rowView.getChildAt(i);
            cellView.setTextSize(size);
        }
    }
}
