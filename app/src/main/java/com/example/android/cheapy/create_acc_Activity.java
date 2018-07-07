package com.example.android.cheapy;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public class create_acc_Activity extends AppCompatActivity {

    //widgets variables
    TextView txt_choose_on_map, txt_login;
    Button btn_register;
    EditText edt_first_name, edt_last_name, edt_mail, edt_mobile_no, edt_password, edt_confirm_password, edt_store_name;



    //variables gor picking a place
    public static final int PLACE_PICKER_REQUEST = 1;
    public LatLng pickedPlace_latLng;
    public String pickedPlace_address;

    //database object
    sqlite_Handler sqliteHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cretae_acc);

        //set back button in actionbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //set widgets variables
        txt_choose_on_map = findViewById(R.id.id_txt_choose_on_map_in_create_acc_activity);
        txt_login = findViewById(R.id.id_txt_login);
        btn_register = findViewById(R.id.id_btn_register);
        edt_first_name = findViewById(R.id.id_edt_first_name);
        edt_last_name = findViewById(R.id.id_edt_last_name);
        edt_mail = findViewById(R.id.id_edt_email);
        edt_mobile_no = findViewById(R.id.id_edt_mobileNo);
        edt_password = findViewById(R.id.id_edt_password);
        edt_confirm_password = findViewById(R.id.id_edt_confirm_password);
        edt_store_name = findViewById(R.id.id_edt_storeName);

        //first hide keyboard in the starting
        hideSoftKeyboard();


        //underline this textView--Choose On Location
        txt_choose_on_map.setPaintFlags(txt_choose_on_map.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);



        //register user when user hit the register button
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //even if a single editText is not completed then show a toast
                if(edt_first_name.getText().toString().matches("") || edt_last_name.getText().toString().matches("")
                        ||edt_mail.getText().toString().matches("") ||edt_mobile_no.getText().toString().matches("")
                        ||edt_password.getText().toString().matches("") ||edt_confirm_password.getText().toString().matches("")
                        ||edt_store_name.getText().toString().matches("") || txt_choose_on_map.getText().toString().matches("choose on map")){
                    Toast.makeText(create_acc_Activity.this, "please fill all the required details", Toast.LENGTH_SHORT).show();
                    return;
                }
                //if password and confirm_password is not equal than make a toast
                if(!edt_confirm_password.getText().toString().equals(edt_password.getText().toString())){
                    Toast.makeText(create_acc_Activity.this, "re-enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                //make an object of store_details and add it to the database
                store_details storeDetails = new store_details();

                storeDetails.setManagerName(edt_first_name.getText().toString()+edt_last_name.getText().toString());
                storeDetails.setEmail(edt_mail.getText().toString());
                storeDetails.setMobileNo(edt_mobile_no.getText().toString());
                storeDetails.setPassword(edt_password.getText().toString());
                storeDetails.setStoreName(edt_store_name.getText().toString());
                storeDetails.setStoreAddress(pickedPlace_address);
                storeDetails.setStoreLatLng(pickedPlace_latLng);
                storeDetails.setNumberOfProducts(0);


                sqliteHandler = new sqlite_Handler(create_acc_Activity.this);

                Log.d("create_acc_Activity","requesting to add details");
                sqliteHandler.addStoreDetails(storeDetails);

                Toast.makeText(create_acc_Activity.this, "creating your account....\nplease wait!", Toast.LENGTH_SHORT).show();

                //also add this to current_user and take to the homeActivity
                sqliteHandler.addCurrentUser(edt_mail.getText().toString());
                Intent intent = new Intent(create_acc_Activity.this,Home.class);
                startActivity(intent);
            }
        });

        //take user to loginActivity if user hit the text "Login"
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(create_acc_Activity.this,login_Activity.class);
                startActivity(intent);
            }
        });

        //take user to place_picker window if user hit the text "choose on map"
        txt_choose_on_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(create_acc_Activity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    Log.d("map_Activity : ", e.getMessage());
                }
            }
        });


    }


    //this method will retrieves the places that user have selected in place_picker_window. and set that location to our textView
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                //set this location on our textView
                txt_choose_on_map.setText(place.getName());

                //store this info in address and latLng
                pickedPlace_latLng = place.getLatLng();
                pickedPlace_address = Objects.requireNonNull(place.getAddress()).toString();

            }
        }
    }

    //method to hide keyboard
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
