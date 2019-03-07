package mdoto.demoruntasticv2.ReceivedRequestsModule;


import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.parse.*;
import mdoto.demoruntasticv2.ConstantCollections;
import mdoto.demoruntasticv2.R;


import java.util.List;

/**
 * Activity responsible for viewing any new requests
 */
public class ReceiveRequestActivity extends FragmentActivity implements ListToContentReceiver {
    private ParseUser parseUser;
    private FragmentTransaction fragmentTransaction;
    private ReceiveRequestListFragment receiveRequestListFragment;
    private ReceiveRequestContentFragment receiveRequestContentFragment;
    public List<ParseObject> requests;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragments_container);

        parseUser=ParseUser.getCurrentUser();
        receiveRequestListFragment = new ReceiveRequestListFragment();
        fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_layout_container,receiveRequestListFragment);
        ParseQuery.getQuery("Request").whereNotEqualTo(ConstantCollections.Request.sender_key,parseUser.getObjectId())
                .findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if(e!=null)
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        else{
                            requests=objects;
                            fragmentTransaction.commitNow();
                        }
                    }
                });

    }

    //Implementing display method of the interface which essentially pops up a new fragment
    //with detailed information about the said request
    @Override
    public void display(int i) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ReceiveRequestContentFragment receiveRequestContentFragment = new ReceiveRequestContentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Position",i);
        receiveRequestContentFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.frame_layout_container,receiveRequestContentFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    //User takes up a request in ReceiverRequestContentFragment, reflected here
    @Override
    public void takeup(int i) {
        ParseObject parseObject =requests.get(i);
        parseObject.put(ConstantCollections.Request.takenupby_key,parseUser.getObjectId());
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Successfully taken up",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
