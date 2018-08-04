package com.example.sebastianczuma.officevisor.LayoutClassesRooms;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

public class ItemObjectRoom {

    private String name;
    private int devicesNumber;
    private int photo;

    public ItemObjectRoom(String name, int devicesNumber, int photo) {
        this.name = name;
        this.photo = photo;
        this.devicesNumber = devicesNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public int getDevicesNumber() {
        return devicesNumber;
    }

    public void setDevicesNumber(int devicesNumber) {
        this.devicesNumber = devicesNumber;
    }

}