package com.example.android.connectwifi;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeviceInfo extends AppCompatActivity {

    private TextView status, result;
    private EditText password, urlPath;
    private Button connectBtn, addDeviceBtn;
    private WifiManager wifiManager;
    String ssid;

    private static String TAG = "DeviceInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        ssid = intent.getStringExtra("ssid");
        setTitle(ssid);

        setContentView(R.layout.activity_device_info);
        password = findViewById(R.id.editTextPassword);
        connectBtn = findViewById(R.id.buttonConnect);
        status = findViewById(R.id.textViewStatus);
        addDeviceBtn = findViewById(R.id.buttonAddDevice);
        urlPath = findViewById(R.id.editTextURL);
        result = findViewById(R.id.textViewResult);
        result.setMovementMethod( new ScrollingMovementMethod());
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

        addDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddDevice().execute(urlPath.getText().toString() , "data");
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

    public class AddDevice extends AsyncTask<String, String, Boolean> {

        public AddDevice(){
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String urlString = params[0]; // URL to call
            String data = params[1]; //data to post
            OutputStream out = null;

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();
                out.close();

                urlConnection.connect();

                // Check the connection response
                InputStream  response = urlConnection.getInputStream();
                InputStreamReader isw = new InputStreamReader(response);

                int dataRes = isw.read();
                StringBuilder sb = new StringBuilder();

                while (dataRes != -1) {
                    char current = (char) dataRes;
                    dataRes = isw.read();
                    sb.append(current);
                }
                Log.d(TAG, "response:" +  sb.toString());

                result.setText(sb.toString());
                return true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return false;
        }
    }
}
