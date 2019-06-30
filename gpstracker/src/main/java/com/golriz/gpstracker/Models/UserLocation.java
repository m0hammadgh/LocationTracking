package com.golriz.gpstracker.Models;

import android.location.Location;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "userLocation")
public class UserLocation {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "location")
    private Location location;

    @ColumnInfo(name = "isSynced")
    private boolean isSynced;

    @ColumnInfo(name = "time")
    private Long time;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}