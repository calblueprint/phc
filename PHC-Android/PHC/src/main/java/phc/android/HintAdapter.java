package phc.android;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Creates a simple variation of an ArrayAdapter which supports hints.
 */
public class HintAdapter extends ArrayAdapter<String> {

    public HintAdapter(Context context, int textViewResourceId, String[] items) {
        super(context, textViewResourceId);
        for (int i = 0; i < items.length; i++) {
            this.add(items[i]);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = super.getView(position, convertView, parent);
        if (position == getCount()) {
            ((TextView)v.findViewById(android.R.id.text1)).setText("");
            ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
        }

        return v;
    }

    @Override
    public int getCount() {
        return super.getCount()-1; // you dont display last item. It is used as hint.
    }
}
