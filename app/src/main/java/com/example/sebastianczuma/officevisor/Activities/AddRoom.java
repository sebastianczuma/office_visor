package com.example.sebastianczuma.officevisor.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sebastianczuma.officevisor.BackgroundBlur.BackgroundBlur;
import com.example.sebastianczuma.officevisor.DataKeepers.Rooms;
import com.example.sebastianczuma.officevisor.Database.DbHandlerFloors;
import com.example.sebastianczuma.officevisor.Database.DbHandlerRooms;
import com.example.sebastianczuma.officevisor.R;

import java.util.ArrayList;

public class AddRoom extends AppCompatActivity {
    BackgroundBlur backgroundBlur;
    AlertDialog.Builder builder;
    Button choose;
    DbHandlerFloors dbHandlerFloors;
    DbHandlerRooms dbHandlerRooms;
    String extra_building_name;
    String extra_floor_number;
    int extra_number_of_rooms;

    private void checkExtraStringsAtStart() {
        extra_building_name = getIntent().getStringExtra("EXTRA_BUILDING_NAME");
        extra_floor_number = getIntent().getStringExtra("EXTRA_FLOOR_NUMBER");
        extra_number_of_rooms = getIntent().getIntExtra("EXTRA_NUMBER_OF_ROOMS", 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHandlerFloors.close();
        dbHandlerRooms.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        // Transparent system navbar, statusbar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        // checking what building, floor is it and number of rooms on floor to update floor info
        checkExtraStringsAtStart();

        // Declarations
        TextView activityInfo = (TextView) findViewById(R.id.activity_info);
        final Button add = (Button) findViewById(R.id.add_new_room);
        choose = (Button) findViewById(R.id.choose_room_type);
        dbHandlerFloors = new DbHandlerFloors(this);
        dbHandlerRooms = new DbHandlerRooms(this);
        final EditText name = (EditText) findViewById(R.id.new_room_name);
        final ArrayList<String> names = new ArrayList<>();
        RelativeLayout SCREEN = (RelativeLayout) findViewById(R.id.activity_add_room_relative_layout);
        ImageView SCREEN_OVERLAYING_IMAGE = (ImageView) findViewById(R.id.image_for_blur);
        backgroundBlur = new BackgroundBlur(SCREEN, SCREEN_OVERLAYING_IMAGE, this);

        // Additional setup
        activityInfo.setText("Dodaj nowe pomieszczenie na poziomie " + extra_floor_number +
                " w budynku " + extra_building_name);

        // Listeners
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseItem();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Rooms k : dbHandlerRooms.returnAllRooms(
                        extra_building_name,
                        extra_floor_number)) {
                    names.add(k.getNazwa());
                }
                if (names.contains(name.getText().toString())) {
                    nameExistDialog().show();
                } else {
                    if (choose.getText().toString().equals(getString(R.string.choose))) {
                        userPleaseChoose().show();
                    } else {
                        Rooms room = new Rooms();
                        room.setNazwa(name.getText().toString());
                        room.setTyp(choose.getText().toString());
                        room.setIleUrzadzen(0);
                        room.setNazwaBudynku(extra_building_name);
                        room.setNumerPoziomu(extra_floor_number);
                        dbHandlerRooms.addRoom(room);

                        dbHandlerFloors.UpdateRoomsNumber(
                                extra_building_name,
                                extra_floor_number,
                                extra_number_of_rooms + 1);

                        dbHandlerFloors.close();
                        dbHandlerRooms.close();

                        Intent intent = new Intent(getApplicationContext(), FloorActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXTRA_NEW_ROOM", "success");
                        intent.putExtra("EXTRA_BUILDING_NAME", extra_building_name);
                        intent.putExtra("EXTRA_FLOOR_NUMBER", extra_floor_number);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    public Dialog nameExistDialog() {
        backgroundBlur.blurBackground();

        builder = new AlertDialog.Builder(this);
        builder.setMessage("Podana nazwa pomieszczenia już istnieje. Proszę, wybierz inną.")
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

    public Dialog userPleaseChoose() {
        backgroundBlur.blurBackground();

        builder = new AlertDialog.Builder(this);
        builder.setMessage("Proszę wybierz typ pomieszczenia")
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
        builderSingle.setTitle("Wybierz typ pomieszczenia");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Biuro");
        arrayAdapter.add("Pokój rozmów");
        arrayAdapter.add("Wypoczynek");
        arrayAdapter.add("Szatnia");
        arrayAdapter.add("Centrum spotkań");
        arrayAdapter.add("Palarnia");
        arrayAdapter.add("Schowek");
        arrayAdapter.add("Szatnia");

        builderSingle.setNegativeButton(
                getString(R.string.cancel),
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
}
