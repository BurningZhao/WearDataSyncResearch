package mars.c.weardatasyncresearch;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mars.c.wearsynclib.Client;

public class MainActivity extends Activity {

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

        final Activity activity = this;
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
                    }
                });
            }
        });
    }
}
