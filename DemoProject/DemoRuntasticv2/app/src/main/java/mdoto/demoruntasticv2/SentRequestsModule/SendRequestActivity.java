package mdoto.demoruntasticv2.SentRequestsModule;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.parse.*;
import mdoto.demoruntasticv2.ConstantCollections;
import mdoto.demoruntasticv2.R;
import mdoto.demoruntasticv2.ReceivedRequestsModule.ReceiveRequestActivity;
import mdoto.demoruntasticv2.ProfilesModule.UserProfile;
import mdoto.demoruntasticv2.StartUpModule.LoginActivity;

import java.util.List;
/**
 * Activity responsible for viewing all requests sent by user
 */
public class SendRequestActivity extends FragmentActivity implements ListToContentSender {
    private ParseUser parseUser;
    private FrameLayout frameLayout;
    private FragmentTransaction fragmentTransaction;
    private SendRequestListFragment listViewFragment;
    private SendRequestContentFragment contentView;

    private List<ParseObject> requests;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragments_container);

        parseUser=ParseUser.getCurrentUser();
        requests=null;
        listViewFragment = new SendRequestListFragment();
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Request");
        parseQuery.whereEqualTo(ConstantCollections.Request.sender_key,parseUser.getObjectId());
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e!=null){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                requests=objects;
                listViewFragment.setupList(requests);
            }
        });
        frameLayout=findViewById(R.id.frame_layout_container);
        fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_layout_container, listViewFragment);
        fragmentTransaction.commit();
    }



    //Implementing this method to remove any sent request deleted in SendRequestContentFragment
    @Override
    public void remove(final int i) {
        requests.get(i).deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                requests.remove(i);
                listViewFragment.refreshAdapter();
            }
        });
    }


    //Implementing this method to display the detailed content of any sent request
    @Override
    public void display(int i) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(requests!=null){
            SendRequestContentFragment sendRequestContentFragment = new SendRequestContentFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Position",i);
            sendRequestContentFragment.setArguments(bundle);
            fragmentTransaction.add(R.id.frame_layout_container,sendRequestContentFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }

    }

    /**
     *
     * @param i position of parse object in the list
     * @return Parse object whose content will be displayed
     */
    public ParseObject getRequest(int i){
        return requests.get(i);
    }
}
