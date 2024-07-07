package dev.nimrod.customchart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChartView extends LinearLayout {
    private List<ChartRow> rows;
    private boolean hasTitle = false;

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
        addView(createRowView(row, rows.size()));
    }

    public void addTitle(String title) {
        int maxColumns = getMaxColumns();
        ChartRow titleRow = new ChartRow();
        ChartCell titleCell = new ChartCell(title);
        titleCell.setBold(true);
        titleRow.addCell(titleCell);
        for (int i = 1; i < maxColumns; i++) {
            titleRow.addCell(new ChartCell(""));
        }
        rows.add(0, titleRow);
        addView(createRowView(titleRow, 0), 0);
        hasTitle = true;
    }

    public void removeRow(int index) {
        rows.remove(index);
        removeViewAt(index);
    }

    public void updateRow(int index, ChartRow newRow) {
        rows.set(index, newRow);
        removeViewAt(index);
        addView(createRowView(newRow, index), index);
    }

    private LinearLayout createRowView(ChartRow row, int rowIndex) {
        LinearLayout rowView = new LinearLayout(getContext());
        rowView.setOrientation(HORIZONTAL);
        int maxColumns = getMaxColumns();
        for (int i = 0; i < row.getCells().size(); i++) {
            TextView cellView = new TextView(getContext());
            ChartCell cell = row.getCells().get(i);
            cellView.setText(cell.getData());
            cellView.setTextColor(cell.getTextColor());
            if (cell.isBold()) {
                cellView.setTypeface(null, Typeface.BOLD);
            }
            cellView.setGravity(Gravity.CENTER);
            LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
            cellView.setLayoutParams(params);
            rowView.addView(cellView);
        }
        for (int i = row.getCells().size(); i < maxColumns; i++) {
            TextView cellView = new TextView(getContext());
            cellView.setText("");
            LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
            cellView.setLayoutParams(params);
            rowView.addView(cellView);
        }
        if (rowIndex > 0 && !hasTitle) {
            addAutomaticNumbering(rowView, rowIndex);
        }
        return rowView;
    }

    private int getMaxColumns() {
        int maxColumns = 1; // Default for title
        for (ChartRow row : rows) {
            if (row.getCells().size() > maxColumns) {
                maxColumns = row.getCells().size();
            }
        }
        return maxColumns;
    }

    private void addAutomaticNumbering(LinearLayout rowView, int rowIndex) {
        TextView numberView = new TextView(getContext());
        numberView.setText(String.valueOf(rowIndex));
        numberView.setGravity(Gravity.CENTER);
        LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
        numberView.setLayoutParams(params);
        rowView.addView(numberView, 0);
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

    public void setTableColor(int color) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setBackgroundColor(color);
        }
    }

    public void setFirstRowColor(int color) {
        setRowColor(1, color); // Skip the title row
    }

    public void setFirstColumnColor(int color) {
        for (int i = 1; i < getChildCount(); i++) { // Skip the title row
            LinearLayout rowView = (LinearLayout) getChildAt(i);
            rowView.getChildAt(0).setBackgroundColor(color);
        }
    }

    public void setCellTextColor(int rowIndex, int cellIndex, int color) {
        LinearLayout rowView = (LinearLayout) getChildAt(rowIndex);
        TextView cellView = (TextView) rowView.getChildAt(cellIndex);
        cellView.setTextColor(color);
    }

    public void setSeparationLines(int color) {
        for (int i = 0; i < getChildCount(); i++) {
            LinearLayout rowView = (LinearLayout) getChildAt(i);
            for (int j = 0; j < rowView.getChildCount(); j++) {
                TextView cellView = (TextView) rowView.getChildAt(j);
                cellView.setPadding(2, 2, 2, 2);
                cellView.setBackgroundColor(color);
            }
        }
    }
}