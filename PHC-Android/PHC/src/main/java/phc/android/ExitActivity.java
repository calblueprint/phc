package phc.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

/**
 * An activity for when a client exits an event and wants someone to follow up
 * on them.
 *
 * Created by howardchen on 12/1/14.
 */
public class ExitActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);
        FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        //move this activity to a fragment
        //remove this fragment.
        transaction.add(R.id.checkout_activity_container, new CheckoutFormFragment());
        transaction.commit();
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

}
