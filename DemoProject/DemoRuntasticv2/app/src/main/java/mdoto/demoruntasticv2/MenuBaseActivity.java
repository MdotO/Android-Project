package mdoto.demoruntasticv2;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import com.parse.ParseUser;
import mdoto.demoruntasticv2.ReceivedRequestsModule.ReceiveRequestActivity;
import mdoto.demoruntasticv2.SentRequestsModule.SendRequestActivity;
import mdoto.demoruntasticv2.ProfilesModule.UserProfile;
import mdoto.demoruntasticv2.StartUpModule.LoginActivity;

/**
 * Base class which simply intializes Options Menu for classes that needs it.No need to duplicate or make
 * Static methods for this purpose
 */
public class MenuBaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.test,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.User_Profile: {
                //start UserProfileActivity
                Intent i= new Intent(getApplicationContext(), UserProfile.class);
                startActivity(i);
                finish();
                return true;
            }
            case R.id.Sent_Request :{
                Intent i= new Intent(getApplicationContext(),SendRequestActivity.class);
                startActivity(i);
                if(!(this instanceof UserProfile))
                finish();
                return true;
            }
            case R.id.Received_Request :{
                Intent i= new Intent(getApplicationContext(), ReceiveRequestActivity.class);
                startActivity(i);
                if(!(this instanceof UserProfile))
                    finish();
            return true;}
            case R.id.logout:{
                ParseUser.logOut();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                if(!(this instanceof UserProfile))
                    finish();
                return  true;
            }
            default: {
                //do nothing
            return super.onOptionsItemSelected(item);
            }
        }
    }


}
