package com.example.sebastianczuma.officevisor.LayoutClassesBuildings;

/**
 * Created by sebastianczuma on 26.11.2016.
 */

public class ItemObjectBuilding {
    private String name;
    private int floorsNumber;

    public ItemObjectBuilding(String name, int floorsNumber) {
        this.name = name;
        this.floorsNumber = floorsNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFloorsNumber() {
        return floorsNumber;
    }

    public void setFloorsNumber(int devicesNumber) {
        this.floorsNumber = floorsNumber;
    }

}