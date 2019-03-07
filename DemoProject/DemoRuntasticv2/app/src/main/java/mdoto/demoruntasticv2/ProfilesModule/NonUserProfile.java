package mdoto.demoruntasticv2.ProfilesModule;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.*;
import mdoto.demoruntasticv2.ChatModule.ChatActivity;
import mdoto.demoruntasticv2.ConstantCollections;
import mdoto.demoruntasticv2.MenuBaseActivity;
import mdoto.demoruntasticv2.R;

/**
 * Non User profile class,involved in viewing other users profiles
 * Needs Intent data,non user ID, to function
 */
public class NonUserProfile extends MenuBaseActivity {
    private ImageView profile_pic_ImageView;
    private TextView username,status,location;
    private FloatingActionButton chat_fab;
    private ParseUser parseNonUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nonuser__profile);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        profile_pic_ImageView=findViewById(R.id.Profile_pic_IV);
        username= findViewById(R.id.nonuser_name);
        status =findViewById(R.id.nonuser_status);
        location=findViewById(R.id.nonuser_location);
        chat_fab=findViewById(R.id.Chat_FAB);
        getNonUser();
        SettingNonUserData();
        chat_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra(ConstantCollections.NonUser_Constants.ID,parseNonUser.getObjectId());
                startActivity(intent);
            }
        });
    }





    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Paused state","Should appear in logs on back press of the activity UserProfile");

    }


    // Get the other users data and also valdiate if it exists or not
    private void getNonUser(){
        ParseQuery<ParseUser> parseQuery =ParseUser.getQuery();
        try {
            parseNonUser = parseQuery.get(getIntent().getStringExtra(ConstantCollections.NonUser_Constants.ID));
        } catch (ParseException e) {
            if(e!=null)
                new AlertDialog.Builder(getApplicationContext()).setTitle("User not in database")
                        .setMessage("Retry searching for this user or Go back to your profile ?")
                        .setNegativeButton("My profile"
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(getApplicationContext(),UserProfile.class));
                                finish();
                            }
                        }).setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getNonUser();
                    }
                });
        }
    }

    // Set this non user's data in Layout
    private void SettingNonUserData(){

        ParseFile profile_pic_file=(ParseFile)parseNonUser.get(ConstantCollections.NonUser_Constants.profile_pic_key);
        byte[] profile_pic_bytes = null;
        if(profile_pic_file!=null) {
            try {
                profile_pic_bytes = profile_pic_file.getData();
            } catch (ParseException e) {
                e.printStackTrace();

            }
        }
        Bitmap profile_pic;
        if(profile_pic_bytes !=null) {
            profile_pic = BitmapFactory.decodeByteArray(profile_pic_bytes, 0, profile_pic_bytes.length);
            profile_pic_ImageView.setImageBitmap(profile_pic);
        }

        username.setText((String)parseNonUser.get(ConstantCollections.NonUser_Constants.name_key));
        status.setText((String)parseNonUser.get(ConstantCollections.NonUser_Constants.status_key));
        location.setText((String)parseNonUser.get(ConstantCollections.NonUser_Constants.location_key));
    }

}
