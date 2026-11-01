package com.borrowhub.servlets;

import java.io.IOException;
import java.sql.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.borrowhub.dao.BorrowTransactionDAO;
import com.borrowhub.models.Student;


@WebServlet(name = "ReturnServlet", urlPatterns = {"/return", "/return/confirm"})
public class ReturnServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BorrowTransactionDAO transactionDAO;

    @Override
    public void init() {
        transactionDAO = new BorrowTransactionDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Student currentUser = (session != null) ? (Student) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/index.html?error=login_required#auth");
            return;
        }

        try {
            int transactionId = Integer.parseInt(request.getParameter("transaction_id"));
            Date today = new Date(System.currentTimeMillis());

            boolean success = transactionDAO.returnItem(transactionId, today);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/index.html?msg=return_successful#dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/index.html?error=return_failed#dashboard");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/index.html?error=invalid_request#dashboard");
        }
    }
}
