package phc.android.Helpers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * TextLengthWatcher changes focus to the next view when
 * the input for the current edittext has reached the max length.
 */
public class TextLengthWatcher implements TextWatcher {
    /* Max length of the current view. */
    private int mLength;
    /* The next edittext view to switch to. */
    private EditText mNextEditText;

    public TextLengthWatcher(int length, EditText next) {
        mLength = length;
        mNextEditText = next;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() == mLength) {
            mNextEditText.requestFocus();
        }
    }
}
