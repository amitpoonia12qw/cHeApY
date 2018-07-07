package com.example.android.cheapy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

public class profile_Activity extends AppCompatActivity {


    sqlite_Handler sqliteHandler;
    store_details storeDetails;
    TextView txt_userName, txt_email, txt_mobNo, txt_storeName, txt_Address, txt_edit_profile, txt_change_password, txt_delete_account;

    EditText edit_email, edit_mobile_no, edit_store_name;
    TextView edit_store_address;

    Button btn_logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //set back button in actionbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        sqliteHandler = new sqlite_Handler(this);
        String currentUsername = sqliteHandler.getCurrentUser();
        storeDetails = sqliteHandler.getStoreDetailsForUsername(currentUsername);

        txt_userName = findViewById(R.id.id_txt_username);
        txt_email = findViewById(R.id.id_txt_email);
        txt_mobNo = findViewById(R.id.id_txt_mobileNo);
        txt_storeName = findViewById(R.id.id_txt_storeName);
        txt_Address = findViewById(R.id.id_txt_storeAddress);
        txt_edit_profile = findViewById(R.id.id_txt_edit_profile);
        txt_change_password = findViewById(R.id.id_txt_change_password);
        txt_delete_account = findViewById(R.id.id_txt_delete_account);
        edit_email = findViewById(R.id.id_edit_email);
        edit_mobile_no = findViewById(R.id.id_edit_mobile_no);
        edit_store_name = findViewById(R.id.id_edit_store_name);
        edit_store_address = findViewById(R.id.id_edit_store_address);
        btn_logout = findViewById(R.id.id_btn_logout);

        txt_userName.setText(storeDetails.getManagerName());
        txt_Address.setText(storeDetails.getStoreAddress());
        txt_mobNo.setText(storeDetails.getMobileNo());
        txt_email.setText(storeDetails.getEmail());
        txt_storeName.setText(storeDetails.getStoreName());

        txt_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_edit_profile.getText().toString().matches("Edit Profile")) {

                    txt_edit_profile.setText(R.string.save);

                    edit_email.setVisibility(View.VISIBLE);
                    txt_email.setVisibility(View.INVISIBLE);
                    edit_email.setWidth(txt_email.getWidth());
                    edit_email.setText(txt_email.getText());

                    edit_mobile_no.setVisibility(View.VISIBLE);
                    txt_mobNo.setVisibility(View.INVISIBLE);
                    edit_mobile_no.setWidth(txt_mobNo.getWidth());
                    edit_mobile_no.setText(txt_mobNo.getText());

                    edit_store_name.setVisibility(View.VISIBLE);
                    txt_storeName.setVisibility(View.INVISIBLE);
                    edit_store_name.setText(txt_storeName.getText());
                    edit_store_name.setWidth(txt_storeName.getWidth());

                    edit_store_address.setVisibility(View.VISIBLE);
                    edit_store_address.setPaintFlags(edit_store_address.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    txt_Address.setVisibility(View.INVISIBLE);
                    ////here open place picker window when user hit the text choose_on_map
                }
                else {
                    Log.d("profile_Activity : ","changing to edit_profile");

                    txt_edit_profile.setText("Edit Profile");

                    txt_email.setVisibility(View.VISIBLE);
                    edit_email.setVisibility(View.INVISIBLE);

                    txt_storeName.setVisibility(View.VISIBLE);
                    edit_store_name.setVisibility(View.INVISIBLE);

                    txt_mobNo.setVisibility(View.VISIBLE);
                    edit_mobile_no.setVisibility(View.INVISIBLE);

                    txt_Address.setVisibility(View.VISIBLE);
                    edit_store_address.setVisibility(View.INVISIBLE);
                }
            }
        });

        txt_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(profile_Activity.this,android.R.style.Theme_Translucent_NoTitleBar);
                dialog.setContentView(R.layout.change_password_dialog);
                Window window = dialog.getWindow();

                if (window != null) {
                    window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    window.setGravity(Gravity.CENTER);
                }


                dialog.show();

                dialog.findViewById(R.id.id_txt_done).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.findViewById(R.id.id_txt_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        txt_delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(profile_Activity.this,android.R.style.Theme_Translucent_NoTitleBar);
                dialog.setContentView(R.layout.delete_account_dialog);
                Window window = dialog.getWindow();

                if (window != null) {
                    window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    window.setGravity(Gravity.CENTER_VERTICAL);
                }


                dialog.show();

                dialog.findViewById(R.id.id_cancel_in_delete_account_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.findViewById(R.id.id_txt_delete_in_delete_account_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqliteHandler = new sqlite_Handler(profile_Activity.this);
                sqliteHandler.deleteCurrentUser();
                Intent intent = new Intent(profile_Activity.this,Home.class);
                startActivity(intent);
            }
        });
    }



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
