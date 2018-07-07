package com.example.android.cheapy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cheapy.models.place_details;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class map_Activity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = map_Activity.class.getSimpleName();
    String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    //variable to check if location permission is granted or not
    private boolean isLocationPermissionGranted = false;

    //variable to check time between two continuous click
    private long mLastClickTime = 0;

    //variables to get data from Intent
    private boolean is_offline_included;
    private boolean use_current_location;
    private int radius;

    //googleMap variable to start the Map and to getLocation and to set Zoom
    private GoogleMap mgoogleMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;
    private GoogleApiClient mGoogleApiClient; //PlaceAutocompleteAdapter uses this variable as a parameter. That's why we've created this.
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private place_details place_details; //this variable can store all the details about a place. place_details.java class is created by me to store these info.
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int REQUEST_PERMISSIONS_CURRENT_LOCATION_REQUEST_CODE = 34;
    private static final int REQUEST_PERMISSIONS_LOCATION_SETTINGS_REQUEST_CODE = 35;

    protected static long MIN_UPDATE_INTERVAL = 1800 * 1000; //after 5 minute user will automatically get to current location

    LocationRequest locationRequest;
    Location lastLocation = null;
    Location currentLocation = null;


    Intent intent;


    //Adapter for AutoCompleteTextView
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;

    //widgets variables
    private AutoCompleteTextView editText_search_location;
    private ImageView img_my_location_btn, img_place_picker_btn, img_next_step_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);



        Log.d("map_Activity : ", "I'm in map_Activity");

        Log.d(TAG,"calling checkForLocationRequest........... from onCreate.....");
        checkForLocationRequest();
        Log.d(TAG,"calling checkForLocationSetting........... from onCreate.....");
        checkForLocationSettings();

        //this variable is useful when we get current location of device
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(map_Activity.this, permission, 1234);

        //get data from Intent
        intent = getIntent();
        if(intent.hasExtra("isOfflineIncluded") && intent.hasExtra("useCurrentLocation") && intent.hasExtra("radius")) {
            is_offline_included = intent.getBooleanExtra("isOfflineIncluded", false);
            use_current_location = intent.getBooleanExtra("useCurrentLocation", false);
            radius = intent.getIntExtra("radius", 0);
        }


        //in this activity we don't want any actionbar, so in android-manifesto file, for this particular activity, i've added android:theme="@android:style/Theme.NoTitleBar"
        //but adding only this will crash our app. To avoid this, we need to extend FragmentActivity instead of AppCompatActivity with this class. that's why i've extended this class with FragmentActivity


        //initialize widget variables
        editText_search_location = findViewById(R.id.id_edit_text_search_location);
        img_my_location_btn = findViewById(R.id.id_btn_my_location);
        img_place_picker_btn = findViewById(R.id.id_btn_place_picker);
        img_next_step_btn = findViewById(R.id.id_btn_next_step);



        //go to Current_Location if user hit the gps_btn
        img_my_location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("map_Activity : ", "user hit the GPS button....so calling getDeviceLocation()...");
                //moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM,"Current Location");
                getDeviceLocation();
            }
        });

        //go to place picker if user hit the place_picker button
        img_place_picker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("map_Activity : ", "user hit placePicker Button.....");

                img_place_picker_btn.setClickable(false); //two avoid continuous click

                //this block of code is copied from google_documentation and it will take us to place_picker window.
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(map_Activity.this), PLACE_PICKER_REQUEST);
                    Log.d("map_Activity : ", "placePicking Successful.....now asking for result from placePicker....");
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    Log.d("map_Activity : ", "exception in placePicker....= " + e.getMessage());
                }
            }
        });

        //go to search_Activity if user hit the next_step_btn
        img_next_step_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(map_Activity.this, search_Activity.class);
                intent.putExtra("isOfflineIncluded",is_offline_included);
                intent.putExtra("useCurrentLocation",use_current_location);
                intent.putExtra("radius",radius);
                startActivity(intent);
            }
        });

    }

    //as we won't add any button to start searching when user user enter something in EditText, So we will use KeyBoard to start searching.
    //for this we will use this method
    private void useKeyboardButtonToMakeSearch() {

        Log.d("map_Activity : ", "I'm in useKeyboardButtonToMakeSearch()...");

        //also set onItemClickListener to the AutoCompleteTextView so when user select from Drop-Down list, clickListener will get called automatically.
        editText_search_location.setOnItemClickListener(mAutoCompleteClickListener);  //mAutoCompleteClickListener is defined in the end of this class.
        //initialize the adapter to autocomplete the search_bar
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null);

        editText_search_location.setAdapter(placeAutocompleteAdapter);
        editText_search_location.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyevent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || keyevent.getAction() == KeyEvent.ACTION_DOWN || keyevent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //now hide the keyboard once user hit keyboard's search button
                    hideSoftKeyboard(editText_search_location);
                    //also hide the DropDownList once user hit keyboard's search button
                    editText_search_location.dismissDropDown();

                    //here we execute our method to search location entered in searchBar
                    Log.d("map_Activity : ", "user selected something from dropDown List....calling geoLocate().....");
                    geoLocate();
                }
                return false;
            }
        });

    }

    //method to draw circle on map
    public void draw_circle(LatLng latLng, int radius) {
        CircleOptions options = new CircleOptions().center(latLng).radius(radius).strokeColor(Color.WHITE).strokeWidth(5);
        mgoogleMap.addCircle(options);
    }

    //this method will retrieves the places that user have selected in place_picker_window.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("map_Activity : ", "I'm in onActivityResult()....");

        //now let's make the place_picker_button clickable again(remember we made it un-clickable to prevent second click)
        img_place_picker_btn.setClickable(true);

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Log.d("map_Activity : ", "got results from placePicker...");
                Place place = PlacePicker.getPlace(this, data);

                //now we have to submit a request to google to get data about this place.
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, place.getId());

                //set a ResultCallBack method with the variable placeResult, so we will know when results are ready.
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

                Log.d("map_Activity : ", "calling onResults()....");
                //now it will automatically call onResult method which is defined in the end of this class

                //if our request(which we submitted to google to get data about this place) is successful/unsuccessful(in both cases) then we get a call from google which will be received through onResult method.
                // and it will be received by the variable which we provided at the time of setResultCallback method
            }
        }
    }

    //function to search location entered in search_bar
    private void geoLocate() {
        Log.d("map_Activity : ", "I'm in geoLocate().....");
        String search_string = editText_search_location.getText().toString();
        Geocoder geocoder = new Geocoder(map_Activity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(search_string, 1); //here 1 means Max_No_of_results we are looking for is 1.
        } catch (IOException e) {
            Toast.makeText(this, "geoLocate IoException : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            Toast.makeText(this, "Found a Location : \n" + address.toString(), Toast.LENGTH_SHORT).show();
            //now let's move camera to this location
            //so first let's get Latlng of this location
            Log.d("map_Activity : ", "moving camera to selected location.....");
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            moveCamera(latLng, DEFAULT_ZOOM, address.getAddressLine(0));
        }

    }


    //function to get device's location
    private void getDeviceLocation() {

        Log.d(TAG,"inside getDeviceLocation()................");
        try {
            Log.d(TAG,"checking permission.............");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG,"don't have permission...so requesting permission.........(code = REQUEST_PERMISSIONS_CURRENT_LOCATION_REQUEST_CODE....");

                requestPermissions(REQUEST_PERMISSIONS_CURRENT_LOCATION_REQUEST_CODE);
                return;
            }

            Log.d(TAG,"we have permission...........requesting LocationUpdates()......");
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    Log.d(TAG,"inside onLocationResult().............just got the result...........");
                    currentLocation =  locationResult.getLastLocation();

                    Log.d(TAG,"LatLng = "+currentLocation.getLatitude()+", "+currentLocation.getLongitude());

                    if (!intent.hasExtra("LAT") && !intent.hasExtra("LNG") && !intent.hasExtra("STORENAME")) {
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "Current Location");
                    }


                }
            }, Looper.myLooper());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(map_Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(map_Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("map_Activity : ", "we don't have permission....in initMap()...");
            Log.d("map_Activity : ", "requesting permission to mark location...");
            String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

            ActivityCompat.requestPermissions(map_Activity.this, permission, 1235);


        } else {
            Log.d("map_Activity : ", "we have permission.....now marking current location...");
            mgoogleMap.setMyLocationEnabled(true); //now it will mark a blue dot on current location
             //it will disable the button which was used to get back to current_location if we scroll somewhere else in the map
            //because we will add a search bar and that's gonna hide this button so we will add this button below our search bar

            //now using keyboard button, let's search about what is entered in search_bar
            Log.d("map_Activity : ", "current location is marked...now calling useKeyboardButtonToMakeSearch()....");

            if (!intent.hasExtra("LAT") && !intent.hasExtra("LNG") && !intent.hasExtra("STORENAME")) {
                mgoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                useKeyboardButtonToMakeSearch();
            }
            else{
                mgoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        }

    }




    //function to move camera to given location
    private void moveCamera(LatLng latLng, float zoom, String locationTitle) {

        if (latLng.longitude == -91 || latLng.latitude == -91) {
            Log.d("Map_Activity : ", "LatLng is (-91,-91)................");
            return;
        }

        Log.d("Map_Activity : ", "LATLNG = ( " + latLng.latitude + ", " + latLng.longitude + " )");
        mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        //after moving camera to this location. let's add a marker to this location. but let's not add marker on current location

        if (locationTitle.compareTo("Current Location") != 0) {
            //clear all markers before marking this new location
            if (!intent.hasExtra("LAT") && !intent.hasExtra("LNG") && !intent.hasExtra("STORENAME")) {
                mgoogleMap.clear();
                draw_circle(latLng, (radius * 1000));
            }
            MarkerOptions options = new MarkerOptions().position(latLng).title(locationTitle);
            mgoogleMap.addMarker(options);

            Log.d("map_Activity : ", "camera is moved to given location.....and marked the location...and circled...");

            //write your code here to mark all stores that lies inside this circle

        } else {
            Log.d("map_Activity : ", "camera is moved to current location");

            //write your code here to mark all stores that lies inside the circle around the current location

        }

    }




    //function to initialize the Map if permission is granted
    private void initMap() {
        Log.d("map_Activity : ", "I'm in initMap()...");

        //this mGoogleApiClient variable is used as a parameter in constructor of PlaceAutocompleteAdapter.java(remember we copied this class from github).
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mGoogleApiClient.connect();

        Toast.makeText(this, "initializing Map...", Toast.LENGTH_SHORT).show();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d("map_Activity : ", "inside onMapReady....");

                mgoogleMap = googleMap; //there is another way...implement OnMapReadyCallback interface and then overRide onMapReady method and set mgoogleMap = googleMap.
                // and remove OnMapReadyCallback() method from here and pass context to method getMapAsync


                if (intent.hasExtra("LAT") && intent.hasExtra("LNG") && intent.hasExtra("STORENAME")) {

                    //means intent came from search_result_activity..so let's mark this latLng on map
                    LatLng latLngX = new LatLng(intent.getDoubleExtra("LAT", -91), intent.getDoubleExtra("LNG", -91));
                    Log.d(TAG, "got an intent from search_result_Activity....LATLNG = " + latLngX);
                    String storeName = intent.getStringExtra("STORENAME");

                    img_next_step_btn.setVisibility(View.INVISIBLE);
                    img_place_picker_btn.setVisibility(View.INVISIBLE);
                    img_my_location_btn.setVisibility(View.INVISIBLE);
                    RelativeLayout relativeLayout = findViewById(R.id.id_relative_layout_for_search_bar_in_map);
                    relativeLayout.setVisibility(View.INVISIBLE);

                    //mgoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    moveCamera(latLngX, DEFAULT_ZOOM, storeName);
                    getDeviceLocation();


                } else {
                    //now call getDeviceLocation method
                    Log.d("map_Activity : ", "calling getDeviceLocation......");
                    getDeviceLocation();

                }

            }
        });
    }


    //function called when we get result from method ActivityCompat.requestPermission with request_code = 1234
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //we got the results from ActivityCompat.requestPermission which tells us if user granted our permission
        //now we check these results and if permission is granted then we set the below variable to true

        Log.d("map_Activity : ", "just got results from requestPermission....checking if results are in favor....");

        isLocationPermissionGranted = false;

        switch (requestCode) {
            case 1234:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Log.d("map_Activity : ", "results are not in favor");
                        Toast.makeText(this, "user is not allowing", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (i == grantResults.length - 1) {
                        //it means all the permissions are granted so set our boolean true
                        isLocationPermissionGranted = true;
                        Log.d("map_Activity : ", "results are in favor");
                        Log.d("map_Activity : ", "now calling initMap()....");

                        //now user is ready to initialize the map
                        Toast.makeText(this, "Permission is granted", Toast.LENGTH_SHORT).show();
                        initMap();
                    }
                }
                break;

            case 1235:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Log.d("map_Activity : ", "results are not in favor");
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (i == grantResults.length - 1) {

                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mgoogleMap.setMyLocationEnabled(true); //now it will mark a blue dot on current location
                        mgoogleMap.getUiSettings().setMyLocationButtonEnabled(false); //it will disable the button which was used to get back to current_location if we scroll somewhere else in the map
                        //because we will add a search bar and that's gonna hide this button so we will add this button below our search bar

                        //now using keyboard button, let's search about what is entered in search_bar
                        Log.d("map_Activity : ", "current location is marked...now calling useKeyboardButtonToMakeSearch()....");
                        useKeyboardButtonToMakeSearch();

                    }
                }
                break;

            case REQUEST_PERMISSIONS_CURRENT_LOCATION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG,"calling callCurrentLocation()............from onPermissionRequestResult().......");
                    getDeviceLocation();
                }
                break;


        }
    }



    //method to hide keyboard after search..this method hides keyboard if keyboard's button is set with a view to make search
    private void hideSoftKeyboard(View FOCUSABLE_VIEW){
        Log.d("map_Activity","hideSoftKeyboard has called");
        InputMethodManager imm = (InputMethodManager)
        getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(
                FOCUSABLE_VIEW.getWindowToken(), 0);
        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }




    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //this method is needed to implement as we are implementing the interface onConnectionFailedListener
        Toast.makeText(this, "Connection Failed : " + connectionResult.toString(), Toast.LENGTH_SHORT).show();
        Log.d("map_Activity : ", "connection failed.......");
    }



    /*---------here we implement a method which will be called when user select from drop-down list of AutoCompleteTextView------------*/
    private AdapterView.OnItemClickListener mAutoCompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //first hide the keyboard when user select from drop-down list
            hideSoftKeyboard(editText_search_location);

            //now let's get the item which was selected by user. remember it is a AutocompletePrediction object. and it is selected from our placeAutocompleteAdapter.
            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(position);
            assert item != null;
            final String placeId = item.getPlaceId();

            //now we have to submit a request to google to get data about this item.
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient,placeId);

            //set a ResultCallBack method with the variable placeResult, so we will know when results are ready.
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };




    //if our request(which we submitted to google to get data about an item) is successful/unsuccessful(in both cases) then we get a call from google which will be received through onResult method.
    // and it will be received by the variable which we provided at the time of setResultCallback method
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>(){
        @Override
        public void onResult(@NonNull PlaceBuffer places){

            //if our request is not successful then make a Toast and release the memory of variable "places".
            if(!places.getStatus().isSuccess()){
                Toast.makeText(map_Activity.this,"something went wrong \n" + places.getStatus().toString(), Toast.LENGTH_SHORT).show();
                places.release();
                //now let's make the place_picker_button clickable again(remember we made it un-clickable to prevent second click)
                img_place_picker_btn.setClickable(true);
            }
            //if request is successful then we get the details about that place.
            //so let's print those details.
            else{

                final Place place = places.get(0);

                //if we use try_Catch over all the lines together then if second line throws exception, next lines of code won't execute and we won't get remaining details
                //that's why i've used try_catch after each line of code.
                try {
                    place_details = new place_details();
                }catch (NullPointerException e){

                }
                try {
                    place_details.setAddress(Objects.requireNonNull(place.getAddress()).toString());
                }catch (NullPointerException e){

                }
                try {
                    place_details.setName(place.getName().toString());
                }catch (NullPointerException e){

                }
                try {
                    place_details.setId(place.getId());
                }catch (NullPointerException e){

                }
                try {
                    place_details.setPhoneNumber(Objects.requireNonNull(place.getPhoneNumber()).toString());
                }catch (NullPointerException e){

                }
                try {
                    place_details.setAttributions(Objects.requireNonNull(place.getAttributions()).toString());
                }catch (NullPointerException e){

                }
                try {
                    place_details.setWebsiteUri(place.getWebsiteUri());
                }catch (NullPointerException e){

                }
                try {
                    place_details.setRating(place.getRating());
                }catch (NullPointerException e){

                }
                try {
                    place_details.setLatLng(place.getLatLng());
                }catch (NullPointerException e){

                }

                //let's make a Toast of whatever details we have got
                Toast.makeText(map_Activity.this, place_details.toString(), Toast.LENGTH_LONG).show();


                //now let's move camera to this selected place
                moveCamera(new LatLng(Objects.requireNonNull(place.getViewport()).getCenter().latitude,place.getViewport().getCenter().longitude),DEFAULT_ZOOM, place_details.getName());

                //now release place object
                places.release();

            }
        }
    };



    //when user hit device's back then go to home activity....if you don't override this method then on pressing devices's back button,you will
    // find the navigation bar open in home_Activity because it's state was saved. we sre using intent in this method, so it will re-create home_Activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Log.d("map_Activity : ", "user hit the keyboard's back button..");

        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(intent.hasExtra("LAT") && intent.hasExtra("LNG") && intent.hasExtra("STORENAME")){
                Log.d("map_Activity : ","but intent came from search_result_Actinity....so returning result.....");
                Intent data = new Intent();
                setResult(RESULT_OK, data);
                finish();
                return true;
            }
            Intent intent = new Intent(this,Home.class);
            startActivity(intent);
            return true;
        }
        return false;
    }



    public void checkForLocationRequest(){
        Log.d(TAG,"inside checkForLocationRequest()............");
        locationRequest = LocationRequest.create();
        ///locationRequest.setInterval(MIN_UPDATE_INTERVAL);             //if you uncomment this statement, user will return to his current location after every MIN_UPDATE_INTERVAL which we've defined at the top
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    //Check for location settings.
    public void checkForLocationSettings() {
        Log.d(TAG,"inside checkForLocationSetting()............");
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.addLocationRequest(locationRequest);
            SettingsClient settingsClient = LocationServices.getSettingsClient(map_Activity.this);

            settingsClient.checkLocationSettings(builder.build())
                    .addOnSuccessListener(map_Activity.this, new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            Log.d(TAG,"inside onSuccess()............");
                            //Setting is success...
                            Toast.makeText(map_Activity.this, "Enabled the Location successfully. Now you can press the buttons..", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(map_Activity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG,"inside onFailure()............");
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                    try {
                                        // Show the dialog by calling startResolutionForResult(), and check the
                                        // result in onActivityResult().
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult(map_Activity.this, REQUEST_PERMISSIONS_LOCATION_SETTINGS_REQUEST_CODE);
                                    } catch (IntentSender.SendIntentException sie) {
                                        sie.printStackTrace();
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    Toast.makeText(map_Activity.this, "Setting change is not available.Try in another device.", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void startLocationPermissionRequest(int requestCode) {
        Log.d(TAG,"inside startLocationPermissionRequest()................................and requesting permission..... ");
        ActivityCompat.requestPermissions(map_Activity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
    }


    private void showSnackbar(final String mainTextString, final String actionString,
                              View.OnClickListener listener) {
        Log.d(TAG,"showing SnackBar.........");
        Snackbar.make(findViewById(android.R.id.content),
                mainTextString,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(actionString, listener).show();
    }

    private void requestPermissions(final int requestCode) {

        Log.d(TAG,"inside requestPermission()....................");
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            showSnackbar("Permission is must to find the location", "Ok",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            Log.d(TAG,"calling startLocationPermissionRequest()................from requestPermission()..");
                            startLocationPermissionRequest(requestCode);
                        }
                    });

        } else {
            Log.d(TAG,"calling startLocationPermissionRequest()................from requestPermission()..");
            startLocationPermissionRequest(requestCode);
        }
    }


}
