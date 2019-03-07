package mdoto.demoruntasticv2.ChatModule;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import com.parse.*;
import mdoto.demoruntasticv2.ConstantCollections;
import mdoto.demoruntasticv2.MenuBaseActivity;
import mdoto.demoruntasticv2.R;
import mdoto.demoruntasticv2.StartUpModule.LoginActivity;

import java.util.*;

/**
 * Chat Activity which enables chatting ,via polling currently between users
 */
public class ChatActivity extends MenuBaseActivity {

    //Declaring private variables for the class
    private Button sendButton;
    private EditText chatText;
    private ListView chatMessagesListView;
    private ChatListAdapter chatMessagesAdapter;
    private List<ParseObject> chatmessageslist;
    private ParseUser parseUser;
    private Date latestRtoSDateMessage;
    private Handler handler;
    private Runnable updateMessages;

    //All these variables were supposed to be used but alas the Parse Server LiveQuery didn't work!
   // private ParseLiveQueryClient parseLiveQueryClient;
   // private ParseQuery<Message> parseLiveQueryMessages;
   // private SubscriptionHandling<Message> messageHandler;
    private Intent receivedIntent;
    private String receiverID;

    /**
     * This menthod instantiates the variables
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);
        handler=new Handler();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));


        //Ascertaining whether session is valid or not

        parseUser=ParseUser.getCurrentUser();
        if(parseUser == null){
            Toast.makeText(this,"No Session ID.Rerouting to Login page",Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            },ConstantCollections.DelayMillisLoginPage);
            while (true);
        }

        chatmessageslist = new ArrayList();

        receivedIntent=getIntent();
        receiverID=receivedIntent.getStringExtra(ConstantCollections.NonUser_Constants.ID);
        chatText=findViewById(R.id.messagetext);
        sendButton = findViewById(R.id.send_chat);
        latestRtoSDateMessage = new Date();
        latestRtoSDateMessage.setTime(1);
        Log.i("Date now",latestRtoSDateMessage.toString());
        sendButton.setEnabled(false);
        sendButton.setAlpha(0.3f);
        chatMessagesListView =findViewById(R.id.chat_listview);
        chatText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0)
                {sendButton.setEnabled(false);
                sendButton.setAlpha(0.3f); }
                else
                {sendButton.setEnabled(true);
                sendButton.setAlpha(1.0f);}
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendButton.setEnabled(false);
                Editable text = chatText.getText();
                if(text.length()!=0){
                    final ParseObject parseObject = new ParseObject("Message");
                    parseObject.put(ConstantCollections.Message.senderID_key,parseUser.getObjectId());
                    parseObject.put(ConstantCollections.Message.receiverID_key,receiverID);
                    parseObject.put(ConstantCollections.Message.message_key,text.toString());
                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e!=null)
                            {
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                            else {runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addToChatListView(parseObject);
                                    Log.i("ded",parseObject.toString());

                                }
                            });

                            }
                        }
                    });
                    chatText.setText("");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendButton.setEnabled(true);
                        }
                    },100);
                }
            }
        });
        //Retrieve all old messages
        setOldMessages();



    }
    //Setting up a periodwise polling for messages method since the
    //defunct LiveQuery doesnt work
    private void pollNewMessages(){
    ParseQuery<ParseObject> parseQueryNewMessages= ParseQuery.getQuery("Message");
    parseQueryNewMessages.whereEqualTo(ConstantCollections.Message.senderID_key,receiverID);
    parseQueryNewMessages.whereEqualTo(ConstantCollections.Message.receiverID_key,parseUser.getObjectId());
    parseQueryNewMessages.whereGreaterThan("createdAt",latestRtoSDateMessage);
    parseQueryNewMessages.findInBackground(new FindCallback<ParseObject>() {
        @Override
        public void done(final List<ParseObject> objects, ParseException e) {
            if(e!=null)
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            if(objects!=null && objects.size()>0){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatmessageslist.addAll(objects);
                        chatMessagesAdapter.notifyDataSetChanged();
                        chatMessagesListView.smoothScrollToPosition(chatmessageslist.size()-1);
                    }
                });
                latestRtoSDateMessage = objects.get(objects.size()-1).getCreatedAt();
                Log.i("New date",latestRtoSDateMessage.toString());
            }
            Log.i("Test date",":(");

        }
    });
    }
    private void setOldMessages(){
        ParseQuery parseQueryOldMessages_stor = ParseQuery.getQuery("Message");
        parseQueryOldMessages_stor.whereEqualTo(ConstantCollections.Message.receiverID_key,receiverID);
        parseQueryOldMessages_stor.whereEqualTo(ConstantCollections.Message.senderID_key,parseUser.getObjectId());
        ParseQuery parseQueryOldMessages_rtos = ParseQuery.getQuery("Message");
        parseQueryOldMessages_rtos.whereEqualTo(ConstantCollections.Message.senderID_key,receiverID);
        parseQueryOldMessages_rtos.whereEqualTo(ConstantCollections.Message.receiverID_key,parseUser.getObjectId());
        List<ParseQuery<ParseObject>> queries =new ArrayList<>();
        queries.add(parseQueryOldMessages_stor);
        queries.add(parseQueryOldMessages_rtos);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        //mainQuery.addAscendingOrder("createdAt");
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e!=null){
                    chatMessagesAdapter= new ChatListAdapter(getApplicationContext(),chatmessageslist);
                    chatMessagesListView.setAdapter(chatMessagesAdapter);

                    Toast.makeText(getApplicationContext(),e.getMessage()+" Cannot get old messages right now",
                            Toast.LENGTH_SHORT).show();
                    updateMessages= new Runnable() {
                        @Override
                        public void run() {
                            Log.i("Running","running");
                            pollNewMessages();
                            handler.postDelayed(this,1000);
                        }
                    };
                    handler.postDelayed(updateMessages,1000);
                }
                else{
                    chatmessageslist=objects;
                    for (int i=chatmessageslist.size()-1;i>-1;i--){
                        if (chatmessageslist.get(i).getString(ConstantCollections.Message.senderID_key).
                                equals(receiverID)){
                            latestRtoSDateMessage =chatmessageslist.get(i).getCreatedAt();
                            break;
                        }
                    }
                    chatMessagesAdapter = new ChatListAdapter(getApplicationContext(),chatmessageslist);
                    chatMessagesListView.setAdapter(chatMessagesAdapter);

                    updateMessages= new Runnable() {
                        @Override
                        public void run() {
                            Log.i("Running","running");
                            pollNewMessages();
                            handler.postDelayed(this,1000);
                        }
                    };
                    handler.postDelayed(updateMessages,1000);
                }
            }
        });


    }
    //Adds new message to listview
    private void addToChatListView(ParseObject chatmessage){
        Log.i("ded",chatmessage.toString());
        chatmessageslist.add(chatmessage);
        chatMessagesAdapter.notifyDataSetChanged();
        chatMessagesListView.smoothScrollToPosition(chatmessageslist.size()-1);
    }

    //remove callbakcs to rpevent any leaks
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateMessages);
    }
}
