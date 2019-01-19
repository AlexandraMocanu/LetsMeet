package realm;

import java.util.Set;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Conversation extends RealmObject implements GetIdCompliant{

    @Required
    @PrimaryKey
    private Long id;

    @Required
    private Long topicId;

    @Required
    private Long respondingUserId;

    private Set<Message> messages;

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

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }
}
