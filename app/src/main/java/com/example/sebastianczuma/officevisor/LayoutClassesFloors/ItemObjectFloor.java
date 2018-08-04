package com.example.sebastianczuma.officevisor.LayoutClassesFloors;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

public class ItemObjectFloor {
    private String floorNumber;
    private int roomsNumber;
    private int devicesNumber;

    public ItemObjectFloor(String floorNumber, int roomsNumber, int devicesNumber) {
        this.floorNumber = floorNumber;
        this.roomsNumber = roomsNumber;
        this.devicesNumber = devicesNumber;
    }

    int getRoomsNumber() {
        return roomsNumber;
    }

    public void setRoomsNumber(int roomsDevicesNumber) {
        this.roomsNumber = roomsDevicesNumber;
    }

    int getDevicesNumber() {
        return devicesNumber;
    }

    public void setDevicesNumber(int devicesNumber) {
        this.devicesNumber = devicesNumber;
    }

    String getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(String floorNumber) {
        this.floorNumber = floorNumber;
    }
}