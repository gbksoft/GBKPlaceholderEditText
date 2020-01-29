package com.example.gbkplaceholderedittext;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.gbksoft.view.PlaceholderEditText;

public class MainActivity extends AppCompatActivity {

    int ssdsd = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final PlaceholderEditText inputField = findViewById(R.id.edittext_main_number);

        /*AutofillManager mAutofillManager = getSystemService(AutofillManager.class);
        mAutofillManager.requestAutofill(inputField);
        mAutofillManager.commit();
        Log.d("TESTED", "hasEnabledAutofillServices " + mAutofillManager.hasEnabledAutofillServices()
                + " " + "isEnabled " + mAutofillManager.isEnabled());*/

        //final PlaceholderEditText inputField = findViewById(R.id.edittext_main_number);
    }
}
