package mars.c.wearsynclib;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by Constantine Mars on 1/24/15.
 */
public class Client {
    private static final String TAG = Client.class.getName();
    private static final int REQUEST_OAUTH = 1;

    private Activity activity;

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    private GoogleApiClient googleApiClient;
    private boolean authInProgress = false;

    public interface ConnectionListener {
        public void onConnected();
    }
    private ConnectionListener connectionListener;

    public Client(Activity activity, ConnectionListener connectionListener) {
        this.activity = activity;
        this.connectionListener = connectionListener;
        init();
        connect();
    }

    public void connect() {
        googleApiClient.connect();
    }
    public void disconnect() {
        googleApiClient.disconnect();
    }

    private void init() {
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        // Now you can use the Data Layer API

                        connectionListener.onConnected();
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);

                        if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
                            // The Android Wear app is not installed
                            Log.d(TAG, "The Android Wear app is not installed: " + result);
                        }

                        if (!result.hasResolution()) {
                            Log.d(TAG, "!result.hasResolution - no resolution");
                            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), activity, 0).show();
                            return;
                        }

                        if (!authInProgress) {
                            try {
                                Log.d(TAG, "invoking resolution - attempting to resolve failed connection");
                                authInProgress = true;
                                result.startResolutionForResult(activity, REQUEST_OAUTH);
                            } catch (IntentSender.SendIntentException e) {
                                Log.d(TAG, "Exception while starting resolution activity: " + e.getMessage());
                            }
                        }
                    }
                })

                // Request access only to the Wearable API
                .addApi(Wearable.API)
                .build();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {
            Log.d(TAG, "onActivityResult: REQUEST_OAUTH");
            authInProgress = false;
            if (resultCode == Activity.RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!googleApiClient.isConnecting() && !googleApiClient.isConnected()) {
                    Log.d(TAG, "onActivityResult: client.connect()");
                    googleApiClient.connect();
                }
            }
        }
    }
}
