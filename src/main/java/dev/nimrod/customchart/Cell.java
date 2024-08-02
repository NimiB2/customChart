package dev.nimrod.customchart;

import android.graphics.Color;
import android.graphics.Typeface;

public class Cell {
    private String text;
    private int backgroundColor = Color.TRANSPARENT;
    private int textColor = Color.BLACK;
    private float textSize = 18f;
    private Typeface typeface = Typeface.DEFAULT;
    private boolean expanded = false;


    public Cell(String text) {
        this.text = text;
    }

    // Getters and setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public int getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(int backgroundColor) { this.backgroundColor = backgroundColor; }
    public int getTextColor() { return textColor; }
    public void setTextColor(int textColor) { this.textColor = textColor; }
    public float getTextSize() { return textSize; }
    public void setTextSize(float textSize) { this.textSize = textSize; }
    public Typeface getTypeface() { return typeface; }
    public void setTypeface(Typeface typeface) { this.typeface = typeface; }
    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
