package phc.android;


import android.app.Application;

/**
 * Created by Nishant on 10/19/14.
 */
public class PHCApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

		/*
		 * Un-comment the line below to enable push notifications in this app.
		 * Replace 'pnInterface' with your implementation of 'PushNotificationInterface'.
		 * Add your Google package ID in 'bootonfig.xml', as the value
		 * for the key 'androidPushNotificationClientId'.
		 */
        // SalesforceSDKManager.getInstance().setPushNotificationReceiver(pnInterface);
    }
}
