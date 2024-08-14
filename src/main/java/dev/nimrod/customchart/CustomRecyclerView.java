package dev.nimrod.customchart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.recyclerview.widget.RecyclerView;

public class CustomRecyclerView extends RecyclerView {

    private boolean isHorizontalScrollingEnabled = false;

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (isHorizontalScrollingEnabled) {
            return true; // Intercept touch events to prevent sliding
        } else {
            return super.onInterceptTouchEvent(e);
        }
    }

    public void setHorizontalScrollingEnabled(boolean enabled) {
        this.isHorizontalScrollingEnabled = enabled;
    }

    public boolean isHorizontalScrollingEnabled() {
        return isHorizontalScrollingEnabled;
    }
}

