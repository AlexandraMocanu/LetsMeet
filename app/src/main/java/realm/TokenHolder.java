package realm;

import io.realm.RealmObject;

public class TokenHolder extends RealmObject {

    private String idToken;

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

}
