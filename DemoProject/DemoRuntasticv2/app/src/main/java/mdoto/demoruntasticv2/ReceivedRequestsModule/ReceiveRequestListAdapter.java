package mdoto.demoruntasticv2.ReceivedRequestsModule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.parse.ParseObject;
import mdoto.demoruntasticv2.ConstantCollections;
import mdoto.demoruntasticv2.R;

import java.util.List;

/**
 * Custom Adpater made for displaying received request lists for the user
 */
public class ReceiveRequestListAdapter extends BaseAdapter {
    private Context context;
    private List<ParseObject> parseObjects;
    public ReceiveRequestListAdapter(Context context, List<ParseObject> parseObjects) {
        this.context=context;
        this.parseObjects=parseObjects;
    }
    //Possible used in future
    public void addItem(ParseObject parseObject){
        this.parseObjects.add(parseObject);
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

    /**
     * ViewModel + view for better efficiency
     * @param position
     * @param convertView
     * @param parent
     * @return ItemView for each item in the adapter
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReceiveRequestListAdapter.ViewModel viewModel;
        if (convertView == null)
        {   //Inflate resources if null
            convertView = LayoutInflater.from(context).inflate(R.layout.receive_request_listitem,null);
            viewModel = new ReceiveRequestListAdapter.ViewModel(convertView);
            convertView.setTag(viewModel);
        }
        else
        {
            viewModel =(ReceiveRequestListAdapter.ViewModel) convertView.getTag();
        }
        ParseObject parseObject =(ParseObject)getItem(position);

        //Set values
        viewModel.setText(parseObject.getString(ConstantCollections.Request.title_key),
                parseObject.getString(ConstantCollections.Request.sender_key));


        return convertView ;
    }

    //Custom View model class for this adapter
    public class ViewModel{
        //Declared some variables
        private String titleText;
        private TextView titleTextView;
        private String sender;
        private TextView senderTextView;

        public ViewModel(View v) {
            titleTextView=v.findViewById(R.id.title_listitem);
            senderTextView=v.findViewById(R.id.from_userid_listitem);
        }

        public void setText(String titleText,String takenupby){
            this.titleText = new String(titleText);
            this.sender=new String(takenupby);
            titleTextView.setText(titleText);
            senderTextView.setText("From : "+ takenupby);
        }

        public String getTitleTextText(){
            return titleText;
        }
        public String getTakenupby(){ return sender;}
    }

}
