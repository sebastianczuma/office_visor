package com.example.sebastianczuma.officevisor.LayoutClassesDevices;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sebastianczuma.officevisor.Activities.DeviceInfo;
import com.example.sebastianczuma.officevisor.Activities.RoomActivity;
import com.example.sebastianczuma.officevisor.Database.DbHandlerDevices;
import com.example.sebastianczuma.officevisor.Database.DbHandlerFloors;
import com.example.sebastianczuma.officevisor.Database.DbHandlerRooms;
import com.example.sebastianczuma.officevisor.Fragments.FragmentFirst;
import com.example.sebastianczuma.officevisor.R;

class RecyclerViewHoldersDevice extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    TextView deviceName;
    TextView deviceType;
    ImageView roomIcon;
    ImageView favourite;
    TextView data;
    LinearLayout mainContainer;
    String nazwaPomieszczenia;
    String numerPoziomu;
    String nazwaBudynku;
    String ulubione;
    String pozycja;
    private Dialog dialog;
    private RoomActivity contextR;
    private FragmentFirst contextF;
    private boolean favouritesFragment;

    RecyclerViewHoldersDevice(View itemView, RoomActivity contextR) {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        this.contextR = contextR;

        deviceName = (TextView) itemView.findViewById(R.id.name);
        deviceType = (TextView) itemView.findViewById(R.id.devnumber);
        data = (TextView) itemView.findViewById(R.id.data);
        roomIcon = (ImageView) itemView.findViewById(R.id.grid_item_image);

        favourite = (ImageView) itemView.findViewById(R.id.imageView);

        mainContainer = (LinearLayout) itemView.findViewById(R.id.main_container);
    }

    RecyclerViewHoldersDevice(View itemView, FragmentFirst contextF) {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        this.contextF = contextF;

        deviceName = (TextView) itemView.findViewById(R.id.name);
        deviceType = (TextView) itemView.findViewById(R.id.devnumber);
        data = (TextView) itemView.findViewById(R.id.data);
        roomIcon = (ImageView) itemView.findViewById(R.id.grid_item_image);

        favourite = (ImageView) itemView.findViewById(R.id.imageView);

        mainContainer = (LinearLayout) itemView.findViewById(R.id.main_container);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), DeviceInfo.class);
        intent.putExtra("EXTRA_DEVICE_NAME", deviceName.getText().toString());
        intent.putExtra("EXTRA_ROOM_NAME", nazwaPomieszczenia);
        intent.putExtra("EXTRA_FLOOR_NUMBER", numerPoziomu);
        intent.putExtra("EXTRA_BUILDING_NAME", nazwaBudynku);
        intent.putExtra("EXTRA_DEVICE_POSITION", pozycja);
        intent.putExtra("EXTRA_DEVICE_TYPE", deviceType.getText().toString());
        intent.putExtra("EXTRA_DEVICE_FAVOURITE", ulubione);
        view.getContext().startActivity(intent);
    }

    @Override
    public boolean onLongClick(final View view) {
        if (contextF != null) {
            favouritesFragment = true;
            addOrRemoveFavourite(view, ulubione);
        } else {
            if (contextR.isEditingEnabled) {
                chooseItem(view);
            } else {
                addOrRemoveFavourite(view, ulubione);
            }
        }
        return false;
    }

    private void chooseItem(final View view) {
        if (contextR != null) {
            contextR.blur();
        }
        dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.add_to_favourites_or_remove);
        LinearLayout favourites = (LinearLayout) dialog.findViewById(R.id.add_to_favourites);
        LinearLayout remove = (LinearLayout) dialog.findViewById(R.id.remove);

        favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOrRemoveFavourite(view, ulubione);
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeRoom(view);
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (contextR != null) {
                    contextR.unblur();
                }
            }
        });
        dialog.show();
    }

    private void addOrRemoveFavourite(final View view, String what) {
        if (favouritesFragment) {
            contextF.mainActivity.blur();
        } else if (!contextR.isEditingEnabled) {
            contextR.blur();
        }

        final String devName = deviceName.getText().toString();

        AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).create();
        alertDialog.setTitle("Ulubione");
        if (what.equals("tak")) {
            alertDialog.setMessage("Usunąć " + devName + " z ulubionych?");
            what = "";
        } else {
            alertDialog.setMessage("Dodać " + devName + " do ulubionych?");
            what = "tak";
        }
        final String reallyWhat = what;
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final DbHandlerDevices dbHandlerDevices = new DbHandlerDevices(view.getContext());
                        dbHandlerDevices.addOrRemoveNewFavourite(nazwaBudynku, numerPoziomu, nazwaPomieszczenia, devName, reallyWhat);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        if (contextR != null) {
                            contextR.dataSetChanged();
                        } else {
                            contextF.dataSetChanged();
                        }
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
                if (favouritesFragment) {
                    contextF.mainActivity.unblur();
                } else if (!contextR.isEditingEnabled) {
                    contextR.unblur();
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        alertDialog.show();
    }

    private void removeRoom(final View view) {
        final String thisDevice = deviceName.getText().toString();
        AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).create();
        alertDialog.setTitle("Uwaga");
        alertDialog.setMessage("Usunąć " + thisDevice + " z listy pomieszczeń?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final DbHandlerFloors dbHandlerFloors = new DbHandlerFloors(view.getContext());
                        final DbHandlerRooms dbHandlerRooms = new DbHandlerRooms(view.getContext());
                        final DbHandlerDevices dbHandlerDevices = new DbHandlerDevices(view.getContext());

                        int floorDevNumber = dbHandlerFloors.returnDevicesNumber(nazwaBudynku, numerPoziomu);
                        dbHandlerFloors.UpdateDevicesNumber(nazwaBudynku, numerPoziomu, floorDevNumber - 1);

                        int roomDevNumber = dbHandlerRooms.returnDevicesNumber(nazwaBudynku, numerPoziomu, nazwaPomieszczenia);
                        dbHandlerRooms.UpdateDevicesNumber(nazwaPomieszczenia, roomDevNumber - 1, nazwaBudynku, numerPoziomu);

                        dbHandlerDevices.deleteOneDevice(nazwaBudynku, numerPoziomu, nazwaPomieszczenia, thisDevice);

                        dbHandlerFloors.close();
                        dbHandlerRooms.close();
                        dbHandlerDevices.close();

                        dialog.dismiss();

                        if (contextR != null) {
                            contextR.dataSetChanged();
                        }
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
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}