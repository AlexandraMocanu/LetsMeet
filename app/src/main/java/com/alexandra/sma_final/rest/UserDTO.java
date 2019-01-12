package com.alexandra.sma_final.rest;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class UserDTO {

    @Required
    @PrimaryKey
    @SerializedName("id")
    private Long ID;

    @Required
    @SerializedName("login")
    private String username;

    @SerializedName("karma")
    private int karma;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

}
