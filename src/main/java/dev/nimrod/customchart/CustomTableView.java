package dev.nimrod.customchart;

import static android.net.http.SslCertificate.saveState;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import dev.nimrod.customchart.Util.TableViewCaretaker;
import dev.nimrod.customchart.Util.TableViewMemento;
import dev.nimrod.customchart.utilites.TableConstants;

public class CustomTableView extends RelativeLayout {
    private static final String TAG = "CustomTableView";

    private String numberingHeaderText = TableConstants.DEFAULT_NUMBERING_HEADER;
    private boolean isNumberColumnVisible = true;
    private boolean hasHeader = false;
    private AppCompatEditText filterText;
    private MaterialButton filterButton;
    private MaterialButton clearFilterButton;
    private TableViewCaretaker caretaker;
    private Stack<TableViewMemento> undoStack;
    private Stack<TableViewMemento> redoStack;
    private FloatingActionButton undoButton;
    private FloatingActionButton redoButton;
    private TableAdapter tableAdapter;
    private List<Row> tableData;
    private MaterialTextView tableTitle;
    private int sortedColumn = -1;
    private boolean isAscending = true;
    private CustomRecyclerView recyclerView;
    private FloatingActionButton leftScrollButton;
    private FloatingActionButton rightScrollButton;
    private boolean isHorizontalScrollingEnabled = false;
    private boolean isSlidingEnabled = true;
    private boolean isFiltering = false;
    private FloatingActionButton fabMain;

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

        recyclerView = findViewById(R.id.table_recycler_view);
        leftScrollButton = findViewById(R.id.table_fab_scroll_left);
        rightScrollButton = findViewById(R.id.table_fab_scroll_right);
        CustomHorizontalScrollView horizontalScrollView = findViewById(R.id.table_scroll_horizontal);

        filterText = findViewById(R.id.table_edit_filter);
        filterButton = findViewById(R.id.table_btn_filter);
        clearFilterButton = findViewById(R.id.table_btn_clear_filter);
        tableTitle = findViewById(R.id.table_text_title);
        caretaker = new TableViewCaretaker();
        tableData = new ArrayList<>();
        tableAdapter = new TableAdapter(context, new ArrayList<>());
        recyclerView.setAdapter(tableAdapter);
        fabMain = findViewById(R.id.table_fab_main);

        undoButton = findViewById(R.id.table_fab_undo);
        redoButton = findViewById(R.id.table_fab_redo);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(tableAdapter);

        undoStack = new Stack<>();
        redoStack = new Stack<>();
        caretaker = new TableViewCaretaker();

        setupUndoRedoButtons();
        setupHorizontalScrollButtons(horizontalScrollView);
        setupFloatingActionButton();
        setupButtons();
        setupItemTouchHelper();
    }

    private void setupFloatingActionButton() {
        fabMain.setOnClickListener(v -> showPopupMenu(v));
    }
    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.custom_table_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            TableViewMemento beforeState = saveToMemento();
            undoStack.push(beforeState);

            int itemId = item.getItemId();
            if (itemId == R.id.table_menu_add_row) {
                showAddRowDialog();
                return true;
            } else if (itemId == R.id.table_menu_remove_row) {
                showRemoveRowDialog();
                return true;
            } else if (itemId == R.id.table_menu_add_column) {
                showAddColumnDialog();
                return true;
            } else if (itemId == R.id.table_menu_remove_column) {
                showRemoveColumnDialog();
                return true;
            } else if (itemId == R.id.table_menu_toggle_title) {
                if (tableTitle.getVisibility() == VISIBLE) {
                    hideTitle();
                } else {
                    setTitle(getContext().getString(R.string.table_title));
                }
                return true;
            } else if (itemId == R.id.table_menu_toggle_numbering) {
                if (isNumberColumnVisible) {
                    hideNumberColumn();
                } else {
                    showNumberColumn();
                }
                return true;
            } else if (itemId == R.id.table_menu_toggle_header) {
                toggleHeader();
                return true;
            }
            return false;
        });
        popup.show();
    }


    private void toggleHeader() {
        if (hasHeader) {
            setHasHeader(false);  // Remove header
        } else {
            setHasHeader(true);
        }
    }


    private void showAddRowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Row");

        final AppCompatEditText input = new AppCompatEditText(getContext());
        input.setHint("Enter values separated by //");

        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String inputText = input.getText().toString();
            String[] cellValues = inputText.split("//");
            addRow(cellValues);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showRemoveRowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Remove Row");

        final AppCompatEditText input = new AppCompatEditText(getContext());
        input.setHint("Enter row number to remove");

        builder.setView(input);

        builder.setPositiveButton("Remove", (dialog, which) -> {
            int rowIndex = Integer.parseInt(input.getText().toString());
            removeRow(rowIndex);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void showAddColumnDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Column");

        final AppCompatEditText input = new AppCompatEditText(getContext());
        input.setHint("Enter values separated by //");

        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String inputText = input.getText().toString();
            String[] columnValues = inputText.split("//");
            addColumn(columnValues);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void showRemoveColumnDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Remove Column");

        final AppCompatEditText input = new AppCompatEditText(getContext());
        input.setHint("Enter column number to remove");

        builder.setView(input);

        builder.setPositiveButton("Remove", (dialog, which) -> {
            int columnIndex = Integer.parseInt(input.getText().toString());
            removeColumn(columnIndex);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    private void setupHorizontalScrollButtons(CustomHorizontalScrollView horizontalScrollView) {
        leftScrollButton.setOnClickListener(v -> horizontalScrollView.smoothScrollBy(-500, 0)); // Scroll left
        rightScrollButton.setOnClickListener(v -> horizontalScrollView.smoothScrollBy(500, 0)); // Scroll right
    }


    private void setupSortingListener() {
        tableAdapter.setOnHeaderClickListener(columnIndex -> sortColumn(columnIndex));
    }

    private void removeSortingListener() {
        tableAdapter.setOnHeaderClickListener(null);
    }

    private void sortColumn(int columnIndex) {
        if (caretaker.getMemento() != null) {
            // Don't sort if filtering is active
            return;
        }

        if (columnIndex == sortedColumn) {
            isAscending = !isAscending;
        } else {
            sortedColumn = columnIndex;
            isAscending = true;
        }

        if (hasHeader && tableData.size() > 1) {
            Collections.sort(tableData.subList(1, tableData.size()),
                    (row1, row2) -> compareValues(row1.getCell(columnIndex).getText(),
                            row2.getCell(columnIndex).getText()));
            tableAdapter.setData(tableData);
            tableAdapter.notifyDataSetChanged();
        }
    }

    private int compareValues(String value1, String value2) {
        // Check for "Row X, Col Y" pattern
        String pattern = "Row (\\d+), Col (\\d+)";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m1 = r.matcher(value1);
        java.util.regex.Matcher m2 = r.matcher(value2);

        if (m1.find() && m2.find()) {
            int row1 = Integer.parseInt(m1.group(1));
            int row2 = Integer.parseInt(m2.group(1));
            int comparison = Integer.compare(row1, row2);
            if (comparison != 0) {
                return isAscending ? comparison : -comparison;
            }
            // If rows are the same, compare column numbers
            int col1 = Integer.parseInt(m1.group(2));
            int col2 = Integer.parseInt(m2.group(2));
            return isAscending ? Integer.compare(col1, col2) : Integer.compare(col2, col1);
        }

        // If not matching the pattern, try to parse as numbers
        try {
            double num1 = Double.parseDouble(value1);
            double num2 = Double.parseDouble(value2);
            return isAscending ? Double.compare(num1, num2) : Double.compare(num2, num1);
        } catch (NumberFormatException e) {
            // If parsing fails, compare as strings
            return isAscending ? value1.compareTo(value2) : value2.compareTo(value1);
        }
    }
    public void setTitle(String title) {
        tableTitle.setText(title);
        tableTitle.setVisibility(VISIBLE);

    }

    public void hideTitle() {
        tableTitle.setVisibility(GONE);

    }
    private void clearFilter() {
        isFiltering = false;
        fabMain.setVisibility(VISIBLE);

        caretaker.restoreState(this); // Restore original state
        caretaker.clearMemento(); // Clear the saved state
        tableAdapter.setData(tableData);
        tableAdapter.notifyDataSetChanged();
        filterText.setText(""); // Clear the filter text input
    }
    private void setupButtons() {
        filterButton.setOnClickListener(v -> {
            String query = filterText.getText().toString().trim();
            if (!query.isEmpty()) {
                saveOriginalTableData(); // Save the original table data before filtering
                filterRows(query);
            } else {
                clearFilter(); // Clear the filter if the text is empty
            }
        });

        clearFilterButton.setOnClickListener(v -> clearFilter()); // Clear the filter when clicked
    }

    private void filterRows(String query) {
        List<Row> filteredData = new ArrayList<>();
        String lowercaseQuery = query.toLowerCase();

        isFiltering = true;
        fabMain.setVisibility(GONE);

        for (Row row : tableData) {
            boolean rowContainsQuery = false;
            for (int i = 0; i < row.getCellCount(); i++) {
                Cell cell = row.getCell(i);
                String cellText = cell.getText().toLowerCase();

                if (cellText.contains(lowercaseQuery)) {
                    rowContainsQuery = true;
                    cell.setBackgroundColor(Color.YELLOW); // Highlight the matching cells in yellow
                } else {
                    cell.setBackgroundColor(Color.TRANSPARENT); // Reset non-matching cells
                }
            }
            if (rowContainsQuery) {
                filteredData.add(row);
            }
        }
        tableAdapter.setData(filteredData);
        tableAdapter.notifyDataSetChanged();
    }


    public void addRow(String[] cellValues) {
        List<Cell> cellList = new ArrayList<>();
        cellList.add(new Cell("")); // Placeholder for number column

        for (String value : cellValues) {
            cellList.add(createCell(value, false));
        }

        Row newRow = new Row(cellList);
        tableData.add(newRow);

        ensureTableCompleteness();
        updateRowNumbers();
        tableAdapter.setData(tableData);
        tableAdapter.notifyItemInserted(tableData.size() - 1);
    }

    public void removeRow(int position) {
        if (position > 0 || (position == 0 && hasHeader)) {
            if (position < tableData.size()) {
                if(position == 0) {
                    hasHeader = false;
                } else if (!hasHeader){
                    position --;
                }
                TableViewMemento beforeState = saveToMemento();
                undoStack.push(beforeState);
                redoStack.clear();

                tableData.remove(position);
                tableAdapter.setData(tableData);
                tableAdapter.notifyItemRemoved(position);
                if (isNumberColumnVisible) {
                    updateRowNumbers();
                }
                updateUndoRedoButtons();
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

    public String getCellText(int row, int column) {
        if (isValidPosition(row, column)) {
            return tableData.get(row).getCell(column).getText();
        }
        return "";
    }
    public void setCellTypeface(int row, int column, Typeface typeface) {
        if (isValidPosition(row, column)) {
            Cell cell = tableData.get(row).getCell(column);
            cell.setTypeface(typeface);
            tableAdapter.updateCell(row, column, cell);
        }
    }

    private void setCellColorHelper(int row, int column, int color) {
        if (isValidPosition(row, column)) {
            Cell cell = tableData.get(row).getCell(column);
            cell.setBackgroundColor(color);
            tableAdapter.updateCell(row, column, cell);
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
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            private boolean isDragging = false;
            private int dragFrom = -1;
            private int dragTo = -1;
            private int originalBackgroundColor;
            @Override
            public boolean isLongPressDragEnabled() {
                return !isFiltering;
            }
            @Override
            public boolean isItemViewSwipeEnabled() {
                return !isFiltering;
            }
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();


                if (dragFrom == -1) {
                    dragFrom = fromPosition;
                }
                dragTo = toPosition;

                if (hasHeader && (fromPosition == 0 || toPosition == 0)) {
                    return false; // Still prevent moving the header row
                }

                Collections.swap(tableData, fromPosition, toPosition);
                tableAdapter.notifyItemMoved(fromPosition, toPosition);

                return true;
            }
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder.itemView.setElevation(TableConstants.DRAG_ELEVATION);
                    originalBackgroundColor = ((ColorDrawable) viewHolder.itemView.getBackground()).getColor();
                    viewHolder.itemView.setBackgroundColor(TableConstants.DRAG_BACKGROUND_COLOR);
                    isDragging = true;
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                viewHolder.itemView.setElevation(0f);
                viewHolder.itemView.setBackgroundColor(originalBackgroundColor);

                if (hasHeader && (dragFrom == 0 || dragTo == 0)) {
                    showToast(getContext().getString(R.string.cannot_swap_header));
                } else if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                    updateRowNumbers();
                    if(!hasHeader){
                        dragFrom++;
                        dragTo++;
                    }
                    showToast(getContext().getString(R.string.rows_swapped, dragFrom, dragTo));
                }

                dragFrom = dragTo = -1;
                isDragging = false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();


                if (direction == ItemTouchHelper.RIGHT) {
                    showDeleteConfirmationDialog(position);
                } else if (direction == ItemTouchHelper.LEFT) {
                    highlightRow(position);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    ColorDrawable background;
                    Drawable icon;
                    int iconSize = 80; // Increased icon size
                    int iconMargin = (itemView.getHeight() - iconSize) / 2;

                    if (dX > 0) { // Swiping to the right (delete)
                        background = new ColorDrawable(Color.RED);
                        icon = ContextCompat.getDrawable(getContext(), android.R.drawable.ic_menu_delete);
                        background.setBounds(itemView.getLeft(), itemView.getTop(), (int) dX, itemView.getBottom());
                        icon.setBounds(itemView.getLeft() + iconMargin, itemView.getTop() + iconMargin,
                                itemView.getLeft() + iconMargin + 40, itemView.getBottom() - iconMargin);
                    } else if (dX < 0) { // Swiping to the left (highlight)
                        background = new ColorDrawable(Color.YELLOW);
                        icon = ContextCompat.getDrawable(getContext(), android.R.drawable.btn_star);
                        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                        icon.setBounds(itemView.getRight() - iconMargin - 40, itemView.getTop() + iconMargin,
                                itemView.getRight() - iconMargin, itemView.getBottom() - iconMargin);
                    } else { // No swipe
                        background = new ColorDrawable(Color.TRANSPARENT);
                        icon = null;
                    }

                    background.draw(c);
                    if (icon != null) {
                        icon.draw(c);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void showDeleteConfirmationDialog(final int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Row")
                .setMessage("Are you sure you want to delete this row?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeRow(position);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tableAdapter.notifyItemChanged(position);
                    }
                })
                .show();
    }

//    private void deleteRow(int position) {
//        tableData.remove(position+1);
//        tableAdapter.notifyItemRemoved(position);
//        updateRowNumbers();
//    }
private void highlightRow(int position) {
    Row row = tableData.get(position);
    boolean isCurrentlyHighlighted = row.isHighlighted();
    row.setHighlighted(!isCurrentlyHighlighted);
    tableAdapter.notifyItemChanged(position);
}

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

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
        for (int i = 0; i < tableData.size(); i++) {
            Row row = tableData.get(i);
            if (row.getCellCount() > 0) {
                if (i == 0 && hasHeader) {
                    row.setCell(0, new Cell(numberingHeaderText));
                } else if (i > 0 || !hasHeader) {
                    row.setCell(0, new Cell(String.valueOf(i + (hasHeader ? 0 : 1))));
                }
            } else if (i > 0 || !hasHeader) {
                row.addCell(new Cell(String.valueOf(i + (hasHeader ? 0 : 1))));
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

    private void ensureTableCompleteness() {
        int maxColumns = getMaxColumns();

        for (Row row : tableData) {
            int missingCells = maxColumns - row.getCellCount();
            for (int i = 0; i < missingCells; i++) {
                row.addCell(new Cell("")); // Add empty cells to match the longest row
            }
        }
    }

    private int getMaxColumns() {
        int maxColumns = 0;
        for (Row row : tableData) {
            maxColumns = Math.max(maxColumns, row.getCellCount());
        }
        return maxColumns;
    }

    public void addCellToRow(int rowIndex, String cellValue) {
        if (rowIndex < 0 || rowIndex >= tableData.size()) return;

        Row row = tableData.get(rowIndex);
        Cell newCell = new Cell(cellValue);

        // Check if it's the first row and there's a header
        if (rowIndex == 0 && hasHeader) {
            newCell.setBorderDrawableResId(R.drawable.header_cell_border);
            newCell.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        }

        row.addCell(newCell);
        ensureTableCompleteness();
        tableAdapter.setData(tableData);
        tableAdapter.notifyDataSetChanged();
    }

    public void addColumn(String[] columnValues) {
        // Check if there's a header and add the first cell as a header if applicable
        if (hasHeader && !tableData.isEmpty()) {
            Cell headerCell = new Cell(columnValues[0]);
            headerCell.setBorderDrawableResId(R.drawable.header_cell_border);
            headerCell.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            tableData.get(0).addCell(headerCell);

            // Start adding from the second element in the array
            for (int i = 1; i < columnValues.length; i++) {
                int rowIndex = i; // Adjust row index accordingly
                if (rowIndex < tableData.size()) {
                    Row row = tableData.get(rowIndex);
                    row.addCell(new Cell(columnValues[i]));
                }
            }
        } else {
            // No header, add all elements normally
            for (int i = 0; i < columnValues.length; i++) {
                int rowIndex = i;
                if (rowIndex < tableData.size()) {
                    Row row = tableData.get(rowIndex);
                    row.addCell(new Cell(columnValues[i]));
                }
            }
        }

        // Ensure the table remains complete
        ensureTableCompleteness();
        tableAdapter.setData(tableData);
        tableAdapter.notifyDataSetChanged();
    }

    private Cell createCell(String value, boolean isHeader) {
        Cell cell = new Cell(value);
        if (isHeader) {
            cell.setBorderDrawableResId(R.drawable.header_cell_border);
            cell.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        }
        return cell;
    }

    public void addHeaderRow(String[] headerValues) {
        hasHeader = true;
        List<Cell> headerCells = new ArrayList<>();

        Cell numberHeaderCell = createCell(numberingHeaderText, true);
        headerCells.add(numberHeaderCell);

        for (String value : headerValues) {
            headerCells.add(createCell(value, true));
        }

        Row headerRow = new Row(headerCells);
        tableData.add(0, headerRow);

        ensureTableCompleteness();
        updateRowNumbers();
        tableAdapter.setData(tableData);
        tableAdapter.notifyItemInserted(0);
    }


    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
        tableAdapter.setHasHeader(hasHeader);
        if (hasHeader) {
            setupSortingListener();
            applyHeaderStyling();
        } else {
            removeSortingListener();
            removeHeaderStyling();
        }
        tableAdapter.notifyDataSetChanged();
    }
    private void applyHeaderStyling() {
        if (!tableData.isEmpty()) {
            Row headerRow = tableData.get(0);
            for (int i = 0; i < headerRow.getCellCount(); i++) {
                Cell cell = headerRow.getCell(i);
                cell.setBorderDrawableResId(R.drawable.header_cell_border);
                cell.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                cell.setTextColor(Color.BLACK); // You can choose a different color if needed
            }
        }
    }

    private void removeHeaderStyling() {
        if (!tableData.isEmpty()) {
            Row firstRow = tableData.get(0);
            for (int i = 0; i < firstRow.getCellCount(); i++) {
                Cell cell = firstRow.getCell(i);
                cell.setBorderDrawableResId(R.drawable.cell_border);
                cell.setTypeface(Typeface.DEFAULT);
                cell.setTextColor(Color.BLACK);
                cell.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    private boolean isValidPosition(int row, int column) {
        return (row > 0 || (row == 0 && hasHeader)) && row < tableData.size() && column >= 0 && column < tableData.get(row).getCellCount();
    }

    private void setupUndoRedoButtons() {
        if (undoButton != null) {
            undoButton.setOnClickListener(v -> {
                undo();
            });
        }
        if (redoButton != null) {
            redoButton.setOnClickListener(v -> {
                redo();
            });
        }
        updateUndoRedoButtons();
    }

    private void undo() {
        if (!undoStack.isEmpty()) {
            TableViewMemento currentState = saveToMemento();
            redoStack.push(currentState);

            TableViewMemento previousState = undoStack.pop();
            restoreFromMemento(previousState);

            updateUndoRedoButtons();
            tableAdapter.notifyDataSetChanged();
            logTableState("After undo");
        }
    }
    private void redo() {
        if (!redoStack.isEmpty()) {
            TableViewMemento currentState = saveToMemento();
            undoStack.push(currentState);

            TableViewMemento nextState = redoStack.pop();
            restoreFromMemento(nextState);

            updateUndoRedoButtons();
            tableAdapter.notifyDataSetChanged();
            logTableState("After redo");
        }
    }

    private void logTableState(String action) {
        StringBuilder sb = new StringBuilder();
        sb.append(action).append(" - Table state:\n");
        sb.append("Rows: ").append(tableData.size()).append("\n");
        sb.append("Columns: ").append(tableData.isEmpty() ? 0 : tableData.get(0).getCellCount()).append("\n");
        sb.append("Has Header: ").append(hasHeader).append("\n");
        sb.append("Number Column Visible: ").append(isNumberColumnVisible).append("\n");
        sb.append("Row contents:\n");
        for (int i = 0; i < Math.min(tableData.size(), 5); i++) {
            Row row = tableData.get(i);
            sb.append("Row ").append(i).append(": ");
            for (int j = 0; j < Math.min(row.getCellCount(), 5); j++) {
                sb.append(row.getCell(j).getText()).append(", ");
            }
            if (row.getCellCount() > 5) sb.append("...");
            sb.append("\n");
        }
        if (tableData.size() > 5) sb.append("...\n");
        Log.d(TAG, sb.toString());
    }
    private void updateUndoRedoButtons() {
        if (undoButton != null) {
            undoButton.setEnabled(!undoStack.isEmpty());  // Enable only if there's more than one state
        }
        if (redoButton != null) {
            redoButton.setEnabled(!redoStack.isEmpty());
        }
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

        updateRowNumbers();
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
                cellCopy.setBorderDrawableResId(cell.getBorderDrawableResId());
                cellsCopy.add(cellCopy);
            }
            copy.add(new Row(cellsCopy));
        }
        return copy;
    }

    private void saveOriginalTableData() {
        if (caretaker.getMemento() == null) {
            caretaker.saveState(this); // Save the current table state
        }
    }
}