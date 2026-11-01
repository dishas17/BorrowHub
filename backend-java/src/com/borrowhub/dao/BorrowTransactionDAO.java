package com.borrowhub.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.borrowhub.models.BorrowTransaction;


public class BorrowTransactionDAO {

    /**
     * Creates a transaction once a borrow request is approved.
     * Also automatically marks the item as BORROWED.
     */
    public boolean createTransaction(int requestId, Date borrowDate, Date dueDate) {
        String sqlTrans = "INSERT INTO Borrow_Transactions (request_id, borrowed_date, due_date, transaction_status) VALUES (?, ?, ?, 'ACTIVE')";
        String sqlItem = "UPDATE Items i JOIN Borrow_Requests r ON i.item_id = r.item_id SET i.availability_status = 'BORROWED' WHERE r.request_id = ?";
        
        Connection conn = null;
        PreparedStatement stmtTrans = null;
        PreparedStatement stmtItem = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            stmtTrans = conn.prepareStatement(sqlTrans);
            stmtTrans.setInt(1, requestId);
            stmtTrans.setDate(2, borrowDate);
            stmtTrans.setDate(3, dueDate);
            stmtTrans.executeUpdate();

            stmtItem = conn.prepareStatement(sqlItem);
            stmtItem.setInt(1, requestId);
            stmtItem.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
        } finally {
            DBConnection.close(stmtTrans, stmtItem, conn);
        }
        return false;
    }

    /**
     * Marks an item as returned and restores item availability to AVAILABLE.
     */
    public boolean returnItem(int transactionId, Date actualReturnDate) {
        String sqlTrans = "UPDATE Borrow_Transactions SET actual_return_date = ?, transaction_status = 'RETURNED', lender_confirmed_return = TRUE WHERE transaction_id = ?";
        String sqlItem = "UPDATE Items i " +
                         "JOIN Borrow_Requests r ON i.item_id = r.item_id " +
                         "JOIN Borrow_Transactions t ON r.request_id = t.request_id " +
                         "SET i.availability_status = 'AVAILABLE' WHERE t.transaction_id = ?";
        Connection conn = null;
        PreparedStatement stmtTrans = null;
        PreparedStatement stmtItem = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            stmtTrans = conn.prepareStatement(sqlTrans);
            stmtTrans.setDate(1, actualReturnDate);
            stmtTrans.setInt(2, transactionId);
            stmtTrans.executeUpdate();

            stmtItem = conn.prepareStatement(sqlItem);
            stmtItem.setInt(1, transactionId);
            stmtItem.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
        } finally {
            DBConnection.close(stmtTrans, stmtItem, conn);
        }
        return false;
    }

    /**
     * Gets all active transactions for a student (either borrower or lender).
     */
    public List<BorrowTransaction> getTransactionsByStudent(int studentId) {
        List<BorrowTransaction> list = new ArrayList<>();
        String sql = "SELECT t.*, r.item_id, r.borrower_id, i.item_name, i.image_url, i.owner_id, " +
                     "b.name AS borrower_name, o.name AS owner_name " +
                     "FROM Borrow_Transactions t " +
                     "JOIN Borrow_Requests r ON t.request_id = r.request_id " +
                     "JOIN Items i ON r.item_id = i.item_id " +
                     "JOIN Students b ON r.borrower_id = b.student_id " +
                     "JOIN Students o ON i.owner_id = o.student_id " +
                     "WHERE r.borrower_id = ? OR i.owner_id = ? " +
                     "ORDER BY t.transaction_id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            stmt.setInt(2, studentId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
        return list;
    }

    /**
     * Gets all campus transactions for Admin monitoring.
     */
    public List<BorrowTransaction> getAllTransactions() {
        List<BorrowTransaction> list = new ArrayList<>();
        String sql = "SELECT t.*, r.item_id, r.borrower_id, i.item_name, i.image_url, i.owner_id, " +
                     "b.name AS borrower_name, o.name AS owner_name " +
                     "FROM Borrow_Transactions t " +
                     "JOIN Borrow_Requests r ON t.request_id = r.request_id " +
                     "JOIN Items i ON r.item_id = i.item_id " +
                     "JOIN Students b ON r.borrower_id = b.student_id " +
                     "JOIN Students o ON i.owner_id = o.student_id " +
                     "ORDER BY t.transaction_id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
        return list;
    }

    private BorrowTransaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        BorrowTransaction t = new BorrowTransaction();
        t.setTransactionId(rs.getInt("transaction_id"));
        t.setRequestId(rs.getInt("request_id"));
        t.setItemId(rs.getInt("item_id"));
        t.setItemName(rs.getString("item_name"));
        t.setItemImageUrl(rs.getString("image_url"));
        t.setBorrowerId(rs.getInt("borrower_id"));
        t.setBorrowerName(rs.getString("borrower_name"));
        t.setOwnerId(rs.getInt("owner_id"));
        t.setOwnerName(rs.getString("owner_name"));
        t.setBorrowedDate(rs.getDate("borrowed_date"));
        t.setDueDate(rs.getDate("due_date"));
        t.setActualReturnDate(rs.getDate("actual_return_date"));
        t.setTransactionStatus(rs.getString("transaction_status"));
        t.setLenderConfirmedReturn(rs.getBoolean("lender_confirmed_return"));
        t.setBorrowerNotes(rs.getString("borrower_notes"));
        t.setCreatedAt(rs.getTimestamp("created_at"));
        return t;
    }
}
