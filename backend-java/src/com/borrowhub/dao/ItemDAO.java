package com.borrowhub.dao;

import com.borrowhub.models.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for campus items and inventory.
 */
public class ItemDAO {

    /**
     * Lists a new item from a student.
     */
    public boolean addItem(Item item) {
        String sql = "INSERT INTO Items (owner_id, item_name, category, description, rate_info, location, item_condition, image_url, availability_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, item.getOwnerId());
            stmt.setString(2, item.getItemName());
            stmt.setString(3, item.getCategory());
            stmt.setString(4, item.getDescription());
            stmt.setString(5, item.getRateInfo());
            stmt.setString(6, item.getLocation());
            stmt.setString(7, item.getItemCondition());
            stmt.setString(8, item.getImageUrl());
            stmt.setString(9, item.getAvailabilityStatus() != null ? item.getAvailabilityStatus() : "AVAILABLE");

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    item.setItemId(rs.getInt(1));
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

    /**
     * Retrieves all items joined with owner information.
     */
    public List<Item> getAllItems() {
        return searchItems(null, null, null);
    }

    /**
     * Searches items with flexible filters: query string, category, availability status.
     */
    public List<Item> searchItems(String query, String category, String status) {
        List<Item> items = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT i.*, s.name AS owner_name, s.department AS owner_dept, s.year_of_study AS owner_year " +
            "FROM Items i JOIN Students s ON i.owner_id = s.student_id WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (LOWER(i.item_name) LIKE ? OR LOWER(i.description) LIKE ? OR LOWER(i.category) LIKE ?)");
            String q = "%" + query.trim().toLowerCase() + "%";
            params.add(q);
            params.add(q);
            params.add(q);
        }

        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("All")) {
            sql.append(" AND i.category = ?");
            params.add(category.trim());
        }

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("All")) {
            sql.append(" AND i.availability_status = ?");
            params.add(status.trim().toUpperCase());
        }

        sql.append(" ORDER BY i.item_id DESC");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            rs = stmt.executeQuery();
            while (rs.next()) {
                Item item = mapResultSetToItem(rs);
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
        return items;
    }

    /**
     * Fetches single item by its ID.
     */
    public Item getItemById(int itemId) {
        String sql = "SELECT i.*, s.name AS owner_name, s.department AS owner_dept, s.year_of_study AS owner_year " +
                     "FROM Items i JOIN Students s ON i.owner_id = s.student_id WHERE i.item_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, itemId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToItem(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
        return null;
    }

    /**
     * Fetches all items listed by a specific student.
     */
    public List<Item> getItemsByOwner(int ownerId) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.*, s.name AS owner_name, s.department AS owner_dept, s.year_of_study AS owner_year " +
                     "FROM Items i JOIN Students s ON i.owner_id = s.student_id WHERE i.owner_id = ? ORDER BY i.item_id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, ownerId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
        return items;
    }

    /**
     * Updates item availability (e.g., 'AVAILABLE', 'BORROWED', 'UNAVAILABLE').
     */
    public boolean updateItemStatus(int itemId, String status) {
        String sql = "UPDATE Items SET availability_status = ? WHERE item_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, itemId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(stmt, conn);
        }
        return false;
    }

    /**
     * Deletes item from listing (admin or owner).
     */
    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM Items WHERE item_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, itemId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(stmt, conn);
        }
        return false;
    }

    private Item mapResultSetToItem(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setItemId(rs.getInt("item_id"));
        item.setOwnerId(rs.getInt("owner_id"));
        item.setItemName(rs.getString("item_name"));
        item.setCategory(rs.getString("category"));
        item.setDescription(rs.getString("description"));
        item.setRateInfo(rs.getString("rate_info"));
        item.setLocation(rs.getString("location"));
        item.setItemCondition(rs.getString("item_condition"));
        item.setImageUrl(rs.getString("image_url"));
        item.setAvailabilityStatus(rs.getString("availability_status"));
        item.setCreatedAt(rs.getTimestamp("created_at"));
        item.setOwnerName(rs.getString("owner_name"));
        item.setOwnerDepartment(rs.getString("owner_dept"));
        item.setOwnerLocation(rs.getString("location"));
        return item;
    }
}
