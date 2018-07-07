package com.example.android.cheapy;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    //variables for navigation drawer
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    //var to check if any user is logged in...if count is 1 => logged in....if count is 0 =>not logged in
    int count;

    //variables for spinner to change radius
    private String[] radius_string = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};

    //variables to send to Search_Activity
    private boolean is_offline_included;
    private boolean use_current_location;
    private int radius;

    //database variable
    sqlite_Handler sqliteHandler;


    //variables to check if user have updated google_play_services. it will be helpful when user hit change_location_button
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //set title in the centre
        setTitle_in_centre();

        //first add navigation drawer
        add_and_set_navigationDrawer();

        //add laptopData and then show in logcat
        //store_laptop_data();
        //showInLogCat();


        sqliteHandler = new sqlite_Handler(this);

        //show all available store in our database
        //Log.d("Home_Activity : ", Arrays.toString(sqliteHandler.getAllStoresDetails()));

        //check for currentUser in database
        count = sqliteHandler.getCurrentUserCount();
        //if currentUser exists in table(means count=1) then change the navigationBAr
        if (count == 1) {
            Toast.makeText(this, "currentUserFound....changing accordingly", Toast.LENGTH_SHORT).show();
            NavigationView navigationView = findViewById(R.id.id_navigation_view);
            Menu menu = navigationView.getMenu();
            View header_layout = navigationView.getHeaderView(0);
            header_layout.setBackgroundColor(Color.WHITE);
            header_layout.findViewById(R.id.id_nav_header_user_image).setVisibility(View.VISIBLE);
            TextView textView = header_layout.findViewById(R.id.id_header_user_name);
            textView.setText(sqliteHandler.getCurrentUser());
            header_layout.findViewById(R.id.id_header_user_name).setVisibility(View.VISIBLE);
            menu.setGroupVisible(R.id.id_group1, true);
            menu.setGroupVisible(R.id.id_group3, false);
            invalidateOptionsMenu();
        } else if (count == 0) {
            NavigationView navigationView = findViewById(R.id.id_navigation_view);
            Menu menu = navigationView.getMenu();
            menu.setGroupVisible(R.id.id_group1, false);
            menu.setGroupVisible(R.id.id_group3, true);
            invalidateOptionsMenu();
            Toast.makeText(this, "no currentUser Found", Toast.LENGTH_SHORT).show();
        } else {
            invalidateOptionsMenu();
            Toast.makeText(this, "more than one currentUser Found\n don't know what to do :)", Toast.LENGTH_SHORT).show();
        }

        //set spinner to change radius
        final Spinner spinner = findViewById(R.id.id_radius_spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, radius_string);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);


        //go to Search_Activity when user hit the COMPARE Button to compare between online stores
        Button btn_compare = findViewById(R.id.id_btn_compare_online);
        btn_compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_offline_included = false;
                use_current_location = false;
                radius = 0;
                Intent intent = new Intent(getApplicationContext(), search_Activity.class);
                intent.putExtra("isOfflineIncluded", is_offline_included);
                intent.putExtra("useCurrentLocation", use_current_location);
                intent.putExtra("radius", radius);
                startActivity(intent);
            }
        });

        //go to search_Activity if user hit the use_current_location button
        Button btn_useCurrentLocation = findViewById(R.id.id_btn_use_current_location);
        btn_useCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_offline_included = true;
                use_current_location = true;
                radius = Integer.parseInt(spinner.getSelectedItem().toString());
                Intent intent = new Intent(getApplicationContext(), search_Activity.class);
                intent.putExtra("isOfflineIncluded", is_offline_included);
                intent.putExtra("useCurrentLocation", use_current_location);
                intent.putExtra("radius", radius);
                startActivity(intent);
            }
        });

        //go to map_Activity if user hit the Change_location Button
        Button btn_change_location = findViewById(R.id.id_btn_change_location);
        btn_change_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_offline_included = true;
                use_current_location = false;
                radius = Integer.parseInt(spinner.getSelectedItem().toString());
                Intent intent = new Intent(getApplicationContext(), map_Activity.class);
                intent.putExtra("isOfflineIncluded", is_offline_included);
                intent.putExtra("useCurrentLocation", use_current_location);
                intent.putExtra("radius", radius);
                //start Map_Activity if google_play_services is okay
                if (isServicesOk()) {
                    startActivity(intent);
                }
            }
        });

        //also invoke spinner if user hit change_radius
        Button btn_change_radius = findViewById(R.id.id_btn_change_radius);
        btn_change_radius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.performClick();
            }
        });

    }


    void setTitle_in_centre() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
    }

    private void add_and_set_navigationDrawer() {
        DrawerLayout mDrawerLayout = findViewById(R.id.id_drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.Open, R.string.Close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.id_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.id_cart_menu) {
            Intent intent = new Intent(this, cart_Activity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.id_profile_menu) {
            Intent intent = new Intent(this, profile_Activity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.id_nav_login:
                Intent intent1 = new Intent(this, login_Activity.class);
                startActivity(intent1);
                break;

            case R.id.id_nav_log_out:
                //this button should work for log out
                //so let's delete current user from database
                sqliteHandler = new sqlite_Handler(Home.this);
                sqliteHandler.deleteCurrentUser();
                //now we have to set default navigation bar again
                NavigationView navigationView = findViewById(R.id.id_navigation_view);
                Menu menu = navigationView.getMenu();
                count = 0;
                menu.setGroupVisible(R.id.id_group1, false);
                menu.setGroupVisible(R.id.id_group3, true);
                View header_layout = navigationView.getHeaderView(0);
                header_layout.setBackground(getDrawable(R.drawable.cheapy_logo));
                header_layout.findViewById(R.id.id_nav_header_user_image).setVisibility(View.INVISIBLE);
                header_layout.findViewById(R.id.id_header_user_name).setVisibility(View.INVISIBLE);
                invalidateOptionsMenu();
                break;

            case R.id.id_nav_upload_items:
                Intent intent = new Intent(this, upload_item_Activity.class);
                startActivity(intent);
                break;

            case R.id.id_nav_my_items:
                Toast.makeText(this, "sorry:)\ncurrently this feature does not work", Toast.LENGTH_SHORT).show();
                break;

            case R.id.id_nav_new_account:
                Intent intent2 = new Intent(this, create_acc_Activity.class);
                startActivity(intent2);
                break;

            case R.id.id_settings:
                Intent intent3 = new Intent(this, setting_Activity.class);
                startActivity(intent3);
                break;

            case R.id.id_history:
                Intent intent4 = new Intent(this, history_Activity.class);
                startActivity(intent4);
                break;

            case R.id.id_help_feedback:
                Intent intent5 = new Intent(this, help_feedback_Activity.class);
                startActivity(intent5);
                break;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        inflater.inflate(R.menu.cart_menu, menu);
        if (count == 0) {
            menu.findItem(R.id.id_profile_menu).setVisible(false);
        } else {
            menu.findItem(R.id.id_cart_menu).setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //function to check if google_services is okay
    public boolean isServicesOk() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if (available == ConnectionResult.SUCCESS) {
            //user can make map request
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occurred but we can resolve this
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "you can't make map request", Toast.LENGTH_SHORT).show();

        }
        return false;
    }

    //method to refresh actionbar

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //MenuItem item= menu.findItem(R.id.id_profile_menu);
        //depending on your conditions, either enable/disable
        if (count == 0) {
            menu.findItem(R.id.id_profile_menu).setVisible(false);
            menu.findItem(R.id.id_cart_menu).setVisible(true);
        } else {
            menu.findItem(R.id.id_profile_menu).setVisible(true);
            menu.findItem(R.id.id_cart_menu).setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
        return true;
    }


    //this method will exit the app if user hit the device's back key
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return false;
    }


    //function to store laptop_data in sqlite
    public void store_laptop_data() {

        sqlite_Handler sqliteHandler1 = new sqlite_Handler(this);
        product_details productDetails1 = new product_details("Flipkart", "Dell Inspiron Core i3 6th Gen - (4 GB/1 TB HDD/Linux) 5567 Laptop", 26990,
                "https://rukminim1.flixcart.com/image/832/832/jdbzcsw0/computer/v/u/f/dell-na-notebook-original-imaf29nnjju76t2h.jpeg?q=70",
                "Intel Core i3 Processor (6th Gen)\n" +
                        "4 GB DDR4 RAM\n" +
                        "Linux/Ubuntu Operating System\n" +
                        "1 TB HDD\n" +
                        "15.6 inch Display", "https://www.flipkart.com/dell-inspiron-core-i3-6th" +
                "-gen-4-gb-1-tb-hdd-linux-5567-laptop/p/itmewm96hzkuruhe?pid=C" +
                "OMEWM96FYRZMVUF&srno=b_1_1&otracker=browse&lid" +
                "=LSTCOMEWM96FYRZMVUFGONV1T&fm=organic&iid=67616489-8461-48cf-8b07" +
                "-701f35279702.COMEWM96FYRZMVUF.SEARCH");

        product_details productDetails2 = new product_details("Amazon", "Dell Vostro 3000 Core i5 7th Gen", 38990,
                "https://rukminim1.flixcart.com/image/832/832/jehf4i80/computer/g/j/j/dell-vostro-3568-laptop-original-imaf35hv5dzvhbpp.jpeg?q=70",
                "Intel Core i5 Processor (7th Gen)\n" +
                        "8 GB DDR4 RAM\n" +
                        "Linux/Ubuntu Operating System\n" +
                        "1 TB HDD\n" +
                        "15.6 inch Display",
                "https://www.flipkart.com/dell-vostro-3000-core-i5-7th-gen-8-gb-1-tb-hdd-ubuntu-2-gb-graphics-3568-laptop/p/itmf3s3fzdeb5bhh?pid=COMESY4KE7H7JGJJ&srno=s_1_3&otracker=search&lid=LSTCOMESY4KE7H7JGJJ6WRF8L&fm=SEARCH&iid=a8cb1438-a653-4efe-8707-2494e2625481.COMESY4KE7H7JGJJ.SEARCH&ssid=i3bapsjbc00000001529965358696&qH=a3d24b555bc2ee18");

        product_details productDetails3 = new product_details("Flipkart", "Dell 3000 Core i3 6th Gen", 27740,
                "https://rukminim1.flixcart.com/image/832/832/j3orcsw0/computer/p/d/f/dell-3567-notebook-original-imaemzn9vfshfkyq.jpeg?q=70",
                "\n" +
                        "Intel Core i3 Processor (6th Gen)\n" +
                        "4 GB DDR4 RAM\n" +
                        "64 bit Linux/Ubuntu Operating System\n" +
                        "1 TB HDD\n" +
                        "15.6 inch Display",
                "https://www.flipkart.com/dell-3000-core-i3-6th-gen-4-gb-1-tb-hdd-ubuntu-3568-laptop/p/itmf3s3ytbaf6hwj?pid=COMEUGAHR4MEHBGJ&srno=s_1_24&otracker=search&lid=LSTCOMEUGAHR4MEHBGJ9L633T&fm=SEARCH&iid=fd63a4b8-78ba-4608-ba49-614c5833ac2f.COMEUGAHR4MEHBGJ.SEARCH&ssid=i3bapsjbc00000001529965358696&qH=a3d24b555bc2ee18");

        sqliteHandler1.addToAllProductTable(productDetails1);
        sqliteHandler1.addToAllProductTable(productDetails2);
        sqliteHandler1.addToAllProductTable(productDetails3);
    }

    //show all product in logcat
    public void showInLogCat() {
        sqlite_Handler sqliteHandler2 = new sqlite_Handler(this);

        product_details[] productDetails = sqliteHandler2.getProductDetails("Flipkart");
        if (productDetails != null)
            for (product_details productDetail : productDetails) {
                Log.d("Home : ", productDetail.toString());
            }
        else
            Log.d("Home : ", "productDetails.toString() = null....");

    }


}
