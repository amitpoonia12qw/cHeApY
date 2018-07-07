package com.example.android.cheapy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

public class sqlite_Handler extends SQLiteOpenHelper {

    //database name and version
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "storeDetails2";


    //table_name and columns of table for store_details....remove these when we get data from server
    private static final String STORE_DETAILS_TABLE = "storeDetails";
    private static final String ID = "id";
    private static final String MANAGER = "managerName";
    private static final String Email = "email";
    private static final String CONTACT = "mobileNo";
    private static final String PASSWORD = "password";
    private static final String STORE_NAME = "storeName";
    private static final String ADDRESS = "address";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String NUMBEROFPRODUCTS = "numberOfProducts";


    //table to check if a user is logged in......don't delete tis table even if we get data from server
    private static final String CURRENT_USER_TABLE = "CurrentUserDetails";
    private static final String currentUser = "currentUser";
    private static final String sNo = "serialNumber";

    //table for all products
    private static final String AllProduct_table_name = "AllProductDetails";
    private static final String ProductId = "serialNumber";
    private static final String StoreName = "storeName";
    private static final String item = "item" ;
    private static final String Image = "Image";
    private static final String Description = "Description";
    private static final String Price = "Price";
    private static final String BuyLink = "buyLink";

    //create table for online stores


    //constructor....compulsory!!
    public sqlite_Handler(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //query to create table STORE DETAILS
        String create_store_details_table = "CREATE TABLE " + STORE_DETAILS_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + MANAGER + " text, " + Email + " TEXT, " + CONTACT + " TEXT, " + PASSWORD + " TEXT, " + STORE_NAME + " TEXT, " + ADDRESS + " TEXT, " + LATITUDE + " double, " + LONGITUDE + " double, " + NUMBEROFPRODUCTS + " INTEGER " + ");" ;
        String create_ifUserLoggedIn_table = "CREATE TABLE "+ CURRENT_USER_TABLE + "(" + sNo + " INTEGER, " + currentUser + " text);";
        String create_AllProduct_table = "CREATE TABLE "+ AllProduct_table_name+ "(" + ProductId + " INTEGER PRIMARY KEY AUTOINCREMENT, " + StoreName + " text, " + item + " text, "
                + Price + " INTEGER, " + Image + " TEXT, " + Description + " TEXT, " + BuyLink + " TEXT);";


        Log.d("sqlite_Handler","creating your database");
        db.execSQL(create_store_details_table);
        db.execSQL(create_ifUserLoggedIn_table);
        db.execSQL(create_AllProduct_table);
        Log.d("sqlite_Handler","database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + STORE_DETAILS_TABLE);
        //onCreate(db);
    }


    public void addToAllProductTable(product_details product_details){

        SQLiteDatabase database = sqlite_Handler.this.getWritableDatabase();

       // database.execSQL("DROP TABLE IF EXISTS " + AllProduct_table_name);

        database.execSQL("CREATE TABLE IF NOT EXISTS " + AllProduct_table_name + "(" + ProductId + " INTEGER PRIMARY KEY AUTOINCREMENT, " + StoreName + " text, " + item + " text, "
                + Price + " INTEGER, " + Image + " TEXT, " + Description + " TEXT, " + BuyLink + " TEXT);");


            ContentValues values = new ContentValues();

            values.put(StoreName,product_details.getStoreName());
            values.put(item, product_details.getProductName());
            values.put(Price, product_details.getProductPrice());
            values.put(Image, product_details.getProductEncodedImage());
            values.put(Description, product_details.getProductDescription());
            values.put(BuyLink, product_details.getProductBuyLink());


            database.insert(AllProduct_table_name, null, values);
            database.close();

    }

    // get data from the database
    public product_details[] getProductDetails(String storename){
        SQLiteDatabase database = sqlite_Handler.this.getReadableDatabase();


        Cursor cursor = database.query(
                AllProduct_table_name,
                new String[]{ProductId,StoreName,item,Price,Image,Description,BuyLink},
                StoreName + "=?",
                new String[]{String.valueOf(storename)},
                null, null, null, null
        );


        Log.d("sqliteHandler.... : "," No of Rows.... = " + cursor.getCount());

        cursor.moveToFirst();
        product_details[] productDetails = new product_details[cursor.getCount()];
        int count = 0;

        do{
            if (cursor.getColumnCount() == 7) {

                productDetails[count] = new product_details(cursor.getString(1),
                        cursor.getString(2), Integer.parseInt(cursor.getString(3)), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                count++;

            }
        }while(cursor.moveToNext());

            cursor.close();
        return productDetails;
    }

    // get all product_from_offline_stores from the AllProductTable
    public product_details[] getAllOfflineProductDetails(){
        SQLiteDatabase database = sqlite_Handler.this.getReadableDatabase();

        Cursor cursor = database.query(
                AllProduct_table_name,
                new String[]{ProductId,StoreName,item,Price,Image,Description,BuyLink},
                BuyLink + "=?",
                new String[]{String.valueOf("OFFLINE")},
                null, null, null, null
        );


        Log.d("sqliteHandler.... : "," No of Rows.... = " + cursor.getCount());

        cursor.moveToFirst();
        product_details[] productDetails = new product_details[cursor.getCount()];
        int count = 0;

        do{
            if (cursor.getColumnCount() == 7) {

                productDetails[count] = new product_details(cursor.getString(1),
                        cursor.getString(2), Integer.parseInt(cursor.getString(3)), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                count++;

            }
        }while(cursor.moveToNext());

        cursor.close();
        return productDetails;

    }


    //Add data to isUserLoggedIn table
    public void addCurrentUser(String currentUserName){
        if(getCurrentUserCount() == 0){
            SQLiteDatabase database = sqlite_Handler.this.getWritableDatabase();
            if (database != null) {
                ContentValues values = new ContentValues();

                values.put(sNo,0);
                values.put(currentUser, currentUserName);

                database.insert(CURRENT_USER_TABLE, null, values);
                Log.d("sqlite_Handler", "currentUser state added successfully");
            }

            //print all from currentUserTable
            Log.d("sqlite_handler : ","here is all the data from database :-> \n");
            assert database != null;
            Log.d("sqlite_handler : ",getTableAsString(database,CURRENT_USER_TABLE +  "\n"));
            Log.d("sqlite_handler : ",getTableAsString(database,STORE_DETAILS_TABLE));
            database.close();
        }
    }
    //remove data from isUserLoggedIn table if user get logged out
    public void deleteCurrentUser(){
        SQLiteDatabase database  = this.getWritableDatabase();
        database.delete(CURRENT_USER_TABLE, sNo+ "=?", new String[]{String.valueOf(0)});
        //it means user is logged out
        Log.d("sqlite_Handler : ","user got logged out");
        Log.d("sqlite_handler : ","here is all the data from database :-> \n");
        Log.d("sqlite_handler : ",getTableAsString(database,CURRENT_USER_TABLE +  "\n"));
        Log.d("sqlite_handler : ",getTableAsString(database,STORE_DETAILS_TABLE));
        database.close();
    }

    //get currentUser from CURRENT_USER_TABLE
    public String getCurrentUser(){
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(
                CURRENT_USER_TABLE,
                new String[]{sNo,currentUser},
                sNo + "=?",
                new String[]{String.valueOf(0)},
                null, null, null
        );
        cursor.moveToFirst();
        String currentUserName = cursor.getString(1);
        cursor.close();
        return currentUserName;
    }
    //get storeName given userName
    public String getStoreNameForUserName(String username){
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(
                STORE_DETAILS_TABLE,
                new String[]{ID,MANAGER,Email,CONTACT,PASSWORD,STORE_NAME,ADDRESS,LATITUDE,LONGITUDE,NUMBEROFPRODUCTS},
                Email + "=?",
                new String[]{String.valueOf(username)},
                null, null, null
        );
        cursor.moveToFirst();
        String StoreName = cursor.getString(5);
        cursor.close();
        return StoreName;
    }

    //get all details about store by username
    public store_details getStoreDetailsForUsername(String username){
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(
                STORE_DETAILS_TABLE,
                new String[]{ID,MANAGER,Email,CONTACT,PASSWORD,STORE_NAME,ADDRESS,LATITUDE,LONGITUDE,NUMBEROFPRODUCTS},
                Email + "=?",
                new String[]{String.valueOf(username)},
                null, null, null
        );
        cursor.moveToFirst();
        store_details storeDetails = new store_details(cursor.getString(1),cursor.getString(2),cursor.getString(3),
                cursor.getString(4),cursor.getString(5),cursor.getString(6),new LatLng(cursor.getDouble(7),cursor.getDouble(8)),
                Integer.parseInt(cursor.getString(9)));
        cursor.close();
        return storeDetails;
    }
    //get LatLng for given storeName
    public LatLng getLatLngForStore(String storeName){
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(
                STORE_DETAILS_TABLE,
                new String[]{ID,MANAGER,Email,CONTACT,PASSWORD,STORE_NAME,ADDRESS,LATITUDE,LONGITUDE,NUMBEROFPRODUCTS},
                STORE_NAME + "=?",
                new String[]{String.valueOf(storeName)},
                null, null, null
        );

        cursor.moveToFirst();
        LatLng latLng = new LatLng(cursor.getDouble(7),cursor.getDouble(8));
        cursor.close();
        return latLng;
    }

    //Add data(type of data is  store_details-- an object of store_details class) to the database
    public void addStoreDetails(store_details storeDetails){

        Log.d("sqlite_Handler","adding details to database");
        SQLiteDatabase database = sqlite_Handler.this.getWritableDatabase();



        if(database != null) {
            ContentValues values = new ContentValues();

            values.put(MANAGER, storeDetails.getManagerName());
            values.put(Email, storeDetails.getEmail());
            values.put(CONTACT, storeDetails.getMobileNo());
            values.put(PASSWORD, storeDetails.getPassword());
            values.put(STORE_NAME, storeDetails.getStoreName());
            values.put(ADDRESS, storeDetails.getStoreAddress());
            values.put(LATITUDE, storeDetails.getStoreLatLng().latitude);
            values.put(LONGITUDE, storeDetails.getStoreLatLng().longitude);
            values.put(NUMBEROFPRODUCTS, storeDetails.getNumberOfProducts());

            database.insert(STORE_DETAILS_TABLE, null, values);

            Log.d("sqlite_Handler","details added successfully");
        }

        assert database != null;
        database.close();
    }

    // get data of a particular store from the database
    public store_details getStoreDetails(int id){
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(
                STORE_DETAILS_TABLE,
                new String[]{ID,MANAGER,Email,CONTACT,PASSWORD,STORE_NAME,ADDRESS,LATITUDE,LONGITUDE,NUMBEROFPRODUCTS},
                ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        store_details storeDetails = new store_details(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),
                cursor.getString(6),new LatLng(Double.parseDouble(cursor.getString(7)),Double.parseDouble(cursor.getString(8))),Integer.parseInt(cursor.getString(9)));

        cursor.close();

        return storeDetails;
    }

    // get all stores from the database
    public store_details[] getAllStoresDetails(){
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(
                STORE_DETAILS_TABLE,
                new String[]{ID,MANAGER,Email,CONTACT,PASSWORD,STORE_NAME,ADDRESS,LATITUDE,LONGITUDE,NUMBEROFPRODUCTS},
                null,
                null,
                null, null, null
        );
        store_details[] storeDetails = new store_details[cursor.getCount()];
        int count = 0;
        cursor.moveToFirst();
        do{
            storeDetails[count] = new store_details(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                    cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),
                    cursor.getString(6),new LatLng(Double.parseDouble(cursor.getString(7)),Double.parseDouble(cursor.getString(8))),Integer.parseInt(cursor.getString(9)));
            count++;

        }while (cursor.moveToNext());

        cursor.close();

        return storeDetails;
    }

    //update a single store
    public int updateStore(store_details storeDetails){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MANAGER, storeDetails.getManagerName());
        values.put(Email, storeDetails.getEmail());
        values.put(CONTACT, storeDetails.getMobileNo());
        values.put(STORE_NAME, storeDetails.getStoreName());
        values.put(ADDRESS, storeDetails.getStoreAddress());
        values.put(PASSWORD, storeDetails.getPassword());
        values.put(LATITUDE, storeDetails.getStoreLatLng().latitude);
        values.put(LONGITUDE, storeDetails.getStoreLatLng().longitude);
        values.put(NUMBEROFPRODUCTS, storeDetails.getNumberOfProducts());

        return database.update(STORE_DETAILS_TABLE, values, ID + "=?",
                new String[]{String.valueOf(storeDetails.getId())}
        );
    }

    //Delete a store's data from database
    public void deleteStore(store_details storeDetails){
        SQLiteDatabase database  = this.getWritableDatabase();
        database.delete(STORE_DETAILS_TABLE, ID + "=?", new String[]{String.valueOf(storeDetails.getId())});
        database.close();
    }

    //get the number of stores in database
    public int getStoreCount(){
        String queryForWholeTable = "SELECT * FROM " + STORE_DETAILS_TABLE;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(queryForWholeTable, null);
        cursor.close();
        return cursor.getCount();
    }

    //check number of rows in CURRENT_USER_TABLE. if it's 1, means user is logged in
    public int getCurrentUserCount(){
        SQLiteDatabase database = sqlite_Handler.this.getWritableDatabase();
        String queryForAllComputers = "SELECT * FROM " + CURRENT_USER_TABLE;
        Cursor cursor = database.rawQuery(queryForAllComputers, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    //method to check if given username and password exist in our database
    public boolean isDataExist(String username, String password){

        Log.d("sqlite_Handler","given data : "+username+" and "+password);
        Log.d("sqlite_Handler","checking if given data exist in our database");
        String queryForAll = "SELECT * FROM " + STORE_DETAILS_TABLE;
        SQLiteDatabase database = sqlite_Handler.this.getReadableDatabase();

        Cursor cursor = database.rawQuery(queryForAll,null);

        if(cursor.moveToFirst()){
            Log.d("sqlite_Handler","again : checking if given data exist in our database");
            do {
                Log.d("sqlite_Handler","checking with : " + cursor.getString(2) + ", " + cursor.getString(4));

                if(username.compareTo(cursor.getString(2)) == 0){
                    if (password.compareTo(cursor.getString(4)) == 0){
                        cursor.close();
                        return true;
                    }
                }
            }while(cursor.moveToNext());
        }

        cursor.close();
        return false;
    }


    //to get all rows of a table as string
    private String getTableAsString(SQLiteDatabase db, String tableName) {
        Log.d("sqlite_Handler : ", "getTableAsString called");
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }
        allRows.close();
        return tableString;
    }




}


