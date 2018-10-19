package com.example.leoruan.market_buddy_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    TextView usrname, pwd;
    final static String DEFAULT = "not available";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usrname = findViewById(R.id.username_input_login);
        pwd = findViewById(R.id.password_input_login);

        try {
            SharedPreferences user_prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
            String username = user_prefs.getString("USERNAME", DEFAULT);
            if(!username.equals(DEFAULT)) {
                Intent i = new Intent(this, UserProfile.class);
                startActivity(i);
            }
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred while getting user info", Toast.LENGTH_SHORT).show();
        }
    }

    public void register(View v) {
        Intent i = new Intent(this, Register.class);
        startActivity(i);
    }

    public void start_login(View v) {
        String usr = usrname.getText().toString();
        String passw = pwd.getText().toString();

        if(!usr.isEmpty() && usr != null) {
            if(!passw.isEmpty() && passw != null) {
                // input validation Success

                // Credential validation
                SharedPreferences user_prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                String username = user_prefs.getString("USERNAME", DEFAULT);
                String password = user_prefs.getString("PASSWORD", DEFAULT);

                if(username.equals(usr) && password.equals(passw)) { // Credential validation success
                    Intent i = new Intent(this, UserProfile.class);
                    startActivity(i);
                } else {
                    Toast.makeText(this, "Your user name or password did not match our record, please try again or register for an account", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You entered invalid password, please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "You entered invalid username, please try again", Toast.LENGTH_SHORT).show();
        }
    }
}
