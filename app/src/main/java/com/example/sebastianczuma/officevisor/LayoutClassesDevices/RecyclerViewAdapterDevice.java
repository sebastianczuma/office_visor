package com.example.sebastianczuma.officevisor.LayoutClassesDevices;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sebastianczuma.officevisor.Activities.RoomActivity;
import com.example.sebastianczuma.officevisor.Fragments.FragmentFirst;
import com.example.sebastianczuma.officevisor.R;

import java.util.List;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

public class RecyclerViewAdapterDevice extends RecyclerView.Adapter<RecyclerViewHoldersDevice> {
    private RoomActivity contextR;
    private FragmentFirst contextF;
    private List<ItemObjectDevice> itemList;

    public RecyclerViewAdapterDevice(List<ItemObjectDevice> itemList, RoomActivity context) {
        this.itemList = itemList;
        this.contextR = context;
    }

    public RecyclerViewAdapterDevice(List<ItemObjectDevice> itemList, FragmentFirst context) {
        this.itemList = itemList;
        this.contextF = context;
    }

    @Override
    public RecyclerViewHoldersDevice onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_device, null);
        if (contextR != null) {
            return new RecyclerViewHoldersDevice(layoutView, contextR);
        } else {
            return new RecyclerViewHoldersDevice(layoutView, contextF);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHoldersDevice holder, int position) {
        holder.deviceName.setText(itemList.get(position).getName());
        holder.roomIcon.setImageResource(itemList.get(position).getPhoto());
        holder.deviceType.setText(itemList.get(position).getType());
        holder.data.setText(itemList.get(position).getData());

        holder.nazwaPomieszczenia = itemList.get(position).getNazwaPomieszczenia();
        holder.numerPoziomu = itemList.get(position).getNumerPoziomu();
        holder.nazwaBudynku = itemList.get(position).getNazwaBudynku();

        String ulubione = itemList.get(position).getUlubione();
        holder.ulubione = ulubione;

        String pozycja = itemList.get(position).getPozycja();
        holder.pozycja = pozycja;

        String alarmActive = itemList.get(position).getIsAlarmActive();

        if (alarmActive != null) {
            Log.d("GG", alarmActive);
            if (alarmActive.equals("tak")) {
                holder.mainContainer.setBackgroundResource(R.drawable.one_device_background_red);
            }
        }

        if (ulubione.equals("")) {
            holder.favourite.setVisibility(View.INVISIBLE);
        } else {
            holder.favourite.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}