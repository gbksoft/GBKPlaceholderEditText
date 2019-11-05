# GBKPlaceholderEditText

## Installation

Add to the top level gradle file:
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add to the app level gradle:
```groovy
dependencies {
    implementation 'com.github.gbksoft:GBKPlaceholderEditText:v1.0.0'
}
```
## How to use
Simply add this code to the layout file, specify input type, allowed characters and template. Component behaves as usual EditText

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.gbksoft.view.PlaceholderEditText
    android:id="@+id/edittext_main_number"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:autofillHints="phone"
    android:hint="@string/main_placeholder"
    android:importantForAutofill="yes"
    android:inputType="phone"
    app:allowed_chars="1234567890"
    app:template="+38(###)###-##-##"
    tools:targetApi="o" />
```

# Let us know
This scrollable view android adjustment is not our only original decision. Contact us by email [hello@gbksoft.com](hello@gbksoft.com) to find out more about our projects! Share your feedback and tell us about yourself. 