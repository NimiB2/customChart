package dev.nimrod.customchart;

import android.graphics.Color;

public class ChartCell {
    private String data;
    private int textColor = Color.BLACK;
    private boolean isBold = false;

    public ChartCell(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }
}
