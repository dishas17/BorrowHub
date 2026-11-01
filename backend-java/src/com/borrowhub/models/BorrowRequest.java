package com.borrowhub.models;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class BorrowRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int requestId;
    private int itemId;
    private String itemName;
    private String itemImageUrl;
    private int borrowerId;
    private String borrowerName;
    private String borrowerEmail;
    private String borrowerCollegeId;
    private int ownerId;
    private String ownerName;
    private Date requestDate;
    private Date returnDate;
    private String purpose;
    private String requestStatus; // "PENDING", "APPROVED", "REJECTED", "CANCELLED"
    private Timestamp createdAt;

    public BorrowRequest() {}

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemImageUrl() { return itemImageUrl; }
    public void setItemImageUrl(String itemImageUrl) { this.itemImageUrl = itemImageUrl; }

    public int getBorrowerId() { return borrowerId; }
    public void setBorrowerId(int borrowerId) { this.borrowerId = borrowerId; }

    public String getBorrowerName() { return borrowerName; }
    public void setBorrowerName(String borrowerName) { this.borrowerName = borrowerName; }

    public String getBorrowerEmail() { return borrowerEmail; }
    public void setBorrowerEmail(String borrowerEmail) { this.borrowerEmail = borrowerEmail; }

    public String getBorrowerCollegeId() { return borrowerCollegeId; }
    public void setBorrowerCollegeId(String borrowerCollegeId) { this.borrowerCollegeId = borrowerCollegeId; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public Date getRequestDate() { return requestDate; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }

    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getRequestStatus() { return requestStatus; }
    public void setRequestStatus(String requestStatus) { this.requestStatus = requestStatus; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
