package com.example.android2project;

import android.content.Context;
import android.preference.DialogPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

public class NumberPickerPreference extends DialogPreference {

    private NumberPicker numberPicker;
    private String mText;
    private boolean mSetText;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        return GenerateNumberPicker();
    }

    public NumberPicker GenerateNumberPicker(){
        numberPicker = new NumberPicker(getContext());
        numberPicker.setMinValue(15);
        numberPicker.setMaxValue(60);
        numberPicker.setValue(15);
        return numberPicker;

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if(positiveResult) {
            int value = numberPicker.getValue();
            Log.d("NumberPickerPreference", "NumberPickerValue: " + value);
            persistString(String.valueOf(value));
            if (callChangeListener(value)) {
                setText(String.valueOf(value));
            }
        }
    }
    public void setText(String text) {
        // Always persist/notify the first time.
        final boolean changed = !TextUtils.equals(mText, text);
        if (changed || !mSetText) {
            mText = text;
            mSetText = true;
            persistString(text);
            if (changed) {
                notifyDependencyChange(shouldDisableDependents());
                notifyChanged();
            }
        }
    }
}


