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
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dev.nimrod.customchart.Util.TableViewCaretaker;
import dev.nimrod.customchart.Util.TableViewMemento;

public class CustomTableView extends RelativeLayout {
    private static final int DEFAULT_PADDING = 8;
    private static final String NUMBERING_TITLE = "NUM";
    private static final int TEXT_COLOR = Color.BLACK;
    private static final float TEXT_SIZE = 18;
    private int highlightColor = Color.YELLOW;
    private boolean[] columnHasHeader;

    private TableViewCaretaker caretaker = new TableViewCaretaker();

    private String numberingHeaderText;
    private boolean isRowNumberingEnabled = false;
    private boolean hasHeader = false;
    private AppCompatEditText filterText;
    private MaterialButton filterButton;
    private RecyclerView recyclerView;
    private TableAdapter tableAdapter;
    private List<List<String>> tableData;
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
    public void setTitle(String title) {
        tableTitle.setText(title);
        tableTitle.setVisibility(VISIBLE);
    }

    public void hideTitle() {
        tableTitle.setVisibility(GONE);
    }

    public void addRow(String[] cellValues) {
        List<String> rowData = new ArrayList<>(Arrays.asList(cellValues));
        int maxColumns = getMaxColumns();
        while (rowData.size() < maxColumns) {
            rowData.add("");
        }
        tableData.add(rowData);
        tableAdapter.recalculateCellSizes();
    }
    public void removeRow(int position) {
        if (position >= 0 && position < tableData.size()) {
            tableData.remove(position);
            tableAdapter.notifyItemRemoved(position);
            if (isRowNumberingEnabled) {
                updateRowNumbers();
            }
        }
    }


    public void setColumnHeader(int column, boolean hasHeader) {
        if (columnHasHeader == null || column >= columnHasHeader.length) {
            boolean[] newColumnHasHeader = new boolean[Math.max(column + 1, getMaxColumns())];
            if (columnHasHeader != null) {
                System.arraycopy(columnHasHeader, 0, newColumnHasHeader, 0, columnHasHeader.length);
            }
            columnHasHeader = newColumnHasHeader;
        }
        columnHasHeader[column] = hasHeader;
        tableAdapter.setColumnHasHeader(columnHasHeader);
        tableAdapter.notifyDataSetChanged();
    }
    public void addColumn(String[] columnValues) {
        int maxColumns = getMaxColumns();
        for (int i = 0; i < tableData.size(); i++) {
            if (i < columnValues.length) {
                tableData.get(i).add(columnValues[i]);
            } else {
                tableData.get(i).add("");
            }
        }
        tableAdapter.recalculateCellSizes();
    }
    private int getMaxColumns() {
        int maxColumns = 0;
        for (List<String> row : tableData) {
            maxColumns = Math.max(maxColumns, row.size());
        }
        return maxColumns;
    }
    public void removeColumn(int column) {
        for (List<String> row : tableData) {
            if (column >= 0 && column < row.size()) {
                row.remove(column);
            }
        }
        tableAdapter.notifyDataSetChanged();
    }
    public void setRowColor(int row, int color) {
        for (int i = 0; i < tableData.get(row).size(); i++) {
            tableAdapter.setCellColor(row, i, color);
        }
    }

    public void setColumnColor(int column, int color) {
        for (int i = 0; i < tableData.size(); i++) {
            tableAdapter.setCellColor(i, column, color);
        }
    }

    public void setCellColor(int row, int column, int color) {
        tableAdapter.setCellColor(row, column, color);
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
        List<List<String>> filteredData = new ArrayList<>();
        for (int i = hasHeader ? 1 : 0; i < tableData.size(); i++) {
            List<String> row = tableData.get(i);
            boolean rowContainsQuery = false;
            for (String cell : row) {
                if (cell.contains(query)) {
                    rowContainsQuery = true;
                    break;
                }
            }
            if (rowContainsQuery) {
                filteredData.add(row);
            }
        }
        tableAdapter.setData(filteredData);
    }
    public void clearFilter() {
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
            List<String> row = tableData.get(i);
            if (isRowNumberingEnabled) {
                if (row.size() > 0 && !row.get(0).equals(String.valueOf(i))) {
                    row.add(0, String.valueOf(i));
                } else {
                    row.set(0, String.valueOf(i));
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
            List<String> headerRow = tableData.get(0);
            headerRow.set(0, numberingHeaderText);
            tableAdapter.notifyItemChanged(0);
        }
    }

    public void addHeaderRow(String[] headerValues) {
        hasHeader = true;
        List<String> headerRow = new ArrayList<>(Arrays.asList(headerValues));
        if (isRowNumberingEnabled) {
            headerRow.add(0, numberingHeaderText);
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
    }

    public TableViewMemento saveToMemento() {
        return new TableViewMemento(new ArrayList<>(tableData), hasHeader, isRowNumberingEnabled);
    }

    public void restoreFromMemento(TableViewMemento memento) {
        this.tableData = new ArrayList<>(memento.getState());
        this.hasHeader = memento.isHasHeader();
        this.isRowNumberingEnabled = memento.isRowNumberingEnabled();

        tableAdapter.setData(this.tableData);
        tableAdapter.setHasHeader(this.hasHeader);
        tableAdapter.setRowNumberingEnabled(this.isRowNumberingEnabled);

        tableAdapter.notifyDataSetChanged();
    }
}