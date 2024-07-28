package dev.nimrod.customchart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.nimrod.customchart.Util.TableViewCaretaker;
import dev.nimrod.customchart.Util.TableViewMemento;

public class CustomTableView extends HorizontalScrollView {
    private static final int DEFAULT_PADDING = 8;
    private static final String NUMBERING_TITLE = "NUM";
    private static final int TEXT_COLOR = Color.BLACK;
    private static final float TEXT_SIZE = 18;
    private int highlightColor = Color.YELLOW;

    private TableViewCaretaker caretaker = new TableViewCaretaker();

    private String numberingHeaderText;
    private boolean isRowNumberingEnabled = false;
    private TableLayout tableLayout;
    private MaterialTextView tableTitle;
    private boolean hasHeader = false;
    private AppCompatEditText filterText;
    private MaterialButton filterButton;

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
        filterText = findViewById(R.id.filter_text);
        filterButton = findViewById(R.id.filter_button);
        numberingHeaderText = NUMBERING_TITLE;

        filterButton.setOnClickListener(v -> {
            String query = filterText.getText().toString();
            if (TextUtils.isEmpty(query)) {
                clearFilter();
            } else {
                filterRows(query);
            }
        });

        tableLayout.addView(createEmptyHeaderRow(), 0);
    }

    public void filterRows(String query) {
        if (caretaker.getMemento() == null) {
            caretaker.saveState(this);
        }
        clearHighlights();
        for (int i = hasHeader ? 1 : 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            boolean rowContainsQuery = false;
            for (int j = 0; j < row.getChildCount(); j++) {
                TextView cell = (TextView) row.getChildAt(j);
                if (cell.getText().toString().contains(query)) {
                    rowContainsQuery = true;
                    highlightCell(cell);
                }
            }
            if (rowContainsQuery) {
                row.setVisibility(View.VISIBLE);
            } else {
                row.setVisibility(View.GONE);
            }
        }
    }

    public void clearFilter() {
        clearHighlights();
        caretaker.restoreState(this);
        caretaker.clearMemento();
    }
    private void highlightCell(TextView cell) {
        cell.setBackgroundColor(highlightColor);
    }

    public void clearHighlights() {
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                TextView cell = (TextView) row.getChildAt(j);
                cell.setBackgroundResource(R.drawable.cell_border); // Reset to default background
            }
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
        TableRow row = createTableRow(cellValues);
        resetRowProperties(row); // Reset properties for new row
        tableLayout.addView(row, tableLayout.getChildCount());
        updateAllRowsToLongestRow();
        adjustColumnWidths();

        // Reset row color to default (assuming default is Color.TRANSPARENT)
        setRowColor(tableLayout.getChildCount() - 1, Color.TRANSPARENT);
    }

    private void resetRowProperties(TableRow row) {
        for (int i = 0; i < row.getChildCount(); i++) {
            TextView cell = (TextView) row.getChildAt(i);
            cell.setTextColor(TEXT_COLOR);
            cell.setTypeface(null, Typeface.NORMAL);
            cell.setTextSize(TEXT_SIZE);
            cell.setBackgroundResource(R.drawable.cell_border); // Reset to default background
        }
    }
    public void removeRow(int position) {
        int actualPosition = hasHeader ? position + 1 : position;
        if (position >= 0 && position < tableLayout.getChildCount()) {
            tableLayout.removeViewAt(position);
            if (isRowNumberingEnabled) {
                updateRowNumbers();
            }
            adjustColumnWidths();
        }
    }

    private TextView createNumberingCell(String text) {
        TextView cell = new TextView(getContext());
        cell.setText(text);
        cell.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
        cell.setBackgroundResource(R.drawable.cell_border);
        cell.setSingleLine(false);
        cell.setEllipsize(null);
        cell.setGravity(Gravity.CENTER);
        return cell;
    }

    public void enableRowNumbering() {
        isRowNumberingEnabled = true;
        updateRowNumbers();
        updateHeaderForNumbering();

    }

    public void disableRowNumbering() {
        isRowNumberingEnabled = false;
        updateRowNumbers();
        updateHeaderForNumbering();

    }

    private void updateRowNumbers() {
        for (int i = 1; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            updateNumberingCell(row, i);
        }
    }

    private void updateNumberingCell(TableRow row, int index) {
        TextView numberCell = createNumberingCell(String.valueOf(index));
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING); // Adjust as necessary
        numberCell.setLayoutParams(params);

        if (isRowNumberingEnabled) {
            if (!(row.getChildAt(0) instanceof TextView && ((TextView) row.getChildAt(0)).getText().toString().equals(String.valueOf(index)))) {
                row.addView(numberCell, 0);
            } else {
                ((TextView) row.getChildAt(0)).setText(String.valueOf(index));
            }
        } else {
            if (row.getChildCount() > 0 && row.getChildAt(0) instanceof TextView) {
                row.removeViewAt(0);
            }
        }
    }

    private void updateHeaderForNumbering() {
        if (hasHeader) {
            TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
            if (isRowNumberingEnabled) {
                if (!(headerRow.getChildAt(0) instanceof TextView && ((TextView) headerRow.getChildAt(0)).getText().toString().equals(numberingHeaderText))) {
                    headerRow.addView(createHeaderCell(numberingHeaderText), 0);
                } else {
                    ((TextView) headerRow.getChildAt(0)).setText(numberingHeaderText);
                }
            } else {
                if (headerRow.getChildCount() > 0 && headerRow.getChildAt(0) instanceof TextView) {
                    headerRow.removeViewAt(0);
                }
            }
        }
    }

    public void setNumberingHeaderText(String text) {
        this.numberingHeaderText = text;
        if (isRowNumberingEnabled) {
            TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
            if (headerRow.getChildCount() > 0 && headerRow.getChildAt(0) instanceof TextView) {
                ((TextView) headerRow.getChildAt(0)).setText(numberingHeaderText);
            }
            updateRowNumbers();
        }
    }

    public void setCellColor(int row, int column, int color) {
        if (isValidPosition(row, column)) {
            TextView cell = getCellAt(row, column);
            cell.setBackgroundResource(R.drawable.cell_border); // Reset to default background
            Drawable background = DrawableUtil.updateDrawableInteriorColor(getContext(), R.drawable.cell_border, color);
            cell.setBackground(background);
        }
    }

    public void setRowColor(int row, int color) {
        if (isValidRow(row)) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
            for (int i = 0; i < tableRow.getChildCount(); i++) {
                TextView cell = (TextView) tableRow.getChildAt(i);
                Drawable background = DrawableUtil.updateDrawableInteriorColor(getContext(), R.drawable.cell_border, color);
                cell.setBackground(background);
            }
        }
    }

    public void setColumnColor(int column, int color) {
        if (isValidColumn(column)) {
            for (int i = 0; i < tableLayout.getChildCount(); i++) {
                TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
                if (column < tableRow.getChildCount()) {
                    TextView cell = (TextView) tableRow.getChildAt(column);
                    cell.setBackgroundResource(R.drawable.cell_border); // Reset to default background
                    Drawable background = DrawableUtil.updateDrawableInteriorColor(getContext(), R.drawable.cell_border, color);
                    cell.setBackground(background);
                }
            }
        }
    }

    public void setCellTextColor(int row, int column, int color) {
        if (isValidPosition(row, column)) {
            TextView cell = getCellAt(row, column);
            cell.setTextColor(color);
        }
    }

    public void setCellTextSize(int row, int column, float size) {
        if (isValidPosition(row, column)) {
            TextView cell = getCellAt(row, column);
            cell.setTextSize(size);
        }
    }

    public void setCellTextStyle(int row, int column, int style) {
        if (isValidPosition(row, column)) {
            TextView cell = getCellAt(row, column);
            cell.setTypeface(null, style);
        }
    }

    public void insertRow(int position, String[] cellValues) {
        TableRow row = createTableRow(cellValues);
        resetRowProperties(row); // Reset properties for inserted row
        tableLayout.addView(row, position);
        if (isRowNumberingEnabled) {
            updateRowNumbers();
        }
        updateAllRowsToLongestRow();
        adjustColumnWidths();
    }
    private void updateAllRowsToLongestRow() {
        int longestRowCells = getLongestRowCells();
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            boolean isHeaderRow = (hasHeader && i == 0);
            while (row.getChildCount() < longestRowCells) {
                if (isHeaderRow) {
                    row.addView(createHeaderCell(""));
                } else {
                    row.addView(createCell(""));
                }
            }
        }
        adjustColumnWidths();
    }

    private void adjustColumnWidths() {
        int[] columnWidths = measureColumnWidths();
        setColumnWidths(columnWidths);
    }
    public void removeCell(int row, int column) {
        if (isValidPosition(row, column)) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
            if (tableRow.getChildCount() > 1) {
                TextView cell = (TextView) tableRow.getChildAt(column);
                cell.setTextColor(TEXT_COLOR);
                cell.setTypeface(null, Typeface.NORMAL);
                cell.setTextSize(TEXT_SIZE);
                cell.setBackgroundResource(R.drawable.cell_border);
                cell.setText(""); // Empty the cell content
                cell.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
                cell.setSingleLine(false);
                cell.setEllipsize(null);
            } else {
                tableRow.removeViewAt(column);
            }
        }
    }

    public void setCellText(int row, int column, String text) {
        if (isValidPosition(row, column)) {
            TextView cell = getCellAt(row, column);
            cell.setText(text);
        }
    }

    public void addCellToRow(int row, String cellValue) {
        if (isValidRow(row)) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
            int columnIndex = tableRow.getChildCount(); // The new column index will be at the end
            tableRow.addView(createCell(cellValue));

            // Add empty cells to other rows if needed
            for (int i = 0; i < tableLayout.getChildCount(); i++) {
                // Skip the header row if it exists
                if (i != row && (!hasHeader || i > 0)) {
                    TableRow otherRow = (TableRow) tableLayout.getChildAt(i);
                    while (otherRow.getChildCount() <= columnIndex) {
                        otherRow.addView(createCell(""));
                    }
                }
            }

            updateAllRowsToLongestRow();
            adjustColumnWidths();
        }
    }

    public void addHeaderRow(String[] headerValues) {
        hasHeader = true;
        TableRow headerRow = createTableRow(headerValues, true);
        if (tableLayout.getChildCount() > 0) {
            tableLayout.removeViewAt(0); // Remove any existing header row
        }
        tableLayout.addView(headerRow, 0);
        updateAllRowsToLongestRow();
        adjustColumnWidths();
    }

    public void setCellClickListener(int row, int column, View.OnClickListener listener) {
        if (isValidPosition(row, column)) {
            TextView cell = getCellAt(row, column);
            cell.setOnClickListener(listener);
        }
    }

    public void addColumnWithHeader(String[] columnValues, String headerValue) {
        if (!hasHeader) {
            addHeaderRow(new String[]{headerValue});
        } else {
            TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
            TextView headerCell = createHeaderCell(headerValue); // Use createHeaderCell for header style
            headerRow.addView(headerCell);
        }
        addColumn(columnValues);
        int newColumnIndex = getLongestRowCells() - 1;
        setColumnColor(newColumnIndex, Color.TRANSPARENT); // Set transparent color for the new column

        // Ensure the header cell uses the correct style
        if (hasHeader) {
            TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
            if (newColumnIndex < headerRow.getChildCount()) {
                TextView headerCell = (TextView) headerRow.getChildAt(newColumnIndex);
                applyHeaderStyle(headerCell);
            }
        }
    }

    private void applyHeaderStyle(TextView cell) {
        cell.setBackgroundResource(R.drawable.header_cell_border); // Ensure default header background
        cell.setTypeface(null, Typeface.BOLD);
        cell.setTextSize(TEXT_SIZE);
        cell.setTextColor(TEXT_COLOR);
        cell.setGravity(Gravity.CENTER);
        cell.setSingleLine(false);
        cell.setEllipsize(null);
    }

    public void addColumn(String[] columnValues) {
        int longestRowCells = getLongestRowCells();
        for (int i = hasHeader ? 1 : 0; i < columnValues.length + (hasHeader ? 1 : 0); i++) {
            if (i < tableLayout.getChildCount()) {
                TableRow row = (TableRow) tableLayout.getChildAt(i);
                TextView cell = createCell(columnValues[i - (hasHeader ? 1 : 0)]);
                cell.setBackgroundResource(R.drawable.cell_border); // Ensure default background
                row.addView(cell);
            } else {
                TableRow newRow = createEmptyRow(longestRowCells, false);
                TextView cell = createCell(columnValues[i - (hasHeader ? 1 : 0)]);
                cell.setBackgroundResource(R.drawable.cell_border); // Ensure default background
                newRow.addView(cell);
                tableLayout.addView(newRow);
            }
        }
        updateAllRowsToLongestRow();
        adjustColumnWidths();
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

    private TextView createCell(String text) {
        TextView cell = new TextView(getContext());
        cell.setText(text);
        cell.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
        cell.setBackgroundResource(R.drawable.cell_border); // Ensure default background
        cell.setSingleLine(false);
        cell.setEllipsize(null);
        return cell;
    }

    private TextView createHeaderCell(String text) {
        TextView cell = new TextView(getContext());
        cell.setText(text);
        cell.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
        cell.setBackgroundResource(R.drawable.header_cell_border); // Ensure default header background
        cell.setTypeface(null, Typeface.BOLD);
        cell.setTextSize(TEXT_SIZE);
        cell.setTextColor(TEXT_COLOR);
        cell.setGravity(Gravity.CENTER);
        cell.setSingleLine(false);
        cell.setEllipsize(null);
        return cell;
    }
    private TableRow createTableRow(String[] cellValues) {
        return createTableRow(cellValues, false);
    }

    private TableRow createTableRow(String[] cellValues, boolean isHeader) {
        TableRow row = new TableRow(getContext());
        for (String cellValue : cellValues) {
            TextView cell = isHeader ? createHeaderCell(cellValue) : createCell(cellValue);
            row.addView(cell);
        }
        return row;
    }

    private TableRow createEmptyHeaderRow() {
        TableRow headerRow = new TableRow(getContext());
        if (!hasHeader) {
            headerRow.setVisibility(GONE);
        }
        return headerRow;
    }

    private TableRow createEmptyRow(int cellCount, boolean isHeader) {
        TableRow row = new TableRow(getContext());
        for (int j = 0; j < cellCount; j++) {
            TextView cell = isHeader ? createHeaderCell("") : createCell("");
            row.addView(cell);
        }
        return row;
    }

    private TextView getCellAt(int row, int column) {
        TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
        return (TextView) tableRow.getChildAt(column);
    }

    private int getLongestRowCells() {
        int maxCells = 0;
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            if (row.getChildCount() > maxCells) {
                maxCells = row.getChildCount();
            }
        }
        return maxCells;
    }
    private int[] measureColumnWidths() {
        int columnCount = getLongestRowCells();
        int[] columnWidths = new int[columnCount];

        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                TextView cell = (TextView) row.getChildAt(j);
                int cellWidth = measureCellWidth(cell);
                if (cellWidth > columnWidths[j]) {
                    columnWidths[j] = cellWidth;
                }
            }
        }
        return columnWidths;
    }

    private int measureCellWidth(TextView cell) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        cell.measure(widthSpec, heightSpec);
        return cell.getMeasuredWidth();
    }

    private void setColumnWidths(int[] columnWidths) {
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                TextView cell = (TextView) row.getChildAt(j);
                TableRow.LayoutParams params = (TableRow.LayoutParams) cell.getLayoutParams();
                params.width = columnWidths[j];
                cell.setLayoutParams(params);
            }
        }
    }
    public void enableColumnSorting() {
        if (!hasHeader) return;

        TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
        for (int i = 0; i < headerRow.getChildCount(); i++) {
            final int columnIndex = i;
            TextView headerCell = (TextView) headerRow.getChildAt(i);
            headerCell.setOnClickListener(v -> sortColumn(columnIndex));
        }
    }

    private void sortColumn(int columnIndex) {
        List<TableRow> rows = new ArrayList<>();
        for (int i = 1; i < tableLayout.getChildCount(); i++) {
            rows.add((TableRow) tableLayout.getChildAt(i));
        }

        boolean isNumericColumn = true;
        for (TableRow row : rows) {
            TextView cell = (TextView) row.getChildAt(columnIndex);
            if (!isNumeric(cell.getText().toString())) {
                isNumericColumn = false;
                break;
            }
        }

        if (isNumericColumn) {
            Collections.sort(rows, (row1, row2) -> {
                TextView cell1 = (TextView) row1.getChildAt(columnIndex);
                TextView cell2 = (TextView) row2.getChildAt(columnIndex);
                Integer value1 = Integer.valueOf(cell1.getText().toString());
                Integer value2 = Integer.valueOf(cell2.getText().toString());
                return value1.compareTo(value2);
            });
        } else {
            Collections.sort(rows, (row1, row2) -> {
                TextView cell1 = (TextView) row1.getChildAt(columnIndex);
                TextView cell2 = (TextView) row2.getChildAt(columnIndex);
                return cell1.getText().toString().compareTo(cell2.getText().toString());
            });
        }

        for (int i = 0; i < rows.size(); i++) {
            tableLayout.removeView(rows.get(i));
            tableLayout.addView(rows.get(i), i + 1);
        }
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public TableViewMemento saveToMemento() {
        return new TableViewMemento(getCurrentState());
    }

    public void restoreFromMemento(TableViewMemento memento) {
        restoreState(memento.getState());
    }

    private List<TableRow> getCurrentState() {
        List<TableRow> state = new ArrayList<>();
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            TableRow rowCopy = new TableRow(getContext());
            for (int j = 0; j < row.getChildCount(); j++) {
                TextView cell = (TextView) row.getChildAt(j);
                TextView cellCopy = new TextView(getContext());
                cellCopy.setText(cell.getText());
                cellCopy.setLayoutParams(cell.getLayoutParams());
                cellCopy.setBackground(cell.getBackground());
                cellCopy.setTextColor(cell.getTextColors());
                cellCopy.setTypeface(cell.getTypeface());
                cellCopy.setTextSize(TypedValue.COMPLEX_UNIT_PX, cell.getTextSize());
                rowCopy.addView(cellCopy);
            }
            state.add(rowCopy);
        }
        return state;
    }

    private void restoreState(List<TableRow> state) {
        tableLayout.removeAllViews();
        for (TableRow row : state) {
            tableLayout.addView(row);
        }
    }
}
