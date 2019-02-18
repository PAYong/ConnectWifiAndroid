package com.example.android.connectwifi;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class FloorsListAdapter extends RecyclerView.Adapter<FloorsListAdapter.ViewHolder> {

    public Context mContext;
    public List<Floors> floorsList;

    public FloorsListAdapter(Context context, List<Floors> floorsList){
        this.floorsList = floorsList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.floor_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        viewHolder.floorNameText.setText(floorsList.get(i).getFloorName());
        viewHolder.floorConfigText.setText(floorsList.get(i).getFloorConfig());
        viewHolder.floorID = floorsList.get(i).getFloor_id();
        viewHolder.buildingID = floorsList.get(i).getBuilding_id();
        viewHolder.buildingName = floorsList.get(i).getBuilding_name();

        viewHolder.parentLayoutFloors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(mContext, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mainIntent.putExtra("floorID",  viewHolder.floorID);
                mainIntent.putExtra("floorName",  viewHolder.floorNameText.getText());
                mainIntent.putExtra("buildingID",  viewHolder.buildingID);
                mainIntent.putExtra("buildingName", viewHolder.buildingName);
                mContext.startActivity(mainIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return floorsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public TextView floorNameText, floorConfigText;
        public RelativeLayout parentLayoutFloors;
        public String floorID, buildingID, buildingName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            floorID = "";
            buildingID = "";
            buildingName = "";

            floorNameText = mView.findViewById(R.id.txtFloorName);
            floorConfigText = mView.findViewById(R.id.txtConfig);
            parentLayoutFloors = mView.findViewById(R.id.parentLayoutFloors);
        }
    }
}
