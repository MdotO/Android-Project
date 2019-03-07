package mdoto.demoruntasticv2.ReceivedRequestsModule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.parse.ParseObject;
import com.parse.ParseUser;
import mdoto.demoruntasticv2.ConstantCollections;
import mdoto.demoruntasticv2.ProfilesModule.NonUserProfile;
import mdoto.demoruntasticv2.ProfilesModule.UserProfile;
import mdoto.demoruntasticv2.R;
import mdoto.demoruntasticv2.SentRequestsModule.SendRequestActivity;
import mdoto.demoruntasticv2.StartUpModule.LoginActivity;

/**
 * This Fragment is responsible for displaying detailed received request info and also
 * as an entry port for other non user profiles and also taking up those requests too!
 */
public class ReceiveRequestContentFragment extends Fragment {
    private View myView;
    private FragmentActivity hostActivity;
    private int position;

    //get reference to hostactivity
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            hostActivity=(FragmentActivity)context;
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return The view/viewgroup/layout which is displayed on the fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        position = getArguments().getInt("Position");
        myView= inflater.inflate(R.layout.receive_request_detail_content,null,false);
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
        fillUpContent(((ReceiveRequestActivity)hostActivity).requests.get(position));
        return myView;
    }


    //nullify to avoid memory leaks
    @Override
    public void onDetach() {
        super.onDetach();
        hostActivity=null;
        myView=null;
    }

    //Fill up this fragment's layout views with content
    public void fillUpContent(final ParseObject parseObject){
        ((TextView)myView.findViewById(R.id.receive_request_senderID)).
                setText(parseObject.getString(ConstantCollections.Request.sender_key));
        ((TextView)myView.findViewById(R.id.receive_request_senderID)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(hostActivity, NonUserProfile.class);
                i.putExtra(ConstantCollections.NonUser_Constants.ID,parseObject.getString(ConstantCollections.Request.sender_key));
                startActivity(i);
                hostActivity.finish();
                return;
            }
        });
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
        Button takeup = myView.findViewById(R.id.take_up_request);
        if (!((TextView) myView.findViewById(R.id.request_taken_up_by)).getText().toString().equals("")){
            takeup.setEnabled(false);
            takeup.setAlpha(0.3f);
        }
        else{
            takeup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListToContentReceiver)hostActivity).takeup(position);
                    hostActivity.getSupportFragmentManager().popBackStackImmediate();
                }
            });
        }
    }
}
