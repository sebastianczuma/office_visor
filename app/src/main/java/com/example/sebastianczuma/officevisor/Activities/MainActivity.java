package com.example.sebastianczuma.officevisor.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.sebastianczuma.officevisor.Activities.LayoutAdapters.ViewPagerAdapter;
import com.example.sebastianczuma.officevisor.Animaitons.Animations;
import com.example.sebastianczuma.officevisor.BackgroundBlur.BackgroundBlur;
import com.example.sebastianczuma.officevisor.DataKeepers.Buildings;
import com.example.sebastianczuma.officevisor.DataKeepers.Floors;
import com.example.sebastianczuma.officevisor.Database.DbHandlerBuildings;
import com.example.sebastianczuma.officevisor.Database.DbHandlerDevices;
import com.example.sebastianczuma.officevisor.Database.DbHandlerFloors;
import com.example.sebastianczuma.officevisor.Database.DbHandlerRooms;
import com.example.sebastianczuma.officevisor.Fragments.FragmentFirst;
import com.example.sebastianczuma.officevisor.Fragments.FragmentSecond;
import com.example.sebastianczuma.officevisor.Fragments.FragmentThird;
import com.example.sebastianczuma.officevisor.NotificationUpload.UploadEventReceiver;
import com.example.sebastianczuma.officevisor.R;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    static int tabLayoutPosition;
    BackgroundBlur backgroundBlur;
    boolean isEditingEnabled = false;
    ViewPager mViewPager;
    Button mainEditButton;
    RelativeLayout moreEditButtons;
    int numberOfFragments;
    int oldTabLayoutPosition;
    int ran1 = 0;
    int ran2;
    Random randomGeneratorForBackgroundAnimaiton;
    View SCREEN;
    ImageView SCREEN_OVERLAYING_IMAGE;
    int tableOfDrawablesForBackgroundAnimaiton[] = {
            R.drawable.gradient_1, R.drawable.gradient_2,
            R.drawable.gradient_3, R.drawable.gradient_4,
            R.drawable.gradient_5, R.drawable.gradient_6,
            R.drawable.gradient_7, R.drawable.gradient_8,
            R.drawable.gradient_9, R.drawable.gradient_10,
            R.drawable.gradient_11, R.drawable.gradient_12};

    @Override
    protected void onRestart() {
        super.onRestart();
        setupViewPager(mViewPager);
        updateEditButtonsState();
    }

    void checkExtraStringsAtStart() {
        String extra_edit = getIntent().getStringExtra("EXTRA_EDIT");
        String extra_new_building = getIntent().getStringExtra("EXTRA_NEW_BUILDING");

        if (extra_edit != null) {
            if (extra_edit.equals("1")) {
                moreEditButtons.setVisibility(View.VISIBLE);
                mainEditButton.setText(getString(R.string.done));
                isEditingEnabled = true;
            }
        }
        if (extra_new_building != null) {
            if (extra_new_building.equals("success")) {
                mainEditButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Transparent system navbar, statusbar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        start();

        // Declarations
        ImageButton addNewFloor = (ImageButton) findViewById(R.id.add_floor);
        Button deleteBuilding = (Button) findViewById(R.id.delete_building);
        mainEditButton = (Button) findViewById(R.id.edit_button);
        moreEditButtons = (RelativeLayout) findViewById(R.id.edit_buttons_container);
        mViewPager = (ViewPager) findViewById(R.id.tabs_container);
        randomGeneratorForBackgroundAnimaiton = new Random();
        ImageButton removeFloor = (ImageButton) findViewById(R.id.remove_floor);
        SCREEN = findViewById(R.id.activity_main_relative_layout);
        SCREEN_OVERLAYING_IMAGE = (ImageView) findViewById(R.id.image_for_blur);
        // Contains list of names for buildings removal
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs_titles);

        // Additional setup
        backgroundBlur = new BackgroundBlur(SCREEN, SCREEN_OVERLAYING_IMAGE, this);

        // Set up the ViewPager with the sections adapter.
        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);

        // Checking extra strings from intent
        checkExtraStringsAtStart();

        checkIfActivityRecreatedAfterBuildingRemoval();

        // Listeners
        mViewPager.addOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        tabLayoutPosition = position;
                        updateEditButtonsState();
                        animateBackgroundOnSwipe();
                        oldTabLayoutPosition = position;
                    }
                }
        );

        deleteBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAlertDialogForBuildingRemoval();
            }
        });

        mainEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButtonStateInOnClick();
            }
        });

        addNewFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreateFragmentAfterFloorAdd();
            }
        });

        removeFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreateFragmentAfterFloorRemoval();
            }
        });
    }

    void setupViewPager(ViewPager mViewPager) {
        String buildingName;
        final DbHandlerBuildings dbHandlerBuildings = new DbHandlerBuildings(this);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentFirst(), getString(R.string.favourites));

        for (Buildings r : dbHandlerBuildings.returnAllBuildings()) {
            buildingName = r.getNazwa();

            Fragment fragment = new FragmentSecond();
            Bundle bundle = new Bundle();
            bundle.putString("buildingName", buildingName);
            fragment.setArguments(bundle);
            adapter.addFragment(fragment, buildingName);
        }

        dbHandlerBuildings.close();

        adapter.addFragment(new FragmentThird(), getString(R.string.add));

        numberOfFragments = adapter.getCount() - 1;

        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(tabLayoutPosition);
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        mViewPager.setCurrentItem(item, smoothScroll);
    }

    void updateEditButtonsState() {
        if (tabLayoutPosition == 0) {
            ran2 = 0;
            hideEditButtons();
        } else if (tabLayoutPosition == numberOfFragments) {
            ran2 = 2;
            hideEditButtons();
        } else {
            ran2 = randomGeneratorForBackgroundAnimaiton.nextInt(12);
            showEditButtons();
        }
    }

    void animateBackgroundOnSwipe() {
        if (oldTabLayoutPosition != tabLayoutPosition) {
            TransitionDrawable out = new TransitionDrawable(
                    new Drawable[]{
                            ResourcesCompat.getDrawable(getResources(), tableOfDrawablesForBackgroundAnimaiton[ran1], null),
                            ResourcesCompat.getDrawable(getResources(), tableOfDrawablesForBackgroundAnimaiton[ran2], null),
                    });
            SCREEN.setBackground(out);
            out.startTransition(700);
            ran1 = ran2;
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

    void hideEditButtons() {
        if (moreEditButtons.getVisibility() == View.VISIBLE) {
            moreEditButtons.startAnimation(Animations.animate(0, -300));
            moreEditButtons.setVisibility(View.INVISIBLE);
        }
        if (mainEditButton.getVisibility() == View.VISIBLE) {
            mainEditButton.startAnimation(Animations.animate(0, -300));
            mainEditButton.setVisibility(View.INVISIBLE);
        }
    }

    void showEditButtons() {
        if (mainEditButton.getVisibility() == View.INVISIBLE) {
            mainEditButton.startAnimation(Animations.animate(-300, 0));
            mainEditButton.setVisibility(View.VISIBLE);
        }
        if (moreEditButtons.getVisibility() == View.INVISIBLE && isEditingEnabled) {
            moreEditButtons.startAnimation(Animations.animate(-300, 0));
            moreEditButtons.setVisibility(View.VISIBLE);
        }
    }

    void recreateFragmentAfterFloorAdd() {
        String buildingName = mViewPager.getAdapter().getPageTitle(tabLayoutPosition).toString();

        final DbHandlerFloors dbHandlerFloors = new DbHandlerFloors(getApplicationContext());
        int numerPietra = dbHandlerFloors.returnAllFloors(buildingName).size();

        Floors floor = new Floors();
        floor.setNazwaBudynku(buildingName);
        floor.setNumerPietra(Integer.toString(numerPietra + 1));
        floor.setIlePomieszczen(0);
        floor.setIleUrzadzen(0);
        dbHandlerFloors.addFloor(floor);

        dbHandlerFloors.close();

        setupViewPager(mViewPager);
    }

    void recreateFragmentAfterFloorRemoval() {
        Context ctx = getApplicationContext();
        String buildingName = mViewPager.getAdapter().getPageTitle(tabLayoutPosition).toString();
        final DbHandlerFloors dbHandlerFloors = new DbHandlerFloors(ctx);
        final DbHandlerRooms dbHandlerRooms = new DbHandlerRooms(ctx);
        final DbHandlerDevices dbHandlerDevices = new DbHandlerDevices(ctx);

        String floorNumber = dbHandlerFloors.deleteOneFloor(buildingName);
        dbHandlerRooms.deleteFloorRooms(buildingName, floorNumber);
        dbHandlerDevices.deleteFloorDevices(buildingName, floorNumber);

        dbHandlerFloors.close();
        dbHandlerRooms.close();
        dbHandlerDevices.close();

        setupViewPager(mViewPager);
    }

    void createAlertDialogForBuildingRemoval() {
        backgroundBlur.blurBackground();

        final String buildingName = mViewPager.getAdapter().getPageTitle(tabLayoutPosition).toString();
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(getString(R.string.warning));
        alertDialog.setMessage(getString(R.string.question_delete_building) + " " + buildingName + "?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        recreateFragmentAfterBuildingRemoval(buildingName);
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                backgroundBlur.unblurBackground();
            }
        });
        alertDialog.show();
    }

    public void blur() {
        backgroundBlur.blurBackground();
    }

    public void unblur() {
        backgroundBlur.unblurBackground();
    }

    void recreateFragmentAfterBuildingRemoval(String buildingName) {
        Context ctx = getApplicationContext();
        final DbHandlerBuildings dbHandlerBuildings = new DbHandlerBuildings(ctx);
        final DbHandlerFloors dbHandlerFloors = new DbHandlerFloors(ctx);
        final DbHandlerRooms dbHandlerRooms = new DbHandlerRooms(ctx);
        final DbHandlerDevices dbHandlerDevices = new DbHandlerDevices(ctx);

        dbHandlerBuildings.deleteOneBuilding(buildingName);
        dbHandlerFloors.deleteBuildingFloors(buildingName);
        dbHandlerRooms.deleteBuildingRooms(buildingName);
        dbHandlerDevices.deleteBuildingDevices(buildingName);

        dbHandlerBuildings.close();
        dbHandlerFloors.close();
        dbHandlerRooms.close();
        dbHandlerDevices.close();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXTRA_EDIT", "0");
        startActivity(intent);
    }

    void checkIfActivityRecreatedAfterBuildingRemoval() {
        if (tabLayoutPosition == 0 || tabLayoutPosition == numberOfFragments) {
            mainEditButton.setVisibility(View.INVISIBLE);
        } else {
            mainEditButton.setVisibility(View.VISIBLE);
        }
    }

    public void start() {
        UploadEventReceiver.setupAlarm(getApplicationContext());
    }
}
