package realm;

import com.alexandra.sma_final.adapters.MessageListAdapter;
import com.alexandra.sma_final.rest.UserDTO;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ConversationHelper implements RealmChangeListener<Conversation> {

    private Conversation conversation;
    private MessageListAdapter adapter;

    public void queryRealm(UserDTO mCurrentUser, Long topicId) {
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final RealmResults<Conversation> convs  = realm.where(Conversation.class).equalTo("topicId", topicId).findAll();
                if(convs.first() != null){
                    setConversation(convs.first());
                }
            });
        }
        if(conversation != null){
            conversation.addChangeListener(this);
        }
    }

    public void setAdapter (MessageListAdapter ad){
        this.adapter = ad;
    }

    private void setConversation(Conversation c){
        this.conversation = c;
    }

    public Conversation getConversation(){
        return this.conversation;
    }

    @Override
    public void onChange(Conversation newConv) { // called once the query complete and on every update
        adapter.notifyDataSetChanged();
        adapter.notifyItemInserted(adapter.getItemCount());
    }
}
