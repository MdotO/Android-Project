package mdoto.demoruntasticv2.SentRequestsModule;




import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.parse.ParseObject;
import com.parse.ParseUser;
import mdoto.demoruntasticv2.ConstantCollections;
import mdoto.demoruntasticv2.ProfilesModule.UserProfile;
import mdoto.demoruntasticv2.R;
import mdoto.demoruntasticv2.ReceivedRequestsModule.ReceiveRequestActivity;
import mdoto.demoruntasticv2.StartUpModule.LoginActivity;

/**
 * This Fragment is responsible for displaying detailed sent request info and also
 * gives an interface to delete them
 */
public class SendRequestContentFragment extends Fragment {
    private SendRequestActivity hostActivity;
    private View myView;
    private int position;

    public SendRequestContentFragment(){
        //Nothing to do
    }

    //getting reference to hostActivity
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(hostActivity==null)
            hostActivity= (SendRequestActivity) getActivity();
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view to be displayed by the fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        position = getArguments().getInt("Position");
        myView = inflater.inflate(R.layout.send_request_detail_content,container,false);
        ((Toolbar) myView.findViewById(R.id.toolbar)).inflateMenu(R.menu.test);
        ((Toolbar)myView.findViewById(R.id.toolbar)).setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.User_Profile: {
                        //start UserProfileActivity
                        Intent i= new Intent(hostActivity, UserProfile.class);
                        startActivity(i);
                        hostActivity.finish();
                        return true;
                    }
                    case R.id.Sent_Request :{
                        Intent i= new Intent(hostActivity,SendRequestActivity.class);
                        startActivity(i);
                        if(!(hostActivity.getApplicationContext() instanceof UserProfile))
                            hostActivity.finish();
                        return true;
                    }
                    case R.id.Received_Request :{
                        Intent i= new Intent(hostActivity, ReceiveRequestActivity.class);
                        startActivity(i);
                        if(!(hostActivity.getApplicationContext() instanceof UserProfile))
                            hostActivity.finish();
                        return true;}
                    case R.id.logout:{
                        ParseUser.logOut();
                        Intent i = new Intent(hostActivity, LoginActivity.class);
                        startActivity(i);
                        if(!(hostActivity.getApplicationContext() instanceof UserProfile))
                            hostActivity.finish();
                        return true;
                    }
                    default: {
                        //do nothing
                        return  true;
                    }
                }
            }
        });
        fillUpContent(hostActivity.getRequest(position));
        return myView;
    }
    // fill up the layout's child views with content from the received parseobject
    public void fillUpContent(ParseObject parseObject){

        ((TextView)myView.findViewById(R.id.request_title)).
                setText(parseObject.getString(ConstantCollections.Request.title_key));
        ((TextView)myView.findViewById(R.id.request_description)).
                setText(parseObject.getString(ConstantCollections.Request.description_key));
        ((TextView)myView.findViewById(R.id.request_location)).
                setText(parseObject.getString(ConstantCollections.Request.location_key));
        ((TextView)myView.findViewById(R.id.request_price)).
                setText(parseObject.getString(ConstantCollections.Request.reward_key));
        ((TextView)myView.findViewById(R.id.request_taken_up_by)).
                setText(parseObject.getString(ConstantCollections.Request.takenupby_key));
        ((TextView)myView.findViewById(R.id.request_date)).
                setText(parseObject.getString(ConstantCollections.Request.date_key));
        myView.findViewById(R.id.delete_request_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListToContentSender)hostActivity).remove(position);
                hostActivity.getSupportFragmentManager().popBackStackImmediate();
            }
        });
    }
    //Avoid memory leaks
    @Override
    public void onDetach() {
        super.onDetach();
        myView=null;
        hostActivity=null;
    }
}
