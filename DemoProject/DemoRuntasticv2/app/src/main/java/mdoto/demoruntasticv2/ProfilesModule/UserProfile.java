package mdoto.demoruntasticv2.ProfilesModule;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.core.app.ActivityCompat;
import com.parse.*;
import mdoto.demoruntasticv2.ConstantCollections;
import mdoto.demoruntasticv2.MenuBaseActivity;
import mdoto.demoruntasticv2.R;
import mdoto.demoruntasticv2.StartUpModule.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * User profile class for viewing the user associated with the current session on the device
 *
 */
public class UserProfile extends MenuBaseActivity {
    private ImageView profile_pic_ImageView;
    private Button add_profile_pic;
    private EditText username,status,location;
    private ParseUser parseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__profile);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        profile_pic_ImageView=findViewById(R.id.Profile_pic_IV);
        add_profile_pic=findViewById(R.id.add_profile_pic);
        username= findViewById(R.id.name);
        status =findViewById(R.id.status);
        location=findViewById(R.id.location);

        // Profile_pic_ImageView will be able to longclickable AND give a context menu,which has the
        //option of removing current profile photo
        registerForContextMenu(profile_pic_ImageView);

        add_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Choose Image from gallery after getting permission
                if (ActivityCompat.checkSelfPermission(UserProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(UserProfile.this,new String[]{Manifest.permission
                    .READ_EXTERNAL_STORAGE},ConstantCollections.Pick_Image);
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, ConstantCollections.Pick_Image);
                }
            }
        } );
        parseUser=ParseUser.getCurrentUser();

        SettingUserData();
    }


    //Save data on Pause only,not every time user changes data
    @Override
    protected void onPause() {
        super.onPause();
        if(ParseUser.getCurrentUser()!=null) {
            Log.i("Paused state", "Should appear in logs on back press of the activity UserProfile");
            parseUser.put(ConstantCollections.User_Constants.name_key, username.getText().toString());
            parseUser.put(ConstantCollections.User_Constants.status_key, status.getText().toString());
            parseUser.put(ConstantCollections.User_Constants.location_key, location.getText().toString());
            parseUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null)
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Setup the menu layout
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.remove_profilepic,menu);
    }


    //For compatible versions, also setup the item listener for the said context menu
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.remove_profilepic:
            {
                    if(parseUser.containsKey(ConstantCollections.User_Constants.profile_pic_key)) {
                        parseUser.remove(ConstantCollections.User_Constants.profile_pic_key);
                        Toast.makeText(getApplicationContext(),"Removing....." ,Toast.LENGTH_SHORT).show();
                        parseUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null)
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                else {
                                    profile_pic_ImageView.setImageDrawable(getDrawable(android.R.drawable.ic_menu_help));
                                }
                            }
                        });
                    }
               return super.onContextItemSelected(item);
            }
            default:
                return super.onContextItemSelected(item);
        }
    }

    //Set user data. Also verify if session token is valid or not
    private void SettingUserData(){
        if(parseUser == null)
        {
            Toast.makeText(this,"Not Logged on.Rerouting to Login Page",Toast.LENGTH_SHORT).show();
            Handler handler =new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            },ConstantCollections.DelayMillisLoginPage);
            while(true){

            }
        }

        //Converting byte array into bitmap image
        ParseFile profile_pic_file=(ParseFile)parseUser.get(ConstantCollections.User_Constants.profile_pic_key);
        byte[] profile_pic_bytes= null;
        try {
            if(profile_pic_file!=null)
            profile_pic_bytes= profile_pic_file.getData();
        } catch (ParseException e) {
            e.printStackTrace();

        }
        Bitmap profile_pic;
        if(profile_pic_bytes !=null) {
            profile_pic = BitmapFactory.decodeByteArray(profile_pic_bytes, 0, profile_pic_bytes.length);
            profile_pic_ImageView.setImageBitmap(profile_pic);
        }

        username.setText((String)parseUser.get(ConstantCollections.User_Constants.name_key));
        status.setText((String)parseUser.get(ConstantCollections.User_Constants.status_key));
        location.setText((String)parseUser.get(ConstantCollections.User_Constants.location_key));
    }













    //Activity result from picking image activity. Use Content resolver URI
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode== ConstantCollections.Pick_Image)
        {
            Uri Imagedata = data.getData();
            profile_pic_ImageView.setImageURI(Imagedata);
            //Stroing image,since it's large piece of data, now in parse ratehr than at Pause
            Bitmap storeImage = null;
            try {
                storeImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),Imagedata);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(storeImage==null){
                Toast.makeText(this,"Could not be stored online",Toast.LENGTH_SHORT).show();
            }
            else{
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                storeImage.compress(Bitmap.CompressFormat.JPEG,100,stream);
                ParseFile parseFile = new ParseFile(ConstantCollections.User_Constants.profile_pic_file_name_jpeg
                        ,stream.toByteArray());
                ParseUser parseUser= ParseUser.getCurrentUser();
                parseUser.put(ConstantCollections.User_Constants.profile_pic_key,parseFile);
                parseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e !=null)
                        {
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}
