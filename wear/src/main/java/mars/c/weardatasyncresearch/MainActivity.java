package mars.c.weardatasyncresearch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Wearable;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mars.c.wearsynclib.Client;

public class MainActivity extends Activity implements DataApi.DataListener{

    private static final String TAG = MainActivity.class.getCanonicalName();
    private Client client;
    @InjectView(R.id.status)
    protected TextView status;
    @InjectView(R.id.data)
    protected TextView data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MainActivity activity = this;
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                ButterKnife.inject(activity);
                client = new Client(activity, new Client.ConnectionListener() {
                    @Override
                    public void onConnected() {
                        Log.d(TAG, "connected");
                        status.setText("connected");
                        Wearable.DataApi.addListener(client.getGoogleApiClient(), activity);
                    }
                });
            }
        });
    }

    @Override
    protected void onStop() {
        Wearable.DataApi.removeListener(client.getGoogleApiClient(), this);
        client.disconnect();
        super.onStop();
    }

    private Handler handler = new Handler();

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        final String message = "onDataChanged: " + dataEvents.getCount();
        Log.d(TAG, message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                data.setText(message);
            }
        });

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                final String message1 = "onDataChanged: DELETED="+event.getDataItem().toString();
                Log.d(TAG, message1);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        data.setText(message1);
                    }
                });
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                final String message1 = "onDataChanged: CHANGED="+event.getDataItem().toString();
                Log.d(TAG, message1);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        data.setText(message);
                    }
                });
            }
        }
    }
}
