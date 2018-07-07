package com.example.android.cheapy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static java.sql.Types.NULL;

public class search_Activity extends AppCompatActivity {

    private boolean is_offline_included;
    private boolean use_current_location;
    private int radius;
    private AutoCompleteTextView search_box;
    private String key_search_product; //text entered in search_box



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //set back button in actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //hide the keyboard in the starting of this activity
        hideSoftKeyboard();

        //get data from intent
        Intent intent = getIntent();
        is_offline_included = intent.getBooleanExtra("isOfflineIncluded", false);
        use_current_location = intent.getBooleanExtra("useCurrentLocation", false);
        radius = intent.getIntExtra("radius", 0);

        //set widgets to their respective views
        search_box = findViewById(R.id.id_search_box);
        //now let user make search using keyboard's search button
        search_box.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyevent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || keyevent.getAction() == KeyEvent.ACTION_DOWN || keyevent.getAction() == KeyEvent.KEYCODE_ENTER){

                    key_search_product = search_box.getText().toString();
                    //now hide the keyboard once user hit keyboard's search button
                    hideSoftKeyboard(search_box);

                    //from here user can go to next activity to get results about search_key
                    Intent intent1 = new Intent(search_Activity.this,search_results_activity.class);
                    intent1.putExtra("iSsOfflineIncluded",is_offline_included);
                    intent1.putExtra("isCurrentLocationAllowed",use_current_location);
                    intent1.putExtra("Radius",radius);
                    startActivity(intent1);
                    Toast.makeText(getApplicationContext(),"searching...." + key_search_product, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        //make toast of data obtained from intent
        Toast.makeText(this,"isOfflineIncluded : "+is_offline_included+"\nuseCurrentLocation : " + use_current_location+"\nradius : "+radius, Toast.LENGTH_LONG).show();

    }


    //method to hide keyboard after search..this method hides keyboard if keyboard's button is set with a view to make search
    private void hideSoftKeyboard(View FOCUSABLE_VIEW){
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(
                    FOCUSABLE_VIEW.getWindowToken(), 0);
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
