package com.example.android.cheapy;

import android.app.Activity;
import android.app.SearchManager;
import android.support.v7.widget.SearchView;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.text.MessageFormat;
import java.util.Objects;

public class search_results_activity extends AppCompatActivity {

    product_details[] productDetails;
    product_details[] productDetailsOFFLINE;
    sqlite_Handler sqliteHandler;


    //we will use id_count to set ID dynamically to views
    int id_count = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        //set back button in actionbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //get data from intent
        Intent intent = getIntent();
        boolean isOfflineIncluded = intent.getBooleanExtra("iSsOfflineIncluded",true);
        int radius = intent.getIntExtra("Radius",5);
        boolean isCurrentLocationAllowed = intent.getBooleanExtra("isCurrentLocationAllowed",true);

        //get Screen Size
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            display.getRealSize(size);
        } catch (NoSuchMethodError err) {
            display.getSize(size);
        }
        int width = size.x;
        int height = size.y;


        //now get data for products which matches with user's search
        sqliteHandler = new sqlite_Handler(this);
        productDetails = sqliteHandler.getProductDetails("Flipkart");
        productDetailsOFFLINE = sqliteHandler.getAllOfflineProductDetails();


        for (int  i = 0; i < 6; i++) { //this loop is for total no of stores having the product that user has searched for.

            LinearLayout verticalHeadLayout = findViewById(R.id.id_verticalHeadLayout2_inSearchResult);
            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
            HorizontalScrollView.LayoutParams params= new HorizontalScrollView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,height/3);
            horizontalScrollView.setLayoutParams(params);
            horizontalScrollView.setMinimumHeight(height/3);

           /* if(i==0){

                TextView text_ONLINE_STORES = new TextView(this);
                text_ONLINE_STORES.setText("ONLINE STORES");
                text_ONLINE_STORES.setWidth(width);
                text_ONLINE_STORES.setLines(1);
                text_ONLINE_STORES.setGravity(Gravity.CENTER);
                //text_ONLINE_STORES.setTextSize(getResources().getDimension(R.dimen.size_20sp));
                text_ONLINE_STORES.setBackgroundColor(Color.parseColor("#800080"));
                verticalHeadLayout.addView(text_ONLINE_STORES);

            }*/
            verticalHeadLayout.addView(horizontalScrollView);
            LinearLayout linearLayout_intoHorizontalScrollView = new LinearLayout(this);
            linearLayout_intoHorizontalScrollView.setMinimumWidth(width);
            linearLayout_intoHorizontalScrollView.setMinimumHeight(height/3);
            linearLayout_intoHorizontalScrollView.setOrientation(LinearLayout.HORIZONTAL);
            horizontalScrollView.addView(linearLayout_intoHorizontalScrollView);

            for (int j =0; j < productDetails.length; j++) {

                String p_name = productDetails[j].getProductName();
                String p_encodedImage = productDetails[j].getProductEncodedImage();
                int p_price = productDetails[j].getProductPrice();
                final String p_buyLink = productDetails[j].getProductBuyLink();
                String p_storeName = productDetails[j].getStoreName();

                //first add a relative layout to the linearLayout_intoHorizontalScrollView
                RelativeLayout relativeLayout = new RelativeLayout(this);
                relativeLayout.setMinimumWidth(width);
                relativeLayout.setMinimumHeight(height/3);
                linearLayout_intoHorizontalScrollView.addView(relativeLayout);

                //now add products to this relayoutLayout

                TextView textView_storeName = new TextView(this);
                textView_storeName.setText(p_storeName);
                textView_storeName.setWidth(width);
                textView_storeName.setId(id_count); id_count++;
                //textView_storeName.setTextSize(getResources().getDimension(R.dimen.size_15sp));
                textView_storeName.setBackgroundColor(Color.parseColor("#C0C0C0"));
                relativeLayout.addView(textView_storeName);

                TextView textView_productName = new TextView(this);
                RelativeLayout.LayoutParams params1= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.addRule(RelativeLayout.BELOW, textView_storeName.getId());
                textView_productName.setLayoutParams(params1);
                textView_productName.setText(p_name);
                textView_productName.setWidth(width);
                textView_productName.setId(id_count); id_count++;
                //textView_storeName.setTextSize(getResources().getDimension(R.dimen.size_12sp));
                relativeLayout.addView(textView_productName);

                ImageView imageView_productImage = new ImageView(this);
                Picasso.with(this).load(p_encodedImage).into(imageView_productImage);
                imageView_productImage.setScaleType(ImageView.ScaleType.FIT_START);
                imageView_productImage.setMaxHeight(height/4);
                imageView_productImage.setId(id_count); id_count++;
                RelativeLayout.LayoutParams params2= new RelativeLayout.LayoutParams(width/2,ViewGroup.LayoutParams.FILL_PARENT);
                params2.setMargins( 0,9,0, 0);
                params2.addRule(RelativeLayout.BELOW,textView_productName.getId());
                imageView_productImage.setLayoutParams(params2);
                relativeLayout.addView(imageView_productImage);

                TextView textView_productPrice = new TextView(this);
                textView_productPrice.setText(MessageFormat.format("₹ {0}", p_price));
                textView_productPrice.setLines(1);
                textView_productPrice.setMaxWidth(width/2);
                textView_productPrice.setId(id_count); id_count++;
                textView_productPrice.setTextColor(Color.parseColor("#800000"));
                //textView_productPrice.setTextSize(getResources().getDimension(R.dimen.size_15sp));
                RelativeLayout.LayoutParams params3= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                params3.setMargins(20,15,0,0);
                params3.addRule(RelativeLayout.RIGHT_OF, imageView_productImage.getId());
                params3.addRule(RelativeLayout.BELOW,textView_productName.getId());
                textView_productPrice.setLayoutParams(params3);
                relativeLayout.addView(textView_productPrice);


                TextView textView_orderAt = new TextView(this);
                textView_orderAt.setText(MessageFormat.format("ORDER AT\n{0}", p_storeName));
                //textView_orderAt.setTextSize(getResources().getDimension(R.dimen.size_10sp));
                textView_orderAt.setGravity(Gravity.CENTER);
                textView_orderAt.setTextColor(Color.parseColor("#008080"));
                textView_orderAt.setTypeface(textView_orderAt.getTypeface(), Typeface.ITALIC);
                textView_orderAt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(p_buyLink));
                        startActivity(browserIntent);
                    }
                });
                RelativeLayout.LayoutParams params4= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                params4.setMargins(width/7,25,0,0);
                params4.addRule(RelativeLayout.RIGHT_OF, imageView_productImage.getId());
                params4.addRule(RelativeLayout.BELOW,textView_productPrice.getId());
                textView_orderAt.setLayoutParams(params4);
                relativeLayout.addView(textView_orderAt);

            }
        }



        if(isOfflineIncluded) {

            findViewById(R.id.id_txt_offline_stores).setVisibility(View.VISIBLE);

            for (int k = 0; k < 2; k++) {


                LinearLayout verticalHeadLayout = findViewById(R.id.id_verticalHeadLayout3_inSearchResult);
                verticalHeadLayout.setVisibility(View.VISIBLE);
                HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
                HorizontalScrollView.LayoutParams params = new HorizontalScrollView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height / 3);
                horizontalScrollView.setLayoutParams(params);
                horizontalScrollView.setMinimumHeight(height / 3);

                /*if (k == 0) {
                    TextView text_OFFLINE_STORES = new TextView(this);
                    text_OFFLINE_STORES.setText("OFFLINE STORES");
                    text_OFFLINE_STORES.setWidth(width);
                    text_OFFLINE_STORES.setGravity(Gravity.CENTER_HORIZONTAL);
                    //text_OFFLINE_STORES.setTextSize(getResources().getDimension(R.dimen.size_20sp));
                    text_OFFLINE_STORES.setBackgroundColor(Color.parseColor("#800080"));
                    verticalHeadLayout.addView(text_OFFLINE_STORES);
                }*/


                verticalHeadLayout.addView(horizontalScrollView);
                LinearLayout linearLayout_intoHorizontalScrollView = new LinearLayout(this);
                linearLayout_intoHorizontalScrollView.setMinimumWidth(width);
                linearLayout_intoHorizontalScrollView.setMinimumHeight(height / 3);
                linearLayout_intoHorizontalScrollView.setOrientation(LinearLayout.HORIZONTAL);
                horizontalScrollView.addView(linearLayout_intoHorizontalScrollView);

                for (int j = 0; j < productDetailsOFFLINE.length; j++) {

                    String p_name = productDetailsOFFLINE[j].getProductName();
                    String p_encodedImage = productDetailsOFFLINE[j].getProductEncodedImage();
                    int p_price = productDetailsOFFLINE[j].getProductPrice();
                    final String p_buyLink = productDetailsOFFLINE[j].getProductBuyLink();
                    final String p_storeName = productDetailsOFFLINE[j].getStoreName();

                    //first add a relative layout to the linearLayout_intoHorizontalScrollView
                    RelativeLayout relativeLayout = new RelativeLayout(this);
                    relativeLayout.setMinimumWidth(width);
                    relativeLayout.setMinimumHeight(height / 3);
                    linearLayout_intoHorizontalScrollView.addView(relativeLayout);

                    //now add products to this relayoutLayout

                    TextView textView_storeName = new TextView(this);
                    textView_storeName.setText(p_storeName);
                    textView_storeName.setWidth(width);
                    textView_storeName.setId(id_count);
                    id_count++;
                    //textView_storeName.setTextSize(getResources().getDimension(R.dimen.size_15sp));
                    textView_storeName.setBackgroundColor(Color.parseColor("#C0C0C0"));
                    relativeLayout.addView(textView_storeName);

                    TextView textView_productName = new TextView(this);
                    RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params1.addRule(RelativeLayout.BELOW, textView_storeName.getId());
                    textView_productName.setLayoutParams(params1);
                    textView_productName.setText(p_name);
                    textView_productName.setWidth(width);
                    textView_productName.setId(id_count);
                    id_count++;
                    //textView_storeName.setTextSize(getResources().getDimension(R.dimen.size_12sp));
                    relativeLayout.addView(textView_productName);

                    ImageView imageView_productImage = new ImageView(this);
                    Log.d("Search_Result : ", "encoded image : " + p_encodedImage);
                    if (StringToBitMap(p_encodedImage) != null) {
                        imageView_productImage.setImageBitmap(StringToBitMap(p_encodedImage));
                    }
                    imageView_productImage.setScaleType(ImageView.ScaleType.FIT_START);
                    imageView_productImage.setMaxHeight(height / 4);
                    imageView_productImage.setId(id_count);
                    id_count++;
                    RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(width / 2, ViewGroup.LayoutParams.FILL_PARENT);
                    params2.setMargins(0, 9, 0, 0);
                    params2.addRule(RelativeLayout.BELOW, textView_productName.getId());
                    imageView_productImage.setLayoutParams(params2);
                    relativeLayout.addView(imageView_productImage);

                    TextView textView_productPrice = new TextView(this);
                    textView_productPrice.setText(MessageFormat.format("₹ {0}", p_price));
                    textView_productPrice.setMaxWidth(width / 3);
                    textView_productPrice.setId(id_count);
                    id_count++;
                    textView_productPrice.setTextColor(Color.parseColor("#800000"));
                    //textView_productPrice.setTextSize(getResources().getDimension(R.dimen.size_20sp));
                    RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params3.setMargins(20, 15, 0, 0);
                    params3.addRule(RelativeLayout.RIGHT_OF, imageView_productImage.getId());
                    params3.addRule(RelativeLayout.BELOW, textView_productName.getId());
                    textView_productPrice.setLayoutParams(params3);
                    relativeLayout.addView(textView_productPrice);


                    TextView textView_LocateOnMap = new TextView(this);
                    textView_LocateOnMap.setText("Locate on\nMap");
                    //textView_LocateOnMap.setTextSize(getResources().getDimension(R.dimen.size_10sp));
                    textView_LocateOnMap.setGravity(Gravity.CENTER);
                    textView_LocateOnMap.setTextColor(Color.parseColor("#008080"));
                    textView_LocateOnMap.setTypeface(textView_LocateOnMap.getTypeface(), Typeface.ITALIC);
                    final double LAT = sqliteHandler.getLatLngForStore(productDetailsOFFLINE[j].getStoreName()).latitude;
                    final double LNG = sqliteHandler.getLatLngForStore(productDetailsOFFLINE[j].getStoreName()).longitude;
                    textView_LocateOnMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(search_results_activity.this, map_Activity.class);
                            intent.putExtra("LAT", LAT);
                            intent.putExtra("LNG", LNG);
                            intent.putExtra("STORENAME", p_storeName);
                            startActivityForResult(intent, 12345);
                            Toast.makeText(search_results_activity.this, "Getting Directions....", Toast.LENGTH_SHORT).show();
                        }
                    });
                    RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params4.setMargins(width / 7, 25, 0, 0);
                    params4.addRule(RelativeLayout.RIGHT_OF, imageView_productImage.getId());
                    params4.addRule(RelativeLayout.BELOW, textView_productPrice.getId());
                    textView_LocateOnMap.setLayoutParams(params4);
                    relativeLayout.addView(textView_LocateOnMap);

                }
            }
        }


    }


    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        }catch(Exception e){
            e.getMessage();
            Log.d("search_Result : ",e.getMessage());
            return null;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 12345) {
            if (resultCode == RESULT_OK) {
                Log.d("searchResultActivity : ","successfully returned back from map_Activity....");
            }
        }
    }


    //function to convert dp to px
    private int px(int dp){
        Resources r = search_results_activity.this.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.id_search_menu).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(search_results_activity.this,"You searched "+query, Toast.LENGTH_SHORT).show();
                hideKeyboard(search_results_activity.this);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(search_results_activity.this,""+newText,Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);

        return true;
    }


    //method to hide keyboard after search..always works for every activity
    void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
