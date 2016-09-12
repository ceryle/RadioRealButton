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
import android.os.Build;
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
import co.ceryle.radiorealbutton.library.util.RoundHelper;

/**
 * Created by EGE on 09/08/2016.
 */
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

    private int buttonWidth;
    private ArrayList<RadioRealButton> radioRealButtons;

    private View movingView, viewBottomLine;
    private RoundedCornerLayout cardView;

    private LinearLayout container;

    private void init(AttributeSet attrs) {
        getAttributes(attrs);
        radioRealButtons = new ArrayList<>();

        View view = inflate(getContext(), R.layout.ceryle_radiorealbuttongroup, this);

        container = (LinearLayout) view.findViewById(R.id.ceryle_radioRealButtonGroup_container);
        movingView = view.findViewById(R.id.ceryle_radioRealButtonGroup_movingView);
        viewBottomLine = view.findViewById(R.id.ceryle_radioRealButtonGroup_bottomLine);
        cardView = (RoundedCornerLayout) view.findViewById(R.id.ceryle_radioRealButtonGroup_frameLayout);

        setCardViewAttrs();
        setBottomLineAttrs();
        setSelectorAttrs();
        initInterpolations();
        setContainerAttrs();
        setGroupBackgroundColor();
    }

    private void setCardViewAttrs() {
        if (shadow) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.setElevation(shadowElevation);
            }
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cardView.getLayoutParams();
        if (shadowMargin != -1) {
            layoutParams.setMargins((int) shadowMargin, (int) shadowMargin, (int) shadowMargin, (int) shadowMargin);
        } else {
            layoutParams.setMargins((int) shadowMarginLeft, (int) shadowMarginTop, (int) shadowMarginRight, (int) shadowMarginBottom);
        }

        cardView.setRadius(radius);
    }

    private void setBottomLineAttrs() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewBottomLine.getLayoutParams();

        layoutParams.height = (int) bottomLineSize;

        if (bottomLineBringToFront)
            viewBottomLine.bringToFront();

        RoundHelper.makeRound(viewBottomLine, bottomLineColor, (int) bottomLineRadius, (int) bottomLineRadius);
    }

    private void setSelectorAttrs() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) movingView.getLayoutParams();

        if (selectorBringToFront)
            movingView.bringToFront();

        layoutParams.height = (int) selectorSize;

        if (selectorAboveOfBottomLine) {
            layoutParams.setMargins(0, 0, 0, (int) bottomLineSize);
        }

        RoundHelper.makeRound(movingView, selectorColor, (int) selectorRadius, (int) selectorRadius);
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

    private int dividerColor, bottomLineColor, selectorColor, animateImages, animateTexts, animateImagesDuration, animateTextsDuration, animateSelector, animateSelectorDuration, animateImagesExit, animateImagesExitDuration, animateTextsExit, animateTextsExitDuration, position, buttonPadding, buttonPaddingLeft, buttonPaddingRight, buttonPaddingTop, buttonPaddingBottom, groupBackgroundColor;

    private float bottomLineSize, dividerSize, dividerRadius, dividerPadding, shadowElevation, selectorSize,
            shadowMargin, shadowMarginTop, shadowMarginBottom, shadowMarginLeft, shadowMarginRight, radius, bottomLineRadius, selectorRadius, animateImagesScale, animateTextsScale;

    private boolean shadow, bottomLineBringToFront, selectorBringToFront, selectorAboveOfBottomLine,
            hasPadding, hasPaddingLeft, hasPaddingRight, hasPaddingTop, hasPaddingBottom, hasGroupBackgroundColor;

    @SuppressWarnings("ResourceType")
    private void getAttributes(AttributeSet attrs) {
        /** GET ATTRIBUTES FROM XML **/
        // Custom attributes
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

        position = typedArray.getInt(R.styleable.RadioRealButtonGroup_rrbg_position, 0);
        lastPosition = position;

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

        typedArray.recycle();
    }

    public void setGroupBackgroundColor() {
        if (hasGroupBackgroundColor)
            container.setBackgroundColor(groupBackgroundColor);
    }

    private void setButtonsPadding(int position) {
        if (hasPadding)
            radioRealButtons.get(position).setButtonPadding(buttonPadding);
        else if (hasPaddingBottom || hasPaddingTop || hasPaddingLeft || hasPaddingRight)
            radioRealButtons.get(position).setButtonPadding(buttonPaddingLeft, buttonPaddingTop, buttonPaddingRight, buttonPaddingBottom);
    }

    private boolean isAnimationAlreadySet = false;

    private void setAnimationAttrs() {
        if (radioRealButtons.size() > 0 && !isAnimationAlreadySet) {
            isAnimationAlreadySet = true;

            if (animateImages != 0)
                radioRealButtons.get(position).bounceImage(animateImagesScale, 0, interpolatorImage);
            if (animateTexts != 0)
                radioRealButtons.get(position).bounceText(animateTextsScale, 0, interpolatorText);

            movingView.animate()
                    .translationX(buttonWidth * position + dividerSize * position)
                    .setDuration(0);

        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (container == null) {
            super.addView(child, index, params);
        } else {
            RadioRealButton radioRealButton = (RadioRealButton) child;

            container.addView(child, index, params);

            final int c = radioRealButtons.size();
            radioRealButton.setOnClickedButton(new RadioRealButton.OnClickedButton() {
                @Override
                public void onClickedButton(View view) {
                    toggleSegmentedButton(c);
                }
            });
            radioRealButtons.add(radioRealButton);

            setButtonsPadding(c);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
            param.gravity = Gravity.CENTER;
            radioRealButton.setLayoutParams(param);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            if (radioRealButtons.size() > 0) {
                buttonWidth = radioRealButtons.get(0).getWidth();
                movingView.getLayoutParams().width = buttonWidth;

                setAnimationAttrs();
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (movingView != null) {
            movingView.requestLayout();
        }
    }


    private void toPositionMovingView(int atWhichButton, View view) {
        view.animate()
                .translationX((buttonWidth + 1) * atWhichButton + (dividerSize) * atWhichButton)
                .setInterpolator(interpolatorSelector)
                .setDuration(animateSelectorDuration);
    }

    private int lastPosition = 0;

    private void toggleSegmentedButton(int i) {
        toPositionMovingView(i, movingView);
        if (null != onClickedButtonPosition)
            onClickedButtonPosition.onClickedButtonPosition(i);

        if (i != lastPosition) {
            if (animateTexts != 0) {
                radioRealButtons.get(i).bounceText(animateTextsScale, animateTextsDuration, interpolatorText);
                radioRealButtons.get(lastPosition).bounceText(-animateTextsScale, animateTextsExitDuration, interpolatorTextExit);
            }
            if (animateImages != 0) {
                radioRealButtons.get(i).bounceImage(animateImagesScale, animateImagesDuration, interpolatorImage);
                radioRealButtons.get(lastPosition).bounceImage(-animateImagesScale, animateImagesExitDuration, interpolatorImageExit);
            }
        }
        lastPosition = i;
    }

    private OnClickedButtonPosition onClickedButtonPosition;

    public void setOnClickedButtonPosition(OnClickedButtonPosition onClickedButtonPosition) {
        this.onClickedButtonPosition = onClickedButtonPosition;
    }

    public interface OnClickedButtonPosition {
        void onClickedButtonPosition(int position);
    }


    // Setters...
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
        this.radioRealButtons = radioRealButtons;
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

    public void setPosition(int position) {
        this.position = position;
    }
}
