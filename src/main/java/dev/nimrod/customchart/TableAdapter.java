package dev.nimrod.customchart;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
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

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableRowViewHolder> {
    private static final int VERTICAL_PADDING = 16;
    private List<List<Cell>> data;
    private Context context;
    private boolean hasHeader;
    private boolean isRowNumberingEnabled;
    private int[] columnWidths;
    private int[] rowHeights;
    private int cellWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int cellHeight;
    private int defaultCellWidth;
    private int defaultCellHeight;
    private int minCellHeight;

    public TableAdapter(Context context, List<List<Cell>> data) {
        this.context = context;
        this.data = data;
        this.minCellHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 68, context.getResources().getDisplayMetrics());
        this.defaultCellWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
        this.defaultCellHeight = minCellHeight;
        measureCells();
    }

    public void setDefaultCellSize(int width, int height) {
        this.defaultCellWidth = Math.max(width, 0);
        this.defaultCellHeight = Math.max(height, minCellHeight);
        recalculateCellSizes();
    }

    public void setDefaultCellWidth(int width) {
        this.defaultCellWidth = Math.max(width, 0);
        recalculateCellSizes();
    }

    public void setDefaultCellHeight(int height) {
        this.defaultCellHeight = Math.max(height, minCellHeight);
        recalculateCellSizes();
    }

    private void measureCells() {
        Paint paint = new Paint();
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, context.getResources().getDisplayMetrics()));

        int columnCount = data.isEmpty() ? 0 : data.get(0).size();
        columnWidths = new int[columnCount];
        rowHeights = new int[data.size()];

        for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
            List<Cell> row = data.get(rowIndex);
            int rowHeight = 0;

            for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                Cell cell = row.get(columnIndex);
                Rect bounds = new Rect();
                paint.getTextBounds(cell.getText(), 0, cell.getText().length(), bounds);

                int cellWidth = bounds.width() + 32; // Add padding
                int cellHeight = bounds.height() + 32 + (2 * VERTICAL_PADDING); // Add padding

                columnWidths[columnIndex] = Math.max(columnWidths[columnIndex], cellWidth);
                rowHeight = Math.max(rowHeight, cellHeight);
            }
            rowHeights[rowIndex] = rowHeight;
        }
    }

    @NonNull
    @Override
    public TableRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.table_row_item, parent, false);
        return new TableRowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableRowViewHolder holder, int position) {
        List<Cell> rowData = data.get(position);
        boolean isHeader = hasHeader && position == 0;
        holder.bind(rowData, isHeader, position);
    }

    @Override
    public int getItemCount() {
        return data.size();
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

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    public void setRowNumberingEnabled(boolean enabled) {
        this.isRowNumberingEnabled = enabled;
    }

    public void moveItem(int fromPosition, int toPosition) {
        Collections.swap(data, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void recalculateCellSizes() {
        measureCells();
        notifyDataSetChanged();
    }

    public class TableRowViewHolder extends RecyclerView.ViewHolder {
        private TableRow tableRow;

        public TableRowViewHolder(@NonNull View itemView) {
            super(itemView);
            tableRow = itemView.findViewById(R.id.table_row);
        }

        public void bind(List<Cell> rowData, boolean isHeader, int rowPosition) {
            tableRow.removeAllViews();
            int rowHeight = rowHeights[rowPosition];
            boolean isRowExpanded = rowData.stream().anyMatch(Cell::isExpanded);

            for (int i = 0; i < rowData.size(); i++) {
                Cell cell = rowData.get(i);
                TextView textView = new TextView(tableRow.getContext());
                textView.setText(cell.getText());
                textView.setPadding(8, VERTICAL_PADDING, 8, VERTICAL_PADDING);
                textView.setBackgroundResource(cell.getBorderDrawableResId());
                textView.getBackground().setColorFilter(cell.getBackgroundColor(), PorterDuff.Mode.SRC_ATOP);
                textView.setTextColor(cell.getTextColor());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, cell.getTextSize());
                textView.setTypeface(cell.getTypeface());
                textView.setGravity(Gravity.CENTER_VERTICAL);

                // Check if text is longer than the cell can present
                textView.measure(View.MeasureSpec.makeMeasureSpec(columnWidths[i], View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                boolean isTextTooLong = textView.getLineCount() > 1;

                if (isTextTooLong) {
                    if (isRowExpanded) {
                        textView.setMaxLines(Integer.MAX_VALUE);
                    } else {
                        textView.setMaxLines(1);
                        textView.setEllipsize(TextUtils.TruncateAt.END);
                    }

                    textView.setOnClickListener(v -> {
                        boolean newExpandedState = !rowData.get(0).isExpanded();
                        for (Cell c : rowData) {
                            c.setExpanded(newExpandedState);
                        }
                        notifyItemChanged(rowPosition);
                    });
                } else {
                    textView.setMaxLines(1);
                }

                TableRow.LayoutParams params = new TableRow.LayoutParams(columnWidths[i], isRowExpanded ? ViewGroup.LayoutParams.WRAP_CONTENT : rowHeight);
                textView.setLayoutParams(params);

                if (isHeader || (isRowNumberingEnabled && i == 0)) {
                    textView.setTypeface(null, Typeface.BOLD);
                }

                tableRow.addView(textView);
            }

            tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    isRowExpanded ? ViewGroup.LayoutParams.WRAP_CONTENT : rowHeight));
        }
    }
}