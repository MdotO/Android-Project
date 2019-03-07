package mdoto.demoruntasticv2.ChatModule;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import mdoto.demoruntasticv2.ConstantCollections;


import java.util.List;

/**
 * Custom Adapter class to display chat text views, also color them according to sender and receiver messages
 */
public class ChatListAdapter extends BaseAdapter {
    private Context context;
    private List<ParseObject> parseObjects;
    private String senderID;

    //Constructor which gets a reference of context and the objects to display
    public ChatListAdapter(Context context, List<ParseObject> parseObjects) {
        this.context=context;
        senderID = ParseUser.getCurrentUser().getObjectId();
        this.parseObjects=parseObjects;
    }

    @Override
    public int getCount() {
        return parseObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return parseObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Using view + viewmodel for better efficiency for displaying views on list view
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewModel viewModel;
        if (convertView == null)
        {   //Inflate resources if null
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1,null);
            viewModel = new ViewModel(convertView);
            convertView.setTag(viewModel);
        }
        else
        {
            viewModel =(ViewModel) convertView.getTag();
        }
        ParseObject parseObject =(ParseObject)getItem(position);
        //Set values
        Log.i("Chatlist adapter",parseObject.getString(ConstantCollections.Message.message_key)+" "+
                parseObject.getString(ConstantCollections.Message.senderID_key)+ " "+senderID);
        viewModel.setText(parseObject.getString(ConstantCollections.Message.message_key));
        if(parseObject.getString(ConstantCollections.Message.senderID_key).equals(
                senderID))
            viewModel.displayColor("s");
        else
            viewModel.displayColor("r");
        return convertView ;
    }


    //Custom ViewModel class
    public class ViewModel{
        //Declare some variables.TODO
        private String chatText;
        private TextView chatTextView;

        public ViewModel(View v) {
            chatTextView = (TextView)v;
        }

            public void setText(String newValue){
                chatText = new String(newValue);
                chatTextView.setText(chatText);
            }
            public void displayColor(String type) {
                if (type.equals("r"))
                    chatTextView.setBackgroundColor(Color.LTGRAY);
                else
                    chatTextView.setBackgroundColor(Color.WHITE);
            }


            public String getText(){
                return chatText;
            }
        }

    }

