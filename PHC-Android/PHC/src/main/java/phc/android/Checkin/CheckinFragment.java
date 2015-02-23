package phc.android.Checkin;

import android.app.Fragment;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * A class that represents Fragments in the Registration Activity.
 * Currently it just hides the keyboard when switching between fragments.
 * Created by howardchen on 11/15/14.
 */
public class CheckinFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        hideKeyboard();
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard();
    }

    /**
     * Lowers the keyboard.
     */
    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
    }
}
