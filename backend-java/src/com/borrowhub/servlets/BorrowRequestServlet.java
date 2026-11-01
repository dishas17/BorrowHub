package com.borrowhub.servlets;

import java.io.IOException;
import java.sql.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.borrowhub.dao.BorrowRequestDAO;
import com.borrowhub.dao.BorrowTransactionDAO;
import com.borrowhub.dao.ItemDAO;
import com.borrowhub.models.BorrowRequest;
import com.borrowhub.models.Item;
import com.borrowhub.models.Student;


@WebServlet(name = "BorrowRequestServlet", urlPatterns = {"/requests", "/requests/create", "/requests/decision"})
public class BorrowRequestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BorrowRequestDAO requestDAO;
    private BorrowTransactionDAO transactionDAO;
    private ItemDAO itemDAO;

    @Override
    public void init() {
        requestDAO = new BorrowRequestDAO();
        transactionDAO = new BorrowTransactionDAO();
        itemDAO = new ItemDAO();
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

        String action = request.getParameter("action");

        if ("create".equalsIgnoreCase(action)) {
            // Submit borrow request
            int itemId = Integer.parseInt(request.getParameter("item_id"));
            String returnDateStr = request.getParameter("return_date");
            String purpose = request.getParameter("purpose");

            Item item = itemDAO.getItemById(itemId);
            if (item == null || !"AVAILABLE".equalsIgnoreCase(item.getAvailabilityStatus())) {
                response.sendRedirect(request.getContextPath() + "/index.html?error=item_not_available");
                return;
            }

            // Student cannot borrow their own item
            if (item.getOwnerId() == currentUser.getStudentId()) {
                response.sendRedirect(request.getContextPath() + "/index.html?error=cannot_borrow_own_item");
                return;
            }

            BorrowRequest req = new BorrowRequest();
            req.setItemId(itemId);
            req.setBorrowerId(currentUser.getStudentId());
            req.setRequestDate(new Date(System.currentTimeMillis()));
            req.setReturnDate(Date.valueOf(returnDateStr));
            req.setPurpose(purpose);
            req.setRequestStatus("PENDING");

            boolean created = requestDAO.createRequest(req);
            if (created) {
                response.sendRedirect(request.getContextPath() + "/index.html?msg=request_sent#dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/index.html?error=request_failed");
            }

        } else if ("decision".equalsIgnoreCase(action)) {
            // Lender approves or rejects
            int requestId = Integer.parseInt(request.getParameter("request_id"));
            String decision = request.getParameter("decision"); // "APPROVED" or "REJECTED"

            BorrowRequest req = requestDAO.getRequestById(requestId);
            if (req == null) {
                response.sendRedirect(request.getContextPath() + "/index.html?error=not_found");
                return;
            }

            if ("APPROVED".equalsIgnoreCase(decision)) {
                requestDAO.updateRequestStatus(requestId, "APPROVED");
                Date today = new Date(System.currentTimeMillis());
                transactionDAO.createTransaction(requestId, today, req.getReturnDate());
                response.sendRedirect(request.getContextPath() + "/index.html?msg=request_approved#dashboard");
            } else {
                requestDAO.updateRequestStatus(requestId, "REJECTED");
                response.sendRedirect(request.getContextPath() + "/index.html?msg=request_rejected#dashboard");
            }
        }
    }
}
