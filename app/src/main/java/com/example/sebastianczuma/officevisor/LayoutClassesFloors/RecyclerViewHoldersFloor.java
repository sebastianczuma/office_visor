package com.example.sebastianczuma.officevisor.LayoutClassesFloors;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.sebastianczuma.officevisor.Activities.FloorActivity;
import com.example.sebastianczuma.officevisor.R;

class RecyclerViewHoldersFloor extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView floorNumber;
    TextView roomsDevicesNumber;
    String floorNumberForDatabase;
    private String buildingName;

    RecyclerViewHoldersFloor(View itemView, String buildingName) {
        super(itemView);
        itemView.setOnClickListener(this);

        this.buildingName = buildingName;

        floorNumber = (TextView) itemView.findViewById(R.id.floorNumber);
        roomsDevicesNumber = (TextView) itemView.findViewById(R.id.roomsDevicesNumber);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), FloorActivity.class);
        intent.putExtra("EXTRA_BUILDING_NAME", buildingName);
        intent.putExtra("EXTRA_FLOOR_NUMBER", floorNumberForDatabase);
        view.getContext().startActivity(intent);
    }
}