package com.gbksoft.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import com.gbksoft.R;
import com.gbksoft.listeners.PositionListener;
import com.gbksoft.util.Distance;
import com.gbksoft.util.RawText;
import com.gbksoft.util.Util;

import java.util.Objects;

public class PlaceholderEditText extends AppCompatEditText implements TextWatcher, PositionListener {

    private String template;
    private int[] arrayBasedOnTemplate;
    private int[] resultingTemplateArray;

    private String allowedChars;
    private String deniedChars;

    private boolean beforeEditing;
    private boolean editingOnChanged;
    private boolean afterEditing;
    private boolean ignore;

    private boolean initialized;
    private int selection;
    private boolean selectionChanged;
    private int lastValidPosition;

    private int maxAttributesLength;
    private RawText rawText;

    private boolean longPress;

    public PlaceholderEditText(Context context) {
        super(context);
        init();
    }

    public PlaceholderEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.PlaceholderEditText);
        template = attributes.getString(R.styleable.PlaceholderEditText_template);

        allowedChars = attributes.getString(R.styleable.PlaceholderEditText_allowed_chars);
        deniedChars = attributes.getString(R.styleable.PlaceholderEditText_denied_chars);

        configure();

        attributes.recycle();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Parcelable superParcelable = super.onSaveInstanceState();
        final Bundle state = new Bundle();
        state.putParcelable("super", superParcelable);
        state.putString("text", getRawText());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(((Bundle) state).getParcelable("super"));
        final String text = bundle.getString("text");
        setText(text);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        // On Android 4+ this method is being called more than 1 time if there is a hint in the EditText, what moves the cursor to left
        // Using the boolean var selectionChanged to limit to one execution
        if(initialized ){
            if(!selectionChanged) {
                selStart = Util.fixInitialCharacters(selStart, this);
                selEnd = Util.fixInitialCharacters(selEnd, this);

                // exactly in this order. If getText.length() == 0 then selStart will be -1
                if (selStart > Objects.requireNonNull(getText()).length()) {
                    selStart = getText().length();
                }
                if (selStart < 0) {
                    selStart = 0;
                }

                // exactly in this order. If getText.length() == 0 then selEnd will be -1
                if (selEnd > getText().length()) {
                    selEnd = getText().length();
                }
                if (selEnd < 0) {
                    selEnd = 0;
                }

                setSelection(selStart, selEnd);
                selectionChanged = true;
            } else{
                //check to see if the current selection is outside the already entered text
                if(selStart > rawText.length() - 1){
                    final int start = Util.fixInitialCharacters(selStart, this);
                    final int end = Util.fixInitialCharacters(selEnd, this);
                    if (start > 0 && end < Objects.requireNonNull(getText()).length()){
                        setSelection(start, end);
                    }
                }
            }
        }
        super.onSelectionChanged(selStart, selEnd);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if(!beforeEditing) {
            beforeEditing = true;
            if(start > lastValidPosition) {
                ignore = true;
            }
            int rangeStart = start;
            if(after == 0) {
                rangeStart = Util.blockFirstSymbols(start, arrayBasedOnTemplate);
            }
            Distance distance = Util.calculateDistance(rangeStart, start + count,
                    template, arrayBasedOnTemplate, rawText.length(), this);
            if(distance.getStart() != -1) {
                rawText.subtractFromString(distance);
            }
            if(count > 0) {
                selection = previousValidPosition(start);
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!editingOnChanged && beforeEditing) {
            editingOnChanged = true;
            if(ignore) {
                return;
            }
            if(count > 0) {
                int startingPosition = arrayBasedOnTemplate[nextValidPosition(start)];
                String addedString = s.subSequence(start, start + count).toString();
                count = rawText.addToString(Util.clear(addedString, allowedChars, deniedChars), startingPosition, maxAttributesLength);
                if(initialized) {
                    int currentPosition;
                    if(startingPosition + count < resultingTemplateArray.length)
                        currentPosition = resultingTemplateArray[startingPosition + count];
                    else
                        currentPosition = lastValidPosition + 1;
                    selection = nextValidPosition(currentPosition);
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(!afterEditing && beforeEditing && editingOnChanged) {
            afterEditing = true;
            if (hasHint() && (rawText.length() == 0)) {
                setText(placeholderWithHint());
            } else {
                setText(placeholderWithoutHint());
            }

            selectionChanged = false;
            setSelection(selection);

            if (longPress && (s.length() == 0)) {
                longPress = false;
                setText(placeholderWithoutHint());
            }

            beforeEditing = false;
            editingOnChanged = false;
            afterEditing = false;
            ignore = false;
        }
    }

    private void init() {
        setTextIsSelectable(true);
        addTextChangedListener(this);
    }

    private void configure() {
        initialized = false;

        generatePositionsFromPresentedTemplate();

        rawText = new RawText();
        selection = resultingTemplateArray[0];

        beforeEditing = true;
        editingOnChanged = true;
        afterEditing = true;

        if(hasHint() && rawText.length() == 0) {
            this.setText(placeholderWithHint());
        } else {
            this.setText(placeholderWithoutHint());
        }

        beforeEditing = false;
        editingOnChanged = false;
        afterEditing = false;

        maxAttributesLength = arrayBasedOnTemplate[previousValidPosition(template.length() - 1)] + 1;
        lastValidPosition = findLastValidPosition();
        initialized = true;

        super.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus()) {
                    selectionChanged = false;
                    PlaceholderEditText.this.setSelection(lastValidPosition());
                }
            }
        });
        super.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longPress = true;
                if (getRawText().length() == 0) {
                    setText("");
                }
                return false;
            }
        });
    }

    private void generatePositionsFromPresentedTemplate() {
        int[] arrayOfTemplate = new int[template.length()];
        arrayBasedOnTemplate = new int[template.length()];

        char presetTemplate = '#';

        int index = 0;
        for(int i = 0; i < template.length(); i++) {
            char currentChar = template.charAt(i);
            if(currentChar == presetTemplate) {
                // the current character is equal to the given template
                arrayOfTemplate[index] = i;
                arrayBasedOnTemplate[i] = index++;
            } else {
                arrayBasedOnTemplate[i] = -1;
            }
        }

        resultingTemplateArray = new int[index];
        System.arraycopy(arrayOfTemplate, 0, resultingTemplateArray, 0, index);
    }

    @Override
    public int previousValidPosition(int currentPosition) {
        while(currentPosition >= 0 && arrayBasedOnTemplate[currentPosition] == -1) {
            currentPosition--;
            if(currentPosition < 0) {
                return nextValidPosition(0);
            }
        }
        return currentPosition;
    }

    @Override
    public int nextValidPosition(int currentPosition) {
        while(currentPosition < lastValidPosition && arrayBasedOnTemplate[currentPosition] == -1) {
            currentPosition++;
        }
        if(currentPosition > lastValidPosition) return lastValidPosition + 1;
        return currentPosition;
    }

    @Override
    public int lastValidPosition() {
        if(rawText.length() == maxAttributesLength) {
            return resultingTemplateArray[rawText.length() - 1] + 1;
        }
        return nextValidPosition(resultingTemplateArray[rawText.length()]);
    }

    @Override
    public int findLastValidPosition() {
        for(int i = arrayBasedOnTemplate.length - 1; i >= 0; i--) {
            if(arrayBasedOnTemplate[i] != -1) return i;
        }
        throw new RuntimeException("Template must contain at least one preset char");
    }

    private String placeholderWithoutHint() {
        int textLength;
        if (rawText.length() < resultingTemplateArray.length) {
            textLength = resultingTemplateArray[rawText.length()];
        } else {
            textLength = template.length();
        }
        char[] templateText = new char[textLength];
        for (int i = 0; i < templateText.length; i++) {
            int rawIndex = arrayBasedOnTemplate[i];
            if (rawIndex == -1) {
                templateText[i] = template.charAt(i);
            } else {
                templateText[i] = rawText.charAt(rawIndex);
            }
        }
        return new String(templateText);
    }

    private CharSequence placeholderWithHint() {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        int mtrv;
        int templateFirstChunkEnd = resultingTemplateArray[0];
        int templateLength = template.length();
        int hintLength = getHint().length();
        for(int i = 0; i < templateLength; i++) {
            mtrv = arrayBasedOnTemplate[i];
            if (mtrv != -1) {
                if (mtrv < rawText.length()) {
                    ssb.append(rawText.charAt(mtrv));
                } else {
                    ssb.append(getHint().charAt(arrayBasedOnTemplate[i] % hintLength));
                }
            } else {
                ssb.append(template.charAt(i));
            }
            if ((rawText.length() < resultingTemplateArray.length && i >= resultingTemplateArray[rawText.length()]) || (i >= templateFirstChunkEnd)) {
                ssb.setSpan(new ForegroundColorSpan(getCurrentHintTextColor()), i, i + 1, 0);
            }
        }
        return ssb;
    }

    private boolean hasHint() {
        return getHint() != null;
    }

    private String getRawText() {
        return this.rawText.getText();
    }

    public void setTemplate(String template) {
        this.template = template;
        configure();
    }

    public String getTemplate() {
        return this.template;
    }

}
