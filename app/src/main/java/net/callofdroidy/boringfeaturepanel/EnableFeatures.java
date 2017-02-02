package net.callofdroidy.boringfeaturepanel;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.RuntimeExecutionException;

/**
 * Created by yli on 30/01/17.
 */

public class EnableFeatures {
    private static final String TAG = "EnableFeatures";

    public static void enableBluetooth(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter != null)
            if(!bluetoothAdapter.isEnabled())
                bluetoothAdapter.enable();
        else throw new RuntimeException("No Bluetooth Adapter Found");
    }

    public static void enableLocationService(final AppCompatActivity currentActivity, GoogleApiClient googleApiClient,
                                             final FeatureCallback featureCallback, final int requestCode){
        LocationRequest locationRequest = LocationRequest.create();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);

        PendingResult<LocationSettingsResult> locationResult = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        locationResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.e(TAG, "statusCheck: location service enabled");
                        featureCallback.onFeatureEnabled();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // need the currentActivity override onActivityResult
                            status.startResolutionForResult(currentActivity, requestCode);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "statusCheck: " + e.toString());
                        }
                        break;
                }
            }
        });
    }

    public static boolean checkNetworkConnection(AppCompatActivity currentActivity, BroadcastReceiver receiver){
        ConnectivityManager cm = (ConnectivityManager)currentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        if(!isConnected){
            IntentFilter networkStateFilter = new IntentFilter();
            networkStateFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            currentActivity.registerReceiver(receiver, networkStateFilter);
            return false;
        }else
            return true;
    }
}
