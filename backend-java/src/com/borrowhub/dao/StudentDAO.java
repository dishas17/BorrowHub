package com.borrowhub.dao;

import com.borrowhub.models.Student;
import com.borrowhub.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Student authentication and profiles.
 */
public class StudentDAO {

    /**
     * Registers a new student after hashing the password.
     */
    public boolean register(Student s, String rawPassword) {
        String sql = "INSERT INTO Students (name, email, password_hash, department, college_id, phone, year_of_study, role, avatar_url) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, s.getName());
            stmt.setString(2, s.getEmail());
            stmt.setString(3, PasswordUtil.hashPassword(rawPassword));
            stmt.setString(4, s.getDepartment());
            stmt.setString(5, s.getCollegeId());
            stmt.setString(6, s.getPhone());
            stmt.setString(7, s.getYearOfStudy() != null ? s.getYearOfStudy() : "1st Year");
            stmt.setString(8, s.getRole() != null ? s.getRole() : "STUDENT");
            stmt.setString(9, s.getAvatarUrl() != null ? s.getAvatarUrl() : "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150");

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    s.setStudentId(rs.getInt(1));
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
     * Authenticates student by checking hashed password.
     */
    public Student login(String emailOrCollegeId, String rawPassword) {
        String sql = "SELECT * FROM Students WHERE email = ? OR college_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, emailOrCollegeId);
            stmt.setString(2, emailOrCollegeId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                if (PasswordUtil.verifyPassword(rawPassword, storedHash)) {
                    Student s = new Student();
                    s.setStudentId(rs.getInt("student_id"));
                    s.setName(rs.getString("name"));
                    s.setEmail(rs.getString("email"));
                    s.setDepartment(rs.getString("department"));
                    s.setCollegeId(rs.getString("college_id"));
                    s.setPhone(rs.getString("phone"));
                    s.setYearOfStudy(rs.getString("year_of_study"));
                    s.setRole(rs.getString("role"));
                    s.setAvatarUrl(rs.getString("avatar_url"));
                    s.setCreatedAt(rs.getTimestamp("created_at"));
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
        return null;
    }

    /**
     * Fetches student profile by ID.
     */
    public Student getStudentById(int studentId) {
        String sql = "SELECT * FROM Students WHERE student_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                Student s = new Student();
                s.setStudentId(rs.getInt("student_id"));
                s.setName(rs.getString("name"));
                s.setEmail(rs.getString("email"));
                s.setDepartment(rs.getString("department"));
                s.setCollegeId(rs.getString("college_id"));
                s.setPhone(rs.getString("phone"));
                s.setYearOfStudy(rs.getString("year_of_study"));
                s.setRole(rs.getString("role"));
                s.setAvatarUrl(rs.getString("avatar_url"));
                return s;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
        return null;
    }

    /**
     * Admin view: lists all campus students.
     */
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM Students ORDER BY student_id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Student s = new Student();
                s.setStudentId(rs.getInt("student_id"));
                s.setName(rs.getString("name"));
                s.setEmail(rs.getString("email"));
                s.setDepartment(rs.getString("department"));
                s.setCollegeId(rs.getString("college_id"));
                s.setPhone(rs.getString("phone"));
                s.setYearOfStudy(rs.getString("year_of_study"));
                s.setRole(rs.getString("role"));
                s.setAvatarUrl(rs.getString("avatar_url"));
                s.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
        return list;
    }
}
