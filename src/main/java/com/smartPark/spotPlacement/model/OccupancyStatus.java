package com.smartPark.spotPlacement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "Occupancy_Status")
public class OccupancyStatus {

    @Id

    //private int global_id;

    private long global_id;
    private String cam_id;


    private String status;

    private String type;

    private long date;

    public long getGlobal_id() {
        return global_id;
    }

    public void setGlobal_id(long global_id) {
        this.global_id = global_id;
    }

    public String getCam_id() {
        return cam_id;
    }

    public void setCam_id(String cam_id) {
        this.cam_id = cam_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public OccupancyStatus(long global_id, String cam_id, String status, String type, long date) {
        this.global_id = global_id;
        this.cam_id = cam_id;
        this.status = status;
        this.type = type;
        this.date = date;

    }
}


