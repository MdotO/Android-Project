package mdoto.demoruntasticv2.ReceivedRequestsModule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.parse.ParseObject;
import com.parse.ParseUser;
import mdoto.demoruntasticv2.ProfilesModule.UserProfile;
import mdoto.demoruntasticv2.R;
import mdoto.demoruntasticv2.SentRequestsModule.SendRequestActivity;
import mdoto.demoruntasticv2.StartUpModule.LoginActivity;

import java.util.List;

/**
 * This fragment is responsible for displaying all the received requests.Not real time currently,Involves refreshing
 */
public class ReceiveRequestListFragment extends Fragment {
    private FragmentActivity hostActivity;
    private ListView receiveRequestListView;
    private View myView;
    private ReceiveRequestListAdapter receiveRequestListAdapter;

    public ReceiveRequestListFragment(){
        //Nothing to do
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view which displays the list of received requests
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            myView = inflater.inflate(R.layout.receive_request_list_fragmentcontent, null, false);
            receiveRequestListView = myView.findViewById(R.id.receive_request_listview);
            receiveRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ((ListToContentReceiver) hostActivity).display(position);
                }
            });
            ((Toolbar) myView.findViewById(R.id.toolbar)).inflateMenu(R.menu.test);
            ((Toolbar) myView.findViewById(R.id.toolbar)).setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.User_Profile: {
                            //start UserProfileActivity
                            Intent i = new Intent(hostActivity, UserProfile.class);
                            startActivity(i);
                            hostActivity.finish();
                            return true;
                        }
                        case R.id.Sent_Request: {
                            Intent i = new Intent(hostActivity, SendRequestActivity.class);
                            startActivity(i);
                            if(!(hostActivity.getApplicationContext() instanceof UserProfile))
                                hostActivity.finish();
                            return true;
                        }
                        case R.id.Received_Request: {
                            Intent i = new Intent(hostActivity, ReceiveRequestActivity.class);
                            startActivity(i);
                            if(!(hostActivity.getApplicationContext() instanceof UserProfile))
                                hostActivity.finish();
                            return true;
                        }
                        case R.id.logout: {
                            ParseUser.logOut();
                            Intent i = new Intent(hostActivity, LoginActivity.class);
                            startActivity(i);
                            if(!(hostActivity.getApplicationContext() instanceof UserProfile))
                                hostActivity.finish();
                            return true;
                        }
                        default: {
                            //do nothing
                            return true;
                        }
                    }
                }
            });

        setupList(((ReceiveRequestActivity)hostActivity).requests);
        return myView;

    }


    //get reference to host activity
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            hostActivity =(FragmentActivity)context;
        }
        if (hostActivity == null){
            hostActivity=getActivity();
        }
    }

    //Avoid memory leaks
    @Override
    public void onDetach() {
        super.onDetach();
        hostActivity=null;
        myView=null;
    }

    //Initialize the adpater and thus the listview
    public void setupList(List<ParseObject> parseObjects){
        receiveRequestListAdapter = new ReceiveRequestListAdapter(hostActivity,parseObjects);
        receiveRequestListView.setAdapter(receiveRequestListAdapter);
    }

}
