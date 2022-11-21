package com.example.nymea_dashboard_v3;

import java.util.UUID;

public class State {
    public UUID typeId;
    public String name;
    public String displayName;
    public String value;
    public boolean update;

    public String getName() {
        return name;
    }

    public UUID getTypeId() {
        return typeId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue() {
        return value;
    }

    public boolean getUpdate(){return update; }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setTypeId(UUID typeId) {
        this.typeId = typeId;
    }

    public void setValue(String value) {
        this.value = value;
        this.update = true;
    }

    public void setUpdate(boolean value){this.update = value;}
}
