package com.borrowhub.models;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model representing a Student in BorrowHub.
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private int studentId;
    private String name;
    private String email;
    private String passwordHash;
    private String department;
    private String collegeId;
    private String phone;
    private String yearOfStudy;
    private String role; // "STUDENT" or "ADMIN"
    private String avatarUrl;
    private Timestamp createdAt;

    public Student() {}

    public Student(int studentId, String name, String email, String passwordHash, 
                   String department, String collegeId, String phone, 
                   String yearOfStudy, String role, String avatarUrl) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.department = department;
        this.collegeId = collegeId;
        this.phone = phone;
        this.yearOfStudy = yearOfStudy;
        this.role = role;
        this.avatarUrl = avatarUrl;
    }

    // Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCollegeId() { return collegeId; }
    public void setCollegeId(String collegeId) { this.collegeId = collegeId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(String yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
