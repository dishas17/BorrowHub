const AppState = {
    users: [],
    items: [],
    requests: [],
    transactions: [],
    currentUser: null,
    wishlist: new Set(),
    activeCategory: "All",
    activeSearch: "",
    activeStatus: "All",
    activeScreen: "home",
    activeDashTab: "incoming"
};

// Storage Keys
const STORAGE_KEY = "borrowhub_data_v3";
const USER_KEY = "borrowhub_current_user_id_v3";

// Initialize App
function initApp() {
    loadData();
    setupNavigation();
    setupModals();
    renderAll();
    setupSearchListeners();
}

// Load data from LocalStorage or initialize with DEFAULT_DATA
function loadData() {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) {
        try {
            const parsed = JSON.parse(raw);
            AppState.users = parsed.users || DEFAULT_DATA.users;
            AppState.items = parsed.items || DEFAULT_DATA.items;
            AppState.requests = parsed.requests || DEFAULT_DATA.requests;
            AppState.transactions = parsed.transactions || DEFAULT_DATA.transactions;
        } catch (e) {
            console.error("Error parsing stored data", e);
            resetToDefaultData(false);
        }
    } else {
        resetToDefaultData(false);
    }

    // Load active user (default to Riya Sharma if none set)
    const savedUserId = localStorage.getItem(USER_KEY);
    if (savedUserId) {
        const foundUser = AppState.users.find(u => u.id == savedUserId);
        AppState.currentUser = foundUser || AppState.users[0];
    } else {
        // Initial user
        AppState.currentUser = AppState.users[0];
    }
}

// Save state to LocalStorage
function saveData() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify({
        users: AppState.users,
        items: AppState.items,
        requests: AppState.requests,
        transactions: AppState.transactions
    }));
    if (AppState.currentUser) {
        localStorage.setItem(USER_KEY, AppState.currentUser.id);
    } else {
        localStorage.removeItem(USER_KEY);
    }
}

// Reset to initial mock data
function resetToDefaultData(shouldReload = true) {
    AppState.users = JSON.parse(JSON.stringify(DEFAULT_DATA.users));
    AppState.items = JSON.parse(JSON.stringify(DEFAULT_DATA.items));
    AppState.requests = JSON.parse(JSON.stringify(DEFAULT_DATA.requests));
    AppState.transactions = JSON.parse(JSON.stringify(DEFAULT_DATA.transactions));
    AppState.currentUser = AppState.users[0];
    saveData();
    if (shouldReload) {
        showToast("Demo data restored to initial state!", "success");
        renderAll();
    }
}

// Screen navigation
function setupNavigation() {
    const navLinks = document.querySelectorAll("[data-nav-target]");
    navLinks.forEach(link => {
        link.addEventListener("click", (e) => {
            e.preventDefault();
            const targetScreen = link.getAttribute("data-nav-target");
            navigateTo(targetScreen);
        });
    });

    window.addEventListener("hashchange", () => {
        const hash = window.location.hash.replace("#", "");
        if (hash) {
            navigateTo(hash, false);
        }
    });

    if (window.location.hash) {
        navigateTo(window.location.hash.replace("#", ""), false);
    }
}

function navigateTo(screenName, updateHash = true) {
    const validScreens = ["home", "browse", "dashboard", "admin", "login", "about"];
    if (!validScreens.includes(screenName)) {
        screenName = "home";
    }

    // Protected Route: Admin Panel requires ADMIN role
    if (screenName === "admin") {
        if (!AppState.currentUser || AppState.currentUser.role !== "ADMIN") {
            screenName = "admin"; // Will display Access Denied view inside admin screen
        }
    }

    // Protected Route: Dashboard requires logged in user
    if (screenName === "dashboard") {
        if (!AppState.currentUser) {
            showToast("Please log in to view your dashboard.", "info");
            screenName = "login";
        }
    }

    AppState.activeScreen = screenName;

    document.querySelectorAll(".view-screen").forEach(screen => {
        screen.classList.remove("active");
    });
    const targetScreenEl = document.getElementById(`screen-${screenName}`);
    if (targetScreenEl) {
        targetScreenEl.classList.add("active");
    }

    // Update active nav links
    document.querySelectorAll(".nav-link").forEach(link => {
        if (link.getAttribute("data-nav-target") === screenName) {
            link.classList.add("active");
        } else {
            link.classList.remove("active");
        }
    });

    if (updateHash) {
        window.location.hash = screenName;
    }

    window.scrollTo({ top: 0, behavior: "smooth" });
    renderAll();
}

// Search listeners
function setupSearchListeners() {
    const globalSearch = document.getElementById("global-search-input");
    if (globalSearch) {
        globalSearch.addEventListener("input", (e) => {
            AppState.activeSearch = e.target.value.trim().toLowerCase();
            if (AppState.activeScreen !== "browse") {
                navigateTo("browse");
            } else {
                renderBrowseItems();
            }
        });
    }

    const browseSearch = document.getElementById("browse-search-input");
    if (browseSearch) {
        browseSearch.addEventListener("input", (e) => {
            AppState.activeSearch = e.target.value.trim().toLowerCase();
            renderBrowseItems();
        });
    }

    const sortSelect = document.getElementById("browse-sort-select");
    if (sortSelect) {
        sortSelect.addEventListener("change", () => {
            renderBrowseItems();
        });
    }
}

// Master Render
function renderAll() {
    renderNavbar();
    renderCategories();
    renderFeaturedItems();
    renderBrowseItems();
    renderDashboard();
    renderAdminPanel();
    updatePendingBadge();
}

// Render Top Navbar based on Login status & Admin role
function renderNavbar() {
    const userContainer = document.getElementById("nav-user-container");
    const adminNavLink = document.getElementById("nav-link-admin");
    const requestsNavLink = document.getElementById("nav-link-requests");

    // Show/hide Admin Link strictly if logged in as ADMIN
    if (adminNavLink) {
        if (AppState.currentUser && AppState.currentUser.role === "ADMIN") {
            adminNavLink.style.display = "inline-flex";
        } else {
            adminNavLink.style.display = "none";
        }
    }

    // Show/hide Requests Link if logged in
    if (requestsNavLink) {
        if (AppState.currentUser) {
            requestsNavLink.style.display = "inline-flex";
        } else {
            requestsNavLink.style.display = "none";
        }
    }

    if (!userContainer) return;

    if (AppState.currentUser) {
        const u = AppState.currentUser;
        userContainer.innerHTML = `
            <div class="user-profile-badge" onclick="navigateTo('dashboard')" title="View profile & dashboard">
                <img src="${u.avatar}" alt="${u.name}" class="user-avatar-sm" onerror="this.src='https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150'">
                <div class="user-name-text">${u.name}</div>
                <span class="user-role-tag">${u.role === 'ADMIN' ? '🛡️ Admin' : u.year}</span>
            </div>
            <button class="btn-nav-outline" onclick="openModal('modal-add-item')">+ List Item</button>
            <button class="btn-nav-outline" style="padding: 7px 12px; border-color: rgba(255,255,255,0.25);" onclick="logoutUser()" title="Sign Out">Logout</button>
        `;
    } else {
        userContainer.innerHTML = `
            <button class="btn-nav-outline" onclick="navigateTo('login')">Login</button>
            <button class="btn-nav-solid" onclick="openRegisterTab()">Join as Student</button>
        `;
    }
}

// Logout
function logoutUser() {
    AppState.currentUser = null;
    saveData();
    showToast("Logged out successfully.", "info");
    navigateTo("login");
}

// Instant Demo Login (One click from Login Page)
function loginWithDemo(userId) {
    const user = AppState.users.find(u => u.id === userId);
    if (!user) return;

    AppState.currentUser = user;
    saveData();
    showToast(`Welcome, ${user.name}! (${user.role === 'ADMIN' ? 'Campus Admin' : user.year})`, "success");

    if (user.role === "ADMIN") {
        navigateTo("admin");
    } else {
        navigateTo("dashboard");
    }
}

// Notification count badge on "My Requests"
function updatePendingBadge() {
    if (!AppState.currentUser) return;
    const incomingPending = AppState.requests.filter(r => {
        const item = AppState.items.find(i => i.id === r.itemId);
        return item && item.ownerId === AppState.currentUser.id && r.status === "PENDING";
    }).length;

    const navBadge = document.getElementById("nav-requests-badge");
    if (navBadge) {
        if (incomingPending > 0) {
            navBadge.textContent = incomingPending;
            navBadge.style.display = "inline-block";
        } else {
            navBadge.style.display = "none";
        }
    }
}

// Categories
function renderCategories() {
    const container = document.getElementById("categories-container");
    if (!container) return;

    container.innerHTML = DEFAULT_DATA.categories.map(cat => {
        const isActive = AppState.activeCategory === cat.id;
        return `
            <div class="category-card ${isActive ? 'active' : ''}" onclick="selectCategory('${cat.id}')">
                <div class="category-icon-box" style="background-color: ${cat.bg}; color: ${cat.color};">
                    ${cat.icon}
                </div>
                <div class="category-info">
                    <div class="category-name">${cat.name}</div>
                    <div class="category-sub">${cat.sub}</div>
                </div>
            </div>
        `;
    }).join('');
}

function selectCategory(catId) {
    AppState.activeCategory = catId;
    renderCategories();
    navigateTo("browse");
    renderBrowseItems();
}

// ==========================================================================
// FEATURED ITEMS (Home Page - 5 Items Row, 100% Free, No Hostel Location)
// ==========================================================================
function renderFeaturedItems() {
    const container = document.getElementById("featured-items-container");
    if (!container) return;

    // Show first 5 items
    const featuredList = AppState.items.slice(0, 5);
    container.innerHTML = featuredList.map(item => createItemCardHTML(item)).join('');
}

// ==========================================================================
// BROWSE ITEMS
// ==========================================================================
function renderBrowseItems() {
    const container = document.getElementById("browse-items-container");
    if (!container) return;

    let filtered = [...AppState.items];

    if (AppState.activeCategory && AppState.activeCategory !== "All") {
        filtered = filtered.filter(i => i.category.toLowerCase() === AppState.activeCategory.toLowerCase());
    }

    if (AppState.activeStatus && AppState.activeStatus !== "All") {
        filtered = filtered.filter(i => i.status.toUpperCase() === AppState.activeStatus.toUpperCase());
    }

    if (AppState.activeSearch) {
        filtered = filtered.filter(i => 
            i.title.toLowerCase().includes(AppState.activeSearch) ||
            i.description.toLowerCase().includes(AppState.activeSearch) ||
            i.category.toLowerCase().includes(AppState.activeSearch)
        );
    }

    const sortSelect = document.getElementById("browse-sort-select");
    const sortVal = sortSelect ? sortSelect.value : "newest";
    if (sortVal === "available") {
        filtered.sort((a, b) => (a.status === "AVAILABLE" ? -1 : 1));
    } else {
        filtered.sort((a, b) => b.id - a.id);
    }

    const countElem = document.getElementById("browse-results-count");
    if (countElem) {
        countElem.textContent = `${filtered.length} campus items available`;
    }

    if (filtered.length === 0) {
        container.innerHTML = `
            <div style="grid-column: 1 / -1; text-align: center; padding: 60px 20px; background: #FFFFFF; border-radius: 20px;">
                <div style="font-size: 48px; margin-bottom: 12px;">🔍</div>
                <h3 style="font-size: 18px; font-weight: 700; color: #0F172A;">No items found</h3>
                <p style="color: #64748B; font-size: 14px; margin-top: 4px;">Try searching for a different keyword or category.</p>
                <button class="btn-primary" style="margin-top: 16px;" onclick="resetBrowseFilters()">Clear Filters</button>
            </div>
        `;
        return;
    }

    container.innerHTML = filtered.map(item => createItemCardHTML(item)).join('');
}

function resetBrowseFilters() {
    AppState.activeCategory = "All";
    AppState.activeSearch = "";
    AppState.activeStatus = "All";
    const gSearch = document.getElementById("global-search-input");
    const bSearch = document.getElementById("browse-search-input");
    if (gSearch) gSearch.value = "";
    if (bSearch) bSearch.value = "";
    document.querySelectorAll(".filter-pill").forEach(p => p.classList.remove("active"));
    const allPill = document.querySelector(".filter-pill[data-cat='All']");
    if (allPill) allPill.classList.add("active");
    renderCategories();
    renderBrowseItems();
}

function setAvailabilityFilter(status, elem) {
    AppState.activeStatus = status;
    document.querySelectorAll(".status-filter-pill").forEach(p => p.classList.remove("active"));
    if (elem) elem.classList.add("active");
    renderBrowseItems();
}

// Single Item Card HTML (No Price / Free to borrow, No Hostel Location)
function createItemCardHTML(item) {
    const owner = AppState.users.find(u => u.id === item.ownerId) || {
        name: "Campus Student",
        year: "2nd Year",
        avatar: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150"
    };

    const isAvailable = item.status === "AVAILABLE";
    const isWishlisted = AppState.wishlist.has(item.id);
    const isOwner = AppState.currentUser && AppState.currentUser.id === item.ownerId;

    const fallbackImg = item.category === 'Calculators' ? 'assets/images/calculator.svg' :
                        item.category === 'Textbooks' ? 'assets/images/dsa-book.jpg' :
                        item.category === 'Lab Equipment' ? 'assets/images/lab-coat.jpg' :
                        item.category === 'Sports Gear' ? 'assets/images/badminton.jpg' :
                        item.category === 'Electronics' ? 'assets/images/camera.jpg' : 'assets/images/dsa-book.jpg';

    let buttonHTML = "";
    if (isAvailable && !isOwner) {
        buttonHTML = `<button class="btn-card-primary" onclick="openBorrowModal(${item.id})">Borrow Now</button>`;
    } else if (isOwner) {
        buttonHTML = `<button class="btn-outline-card" onclick="openItemDetailsModal(${item.id})">My Listing</button>`;
    } else {
        buttonHTML = `<button class="btn-outline-card" onclick="openItemDetailsModal(${item.id})">View Details</button>`;
    }

    return `
        <div class="item-card">
            <div>
                <div class="item-image-container">
                    <img src="${item.image}" alt="${item.title}" class="item-image" loading="lazy" onerror="this.src='${fallbackImg}'">
                    <span class="item-status-badge ${isAvailable ? 'status-available' : 'status-borrowed'}">
                        ${isAvailable ? 'Available' : 'Borrowed'}
                    </span>
                    <button class="btn-wishlist ${isWishlisted ? 'active' : ''}" onclick="toggleWishlist(${item.id}, this)" title="Wishlist">
                        ${isWishlisted ? '❤️' : '🤍'}
                    </button>
                </div>

                <div class="item-details">
                    <div class="item-title" title="${item.title}">${item.title}</div>
                    <div class="item-category-tag">${item.category}</div>
                    <div class="item-free-badge">✨ Free to borrow</div>
                </div>
            </div>

            <div>
                <div class="item-owner-row">
                    <div class="item-owner-info">
                        <img src="${owner.avatar}" alt="${owner.name}" class="item-owner-avatar" onerror="this.src='https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150'">
                        <span class="item-owner-name">${owner.name}</span>
                        <span class="item-owner-year">• ${owner.year}</span>
                    </div>
                </div>

                ${buttonHTML}
            </div>
        </div>
    `;
}

function toggleWishlist(itemId, btnElem) {
    if (AppState.wishlist.has(itemId)) {
        AppState.wishlist.delete(itemId);
        btnElem.innerHTML = "🤍";
        btnElem.classList.remove("active");
        showToast("Removed from wishlist", "info");
    } else {
        AppState.wishlist.add(itemId);
        btnElem.innerHTML = "❤️";
        btnElem.classList.add("active");
        showToast("Added to wishlist!", "success");
    }
}

// ==========================================================================
// MY DASHBOARD
// ==========================================================================
function renderDashboard() {
    if (!AppState.currentUser) return;
    const user = AppState.currentUser;

    const avatar = document.getElementById("dash-user-avatar");
    const name = document.getElementById("dash-user-name");
    const dept = document.getElementById("dash-user-dept");
    const colId = document.getElementById("dash-user-college-id");

    if (avatar) avatar.src = user.avatar;
    if (name) name.textContent = user.name;
    if (dept) dept.textContent = `${user.department} • ${user.year}`;
    if (colId) colId.textContent = `College ID: ${user.collegeId}`;

    const myItems = AppState.items.filter(i => i.ownerId === user.id);
    const incomingReqs = AppState.requests.filter(r => {
        const item = AppState.items.find(i => i.id === r.itemId);
        return item && item.ownerId === user.id && r.status === "PENDING";
    });
    const myActiveBorrows = AppState.transactions.filter(t => t.borrowerId === user.id && t.status === "ACTIVE");
    const myLentOut = AppState.transactions.filter(t => {
        const item = AppState.items.find(i => i.id === t.itemId);
        return item && item.ownerId === user.id && t.status === "ACTIVE";
    });

    const statLent = document.getElementById("stat-lent-count");
    const statBorrowed = document.getElementById("stat-borrowed-count");
    const statPending = document.getElementById("stat-pending-count");
    const statKarma = document.getElementById("stat-karma-score");

    if (statLent) statLent.textContent = myLentOut.length;
    if (statBorrowed) statBorrowed.textContent = myActiveBorrows.length;
    if (statPending) statPending.textContent = incomingReqs.length;
    if (statKarma) statKarma.textContent = `${user.karma || 120} pts`;

    renderDashboardTabContent();
}

function switchDashboardTab(tabName) {
    AppState.activeDashTab = tabName;
    document.querySelectorAll(".dash-tab-btn").forEach(btn => {
        if (btn.getAttribute("data-tab") === tabName) {
            btn.classList.add("active");
        } else {
            btn.classList.remove("active");
        }
    });
    renderDashboardTabContent();
}

function renderDashboardTabContent() {
    const user = AppState.currentUser;
    const container = document.getElementById("dashboard-tab-content");
    if (!container || !user) return;

    if (AppState.activeDashTab === "incoming") {
        const incoming = AppState.requests.filter(r => {
            const item = AppState.items.find(i => i.id === r.itemId);
            return item && item.ownerId === user.id;
        });

        if (incoming.length === 0) {
            container.innerHTML = `
                <div class="table-card" style="text-align: center; padding: 48px 20px;">
                    <div style="font-size: 40px; margin-bottom: 8px;">📬</div>
                    <h3 style="font-weight: 700;">No incoming borrow requests</h3>
                    <p style="color: #64748B; font-size: 13.5px; margin-top: 4px;">When classmates request your listed items, they appear here for your approval.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = `
            <div class="table-card">
                <table class="clean-table">
                    <thead>
                        <tr>
                            <th>Item Requested</th>
                            <th>Borrower</th>
                            <th>Proposed Return Date</th>
                            <th>Purpose / Note</th>
                            <th>Status</th>
                            <th>Decision</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${incoming.map(req => {
                            const item = AppState.items.find(i => i.id === req.itemId) || { title: "Item" };
                            const borrower = AppState.users.find(u => u.id === req.borrowerId) || { name: "Classmate", collegeId: "ID", avatar: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150" };
                            return `
                                <tr>
                                    <td><strong>${item.title}</strong></td>
                                    <td>
                                        <div style="display: flex; align-items: center; gap: 8px;">
                                            <img src="${borrower.avatar}" style="width: 24px; height: 24px; border-radius: 50%;">
                                            <div>
                                                <div>${borrower.name}</div>
                                                <small style="color: #64748B;">${borrower.collegeId}</small>
                                            </div>
                                        </div>
                                    </td>
                                    <td><strong>${req.returnDate}</strong></td>
                                    <td><span style="font-size: 13px; color: #475569;">${req.purpose || 'Campus academic use'}</span></td>
                                    <td>
                                        <span class="pill-badge" style="${req.status === 'APPROVED' ? 'background: #DCFCE7; color: #15803D;' : req.status === 'REJECTED' ? 'background: #FEE2E2; color: #B91C1C;' : 'background: #FEF3C7; color: #B45309;'}">
                                            ${req.status}
                                        </span>
                                    </td>
                                    <td>
                                        ${req.status === 'PENDING' ? `
                                            <div style="display: flex; gap: 6px;">
                                                <button class="btn-action-green" onclick="decideRequest(${req.id}, 'APPROVED')">Approve</button>
                                                <button class="btn-action-red" onclick="decideRequest(${req.id}, 'REJECTED')">Reject</button>
                                            </div>
                                        ` : `
                                            <span style="font-size: 12px; color: #64748B;">Completed</span>
                                        `}
                                    </td>
                                </tr>
                            `;
                        }).join('')}
                    </tbody>
                </table>
            </div>
        `;

    } else if (AppState.activeDashTab === "outgoing") {
        const myActiveBorrows = AppState.transactions.filter(t => t.borrowerId === user.id);

        if (myActiveBorrows.length === 0) {
            container.innerHTML = `
                <div class="table-card" style="text-align: center; padding: 48px 20px;">
                    <div style="font-size: 40px; margin-bottom: 8px;">🎒</div>
                    <h3 style="font-weight: 700;">No items borrowed yet</h3>
                    <p style="color: #64748B; font-size: 13.5px; margin-top: 4px;">Need a textbook, lab coat, or calculator for class? Everything is free to borrow.</p>
                    <button class="btn-primary" style="margin-top: 14px;" onclick="navigateTo('browse')">Browse Available Items</button>
                </div>
            `;
            return;
        }

        container.innerHTML = `
            <div class="table-card">
                <table class="clean-table">
                    <thead>
                        <tr>
                            <th>Item</th>
                            <th>Owner</th>
                            <th>Due Date</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${myActiveBorrows.map(trans => {
                            const item = AppState.items.find(i => i.id === trans.itemId) || { title: "Item" };
                            const owner = AppState.users.find(u => u.id === item.ownerId) || { name: "Owner" };
                            const isReturned = trans.status === "RETURNED";
                            return `
                                <tr>
                                    <td><strong>${item.title}</strong></td>
                                    <td>${owner.name}</td>
                                    <td><strong>${trans.dueDate}</strong></td>
                                    <td>
                                        <span class="pill-badge" style="${isReturned ? 'background: #DCFCE7; color: #15803D;' : 'background: #EDE9FE; color: #6D28D9;'}">
                                            ${isReturned ? 'Returned' : 'Currently with you'}
                                        </span>
                                    </td>
                                    <td>
                                        ${!isReturned ? `
                                            <button class="btn-action-blue" onclick="returnItem(${trans.id})">Mark as Returned</button>
                                        ` : `
                                            <span style="font-size: 12px; color: #10B981; font-weight: 600;">✓ Returned</span>
                                        `}
                                    </td>
                                </tr>
                            `;
                        }).join('')}
                    </tbody>
                </table>
            </div>
        `;

    } else if (AppState.activeDashTab === "my-items") {
        const myItems = AppState.items.filter(i => i.ownerId === user.id);

        container.innerHTML = `
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
                <h3 style="font-size: 18px; font-weight: 700;">My Listed Campus Items (${myItems.length})</h3>
                <button class="btn-primary" onclick="openModal('modal-add-item')">+ List Another Item</button>
            </div>
            ${myItems.length === 0 ? `
                <div class="table-card" style="text-align: center; padding: 48px 20px;">
                    <div style="font-size: 40px; margin-bottom: 8px;">📦</div>
                    <h3 style="font-weight: 700;">You haven't listed any items yet</h3>
                    <p style="color: #64748B; font-size: 13.5px; margin-top: 4px;">Share your spare textbooks, sports equipment, or calculators with campus peers.</p>
                    <button class="btn-primary" style="margin-top: 14px;" onclick="openModal('modal-add-item')">List an Item</button>
                </div>
            ` : `
                <div class="table-card">
                    <table class="clean-table">
                        <thead>
                            <tr>
                                <th>Item</th>
                                <th>Category</th>
                                <th>Sharing Model</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${myItems.map(item => `
                                <tr>
                                    <td>
                                        <div style="display: flex; align-items: center; gap: 10px;">
                                            <img src="${item.image}" style="width: 40px; height: 40px; border-radius: 8px; object-fit: cover;">
                                            <div>
                                                <strong>${item.title}</strong><br>
                                                <small style="color: #64748B;">${item.condition}</small>
                                            </div>
                                        </div>
                                    </td>
                                    <td>${item.category}</td>
                                    <td><span class="item-free-badge">Free</span></td>
                                    <td>
                                        <span class="pill-badge ${item.status === 'AVAILABLE' ? 'status-available' : 'status-borrowed'}">
                                            ${item.status}
                                        </span>
                                    </td>
                                    <td>
                                        <div style="display: flex; gap: 6px;">
                                            <button class="btn-action-blue" onclick="toggleItemStatus(${item.id})">
                                                ${item.status === 'AVAILABLE' ? 'Set Borrowed' : 'Set Available'}
                                            </button>
                                            <button class="btn-action-red" onclick="deleteItem(${item.id})">Delete</button>
                                        </div>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `}
        `;

    } else if (AppState.activeDashTab === "history") {
        container.innerHTML = `
            <div class="table-card">
                <table class="clean-table">
                    <thead>
                        <tr>
                            <th>Transaction ID</th>
                            <th>Item Name</th>
                            <th>Borrowed Date</th>
                            <th>Due Date</th>
                            <th>Status</th>
                            <th>Notes</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${AppState.transactions.map(t => {
                            const item = AppState.items.find(i => i.id === t.itemId) || { title: "Item" };
                            return `
                                <tr>
                                    <td><strong>#TRX-${t.id}</strong></td>
                                    <td>${item.title}</td>
                                    <td>${t.borrowedDate}</td>
                                    <td>${t.dueDate}</td>
                                    <td>
                                        <span class="pill-badge" style="${t.status === 'RETURNED' ? 'background: #DCFCE7; color: #15803D;' : 'background: #EDE9FE; color: #6D28D9;'}">
                                            ${t.status}
                                        </span>
                                    </td>
                                    <td style="color: #64748B; font-size: 12.5px;">${t.notes || 'Verified on campus'}</td>
                                </tr>
                            `;
                        }).join('')}
                    </tbody>
                </table>
            </div>
        `;
    }
}

// ==========================================================================
// WORKFLOW ACTIONS
// ==========================================================================
function openBorrowModal(itemId) {
    const item = AppState.items.find(i => i.id === itemId);
    if (!item) return;

    if (!AppState.currentUser) {
        showToast("Please log in to submit a borrowing request.", "error");
        navigateTo("login");
        return;
    }

    if (AppState.currentUser.id === item.ownerId) {
        showToast("You are the owner of this item!", "info");
        return;
    }

    const owner = AppState.users.find(u => u.id === item.ownerId);

    document.getElementById("borrow-modal-item-id").value = item.id;
    document.getElementById("borrow-modal-item-title").textContent = item.title;
    document.getElementById("borrow-modal-item-category").textContent = item.category;
    document.getElementById("borrow-modal-item-owner").textContent = `Listed by: ${owner ? owner.name : 'Peer'} (${owner ? owner.year : 'Student'})`;
    document.getElementById("borrow-modal-item-img").src = item.image;

    const nextWeek = new Date();
    nextWeek.setDate(nextWeek.getDate() + 7);
    const dateInput = document.getElementById("borrow-return-date");
    if (dateInput) {
        dateInput.value = nextWeek.toISOString().split('T')[0];
        dateInput.min = new Date().toISOString().split('T')[0];
    }

    openModal("modal-borrow-request");
}

function handleBorrowSubmit(e) {
    e.preventDefault();
    const itemId = parseInt(document.getElementById("borrow-modal-item-id").value);
    const returnDate = document.getElementById("borrow-return-date").value;
    const purpose = document.getElementById("borrow-purpose").value.trim();

    if (!returnDate) {
        showToast("Please select a proposed return date.", "error");
        return;
    }

    const item = AppState.items.find(i => i.id === itemId);
    const owner = AppState.users.find(u => u.id === item.ownerId);

    const newRequest = {
        id: Date.now(),
        itemId: itemId,
        borrowerId: AppState.currentUser.id,
        requestDate: new Date().toISOString().split('T')[0],
        returnDate: returnDate,
        purpose: purpose || "Campus academic use",
        status: "PENDING"
    };

    AppState.requests.unshift(newRequest);
    saveData();
    closeModal("modal-borrow-request");
    showToast(`Borrow request sent to ${owner ? owner.name : 'the owner'}!`, "success");
    renderAll();
    navigateTo("dashboard");
    switchDashboardTab("outgoing");
}

function decideRequest(requestId, decision) {
    const req = AppState.requests.find(r => r.id === requestId);
    if (!req) return;

    req.status = decision;

    if (decision === "APPROVED") {
        const item = AppState.items.find(i => i.id === req.itemId);
        if (item) {
            item.status = "BORROWED";
        }

        const newTrans = {
            id: Date.now(),
            requestId: req.id,
            itemId: req.itemId,
            borrowerId: req.borrowerId,
            borrowedDate: new Date().toISOString().split('T')[0],
            dueDate: req.returnDate,
            actualReturnDate: null,
            status: "ACTIVE",
            notes: "Approved via BorrowHub dashboard."
        };
        AppState.transactions.unshift(newTrans);
        showToast("Request approved! Item marked as Borrowed.", "success");
    } else {
        showToast("Request has been rejected.", "info");
    }

    saveData();
    renderAll();
}

function returnItem(transactionId) {
    const trans = AppState.transactions.find(t => t.id === transactionId);
    if (!trans) return;

    trans.status = "RETURNED";
    trans.actualReturnDate = new Date().toISOString().split('T')[0];

    const item = AppState.items.find(i => i.id === trans.itemId);
    if (item) {
        item.status = "AVAILABLE";
    }

    if (AppState.currentUser) {
        AppState.currentUser.karma = (AppState.currentUser.karma || 100) + 20;
    }

    saveData();
    showToast("Item marked as returned! Thank you for sharing responsibly.", "success");
    renderAll();
}

function toggleItemStatus(itemId) {
    const item = AppState.items.find(i => i.id === itemId);
    if (!item) return;

    item.status = item.status === "AVAILABLE" ? "BORROWED" : "AVAILABLE";
    saveData();
    showToast(`Item status updated to ${item.status}`, "success");
    renderAll();
}

function deleteItem(itemId) {
    if (!confirm("Are you sure you want to remove this item listing?")) return;
    AppState.items = AppState.items.filter(i => i.id !== itemId);
    saveData();
    showToast("Item listing removed.", "info");
    renderAll();
}

function handleAddItemSubmit(e) {
    e.preventDefault();
    if (!AppState.currentUser) {
        showToast("Please log in to list an item.", "error");
        navigateTo("login");
        return;
    }

    const title = document.getElementById("add-item-title").value.trim();
    const category = document.getElementById("add-item-category").value;
    const condition = document.getElementById("add-item-condition").value;
    const description = document.getElementById("add-item-description").value.trim();
    const imageUrl = document.getElementById("add-item-image").value.trim();

    if (!title || !description) {
        showToast("Please fill in the item title and description.", "error");
        return;
    }

    const newItem = {
        id: Date.now(),
        ownerId: AppState.currentUser.id,
        title: title,
        category: category,
        description: description,
        rate: "Free to borrow",
        condition: condition,
        status: "AVAILABLE",
        image: imageUrl || "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600",
        createdAt: new Date().toISOString().split('T')[0]
    };

    AppState.items.unshift(newItem);
    saveData();
    closeModal("modal-add-item");
    showToast(`"${newItem.title}" is now listed for campus sharing!`, "success");
    renderAll();
    navigateTo("browse");
}

function openItemDetailsModal(itemId) {
    const item = AppState.items.find(i => i.id === itemId);
    if (!item) return;

    const owner = AppState.users.find(u => u.id === item.ownerId) || {
        name: "Campus Student",
        department: "Engineering",
        year: "2nd Year",
        avatar: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150"
    };

    document.getElementById("detail-modal-img").src = item.image;
    document.getElementById("detail-modal-title").textContent = item.title;
    document.getElementById("detail-modal-category").textContent = item.category;
    document.getElementById("detail-modal-condition").textContent = item.condition;
    document.getElementById("detail-modal-desc").textContent = item.description;

    document.getElementById("detail-modal-owner-name").textContent = owner.name;
    document.getElementById("detail-modal-owner-dept").textContent = `${owner.department} • ${owner.year}`;
    document.getElementById("detail-modal-owner-avatar").src = owner.avatar;

    const isAvailable = item.status === "AVAILABLE";
    const statusElem = document.getElementById("detail-modal-status");
    statusElem.textContent = isAvailable ? "Available" : "Borrowed";
    statusElem.className = `pill-badge ${isAvailable ? 'status-available' : 'status-borrowed'}`;

    const borrowBtn = document.getElementById("detail-modal-borrow-btn");
    const isOwner = AppState.currentUser && AppState.currentUser.id === item.ownerId;

    if (isAvailable && !isOwner) {
        borrowBtn.style.display = "inline-flex";
        borrowBtn.onclick = () => {
            closeModal("modal-item-details");
            openBorrowModal(item.id);
        };
    } else {
        borrowBtn.style.display = "none";
    }

    openModal("modal-item-details");
}

// ==========================================================================
// ADMIN PANEL (Protected strictly for ADMIN role)
// ==========================================================================
function renderAdminPanel() {
    const adminMain = document.getElementById("screen-admin");
    if (!adminMain) return;

    const isAdmin = AppState.currentUser && AppState.currentUser.role === "ADMIN";

    if (!isAdmin) {
        adminMain.innerHTML = `
            <div class="access-denied-box">
                <div style="font-size: 52px; margin-bottom: 16px;">🛡️</div>
                <h2 style="font-size: 24px; font-weight: 800; color: #172554; margin-bottom: 8px;">Campus Administrator Portal</h2>
                <p style="color: #64748B; font-size: 14.5px; max-width: 480px; margin: 0 auto 24px auto;">
                    This section is restricted to campus resource coordinators and administrators. Please log in with an administrator account to view audit metrics and manage campus listings.
                </p>
                <div style="display: flex; justify-content: center; gap: 12px;">
                    <button class="btn-primary" onclick="loginWithDemo(99)">
                        🛡️ Log In as Prof. Arvind (Admin)
                    </button>
                    <button class="btn-secondary" onclick="navigateTo('home')">
                        Back to Home
                    </button>
                </div>
            </div>
        `;
        return;
    }

    // Render full administrator panel when authenticated
    const totalStudents = AppState.users.filter(u => u.role === 'STUDENT').length;
    const totalItems = AppState.items.length;
    const activeLoans = AppState.transactions.filter(t => t.status === "ACTIVE").length;
    const totalReturned = AppState.transactions.filter(t => t.status === "RETURNED").length;

    adminMain.innerHTML = `
        <div class="admin-badge-strip">
            <div>🛡️ <strong>BorrowHub Campus Administrator Portal</strong> — College Resource Cell</div>
            <div>Active Admin: ${AppState.currentUser.name}</div>
        </div>

        <div class="dashboard-stats-strip">
            <div class="stat-metric-card">
                <div class="stat-icon-wrap" style="background: #EDE9FE; color: #7C3AED;">👥</div>
                <div class="stat-info">
                    <div class="stat-num">${totalStudents}</div>
                    <div class="stat-lbl">Registered Students</div>
                </div>
            </div>
            <div class="stat-metric-card">
                <div class="stat-icon-wrap" style="background: #E0F2FE; color: #0284C7;">📚</div>
                <div class="stat-info">
                    <div class="stat-num">${totalItems}</div>
                    <div class="stat-lbl">Active Listings</div>
                </div>
            </div>
            <div class="stat-metric-card">
                <div class="stat-icon-wrap" style="background: #FEF3C7; color: #D97706;">🔄</div>
                <div class="stat-info">
                    <div class="stat-num">${activeLoans}</div>
                    <div class="stat-lbl">Active Loans</div>
                </div>
            </div>
            <div class="stat-metric-card">
                <div class="stat-icon-wrap" style="background: #DCFCE7; color: #15803D;">✅</div>
                <div class="stat-info">
                    <div class="stat-num">${totalReturned}</div>
                    <div class="stat-lbl">Completed Returns</div>
                </div>
            </div>
        </div>

        <div style="display: flex; flex-direction: column; gap: 24px;">
            <div class="table-card">
                <h3 style="font-size: 17px; font-weight: 700; margin-bottom: 14px;">Campus Item Moderation</h3>
                <table class="clean-table">
                    <thead>
                        <tr>
                            <th>Item Name</th>
                            <th>Category</th>
                            <th>Listed By</th>
                            <th>Status</th>
                            <th>Moderation</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${AppState.items.map(item => {
                            const owner = AppState.users.find(u => u.id === item.ownerId) || { name: 'Student' };
                            return `
                                <tr>
                                    <td><strong>${item.title}</strong></td>
                                    <td>${item.category}</td>
                                    <td>${owner.name}</td>
                                    <td>
                                        <span class="pill-badge ${item.status === 'AVAILABLE' ? 'status-available' : 'status-borrowed'}">
                                            ${item.status}
                                        </span>
                                    </td>
                                    <td>
                                        <button class="btn-action-red" onclick="deleteItem(${item.id})">Remove Listing</button>
                                    </td>
                                </tr>
                            `;
                        }).join('')}
                    </tbody>
                </table>
            </div>

            <div class="table-card">
                <h3 style="font-size: 17px; font-weight: 700; margin-bottom: 14px;">Registered Campus Students</h3>
                <table class="clean-table">
                    <thead>
                        <tr>
                            <th>Student</th>
                            <th>College ID</th>
                            <th>Department</th>
                            <th>Role</th>
                            <th>Items Listed</th>
                            <th>Karma Points</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${AppState.users.map(u => {
                            const userItemsCount = AppState.items.filter(i => i.ownerId === u.id).length;
                            return `
                                <tr>
                                    <td>
                                        <div style="display: flex; align-items: center; gap: 8px;">
                                            <img src="${u.avatar}" style="width: 28px; height: 28px; border-radius: 50%;">
                                            <div>
                                                <strong>${u.name}</strong><br>
                                                <small style="color: #64748B;">${u.email}</small>
                                            </div>
                                        </div>
                                    </td>
                                    <td><strong>${u.collegeId}</strong></td>
                                    <td>${u.department} (${u.year})</td>
                                    <td><span class="pill-badge">${u.role}</span></td>
                                    <td>${userItemsCount} items</td>
                                    <td><strong style="color: #4F46E5;">${u.karma || 100} pts</strong></td>
                                </tr>
                            `;
                        }).join('')}
                    </tbody>
                </table>
            </div>
        </div>
    `;
}

// ==========================================================================
// LOGIN & REGISTRATION HANDLERS
// ==========================================================================
function switchLoginScreenTab(tab) {
    const loginForm = document.getElementById("login-page-form");
    const registerForm = document.getElementById("register-page-form");
    const loginTabBtn = document.getElementById("login-screen-tab-btn");
    const registerTabBtn = document.getElementById("register-screen-tab-btn");

    if (tab === "login") {
        if (loginForm) loginForm.style.display = "block";
        if (registerForm) registerForm.style.display = "none";
        if (loginTabBtn) loginTabBtn.classList.add("active");
        if (registerTabBtn) registerTabBtn.classList.remove("active");
    } else {
        if (loginForm) loginForm.style.display = "none";
        if (registerForm) registerForm.style.display = "block";
        if (loginTabBtn) loginTabBtn.classList.remove("active");
        if (registerTabBtn) registerTabBtn.classList.add("active");
    }
}

function openRegisterTab() {
    navigateTo("login");
    switchLoginScreenTab("register");
}

function handleLoginSubmit(e) {
    e.preventDefault();
    const email = document.getElementById("login-page-email").value.trim().toLowerCase();
    const user = AppState.users.find(u => u.email.toLowerCase() === email || u.collegeId.toLowerCase() === email);

    if (user) {
        AppState.currentUser = user;
        saveData();
        showToast(`Welcome back, ${user.name}!`, "success");
        if (user.role === "ADMIN") {
            navigateTo("admin");
        } else {
            navigateTo("dashboard");
        }
    } else {
        showToast("Invalid credentials. Try using one of the instant demo accounts!", "error");
    }
}

function handleRegisterSubmit(e) {
    e.preventDefault();
    const name = document.getElementById("reg-page-name").value.trim();
    const email = document.getElementById("reg-page-email").value.trim().toLowerCase();
    const collegeId = document.getElementById("reg-page-college-id").value.trim().toUpperCase();
    const dept = document.getElementById("reg-page-dept").value;
    const year = document.getElementById("reg-page-year").value;

    if (!name || !email || !collegeId) {
        showToast("Please complete all registration fields.", "error");
        return;
    }

    const newUser = {
        id: Date.now(),
        name: name,
        email: email,
        collegeId: collegeId,
        department: dept,
        year: year,
        role: "STUDENT",
        phone: "+91 98765 00000",
        avatar: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
        karma: 100
    };

    AppState.users.push(newUser);
    AppState.currentUser = newUser;
    saveData();
    showToast(`Account created! Welcome to BorrowHub, ${newUser.name}!`, "success");
    navigateTo("dashboard");
}

// ==========================================================================
// MODAL CONTROLS
// ==========================================================================
function setupModals() {
    document.querySelectorAll(".preset-img-thumb").forEach(img => {
        img.addEventListener("click", () => {
            document.querySelectorAll(".preset-img-thumb").forEach(i => i.classList.remove("selected"));
            img.classList.add("selected");
            const input = document.getElementById("add-item-image");
            if (input) input.value = img.src;
        });
    });

    document.querySelectorAll(".modal-overlay").forEach(overlay => {
        overlay.addEventListener("click", (e) => {
            if (e.target === overlay) {
                overlay.classList.remove("active");
            }
        });
    });
}

function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add("active");
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove("active");
    }
}

function showToast(message, type = "info") {
    const container = document.getElementById("toast-container");
    if (!container) return;

    const toast = document.createElement("div");
    toast.className = `toast toast-${type}`;
    let icon = "🔔";
    if (type === "success") icon = "✅";
    if (type === "error") icon = "⚠️";
    toast.innerHTML = `<span>${icon}</span> <span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = "0";
        toast.style.transform = "translateX(50px)";
        toast.style.transition = "all 0.3s ease";
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

document.addEventListener("DOMContentLoaded", initApp);
