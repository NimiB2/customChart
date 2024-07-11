package dev.nimrod.customchart;

import android.content.Context;
import android.graphics.Color;
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
    private final String NUMBERING_TITLE = "Numbering";
    private final int TEXT_COLOR = Color.BLACK;
    private final float TEXT_SIZE = 18;

    private String numberingHeaderText;
    private boolean isRowNumberingEnabled = false;
    private TableLayout tableLayout;
    private MaterialTextView tableTitle;
    private boolean hasHeader = false;


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
        // Add an empty header row
        TableRow headerRow = new TableRow(getContext());
        tableLayout.addView(headerRow, 0);

        // Hide the header row if hasHeader is false
        if (!hasHeader) {
            headerRow.setVisibility(GONE);
        }
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
        tableLayout.addView(row, hasHeader ? tableLayout.getChildCount() : tableLayout.getChildCount());
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
        TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
        if (isRowNumberingEnabled) {
            if (headerRow.getChildCount() == 0 || !(headerRow.getChildAt(0) instanceof TextView && ((TextView) headerRow.getChildAt(0)).getText().toString().equals("Numbering"))) {
                TextView headerCell = new TextView(getContext());
                numberingHeaderText = NUMBERING_TITLE;
                headerCell.setText(numberingHeaderText);
                headerCell.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
                headerCell.setBackgroundResource(R.drawable.header_cell_border);
                headerCell.setTypeface(null, Typeface.BOLD);
                headerRow.addView(headerCell, 0);
            }
        } else {
            if (headerRow.getChildCount() > 0 && headerRow.getChildAt(0) instanceof TextView && ((TextView) headerRow.getChildAt(0)).getText().toString().equals("Numbering")) {
                headerRow.removeViewAt(0);
            }
        }

        for (int i = hasHeader ? 1 : 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            TextView numberCell = new TextView(getContext());
            numberCell.setText(String.valueOf(i - (hasHeader ? 1 : 0) + 1));
            numberCell.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
            if (isRowNumberingEnabled) {
                if (row.getChildCount() == 0 || !(row.getChildAt(0) instanceof TextView && ((TextView) row.getChildAt(0)).getText().toString().equals(String.valueOf(i - (hasHeader ? 1 : 0) + 1)))) {
                    row.addView(numberCell, 0);
                }
            } else {
                if (row.getChildCount() > 0 && row.getChildAt(0) instanceof TextView && ((TextView) row.getChildAt(0)).getText().toString().equals(String.valueOf(i - (hasHeader ? 1 : 0) + 1))) {
                    row.removeViewAt(0);
                }
            }
        }
    }

    public void setNumberingHeaderText(String text) {
        this.numberingHeaderText = text;
        if (isRowNumberingEnabled) {
            updateRowNumbers();
        }
    }

    private int findLongestRow() {
        int rowCount = tableLayout.getChildCount();
        int longestRowIndex = 0;
        int maxCells = 0;

        for (int i = 0; i < rowCount; i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            if (row.getChildCount() > maxCells) {
                maxCells = row.getChildCount();
                longestRowIndex = i;
            }
        }

        return longestRowIndex;
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
            if (tableRow.getChildCount() > 1) {
                TextView cell = (TextView) tableRow.getChildAt(column);
                cell.setTextColor(TEXT_COLOR);
                cell.setTypeface(null, Typeface.NORMAL);
                cell.setTextSize(TEXT_SIZE);
                cell.setText(""); // Empty the cell content
                cell.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
                cell.setBackgroundResource(R.drawable.cell_border);
                cell.setSingleLine(false);
                cell.setEllipsize(null);
            } else {
                tableRow.removeViewAt(column);
            }
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
        hasHeader = true;
        TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
        headerRow.removeAllViews(); // Clear any existing header cells
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
        headerRow.setVisibility(VISIBLE);
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

    private void addColumnValues(String[] columnValues, int startIndex, int longestRowCells) {

        // Insert the new column cells
        for (int i = 0; i < columnValues.length; i++) {
            if (i + startIndex < tableLayout.getChildCount()) {
                TableRow currentRow = (TableRow) tableLayout.getChildAt(i + startIndex);

                // Add empty cells if current row has fewer cells than the longest row
                while (currentRow.getChildCount() < longestRowCells) {
                    addCellToRow(i + startIndex, "");
                }

                addCellToRow(i + startIndex, columnValues[i]);
            } else {
                // Add an empty row with the new column value in the last position
                String[] newRowValues = new String[longestRowCells + 1];
                for (int j = 0; j < longestRowCells; j++) {
                    newRowValues[j] = "";
                }
                newRowValues[longestRowCells] = columnValues[i];
                addRow(newRowValues);
            }
        }

        // Add remaining empty cells to existing rows if needed
        for (int i = startIndex; i < tableLayout.getChildCount(); i++) {
            TableRow currentRow = (TableRow) tableLayout.getChildAt(i);
            while (currentRow.getChildCount() < longestRowCells + 1) { // +1 to account for the new column
                addCellToRow(i, "");
            }
        }
    }


    public void addColumnWithHeader(String[] columnValues, String headerValue) {
        int longestRowIndex = findLongestRow();
        int columnIndex = ((TableRow) tableLayout.getChildAt(longestRowIndex)).getChildCount();
        int longestRowCells = ((TableRow) tableLayout.getChildAt(longestRowIndex)).getChildCount();

        if (!hasHeader) {
            // If there are no headers, add a header row with empty values and the new header
            String[] headerValues = new String[columnIndex + 1];
            for (int i = 0; i < columnIndex; i++) {
                headerValues[i] = "";
            }
            headerValues[columnIndex] = headerValue;
            addHeaderRow(headerValues);
            hasHeader = true;
        } else {
            // Insert the header cell
            addHeaderCell(headerValue, columnIndex);
        }

        // Use the helper function to add the column values
        addColumnValues(columnValues, 1, longestRowCells);
    }


    public void addColumn(String[] columnValues) {
        int longestRowIndex = findLongestRow();
        int longestRowCells = ((TableRow) tableLayout.getChildAt(longestRowIndex)).getChildCount();

        if (hasHeader) {
            // Add an empty header cell
            addHeaderCell("", longestRowCells);
        }

        // Use the helper function to add the column values
        addColumnValues(columnValues, hasHeader ? 1 : 0, longestRowCells);
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
        return row >= (hasHeader ? 1 : 0) && row < tableLayout.getChildCount();
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
