package dev.nimrod.customchart;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
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

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableRowViewHolder> {
    private static final int VERTICAL_PADDING = 16;
    private static final int HORIZONTAL_PADDING = 16;
    private static final int TEXT_WIDTH_PADDING = 20;
    private List<Row> rows;
    private Context context;
    private boolean hasHeader;
    private boolean isNumberColumnVisible = true;
    private int[] maxColumnWidths;
    private int minCellHeight;

    public TableAdapter(Context context, List<List<Cell>> data) {
        this.context = context;
        this.minCellHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 68, context.getResources().getDisplayMetrics());

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

    private void measureRow(Row row, Paint paint) {
        int maxRowHeight = 0;

        for (int columnIndex = 0; columnIndex < row.getCellCount(); columnIndex++) {
            Cell cell = row.getCell(columnIndex);
            paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, cell.getTextSize(), context.getResources().getDisplayMetrics()));

            Rect bounds = new Rect();
            paint.getTextBounds(cell.getText(), 0, cell.getText().length(), bounds);

            int cellWidth = bounds.width() + 2 * HORIZONTAL_PADDING + TEXT_WIDTH_PADDING;
            int cellHeight = Math.max(bounds.height() + 2 * VERTICAL_PADDING, minCellHeight);

            maxColumnWidths[columnIndex] = Math.max(maxColumnWidths[columnIndex], cellWidth);
            maxRowHeight = Math.max(maxRowHeight, cellHeight);
        }

        row.setHeight(maxRowHeight);
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
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }


    public void moveItem(int fromPosition, int toPosition) {
        Collections.swap(rows, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public class TableRowViewHolder extends RecyclerView.ViewHolder {
        private TableRow tableRow;

        public TableRowViewHolder(@NonNull View itemView) {
            super(itemView);
            tableRow = itemView.findViewById(R.id.table_row);
        }

        public void bind(Row row, boolean isHeader, int rowPosition, boolean isNumberColumnVisible) {
            tableRow.removeAllViews();
            int rowHeight = row.getHeight();

            for (int i = 0; i < row.getCellCount(); i++) {
                if (i == 0 && !isNumberColumnVisible) {
                    continue;
                }
                Cell cell = row.getCell(i);
                TextView textView = createCellView(cell, isHeader, i == 0);
                TableRow.LayoutParams params = new TableRow.LayoutParams(maxColumnWidths[i], rowHeight);
                textView.setLayoutParams(params);
                tableRow.addView(textView);
            }

            tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, rowHeight));
        }

        private TextView createCellView(Cell cell, boolean isHeader, boolean isRowNumber) {
            TextView textView = new TextView(tableRow.getContext());
            textView.setText(cell.getText());
            textView.setPadding(HORIZONTAL_PADDING, VERTICAL_PADDING, HORIZONTAL_PADDING, VERTICAL_PADDING);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, cell.getTextSize());
            Drawable borderDrawable = ContextCompat.getDrawable(context, cell.getBorderDrawableResId()).mutate();
            GradientDrawable backgroundDrawable = new GradientDrawable();
            backgroundDrawable.setColor(cell.getBackgroundColor());
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{backgroundDrawable, borderDrawable});
            textView.setBackground(layerDrawable);

            textView.setTextColor(cell.getTextColor());
            textView.setTypeface(cell.getTypeface());
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);

            textView.setSingleLine(true);
            textView.setEllipsize(null);

            if (isHeader || isRowNumber) {
                textView.setTypeface(null, Typeface.BOLD);
            }

            return textView;
        }
    }
}