package dev.nimrod.customchart;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;

public class CustomTableView extends HorizontalScrollView {
    private final int DEFAULT_PADDING = 8;
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
            cell.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
            cell.setBackgroundResource(R.drawable.cell_border);
            cell.setSingleLine(false);
            cell.setEllipsize(null);
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
            numberCell.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
            if (isRowNumberingEnabled) {
                if (row.getChildCount() == 0 || !(row.getChildAt(0) instanceof TextView && ((TextView) row.getChildAt(0)).getText().toString().equals(String.valueOf(i + 1)))) {
                    row.addView(numberCell, 0);
                }
            } else {
                if (row.getChildCount() > 0 && row.getChildAt(0) instanceof TextView && ((TextView) row.getChildAt(0)).getText().toString().equals(String.valueOf(i + 1))) {
                    row.removeViewAt(0);
                }
            }
        }
    }

    public void setCellColor(int row, int column, int color) {
        if (isValidPosition(row, column)) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
            TextView cell = (TextView) tableRow.getChildAt(column);
            cell.setBackgroundColor(color);
        }
    }

    public void setRowColor(int row, int color) {
        if (isValidRow(row)) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
            for (int i = 0; i < tableRow.getChildCount(); i++) {
                TextView cell = (TextView) tableRow.getChildAt(i);
                cell.setBackgroundColor(color);
            }
        }
    }

    public void setColumnColor(int column, int color) {
        if (isValidColumn(column)) {
            for (int i = 0; i < tableLayout.getChildCount(); i++) {
                TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
                if (column < tableRow.getChildCount()) {
                    TextView cell = (TextView) tableRow.getChildAt(column);
                    cell.setBackgroundColor(color);
                }
            }
        }
    }

    public void setCellTextColor(int row, int column, int color) {
        if (isValidPosition(row, column)) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
            TextView cell = (TextView) tableRow.getChildAt(column);
            cell.setTextColor(color);
        }
    }

    public void setCellTextSize(int row, int column, float size) {
        if (isValidPosition(row, column)) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
            TextView cell = (TextView) tableRow.getChildAt(column);
            cell.setTextSize(size);
        }
    }

    public void setCellTextStyle(int row, int column, int style) {
        if (isValidPosition(row, column)) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
            TextView cell = (TextView) tableRow.getChildAt(column);
            cell.setTypeface(null, style);
        }
    }

    public void insertRow(int position, String[] cellValues) {
        if (position >= 0 && position <= tableLayout.getChildCount()) {
            TableRow row = new TableRow(getContext());
            for (String cellValue : cellValues) {
                TextView cell = new TextView(getContext());
                cell.setText(cellValue);
                cell.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
                cell.setBackgroundResource(R.drawable.cell_border);
                cell.setSingleLine(false);
                cell.setEllipsize(null);
                row.addView(cell);
            }
            tableLayout.addView(row, position);
            if (isRowNumberingEnabled) {
                updateRowNumbers();
            }
        }
    }

    public void removeRow(int position) {
        if (position >= 0 && position < tableLayout.getChildCount()) {
            tableLayout.removeViewAt(position);
            if (isRowNumberingEnabled) {
                updateRowNumbers();
            }
        }
    }

    public void removeCell(int row, int column) {
        if (isValidPosition(row, column)) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
            tableRow.removeViewAt(column);
        }
    }

    public void setCellText(int row, int column, String text) {
        if (isValidPosition(row, column)) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
            TextView cell = (TextView) tableRow.getChildAt(column);
            cell.setText(text);
        }
    }

    public void addCellToRow(int row, String cellValue) {
        if (isValidRow(row)) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
            TextView cell = new TextView(getContext());
            cell.setText(cellValue);
            cell.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
            cell.setBackgroundResource(R.drawable.cell_border);
            cell.setSingleLine(false);
            cell.setEllipsize(null);
            tableRow.addView(cell);
        }
    }

    private void addHeaderCell(String headerValue, int columnIndex) {
        if (tableLayout.getChildCount() > 0) {
            TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
            TextView headerCell = new TextView(getContext());
            headerCell.setText(headerValue);
            headerCell.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
            headerCell.setBackgroundResource(R.drawable.header_cell_border);
            headerCell.setTypeface(null, Typeface.BOLD);
            headerCell.setSingleLine(false);
            headerCell.setEllipsize(null);
            headerRow.addView(headerCell, columnIndex);
        }
    }

    public void addHeaderRow(String[] headerValues) {
        if (tableLayout.getChildCount() == 0) {
            TableRow headerRow = new TableRow(getContext());
            for (String headerValue : headerValues) {
                TextView headerCell = new TextView(getContext());
                headerCell.setText(headerValue);
                headerCell.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
                headerCell.setBackgroundResource(R.drawable.header_cell_border);
                headerCell.setTypeface(null, Typeface.BOLD);
                headerCell.setSingleLine(false);
                headerCell.setEllipsize(null);
                headerRow.addView(headerCell);
            }
            tableLayout.addView(headerRow, 0);
        } else {
            TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
            for (int i = 0; i < headerValues.length; i++) {
                TextView headerCell = new TextView(getContext());
                headerCell.setText(headerValues[i]);
                headerCell.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
                headerCell.setBackgroundResource(R.drawable.header_cell_border);
                headerCell.setTypeface(null, Typeface.BOLD);
                headerCell.setSingleLine(false);
                headerCell.setEllipsize(null);
                headerRow.addView(headerCell, i);
            }
        }
    }

    public void setCellSpan(int row, int column, int rowSpan, int colSpan) {
        if (isValidPosition(row, column)) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
            TextView cell = (TextView) tableRow.getChildAt(column);
            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.span = colSpan;
            params.height = rowSpan;
            cell.setLayoutParams(params);
        }
    }

    public void setCellClickListener(int row, int column, View.OnClickListener listener) {
        if (isValidPosition(row, column)) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
            TextView cell = (TextView) tableRow.getChildAt(column);
            cell.setOnClickListener(listener);
        }
    }

    public void addColumn(String[] columnValues) {
        int rowCount = tableLayout.getChildCount();
        int columnIndex = 0;

        if (rowCount > 0) {
            TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
            columnIndex = headerRow.getChildCount();
        }

        for (int i = 0; i < rowCount; i++) {
            if (i == 0) {
                // Add header cell
                if (i < columnValues.length) {
                    addHeaderCell(columnValues[i], columnIndex);
                } else {
                    addHeaderCell("", columnIndex);
                }
            } else {
                // Add regular cell
                if (i < columnValues.length) {
                    addCellToRow(i, columnValues[i]);
                } else {
                    addCellToRow(i, "");
                }
            }
        }
    }

    public void addColumn(String[] columnValues, String headerValue) {
        int rowCount = tableLayout.getChildCount();
        int columnIndex = 0;

        if (rowCount > 0) {
            TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
            columnIndex = headerRow.getChildCount();
        }

        addHeaderCell(headerValue, columnIndex);

        for (int i = 1; i < rowCount; i++) {
            // Add regular cell
            if (i < columnValues.length) {
                addCellToRow(i, columnValues[i]);
            } else {
                addCellToRow(i, "");
            }
        }
    }

    public void removeColumn(int column) {
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
            if (column >= 0 && column < tableRow.getChildCount()) {
                tableRow.removeViewAt(column);
            }
        }
    }

    private boolean isValidPosition(int row, int column) {
        return isValidRow(row) && isValidColumn(column, row);
    }

    private boolean isValidRow(int row) {
        return row >= 0 && row < tableLayout.getChildCount();
    }

    private boolean isValidColumn(int column, int row) {
        if (isValidRow(row)) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
            return column >= 0 && column < tableRow.getChildCount();
        }
        return false;
    }

    private boolean isValidColumn(int column) {
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
            if (column >= 0 && column < tableRow.getChildCount()) {
                return true;
            }
        }
        return false;
    }
}
