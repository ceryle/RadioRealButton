[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-RadioRealButton-green.svg?style=true)](https://android-arsenal.com/details/1/4258) [![](https://jitpack.io/v/ceryle/RadioRealButton.svg)](https://jitpack.io/#ceryle/RadioRealButton)
# RadioRealButton

![poster](https://cloud.githubusercontent.com/assets/20969019/18050549/f8399734-6df7-11e6-9a2c-511a22956193.png)

Radio Real Button is a substitute of the Radio Button. Its purpose is to give more elegant view for Android users.

## Preview
![1](https://cloud.githubusercontent.com/assets/20969019/24070628/e081f5f8-0bc9-11e7-9791-9db6d4e25ece.gif)
<br />
![2](https://cloud.githubusercontent.com/assets/20969019/23513066/77a25146-ff6b-11e6-8af1-cbd68bf5aec4.gif)
<br />
![3a](https://cloud.githubusercontent.com/assets/20969019/18049724/9f878100-6df2-11e6-9b3d-e3a01a59727d.gif)
![3b](https://cloud.githubusercontent.com/assets/20969019/18049725/9f887a42-6df2-11e6-8974-413950c61ed5.gif)
<br />
![4](https://cloud.githubusercontent.com/assets/20969019/18049727/9f94a222-6df2-11e6-9271-83a4ad714703.gif)
<br />
![5](https://cloud.githubusercontent.com/assets/20969019/18049726/9f8d4c8e-6df2-11e6-8819-94d82c305667.gif)
<br />
![6](https://cloud.githubusercontent.com/assets/20969019/19188191/c70cc8ec-8c98-11e6-9bae-9571e43f645e.gif)


## Installation

#### Gradle

Add it to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
and:

```gradle
dependencies {
    compile 'com.github.ceryle:RadioRealButton:v2.0.5'
}
```

### What has changed with version 2?
- Border is now part of the round container layout.
- android:padding and rest padding attributes were working not as expected. Now, they are fixed.
- textTypeface attribute now works like button's typeface. You can give your typeface using textTypefacePath attribute.
- Default scaling value is now 1 as it has to be.
##### What added?
- New animations added. There was only a sliding view, but now 4 more added. You can use them with app:selectorAnimationType attribute.
- enableDeselection attribute is added. With this attribute, you can deselect buttons if you re-click on them.
- Each button has their own selector (sliding selector has still one) and you can give divider between them.
- textGravity attribute is added which is actually android:gravity. You can give 3 values which are left, center, right.

##### What removed?
- 'image' word is replaced with "drawable" word from all attributes which have them. Reason is everyone is used to 'drawable' word and
how I have used ImageView in RadioRealButton is no different than regular drawable which is used in buttons expect animations.
- rrbg_shadow, rrbg_shadowElevation and its margin attributes are removed. You can use android:elevation attribute.
- rrbg_enabled is removed. You can use android:enabled attribute.


## Customization

### Some Attributes

#### Radio Real Button
| Option Name      	| Format      | Description                                               |
| ----------------- | ----------- | --------------------------------------------------------- |
| drawable          | `integer`   | set drawable to button                                    |
| drawablePadding   | `dimension` | set padding between text and drawable                     |
| drawableTint      | `color`     | set drawable tint by giving a color code or reference     |
| drawableWidth     | `dimension` | change drawable's width                                   |
| drawableHeight    | `dimension` | change drawable's height                                  |
| drawableGravity   | `integer`   | set drawable position relative to text                    |
| text    		    | `string`    | set button's text                                         |
| textColor			| `color`	  | change button's text color                                |
| textSize		    | `dimension` | change button's text size                                 |
| textTypeface		| `integer`	  | default typefaces offered by android itself               |
| textTypefacePath	| `string`	  | give your typeface by giving its path                     |
| textFillSpace		| `boolean`	  | when enabled, it pushes drawable to edges of the button   |
| textStyle			| `integer`	  | default styles offered by android itself                  |
| textGravity		| `integer`	  | give text gravity(not layout_gravity)                     |
| ripple 		    | `boolean`   | set it true for default ripple                            |
| rippleColor       | `color`     | give any color to achieve colorful ripples                |
| backgroundColor   | `color`     | give background color by giving a color code or reference |
| checked		    | `boolean`	  | its usage is the same as radio button                     |

#### Radio Real Button Group
| Option Name      	        | Format                 | Description                              |
| ------------------------- | ----------- | -----------------------------            |
| radius                    | `integer`   | set radius to make radio real button group rounder    |
| borderSize			    | `dimension` | adds border to group with the given size |
| borderColor			    | `dimension` | changes border color |
| backgroundColor           | `color`     | give background color by giving a color code or reference |
| enableDeselection         | `color`     | enable deselection to un-check a button  |
| dividerSize			    | `dimension` | set divider size for the line between buttons |
| dividerPadding		    | `dimension` | gives padding to divider's top and bottom  |
| dividerColor			    | `color`     | give color code or reference  |
| dividerRadius			    | `dimension` | give dimension to make divider's corners rounder  |
| selectorDividerSize	    | `dimension` | set selector divider size for the line between buttons  |
| selectorDividerPadding    | `dimension` | gives padding to selector divider's top and bottom  |
| selectorDividerColor	    | `color`     | give color code or reference  |
| selectorDividerRadius	    | `dimension` | give dimension to make selector divider's corners rounder  |
| bottomLineColor    	    | `color`     | set bottom line color  |
| bottomLineSize		    | `dimension` | set bottom line height |
| bottomLineBringToFront    | `boolean`   | if it is true, it brings bottomLine on top of selector  |
| bottomLineRadius		    | `dimension` | give dimension to make bottomLine's corners rounder  |
| selectorTop			    | `boolean`	  | align selector to top |
| selectorBottom		    | `boolean`	  | align selector to bottom |
| selectorColor			    | `color`	  | set color of selector |
| selectorSize			    | `dimension` | set height of selector |
| selectorRadius		    | `dimension` | give dimension to make selector's corners rounder |
| selectorBringToFront	    | `boolean`   | if it is true, it brings selector on top of everything  |
| selectorAboveOfBottomLine | `boolean`	  | if it is true, it brings selector above of bottom line |
| selectorFullSize		    | `boolean`	  | selector fills space up to button's height  |
| checkedPosition		    | `integer`	  | check a button by a position number |
| checkedButton			    | `reference` | check a button by button's unique id |
| animate                   | `boolean`   | set animation on bottom moving view    |
| animateSelector           | `integer`   | gives interpolator to selector  |
| animateSelector_delay     | `integer`   | gives delay to selector's animation when it enters  |
| animateSelector_duration  | `integer`   | animation duration of selector in ms  |
| animateDrawables_scale         | `float`     | adjust drawable's size when it is checked |
| animateDrawables_enter         | `integer`   | enter animation on drawable when button is checked |
| animateDrawables_enterDuration | `integer`   | enter animation duration of drawable in ms |
| animateDrawables_exit 	     | `integer`   | exit animation on drawable when other button is checked  |
| animateDrawables_exitDuration  | `integer`   | exit animation duration of drawable in ms  |
| animateTexts_scale             | `float`     | adjust text's size when it is checked |
| animateTexts_enter             | `integer`   | enter animation on text when button is checked |
| animateTexts_enterDuration     | `integer`   | enter animation duration of text in ms |
| animateTexts_exit 	         | `integer`   | exit animation on text when other button is checked  |
| animateTexts_exitDuration 	 | `integer`   | exit animation duration of text in ms  |


#### Examples

##### In Xml Layout

```xml
<co.ceryle.radiorealbutton.library.RadioRealButtonGroup
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:rrbg_animateDrawables_enter="overshoot"
    app:rrbg_animateTexts_enter="overshoot"
    app:rrbg_dividerColor="@color/black"
    app:rrbg_dividerSize="1dp"
    app:rrbg_radius="10dp"
    app:rrbg_selectorColor="@color/red_700"
    app:rrbg_selectorSize="6dp">

    <co.ceryle.radiorealbutton.library.RadioRealButton
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:rrb_drawable="@mipmap/ic_launcher"
        app:rrb_drawableHeight="36dp"
        app:rrb_drawablePadding="8dp"
        app:rrb_drawableWidth="36dp"
        app:rrb_ripple="true"
        app:rrb_rippleColor="@color/black"
        app:rrb_text="Button 1"
        app:rrb_textColor="@color/black"/>

    <co.ceryle.radiorealbutton.library.RadioRealButton
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:rrb_drawable="@mipmap/ic_launcher"
        app:rrb_drawableGravity="right"
        app:rrb_drawableHeight="36dp"
        app:rrb_drawablePadding="8dp"
        app:rrb_drawableWidth="36dp"
        app:rrb_ripple="true"
        app:rrb_rippleColor="@color/black"
        app:rrb_text="Button 2"
        app:rrb_textColor="@color/black"/>
</co.ceryle.radiorealbutton.library.RadioRealButtonGroup>
```

##### Listener Example
```java
final RadioRealButton button1 = (RadioRealButton) findViewById(R.id.button1);
final RadioRealButton button2 = (RadioRealButton) findViewById(R.id.button2);

RadioRealButtonGroup group = (RadioRealButtonGroup) findViewById(R.id.group);

// onClickButton listener detects any click performed on buttons by touch
group.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
    @Override
    public void onClickedButton(RadioRealButton button, int position) {
        Toast.makeText(MainActivity.this, "Clicked! Position: " + position, Toast.LENGTH_SHORT).show();
    }
});

// onPositionChanged listener detects if there is any change in position
group.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
    @Override
    public void onPositionChanged(RadioRealButton button, int position) {
        Toast.makeText(MainActivity.this, "Position Changed! Position: " + position, Toast.LENGTH_SHORT).show();
    }
});

// onLongClickedButton detects long clicks which are made on any button in group.
// return true if you only want to detect long click, nothing else
// return false if you want to detect long click and change position when you release
group.setOnLongClickedButtonListener(new RadioRealButtonGroup.OnLongClickedButtonListener() {
    @Override
    public boolean onLongClickedButton(RadioRealButton button, int position) {
        Toast.makeText(MainActivity.this, "Long Clicked! Position: " + position, Toast.LENGTH_SHORT).show();
        return false;
    }
});
```

## License

This project is licensed under the Apache License Version 2.0 - see the [LICENSE.md](LICENSE.md) file for details

