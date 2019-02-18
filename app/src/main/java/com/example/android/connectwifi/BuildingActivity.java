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

public class BuildingActivity extends AppCompatActivity {

    private static final String TAG = "FireLog";
    private RecyclerView buildingListView;

    private List<Buildings> buildingsList;
    private BuildingsListAdapter buildingListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        setTitle("Select Building and Floor");

        buildingsList = new ArrayList<>();
        buildingListAdapter = new BuildingsListAdapter(getApplicationContext(), buildingsList);

        buildingListView = findViewById(R.id.buildingList);
        buildingListView.setHasFixedSize(true);
        buildingListView.setLayoutManager(new LinearLayoutManager(this));
        buildingListView.setAdapter(buildingListAdapter);

        FirebaseFirestore db_buildings = FirebaseFirestore.getInstance();
        db_buildings.collection("buildings").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, "Error" + e.getMessage());
                }

                for(DocumentChange doc: documentSnapshots.getDocumentChanges()){

                    if(doc.getType() == DocumentChange.Type.ADDED){

                        Buildings buildings = new Buildings();

                        buildings.setBuilding_id(doc.getDocument().getId());
                        buildings.setBuildingName(doc.getDocument().getString("name"));
                        buildings.setBuildingAddress(doc.getDocument().getString("address"));
                        buildings.setBuildingUser_id(doc.getDocument().getString("user_id"));

                        buildingsList.add(buildings);
                        buildingListAdapter.notifyDataSetChanged();

                    }
                }
            }
        });
        Log.d(TAG, "BUILDING LIST");


    }
}
