package com.borrowhub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.borrowhub.models.BorrowRequest;


public class BorrowRequestDAO {

    public boolean createRequest(BorrowRequest req) {
        String sql = "INSERT INTO Borrow_Requests (item_id, borrower_id, request_date, return_date, purpose, request_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, req.getItemId());
            stmt.setInt(2, req.getBorrowerId());
            stmt.setDate(3, req.getRequestDate());
            stmt.setDate(4, req.getReturnDate());
            stmt.setString(5, req.getPurpose());
            stmt.setString(6, req.getRequestStatus() != null ? req.getRequestStatus() : "PENDING");

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    req.setRequestId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(stmt, conn);
        }
        return false;
    }

    public List<BorrowRequest> getRequestsByOwner(int ownerId) {
        List<BorrowRequest> list = new ArrayList<>();
        String sql = "SELECT r.*, i.item_name, i.image_url, i.owner_id, " +
                     "s.name AS borrower_name, s.email AS borrower_email, s.college_id AS borrower_college_id " +
                     "FROM Borrow_Requests r " +
                     "JOIN Items i ON r.item_id = i.item_id " +
                     "JOIN Students s ON r.borrower_id = s.student_id " +
                     "WHERE i.owner_id = ? ORDER BY r.request_id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, ownerId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
        return list;
    }

    public List<BorrowRequest> getRequestsByBorrower(int borrowerId) {
        List<BorrowRequest> list = new ArrayList<>();
        String sql = "SELECT r.*, i.item_name, i.image_url, i.owner_id, " +
                     "o.name AS owner_name, " +
                     "s.name AS borrower_name, s.email AS borrower_email, s.college_id AS borrower_college_id " +
                     "FROM Borrow_Requests r " +
                     "JOIN Items i ON r.item_id = i.item_id " +
                     "JOIN Students o ON i.owner_id = o.student_id " +
                     "JOIN Students s ON r.borrower_id = s.student_id " +
                     "WHERE r.borrower_id = ? ORDER BY r.request_id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, borrowerId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                BorrowRequest req = mapResultSetToRequest(rs);
                req.setOwnerName(rs.getString("owner_name"));
                list.add(req);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
        return list;
    }

    public BorrowRequest getRequestById(int requestId) {
        String sql = "SELECT r.*, i.item_name, i.image_url, i.owner_id, " +
                     "s.name AS borrower_name, s.email AS borrower_email, s.college_id AS borrower_college_id " +
                     "FROM Borrow_Requests r " +
                     "JOIN Items i ON r.item_id = i.item_id " +
                     "JOIN Students s ON r.borrower_id = s.student_id " +
                     "WHERE r.request_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, requestId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToRequest(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
        return null;
    }

    /**
     * Updates request status ('APPROVED', 'REJECTED', 'CANCELLED').
     */
    public boolean updateRequestStatus(int requestId, String status) {
        String sql = "UPDATE Borrow_Requests SET request_status = ? WHERE request_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(stmt, conn);
        }
        return false;
    }

    private BorrowRequest mapResultSetToRequest(ResultSet rs) throws SQLException {
        BorrowRequest req = new BorrowRequest();
        req.setRequestId(rs.getInt("request_id"));
        req.setItemId(rs.getInt("item_id"));
        req.setItemName(rs.getString("item_name"));
        req.setItemImageUrl(rs.getString("image_url"));
        req.setBorrowerId(rs.getInt("borrower_id"));
        req.setBorrowerName(rs.getString("borrower_name"));
        req.setBorrowerEmail(rs.getString("borrower_email"));
        req.setBorrowerCollegeId(rs.getString("borrower_college_id"));
        req.setOwnerId(rs.getInt("owner_id"));
        req.setRequestDate(rs.getDate("request_date"));
        req.setReturnDate(rs.getDate("return_date"));
        req.setPurpose(rs.getString("purpose"));
        req.setRequestStatus(rs.getString("request_status"));
        req.setCreatedAt(rs.getTimestamp("created_at"));
        return req;
    }
}
