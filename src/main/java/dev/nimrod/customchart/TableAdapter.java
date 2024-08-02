package dev.nimrod.customchart;

import android.app.AlertDialog;
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
    private int[] columnWidths;
    private int cellWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int cellHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
    private boolean showFullText = false;
    private LineTableView lineTableView;


    public TableAdapter(Context context, List<List<Cell>> data, LineTableView lineTableView) {
        this.context = context;
        this.data = data;
        this.lineTableView = lineTableView;

        measureLargestCell();
    }

    public void setCellSize(int width, int height) {
        this.cellWidth = width;
        this.cellHeight = height;
        recalculateCellSizes();
    }

    public void setShowFullText(boolean showFull) {
        this.showFullText = showFull;
        recalculateCellSizes();
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
            columnWidths[i] = Math.max(columnWidths[i] + padding * 2, cellWidth);
        }
        cellHeight = Math.max(maxHeight + padding * 2, cellHeight);

        if (lineTableView != null) {
            float[] columnWidthsFloat = new float[columnWidths.length];
            for (int i = 0; i < columnWidths.length; i++) {
                columnWidthsFloat[i] = columnWidths[i];
            }
            int padding1 = context.getResources().getDimensionPixelSize(R.dimen.table_padding);
            lineTableView.setTableDimensions(data.size(), columnWidths.length, columnWidthsFloat, cellHeight, padding1);
            lineTableView.requestLayout();
            lineTableView.invalidate();
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

//    public void setColumnHasHeader(boolean[] columnHasHeader) {
//        this.columnHasHeader = columnHasHeader;
//    }

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
        holder.bind(rowData, isHeader, isRowNumberingEnabled, columnWidths, cellHeight, showFullText);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void moveItem(int fromPosition, int toPosition) {
        Collections.swap(data, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public class TableRowViewHolder extends RecyclerView.ViewHolder {
        private TableRow tableRow;

        public TableRowViewHolder(@NonNull View itemView) {
            super(itemView);
            tableRow = itemView.findViewById(R.id.table_row);
        }

        public void bind(List<Cell> rowData, boolean isHeader, boolean isNumbered, int[] columnWidths, int cellHeight, boolean showFullText) {
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

                textView.setSingleLine(!cell.isExpanded());
                if (!cell.isExpanded()) {
                    textView.setEllipsize(TextUtils.TruncateAt.END);
                }

                TableRow.LayoutParams params = new TableRow.LayoutParams(columnWidths[i], cell.isExpanded() ? ViewGroup.LayoutParams.WRAP_CONTENT : cellHeight);
                textView.setLayoutParams(params);

                if (isHeader || (isNumbered && i == 0)) {
                    textView.setTypeface(null, Typeface.BOLD);
                }

                final int column = i;
                textView.setOnClickListener(v -> {
                    cell.setExpanded(!cell.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                });

                tableRow.addView(textView);
            }
        }

        private void showFullTextDialog(Cell cell) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Full Text");
            builder.setMessage(cell.getText());
            builder.setPositiveButton("OK", null);
            builder.show();
        }
    }
}