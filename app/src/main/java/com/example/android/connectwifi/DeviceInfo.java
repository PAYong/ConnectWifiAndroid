package com.example.android.connectwifi;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.json.*;

public class DeviceInfo extends AppCompatActivity {

    private TextView status;
    private EditText password, deviceName;
    private Button connectBtn, blinkOnBtn, blinkOffBtn, sendConfigBtn;
    private WifiManager wifiManager;
    private WifiConfiguration wifiConfig;
    private JSONObject obj;
    private JSONObject configObject;
    private String selectedSSID, configWifiSSID, configWifiPassword, configWifiEndpoint, configEndpoints, selectedFloorID;

    private ProgressDialog configDialog, connectDialog;

    private static String TAG = "DeviceInfo";

    ConfigurationFile config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        config = new ConfigurationFile();
        configDialog = new ProgressDialog(DeviceInfo.this);
        connectDialog = new ProgressDialog(DeviceInfo.this);


        Intent intent = getIntent();
        selectedSSID = intent.getStringExtra("ssid");
        setTitle(selectedSSID);
        configWifiSSID = intent.getStringExtra("configWifiSSID");
        configWifiPassword = intent.getStringExtra("configWifiPassword");
        configWifiEndpoint = intent.getStringExtra("configWifiEndpoint");
        configEndpoints = intent.getStringExtra("configEndpoints");
        selectedFloorID = intent.getStringExtra("selectedFloorID");

        setContentView(R.layout.activity_device_info);
        password = findViewById(R.id.editTextPassword);
        deviceName = findViewById(R.id.txtDeviceName);
        connectBtn = findViewById(R.id.buttonConnect);
        status = findViewById(R.id.textViewStatus);
        blinkOnBtn = findViewById(R.id.btnBlinkOn);
        blinkOffBtn = findViewById(R.id.btnBlinkOff);
        sendConfigBtn = findViewById(R.id.btnSendConfig);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiConfig = new WifiConfiguration();

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!password.getText().toString().isEmpty()){
                    connectToWifi(password.getText().toString(), selectedSSID);
                }else{
                    Toast.makeText(getApplicationContext(), "Password is not set.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        blinkOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sendAction("BLINKON");
            }
        });


        blinkOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAction("BLINKOFF");
            }
        });


        sendConfigBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!deviceName.getText().toString().isEmpty()){
                    sendConfigFile();
                }else{
                    Toast.makeText(getApplicationContext(), "Device name is required.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(wifiManager.getConnectionInfo().getSSID().contains(selectedSSID) && isOnline(DeviceInfo.this)){
            status.setText("Device is connected.");
            status.setTextColor(getResources().getColor(R.color.colorTextLight));
            status.setBackgroundColor(getResources().getColor(R.color.colorSuccess));
        }


    }
    private boolean connectToWifi(String networkPass, String networkSSID) {
//
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);

        // remember id
        int netId;
        netId = wifiManager.addNetwork(wifiConfig);
;
        if(netId != -1){
            wifiManager.disconnect();
            wifiManager.disableNetwork(netId); //disable current network
            wifiManager.enableNetwork(netId, true);
            wifiManager.setWifiEnabled(true);
            wifiManager.reconnect();
        }
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"\"" + networkSSID + "\"\"";
        conf.preSharedKey = "\"" + networkPass + "\"";
        wifiManager.addNetwork(conf);

        new CheckConnectivity().execute();

        return false;
    }

    @SuppressLint("StaticFieldLeak")
    public class CheckConnectivity extends AsyncTask<String, String, Boolean> {

        public CheckConnectivity(){
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {

            connectDialog.setMessage("Connecting...");
            connectDialog.show();
            connectDialog.setCancelable(false);
            connectDialog.setCanceledOnTouchOutside(false);

            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                Thread.sleep(10000);
                if(isOnline(getApplicationContext()) && wifiManager.getConnectionInfo().getSSID().contains(selectedSSID)){
                    return true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d(TAG, "CONNECTION ERROR" + e.getMessage());
            }
            return false;

        }

        @Override
        protected void onPostExecute(Boolean results) {

            if(results){
                status.setText("Device is connected.");
                status.setTextColor(getResources().getColor(R.color.colorTextLight));
                status.setBackgroundColor(getResources().getColor(R.color.colorSuccess));
            }else{
                status.setText("Connection error. Please try again.");
                status.setTextColor(getResources().getColor(R.color.colorTextLight));
                status.setBackgroundColor(getResources().getColor(R.color.colorError));
                Toast.makeText(getApplicationContext(), "Error connecting.", Toast.LENGTH_SHORT).show();
            }

            connectDialog.hide();
            super.onPostExecute(results);
        }

    }


    @SuppressLint("StaticFieldLeak")
    public class SendPOST extends AsyncTask<String, String, Boolean> {

        public SendPOST(){
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
            OutputStream out;

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Content-Length", "" + data.getBytes().length);
                urlConnection.setRequestProperty("Content-Language", "en-US");
                urlConnection.setUseCaches(false);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);

                out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(data.replaceAll("\\\\",""));
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
                return true;
            } catch (Exception e) {
                System.out.println(e.getMessage());

            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            configDialog.hide();
            Toast.makeText(getApplicationContext(), "Successful send configuration.", Toast.LENGTH_LONG).show();
            super.onPostExecute(aBoolean);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class blinkGET extends AsyncTask<String, Void, String> {

        String server_response;

        public blinkGET(){
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0]; // URL to call

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                server_response = bufferedReader.readLine();

            } catch (Exception e) {
                System.out.println(e.getMessage());
                server_response = e.getMessage();

            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), server_response, Toast.LENGTH_SHORT).show();

        }

    }

    private void sendConfigFile(){

        DhcpInfo dhcp = wifiManager.getDhcpInfo();

        if(wifiManager.getConnectionInfo().getSSID().contains(selectedSSID) && dhcp.serverAddress != 0) {
            try {

                List<String> items = Arrays.asList(configEndpoints.split("\\s*,\\s*"));

                JSONArray endpointArrays = new JSONArray();
                for(String str : items)
                {
                    endpointArrays.put(str);
                }

                configObject = new JSONObject();
                configObject.put("wifi_ssid", configWifiSSID);
                configObject.put("wifi_password", configWifiPassword);
                configObject.put("endpoint", configWifiEndpoint);
                configObject.put("endpoints", endpointArrays);
                configObject.put("config", "config_1");
                configObject.put("floor_id", selectedFloorID);
                configObject.put("device_name", deviceName.getText().toString());

                String ipAddress = Formatter.formatIpAddress(dhcp.serverAddress);
                String getEndpoint = "http://" + ipAddress + "/post";

                new SendPOST().execute(getEndpoint, configObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Device is not currently connected.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendAction(String action){

        DhcpInfo dhcp = wifiManager.getDhcpInfo();

        if(wifiManager.getConnectionInfo().getSSID().contains(selectedSSID) && dhcp.serverAddress != 0){

            try {
                String ipAddress = Formatter.formatIpAddress(dhcp.serverAddress);
                String getEndpoint = "";

                if(action.equals("BLINKON")){
                    getEndpoint = "http://" + ipAddress + "/blinkon";
                }else if(action.equals("BLINKOFF")){
                    getEndpoint = "http://" + ipAddress + "/blinkoff";
                }

                Toast.makeText(getApplicationContext(), getEndpoint, Toast.LENGTH_LONG).show();
                new blinkGET().execute(getEndpoint);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Device is not currently connected.", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

}
