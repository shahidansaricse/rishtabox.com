/* =========================================================
     RB ADMIN DASHBOARD
   ========================================================= */

"use strict";


/* =========================================================
   CONFIGURATION
========================================================= */

const API_BASE_URL = "http://localhost:8080";

/* =========================================================
   USERS STATE
========================================================= */

let allAdminUsers = [];
let filteredAdminUsers = [];

let currentUsersPage = 1;

const USERS_PER_PAGE = 10;

let selectedUserId = null;
let currentUser = null;
let editingProductId = null;
/* =========================================================
   DOM HELPER
========================================================= */

function $(id) {
    return document.getElementById(id);
}


/* =========================================================
   STORAGE
   ADMIN SESSION IS SEPARATE FROM CUSTOMER SESSION
========================================================= */

function getStoredUser() {

    const raw =
        localStorage.getItem("rishtaBoxAdminUser");

    if (!raw) {
        return null;
    }

    try {

        const parsed =
            JSON.parse(raw);

        if (
            parsed &&
            typeof parsed === "object"
        ) {
            return parsed;
        }

    } catch (error) {

        console.warn(
            "Invalid admin user data in localStorage."
        );
    }

    return null;
}


/* =========================================================
   TOKEN
========================================================= */

function getToken() {

    const user =
        currentUser ||
        getStoredUser();

    if (!user) {
        return null;
    }

    return (
        user.token ||
        user.jwt ||
        user.accessToken ||
        user.jwtToken ||
        null
    );
}


/* =========================================================
   AUTH HEADERS
========================================================= */

function getAuthHeaders() {

    const token =
        getToken();

    const headers = {
        "Content-Type": "application/json"
    };

    if (token) {
        headers["Authorization"] =
            "Bearer " + token;
    }

    return headers;
}


/* =========================================================
   ROLE
========================================================= */

function getUserRole() {

    const user =
        currentUser ||
        getStoredUser();

    if (!user) {
        return "";
    }

    const role =
        user.role ||
        user.authorities?.[0]?.authority ||
        user.authority ||
        "";

    return String(role)
        .replace("ROLE_", "")
        .toUpperCase();
}

function isAdmin() {

    const role =
        getUserRole();

    return (
        role === "ADMIN" ||
        role === "SUPER_ADMIN"
    );
}

function isSuperAdmin() {

    return getUserRole() === "SUPER_ADMIN";
}
/* =========================================================
   ESCAPE HTML
========================================================= */

function escapeHtml(value) {

    if (
        value === null ||
        value === undefined
    ) {
        return "";
    }

    return String(value)

        .replace(/&/g, "&amp;")

        .replace(/</g, "&lt;")

        .replace(/>/g, "&gt;")

        .replace(/"/g, "&quot;")

        .replace(/'/g, "&#039;");
}


/* =========================================================
   INITIALIZATION
========================================================= */

document.addEventListener(
    "DOMContentLoaded",
    () => {

        console.log(
            "================================="
        );

        console.log(
            "RishtaBox Admin Dashboard Started"
        );

        console.log(
            "================================="
        );

        currentUser =
            getStoredUser();

        console.log(
            "Stored admin user:",
            currentUser
        );

        console.log(
            "Role:",
            getUserRole()
        );

        console.log(
            "Token available:",
            Boolean(getToken())
        );

        setupNavigation();

        setupForms();

        initializeAdminPage();
    }
);


/* =========================================================
   INITIALIZE ADMIN PAGE
========================================================= */

async function initializeAdminPage() {

    if (!currentUser) {

        showAdminAuthPage();

        return;
    }

    if (!isAdmin()) {

        showAccessDenied();

        return;
    }

    showAdminDashboard();

    await loadDashboardStats();

    await loadProducts();

    await loadOrders();

    await loadUsers();

    await loadBlogs();
}


/* =========================================================
   AUTH PAGE
   ONLY ADMIN LOGIN
========================================================= */

function showAdminAuthPage() {

    const authPage =
        $("adminAuthPage");

    const dashboard =
        $("adminDashboard");

    if (authPage) {

        authPage.classList.remove("hidden");

        authPage.style.display =
            "block";
    }

    if (dashboard) {

        dashboard.classList.add("hidden");

        dashboard.style.display =
            "none";
    }

    const loginSection =
        $("adminLoginSection");

    if (loginSection) {

        loginSection.classList.remove("hidden");

        loginSection.style.display =
            "block";
    }

    updateAdminHeader();
}


/* =========================================================
   SHOW DASHBOARD
========================================================= */

function showAdminDashboard() {

    const authPage =
        $("adminAuthPage");

    const dashboard =
        $("adminDashboard");

    if (authPage) {

        authPage.classList.add("hidden");

        authPage.style.display =
            "none";
    }

    if (dashboard) {

        dashboard.classList.remove("hidden");

        dashboard.style.display =
            "block";
    }

    updateAdminHeader();

    showSection("overview");
}


/* =========================================================
   ACCESS DENIED
========================================================= */

function showAccessDenied() {

    const authPage =
        $("adminAuthPage");

    const dashboard =
        $("adminDashboard");

    const message =
        $("accessMessage");

    if (dashboard) {

        dashboard.classList.add("hidden");

        dashboard.style.display =
            "none";
    }

    if (authPage) {

        authPage.classList.remove("hidden");

        authPage.style.display =
            "block";
    }

    const loginSection =
        $("adminLoginSection");

    if (loginSection) {

        loginSection.classList.remove("hidden");

        loginSection.style.display =
            "block";
    }

    if (message) {

        message.classList.remove("hidden");

        message.style.display =
            "block";

        message.innerHTML = `

            <div
                style="
                    padding:20px;
                    text-align:center;
                    color:#b42318;
                "
            >

                <h2>
                    Admin Login Required
                </h2>

                <p>
                    Please login with an ADMIN
                    account to access the dashboard.
                </p>

            </div>

        `;
    }

    updateAdminHeader();
}


/* =========================================================
   HEADER
========================================================= */

function updateAdminHeader() {

    const loginButton =
        $("headerLoginBtn");

    const signupButton =
        $("headerSignupBtn");

    const userLabel =
        $("adminUserLabel");

    const logoutButton =
        $("logoutBtn");

    const dashboard =
        $("adminDashboard");

    const isDashboardVisible =
        dashboard &&
        !dashboard.classList.contains("hidden");

    if (loginButton) {

        loginButton.classList.toggle(
            "hidden",
            isDashboardVisible
        );
    }

    /*
     * Signup removed.
     * Hide signup button if it still exists in HTML.
     */

    if (signupButton) {

        signupButton.classList.add("hidden");

        signupButton.style.display =
            "none";
    }

    if (userLabel) {

        userLabel.classList.toggle(
            "hidden",
            !isDashboardVisible
        );
    }

    if (logoutButton) {

        logoutButton.classList.toggle(
            "hidden",
            !isDashboardVisible
        );
    }
}


/* =========================================================
   NAVIGATION
========================================================= */

function setupNavigation() {

    const buttons =
        document.querySelectorAll(".nav-btn");

    buttons.forEach(
        button => {

            button.addEventListener(
                "click",
                () => {

                    const section =
                        button.dataset.section;

                    if (section) {

                        showSection(section);
                    }
                }
            );
        }
    );
}


/* =========================================================
   SHOW SECTION
========================================================= */
function showSection(sectionName) {

    if (!isAdmin()) {

        showAccessDenied();

        return;
    }

    const sections = [
        "overview",
        "products",
        "orders",
        "users",
        "blogs",
        "reviews",
        "testimonials"
    ];

    sections.forEach(
        section => {

            const content = $(section);

            if (content) {

                content.classList.toggle(
                    "hidden",
                    section !== sectionName
                );
            }
        }
    );

    document
        .querySelectorAll(".nav-btn")
        .forEach(
            button => {

                button.classList.toggle(
                    "active",
                    button.dataset.section === sectionName
                );
            }
        );

    const title = $("sectionTitle");

    if (title) {

        const titles = {

            overview:
                "Overview",

            products:
                "Products",

            orders:
                "Orders",

            users:
                "Users",

            blogs:
                "Blogs",

            reviews:
                "Reviews",

            testimonials:
                "Testimonials"
        };

        title.textContent =
            titles[sectionName] ||
            "Admin Dashboard";
    }

    if (sectionName === "products") {

        loadProducts();
    }

    if (sectionName === "orders") {

        loadOrders();
    }

    if (sectionName === "users") {

        loadUsers();
    }

    if (sectionName === "blogs") {

        loadBlogs();
    }

    if (sectionName === "reviews") {

        if (typeof loadReviews === "function") {

            loadReviews();

        } else {

            console.error(
                "loadReviews() function is not defined"
            );
        }
    }

    if (sectionName === "testimonials") {

        if (typeof loadTestimonials === "function") {

            loadTestimonials();

        } else {

            console.error(
                "loadTestimonials() function is not defined"
            );
        }
    }
}
/* =========================================================
   FORM SETUP
========================================================= */

function setupForms() {

    /* -----------------------------------------------------
       ADMIN LOGIN ONLY
    ----------------------------------------------------- */

    const loginForm =
        $("adminLoginForm");

    if (loginForm) {

        loginForm.addEventListener(
            "submit",
            handleAdminLogin
        );
    }


    /* -----------------------------------------------------
       PRODUCT FORM
    ----------------------------------------------------- */

    const productForm =
        $("productForm");

    if (productForm) {

        productForm.addEventListener(
            "submit",
            handleProductSubmit
        );
    }


    /* -----------------------------------------------------
       LOGOUT
    ----------------------------------------------------- */

    const logoutButton =
        $("logoutBtn");

    if (logoutButton) {

        logoutButton.addEventListener(
            "click",
            logoutAdmin
        );
    }


    /* -----------------------------------------------------
       REFRESH
    ----------------------------------------------------- */

    const refreshButton =
        $("refreshBtn");

    if (refreshButton) {

        refreshButton.addEventListener(
            "click",
            refreshAdminDashboard
        );
    }
}
/* =========================================================
   REFRESH ADMIN DASHBOARD
========================================================= */

async function refreshAdminDashboard() {

    console.log(
        "Refreshing admin dashboard..."
    );

    try {

        if (
            typeof loadDashboardStats ===
            "function"
        ) {
            await loadDashboardStats();
        }

        if (
            typeof loadProducts ===
            "function"
        ) {
            await loadProducts();
        }

        if (
            typeof loadOrders ===
            "function"
        ) {
            await loadOrders();
        }

        if (
            typeof loadUsers ===
            "function"
        ) {
            await loadUsers();
        }

        if (
            typeof loadBlogs ===
            "function"
        ) {
            await loadBlogs();
        }

        if (
            typeof loadTestimonials ===
            "function"
        ) {
            await loadTestimonials();
        }

        if (
            typeof loadReviews ===
            "function"
        ) {
            await loadReviews();
        }

        console.log(
            "Admin dashboard refresh completed."
        );

    } catch (error) {

        console.error(
            "refreshAdminDashboard error:",
            error
        );
    }
}

/* =========================================================
   ADMIN LOGIN
========================================================= */

async function handleAdminLogin(event) {

    event.preventDefault();

    const email =
        $("adminLoginEmail")
            ?.value
            .trim();

    const password =
        $("adminLoginPassword")
            ?.value;

    if (
        !email ||
        !password
    ) {

        alert(
            "Please enter email and password."
        );

        return;
    }

    try {

        const response =
            await fetch(
                `${API_BASE_URL}/api/auth/login`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify({
                            email,
                            password
                        })
                }
            );

        const text =
            await response.text();

        console.log(
            "Login status:",
            response.status
        );

        console.log(
            "Login response:",
            text
        );

        if (!response.ok) {

            throw new Error(
                text ||
                `Login failed: ${response.status}`
            );
        }

        let data;

        try {

            data =
                JSON.parse(text);

        } catch (error) {

            throw new Error(
                "Invalid login response from server."
            );
        }

        currentUser =
            data;

        localStorage.setItem(
            "rishtaBoxAdminUser",
            JSON.stringify(data)
        );

        console.log(
            "Logged in admin:",
            currentUser
        );

        console.log(
            "Logged in role:",
            getUserRole()
        );

        if (!isAdmin()) {

            alert(
                "Login successful, but this account does not have ADMIN access."
            );

            currentUser =
                null;

            localStorage.removeItem(
                "rishtaBoxAdminUser"
            );

            showAccessDenied();

            return;
        }

        alert(
            "Admin login successful."
        );

        showAdminDashboard();

        await loadDashboardStats();

        await loadProducts();

        await loadOrders();

        await loadUsers();

        await loadBlogs();

    } catch (error) {

        console.error(
            "Admin login error:",
            error
        );

        alert(
            "Admin login failed.\n\n" +
            error.message
        );
    }
}


/* =========================================================
   SHOW ADMIN LOGIN
========================================================= */

function showAdminLogin() {

    const authPage =
        $("adminAuthPage");

    const dashboard =
        $("adminDashboard");

    const login =
        $("adminLoginSection");

    if (authPage) {

        authPage.classList.remove("hidden");

        authPage.style.display =
            "block";
    }

    if (dashboard) {

        dashboard.classList.add("hidden");

        dashboard.style.display =
            "none";
    }

    if (login) {

        login.classList.remove("hidden");

        login.style.display =
            "block";
    }

    updateAdminHeader();
}


/* =========================================================
   LOGOUT ADMIN
   DO NOT REMOVE CUSTOMER LOGIN DATA
========================================================= */

function logoutAdmin(event) {

    if (event) {

        event.preventDefault();
    }

    localStorage.removeItem(
        "rishtaBoxAdminUser"
    );

    currentUser =
        null;

    showAdminAuthPage();

    updateAdminHeader();

    showAdminLogin();

    console.log(
        "Admin logged out successfully."
    );
}


/* =========================================================
   DASHBOARD STATS
========================================================= */

async function loadDashboardStats() {

    if (!isAdmin()) {
        return;
    }

    await loadProductCount();

    await loadOrderStats();

    await loadUserCount();
}


/* =========================================================
   PRODUCT COUNT
========================================================= */

async function loadProductCount() {

    try {

        const response =
            await fetch(
                `${API_BASE_URL}/api/products/admin/all`,
                {
                    method: "GET",
                    headers: getAuthHeaders()
                }
            );

        if (!response.ok) {

            console.error(
                "Product count request failed:",
                response.status
            );

            return;
        }

        const products =
            await response.json();

        const count =
            $("productCount");

        if (count) {

            count.textContent =
                Array.isArray(products)
                    ? products.length
                    : "0";
        }

    } catch (error) {

        console.error(
            "Product count error:",
            error
        );
    }
}


/* =========================================================
   ORDER STATS
========================================================= */

async function loadOrderStats() {

    try {

        const response =
            await fetch(
                `${API_BASE_URL}/api/orders/admin/all`,
                {
                    method: "GET",
                    headers: getAuthHeaders()
                }
            );

        if (!response.ok) {

            console.error(
                "Order stats request failed:",
                response.status
            );

            return;
        }

        const orders =
            await response.json();

        const orderCount =
            $("orderCount");

        if (orderCount) {

            orderCount.textContent =
                Array.isArray(orders)
                    ? orders.length
                    : "0";
        }

        let revenue = 0;

        if (Array.isArray(orders)) {

            orders.forEach(
                order => {

                    const status =
                        String(
                            order.paymentStatus ||
                            ""
                        ).toUpperCase();

                    if (
                        status === "PAID" ||
                        status === "SUCCESS" ||
                        status === "COMPLETED"
                    ) {

                        revenue +=
                            Number(
                                order.totalAmount ||
                                0
                            );
                    }
                }
            );
        }

        const revenueElement =
            $("revenue");

        if (revenueElement) {

            revenueElement.textContent =
                "₹" +
                revenue.toLocaleString(
                    "en-IN"
                );
        }

    } catch (error) {

        console.error(
            "Order stats error:",
            error
        );
    }
}


/* =========================================================
   USER COUNT
========================================================= */

async function loadUserCount() {

    try {

        const response =
            await fetch(
                `${API_BASE_URL}/api/users/admin/count`,
                {
                    method: "GET",
                    headers: getAuthHeaders()
                }
            );

        if (!response.ok) {

            console.error(
                "User count failed:",
                response.status
            );

            return;
        }

        const count =
            await response.json();

        const element =
            $("userCount");

        if (element) {

            element.textContent =
                count;
        }

    } catch (error) {

        console.error(
            "User count error:",
            error
        );
    }
}


/* =========================================================
   PRODUCTS
========================================================= */

async function loadProducts() {

    if (!isAdmin()) {

        console.warn(
            "loadProducts blocked: user is not ADMIN"
        );

        return;
    }

    const container =
        $("productsContent");

    if (!container) {

        console.error(
            "productsContent element not found."
        );

        return;
    }

    container.innerHTML = `

        <div
            style="
                padding:20px;
                text-align:center;
            "
        >
            Loading products...
        </div>

    `;

    try {

        console.log(
            "Loading admin products..."
        );

        const response =
            await fetch(
                `${API_BASE_URL}/api/products/admin/all`,
                {
                    method: "GET",
                    headers: getAuthHeaders()
                }
            );

        console.log(
            "Admin products status:",
            response.status
        );

        const responseText =
            await response.text();

        console.log(
            "Admin products response:",
            responseText
        );

        if (!response.ok) {

            let message =
                responseText ||
                `HTTP ${response.status}`;

            try {

                const errorData =
                    JSON.parse(responseText);

                message =
                    errorData.message ||
                    errorData.error ||
                    message;

            } catch (error) {}

            throw new Error(
                `HTTP ${response.status}: ${message}`
            );
        }

        let products = [];

        try {

            products =
                JSON.parse(responseText);

        } catch (error) {

            throw new Error(
                "Server returned invalid JSON."
            );
        }

        if (!Array.isArray(products)) {

            throw new Error(
                "Invalid products response."
            );
        }

        console.log(
            "Products loaded:",
            products.length
        );

        renderProducts(products);

    } catch (error) {

        console.error(
            "Load products error:",
            error
        );

        container.innerHTML = `

            <div
                style="
                    margin:20px;
                    padding:20px;
                    border:1px solid #f1aeb5;
                    background:#fff5f5;
                    border-radius:10px;
                    color:#842029;
                "
            >

                <h3>
                    Unable to load products
                </h3>

                <p>
                    ${escapeHtml(error.message)}
                </p>

                <button
                    type="button"
                    onclick="loadProducts()"
                >
                    Retry
                </button>

            </div>

        `;
    }
}


/* =========================================================
   RENDER PRODUCTS
========================================================= */

function renderProducts(products) {

    const container =
        $("productsContent");

    if (!container) {
        return;
    }

    if (
        !Array.isArray(products) ||
        products.length === 0
    ) {

        container.innerHTML = `

            <div
                style="
                    padding:40px;
                    text-align:center;
                "
            >

                <h3>
                    No products found.
                </h3>

            </div>

        `;

        return;
    }

    let html = `

        <div
            style="
                width:100%;
                overflow-x:auto;
            "
        >

            <table
                style="
                    width:100%;
                    border-collapse:collapse;
                "
            >

                <thead>

                    <tr>

                        <th>ID</th>
                        <th>Image</th>
                        <th>Name</th>
                        <th>Category</th>
                        <th>Festival</th>
                        <th>Relationship</th>
                        <th>Price</th>
                        <th>Stock</th>
                        <th>Status</th>
                        <th>Actions</th>

                    </tr>

                </thead>

                <tbody>

    `;

    products.forEach(
        product => {

            const active =
                product.active === true ||
                product.active === 1 ||
                product.active === "true" ||
                product.active === "1";

            const stock =
                Number(product.stock || 0);

            const image =
                product.image || "";

            const imageHtml =
                image

                    ? `
                        <img
                            src="${escapeHtml(image)}"
                            alt="${escapeHtml(product.name || "")}"
                            style="
                                width:60px;
                                height:60px;
                                object-fit:cover;
                                border-radius:8px;
                            "
                            onerror="
                                this.style.display='none';
                            "
                        >
                    `

                    : "No image";

            const categoryName =
                product.category?.name || "-";

            const festivalName =
                product.festival?.name || "-";

            const relationshipName =
                product.relationship?.name || "-";

            let statusHtml = "";

            if (!active) {

                statusHtml = `

                    <span
                        style="
                            display:inline-block;
                            padding:5px 10px;
                            border-radius:20px;
                            background:#fff0f0;
                            color:#dc3545;
                            font-weight:600;
                        "
                    >
                        Inactive
                    </span>

                `;

            } else if (stock <= 0) {

                statusHtml = `

                    <span
                        style="
                            display:inline-block;
                            padding:5px 10px;
                            border-radius:20px;
                            background:#fff3cd;
                            color:#856404;
                            font-weight:600;
                        "
                    >
                        Out of Stock
                    </span>

                `;

            } else {

                statusHtml = `

                    <span
                        style="
                            display:inline-block;
                            padding:5px 10px;
                            border-radius:20px;
                            background:#e8f7ee;
                            color:#198754;
                            font-weight:600;
                        "
                    >
                        Active
                    </span>

                `;
            }

            const stockHtml =
                stock <= 0

                    ? `
                        <span
                            style="
                                color:#dc3545;
                                font-weight:600;
                            "
                        >
                            0
                        </span>
                    `

                    : `
                        <span
                            style="
                                color:#198754;
                                font-weight:600;
                            "
                        >
                            ${escapeHtml(stock)}
                        </span>
                    `;

            const numericId =
                Number(product.id);

            const actionHtml =
                active

                    ? `
                        <button
                            type="button"
                            onclick="editProduct(${numericId})"
                        >
                            Edit
                        </button>

                        <button
                            type="button"
                            onclick="deleteProduct(${numericId})"
                        >
                            Delete
                        </button>
                    `

                    : `
                        <button
                            type="button"
                            onclick="editProduct(${numericId})"
                        >
                            Edit
                        </button>

                        <button
                            type="button"
                            onclick="restoreProduct(${numericId})"
                        >
                            Restore
                        </button>
                    `;

            html += `

                <tr>

                    <td>
                        ${escapeHtml(product.id)}
                    </td>

                    <td>
                        ${imageHtml}
                    </td>

                    <td>
                        <strong>
                            ${escapeHtml(product.name || "")}
                        </strong>
                    </td>

                    <td>
                        ${escapeHtml(categoryName)}
                    </td>

                    <td>
                        ${escapeHtml(festivalName)}
                    </td>

                    <td>
                        ${escapeHtml(relationshipName)}
                    </td>

                    <td>
                        ₹${Number(
                product.price || 0
            ).toLocaleString("en-IN")}
                    </td>

                    <td>
                        ${stockHtml}
                    </td>

                    <td>
                        ${statusHtml}
                    </td>

                    <td>
                        ${actionHtml}
                    </td>

                </tr>

            `;
        }
    );

    html += `

                </tbody>

            </table>

        </div>

    `;

    container.innerHTML =
        html;
}


/* =========================================================
   OPEN PRODUCT FORM
========================================================= */

function openProductForm() {

    editingProductId =
        null;

    const formContainer =
        $("productFormContainer");

    const form =
        $("productForm");

    const title =
        $("productFormTitle");

    const productId =
        $("productId");

    const message =
        $("productFormMessage");

    if (formContainer) {

        formContainer.classList.remove("hidden");

        formContainer.style.display =
            "block";
    }

    if (form) {

        form.reset();
    }

    if (title) {

        title.textContent =
            "Add Product";
    }

    if (productId) {

        productId.value =
            "";
    }

    if (message) {

        message.textContent =
            "";
    }

    console.log(
        "Add Product form opened"
    );
}


/* =========================================================
   CLOSE PRODUCT FORM
========================================================= */

function closeProductForm() {

    const formContainer =
        $("productFormContainer");

    const form =
        $("productForm");

    const title =
        $("productFormTitle");

    const productId =
        $("productId");

    const message =
        $("productFormMessage");

    if (formContainer) {

        formContainer.classList.add("hidden");

        formContainer.style.display =
            "none";
    }

    if (form) {

        form.reset();
    }

    editingProductId =
        null;

    if (title) {

        title.textContent =
            "Add Product";
    }

    if (productId) {

        productId.value =
            "";
    }

    if (message) {

        message.textContent =
            "";
    }

    console.log(
        "Product form closed"
    );
}


/* =========================================================
   EDIT PRODUCT
========================================================= */

async function editProduct(productId) {

    if (!isAdmin()) {

        alert(
            "Admin access required."
        );

        return;
    }

    if (
        productId === null ||
        productId === undefined ||
        productId === "" ||
        isNaN(Number(productId))
    ) {

        alert(
            "Invalid product ID."
        );

        return;
    }

    try {

        const response =
            await fetch(
                `${API_BASE_URL}/api/products/${Number(productId)}`,
                {
                    method: "GET",
                    headers: getAuthHeaders()
                }
            );

        const text =
            await response.text();

        if (!response.ok) {

            let message =
                text ||
                `Failed to load product: ${response.status}`;

            try {

                const errorData =
                    JSON.parse(text);

                message =
                    errorData.message ||
                    errorData.error ||
                    message;

            } catch (error) {}

            throw new Error(message);
        }

        let product;

        try {

            product =
                JSON.parse(text);

        } catch (error) {

            throw new Error(
                "Server returned invalid product data."
            );
        }

        if (
            !product ||
            product.id === null ||
            product.id === undefined
        ) {

            throw new Error(
                "Product data is invalid."
            );
        }

        editingProductId =
            product.id;

        const formContainer =
            $("productFormContainer");

        if (formContainer) {

            formContainer.classList.remove("hidden");

            formContainer.style.display =
                "block";
        }

        const productIdInput =
            $("productId");

        if (productIdInput) {

            productIdInput.value =
                product.id;
        }

        const productName =
            $("productName");

        if (productName) {

            productName.value =
                product.name || "";
        }

        const productDescription =
            $("productDescription");

        if (productDescription) {

            productDescription.value =
                product.description || "";
        }

        const productPrice =
            $("productPrice");

        if (productPrice) {

            productPrice.value =
                product.price ?? "";
        }

        const productOriginalPrice =
            $("productOriginalPrice");

        if (productOriginalPrice) {

            productOriginalPrice.value =
                product.originalPrice ?? "";
        }

        const productStock =
            $("productStock");

        if (productStock) {

            productStock.value =
                product.stock ?? 0;
        }

        const productImage =
            $("productImage");

        if (productImage) {

            productImage.value =
                product.image || "";
        }

        const productCategoryId =
            $("productCategoryId");

        if (productCategoryId) {

            productCategoryId.value =
                product.category?.id || "";
        }

        const productFestivalId =
            $("productFestivalId");

        if (productFestivalId) {

            productFestivalId.value =
                product.festival?.id || "";
        }

        const productRelationshipId =
            $("productRelationshipId");

        if (productRelationshipId) {

            productRelationshipId.value =
                product.relationship?.id || "";
        }

        const title =
            $("productFormTitle");

        if (title) {

            title.textContent =
                "Edit Product";
        }

        const message =
            $("productFormMessage");

        if (message) {

            message.textContent =
                "";
        }

        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });

        console.log(
            "Editing product:",
            product
        );

    } catch (error) {

        console.error(
            "Edit product error:",
            error
        );

        alert(
            "Unable to edit product.\n\n" +
            error.message
        );
    }
}


/* =========================================================
   ADD / UPDATE PRODUCT
========================================================= */

async function handleProductSubmit(event) {

    event.preventDefault();

    if (!isAdmin()) {

        alert(
            "Admin access required."
        );

        return;
    }

    const name =
        $("productName")
            ?.value
            .trim() || "";

    const description =
        $("productDescription")
            ?.value
            .trim() || "";

    const price =
        Number(
            $("productPrice")
                ?.value || 0
        );

    const originalPrice =
        Number(
            $("productOriginalPrice")
                ?.value || 0
        );

    const image =
        $("productImage")
            ?.value
            .trim() || "";

    const stock =
        Number(
            $("productStock")
                ?.value || 0
        );

    const categoryId =
        $("productCategoryId")
            ?.value
            .trim() || "";

    const festivalId =
        $("productFestivalId")
            ?.value
            .trim() || "";

    const relationshipId =
        $("productRelationshipId")
            ?.value
            .trim() || "";

    if (!name) {

        alert(
            "Product name is required."
        );

        return;
    }

    if (price < 0) {

        alert(
            "Product price cannot be negative."
        );

        return;
    }

    if (originalPrice < 0) {

        alert(
            "Original price cannot be negative."
        );

        return;
    }

    if (
        !Number.isInteger(stock) ||
        stock < 0
    ) {

        alert(
            "Stock must be a whole number and cannot be negative."
        );

        return;
    }

    const productData = {

        name,
        description,
        price,
        originalPrice,
        image,
        stock,

        categoryId:
            categoryId || null,

        festivalId:
            festivalId || null,

        relationshipId:
            relationshipId || null
    };

    console.log(
        "Product data being sent:",
        productData
    );

    try {

        const isEditing =
            Boolean(editingProductId);

        const url =
            isEditing
                ? `${API_BASE_URL}/api/products/${editingProductId}`
                : `${API_BASE_URL}/api/products`;

        const method =
            isEditing
                ? "PUT"
                : "POST";

        console.log(
            "Product request:",
            method,
            url
        );

        const response =
            await fetch(
                url,
                {
                    method,
                    headers:
                        getAuthHeaders(),

                    body:
                        JSON.stringify(
                            productData
                        )
                }
            );

        const text =
            await response.text();

        console.log(
            "Product save status:",
            response.status
        );

        console.log(
            "Product save response:",
            text
        );

        if (!response.ok) {

            let errorMessage =
                text ||
                `Request failed: ${response.status}`;

            try {

                const errorData =
                    JSON.parse(text);

                errorMessage =
                    errorData.message ||
                    errorData.error ||
                    errorMessage;

            } catch (error) {}

            throw new Error(
                errorMessage
            );
        }

        alert(
            isEditing
                ? "Product updated successfully."
                : "Product added successfully."
        );

        closeProductForm();

        await loadProducts();

        await loadDashboardStats();

    } catch (error) {

        console.error(
            "Product save error:",
            error
        );

        const message =
            $("productFormMessage");

        if (message) {

            message.textContent =
                error.message;
        }

        alert(
            "Product save failed.\n\n" +
            error.message
        );
    }
}


/* =========================================================
   SOFT DELETE PRODUCT
========================================================= */

async function deleteProduct(productId) {

    console.log(
        "Delete clicked. Product ID:",
        productId
    );

    if (!isAdmin()) {

        alert(
            "Admin access required."
        );

        return;
    }

    if (
        productId === null ||
        productId === undefined ||
        productId === "" ||
        isNaN(Number(productId))
    ) {

        alert(
            "Invalid product ID."
        );

        return;
    }

    const confirmed =
        confirm(
            "Are you sure you want to deactivate this product?"
        );

    if (!confirmed) {
        return;
    }

    try {

        const response =
            await fetch(
                `${API_BASE_URL}/api/products/${Number(productId)}`,
                {
                    method: "DELETE",
                    headers: getAuthHeaders()
                }
            );

        const text =
            await response.text();

        console.log(
            "Delete status:",
            response.status
        );

        console.log(
            "Delete response:",
            text
        );

        if (!response.ok) {

            let errorMessage =
                text ||
                `Delete failed with status: ${response.status}`;

            try {

                const errorData =
                    JSON.parse(text);

                errorMessage =
                    errorData.message ||
                    errorData.error ||
                    errorMessage;

            } catch (error) {}

            throw new Error(
                errorMessage
            );
        }

        alert(
            "Product deactivated successfully."
        );

        await loadProducts();

        await loadDashboardStats();

    } catch (error) {

        console.error(
            "Delete product error:",
            error
        );

        alert(
            "Product deactivation failed.\n\n" +
            error.message
        );
    }
}


/* =========================================================
   RESTORE PRODUCT
========================================================= */

async function restoreProduct(productId) {

    if (!isAdmin()) {

        alert(
            "Admin access required."
        );

        return;
    }

    if (
        productId === null ||
        productId === undefined ||
        productId === "" ||
        isNaN(Number(productId))
    ) {

        alert(
            "Invalid product ID."
        );

        return;
    }

    const confirmed =
        confirm(
            "Are you sure you want to restore this product?"
        );

    if (!confirmed) {
        return;
    }

    try {

        console.log(
            "Restoring product:",
            productId
        );

        const response =
            await fetch(
                `${API_BASE_URL}/api/products/${Number(productId)}/restore`,
                {
                    method: "PUT",
                    headers: getAuthHeaders()
                }
            );

        const responseText =
            await response.text();

        console.log(
            "Restore status:",
            response.status
        );

        console.log(
            "Restore response:",
            responseText
        );

        if (!response.ok) {

            let errorMessage =
                responseText ||
                `Restore failed with status ${response.status}`;

            try {

                const errorData =
                    JSON.parse(responseText);

                errorMessage =
                    errorData.message ||
                    errorData.error ||
                    errorMessage;

            } catch (error) {}

            throw new Error(
                errorMessage
            );
        }

        alert(
            "Product restored successfully."
        );

        await loadProducts();

        await loadDashboardStats();

    } catch (error) {

        console.error(
            "Restore product error:",
            error
        );

        alert(
            "Product restore failed.\n\n" +
            error.message
        );
    }
}


/* =========================================================
   ORDER STATUS OPTIONS
========================================================= */

const ORDER_STATUS_OPTIONS = [

    "PAYMENT_PENDING",
    "PLACED",
    "CONFIRMED",
    "PROCESSING",
    "SHIPPED",
    "OUT_FOR_DELIVERY",
    "DELIVERED",
    "COMPLETED",
    "CANCELLED"

];


/* =========================================================
   SHIPPING MODE OPTIONS
========================================================= */

const SHIPPING_MODE_OPTIONS = [

    "SHIPROCKET",
    "GOSWIFT",
    "OTHER"

];


/* =========================================================
   FORMAT ORDER STATUS
========================================================= */

function formatOrderStatus(status) {

    const labels = {

        PAYMENT_PENDING:
            "Payment Pending",

        PLACED:
            "Placed",

        CONFIRMED:
            "Confirmed",

        PROCESSING:
            "Processing",

        SHIPPED:
            "Shipped",

        OUT_FOR_DELIVERY:
            "Out for Delivery",

        DELIVERED:
            "Delivered",

        COMPLETED:
            "Completed",

        CANCELLED:
            "Cancelled"

    };

    return (
        labels[
            String(
                status || ""
            ).toUpperCase()
            ] ||
        status ||
        "-"
    );
}


/* =========================================================
   FORMAT SHIPPING MODE
========================================================= */

function formatShippingMode(mode) {

    const labels = {

        SHIPROCKET:
            "Shiprocket",

        GOSWIFT:
            "GoSwift",

        SELF_DELIVERY:
            "Self Delivery",

        OTHER:
            "Other"

    };

    return (
        labels[
            String(
                mode || ""
            ).toUpperCase()
            ] ||
        mode ||
        "-"
    );
}


/* =========================================================
   LOAD ORDERS
========================================================= */

async function loadOrders() {

    if (!isAdmin()) {
        return;
    }

    const container =
        $("ordersContent");

    if (!container) {
        return;
    }

    container.innerHTML = `

        <div
            style="
                padding:20px;
                text-align:center;
            "
        >
            Loading orders...
        </div>

    `;

    try {

        const response =
            await fetch(
                `${API_BASE_URL}/api/orders/admin/all`,
                {
                    method: "GET",
                    headers: getAuthHeaders()
                }
            );

        const text =
            await response.text();

        if (!response.ok) {

            throw new Error(
                text ||
                `Orders failed: ${response.status}`
            );
        }

        let orders;

        try {

            orders =
                JSON.parse(text);

        } catch (error) {

            throw new Error(
                "Server returned invalid orders data."
            );
        }

        renderOrders(orders);

    } catch (error) {

        console.error(
            "Load orders error:",
            error
        );

        container.innerHTML = `

            <div
                style="
                    padding:20px;
                    color:#b42318;
                "
            >

                Failed to load orders:

                ${escapeHtml(error.message)}

            </div>

        `;
    }
}


/* =========================================================
   RENDER ORDERS
========================================================= */

function renderOrders(orders) {

    const container =
        $("ordersContent");

    if (!container) {
        return;
    }

    if (
        !Array.isArray(orders) ||
        orders.length === 0
    ) {

        container.innerHTML = `

            <div
                style="
                    padding:40px;
                    text-align:center;
                "
            >

                <h3>
                    No orders found.
                </h3>

            </div>

        `;

        return;
    }

    let html = `

        <div class="orders-table-wrapper">

            <table class="orders-table">

                <thead>

                    <tr>

                        <th>Order ID</th>
                        <th>Customer</th>
                        <th>Total</th>
                        <th>Payment Mode</th>
                        <th>Payment Status</th>
                        <th>Order Status</th>
                        <th>Shipping Mode</th>
                        <th>Tracking ID</th>
                        <th>Order Date</th>
                        <th>Action</th>

                    </tr>

                </thead>

                <tbody>

    `;

    orders.forEach(
        order => {

            const orderId =
                Number(order.id);

            const customerName =
                order.user?.name ||
                order.user?.email ||
                "-";

            const totalAmount =
                Number(
                    order.totalAmount || 0
                );

            const paymentMethod =
                String(
                    order.paymentMethod || "-"
                ).toUpperCase();

            const paymentStatus =
                String(
                    order.paymentStatus || "-"
                ).toUpperCase();

            const orderStatus =
                String(
                    order.orderStatus ||
                    "PAYMENT_PENDING"
                ).toUpperCase();

            const shippingMode =
                String(
                    order.shippingMode || ""
                ).toUpperCase();

            const trackingId =
                order.trackingId || "";

            html += `

                <tr>

                    <td>
                        <strong>
                            #RB${escapeHtml(orderId)}
                        </strong>
                    </td>

                    <td>
                        ${escapeHtml(customerName)}
                    </td>

                    <td>
                        <strong>
                            ₹${totalAmount.toLocaleString("en-IN")}
                        </strong>
                    </td>

                    <td>

                        <span class="order-readonly-value">
                            ${escapeHtml(paymentMethod)}
                        </span>

                    </td>

                    <td>

                        <span class="order-status-badge">
                            ${escapeHtml(paymentStatus)}
                        </span>

                    </td>

                    <td>

                        <select
                            id="order-status-${orderId}"
                            class="order-edit-select"
                        >

                            ${ORDER_STATUS_OPTIONS
                .map(
                    status => `
                                        <option
                                            value="${status}"
                                            ${
                        status === orderStatus
                            ? "selected"
                            : ""
                    }
                                        >
                                            ${formatOrderStatus(status)}
                                        </option>
                                    `
                )
                .join("")}

                        </select>

                    </td>

                    <td>

                        <select
                            id="shipping-mode-${orderId}"
                            class="order-edit-select"
                        >

                            <option value="">
                                Select
                            </option>

                            ${SHIPPING_MODE_OPTIONS
                .map(
                    mode => `
                                        <option
                                            value="${mode}"
                                            ${
                        mode === shippingMode
                            ? "selected"
                            : ""
                    }
                                        >
                                            ${formatShippingMode(mode)}
                                        </option>
                                    `
                )
                .join("")}

                        </select>

                    </td>

                    <td>

                        <input
                            type="text"
                            id="tracking-id-${orderId}"
                            class="tracking-input"
                            value="${escapeHtml(trackingId)}"
                            placeholder="Tracking ID"
                        >

                    </td>

                    <td>

                        ${formatDate(order.createdAt)}

                    </td>

                    <td>

                        <button
                            type="button"
                            class="
                                primary-btn
                                order-save-btn
                            "
                            onclick="
                                updateAdminOrder(${orderId})
                            "
                        >
                            Save
                        </button>

                    </td>

                </tr>

            `;
        }
    );

    html += `

                </tbody>

            </table>

        </div>

    `;

    container.innerHTML =
        html;
}


/* =========================================================
   UPDATE ORDER FROM ADMIN
========================================================= */

async function updateAdminOrder(orderId) {

    if (!isAdmin()) {

        alert(
            "Admin access required."
        );

        return;
    }

    if (
        orderId === null ||
        orderId === undefined ||
        orderId === "" ||
        isNaN(Number(orderId))
    ) {

        alert(
            "Invalid order ID."
        );

        return;
    }

    const statusElement =
        document.getElementById(
            `order-status-${orderId}`
        );

    const shippingElement =
        document.getElementById(
            `shipping-mode-${orderId}`
        );

    const trackingElement =
        document.getElementById(
            `tracking-id-${orderId}`
        );

    if (
        !statusElement ||
        !shippingElement ||
        !trackingElement
    ) {

        alert(
            "Order fields not found."
        );

        return;
    }

    const orderStatus =
        statusElement.value;

    const shippingMode =
        shippingElement.value ||
        null;

    const trackingId =
        trackingElement.value.trim() ||
        null;

    const updateData = {

        orderStatus,

        shippingMode,

        trackingId
    };

    console.log(
        "Updating admin order:",
        orderId,
        updateData
    );

    try {

        const response =
            await fetch(
                `${API_BASE_URL}/api/orders/admin/${Number(orderId)}`,
                {
                    method: "PUT",

                    headers: {
                        ...getAuthHeaders(),

                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(updateData)
                }
            );

        const responseText =
            await response.text();

        console.log(
            "Admin order update status:",
            response.status
        );

        console.log(
            "Admin order update response:",
            responseText
        );

        if (!response.ok) {

            let message =
                responseText ||
                `Order update failed: ${response.status}`;

            try {

                const errorData =
                    JSON.parse(responseText);

                message =
                    errorData.message ||
                    errorData.error ||
                    message;

            } catch (error) {}

            throw new Error(message);
        }

        alert(
            "Order updated successfully."
        );

        await loadOrders();

        await loadDashboardStats();

    } catch (error) {

        console.error(
            "Admin order update error:",
            error
        );

        alert(
            "Order update failed.\n\n" +
            error.message
        );
    }
}


/* =========================================================
   LOAD USERS
========================================================= */

async function loadUsers() {

    const usersTableBody =
        $("usersTableBody");

    if (!usersTableBody) {

        console.error(
            "usersTableBody element not found."
        );

        return;
    }

    if (!isAdmin()) {

        console.warn(
            "loadUsers blocked. Current role:",
            getUserRole()
        );

        usersTableBody.innerHTML = `

            <tr>

                <td
                    colspan="9"
                    class="empty-state"
                >
                    Access denied. Admin required.
                </td>

            </tr>

        `;

        return;
    }

    usersTableBody.innerHTML = `

        <tr>

            <td
                colspan="9"
                class="empty-state"
            >
                Loading users...
            </td>

        </tr>

    `;

    const endpoint =
        `${API_BASE_URL}/api/users/admin/all`;

    try {

        console.log(
            "================================="
        );

        console.log(
            "Loading admin users"
        );

        console.log(
            "Endpoint:",
            endpoint
        );

        console.log(
            "Role:",
            getUserRole()
        );

        console.log(
            "Token available:",
            Boolean(getToken())
        );

        console.log(
            "================================="
        );


        const response =
            await fetch(
                endpoint,
                {
                    method: "GET",

                    headers:
                        getAuthHeaders()
                }
            );


        const responseText =
            await response.text();


        console.log(
            "Users HTTP Status:",
            response.status
        );

        console.log(
            "Users Response:",
            responseText
        );


        if (!response.ok) {

            let errorMessage =
                responseText ||
                `HTTP ${response.status}`;


            try {

                const errorData =
                    JSON.parse(
                        responseText
                    );

                errorMessage =
                    errorData.message ||
                    errorData.error ||
                    errorData.detail ||
                    errorMessage;

            } catch (parseError) {

                console.warn(
                    "Could not parse error response."
                );
            }


            throw new Error(
                `Users API failed: ${errorMessage}`
            );
        }


        let data = [];


        if (responseText.trim()) {

            try {

                data =
                    JSON.parse(
                        responseText
                    );

            } catch (parseError) {

                throw new Error(
                    "Backend returned invalid JSON."
                );
            }
        }


        let users = [];


        if (Array.isArray(data)) {

            users = data;

        } else if (
            data &&
            Array.isArray(data.users)
        ) {

            users =
                data.users;

        } else if (
            data &&
            Array.isArray(data.data)
        ) {

            users =
                data.data;

        } else if (
            data &&
            Array.isArray(data.content)
        ) {

            users =
                data.content;

        } else {

            console.error(
                "Unexpected users response:",
                data
            );

            throw new Error(
                "Backend returned unsupported users response."
            );
        }


        console.log(
            "Users loaded successfully:",
            users.length
        );

        console.table(
            users
        );


        /* =====================================================
           STORE USERS
        ===================================================== */

        allAdminUsers =
            users.map(
                normalizeAdminUser
            );


        /*
         * Start from first page whenever
         * fresh users are loaded.
         */

        currentUsersPage = 1;


        /* =====================================================
           APPLY SEARCH / FILTER / SORT
        ===================================================== */

        if (
            typeof applyUserFilters ===
            "function"
        ) {

            applyUserFilters();

        } else {

            /*
             * Fallback for current renderer
             * if filter functions have not
             * been added yet.
             */

            renderUsers(
                allAdminUsers
            );
        }


    } catch (error) {

        console.error(
            "loadUsers() ERROR:",
            error
        );


        usersTableBody.innerHTML = `

            <tr>

                <td
                    colspan="9"
                    class="empty-state"
                >

                    <h3>
                        Unable to Load Users
                    </h3>


                    <p>

                        ${escapeHtml(
            error.message ||
            "Unknown error"
        )}

                    </p>


                    <button
                        type="button"
                        class="primary-btn"
                        onclick="loadUsers()"
                    >
                        Retry
                    </button>

                </td>

            </tr>

        `;
    }
}


/* =========================================================
   RENDER USERS
========================================================= */
function renderUsers(users) {

    const usersTableBody =
        $("usersTableBody");

    if (!usersTableBody) {

        console.error(
            "usersTableBody element not found."
        );

        return;
    }

    if (
        !Array.isArray(users) ||
        users.length === 0
    ) {

        usersTableBody.innerHTML = `

            <tr>

                <td
                    colspan="9"
                    class="empty-state"
                >

                    <h3>
                        No users found
                    </h3>

                    <p>
                        No registered users are available.
                    </p>

                </td>

            </tr>

        `;

        updateUserManagementStats([]);

        return;
    }

    updateUserManagementStats(users);

    const rows =
        users
            .map(user => {

                const id =
                    user.id ??
                    user.userId ??
                    "-";

                const name =
                    user.name ??
                    user.fullName ??
                    "-";

                const email =
                    user.email ??
                    "-";

                const phone =
                    user.phone ??
                    user.mobile ??
                    user.mobileNumber ??
                    "-";

                /* =========================
                   ROLE
                ========================= */

                let role =
                    user.role ??
                    user.authority ??
                    "USER";

                if (
                    typeof role === "object"
                ) {

                    role =
                        role.name ||
                        role.authority ||
                        "USER";
                }

                role =
                    String(role)
                        .replace(
                            "ROLE_",
                            ""
                        )
                        .toUpperCase();

                /* =========================
                   ORDER COUNT
                ========================= */

                const orders =
                    user.ordersCount ??
                    user.orderCount ??
                    user.totalOrders ??
                    (
                        Array.isArray(user.orders)
                            ? user.orders.length
                            : 0
                    );

                /* =========================
                   TOTAL SPENT
                ========================= */

                const totalSpent =
                    Number(
                        user.totalSpent ??
                        user.totalAmountSpent ??
                        user.spent ??
                        0
                    );

                /* =========================
                   STATUS
                ========================= */

                let active =
                    user.active;

                if (
                    active === undefined ||
                    active === null
                ) {

                    active =
                        user.enabled;
                }

                if (
                    active === undefined ||
                    active === null
                ) {

                    if (
                        user.status !== undefined
                    ) {

                        active =
                            String(
                                user.status
                            ).toUpperCase() ===
                            "ACTIVE";

                    } else {

                        active = true;
                    }
                }

                const isActive =
                    active === true ||
                    active === 1 ||
                    active === "1" ||
                    String(active)
                        .toLowerCase() ===
                    "true";

                const status =
                    isActive
                        ? "Active"
                        : "Blocked";

                /* =========================
                   JOINED DATE
                ========================= */

                const joined =
                    user.createdAt ??
                    user.joinedAt ??
                    user.createdDate ??
                    user.registrationDate ??
                    user.date;

                /* =========================
                   ROLE CLASS
                ========================= */

                let roleClass = "user";

                if (role === "ADMIN") {

                    roleClass = "admin";

                } else if (
                    role === "SUPER_ADMIN"
                ) {

                    roleClass = "super-admin";
                }

                /* =========================
                   CURRENT ADMIN
                ========================= */

                const currentAdminId =
                    currentUser?.id ??
                    currentUser?.userId ??
                    null;

                const isCurrentAdmin =
                    currentAdminId !== null &&
                    String(currentAdminId) ===
                    String(id);

                /* =========================
                   ACTIONS
                ========================= */

                let actions = `
                    <button
                        type="button"
                        class="user-action-btn"
                        data-user-id="${escapeHtml(
                    String(id)
                )}"
                        onclick="openUserActionsMenu(this)"
                        aria-label="User actions"
                    >
                        ⋮
                    </button>
                `;

                /*
                 * Do not allow an admin to manage himself
                 * from another user's action menu.
                 */

                if (isCurrentAdmin) {

                    actions = `
                        <span
                            class="current-user-label"
                        >
                            You
                        </span>
                    `;
                }

                return `

                    <tr
                        data-user-id="${escapeHtml(
                    String(id)
                )}"
                    >

                        <!-- CHECKBOX -->

                        <td
                            class="checkbox-column"
                        >

                            <input
                                type="checkbox"
                                class="user-select-checkbox"
                                data-user-id="${escapeHtml(
                    String(id)
                )}"
                                aria-label="Select user ${escapeHtml(
                    String(name)
                )}"
                            >

                        </td>


                        <!-- USER -->

                        <td>

                            <div class="user-name">

                                <strong>
                                    ${escapeHtml(
                    String(name)
                )}
                                </strong>

                            </div>

                            <div class="user-id">

                                #${escapeHtml(
                    String(id)
                )}

                            </div>

                        </td>


                        <!-- CONTACT -->

                        <td>

                            <div>
                                ${escapeHtml(
                    String(email)
                )}
                            </div>

                            <div>
                                ${escapeHtml(
                    String(phone)
                )}
                            </div>

                        </td>


                        <!-- ROLE -->

                        <td>

                            <span
                                class="
                                    role-badge
                                    ${roleClass}
                                "
                            >

                                ${escapeHtml(role)}

                            </span>

                        </td>


                        <!-- ORDERS -->

                        <td>

                            <strong>
                                ${escapeHtml(
                    String(orders)
                )}
                            </strong>

                        </td>


                        <!-- TOTAL SPENT -->

                        <td>

                            <strong>
                                ₹${totalSpent.toLocaleString(
                    "en-IN"
                )}
                            </strong>

                        </td>


                        <!-- STATUS -->

                        <td>

                            <span
                                class="
                                    status-badge
                                    ${
                    isActive
                        ? "active"
                        : "blocked"
                }
                                "
                            >

                                ${status}

                            </span>

                        </td>


                        <!-- JOINED -->

                        <td>

                            ${formatDate(joined)}

                        </td>


                        <!-- ACTIONS -->

                        <td
                            class="user-actions-cell"
                        >

                            ${actions}

                        </td>

                    </tr>

                `;
            })
            .join("");


    usersTableBody.innerHTML =
        rows;


    /* =========================
       SELECT ALL RESET
    ========================= */

    const selectAll =
        $("selectAllUsers");

    if (selectAll) {

        selectAll.checked =
            false;

        selectAll.indeterminate =
            false;
    }


    /* =========================
       SELECTED COUNT
    ========================= */

    updateSelectedUsersCount();
}
/* =========================================================
   USER ACTIONS MENU
========================================================= */

function openUserActionsMenu(button) {

    if (!button) {
        return;
    }

    const userId =
        button.dataset.userId;

    if (!userId) {
        return;
    }

    /* Remove any existing menu */

    document
        .querySelectorAll(".user-actions-menu")
        .forEach(menu => menu.remove());


    const user =
        allAdminUsers.find(
            item =>
                String(
                    item.id ??
                    item.userId
                ) === String(userId)
        );

    if (!user) {

        console.error(
            "User not found:",
            userId
        );

        return;
    }


    /* =========================
       USER ROLE
    ========================= */

    let role =
        user.role ??
        user.authority ??
        "USER";

    if (typeof role === "object") {

        role =
            role.name ||
            role.authority ||
            "USER";
    }

    role =
        String(role)
            .replace("ROLE_", "")
            .toUpperCase();


    /* =========================
       STATUS
    ========================= */

    const active =
        user.active === true ||
        user.active === 1 ||
        user.active === "1" ||
        String(user.active)
            .toLowerCase() === "true";

    const currentAdminRole =
        getUserRole();


    /* =========================
       PERMISSIONS
    ========================= */

    const canChangeRole =
        currentAdminRole === "SUPER_ADMIN" &&
        role !== "SUPER_ADMIN";

    const canDelete =
        currentAdminRole === "SUPER_ADMIN" &&
        role !== "SUPER_ADMIN";

    const canManage =
        currentAdminRole === "SUPER_ADMIN" ||
        (
            currentAdminRole === "ADMIN" &&
            role === "USER"
        );


    /* =========================
       CREATE MENU
    ========================= */

    const menu =
        document.createElement("div");

    menu.className =
        "user-actions-menu";


    menu.innerHTML = `

        <button
            type="button"
            class="user-action-menu-item"
            onclick="viewUserDetails('${escapeHtml(
        String(userId)
    )}')"
        >
            <span>👁</span>
            View User
        </button>


        ${
        canManage
            ? `
                    <button
                        type="button"
                        class="user-action-menu-item"
                        onclick="editUser('${escapeHtml(
                String(userId)
            )}')"
                    >
                        <span>✏️</span>
                        Edit User
                    </button>
                `
            : ""
    }


        ${
        canChangeRole
            ? `
                    <button
                        type="button"
                        class="user-action-menu-item"
                        onclick="changeUserRole('${escapeHtml(
                String(userId)
            )}')"
                    >
                        <span>🔐</span>
                        Change Role
                    </button>
                `
            : ""
    }


        ${
        canManage
            ? `
                    <button
                        type="button"
                        class="user-action-menu-item"
                        onclick="toggleUserStatus('${escapeHtml(
                String(userId)
            )}')"
                    >
                        <span>
                            ${active ? "🚫" : "✓"}
                        </span>

                        ${
                active
                    ? "Block User"
                    : "Unblock User"
            }
                    </button>
                `
            : ""
    }


        ${
        canManage
            ? `
                    <button
                        type="button"
                        class="user-action-menu-item"
                        onclick="forceLogoutUser('${escapeHtml(
                String(userId)
            )}')"
                    >
                        <span>↪</span>
                        Force Logout
                    </button>
                `
            : ""
    }


        ${
        canDelete
            ? `
                    <div
                        class="user-action-menu-divider"
                    ></div>

                    <button
                        type="button"
                        class="
                            user-action-menu-item
                            danger
                        "
                        onclick="deleteUser('${escapeHtml(
                String(userId)
            )}')"
                    >
                        <span>🗑</span>
                        Delete User
                    </button>
                `
            : ""
    }

    `;


    document.body.appendChild(menu);


    /* =========================
       POSITION MENU
    ========================= */

    const rect =
        button.getBoundingClientRect();

    const menuWidth =
        210;

    let left =
        rect.right -
        menuWidth;

    let top =
        rect.bottom + 6;


    if (
        left < 8
    ) {

        left = 8;
    }


    if (
        left + menuWidth >
        window.innerWidth - 8
    ) {

        left =
            window.innerWidth -
            menuWidth -
            8;
    }


    const menuHeight =
        260;


    if (
        top + menuHeight >
        window.innerHeight - 8
    ) {

        top =
            rect.top -
            menuHeight -
            6;
    }


    menu.style.left =
        `${left}px`;

    menu.style.top =
        `${top}px`;


    /* =========================
       CLOSE WHEN CLICKING OUTSIDE
    ========================= */

    setTimeout(() => {

        document.addEventListener(
            "click",
            closeUserActionsMenu,
            {
                once: true
            }
        );

    }, 0);
}
function normalizeAdminUser(user) {

    const active =
        user.active === true ||
        user.active === 1 ||
        user.active === "1" ||
        String(user.active).toLowerCase() === "true";

    let role =
        user.role ||
        user.authority ||
        "USER";

    if (typeof role === "object") {
        role =
            role.name ||
            role.authority ||
            "USER";
    }

    role = String(role)
        .replace("ROLE_", "")
        .toUpperCase();

    return {

        id:
            user.id ??
            user.userId ??
            "",

        name:
            user.name ||
            user.fullName ||
            "Unknown User",

        email:
            user.email ||
            "",

        phone:
            user.phone ||
            user.mobile ||
            user.mobileNumber ||
            "",

        role: role,

        active: active,

        status:
            active
                ? "ACTIVE"
                : "BLOCKED",

        orders:
            Number(
                user.orders ??
                user.ordersCount ??
                user.orderCount ??
                user.totalOrders ??
                (
                    Array.isArray(user.orders)
                        ? user.orders.length
                        : 0
                )
            ),

        totalSpent:
            Number(
                user.totalSpent ??
                user.totalAmountSpent ??
                user.spent ??
                user.totalAmount ??
                0
            ),

        createdAt:
            user.createdAt ||
            user.joinedAt ||
            user.createdDate ||
            user.registrationDate ||
            user.date ||
            null
    };
}
/* =========================================================
   VIEW USER DETAILS
========================================================= */
function viewUserDetails(userId) {

    /* =====================================================
       CLOSE ACTION MENU
    ===================================================== */

    if (typeof closeUserActionsMenu === "function") {
        closeUserActionsMenu();
    }


    /* =====================================================
       FIND USER
    ===================================================== */

    const user =
        allAdminUsers.find(
            item =>
                String(
                    item.id ??
                    item.userId
                ) === String(userId)
        );


    if (!user) {

        console.error(
            "User not found:",
            userId
        );

        alert("User not found.");

        return;
    }


    /* =====================================================
       SELECTED USER
    ===================================================== */

    selectedUserId =
        user.id ??
        user.userId;


    /* =====================================================
       BASIC INFORMATION
    ===================================================== */

    const name =
        user.name ??
        user.fullName ??
        "Unknown User";

    const email =
        user.email ??
        "-";

    const phone =
        user.phone ??
        user.mobile ??
        user.mobileNumber ??
        "-";


    /* =====================================================
       ROLE
    ===================================================== */

    let role =
        user.role ??
        user.authority ??
        "USER";


    if (typeof role === "object") {

        role =
            role.name ||
            role.authority ||
            "USER";
    }


    role =
        String(role)
            .replace("ROLE_", "")
            .toUpperCase();


    /* =====================================================
       STATUS
    ===================================================== */

    const active =
        user.active === true ||
        user.active === 1 ||
        user.active === "1" ||
        String(user.active)
            .toLowerCase() === "true";


    const status =
        active
            ? "Active"
            : "Blocked";


    /* =====================================================
       ORDERS
    ===================================================== */

    let orders =
        user.ordersCount ??
        user.orderCount ??
        user.totalOrders;


    if (orders === undefined || orders === null) {

        orders =
            Array.isArray(user.orders)
                ? user.orders.length
                : (
                    user.orders ??
                    0
                );
    }


    orders =
        Number(orders) || 0;


    /* =====================================================
       TOTAL SPENT
    ===================================================== */

    const totalSpent =
        Number(
            user.totalSpent ??
            user.totalAmountSpent ??
            user.spent ??
            user.totalAmount ??
            0
        ) || 0;


    /* =====================================================
       JOINED DATE
    ===================================================== */

    const joined =
        user.createdAt ??
        user.joinedAt ??
        user.createdDate ??
        user.registrationDate ??
        user.date ??
        null;


    /* =====================================================
       GET MODAL ELEMENTS
    ===================================================== */

    const avatar =
        $("userDetailsAvatar");

    const detailsName =
        $("userDetailsName");

    const detailsRole =
        $("userDetailsRole");

    const detailsId =
        $("userDetailsId");

    const detailsEmail =
        $("userDetailsEmail");

    const detailsMobile =
        $("userDetailsMobile");

    const detailsStatus =
        $("userDetailsStatus");

    const detailsJoined =
        $("userDetailsJoined");

    const detailsOrders =
        $("userDetailsOrders");

    const detailsSpent =
        $("userDetailsSpent");


    /* =====================================================
       AVATAR
    ===================================================== */

    if (avatar) {

        avatar.textContent =
            String(name)
                .trim()
                .charAt(0)
                .toUpperCase() || "U";
    }


    /* =====================================================
       NAME
    ===================================================== */

    if (detailsName) {

        detailsName.textContent =
            name;
    }


    /* =====================================================
       ROLE
    ===================================================== */

    if (detailsRole) {

        detailsRole.textContent =
            role;

        detailsRole.className =
            "role-badge " +
            (
                role === "SUPER_ADMIN"
                    ? "super-admin"
                    : role === "ADMIN"
                        ? "admin"
                        : "user"
            );
    }


    /* =====================================================
       USER ID
    ===================================================== */

    if (detailsId) {

        detailsId.textContent =
            "#" +
            String(
                user.id ??
                user.userId ??
                "-"
            );
    }


    /* =====================================================
       EMAIL
    ===================================================== */

    if (detailsEmail) {

        detailsEmail.textContent =
            email;
    }


    /* =====================================================
       MOBILE
    ===================================================== */

    if (detailsMobile) {

        detailsMobile.textContent =
            phone;
    }


    /* =====================================================
       STATUS
    ===================================================== */

    if (detailsStatus) {

        detailsStatus.textContent =
            status;

        detailsStatus.className =
            active
                ? "status-badge active"
                : "status-badge blocked";
    }


    /* =====================================================
       JOINED
    ===================================================== */

    if (detailsJoined) {

        if (
            joined &&
            typeof formatDate === "function"
        ) {

            detailsJoined.textContent =
                formatDate(joined);

        } else if (joined) {

            const date =
                new Date(joined);

            detailsJoined.textContent =
                isNaN(date.getTime())
                    ? String(joined)
                    : date.toLocaleDateString(
                        "en-IN"
                    );

        } else {

            detailsJoined.textContent =
                "-";
        }
    }


    /* =====================================================
       ORDERS
    ===================================================== */

    if (detailsOrders) {

        detailsOrders.textContent =
            String(orders);
    }


    /* =====================================================
       TOTAL SPENT
    ===================================================== */

    if (detailsSpent) {

        detailsSpent.textContent =
            "₹" +
            totalSpent.toLocaleString(
                "en-IN"
            );
    }


    /* =====================================================
       ACTION BUTTONS
    ===================================================== */

    const editBtn =
        $("editUserBtn");

    const roleBtn =
        $("changeUserRoleBtn");

    const statusBtn =
        $("toggleUserStatusBtn");

    const deleteBtn =
        $("deleteUserBtn");


    /* =====================================================
       CURRENT ADMIN ROLE
    ===================================================== */

    const currentAdminRole =
        getUserRole();


    /* =====================================================
       PERMISSIONS
    ===================================================== */

    const isSuperAdmin =
        currentAdminRole ===
        "SUPER_ADMIN";


    const isAdmin =
        currentAdminRole ===
        "ADMIN";


    /*
     * ADMIN:
     * Can manage USER only.
     *
     * SUPER_ADMIN:
     * Can manage USER and ADMIN.
     *
     * SUPER_ADMIN cannot manage another
     * SUPER_ADMIN.
     */

    const canManage =
        (
            isSuperAdmin &&
            role !== "SUPER_ADMIN"
        ) ||
        (
            isAdmin &&
            role === "USER"
        );


    const canChangeRole =
        isSuperAdmin &&
        role !== "SUPER_ADMIN";


    const canDelete =
        isSuperAdmin &&
        role !== "SUPER_ADMIN";


    /* =====================================================
       EDIT BUTTON
    ===================================================== */

    if (editBtn) {

        editBtn.style.display =
            canManage
                ? ""
                : "none";
    }


    /* =====================================================
       CHANGE ROLE BUTTON
    ===================================================== */

    if (roleBtn) {

        roleBtn.style.display =
            canChangeRole
                ? ""
                : "none";
    }


    /* =====================================================
       BLOCK / UNBLOCK BUTTON
    ===================================================== */

    if (statusBtn) {

        statusBtn.style.display =
            canManage
                ? ""
                : "none";

        statusBtn.textContent =
            active
                ? "Block User"
                : "Unblock User";
    }


    /* =====================================================
       DELETE BUTTON
    ===================================================== */

    if (deleteBtn) {

        deleteBtn.style.display =
            canDelete
                ? ""
                : "none";
    }


    /* =====================================================
       OPEN MODAL
    ===================================================== */

    const modal =
        $("userDetailsModal");


    if (!modal) {

        console.error(
            "userDetailsModal not found."
        );

        return;
    }


    /*
     * Remove hidden state.
     */

    modal.classList.remove(
        "hidden"
    );


    /*
     * Clear any old inline display:none.
     */

    modal.style.display =
        "flex";


    /*
     * Make sure modal is above overlay.
     */

    modal.style.zIndex =
        "99999";


    console.log(
        "Viewing user:",
        user
    );

    console.log(
        "User details modal opened"
    );
}
/* =========================================================
   CLOSE USER DETAILS MODAL
========================================================= */

function closeUserDetails() {

    const modal =
        document.getElementById("userDetailsModal");

    if (modal) {

        modal.classList.add("hidden");

        modal.style.display = "none";
        modal.style.pointerEvents = "none";
    }

    selectedUserId = null;

    console.log("User Details modal closed");
}

window.closeUserDetails = closeUserDetails;
/* =========================================================
   EDIT USER
========================================================= */

function editUser(userId) {

    const user = allAdminUsers.find(
        u => String(u.id) === String(userId)
    );

    if (!user) {
        console.error("User not found:", userId);
        return;
    }

    const currentRole = getUserRole();

    const targetRole =
        String(user.role || "USER").toUpperCase();


    // ADMIN can edit only normal USER accounts
    // SUPER_ADMIN can edit USER and ADMIN accounts

    if (
        currentRole === "ADMIN" &&
        targetRole !== "USER"
    ) {

        alert(
            "Admin can edit only normal users."
        );

        return;
    }


    /* -----------------------------------------------------
       FILL USER DATA
    ----------------------------------------------------- */

    document.getElementById(
        "editUserId"
    ).value = user.id;

    document.getElementById(
        "editUserName"
    ).value = user.name || "";

    document.getElementById(
        "editUserEmail"
    ).value = user.email || "";

    document.getElementById(
        "editUserMobile"
    ).value = user.phone || "";


    /* -----------------------------------------------------
       ROLE
    ----------------------------------------------------- */

    const roleSelect =
        document.getElementById(
            "editUserRole"
        );

    const statusSelect =
        document.getElementById(
            "editUserStatus"
        );


    if (roleSelect) {

        roleSelect.value =
            targetRole;


        // ADMIN cannot change roles
        if (currentRole === "ADMIN") {

            roleSelect.disabled = true;

        } else {

            roleSelect.disabled = false;
        }
    }


    /* -----------------------------------------------------
       STATUS
    ----------------------------------------------------- */

    if (statusSelect) {

        statusSelect.value =
            user.active === false
                ? "BLOCKED"
                : "ACTIVE";


        // ADMIN can change status of USER only
        statusSelect.disabled =
            currentRole === "ADMIN" &&
            targetRole !== "USER";
    }


    /* -----------------------------------------------------
       CLEAR MESSAGE
    ----------------------------------------------------- */

    const message =
        document.getElementById(
            "editUserMessage"
        );

    if (message) {

        message.textContent = "";

        message.className =
            "form-message";
    }


    /* -----------------------------------------------------
       CLOSE USER DETAILS
    ----------------------------------------------------- */

    closeUserDetails();


    /* -----------------------------------------------------
       OPEN EDIT USER MODAL
    ----------------------------------------------------- */

    const modal =
        document.getElementById(
            "editUserModal"
        );


    if (!modal) {

        console.error(
            "editUserModal not found."
        );

        return;
    }


    modal.classList.remove(
        "hidden"
    );


    modal.style.display =
        "flex";

    modal.style.position =
        "fixed";

    modal.style.inset =
        "0";

    modal.style.zIndex =
        "100000";

    modal.style.alignItems =
        "center";

    modal.style.justifyContent =
        "center";


    console.log(
        "Edit User modal opened for:",
        user.id
    );
}

/* =========================================================
   CLOSE EDIT USER
========================================================= */

function closeEditUser() {

    const modal =
        document.getElementById("editUserModal");

    if (modal) {
        modal.classList.add("hidden");
    }

    const form =
        document.getElementById("editUserForm");

    if (form) {
        form.reset();
    }

    const message =
        document.getElementById("editUserMessage");

    if (message) {
        message.textContent = "";
        message.className = "form-message";
    }
}
/* =========================================================
   SAVE EDITED USER
========================================================= */

async function handleEditUserSubmit(event) {

    event.preventDefault();

    const id =
        document.getElementById("editUserId")?.value;

    const name =
        document.getElementById("editUserName")?.value.trim();

    const email =
        document.getElementById("editUserEmail")?.value.trim();

    const phone =
        document.getElementById("editUserMobile")?.value.trim();

    const role =
        document.getElementById("editUserRole")?.value;

    const status =
        document.getElementById("editUserStatus")?.value;

    const message =
        document.getElementById("editUserMessage");

    if (!id || !name || !email || !phone) {
        if (message) {
            message.textContent =
                "Please fill all required fields.";
            message.className =
                "form-message error";
        }
        return;
    }

    try {

        if (message) {
            message.textContent = "Saving changes...";
            message.className = "form-message";
        }

        /*
         * Update basic user information
         */
        const updateResponse = await fetch(
            `${API_BASE_URL}/api/users/admin/${id}`,
            {
                method: "PUT",
                headers: getAuthHeaders(),
                body: JSON.stringify({
                    name: name,
                    email: email,
                    phone: phone
                })
            }
        );

        const updateText =
            await updateResponse.text();

        if (!updateResponse.ok) {

            let errorMessage =
                "Failed to update user.";

            try {
                const errorData =
                    JSON.parse(updateText);

                errorMessage =
                    errorData.message ||
                    errorData.error ||
                    errorMessage;

            } catch (_) {}

            throw new Error(errorMessage);
        }

        /*
         * Role is changed separately because
         * backend protects role changes.
         */
        const existingUser =
            allAdminUsers.find(
                u => String(u.id) === String(id)
            );

        const oldRole =
            String(existingUser?.role || "USER")
                .toUpperCase();

        if (
            role &&
            role !== oldRole
        ) {

            const roleResponse = await fetch(
                `${API_BASE_URL}/api/users/admin/${id}/role`,
                {
                    method: "PUT",
                    headers: getAuthHeaders(),
                    body: JSON.stringify({
                        role: role
                    })
                }
            );

            const roleText =
                await roleResponse.text();

            if (!roleResponse.ok) {

                let errorMessage =
                    "Failed to change user role.";

                try {
                    const errorData =
                        JSON.parse(roleText);

                    errorMessage =
                        errorData.message ||
                        errorData.error ||
                        errorMessage;

                } catch (_) {}

                throw new Error(errorMessage);
            }
        }

        /*
         * Account status is changed separately.
         */
        const oldActive =
            existingUser?.active !== false;

        const newActive =
            status !== "BLOCKED";

        if (newActive !== oldActive) {

            const statusResponse = await fetch(
                `${API_BASE_URL}/api/users/admin/${id}/status`,
                {
                    method: "PUT",
                    headers: getAuthHeaders(),
                    body: JSON.stringify({
                        active: newActive
                    })
                }
            );

            const statusText =
                await statusResponse.text();

            if (!statusResponse.ok) {

                let errorMessage =
                    "Failed to update account status.";

                try {
                    const errorData =
                        JSON.parse(statusText);

                    errorMessage =
                        errorData.message ||
                        errorData.error ||
                        errorMessage;

                } catch (_) {}

                throw new Error(errorMessage);
            }
        }

        if (message) {
            message.textContent =
                "User updated successfully.";

            message.className =
                "form-message success";
        }

        /*
         * Refresh user list from backend.
         */
        await loadUsers();

        setTimeout(() => {
            closeEditUser();
        }, 600);

    } catch (error) {

        console.error(
            "Edit user error:",
            error
        );

        if (message) {
            message.textContent =
                error.message ||
                "Failed to update user.";

            message.className =
                "form-message error";
        }
    }
}
document.addEventListener("DOMContentLoaded", () => {

    const editForm =
        document.getElementById("editUserForm");

    if (editForm) {
        editForm.addEventListener(
            "submit",
            handleEditUserSubmit
        );
    }

    const closeBtn =
        document.getElementById("closeEditUserBtn");

    if (closeBtn) {
        closeBtn.addEventListener(
            "click",
            closeEditUser
        );
    }

    const cancelBtn =
        document.getElementById("cancelEditUserBtn");

    if (cancelBtn) {
        cancelBtn.addEventListener(
            "click",
            closeEditUser
        );
    }

    const overlay =
        document.getElementById("editUserOverlay");

    if (overlay) {
        overlay.addEventListener(
            "click",
            closeEditUser
        );
    }

});
/* =========================================================
   CLOSE USER ACTIONS MENU
========================================================= */

function closeUserActionsMenu(event) {

    const menu =
        document.querySelector(
            ".user-actions-menu"
        );

    if (!menu) {
        return;
    }

    if (
        event &&
        (
            menu.contains(event.target) ||
            event.target.closest(
                ".user-action-btn"
            )
        )
    ) {

        return;
    }

    menu.remove();
}
function updateUsersPagination() {

    const totalUsers =
        filteredAdminUsers.length;

    const totalPages =
        Math.max(
            1,
            Math.ceil(
                totalUsers /
                USERS_PER_PAGE
            )
        );

    if (currentUsersPage > totalPages) {
        currentUsersPage = totalPages;
    }

    const start =
        totalUsers === 0
            ? 0
            : (currentUsersPage - 1) *
            USERS_PER_PAGE + 1;

    const end =
        totalUsers === 0
            ? 0
            : Math.min(
                currentUsersPage *
                USERS_PER_PAGE,
                totalUsers
            );

    const info =
        $("usersPaginationInfo");

    if (info) {

        info.textContent =
            `Showing ${start}–${end} of ${totalUsers} users`;
    }


    const previous =
        $("usersPrevPage");

    const next =
        $("usersNextPage");


    if (previous) {

        previous.disabled =
            currentUsersPage <= 1;
    }

    if (next) {

        next.disabled =
            currentUsersPage >= totalPages;
    }


    const pageNumbers =
        $("usersPageNumbers");

    if (!pageNumbers) {
        return;
    }

    pageNumbers.innerHTML = "";


    for (
        let page = 1;
        page <= totalPages;
        page++
    ) {

        const button =
            document.createElement("button");

        button.type = "button";

        button.className =
            "page-number-btn" +
            (
                page === currentUsersPage
                    ? " active"
                    : ""
            );

        button.textContent =
            page;

        button.addEventListener(
            "click",
            () => {

                currentUsersPage =
                    page;

                renderUsersPage();
            }
        );

        pageNumbers.appendChild(
            button
        );
    }
}
/* =========================================================
   USER MANAGEMENT STATISTICS
========================================================= */function updateUserManagementStats(users) {

    const total =
        users.length;

    const active =
        users.filter(
            user => user.active === true
        ).length;

    const admins =
        users.filter(
            user =>
                String(user.role)
                    .toUpperCase() === "ADMIN"
        ).length;

    const superAdmins =
        users.filter(
            user =>
                String(user.role)
                    .toUpperCase() === "SUPER_ADMIN"
        ).length;


    const totalElement =
        $("totalUsersCount");

    const activeElement =
        $("activeUsersCount");

    const adminElement =
        $("adminUsersCount");

    const superAdminElement =
        $("superAdminUsersCount");


    if (totalElement) {
        totalElement.textContent =
            total;
    }

    if (activeElement) {
        activeElement.textContent =
            active;
    }

    if (adminElement) {
        adminElement.textContent =
            admins;
    }

    if (superAdminElement) {
        superAdminElement.textContent =
            superAdmins;
    }
}


/* =========================================================
   UPDATE SELECTED USERS COUNT
========================================================= */

function updateSelectedUsersCount() {

    const checkboxes =
        document.querySelectorAll(
            ".user-select-checkbox:checked"
        );

    const selectedCount =
        checkboxes.length;

    const selectedUsersCount =
        $("selectedUsersCount");

    if (selectedUsersCount) {

        selectedUsersCount.textContent =
            selectedCount;
    }

    const bulkActions =
        $("userBulkActions");

    if (bulkActions) {

        if (selectedCount > 0) {

            bulkActions.classList.remove(
                "hidden"
            );

        } else {

            bulkActions.classList.add(
                "hidden"
            );
        }
    }

    const selectAll =
        $("selectAllUsers");

    if (selectAll) {

        const allCheckboxes =
            document.querySelectorAll(
                ".user-select-checkbox"
            );

        const totalCheckboxes =
            allCheckboxes.length;

        const checkedCheckboxes =
            document.querySelectorAll(
                ".user-select-checkbox:checked"
            ).length;

        if (
            totalCheckboxes > 0 &&
            checkedCheckboxes ===
            totalCheckboxes
        ) {

            selectAll.checked =
                true;

            selectAll.indeterminate =
                false;

        } else if (
            checkedCheckboxes > 0
        ) {

            selectAll.checked =
                false;

            selectAll.indeterminate =
                true;

        } else {

            selectAll.checked =
                false;

            selectAll.indeterminate =
                false;
        }
    }
}


/* =========================================================
   USER CHECKBOX EVENTS
========================================================= */

document.addEventListener(
    "change",
    function (event) {

        if (
            event.target.classList.contains(
                "user-select-checkbox"
            )
        ) {

            updateSelectedUsersCount();
        }

        if (
            event.target.id ===
            "selectAllUsers"
        ) {

            const checked =
                event.target.checked;

            const checkboxes =
                document.querySelectorAll(
                    ".user-select-checkbox"
                );

            checkboxes.forEach(
                checkbox => {

                    checkbox.checked =
                        checked;
                }
            );

            updateSelectedUsersCount();
        }
    }
);


/* =========================================================
   BLOGS
========================================================= */
function closeBlogForm() {

    const formContainer =
        document.getElementById("blogFormContainer");

    const form =
        document.getElementById("blogForm");

    if (form) {
        form.reset();
    }

    const blogId =
        document.getElementById("blogId");

    if (blogId) {
        blogId.value = "";
    }

    const author =
        document.getElementById("blogAuthor");

    if (author) {
        author.value = "RishtaBox";
    }

    const published =
        document.getElementById("blogPublished");

    if (published) {
        published.checked = true;
    }

    const formTitle =
        document.getElementById("blogFormTitle");

    if (formTitle) {
        formTitle.textContent = "Add Blog";
    }

    if (formContainer) {
        formContainer.classList.add("hidden");
    }
}
/* =========================================================
   RENDER BLOGS
========================================================= */

function renderBlogs(blogs) {

    const container = document.getElementById("blogsContent");

    if (!container) {
        console.error("blogsContent not found");
        return;
    }

    if (!Array.isArray(blogs) || blogs.length === 0) {

        container.innerHTML = `
            <div class="empty-state">
                <h3>No Blogs Found</h3>
                <p>Create your first blog to display it here.</p>
            </div>
        `;

        return;
    }


    container.innerHTML = blogs.map(blog => {

        const blogId = Number(blog.id);

        return `
            <article class="admin-blog-card">

                <div class="admin-blog-image">

                    <img
                        src="${getAdminBlogImage(blog.image)}"
                        alt="${escapeHTML(blog.title || "Blog")}"
                        onerror="
                            this.onerror=null;
                            this.src='../Frontend/images/logo.jpeg';
                        "
                    >

                </div>


                <div class="admin-blog-content">

                    <div class="admin-blog-header">

                        <div>

                            <h3>
                                ${escapeHTML(blog.title || "Untitled Blog")}
                            </h3>

                            <p class="admin-blog-author">
                                By ${escapeHTML(blog.author || "RishtaBox")}
                            </p>

                        </div>

                        <span class="blog-status-badge ${
            blog.published ? "published" : "unpublished"
        }">
                            ${blog.published ? "Published" : "Unpublished"}
                        </span>

                    </div>


                    <p class="admin-blog-description">
                        ${escapeHTML(blog.description || "")}
                    </p>


                    <div class="admin-blog-meta">

                        <span>
                            ${formatAdminBlogDate(blog.createdAt)}
                        </span>

                        <span>
                            Blog ID: ${blogId}
                        </span>

                    </div>


                    <div class="admin-blog-actions">

                        <button
                            type="button"
                            class="outline-btn"
                            onclick="editBlog(${blogId})"
                        >
                            Edit
                        </button>


                        <button
                            type="button"
                            class="danger-btn"
                            onclick="deleteBlog(${blogId})"
                        >
                            Delete
                        </button>

                    </div>

                </div>

            </article>
        `;

    }).join("");
}
function getAdminBlogImage(image) {

    if (!image) {
        return "../Frontend/images/logo.jpeg";
    }

    const value = String(image).trim();

    // Full URL
    if (
        value.startsWith("http://") ||
        value.startsWith("https://")
    ) {
        return value;
    }

    // Absolute path
    if (value.startsWith("/")) {
        return value;
    }

    // Already contains images/
    if (value.startsWith("images/")) {
        return `../Frontend/${value}`;
    }

    // Only filename, for example: wedding.jpg
    return `../Frontend/images/${value}`;
}
function escapeHTML(value) {

    if (value === null || value === undefined) {
        return "";
    }

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
function formatAdminBlogDate(date) {

    if (!date) {
        return "";
    }

    const parsedDate = new Date(date);

    if (Number.isNaN(parsedDate.getTime())) {
        return "";
    }

    return parsedDate.toLocaleDateString("en-IN", {
        day: "2-digit",
        month: "long",
        year: "numeric"
    });
}

async function editBlog(blogId) {

    console.log("EDIT BLOG ID:", blogId);

    if (!blogId) {
        alert("Invalid blog ID.");
        return;
    }

    try {

        const token =
            getToken();

        if (!token) {
            alert("Admin session expired. Please login again.");
            return;
        }

        const response = await fetch(
            `${API_BASE_URL}/api/blogs/${blogId}`,
            {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            }
        );

        const blog = await response.json();

        if (!response.ok) {
            throw new Error(
                blog.message ||
                `Failed to load blog (${response.status})`
            );
        }

        openBlogForm(blog);

    } catch (error) {

        console.error("EDIT BLOG ERROR:", error);

        alert(
            error.message ||
            "Unable to load blog for editing."
        );
    }
}
function openBlogForm(blog = null) {

    const formContainer =
        document.getElementById("blogFormContainer");

    const formTitle =
        document.getElementById("blogFormTitle");

    const blogId =
        document.getElementById("blogId");

    const title =
        document.getElementById("blogTitle");

    const description =
        document.getElementById("blogDescription");

    const content =
        document.getElementById("blogContent");

    const image =
        document.getElementById("blogImage");

    const author =
        document.getElementById("blogAuthor");

    const published =
        document.getElementById("blogPublished");

    if (!formContainer) {
        console.error("blogFormContainer not found.");
        return;
    }


    formContainer.classList.remove("hidden");


    if (blog) {

        // EDIT MODE

        formTitle.textContent = "Edit Blog";

        blogId.value = blog.id || "";

        title.value = blog.title || "";

        description.value =
            blog.description || "";

        content.value =
            blog.content || "";

        image.value =
            blog.image || "";

        author.value =
            blog.author || "RishtaBox";

        published.checked =
            blog.published !== false;

    } else {

        // ADD MODE

        formTitle.textContent = "Add Blog";

        blogId.value = "";

        title.value = "";

        description.value = "";

        content.value = "";

        image.value = "";

        author.value = "RishtaBox";

        published.checked = true;
    }


    formContainer.scrollIntoView({
        behavior: "smooth",
        block: "start"
    });
}
async function deleteBlog(blogId) {

    console.log("DELETE BLOG ID:", blogId);


    if (!blogId) {

        alert("Invalid blog ID.");

        return;
    }


    if (
        !confirm(
            "Are you sure you want to delete this blog?\n\n" +
            "This action cannot be undone."
        )
    ) {
        return;
    }


    try {

        const token =
            currentUser?.token ||
            localStorage.getItem("token");


        if (!token) {

            alert(
                "Admin session expired. Please login again."
            );

            return;
        }


        const response = await fetch(

            `${API_BASE_URL}/api/blogs/${blogId}`,

            {
                method: "DELETE",

                headers: {
                    "Authorization":
                        `Bearer ${token}`
                }
            }
        );


        const text =
            await response.text();


        let data = {};

        try {
            data =
                text ? JSON.parse(text) : {};
        } catch {
            data = {};
        }


        if (!response.ok) {

            throw new Error(
                data.message ||
                text ||
                `Delete failed (${response.status})`
            );
        }


        alert(
            "Blog deleted successfully."
        );


        await loadBlogs();


    } catch (error) {

        console.error(
            "DELETE BLOG ERROR:",
            error
        );

        alert(
            error.message ||
            "Unable to delete blog."
        );
    }
}
/* =========================================================
   DATE
========================================================= */

function formatDate(value) {

    if (!value) {
        return "-";
    }

    try {

        return new Date(value)
            .toLocaleString("en-IN");

    } catch (error) {

        return String(value);
    }
}

async function loadBlogs() {

    const container =
        document.getElementById("blogsContent");

    if (!container) {
        console.error("blogsContent not found.");
        return;
    }


    try {

        container.innerHTML = `
            <div class="empty-state">
                Loading blogs...
            </div>
        `;


        const token =
            currentUser?.token ||
            localStorage.getItem("token");


        const response = await fetch(
            `${API_BASE_URL}/api/blogs/all`,
            {
                method: "GET",

                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            }
        );


        const blogs = await response.json();


        if (!response.ok) {

            throw new Error(
                blogs.message ||
                `Failed to load blogs (${response.status})`
            );
        }


        renderBlogs(blogs);


    } catch (error) {

        console.error("LOAD BLOGS ERROR:", error);


        container.innerHTML = `
            <div class="empty-state">
                <h3>Blogs unavailable</h3>
                <p>${escapeHTML(error.message)}</p>
            </div>
        `;
    }
}
async function saveBlog(event) {

    event.preventDefault();


    const blogId =
        document.getElementById("blogId").value.trim();

    const title =
        document.getElementById("blogTitle").value.trim();

    const description =
        document.getElementById("blogDescription").value.trim();

    const content =
        document.getElementById("blogContent").value.trim();

    const image =
        document.getElementById("blogImage").value.trim();

    const author =
        document.getElementById("blogAuthor").value.trim();

    const published =
        document.getElementById("blogPublished").checked;


    if (!title || !description || !content || !image || !author) {

        alert("Please fill all required fields.");

        return;
    }


    const token =
        currentUser?.token ||
        localStorage.getItem("token");


    if (!token) {

        alert("Admin session expired. Please login again.");

        return;
    }


    const blogData = {

        title: title,

        description: description,

        content: content,

        image: image,

        author: author,

        published: published
    };


    const isEdit =
        Boolean(blogId);


    try {

        const response = await fetch(

            isEdit
                ? `${API_BASE_URL}/api/blogs/${blogId}`
                : `${API_BASE_URL}/api/blogs`,

            {

                method: isEdit
                    ? "PUT"
                    : "POST",

                headers: {

                    "Authorization":
                        `Bearer ${token}`,

                    "Content-Type":
                        "application/json"
                },

                body:
                    JSON.stringify(blogData)
            }
        );


        const data =
            await response.json();


        if (!response.ok) {

            throw new Error(
                data.message ||
                `Failed to ${isEdit ? "update" : "create"} blog`
            );
        }


        alert(
            isEdit
                ? "Blog updated successfully."
                : "Blog created successfully."
        );


        closeBlogForm();


        await loadBlogs();


    } catch (error) {

        console.error(
            "SAVE BLOG ERROR:",
            error
        );

        alert(
            error.message ||
            "Unable to save blog."
        );
    }
}
document.addEventListener("DOMContentLoaded", function () {

    const blogForm =
        document.getElementById("blogForm");

    if (blogForm) {

        blogForm.addEventListener(
            "submit",
            saveBlog
        );
    }

});
/* =========================================================
   LOAD REVIEWS WHEN SECTION OPENS
========================================================= */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        const reviewNavButtons =
            document.querySelectorAll(
                '[data-section="reviews"]'
            );

        reviewNavButtons.forEach(button => {

            button.addEventListener(
                "click",
                function () {

                    setTimeout(
                        () => loadReviews(),
                        100
                    );
                }
            );
        });

    }
);
/* =========================================================
   REVIEWS MANAGEMENT
========================================================= */

const REVIEW_API_BASE_URL = "http://localhost:8080";

let allAdminReviews = [];
let currentAdminReviews = [];


/* =========================================================
   GET TOKEN
========================================================= */
function getReviewToken() {

    return getToken();
}
/* =========================================================
   REVIEW HEADERS
========================================================= */

function getReviewHeaders() {

    const headers = {
        "Content-Type": "application/json"
    };

    const token = getReviewToken();

    if (token) {
        headers["Authorization"] =
            "Bearer " + token;
    }

    return headers;
}


/* =========================================================
   HTML ESCAPE
========================================================= */

function escapeReviewHTML(value) {

    if (
        value === null ||
        value === undefined
    ) {
        return "";
    }

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}


/* =========================================================
   GET REVIEW DATA
========================================================= */

function getReviewId(review) {

    return (
        review.id ??
        review.reviewId ??
        review.review_id ??
        null
    );
}


function getReviewCustomerName(review) {

    return (
        review.user?.name ||
        review.user?.username ||
        review.customer?.name ||
        review.customerName ||
        review.userName ||
        review.name ||
        "Customer"
    );
}


function getReviewCustomerEmail(review) {

    return (
        review.user?.email ||
        review.customer?.email ||
        review.customerEmail ||
        review.email ||
        ""
    );
}


function getReviewProductName(review) {

    return (
        review.product?.name ||
        review.product?.title ||
        review.productName ||
        review.productTitle ||
        "Product"
    );
}


function getReviewText(review) {

    return (
        review.comment ||
        review.review ||
        review.text ||
        review.content ||
        review.message ||
        ""
    );
}


function getReviewRating(review) {

    const rating = Number(
        review.rating ??
        review.stars ??
        review.score ??
        0
    );

    return Number.isFinite(rating)
        ? Math.max(0, Math.min(5, rating))
        : 0;
}


function getReviewDate(review) {

    return (
        review.createdAt ||
        review.createdDate ||
        review.date ||
        review.updatedAt ||
        null
    );
}


function isReviewApproved(review) {
    if (!review) {
        return false;
    }

    // Backend sends boolean
    if (review.approved === true) {
        return true;
    }

    // Handle possible string/number values
    if (
        review.approved === "true" ||
        review.approved === 1 ||
        review.approved === "1"
    ) {
        return true;
    }

    // Fallback for older status-based responses
    const status = String(review.status || "").toUpperCase();

    return (
        status === "APPROVED" ||
        status === "ACTIVE" ||
        status === "PUBLISHED"
    );
}


/* =========================================================
   FORMAT DATE
========================================================= */

function formatReviewDate(dateValue) {

    if (!dateValue) {
        return "—";
    }

    const date = new Date(dateValue);

    if (Number.isNaN(date.getTime())) {
        return escapeReviewHTML(dateValue);
    }

    return date.toLocaleDateString("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric"
    });
}


/* =========================================================
   STARS
========================================================= */

function renderReviewStars(rating) {

    let stars = "";

    for (let i = 1; i <= 5; i++) {

        stars +=
            i <= rating
                ? "★"
                : "☆";
    }

    return `
        <span class="review-stars">
            ${stars}
        </span>
        <span>
            ${Number(rating).toFixed(1)}
        </span>
    `;
}


/* =========================================================
   NORMALIZE RESPONSE
========================================================= */
function normalizeReviewsResponse(data) {

    if (!Array.isArray(data)) {
        return [];
    }

    return data.map(review => {

        if (!review) {
            return null;
        }

        return {
            ...review,

            id: review.id,

            productId: review.productId,

            productName: review.productName || "Unknown Product",

            userName: review.userName || "Unknown User",

            rating: Number(review.rating || 0),

            comment: review.comment || "",

            verifiedPurchaser:
                review.verifiedPurchaser === true ||
                review.verifiedPurchaser === "true" ||
                review.verifiedPurchaser === 1 ||
                review.verifiedPurchaser === "1",

            // IMPORTANT
            approved:
                review.approved === true ||
                review.approved === "true" ||
                review.approved === 1 ||
                review.approved === "1",

            createdAt: review.createdAt || null
        };
    }).filter(Boolean);
}

/* =========================================================
   LOAD REVIEWS
========================================================= */
async function loadReviews() {
    try {
        const token = getReviewToken();

        if (!token) {
            return;
        }

        const response = await fetch(
            `${REVIEW_API_BASE_URL}/api/reviews/admin/all`,
            {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token,
                    "Content-Type": "application/json"
                }
            }
        );

        if (!response.ok) {
            throw new Error(
                `Failed to load reviews (${response.status})`
            );
        }

        const data = await response.json();

        console.log("Raw reviews from backend:", data);

        currentAdminReviews = normalizeReviewsResponse(data);

        console.log(
            "Normalized reviews:",
            currentAdminReviews
        );

        updateReviewStats();
        renderReviews();

    } catch (error) {
        console.error("Reviews loading error:", error);
    }
}
/* =========================================================
   REVIEW STATISTICS
========================================================= */
function updateReviewStats() {

    const reviews = Array.isArray(currentAdminReviews)
        ? currentAdminReviews
        : [];

    const total = reviews.length;

    const approved = reviews.filter(function (review) {
        return isReviewApproved(review);
    }).length;

    const pending = total - approved;

    const ratings = reviews
        .map(function (review) {
            return Number(review.rating);
        })
        .filter(function (rating) {
            return Number.isFinite(rating) && rating > 0;
        });

    const averageRating = ratings.length > 0
        ? ratings.reduce(function (sum, rating) {
        return sum + rating;
    }, 0) / ratings.length
        : 0;


    /*
     * Find the Reviews section
     */
    const reviewsSection = document.getElementById("reviews");

    if (!reviewsSection) {
        console.error("Reviews section not found.");
        return;
    }


    /*
     * Find all stat cards inside Reviews
     */
    const statCards = reviewsSection.querySelectorAll(
        ".stat-card, .review-stat-card, .stat-item, .review-stat"
    );

    console.log("Review stat cards:", statCards.length);


    /*
     * Update cards by their position
     */
    if (statCards.length >= 4) {

        const values = [
            String(total),
            String(approved),
            String(pending),
            averageRating.toFixed(1)
        ];

        statCards.forEach(function (card, index) {

            if (index >= 4) {
                return;
            }

            /*
             * Find the number inside the card
             */
            const valueElement =
                card.querySelector(
                    ".stat-value, .stat-number, .stat-count, .value, strong, h3, h2"
                );

            if (valueElement) {
                valueElement.textContent = values[index];
            } else {
                console.warn(
                    "Could not find number element in stat card:",
                    card
                );
            }
        });

    } else {

        /*
         * Fallback to IDs
         */
        const elements = [
            document.getElementById("totalReviews"),
            document.getElementById("approvedReviews"),
            document.getElementById("pendingReviews"),
            document.getElementById("averageRating")
        ];

        const values = [
            String(total),
            String(approved),
            String(pending),
            averageRating.toFixed(1)
        ];

        elements.forEach(function (element, index) {

            if (element) {
                element.textContent = values[index];
            }
        });
    }


    console.log("Review count updated:", {
        total: total,
        approved: approved,
        pending: pending,
        average: averageRating.toFixed(1)
    });
}
/* =========================================================
   RENDER REVIEWS
========================================================= */

function renderReviews() {

    const container =
        document.getElementById(
            "reviewsContent"
        );


    if (!container) {
        return;
    }


    const reviews =
        currentAdminReviews || [];


    if (reviews.length === 0) {

        container.innerHTML = `
            <div class="empty-state">
                No customer reviews found.
            </div>
        `;

        return;
    }


    let html = `

        <div class="reviews-table-wrapper">

            <table class="reviews-table">

                <thead>

                    <tr>

                        <th>Customer</th>

                        <th>Product</th>

                        <th>Rating</th>

                        <th>Review</th>

                        <th>Status</th>

                        <th>Date</th>

                        <th>Actions</th>

                    </tr>

                </thead>

                <tbody>
    `;


    reviews.forEach(review => {

        const id =
            getReviewId(review);

        const customer =
            getReviewCustomerName(review);

        const email =
            getReviewCustomerEmail(review);

        const product =
            getReviewProductName(review);

        const text =
            getReviewText(review);

        const rating =
            getReviewRating(review);

        const date =
            formatReviewDate(
                getReviewDate(review)
            );

        const approved =
            isReviewApproved(review);


        html += `

            <tr>

                <td>

                    <strong>
                        ${escapeReviewHTML(customer)}
                    </strong>

                    ${
            email
                ? `
                                <br>
                                <small>
                                    ${escapeReviewHTML(email)}
                                </small>
                              `
                : ""
        }

                </td>


                <td>
                    ${escapeReviewHTML(product)}
                </td>


                <td>

                    ${renderReviewStars(rating)}

                </td>


                <td>

                    <div class="review-text">
                        ${escapeReviewHTML(text)}
                    </div>

                </td>


                <td>

                    <span class="review-status ${
            approved
                ? "approved"
                : "pending"
        }">

                        ${
            approved
                ? "Approved"
                : "Pending"
        }

                    </span>

                </td>


                <td>
                    ${date}
                </td>


                <td>

                    <div class="review-actions">

                        ${
            approved
                ? `
                                    <button
                                        type="button"
                                        class="outline-btn"
                                        onclick="updateReviewStatus(${Number(id)}, false)"
                                    >
                                        Hide
                                    </button>
                                  `
                : `
                                    <button
                                        type="button"
                                        class="primary-btn"
                                        onclick="updateReviewStatus(${Number(id)}, true)"
                                    >
                                        Approve
                                    </button>
                                  `
        }


                        <button
                                type="button"
                                class="danger-btn"
                                onclick="deleteReview(${Number(id)})"
                        >
                            Delete
                        </button>

                    </div>

                </td>

            </tr>
        `;

    });


    html += `

                </tbody>

            </table>

        </div>
    `;


    container.innerHTML = html;

}


/* =========================================================
   APPROVE / HIDE REVIEW
========================================================= */
async function updateReviewStatus(reviewId, approved) {
    if (!reviewId) {
        console.error("Review ID missing:", reviewId);
        alert("Review ID is missing.");
        return;
    }

    if (!currentUser || !currentUser.token) {
        alert("Admin session expired. Please login again.");
        return;
    }

    try {
        const response = await fetch(
            `${API_BASE_URL}/api/reviews/admin/${reviewId}/status`,
            {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + currentUser.token
                },
                body: JSON.stringify({
                    approved: Boolean(approved)
                })
            }
        );

        const responseText = await response.text();

        console.log("Approve response status:", response.status);
        console.log("Approve response:", responseText);

        if (!response.ok) {
            throw new Error(
                responseText || `Failed to update review (${response.status})`
            );
        }

        alert(
            approved
                ? "Review approved successfully."
                : "Review rejected successfully."
        );

        await loadReviews();

    } catch (error) {
        console.error("Review status update error:", error);
        alert("Unable to update review: " + error.message);
    }
}
/* =========================================================
   DELETE REVIEW
========================================================= */

async function deleteReview(reviewId) {

    if (!reviewId) {

        alert("Review ID is missing.");

        return;
    }


    if (
        !confirm(
            "Are you sure you want to permanently delete this review?"
        )
    ) {
        return;
    }


    try {

        const response = await fetch(
            REVIEW_API_BASE_URL +
            `/api/reviews/admin/${reviewId}`,
            {
                method: "DELETE",
                headers: getReviewHeaders()
            }
        );


        if (!response.ok) {

            throw new Error(
                "Unable to delete review. HTTP " +
                response.status
            );

        }


        await loadReviews();


    } catch (error) {

        console.error(
            "Delete review error:",
            error
        );

        alert(
            error.message ||
            "Unable to delete review."
        );

    }

}

/* =========================================================
   TESTIMONIALS MANAGEMENT
========================================================= */

const TESTIMONIAL_API_BASE_URL = "http://localhost:8080";

let allAdminTestimonials = [];
let currentAdminTestimonials = [];


/* =========================================================
   GET TESTIMONIAL TOKEN
========================================================= */

function getTestimonialToken() {

    return getToken();
}

/* =========================================================
   TESTIMONIAL HEADERS
========================================================= */

function getTestimonialHeaders() {

    const headers = {
        "Content-Type": "application/json"
    };

    const token = getTestimonialToken();

    if (token) {

        headers["Authorization"] =
            "Bearer " + token;
    }

    return headers;
}


/* =========================================================
   ESCAPE HTML
========================================================= */

function escapeTestimonialHTML(value) {

    if (
        value === null ||
        value === undefined
    ) {
        return "";
    }

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}


/* =========================================================
   LOAD TESTIMONIALS
========================================================= */

async function loadTestimonials() {

    const container =
        document.getElementById(
            "testimonialsContent"
        );

    if (!container) {

        console.error(
            "testimonialsContent not found."
        );

        return;
    }

    const token =
        getTestimonialToken();

    if (!token) {

        container.innerHTML = `
<div class="empty-state">
    Admin session expired. Please login again.
</div>
`;

        return;
    }

    container.innerHTML = `
<div class="empty-state">
    Loading testimonials...
</div>
`;

    try {

        const response =
            await fetch(
                `${TESTIMONIAL_API_BASE_URL}/api/testimonials/admin/all`,
                {
                    method: "GET",
                    headers: getTestimonialHeaders()
                }
            );

        const responseText =
            await response.text();

        console.log(
            "Testimonials status:",
            response.status
        );

        console.log(
            "Testimonials response:",
            responseText
        );

        if (!response.ok) {

            let message =
                responseText ||
                `HTTP ${response.status}`;

            try {

                const errorData =
                    JSON.parse(responseText);

                message =
                    errorData.message ||
                    errorData.error ||
                    message;

            } catch (error) {}

            throw new Error(message);
        }

        let data = [];

        try {

            data =
                responseText
                    ? JSON.parse(responseText)
                    : [];

        } catch (error) {

            throw new Error(
                "Backend returned invalid JSON."
            );
        }

        if (Array.isArray(data)) {

            currentAdminTestimonials =
                data;

        } else if (
            Array.isArray(data.testimonials)
        ) {

            currentAdminTestimonials =
                data.testimonials;

        } else if (
            Array.isArray(data.data)
        ) {

            currentAdminTestimonials =
                data.data;

        } else {

            currentAdminTestimonials = [];
        }

        allAdminTestimonials =
            [...currentAdminTestimonials];

        updateTestimonialStats();

        renderTestimonials();

    } catch (error) {

        console.error(
            "LOAD TESTIMONIALS ERROR:",
            error
        );

        container.innerHTML = `
<div class="empty-state">

    <h3>
    Testimonials unavailable
</h3>

<p>
    ${escapeTestimonialHTML(
    error.message
)}
</p>

</div>
`;
    }
}


/* =========================================================
   TESTIMONIAL STATISTICS
========================================================= */

function updateTestimonialStats() {

    const testimonials =
        Array.isArray(
            currentAdminTestimonials
        )
            ? currentAdminTestimonials
            : [];

    const total =
        testimonials.length;

    const published =
        testimonials.filter(
            testimonial => {

                const status =
                    String(
                        testimonial.status ||
                        ""
                    ).toUpperCase();

                return (
                    status === "PUBLISHED" ||
                    testimonial.published === true
                );
            }
        ).length;

    const hidden =
        total - published;

    const ratings =
        testimonials
            .map(
                testimonial =>
                    Number(
                        testimonial.rating || 0
                    )
            )
            .filter(
                rating =>
                    Number.isFinite(rating) &&
                    rating > 0
            );

    const average =
        ratings.length > 0
            ? ratings.reduce(
                (sum, rating) =>
                    sum + rating,
                0
            ) / ratings.length
            : 0;


    const totalElement =
        document.getElementById(
            "totalTestimonialsCount"
        );

    const publishedElement =
        document.getElementById(
            "publishedTestimonialsCount"
        );

    const hiddenElement =
        document.getElementById(
            "hiddenTestimonialsCount"
        );

    const averageElement =
        document.getElementById(
            "averageTestimonialRating"
        );


    if (totalElement) {

        totalElement.textContent =
            total;
    }

    if (publishedElement) {

        publishedElement.textContent =
            published;
    }

    if (hiddenElement) {

        hiddenElement.textContent =
            hidden;
    }

    if (averageElement) {

        averageElement.textContent =
            average.toFixed(1);
    }
}


/* =========================================================
   RENDER TESTIMONIALS
========================================================= */

function renderTestimonials() {

    const container =
        document.getElementById("testimonialsContent");

    console.log(
        "TESTIMONIAL CONTAINER:",
        container
    );

    console.log(
        "TESTIMONIAL DATA:",
        currentAdminTestimonials
    );

    if (!container) {

        console.error(
            "ERROR: #testimonialsContent does not exist in HTML."
        );

        return;
    }

    const testimonials =
        Array.isArray(currentAdminTestimonials)
            ? currentAdminTestimonials
            : [];

    console.log(
        "TESTIMONIAL COUNT:",
        testimonials.length
    );

    if (testimonials.length === 0) {

        container.innerHTML = `
<div class="empty-state">
    <h3>No testimonials found</h3>
<p>Add your first customer testimonial.</p>
</div>
`;

        return;
    }

    let html = `
<div class="reviews-table-wrapper">
    <table class="reviews-table">

    <thead>
    <tr>
    <th>Customer</th>
<th>Rating</th>
<th>Testimonial</th>
<th>Status</th>
<th>Date</th>
<th>Actions</th>
</tr>
</thead>

<tbody>
`;

testimonials.forEach(testimonial => {

    const id =
    testimonial.id;

    const name =
    testimonial.customerName ||
    "Unknown Customer";

    const image =
    testimonial.customerImage ||
    "";

    const rating =
    Number(testimonial.rating || 0);

    const message =
    testimonial.message ||
    "";

    const status =
    String(
    testimonial.status || "HIDDEN"
    ).toUpperCase();

    const isPublished =
    status === "PUBLISHED";

    const date =
    testimonial.createdAt
    ? formatDate(testimonial.createdAt)
    : "-";

    const safeRating =
    Math.min(
    5,
    Math.max(
    0,
    rating
    )
    );

    const stars =
    "★".repeat(safeRating) +
    "☆".repeat(5 - safeRating);

    html += `
            <tr>

                <td>
                    <div style="
                        display:flex;
                        align-items:center;
                        gap:10px;
                    ">

                        ${
    image
    ? `
                                    <img
                                        src="${escapeTestimonialHTML(image)}"
                                        alt="${escapeTestimonialHTML(name)}"
                                        style="
                                            width:45px;
                                            height:45px;
                                            border-radius:50%;
                                            object-fit:cover;
                                        "
                                        onerror="this.style.display='none'"
                                    >
                                  `
    : ""
}

                        <strong>
                            ${escapeTestimonialHTML(name)}
                        </strong>

                    </div>
                </td>

                <td>
                    <span class="review-rating">
                        ${stars}
                    </span>
                </td>

                <td>
                    <div class="review-text">
                        ${escapeTestimonialHTML(message)}
                    </div>
                </td>

                <td>
                    <span class="
                        review-status
                        ${isPublished ? "approved" : "pending"}
                    ">
                        ${isPublished ? "Published" : "Hidden"}
                    </span>
                </td>

                <td>
                    ${date}
                </td>

                <td>
                    <div class="review-actions">

                        <button
                            type="button"
                            class="outline-btn"
                            onclick="editTestimonial(${Number(id)})"
                        >
                            Edit
                        </button>

                        <button
                            type="button"
                            class="primary-btn"
                            onclick="updateTestimonialStatus(${Number(id)}, ${!isPublished})"
                        >
                            ${isPublished ? "Hide" : "Publish"}
                        </button>

                        <button
                            type="button"
                            class="danger-btn"
                            onclick="deleteTestimonial(${Number(id)})"
                        >
                            Delete
                        </button>

                    </div>
                </td>

            </tr>
        `;
});

html += `
</tbody>
</table>
</div>
`;

    container.innerHTML = html;

    console.log(
        "Testimonials rendered successfully."
    );
}



/* =========================================================
   OPEN TESTIMONIAL FORM
========================================================= */

function openTestimonialForm() {

    const container =
        document.getElementById(
            "testimonialFormContainer"
        );

    const form =
        document.getElementById(
            "testimonialForm"
        );

    if (!container || !form) {

        console.error(
            "Testimonial form not found."
        );

        return;
    }


    form.reset();


    document.getElementById(
        "testimonialId"
    ).value = "";


    document.getElementById(
        "testimonialRating"
    ).value = "5";


    document.getElementById(
        "testimonialStatus"
    ).value = "PUBLISHED";


    const title =
        document.getElementById(
            "testimonialFormTitle"
        );

    if (title) {

        title.textContent =
            "Add Testimonial";
    }


    const message =
        document.getElementById(
            "testimonialFormMessage"
        );

    if (message) {

        message.textContent =
            "";
    }


    container.classList.remove(
        "hidden"
    );

    container.scrollIntoView({
        behavior: "smooth",
        block: "start"
    });
}


/* =========================================================
   CLOSE TESTIMONIAL FORM
========================================================= */

function closeTestimonialForm() {

    const container =
        document.getElementById(
            "testimonialFormContainer"
        );

    const form =
        document.getElementById(
            "testimonialForm"
        );

    if (form) {

        form.reset();
    }

    if (container) {

        container.classList.add(
            "hidden"
        );
    }
}


/* =========================================================
   EDIT TESTIMONIAL
========================================================= */

function editTestimonial(testimonialId) {

    const testimonial =
        currentAdminTestimonials.find(
            item =>
                String(item.id) ===
                String(testimonialId)
        );

    if (!testimonial) {

        alert(
            "Testimonial not found."
        );

        return;
    }


    document.getElementById(
        "testimonialId"
    ).value =
        testimonial.id;


    document.getElementById(
        "testimonialCustomerName"
    ).value =
        testimonial.customerName ||
        testimonial.name ||
        "";


    document.getElementById(
        "testimonialCustomerImage"
    ).value =
        testimonial.customerImage ||
        testimonial.image ||
        "";


    document.getElementById(
        "testimonialRating"
    ).value =
        testimonial.rating || 5;


    document.getElementById(
        "testimonialMessage"
    ).value =
        testimonial.message ||
        testimonial.text ||
        testimonial.testimonial ||
        "";


    document.getElementById(
        "testimonialStatus"
    ).value =
        testimonial.status ||
        (
            testimonial.published
                ? "PUBLISHED"
                : "HIDDEN"
        );


    const title =
        document.getElementById(
            "testimonialFormTitle"
        );

    if (title) {

        title.textContent =
            "Edit Testimonial";
    }


    const container =
        document.getElementById(
            "testimonialFormContainer"
        );

    if (container) {

        container.classList.remove(
            "hidden"
        );

        container.scrollIntoView({
            behavior: "smooth",
            block: "start"
        });
    }
}


/* =========================================================
   SAVE TESTIMONIAL
========================================================= */

async function saveTestimonial(event) {

    event.preventDefault();


    const testimonialId =
        document.getElementById(
            "testimonialId"
        ).value.trim();


    const customerName =
        document.getElementById(
            "testimonialCustomerName"
        ).value.trim();


    const customerImage =
        document.getElementById(
            "testimonialCustomerImage"
        ).value.trim();


    const rating =
        Number(
            document.getElementById(
                "testimonialRating"
            ).value
        );


    const message =
        document.getElementById(
            "testimonialMessage"
        ).value.trim();


    const status =
        document.getElementById(
            "testimonialStatus"
        ).value;


    if (!customerName) {

        alert(
            "Please enter customer name."
        );

        return;
    }


    if (!message) {

        alert(
            "Please enter testimonial."
        );

        return;
    }


    if (
        rating < 1 ||
        rating > 5
    ) {

        alert(
            "Rating must be between 1 and 5."
        );

        return;
    }


    const token =
        getTestimonialToken();


    if (!token) {

        alert(
            "Admin session expired. Please login again."
        );

        return;
    }


    const testimonialData = {

        customerName:
            customerName,

        customerImage:
            customerImage,

        rating:
            rating,

        message:
            message,

        status:
            status,

        published:
            status === "PUBLISHED"
    };


    const isEdit =
        Boolean(testimonialId);


    try {

        const response =
            await fetch(

                isEdit

                    ? `${TESTIMONIAL_API_BASE_URL}/api/testimonials/${testimonialId}`

                    : `${TESTIMONIAL_API_BASE_URL}/api/testimonials`,

                {

                    method:
                        isEdit
                            ? "PUT"
                            : "POST",

                    headers:
                        getTestimonialHeaders(),

                    body:
                        JSON.stringify(
                            testimonialData
                        )
                }
            );


        const responseText =
            await response.text();


        console.log(
            "Save testimonial status:",
            response.status
        );

        console.log(
            "Save testimonial response:",
            responseText
        );


        if (!response.ok) {

            let message =
                responseText ||
                `HTTP ${response.status}`;

            try {

                const data =
                    JSON.parse(
                        responseText
                    );

                message =
                    data.message ||
                    data.error ||
                    message;

            } catch (error) {}

            throw new Error(
                message
            );
        }


        alert(
            isEdit
                ? "Testimonial updated successfully."
                : "Testimonial created successfully."
        );


        closeTestimonialForm();


        await loadTestimonials();


    } catch (error) {

        console.error(
            "SAVE TESTIMONIAL ERROR:",
            error
        );

        alert(
            "Unable to save testimonial.\n\n" +
            error.message
        );
    }
}


/* =========================================================
   PUBLISH / HIDE TESTIMONIAL
========================================================= */

async function updateTestimonialStatus(
    testimonialId,
    published
) {

    if (!testimonialId) {

        alert(
            "Testimonial ID is missing."
        );

        return;
    }

    const status =
        published
            ? "PUBLISHED"
            : "HIDDEN";

    const token =
        getTestimonialToken();

    if (!token) {

        alert(
            "Admin session expired. Please login again."
        );

        return;
    }

    try {

        const response =
            await fetch(
                `${TESTIMONIAL_API_BASE_URL}/api/testimonials/admin/${testimonialId}/status?status=${encodeURIComponent(status)}`,
{
    method: "PUT",
        headers: getTestimonialHeaders()
}
);

const responseText =
    await response.text();

console.log(
    "Update testimonial status:",
    response.status
);

console.log(
    "Update testimonial response:",
    responseText
);

if (!response.ok) {

    let message =
        responseText ||
        `HTTP ${response.status}`;

    try {

        const errorData =
            JSON.parse(responseText);

        message =
            errorData.message ||
            errorData.error ||
            message;

    } catch (error) {}

    throw new Error(message);
}

alert(
    published
        ? "Testimonial published successfully."
        : "Testimonial hidden successfully."
);

await loadTestimonials();

} catch (error) {

    console.error(
        "UPDATE TESTIMONIAL STATUS ERROR:",
        error
    );

    alert(
        "Unable to update testimonial.\n\n" +
        error.message
    );
}
}




/* =========================================================
   DELETE TESTIMONIAL
========================================================= */

async function deleteTestimonial(
    testimonialId
) {

    if (!testimonialId) {

        alert(
            "Testimonial ID is missing."
        );

        return;
    }


    const confirmed =
        confirm(
            "Are you sure you want to permanently delete this testimonial?\n\n" +
            "This action cannot be undone."
        );


    if (!confirmed) {

        return;
    }


    try {

        const response =
            await fetch(

                `${TESTIMONIAL_API_BASE_URL}/api/testimonials/admin/${testimonialId}`,

                {

                    method: "DELETE",

                    headers:
                        getTestimonialHeaders()
                }
            );


        const responseText =
            await response.text();


        if (!response.ok) {

            throw new Error(
                responseText ||
                `HTTP ${response.status}`
            );
        }


        alert(
            "Testimonial deleted successfully."
        );


        await loadTestimonials();


    } catch (error) {

        console.error(
            "DELETE TESTIMONIAL ERROR:",
            error
        );

        alert(
            "Unable to delete testimonial.\n\n" +
            error.message
        );
    }
}


/* =========================================================
   TESTIMONIAL FORM SUBMIT
========================================================= */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        const form =
            document.getElementById(
                "testimonialForm"
            );

        if (form) {

            form.addEventListener(
                "submit",
                saveTestimonial
            );
        }

    }
);


/* =========================================================
   LOAD WHEN TESTIMONIAL SECTION OPENS
========================================================= */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        const buttons =
            document.querySelectorAll(
                '[data-section="testimonials"]'
            );

        buttons.forEach(
            button => {

                button.addEventListener(
                    "click",
                    function () {

                        setTimeout(
                            () => {
                                loadTestimonials();
                            },
                            100
                        );
                    }
                );

            }
        );

    }
);


/* =========================================================
   MAKE FUNCTIONS AVAILABLE TO HTML
========================================================= */

window.loadTestimonials =
    loadTestimonials;

window.openTestimonialForm =
    openTestimonialForm;

window.closeTestimonialForm =
    closeTestimonialForm;

window.editTestimonial =
    editTestimonial;

window.saveTestimonial =
    saveTestimonial;

window.updateTestimonialStatus =
    updateTestimonialStatus;

window.deleteTestimonial =
    deleteTestimonial;


/* =========================================================
   UPDATE SHOW SECTION
========================================================= */

/*
 * IMPORTANT:
 * Apne existing showSection() ke sections array mein
 * "testimonials" add karein.
 *
 * Existing:
 *
 * const sections = [
 *     "overview",
 *     "products",
 *     "orders",
 *     "users",
 *     "blogs",
 *     "reviews"
 * ];
 *
 * Isko:
 */

const TESTIMONIAL_SECTION_NAME =
    "testimonials";

/* =========================================================
   MAKE FUNCTIONS AVAILABLE TO HTML
========================================================= */

window.loadReviews =
    loadReviews;

window.updateReviewStatus =
    updateReviewStatus;

window.deleteReview =
    deleteReview;

window.renderReviewStars =
    renderReviewStars;
/* =========================================================
   CHANGE USER ROLE
========================================================= */

async function changeUserRole(userId) {

    if (!isSuperAdmin()) {
        alert("Only Super Admin can change user roles.");
        return;
    }

    const user = allAdminUsers.find(
        u => String(u.id) === String(userId)
    );

    if (!user) {
        alert("User not found.");
        return;
    }

    const currentRole =
        String(user.role || "USER").toUpperCase();

    if (currentRole === "SUPER_ADMIN") {
        alert("Super Admin account cannot be changed here.");
        return;
    }

    const newRole = prompt(
        `Current role: ${currentRole}\n\n` +
        `Enter new role:\n` +
        `USER\n` +
        `ADMIN`,
        currentRole
    );

    if (newRole === null) {
        return;
    }

    const role = newRole.trim().toUpperCase();

    if (!["USER", "ADMIN"].includes(role)) {
        alert("Invalid role. Use USER or ADMIN.");
        return;
    }

    if (role === currentRole) {
        return;
    }

    if (
        !confirm(
            `Change ${user.name}'s role from ${currentRole} to ${role}?`
        )
    ) {
        return;
    }

    try {

        const response = await fetch(
            `${API_BASE_URL}/api/users/admin/${userId}/role`,
            {
                method: "PUT",
                headers: getAuthHeaders(),
                body: JSON.stringify({
                    role: role
                })
            }
        );

        const responseText =
            await response.text();

        if (!response.ok) {

            let message =
                "Failed to change user role.";

            try {
                const data =
                    JSON.parse(responseText);

                message =
                    data.message ||
                    data.error ||
                    message;

            } catch (_) {}

            throw new Error(message);
        }

        alert("User role changed successfully.");

        await loadUsers();

        if (
            selectedUserId &&
            String(selectedUserId) === String(userId)
        ) {
            closeUserDetails();
        }

    } catch (error) {

        console.error(
            "Change role error:",
            error
        );

        alert(
            "Failed to change role.\n\n" +
            error.message
        );
    }
}
/* =========================================================
   BLOCK / UNBLOCK USER
========================================================= */

async function toggleUserStatus(userId) {

    const user = allAdminUsers.find(
        u => String(u.id) === String(userId)
    );

    if (!user) {
        alert("User not found.");
        return;
    }

    const currentRole =
        String(user.role || "USER").toUpperCase();

    const adminRole = getUserRole();

    // ADMIN can manage USER only
    if (
        adminRole === "ADMIN" &&
        currentRole !== "USER"
    ) {
        alert("Admin can manage only normal users.");
        return;
    }

    // Nobody can block a SUPER_ADMIN
    if (currentRole === "SUPER_ADMIN") {
        alert("Super Admin account cannot be blocked.");
        return;
    }

    const isActive =
        user.active !== false;

    const action =
        isActive ? "block" : "unblock";

    if (
        !confirm(
            `Are you sure you want to ${action} ${user.name}?`
        )
    ) {
        return;
    }

    try {

        const endpoint =
            isActive
                ? `${API_BASE_URL}/api/users/admin/${userId}/block`
                : `${API_BASE_URL}/api/users/admin/${userId}/unblock`;

        const response = await fetch(
            endpoint,
            {
                method: "PUT",
                headers: getAuthHeaders()
            }
        );

        const responseText =
            await response.text();

        if (!response.ok) {

            let message =
                `Failed to ${action} user.`;

            try {
                const data =
                    JSON.parse(responseText);

                message =
                    data.message ||
                    data.error ||
                    message;

            } catch (_) {}

            throw new Error(message);
        }

        alert(
            isActive
                ? "User blocked successfully."
                : "User unblocked successfully."
        );

        await loadUsers();

        if (
            selectedUserId &&
            String(selectedUserId) === String(userId)
        ) {
            closeUserDetails();
        }

    } catch (error) {

        console.error(
            "User status error:",
            error
        );

        alert(
            "Failed to update user status.\n\n" +
            error.message
        );
    }
}
/* =========================================================
   FORCE LOGOUT USER
========================================================= */

async function forceLogoutUser(userId) {

    const user = allAdminUsers.find(
        u => String(u.id) === String(userId)
    );

    if (!user) {
        alert("User not found.");
        return;
    }

    const currentRole =
        String(user.role || "USER").toUpperCase();

    const adminRole = getUserRole();

    if (
        adminRole === "ADMIN" &&
        currentRole !== "USER"
    ) {
        alert("Admin can force logout only normal users.");
        return;
    }

    if (currentRole === "SUPER_ADMIN") {
        alert("Super Admin cannot be force logged out here.");
        return;
    }

    if (
        !confirm(
            `Force logout ${user.name}?\n\n` +
            `Their current login session/token will be invalidated.`
        )
    ) {
        return;
    }

    try {

        const response = await fetch(
            `${API_BASE_URL}/api/users/admin/${userId}/force-logout`,
            {
                method: "PUT",
                headers: getAuthHeaders()
            }
        );

        const responseText =
            await response.text();

        if (!response.ok) {

            let message =
                "Failed to force logout user.";

            try {
                const data =
                    JSON.parse(responseText);

                message =
                    data.message ||
                    data.error ||
                    message;

            } catch (_) {}

            throw new Error(message);
        }

        alert(
            "User has been logged out from existing sessions."
        );

    } catch (error) {

        console.error(
            "Force logout error:",
            error
        );

        alert(
            "Force logout failed.\n\n" +
            error.message
        );
    }
}
/* =========================================================
   DELETE USER
========================================================= */

async function deleteUser(userId) {

    if (!isSuperAdmin()) {
        alert("Only Super Admin can delete users.");
        return;
    }

    const user = allAdminUsers.find(
        u => String(u.id) === String(userId)
    );

    if (!user) {
        alert("User not found.");
        return;
    }

    const currentRole =
        String(user.role || "USER").toUpperCase();

    if (currentRole === "SUPER_ADMIN") {
        alert("Super Admin account cannot be deleted.");
        return;
    }

    const confirmed =
        confirm(
            `Delete ${user.name}?\n\n` +
            `Email: ${user.email}\n\n` +
            `This action cannot be undone.`
        );

    if (!confirmed) {
        return;
    }

    try {

        const response = await fetch(
            `${API_BASE_URL}/api/users/admin/${userId}`,
            {
                method: "DELETE",
                headers: getAuthHeaders()
            }
        );

        const responseText =
            await response.text();

        if (!response.ok) {

            let message =
                "Failed to delete user.";

            try {
                const data =
                    JSON.parse(responseText);

                message =
                    data.message ||
                    data.error ||
                    message;

            } catch (_) {
            }

            throw new Error(message);
        }

        alert(
            "User deleted successfully."
        );

        closeUserDetails();

        await loadUsers();

        await loadDashboardStats();

    } catch (error) {

        console.error(
            "Delete user error:",
            error
        );

        alert(
            "Delete failed.\n\n" +
            error.message
        );
    }

    /* =========================================================
   BULK USER SELECTION
========================================================= */

    function getSelectedUserIds() {

        const checkboxes =
            document.querySelectorAll(
                "#usersTableBody .user-select-checkbox:checked"
            );

        return Array.from(checkboxes)
            .map(checkbox => checkbox.value)
            .filter(Boolean);
    }


    function updateSelectedUsersCount() {

        const selectedIds =
            getSelectedUserIds();

        const countElement =
            document.getElementById(
                "selectedUsersCount"
            );

        if (countElement) {
            countElement.textContent =
                selectedIds.length;
        }

        const bulkActions =
            document.getElementById(
                "bulkUserActions"
            );

        if (bulkActions) {
            bulkActions.classList.toggle(
                "hidden",
                selectedIds.length === 0
            );
        }
    }

    document.addEventListener(
        "change",
        event => {

            if (
                event.target.classList.contains(
                    "user-select-checkbox"
                )
            ) {

                updateSelectedUsersCount();
            }

            if (
                event.target.id ===
                "selectAllUsers"
            ) {

                toggleSelectAllUsers(
                    event.target.checked
                );
            }
        }
    );

    /* =========================================================
   BULK BLOCK USERS
========================================================= */

    async function blockSelectedUsers() {

        if (!isAdmin()) {
            alert("Admin access required.");
            return;
        }

        const selectedIds =
            getSelectedUserIds();

        if (selectedIds.length === 0) {
            alert("Select at least one user.");
            return;
        }

        const selectedUsers =
            selectedIds
                .map(id =>
                    allAdminUsers.find(
                        user =>
                            String(user.id) ===
                            String(id)
                    )
                )
                .filter(Boolean);

        // ADMIN can only block USER accounts
        const manageableUsers =
            selectedUsers.filter(user => {

                const role =
                    String(user.role || "USER")
                        .toUpperCase();

                if (role === "SUPER_ADMIN") {
                    return false;
                }

                if (
                    getUserRole() === "ADMIN" &&
                    role !== "USER"
                ) {
                    return false;
                }

                return user.active !== false;
            });

        if (manageableUsers.length === 0) {
            alert(
                "There are no eligible active users selected."
            );
            return;
        }

        if (
            !confirm(
                `Block ${manageableUsers.length} selected user(s)?`
            )
        ) {
            return;
        }

        let successCount = 0;
        let failedCount = 0;

        for (const user of manageableUsers) {

            try {

                const response =
                    await fetch(
                        `${API_BASE_URL}/api/users/admin/${user.id}/block`,
                        {
                            method: "PUT",
                            headers: getAuthHeaders()
                        }
                    );

                if (response.ok) {
                    successCount++;
                } else {
                    failedCount++;
                }

            } catch (error) {

                console.error(
                    "Bulk block failed:",
                    user.id,
                    error
                );

                failedCount++;
            }
        }

        alert(
            `Bulk block completed.\n\n` +
            `Blocked: ${successCount}\n` +
            `Failed: ${failedCount}`
        );

        await loadUsers();

        updateSelectedUsersCount();
    }

    /* =========================================================
       BULK DELETE USERS
    ========================================================= */

    async function deleteSelectedUsers() {

        if (!isSuperAdmin()) {
            alert(
                "Only Super Admin can delete users."
            );
            return;
        }

        const selectedIds =
            getSelectedUserIds();

        if (selectedIds.length === 0) {
            alert("Select at least one user.");
            return;
        }

        const selectedUsers =
            selectedIds
                .map(id =>
                    allAdminUsers.find(
                        user =>
                            String(user.id) ===
                            String(id)
                    )
                )
                .filter(Boolean);

        // Never allow bulk deletion of SUPER_ADMIN
        const deletableUsers =
            selectedUsers.filter(user => {

                const role =
                    String(user.role || "USER")
                        .toUpperCase();

                return role !== "SUPER_ADMIN";
            });

        if (deletableUsers.length === 0) {
            alert(
                "No eligible users selected for deletion."
            );
            return;
        }

        const skipped =
            selectedUsers.length -
            deletableUsers.length;

        const confirmationText =
            `Delete ${deletableUsers.length} user(s)?\n\n` +
            `This action cannot be undone.` +
            (
                skipped > 0
                    ? `\n\n${skipped} Super Admin account(s) will be skipped.`
                    : ""
            );

        if (!confirm(confirmationText)) {
            return;
        }

        let successCount = 0;
        let failedCount = 0;

        for (const user of deletableUsers) {

            try {

                const response =
                    await fetch(
                        `${API_BASE_URL}/api/users/admin/${user.id}`,
                        {
                            method: "DELETE",
                            headers: getAuthHeaders()
                        }
                    );

                if (response.ok) {
                    successCount++;
                } else {
                    failedCount++;
                }

            } catch (error) {

                console.error(
                    "Bulk delete failed:",
                    user.id,
                    error
                );

                failedCount++;
            }
        }

        alert(
            `Bulk delete completed.\n\n` +
            `Deleted: ${successCount}\n` +
            `Failed: ${failedCount}`
        );

        await loadUsers();

        await loadDashboardStats();

        updateSelectedUsersCount();
    }

    document.addEventListener(
        "DOMContentLoaded",
        () => {

            const blockButton =
                document.getElementById(
                    "blockSelectedUsers"
                );

            const deleteButton =
                document.getElementById(
                    "deleteSelectedUsers"
                );

            if (blockButton) {

                blockButton.addEventListener(
                    "click",
                    blockSelectedUsers
                );
            }

            if (deleteButton) {

                deleteButton.addEventListener(
                    "click",
                    deleteSelectedUsers
                );
            }
        }
    );

    /* =========================================================
       REFRESH
    ========================================================= */
    /* =========================================================
   REFRESH ADMIN DASHBOARD
========================================================= */

    async function refreshAdminDashboard() {

        console.log(
            "Refreshing admin dashboard..."
        );

        try {

            await Promise.allSettled([

                loadDashboardStats(),

                loadProducts(),

                loadOrders(),

                loadUsers(),

                loadBlogs(),

                loadTestimonials(),

                loadReviews()

            ]);

            console.log(
                "Admin dashboard refreshed successfully."
            );

        } catch (error) {

            console.error(
                "Dashboard refresh error:",
                error
            );
        }
    }

    /* =========================================================
       GLOBAL FUNCTIONS
    ========================================================= */
    window.showSection =
        showSection;

    window.showAdminLogin =
        showAdminLogin;

    window.logoutAdmin =
        logoutAdmin;

    window.openProductForm =
        openProductForm;



    window.editProduct =
        editProduct;
    window.closeProductForm =
        closeProductForm;
    window.deleteProduct =
        deleteProduct;

    window.restoreProduct =
        restoreProduct;

    window.loadProducts =
        loadProducts;

    window.loadOrders =
        loadOrders;

    window.loadUsers =
        loadUsers;

    window.loadBlogs =
        loadBlogs;

    window.refreshAdminDashboard =
        refreshAdminDashboard;

    window.updateAdminOrder =
        updateAdminOrder;

    window.isAdmin =
        isAdmin;

    window.isSuperAdmin =
        isSuperAdmin;
    window.editUser = editUser;
    window.closeEditUser = closeEditUser;
    window.handleEditUserSubmit = handleEditUserSubmit;
    window.changeUserRole = changeUserRole;
    window.toggleUserStatus = toggleUserStatus;
    window.forceLogoutUser = forceLogoutUser;
    window.deleteUser = deleteUser;
}
/* =========================================================
   USER DETAILS BUTTON EVENTS
========================================================= */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        const editButton =
            document.getElementById(
                "editUserBtn"
            );

        const roleButton =
            document.getElementById(
                "changeUserRoleBtn"
            );

        const statusButton =
            document.getElementById(
                "toggleUserStatusBtn"
            );

        const deleteButton =
            document.getElementById(
                "deleteUserBtn"
            );


        /* -------------------------------------------------
           EDIT USER
        ------------------------------------------------- */

        if (editButton) {

            editButton.addEventListener(
                "click",
                function () {

                    console.log(
                        "Edit User clicked:",
                        selectedUserId
                    );

                    if (
                        selectedUserId === null ||
                        selectedUserId === undefined
                    ) {

                        alert(
                            "No user selected."
                        );

                        return;
                    }

                    editUser(
                        selectedUserId
                    );
                }
            );
        }


        /* -------------------------------------------------
           CHANGE ROLE
        ------------------------------------------------- */

        if (roleButton) {

            roleButton.addEventListener(
                "click",
                function () {

                    console.log(
                        "Change Role clicked:",
                        selectedUserId
                    );

                    if (
                        selectedUserId === null ||
                        selectedUserId === undefined
                    ) {

                        alert(
                            "No user selected."
                        );

                        return;
                    }

                    changeUserRole(
                        selectedUserId
                    );
                }
            );
        }


        /* -------------------------------------------------
           BLOCK / UNBLOCK
        ------------------------------------------------- */

        if (statusButton) {

            statusButton.addEventListener(
                "click",
                function () {

                    console.log(
                        "Toggle Status clicked:",
                        selectedUserId
                    );

                    if (
                        selectedUserId === null ||
                        selectedUserId === undefined
                    ) {

                        alert(
                            "No user selected."
                        );

                        return;
                    }

                    toggleUserStatus(
                        selectedUserId
                    );
                }
            );
        }


        /* -------------------------------------------------
           DELETE USER
        ------------------------------------------------- */

        if (deleteButton) {

            deleteButton.addEventListener(
                "click",
                function () {

                    console.log(
                        "Delete User clicked:",
                        selectedUserId
                    );

                    if (
                        selectedUserId === null ||
                        selectedUserId === undefined
                    ) {

                        alert(
                            "No user selected."
                        );

                        return;
                    }

                    deleteUser(
                        selectedUserId
                    );
                }
            );
        }

    }
);
/* =========================================================
   USER DETAILS MODAL BUTTON FIX
========================================================= */

document.addEventListener("DOMContentLoaded", function () {

    const closeBtn =
        document.getElementById("closeUserDetailsBtn");

    const editBtn =
        document.getElementById("editUserBtn");

    const roleBtn =
        document.getElementById("changeUserRoleBtn");

    const statusBtn =
        document.getElementById("toggleUserStatusBtn");

    const deleteBtn =
        document.getElementById("deleteUserBtn");


    /* =========================
       CLOSE USER DETAILS
    ========================= */

    if (closeBtn) {

        closeBtn.addEventListener("click", function (event) {

            event.preventDefault();
            event.stopPropagation();

            const modal =
                document.getElementById("userDetailsModal");

            if (modal) {
                modal.classList.add("hidden");
                modal.style.display = "none";
            }

            selectedUserId = null;

            console.log("User details closed");
        });

    }


    /* =========================
       EDIT USER
    ========================= */

    if (editBtn) {

        editBtn.addEventListener("click", function (event) {

            event.preventDefault();
            event.stopPropagation();

            if (
                selectedUserId === null ||
                selectedUserId === undefined
            ) {
                alert("No user selected.");
                return;
            }

            editUser(selectedUserId);
        });

    }


    /* =========================
       CHANGE ROLE
    ========================= */

    if (roleBtn) {

        roleBtn.addEventListener("click", function (event) {

            event.preventDefault();
            event.stopPropagation();

            if (
                selectedUserId === null ||
                selectedUserId === undefined
            ) {
                alert("No user selected.");
                return;
            }

            changeUserRole(selectedUserId);
        });

    }


    /* =========================
       BLOCK / UNBLOCK
    ========================= */

    if (statusBtn) {

        statusBtn.addEventListener("click", function (event) {

            event.preventDefault();
            event.stopPropagation();

            if (
                selectedUserId === null ||
                selectedUserId === undefined
            ) {
                alert("No user selected.");
                return;
            }

            toggleUserStatus(selectedUserId);
        });

    }


    /* =========================
       DELETE USER
    ========================= */

    if (deleteBtn) {

        deleteBtn.addEventListener("click", function (event) {

            event.preventDefault();
            event.stopPropagation();

            if (
                selectedUserId === null ||
                selectedUserId === undefined
            ) {
                alert("No user selected.");
                return;
            }

            deleteUser(selectedUserId);
        });

    }

});