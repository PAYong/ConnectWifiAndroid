package com.example.android.connectwifi;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private WifiManager wifiManager;
    private ListView listView;
    private Button buttonScan, buttonSelectBuilding;
    private int size = 0;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    private EditText pass;


    private FirebaseFirestore firestore;
    private FirebaseFirestoreSettings settings;
    private DocumentReference configReference;
    private ConfigurationFile config;

    private String buildingName, buildingID, floorID, floorName;
    private TextView selectedBuildingFloor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getIncomingIntent();

        config = new ConfigurationFile();

        firestore = FirebaseFirestore.getInstance();
        settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        buttonScan = findViewById(R.id.scanBtn);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                    //do something if have the permissions
                    scanWifi();
                } else {
                    //do something, permission was previously granted; or legacy device
                    scanWifi();
                }

            }
        });

        listView = findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String ssid = ((TextView) view).getText().toString();
                Intent intent = new Intent(getApplicationContext(), DeviceInfo.class);

                if(!config.getConfigSSID().isEmpty() && !floorName.isEmpty()){
                    intent.putExtra("ssid", ssid);
                    intent.putExtra("configWifiSSID", config.getConfigSSID());
                    intent.putExtra("configWifiPassword", config.getConfigPassword());
                    intent.putExtra("configWifiEndpoint", config.getConfigEndpoint());
                    intent.putExtra("configEndpoints", config.getConfigEndpoints());
                    intent.putExtra("selectedFloorID", floorID);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "No floor is selected.", Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonSelectBuilding = findViewById(R.id.buildingBtn);
        buttonSelectBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent buildingIntent = new Intent(getApplicationContext(), BuildingActivity.class);
                startActivity(buildingIntent);
            }
        });

        scanWifi();
        getConfigFile();
    }

    private void scanWifi() {
        arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("buildingName") && getIntent().hasExtra("buildingID") && getIntent().hasExtra("floorID")  && getIntent().hasExtra("floorName")){
            buildingName = getIntent().getStringExtra("buildingName");
            buildingID = getIntent().getStringExtra("buildingID");
            floorName = getIntent().getStringExtra("floorName");
            floorID = getIntent().getStringExtra("floorID");

            selectedBuildingFloor = findViewById(R.id.selectedBuildingFloor);
            selectedBuildingFloor.setText(buildingName + " / " + floorName);
        }else{
            floorName = "";
        }
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);

            for (ScanResult scanResult : results) {
                if(!scanResult.SSID.isEmpty() && scanResult.SSID.contains("EXITMNGR")){
                    arrayList.add(scanResult.SSID);
                    adapter.notifyDataSetChanged();
                }
            }
        };
    };


    private void getConfigFile(){

        configReference = firestore.collection("device_configuration").document("/config_1");
        Task<DocumentSnapshot> configDocument = configReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {


                config.setConfigSSID(documentSnapshot.getString("wifi_ssid"));
                config.setConfigPassword(documentSnapshot.getString("wifi_password"));
                config.setConfigEndpoint(documentSnapshot.getString("endpoint"));

                Map<String, Object> listEndpoints = documentSnapshot.getData();

                config.setConfigEndpoints(listEndpoints.get("endpoints").toString());
                Toast.makeText(getApplicationContext(),"Successfully retrieve configuration.", Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.signout:
                FirebaseAuth.getInstance().signOut();
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(login);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
