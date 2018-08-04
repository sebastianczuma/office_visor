package com.example.sebastianczuma.officevisor.DevicePositionInDeviceInfo;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.sebastianczuma.officevisor.Activities.AddDevice;
import com.example.sebastianczuma.officevisor.Activities.DeviceInfo;
import com.example.sebastianczuma.officevisor.R;

class RecyclerViewHoldersDevicePosition extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView deviceName;
    private AddDevice addDevice;
    private DeviceInfo deviceInfo;

    RecyclerViewHoldersDevicePosition(View itemView, AddDevice context) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.addDevice = context;

        deviceName = (TextView) itemView.findViewById(R.id.name);
    }

    RecyclerViewHoldersDevicePosition(View itemView, DeviceInfo context) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.deviceInfo = context;

        deviceName = (TextView) itemView.findViewById(R.id.name);
    }

    @Override
    public void onClick(View view) {
        if (addDevice != null) {
            String newPosition = deviceName.getText().toString();
            view.setBackgroundResource(R.drawable.device_position_background_choosed);
            addDevice.newPosition = newPosition;
            addDevice.dataSetChanged(view);
        }

    }
}