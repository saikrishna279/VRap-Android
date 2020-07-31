package com.thanoscorp.athene.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.thanoscorp.athene.interfaces.TextChangeListener;

/**
 *
 *  A Custom EditText implementation with TextChange Listener (even though there is a default implementation smh)
 *
 */

@SuppressLint("AppCompatCustomView")
public class EditTextX extends EditText {

    TextChangeListener listener;
    TextWatcher watcher;

    public EditTextX(Context context) {
        super(context);
    }

    public EditTextX(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextX(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EditTextX(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOnTextChangeListener(final TextChangeListener listener){
        this.listener = listener;
        watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listener.onTextChange(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        this.addTextChangedListener(watcher);
    }

    public void removeAllOnTextChangeListeners(){
        listener=null;
        this.removeTextChangedListener(watcher);
        watcher=null;
    }
}
