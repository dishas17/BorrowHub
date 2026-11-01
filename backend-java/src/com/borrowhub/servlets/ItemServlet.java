package com.borrowhub.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.borrowhub.dao.ItemDAO;
import com.borrowhub.models.Item;
import com.borrowhub.models.Student;


@WebServlet(name = "ItemServlet", urlPatterns = {"/items", "/items/new", "/items/delete"})
public class ItemServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ItemDAO itemDAO;

    @Override
    public void init() {
        itemDAO = new ItemDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String category = request.getParameter("category");
        String query = request.getParameter("q");
        String status = request.getParameter("status");

        if ("detail".equalsIgnoreCase(action)) {
            try {
                int itemId = Integer.parseInt(request.getParameter("id"));
                Item item = itemDAO.getItemById(itemId);
                request.setAttribute("item", item);
                request.getRequestDispatcher("/views/item-detail.jsp").forward(request, response);
                return;
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/items");
                return;
            }
        }

        List<Item> items = itemDAO.searchItems(query, category, status);
        request.setAttribute("items", items);
        request.setAttribute("selectedCategory", category != null ? category : "All");
        request.setAttribute("searchQuery", query != null ? query : "");
        request.getRequestDispatcher("/views/browse.jsp").forward(request, response);
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

        if ("delete".equalsIgnoreCase(action)) {
            int itemId = Integer.parseInt(request.getParameter("item_id"));
            Item item = itemDAO.getItemById(itemId);
            // Allow deletion if owner or admin
            if (item != null && (item.getOwnerId() == currentUser.getStudentId() || "ADMIN".equalsIgnoreCase(currentUser.getRole()))) {
                itemDAO.deleteItem(itemId);
            }
            response.sendRedirect(request.getContextPath() + "/index.html?msg=item_deleted#dashboard");
            return;
        }

        // Add new item
        String itemName = request.getParameter("item_name");
        String category = request.getParameter("category");
        String description = request.getParameter("description");
        String rateInfo = request.getParameter("rate_info");
        String location = request.getParameter("location");
        String itemCondition = request.getParameter("item_condition");
        String imageUrl = request.getParameter("image_url");

        if (itemName == null || category == null || description == null) {
            response.sendRedirect(request.getContextPath() + "/index.html?error=missing_item_data");
            return;
        }

        Item item = new Item();
        item.setOwnerId(currentUser.getStudentId());
        item.setItemName(itemName.trim());
        item.setCategory(category.trim());
        item.setDescription(description.trim());
        item.setRateInfo("Free to borrow");
        item.setLocation("");
        item.setItemCondition(itemCondition != null ? itemCondition.trim() : "Good Condition");
        item.setImageUrl(imageUrl != null && !imageUrl.trim().isEmpty() ? imageUrl.trim() : "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600");
        item.setAvailabilityStatus("AVAILABLE");

        boolean success = itemDAO.addItem(item);
        if (success) {
            response.sendRedirect(request.getContextPath() + "/index.html?msg=item_added#browse");
        } else {
            response.sendRedirect(request.getContextPath() + "/index.html?error=add_failed");
        }
    }
}
