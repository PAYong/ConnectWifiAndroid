package com.example.android.connectwifi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

public class FloorsActivity extends AppCompatActivity {

    String buildingName, buildingID;

    private static final String TAG = "FireLog";
    private RecyclerView floorListView;

    private List<Floors> floorsList;
    private FloorsListAdapter floorsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floors);
        getIncomingIntent();

        Log.d(TAG, "FLOOR LIST");
        if(!buildingName.isEmpty()){
            setTitle(buildingName);
            Toast.makeText(getApplicationContext(), buildingID, Toast.LENGTH_SHORT).show();
        }

        floorsList = new ArrayList<>();
        floorsListAdapter = new FloorsListAdapter(getApplicationContext(), floorsList);

        floorListView = findViewById(R.id.floorsList);
        floorListView.setHasFixedSize(true);
        floorListView.setLayoutManager(new LinearLayoutManager(this));
        floorListView.setAdapter(floorsListAdapter);

        FirebaseFirestore db_floors = FirebaseFirestore.getInstance();
        db_floors.collection("floors").whereEqualTo("building_id", buildingID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, "FLOOR ERROR" + e.getMessage());
                }

                for(DocumentChange doc: documentSnapshots.getDocumentChanges()){

                    if(doc.getType() == DocumentChange.Type.ADDED){

                        Floors floors = new Floors();

                        floors.setBuilding_name(buildingName);
                        floors.setBuilding_id(buildingID);
                        floors.setFloor_id(doc.getDocument().getId());
                        floors.setFloorName(doc.getDocument().getString("name"));
                        floors.setFloorConfig(doc.getDocument().getString("config"));

                        floorsList.add(floors);
                        floorsListAdapter.notifyDataSetChanged();

                    }
                }
            }
        });



    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("buildingName") && getIntent().hasExtra("buildingID")){
            buildingName = getIntent().getStringExtra("buildingName");
            buildingID = getIntent().getStringExtra("buildingID");
        }
    }
}
