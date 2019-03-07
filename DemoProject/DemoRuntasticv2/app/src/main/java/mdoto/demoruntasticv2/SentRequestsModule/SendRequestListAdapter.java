package mdoto.demoruntasticv2.SentRequestsModule;

import android.content.Context;

import android.util.Log;
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
 * Custom Adapter for displaying sent requests
 */
public class SendRequestListAdapter extends BaseAdapter {
    private Context context;
    private List<ParseObject> parseObjects;
    public SendRequestListAdapter(Context context, List<ParseObject> parseObjects) {
        this.context=context;
        this.parseObjects=parseObjects;
    }

    public void addItem(ParseObject parseObject){
        this.parseObjects.add(parseObject);
        Log.i("SELAdapter","added item "+ parseObject.toString()+ " "+getCount());
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
     * View model + view for better efficiency
     * @param position
     * @param convertView
     * @param parent
     * @return View of a single sent request item
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewModel viewModel;
        if (convertView == null)
        {   //Inflate resources if null
            convertView = LayoutInflater.from( context).inflate(R.layout.send_request_list_item,null,false);
            viewModel = new ViewModel(convertView);
            convertView.setTag(viewModel);
        }
        else
        {
            viewModel =(ViewModel) convertView.getTag();
        }
        ParseObject parseObject =(ParseObject)getItem(position);

        //Set values
        viewModel.setText(parseObject.getString(ConstantCollections.Request.title_key),
                parseObject.getString(ConstantCollections.Request.takenupby_key));

        return convertView ;
    }

    //Custom View model class
    public class ViewModel{

        private String titleText;
        private TextView titleTextView;
        private String takenupby;
        private TextView takenupbyTextView;

        public ViewModel(View v) {
            titleTextView=v.findViewById(R.id.title_listitem);
            takenupbyTextView=v.findViewById(R.id.takenupby_userid_listitem);
        }

        public void setText(String titleText,String takenupby){
            this.titleText = new String(titleText);
            this.takenupby=new String(takenupby);
            titleTextView.setText(titleText);
            takenupbyTextView.setText(takenupby);
        }


    }

}
