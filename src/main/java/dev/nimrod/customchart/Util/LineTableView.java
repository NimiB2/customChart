package dev.nimrod.customchart.Util;

import static android.opengl.ETC1.getHeight;
import static android.opengl.ETC1.getWidth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class LineTableView extends View {
    private Paint linePaint;
    private int rowCount;
    private int columnCount;
    private float[] columnWidths;
    private float rowHeight;
    private int padding;

    public LineTableView(Context context) {
        super(context);
        init();
    }

    public LineTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(2);
    }

    public void setTableDimensions(int rowCount, int columnCount, float[] columnWidths, float rowHeight, int padding) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.columnWidths = columnWidths;
        this.rowHeight = rowHeight;
        this.padding = padding;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = padding * 2;
        if (columnWidths != null) {
            for (float columnWidth : columnWidths) {
                width += columnWidth;
            }
        }
        int height = (int) (rowCount * rowHeight) + padding * 2;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (columnWidths == null || rowCount == 0 || columnCount == 0) {
            return;
        }

        // Draw horizontal lines
        float y = padding;
        for (int i = 0; i <= rowCount; i++) {
            canvas.drawLine(padding, y, getWidth() - padding, y, linePaint);
            y += rowHeight;
        }

        // Draw vertical lines
        float x = padding;
        for (int i = 0; i <= columnCount; i++) {
            canvas.drawLine(x, padding, x, getHeight() - padding, linePaint);
            if (i < columnCount) {
                x += columnWidths[i];
            }
        }
    }
}