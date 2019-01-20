package realm;

import androidx.fragment.app.Fragment;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/*
* helper class for getting converstaions in order to avoid results getting garbage collected
* */
public class ConversationResults {

    private RealmResults<Conversation> conversations = null;
    private Realm realm = null;
    private Long topicId;
    private Long userId;
    private Conversation conv = null;

    public ConversationResults(Realm realm, Long topic, Long user){
        this.realm = realm;
        this.topicId = topic;
        this.userId = user;
    }

    RealmChangeListener callback = new RealmChangeListener<RealmResults<Conversation>>() {
        @Override
        public void onChange(RealmResults<Conversation> conv_results) {
            // called once the query complete and on every update
            // use the result
            for(Conversation c : conv_results){
                if(c.getRespondingUserId() == userId){
                    if(c != null){
                        setConversation(c);
                    }
                }
            }
        }
    };

    public void queryRealm(){
        conversations = realm.where(Conversation.class).equalTo("topicId", topicId).findAllAsync();
        conversations.addChangeListener(callback);
    }

    private void setConversation(Conversation c){
        this.conv = c;
    }

    public Conversation getConv(){
        return this.conv;
    }

    public void onStop () {
        conversations.removeChangeListener(callback); // remove a particular listener
    }

}
