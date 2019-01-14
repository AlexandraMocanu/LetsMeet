package realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Conversation extends RealmObject {

    @Required
    @PrimaryKey
    private String id;

    private Long topicId;

    private Long respondingUserId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Long getRespondingUserId() {
        return respondingUserId;
    }

    public void setRespondingUserId(Long respondingUserId) {
        this.respondingUserId = respondingUserId;
    }

}
