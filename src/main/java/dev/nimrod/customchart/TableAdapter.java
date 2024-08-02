package dev.nimrod.customchart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import dev.nimrod.customchart.Util.LineTableView;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableRowViewHolder> {
    private List<List<Cell>> data;
    private Context context;
    private boolean hasHeader;
    private boolean isRowNumberingEnabled;
    private boolean[] columnHasHeader;
    private LineTableView lineTableView;
    private int[] columnWidths;

    private int cellWidth;
    private int cellHeight;

    public TableAdapter(Context context, List<List<Cell>> data, LineTableView lineTableView) {
        this.context = context;
        this.data = data;
        this.lineTableView = lineTableView;

        measureLargestCell();
    }

    private void measureLargestCell() {
        Paint paint = new Paint();
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, context.getResources().getDisplayMetrics()));

        int columnCount = data.isEmpty() ? 0 : data.get(0).size();
        columnWidths = new int[columnCount];
        int maxHeight = 0;

        for (List<Cell> row : data) {
            for (int i = 0; i < row.size(); i++) {
                Cell cell = row.get(i);
                Rect bounds = new Rect();
                paint.getTextBounds(cell.getText(), 0, cell.getText().length(), bounds);
                columnWidths[i] = Math.max(columnWidths[i], bounds.width());
                maxHeight = Math.max(maxHeight, bounds.height());
            }
        }

        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());
        for (int i = 0; i < columnWidths.length; i++) {
            columnWidths[i] += padding * 2;
        }
        cellHeight = maxHeight + padding * 2;

        if (lineTableView != null) {
            float[] columnWidthsFloat = new float[columnWidths.length];
            for (int i = 0; i < columnWidths.length; i++) {
                columnWidthsFloat[i] = columnWidths[i];
            }
            lineTableView.setTableDimensions(data.size(), columnWidths.length, columnWidthsFloat, cellHeight);
            lineTableView.requestLayout(); // Request a new layout
            lineTableView.invalidate(); // Force redraw
        }
    }

    public void recalculateCellSizes() {
        measureLargestCell();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TableRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.table_row_item, parent, false);
        return new TableRowViewHolder(view);
    }


    public void setData(List<List<Cell>> newData) {
        this.data = newData;
        recalculateCellSizes();
    }

    public void setCellStyle(int row, int column, Cell cell) {
        if (row >= 0 && row < data.size() && column >= 0 && column < data.get(row).size()) {
            data.get(row).set(column, cell);
            recalculateCellSizes();
        }
    }

    public void setColumnHasHeader(boolean[] columnHasHeader) {
        this.columnHasHeader = columnHasHeader;
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    public void setRowNumberingEnabled(boolean enabled) {
        this.isRowNumberingEnabled = enabled;
    }


    @Override
    public void onBindViewHolder(@NonNull TableRowViewHolder holder, int position) {
        List<Cell> rowData = data.get(position);
        boolean isHeader = hasHeader && position == 0;
        holder.bind(rowData, isHeader, isRowNumberingEnabled, columnWidths, cellHeight);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void moveItem(int fromPosition, int toPosition) {
        Collections.swap(data, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public static class TableRowViewHolder extends RecyclerView.ViewHolder {
        private TableRow tableRow;

        public TableRowViewHolder(@NonNull View itemView) {
            super(itemView);
            tableRow = itemView.findViewById(R.id.table_row);
        }

        public void bind(List<Cell> rowData, boolean isHeader, boolean isNumbered, int[] columnWidths, int cellHeight) {
            tableRow.removeAllViews();
            for (int i = 0; i < rowData.size(); i++) {
                Cell cell = rowData.get(i);
                TextView textView = new TextView(tableRow.getContext());
                textView.setText(cell.getText());
                textView.setPadding(8, 8, 8, 8);
                textView.setBackgroundColor(cell.getBackgroundColor());
                textView.setTextColor(cell.getTextColor());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, cell.getTextSize());
                textView.setTypeface(cell.getTypeface());
                textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                textView.setSingleLine(true);

                TableRow.LayoutParams params = new TableRow.LayoutParams(columnWidths[i], cellHeight);
                textView.setLayoutParams(params);

                if (isHeader || (isNumbered && i == 0)) {
                    textView.setTypeface(null, Typeface.BOLD);
                }
                tableRow.addView(textView);
            }
        }
    }
}
