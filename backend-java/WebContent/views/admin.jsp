<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>BorrowHub - Campus Admin Panel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body style="background-color: #EDE9FE; font-family: sans-serif; padding: 24px;">
    <div style="max-width: 1100px; margin: 0 auto; background: white; padding: 30px; border-radius: 20px; box-shadow: 0 10px 25px rgba(23,37,84,0.1);">
        <h1 style="color: #172554; margin-bottom: 8px;">🛡️ BorrowHub Campus Administrator Dashboard</h1>
        <p style="color: #64748B; margin-bottom: 24px;">Manage registered students, approve listings, and supervise campus item returns.</p>

        <!-- KPI Strip -->
        <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 28px;">
            <div style="background: #F8FAFC; border: 1px solid #E2E8F0; padding: 18px; border-radius: 12px;">
                <div style="font-size: 26px; font-weight: 800; color: #172554;">${students.size()}</div>
                <div style="color: #64748B; font-size: 13px;">Registered Students</div>
            </div>
            <div style="background: #F8FAFC; border: 1px solid #E2E8F0; padding: 18px; border-radius: 12px;">
                <div style="font-size: 26px; font-weight: 800; color: #172554;">${items.size()}</div>
                <div style="color: #64748B; font-size: 13px;">Campus Items Listed</div>
            </div>
            <div style="background: #F8FAFC; border: 1px solid #E2E8F0; padding: 18px; border-radius: 12px;">
                <div style="font-size: 26px; font-weight: 800; color: #10B981;">${transactions.size()}</div>
                <div style="color: #64748B; font-size: 13px;">Total Transactions</div>
            </div>
        </div>

        <h3 style="color: #172554; margin-bottom: 12px;">All Campus Items Listed</h3>
        <table class="clean-table" style="width: 100%; border-collapse: collapse; margin-bottom: 30px;">
            <thead>
                <tr style="background: #F1F5F9; text-align: left;">
                    <th style="padding: 10px;">ID</th>
                    <th style="padding: 10px;">Item Name</th>
                    <th style="padding: 10px;">Category</th>
                    <th style="padding: 10px;">Owner</th>
                    <th style="padding: 10px;">Rate</th>
                    <th style="padding: 10px;">Status</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${items}">
                    <tr style="border-bottom: 1px solid #E2E8F0;">
                        <td style="padding: 10px;">#${item.itemId}</td>
                        <td style="padding: 10px;"><strong>${item.itemName}</strong></td>
                        <td style="padding: 10px;">${item.category}</td>
                        <td style="padding: 10px;">${item.ownerName} (${item.ownerDepartment})</td>
                        <td style="padding: 10px;">${item.rateInfo}</td>
                        <td style="padding: 10px;">
                            <span class="pill-badge">${item.availabilityStatus}</span>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <a href="${pageContext.request.contextPath}/index.html" class="btn-primary" style="display: inline-block;">Return to Student Portal</a>
    </div>
</body>
</html>
