package dev.nimrod.customchart;

import android.content.Context;
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

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableRowViewHolder> {
    private List<List<String>> data;
    private Context context;
    private boolean hasHeader;
    private boolean isRowNumberingEnabled;
    private boolean[] columnHasHeader;
    private int cellWidth;
    private int cellHeight;

    public TableAdapter(Context context, List<List<String>> data) {
        this.context = context;
        this.data = data;
        measureLargestCell();
    }

    private void measureLargestCell() {
        Paint paint = new Paint();
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, context.getResources().getDisplayMetrics()));

        int maxWidth = 0;
        int maxHeight = 0;

        for (List<String> row : data) {
            for (String cell : row) {
                Rect bounds = new Rect();
                paint.getTextBounds(cell, 0, cell.length(), bounds);
                maxWidth = Math.max(maxWidth, bounds.width());
                maxHeight = Math.max(maxHeight, bounds.height());
            }
        }

        // Add padding to the cell size
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());
        cellWidth = maxWidth + padding * 2;
        cellHeight = maxHeight + padding * 2;
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



    public void setData(List<List<String>> newData) {
        this.data = newData;
        notifyDataSetChanged();
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

    public void setCellColor(int row, int column, int color) {
        notifyItemChanged(row, new CellUpdate(column, color, CellUpdate.Type.BACKGROUND));
    }

    public void setCellTextColor(int row, int column, int color) {
        notifyItemChanged(row, new CellUpdate(column, color, CellUpdate.Type.TEXT_COLOR));
    }

    public void setCellTextSize(int row, int column, float size) {
        notifyItemChanged(row, new CellUpdate(column, size, CellUpdate.Type.TEXT_SIZE));
    }
    @Override
    public void onBindViewHolder(@NonNull TableRowViewHolder holder, int position) {
        List<String> rowData = data.get(position);
        boolean isHeader = hasHeader && position == 0;
        holder.bind(rowData, isHeader, isRowNumberingEnabled, cellWidth, cellHeight);
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

        public void bind(List<String> rowData, boolean isHeader, boolean isNumbered, int cellWidth, int cellHeight) {
            tableRow.removeAllViews();
            for (int i = 0; i < rowData.size(); i++) {
                TextView cell = new TextView(tableRow.getContext());
                cell.setText(rowData.get(i));
                cell.setPadding(8, 8, 8, 8);
                cell.setBackgroundResource(isHeader ? R.drawable.header_cell_border : R.drawable.cell_border);
                cell.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);

                // Ensure single line
                cell.setSingleLine(true);
                cell.setEllipsize(null);

                TableRow.LayoutParams params = new TableRow.LayoutParams(cellWidth, cellHeight);
                cell.setLayoutParams(params);

                if (isHeader || (isNumbered && i == 0)) {
                    cell.setTypeface(null, Typeface.BOLD);
                }
                tableRow.addView(cell);
            }
        }


        public void updateCell(CellUpdate update) {
            if (update.column < tableRow.getChildCount()) {
                TextView cell = (TextView) tableRow.getChildAt(update.column);
                switch (update.type) {
                    case BACKGROUND:
                        cell.setBackgroundColor((int) update.value);
                        break;
                    case TEXT_COLOR:
                        cell.setTextColor((int) update.value);
                        break;
                    case TEXT_SIZE:
                        cell.setTextSize((float) update.value);
                        break;
                }
            }
        }
    }

    private static class CellUpdate {
        int column;
        Object value;
        Type type;

        enum Type {
            BACKGROUND, TEXT_COLOR, TEXT_SIZE
        }

        CellUpdate(int column, Object value, Type type) {
            this.column = column;
            this.value = value;
            this.type = type;
        }
    }
}
