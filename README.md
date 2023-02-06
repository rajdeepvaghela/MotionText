# MotionText
[![Release](https://jitpack.io/v/com.github.rajdeepvaghela/MotionText.svg)](https://jitpack.io/#com.github.rajdeepvaghela/MotionText)

If you use a simple TextView in MotionLayout for textSize transitions, the animation won't be smooth. Here MotionText will solve the issue, as internally 
it will convert text to Image and during transition only the image is resized. It also have a few out of the box design attributes.

## Installation
Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
    repositories {
    	...
        maven { url 'https://jitpack.io' }
    }
} 
```
Add the dependency
```gradle
dependencies {
    implementation 'com.github.rajdeepvaghela:MotionText:1.0.0'
}
```
## Usage
```xml
    <com.rdapps.motiontext.MotionText
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:paddingHorizontal="5dp"
        android:paddingVertical="2dp"
        app:text="MotionText"
        app:textAllCaps="true"
        app:textColor="@color/textColor"
        app:textSize="14sp" />
```
You can also use these inbuild attributes to design it
```xml
    <com.rdapps.motiontext.MotionText
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:paddingHorizontal="5dp"
        android:paddingVertical="2dp"
        app:backgroundColor="@color/backgroundColor"
        app:text="MotionText"
        app:cornerRadius="4dp"
        app:textColor="@color/textColor"
        app:textSize="12sp" />
```
Or add an icon on the right to it
```xml
    <com.rdapps.motiontext.MotionText
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:paddingHorizontal="5dp"
        android:paddingVertical="2dp"
        app:icon="@drawable/ic_arrow_right"
        app:iconPadding="10dp"
        app:iconTint="#000000"
        app:text="Motion Text"
        app:textAllCaps="true"
        app:textColor="#000000"
        app:textSize="14sp" />
```
To create a pass through text enable this attribute
```xml
app:passThroughText="true"
```
