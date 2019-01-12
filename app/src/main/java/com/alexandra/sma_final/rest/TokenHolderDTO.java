package com.alexandra.sma_final.rest;

import com.google.gson.annotations.SerializedName;

public class TokenHolderDTO {

    @SerializedName("id_token")
    private String idToken;

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

}
