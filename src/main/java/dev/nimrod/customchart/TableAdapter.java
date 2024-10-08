package dev.nimrod.customchart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.nimrod.customchart.utilites.TableConstants;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableRowViewHolder> {
    private static final String TAG = "TableAdapter";

    private List<Row> rows;
    private Context context;
    private boolean hasHeader;
    private boolean isNumberColumnVisible = true;
    private int[] maxColumnWidths;
    private int minCellHeight;

    private OnHeaderClickListener headerClickListener;

    public TableAdapter(Context context, List<List<Cell>> data) {
        this.context = context;
        this.minCellHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TableConstants.DEFAULT_MIN_CELL_HEIGHT, context.getResources().getDisplayMetrics());

        this.rows = new ArrayList<>();
        for (List<Cell> cellList : data) {
            this.rows.add(new Row(cellList));
        }

        int maxColumns = getMaxColumns();
        this.maxColumnWidths = new int[maxColumns];

        measureCells();
    }

    public void setNumberColumnVisible(boolean visible) {
        this.isNumberColumnVisible = visible;
        recalculateCellSizes();
    }

    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        this.headerClickListener = listener;
    }

    private int getMaxColumns() {
        int maxColumns = 0;
        for (Row row : rows) {
            maxColumns = Math.max(maxColumns, row.getCellCount());
        }
        return maxColumns;
    }

    private void measureCells() {
        Paint paint = new Paint();

        int maxColumns = getMaxColumns();
        maxColumnWidths = new int[maxColumns];

        for (Row row : rows) {
            measureRow(row, paint);
        }
    }

    public void measureRow(Row row, Paint paint) {
        int maxRowHeight = 0;

        for (int columnIndex = 0; columnIndex < row.getCellCount(); columnIndex++) {
            Cell cell = row.getCell(columnIndex);
            if (hasHeader && columnIndex == 0) {
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            } else {
                paint.setTypeface(cell.getTypeface());
            }
            paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, cell.getTextSize(), context.getResources().getDisplayMetrics()));

            Rect bounds = new Rect();
            paint.getTextBounds(cell.getText(), 0, cell.getText().length(), bounds);

            int cellWidth = bounds.width() + 2 * TableConstants.HORIZONTAL_PADDING + TableConstants.TEXT_WIDTH_PADDING;
            int cellHeight = Math.max(bounds.height() + 2 * TableConstants.VERTICAL_PADDING, minCellHeight);

            maxColumnWidths[columnIndex] = Math.max(maxColumnWidths[columnIndex], cellWidth);
            maxRowHeight = Math.max(maxRowHeight, cellHeight);
        }

        row.setHeight(maxRowHeight);
        notifyDataSetChanged();
    }

    public void updateCell(int rowIndex, int columnIndex, Cell newCell) {
        if (rowIndex >= 0 && rowIndex < rows.size() && columnIndex >= 0 && columnIndex < rows.get(rowIndex).getCellCount()) {
            Row row = rows.get(rowIndex);
            row.setCell(columnIndex, newCell);
            Paint paint = new Paint();
            measureRow(row, paint);
            notifyItemChanged(rowIndex);
        }
    }

    public void updateCellTextSize(int rowIndex, int columnIndex, float newTextSize) {
        if (rowIndex >= 0 && rowIndex < rows.size() && columnIndex >= 0 && columnIndex < rows.get(rowIndex).getCellCount()) {
            Row row = rows.get(rowIndex);
            Cell cell = row.getCell(columnIndex);
            cell.setTextSize(newTextSize);
            Paint paint = new Paint();
            measureRow(row, paint);
            notifyItemChanged(rowIndex);
        }
    }

    public void recalculateCellSizes() {
        measureCells();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TableRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.table_row_item, parent, false);
        return new TableRowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableRowViewHolder holder, int position) {
        Row row = rows.get(position);
        boolean isHeader = hasHeader && position == 0;
        holder.bind(row, isHeader, position, isNumberColumnVisible);
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    public void setData(List<Row> newRows) {
        this.rows = newRows;
        recalculateCellSizes();
        notifyDataSetChanged();
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    public class TableRowViewHolder extends RecyclerView.ViewHolder {
        private TableRow tableRow;

        public TableRowViewHolder(@NonNull View itemView) {
            super(itemView);
            tableRow = itemView.findViewById(R.id.table_row_item);
        }

        public void bind(Row row, boolean isHeader, int rowPosition, boolean isNumberColumnVisible) {
            tableRow.removeAllViews();
            int rowHeight = row.getHeight();
            if (row.isHighlighted()) {
                tableRow.setBackgroundColor(TableConstants.COLOR_YELLOW);
            } else {
                tableRow.setBackgroundColor(TableConstants.COLOR_TRANSPARENT);
            }
            for (int i = 0; i < row.getCellCount(); i++) {
                if (i == 0 && !isNumberColumnVisible) {
                    continue;
                }
                Cell cell = row.getCell(i);
                TextView textView = createCellView(cell, isHeader, i == 0);
                textView.setTypeface(cell.getTypeface());

                TableRow.LayoutParams params = new TableRow.LayoutParams(maxColumnWidths[i], rowHeight);
                textView.setLayoutParams(params);

                if (row.isHighlighted()) {
                    textView.setBackgroundColor(TableConstants.COLOR_YELLOW);
                }

                if (isHeader && hasHeader) {
                    final int columnIndex = i;
                    textView.setOnClickListener(v -> {
                        if (headerClickListener != null) {
                            headerClickListener.onHeaderClick(columnIndex);
                        }
                    });
                } else {
                    textView.setOnClickListener(null);
                }
                tableRow.addView(textView);
            }

            tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, rowHeight));
        }

        private TextView createCellView(Cell cell, boolean isHeader, boolean isRowNumber) {
            TextView textView = new TextView(tableRow.getContext());
            textView.setText(cell.getText());
            textView.setPadding(TableConstants.HORIZONTAL_PADDING, TableConstants.VERTICAL_PADDING, TableConstants.HORIZONTAL_PADDING, TableConstants.VERTICAL_PADDING);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, cell.getTextSize());

            textView.setBackground(cell.getBackgroundDrawable(context));

            textView.setTextColor(cell.getTextColor());
            textView.setTypeface(cell.getTypeface());
            textView.setGravity(Gravity.CENTER);

            textView.setSingleLine(true);
            textView.setEllipsize(null);

            if (isHeader || isRowNumber) {
                textView.setTypeface(null, Typeface.BOLD);
            }

            return textView;
        }
    }

    public interface OnHeaderClickListener {
        void onHeaderClick(int columnIndex);
    }
}