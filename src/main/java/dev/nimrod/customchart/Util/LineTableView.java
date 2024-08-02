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
        linePaint.setStrokeWidth(5);
    }

    public void setTableDimensions(int rowCount, int columnCount, float[] columnWidths, float rowHeight) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.columnWidths = columnWidths;
        this.rowHeight = rowHeight;
        invalidate();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        if (columnWidths != null) {
            for (float columnWidth : columnWidths) {
                width += columnWidth;
            }
        }
        int height = (int) (rowCount * rowHeight);
        setMeasuredDimension(width, height);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (columnWidths == null || rowCount == 0 || columnCount == 0) {
            return;
        }

        // Draw horizontal lines
        for (int i = 0; i <= rowCount; i++) {
            float y = i * rowHeight;
            canvas.drawLine(0, y, getWidth(), y, linePaint);
        }

        // Draw vertical lines
        float x = 0;
        for (int i = 0; i <= columnCount; i++) {
            canvas.drawLine(x, 0, x, getHeight(), linePaint);
            if (i < columnCount) {
                x += columnWidths[i];
            }
        }
    }
}
