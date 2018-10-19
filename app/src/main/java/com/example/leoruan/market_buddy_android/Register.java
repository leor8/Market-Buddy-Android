package com.example.leoruan.market_buddy_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends AppCompatActivity {
    TextView usrname, pwd, pwd_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usrname = findViewById(R.id.username_input);
        pwd = findViewById(R.id.password_input);
        pwd_confirm = findViewById(R.id.password_input_confirm);
    }

    public void login(View v) {
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }

    public void start_register(View v) {
        if(usrname.getText() != null && !usrname.getText().toString().isEmpty()){ // User name validation for better UI
            if(!pwd.getText().toString().isEmpty()){ // password validation for better UI
                if(pwd.getText().toString().equals(pwd_confirm.getText().toString())){ // All validations passed

                    // Getting shared preferences and edit it
                    SharedPreferences user_pref = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = user_pref.edit();

                    // Putting username and password to editor
                    editor.putString("USERNAME", usrname.getText().toString());
                    editor.putString("PASSWORD", pwd.getText().toString());

                    // Commit the changes to shared preferences
                    editor.commit();

                    // Start user profile activity
                    Intent i = new Intent(this, UserProfile.class);
                    startActivity(i);

                } else {
                    Toast.makeText(this, "Your passwords did not match, please try again", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You entered invalid password, please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "You entered invalid username, please try again", Toast.LENGTH_SHORT).show();
        }
    }

}
