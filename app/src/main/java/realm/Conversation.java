package realm;

import java.util.Set;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Conversation extends RealmObject implements GetIdCompliant{

    @Required
    @PrimaryKey
    private Long id;

    @Required
    private Long topicId;

    private Long respondingUserId;

    private RealmList<Message> messages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public RealmList<Message> getMessages() {
        return messages;
    }

    public void setMessages(RealmList<Message> messages) {
        this.messages = messages;
    }
}
