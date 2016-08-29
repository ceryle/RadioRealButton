package co.ceryle.radiorealbutton.library.util;

import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by EGE on 22/08/2016.
 */
public class RoundHelper {

    private static GradientDrawable getGradientDrawable(int dividerColor, int dividerRadius, int dividerSize) {
        GradientDrawable gradient =
                new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{dividerColor, dividerColor});
        gradient.setShape(GradientDrawable.RECTANGLE);
        gradient.setCornerRadius(dividerRadius);
        gradient.setSize(dividerSize, 0);
        return gradient;
    }

    public static void makeRound(View view, int dividerColor, int dividerRadius, int dividerSize) {
        GradientDrawable gradient = getGradientDrawable(dividerColor, dividerRadius, dividerSize);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            view.setBackground(gradient);
        else
            view.setBackgroundDrawable(gradient);
    }


    public static void makeDividerRound(LinearLayout layout, int dividerColor, int dividerRadius, int dividerSize){
        GradientDrawable gradient = getGradientDrawable(dividerColor, dividerRadius, dividerSize);
        layout.setDividerDrawable(gradient);
    }
}
