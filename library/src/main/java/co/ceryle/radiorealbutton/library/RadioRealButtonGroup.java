/*
 * Copyright (C) 2016 Ege Aker <egeaker@gmail.com>
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
package co.ceryle.radiorealbutton.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import co.ceryle.radiorealbutton.R;
import co.ceryle.radiorealbutton.library.util.RippleHelper;
import co.ceryle.radiorealbutton.library.util.RoundHelper;

public class RadioRealButtonGroup extends RelativeLayout {

    public RadioRealButtonGroup(Context context) {
        super(context);
        init(null);
    }

    public RadioRealButtonGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RadioRealButtonGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RadioRealButtonGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("state", super.onSaveInstanceState());
        bundle.putInt("position", lastPosition);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            lastPosition = bundle.getInt("position");
            state = bundle.getParcelable("state");
        }
        super.onRestoreInstanceState(state);
    }

    private int buttonWidth;
    private ArrayList<RadioRealButton> buttons;

    private View v_selector, v_bottomLine;
    private RoundedCornerLayout groupContainer;

    private LinearLayout container;

    private void init(AttributeSet attrs) {
        getAttributes(attrs);
        buttons = new ArrayList<>();

        View view = inflate(getContext(), R.layout.ceryle_radiorealbuttongroup, this);

        container = (LinearLayout) view.findViewById(R.id.ceryle_radioRealButtonGroup_container);
        v_selector = view.findViewById(R.id.ceryle_radioRealButtonGroup_movingView);
        v_bottomLine = view.findViewById(R.id.ceryle_radioRealButtonGroup_bottomLine);
        groupContainer = (RoundedCornerLayout) view.findViewById(R.id.ceryle_radioRealButtonGroup_frameLayout);

        setGroupContainerAttrs();
        setBottomLineAttrs();
        setSelectorAttrs();
        initInterpolations();
        setContainerAttrs();
        setGroupBackgroundColor();
        setBorderAttrs();
    }

    private void setGroupContainerAttrs() {
        if (shadow) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                groupContainer.setElevation(shadowElevation);
            }
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) groupContainer.getLayoutParams();
        if (shadowMargin != -1) {
            layoutParams.setMargins((int) shadowMargin, (int) shadowMargin, (int) shadowMargin, (int) shadowMargin);
        } else {
            layoutParams.setMargins((int) shadowMarginLeft, (int) shadowMarginTop, (int) shadowMarginRight, (int) shadowMarginBottom);
        }

        groupContainer.setRadius(radius);
    }

    private void setBottomLineAttrs() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) v_bottomLine.getLayoutParams();

        layoutParams.height = (int) bottomLineSize;

        if (bottomLineBringToFront)
            v_bottomLine.bringToFront();

        RoundHelper.makeRound(v_bottomLine, bottomLineColor, (int) bottomLineRadius, (int) bottomLineRadius);
    }

    private void setSelectorAttrs() {
        FrameLayout.LayoutParams selectorParams = (FrameLayout.LayoutParams) v_selector.getLayoutParams();
        FrameLayout.LayoutParams bottomLineParams = (FrameLayout.LayoutParams) v_bottomLine.getLayoutParams();

        if (selectorBringToFront)
            v_selector.bringToFront();

        selectorParams.height = (int) selectorSize;

        int topMargin = 0, bottomMargin = 0;

        if (selectorTop) {
            selectorParams.gravity = Gravity.TOP;
            bottomLineParams.gravity = Gravity.TOP;
            topMargin = (int) bottomLineSize;
        } else if (selectorBottom) {
            selectorParams.gravity = Gravity.BOTTOM;
            bottomLineParams.gravity = Gravity.BOTTOM;
            bottomMargin = (int) bottomLineSize;
        }

        if (selectorAboveOfBottomLine) {
            selectorParams.setMargins(0, topMargin, 0, bottomMargin);
        }

        RoundHelper.makeRound(v_selector, selectorColor, (int) selectorRadius, (int) selectorRadius);
    }


    private Interpolator interpolatorImage, interpolatorText, interpolatorSelector;
    private Interpolator interpolatorImageExit, interpolatorTextExit;

    private void initInterpolations() {
        ArrayList<Class> interpolatorList = new ArrayList<Class>() {{
            add(FastOutSlowInInterpolator.class);
            add(BounceInterpolator.class);
            add(LinearInterpolator.class);
            add(DecelerateInterpolator.class);
            add(CycleInterpolator.class);
            add(AnticipateInterpolator.class);
            add(AccelerateDecelerateInterpolator.class);
            add(AccelerateInterpolator.class);
            add(AnticipateOvershootInterpolator.class);
            add(FastOutLinearInInterpolator.class);
            add(LinearOutSlowInInterpolator.class);
            add(OvershootInterpolator.class);
        }};

        try {
            interpolatorText = (Interpolator) interpolatorList.get(animateTexts).newInstance();
            interpolatorImage = (Interpolator) interpolatorList.get(animateImages).newInstance();
            interpolatorSelector = (Interpolator) interpolatorList.get(animateSelector).newInstance();

            interpolatorTextExit = (Interpolator) interpolatorList.get(animateTextsExit).newInstance();
            interpolatorImageExit = (Interpolator) interpolatorList.get(animateImagesExit).newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setContainerAttrs() {
        RoundHelper.makeDividerRound(container, dividerColor, (int) dividerRadius, (int) dividerSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            container.setDividerPadding((int) dividerPadding);
        }
    }

    public void setGroupBackgroundColor() {
        if (hasGroupBackgroundColor)
            container.setBackgroundColor(groupBackgroundColor);
    }

    private RelativeLayout.LayoutParams borderParams;

    private void setBorderAttrs() {
        View borderView = findViewById(R.id.ceryle_radioRealButtonGroup_border);
        borderParams = (RelativeLayout.LayoutParams) borderView.getLayoutParams();
        int margin = (int) shadowMargin;
        borderParams.setMargins(margin - borderSize, margin - borderSize, margin - borderSize, margin - borderSize);

        if (borderSize > 0) {
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(borderColor);
            gd.setCornerRadius(radius + 3); // TODO

            RippleHelper.setBackground(borderView, gd);
        }
    }

    private int borderSize, borderColor, dividerColor, bottomLineColor, selectorColor, animateImages, animateTexts,
            animateImagesDuration, animateTextsDuration, animateSelector, animateSelectorDuration, animateImagesExit,
            animateImagesExitDuration, animateTextsExit, animateTextsExitDuration, lastPosition, buttonPadding,
            buttonPaddingLeft, buttonPaddingRight, buttonPaddingTop, buttonPaddingBottom, groupBackgroundColor;

    private float bottomLineSize, dividerSize, dividerRadius, dividerPadding, shadowElevation, selectorSize,
            shadowMargin, shadowMarginTop, shadowMarginBottom, shadowMarginLeft, shadowMarginRight, radius,
            bottomLineRadius, selectorRadius, animateImagesScale, animateTextsScale;

    private boolean shadow, bottomLineBringToFront, selectorBringToFront, selectorAboveOfBottomLine, selectorTop, selectorBottom,
            hasPadding, hasPaddingLeft, hasPaddingRight, hasPaddingTop, hasPaddingBottom, hasGroupBackgroundColor, hasAnimation,
            clickable, enabled;

    private void getAttributes(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RadioRealButtonGroup);

        bottomLineColor = typedArray.getColor(R.styleable.RadioRealButtonGroup_rrbg_bottomLineColor, Color.GRAY);
        bottomLineSize = typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_bottomLineSize, 6);
        bottomLineBringToFront = typedArray.getBoolean(R.styleable.RadioRealButtonGroup_rrbg_bottomLineBringToFront, false);
        bottomLineRadius = typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_bottomLineRadius, 0);

        selectorColor = typedArray.getColor(R.styleable.RadioRealButtonGroup_rrbg_selectorColor, Color.GRAY);
        selectorBringToFront = typedArray.getBoolean(R.styleable.RadioRealButtonGroup_rrbg_selectorBringToFront, false);
        selectorAboveOfBottomLine = typedArray.getBoolean(R.styleable.RadioRealButtonGroup_rrbg_selectorAboveOfBottomLine, false);
        selectorSize = typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_selectorSize, 12);
        selectorRadius = typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_selectorRadius, 0);
        animateSelector = typedArray.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateSelector, 0);
        animateSelectorDuration = typedArray.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateSelectorDuration, 500);

        dividerSize = typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_dividerSize, 3);
        dividerRadius = typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_dividerRadius, 0);
        dividerPadding = typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_dividerPadding, 30);
        dividerColor = typedArray.getColor(R.styleable.RadioRealButtonGroup_rrbg_dividerColor, Color.GRAY);

        shadow = typedArray.getBoolean(R.styleable.RadioRealButtonGroup_rrbg_shadow, false);
        shadowElevation = typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_shadowElevation, 0);
        shadowMargin = typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_shadowMargin, -1);
        shadowMarginTop = typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_shadowMarginTop, 0);
        shadowMarginBottom = typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_shadowMarginBottom, 0);
        shadowMarginLeft = typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_shadowMarginLeft, 0);
        shadowMarginRight = typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_shadowMarginRight, 0);
        radius = typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_radius, 0);

        animateImages = typedArray.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateImages_enter, 0);
        animateImagesExit = typedArray.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateImages_exit, 0);
        animateImagesDuration = typedArray.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateImages_enterDuration, 500);
        animateImagesExitDuration = typedArray.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateImages_exitDuration, 100);
        animateImagesScale = typedArray.getFloat(R.styleable.RadioRealButtonGroup_rrbg_animateImagesScale, 0.2f);

        animateTexts = typedArray.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateTexts_enter, 0);
        animateTextsExit = typedArray.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateTexts_exit, 0);
        animateTextsDuration = typedArray.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateTexts_enterDuration, 500);
        animateTextsExitDuration = typedArray.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateTexts_exitDuration, 100);
        animateTextsScale = typedArray.getFloat(R.styleable.RadioRealButtonGroup_rrbg_animateTextsScale, 0.2f);

        lastPosition = typedArray.getInt(R.styleable.RadioRealButtonGroup_rrbg_position, -1);

        buttonPadding = (int) typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_buttonsPadding, 0);
        buttonPaddingLeft = (int) typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_buttonsPaddingLeft, 0);
        buttonPaddingRight = (int) typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_buttonsPaddingRight, 0);
        buttonPaddingTop = (int) typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_buttonsPaddingTop, 0);
        buttonPaddingBottom = (int) typedArray.getDimension(R.styleable.RadioRealButtonGroup_rrbg_buttonsPaddingBottom, 0);

        hasPadding = typedArray.hasValue(R.styleable.RadioRealButtonGroup_rrbg_buttonsPadding);
        hasPaddingLeft = typedArray.hasValue(R.styleable.RadioRealButtonGroup_rrbg_buttonsPaddingLeft);
        hasPaddingRight = typedArray.hasValue(R.styleable.RadioRealButtonGroup_rrbg_buttonsPaddingRight);
        hasPaddingTop = typedArray.hasValue(R.styleable.RadioRealButtonGroup_rrbg_buttonsPaddingTop);
        hasPaddingBottom = typedArray.hasValue(R.styleable.RadioRealButtonGroup_rrbg_buttonsPaddingBottom);

        groupBackgroundColor = typedArray.getColor(R.styleable.RadioRealButtonGroup_rrbg_backgroundColor, Color.WHITE);
        hasGroupBackgroundColor = typedArray.hasValue(R.styleable.RadioRealButtonGroup_rrbg_backgroundColor);

        selectorTop = typedArray.getBoolean(R.styleable.RadioRealButtonGroup_rrbg_selectorTop, false);
        selectorBottom = typedArray.getBoolean(R.styleable.RadioRealButtonGroup_rrbg_selectorBottom, true);

        borderSize = typedArray.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_rrbg_borderSize, 0);
        borderColor = typedArray.getColor(R.styleable.RadioRealButtonGroup_rrbg_borderColor, Color.BLACK);

        hasAnimation = typedArray.getBoolean(R.styleable.RadioRealButtonGroup_rrbg_animate, true);

        enabled = typedArray.getBoolean(R.styleable.RadioRealButtonGroup_rrbg_enabled, true);
        try {
            clickable = typedArray.getBoolean(R.styleable.RadioRealButtonGroup_android_clickable, true);
        } catch (Exception ex) {
            Log.d("RadioRealButtonGroup", ex.toString());
        }

        typedArray.recycle();
    }

    private void setButtonPadding(RadioRealButton button) {
        if (hasPadding)
            button.setButtonPadding(buttonPadding, buttonPadding, buttonPadding, buttonPadding);
        else if (hasPaddingBottom || hasPaddingTop || hasPaddingLeft || hasPaddingRight)
            button.setButtonPadding(buttonPaddingLeft, buttonPaddingTop, buttonPaddingRight, buttonPaddingBottom);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (container == null) {
            super.addView(child, index, params);
        } else {
            RadioRealButton button = (RadioRealButton) child;

            final int buttonPosition = buttons.size();
            button.setOnClickedButton(new RadioRealButton.OnClickedButton() {
                @Override
                public void onClickedButton(View view) {
                    if (enabled && clickable)
                        setPosition(buttonPosition, hasAnimation);
                }
            });

            setButtonPadding(button);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
            param.gravity = Gravity.CENTER;

            container.addView(child, index, param);
            buttons.add(button);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!changed)
            return;

        if (buttons.size() > 0) {
            buttonWidth = buttons.get(0).getWidth();
            v_selector.getLayoutParams().width = buttonWidth;

            setPosition(lastPosition, false);
        }
        borderParams.width = getWidth() + borderSize;
        borderParams.height = buttons.get(0).getHeight() + borderSize * 2;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (v_selector != null) {
            v_selector.requestLayout();
        }
    }

    public void setPosition(int position, boolean hasAnimation) {
        if (hasAnimation) {
            moveSelector(position, v_selector);
            if (null != onClickedButtonPosition)
                onClickedButtonPosition.onClickedButtonPosition(position);
        } else {
            v_selector.setX(buttonWidth * position + dividerSize * position);
        }

        animateImageText(position, lastPosition);
    }

    private void moveSelector(int toPosition, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            view.animate()
                    .translationX((buttonWidth + 1) * toPosition + (dividerSize) * toPosition)
                    .setInterpolator(interpolatorSelector)
                    .setDuration(animateSelectorDuration);
        } else {
            v_selector.setX((buttonWidth + 1) * toPosition + (dividerSize) * toPosition);
        }
    }

    private void animateImageText(int position, int lastPosition) {
        if (animateTexts != 0)
            animateText(position, lastPosition);
        if (animateImages != 0)
            animateImage(position, lastPosition);
        this.lastPosition = position;
    }


    private void animateText(int position, int lastPosition) {
        if (lastPosition != -1 && lastPosition != position)
            buttons.get(lastPosition).bounceText(1, animateTextsExitDuration, interpolatorTextExit);
        if (position != -1)
            buttons.get(position).bounceText(1 + animateTextsScale, animateTextsDuration, interpolatorText);
    }

    private void animateImage(int position, int lastPosition) {
        if (lastPosition != -1 && lastPosition != position)
            buttons.get(lastPosition).bounceImage(1, animateImagesExitDuration, interpolatorImageExit);
        if (position != -1)
            buttons.get(position).bounceImage(1 + animateImagesScale, animateImagesDuration, interpolatorImage);
    }


    private OnClickedButtonPosition onClickedButtonPosition;

    public void setOnClickedButtonPosition(OnClickedButtonPosition onClickedButtonPosition) {
        this.onClickedButtonPosition = onClickedButtonPosition;
    }

    public interface OnClickedButtonPosition {
        void onClickedButtonPosition(int position);
    }

    public void setAnimateImages(int animateImages) {
        this.animateImages = animateImages;
    }

    public void setAnimateImagesDuration(int animateImagesDuration) {
        this.animateImagesDuration = animateImagesDuration;
    }

    public void setAnimateImagesExit(int animateImagesExit) {
        this.animateImagesExit = animateImagesExit;
    }

    public void setAnimateImagesExitDuration(int animateImagesExitDuration) {
        this.animateImagesExitDuration = animateImagesExitDuration;
    }

    public void setAnimateImagesScale(float animateImagesScale) {
        this.animateImagesScale = animateImagesScale;
    }

    public void setAnimateSelector(int animateSelector) {
        this.animateSelector = animateSelector;
    }

    public void setAnimateSelectorDuration(int animateSelectorDuration) {
        this.animateSelectorDuration = animateSelectorDuration;
    }

    public void setAnimateTexts(int animateTexts) {
        this.animateTexts = animateTexts;
    }

    public void setAnimateTextsDuration(int animateTextsDuration) {
        this.animateTextsDuration = animateTextsDuration;
    }

    public void setAnimateTextsExit(int animateTextsExit) {
        this.animateTextsExit = animateTextsExit;
    }

    public void setAnimateTextsExitDuration(int animateTextsExitDuration) {
        this.animateTextsExitDuration = animateTextsExitDuration;
    }

    public void setAnimateTextsScale(float animateTextsScale) {
        this.animateTextsScale = animateTextsScale;
    }

    public void setBottomLineBringToFront(boolean bottomLineBringToFront) {
        this.bottomLineBringToFront = bottomLineBringToFront;
    }

    public void setBottomLineColor(int bottomLineColor) {
        this.bottomLineColor = bottomLineColor;
    }

    public void setBottomLineRadius(float bottomLineRadius) {
        this.bottomLineRadius = bottomLineRadius;
    }

    public void setBottomLineSize(float bottomLineSize) {
        this.bottomLineSize = bottomLineSize;
    }

    public void setContainer(LinearLayout container) {
        this.container = container;
    }

    public void setButtonWidth(int buttonWidth) {
        this.buttonWidth = buttonWidth;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
    }

    public void setDividerPadding(float dividerPadding) {
        this.dividerPadding = dividerPadding;
    }

    public void setDividerRadius(float dividerRadius) {
        this.dividerRadius = dividerRadius;
    }

    public void setDividerSize(float dividerSize) {
        this.dividerSize = dividerSize;
    }

    public void setInterpolatorImage(Interpolator interpolatorImage) {
        this.interpolatorImage = interpolatorImage;
    }

    public void setInterpolatorImageExit(Interpolator interpolatorImageExit) {
        this.interpolatorImageExit = interpolatorImageExit;
    }

    public void setInterpolatorSelector(Interpolator interpolatorSelector) {
        this.interpolatorSelector = interpolatorSelector;
    }

    public void setInterpolatorText(Interpolator interpolatorText) {
        this.interpolatorText = interpolatorText;
    }

    public void setInterpolatorTextExit(Interpolator interpolatorTextExit) {
        this.interpolatorTextExit = interpolatorTextExit;
    }

    public void setRadioRealButtons(ArrayList<RadioRealButton> radioRealButtons) {
        this.buttons = radioRealButtons;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setSelectorAboveOfBottomLine(boolean selectorAboveOfBottomLine) {
        this.selectorAboveOfBottomLine = selectorAboveOfBottomLine;
    }

    public void setSelectorBringToFront(boolean selectorBringToFront) {
        this.selectorBringToFront = selectorBringToFront;
    }

    public void setSelectorColor(int selectorColor) {
        this.selectorColor = selectorColor;
    }

    public void setSelectorRadius(float selectorRadius) {
        this.selectorRadius = selectorRadius;
    }

    public void setSelectorSize(float selectorSize) {
        this.selectorSize = selectorSize;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public void setShadowElevation(float shadowElevation) {
        this.shadowElevation = shadowElevation;
    }

    public void setShadowMargin(float shadowMargin) {
        this.shadowMargin = shadowMargin;
    }

    public void setShadowMarginBottom(float shadowMarginBottom) {
        this.shadowMarginBottom = shadowMarginBottom;
    }

    public void setShadowMarginLeft(float shadowMarginLeft) {
        this.shadowMarginLeft = shadowMarginLeft;
    }

    public void setShadowMarginRight(float shadowMarginRight) {
        this.shadowMarginRight = shadowMarginRight;
    }

    public void setShadowMarginTop(float shadowMarginTop) {
        this.shadowMarginTop = shadowMarginTop;
    }

    public int getPosition() {
        return lastPosition;
    }

    public int getNumberOfButton() {
        return buttons.size();
    }


    public int getButtonWidth() {
        return buttonWidth;
    }

    public ArrayList<RadioRealButton> getRadioRealButtons() {
        return buttons;
    }

    public Interpolator getInterpolatorImage() {
        return interpolatorImage;
    }

    public Interpolator getInterpolatorText() {
        return interpolatorText;
    }

    public Interpolator getInterpolatorSelector() {
        return interpolatorSelector;
    }

    public Interpolator getInterpolatorImageExit() {
        return interpolatorImageExit;
    }

    public Interpolator getInterpolatorTextExit() {
        return interpolatorTextExit;
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public int getBottomLineColor() {
        return bottomLineColor;
    }

    public int getSelectorColor() {
        return selectorColor;
    }

    public int getAnimateImages() {
        return animateImages;
    }

    public int getAnimateTexts() {
        return animateTexts;
    }

    public int getAnimateImagesDuration() {
        return animateImagesDuration;
    }

    public int getAnimateTextsDuration() {
        return animateTextsDuration;
    }

    public int getAnimateSelector() {
        return animateSelector;
    }

    public int getAnimateSelectorDuration() {
        return animateSelectorDuration;
    }

    public int getAnimateImagesExit() {
        return animateImagesExit;
    }

    public int getAnimateImagesExitDuration() {
        return animateImagesExitDuration;
    }

    public int getAnimateTextsExit() {
        return animateTextsExit;
    }

    public int getAnimateTextsExitDuration() {
        return animateTextsExitDuration;
    }

    public int getButtonPadding() {
        return buttonPadding;
    }

    public int getButtonPaddingLeft() {
        return buttonPaddingLeft;
    }

    public int getButtonPaddingRight() {
        return buttonPaddingRight;
    }

    public int getButtonPaddingTop() {
        return buttonPaddingTop;
    }

    public int getButtonPaddingBottom() {
        return buttonPaddingBottom;
    }

    public int getGroupBackgroundColor() {
        return groupBackgroundColor;
    }

    public float getBottomLineSize() {
        return bottomLineSize;
    }

    public float getDividerSize() {
        return dividerSize;
    }

    public float getDividerRadius() {
        return dividerRadius;
    }

    public float getDividerPadding() {
        return dividerPadding;
    }

    public float getShadowElevation() {
        return shadowElevation;
    }

    public float getSelectorSize() {
        return selectorSize;
    }

    public float getShadowMargin() {
        return shadowMargin;
    }

    public float getShadowMarginTop() {
        return shadowMarginTop;
    }

    public float getShadowMarginBottom() {
        return shadowMarginBottom;
    }

    public float getShadowMarginLeft() {
        return shadowMarginLeft;
    }

    public float getShadowMarginRight() {
        return shadowMarginRight;
    }

    public float getRadius() {
        return radius;
    }

    public float getBottomLineRadius() {
        return bottomLineRadius;
    }

    public float getSelectorRadius() {
        return selectorRadius;
    }

    public float getAnimateImagesScale() {
        return animateImagesScale;
    }

    public float getAnimateTextsScale() {
        return animateTextsScale;
    }

    public boolean isShadow() {
        return shadow;
    }

    public boolean isBottomLineBringToFront() {
        return bottomLineBringToFront;
    }

    public boolean isSelectorBringToFront() {
        return selectorBringToFront;
    }

    public boolean isSelectorAboveOfBottomLine() {
        return selectorAboveOfBottomLine;
    }

    public void deselect() {
        int size = buttons.size();

        if (lastPosition > size / 2) {
            animateImageText(size, lastPosition);
            moveSelector(size, v_selector);
        } else {
            animateImageText(-1, lastPosition);
            moveSelector(-1, v_selector);
        }

        lastPosition = -1;
    }

    private void setRippleState(boolean state) {
        for (RadioRealButton b : buttons) {
            b.setRipple(state);
        }
    }

    private void setEnabledAlpha(boolean enabled) {
        float alpha = 1f;
        if (!enabled)
            alpha = 0.5f;

        setAlpha(alpha);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setEnabledAlpha(enabled);
        setRippleState(enabled);
    }

    @Override
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
        setRippleState(clickable);
    }

}
