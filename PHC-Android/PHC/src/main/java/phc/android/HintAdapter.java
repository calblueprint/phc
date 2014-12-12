package phc.android;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Creates a simple variation of an ArrayAdapter which supports hints.
 *
 * Thanks @Boni2k http://stackoverflow.com/questions/6602339/android-spinner-hint
 */
public class HintAdapter extends ArrayAdapter<String> {


    /**
     * Adds items into the ArrayAdapter for you upon construction.
     */
    public HintAdapter(Context context, int textViewResourceId, String[] items) {
        super(context, textViewResourceId);
        for (String item : items) {
            this.add(item);
        }
    }

    /**
     * If we're looking at the last element of the ArrayAdapter (the hint), we
     * want to display a hint instead of regular text.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        if (position == getCount()) {
            ((TextView) v.findViewById(android.R.id.text1)).setText("");
            ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); // displays hint
            ((TextView) v.findViewById(android.R.id.text1)).setHintTextColor(v.getResources().getColor(R.color.hint_color));
        }
        return v;
    }

    /**
     * Shorten the list of elements displayed in the spinner, so we don't include
     * the item we're using as the hint.
     */
    @Override
    public int getCount() {
        return super.getCount()-1; // last item is used as a hint
    }
}
