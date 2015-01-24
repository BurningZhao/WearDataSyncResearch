package mars.c.wearsynclib;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.Callable;

/**
 * Created by Constantine Mars on 1/24/15.
 */
public class WearConnector {
    private Client client;
    private static final String TAG = WearConnector.class.getCanonicalName();
    public static final String DATA_KEY = "DATA_KEY";
    public static final String DATA_PATH = "/DATA_PATH";

    public interface Display {
        public void show(String message);
    }
    private Display display;

    public WearConnector(Activity activity, Client.ConnectionListener connectionListener, Display display) {
        this.client = new Client(activity, connectionListener);
        this.display = display;
    }

    public void disconnect() {
        client.disconnect();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        client.onActivityResult(requestCode, resultCode, data);
    }

    public void send(String data) {
        PutDataMapRequest dataMap = PutDataMapRequest.create(DATA_PATH);
        dataMap.getDataMap().putString(DATA_KEY, data);
        final PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(client.getGoogleApiClient(), request);

        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                String result = "pendingResult.onResult: " + dataItemResult.getStatus() + ", data:"+ dataItemResult.getDataItem().getData().toString()+", uri:" + dataItemResult.getDataItem().getUri();
                Log.d(TAG, result);
                display.show(result);
            }
        });
    }
}
