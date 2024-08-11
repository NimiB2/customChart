
package dev.nimrod.customchart;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.core.content.res.ResourcesCompat;

public class DrawableUtil {

    public static Drawable updateDrawableInteriorColor(Context context, int drawableResId, int color) {
        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), drawableResId, null);
        if (drawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            gradientDrawable.setColor(color); // Update the solid color
        }
        return drawable;
    }
}
