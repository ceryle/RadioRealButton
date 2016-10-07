[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-RadioRealButton-green.svg?style=true)](https://android-arsenal.com/details/1/4258)
# RadioRealButton

![poster](https://cloud.githubusercontent.com/assets/20969019/18050549/f8399734-6df7-11e6-9a2c-511a22956193.png)

Radio Real Button is a substitute of the Radio Button. Its purpose is to give more elegant view for Android users.

##Preview
![1](https://cloud.githubusercontent.com/assets/20969019/18049713/8c16bcee-6df2-11e6-90c1-e39571abb9fb.gif)
<br />
![2](https://cloud.githubusercontent.com/assets/20969019/18049723/9f86cfee-6df2-11e6-8ef4-9abf09d382d2.gif)
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
    compile 'com.github.ceryle:RadioRealButton:v1.4.1'
}
```

## Customization

### Some Attributes

#### Radio Real Button
| Option Name      				| Format                 | Description                              |
| ---------------- 				| ---------------------- | -----------------------------            |
| custom:rrb_image         | `integer`               |  Button's image icon    |
| custom:rrb_imageTint       | `color`               | Tint for button's image icon |
| custom:rrb_ripple 		| `boolean`		         | Set it true for default ripple  |
| custom:rrb_rippleColor     | `color`               | Ripple color       |
| custom:rrb_backgroundColor        | `color`               | Background color, and it can be used with ripple  |
| custom:rrb_text    		| `string`           | Set button's text  |
| custom:rrb_textColor			| `color`	         | Change button's text color |
| custom:rrb_imageRight			| `boolean`	         | Align image to right |
| custom:rrb_imageTop			| `boolean`	         | Align image to top |
| custom:rrb_imageBottom			| `boolean`	         | Align image to bottom |


#### Radio Real Button Group
| Option Name      				| Format                 | Description                              |
| ---------------- 				| ---------------------- | -----------------------------            |
| custom:rrbg_radius         | `integer`               |  Set radius to make radio real button group rounder    |
| custom:rrbg_animateSelector         | `integer`               |  Set animation on bottom moving view    |
| custom:rrbg_animateImages_enter       | `integer`               | Animate image when button clicked |
| custom:rrbg_animateImages_exit 		| `integer`		         | Animate image when other than selected button clicked  |
| custom:rrbg_animateTexts_enter     | `integer`               | Acts same as image animation       |
| custom:rrbg_animateTexts_exit        | `integer`               | Acts same as image animation  |
| custom:rrbg_bottomLineColor    		| `color`           | Set bottom line color  |
| custom:rrbg_bottomLineSize			| `dimension`	         | Set bottom line height |
| custom:rrbg_selectorColor			| `color`	         | Set color of moving view |
| custom:rrbg_selectorSize			| `dimension`	         | Set height of selecter |
| custom:rrbg_selectorRadius			| `dimension`	         | Set selector radius to make it rounder |
| custom:rrbg_selectorAboveOfBottomLine			| `boolean`	         | Set true if selector is desired above of bottom line |
| custom:rrbg_selectorTop			| `boolean`	         | Align selector to top |
| custom:rrbg_shadow			| `boolean`	         | Set true for shadow |
| custom:rrbg_shadowElevation			| `dimension`	         | Elevation value must be set for shadow |
| custom:rrbg_shadowMargin			| `dimension`	         | Set margin to create space for shadow |
| custom:rrbg_position			| `integer`	         | Set position to select button, starts from 0 |
| custom:rrbg_dividerSize			| `dimension`	         | Set divider size for the line between buttons |
| custom:rrbg_dividerRadius			| `dimension`	         | Set divider radius to make it rounder |

#### Examples

##### In Xml Layout

```xml
  <co.ceryle.radiorealbutton.library.RadioRealButtonGroup
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
            app:rrbg_bottomLineSize="0dp"
            app:rrbg_dividerColor="@color/black"
            app:rrbg_dividerSize="1dp"
            app:rrbg_selectorColor="@color/red_700"
            app:rrbg_selectorSize="6dp"
            app:rrbg_radius="10dp"
            app:rrbg_shadow="true"
            app:rrbg_shadowElevation="2dp"
            app:rrbg_shadowMargin="3dp"
            app:rrbg_animateTexts_enter="overshoot"
            app:rrbg_animateImages_enter="overshoot">
            <co.ceryle.radiorealbutton.library.RadioRealButton
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                app:rrb_image="@mipmap/ic_launcher"
                app:rrb_imageHeight="36dp"
                app:rrb_imageWidth="36dp"
                app:rrb_ripple="true"
                app:rrb_rippleColor="@color/black"
                app:rrb_text="Button 1"
                app:rrb_textColor="@color/black"/>
            <co.ceryle.radiorealbutton.library.RadioRealButton
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                app:rrb_image="@mipmap/ic_launcher"
                app:rrb_imageHeight="36dp"
                app:rrb_imageWidth="36dp"
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
group.setOnClickedButtonPosition(new RadioRealButtonGroup.OnClickedButtonPosition() {
      @Override
      public void onClickedButtonPosition(int position) {
            Toast.makeText(MainActivity.this, "Position: " + position, Toast.LENGTH_SHORT).show();
            button1.getImageView().setImageResource(R.drawable.b1);
            button2.getImageView().setImageResource(R.drawable.b2);
      }
});
```

## License

This project is licensed under the Apache License Version 2.0 - see the [LICENSE.md](LICENSE.md) file for details

