# 📖 BorrowHub — Campus Resource Sharing

> **"Borrow what you need. Share what you have."**  
> *A peer-to-peer campus item lending and borrowing web application tailored for college students and faculty.*

---

## 🎨 Color Palette & Design Identity

As specified in the project requirements:
- **Background**: Soft Lavender (`#EDE9FE`) across all views for a clean, comforting, modern feel.
- **Buttons & Accents**: Dark Blue (`#172554`) for prominent, accessible call-to-actions, hover depth, and consistent visual hierarchy.
- **Surface Cards**: Crisp White (`#FFFFFF`) with lavender drop shadows and `20px` rounded corners.
- **Status Badges**:
  - `Available`: Emerald Green (`#DCFCE7` / `#15803D`)
  - `Borrowed`: Violet Pill (`#EDE9FE` / `#6D28D9`)
  - `Pending`: Amber Pill (`#FEF3C7` / `#B45309`)

---

## 🚀 Quick Start (Zero Setup)

### Option 1: Double-click Runner (Windows)
Double-click **`run_borrowhub.bat`** in the project root folder. It will start the server and open BorrowHub automatically in your default browser at:
```
http://localhost:8000/index.html
```

### Option 2: Python Command Line
Open a terminal in this directory and run:
```bash
python server.py
```

### Option 3: Direct Browser Launch
You can also directly double-click **`index.html`** in your browser. All interactive workflows, role switching, search, and local storage state will run seamlessly!

---

## 📂 Project Directory Structure

```
BorrowHub-for Campus/
├── index.html                     # Full responsive single-page web app
├── run_borrowhub.bat              # One-click Windows starter script
├── server.py                      # Lightweight local web server
├── css/
│   └── style.css                  # Custom Lavender (#EDE9FE) & Dark Blue (#172554) styling
├── js/
│   ├── app.js                     # Complete client-side state engine & workflows
│   └── mockData.js                # Preloaded campus students, items & mock transactions
├── database/
│   └── schema.sql                 # MySQL database schema with tables, indexes & sample data
├── backend-java/                  # Full Java Servlets + JDBC architecture for Eclipse / Tomcat
│   ├── src/com/borrowhub/
│   │   ├── models/
│   │   │   ├── Student.java       # Student account model
│   │   │   ├── Item.java          # Item listing model
│   │   │   ├── BorrowRequest.java # Borrow request model
│   │   │   └── BorrowTransaction.java # Active loan & return transaction model
│   │   ├── dao/
│   │   │   ├── DBConnection.java  # JDBC singleton connection manager
│   │   │   ├── StudentDAO.java    # Authentication & student queries
│   │   │   ├── ItemDAO.java       # Item CRUD & category search
│   │   │   ├── BorrowRequestDAO.java # Request management & status updates
│   │   │   └── BorrowTransactionDAO.java # Transaction & return updates
│   │   ├── servlets/
│   │   │   ├── AuthServlet.java   # /login, /register, /logout
│   │   │   ├── ItemServlet.java   # /items, /items/new, /items/delete
│   │   │   ├── BorrowRequestServlet.java # /requests/create, /requests/decision
│   │   │   ├── ReturnServlet.java # /return/confirm
│   │   │   └── AdminServlet.java  # /admin campus dashboard
│   │   └── util/
│   │       └── PasswordUtil.java  # SHA-256 password hashing & verification
│   └── WebContent/
│       ├── WEB-INF/
│       │   └── web.xml            # Servlet mapping & session timeout configuration
│       ├── index.jsp              # Entry JSP
│       └── views/
│           └── admin.jsp          # Server-rendered admin management page
└── README.md                      # Documentation & viva presentation guide
```

---

## 🌟 Key Features Implemented

### 1. Student Registration & Authentication
- Students register with **Full Name**, **College ID** (e.g. `CS2024-042`), **Department**, **Year of Study**, and **Password**.
- Secure password hashing using **SHA-256** (implemented in both Java `PasswordUtil.java` and client simulation).
- **Fast Demo Account Switcher** at the top bar enables instant 1-click testing between:
  - 👩‍🎓 **Riya Sharma** (*Item Owner* – DSA Textbook & Lab Coat)
  - 👨‍🎓 **Aryan Patel** (*Borrower* – Looking for textbooks & calculator)
  - 👩‍🎓 **Neha Kapoor** (*Senior* – MacBook Air owner)
  - 👨‍🏫 **Prof. Arvind Sharma** (*Campus Admin*)

### 2. Add and Manage Items
- Students can list textbooks, scientific calculators, lab equipment, and sports gear.
- Set **Lending Rate** (e.g. `Free to borrow`, `₹50 / week`, `₹150 / week`), **Condition** (*Brand New, Like New, Good*), **Pickup Spot** (*Hostel Block B, Central Library*), and **Photos**.
- Instant local catalog publishing and inventory status toggling.

### 3. Browse and Search
- **Live Search**: Instant keyword filtering across title, category, description, and pickup spot.
- **Category Filter Pills**: Textbooks, Calculators, Lab Equipment, Sports Gear, Electronics, Others.
- **Availability Filter**: Toggle between *All*, *🟢 Available Now*, and *🟣 Currently Borrowed*.
- **Sorting**: Newest first, Available now, or Free items first.

### 4. Borrowing Requests
- Borrowers view item details and click **"Borrow Now"** (`#172554` Dark Blue button).
- Select a **Proposed Return Date** and write a brief note (e.g., *"Need for mid-term practical exam"*).
- The item owner receives an incoming notification with a counter badge on their navbar.

### 5. Borrowing and Return Tracking (The Full Cycle)
1. **Lender Review**: The owner checks their *Incoming Requests* tab in **My Dashboard** and clicks **"Approve"** or **"Reject"**.
2. **Status Change**: Once approved, the item's status automatically flips from `AVAILABLE` to `BORROWED`, and an active transaction record is generated.
3. **Borrower Usage**: The borrower sees the item in *My Borrowed Items* with a due date countdown.
4. **Return & Handover**: The borrower clicks **"Mark as Returned"**. The item immediately flips back to `AVAILABLE`, and community karma points are awarded!

### 6. Campus Admin Panel
- **Campus KPIs**: Total Registered Students, Cataloged Items, Active Campus Loans, and Completed Returns.
- **Moderation Table**: Remove inappropriate listings with a single click.
- **Student Directory**: Inspect student enrollment, department, listings count, and trust karma score.

---

## 🗄️ Database Design (MySQL)

Run the SQL script located in `database/schema.sql` inside your MySQL client:
```sql
SOURCE database/schema.sql;
```

### Table 1: `Students`
Stores student accounts and credentials.
| Column | Type | Constraints | Description |
|---|---|---|---|
| `student_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique student identifier |
| `name` | VARCHAR(100) | NOT NULL | Full name of student |
| `email` | VARCHAR(120) | UNIQUE, NOT NULL | College email address |
| `password_hash` | VARCHAR(255) | NOT NULL | SHA-256 hashed password |
| `department` | VARCHAR(80) | NOT NULL | Academic branch (e.g. CS, IT, ME) |
| `college_id` | VARCHAR(50) | UNIQUE, NOT NULL | Institutional roll/ID number |
| `year_of_study` | VARCHAR(20) | DEFAULT '2nd Year' | Current year |
| `role` | ENUM | 'STUDENT', 'ADMIN' | Access role |

### Table 2: `Items`
Stores belongings listed by students.
| Column | Type | Constraints | Description |
|---|---|---|---|
| `item_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique item identifier |
| `owner_id` | INT | FOREIGN KEY (`Students.student_id`) | Student who owns the item |
| `item_name` | VARCHAR(150) | NOT NULL | Name of the equipment/book |
| `category` | VARCHAR(60) | NOT NULL | Textbooks, Calculators, Lab, etc. |
| `description` | TEXT | NOT NULL | Item specs & condition notes |
| `rate_info` | VARCHAR(60) | DEFAULT 'Free / Week' | Lending charge or free |
| `location` | VARCHAR(100) | DEFAULT 'Campus' | Hostel block or pickup point |
| `availability_status`| ENUM | 'AVAILABLE', 'BORROWED', 'UNAVAILABLE' | Current loan status |

### Table 3: `Borrow_Requests`
Tracks borrowing requests sent from one student to another.
| Column | Type | Constraints | Description |
|---|---|---|---|
| `request_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique request ID |
| `item_id` | INT | FOREIGN KEY (`Items.item_id`) | Target item |
| `borrower_id` | INT | FOREIGN KEY (`Students.student_id`) | Requesting student |
| `request_date` | DATE | NOT NULL | Date request submitted |
| `return_date` | DATE | NOT NULL | Expected date of return |
| `purpose` | TEXT | | Purpose/note to owner |
| `request_status`| ENUM | 'PENDING', 'APPROVED', 'REJECTED' | Approval status |

### Table 4: `Borrow_Transactions`
Audit log of approved handovers and verified returns.
| Column | Type | Constraints | Description |
|---|---|---|---|
| `transaction_id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique transaction record |
| `request_id` | INT | FOREIGN KEY (`Borrow_Requests.request_id`) | Originating request |
| `borrowed_date` | DATE | NOT NULL | Handover date |
| `due_date` | DATE | NOT NULL | Agreed return date |
| `actual_return_date`| DATE | NULLABLE | Date returned |
| `transaction_status`| ENUM | 'ACTIVE', 'RETURNED', 'OVERDUE' | Current transaction state |

---

## 📦 Included Dependencies & Build System

All required Java dependencies are **pre-downloaded and included** in `backend-java/WebContent/WEB-INF/lib/`:
1. **`mysql-connector-j-8.3.0.jar`** (Official MySQL JDBC Driver)
2. **`javax.servlet-api-4.0.1.jar`** (Standard Java Servlet API)
3. **`jstl-1.2.jar`** (JavaServer Pages Standard Tag Library)
4. **`javax.servlet.jsp-api-2.3.3.jar`** (JSP API)

Additionally:
- A complete **`pom.xml`** is configured for Maven users.
- A pre-compiled, production-ready **`BorrowHub.war`** is generated in the root folder. You can directly copy `BorrowHub.war` to Apache Tomcat's `webapps/` folder for instant deployment!
- All `.class` files have been compiled using JDK and placed into `backend-java/WebContent/WEB-INF/classes/`.

1. **Import Project**:
   - Open Eclipse IDE for Java EE Developers.
   - Choose **File -> Import -> General -> Existing Projects into Workspace** and browse to `backend-java`.
2. **Add MySQL Connector JAR**:
   - Download `mysql-connector-j-8.x.x.jar`.
   - Place it in `backend-java/WebContent/WEB-INF/lib/`.
3. **Configure Database Credentials**:
   - Open `src/com/borrowhub/dao/DBConnection.java` and verify your MySQL username and password (defaults to `root`/`root`).
4. **Deploy to Tomcat**:
   - Right-click project -> **Run As -> Run on Server** -> Select **Apache Tomcat 9.0/10.0**.
   - Navigate to `http://localhost:8080/BorrowHub/`.

---
