package com.example.sebastianczuma.officevisor.DevicePositionInDeviceInfo;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sebastianczuma.officevisor.Activities.AddDevice;
import com.example.sebastianczuma.officevisor.Activities.DeviceInfo;
import com.example.sebastianczuma.officevisor.R;

import java.util.List;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

public class RecyclerViewAdapterDevicePosition extends RecyclerView.Adapter<RecyclerViewHoldersDevicePosition> {
    private List<ItemObjectDevicePosition> itemList;
    private AddDevice addDevice;
    private DeviceInfo deviceInfo;

    private int positionToChangeBackground;

    public RecyclerViewAdapterDevicePosition(List<ItemObjectDevicePosition> itemList, AddDevice context) {
        this.itemList = itemList;
        this.addDevice = context;
    }

    public RecyclerViewAdapterDevicePosition(List<ItemObjectDevicePosition> itemList, DeviceInfo context, String positionNew) {
        this.itemList = itemList;
        this.deviceInfo = context;
        this.positionToChangeBackground = Integer.parseInt(positionNew);
    }

    @Override
    public RecyclerViewHoldersDevicePosition onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_position, null);
        if (addDevice != null) {
            return new RecyclerViewHoldersDevicePosition(layoutView, addDevice);
        } else {
            return new RecyclerViewHoldersDevicePosition(layoutView, deviceInfo);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHoldersDevicePosition holder, int position) {
        holder.deviceName.setText(itemList.get(position).getName());
        if (positionToChangeBackground != 0) {
            if (position == positionToChangeBackground - 1) {
                holder.itemView.setBackgroundResource(R.drawable.device_position_background_choosed);
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}