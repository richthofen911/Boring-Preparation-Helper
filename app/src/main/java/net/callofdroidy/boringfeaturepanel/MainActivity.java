package net.callofdroidy.boringfeaturepanel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    BroadcastReceiver networkCheckReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // check again if it is connected
            if(EnableFeatures.checkNetworkConnection(MainActivity.this, networkCheckReceiver))
                unregisterReceiver(this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EnableFeatures.checkNetworkConnection(this, networkCheckReceiver);


    }
}
