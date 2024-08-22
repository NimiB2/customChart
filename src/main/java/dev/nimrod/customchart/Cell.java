package dev.nimrod.customchart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;

import androidx.core.content.ContextCompat;

import java.util.Objects;

import dev.nimrod.customchart.utilites.TableConstants;

public class Cell {
    private String text;
    private int backgroundColor;
    private int textColor;
    private float textSize;
    private Typeface typeface;
    private int borderDrawableResId;

    public Cell(String text) {
        this.text = text;
        this.backgroundColor = TableConstants.COLOR_TRANSPARENT;
        this.textColor = TableConstants.COLOR_BLACK;
        this.textSize = TableConstants.DEFAULT_TEXT_SIZE;
        this.typeface = Typeface.DEFAULT;
        this.borderDrawableResId = R.drawable.cell_border;
    }

    // Getters and setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public int getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(int backgroundColor) { this.backgroundColor = backgroundColor; }

    public Drawable getBackgroundDrawable(Context context) {
        Drawable borderDrawable = ContextCompat.getDrawable(context, borderDrawableResId).mutate();
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(backgroundColor);
        return new LayerDrawable(new Drawable[]{backgroundDrawable, borderDrawable});
    }

    public int getTextColor() { return textColor; }
    public void setTextColor(int textColor) { this.textColor = textColor; }

    public float getTextSize() { return textSize; }
    public void setTextSize(float textSize) { this.textSize = textSize; }

    public Typeface getTypeface() { return typeface; }
    public void setTypeface(Typeface typeface) { this.typeface = typeface; }

    public int getBorderDrawableResId() { return borderDrawableResId; }
    public Cell setBorderDrawableResId(int borderDrawableResId) {
        this.borderDrawableResId = borderDrawableResId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return backgroundColor == cell.backgroundColor &&
                textColor == cell.textColor &&
                Float.compare(cell.textSize, textSize) == 0 &&
                borderDrawableResId == cell.borderDrawableResId &&
                Objects.equals(text, cell.text) &&
                Objects.equals(typeface, cell.typeface);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, backgroundColor, textColor, textSize, typeface, borderDrawableResId);
    }
}