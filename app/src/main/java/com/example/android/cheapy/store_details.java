package com.example.android.cheapy;

import com.google.android.gms.maps.model.LatLng;

public class store_details {

    private int id;
    private String managerName;
    private String Email;
    private String mobileNo;
    private String password;
    private String storeName;
    private String storeAddress;
    private LatLng storeLatLng;
    private int numberOfProducts;

    // Empty Constructor
    public store_details(){
        super();
    }

    //Constructor-1
    public store_details(int id, String managerName, String email, String mobileNo,
                         String password,String storeName, String storeAddress, LatLng storeLatLng) {
        this.id = id;
        this.managerName = managerName;
        this.storeName = storeName;
        Email = email;
        this.mobileNo = mobileNo;
        this.password = password;
        this.storeAddress = storeAddress;
        this.storeLatLng = storeLatLng;
    }

    //Constructor-2
    public store_details(int id, String managerName, String email, String mobileNo,
                         String password,String storeName, String storeAddress, LatLng storeLatLng,int numberOfProducts){
        this.id = id;
        this.managerName = managerName;
        this.storeName = storeName;
        Email = email;
        this.mobileNo = mobileNo;
        this.password = password;
        this.storeAddress = storeAddress;
        this.storeLatLng = storeLatLng;
        this.numberOfProducts = numberOfProducts;
    }

    //Constructor-3
    public store_details( String managerName, String email, String mobileNo,
                         String password,String storeName, String storeAddress, LatLng storeLatLng) {
        this.managerName = managerName;
        this.storeName = storeName;
        Email = email;
        this.mobileNo = mobileNo;
        this.password = password;
        this.storeAddress = storeAddress;
        this.storeLatLng = storeLatLng;
    }

    //Constructor-4
    public store_details(String managerName, String email, String mobileNo,
                         String password,String storeName, String storeAddress, LatLng storeLatLng,int numberOfProducts){
        this.managerName = managerName;
        this.storeName = storeName;
        Email = email;
        this.mobileNo = mobileNo;
        this.password = password;
        this.storeAddress = storeAddress;
        this.storeLatLng = storeLatLng;
        this.numberOfProducts = numberOfProducts;
    }
    //getter and setter method

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public LatLng getStoreLatLng() {
        return storeLatLng;
    }

    public void setStoreLatLng(LatLng storeLatLng) {
        this.storeLatLng = storeLatLng;
    }

    public int getNumberOfProducts() {
        return numberOfProducts;
    }

    public void setNumberOfProducts(int numberOfProducts) {
        this.numberOfProducts = numberOfProducts;
    }

    @Override
    public String toString() {
        return "STORE_DETAILS : \n Manager : " +managerName +"\nEmail : "+ Email +"\nMobile No. : "+ mobileNo +"\nPassword : "+ password +"\nStore Name : "+ storeName +"\nAddress : "+ storeAddress +"\nLatLng : "+ storeLatLng +"\nNo. of products : "+ numberOfProducts;
    }
}
