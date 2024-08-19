package dev.nimrod.customchart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;

import androidx.core.content.ContextCompat;

import java.util.Objects;

public class Cell {
    private String text;
    private int backgroundColor = Color.TRANSPARENT;
    private int textColor = Color.BLACK;
    private float textSize = 20f;
    private Typeface typeface = Typeface.DEFAULT;
    private int borderDrawableResId = R.drawable.cell_border;


    public Cell(String text) {
        this.text = text;
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
    public int getBorderDrawableResId() {
        return borderDrawableResId;
    }
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