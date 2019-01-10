package realm;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class CurrentUserHolder extends RealmObject {

    @Required
    private Long ID;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }
}
