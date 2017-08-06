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

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
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

import java.util.ArrayList;
import java.util.List;

public class RadioRealButtonGroup extends RoundedCornerLayout implements RadioRealButton.OnSelectorColorChangedListener {

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

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("state", super.onSaveInstanceState());
        bundle.putInt("position", lastPosition);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            state = bundle.getParcelable("state");
            int position = bundle.getInt("position");

            if (lastPosition != position) {
                if (animationType == ANIM_TRANSLATE_X) {
                    if (initialPosition != -1)
                        v_selectors.get(initialPosition).setVisibility(INVISIBLE);
                    v_selectors.get(position).setVisibility(VISIBLE);
                    lastPosition = initialPosition = position;
                }
                setPosition(position, false);
            }
        }
        super.onRestoreInstanceState(state);
    }

    private View v_bottomLine;
    private LinearLayout container, selectorContainer;

    private List<View> v_selectors = new ArrayList<>();
    private List<RadioRealButton> buttons = new ArrayList<>();

    private void init(AttributeSet attrs) {
        getAttributes(attrs);

        setBorderAttrs();
        initViews();

        setBottomLineAttrs();
        setSelectorAttrs();

        initInterpolations();

        setState();
    }

    private void initViews() {
        setCornerRadius(radius);

        BackgroundView backgroundView = new BackgroundView(getContext());
        backgroundView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        backgroundView.setBackgroundColor(groupBackgroundColor);
        addView(backgroundView);

        selectorContainer = new LinearLayout(getContext());
        selectorContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        selectorContainer.setOrientation(LinearLayout.HORIZONTAL);
        selectorContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        RoundHelper.makeDividerRound(selectorContainer, selectorDividerColor, selectorDividerRadius, selectorDividerSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            selectorContainer.setDividerPadding(selectorDividerPadding);
        }
        addView(selectorContainer);

        container = new LinearLayout(getContext());
        container.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        RoundHelper.makeDividerRound(container, dividerColor, dividerRadius, dividerSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            container.setDividerPadding(dividerPadding);
        }
        addView(container);

        v_bottomLine = new View(getContext());
        v_bottomLine.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 3));
        addView(v_bottomLine);
    }

    private void setBottomLineAttrs() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) v_bottomLine.getLayoutParams();

        layoutParams.height = bottomLineSize;

        if (bottomLineBringToFront)
            v_bottomLine.bringToFront();

        updateViewBottomLine();
    }

    private void setSelectorAttrs() {
        FrameLayout.LayoutParams selectorParams = (FrameLayout.LayoutParams) selectorContainer.getLayoutParams();
        FrameLayout.LayoutParams bottomLineParams = (FrameLayout.LayoutParams) v_bottomLine.getLayoutParams();

        if (selectorBringToFront)
            selectorContainer.bringToFront();

        if (!selectorFullSize)
            selectorParams.height = selectorSize;

        int topMargin = 0, bottomMargin = 0;

        if (selectorTop) {
            selectorParams.gravity = Gravity.TOP;
            bottomLineParams.gravity = Gravity.TOP;
            topMargin = bottomLineSize;
        } else if (selectorBottom) {
            selectorParams.gravity = Gravity.BOTTOM;
            bottomLineParams.gravity = Gravity.BOTTOM;
            bottomMargin = bottomLineSize;
        }

        if (selectorAboveOfBottomLine) {
            selectorParams.setMargins(0, topMargin, 0, bottomMargin);
        }
    }

    private Interpolator interpolatorDrawablesEnter, interpolatorText, interpolatorSelector;
    private Interpolator interpolatorDrawablesExit, interpolatorTextExit;

    private void initInterpolations() {
        Class[] interpolations = {
                FastOutSlowInInterpolator.class,
                BounceInterpolator.class,
                LinearInterpolator.class,
                DecelerateInterpolator.class,
                CycleInterpolator.class,
                AnticipateInterpolator.class,
                AccelerateDecelerateInterpolator.class,
                AccelerateInterpolator.class,
                AnticipateOvershootInterpolator.class,
                FastOutLinearInInterpolator.class,
                LinearOutSlowInInterpolator.class,
                OvershootInterpolator.class};

        try {
            interpolatorText = (Interpolator) interpolations[animateTextsEnter].newInstance();
            interpolatorDrawablesEnter = (Interpolator) interpolations[animateDrawablesEnter].newInstance();
            interpolatorSelector = (Interpolator) interpolations[animateSelector].newInstance();

            interpolatorTextExit = (Interpolator) interpolations[animateTextsExit].newInstance();
            interpolatorDrawablesExit = (Interpolator) interpolations[animateDrawablesExit].newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBorderAttrs() {
        setStroke(hasBorder);
        setStrokeColor(borderColor);
        setStrokeSize(borderSize);
    }

    private int borderSize, borderColor, dividerColor, bottomLineColor, selectorColor, animateDrawablesEnter, animateTextsEnter, animationType,
            animateDrawablesEnterDuration, animateTextsEnterDuration, animateSelector, animateSelectorDuration, animateSelectorDelay, animateDrawablesExit,
            animateDrawablesExitDuration, animateTextsExit, animateTextsExitDuration, lastPosition, buttonPadding,
            buttonPaddingLeft, buttonPaddingRight, buttonPaddingTop, buttonPaddingBottom, groupBackgroundColor,
            dividerPadding, dividerSize, dividerRadius, bottomLineSize, bottomLineRadius, selectorSize, selectorRadius, initialPosition,
            selectorDividerSize, selectorDividerRadius, selectorDividerColor, selectorDividerPadding, checkedButtonId,
            animateTextsColorExit, animateTextsColorEnter, animateTextsColorDuration, animateTextsColorDurationExit, animateTextsColorDurationEnter,
            animateDrawablesTintExit, animateDrawablesTintEnter, animateDrawablesTintDuration, animateDrawablesTintDurationExit, animateDrawablesTintDurationEnter;

    private float radius, animateDrawablesScale, animateTextsScale;

    private boolean bottomLineBringToFront, selectorBringToFront, selectorAboveOfBottomLine, selectorTop, selectorBottom, hasPadding,
            hasPaddingLeft, hasPaddingRight, hasPaddingTop, hasPaddingBottom, clickable, enabled,
            enableDeselection, hasEnabled, hasClickable, hasBorder, hasAnimateDrawables, hasAnimateTexts, hasAnimation, selectorFullSize,
            hasAnimateTextsColor, hasAnimateDrawablesTint;

    private void getAttributes(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.RadioRealButtonGroup);

        hasAnimateTextsColor = ta.hasValue(R.styleable.RadioRealButtonGroup_rrbg_animateTexts_textColorTo);
        animateTextsColorExit = ta.getColor(R.styleable.RadioRealButtonGroup_rrbg_animateTexts_textColorFrom, Color.GRAY);
        animateTextsColorEnter = ta.getColor(R.styleable.RadioRealButtonGroup_rrbg_animateTexts_textColorTo, Color.BLACK);
        int animateTextsColorDuration = ta.getColor(R.styleable.RadioRealButtonGroup_rrbg_animateTexts_textColor_duration, 500) / 2;
        animateTextsColorDurationExit = ta.getColor(R.styleable.RadioRealButtonGroup_rrbg_animateTexts_textColorFrom_duration, animateTextsColorDuration);
        animateTextsColorDurationEnter = ta.getColor(R.styleable.RadioRealButtonGroup_rrbg_animateTexts_textColorTo_duration, animateTextsColorDuration);

        hasAnimateDrawablesTint = ta.hasValue(R.styleable.RadioRealButtonGroup_rrbg_animateDrawables_tintColorTo);
        animateDrawablesTintExit = ta.getColor(R.styleable.RadioRealButtonGroup_rrbg_animateDrawables_tintColorFrom, Color.GRAY);
        animateDrawablesTintEnter = ta.getColor(R.styleable.RadioRealButtonGroup_rrbg_animateDrawables_tintColorTo, Color.BLACK);
        int animateDrawablesTintDuration = ta.getColor(R.styleable.RadioRealButtonGroup_rrbg_animateDrawables_tintColor_duration, 500) / 2;
        animateDrawablesTintDurationExit = ta.getColor(R.styleable.RadioRealButtonGroup_rrbg_animateDrawables_tintColorFrom_duration, animateDrawablesTintDuration);
        animateDrawablesTintDurationEnter = ta.getColor(R.styleable.RadioRealButtonGroup_rrbg_animateDrawables_tintColorTo_duration, animateDrawablesTintDuration);


        bottomLineColor = ta.getColor(R.styleable.RadioRealButtonGroup_rrbg_bottomLineColor, Color.GRAY);
        bottomLineSize = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_rrbg_bottomLineSize, 0);
        bottomLineBringToFront = ta.getBoolean(R.styleable.RadioRealButtonGroup_rrbg_bottomLineBringToFront, false);
        bottomLineRadius = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_rrbg_bottomLineRadius, 0);

        selectorColor = ta.getColor(R.styleable.RadioRealButtonGroup_rrbg_selectorColor, Color.GRAY);
        selectorBringToFront = ta.getBoolean(R.styleable.RadioRealButtonGroup_rrbg_selectorBringToFront, false);
        selectorAboveOfBottomLine = ta.getBoolean(R.styleable.RadioRealButtonGroup_rrbg_selectorAboveOfBottomLine, false);
        selectorSize = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_rrbg_selectorSize, 12);
        selectorRadius = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_rrbg_selectorRadius, 0);

        animateSelector = ta.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateSelector, 0);
        animateSelectorDuration = ta.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateSelector_duration, 500);
        animateSelectorDelay = ta.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateSelector_delay, 0);

        dividerSize = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_rrbg_dividerSize, 0);
        boolean hasDividerSize = ta.hasValue(R.styleable.RadioRealButtonGroup_rrbg_dividerSize);
        dividerRadius = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_rrbg_dividerRadius, 0);
        dividerPadding = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_rrbg_dividerPadding, 30);
        dividerColor = ta.getColor(R.styleable.RadioRealButtonGroup_rrbg_dividerColor, Color.TRANSPARENT);

        selectorDividerSize = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_rrbg_selectorDividerSize, dividerSize);
        if (!hasDividerSize) {
            dividerSize = selectorDividerSize;
        }
        selectorDividerRadius = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_rrbg_selectorDividerRadius, 0);
        selectorDividerPadding = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_rrbg_selectorDividerPadding, 0);
        selectorDividerColor = ta.getColor(R.styleable.RadioRealButtonGroup_rrbg_selectorDividerColor, Color.TRANSPARENT);

        radius = ta.getDimension(R.styleable.RadioRealButtonGroup_rrbg_radius, 0);

        animateDrawablesEnter = ta.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateDrawables_enter, 0);
        hasAnimateDrawables = ta.hasValue(R.styleable.RadioRealButtonGroup_rrbg_animateDrawables_enter);
        animateDrawablesExit = ta.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateDrawables_exit, 0);
        int animateDrawablesDuration = ta.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateDrawables_duration, 500) / 2;
        animateDrawablesEnterDuration = ta.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateDrawables_enterDuration, animateDrawablesDuration);
        animateDrawablesExitDuration = ta.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateDrawables_exitDuration, animateDrawablesDuration);
        animateDrawablesScale = ta.getFloat(R.styleable.RadioRealButtonGroup_rrbg_animateDrawables_scale, 1.2f);

        animateTextsEnter = ta.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateTexts_enter, 0);
        hasAnimateTexts = ta.hasValue(R.styleable.RadioRealButtonGroup_rrbg_animateTexts_enter);
        animateTextsExit = ta.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateTexts_exit, 0);
        int animateTextsDuration = ta.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateTexts_duration, 500) / 2;
        animateTextsEnterDuration = ta.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateTexts_enterDuration, animateTextsDuration);
        animateTextsExitDuration = ta.getInt(R.styleable.RadioRealButtonGroup_rrbg_animateTexts_exitDuration, animateTextsDuration);
        animateTextsScale = ta.getFloat(R.styleable.RadioRealButtonGroup_rrbg_animateTexts_scale, 1.2f);

        lastPosition = initialPosition = ta.getInt(R.styleable.RadioRealButtonGroup_rrbg_checkedPosition, -1);
        checkedButtonId = ta.getResourceId(R.styleable.RadioRealButtonGroup_rrbg_checkedButton, NO_ID);

        buttonPadding = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_rrbg_buttonsPadding, 0);
        buttonPaddingLeft = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_rrbg_buttonsPaddingLeft, 0);
        buttonPaddingRight = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_rrbg_buttonsPaddingRight, 0);
        buttonPaddingTop = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_rrbg_buttonsPaddingTop, 0);
        buttonPaddingBottom = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_rrbg_buttonsPaddingBottom, 0);

        hasPadding = ta.hasValue(R.styleable.RadioRealButtonGroup_rrbg_buttonsPadding);
        hasPaddingLeft = ta.hasValue(R.styleable.RadioRealButtonGroup_rrbg_buttonsPaddingLeft);
        hasPaddingRight = ta.hasValue(R.styleable.RadioRealButtonGroup_rrbg_buttonsPaddingRight);
        hasPaddingTop = ta.hasValue(R.styleable.RadioRealButtonGroup_rrbg_buttonsPaddingTop);
        hasPaddingBottom = ta.hasValue(R.styleable.RadioRealButtonGroup_rrbg_buttonsPaddingBottom);

        groupBackgroundColor = ta.getColor(R.styleable.RadioRealButtonGroup_rrbg_backgroundColor, Color.WHITE);

        selectorTop = ta.getBoolean(R.styleable.RadioRealButtonGroup_rrbg_selectorTop, false);
        selectorBottom = ta.getBoolean(R.styleable.RadioRealButtonGroup_rrbg_selectorBottom, true);

        borderSize = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_rrbg_borderSize, ConversionHelper.dpToPx(getContext(), 1));
        borderColor = ta.getColor(R.styleable.RadioRealButtonGroup_rrbg_borderColor, Color.BLACK);

        boolean hasBorderSize = ta.hasValue(R.styleable.RadioRealButtonGroup_rrbg_borderSize);
        boolean hasBorderColor = ta.hasValue(R.styleable.RadioRealButtonGroup_rrbg_borderColor);
        hasBorder = hasBorderColor || hasBorderSize;

        clickable = ta.getBoolean(R.styleable.RadioRealButtonGroup_android_clickable, true);
        hasClickable = ta.hasValue(R.styleable.RadioRealButtonGroup_android_clickable);
        enabled = ta.getBoolean(R.styleable.RadioRealButtonGroup_android_enabled, true);
        hasEnabled = ta.hasValue(R.styleable.RadioRealButtonGroup_android_enabled);

        animationType = ta.getInt(R.styleable.RadioRealButtonGroup_rrbg_selectorAnimationType, 0);
        enableDeselection = ta.getBoolean(R.styleable.RadioRealButtonGroup_rrbg_enableDeselection, false);

        hasAnimation = ta.getBoolean(R.styleable.RadioRealButtonGroup_rrbg_animate, true);

        selectorFullSize = ta.getBoolean(R.styleable.RadioRealButtonGroup_rrbg_selectorFullSize, false);

        ta.recycle();
    }

    private void setButtonPadding(RadioRealButton button) {
        if (hasPadding)
            button.setPaddings(buttonPadding, buttonPadding, buttonPadding, buttonPadding);
        else if (hasPaddingBottom || hasPaddingTop || hasPaddingLeft || hasPaddingRight)
            button.setPaddings(buttonPaddingLeft, buttonPaddingTop, buttonPaddingRight, buttonPaddingBottom);
    }

    private int numberOfButtons = 0;

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof RadioRealButton) {
            RadioRealButton button = (RadioRealButton) child;

            int position = buttons.size();

            int id = button.getId();

            if (lastPosition == -1) {
                if (checkedButtonId != NO_ID && checkedButtonId == id)
                    lastPosition = initialPosition = position;
                else if (checkedButtonId == NO_ID && button.isChecked())
                    lastPosition = initialPosition = position;
            }

            if (lastPosition == position) {
                button.setChecked(true);

                if (hasAnimateDrawables)
                    button.bounceDrawable(animateDrawablesScale);
                if (hasAnimateTexts)
                    button.bounceText(animateTextsScale);

                if (button.hasTextColorTo()) {
                    button.setCheckedTextColor(button.getTextColorTo());
                } else if (hasAnimateTextsColor) {
                    button.setCheckedTextColor(animateTextsColorEnter);
                }

                if (button.hasDrawableTintTo()) {
                    button.setCheckedDrawableTint(button.getDrawableTintTo());
                } else if (hasAnimateDrawablesTint) {
                    button.setCheckedDrawableTint(animateDrawablesTintEnter);
                }

            } else {
                button.setChecked(false);

                if (!button.hasTextColor() && hasAnimateTextsColor)
                    button.setTextColor(animateTextsColorExit);

                if (!button.hasDrawableTint() && hasAnimateDrawablesTint)
                    button.setDrawableTint(animateDrawablesTintExit);
            }

            initButtonListener(button, position);
            setButtonPadding(button);
            container.addView(button);
            createSelectorItem(position, button);
            buttons.add(button);

            numberOfButtons = buttons.size();
        } else
            super.addView(child, index, params);
    }

    private void createSelectorItem(int position, RadioRealButton button) {
        BackgroundView view = new BackgroundView(getContext());

        int height = selectorSize;
        if (selectorFullSize)
            height = LayoutParams.MATCH_PARENT;
        view.setLayoutParams(new LinearLayout.LayoutParams(0, height, 1));

        int value = 0;
        if (position == lastPosition) {
            value = 1;
        }

        switch (animationType) {
            case ANIM_SCALE_X:
                view.setScaleX(value);
                break;
            case ANIM_SCALE_Y:
                view.setScaleY(value);
                break;
            case ANIM_TRANSLATE_X:
                if (value == 0)
                    view.setVisibility(INVISIBLE);
                break;
            case ANIM_TRANSLATE_Y:
                view.setTranslationY(value == 1 ? value : selectorSize);
                break;
            case ANIM_ALPHA:
                view.setAlpha(value);
                break;
        }
        button.setOnSelectorColorChangedListener(this, position);
        updateViewSelectorColor(view, button.hasSelectorColor() ? button.getSelectorColor() : selectorColor);

        v_selectors.add(view);
        selectorContainer.addView(view);
    }

    private void initButtonListener(RadioRealButton button, final int position) {
        boolean isClickable = button.isClickable();
        boolean isEnabled = button.isEnabled();

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                makeSelection(position, true, hasAnimation);
            }
        });

        if (hasEnabled || hasClickable) {
            button.setClickable(clickable && enabled);
        } else if (button.hasClickable()) {
            button.setClickable(isClickable);
        } else if (button.hasEnabled()) {
            button.setEnabled(isEnabled);
        }
    }

    public int getNumberOfButtons() {
        return numberOfButtons;
    }

    public void setPosition(int position) {
        makeSelection(position, false, hasAnimation);
    }

    public void setPosition(int position, boolean hasAnimation) {
        makeSelection(position, false, hasAnimation);
    }

    public void deselect() {
        deselect(hasAnimation);
    }

    public void deselect(boolean hasAnimation) {
        if (lastPosition == -1 || !buttons.get(lastPosition).isChecked())
            return;
        makeSelection(lastPosition, false, hasAnimation, true);
    }

    private boolean isInRange(int value) {
        return value >= 0 && value < numberOfButtons;
    }

    private void makeSelection(int position, boolean isToggledByTouch, boolean hasAnimation) {
        makeSelection(position, isToggledByTouch, hasAnimation, enableDeselection);
    }

    private void makeSelection(int position, boolean isToggledByTouch, boolean hasAnimation, boolean enableDeselection) {
        if (!enabled && !clickable)
            return;

        RadioRealButton buttonIn = isInRange(position) ? buttons.get(position) : null;
        RadioRealButton buttonOut = isInRange(lastPosition) ? buttons.get(lastPosition) : null;

        if ((buttonIn == null || !buttonIn.isClickable() || !buttonIn.isEnabled()))
            return;

        moveSelector(position, hasAnimation, enableDeselection);
        animateTextAndDrawable(position, hasAnimation, buttonIn, buttonOut, enableDeselection);

        if (!enableDeselection) {
            buttonIn.setChecked(true);
            if (null != buttonOut)
                buttonOut.setChecked(false);
        } else {
            if (lastPosition == position && buttonIn.isChecked()) {
                buttonIn.setChecked(false);
                position = -1;
            } else
                buttonIn.setChecked(true);
        }

        if (null != onClickedButtonListener && isToggledByTouch)
            onClickedButtonListener.onClickedButton(buttonIn, position);
        if (null != onPositionChangedListener && (lastPosition != position || enableDeselection)) {
            onPositionChangedListener.onPositionChanged(buttonIn, position, lastPosition);
        }

        this.lastPosition = position;
    }

    public int getPosition() {
        return lastPosition;
    }

    /* DRAWABLE AND TEXT ANIMATION BEGINS */
    private void animateTextAndDrawable(int toPosition, boolean hasAnimation, RadioRealButton buttonIn, RadioRealButton buttonOut, boolean enableDeselection) {
        if (lastPosition == toPosition && enableDeselection) {
            if (buttonIn.isChecked())
                animateExit(buttonIn, hasAnimation);
            else
                animateEnter(buttonIn, hasAnimation);
        } else {
            if (null != buttonOut)
                animateExit(buttonOut, hasAnimation);
            if (null != buttonIn)
                animateEnter(buttonIn, hasAnimation);
        }
    }

    private void animateColorTransitions(RadioRealButton button, boolean hasAnimation, boolean onEnter) {
        int textDuration = onEnter ? animateTextsColorDurationEnter : animateTextsColorDurationExit;
        int drawableDuration = onEnter ? animateDrawablesTintDurationEnter : animateDrawablesTintDurationExit;

        button.colorTransitionText(hasAnimateTextsColor, animateTextsColorExit, animateTextsColorEnter, textDuration, hasAnimation, onEnter);
        button.colorTransitionDrawable(hasAnimateDrawablesTint, animateDrawablesTintExit, animateDrawablesTintEnter, drawableDuration, hasAnimation, onEnter);
    }

    private void animateExit(RadioRealButton button, boolean hasAnimation) {
        if (hasAnimateTexts)
            button.bounceText(1, animateTextsExitDuration, interpolatorTextExit, hasAnimation);
        if (hasAnimateDrawables)
            button.bounceDrawable(1, animateDrawablesExitDuration, interpolatorDrawablesExit, hasAnimation);
        animateColorTransitions(button, hasAnimation, false);
    }

    private void animateEnter(RadioRealButton button, boolean hasAnimation) {
        if (hasAnimateTexts)
            button.bounceText(animateTextsScale, animateTextsEnterDuration, interpolatorText, hasAnimation);
        if (hasAnimateDrawables)
            button.bounceDrawable(animateDrawablesScale, animateDrawablesEnterDuration, interpolatorDrawablesEnter, hasAnimation);
        animateColorTransitions(button, hasAnimation, true);
    }
    /* DRAWABLE AND TEXT ANIMATION ENDS */

    public final int ANIM_TRANSLATE_X = 0;
    public final int ANIM_TRANSLATE_Y = 1;
    public final int ANIM_SCALE_X = 2;
    public final int ANIM_SCALE_Y = 3;
    public final int ANIM_ALPHA = 4;

    private void moveSelector(int toPosition, boolean hasAnimation, boolean enableDeselection) {
        if (toPosition == lastPosition && !enableDeselection)
            return;
        String[] properties = {"translationX", "translationY", "scaleX", "scaleY", "alpha", "translationY"};

        if (animationType == ANIM_TRANSLATE_X) {
            animateSelectorSliding(toPosition, properties[animationType], hasAnimation, enableDeselection);
        } else {
            animateSelector(toPosition, properties[animationType], hasAnimation, enableDeselection);
        }
    }

    private void animateSelector(int toPosition, String property, boolean hasAnimation, boolean enableDeselection) {
        int value1 = 0, value2 = 1;
        if (animationType == ANIM_TRANSLATE_Y) {
            value1 = selectorSize;
            value2 = 0;
        }

        if (enableDeselection && toPosition == lastPosition && !buttons.get(toPosition).isChecked()) {
            int temp = value1;
            value1 = value2;
            value2 = temp;
        }

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator to = createAnimator(v_selectors.get(toPosition), property, value2, false, hasAnimation);

        if (lastPosition > -1) {
            ObjectAnimator from = createAnimator(v_selectors.get(lastPosition), property, value1, true, hasAnimation);
            set.playTogether(to, from);
        } else
            set.play(to);

        set.start();
    }

    private void animateSelectorSliding(int toPosition, String property, boolean hasAnimation, boolean enableDeselection) {
        boolean isViewDrawn = buttons.size() > 0 && buttons.get(0).getWidth() > 0;
        if (!isViewDrawn) {
            if (initialPosition != -1)
                v_selectors.get(initialPosition).setVisibility(INVISIBLE);
            v_selectors.get(toPosition).setVisibility(VISIBLE);
            initialPosition = toPosition;
            return;
        }

        if (initialPosition < 0) {
            initialPosition = 0;

            View view = v_selectors.get(initialPosition);
            view.setTranslationX(-buttons.get(initialPosition).getMeasuredWidth());
            view.setVisibility(VISIBLE);
        }

        if (enableDeselection && toPosition == lastPosition && buttons.get(toPosition).isChecked()) {
            toPosition = lastPosition > numberOfButtons / 2 ? numberOfButtons : -1;
        }

        float position = toPosition - initialPosition;

        float value = buttons.get(initialPosition).getMeasuredWidth() * position + dividerSize * position;
        ObjectAnimator animator = createAnimator(v_selectors.get(initialPosition), property, value, false, hasAnimation);
        animator.start();
    }

    private ObjectAnimator createAnimator(View view, String property, float value, boolean hasDelay, boolean hasDuration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, property, value);
        animator.setInterpolator(interpolatorSelector);
        if (hasDuration)
            animator.setDuration(animateSelectorDuration);
        else
            animator.setDuration(0);
        if (hasDelay)
            animator.setStartDelay(animateSelectorDelay);
        return animator;
    }

    private void setRippleState(boolean state) {
        for (RadioRealButton b : buttons) {
            b.setClickable(state);
        }
    }

    private void setState() {
        if (hasEnabled)
            setEnabled(enabled);
        else if (hasClickable)
            setClickable(clickable);
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

    /**
     * LISTENERS
     */
    private OnClickedButtonListener onClickedButtonListener;

    public void setOnClickedButtonListener(OnClickedButtonListener onClickedButtonListener) {
        this.onClickedButtonListener = onClickedButtonListener;
    }

    public interface OnClickedButtonListener {
        void onClickedButton(RadioRealButton button, int position);
    }

    private OnPositionChangedListener onPositionChangedListener;

    public void setOnPositionChangedListener(OnPositionChangedListener onPositionChangedListener) {
        this.onPositionChangedListener = onPositionChangedListener;
    }

    public interface OnPositionChangedListener {
        void onPositionChanged(RadioRealButton button, int currentPosition, int lastPosition);
    }

    public void setOnLongClickedButtonListener(final OnLongClickedButtonListener onLongClickedButtonListener) {
        for (int i = 0; i < numberOfButtons; i++) {
            final int buttonPosition = i;
            buttons.get(i).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onLongClickedButtonListener == null || onLongClickedButtonListener.onLongClickedButton((RadioRealButton) v, buttonPosition);
                }
            });
        }
    }

    public interface OnLongClickedButtonListener {
        boolean onLongClickedButton(RadioRealButton button, int position);

    }

    /**
     * LISTENERS --- ENDS
     */


    public int getAnimateTextsColorExit() {
        return animateTextsColorExit;
    }

    public void setAnimateTextsColorExit(int animateTextsColorExit) {
        this.animateTextsColorExit = animateTextsColorExit;
    }

    public int getAnimateTextsColorEnter() {
        return animateTextsColorEnter;
    }

    public void setAnimateTextsColorEnter(int animateTextsColorEnter) {
        this.animateTextsColorEnter = animateTextsColorEnter;
    }

    public int getAnimateTextsColorDuration() {
        return animateTextsColorDuration;
    }

    public void setAnimateTextsColorDuration(int animateTextsColorDuration) {
        this.animateTextsColorDuration = animateTextsColorDuration;
    }

    public int getAnimateDrawablesTintExit() {
        return animateDrawablesTintExit;
    }

    public void setAnimateDrawablesTintExit(int animateDrawablesTintExit) {
        this.animateDrawablesTintExit = animateDrawablesTintExit;
    }

    public int getAnimateDrawablesTintEnter() {
        return animateDrawablesTintEnter;
    }

    public void setAnimateDrawablesTintEnter(int animateDrawablesTintEnter) {
        this.animateDrawablesTintEnter = animateDrawablesTintEnter;
    }

    public int getAnimateDrawablesTintColorDuration() {
        return animateDrawablesTintDuration;
    }

    public void setAnimateDrawablesTintColorDuration(int animateDrawablesTintColorDuration) {
        this.animateDrawablesTintDuration = animateDrawablesTintColorDuration;
    }

    public List<RadioRealButton> getButtons() {
        return buttons;
    }

    public Interpolator getInterpolatorDrawablesEnter() {
        return interpolatorDrawablesEnter;
    }

    public void setInterpolatorDrawablesEnter(Interpolator interpolatorDrawablesEnter) {
        this.interpolatorDrawablesEnter = interpolatorDrawablesEnter;
    }

    public Interpolator getInterpolatorText() {
        return interpolatorText;
    }

    public void setInterpolatorText(Interpolator interpolatorText) {
        this.interpolatorText = interpolatorText;
    }

    public Interpolator getInterpolatorSelector() {
        return interpolatorSelector;
    }

    public void setInterpolatorSelector(Interpolator interpolatorSelector) {
        this.interpolatorSelector = interpolatorSelector;
    }

    public Interpolator getInterpolatorDrawablesExit() {
        return interpolatorDrawablesExit;
    }

    public void setInterpolatorDrawablesExit(Interpolator interpolatorDrawablesExit) {
        this.interpolatorDrawablesExit = interpolatorDrawablesExit;
    }

    public Interpolator getInterpolatorTextExit() {
        return interpolatorTextExit;
    }

    public void setInterpolatorTextExit(Interpolator interpolatorTextExit) {
        this.interpolatorTextExit = interpolatorTextExit;
    }

    public int getAnimateTextsEnter() {
        return animateTextsEnter;
    }

    public void setAnimateTextsEnter(int animateTextsEnter) {
        this.animateTextsEnter = animateTextsEnter;
    }

    public int getAnimationType() {
        return animationType;
    }

    public void setAnimationType(int animationType) {
        this.animationType = animationType;
    }

    public int getAnimateDrawablesEnterDuration() {
        return animateDrawablesEnterDuration;
    }

    public void setAnimateDrawablesEnterDuration(int animateDrawablesEnterDuration) {
        this.animateDrawablesEnterDuration = animateDrawablesEnterDuration;
    }

    public int getAnimateTextsEnterDuration() {
        return animateTextsEnterDuration;
    }

    public void setAnimateTextsEnterDuration(int animateTextsEnterDuration) {
        this.animateTextsEnterDuration = animateTextsEnterDuration;
    }

    public int getAnimateSelector() {
        return animateSelector;
    }

    public void setAnimateSelector(int animateSelector) {
        this.animateSelector = animateSelector;
    }

    public int getAnimateSelectorDuration() {
        return animateSelectorDuration;
    }

    public void setAnimateSelectorDuration(int animateSelectorDuration) {
        this.animateSelectorDuration = animateSelectorDuration;
    }

    public int getAnimateSelectorDelay() {
        return animateSelectorDelay;
    }

    public void setAnimateSelectorDelay(int animateSelectorDelay) {
        this.animateSelectorDelay = animateSelectorDelay;
    }

    public int getAnimateDrawablesExitDuration() {
        return animateDrawablesExitDuration;
    }

    public void setAnimateDrawablesExitDuration(int animateDrawablesExitDuration) {
        this.animateDrawablesExitDuration = animateDrawablesExitDuration;
    }

    public int getAnimateTextsExit() {
        return animateTextsExit;
    }

    public void setAnimateTextsExit(int animateTextsExit) {
        this.animateTextsExit = animateTextsExit;
    }

    public int getAnimateTextsExitDuration() {
        return animateTextsExitDuration;
    }

    public void setAnimateTextsExitDuration(int animateTextsExitDuration) {
        this.animateTextsExitDuration = animateTextsExitDuration;
    }

    private void setButtonsPadding(int left, int top, int right, int bottom){
        buttonPaddingLeft = left;
        buttonPaddingTop = top;
        buttonPaddingRight = right;
        buttonPaddingBottom = bottom;

        for (RadioRealButton button : buttons) {
            setButtonPadding(button);
        }
    }

    public int getButtonsPadding() {
        return buttonPadding;
    }

    public int getButtonsPaddingLeft() {
        return buttonPaddingLeft;
    }

    public int getButtonsPaddingRight() {
        return buttonPaddingRight;
    }

    public int getButtonsPaddingTop() {
        return buttonPaddingTop;
    }

    public int getButtonsPaddingBottom() {
        return buttonPaddingBottom;
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public void setDividerPadding(int dividerPadding) {
        this.dividerPadding = dividerPadding;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            container.setDividerPadding(dividerPadding);
        }
    }

    public int getDividerSize() {
        return dividerSize;
    }

    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize;
        RoundHelper.makeDividerRound(container, dividerColor, dividerRadius, dividerSize);
    }

    public int getDividerRadius() {
        return dividerRadius;
    }

    public void setDividerRadius(int dividerRadius) {
        this.dividerRadius = dividerRadius;
        RoundHelper.makeDividerRound(container, dividerColor, dividerRadius, dividerSize);
    }

    public int getBottomLineSize() {
        return bottomLineSize;
    }

    public void setBottomLineSize(int bottomLineSize) {
        this.bottomLineSize = bottomLineSize;
        setBottomLineAttrs();
    }

    public int getBottomLineRadius() {
        return bottomLineRadius;
    }

    public void setBottomLineRadius(int bottomLineRadius) {
        this.bottomLineRadius = bottomLineRadius;
        updateViewBottomLine();
    }

    public int getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
        setBorderAttrs();
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        setBorderAttrs();
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        RoundHelper.makeDividerRound(container, dividerColor, dividerRadius, dividerSize);
    }

    public int getBottomLineColor() {
        return bottomLineColor;
    }

    public void setBottomLineColor(int bottomLineColor) {
        this.bottomLineColor = bottomLineColor;
        updateViewBottomLine();
    }

    private void updateViewBottomLine() {
        RoundHelper.makeRound(v_bottomLine, bottomLineColor, bottomLineRadius, bottomLineRadius);
    }

    public int getSelectorColor() {
        return selectorColor;
    }

    private void updateViewSelectorColor(View view, int color) {
        updateViewSelector(view, color, selectorRadius, selectorSize);
    }

    public void setSelectorColor(int selectorColor) {
        this.selectorColor = selectorColor;

        for (View selector : v_selectors) {
            updateViewSelectorColor(selector, selectorColor);
        }
    }

    @Override
    public void onSelectorColorChanged(int position, int selectorColor) {
        updateViewSelectorColor(v_selectors.get(position), selectorColor);
    }

    private void updateViewSelector(View view, int color, int radius, int size) {
        RoundHelper.makeRound(
                view,
                color,
                radius,
                selectorFullSize ? null : size
        );
    }

    private void updateViewSelector(View view) {
        updateViewSelector(view, selectorColor, selectorRadius, selectorSize);
    }

    public int getSelectorSize() {
        return selectorSize;
    }

    public void setSelectorSize(int selectorSize) {
        this.selectorSize = selectorSize;

        selectorContainer.getLayoutParams().height = selectorSize;

        for (View selector : v_selectors) {
            updateViewSelector(selector);

            selector.getLayoutParams().height = selectorSize;
            selector.requestLayout();
        }
    }

    public int getSelectorRadius() {
        return selectorRadius;
    }

    public void setSelectorRadius(int selectorRadius) {
        this.selectorRadius = selectorRadius;

        for (View selector : v_selectors) {
            updateViewSelector(selector);
        }
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        setCornerRadius(radius);
    }

    public float getAnimateDrawablesScale() {
        return animateDrawablesScale;
    }

    public void setAnimateDrawablesScale(float animateDrawablesScale) {
        this.animateDrawablesScale = animateDrawablesScale;
    }

    public float getAnimateTextsScale() {
        return animateTextsScale;
    }

    public void setAnimateTextsScale(float animateTextsScale) {
        this.animateTextsScale = animateTextsScale;
    }

    public boolean isBottomLineOnFront() {
        return bottomLineBringToFront;
    }

    public void setBottomLineToFront(boolean bottomLineBringToFront) {
        this.bottomLineBringToFront = bottomLineBringToFront;
        setBottomLineAttrs();
    }

    public boolean isSelectorOnFront() {
        return selectorBringToFront;
    }

    public void setSelectorToFront(boolean selectorBringToFront) {
        this.selectorBringToFront = selectorBringToFront;
        setSelectorAttrs();
    }

    public boolean isSelectorAboveOfBottomLine() {
        return selectorAboveOfBottomLine;
    }

    public void setSelectorAboveOfBottomLine(boolean selectorAboveOfBottomLine) {
        this.selectorAboveOfBottomLine = selectorAboveOfBottomLine;
        setSelectorAttrs();
    }

    public boolean isSelectorTop() {
        return selectorTop;
    }

    public void setSelectorTop(boolean selectorTop) {
        this.selectorTop = selectorTop;
    }

    public boolean isSelectorBottom() {
        return selectorBottom;
    }

    public void setSelectorBottom(boolean selectorBottom) {
        this.selectorBottom = selectorBottom;
    }

    public boolean hasPadding() {
        return hasPadding;
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

    @Override
    public boolean isClickable() {
        return clickable;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean hasDeselection() {
        return enableDeselection;
    }

    public void setDeselection(boolean deselection) {
        this.enableDeselection = deselection;
    }
}
