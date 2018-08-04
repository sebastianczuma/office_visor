package com.example.sebastianczuma.officevisor.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sebastianczuma.officevisor.Animaitons.Animations;
import com.example.sebastianczuma.officevisor.BackgroundBlur.BackgroundBlur;
import com.example.sebastianczuma.officevisor.DataKeepers.Rooms;
import com.example.sebastianczuma.officevisor.Database.DbHandlerDevices;
import com.example.sebastianczuma.officevisor.Database.DbHandlerFloors;
import com.example.sebastianczuma.officevisor.Database.DbHandlerRooms;
import com.example.sebastianczuma.officevisor.EndWordSolver;
import com.example.sebastianczuma.officevisor.LayoutClassesRooms.ItemObjectRoom;
import com.example.sebastianczuma.officevisor.LayoutClassesRooms.RecyclerViewAdapterRoom;
import com.example.sebastianczuma.officevisor.R;

import java.util.ArrayList;
import java.util.List;

public class FloorActivity extends AppCompatActivity {
    public boolean isEditingEnabled = false;
    BackgroundBlur backgroundBlur;
    TextView buildingName;
    String extra_building_name;
    String extra_floor_number;
    TextView floorDevicesNumber;
    TextView floorNumber;
    Button mainEditButton;
    RelativeLayout moreEditButtons;
    TextView numberOfRooms;
    RecyclerViewAdapterRoom rcAdapter;
    List<ItemObjectRoom> rowListItem;
    RecyclerView rView;
    View SCREEN;
    ImageView SCREEN_OVERLAYING_IMAGE;

    @Override
    protected void onResume() {
        super.onResume();
        rowListItem = getAllItemList();
        rcAdapter = new RecyclerViewAdapterRoom(rowListItem, extra_building_name, extra_floor_number, this);
        rView.setAdapter(rcAdapter);

        final DbHandlerDevices dbHandlerDevices = new DbHandlerDevices(this);
        int devicesCount = dbHandlerDevices.getFloorDevicesCount(extra_building_name, extra_floor_number);

        floorDevicesNumber.setText(devicesCount + " urządze" + EndWordSolver.returnEnd(devicesCount));
    }

    private void checkExtraStringsAtStart() {
        extra_building_name = getIntent().getStringExtra("EXTRA_BUILDING_NAME");
        extra_floor_number = getIntent().getStringExtra("EXTRA_FLOOR_NUMBER");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor);

        // Transparent system navbar, statusbar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        // checking what building and floor is it
        checkExtraStringsAtStart();

        // Declarations
        ImageButton addNewRoom = (ImageButton) findViewById(R.id.add_room);
        buildingName = (TextView) findViewById(R.id.building_name);
        floorDevicesNumber = (TextView) findViewById(R.id.number_of_devices);
        floorNumber = (TextView) findViewById(R.id.floor_number);
        String forFloorNumber = getString(R.string.floor) + " " + extra_floor_number;
        GridLayoutManager lLayout = new GridLayoutManager(FloorActivity.this, 3);
        mainEditButton = (Button) findViewById(R.id.edit_button);
        moreEditButtons = (RelativeLayout) findViewById(R.id.edit_buttons_container);
        numberOfRooms = (TextView) findViewById(R.id.number_of_rooms);
        rView = (RecyclerView) findViewById(R.id.recycler_view);
        SCREEN = findViewById(R.id.activity_floor_relative_layout);
        SCREEN_OVERLAYING_IMAGE = (ImageView) findViewById(R.id.image_for_blur);

        // Recycle View setup
        rowListItem = getAllItemList();
        rcAdapter = new RecyclerViewAdapterRoom(rowListItem, extra_building_name, extra_floor_number, this);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
        rView.setAdapter(rcAdapter);
        SpaceItemDecoration dividerItemDecoration = new SpaceItemDecoration(20);
        rView.addItemDecoration(dividerItemDecoration);

        // Additional setup
        final int roomsNumber = rowListItem.size();
        backgroundBlur = new BackgroundBlur(SCREEN, SCREEN_OVERLAYING_IMAGE, this);

        numberOfRooms.setText(roomsNumber + " pomieszcze" + EndWordSolver.returnEnd(roomsNumber));
        floorNumber.setText(forFloorNumber);
        buildingName.setText(extra_building_name);

        checkIfThereIsNewRoom();

        // Listeners
        mainEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButtonStateInOnClick();
            }
        });

        addNewRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                Intent intent = new Intent(context, AddRoom.class);
                intent.putExtra("EXTRA_BUILDING_NAME", extra_building_name);
                intent.putExtra("EXTRA_FLOOR_NUMBER", extra_floor_number);
                intent.putExtra("EXTRA_NUMBER_OF_ROOMS", roomsNumber);
                context.startActivity(intent);
            }
        });
    }

    void editButtonStateInOnClick() {
        if (!isEditingEnabled) {
            moreEditButtons.setVisibility(View.VISIBLE);
            moreEditButtons.startAnimation(Animations.animate(-300, 0));
            mainEditButton.setText(getString(R.string.done));
            isEditingEnabled = true;
        } else {
            if (moreEditButtons.getVisibility() == View.VISIBLE) {
                moreEditButtons.startAnimation(Animations.animate(0, -300));
                moreEditButtons.setVisibility(View.INVISIBLE);
                mainEditButton.setText(getString(R.string.edit));
            }
            isEditingEnabled = false;
        }
    }

    void checkIfThereIsNewRoom() {
        String extra_new_room = getIntent().getStringExtra("EXTRA_NEW_ROOM");
        if (extra_new_room != null) {
            if (extra_new_room.equals("success")) {
                Snackbar.make(rView, "Dodano nowe pomieszczenie", Snackbar.LENGTH_LONG).show();
                isEditingEnabled = false;
                editButtonStateInOnClick();
            }
        }
    }

    private List<ItemObjectRoom> getAllItemList() {
        List<ItemObjectRoom> allItems = new ArrayList<>();
        final DbHandlerRooms dbHandlerRooms = new DbHandlerRooms(getApplicationContext());

        for (Rooms r : dbHandlerRooms.returnAllRooms(extra_building_name, extra_floor_number)) {
            switch (r.getTyp()) {
                case "Biuro":
                    allItems.add(new ItemObjectRoom(r.getNazwa(), r.getIleUrzadzen(), R.drawable.office));
                    break;
                case "Pokój rozmów":
                    allItems.add(new ItemObjectRoom(r.getNazwa(), r.getIleUrzadzen(), R.drawable.braingstorm));
                    break;
                case "Wypoczynek":
                    allItems.add(new ItemObjectRoom(r.getNazwa(), r.getIleUrzadzen(), R.drawable.break_area));
                    break;
                case "Szatnia":
                    allItems.add(new ItemObjectRoom(r.getNazwa(), r.getIleUrzadzen(), R.drawable.lockers));
                    break;
                case "Centrum spotkań":
                    allItems.add(new ItemObjectRoom(r.getNazwa(), r.getIleUrzadzen(), R.drawable.meeting_point));
                    break;
                case "Palarnia":
                    allItems.add(new ItemObjectRoom(r.getNazwa(), r.getIleUrzadzen(), R.drawable.smoking_area));
                    break;
                case "Schowek":
                    allItems.add(new ItemObjectRoom(r.getNazwa(), r.getIleUrzadzen(), R.drawable.storage));
                    break;
                case "Poczekalnia":
                    allItems.add(new ItemObjectRoom(r.getNazwa(), r.getIleUrzadzen(), R.drawable.waiting_area));
                    break;
            }
        }
        dbHandlerRooms.close();
        return allItems;
    }

    public void dataSetChanged() {
        rowListItem.clear();
        rowListItem.addAll(getAllItemList());
        int roomsNumber = rowListItem.size();
        String forFloorNumber = getString(R.string.floor) + extra_floor_number;

        rcAdapter.notifyDataSetChanged();

        numberOfRooms.setText(roomsNumber + " pomieszcze" + EndWordSolver.returnEnd(roomsNumber));
        floorNumber.setText(forFloorNumber);
        buildingName.setText(extra_building_name);

        final DbHandlerFloors dbHandlerFloors = new DbHandlerFloors(getApplicationContext());
        final DbHandlerDevices dbHandlerDevices = new DbHandlerDevices(getApplicationContext());
        int devicesCount = dbHandlerDevices.getFloorDevicesCount(extra_building_name, extra_floor_number);

        dbHandlerFloors.UpdateRoomsNumber(extra_building_name, extra_floor_number, roomsNumber);
        dbHandlerFloors.UpdateDevicesNumber(extra_building_name, extra_floor_number, devicesCount);

        dbHandlerFloors.close();
        dbHandlerDevices.close();

        floorDevicesNumber.setText(devicesCount + " urządze" + EndWordSolver.returnEnd(devicesCount));
    }

    public void blur() {
        backgroundBlur.blurBackground();
    }

    public void unblur() {
        backgroundBlur.unblurBackground();
    }

    private class SpaceItemDecoration extends RecyclerView.ItemDecoration {
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
