package com.example.sebastianczuma.officevisor.LayoutClassesRooms;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sebastianczuma.officevisor.Activities.FloorActivity;
import com.example.sebastianczuma.officevisor.EndWordSolver;
import com.example.sebastianczuma.officevisor.R;

import java.util.List;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

public class RecyclerViewAdapterRoom extends RecyclerView.Adapter<RecyclerViewHoldersRoom> {
    private FloorActivity context;
    private String extra_building_name;
    private String extra_floor_number;
    private List<ItemObjectRoom> itemList;

    public RecyclerViewAdapterRoom(List<ItemObjectRoom> itemList, String exta_building_name, String extra_floor_number, FloorActivity context) {
        this.itemList = itemList;
        this.extra_building_name = exta_building_name;
        this.extra_floor_number = extra_floor_number;

        this.context = context;
    }

    @Override
    public RecyclerViewHoldersRoom onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_room, null);
        return new RecyclerViewHoldersRoom(layoutView, context);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHoldersRoom holder, int position) {
        holder.roomName.setText(itemList.get(position).getName());
        holder.roomIcon.setImageResource(itemList.get(position).getPhoto());
        int devicesNumber = itemList.get(position).getDevicesNumber();
        holder.numberDevices.setText(devicesNumber + " urządze" + EndWordSolver.returnEnd(devicesNumber));
        holder.extra_building_name = extra_building_name;
        holder.extra_floor_number = extra_floor_number;

    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}