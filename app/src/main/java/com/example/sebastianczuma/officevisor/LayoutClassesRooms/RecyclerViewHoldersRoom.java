package com.example.sebastianczuma.officevisor.LayoutClassesRooms;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sebastianczuma.officevisor.Activities.FloorActivity;
import com.example.sebastianczuma.officevisor.Activities.RoomActivity;
import com.example.sebastianczuma.officevisor.Database.DbHandlerDevices;
import com.example.sebastianczuma.officevisor.Database.DbHandlerRooms;
import com.example.sebastianczuma.officevisor.R;

class RecyclerViewHoldersRoom extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    TextView roomName;
    TextView numberDevices;
    ImageView roomIcon;

    String extra_building_name;
    String extra_floor_number;

    private FloorActivity context;

    RecyclerViewHoldersRoom(View itemView, FloorActivity context) {
        super(itemView);
        this.context = context;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);

        roomName = (TextView) itemView.findViewById(R.id.name);
        numberDevices = (TextView) itemView.findViewById(R.id.devnumber);
        roomIcon = (ImageView) itemView.findViewById(R.id.grid_item_image);
    }

    @Override
    public void onClick(View view) {
        Log.d("RecyclerHolder", roomName.getText().toString());
        Intent intent = new Intent(view.getContext(), RoomActivity.class);
        intent.putExtra("EXTRA_ROOM_NAME", roomName.getText().toString());
        intent.putExtra("EXTRA_BUILDING_NAME", extra_building_name);
        intent.putExtra("EXTRA_FLOOR_NUMBER", extra_floor_number);
        view.getContext().startActivity(intent);
    }

    @Override
    public boolean onLongClick(final View view) {
        if (context.isEditingEnabled) {
            removeRoom(view);
        }
        return false;
    }

    private void removeRoom(final View view) {
        context.blur();
        final String thisRoom = roomName.getText().toString();
        AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).create();
        alertDialog.setTitle("Uwaga");
        alertDialog.setMessage("Usunąć " + thisRoom + " z listy pomieszczeń?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final DbHandlerRooms dbHandlerRooms = new DbHandlerRooms(view.getContext());
                        dbHandlerRooms.deleteOneRoom(extra_building_name, extra_floor_number, thisRoom);

                        final DbHandlerDevices dbHandlerDevices = new DbHandlerDevices(view.getContext());
                        dbHandlerDevices.deleteRoomDevices(extra_building_name, extra_floor_number, thisRoom);

                        dbHandlerRooms.close();
                        dbHandlerDevices.close();

                        dialog.dismiss();
                        context.dataSetChanged();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Anuluj",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                context.unblur();
            }
        });
        alertDialog.show();
    }
}