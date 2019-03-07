package mdoto.demoruntasticv2.StartUpModule;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.parse.*;
import mdoto.demoruntasticv2.R;
import mdoto.demoruntasticv2.ProfilesModule.UserProfile;

/**
 * Used to login user
 */
public class LoginActivity extends AppCompatActivity {
    private Boolean switchState=true;
    private Button signUpOrLogin;
    private TextView switchText;
    private EditText username,password;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Login");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            toolbar.setTextAlignment(Toolbar.TEXT_ALIGNMENT_CENTER);
            Log.i("28 SDK",":D");
        }
        setSupportActionBar(toolbar);

        if (ParseUser.getCurrentUser()!=null) {
            startActivity(new Intent(this, UserProfile.class));
            }
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        signUpOrLogin=findViewById(R.id.signuporlogin);
        switchText=findViewById(R.id.switchtext);
        signUpOrLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().length() < 1 || password.getText().length() < 1)

                {
                    Toast.makeText(getApplicationContext(),"Username or Password cannot be blank",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(switchState){
                    SignUp();
                    return;
                }
                Login();

            }
        });
        switchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchState){
                    switchState=false;
                    switchText.setText("signup");
                    signUpOrLogin.setText("Login");
                }
                else{
                    switchState=true;
                    switchText.setText("login");
                    signUpOrLogin.setText("SignUp");
                }
            }
        });

    }

    //Sign up user. Any exceptions handled by parse server
    private void SignUp(){
        ParseUser pu=new ParseUser();
        pu.setUsername(username.getText().toString());
        pu.setPassword(password.getText().toString());
        pu.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent =new Intent(getApplicationContext(), UserProfile.class);
                    startActivity(intent);
                }
            }
        });
    }
    //Login user.Any exceptions handled by user
    private void Login(){
        ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
             if(e!=null){
                 Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
             }
             else{
                 Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                 startActivity(intent);
             }
            }
        });
    }
}
