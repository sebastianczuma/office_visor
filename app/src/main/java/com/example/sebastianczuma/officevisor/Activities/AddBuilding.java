package com.example.sebastianczuma.officevisor.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.sebastianczuma.officevisor.BackgroundBlur.BackgroundBlur;
import com.example.sebastianczuma.officevisor.DataKeepers.Buildings;
import com.example.sebastianczuma.officevisor.Database.DbHandlerBuildings;
import com.example.sebastianczuma.officevisor.R;

import java.util.ArrayList;

public class AddBuilding extends AppCompatActivity {

    BackgroundBlur backgroundBlur;
    View SCREEN;
    ImageView SCREEN_OVERLAYING_IMAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_building);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        final ArrayList<String> names = new ArrayList<>();
        final Button add = (Button) findViewById(R.id.add_new_building);
        final DbHandlerBuildings dbHandlerBuildings = new DbHandlerBuildings(getApplicationContext());
        final EditText name = (EditText) findViewById(R.id.new_building_name);


        SCREEN = findViewById(R.id.activity_add_building_relative_layout);
        SCREEN_OVERLAYING_IMAGE = (ImageView) findViewById(R.id.image_for_blur);

        backgroundBlur = new BackgroundBlur(SCREEN, SCREEN_OVERLAYING_IMAGE, this);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Buildings k : dbHandlerBuildings.returnAllBuildings()) {
                    names.add(k.getNazwa());
                }
                if (names.contains(name.getText().toString())) {
                    nameExistDialog().show();
                } else {
                    Buildings building = new Buildings();
                    building.setNazwa(name.getText().toString());
                    dbHandlerBuildings.addBuilding(building);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXTRA_NEW_BUILDING", "success");
                    intent.putExtra("EXTRA_EDIT", "0");
                    startActivity(intent);
                }
                dbHandlerBuildings.close();
            }
        });
    }

    public Dialog nameExistDialog() {
        backgroundBlur.blurBackground();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Podana nazwa budynku już istnieje. Proszę, wybierz inną.")
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                backgroundBlur.unblurBackground();
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
