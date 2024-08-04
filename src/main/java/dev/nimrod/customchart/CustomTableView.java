package dev.nimrod.customchart;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
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

    private boolean[] columnHasHeader;

    private TableViewCaretaker caretaker = new TableViewCaretaker();

    private String numberingHeaderText;
    private boolean isRowNumberingEnabled = false;
    private boolean hasHeader = false;
    private AppCompatEditText filterText;
    private MaterialButton filterButton;
    private RecyclerView recyclerView;
    private TableAdapter tableAdapter;
    private List<List<Cell>> tableData;
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
        tableAdapter = new TableAdapter(context, tableData);
        recyclerView.setAdapter(tableAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        int padding = getResources().getDimensionPixelSize(R.dimen.table_padding);
        recyclerView.setPadding(padding, padding, padding, padding);
        recyclerView.setClipToPadding(false);

//        lineTableView.setClipToPadding(false);

        recyclerView.post(() -> {
            int width = recyclerView.getWidth();
            int height = recyclerView.getHeight();
        });

        filterButton.setOnClickListener(v -> {
            String query = filterText.getText().toString();
            if (TextUtils.isEmpty(query)) {
                clearFilter();
            } else {
                filterRows(query);
            }
        });

        setupItemTouchHelper();
    }

//    public void setShowFullText(boolean showFull) {
//        tableAdapter.setShowFullText(showFull);
//    }
//    public void setCellSize(int width, int height) {
//        tableAdapter.setCellSize(width, height);
//    }
    public void setTitle(String title) {
        tableTitle.setText(title);
        tableTitle.setVisibility(VISIBLE);
    }

    public void hideTitle() {
        tableTitle.setVisibility(GONE);
    }

    public void addRow(String[] cellValues) {
        List<Cell> rowData = new ArrayList<>();
        for (String value : cellValues) {
            rowData.add(new Cell(value));
        }
        tableData.add(rowData);
        tableAdapter.notifyItemInserted(tableData.size() - 1);
        tableAdapter.recalculateCellSizes();
    }

    public void removeRow(int position) {
        if (position >= 0 && position < tableData.size()) {
            tableData.remove(position);
            tableAdapter.notifyItemRemoved(position);
            if (isRowNumberingEnabled) {
                updateRowNumbers();
            }
            tableAdapter.recalculateCellSizes();
        }
    }
    private int getMaxColumns() {
        int maxColumns = 0;
        for (List<Cell> row : tableData) {
            maxColumns = Math.max(maxColumns, row.size());
        }
        return maxColumns;
    }
    public void removeColumn(int column) {
        for (List<Cell> row : tableData) {
            if (column >= 0 && column < row.size()) {
                row.remove(column);
            }
        }
        tableAdapter.notifyDataSetChanged();
    }
    public void setCellColor(int row, int column, int color) {
        if (isValidPosition(row, column)) {
            Cell cell = tableData.get(row).get(column);
            cell.setBackgroundColor(color);
            tableAdapter.notifyItemChanged(row);
        }
    }

    public void setCellTextColor(int row, int column, int color) {
        if (isValidPosition(row, column)) {
            Cell cell = tableData.get(row).get(column);
            cell.setTextColor(color);
            tableAdapter.notifyItemChanged(row);
        }
    }

    public void setCellTextSize(int row, int column, float size) {
        if (isValidPosition(row, column)) {
            Cell cell = tableData.get(row).get(column);
            cell.setTextSize(size);
            tableAdapter.notifyItemChanged(row);
        }
    }
    public void setCellTypeface(int row, int column, Typeface typeface) {
        if (row >= 0 && row < tableData.size() && column >= 0 && column < tableData.get(row).size()) {
            Cell cell = tableData.get(row).get(column);
            cell.setTypeface(typeface);
            tableAdapter.setCellStyle(row, column, cell);
        }
    }

    public void setRowColor(int row, int color) {
        if (row >= 0 && row < tableData.size()) {
            for (Cell cell : tableData.get(row)) {
                cell.setBackgroundColor(color);
            }
            tableAdapter.notifyItemChanged(row);
        }
    }

    public void setColumnColor(int column, int color) {
        for (int i = 0; i < tableData.size(); i++) {
            if (column >= 0 && column < tableData.get(i).size()) {
                tableData.get(i).get(column).setBackgroundColor(color);
            }
        }
        tableAdapter.notifyDataSetChanged();
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
    public void filterRows(String query) {
        if (caretaker.getMemento() == null) {
            caretaker.saveState(this);
        }
        List<List<Cell>> filteredData = new ArrayList<>();
        for (int i = hasHeader ? 1 : 0; i < tableData.size(); i++) {
            List<Cell> row = tableData.get(i);
            boolean rowContainsQuery = false;
            for (Cell cell : row) {
                if (cell.getText().contains(query)) {
                    rowContainsQuery = true;
                    break;
                }
            }
            if (rowContainsQuery) {
                filteredData.add(row);
            }
        }
        tableAdapter.setData(filteredData);
    }    public void clearFilter() {
        caretaker.restoreState(this);
        caretaker.clearMemento();
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
        for (int i = hasHeader ? 1 : 0; i < tableData.size(); i++) {
            List<Cell> row = tableData.get(i);
            if (isRowNumberingEnabled) {
                if (row.size() > 0 && !row.get(0).getText().equals(String.valueOf(i))) {
                    row.add(0, new Cell(String.valueOf(i)));
                } else {
                    row.get(0).setText(String.valueOf(i));
                }
            } else {
                if (row.size() > 0) {
                    row.remove(0);
                }
            }
        }
        tableAdapter.notifyDataSetChanged();
    }


    public void setNumberingHeaderText(String text) {
        this.numberingHeaderText = text;
        if (isRowNumberingEnabled && hasHeader) {
            List<Cell> headerRow = tableData.get(0);
            headerRow.get(0).setText(numberingHeaderText);
            tableAdapter.notifyItemChanged(0);
        }
    }

    public void addHeaderRow(String[] headerValues) {
        hasHeader = true;
        List<Cell> headerRow = new ArrayList<>();
        for (String value : headerValues) {
            headerRow.add(new Cell(value));
        }
        if (isRowNumberingEnabled) {
            headerRow.add(0, new Cell(numberingHeaderText));
        }
        tableData.add(0, headerRow);
        tableAdapter.notifyItemInserted(0);
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
        tableAdapter.setHasHeader(hasHeader);
    }

    public void setRowNumberingEnabled(boolean enabled) {
        this.isRowNumberingEnabled = enabled;
        tableAdapter.setRowNumberingEnabled(enabled);
        updateRowNumbers();
        tableAdapter.recalculateCellSizes();
    }

    private boolean isValidPosition(int row, int column) {
        return row >= 0 && row < tableData.size() && column >= 0 && column < tableData.get(row).size();
    }

    public TableViewMemento saveToMemento() {
        return new TableViewMemento(deepCopyTableData(), hasHeader, isRowNumberingEnabled);
    }

    public void restoreFromMemento(TableViewMemento memento) {
        this.tableData = memento.getState();
        this.hasHeader = memento.isHasHeader();
        this.isRowNumberingEnabled = memento.isRowNumberingEnabled();

        tableAdapter.setData(this.tableData);
        tableAdapter.setHasHeader(this.hasHeader);
        tableAdapter.setRowNumberingEnabled(this.isRowNumberingEnabled);

        tableAdapter.notifyDataSetChanged();
    }

    private List<List<Cell>> deepCopyTableData() {
        List<List<Cell>> copy = new ArrayList<>();
        for (List<Cell> row : tableData) {
            List<Cell> rowCopy = new ArrayList<>();
            for (Cell cell : row) {
                Cell cellCopy = new Cell(cell.getText());
                cellCopy.setBackgroundColor(cell.getBackgroundColor());
                cellCopy.setTextColor(cell.getTextColor());
                cellCopy.setTextSize(cell.getTextSize());
                cellCopy.setTypeface(cell.getTypeface());
                rowCopy.add(cellCopy);
            }
            copy.add(rowCopy);
        }
        return copy;
    }
}