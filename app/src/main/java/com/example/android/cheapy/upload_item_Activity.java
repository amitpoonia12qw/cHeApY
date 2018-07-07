package com.example.android.cheapy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class upload_item_Activity extends AppCompatActivity {


    int count_image = 0;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int GALLERY_REQUEST = 124;
    private Uri mImageUri;


    //variables for popUp window
    LinearLayout linearLayout;
    View customView;
    PopupWindow popupWindow;
    TextView upload_using_camera, upload_from_gallery;

    //variables for UI of this activity
    ImageView uploaded_photo1,uploaded_photo2,uploaded_photo3;
    Button btn_upload_product_data;
    EditText product_name, quantity, price, description;

    @SuppressLint({"InflateParams", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_item);

        Log.d("upload_item_Activity"," in onCreate()...");

        hideSoftKeyboard();

        //set UI variables to their respective Views
        uploaded_photo1 = findViewById(R.id.id_img_uploaded_photo1);
        uploaded_photo2 = findViewById(R.id.id_img_uploaded_photo2);
        uploaded_photo3 = findViewById(R.id.id_img_uploaded_photo3);
        btn_upload_product_data = findViewById(R.id.id_btn_upload_product_data);
        product_name = findViewById(R.id.id_edt_product_name);
        price = findViewById(R.id.id_edt_product_price);
        quantity = findViewById(R.id.id_edt_product_quantity);
        description = findViewById(R.id.id_edt_product_description);
        linearLayout = findViewById(R.id.id_upload_item_linear_layout1);

        //set popUp variables
        LayoutInflater layoutInflater = (LayoutInflater) upload_item_Activity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        customView = Objects.requireNonNull(layoutInflater).inflate(R.layout.upload_photo_popup,null);
        popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        LinearLayout linearLayout = findViewById(R.id.id_upload_item_linear_layout1);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
            }
        });

       product_name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                return false;
            }
        });

        price.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                return false;
            }
        });

        quantity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                return false;
            }
        });

        description.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                return false;
            }
        });



        //set back button in actionbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        //set OnClickListener on necessary Views
        uploaded_photo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                else {
                    count_image = 0;
                    showPopupWindow();
                }
            }
        });
        uploaded_photo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                else {
                    count_image = 1;
                    showPopupWindow();
                }
            }
        });
        uploaded_photo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                else {
                    count_image = 2;
                    showPopupWindow();
                }
            }
        });

        //change ActionBar color
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#81a3d0")));

        //if user hit the UPLOAD_BUTTON then save data in the database
        btn_upload_product_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String encoded_image;
                try {
                    BitmapDrawable drawable = (BitmapDrawable) uploaded_photo1.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] b = baos.toByteArray();
                    encoded_image = Base64.encodeToString(b, Base64.DEFAULT);
                }catch (Exception e){
                    encoded_image = null;
                    Log.d("upload_item_activity","encoded image is null");
                }

                if(product_name.getText().toString().matches("") || quantity.getText().toString().matches("") ||
                        price.getText().toString().matches("") || description.getText().toString().matches("") || encoded_image == null ) {
                    Toast.makeText(upload_item_Activity.this,"can not upload",Toast.LENGTH_SHORT).show();
                }
                else {
                    sqlite_Handler sqliteHandler = new sqlite_Handler(upload_item_Activity.this);
                    product_details productDetails = new product_details(sqliteHandler.getStoreNameForUserName(sqliteHandler.getCurrentUser()), product_name.getText().toString(),
                            Integer.parseInt(price.getText().toString()), encoded_image, description.getText().toString(), "OFFLINE");
                    sqliteHandler.addToAllProductTable(productDetails);

                    Toast.makeText(upload_item_Activity.this, "uploaded successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(upload_item_Activity.this, Home.class));
                }
            }
        });

    }

    //open popup when uploadPhoto button is pressed
    public void showPopupWindow(){

        //hide keyboard if visible before showing popup
        hideSoftKeyboard();

        popupWindow.setWidth(linearLayout.getMeasuredWidth());
        popupWindow.showAtLocation(linearLayout,Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);


        upload_using_camera = customView.findViewById(R.id.id_upload_using_camera);
        upload_using_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(upload_item_Activity.this,Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(upload_item_Activity.this,new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        upload_from_gallery = customView.findViewById(R.id.id_upload_from_gallery);
        upload_from_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLERY_REQUEST);
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
            if(popupWindow.isShowing()){
                popupWindow.dismiss();
                return true;
            }
            Intent intent = new Intent(upload_item_Activity.this,Home.class);
            startActivity(intent);
            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("upload_item_activity : "," in onActivityResult()...");
        popupWindow.dismiss();

        if(requestCode == CAMERA_REQUEST) {

            if (resultCode == Activity.RESULT_OK && count_image == 0) {
                Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                uploaded_photo1.setImageBitmap(photo);
                uploaded_photo2.setVisibility(View.VISIBLE);
            } else if (resultCode == Activity.RESULT_OK && count_image == 1) {
                Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                uploaded_photo2.setImageBitmap(photo);
                uploaded_photo3.setVisibility(View.VISIBLE);
            } else if (resultCode == Activity.RESULT_OK && count_image == 2) {
                Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                uploaded_photo3.setImageBitmap(photo);
            }
        }
        else if(requestCode == GALLERY_REQUEST) {

            if (resultCode == Activity.RESULT_OK && count_image == 0) {
                mImageUri = data.getData();
                //Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                uploaded_photo1.setImageURI(mImageUri);
                uploaded_photo2.setVisibility(View.VISIBLE);
            } else if (resultCode == Activity.RESULT_OK && count_image == 1) {
                mImageUri = data.getData();
                //Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                uploaded_photo2.setImageURI(mImageUri);
                uploaded_photo3.setVisibility(View.VISIBLE);
            } else if (resultCode == Activity.RESULT_OK && count_image == 2) {
                mImageUri = data.getData();
                //Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                uploaded_photo3.setImageURI(mImageUri);
            }

        }
    }

    //method to hide keyboard
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

}
