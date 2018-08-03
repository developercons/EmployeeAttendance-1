package com.codeian.employeeattendance.Helpers;

import android.content.Context;
import android.preference.EditTextPreference;
import android.text.TextUtils;
import android.util.AttributeSet;

public class EncryptedEditTextPreference extends EditTextPreference {
    public EncryptedEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EncryptedEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EncryptedEditTextPreference(Context context) {
        super(context);
    }

    @Override
    public String getText() {
        String value = "";
        return value;
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        super.setText(restoreValue ? getPersistedString(null) : (String) defaultValue);
    }

    @Override
    public void setText(String text) {
        if (TextUtils.isEmpty(text)) {
            super.setText(null);
            return;
        }
        super.setText(Hash.genHash(text));
    }
}
