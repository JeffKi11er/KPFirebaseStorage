package com.example.khoaphamfinaltestfirebasestorage.item;

public class ItemImage {
    private String imageName;
    private String linkName;

    public ItemImage() {
    }

    public ItemImage(String imageName, String linkName) {
        this.imageName = imageName;
        this.linkName = linkName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }
}
