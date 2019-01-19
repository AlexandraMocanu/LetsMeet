package com.alexandra.sma_final.rest;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class UserDTO {

    @Required
    @PrimaryKey
    @SerializedName("id")
    private Long id;

    @Required
    @SerializedName("login")
    private String username;

    @SerializedName("karma")
    private int karma;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
