package dev.nimrod.customchart;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import dev.nimrod.customchart.Util.TableViewCaretaker;
import dev.nimrod.customchart.Util.TableViewMemento;

public class CustomTableView extends RelativeLayout {
    private String numberingHeaderText = "NUMB";
    private boolean isNumberColumnVisible = true;
    private boolean hasHeader = false;
    private AppCompatEditText filterText;
    private MaterialButton filterButton;
    private RecyclerView recyclerView;
    private TableAdapter tableAdapter;
    private List<Row> tableData;
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
        LayoutInflater.from(context).inflate(R.layout.custom_table_view, this, true);

        recyclerView = findViewById(R.id.recycler_view);
        filterText = findViewById(R.id.filter_text);
        filterButton = findViewById(R.id.filter_button);
        tableTitle = findViewById(R.id.table_title);

        tableData = new ArrayList<>();
        tableAdapter = new TableAdapter(context, new ArrayList<>());
        recyclerView.setAdapter(tableAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }

            @Override
            public boolean canScrollHorizontally() {
                return true;
            }
        });

        setupItemTouchHelper();
    }

    public void setTitle(String title) {
        tableTitle.setText(title);
        tableTitle.setVisibility(VISIBLE);
    }

    public void hideTitle() {
        tableTitle.setVisibility(GONE);
    }

    public void addRow(String[] cellValues) {
        List<Cell> cellList = new ArrayList<>();

        cellList.add(new Cell(String.valueOf(tableData.size() + (hasHeader ? 0 : 1))));

        for (String value : cellValues) {
            cellList.add(new Cell(value));
        }
        Row newRow = new Row(cellList);
        tableData.add(newRow);

        updateRowNumbers(); // Ensure numbers are correctly set
        tableAdapter.setData(tableData);
        tableAdapter.notifyItemInserted(tableData.size() - 1);
    }

    public void removeRow(int position) {
        if (position >= 0 && position < tableData.size()) {
            tableData.remove(position);
            tableAdapter.setData(tableData);
            tableAdapter.notifyItemRemoved(position);
            if (isNumberColumnVisible) {
                updateRowNumbers();
            }
        }
    }

    public void removeColumn(int column) {
        for (Row row : tableData) {
            if (column >= 0 && column < row.getCellCount()) {
                row.removeCell(column);
            }
        }
        tableAdapter.setData(tableData);
        tableAdapter.notifyDataSetChanged();
    }

    public void setCellColor(int row, int column, int color) {
        if (isValidPosition(row, column)) {
            Cell cell = tableData.get(row).getCell(column);
            cell.setBackgroundColor(color);
            tableAdapter.updateCell(row, column, cell);
        }
    }

    public void setCellTextColor(int row, int column, int color) {
        if (isValidPosition(row, column)) {
            Cell cell = tableData.get(row).getCell(column);
            cell.setTextColor(color);
            tableAdapter.updateCell(row, column, cell);
        }
    }

    public void setCellTypeface(int row, int column, Typeface typeface) {
        if (isValidPosition(row, column)) {
            Cell cell = tableData.get(row).getCell(column);
            cell.setTypeface(typeface);
            tableAdapter.updateCell(row, column, cell);
        }
    }

    public void setCellTextSize(int row, int column, float size) {
        if (isValidPosition(row, column)) {
            Cell cell = tableData.get(row).getCell(column);
            cell.setTextSize(size);
            tableAdapter.updateCellTextSize(row, column, size);
        }
    }
    public void setRowColor(int row, int color) {
        if (row >= 0 && row < tableData.size()) {
            Row currentRow = tableData.get(row);
            for (int i = 0; i < currentRow.getCellCount(); i++) {
                Cell cell = currentRow.getCell(i);
                cell.setBackgroundColor(color);
                tableAdapter.updateCell(row, i, cell);
            }
        }
    }

    public void setColumnColor(int column, int color) {
        for (int i = 0; i < tableData.size(); i++) {
            if (isValidPosition(i, column)) {
                Cell cell = tableData.get(i).getCell(column);
                cell.setBackgroundColor(color);
                tableAdapter.updateCell(i, column, cell);
            }
        }
    }

    private void setupItemTouchHelper() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                if (hasHeader && (fromPosition == 0 || toPosition == 0)) {
                    return false;
                }
                tableAdapter.moveItem(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Do nothing
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

//    public void filterRows(String query) {
//        if (caretaker.getMemento() == null) {
//            caretaker.saveState(this);
//        }
//        List<Row> filteredData = new ArrayList<>();
//        for (int i = hasHeader ? 1 : 0; i < tableData.size(); i++) {
//            Row row = tableData.get(i);
//            boolean rowContainsQuery = false;
//            for (int j = 0; j < row.getCellCount(); j++) {
//                if (row.getCell(j).getText().contains(query)) {
//                    rowContainsQuery = true;
//                    break;
//                }
//            }
//            if (rowContainsQuery) {
//                filteredData.add(row);
//            }
//        }
//        tableAdapter.setData(filteredData);
//    }
//
//    public void clearFilter() {
//        caretaker.restoreState(this);
//        caretaker.clearMemento();
//    }

    public void showNumberColumn() {
        if (!isNumberColumnVisible) {
            isNumberColumnVisible = true;
            updateRowNumbers();
        }
    }

    public void hideNumberColumn() {
        if (isNumberColumnVisible) {
            isNumberColumnVisible = false;
            updateRowNumbers();
        }
    }

    private void updateRowNumbers() {
        for (int i = hasHeader ? 1 : 0; i < tableData.size(); i++) {
            Row row = tableData.get(i);
            if (row.getCellCount() > 0) {
                if (i == 0 && hasHeader) {
                    row.setCell(0, new Cell(numberingHeaderText));
                } else {
                    row.setCell(0, new Cell(String.valueOf(i - (hasHeader ? 1 : 0))));
                }
            } else {
                row.addCell(new Cell(String.valueOf(i - (hasHeader ? 1 : 0))));
            }
        }
        tableAdapter.setNumberColumnVisible(isNumberColumnVisible);
        tableAdapter.notifyDataSetChanged();
    }

    public void setCellText(int row, int column, String text) {
        if (isValidPosition(row, column)) {
            Cell cell = tableData.get(row).getCell(column);
            cell.setText(text);
            tableAdapter.updateCell(row, column, cell);
        }
    }

    public void setNumberingHeaderText(String text) {
        this.numberingHeaderText = text;
        if (isNumberColumnVisible && hasHeader && !tableData.isEmpty()) {
            Row headerRow = tableData.get(0);
            Cell numberingCell = headerRow.getCell(0);
            numberingCell.setText(numberingHeaderText);
            tableAdapter.measureRow(headerRow, new Paint()); // Recalculate the width based on the new text
            tableAdapter.notifyItemChanged(0);
        }
    }


    public void addHeaderRow(String[] headerValues) {
        hasHeader = true;
        List<Cell> headerCells = new ArrayList<>();

        for (String value : headerValues) {
            Cell headerCell = new Cell(value);
            headerCell.setBorderDrawableResId(R.drawable.header_cell_border); // Use the header cell frame
            headerCell.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD)); // Set bold font for header
            headerCells.add(headerCell);
        }

        if (isNumberColumnVisible) {
            Cell numberHeaderCell = new Cell(numberingHeaderText);
            numberHeaderCell.setBorderDrawableResId(R.drawable.header_cell_border);
            headerCells.add(0, numberHeaderCell);
        }

        Row headerRow = new Row(headerCells);
        tableData.add(0, headerRow);

        if (tableData.size() > 1) {
            Row rowBelowHeader = tableData.get(1);
            for (int i = 0; i < rowBelowHeader.getCellCount(); i++) {
                Cell cell = rowBelowHeader.getCell(i);
                cell.setBorderDrawableResId(R.drawable.cell_border); // Set regular cell border
                cell.setTypeface(Typeface.DEFAULT); // Set regular font

                if (i == 0 && isNumberColumnVisible) {
                    cell.setText("0"); // Set numbering cell to "0"
                }
            }
        }

        tableAdapter.setData(tableData);
        tableAdapter.notifyItemInserted(0);
        tableAdapter.notifyItemChanged(1);
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;

        if (!tableData.isEmpty()) {
            Row firstRow = tableData.get(0);
            int borderDrawableResId = hasHeader ? R.drawable.header_cell_border : R.drawable.cell_border;

            for (int i = 0; i < firstRow.getCellCount(); i++) {
                Cell cell = firstRow.getCell(i);
                cell.setBorderDrawableResId(borderDrawableResId);
            }

            tableAdapter.setData(tableData);
            tableAdapter.notifyItemChanged(0); // Refresh the first row to apply the new border
        }
        tableAdapter.setHasHeader(hasHeader);
    }


    private boolean isValidPosition(int row, int column) {
        return row >= 0 && row < tableData.size() && column >= 0 && column < tableData.get(row).getCellCount();
    }

    public TableViewMemento saveToMemento() {
        return new TableViewMemento(deepCopyTableData(), hasHeader, isNumberColumnVisible);
    }

    public void restoreFromMemento(TableViewMemento memento) {
        this.tableData = memento.getState();
        this.hasHeader = memento.isHasHeader();
        this.isNumberColumnVisible = memento.isRowNumberingEnabled();

        tableAdapter.setData(this.tableData);
        tableAdapter.setHasHeader(this.hasHeader);
        tableAdapter.setNumberColumnVisible(this.isNumberColumnVisible);

        tableAdapter.notifyDataSetChanged();
    }

    private List<Row> deepCopyTableData() {
        List<Row> copy = new ArrayList<>();
        for (Row row : tableData) {
            List<Cell> cellsCopy = new ArrayList<>();
            for (int i = 0; i < row.getCellCount(); i++) {
                Cell cell = row.getCell(i);
                Cell cellCopy = new Cell(cell.getText());
                cellCopy.setBackgroundColor(cell.getBackgroundColor());
                cellCopy.setTextColor(cell.getTextColor());
                cellCopy.setTextSize(cell.getTextSize());
                cellCopy.setTypeface(cell.getTypeface());
                cellsCopy.add(cellCopy);
            }
            copy.add(new Row(cellsCopy));
        }
        return copy;
    }
}