package phc.android;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
    }
}
