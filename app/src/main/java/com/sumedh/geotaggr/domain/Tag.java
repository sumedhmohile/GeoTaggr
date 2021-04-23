package com.sumedh.geotaggr.domain;

import com.google.android.gms.maps.model.LatLng;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tag")
public class Tag {
    @PrimaryKey
    private Integer tagId;

    @ColumnInfo(name = "tagText")
    private String tagText;

    @ColumnInfo(name = "latitude")
    private Double latitude;

    @ColumnInfo(name = "longitude")
    private Double longitude;

    @ColumnInfo(name = "setByName")
    private String setByName;

    @ColumnInfo(name = "setById")
    private String setById;

    public Tag(Integer tagId, String tagText, Double latitude, Double longitude, String setByName, String setById) {
        this.tagId = tagId;
        this.tagText = tagText;
        this.latitude = latitude;
        this.longitude = longitude;
        this.setByName = setByName;
        this.setById = setById;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getTagText() {
        return tagText;
    }

    public void setTagText(String tagText) {
        this.tagText = tagText;
    }

    public String getSetByName() {
        return setByName;
    }

    public void setSetByName(String setByName) {
        this.setByName = setByName;
    }

    public String getSetById() {
        return setById;
    }

    public void setSetById(String setById) {
        this.setById = setById;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "<Tag: " + tagId + " | " + tagText + " | " + setByName + ">";
    }
}
