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

import co.ceryle.radiorealbutton.R;

public class RadioRealButtonGroup extends RoundedCornerLayout {

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

    private boolean isRedrawn = false;

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            state = bundle.getParcelable("state");
            int position = bundle.getInt("position");

            if (lastPosition != position && initialPosition != 0) {
                if (animationType == ANIM_TRANSLATE_X) {
                    isRedrawn = true;
                    v_selectors.get(initialPosition).setVisibility(INVISIBLE);
                    initialPosition = position;
                    v_selectors.get(initialPosition).setVisibility(VISIBLE);
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
        setGroupBackgroundColor();

        initInterpolations();

        setState();
    }

    private void initViews() {
        setCornerRadius(radius);

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

        selectorContainer = new LinearLayout(getContext());
        selectorContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        selectorContainer.setOrientation(LinearLayout.HORIZONTAL);
        selectorContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        RoundHelper.makeDividerRound(selectorContainer, selectorDividerColor, selectorDividerRadius, selectorDividerSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            selectorContainer.setDividerPadding(selectorDividerPadding);
        }
        addView(selectorContainer);
    }

    private void setBottomLineAttrs() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) v_bottomLine.getLayoutParams();

        layoutParams.height = bottomLineSize;

        if (bottomLineBringToFront)
            v_bottomLine.bringToFront();

        RoundHelper.makeRound(v_bottomLine, bottomLineColor, bottomLineRadius, bottomLineRadius);
    }

    private void setSelectorAttrs() {
        View selectorView = selectorContainer;

        FrameLayout.LayoutParams selectorParams = (FrameLayout.LayoutParams) selectorView.getLayoutParams();
        FrameLayout.LayoutParams bottomLineParams = (FrameLayout.LayoutParams) v_bottomLine.getLayoutParams();

        if (selectorBringToFront)
            selectorView.bringToFront();

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

    private Interpolator interpolatorImage, interpolatorText, interpolatorSelector;
    private Interpolator interpolatorImageExit, interpolatorTextExit;

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
            interpolatorText = (Interpolator) interpolations[animateTexts].newInstance();
            interpolatorImage = (Interpolator) interpolations[animateImages].newInstance();
            interpolatorSelector = (Interpolator) interpolations[animateSelector].newInstance();

            interpolatorTextExit = (Interpolator) interpolations[animateTextsExit].newInstance();
            interpolatorImageExit = (Interpolator) interpolations[animateImagesExit].newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setGroupBackgroundColor() {
        container.setBackgroundColor(groupBackgroundColor);
        // container.setBackgroundColor(hasDividerBackgroundColor ? dividerBackgroundColor : groupBackgroundColor);
    }

    private void setBorderAttrs() {
        if (!hasBorder)
            return;

        setStroke(true);
        setStrokeColor(borderColor);
        setStrokeSize(borderSize);
    }

    private int borderSize, borderColor, dividerColor, bottomLineColor, selectorColor, animateImages, animateTexts, animationType,
            animateImagesDuration, animateTextsDuration, animateSelector, animateSelectorDuration, animateSelectorDelay, animateImagesExit,
            animateImagesExitDuration, animateTextsExit, animateTextsExitDuration, lastPosition, buttonPadding,
            buttonPaddingLeft, buttonPaddingRight, buttonPaddingTop, buttonPaddingBottom, groupBackgroundColor, dividerBackgroundColor,
            dividerPadding, dividerSize, dividerRadius, bottomLineSize, bottomLineRadius, selectorSize, selectorRadius, initialPosition,
            selectorDividerSize, selectorDividerRadius, selectorDividerColor, selectorDividerPadding, checkedButtonId;

    private float radius, animateImagesScale, animateTextsScale;

    private boolean bottomLineBringToFront, selectorBringToFront, selectorAboveOfBottomLine, selectorTop, selectorBottom,
            hasPadding, hasPaddingLeft, hasPaddingRight, hasPaddingTop, hasPaddingBottom, hasDividerBackgroundColor, clickable,
            enabled, enableDeselection, hasEnabled, hasClickable, hasBorder, hasAnimateImages, hasAnimateTexts, hasAnimation;

    private void getAttributes(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.RadioRealButtonGroup);

        bottomLineColor = ta.getColor(R.styleable.RadioRealButtonGroup_bottomLineColor, Color.GRAY);
        bottomLineSize = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_bottomLineSize, 6);
        bottomLineBringToFront = ta.getBoolean(R.styleable.RadioRealButtonGroup_bottomLineBringToFront, false);
        bottomLineRadius = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_bottomLineRadius, 0);

        selectorColor = ta.getColor(R.styleable.RadioRealButtonGroup_selectorColor, Color.GRAY);
        selectorBringToFront = ta.getBoolean(R.styleable.RadioRealButtonGroup_selectorBringToFront, false);
        selectorAboveOfBottomLine = ta.getBoolean(R.styleable.RadioRealButtonGroup_selectorAboveOfBottomLine, false);
        selectorSize = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_selectorSize, 12);
        selectorRadius = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_selectorRadius, 0);

        animateSelector = ta.getInt(R.styleable.RadioRealButtonGroup_animateSelector, 0);
        animateSelectorDuration = ta.getInt(R.styleable.RadioRealButtonGroup_animateSelectorDuration, 500);
        animateSelectorDelay = ta.getInt(R.styleable.RadioRealButtonGroup_animateSelectorDelay, 0);

        dividerSize = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_dividerSize, 0);
        boolean hasDividerSize = ta.hasValue(R.styleable.RadioRealButtonGroup_dividerSize);
        dividerRadius = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_dividerRadius, 0);
        dividerPadding = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_dividerPadding, 30);
        dividerColor = ta.getColor(R.styleable.RadioRealButtonGroup_dividerColor, Color.TRANSPARENT);
        dividerBackgroundColor = ta.getColor(R.styleable.RadioRealButtonGroup_dividerBackgroundColor, Color.WHITE);
        hasDividerBackgroundColor = ta.hasValue(R.styleable.RadioRealButtonGroup_dividerBackgroundColor);

        selectorDividerSize = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_selectorDividerSize, dividerSize);
        if (!hasDividerSize) {
            dividerSize = selectorDividerSize;
        }
        selectorDividerRadius = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_selectorDividerRadius, 0);
        selectorDividerPadding = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_selectorDividerPadding, 0);
        selectorDividerColor = ta.getColor(R.styleable.RadioRealButtonGroup_selectorDividerColor, Color.TRANSPARENT);

        radius = ta.getDimension(R.styleable.RadioRealButtonGroup_radius, 0);

        animateImages = ta.getInt(R.styleable.RadioRealButtonGroup_animateImages_enter, 0);
        hasAnimateImages = ta.hasValue(R.styleable.RadioRealButtonGroup_animateImages_enter);
        animateImagesExit = ta.getInt(R.styleable.RadioRealButtonGroup_animateImages_exit, 0);
        animateImagesDuration = ta.getInt(R.styleable.RadioRealButtonGroup_animateImages_enterDuration, 500);
        animateImagesExitDuration = ta.getInt(R.styleable.RadioRealButtonGroup_animateImages_exitDuration, 100);
        animateImagesScale = ta.getFloat(R.styleable.RadioRealButtonGroup_animateImagesScale, 0.2f);

        animateTexts = ta.getInt(R.styleable.RadioRealButtonGroup_animateTexts_enter, 0);
        hasAnimateTexts = ta.hasValue(R.styleable.RadioRealButtonGroup_animateTexts_enter);
        animateTextsExit = ta.getInt(R.styleable.RadioRealButtonGroup_animateTexts_exit, 0);
        animateTextsDuration = ta.getInt(R.styleable.RadioRealButtonGroup_animateTexts_enterDuration, 500);
        animateTextsExitDuration = ta.getInt(R.styleable.RadioRealButtonGroup_animateTexts_exitDuration, 100);
        animateTextsScale = ta.getFloat(R.styleable.RadioRealButtonGroup_animateTextsScale, 0.2f);

        lastPosition = ta.getInt(R.styleable.RadioRealButtonGroup_checkedPosition, -1);
        checkedButtonId = ta.getResourceId(R.styleable.RadioRealButtonGroup_checkedButton, NO_ID);
        initialPosition = lastPosition;

        buttonPadding = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_buttonsPadding, 0);
        buttonPaddingLeft = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_buttonsPaddingLeft, 0);
        buttonPaddingRight = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_buttonsPaddingRight, 0);
        buttonPaddingTop = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_buttonsPaddingTop, 0);
        buttonPaddingBottom = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_buttonsPaddingBottom, 0);

        hasPadding = ta.hasValue(R.styleable.RadioRealButtonGroup_buttonsPadding);
        hasPaddingLeft = ta.hasValue(R.styleable.RadioRealButtonGroup_buttonsPaddingLeft);
        hasPaddingRight = ta.hasValue(R.styleable.RadioRealButtonGroup_buttonsPaddingRight);
        hasPaddingTop = ta.hasValue(R.styleable.RadioRealButtonGroup_buttonsPaddingTop);
        hasPaddingBottom = ta.hasValue(R.styleable.RadioRealButtonGroup_buttonsPaddingBottom);

        groupBackgroundColor = ta.getColor(R.styleable.RadioRealButtonGroup_backgroundColor, Color.WHITE);

        selectorTop = ta.getBoolean(R.styleable.RadioRealButtonGroup_selectorTop, false);
        selectorBottom = ta.getBoolean(R.styleable.RadioRealButtonGroup_selectorBottom, true);

        borderSize = ta.getDimensionPixelSize(R.styleable.RadioRealButtonGroup_borderSize, ConversionHelper.dpToPx(getContext(), 1));
        borderColor = ta.getColor(R.styleable.RadioRealButtonGroup_borderColor, Color.BLACK);

        boolean hasBorderSize = ta.hasValue(R.styleable.RadioRealButtonGroup_borderSize);
        boolean hasBorderColor = ta.hasValue(R.styleable.RadioRealButtonGroup_borderColor);
        hasBorder = hasBorderColor || hasBorderSize;

        clickable = ta.getBoolean(R.styleable.RadioRealButtonGroup_android_clickable, true);
        hasClickable = ta.hasValue(R.styleable.RadioRealButtonGroup_android_clickable);
        enabled = ta.getBoolean(R.styleable.RadioRealButtonGroup_android_enabled, true);
        hasEnabled = ta.hasValue(R.styleable.RadioRealButtonGroup_android_enabled);

        animationType = ta.getInt(R.styleable.RadioRealButtonGroup_selectorAnimationType, 0);
        enableDeselection = ta.getBoolean(R.styleable.RadioRealButtonGroup_enableDeselection, false);

        hasAnimation = ta.getBoolean(R.styleable.RadioRealButtonGroup_animate, true);

        ta.recycle();
    }

    private void setButtonPadding(RadioRealButton button) {
        if (hasPadding)
            button.setPadding(buttonPadding, buttonPadding, buttonPadding, buttonPadding);
        else if (hasPaddingBottom || hasPaddingTop || hasPaddingLeft || hasPaddingRight)
            button.setPadding(buttonPaddingLeft, buttonPaddingTop, buttonPaddingRight, buttonPaddingBottom);
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
                    lastPosition = position;
                else if (checkedButtonId == NO_ID && button.isChecked())
                    lastPosition = position;
            }

            if (lastPosition == position) {
                button.setChecked(true);
                button.bounceDrawable(animateImagesScale + 1);
                button.bounceText(animateTextsScale + 1);
            } else
                button.setChecked(false);

            initButtonListener(button, position);
            setButtonPadding(button);
            container.addView(button);
            createSelectorItem(position);
            buttons.add(button);

            numberOfButtons = buttons.size();
        } else
            super.addView(child, index, params);
    }

    private void createSelectorItem(int position) {
        View view = new View(getContext());
        view.setLayoutParams(new LinearLayout.LayoutParams(0, selectorSize, 1));
        view.setBackgroundColor(selectorColor);

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

        RoundHelper.makeRound(view, selectorColor, selectorRadius, selectorSize);
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        container.getLayoutParams().height = getLayoutParams().height;
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

    private void deselect(int position, boolean enableDeselection) {
        makeSelection(position, false, hasAnimation, enableDeselection);
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

        if (null != onClickedButtonListener && isToggledByTouch)
            onClickedButtonListener.onClickedButton(buttonIn, position);
        if (null != onPositionChangedListener) {
            onPositionChangedListener.onPositionChanged(buttonIn, position);
        }

        moveSelector(position, hasAnimation, enableDeselection);
        animateTextAndImage(position, hasAnimation, buttonIn, buttonOut, enableDeselection);

        if (!enableDeselection) {
            buttonIn.setChecked(true);
            if (null != buttonOut)
                buttonOut.setChecked(false);
        } else {
            if (lastPosition == position && buttonIn.isChecked())
                buttonIn.setChecked(false);
            else
                buttonIn.setChecked(true);
        }
        this.lastPosition = position;
    }

    public int getPosition() {
        return lastPosition;
    }

    /* DRAWABLE AND TEXT ANIMATION BEGINS */
    private void animateTextAndImage(int toPosition, boolean hasAnimation, RadioRealButton buttonIn, RadioRealButton buttonOut, boolean enableDeselection) {
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

    private void animateExit(RadioRealButton button, boolean hasAnimation) {
        if (hasAnimateTexts)
            button.bounceText(1, animateTextsExitDuration, interpolatorTextExit, hasAnimation);
        if (hasAnimateImages)
            button.bounceDrawable(1, animateImagesExitDuration, interpolatorImageExit, hasAnimation);
    }

    private void animateEnter(RadioRealButton button, boolean hasAnimation) {
        if (hasAnimateTexts)
            button.bounceText(1 + animateTextsScale, animateTextsDuration, interpolatorText, hasAnimation);
        if (hasAnimateImages)
            button.bounceDrawable(1 + animateImagesScale, animateImagesDuration, interpolatorImage, hasAnimation);
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
        if (isRedrawn) {
            isRedrawn = false;
            return;
        }

        if (initialPosition < 0) {
            initialPosition = 0;

            View view = v_selectors.get(initialPosition);
            view.setTranslationX(-buttons.get(initialPosition).getWidth());
            view.setVisibility(VISIBLE);
        }

        if (enableDeselection && toPosition == lastPosition && buttons.get(toPosition).isChecked()) {
            toPosition = lastPosition > numberOfButtons / 2 ? numberOfButtons : -1;
        }

        float position = toPosition - initialPosition;

        float value = buttons.get(initialPosition).getWidth() * position + dividerSize * position;
        ObjectAnimator animator = createAnimator(v_selectors.get(initialPosition), property, value, false, hasAnimation);
        animator.start();
    }

    public void deselect() {
        if (animationType == ANIM_TRANSLATE_X) {
            deselect(lastPosition, true);
        } else {
            if (lastPosition != -1 && buttons.get(lastPosition).isChecked()) {
                setPosition(lastPosition);
            }
        }
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
        void onPositionChanged(RadioRealButton button, int position);
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


    /**
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     */

    public List<RadioRealButton> getButtons() {
        return buttons;
    }

    public Interpolator getInterpolatorImage() {
        return interpolatorImage;
    }

    public void setInterpolatorImage(Interpolator interpolatorImage) {
        this.interpolatorImage = interpolatorImage;
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

    public Interpolator getInterpolatorImageExit() {
        return interpolatorImageExit;
    }

    public void setInterpolatorImageExit(Interpolator interpolatorImageExit) {
        this.interpolatorImageExit = interpolatorImageExit;
    }

    public Interpolator getInterpolatorTextExit() {
        return interpolatorTextExit;
    }

    public void setInterpolatorTextExit(Interpolator interpolatorTextExit) {
        this.interpolatorTextExit = interpolatorTextExit;
    }

    public int getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
    }

    public int getBottomLineColor() {
        return bottomLineColor;
    }

    public void setBottomLineColor(int bottomLineColor) {
        this.bottomLineColor = bottomLineColor;
    }

    public int getSelectorColor() {
        return selectorColor;
    }

    public void setSelectorColor(int selectorColor) {
        this.selectorColor = selectorColor;
    }

    public int getAnimateImages() {
        return animateImages;
    }

    public void setAnimateImages(int animateImages) {
        this.animateImages = animateImages;
    }

    public int getAnimateTexts() {
        return animateTexts;
    }

    public void setAnimateTexts(int animateTexts) {
        this.animateTexts = animateTexts;
    }

    public int getAnimationType() {
        return animationType;
    }

    public void setAnimationType(int animationType) {
        this.animationType = animationType;
    }

    public int getAnimateImagesDuration() {
        return animateImagesDuration;
    }

    public void setAnimateImagesDuration(int animateImagesDuration) {
        this.animateImagesDuration = animateImagesDuration;
    }

    public int getAnimateTextsDuration() {
        return animateTextsDuration;
    }

    public void setAnimateTextsDuration(int animateTextsDuration) {
        this.animateTextsDuration = animateTextsDuration;
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

    public int getAnimateImagesExit() {
        return animateImagesExit;
    }

    public void setAnimateImagesExit(int animateImagesExit) {
        this.animateImagesExit = animateImagesExit;
    }

    public int getAnimateImagesExitDuration() {
        return animateImagesExitDuration;
    }

    public void setAnimateImagesExitDuration(int animateImagesExitDuration) {
        this.animateImagesExitDuration = animateImagesExitDuration;
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

    public int getButtonPadding() {
        return buttonPadding;
    }

    public void setButtonPadding(int buttonPadding) {
        this.buttonPadding = buttonPadding;
    }

    public int getButtonPaddingLeft() {
        return buttonPaddingLeft;
    }

    public void setButtonPaddingLeft(int buttonPaddingLeft) {
        this.buttonPaddingLeft = buttonPaddingLeft;
    }

    public int getButtonPaddingRight() {
        return buttonPaddingRight;
    }

    public void setButtonPaddingRight(int buttonPaddingRight) {
        this.buttonPaddingRight = buttonPaddingRight;
    }

    public int getButtonPaddingTop() {
        return buttonPaddingTop;
    }

    public void setButtonPaddingTop(int buttonPaddingTop) {
        this.buttonPaddingTop = buttonPaddingTop;
    }

    public int getButtonPaddingBottom() {
        return buttonPaddingBottom;
    }

    public void setButtonPaddingBottom(int buttonPaddingBottom) {
        this.buttonPaddingBottom = buttonPaddingBottom;
    }

    public int getDividerBackgroundColor() {
        return dividerBackgroundColor;
    }

    public void setDividerBackgroundColor(int dividerBackgroundColor) {
        this.dividerBackgroundColor = dividerBackgroundColor;
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public void setDividerPadding(int dividerPadding) {
        this.dividerPadding = dividerPadding;
    }

    public int getDividerSize() {
        return dividerSize;
    }

    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize;
    }

    public int getDividerRadius() {
        return dividerRadius;
    }

    public void setDividerRadius(int dividerRadius) {
        this.dividerRadius = dividerRadius;
    }

    public int getBottomLineSize() {
        return bottomLineSize;
    }

    public void setBottomLineSize(int bottomLineSize) {
        this.bottomLineSize = bottomLineSize;
    }

    public int getBottomLineRadius() {
        return bottomLineRadius;
    }

    public void setBottomLineRadius(int bottomLineRadius) {
        this.bottomLineRadius = bottomLineRadius;
    }

    public int getSelectorSize() {
        return selectorSize;
    }

    public void setSelectorSize(int selectorSize) {
        this.selectorSize = selectorSize;
    }

    public int getSelectorRadius() {
        return selectorRadius;
    }

    public void setSelectorRadius(int selectorRadius) {
        this.selectorRadius = selectorRadius;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getAnimateImagesScale() {
        return animateImagesScale;
    }

    public void setAnimateImagesScale(float animateImagesScale) {
        this.animateImagesScale = animateImagesScale;
    }

    public float getAnimateTextsScale() {
        return animateTextsScale;
    }

    public void setAnimateTextsScale(float animateTextsScale) {
        this.animateTextsScale = animateTextsScale;
    }

    public boolean isBottomLineBringToFront() {
        return bottomLineBringToFront;
    }

    public void setBottomLineBringToFront(boolean bottomLineBringToFront) {
        this.bottomLineBringToFront = bottomLineBringToFront;
    }

    public boolean isSelectorBringToFront() {
        return selectorBringToFront;
    }

    public void setSelectorBringToFront(boolean selectorBringToFront) {
        this.selectorBringToFront = selectorBringToFront;
    }

    public boolean isSelectorAboveOfBottomLine() {
        return selectorAboveOfBottomLine;
    }

    public void setSelectorAboveOfBottomLine(boolean selectorAboveOfBottomLine) {
        this.selectorAboveOfBottomLine = selectorAboveOfBottomLine;
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

    public boolean isHasPadding() {
        return hasPadding;
    }

    public void setHasPadding(boolean hasPadding) {
        this.hasPadding = hasPadding;
    }

    public boolean isHasPaddingLeft() {
        return hasPaddingLeft;
    }

    public void setHasPaddingLeft(boolean hasPaddingLeft) {
        this.hasPaddingLeft = hasPaddingLeft;
    }

    public boolean isHasPaddingRight() {
        return hasPaddingRight;
    }

    public void setHasPaddingRight(boolean hasPaddingRight) {
        this.hasPaddingRight = hasPaddingRight;
    }

    public boolean isHasPaddingTop() {
        return hasPaddingTop;
    }

    public void setHasPaddingTop(boolean hasPaddingTop) {
        this.hasPaddingTop = hasPaddingTop;
    }

    public boolean isHasPaddingBottom() {
        return hasPaddingBottom;
    }

    public void setHasPaddingBottom(boolean hasPaddingBottom) {
        this.hasPaddingBottom = hasPaddingBottom;
    }

    public boolean isHasDividerBackgroundColor() {
        return hasDividerBackgroundColor;
    }

    public void setHasDividerBackgroundColor(boolean hasDividerBackgroundColor) {
        this.hasDividerBackgroundColor = hasDividerBackgroundColor;
    }

    @Override
    public boolean isClickable() {
        return clickable;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isEnableDeselection() {
        return enableDeselection;
    }

    public void setEnableDeselection(boolean enableDeselection) {
        this.enableDeselection = enableDeselection;
    }
}
