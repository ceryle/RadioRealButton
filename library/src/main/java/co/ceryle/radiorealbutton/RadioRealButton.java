/*
 * Copyright (C) 2016 ceryle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.ceryle.radiorealbutton;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RadioRealButton extends LinearLayout {
    public RadioRealButton(Context context) {
        super(context);
        this.init(null);
    }

    public RadioRealButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public RadioRealButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RadioRealButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(attrs);
    }

    private void init(AttributeSet attrs) {
        getAttributes(attrs);

        initViews();

        setDrawableGravity();
        setState();

        super.setPadding(0, 0, 0, 0);
        setPaddingAttrs();
    }

    private AppCompatImageView imageView;
    private AppCompatTextView textView;

    private void initViews() {
        setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        imageView = new AppCompatImageView(getContext());
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) {{
            gravity = Gravity.CENTER;
        }});
        setDrawableAttrs();
        addView(imageView);

        textView = new AppCompatTextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) {{
            gravity = Gravity.CENTER;
        }});
        setTextAttrs();
        addView(textView);
    }

    private Typeface defaultTypeface, textTypeface;

    private String text, textTypefacePath;

    private int textStyle, textSize, drawable, drawableTint, drawableTintTo, textColor, textColorTo, rippleColor, drawableWidth,
            drawableHeight, selectorColor, padding, paddingLeft, paddingRight, paddingTop, paddingBottom, drawablePadding,
            backgroundColor, textGravity;

    private boolean hasPaddingLeft, hasPaddingRight, hasPaddingTop, hasPaddingBottom, hasDrawableTint, hasTextTypefacePath,
            hasDrawable, hasText, hasDrawableWidth, hasDrawableHeight, checked, enabled, hasEnabled, clickable, hasClickable,
            hasTextStyle, hasTextSize, hasTextColor, textFillSpace, hasRipple, hasRippleColor, hasSelectorColor,
            hasDrawableTintTo, hasTextColorTo;

    /***
     * GET ATTRIBUTES FROM XML
     */
    private void getAttributes(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.RadioRealButton);

        drawable = ta.getResourceId(R.styleable.RadioRealButton_rrb_drawable, -1);
        drawableTint = ta.getColor(R.styleable.RadioRealButton_rrb_drawableTint, 0);
        drawableTintTo = ta.getColor(R.styleable.RadioRealButton_rrb_drawableTintTo, 0);
        drawableWidth = ta.getDimensionPixelSize(R.styleable.RadioRealButton_rrb_drawableWidth, -1);
        drawableHeight = ta.getDimensionPixelSize(R.styleable.RadioRealButton_rrb_drawableHeight, -1);

        hasDrawable = ta.hasValue(R.styleable.RadioRealButton_rrb_drawable);
        hasDrawableTint = ta.hasValue(R.styleable.RadioRealButton_rrb_drawableTint);
        hasDrawableTintTo = ta.hasValue(R.styleable.RadioRealButton_rrb_drawableTintTo);
        hasDrawableWidth = ta.hasValue(R.styleable.RadioRealButton_rrb_drawableWidth);
        hasDrawableHeight = ta.hasValue(R.styleable.RadioRealButton_rrb_drawableHeight);

        text = ta.getString(R.styleable.RadioRealButton_rrb_text);
        hasText = ta.hasValue(R.styleable.RadioRealButton_rrb_text);
        textColor = ta.getColor(R.styleable.RadioRealButton_rrb_textColor, Color.BLACK);
        textColorTo = ta.getColor(R.styleable.RadioRealButton_rrb_textColorTo, Color.BLACK);

        hasTextColor = ta.hasValue(R.styleable.RadioRealButton_rrb_textColor);
        hasTextColorTo = ta.hasValue(R.styleable.RadioRealButton_rrb_textColorTo);
        textSize = ta.getDimensionPixelSize(R.styleable.RadioRealButton_rrb_textSize, -1);
        hasTextSize = ta.hasValue(R.styleable.RadioRealButton_rrb_textSize);
        textStyle = ta.getInt(R.styleable.RadioRealButton_rrb_textStyle, -1);
        hasTextStyle = ta.hasValue(R.styleable.RadioRealButton_rrb_textStyle);

        int typeface = ta.getInt(R.styleable.RadioRealButton_rrb_textTypeface, -1);
        switch (typeface) {
            case 0:
                textTypeface = Typeface.MONOSPACE;
                break;
            case 1:
                textTypeface = Typeface.DEFAULT;
                break;
            case 2:
                textTypeface = Typeface.SANS_SERIF;
                break;
            case 3:
                textTypeface = Typeface.SERIF;
                break;
        }
        textTypefacePath = ta.getString(R.styleable.RadioRealButton_rrb_textTypefacePath);
        hasTextTypefacePath = ta.hasValue(R.styleable.RadioRealButton_rrb_textTypefacePath);

        hasRipple = ta.getBoolean(R.styleable.RadioRealButton_rrb_ripple, true);
        rippleColor = ta.getColor(R.styleable.RadioRealButton_rrb_rippleColor, Color.GRAY);
        hasRippleColor = ta.hasValue(R.styleable.RadioRealButton_rrb_rippleColor);

        backgroundColor = ta.getColor(R.styleable.RadioRealButton_rrb_backgroundColor, Color.TRANSPARENT);

        int defaultPadding = ConversionHelper.dpToPx(getContext(), 10);
        padding = ta.getDimensionPixelSize(R.styleable.RadioRealButton_android_padding, defaultPadding);
        paddingLeft = ta.getDimensionPixelSize(R.styleable.RadioRealButton_android_paddingLeft, 0);
        paddingRight = ta.getDimensionPixelSize(R.styleable.RadioRealButton_android_paddingRight, 0);
        paddingTop = ta.getDimensionPixelSize(R.styleable.RadioRealButton_android_paddingTop, 0);
        paddingBottom = ta.getDimensionPixelSize(R.styleable.RadioRealButton_android_paddingBottom, 0);

        hasPaddingLeft = ta.hasValue(R.styleable.RadioRealButton_android_paddingLeft);
        hasPaddingRight = ta.hasValue(R.styleable.RadioRealButton_android_paddingRight);
        hasPaddingTop = ta.hasValue(R.styleable.RadioRealButton_android_paddingTop);
        hasPaddingBottom = ta.hasValue(R.styleable.RadioRealButton_android_paddingBottom);

        drawablePadding = ta.getDimensionPixelSize(R.styleable.RadioRealButton_rrb_drawablePadding, 4);

        drawableGravity = DrawableGravity.getById(ta.getInteger(R.styleable.RadioRealButton_rrb_drawableGravity, 0));

        checked = ta.getBoolean(R.styleable.RadioRealButton_rrb_checked, false);

        enabled = ta.getBoolean(R.styleable.RadioRealButton_android_enabled, true);
        hasEnabled = ta.hasValue(R.styleable.RadioRealButton_android_enabled);
        clickable = ta.getBoolean(R.styleable.RadioRealButton_android_clickable, true);
        hasClickable = ta.hasValue(R.styleable.RadioRealButton_android_clickable);

        textGravity = ta.getInt(R.styleable.RadioRealButton_rrb_textGravity, Gravity.NO_GRAVITY);

        textFillSpace = ta.getBoolean(R.styleable.RadioRealButton_rrb_textFillSpace, false);

        selectorColor = ta.getColor(R.styleable.RadioRealButton_rrb_selectorColor, Color.TRANSPARENT);
        hasSelectorColor = ta.hasValue(R.styleable.RadioRealButton_rrb_selectorColor);

        ta.recycle();
    }

    public int getTextColorTo() {
        return textColorTo;
    }

    public void setTextColorTo(int textColorTo) {
        this.textColorTo = textColorTo;
    }

    public boolean hasTextColorTo() {
        return hasTextColorTo;
    }

    public boolean hasTextColor() {
        return hasTextColor;
    }

    public void setHasTextColor(boolean hasTextColor) {
        this.hasTextColor = hasTextColor;
    }

    public void setHasTextColorTo(boolean hasTextColorTo) {
        this.hasTextColorTo = hasTextColorTo;
    }

    public boolean hasDrawableTintTo() {
        return hasDrawableTintTo;
    }

    public void setHasDrawableTintTo(boolean hasDrawableTintTo) {
        this.hasDrawableTintTo = hasDrawableTintTo;
    }

    public int getDrawableTintTo() {
        return drawableTintTo;
    }

    public void setDrawableTintTo(int drawableTintTo) {
        this.drawableTintTo = drawableTintTo;
    }

    public int getSelectorColor() {
        return selectorColor;
    }

    public void setSelectorColor(int selectorColor) {
        this.selectorColor = selectorColor;
        onSelectorColorChangedListener.onSelectorColorChanged(position, selectorColor);
    }

    public boolean hasSelectorColor() {
        return hasSelectorColor;
    }

    private OnSelectorColorChangedListener onSelectorColorChangedListener;
    private int position;

    void setOnSelectorColorChangedListener(OnSelectorColorChangedListener onSelectorColorChangedListener, int position) {
        this.onSelectorColorChangedListener = onSelectorColorChangedListener;
        this.position = position;
    }

    interface OnSelectorColorChangedListener {
        void onSelectorColorChanged(int position, int selectorColor);
    }

    public boolean hasEnabled() {
        return hasEnabled;
    }

    public boolean hasClickable() {
        return hasClickable;
    }

    private void setDrawableAttrs() {
        if (hasDrawable) {
            imageView.setImageResource(drawable);
            if (hasDrawableTint)
                imageView.setColorFilter(drawableTint);

            if (hasDrawableWidth)
                setDrawableWidth(drawableWidth);
            if (hasDrawableHeight)
                setDrawableHeight(drawableHeight);
        } else {
            imageView.setVisibility(GONE);
        }
    }

    private void setDrawableGravity() {
        if (!hasDrawable)
            return;

        if (drawableGravity == DrawableGravity.LEFT || drawableGravity == DrawableGravity.TOP) {
            if (getChildAt(0) instanceof AppCompatTextView) {
                removeViewAt(0);
                addView(textView, 1);

                if (textFillSpace) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                    params.weight = 0;
                    params.width = LayoutParams.WRAP_CONTENT;
                }
            }
        } else {
            if (getChildAt(0) instanceof AppCompatImageView) {
                removeViewAt(0);
                addView(imageView, 1);

                if (textFillSpace) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                    params.weight = 1;
                    params.width = 0;
                }
            }
        }

        if (hasText && hasDrawable)
            if (drawableGravity == DrawableGravity.TOP || drawableGravity == DrawableGravity.BOTTOM)
                setOrientation(VERTICAL);
            else
                setOrientation(HORIZONTAL);
    }

    private void setTextAttrs() {
        defaultTypeface = textView.getTypeface();

        textView.setText(text);

        int gravity;

        if (textGravity == 2) {
            gravity = Gravity.END;
        } else if (textGravity == 1) {
            gravity = Gravity.CENTER;
        } else {
            gravity = Gravity.START;
        }

        textView.setGravity(gravity);

        if (hasTextColor)
            textView.setTextColor(textColor);
        if (hasTextSize)
            setTextSizePX(textSize);
        if (hasTextTypefacePath)
            setTypeface(textTypefacePath);
        else if (null != textTypeface) {
            setTypeface(textTypeface);
        }
        if (hasTextStyle)
            setTextStyle(textStyle);
    }

    private void setPaddingAttrs() {
        if (hasPaddingBottom || hasPaddingTop || hasPaddingLeft || hasPaddingRight)
            setPaddings(paddingLeft, paddingTop, paddingRight, paddingBottom);
        else
            setPaddings(padding, padding, padding, padding);
    }

    void colorTransitionDrawable(boolean hasAnimateDrawablesTint, int colorFrom, int colorTo, int duration, boolean hasAnimation, boolean onEnter) {
        int c1, c2;

        if (hasDrawableTintTo) {
            c1 = drawableTint;
            c2 = drawableTintTo;
        } else if (hasAnimateDrawablesTint) {
            c1 = colorFrom;
            c2 = colorTo;
        } else
            return;

        if (!onEnter) {
            int c = c1;
            c1 = c2;
            c2 = c;
        }

        if (hasAnimation)
            colorTransition(imageView, c1, c2, duration);
        else
            setDrawableTint(c2);
    }

    void colorTransitionText(boolean hasAnimateTextsColor, int colorFrom, int colorTo, int duration, boolean hasAnimation, boolean onEnter) {
        int c1, c2;

        if (hasTextColorTo) {
            c1 = textColor;
            c2 = textColorTo;
        } else if (hasAnimateTextsColor) {
            c1 = colorFrom;
            c2 = colorTo;
        } else
            return;

        if (!onEnter) {
            int c = c1;
            c1 = c2;
            c2 = c;
        }

        if (hasAnimation)
            colorTransition(textView, c1, c2, duration);
        else
            setTextColor(c2);
    }

    private void colorTransition(final View v, int colorFrom, int colorTo, int duration) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(duration);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                if (v instanceof ImageView) {
                    ((ImageView) v).setColorFilter((int) animator.getAnimatedValue());
                } else {
                    ((TextView) v).setTextColor((int) animator.getAnimatedValue());
                }
            }
        });
        colorAnimation.start();
    }

    void bounceDrawable(float scale, int duration, Interpolator interpolator, boolean hasAnimation) {
        if (hasAnimation)
            bounce(imageView, scale, duration, interpolator);
        else
            bounceDrawable(scale);
    }

    void bounceText(float scale, int duration, Interpolator interpolator, boolean hasAnimation) {
        if (hasAnimation)
            bounce(textView, scale, duration, interpolator);
        else
            bounceText(scale);
    }

    private void bounce(View view, float scale, int duration, Interpolator interpolator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            view.animate().setDuration(duration).setInterpolator(interpolator).scaleX(scale).scaleY(scale);
        } else {
            bounce(view, scale);
        }
    }

    /*private void bounce(View view, float scale, int duration, Interpolator interpolator) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", scale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", scale);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(duration);
        set.setInterpolator(interpolator);
        set.start();
    }*/

    void bounceDrawable(float scale) {
        bounce(imageView, scale);
    }

    void bounceText(float scale) {
        bounce(textView, scale);
    }

    private void bounce(final View view, final float scale) {
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    /**
     * GRAVITY
     */

    private DrawableGravity drawableGravity;

    public enum DrawableGravity {
        LEFT(0),
        TOP(1),
        RIGHT(2),
        BOTTOM(3);

        private int intValue;

        DrawableGravity(int intValue) {
            this.intValue = intValue;
        }

        private int getIntValue() {
            return intValue;
        }

        public static DrawableGravity getById(int id) {
            for (DrawableGravity e : values()) {
                if (e.intValue == id) return e;
            }
            return null;
        }

        public boolean isHorizontal() {
            return intValue == 0 || intValue == 2;
        }
    }

    /**
     * TEXT VIEW
     */
    public AppCompatTextView getTextView() {
        return textView;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;

        textView.setTextColor(textColor);
    }

    void setCheckedTextColor(int color) {
        textView.setTextColor(color);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;

        textView.setText(text);
    }

    public void setTextSizePX(int size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setTextSizeSP(float size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public int getTextSize() {
        return textSize;
    }

    public int getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(int typeface) {
        textView.setTypeface(textView.getTypeface(), typeface);
    }

    public void restoreTypeface() {
        textView.setTypeface(defaultTypeface);
    }

    public String getTypefacePath() {
        return textTypefacePath;
    }

    /**
     * Typeface.NORMAL: 0
     * Typeface.BOLD: 1
     * Typeface.ITALIC: 2
     * Typeface.BOLD_ITALIC: 3
     *
     * @param typeface you can use above variations using the bitwise OR operator
     */
    public void setTypeface(Typeface typeface) {
        textView.setTypeface(typeface);
    }

    public void setTypeface(String location) {
        if (null != location && !location.equals("")) {
            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), location);
            textView.setTypeface(typeface);
        }
    }

    public Typeface getDefaultTypeface() {
        return defaultTypeface;
    }

    /**
     * PADDING
     */
    public int getPadding() {
        return padding;
    }

    public void setPaddings(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        this.paddingLeft = paddingLeft;
        this.paddingTop = paddingTop;
        this.paddingRight = paddingRight;
        this.paddingBottom = paddingBottom;

        updatePaddings();
    }

    private void updatePaddings() {
        if (hasText)
            updatePadding(textView, hasDrawable);

        if (hasDrawable)
            updatePadding(imageView, hasText);
    }

    private void updatePadding(View view, boolean hasOtherView) {
        if (null == view)
            return;

        int[] paddings = {paddingLeft, paddingTop, paddingRight, paddingBottom};

        if (hasOtherView) {
            int g = drawableGravity.getIntValue();
            if (view instanceof AppCompatImageView) {
                g = g > 1 ? g - 2 : g + 2;
            }
            paddings[g] = drawablePadding / 2;
        }

        MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
        params.setMargins(paddings[0], paddings[1], paddings[2], paddings[3]);
    }

    @Override
    public int getPaddingLeft() {
        return paddingLeft;
    }

    @Override
    public int getPaddingRight() {
        return paddingRight;
    }

    @Override
    public int getPaddingTop() {
        return paddingTop;
    }

    @Override
    public int getPaddingBottom() {
        return paddingBottom;
    }

    public boolean hasPaddingLeft() {
        return hasPaddingLeft;
    }

    public boolean hasPaddingRight() {
        return hasPaddingRight;
    }

    public boolean hasPaddingTop() {
        return hasPaddingTop;
    }

    public boolean hasPaddingBottom() {
        return hasPaddingBottom;
    }

    /**
     * DRAWABLE
     */
    public AppCompatImageView getImageView() {
        return imageView;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;

        imageView.setImageResource(drawable);
    }

    public int getDrawableTint() {
        return drawableTint;
    }

    public void setDrawableTint(int color) {
        this.drawableTint = color;

        imageView.setColorFilter(color);
    }

    void setCheckedDrawableTint(int color) {
        imageView.setColorFilter(color);
    }

    public boolean hasDrawableTint() {
        return hasDrawableTint;
    }

    public void setDrawableTint(boolean hasColor) {
        this.hasDrawableTint = hasColor;

        if (hasColor)
            imageView.setColorFilter(drawableTint);
        else
            imageView.clearColorFilter();
    }

    public int getDrawableWidth() {
        return drawableWidth;
    }

    public void setDrawableWidth(int drawableWidth) {
        this.drawableWidth = drawableWidth;

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        if (null != params) {
            params.width = drawableWidth;
        }
    }

    public int getDrawableHeight() {
        return drawableHeight;
    }

    public void setDrawableHeight(int drawableHeight) {
        this.drawableHeight = drawableHeight;

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        if (null != params) {
            params.height = drawableHeight;
        }
    }

    public void setDrawableSizeByPx(int width, int height) {
        this.drawableWidth = width;
        this.drawableHeight = height;

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        if (null != params) {
            params.width = width;
            params.height = height;
        }
    }

    public void setDrawableSizeByDp(int width, int height) {
        width = ConversionHelper.dpToPx(getContext(), width);
        height = ConversionHelper.dpToPx(getContext(), height);
        setDrawableSizeByPx(width, height);
    }

    public DrawableGravity getDrawableGravity() {
        return drawableGravity;
    }

    public void setDrawableGravity(DrawableGravity gravity) {
        this.drawableGravity = gravity;

        setDrawableGravity();
        setPaddingAttrs();
    }

    public int getDrawablePadding() {
        return drawablePadding;
    }

    public void setDrawablePadding(int drawablePadding) {
        this.drawablePadding = drawablePadding;
        updatePaddings();
    }

    /**
     * RIPPLE
     */
    public int getRippleColor() {
        return rippleColor;
    }

    public void setRippleColor(int rippleColor) {
        this.rippleColor = rippleColor;
        setRippleColor(true);
    }

    public void setRippleColor(boolean state) {
        setRippleBackground(hasRippleColor = state);
    }

    public void setRipple(boolean state) {
        setRippleBackground(hasRipple = state);
    }

    private void setRippleBackground(boolean state) {
        if (state) {
            if (hasRippleColor) {
                RippleHelper.setRipple(this, rippleColor, backgroundColor);
            } else if (hasRipple) {
                RippleHelper.setSelectableItemBackground(getContext(), this);
            }
        } else {
            setBackgroundColor(backgroundColor);
        }
    }

    /**
     * GROUP
     */
    private void setEnabledAlpha(boolean enabled) {
        float alpha = 1f;
        if (!enabled)
            alpha = 0.5f;
        setAlpha(alpha);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setClickable(enabled);
        this.enabled = enabled;
        setEnabledAlpha(enabled);
        setRippleBackground(enabled);
    }

    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        this.clickable = clickable;
        setRippleBackground(clickable);
    }

    private void setState() {
        if (hasEnabled)
            setEnabled(enabled);
        else
            setClickable(clickable);
    }

    @Override
    public boolean isClickable() {
        return clickable;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }
}
