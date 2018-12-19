package com.example.android.connectwifi;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceInfo extends AppCompatActivity {

    private TextView status;
    private EditText password;
    private Button connectBtn;
    private WifiManager wifiManager;
    String ssid;

    private static String TAG = "DeviceInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        password = findViewById(R.id.editTextPassword);
        connectBtn = findViewById(R.id.buttonConnect);
        status = findViewById(R.id.textViewStatus);
        Intent intent = getIntent();
        ssid = intent.getStringExtra("ssid");

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToWifi(password.getText().toString(), ssid);
            }
        });
    }


    private void  connectToWifi(String networkPass, String networkSSID) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);

        // remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.setWifiEnabled(true);
        wifiManager.reconnect();

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"\"" + networkSSID + "\"\"";
        conf.preSharedKey = "\"" + networkPass + "\"";
        netId = wifiManager.addNetwork(conf);

        wifiManager.getConnectionInfo();
        if( netId != -1 ){
            status.setText("Device is connected.");
            status.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            Log.d(TAG, "Device is connected. netId: " +  netId);
        }else{
            Log.d(TAG, "ERROR");
        }
    }
}
