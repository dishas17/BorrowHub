package com.borrowhub.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.borrowhub.dao.BorrowTransactionDAO;
import com.borrowhub.dao.ItemDAO;
import com.borrowhub.dao.StudentDAO;
import com.borrowhub.models.BorrowTransaction;
import com.borrowhub.models.Item;
import com.borrowhub.models.Student;

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin", "/admin/delete-item", "/admin/students"})
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private StudentDAO studentDAO;
    private ItemDAO itemDAO;
    private BorrowTransactionDAO transactionDAO;

    @Override
    public void init() {
        studentDAO = new StudentDAO();
        itemDAO = new ItemDAO();
        transactionDAO = new BorrowTransactionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Student currentUser = (session != null) ? (Student) session.getAttribute("user") : null;

        // Verify Admin permission
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/index.html?error=unauthorized#auth");
            return;
        }

        List<Student> students = studentDAO.getAllStudents();
        List<Item> items = itemDAO.getAllItems();
        List<BorrowTransaction> transactions = transactionDAO.getAllTransactions();

        request.setAttribute("students", students);
        request.setAttribute("items", items);
        request.setAttribute("transactions", transactions);

        request.getRequestDispatcher("/views/admin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Student currentUser = (session != null) ? (Student) session.getAttribute("user") : null;

        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin privileges required");
            return;
        }

        String action = request.getParameter("action");
        if ("delete_item".equalsIgnoreCase(action)) {
            int itemId = Integer.parseInt(request.getParameter("item_id"));
            itemDAO.deleteItem(itemId);
            response.sendRedirect(request.getContextPath() + "/admin?msg=item_removed");
        }
    }
}
