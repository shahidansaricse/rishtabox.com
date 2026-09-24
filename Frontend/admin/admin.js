/* =========================================================
   RISHTABOX ADMIN DASHBOARD
   ========================================================= */

"use strict";


/* =========================================================
   CONFIGURATION
========================================================= */

const API_BASE_URL = "http://localhost:8080";


/* =========================================================
   GLOBAL STATE
========================================================= */

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
   IMPORTANT:
   ADMIN SESSION IS SEPARATE FROM CUSTOMER SESSION
========================================================= */

function getStoredUser() {

    const raw =
        localStorage.getItem(
            "rishtaBoxAdminUser"
        );


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

        "Content-Type":
            "application/json"

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

    const role = getUserRole();

    return (
        role === "ADMIN" ||
        role === "SUPER_ADMIN"
    );
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

        .replace(
            /&/g,
            "&amp;"
        )

        .replace(
            /</g,
            "&lt;"
        )

        .replace(
            />/g,
            "&gt;"
        )

        .replace(
            /"/g,
            "&quot;"
        )

        .replace(
            /'/g,
            "&#039;"
        );
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
========================================================= */

function showAdminAuthPage() {

    const authPage =
        $("adminAuthPage");


    const dashboard =
        $("adminDashboard");


    if (authPage) {

        authPage.classList.remove(
            "hidden"
        );

        authPage.style.display =
            "block";
    }


    if (dashboard) {

        dashboard.classList.add(
            "hidden"
        );

        dashboard.style.display =
            "none";
    }


    const loginSection =
        $("adminLoginSection");


    const signupSection =
        $("adminSignupSection");


    if (loginSection) {

        loginSection.classList.remove(
            "hidden"
        );

        loginSection.style.display =
            "block";
    }


    if (signupSection) {

        signupSection.classList.add(
            "hidden"
        );

        signupSection.style.display =
            "none";
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

        authPage.classList.add(
            "hidden"
        );

        authPage.style.display =
            "none";
    }


    if (dashboard) {

        dashboard.classList.remove(
            "hidden"
        );

        dashboard.style.display =
            "block";
    }


    updateAdminHeader();


    showSection(
        "overview"
    );
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

        dashboard.classList.add(
            "hidden"
        );

        dashboard.style.display =
            "none";
    }


    if (authPage) {

        authPage.classList.remove(
            "hidden"
        );

        authPage.style.display =
            "block";
    }


    const loginSection =
        $("adminLoginSection");


    const signupSection =
        $("adminSignupSection");


    if (loginSection) {

        loginSection.classList.remove(
            "hidden"
        );

        loginSection.style.display =
            "block";
    }


    if (signupSection) {

        signupSection.classList.add(
            "hidden"
        );

        signupSection.style.display =
            "none";
    }


    if (message) {

        message.classList.remove(
            "hidden"
        );

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
        !dashboard.classList.contains(
            "hidden"
        );


    if (loginButton) {

        loginButton.classList.toggle(
            "hidden",
            isDashboardVisible
        );
    }


    if (signupButton) {

        signupButton.classList.toggle(
            "hidden",
            isDashboardVisible
        );
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
        document.querySelectorAll(
            ".nav-btn"
        );


    buttons.forEach(
        button => {

            button.addEventListener(
                "click",
                () => {

                    const section =
                        button.dataset.section;


                    if (section) {

                        showSection(
                            section
                        );
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

        "blogs"

    ];


    sections.forEach(
        section => {

            const content =
                $(section);


            if (content) {

                content.classList.toggle(
                    "hidden",
                    section !== sectionName
                );
            }
        }
    );


    document
        .querySelectorAll(
            ".nav-btn"
        )
        .forEach(
            button => {

                button.classList.toggle(
                    "active",
                    button.dataset.section === sectionName
                );

            }
        );


    const title =
        $("sectionTitle");


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
                "Blogs"

        };


        title.textContent =
            titles[sectionName] ||
            "Admin Dashboard";
    }


    if (
        sectionName ===
        "products"
    ) {

        loadProducts();
    }


    if (
        sectionName ===
        "orders"
    ) {

        loadOrders();
    }


    if (
        sectionName ===
        "users"
    ) {

        loadUsers();
    }


    if (
        sectionName ===
        "blogs"
    ) {

        loadBlogs();
    }
}


/* =========================================================
   FORM SETUP
========================================================= */

function setupForms() {

    /* -----------------------------------------------------
       ADMIN LOGIN
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
       ADMIN SIGNUP
    ----------------------------------------------------- */

    const signupForm =
        $("adminSignupForm");


    if (signupForm) {

        signupForm.addEventListener(
            "submit",
            handleAdminSignup
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

                            email:
                                email,

                            password:
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
   ADMIN SIGNUP
========================================================= */

async function handleAdminSignup(event) {

    event.preventDefault();


    const name =
        $("adminSignupName")
            ?.value
            .trim();


    const email =
        $("adminSignupEmail")
            ?.value
            .trim();


    // HTML ID = adminSignupMobile
    const phone =
        $("adminSignupMobile")
            ?.value
            .trim();


    const password =
        $("adminSignupPassword")
            ?.value;


    const confirmPassword =
        $("adminSignupConfirmPassword")
            ?.value;


    if (
        !name ||
        !email ||
        !phone ||
        !password ||
        !confirmPassword
    ) {

        alert(
            "Please fill all signup fields."
        );

        return;
    }


    if (
        password !==
        confirmPassword
    ) {

        alert(
            "Passwords do not match."
        );

        return;
    }


    try {

        const response =
            await fetch(
                `${API_BASE_URL}/api/users/signup`,
                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json"

                    },

                    body:
                        JSON.stringify({

                            name,

                            email,

                            phone,

                            password,

                            role:
                                "ADMIN"

                        })
                }
            );


        const text =
            await response.text();


        if (!response.ok) {

            throw new Error(
                text ||
                `Signup failed: ${response.status}`
            );
        }


        alert(
            "Account created successfully. Please login."
        );


        showAdminLogin();


    } catch (error) {

        console.error(
            "Admin signup error:",
            error
        );


        alert(
            "Signup failed.\n\n" +
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


    const signup =
        $("adminSignupSection");


    if (authPage) {

        authPage.classList.remove(
            "hidden"
        );

        authPage.style.display =
            "block";
    }


    if (dashboard) {

        dashboard.classList.add(
            "hidden"
        );

        dashboard.style.display =
            "none";
    }


    if (login) {

        login.classList.remove(
            "hidden"
        );

        login.style.display =
            "block";
    }


    if (signup) {

        signup.classList.add(
            "hidden"
        );

        signup.style.display =
            "none";
    }


    updateAdminHeader();
}


/* =========================================================
   SHOW ADMIN SIGNUP
========================================================= */

function showAdminSignup() {

    const authPage =
        $("adminAuthPage");


    const dashboard =
        $("adminDashboard");


    const login =
        $("adminLoginSection");


    const signup =
        $("adminSignupSection");


    if (authPage) {

        authPage.classList.remove(
            "hidden"
        );

        authPage.style.display =
            "block";
    }


    if (dashboard) {

        dashboard.classList.add(
            "hidden"
        );

        dashboard.style.display =
            "none";
    }


    if (login) {

        login.classList.add(
            "hidden"
        );

        login.style.display =
            "none";
    }


    if (signup) {

        signup.classList.remove(
            "hidden"
        );

        signup.style.display =
            "block";
    }


    updateAdminHeader();
}


/* =========================================================
   LOGOUT ADMIN
   IMPORTANT:
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

                    headers:
                        getAuthHeaders()

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

                    headers:
                        getAuthHeaders()

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
                        status ===
                            "PAID" ||

                        status ===
                            "SUCCESS" ||

                        status ===
                            "COMPLETED"
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

                    headers:
                        getAuthHeaders()

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

                    headers:
                        getAuthHeaders()

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
                    JSON.parse(
                        responseText
                    );


                message =
                    errorData.message ||
                    errorData.error ||
                    message;


            } catch (error) {

                // Response was not JSON.
            }


            throw new Error(
                `HTTP ${response.status}: ${message}`
            );
        }


        let products = [];


        try {

            products =
                JSON.parse(
                    responseText
                );

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


        renderProducts(
            products
        );


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
    ${escapeHtml(
    error.message
)}
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


    /* =============================================
       ACTIVE
    ============================================= */

    const active =
    product.active === true ||
    product.active === 1 ||
    product.active === "true" ||
    product.active === "1";


    /* =============================================
       STOCK
    ============================================= */

    const stock =
    Number(
    product.stock || 0
    );


    /* =============================================
       IMAGE
    ============================================= */

    const image =
    product.image || "";


    const imageHtml =
    image

    ? `

                        <img
                            src="${escapeHtml(
    image
    )}"
                            alt="${escapeHtml(
    product.name || ""
    )}"
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


    /* =============================================
       CATEGORY
    ============================================= */

    const categoryName =
    product.category?.name ||
    "-";


    /* =============================================
       FESTIVAL
    ============================================= */

    const festivalName =
    product.festival?.name ||
    "-";


    /* =============================================
       RELATIONSHIP
    ============================================= */

    const relationshipName =
    product.relationship?.name ||
    "-";


    /* =============================================
       STATUS
    ============================================= */

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

} else if (
    stock <= 0
    ) {

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


    /* =============================================
       STOCK HTML
    ============================================= */

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
                            ${escapeHtml(
    stock
    )}
                        </span>

                    `;


    /* =============================================
       ACTIONS
    ============================================= */

    const actionHtml =
    active

    ? `

                        <button
                            type="button"
                            onclick="editProduct(
                                ${Number(product.id)}
                            )"
                        >
                            Edit
                        </button>


                        <button
                            type="button"
                            onclick="deleteProduct(
                                ${Number(product.id)}
                            )"
                        >
                            Delete
                        </button>

                    `

    : `

                        <button
                            type="button"
                            onclick="editProduct(
                                ${Number(product.id)}
                            )"
                        >
                            Edit
                        </button>


                        <button
                            type="button"
                            onclick="restoreProduct(
                                ${Number(product.id)}
                            )"
                        >
                            Restore
                        </button>

                    `;


    /* =============================================
       ROW
    ============================================= */

    html += `

                <tr>

                    <td>
                        ${escapeHtml(
    product.id
    )}
                    </td>


                    <td>
                        ${imageHtml}
                    </td>


                    <td>

                        <strong>
                            ${escapeHtml(
    product.name || ""
    )}
                        </strong>

                    </td>


                    <td>
                        ${escapeHtml(
    categoryName
    )}
                    </td>


                    <td>
                        ${escapeHtml(
    festivalName
    )}
                    </td>


                    <td>
                        ${escapeHtml(
    relationshipName
    )}
                    </td>


                    <td>
                        ₹${Number(
    product.price || 0
    ).toLocaleString(
    "en-IN"
    )}
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

        formContainer.classList.remove(
            "hidden"
        );

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

        formContainer.classList.add(
            "hidden"
        );

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

                    headers:
                        getAuthHeaders()

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


            } catch (error) {

                // Response was not JSON.
            }


            throw new Error(
                message
            );
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

            formContainer.classList.remove(
                "hidden"
            );

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
                product.category?.id ||
                "";
        }


        const productFestivalId =
            $("productFestivalId");


        if (productFestivalId) {

            productFestivalId.value =
                product.festival?.id ||
                "";
        }


        const productRelationshipId =
            $("productRelationshipId");


        if (productRelationshipId) {

            productRelationshipId.value =
                product.relationship?.id ||
                "";
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
            .trim() ||
        "";


    const description =
        $("productDescription")
            ?.value
            .trim() ||
        "";


    const price =
        Number(
            $("productPrice")
                ?.value ||
            0
        );


    const originalPrice =
        Number(
            $("productOriginalPrice")
                ?.value ||
            0
        );


    const image =
        $("productImage")
            ?.value
            .trim() ||
        "";


    const stock =
        Number(
            $("productStock")
                ?.value ||
            0
        );


    const categoryId =
        $("productCategoryId")
            ?.value
            .trim() ||
        "";


    const festivalId =
        $("productFestivalId")
            ?.value
            .trim() ||
        "";


    const relationshipId =
        $("productRelationshipId")
            ?.value
            .trim() ||
        "";


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

        name:

            name,

        description:

            description,

        price:

            price,

        originalPrice:

            originalPrice,

        image:

            image,

        stock:

            stock,

        categoryId:

            categoryId ||
            null,

        festivalId:

            festivalId ||
            null,

        relationshipId:

            relationshipId ||
            null
    };


    console.log(
        "Product data being sent:",
        productData
    );


    try {

        const isEditing =
            Boolean(
                editingProductId
            );


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

                    method:
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


            } catch (error) {

                // Response was not JSON.
            }


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

        const url =
            `${API_BASE_URL}/api/products/${Number(productId)}`;


        const response =
            await fetch(
                url,
                {

                    method:
                        "DELETE",

                    headers:
                        getAuthHeaders()

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


            } catch (error) {

                // Response was not JSON.
            }


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

    method:
        "PUT",

            headers:
    getAuthHeaders()

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
            JSON.parse(
                responseText
            );


        errorMessage =
            errorData.message ||
            errorData.error ||
            errorMessage;


    } catch (error) {

        // Response was not JSON.
    }


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

                    method:
                        "GET",

                    headers:
                        getAuthHeaders()

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


        renderOrders(
            orders
        );


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

                ${escapeHtml(
            error.message
        )}

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

                        <th>
                            Order ID
                        </th>

                        <th>
                            Customer
                        </th>

                        <th>
                            Total
                        </th>

                        <th>
                            Payment Mode
                        </th>

                        <th>
                            Payment Status
                        </th>

                        <th>
                            Order Status
                        </th>

                        <th>
                            Shipping Mode
                        </th>

                        <th>
                            Tracking ID
                        </th>

                        <th>
                            Order Date
                        </th>

                        <th>
                            Action
                        </th>

                    </tr>

                </thead>


                <tbody>

    `;


    orders.forEach(
        order => {

            const orderId =
                Number(
                    order.id
                );


            const customerName =
                order.user?.name ||
                order.user?.email ||
                "-";


            const totalAmount =
                Number(
                    order.totalAmount ||
                    0
                );


            const paymentMethod =
                String(
                    order.paymentMethod ||
                    "-"
                ).toUpperCase();


            const paymentStatus =
                String(
                    order.paymentStatus ||
                    "-"
                ).toUpperCase();


            const orderStatus =
                String(
                    order.orderStatus ||
                    "PAYMENT_PENDING"
                ).toUpperCase();


            const shippingMode =
                String(
                    order.shippingMode ||
                    ""
                ).toUpperCase();


            const trackingId =
                order.trackingId ||
                "";


            html += `

                <tr>

                    <!-- ORDER ID -->

                    <td>

                        <strong>
                            #RB${escapeHtml(
                orderId
            )}
                        </strong>

                    </td>


                    <!-- CUSTOMER -->

                    <td>

                        ${escapeHtml(
                customerName
            )}

                    </td>


                    <!-- TOTAL -->

                    <td>

                        <strong>

                            ₹${totalAmount.toLocaleString(
                "en-IN"
            )}

                        </strong>

                    </td>


                    <!-- PAYMENT MODE -->

                    <td>

                        <span
                            class="
                                order-readonly-value
                            "
                        >

                            ${escapeHtml(
                paymentMethod
            )}

                        </span>

                    </td>


                    <!-- PAYMENT STATUS -->

                    <td>

                        <span
                            class="
                                order-status-badge
                            "
                        >

                            ${escapeHtml(
                paymentStatus
            )}

                        </span>

                    </td>


                    <!-- ORDER STATUS -->

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
                        status ===
                        orderStatus
                            ? "selected"
                            : ""
                    }
                                        >

                                            ${formatOrderStatus(
                        status
                    )}

                                        </option>

                                    `
                )
                .join("")}

                        </select>

                    </td>


                    <!-- SHIPPING MODE -->

                    <td>

                        <select
                            id="shipping-mode-${orderId}"
                            class="order-edit-select"
                        >

                            <option
                                value=""
                            >
                                Select
                            </option>


                            ${SHIPPING_MODE_OPTIONS
                .map(
                    mode => `

                                        <option
                                            value="${mode}"
                                            ${
                        mode ===
                        shippingMode
                            ? "selected"
                            : ""
                    }
                                        >

                                            ${formatShippingMode(
                        mode
                    )}

                                        </option>

                                    `
                )
                .join("")}

                        </select>

                    </td>


                    <!-- TRACKING ID -->

                    <td>

                        <input
                            type="text"
                            id="tracking-id-${orderId}"
                            class="tracking-input"
                            value="${escapeHtml(
                trackingId
            )}"
                            placeholder="Tracking ID"
                        >

                    </td>


                    <!-- DATE -->

                    <td>

                        ${formatDate(
                order.createdAt
            )}

                    </td>


                    <!-- ACTION -->

                    <td>

                        <button
                            type="button"
                            class="
                                primary-btn
                                order-save-btn
                            "
                            onclick="
                                updateAdminOrder(
                                    ${orderId}
                                )
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

        orderStatus:

        orderStatus,

        shippingMode:

        shippingMode,

        trackingId:

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

                `${API_BASE_URL}/api/orders/admin/${Number(
                    orderId
                )}`,

                {

                    method:
                        "PUT",

                    headers: {

                        ...getAuthHeaders(),

                        "Content-Type":
                            "application/json"

                    },

                    body:
                        JSON.stringify(
                            updateData
                        )

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
                    JSON.parse(
                        responseText
                    );


                message =
                    errorData.message ||
                    errorData.error ||
                    message;


            } catch (error) {

                // Response was not JSON.
            }


            throw new Error(
                message
            );
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

    const usersTableBody = $("usersTableBody");

    if (!usersTableBody) {
        console.error("usersTableBody element not found.");
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
                    Access denied. Admin or Super Admin required.
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

        console.log("=================================");
        console.log("Loading admin users");
        console.log("Endpoint:", endpoint);
        console.log("Role:", getUserRole());
        console.log("Token available:", Boolean(getToken()));
        console.log("=================================");

        const response = await fetch(
            endpoint,
            {
                method: "GET",
                headers: getAuthHeaders()
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
                    JSON.parse(responseText);

                errorMessage =
                    errorData.message ||
                    errorData.error ||
                    errorData.detail ||
                    errorMessage;

            } catch (parseError) {

                // Response was not JSON

            }

            throw new Error(
                `Users API failed: ${errorMessage}`
            );
        }

        let data = [];

        if (responseText.trim()) {

            try {

                data = JSON.parse(responseText);

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

            users = data.users;

        } else if (
            data &&
            Array.isArray(data.data)
        ) {

            users = data.data;

        } else if (
            data &&
            Array.isArray(data.content)
        ) {

            users = data.content;

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

        console.table(users);

        renderUsers(users);

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


    /*
     * EMPTY STATE
     */

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


    /*
     * UPDATE TOP USER STATISTICS
     */

    updateUserManagementStats(users);


    /*
     * CURRENT LOGGED-IN ROLE
     */

    const currentRole =
        getUserRole();


    /*
     * BUILD TABLE ROWS
     */

    const rows =
        users
            .map(user => {

                /*
                 * USER ID
                 */

                const id =
                    user.id ??
                    user.userId ??
                    "-";


                /*
                 * NAME
                 */

                const name =
                    user.name ??
                    user.fullName ??
                    "-";


                /*
                 * EMAIL
                 */

                const email =
                    user.email ??
                    "-";


                /*
                 * PHONE
                 */

                const phone =
                    user.phone ??
                    user.mobile ??
                    user.mobileNumber ??
                    "-";


                /*
                 * ROLE
                 */

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


                /*
                 * ORDERS
                 */

                const orders =
                    user.ordersCount ??
                    user.orderCount ??
                    user.totalOrders ??
                    (
                        Array.isArray(
                            user.orders
                        )
                            ? user.orders.length
                            : 0
                    );


                /*
                 * TOTAL SPENT
                 */

                const totalSpent =
                    Number(
                        user.totalSpent ??
                        user.totalAmountSpent ??
                        user.spent ??
                        0
                    );


                /*
                 * STATUS
                 */

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

                    if (user.status !== undefined) {

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


                /*
                 * JOINED DATE
                 */

                const joined =
                    user.createdAt ??
                    user.joinedAt ??
                    user.createdDate ??
                    user.registrationDate ??
                    user.date;


                /*
                 * ROLE CLASS
                 */

                let roleClass =
                    "user";


                if (
                    role === "SUPER_ADMIN"
                ) {

                    roleClass =
                        "super-admin";

                } else if (
                    role === "ADMIN"
                ) {

                    roleClass =
                        "admin";
                }


                /*
                 * ACTION BUTTON
                 *
                 * SUPER_ADMIN can view/manage.
                 * ADMIN gets view button disabled
                 * unless you later add permissions.
                 */

                let actions = `

                    <button
                        type="button"
                        class="secondary-btn"
                        disabled
                    >
                        View
                    </button>

                `;


                if (
                    currentRole ===
                    "SUPER_ADMIN"
                ) {

                    actions = `

                        <button
                            type="button"
                            class="secondary-btn"
                            onclick="viewUser(${Number(id)})"
                        >
                            View
                        </button>

                    `;
                }


                /*
                 * ROLE DISPLAY
                 */

                const roleLabel =
                    role === "SUPER_ADMIN"
                        ? "SUPER ADMIN"
                        : role;


                /*
                 * RETURN ROW
                 */

                return `

                    <tr>

                        <!-- SELECT -->

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
                                ${escapeHtml(
                    roleLabel
                )}
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

                        <td>

                            ${actions}

                        </td>

                    </tr>

                `;

            })
            .join("");


    /*
     * INSERT ROWS INTO EXISTING TABLE
     */

    usersTableBody.innerHTML =
        rows;


    /*
     * RESET SELECT-ALL CHECKBOX
     */

    const selectAll =
        $("selectAllUsers");

    if (selectAll) {

        selectAll.checked =
            false;

    }


    /*
     * UPDATE SELECTED USERS COUNT
     */

    updateSelectedUsersCount();
}
/* =========================================================
   USER MANAGEMENT STATISTICS
========================================================= */
function updateUserManagementStats(users) {

    if (!Array.isArray(users)) {
        users = [];
    }


    /*
     * TOTAL USERS
     */

    const totalUsers =
        users.length;


    /*
     * ACTIVE USERS
     */

    const activeUsers =
        users.filter(user => {

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
                    user.status !== undefined &&
                    user.status !== null
                ) {

                    return String(
                            user.status
                        ).toUpperCase() ===
                        "ACTIVE";
                }

                /*
                 * Backend currently does not
                 * provide status, so treat user
                 * as active by default.
                 */

                return true;
            }


            return (
                active === true ||
                active === 1 ||
                active === "1" ||
                String(active)
                    .toLowerCase() ===
                "true"
            );

        }).length;


    /*
     * ADMINS
     */

    const admins =
        users.filter(user => {

            let role =
                user.role ??
                user.authority ??
                "";

            if (
                typeof role === "object"
            ) {

                role =
                    role.name ||
                    role.authority ||
                    "";
            }


            role =
                String(role)
                    .replace(
                        "ROLE_",
                        ""
                    )
                    .toUpperCase();


            return role === "ADMIN";

        }).length;


    /*
     * SUPER ADMINS
     */

    const superAdmins =
        users.filter(user => {

            let role =
                user.role ??
                user.authority ??
                "";

            if (
                typeof role === "object"
            ) {

                role =
                    role.name ||
                    role.authority ||
                    "";
            }


            role =
                String(role)
                    .replace(
                        "ROLE_",
                        ""
                    )
                    .toUpperCase();


            return role ===
                "SUPER_ADMIN";

        }).length;


    /*
     * UPDATE HTML
     */

    const totalElement =
        $("totalUsersCount");

    if (totalElement) {

        totalElement.textContent =
            totalUsers;
    }


    const activeElement =
        $("activeUsersCount");

    if (activeElement) {

        activeElement.textContent =
            activeUsers;
    }


    const adminElement =
        $("adminUsersCount");

    if (adminElement) {

        adminElement.textContent =
            admins;
    }


    const superAdminElement =
        $("superAdminUsersCount");

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


    /*
     * SELECTED COUNT TEXT
     */

    const selectedUsersCount =
        $("selectedUsersCount");

    if (selectedUsersCount) {

        selectedUsersCount.textContent =
            selectedCount;
    }


    /*
     * BULK ACTION BAR
     */

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


    /*
     * SELECT ALL CHECKBOX
     */

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

async function loadBlogs() {

    if (!isAdmin()) {
        return;
    }


    const container =
        $("blogsContent");


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
            Loading blogs...
        </div>

    `;


    try {

        const response =
            await fetch(
                `${API_BASE_URL}/api/blogs`,
                {

                    method:
                        "GET"

                }
            );


        const text =
            await response.text();


        if (!response.ok) {

            throw new Error(
                text ||
                `Blogs request failed: ${response.status}`
            );
        }


        let blogs;


        try {

            blogs =
                JSON.parse(text);

        } catch (error) {

            throw new Error(
                "Server returned invalid blogs data."
            );
        }


        renderBlogs(
            blogs
        );


    } catch (error) {

        console.error(
            "Load blogs error:",
            error
        );


        container.innerHTML = `

            <div
                style="
                    padding:20px;
                    background:#fff8e1;
                    border-radius:10px;
                "
            >

                <h3>
                    Blogs unavailable
                </h3>

                <p>
                    ${escapeHtml(
            error.message
        )}
                </p>

            </div>

        `;
    }
}


/* =========================================================
   RENDER BLOGS
========================================================= */

function renderBlogs(blogs) {

    const container =
        $("blogsContent");


    if (!container) {
        return;
    }


    if (
        !Array.isArray(blogs) ||
        !blogs.length
    ) {

        container.innerHTML = `

            <div
                style="
                    padding:40px;
                    text-align:center;
                "
            >
                No blogs found.
            </div>

        `;

        return;
    }


    let html = `

        <div
            style="
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

                        <th>
                            ID
                        </th>

                        <th>
                            Title
                        </th>

                        <th>
                            Description
                        </th>

                        <th>
                            Date
                        </th>

                    </tr>

                </thead>


                <tbody>

    `;


    blogs.forEach(
        blog => {

            html += `

                <tr>

                    <td>
                        ${escapeHtml(
                blog.id
            )}
                    </td>


                    <td>
                        ${escapeHtml(
                blog.title
            )}
                    </td>


                    <td>
                        ${escapeHtml(
                blog.description ||
                blog.content ||
                "-"
            )}
                    </td>


                    <td>
                        ${formatDate(
                blog.createdAt
            )}
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
   DATE
========================================================= */

function formatDate(value) {

    if (!value) {
        return "-";
    }


    try {

        return new Date(
            value
        )
            .toLocaleString(
                "en-IN"
            );


    } catch (error) {

        return String(
            value
        );
    }
}


/* =========================================================
   REFRESH
========================================================= */

async function refreshAdminDashboard() {

    if (!isAdmin()) {

        showAccessDenied();

        return;
    }


    await loadDashboardStats();

    await loadProducts();

    await loadOrders();

    await loadUsers();

    await loadBlogs();
}


/* =========================================================
   GLOBAL FUNCTIONS
========================================================= */

window.showSection =
    showSection;


window.showAdminLogin =
    showAdminLogin;


window.showAdminSignup =
    showAdminSignup;


window.logoutAdmin =
    logoutAdmin;


window.openProductForm =
    openProductForm;


window.closeProductForm =
    closeProductForm;


window.editProduct =
    editProduct;


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

