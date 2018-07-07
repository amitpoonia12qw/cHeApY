package com.example.android.cheapy;

public class product_details {

    private String storeName;
    private String productName;
    private int productPrice;
    private String productDescription;
    private String productEncodedImage;
    private String productBuyLink;

   //public product_details() { }

    public product_details(String storeName, String productName, int productPrice, String productEncodedImage, String productDescription, String buyLink) {
        this.storeName = storeName;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productDescription = productDescription;
        this.productEncodedImage = productEncodedImage;
        this.productBuyLink = buyLink;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductEncodedImage() {
        return productEncodedImage;
    }

    public void setProductEncodedImage(String productEncodedImage) {
        this.productEncodedImage = productEncodedImage;
    }

    public String getProductBuyLink() {
        return productBuyLink;
    }

    public void setProductBuyLink(String productBuyLink) {
        this.productBuyLink = productBuyLink;
    }

    @Override
    public String toString() {
        return "Store : " +storeName +"\n Product : "+ productName +" \n Price : "+ productPrice + "\n Description : " + productDescription+ "\n BuyLink : " +productBuyLink + "\n ImageURL : " + productEncodedImage;
    }
}

