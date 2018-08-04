package com.example.sebastianczuma.officevisor.LayoutClassesFloors;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sebastianczuma.officevisor.EndWordSolver;
import com.example.sebastianczuma.officevisor.R;

import java.util.List;

public class RecyclerViewAdapterFloor extends RecyclerView.Adapter<RecyclerViewHoldersFloor> {
    private List<ItemObjectFloor> itemList;
    private String buildingName;

    public RecyclerViewAdapterFloor(List<ItemObjectFloor> itemList, String buildingName) {
        this.itemList = itemList;
        this.buildingName = buildingName;
    }

    @Override
    public RecyclerViewHoldersFloor onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_floor, null);
        return new RecyclerViewHoldersFloor(layoutView, buildingName);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHoldersFloor holder, int position) {
        holder.floorNumber.setText("Poziom " + itemList.get(position).getFloorNumber());
        holder.floorNumberForDatabase = itemList.get(position).getFloorNumber();

        int roomsNumber = itemList.get(position).getRoomsNumber();
        int devicesNumber = itemList.get(position).getDevicesNumber();

        holder.roomsDevicesNumber.setText(
                roomsNumber + " pomieszcze" + EndWordSolver.returnEnd(roomsNumber)
                        + " i " +
                        devicesNumber + " urządze" + EndWordSolver.returnEnd(devicesNumber));
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}