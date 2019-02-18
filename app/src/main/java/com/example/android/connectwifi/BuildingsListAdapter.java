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


public class BuildingsListAdapter extends RecyclerView.Adapter<BuildingsListAdapter.ViewHolder> {

    public Context mContext;
    public List<Buildings> buildingsList;

    public BuildingsListAdapter(Context context, List<Buildings> buildingsList){
        this.buildingsList = buildingsList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.building_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.buildingNameText.setText(buildingsList.get(i).getBuildingName());
        viewHolder.buildingAddressText.setText(buildingsList.get(i).getBuildingAddress());
        viewHolder.buildingID = buildingsList.get(i).getBuilding_id();

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent floorIntent = new Intent(mContext, FloorsActivity.class);
                floorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                floorIntent.putExtra("buildingID",  viewHolder.buildingID);
                floorIntent.putExtra("buildingName",  viewHolder.buildingNameText.getText());
                mContext.startActivity(floorIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return buildingsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public TextView buildingNameText, buildingAddressText;
        public RelativeLayout parentLayout;
        public String buildingID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            buildingID = "";

            buildingNameText = mView.findViewById(R.id.txtBuildingName);
            buildingAddressText = mView.findViewById(R.id.txtBuildingAddress);
            parentLayout = mView.findViewById(R.id.parentLayout);
        }
    }
}
