package com.borrowhub.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.borrowhub.dao.StudentDAO;
import com.borrowhub.models.Student;


@WebServlet(name = "AuthServlet", urlPatterns = {"/auth", "/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private StudentDAO studentDAO;

    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            String path = request.getServletPath();
            if ("/logout".equals(path)) action = "logout";
            else if ("/login".equals(path)) action = "login_page";
            else if ("/register".equals(path)) action = "register_page";
        }

        if ("logout".equalsIgnoreCase(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/index.html?msg=logged_out");
        } else {
            response.sendRedirect(request.getContextPath() + "/index.html#auth");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("register".equalsIgnoreCase(action)) {
            handleRegister(request, response);
        } else if ("login".equalsIgnoreCase(action)) {
            handleLogin(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/index.html");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/index.html?error=missing_credentials#auth");
            return;
        }

        Student student = studentDAO.login(email.trim(), password);
        if (student != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", student);
            session.setAttribute("studentId", student.getStudentId());
            session.setAttribute("userName", student.getName());
            session.setAttribute("userRole", student.getRole());

            if ("ADMIN".equalsIgnoreCase(student.getRole())) {
                response.sendRedirect(request.getContextPath() + "/admin");
            } else {
                response.sendRedirect(request.getContextPath() + "/index.html?msg=welcome_back");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/index.html?error=invalid_credentials#auth");
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String department = request.getParameter("department");
        String collegeId = request.getParameter("college_id");
        String yearOfStudy = request.getParameter("year_of_study");
        String phone = request.getParameter("phone");

        if (name == null || email == null || password == null || collegeId == null) {
            response.sendRedirect(request.getContextPath() + "/index.html?error=incomplete_form#auth");
            return;
        }

        Student s = new Student();
        s.setName(name.trim());
        s.setEmail(email.trim().toLowerCase());
        s.setDepartment(department != null ? department.trim() : "General Science");
        s.setCollegeId(collegeId.trim().toUpperCase());
        s.setYearOfStudy(yearOfStudy != null ? yearOfStudy : "1st Year");
        s.setPhone(phone != null ? phone.trim() : "");
        s.setRole("STUDENT");

        boolean success = studentDAO.register(s, password);
        if (success) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", s);
            session.setAttribute("studentId", s.getStudentId());
            session.setAttribute("userName", s.getName());
            session.setAttribute("userRole", s.getRole());
            response.sendRedirect(request.getContextPath() + "/index.html?msg=account_created");
        } else {
            response.sendRedirect(request.getContextPath() + "/index.html?error=registration_failed#auth");
        }
    }
}
