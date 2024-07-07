package dev.nimrod.customchart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChartView extends LinearLayout {
    private List<ChartRow> rows;
    private TextView titleView;
    private int maxNumberWidth = 0;
    private boolean isNumberingEnabled = false;

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
        titleView = new TextView(getContext());
        titleView.setGravity(Gravity.CENTER);
        titleView.setTextSize(20); // Default title text size
        addView(titleView);
    }

    public void setTitle(String title) {
        titleView.setText(title);
        titleView.setVisibility(View.VISIBLE);
    }

    public void removeTitle() {
        titleView.setText("");
        titleView.setVisibility(View.GONE);
    }

    public void setTitleTextColor(int color) {
        titleView.setTextColor(color);
    }

    public void setTitleTextSize(float size) {
        titleView.setTextSize(size);
    }

    public void setTitleTextBold(boolean bold) {
        titleView.setTypeface(null, bold ? Typeface.BOLD : Typeface.NORMAL);
    }

    public void setTitleBackgroundColor(int color) {
        titleView.setBackgroundColor(color);
    }

    public void addRow(ChartRow row) {
        if (isNumberingEnabled) {
            int rowNumber = rows.size() + 1;
            row.addCell(0, new ChartCell(String.valueOf(rowNumber)));
        }
        rows.add(row);
        addView(createRowView(row));
        updateNumberColumnWidth();
    }

    public void removeRow(int index) {
        rows.remove(index);
        removeViewAt(index + 1); // Adjust for the title row
        updateNumberColumnWidth();
    }

    public void updateRow(int index, ChartRow newRow) {
        if (isNumberingEnabled) {
            // Preserve the existing number if it has been manually set
            String currentNumber = rows.get(index).getCells().get(0).getData();
            newRow.getCells().set(0, new ChartCell(currentNumber));
        }
        rows.set(index, newRow);
        removeViewAt(index + 1); // Adjust for the title row
        addView(createRowView(newRow), index + 1);
        updateNumberColumnWidth();
    }

    private LinearLayout createRowView(ChartRow row) {
        LinearLayout rowView = new LinearLayout(getContext());
        rowView.setOrientation(HORIZONTAL);
        for (int i = 0; i < row.getCells().size(); i++) {
            TextView cellView = new TextView(getContext());
            cellView.setText(row.getCells().get(i).getData());
            if (i == 0 && isNumberingEnabled) {
                cellView.setWidth(maxNumberWidth); // Set width for the numbering column
            }
            rowView.addView(cellView);
            if (i == 0 && isNumberingEnabled) {
                View space = new View(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(16, LayoutParams.MATCH_PARENT);
                rowView.addView(space, params); // Add space between numbering and other cells
            }
        }
        return rowView;
    }

    private void updateNumberColumnWidth() {
        if (!isNumberingEnabled) return;

        int maxWidth = 0;
        for (ChartRow row : rows) {
            TextView tempView = new TextView(getContext());
            tempView.setText(row.getCells().get(0).getData());
            tempView.measure(0, 0);
            int width = tempView.getMeasuredWidth();
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        maxNumberWidth = maxWidth;

        for (int i = 1; i <= rows.size(); i++) { // Skip the title row
            LinearLayout rowView = (LinearLayout) getChildAt(i);
            TextView numberCell = (TextView) rowView.getChildAt(0);
            numberCell.setWidth(maxNumberWidth);
        }
    }

    public void setNumber(int rowIndex, String number) {
        if (!isNumberingEnabled || rowIndex <= 0 || rowIndex > rows.size()) return;
        ChartRow row = rows.get(rowIndex - 1);
        row.getCells().get(0).setData(number);
        updateRow(rowIndex - 1, row);
    }

    public void removeNumbering() {
        isNumberingEnabled = false;
        for (ChartRow row : rows) {
            row.getCells().get(0).setData("");
        }
        updateAllRows();
    }

    public void addNumbering() {
        isNumberingEnabled = true;
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).getCells().get(0).setData(String.valueOf(i + 1));
        }
        updateAllRows();
    }

    private void updateAllRows() {
        removeAllViews();
        addView(titleView);
        for (int i = 0; i < rows.size(); i++) {
            addView(createRowView(rows.get(i)));
        }
        updateNumberColumnWidth();
    }

    public void setRowColor(int rowIndex, int color) {
        LinearLayout rowView = (LinearLayout) getChildAt(rowIndex + 1); // Adjust for the title row
        rowView.setBackgroundColor(color);
    }

    public void setCellColor(int rowIndex, int cellIndex, int color) {
        LinearLayout rowView = (LinearLayout) getChildAt(rowIndex + 1); // Adjust for the title row
        TextView cellView = (TextView) rowView.getChildAt(cellIndex);
        cellView.setBackgroundColor(color);
    }

    public void setRowTextSize(int rowIndex, float size) {
        LinearLayout rowView = (LinearLayout) getChildAt(rowIndex + 1); // Adjust for the title row
        for (int i = 0; i < rowView.getChildCount(); i++) {
            TextView cellView = (TextView) rowView.getChildAt(i);
            cellView.setTextSize(size);
        }
    }
}
