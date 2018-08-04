package com.example.sebastianczuma.officevisor.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sebastianczuma.officevisor.BackgroundBlur.BackgroundBlur;
import com.example.sebastianczuma.officevisor.DataKeepers.Devices;
import com.example.sebastianczuma.officevisor.Database.DbHandlerDevices;
import com.example.sebastianczuma.officevisor.Database.DbHandlerFloors;
import com.example.sebastianczuma.officevisor.Database.DbHandlerRooms;
import com.example.sebastianczuma.officevisor.DevicePositionInDeviceInfo.ItemObjectDevicePosition;
import com.example.sebastianczuma.officevisor.DevicePositionInDeviceInfo.RecyclerViewAdapterDevicePosition;
import com.example.sebastianczuma.officevisor.R;

import java.util.ArrayList;
import java.util.List;

public class AddDevice extends AppCompatActivity {
    public String newPosition = "";
    public String oldPosition = "";
    BackgroundBlur backgroundBlur;
    AlertDialog.Builder builder;
    Button choose;
    DbHandlerDevices dbHandlerDevices;
    DbHandlerRooms dbHandlerRooms;
    DbHandlerFloors dbHandlerFloors;
    String extra_room_name;
    String extra_building_name;
    int extra_dev_number;
    String extra_floor_number;
    RecyclerViewAdapterDevicePosition rcAdapter;
    RecyclerView rView;
    List<ItemObjectDevicePosition> rowListItem;
    View oldView;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHandlerFloors.close();
        dbHandlerRooms.close();
        dbHandlerDevices.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        checkExtraStringsAtStart();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        TextView activityInfo = (TextView) findViewById(R.id.activity_info);
        final Button add = (Button) findViewById(R.id.add_new_device);
        choose = (Button) findViewById(R.id.choose_device_type);
        dbHandlerDevices = new DbHandlerDevices(this);
        dbHandlerRooms = new DbHandlerRooms(this);
        dbHandlerFloors = new DbHandlerFloors(this);
        final EditText name = (EditText) findViewById(R.id.new_device_name);
        final ArrayList<String> names = new ArrayList<>();
        RelativeLayout SCREEN = (RelativeLayout) findViewById(R.id.activity_add_device_relative_layout);
        ImageView SCREEN_OVERLAYING_IMAGE = (ImageView) findViewById(R.id.image_for_blur);

        // Additional setup
        activityInfo.setText("Dodaj nowy sensor, " + extra_room_name + " na poziomie " + extra_floor_number +
                " w budynku " + extra_building_name);
        backgroundBlur = new BackgroundBlur(SCREEN, SCREEN_OVERLAYING_IMAGE, this);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseItem();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Devices k : dbHandlerDevices.returnAllDevices(extra_room_name, extra_floor_number, extra_building_name)) {
                    names.add(k.getNazwa());
                }

                if (names.contains(name.getText().toString())) {
                    nameExistDialog().show();
                } else {
                    if (choose.getText().toString().equals("Wybierz typ")) {
                        userPleaseChoose().show();
                    } else {
                        Devices device = new Devices();
                        device.setNazwa(name.getText().toString());
                        device.setTyp(choose.getText().toString());
                        device.setPomieszczenie(extra_room_name);
                        device.setNazwaBudynku(extra_building_name);
                        device.setNumerPoziomu(extra_floor_number);
                        device.setUlubione("");
                        device.setPozycja(newPosition);
                        dbHandlerDevices.addDevice(device);

                        dbHandlerRooms.UpdateDevicesNumber(extra_room_name, extra_dev_number + 1, extra_building_name, extra_floor_number);

                        int devNumber = dbHandlerFloors.returnDevicesNumber(extra_building_name, extra_floor_number);
                        dbHandlerFloors.UpdateDevicesNumber(extra_building_name, extra_floor_number, devNumber + 1);

                        Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXTRA_NEW_DEVICE", "success");
                        intent.putExtra("EXTRA_ROOM_NAME", extra_room_name);
                        intent.putExtra("EXTRA_BUILDING_NAME", extra_building_name);
                        intent.putExtra("EXTRA_FLOOR_NUMBER", extra_floor_number);
                        startActivity(intent);
                    }
                }
            }
        });


        rView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager lLayout = new GridLayoutManager(this, 7);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
        rowListItem = getAllItemList();
        //extra_dev_number = rowListItem.size();
        rcAdapter = new RecyclerViewAdapterDevicePosition(rowListItem, this);
        rView.setAdapter(rcAdapter);
        AddDevice.SpaceItemDecoration dividerItemDecoration = new AddDevice.SpaceItemDecoration(10);
        rView.addItemDecoration(dividerItemDecoration);
    }

    private List<ItemObjectDevicePosition> getAllItemList() {
        List<ItemObjectDevicePosition> allItems = new ArrayList<>();

        for (int i = 1; i <= 42; i++) {
            allItems.add(new ItemObjectDevicePosition(i + ""));
        }

        return allItems;
    }

    public void dataSetChanged(View view) {
        rcAdapter.notifyItemChanged(Integer.parseInt(newPosition) - 1, view);

        if (!newPosition.equals(oldPosition)) {
            if (oldView != null) {
                oldView.setBackgroundResource(R.drawable.one_room_background);
                rcAdapter.notifyItemChanged(Integer.parseInt(oldPosition) - 1, oldView);
            }
        }
        oldPosition = newPosition;
        oldView = view;
    }


    public Dialog nameExistDialog() {
        backgroundBlur.blurBackground();

        builder = new AlertDialog.Builder(this);
        builder.setMessage("Podana nazwa czujnika już istnieje. Proszę, wybierz inną.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                backgroundBlur.unblurBackground();
            }
        });
        return builder.create();
    }

    public Dialog userPleaseChoose() {
        backgroundBlur.blurBackground();

        builder = new AlertDialog.Builder(this);
        builder.setMessage("Proszę wybierz typ czujnika")
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                backgroundBlur.unblurBackground();
            }
        });
        return builder.create();
    }

    public void chooseItem() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.ic_add_black_24dp);
        builderSingle.setTitle("Wybierz typ czujnika");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Czujnik temperatury");
        arrayAdapter.add("Czujnik dymu");
        arrayAdapter.add("Czujnik wody");
        arrayAdapter.add("Czujnik gazu");
        arrayAdapter.add("Czujnik obecności");
        arrayAdapter.add("Czujnik wilgotności");
        arrayAdapter.add("Zamek");
        arrayAdapter.add("Oświetlenie");

        builderSingle.setNegativeButton(
                "Anuluj",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choose.setText(arrayAdapter.getItem(which));
                        dialog.dismiss();
                    }
                });
        builderSingle.show();
    }

    private void checkExtraStringsAtStart() {
        extra_building_name = getIntent().getStringExtra("EXTRA_BUILDING_NAME");
        extra_floor_number = getIntent().getStringExtra("EXTRA_FLOOR_NUMBER");
        extra_room_name = getIntent().getStringExtra("EXTRA_ROOM_NAME");
        extra_dev_number = getIntent().getIntExtra("EXTRA_DEV_NUMBER", 0);
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = space;
            outRect.left = space;
        }
    }

}
