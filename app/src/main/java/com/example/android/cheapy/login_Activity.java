package com.example.android.cheapy;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class login_Activity extends AppCompatActivity {

    private EditText edt_username;
    private EditText edt_password;

    String username;
    String password;

    //database object
    sqlite_Handler sqliteHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //hide the keyboard in the starting of the activity
        hideSoftKeyboard();

        //set back button in actionbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //get data from both edit_text
        edt_username = findViewById(R.id.id_edit_text_username);
        edt_password = findViewById(R.id.id_edit_text_password);


        //set onClickListener to btn_login
        Button btn_login = findViewById(R.id.id_btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = edt_username.getText().toString();
                password = edt_password.getText().toString();
                sqliteHandler = new sqlite_Handler(login_Activity.this);
                //if username and password matches to our database than let the user login
                if(sqliteHandler.isDataExist(username,password)){
                    Log.d("login_Activity","yes, database exists");

                    //when a user get's logged in. means we have zero current user in database so add this user to database
                    sqliteHandler = new sqlite_Handler(login_Activity.this);
                    sqliteHandler.addCurrentUser(username);

                    Toast.makeText(getApplicationContext(),"logged in successfully",Toast.LENGTH_SHORT).show();
                    //now let's go to home activity
                    Intent intent = new Intent(login_Activity.this,Home.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "wrong username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    //if user hit create_account text then take hin to create_new_account Activity.
    public void goTo_create_new_account_activity(View view){
        Intent intent = new Intent(login_Activity.this,create_acc_Activity.class);
        startActivity(intent);
    }

    //method to hide keyboard
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    //remove login and create account options from navigation drawer if user is logged in



    //when user hit device's back then go to home activity....if you don't override this method then on pressing devices's back button,you will
    // find the navigation bar open in home_Activity because it's state was saved. we sre using intent in this method, so it will re-create home_Activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent intent = new Intent(this,Home.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
