package com.example.sebastianczuma.officevisor.Fragments;

/**
 * Created by sebastianczuma on 11/21/2015.
 */

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sebastianczuma.officevisor.Activities.MainActivity;
import com.example.sebastianczuma.officevisor.BackgroundBlur.BackgroundBlur;
import com.example.sebastianczuma.officevisor.DataKeepers.Devices;
import com.example.sebastianczuma.officevisor.Database.DbHandlerDevices;
import com.example.sebastianczuma.officevisor.EndWordSolver;
import com.example.sebastianczuma.officevisor.LayoutClassesDevices.ItemObjectDevice;
import com.example.sebastianczuma.officevisor.LayoutClassesDevices.RecyclerViewAdapterDevice;
import com.example.sebastianczuma.officevisor.R;
import com.example.sebastianczuma.officevisor.WorkerClasses.DownloadDeviceDataForFragmentFirst;

import java.util.ArrayList;
import java.util.List;

public class FragmentFirst extends Fragment {
    final Handler handler = new Handler();
    public MainActivity mainActivity;
    public ArrayMap<String, String> dataArray = new ArrayMap<>();
    BackgroundBlur backgroundBlur;
    RecyclerViewAdapterDevice rcAdapter;
    List<ItemObjectDevice> rowListItem;
    View SCREEN;
    ImageView SCREEN_OVERLAYING_IMAGE;
    ArrayList<String> arrayOfDevicesNames = new ArrayList<>();
    ArrayList<String> arrayOfBuildingsNames = new ArrayList<>();
    ArrayList<String> arrayOfFloorNumbers = new ArrayList<>();
    ArrayList<String> arrayOfRoomsNames = new ArrayList<>();
    String token;
    FragmentFirst ctx;
    Runnable runnable;

    public FragmentFirst() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        ctx = this;

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        token = preferences.getString("token", "");

        // Declarations
        GridLayoutManager lLayout = new GridLayoutManager(getActivity(), 3);
        TextView numberOfDevices = (TextView) view.findViewById(R.id.textView7);
        RecyclerView rView = (RecyclerView) view.findViewById(R.id.recycler_view);
        SCREEN = view.findViewById(R.id.activity_room_relative_layout);
        SCREEN_OVERLAYING_IMAGE = (ImageView) view.findViewById(R.id.image_for_blur);

        // Recycle View setup
        rowListItem = getAllItemList();
        rcAdapter = new RecyclerViewAdapterDevice(rowListItem, this);

        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
        rView.setAdapter(rcAdapter);
        SpaceItemDecoration dividerItemDecoration = new SpaceItemDecoration(20);
        rView.addItemDecoration(dividerItemDecoration);

        // Additional setup
        int devicesNumber = rowListItem.size();
        mainActivity = (MainActivity) getActivity();
        numberOfDevices.setText(devicesNumber
                + " element" + EndWordSolver.returnEnd2(devicesNumber));
        backgroundBlur = new BackgroundBlur(SCREEN, SCREEN_OVERLAYING_IMAGE, getContext());

        getNewDataFromServer();

        return view;
    }

    private List<ItemObjectDevice> getAllItemList() {
        List<ItemObjectDevice> allItems = new ArrayList<>();
        final DbHandlerDevices dbHandler = new DbHandlerDevices(getActivity());
        String deviceName;
        String buildingName;
        String floorNumber;
        String roomName;
        arrayOfDevicesNames.clear();
        arrayOfBuildingsNames.clear();
        arrayOfFloorNumbers.clear();
        arrayOfRoomsNames.clear();
        for (Devices r : dbHandler.returnAllFavouritesDevices()) {
            deviceName = r.getNazwa();
            buildingName = r.getNazwaBudynku();
            floorNumber = r.getNumerPoziomu();
            roomName = r.getPomieszczenie();
            arrayOfDevicesNames.add(deviceName);
            arrayOfBuildingsNames.add(buildingName);
            arrayOfFloorNumbers.add(floorNumber);
            arrayOfRoomsNames.add(roomName);

            dataArray.put(deviceName, "");

            switch (r.getTyp()) {
                case "Czujnik obecności":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), buildingName, floorNumber, roomName, r.getUlubione(), R.drawable.proximity, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Czujnik temperatury":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), buildingName, floorNumber, roomName, r.getUlubione(), R.drawable.temperature, dataArray.get(deviceName) + "\u00B0C", r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Czujnik dymu":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), buildingName, floorNumber, roomName, r.getUlubione(), R.drawable.smoke, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Czujnik wilgotności":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), buildingName, floorNumber, roomName, r.getUlubione(), R.drawable.humidity, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Oświetlenie":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), buildingName, floorNumber, roomName, r.getUlubione(), R.drawable.light, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Zamek":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), buildingName, floorNumber, roomName, r.getUlubione(), R.drawable.padlock, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Czujnik gazu":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), buildingName, floorNumber, roomName, r.getUlubione(), R.drawable.smoke, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
                case "Czujnik wody":
                    allItems.add(new ItemObjectDevice(deviceName, r.getTyp(), buildingName, floorNumber, roomName, r.getUlubione(), R.drawable.smoke, dataArray.get(deviceName), r.getPozycja(), r.getIsAlarmActive()));
                    break;
            }
            dbHandler.close();
        }
        return allItems;
    }

    public void dataSetChanged() {
        rowListItem.clear();
        rowListItem.addAll(refreshRecycleViewAfterDataDownloaded());
        rcAdapter.notifyDataSetChanged();
    }

    public void getNewDataFromServer() {
        handler.removeCallbacksAndMessages(null);
        runnable = new Runnable() {
            @Override
            public void run() {
                if (ctx.getContext().getCacheDir() != null) {
                    for (int i = 0; i < arrayOfDevicesNames.size(); i++) {
                        DownloadDeviceDataForFragmentFirst.getData(
                                ctx,
                                token,
                                arrayOfBuildingsNames.get(i),
                                arrayOfFloorNumbers.get(i),
                                arrayOfRoomsNames.get(i),
                                arrayOfDevicesNames.get(i));
                    }
                }
                handler.postDelayed(this, 10000);
            }
        };
        handler.postDelayed(runnable, 0);
    }

    private List<ItemObjectDevice> refreshRecycleViewAfterDataDownloaded() {
        List<ItemObjectDevice> allItems = new ArrayList<>();
        final DbHandlerDevices dbHandler = new DbHandlerDevices(getActivity());
        String deviceName;
        for (Devices r : dbHandler.returnAllFavouritesDevices()) {
            deviceName = r.getNazwa();
            Log.d("FF", dataArray.get(deviceName));
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
        dbHandler.close();
        return allItems;
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        private SpaceItemDecoration(int space) {
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
