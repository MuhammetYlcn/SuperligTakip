package com.grupdort.superligtakip.model;

public class LastUpdate {
    private String updated_key;
    private String updated_at;

    public LastUpdate(){}

    public LastUpdate(String updated_key,String updated_at){
        this.updated_key=updated_key;
        this.updated_at=updated_at;
    }

    public String getUpdated_key() {
        return updated_key;
    }

    public void setUpdated_key(String updated_key) {
        this.updated_key = updated_key;
    }

    public String getUpdated_at() {
        return updated_at;
    }
    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
    @Override
    public String toString() {
        return "LastUpdate{" +
                "updated_key='" + updated_key + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }
}
