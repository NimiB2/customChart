package dev.nimrod.customchart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;

public class CustomTableView extends LinearLayout {
    private boolean isRowNumberingEnabled = false;

    private TableLayout tableLayout;
    private MaterialTextView tableTitle;

    public CustomTableView(Context context) {
        super(context);
        init(context);
    }

    public CustomTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.table_view, this, true);
        tableLayout = findViewById(R.id.table_layout);
        tableTitle = findViewById(R.id.table_title);
    }

    public void setTitle(String title) {
        tableTitle.setText(title);
        tableTitle.setVisibility(VISIBLE);
    }

    public void hideTitle() {
        tableTitle.setVisibility(GONE);
    }

    public void addRow(String[] cellValues) {
        TableRow row = new TableRow(getContext());
        for (String cellValue : cellValues) {
            TextView cell = new TextView(getContext());
            cell.setText(cellValue);
            cell.setPadding(8, 8, 8, 8);
            cell.setBackgroundResource(R.drawable.cell_border);
            row.addView(cell);
        }
        tableLayout.addView(row);
    }

    public void showHorizontalLines() {
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                TextView cell = (TextView) row.getChildAt(j);
                cell.setBackgroundResource(R.drawable.cell_border);
            }
        }
    }

    public void hideHorizontalLines() {
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                TextView cell = (TextView) row.getChildAt(j);
                cell.setBackgroundResource(0);
            }
        }
    }


    public void enableRowNumbering() {
        isRowNumberingEnabled = true;
        updateRowNumbers();
    }

    public void disableRowNumbering() {
        isRowNumberingEnabled = false;
        updateRowNumbers();
    }

    private void updateRowNumbers() {
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            TextView numberCell = new TextView(getContext());
            numberCell.setText(String.valueOf(i + 1));
            numberCell.setPadding(8, 8, 8, 8);
            if (isRowNumberingEnabled) {
                row.addView(numberCell, 0);
            } else {
                row.removeViewAt(0);
            }
        }
    }
    public void setCellColor(int row, int column, int color) {
        TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
        TextView cell = (TextView) tableRow.getChildAt(column);
        cell.setBackgroundColor(color);
    }

    public void setRowColor(int row, int color) {
        TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
        for (int i = 0; i < tableRow.getChildCount(); i++) {
            TextView cell = (TextView) tableRow.getChildAt(i);
            cell.setBackgroundColor(color);
        }
    }

    public void setColumnColor(int column, int color) {
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
            TextView cell = (TextView) tableRow.getChildAt(column);
            cell.setBackgroundColor(color);
        }
    }
    public void setCellTextColor(int row, int column, int color) {
        TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
        TextView cell = (TextView) tableRow.getChildAt(column);
        cell.setTextColor(color);
    }

    public void setCellTextSize(int row, int column, float size) {
        TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
        TextView cell = (TextView) tableRow.getChildAt(column);
        cell.setTextSize(size);
    }
    public void insertRow(int position, String[] cellValues) {
        TableRow row = new TableRow(getContext());
        for (String cellValue : cellValues) {
            TextView cell = new TextView(getContext());
            cell.setText(cellValue);
            cell.setPadding(8, 8, 8, 8);
            cell.setBackgroundResource(R.drawable.cell_border);
            row.addView(cell);
        }
        tableLayout.addView(row, position);
        if (isRowNumberingEnabled) {
            updateRowNumbers();
        }
    }
    public void removeRow(int position) {
        tableLayout.removeViewAt(position);
        if (isRowNumberingEnabled) {
            updateRowNumbers();
        }
    }

}
