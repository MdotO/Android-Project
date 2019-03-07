package mdoto.demoruntasticv2.SentRequestsModule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.*;
import mdoto.demoruntasticv2.ConstantCollections;
import mdoto.demoruntasticv2.ProfilesModule.UserProfile;
import mdoto.demoruntasticv2.R;
import mdoto.demoruntasticv2.ReceivedRequestsModule.ReceiveRequestActivity;
import mdoto.demoruntasticv2.StartUpModule.LoginActivity;

import java.util.List;

/**
 * This fragment is responsible for displaying all the sent requests and also provides an
 * interface to create a new view
 */
public class SendRequestListFragment extends Fragment {
    private FragmentActivity hostActivity;
    private View myView;
    private ParseUser parseUser;
    private FloatingActionButton makeRequestFAB;
    private SendRequestListAdapter sendRequestListAdapter;
    private ListView requestListView;

    public SendRequestListFragment(){
        //Nothing to do
    }

    //Getting reference to hostactivity
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            hostActivity=(FragmentActivity) context;
        }
        if(hostActivity == null)
            hostActivity=getActivity();
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View which displays the list
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView=inflater.inflate(R.layout.send_request_list_fragmentcontent,container,false);
        makeRequestFAB = myView.findViewById(R.id.make_request_fab);
        parseUser=ParseUser.getCurrentUser();
        requestListView=myView.findViewById(R.id.send_request_listitem);
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
        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((ListToContentSender)hostActivity).display(position);
            }
        });
        makeRequestFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create a dialog which serves to get user input for making a new request
                final AlertDialog.Builder makeRequestAlertDialog = new AlertDialog.Builder(getActivity());
                View alertDialogBodyView = getLayoutInflater().inflate(R.layout.make_request,null);

                makeRequestAlertDialog.setTitle("Create a Request").setView(alertDialogBodyView).setPositiveButton
                        ("Create req", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (((EditText)((AlertDialog)dialog).findViewById(R.id.request_title)).getText().toString().equals("")||
                                        ((EditText)((AlertDialog)dialog).findViewById(R.id.request_description)).getText().toString().equals("")||
                                        ((EditText)((AlertDialog)dialog).findViewById(R.id.request_price)).getText().toString().equals("")||
                                        ((EditText)((AlertDialog)dialog).findViewById(R.id.request_date)).getText().toString().equals("")){
                                    Toast.makeText(hostActivity,
                                            "Title,Description,Reward and Date fields cannot be empty",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                final ParseObject request = new ParseObject("Request");
                                request.put(ConstantCollections.Request.sender_key,parseUser.getObjectId());
                                request.put(ConstantCollections.Request.title_key,((EditText)((AlertDialog)dialog).findViewById(R.id.request_title)).getText().toString());
                                request.put(ConstantCollections.Request.description_key,((EditText)((AlertDialog)dialog).findViewById(R.id.request_description)).getText().toString());
                                request.put(ConstantCollections.Request.date_key,((EditText)((AlertDialog)dialog).findViewById(R.id.request_date)).getText().toString());
                                request.put(ConstantCollections.Request.reward_key,((EditText)((AlertDialog)dialog).findViewById(R.id.request_price)).getText().toString());
                                request.put(ConstantCollections.Request.takenupby_key,"");
                                request.put(ConstantCollections.Request.location_key,((EditText)((AlertDialog)dialog).findViewById(R.id.request_location)).getText().toString());
                                request.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e!=null){
                                            Toast.makeText(hostActivity,e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(hostActivity,"Request Created",Toast.LENGTH_SHORT).show();

                                            hostActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    sendRequestListAdapter.addItem(request);
                                                    sendRequestListAdapter.notifyDataSetChanged();
                                                    requestListView.smoothScrollToPosition(0);
                                                }
                                            });
                                        }
                                    }
                                });

                                dialog.cancel();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
            }
        });



        return myView;
    }

    //Avoid Memory leaks
    @Override
    public void onDetach() {
        super.onDetach();
        myView=null;
        hostActivity=null;
    }
    //initialize adapter
    public void setupList(List<ParseObject> parseObjects){
        sendRequestListAdapter = new SendRequestListAdapter(getActivity(),parseObjects);
        requestListView.setAdapter(sendRequestListAdapter);
    }
    //Refresh adapter
    public void refreshAdapter(){
        if(sendRequestListAdapter!=null)
            sendRequestListAdapter.notifyDataSetChanged();
    }
}
