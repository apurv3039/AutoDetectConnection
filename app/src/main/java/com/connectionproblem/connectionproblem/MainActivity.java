package com.connectionproblem.connectionproblem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private NetworkChangeReceiver receiver;
    private boolean isConnected = false;
    private LinearLayout connection_problem;
    private TextView textContent;
    private Button refresh_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        connection_problem = (LinearLayout) findViewById(R.id.connection_problem);
        textContent=(TextView) findViewById(R.id.textContent);
        refresh_button = (Button) findViewById(R.id.refresh_button);
        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);

    }
    public void loadContent(){
        textContent.setText("After Loading Content");
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            Log.v("Receieved", "Receieved notification about network status");
            isNetworkAvailable(context);
        }
    }
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        if (!isConnected) {
                            Log.v("Conntect", "Now you are connected to Internet!");
                            connection_problem.setVisibility(View.GONE);
                            loadContent();
                            isConnected = true;
                        }
                        return true;
                    }
                }
            }
        }
        connection_problem.setVisibility(View.VISIBLE);
        isConnected = false;
        return false;
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (isNetworkAvailable(this)) {
            loadContent();
        } else {
            connection_problem.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        System.gc();
    }
}
