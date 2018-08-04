package com.example.sebastianczuma.officevisor.Activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.sebastianczuma.officevisor.Database.DbHandlerDevices;
import com.example.sebastianczuma.officevisor.DevicePositionInDeviceInfo.ItemObjectDevicePosition;
import com.example.sebastianczuma.officevisor.DevicePositionInDeviceInfo.RecyclerViewAdapterDevicePosition;
import com.example.sebastianczuma.officevisor.R;
import com.example.sebastianczuma.officevisor.WorkerClasses.DownloadDeviceDataForGraph;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class DeviceInfo extends AppCompatActivity {
    public ArrayList<String> date = new ArrayList<>();
    public ArrayList<String> data = new ArrayList<>();
    public TextView device_data;
    public int graphSize;
    String extra_building_name;
    String extra_floor_number;
    String extra_room_name;
    String extra_device_name;
    String extra_device_position;
    String extra_device_type;
    String extra_device_favourite;
    RecyclerViewAdapterDevicePosition rcAdapter;
    RecyclerView rView;
    List<ItemObjectDevicePosition> rowListItem;
    GraphView graph;
    DbHandlerDevices dbhd;
    TextView minValue;
    TextView maxValue;

    private void checkExtraStringsAtStart() {
        extra_device_name = getIntent().getStringExtra("EXTRA_DEVICE_NAME");
        extra_room_name = getIntent().getStringExtra("EXTRA_ROOM_NAME");
        extra_building_name = getIntent().getStringExtra("EXTRA_BUILDING_NAME");
        extra_floor_number = getIntent().getStringExtra("EXTRA_FLOOR_NUMBER");
        extra_device_position = getIntent().getStringExtra("EXTRA_DEVICE_POSITION");
        extra_device_type = getIntent().getStringExtra("EXTRA_DEVICE_TYPE");
        extra_device_favourite = getIntent().getStringExtra("EXTRA_DEVICE_FAVOURITE");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        dbhd = new DbHandlerDevices(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);


        checkExtraStringsAtStart();

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = preferences.getString("token", "");

        device_data = (TextView) findViewById(R.id.device_data);

        DownloadDeviceDataForGraph d = new DownloadDeviceDataForGraph(this, token, extra_building_name, extra_floor_number, extra_room_name, extra_device_name);
        d.getData();


        Log.d("ATG", extra_building_name + " " + extra_floor_number + " " + extra_room_name + " " + extra_device_name + " " + extra_device_position);

        TextView device_name_title = (TextView) findViewById(R.id.device_name_title);
        TextView device_localisation_building = (TextView) findViewById(R.id.device_localisation_building);
        TextView device_localisation_floor = (TextView) findViewById(R.id.device_localisation_floor);
        TextView device_localisation_room = (TextView) findViewById(R.id.device_localisation_room);

        TextView building = (TextView) findViewById(R.id.building);
        TextView floor = (TextView) findViewById(R.id.floor);
        TextView room = (TextView) findViewById(R.id.room);

        TextView device_name = (TextView) findViewById(R.id.device_name);
        TextView device_type = (TextView) findViewById(R.id.device_type);
        TextView ulubione = (TextView) findViewById(R.id.ulubione);


        device_name_title.setText(extra_device_name);
        device_localisation_building.setText(extra_building_name);
        device_localisation_floor.setText("Poziom " + extra_floor_number);
        device_localisation_room.setText(extra_room_name);

        building.setText(extra_building_name);
        floor.setText("Poziom " + extra_floor_number);
        room.setText(extra_room_name);

        device_name.setText(extra_device_name);
        device_type.setText(extra_device_type);
        if (extra_device_favourite.equals("")) {
            ulubione.setText("Ulubione: nie");
        } else {
            ulubione.setText("Ulubione: " + extra_device_favourite);
        }
        rView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager lLayout = new GridLayoutManager(this, 7);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
        rowListItem = getAllItemList();
        rcAdapter = new RecyclerViewAdapterDevicePosition(rowListItem, this, extra_device_position);
        rView.setAdapter(rcAdapter);
        DeviceInfo.SpaceItemDecoration dividerItemDecoration = new DeviceInfo.SpaceItemDecoration(10);
        rView.addItemDecoration(dividerItemDecoration);


        //final DbHandlerDevices dbHandlerDevices = new DbHandlerDevices(getApplicationContext());
//        device_data.setText(dbHandlerDevices.returnOneDeviceData(extra_building_name, extra_floor_number, extra_room_name, extra_device_name));


        graph = (GraphView) findViewById(R.id.graph);

        d.getData();


        Button minButton = (Button) findViewById(R.id.minTempButton);
        Button maxButton = (Button) findViewById(R.id.maxTempButton);

        minButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildAlertDialogForAlarmConfiguration(true);
            }
        });

        maxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildAlertDialogForAlarmConfiguration(false);
            }
        });

        minValue = (TextView) findViewById(R.id.minValue);
        maxValue = (TextView) findViewById(R.id.maxValue);

        minValue.setText("Wartość minimalna: " + dbhd.returnDeviceAlarmMinValue(extra_building_name, extra_floor_number, extra_room_name, extra_device_name));
        maxValue.setText("Wartość maksymalna: " + dbhd.returnDeviceAlarmMaxValue(extra_building_name, extra_floor_number, extra_room_name, extra_device_name));

        final Switch alarmOnOff = (Switch) findViewById(R.id.switch1);

        String alarm = dbhd.returnDeviceAlarm(extra_building_name, extra_floor_number, extra_room_name, extra_device_name);
        if (alarm != null) {
            if (alarm.equals("włączony")) {
                alarmOnOff.setChecked(true);
                alarmOnOff.setText("Alarm jest " + alarm);
            } else {
                alarmOnOff.setText("Alarm jest " + alarm);
            }
        }

        alarmOnOff.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    alarmOnOff.setText("Alarm jest włączony");
                    dbhd.updateDeviceAlarm(extra_building_name, extra_floor_number, extra_room_name, extra_device_name, "włączony");
                } else {
                    alarmOnOff.setText("Alarm jest wyłączony");
                    dbhd.updateDeviceAlarm(extra_building_name, extra_floor_number, extra_room_name, extra_device_name, "wyłączony");
                }

            }
        });
    }

    public void buildAlertDialogForAlarmConfiguration(final boolean isValueMin) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alarm_alert_dialog, null);
        if (isValueMin) {
            alertDialogBuilder.setTitle("Ustawianie minimalnej wartości");
        } else {
            alertDialogBuilder.setTitle("Ustawianie maksymalnej wartości");
        }
        alertDialogBuilder.setMessage("Wybierz liczbę");
        alertDialogBuilder.setView(dialogView);

        final EditText numberPicker = (EditText) dialogView.findViewById(R.id.numberPicker);

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int currentValue = Integer.parseInt(numberPicker.getText().toString());
                if (isValueMin) {
                    int maxValueInDB = dbhd.returnDeviceAlarmMaxValue(extra_building_name, extra_floor_number, extra_room_name, extra_device_name);
                    if (currentValue < maxValueInDB) {
                        dbhd.updateDeviceAlarmMinValue(extra_building_name, extra_floor_number, extra_room_name, extra_device_name, currentValue);
                        minValue.setText("Wartość minimalna: " + currentValue);
                    }
                } else {
                    int minValueInDB = dbhd.returnDeviceAlarmMinValue(extra_building_name, extra_floor_number, extra_room_name, extra_device_name);
                    if (currentValue > minValueInDB) {
                        dbhd.updateDeviceAlarmMaxValue(extra_building_name, extra_floor_number, extra_room_name, extra_device_name, currentValue);
                        maxValue.setText("Wartość maksymalna: " + currentValue);
                    }
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void drawGraph() {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(generateData());


        series.setColor(getResources().getColor(R.color.graphSeries));

        series.setThickness(4);
        graph.addSeries(series);
        //graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);

        graph.getViewport().setBackgroundColor(getResources().getColor(R.color.graphBackground));
        graph.getGridLabelRenderer().setGridColor(getResources().getColor(R.color.graphLines));

        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setXAxisBoundsManual(true);

        graph.getViewport().scrollToEnd();

    }

    private List<ItemObjectDevicePosition> getAllItemList() {
        List<ItemObjectDevicePosition> allItems = new ArrayList<>();

        for (int i = 1; i <= 42; i++) {
            allItems.add(new ItemObjectDevicePosition(i + ""));
        }

        return allItems;
    }

    private DataPoint[] generateData() {
        //SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        DataPoint[] values = new DataPoint[graphSize];
        for (int i = 0; i < graphSize; i++) {
            try {
                Log.d("data dcc", date.get(i));
                Log.d("dane dcc", data.get(i));
                Double x = (double) i;
                Double y = Double.valueOf(data.get(i));
                DataPoint v = new DataPoint(x, y);
                values[i] = v;
            } catch (Exception e) {
                Log.d("data dcc", "problem w parse");
            }
        }
        return values;
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
