package com.example.sebastianczuma.officevisor.Fragments;

/**
 * Created by sebastianczuma on 02.05.2016.
 */

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sebastianczuma.officevisor.DataKeepers.Floors;
import com.example.sebastianczuma.officevisor.Database.DbHandlerDevices;
import com.example.sebastianczuma.officevisor.Database.DbHandlerFloors;
import com.example.sebastianczuma.officevisor.Database.DbHandlerRooms;
import com.example.sebastianczuma.officevisor.EndWordSolver;
import com.example.sebastianczuma.officevisor.LayoutClassesFloors.ItemObjectFloor;
import com.example.sebastianczuma.officevisor.LayoutClassesFloors.RecyclerViewAdapterFloor;
import com.example.sebastianczuma.officevisor.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentSecond extends Fragment {
    DbHandlerRooms dbHandlerRooms;
    DbHandlerDevices dbHandlerDevices;
    private String thisBuildingName;

    public FragmentSecond() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            dbHandlerRooms.close();
            dbHandlerDevices.close();
        } catch (Exception e) {

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_second, container, false);

        // Checking for building name
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            thisBuildingName = bundle.getString("buildingName", "");
        }

        // Declarations
        dbHandlerRooms = new DbHandlerRooms(getActivity());
        dbHandlerDevices = new DbHandlerDevices(getActivity());
        int roomsNumber = dbHandlerRooms.getBuildingRoomsCount(thisBuildingName);
        int devicesNumber = dbHandlerDevices.getBuildingDevicesCount(thisBuildingName);

        TextView buildingName = (TextView) mView.findViewById(R.id.buildingName);
        TextView numberOfFloors = (TextView) mView.findViewById(R.id.numberOfFloors);
        TextView numberOfRooms = (TextView) mView.findViewById(R.id.numberOfRooms);
        TextView numberOfDevices = (TextView) mView.findViewById(R.id.numberOfDevices);

        GridLayoutManager lLayout = new GridLayoutManager(getActivity(), 1);
        RecyclerView rView = (RecyclerView) mView.findViewById(R.id.recycler_view);

        // Recycle View setup
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
        List<ItemObjectFloor> rowListItem = getAllItemList();
        RecyclerViewAdapterFloor rcAdapter = new RecyclerViewAdapterFloor(
                rowListItem, thisBuildingName);
        rView.setAdapter(rcAdapter);

        FragmentSecond.SpaceItemDecoration dividerItemDecoration
                = new FragmentSecond.SpaceItemDecoration(20);
        rView.addItemDecoration(dividerItemDecoration);

        // Additional setup
        int floorsNumber = rowListItem.size();

        // Setting text views data
        buildingName.setText(thisBuildingName);
        numberOfFloors.setText(floorsNumber + " poziom" + EndWordSolver.returnEnd2(floorsNumber));
        numberOfRooms.setText(roomsNumber + " pomieszcze" + EndWordSolver.returnEnd(roomsNumber));
        numberOfDevices.setText(devicesNumber + " urządze" + EndWordSolver.returnEnd(devicesNumber));

        return mView;
    }

    private List<ItemObjectFloor> getAllItemList() {
        List<ItemObjectFloor> allItems = new ArrayList<>();

        final DbHandlerFloors dbHandlerFloors = new DbHandlerFloors(getActivity());
        for (Floors r : dbHandlerFloors.returnAllFloors(thisBuildingName)) {
            allItems.add(new ItemObjectFloor(
                    r.getNumerPietra(), r.getIlePomieszczen(), r.getIleUrzadzen()));
        }
        dbHandlerFloors.close();
        return allItems;
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
