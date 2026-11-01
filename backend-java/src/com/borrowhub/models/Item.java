package com.borrowhub.models;

import java.io.Serializable;
import java.sql.Timestamp;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private int itemId;
    private int ownerId;
    private String ownerName;
    private String ownerDepartment;
    private String ownerLocation;
    private String itemName;
    private String category;
    private String description;
    private String rateInfo; 
    private String location;
    private String itemCondition;
    private String imageUrl;
    private String availabilityStatus; // "AVAILABLE", "BORROWED", "UNAVAILABLE"
    private Timestamp createdAt;

    public Item() {}

    public Item(int itemId, int ownerId, String itemName, String category, 
                String description, String rateInfo, String location, 
                String itemCondition, String imageUrl, String availabilityStatus) {
        this.itemId = itemId;
        this.ownerId = ownerId;
        this.itemName = itemName;
        this.category = category;
        this.description = description;
        this.rateInfo = rateInfo;
        this.location = location;
        this.itemCondition = itemCondition;
        this.imageUrl = imageUrl;
        this.availabilityStatus = availabilityStatus;
    }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerDepartment() { return ownerDepartment; }
    public void setOwnerDepartment(String ownerDepartment) { this.ownerDepartment = ownerDepartment; }

    public String getOwnerLocation() { return ownerLocation; }
    public void setOwnerLocation(String ownerLocation) { this.ownerLocation = ownerLocation; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRateInfo() { return rateInfo; }
    public void setRateInfo(String rateInfo) { this.rateInfo = rateInfo; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getItemCondition() { return itemCondition; }
    public void setItemCondition(String itemCondition) { this.itemCondition = itemCondition; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
