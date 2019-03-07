package mdoto.demoruntasticv2.StartUpModule;


import android.app.Application;
import android.util.Log;
import com.parse.*;

import mdoto.demoruntasticv2.R;

/**
 * Initialization class for Parse Application.Involves connection to server with relevant keys
 *
 */
public class ParseInitializer extends Application {


   /* databaseURI: "mongodb://root:1LwKvecQza8m@127.0.0.1:27017/bitnami_parse",
    cloud: "./node_modules/parse-server/lib/cloud-code/Parse.Cloud.js",
    appId: "a540a76160974af3218f9d40ac6fce5b262acc33",
    masterKey: "eb9f52977683aae2218314417439494196175664",
    fileKey: "eaf364437f0dc5c53613602d530ca100173ec1cb",
    serverURL: "http://ec2-18-224-15-47.us-east-2.compute.amazonaws.com:80/parse",


*/

    @Override
    public void onCreate() {
        super.onCreate();
        //app password "1LwKvecQza8m"
        //app username "user"

        Parse.enableLocalDatastore(this);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.parse_app_id))

                .server(getString(R.string.parse_server_url))

                .clientKey(getString(R.string.parse_client_key))


                .build()
        );


        //forbidden user + sessiontoken
       //"bpy7fVBPiLQ9TA01tvPBGKvUv" "r:6610cfdebde55cf0f42b2c17b32189f7"



        //My mobile specific error :( so this invalid session token has ot be logged out
        //Error for anyone else who chooses this username :p
        if(ParseUser.getCurrentUser()!=null &&
                ParseUser.getCurrentUser().getUsername().equals("bpy7fVBPiLQ9TA01tvPBGKvUv"))
        { ParseUser.logOut();}

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
