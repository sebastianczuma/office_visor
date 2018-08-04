package com.example.sebastianczuma.officevisor.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArrayMap;
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
import com.example.sebastianczuma.officevisor.DataKeepers.Devices;
import com.example.sebastianczuma.officevisor.Database.DbHandlerDevices;
import com.example.sebastianczuma.officevisor.EndWordSolver;
import com.example.sebastianczuma.officevisor.LayoutClassesDevices.ItemObjectDevice;
import com.example.sebastianczuma.officevisor.LayoutClassesDevices.RecyclerViewAdapterDevice;
import com.example.sebastianczuma.officevisor.R;
import com.example.sebastianczuma.officevisor.WorkerClasses.DownloadDeviceDataForRoomActivity;

import java.util.ArrayList;
import java.util.List;

public class RoomActivity extends AppCompatActivity {
    public boolean isEditingEnabled = false;
    public ArrayMap<String, String> dataArray = new ArrayMap<>();
    public DbHandlerDevices dbHandlerDevices;
    BackgroundBlur backgroundBlur;
    TextView numberOfDevices;
    List<ItemObjectDevice> rowListItem;
    RecyclerViewAdapterDevice rcAdapter;
    RelativeLayout moreEditButtons;
    Button mainEditButton;
    String extra_building_name;
    String extra_floor_number;
    String extra_room_name;
    int extra_dev_number;
    RecyclerView rView;
    TextView roomName;
    RoomActivity ctx;
    ArrayList<String> arrayOfNames = new ArrayList<>();
    String token;
    View SCREEN;
    ImageView SCREEN_OVERLAYING_IMAGE;

    private void checkExtraStringsAtStart() {
        extra_room_name = getIntent().getStringExtra("EXTRA_ROOM_NAME");
        extra_building_name = getIntent().getStringExtra("EXTRA_BUILDING_NAME");
        extra_floor_number = getIntent().getStringExtra("EXTRA_FLOOR_NUMBER");

        if (extra_room_name != null) {
            roomName.setText(extra_room_name);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        // Transparent system navbar, statusbar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        ctx = this;

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = preferences.getString("token", "");

        // Declarations
        ImageButton addNewDevice = (ImageButton) findViewById(R.id.add_device);
        GridLayoutManager lLayout = new GridLayoutManager(RoomActivity.this, 3);
        mainEditButton = (Button) findViewById(R.id.edit_button);
        moreEditButtons = (RelativeLayout) findViewById(R.id.edit_buttons_container);
        numberOfDevices = (TextView) findViewById(R.id.number_of_devices);
        roomName = (TextView) findViewById(R.id.room_name);
        rView = (RecyclerView) findViewById(R.id.recycler_view);
        SCREEN = findViewById(R.id.activity_room_relative_layout);
        SCREEN_OVERLAYING_IMAGE = (ImageView) findViewById(R.id.image_for_blur);

        dbHandlerDevices = new DbHandlerDevices(getApplicationContext());

        // Checking what building, floor and room it is
        checkExtraStringsAtStart();

        // Recycle View setup
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
        rowListItem = getAllItemList();
        extra_dev_number = rowListItem.size();
        rcAdapter = new RecyclerViewAdapterDevice(rowListItem, this);
        rView.setAdapter(rcAdapter);
        SpaceItemDecoration dividerItemDecoration = new SpaceItemDecoration(20);
        rView.addItemDecoration(dividerItemDecoration);

        // Additional setup
        numberOfDevices.setText(extra_dev_number + " urządze" + EndWordSolver.returnEnd(extra_dev_number));
        backgroundBlur = new BackgroundBlur(SCREEN, SCREEN_OVERLAYING_IMAGE, this);

        checkIfThereIsNewDevice();

        // Listeners
        addNewDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddDevice.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXTRA_ROOM_NAME", extra_room_name);
                intent.putExtra("EXTRA_DEV_NUMBER", extra_dev_number);
                intent.putExtra("EXTRA_BUILDING_NAME", extra_building_name);
                intent.putExtra("EXTRA_FLOOR_NUMBER", extra_floor_number);
                startActivity(intent);
            }
        });

        mainEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButtonStateInOnClick();
            }
        });

        getNewDataFromServer();
    }

    void checkIfThereIsNewDevice() {
        String extra_new_device = getIntent().getStringExtra("EXTRA_NEW_DEVICE");
        if (extra_new_device != null) {
            if (extra_new_device.equals("success")) {
                Snackbar.make(rView, "Dodano nowy czujnik", Snackbar.LENGTH_LONG).show();
                isEditingEnabled = false;
                editButtonStateInOnClick();
            }
        }
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

    private List<ItemObjectDevice> getAllItemList() {
        List<ItemObjectDevice> allItems = new ArrayList<>();
        String deviceName;
        for (Devices r : dbHandlerDevices.returnAllDevices(extra_room_name, extra_floor_number, extra_building_name)) {
            deviceName = r.getNazwa();
            dataArray.put(deviceName, "");
            arrayOfNames.add(deviceName);
            switch (r.getTyp()) {
                case "Czujnik obecności":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), r.getNazwaBudynku(), r.getNumerPoziomu(), r.getPomieszczenie(), r.getUlubione(), R.drawable.proximity, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Czujnik temperatury":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), r.getNazwaBudynku(), r.getNumerPoziomu(), r.getPomieszczenie(), r.getUlubione(), R.drawable.temperature, dataArray.get(deviceName) + "\u00B0C", r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Czujnik dymu":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), r.getNazwaBudynku(), r.getNumerPoziomu(), r.getPomieszczenie(), r.getUlubione(), R.drawable.smoke, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Czujnik wilgotności":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), r.getNazwaBudynku(), r.getNumerPoziomu(), r.getPomieszczenie(), r.getUlubione(), R.drawable.humidity, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Oświetlenie":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), r.getNazwaBudynku(), r.getNumerPoziomu(), r.getPomieszczenie(), r.getUlubione(), R.drawable.light, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Zamek":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), r.getNazwaBudynku(), r.getNumerPoziomu(), r.getPomieszczenie(), r.getUlubione(), R.drawable.padlock, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Czujnik gazu":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), r.getNazwaBudynku(), r.getNumerPoziomu(), r.getPomieszczenie(), r.getUlubione(), R.drawable.smoke, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Czujnik wody":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), r.getNazwaBudynku(), r.getNumerPoziomu(), r.getPomieszczenie(), r.getUlubione(), R.drawable.smoke, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
            }
        }
        dbHandlerDevices.close();
        return allItems;
    }

    public void dataSetChanged() {
        rowListItem.clear();
        rowListItem.addAll(refreshRecycleViewAfterDataDownloaded());
        rcAdapter.notifyDataSetChanged();
    }

    public void getNewDataFromServer() {
        final Handler handler = new Handler();
        handler.removeCallbacksAndMessages(null);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < arrayOfNames.size(); i++) {
                    DownloadDeviceDataForRoomActivity dddfra =
                            new DownloadDeviceDataForRoomActivity
                                    (ctx, token, extra_building_name, extra_floor_number, extra_room_name,
                                            arrayOfNames.get(i));
                    dddfra.getData();
                }

                handler.postDelayed(this, 10000);
            }
        };
        handler.postDelayed(runnable, 0);
    }

    public List<ItemObjectDevice> refreshRecycleViewAfterDataDownloaded() {
        List<ItemObjectDevice> allItems = new ArrayList<>();
        String deviceName;
        final DbHandlerDevices dbHandlerDevices = new DbHandlerDevices(getApplicationContext());
        for (Devices r : dbHandlerDevices.returnAllDevices(extra_room_name, extra_floor_number, extra_building_name)) {
            deviceName = r.getNazwa();
            switch (r.getTyp()) {
                case "Czujnik obecności":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), r.getNazwaBudynku(), r.getNumerPoziomu(), r.getPomieszczenie(), r.getUlubione(), R.drawable.proximity, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Czujnik temperatury":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), r.getNazwaBudynku(), r.getNumerPoziomu(), r.getPomieszczenie(), r.getUlubione(), R.drawable.temperature, dataArray.get(deviceName) + "\u00B0C", r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Czujnik dymu":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), r.getNazwaBudynku(), r.getNumerPoziomu(), r.getPomieszczenie(), r.getUlubione(), R.drawable.smoke, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Czujnik wilgotności":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), r.getNazwaBudynku(), r.getNumerPoziomu(), r.getPomieszczenie(), r.getUlubione(), R.drawable.humidity, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Oświetlenie":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), r.getNazwaBudynku(), r.getNumerPoziomu(), r.getPomieszczenie(), r.getUlubione(), R.drawable.light, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Zamek":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), r.getNazwaBudynku(), r.getNumerPoziomu(), r.getPomieszczenie(), r.getUlubione(), R.drawable.padlock, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Czujnik gazu":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), r.getNazwaBudynku(), r.getNumerPoziomu(), r.getPomieszczenie(), r.getUlubione(), R.drawable.smoke, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Czujnik wody":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), r.getNazwaBudynku(), r.getNumerPoziomu(), r.getPomieszczenie(), r.getUlubione(), R.drawable.smoke, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
            }
        }
        dbHandlerDevices.close();
        return allItems;
    }

    public void blur() {
        backgroundBlur.blurBackground();
    }

    public void unblur() {
        backgroundBlur.unblurBackground();
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
