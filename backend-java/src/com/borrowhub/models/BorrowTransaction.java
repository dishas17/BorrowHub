package com.borrowhub.models;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;


public class BorrowTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private int transactionId;
    private int requestId;
    private int itemId;
    private String itemName;
    private String itemImageUrl;
    private int borrowerId;
    private String borrowerName;
    private int ownerId;
    private String ownerName;
    private Date borrowedDate;
    private Date dueDate;
    private Date actualReturnDate;
    private String transactionStatus; // "ACTIVE", "RETURNED", "OVERDUE"
    private boolean lenderConfirmedReturn;
    private String borrowerNotes;
    private Timestamp createdAt;

    public BorrowTransaction() {}

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

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

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public Date getBorrowedDate() { return borrowedDate; }
    public void setBorrowedDate(Date borrowedDate) { this.borrowedDate = borrowedDate; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public Date getActualReturnDate() { return actualReturnDate; }
    public void setActualReturnDate(Date actualReturnDate) { this.actualReturnDate = actualReturnDate; }

    public String getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }

    public boolean isLenderConfirmedReturn() { return lenderConfirmedReturn; }
    public void setLenderConfirmedReturn(boolean lenderConfirmedReturn) { this.lenderConfirmedReturn = lenderConfirmedReturn; }

    public String getBorrowerNotes() { return borrowerNotes; }
    public void setBorrowerNotes(String borrowerNotes) { this.borrowerNotes = borrowerNotes; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
