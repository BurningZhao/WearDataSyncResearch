package mars.c.weardatasyncresearch;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mars.c.wearsynclib.Client;
import mars.c.wearsynclib.WearConnector;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();
    private WearConnector wearConnector;

    @InjectView(R.id.status)
    protected TextView status;
    @InjectView(R.id.data)
    protected TextView data;
    @InjectView(R.id.list)
    protected ListView list;
    @InjectView(R.id.send)
    protected Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        wearConnector = new WearConnector(this, new Client.ConnectionListener() {
            @Override
            public void onConnected() {
                  Log.d(TAG, "client connected");
                status.setText("connected");
            }
        },
        new WearConnector.Display() {
            @Override
            public void show(String message) {
                data.setText(message);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "sending");
                wearConnector.send(String.valueOf(new Random().nextInt(100)));
            }
        });
    }

    @Override
    protected void onStop() {
        wearConnector.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        wearConnector.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
