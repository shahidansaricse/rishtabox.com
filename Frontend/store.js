function getImagePath(image) {
    if (!image) return "";

    // Online image hai to same URL use karo
    if (image.startsWith("http://") || image.startsWith("https://")) {
        return image;
    }

    // Local image ke liye images folder
    return "images/" + image;
}
/* =========================================================
   DATA
========================================================= */
const API_BASE_URL = "http://localhost:8080";

let categories = [];
let products = [];
let festivalProducts = [];
let relationshipProducts = [];
let festivals=[];
let backendProducts = [];

let currentUser = {
    name: "",
    email: "",
    phone: "",
    address: ""
};
let recentlyViewed = [];
let filteredProducts = [];
let cart = [];
let orders = [];
let currentOrderSteps = 1;
/* =========================================================
   HERO SLIDER
========================================================= */
let currentSlide = 0;

const slides = document.querySelectorAll(".hero-slide");
const dots = document.querySelectorAll(".slider-dot");

function showSlide(index) {

    if (slides.length === 0) return;

    if (index >= slides.length) {
        currentSlide = 0;
    } else if (index < 0) {
        currentSlide = slides.length - 1;
    } else {
        currentSlide = index;
    }

    slides.forEach(slide => {
        slide.classList.remove("active");
    });

    dots.forEach(dot => {
        dot.classList.remove("active");
    });

    slides[currentSlide].classList.add("active");

    if (dots[currentSlide]) {
        dots[currentSlide].classList.add("active");
    }
}

function changeSlide(direction) {
    showSlide(currentSlide + direction);
}

function goToSlide(index) {
    showSlide(index);
}

setInterval(() => {
    changeSlide(1);
}, 6000);
function validateName(name){
    const nameRegex = /^[a-zA-Z\s]{2,50}$/;
    return nameRegex.test(name.trim())
}

function validateEmail(email){
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email.trim());
}

function validatePhone(phone){
    const phoneRegex = /^[6-9]\d{9}$/;
    return phoneRegex.test(phone.trim());
}
/* =========================================================
    LOCAL STORAGE
 ========================================================= */
function saveUserData() {
    try {
        localStorage.setItem("userData",JSON.stringify(currentUser));
    } catch (error) {
        console.error("Could not save user data:", error);
    }
}
function loadUserData() {
    try {
        const savedUser = localStorage.getItem("userData");

        if (savedUser) {
            currentUser = JSON.parse(savedUser);
        }
        if (!currentUser || typeof currentUser !== "object") {
            currentUser = {
                name: "",
                email: "",
                phone: "",
                address: ""
            };
        }
    } catch (error) {
        console.error("Could not load user data:", error);
    }
}
function saveCartData() {
    try {
        localStorage.setItem("cart",JSON.stringify(cart));
    } catch (error) {
        console.error("Storage not available:",error);
    }
}
function loadCartData() {
    try {
        const savedCart =localStorage.getItem("cart");
        if (savedCart) {
            cart =JSON.parse(savedCart);
        }
        if (!Array.isArray(cart)) {
            cart = [];
        }
    } catch (error) {
        console.error("Could not load cart:",error);
        cart = [];
    }
}
function saveOrdersData() {
    try {
        localStorage.setItem(
            "orders",
            JSON.stringify(orders)
        );
    } catch (error) {
        console.error(
            "Could not save orders:",
            error
        );
    }
}

function loadOrdersData() {
    try {
        const savedOrders =localStorage.getItem("orders");
        if (savedOrders) {
            orders =JSON.parse(savedOrders);
        }
        if (!Array.isArray(orders)) {
            orders = [];
        }
    } catch (error) {
        console.error("Could not load orders:",error);
        orders = [];
    }
}
function saveRecentlyViewed() {
    try {
        localStorage.setItem("recentlyViewed",JSON.stringify(recentlyViewed));
    } catch (error) {
        console.error("Could not save recently viewed:",error);
    }
}

function loadRecentlyViewed() {
    try {
        const saved =localStorage.getItem("recentlyViewed");
        if (saved) {
            recentlyViewed = JSON.parse(saved);
        }
        if (!Array.isArray(recentlyViewed)) {
            recentlyViewed = [];
        }
    } catch (error) {
        console.error("Could not load recently viewed:",error);
        recentlyViewed = [];
    }
}
/* =========================================================
    CART COUNT
========================================================= */
function updateCartCount() {
    const cartCount = cart.reduce(
        (total, item) =>
            total + Number(item.quantity || 0),0);
    const cartCountElement =
        document.getElementById("cartCount");
    if (cartCountElement) {
        cartCountElement.textContent =cartCount;
    }
}
/* =========================================================
   LOAD DATA
========================================================= */
//async function loadData() {
//    try {
//        const response = await fetch("data.json");
//        if (!response.ok) {
//            throw new Error(
//                "HTTP Error: " + response.status
//            );
//        }
//        const data = await response.json();
//        console.log("DATA:", data);
//        categories = data.categories || [];
//        products = data.products || [];
//        festivalProducts = data.festivalProducts || [];
//        relationshipProducts = data.relationshipProducts || [];
//
//        initializeApp();
//    } catch (error) {
//        console.error("DATA LOAD ERROR:",error);
//        document.body.innerHTML = `
//            <div style="text-align:center;padding:50px;">
//                <h2>Error loading data</h2>
//                <p>${error.message}</p>
//            </div>
//        `;
//    }
//}
async function loadData() {
    try {

        // =====================================================
        // LOAD CATEGORIES FROM BACKEND
        // =====================================================

        const categoryResponse =
            await fetch("http://localhost:8080/api/categories");

        if (!categoryResponse.ok) {
            throw new Error(
                "Category API Error: " + categoryResponse.status
            );
        }

        categories = await categoryResponse.json();

        console.log("BACKEND CATEGORIES:", categories);


        // =====================================================
        // LOAD FESTIVALS FROM BACKEND
        // =====================================================

        const festivalResponse =
            await fetch("http://localhost:8080/api/festivals");

        if (!festivalResponse.ok) {
            throw new Error(
                "Festival API Error: " + festivalResponse.status
            );
        }

        festivals = await festivalResponse.json();

        console.log("BACKEND FESTIVALS:", festivals);


        // =====================================================
        // LOAD PRODUCTS FROM BACKEND
        // =====================================================

        const productResponse =
            await fetch("http://localhost:8080/api/products");

        if (!productResponse.ok) {
            throw new Error(
                "Product API Error: " + productResponse.status
            );
        }

        const backendProducts =
            await productResponse.json();


        // =====================================================
        // CONVERT BACKEND PRODUCTS
        // =====================================================
        products = backendProducts.map(product => ({
            id: product.id,
            name: product.name,
            description: product.description,
            price: product.price,
            originalPrice: product.originalPrice,
            image: product.image,
            stock: product.stock,

            category: product.category
                ? product.category.id
                : null,

            categoryName: product.category
                ? product.category.name
                : null,

            festival: product.festival
                ? product.festival.id
                : null,

            festivalName: product.festival
                ? product.festival.name
                : null,

            relationship: product.relationship
                ? product.relationship.id
                : null,

            relationshipName: product.relationship
                ? product.relationship.name
                : null,

            brand: product.brand || "",
            rating: product.rating || 0,
            discount: product.discount || 0
        }));

        // =====================================================
        // OLD DATA.JSON PRODUCTS
        // =====================================================

        // Keep empty because products now come from backend
        festivalProducts = [];

        relationshipProducts = [];


        // =====================================================
        // DEBUG
        // =====================================================

        console.log(
            "================================="
        );

        console.log(
            "CATEGORIES:",
            categories
        );

        console.log(
            "FESTIVALS:",
            festivals
        );

        console.log(
            "PRODUCTS:",
            products
        );

        console.log(
            "PRODUCT COUNT:",
            products.length
        );

        console.log(
            "================================="
        );


        // =====================================================
        // INITIALIZE APPLICATION
        // =====================================================

        initializeApp();


    } catch (error) {

        console.error(
            "DATA LOAD ERROR:",
            error
        );

        document.body.innerHTML = `
            <div style="
                text-align:center;
                padding:50px;
                font-family:Arial;
            ">

                <h2>Data Loading Error</h2>

                <p>${error.message}</p>

                <p>
                    Make sure your Spring Boot backend
                    is running on port 8080.
                </p>

            </div>
        `;
    }
}
/* =========================================================
   INITIALIZE APP
========================================================= */
function initializeApp() {

    loadUserData();
    loadCartData();
    loadOrdersData();
    loadRecentlyViewed();

    renderCategories();
    renderFestivals();

    filteredProducts = [...products];

    console.log(
        "ALL PRODUCTS:",
        products
    );

    renderProducts(filteredProducts);

    updateCartCount();

    showPage("home");
}
/* =========================================================
   DOM READY
========================================================= */
document.addEventListener("DOMContentLoaded",() => {
        loadData();
        const sortBy =
            document.getElementById("sortBy");
        const priceRange =
            document.getElementById("priceRange");
        const brandFilter =
            document.getElementById("brandFilter");
        if (sortBy) {
            sortBy.addEventListener(
                "change",
                applyFilters
            );
        }
        if (priceRange) {
            priceRange.addEventListener(
                "input",
                applyFilters
            );
        }
        if (brandFilter) {
            brandFilter.addEventListener(
                "change",
                applyFilters
            );
        }
    }
);

/* =========================================================
   SHOW PAGE
========================================================= */
function showPage(pageId) {
    const pages =
        document.querySelectorAll(".page");
    pages.forEach(page => {
        page.classList.add("hidden");
    });
    const pageMap = {

        home: "homePage",

        cart: "cartPage",

        orders: "ordersPage",

        account: "accountPage",

        category: "categoryPage",

        product: "productPage",

        order: "orderPage",
        login: "loginPage",
    };
    const targetPage =
        document.getElementById(
            pageMap[pageId]
        );
    if (targetPage) {
        targetPage.classList.remove(
            "hidden"
        );
    } else {
        console.error(
            "Page not found:",
            pageMap[pageId]
        );
        return;
    }
    switch (pageId) {
        case "home":
            renderCategories();
            break;
        case "cart":
            if (
                typeof renderCart ===
                "function"
            ) {
                renderCart();
            }
            break;
        case "order":
            currentOrderSteps = 1;

            if (
                typeof renderOrderSteps ===
                "function"
            ) {
                renderOrderSteps();
            }
            break;
        case "account":
            if (
                typeof loadUserAccountPage ===
                "function"
            ) {
                loadUserAccountPage();
            }
            break;
        case "orders":
            if (
                typeof renderOrders ===
                "function"
            ) {
                renderOrders();
            }
            break;
    }
}
function toggleSidebar() {
    const sidebar = document.querySelector(".sidebar");
    const overlay = document.querySelector(".sidebar-overlay");

    if (sidebar) {
        sidebar.classList.toggle("active");
    }

    if (overlay) {
        overlay.classList.toggle("active");
    }
}

function searchProducts() {
    const searchInput = document.getElementById("searchInput");

    if (!searchInput) {
        console.error("searchInput not found");
        return;
    }

    const searchTerm = searchInput.value.trim().toLowerCase();

    // Reset search
    if (searchTerm === "") {
        filteredProducts = [...products];

        const categoryTitle =
            document.getElementById("categoryTitle");

        if (categoryTitle) {
            categoryTitle.textContent = "All Products";
        }

        populateFilters();
        renderProducts();
        showPage("category");
        return;
    }

    filteredProducts = products.filter(product => {
        const name =
            String(product.name || "").toLowerCase();

        const brand =
            String(product.brand || "").toLowerCase();

        const description =
            String(product.description || "").toLowerCase();

        return (
            name.includes(searchTerm) ||
            brand.includes(searchTerm) ||
            description.includes(searchTerm)
        );
    });

    const categoryTitle =
        document.getElementById("categoryTitle");

    if (categoryTitle) {
        categoryTitle.textContent =
            `Search results for "${searchTerm}"`;
    }

    populateFilters();
    renderProducts();
    showPage("category");
}
/* =========================================================
   IMAGE PATH
========================================================= */
function getImagePath(image) {
    if (!image) {
        return "images/logo.jpeg";
    }

    // Backend se complete URL aaye
    if (image.startsWith("http://") || image.startsWith("https://")) {
        return image;
    }

    // Already complete local path hai
    if (image.startsWith("images/")) {
        return image;
    }

    // Sirf filename ho
    return "images/" + image;
}
/* =========================================================
   LOAD CATEGORIES FROM BACKEND
========================================================= */
async function loadCategories() {
    try {
        const response = await fetch(
            "http://localhost:8080/api/categories"
        );

        console.log("Category API status:", response.status);

        const data = await response.text();

        console.log("Category API response:", data);

        if (!response.ok) {
            throw new Error(
                `Category API Error: ${response.status}`
            );
        }

        categories = JSON.parse(data);

        renderCategories();

    } catch (error) {
        console.error("Category loading error:", error);
    }
}
/* =========================================================
   RENDER CATEGORIES
========================================================= */
function renderCategories() {
    const categoryGrid =
        document.getElementById(
            "categoryGrid"
        );
    if (!categoryGrid) {
        console.error(
            "categoryGrid not found"
        );
        return;
    }
    categoryGrid.innerHTML = "";
    categories.forEach(category => {
        const categoryCard =
            document.createElement("div");
        categoryCard.className =
            "category-card";
        categoryCard.onclick = () => {
            showCategory(
                category.id
            );
        };
        let cardContent = `
            <img
               src="${getImagePath(category.image)}"
                alt="${category.name || ""}"
            >
           <div class="category-card-content">
                <h3>
                    ${category.name || ""}
                </h3>
                <p>
                    ${category.description || ""}
                </p>
            </div>
        `;
        if (category.isRecentlyViewed) {
            if (
                recentlyViewed.length === 0
            ) {
                cardContent += `
                    <p>
                        No recently viewed products
                    </p>
                `;
            } else {
                cardContent += `
                    <p>
                        You have
                        ${recentlyViewed.length}
                        recently viewed items
                    </p>
                `;
            }
        }
        cardContent += `
            <a
                href="#"
                class="category-btn"
                onclick="
                    event.preventDefault();
                    event.stopPropagation();
                    showCategory('${category.id}')
                "
            >
                View Products
            </a>
        `;
        categoryCard.innerHTML =
            cardContent;
        categoryGrid.appendChild(
            categoryCard
        );
    });
}
/* =========================================================
   RENDER PRODUCTS
========================================================= */
function renderProducts(
    productsToRender = filteredProducts
) {

    const productGrid =
        document.getElementById("productGrid");

    if (!productGrid) {

        console.error(
            "productGrid not found"
        );

        return;
    }


    productGrid.innerHTML = "";


    if (
        !productsToRender ||
        productsToRender.length === 0
    ) {

        productGrid.innerHTML = `
            <p>
                No products found
                matching your criteria.
            </p>
        `;

        return;
    }


    productsToRender.forEach(function (product) {

        const productCard =
            document.createElement("div");

        productCard.className =
            "product-card";


        productCard.onclick = function () {

            showProduct(
                product.id
            );

        };


        const rating =
            Number(product.rating) || 0;

        const fullStars =
            Math.floor(rating);

        const emptyStars =
            Math.max(
                0,
                5 - fullStars
            );


        productCard.innerHTML = `

            <img
                src="${getImagePath(product.image)}"
                alt="${product.name || "Product"}"
            >

            <div class="product-card-content">

                <div class="product-brand">
                    ${product.brand || ""}
                </div>

                <h3>
                    ${product.name || "Product"}
                </h3>

                <div class="product-rating">

                    ${"★".repeat(fullStars)}
                    ${"☆".repeat(emptyStars)}

                    <span>
                        ${rating}
                    </span>

                </div>


                <div class="product-price">

                    <span class="current-price">
                        ₹${Number(product.price) || 0}
                    </span>


                    ${
            product.originalPrice
                ? `
                                <span class="original-price">
                                    ₹${product.originalPrice}
                                </span>
                            `
                : ""
        }


                    ${
            product.discount
                ? `
                                <span class="discount">
                                    ${product.discount}% OFF
                                </span>
                            `
                : ""
        }

                </div>

            </div>
        `;


        productGrid.appendChild(
            productCard
        );

    });

}
/* =========================================================
   SHOW CATEGORY
========================================================= */
function showCategory(categoryId) {

    console.log("=================================");
    console.log("CLICKED CATEGORY:", categoryId);
    console.log("=================================");

    const selectedId =
        String(categoryId)
            .trim()
            .toLowerCase();

    // =====================================================
    // ALL GIFTS
    // =====================================================

    if (selectedId === "all") {

        console.log("SHOWING ALL GIFTS");

        filteredProducts = products.slice();

        const categoryTitle =
            document.getElementById("categoryTitle");

        if (categoryTitle) {
            categoryTitle.textContent = "All Gifts";
        }

        console.log(
            "ALL GIFTS COUNT:",
            filteredProducts.length
        );

        showPage("category");

        renderProducts(filteredProducts);

        return;
    }


    // =====================================================
    // RECENTLY VIEWED
    // =====================================================

    if (selectedId === "recently-viewed") {

        filteredProducts = products.filter(product =>
            recentlyViewed.some(
                id =>
                    String(id) ===
                    String(product.id)
            )
        );

        const categoryTitle =
            document.getElementById("categoryTitle");

        if (categoryTitle) {
            categoryTitle.textContent =
                "Recently Viewed Products";
        }

        showPage("category");

        renderProducts(filteredProducts);

        return;
    }


    // =====================================================
    // NORMAL CATEGORY
    // =====================================================

    const selectedCategory =
        categories.find(
            category =>
                String(category.id)
                    .trim()
                    .toLowerCase() ===
                selectedId
        );

    console.log(
        "SELECTED CATEGORY:",
        selectedCategory
    );

    const selectedCategoryName =
        selectedCategory
            ? String(selectedCategory.name)
                .trim()
                .toLowerCase()
            : "";

    console.log(
        "SELECTED CATEGORY NAME:",
        selectedCategoryName
    );


    // =====================================================
    // FILTER PRODUCTS
    // =====================================================

    filteredProducts =
        products.filter(product => {

            const productCategoryId =
                product.category != null
                    ? String(product.category)
                        .trim()
                        .toLowerCase()
                    : "";

            const productCategoryName =
                product.categoryName != null
                    ? String(product.categoryName)
                        .trim()
                        .toLowerCase()
                    : "";

            console.log(
                "PRODUCT:",
                product.name,
                "| CATEGORY ID:",
                productCategoryId,
                "| CATEGORY NAME:",
                productCategoryName
            );

            return (
                productCategoryId === selectedId ||
                productCategoryName ===
                selectedCategoryName
            );
        });


    console.log(
        "================================="
    );

    console.log(
        "FILTERED PRODUCTS:",
        filteredProducts
    );

    console.log(
        "FILTERED PRODUCT COUNT:",
        filteredProducts.length
    );

    console.log(
        "================================="
    );


    // =====================================================
    // CATEGORY TITLE
    // =====================================================

    const categoryTitle =
        document.getElementById(
            "categoryTitle"
        );

    if (categoryTitle) {
        categoryTitle.textContent =
            selectedCategory
                ? selectedCategory.name
                : "Products";
    }


    showPage("category");

    renderProducts(filteredProducts);
}
/* =========================================================
   POPULATE BRAND FILTER
========================================================= */
function populateFilters() {
    const brandFilter =
        document.getElementById(
            "brandFilter"
        );
    if (!brandFilter) return;
    const brands = [
        ...new Set(
            filteredProducts
                .map(
                    product =>
                        product.brand
                )
                .filter(Boolean)
        )
    ];
    brandFilter.innerHTML = `
        <option value="">
            All Brands
        </option>
    `;
    brands.forEach(brand => {
        const option =
            document.createElement(
                "option"
            );
        option.value = brand;
        option.textContent = brand;
        brandFilter.appendChild(
            option
        );
    });
}
/* =========================================================
   APPLY FILTERS
========================================================= */
function applyFilters() {
    const sortByElement =
        document.getElementById(
            "sortBy"
        );
    const priceRangeElement =
        document.getElementById(
            "priceRange"
        );
    const brandFilterElement =
        document.getElementById(
            "brandFilter"
        );
    const priceValueElement =
        document.getElementById(
            "priceValue"
        );
    if (
        !sortByElement ||
        !priceRangeElement ||
        !brandFilterElement
    ) {

        return;
    }
    const sortBy =
        sortByElement.value;
    const maxPrice =
        Number(
            priceRangeElement.value
        );
    const selectedBrand =
        brandFilterElement.value;
    if (priceValueElement) {
        priceValueElement.textContent =
            "₹" + maxPrice;

    }
    let filtered =
        filteredProducts.filter(
            product => {

                const price =
                    Number(
                        product.price
                    ) || 0;
                if (
                    price > maxPrice
                ) {

                    return false;
                }
                if (
                    selectedBrand &&
                    product.brand !==
                    selectedBrand
                ) {
                    return false;
                }
                return true;
            }
        );
    switch (sortBy) {
        case "price-low":
            filtered.sort(
                (a, b) =>
                    Number(a.price) -
                    Number(b.price)
            );
            break;
        case "price-high":
            filtered.sort(
                (a, b) =>
                    Number(b.price) -
                    Number(a.price)
            );
            break;
        case "rating":
            filtered.sort(
                (a, b) =>
                    Number(b.rating) -
                    Number(a.rating)
            );
            break;
        default:
            break;
    }
    renderProducts(
        filtered
    );
}
/* =========================================================
   SHOW PRODUCT
========================================================= */
function showProduct(productId) {

    const allProducts = [
        ...products,
        ...festivalProducts,
        ...relationshipProducts
    ];

    const product = allProducts.find(
        p => String(p.id) === String(productId)
    );

    if (!product) {
        console.error(
            "Product not found:",
            productId
        );
        return;
    }

    if (!recentlyViewed.includes(product.id)) {
        recentlyViewed.unshift(product.id);
        if (
            recentlyViewed.length > 10
        ) {
            recentlyViewed.pop();
        }
        saveRecentlyViewed();
    }
    const productDetail =
        document.getElementById(
            "productDetails"
        );
    if (!productDetail) {
        console.error(
            "productDetails not found"
        );
        return;
    }
    const deliveryDate =
        new Date();
    deliveryDate.setDate(
        deliveryDate.getDate() + 7
    );
    const colors =
        Array.isArray(
            product.colors
        )
            ? product.colors
            : [];
    const sizes =
        Array.isArray(
            product.sizes
        )
            ? product.sizes
            : [];
    const rating =
        Number(product.rating) || 0;
    productDetail.innerHTML = `
        <div>
            <img
                src="${getImagePath(product.image)}"
                alt="${product.name || ""}"
                class="product-image"
            >
        </div>
        <div class="product-info">
            <h1>
                ${product.name || ""}
            </h1>
            <div class="brand">
                ${product.brand || ""}
            </div>
            <div class="product-rating">
                ${"★".repeat(
        Math.floor(rating)
    )}
                ${"☆".repeat(
        Math.max(
            0,
            5 -
            Math.floor(rating)
        )
    )}
                ${rating}/5
            </div>
            <div class="product-price">
                <span class="current-price">
                    ₹${Number(product.price) || 0}
                </span>
                ${
        product.originalPrice
            ? `
                            <span class="original-price">
                                ₹${product.originalPrice}
                            </span>
                        `
            : ""
    }
                ${
        product.discount
            ? `
                            <span class="discount">
                                ${product.discount}% OFF
                            </span>
                        `
            : ""
    }
            </div>
            <div class="description">
                ${product.description || ""}
            </div>
            <div class="product-option">
                ${
        colors.length > 0
            ? `
                            <div class="option-group">
                                <label>
                                    Color:
                                </label>
                                <select
                                    id="selectedColor"
                                >
                                    ${colors
                .map(
                    color =>
                        `
                                                <option
                                                    value="${color}"
                                                >
                                                    ${color}
                                                </option>
                                                `
                )
                .join("")}
                                </select>
                            </div>
                        `
            : ""
    }
                ${
        sizes.length > 0
            ? `
                            <div class="option-group">
                                <label>
                                    Size:
                                </label>
                                <select
                                    id="selectedSize"
                                >
                                    ${sizes
                .map(
                    size =>
                        `
                                                <option
                                                    value="${size}"
                                                >
                                                    ${size}
                                                </option>
                                                `
                )
                .join("")}
                                </select>
                            </div>
                        `
            : ""
    }
            </div>
            <div class="address-section">
                <h3>
                    Delivery Address
                </h3>
                ${
        currentUser.address
            ? `
                            <p>
                                ${currentUser.address}
                            </p>
                            <button
                                class="btn-secondary"
                                onclick="
                                    showPage('account')
                                "
                            >
                                Change Address
                            </button>
                        `
            : `
                            <p>
                                No address added
                            </p>
                            <button
                                class="btn-secondary"
                                onclick="
                                    showPage('account')
                                "
                            >
                                Add Address
                            </button>
                        `
    }
            </div>
            <div class="delivery-info">
                <h4>
                    Delivery Information
                </h4>
                <p>
                    🚚 Delivery by
                    ${deliveryDate.toLocaleDateString()}
                </p>
                <p>
                    📦 10 days return policy
                </p>
                <p>
                    💵 Cash on delivery available
                </p>
            </div>
<div class="product-actions">

    <button
        class="btn-primary"
        onclick='addToCart(${JSON.stringify(product.id)})'
    >
        Add to Cart
    </button>

    <button
        class="btn-secondary"
        onclick='buyNow(${JSON.stringify(product.id)})'
    >
        Buy Now
    </button>

</div>
    `;
    showPage("product");
}

/* =========================================================
   ADD TO CART
========================================================= */
async function addToCart(productId) {

    // User must be logged in
    if (!currentUser || !currentUser.id) {
        alert("Please login first.");
        showPage("login");
        return;
    }

    const allProducts = [
        ...products,
        ...festivalProducts,
        ...relationshipProducts
    ];

    const product = allProducts.find(
        product => String(product.id) === String(productId)
    );

    if (!product) {
        console.error("Product not found:", productId);
        alert("Product not found.");
        return;
    }

    const colorElement = document.getElementById("selectedColor");
    const sizeElement = document.getElementById("selectedSize");

    const selectedColor = colorElement ? colorElement.value : "";
    const selectedSize = sizeElement ? sizeElement.value : "";

    // =====================================================
    // 1. Update frontend/localStorage cart
    // =====================================================

    const existingItem = cart.find(item =>
        String(item.id) === String(product.id) &&
        item.color === selectedColor &&
        item.size === selectedSize
    );

    if (existingItem) {

        existingItem.quantity =
            Number(existingItem.quantity || 0) + 1;

    } else {

        cart.push({
            id: product.id,
            name: product.name || "",
            brand: product.brand || "",
            price: Number(product.price) || 0,

            originalPrice:
                Number(product.originalPrice) ||
                Number(product.price) ||
                0,

            discount: Number(product.discount) || 0,
            image: product.image || "",

            color: selectedColor,
            size: selectedSize,

            quantity: 1
        });
    }

    saveCartData();
    updateCartCount();

    // =====================================================
    // 2. Save product in BACKEND / MySQL
    // =====================================================

    try {

        const token = localStorage.getItem("token");

        const response = await fetch(
            "http://localhost:8080/api/cart/add",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },

                body: JSON.stringify({
                    userId: currentUser.id,
                    productId: Number(product.id),
                    quantity: 1
                })
            }
        );

        const responseText = await response.text();

        let data = {};

        try {
            data = JSON.parse(responseText);
        } catch (e) {
            console.log("Backend response:", responseText);
        }

        console.log("Cart API Status:", response.status);
        console.log("Cart API Response:", data);

        if (!response.ok) {

            alert(
                "Cart could not be saved in database.\n" +
                (data.message || responseText || "Unknown error")
            );

            return;
        }

        console.log("Product successfully saved to backend cart.");

        alert("Product added to cart");

    } catch (error) {

        console.error("Cart API Error:", error);

        alert(
            "Product added locally, but could not connect to backend."
        );
    }
}
/* =========================================================
   BUY NOW
========================================================= */
function buyNow(productId) {
    addToCart(productId);
    showPage("cart");
}
/* =========================================================
   RENDER CART
========================================================= */
function renderCart() {
    const cartItems =
        document.getElementById(
            "cartItems"
        );
    const cartSummary =
        document.getElementById(
            "cartSummary"
        );
    if (!cartItems || !cartSummary) {
        console.error(
            "cartItems or cartSummary not found"
        );
        return;
    }
    if (cart.length === 0) {
        cartItems.innerHTML = `
            <p>
               Your cart is empty.
               <a
                    href="#"
                    onclick="
                        event.preventDefault();
                        showPage('home')
                    "
                >
                    Continue Shopping
                </a>
           </p>
       `;
        cartSummary.innerHTML = "";
        return;
    }
    cartItems.innerHTML = "";
    let totalOriginal = 0;

    let totalDiscounted = 0;
    cart.forEach(
        (item, index) => {

            const price =
                Number(item.price) || 0;
            const originalPrice =
                Number(
                    item.originalPrice
                ) || price;
            const quantity =
                Number(
                    item.quantity
                ) || 1;
            const itemTotal =
                price * quantity;
            const itemOriginalTotal =
                originalPrice *
                quantity;
            totalOriginal +=
                itemOriginalTotal;
            totalDiscounted +=
                itemTotal;
            const cartItem =
                document.createElement(
                    "div"
                );
            cartItem.className =
                "cart-item";
            cartItem.innerHTML = `
                <img
                   src="${getImagePath(item.image)}"
                    alt="${item.name || ""}"
                >
                <div class="cart-item-details">
                    <h3>
                        ${item.name || ""}
                    </h3>
                    <div class="product-brand">
                        ${item.brand || ""}
                    </div>
                    ${
                item.color
                    ? `
                                <p>
                                    Color:
                                    ${item.color}
                                </p>
                            `
                    : ""
            }
                    ${
                item.size
                    ? `
                                <p>
                                    Size:
                                    ${item.size}
                                </p>
                            `
                    : ""
            }
                    <div class="product-price">
                        <span class="current-price">
                            ₹${price}
                        </span>
                        ${
                originalPrice > price
                    ? `
                                    <span class="original-price">
                                        ₹${originalPrice}
                                    </span>
                                `
                    : ""
            }
                        ${
                item.discount
                    ? `
                                    <span class="discount">
                                        ${item.discount}% OFF
                                    </span>
                                `
                    : ""
            }
                    </div>
                    <div class="quantity-controls">
                        <button
                            class="quantity-btn"
                            onclick="
                                updateQuantity(
                                    ${index},
                                    -1
                                )
                            "
                        >
                            -
                        </button>
                        <input
                            type="number"
                            class="quantity-input"
                            value="${quantity}"
                            min="1"
                            onchange="
                                updateQuantity(
                                    ${index},
                                    0,
                                    this.value
                                )
                            "
                        >
                        <button
                            class="quantity-btn"
                            onclick="
                                updateQuantity(
                                    ${index},
                                    1
                                )
                            "
                        >
                            +
                        </button>
                    </div>
                    <p>
                        Total:
                        ₹${itemTotal}
                    </p>
                </div>
                <button
                    class="btn-secondary"
                    onclick="
                        removeFromCart(
                            ${index}
                        )
                    "
                >
                    Remove
                </button>

            `;
            cartItems.appendChild(
                cartItem
            );
        }
    );
    const deliveryCharges =
        totalDiscounted >= 500
            ? 0
            : 50;
    const finalTotal =
        totalDiscounted +
        deliveryCharges;
    cartSummary.innerHTML = `
        <h3>
            Price Details
        </h3>
        <div class="summary-row">
            <span>
                Total MRP:
            </span>
            <span>
                ₹${totalOriginal}
            </span>
        </div>
        <div class="summary-row">
            <span>
                Discount:
            </span>
            <span>
                ₹${
        totalOriginal -
        totalDiscounted
    }
            </span>
        </div>
        <div class="summary-row">
            <span>
                Delivery Charges:
            </span>
            <span>
                ${
        deliveryCharges === 0
            ? "FREE"
            : "₹" +
            deliveryCharges
    }
            </span>
        </div>
        <div class="summary-divider"></div>
        <div
            class="summary-row summary-total"
        >
            <span>
                Total Amount:
            </span>

            <span>
                ₹${finalTotal}
            </span>
        </div>
        <button
            class="btn-primary"
            onclick="
                proceedToCheckout()
            "
            style="
                width:100%;
                margin-top:20px;
            "
        >
            Place Order
        </button>

    `;
}
/* =========================================================
   UPDATE QUANTITY
========================================================= */
function updateQuantity(index, change, newValue = null) {

    if (!cart[index]) return;

    if (newValue !== null) {

        cart[index].quantity = Math.max(
            1,
            parseInt(newValue) || 1
        );

    } else {

        cart[index].quantity = Math.max(
            1,
            Number(cart[index].quantity) + change
        );
    }

    updateCartCount();
    saveCartData();
    renderCart();
}
// ======================================================
// ORDER STEPS
// ======================================================

function renderOrderSteps() {

    const orderSteps = document.getElementById("orderSteps");

    if (!orderSteps) return;
// ==================================================
// STEP 1: USER DETAILS
// ==================================================
    if (currentOrderSteps === 1) {
        orderSteps.innerHTML = `
            <div class="order-form">
                <h2>Step 1: Enter Your Details</h2>
                <div class="form-group">
                    <label for="orderName">Name:</label>
                    <input
                        type="text"
                        id="orderName"
                        value="${currentUser.name || ""}"
                        placeholder="Enter your name"
                    >
                </div>
                <div class="form-group">
                    <label for="orderPhone">Phone Number:</label>
                    <input
                        type="tel"
                        id="orderPhone"
                        value="${currentUser.phone || ""}"
                        placeholder="Enter your phone number"
                    >
                </div>
                <div class="form-group">
                    <label for="orderAddress">Address:</label>
                    <textarea
                        id="orderAddress"
                        placeholder="Enter your complete address"
                    >${currentUser.address || ""}</textarea>
                </div>
                <button
                    class="btn-primary"
                    onclick="saveOrderDetails()"
                >
                    Continue to Summary
                </button>
            </div>
        `;
    }
// ==================================================
// STEP 2: ORDER SUMMARY
// ==================================================
    else if (currentOrderSteps === 2) {
        const cartTotal = cart.reduce(
            (total, item) =>
                total + Number(item.price) * Number(item.quantity),
            0
        );
        const deliveryCharges = cartTotal > 500 ? 0 : 50;
        const finalTotal =
            cartTotal + deliveryCharges;
        let cartItemsHtml = "";
        cart.forEach((item) => {
            cartItemsHtml += `
                <div class="cart-item">
                  <img src="${getImagePath(item.image)}"
                        alt="${item.name}"
                    >
                    <div class="cart-item-details">
                        <h3>${item.name}</h3>
                        <div class="product-brand">
                            ${item.brand || ""}
                        </div>
                        ${
                item.color
                    ? `<p>Color: ${item.color}</p>`
                    : ""
            }
                        ${
                item.size
                    ? `<p>Size: ${item.size}</p>`
                    : ""
            }
                        <p>
                            Quantity: ${item.quantity}
                        </p>
                        <p>
                            Price:
                            ₹${Number(item.price) * Number(item.quantity)}
                        </p>
                    </div>
                </div>
            `;
        });
        orderSteps.innerHTML = `
            <div class="order-form">
                <h2>Step 2: Order Summary</h2>
                <div class="address-section">
                    <h3>Delivery Address</h3>
                    <p>
                        <strong>
                            ${currentUser.name}
                        </strong>
                    </p>
                    <p>${currentUser.phone}</p>
                    <p>${currentUser.address}</p>
                </div>
                <h3>Order Items</h3>
                ${cartItemsHtml}
                <div class="cart-summary">
                    <div class="summary-row">
                        <span>Items Total:</span>
                        <span>
                            ₹${cartTotal}
                        </span>
                    </div>
                    <div class="summary-row">
                        <span>Delivery Charges:</span>
                        <span>
                            ${
            deliveryCharges === 0
                ? "FREE"
                : "₹" + deliveryCharges
        }
                        </span>
                    </div>
                    <div class="summary-divider"></div>
                    <div class="summary-row summary-total">
                        <span>Total Amount:</span>
                        <span>
                            ₹${finalTotal}
                        </span>
                    </div>
                </div>
                <button
                    class="btn-primary"
                    onclick="proceedToPayment()"
                >
                    Proceed to Payment
                </button>
            </div>
        `;
    }
        // ==================================================
        // STEP 3: PAYMENT
    // ==================================================
    else if (currentOrderSteps === 3) {
        orderSteps.innerHTML = `
            <div class="order-form">
                <h2>Step 3: Payment</h2>
                <div class="payment-options">
                    <div class="payment-option">
                        <input
                            type="radio"
                            id="upi"
                            name="payment"
                            value="upi"
                        >
                        <label for="upi">
                            UPI Payment
                        </label>
                    </div>
                    <div class="payment-option">
                        <input
                            type="radio"
                            id="card"
                            name="payment"
                            value="card"
                        >
                        <label for="card">
                            Credit/Debit Card
                        </label>
                    </div>
                    <div class="payment-option">
                        <input
                            type="radio"
                            id="cod"
                            name="payment"
                            value="cod"
                            checked
                        >
                        <label for="cod">
                            Cash on Delivery
                        </label>
                    </div>
                </div>
                <button
                    class="btn-primary"
                    onclick="placeOrder()"
                >
                    Place Order
                </button>
            </div>
        `;
    }
}
// ======================================================
// SAVE USER DETAILS
// ======================================================
function saveOrderDetails() {

    const nameElement =
        document.getElementById("orderName");

    const emailElement =
        document.getElementById("orderEmail");

    const phoneElement =
        document.getElementById("orderPhone");

    const addressElement =
        document.getElementById("orderAddress");


    if (
        !nameElement ||
        !phoneElement ||
        !addressElement
    ) {
        console.error(
            "Checkout form fields not found."
        );
        alert(
            "Unable to read checkout details."
        );
        return;
    }


    const name =
        nameElement.value.trim();

    const email =
        emailElement
            ? emailElement.value.trim()
            : "";

    const phone =
        phoneElement.value.trim();

    const address =
        addressElement.value.trim();


    if (!name || !phone || !address) {

        alert(
            "Please fill all required fields."
        );

        return;
    }


    if (!validateName(name)) {

        alert(
            "Please enter a valid name (2-50 characters, letters only)."
        );

        return;
    }


    if (!validatePhone(phone)) {

        alert(
            "Please enter a valid 10-digit phone number."
        );

        return;
    }


    if (
        email &&
        !validateEmail(email)
    ) {

        alert(
            "Please enter a valid email address."
        );

        return;
    }


    // Save customer details
    currentUser.name = name;
    currentUser.email = email;
    currentUser.phone = phone;
    currentUser.address = address;

    saveUserData();


    // Go to summary
    currentOrderSteps = 2;

    renderOrderSteps();
}
// ======================================================
// GO TO PAYMENT
// ======================================================
function proceedToPayment() {

    currentOrderSteps = 3;

    renderOrderSteps();
}
// ======================================================
// PLACE ORDER
// ======================================================
async function placeOrder() {

    // ==========================================
    // 1. CHECK PAYMENT METHOD
    // ==========================================

    const selectedPayment = document.querySelector(
        'input[name="payment"]:checked'
    );

    if (!selectedPayment) {
        alert("Please select a payment method.");
        return;
    }


    // ==========================================
    // 2. CHECK LOGIN / USER
    // ==========================================

    if (!currentUser || !currentUser.id) {
        alert("User ID not found. Please login again.");
        return;
    }

    console.log("Current User:", currentUser);
    console.log("User ID:", currentUser.id);


    // ==========================================
    // 3. CHECK CART
    // ==========================================

    if (!Array.isArray(cart) || cart.length === 0) {
        alert("Your cart is empty.");
        return;
    }


    // ==========================================
    // 4. CHECK CUSTOMER DETAILS
    // ==========================================

    if (
        !currentUser.name ||
        !currentUser.phone ||
        !currentUser.address
    ) {
        alert("Please enter your delivery details.");

        currentOrderSteps = 1;
        renderOrderSteps();

        return;
    }


    // ==========================================
    // 5. GET PAYMENT METHOD
    // ==========================================

    const frontendPaymentMethod = selectedPayment.value;

    console.log(
        "Frontend Payment:",
        frontendPaymentMethod
    );


    let backendPaymentMethod;


    if (
        frontendPaymentMethod === "Cash on Delivery" ||
        frontendPaymentMethod === "COD"
    ) {

        backendPaymentMethod = "COD";

    } else if (
        frontendPaymentMethod === "Online Payment" ||
        frontendPaymentMethod === "RAZORPAY"
    ) {

        backendPaymentMethod = "RAZORPAY";

    } else {

        alert("Invalid payment method.");
        return;
    }


    console.log(
        "Backend Payment:",
        backendPaymentMethod
    );


    // ==========================================
    // 6. CALCULATE TOTAL
    // ==========================================

    const cartTotal = cart.reduce(
        (total, item) => {

            return total +
                Number(item.price || 0) *
                Number(item.quantity || 0);

        },
        0
    );


    const deliveryCharges =
        cartTotal >= 500 ? 0 : 50;


    const finalTotal =
        cartTotal + deliveryCharges;


    console.log(
        "Cart Total:",
        cartTotal
    );

    console.log(
        "Delivery Charges:",
        deliveryCharges
    );

    console.log(
        "Frontend Final Total:",
        finalTotal
    );


    // ==========================================
    // 7. CREATE ORDER IN MYSQL
    // ==========================================

    try {

        const token =
            localStorage.getItem("token");


        console.log(
            "Creating database order..."
        );


        const orderResponse = await fetch(
            "http://localhost:8080/api/orders",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json",

                    "Authorization":
                        `Bearer ${token}`
                },

                body: JSON.stringify({

                    userId:
                    currentUser.id,

                    paymentMethod:
                    backendPaymentMethod
                })
            }
        );


        // ==========================================
        // 8. READ ORDER RESPONSE
        // ==========================================

        const orderText =
            await orderResponse.text();


        let orderData = {};


        try {

            orderData =
                JSON.parse(orderText);

        } catch (e) {

            console.error(
                "Order response is not JSON:",
                orderText
            );
        }


        console.log(
            "Order API Status:",
            orderResponse.status
        );


        console.log(
            "Order API Response:",
            orderData
        );


        // ==========================================
        // 9. CHECK ORDER API
        // ==========================================

        if (!orderResponse.ok) {

            alert(
                "Order failed: " +
                (
                    orderData.message ||
                    orderText ||
                    "Backend could not create order."
                )
            );

            return;
        }


        // ==========================================
        // 10. GET DATABASE ORDER ID
        // ==========================================

        const backendOrderId =
            orderData.id;


        console.log(
            "Database Order ID:",
            backendOrderId
        );


        // ==========================================
        // 11. CHECK DATABASE ORDER ID
        // ==========================================

        if (!backendOrderId) {

            alert(
                "Order created but database Order ID was not received."
            );

            return;
        }


        // ==========================================
        // 12. RAZORPAY PAYMENT
        // ==========================================

        if (
            backendPaymentMethod === "RAZORPAY"
        ) {

            console.log(
                "Starting Razorpay for Order:",
                backendOrderId
            );


            await payWithRazorpay(
                backendOrderId,
                orderData
            );


            // IMPORTANT:
            // Razorpay function will handle
            // payment verification and success.

            return;
        }


        // ==========================================
        // 13. COD ORDER SUCCESS
        // ==========================================

        const orderId =
            "ORD" +
            Date.now() +
            Math.floor(
                Math.random() * 1000
            );


        const orderDate =
            new Date();


        const deliveryDate =
            new Date();


        deliveryDate.setDate(
            deliveryDate.getDate() + 7
        );


        // ==========================================
        // 14. CREATE FRONTEND COD ORDER
        // ==========================================

        const order = {

            id:
            orderId,

            backendOrderId:
            backendOrderId,

            items:
                cart.map(item => ({
                    ...item
                })),

            total:
            cartTotal,

            deliveryCharges:
            deliveryCharges,

            finalTotal:
            finalTotal,

            paymentMethod:
            frontendPaymentMethod,

            paymentStatus:
                "PENDING",

            orderDate:
                orderDate.toISOString(),

            deliveryDate:
                deliveryDate.toISOString(),

            status:
                "confirmed",

            name:
            currentUser.name,

            email:
            currentUser.email,

            phone:
            currentUser.phone,

            address:
            currentUser.address
        };


        console.log(
            "Frontend COD Order:",
            order
        );


        // ==========================================
        // 15. SAVE COD ORDER
        // ==========================================

        orders.push(order);

        saveOrdersData();


        // ==========================================
        // 16. CLEAR FRONTEND CART
        // ==========================================

        cart = [];

        saveCartData();

        updateCartCount();


        // ==========================================
        // 17. SHOW SUCCESS
        // ==========================================
        console.log("========== BEFORE COD SUCCESS PAGE ==========");
        console.log("orderId:", orderId);
        console.log("finalTotal:", finalTotal);
        console.log("deliveryDate:", deliveryDate);
        console.log("showOrderSuccess:", typeof showOrderSuccess);

        showOrderSuccess(
            orderId,
            "Cash on Delivery",
            finalTotal,
            deliveryDate
        );

    } catch (error) {

        console.error("========== FRONTEND ORDER ERROR ==========");
        console.error("Error:", error);
        console.error("Message:", error.message);
        console.error("Stack:", error.stack);

        alert(
            "Payment successful, but frontend display failed.\n\n" +
            "Error: " + error.message
        );
    }
}
// ======================================================
// RENDER ORDERS
// ======================================================
function renderOrders() {
    const ordersList =
        document.getElementById("ordersList");
    if (!ordersList) return;
    if (orders.length === 0) {
        ordersList.innerHTML = `
            <p>
                No orders found.
                <a
                    href="#"
                    onclick="showPage('home')"
                >
                    Start shopping
                </a>
            </p>
        `;
        return;
    }
    ordersList.innerHTML = "";
    const sortedOrders = [...orders].sort(
        (a, b) =>
            new Date(b.orderDate) -
            new Date(a.orderDate)
    );
    sortedOrders.forEach((order) => {
        const currentDate =
            new Date();
        const deliveryDate =
            new Date(order.deliveryDate);
        const orderDate =
            new Date(order.orderDate);
        const isDelivered =
            currentDate >= deliveryDate;
        const orderDiv =
            document.createElement("div");
        orderDiv.className =
            "order-card";
        let orderItemsHtml = "";
        order.items.forEach((item) => {
            orderItemsHtml += `
                <div class="cart-item">
                    <img
                        src="${getImagePath(item.image)}"
                        alt="${item.name}"
                    >
                    <div class="cart-item-details">
                        <h3>
                            ${item.name}
                        </h3>
                        <div class="product-brand">
                            ${item.brand || ""}
                        </div>
                        ${
                item.color
                    ? `<p>Color: ${item.color}</p>`
                    : ""
            }
                        ${
                item.size
                    ? `<p>Size: ${item.size}</p>`
                    : ""
            }
                        <p>
                            Quantity:
                            ${item.quantity}
                        </p>
                        <p>
                            Price:
                            ₹${Number(item.price) * Number(item.quantity)}
                        </p>
                    </div>
                </div>
            `;
        });
        orderDiv.innerHTML = `
            <div
                class="order-header"
                onclick="toggleOrderDetails('${order.id}')"
            >
                <div class="order-summary">
                    <h3>
                        Order ID:
                        ${order.id}
                    </h3>
              <span
    class="status-badge ${
            order.status === "Cancelled"
                ? "cancelled"
                : isDelivered
                    ? "delivered"
                    : "on-way"
        }"
>
    ${
            order.status === "Cancelled"
                ? "Cancelled"
                : isDelivered
                    ? "Delivered"
                    : "On the way"
        }
</span>
                </div>
                <div class="order-meta">
                    <p>
                        <strong>
                            Order Date:
                        </strong>

                        ${orderDate.toLocaleDateString()}
                    </p>
                    <p>
                        <strong>
                            Total:
                        </strong>
                        ₹${
            Number(order.total) +
            Number(order.deliveryCharges)
        }
                    </p>
                    <p>
                        <strong>
                            Items:
                        </strong>
                        ${order.items.length}
                        ${
            order.items.length > 1
                ? "items"
                : "item"
        }
                    </p>
                    <div class="dropdown-arrow">
                        <span class="arrow-icon">
                            ▼
                        </span>
                    </div>
                </div>
            </div>
            <div
                class="order-details"
                id="details-${order.id}"
                style="display: none;"
            >
                <div class="order-info">
                    <p>
                        <strong>
                            Delivery Date:
                        </strong>

                        ${deliveryDate.toLocaleDateString()}
                    </p>
                    <p>
                        <strong>
                            Payment Method:
                        </strong>

                        ${order.paymentMethod.toUpperCase()}
                    </p>
                    <div class="address-section">
                        <h4>
                            Delivery Address:
                        </h4>

                        <p>${order.name}</p>

                        <p>${order.phone}</p>

                        <p>${order.address}</p>
                    </div>
                    <h4>
                        Order Items:
                    </h4>
                    ${orderItemsHtml}
                    <div class="cart-summary">
                        <div class="summary-row">
                            <span>
                                Items Total:
                            </span>
                            <span>
                                ₹${order.total}
                            </span>
                        </div>
                        <div class="summary-row">
                            <span>
                                Delivery Charges:
                            </span>
                            <span>
                                ${
            Number(order.deliveryCharges) === 0
                ? "FREE"
                : "₹" + order.deliveryCharges
        }
                            </span>
                        </div>
                        <div class="summary-divider"></div>
                        <div class="summary-row summary-total">
                            <span>
                                Total Amount:
                            </span>
                            <span>
                                ₹${
            Number(order.total) +
            Number(order.deliveryCharges)
        }
                            </span>
                        </div>
  <div class="order-actions">

    ${
            !isDelivered && order.status !== "Cancelled"
                ? `
                <button
                    type="button"
                    class="cancel-order-btn"
                    onclick="cancelOrder('${order.id}'); event.stopPropagation();">
                    ❌ Cancel Order
                </button>
            `
                : ""
        }

    ${
            order.status !== "Cancelled"
                ? `
                <button
                    type="button"
                    class="track-order-btn"
                    onclick="showOrderTracking('${order.id}'); event.stopPropagation();">
                    🚚 Track Order
                </button>
            `
                : ""
        }

</div>

<div
    id="tracking-${order.id}"
    class="order-tracking"
    style="display:none;">
</div>
                    </div>
                </div>
            </div>
        `;
        ordersList.appendChild(orderDiv);
    });
}
function showOrderTracking(orderId) {

    const order =
        orders.find(o => o.id === orderId);

    if (!order) {
        alert("Order not found.");
        return;
    }

    const trackingElement =
        document.getElementById(
            `tracking-${orderId}`
        );

    if (!trackingElement) return;

    // Toggle
    if (trackingElement.style.display === "block") {
        trackingElement.style.display = "none";
        trackingElement.innerHTML = "";
        return;
    }

    const orderDate =
        new Date(order.orderDate);

    const deliveryDate =
        new Date(order.deliveryDate);

    const currentDate =
        new Date();

    const totalTime =
        deliveryDate.getTime() -
        orderDate.getTime();

    const elapsedTime =
        currentDate.getTime() -
        orderDate.getTime();

    let progress =
        totalTime > 0
            ? elapsedTime / totalTime
            : 1;

    progress =
        Math.max(0, Math.min(1, progress));

    let currentStep = 1;

    if (order.status === "Cancelled") {
        currentStep = -1;
    } else if (progress >= 1) {
        currentStep = 4;
    } else if (progress >= 0.70) {
        currentStep = 3;
    } else if (progress >= 0.35) {
        currentStep = 2;
    }

    const steps = [
        {
            title: "Order Placed",
            icon: "✓"
        },
        {
            title: "Confirmed",
            icon: "✓"
        },
        {
            title: "Shipped",
            icon: "🚚"
        },
        {
            title: "Out for Delivery",
            icon: "🚚"
        },
        {
            title: "Delivered",
            icon: "✓"
        }
    ];

    let stepsHTML = "";

    steps.forEach((step, index) => {

        const stepNumber = index;

        let className = "";

        if (currentStep === -1) {
            className = "cancelled";
        } else if (stepNumber < currentStep) {
            className = "completed";
        } else if (stepNumber === currentStep) {
            className = "current";
        }

        stepsHTML += `
            <div class="tracking-step ${className}">

                <div class="tracking-icon">
                    ${step.icon}
                </div>

                <div class="tracking-step-content">

                    <strong>
                        ${step.title}
                    </strong>

                    <span>
                        ${
            stepNumber === 0
                ? "Your order has been placed"
                : stepNumber === 1
                    ? "Your order has been confirmed"
                    : stepNumber === 2
                        ? "Your order has been shipped"
                        : stepNumber === 3
                            ? "Your order is out for delivery"
                            : "Your order has been delivered"
        }
                    </span>

                </div>

            </div>
        `;
    });

    trackingElement.innerHTML = `

        <div class="order-tracking-box">

            <h3>
                🚚 Track Order
            </h3>

            <p>
                <strong>Order ID:</strong>
                ${order.id}
            </p>

            <p>
                <strong>Expected Delivery:</strong>
                ${deliveryDate.toLocaleDateString()}
            </p>

            ${
        order.status === "Cancelled"
            ? `
                        <div class="tracking-cancelled">
                            ❌ This order has been cancelled.
                        </div>
                    `
            : `
                        <div class="tracking-timeline">
                            ${stepsHTML}
                        </div>
                    `
    }

        </div>
    `;

    trackingElement.style.display = "block";
}
/*--------------------------------------------------*/
/*--------------------------------------------------*/
/* CANCEL ORDER */
/*--------------------------------------------------*/

async function cancelOrder(orderId) {

    const confirmCancel = confirm(
        "Are you sure you want to cancel this order?"
    );

    if (!confirmCancel) {
        return;
    }

    const orderIndex = orders.findIndex(
        order => String(order.id) === String(orderId)
    );

    if (orderIndex === -1) {
        alert("Order not found.");
        return;
    }

    const order = orders[orderIndex];

    const currentDate = new Date();

    const deliveryDate =
        new Date(order.deliveryDate);

    // Don't allow cancellation after delivery date
    if (currentDate >= deliveryDate) {
        alert(
            "This order cannot be cancelled because it has already been delivered."
        );
        return;
    }

    // ==========================================
    // BACKEND ORDER ID CHECK
    // ==========================================

    if (!order.backendOrderId) {

        alert(
            "Backend Order ID not found."
        );

        console.error(
            "backendOrderId missing:",
            order
        );

        return;
    }

    try {

        const token =
            localStorage.getItem("token");

        // ==========================================
        // CANCEL ORDER IN MYSQL
        // ==========================================

        const response = await fetch(
            `http://localhost:8080/api/orders/${order.backendOrderId}/cancel`,
            {
                method: "PUT",

                headers: {
                    "Content-Type": "application/json",

                    "Authorization":
                        `Bearer ${token}`
                }
            }
        );

        const responseText =
            await response.text();

        let responseData = {};

        try {
            responseData =
                JSON.parse(responseText);
        } catch (e) {
            console.log(
                "Backend response:",
                responseText
            );
        }

        console.log(
            "Cancel API Status:",
            response.status
        );

        console.log(
            "Cancel API Response:",
            responseData
        );

        // ==========================================
        // CHECK BACKEND RESPONSE
        // ==========================================

        if (!response.ok) {

            alert(
                responseData.message ||
                responseText ||
                "Order cancellation failed."
            );

            return;
        }

        // ==========================================
        // UPDATE FRONTEND ORDER
        // ==========================================

        orders[orderIndex].status =
            "Cancelled";

        orders[orderIndex].cancelledDate =
            new Date().toISOString();

        // ==========================================
        // SAVE LOCAL STORAGE
        // ==========================================

        localStorage.setItem(
            "orders",
            JSON.stringify(orders)
        );

        alert(
            "Order cancelled successfully."
        );

        // ==========================================
        // REFRESH ORDERS
        // ==========================================

        renderOrders();

    } catch (error) {

        console.error(
            "Cancel Order Error:",
            error
        );

        alert(
            "Unable to cancel order.\n\n" +
            error.message
        );
    }
}
// ======================================================
// TOGGLE ORDER DETAILS
// ======================================================

function toggleOrderDetails(orderId) {
    const detailsDiv =
        document.getElementById(
            `details-${orderId}`
        );
    if (!detailsDiv) return;
    const arrowIcon =
        detailsDiv.previousElementSibling
            .querySelector(".arrow-icon");
    if (detailsDiv.style.display === "none") {
        detailsDiv.style.display =
            "block";
        if (arrowIcon) {
            arrowIcon.style.transform =
                "rotate(180deg)";
        }
    } else {
        detailsDiv.style.display =
            "none";
        if (arrowIcon) {
            arrowIcon.style.transform =
                "rotate(0deg)";
        }
    }
}

function saveUserInfo() {

    const name =
        document.getElementById("userName").value.trim();

    const email =
        document.getElementById("userEmail").value.trim();

    const phone =
        document.getElementById("userPhone").value.trim();

    const address =
        document.getElementById("userAddress").value.trim();

    if (name && !validateName(name)) {
        alert(
            "Please enter a valid name (2-50 characters, letters only)."
        );
        return;
    }

    if (email && !validateEmail(email)) {
        alert("Please enter a valid email address.");
        return;
    }

    if (phone && !validatePhone(phone)) {
        alert("Please enter a valid 10-digit phone number.");
        return;
    }

    currentUser.name = name;
    currentUser.email = email;
    currentUser.phone = phone;
    currentUser.address = address;

    saveUserData();

    alert("Information saved successfully!");
}
/* =========================================================
   REMOVE FROM CART
========================================================= */

function removeFromCart(index) {

    if (!cart[index]) return;
    cart.splice(
        index,
        1
    );
    updateCartCount();

    saveCartData();

    renderCart();
}
/* =========================================================
   PROCEED TO CHECKOUT
========================================================= */
function proceedToCheckout() {
    if (cart.length === 0) {
        alert(
            "Your cart is empty."
        );
        return;
    }
    currentOrderSteps = 1;
    showPage("order");
}
// ================= RENDER ORDER STEPS =================
// ======================================================
// ORDER CHECKOUT STEPS
// ======================================================

function renderOrderSteps() {

    const orderSteps =
        document.getElementById("orderSteps");

    if (!orderSteps) {
        console.error("orderSteps element not found");
        return;
    }

    // Cart must contain products
    if (!cart || cart.length === 0) {

        orderSteps.innerHTML = `
            <div class="empty-cart">

                <h2>Your cart is empty</h2>

                <p>
                    Add some products before proceeding
                    to checkout.
                </p>

                <button
                    type="button"
                    onclick="showPage('home')"
                    class="btn-primary"
                >
                    Continue Shopping
                </button>

            </div>
        `;

        return;
    }

    // Calculate totals
    const cartTotal = cart.reduce(
        (total, item) =>
            total +
            Number(item.price || 0) *
            Number(item.quantity || 0),
        0
    );

    const deliveryCharges =
        cartTotal >= 500 ? 0 : 50;

    const finalTotal =
        cartTotal + deliveryCharges;


    // ==================================================
    // STEP 1 — CUSTOMER DETAILS
    // ==================================================

    if (currentOrderSteps === 1) {

        orderSteps.innerHTML = `

            <div class="order-container">

                <div class="order-header">

                    <h2>Checkout</h2>

                    <div class="checkout-steps">

                        <span class="step active">
                            1. Details
                        </span>

                        <span class="step">
                            2. Summary
                        </span>

                        <span class="step">
                            3. Payment
                        </span>

                    </div>

                </div>


                <div class="order-form">

                    <h3>Delivery Details</h3>


                    <div class="form-group">

                        <label for="orderName">
                            Full Name
                        </label>

                        <input
                            type="text"
                            id="orderName"
                            placeholder="Enter your full name"
                            value="${currentUser.name || ""}"
                        >

                    </div>


                    <div class="form-group">

                        <label for="orderEmail">
                            Email
                        </label>

                        <input
                            type="email"
                            id="orderEmail"
                            placeholder="Enter your email"
                            value="${currentUser.email || ""}"
                        >

                    </div>


                    <div class="form-group">

                        <label for="orderPhone">
                            Mobile Number
                        </label>

                        <input
                            type="tel"
                            id="orderPhone"
                            placeholder="Enter your mobile number"
                            value="${currentUser.phone || ""}"
                        >

                    </div>


                    <div class="form-group">

                        <label for="orderAddress">
                            Delivery Address
                        </label>

                        <textarea
                            id="orderAddress"
                            placeholder="Enter your complete delivery address"
                            rows="4"
                        >${currentUser.address || ""}</textarea>

                    </div>


                    <button
                        type="button"
                        class="btn-primary"
                        onclick="saveOrderDetails()"
                    >
                        Continue to Order Summary
                    </button>

                </div>

            </div>
        `;

        return;
    }


    // ==================================================
    // STEP 2 — ORDER SUMMARY
    // ==================================================

    if (currentOrderSteps === 2) {

        orderSteps.innerHTML = `

            <div class="order-container">

                <div class="order-header">

                    <h2>Order Summary</h2>

                    <div class="checkout-steps">

                        <span class="step completed">
                            1. Details
                        </span>

                        <span class="step active">
                            2. Summary
                        </span>

                        <span class="step">
                            3. Payment
                        </span>

                    </div>

                </div>


                <div class="order-summary">

                    <h3>Your Items</h3>

                    <div class="order-items">

                        ${cart.map(item => `

                            <div class="order-item">

                                <img
                                    src="${getImagePath(item.image)}"
                                    alt="${item.name || "Product"}"
                                >

                                <div class="order-item-info">

                                    <h4>
                                        ${item.name || "Product"}
                                    </h4>

                                    <p>
                                        Quantity:
                                        ${Number(item.quantity || 0)}
                                    </p>

                                    <p>
                                        Price:
                                        ₹${Number(item.price || 0)}
                                    </p>

                                </div>

                                <strong>
                                    ₹${
            Number(item.price || 0) *
            Number(item.quantity || 0)
        }
                                </strong>

                            </div>

                        `).join("")}

                    </div>


                    <div class="price-summary">

                        <div class="summary-row">

                            <span>
                                Subtotal
                            </span>

                            <span>
                                ₹${cartTotal}
                            </span>

                        </div>


                        <div class="summary-row">

                            <span>
                                Delivery Charges
                            </span>

                            <span>
                                ${
            deliveryCharges === 0
                ? "FREE"
                : "₹" + deliveryCharges
        }
                            </span>

                        </div>


                        <div class="summary-row total">

                            <span>
                                Total
                            </span>

                            <strong>
                                ₹${finalTotal}
                            </strong>

                        </div>

                    </div>


                    <div class="order-actions">

                        <button
                            type="button"
                            class="btn-secondary"
                            onclick="
                                currentOrderSteps = 1;
                                renderOrderSteps();
                            "
                        >
                            Back
                        </button>


                        <button
                            type="button"
                            class="btn-primary"
                            onclick="
                                currentOrderSteps = 3;
                                renderOrderSteps();
                            "
                        >
                            Continue to Payment
                        </button>

                    </div>

                </div>

            </div>
        `;

        return;
    }


    // ==================================================
    // STEP 3 — PAYMENT
    // ==================================================

    if (currentOrderSteps === 3) {

        orderSteps.innerHTML = `

            <div class="order-container">

                <div class="order-header">

                    <h2>Payment</h2>

                    <div class="checkout-steps">

                        <span class="step completed">
                            1. Details
                        </span>

                        <span class="step completed">
                            2. Summary
                        </span>

                        <span class="step active">
                            3. Payment
                        </span>

                    </div>

                </div>


                <div class="payment-section">

                    <h3>
                        Select Payment Method
                    </h3>


                    <label class="payment-option">

                        <input
                            type="radio"
                            name="payment"
                            value="Cash on Delivery"
                            checked
                        >

                        <span>
                            Cash on Delivery
                        </span>

                    </label>


                    <label class="payment-option">

                        <input
                            type="radio"
                            name="payment"
                            value="Online Payment"
                        >

                        <span>
                            Online Payment
                        </span>

                    </label>


                    <div class="final-summary">

                        <div class="summary-row">

                            <span>
                                Subtotal
                            </span>

                            <span>
                                ₹${cartTotal}
                            </span>

                        </div>


                        <div class="summary-row">

                            <span>
                                Delivery
                            </span>

                            <span>
                                ${
            deliveryCharges === 0
                ? "FREE"
                : "₹" + deliveryCharges
        }
                            </span>

                        </div>


                        <div class="summary-row total">

                            <span>
                                Amount to Pay
                            </span>

                            <strong>
                                ₹${finalTotal}
                            </strong>

                        </div>

                    </div>


                    <div class="order-actions">

                        <button
                            type="button"
                            class="btn-secondary"
                            onclick="
                                currentOrderSteps = 2;
                                renderOrderSteps();
                            "
                        >
                            Back
                        </button>


                        <button
                            type="button"
                            class="btn-primary"
                            onclick="placeOrder()"
                        >
                            Place Order
                        </button>

                    </div>

                </div>

            </div>
        `;

        return;
    }
}
/* =========================================================
   FOOTER
========================================================= */

function initFooter() {

    // Footer newsletter subscription
    const newsletterForm =
        document.querySelector(".newsletter-form");

    if (!newsletterForm) {
        return;
    }

    const emailInput =
        newsletterForm.querySelector("input[type='email']");

    const subscribeBtn =
        newsletterForm.querySelector("button");

    if (!emailInput || !subscribeBtn) {
        return;
    }

    subscribeBtn.addEventListener("click", function () {

        const email =
            emailInput.value.trim();

        if (!email) {
            alert("Please enter your email address.");
            emailInput.focus();
            return;
        }

        const emailPattern =
            /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailPattern.test(email)) {
            alert("Please enter a valid email address.");
            emailInput.focus();
            return;
        }

        let subscribers =
            JSON.parse(
                localStorage.getItem("rishtaBoxSubscribers")
            ) || [];

        if (subscribers.includes(email)) {
            alert("You are already subscribed!");
            return;
        }

        subscribers.push(email);

        localStorage.setItem(
            "rishtaBoxSubscribers",
            JSON.stringify(subscribers)
        );

        emailInput.value = "";

        alert(
            "🎁 Thank you for subscribing to RishtaBox!"
        );
    });
}


/* =========================================================
   FOOTER LINKS
========================================================= */

function footerHome() {
    showPage("home");
    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}

function footerShop() {
    showPage("category");
    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}

function footerCart() {
    showPage("cart");
    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}

function footerOrders() {
    showPage("orders");
    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}

function footerAccount() {
    showPage("account");
    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}


/* =========================================================
   INITIALIZE FOOTER
========================================================= */

document.addEventListener(
    "DOMContentLoaded",
    function () {
        initFooter();
    }
);


/* =========================================================
   PANEL / SECONDARY NAVIGATION
========================================================= */

function initGiftPanel() {

    const panelAll = document.querySelector(".panel-all");
    const panelOptions =
        document.querySelectorAll(".panel-ops p");
    const panelDeals =
        document.querySelector(".panel-deals");

    /* All Gifts */
    if (panelAll) {
        panelAll.addEventListener("click", function () {

            showPage("category");

            // Show all products
            if (typeof showCategory === "function") {
                showCategory("all");
            }

            window.scrollTo({
                top: 0,
                behavior: "smooth"
            });
        });
    }


    /* Gift Categories */
    panelOptions.forEach(function (option) {

        option.addEventListener("click", function () {

            const categoryName =
                option.textContent.trim();

            // Category mapping
            const categoryMap = {
                "Birthday": "birthday",
                "Anniversary": "anniversary",
                "Wedding": "wedding",
                "For Couples": "couples",
                "Personalized": "personalized"
            };

            const categoryId =
                categoryMap[categoryName];

            if (!categoryId) {
                return;
            }

            showPage("category");

            if (typeof showCategory === "function") {
                showCategory(categoryId);
            }

            window.scrollTo({
                top: 0,
                behavior: "smooth"
            });
        });
    });


    /* Special Offers */
    if (panelDeals) {

        panelDeals.style.cursor = "pointer";

        panelDeals.addEventListener("click", function () {

            showPage("category");

            if (typeof showCategory === "function") {
                showCategory("special-offers");
            }

            window.scrollTo({
                top: 0,
                behavior: "smooth"
            });
        });
    }
}


/* =========================================================
   INITIALIZE PANEL
========================================================= */

document.addEventListener(
    "DOMContentLoaded",
    function () {
        initGiftPanel();
    }
);



/* =========================================================
   SHOW FESTIVAL
========================================================= */
function renderFestivals() {

    const festivalGrid = document.getElementById("festivalGrid");

    if (!festivalGrid) return;

    festivalGrid.innerHTML = "";

    festivals.forEach(festival => {

        const festivalCard = document.createElement("div");

        // EXACT SAME CLASS AS CATEGORY CARD
        festivalCard.className = "category-card";

        // EXACT SAME STRUCTURE AS CATEGORY CARD
        festivalCard.innerHTML = `
            <img 
                src="${getImagePath(festival.image)}"
                alt="${festival.name}"
            >

            <div class="category-card-content">

                <h3>${festival.name}</h3>

                <p>${festival.description}</p>

            </div>

            <a 
                href="#" 
                class="category-btn festival-btn"
                data-festival-id="${festival.id}"
            >
                View Products
            </a>
        `;

        festivalGrid.appendChild(festivalCard);
    });


    // Festival button click
    document
        .querySelectorAll("#festivalGrid .festival-btn")
        .forEach(button => {

            button.addEventListener("click", function(event) {

                event.preventDefault();
                event.stopPropagation();

                const festivalId =
                    this.getAttribute("data-festival-id");

                showFestival(festivalId);

            });

        });
}
async function loadFestivals() {
    try {
        const response = await fetch(
            "http://localhost:8080/api/festivals"
        );

        if (!response.ok) {
            throw new Error("Failed to load festivals");
        }

        festivals = await response.json();

        renderFestivals();

    } catch (error) {
        console.error("Festival loading error:", error);
    }
}
function showFestival(festivalId) {

    const festival = festivals.find(
        festival =>
            String(festival.id).toLowerCase() ===
            String(festivalId).toLowerCase()
    );

    if (!festival) {
        console.error("Festival not found:", festivalId);
        return;
    }

    filteredProducts = products.filter(function(product) {

        return product.festival != null &&
            String(product.festival).toLowerCase() ===
            String(festivalId).toLowerCase();

    });

    console.log("Selected festival:", festivalId);
    console.log("Festival Products:", filteredProducts);

    const categoryTitle =
        document.getElementById("categoryTitle");

    if (categoryTitle) {
        categoryTitle.textContent = festival.name;
    }

    showPage("category");

    renderProducts(filteredProducts);
}
document.addEventListener("DOMContentLoaded", function () {

    loadCategories();
    loadFestivals();

});

function renderTestimonials() {

    const container = document.getElementById("testimonialsContainer");

    if (!container) return;

    const testimonials = [
        {
            name: "Rahul Sharma",
            rating: 5,
            image: "https://i.pravatar.cc/100?img=12",
            message: "The groom mala was beautiful and the quality was excellent. Highly recommended!"
        },
        {
            name: "Priya Singh",
            rating: 5,
            image: "https://i.pravatar.cc/100?img=47",
            message: "I ordered a personalized mug for my anniversary. It was exactly as shown in the picture."
        },
        {
            name: "Amit Kumar",
            rating: 4,
            image: "https://i.pravatar.cc/100?img=33",
            message: "Good product quality, reasonable prices and quick delivery."
        }
    ];

    container.innerHTML = testimonials.map(testimonial => {

        const stars =
            "★".repeat(testimonial.rating) +
            "☆".repeat(5 - testimonial.rating);

        return `
            <div class="testimonial-card">

                <div class="customer-info">

                    <img 
                        src="${getImagePath(testimonial.image)}"
                        alt="${testimonial.name}"
                    >

                    <div>
                        <h3>${testimonial.name}</h3>

                        <div class="rating">
                            ${stars}
                        </div>
                    </div>

                </div>

                <p class="testimonial-text">
                    "${testimonial.message}"
                </p>

                <span class="verified">
                    ✓ Verified Customer
                </span>

            </div>
        `;

    }).join("");
}


function renderBlogs() {

    const container = document.getElementById("blogsContainer");

    if (!container) return;

    const blogs = [
        {
            title: "10 Best Wedding Gift Ideas for Couples",
            date: "September 10, 2026",
            image: "https://images.unsplash.com/photo-1519225421980-715cb0215aed?w=800",
            description: "Discover thoughtful and memorable wedding gifts for newly married couples."
        },
        {
            title: "Best Personalized Gifts for Your Loved Ones",
            date: "September 8, 2026",
            image: "https://images.unsplash.com/photo-1512909006721-3d6018887383?w=800",
            description: "Make your special moments memorable with beautiful personalized gifts."
        },
        {
            title: "Best Birthday Gifts for Someone Special",
            date: "September 5, 2026",
            image: "https://images.unsplash.com/photo-1464349153735-7db50ed83c84?w=800",
            description: "Find unique and thoughtful birthday gift ideas for your loved ones."
        }
    ];

    container.innerHTML = blogs.map(blog => {

        return `
            <div class="blog-card">

                <img 
                    src="${getImagePath(blog.image)}"
                    alt="${blog.title}"
                >

                <div class="blog-content">

                    <span class="blog-date">
                        ${blog.date}
                    </span>

                    <h3>
                        ${blog.title}
                    </h3>

                    <p>
                        ${blog.description}
                    </p>

                    <button 
                        class="read-more-btn"
                        onclick="readBlog('${blog.title}')"
                    >
                        Read More →
                    </button>

                </div>

            </div>
        `;

    }).join("");
}


/* Read More */

function readBlog(title) {

    alert(
        "Opening Blog: " + title
    );

}


/* Load Sections */

document.addEventListener("DOMContentLoaded", function () {

    renderTestimonials();

    renderBlogs();

});
function showBlogDetails(id) {

    const blog = blogDetails[id];

    if (!blog) return;

    const blogSection = document.querySelector(".blog-section");

    const detailsSection =
        document.getElementById("blogDetailsSection");

    const detailsContent =
        document.getElementById("blogDetailsContent");


    detailsContent.innerHTML = `

        <button 
            class="back-to-blog-btn"
            onclick="closeBlogDetails()">
            ← Back to Blogs
        </button>

        <img 
            src="${blog.image}"
            alt="${blog.title}"
            class="blog-details-image"
        >

        <span class="blog-details-date">
            ${blog.date}
        </span>

        <h1 class="blog-details-title">
            ${blog.title}
        </h1>

        <div class="blog-details-text">
            ${blog.content}
        </div>

    `;


    blogSection.style.display = "none";

    detailsSection.style.display = "block";

    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}


function closeBlogDetails() {

    document.getElementById("blogDetailsSection").style.display = "none";

    document.querySelector(".blog-section").style.display = "block";

    document.querySelector(".blog-section").scrollIntoView({
        behavior: "smooth"
    });
}
const blogDetails = {

    1: {
        title: "10 Best Wedding Gift Ideas for Couples",
        date: "September 10, 2026",
        image: "https://images.unsplash.com/photo-1519225421980-715cb0215aed?w=800",

        content: `
            <p>
                Finding the perfect wedding gift can be difficult,
                especially when you want to give something meaningful
                and memorable. A good wedding gift should celebrate
                the couple and their new journey together.
            </p>

            <h2>1. Personalized Gifts</h2>

            <p>
                Personalized gifts are one of the best choices for
                newly married couples. Customized mugs, photo frames,
                cushions and other personalized products can make
                the gift more special and emotional.
            </p>

            <h2>2. Couple Gifts</h2>

            <p>
                Couple gifts are another great option. You can choose
                matching products or something that the couple can
                use together in their everyday life.
            </p>

            <h2>3. Wedding Accessories</h2>

            <p>
                Wedding accessories can also be a thoughtful gift.
                Choose beautiful accessories that match the couple's
                style and make their wedding memories even more special.
            </p>

            <h2>Conclusion</h2>

            <p>
                The best wedding gift is not always the most expensive
                one. A thoughtful and meaningful gift can make the
                couple feel special and create memories that last for
                years.
            </p>
        `
    },


    2: {
        title: "Best Personalized Gifts for Your Loved Ones",
        date: "September 8, 2026",
        image: "https://images.unsplash.com/photo-1512909006721-3d6018887383?w=800",

        content: `
            <p>
                Personalized gifts are a wonderful way to show someone
                that you care. Adding a name, photo or special message
                can turn an ordinary gift into something truly unique.
            </p>

            <h2>Why Choose Personalized Gifts?</h2>

            <p>
                Personalized gifts have an emotional value because
                they are created especially for the person receiving
                them. They are perfect for birthdays, anniversaries,
                weddings and other special occasions.
            </p>

            <h2>Personalized Mugs</h2>

            <p>
                Personalized coffee mugs are simple, useful and
                memorable gifts. You can add photographs, names or
                special messages to make them unique.
            </p>

            <h2>Photo Gifts</h2>

            <p>
                Photo-based gifts are another excellent choice.
                A memorable photograph can be turned into a beautiful
                gift that reminds someone of a special moment.
            </p>

            <h2>Conclusion</h2>

            <p>
                If you want to give something meaningful, personalized
                gifts are an excellent choice because they combine
                creativity with personal memories.
            </p>
        `
    },


    3: {
        title: "Best Birthday Gifts for Someone Special",
        date: "September 5, 2026",
        image: "https://images.unsplash.com/photo-1464349153735-7db50ed83c84?w=800",

        content: `
            <p>
                Birthdays are a perfect opportunity to make someone
                feel special. Choosing the right gift can make the
                celebration even more memorable.
            </p>

            <h2>1. Personalized Gifts</h2>

            <p>
                Personalized gifts are always a great birthday choice.
                You can choose a customized mug, photo frame or another
                gift that includes the person's name or photograph.
            </p>

            <h2>2. Useful Gifts</h2>

            <p>
                Useful gifts are practical and can be enjoyed for a
                long time. Choose something that matches the person's
                interests and daily needs.
            </p>

            <h2>3. Special Memories</h2>

            <p>
                Gifts connected with memories can be more meaningful
                than expensive products. Photos and customized items
                can help preserve special moments.
            </p>

            <h2>Conclusion</h2>

            <p>
                The best birthday gift is something selected with
                thought and care. A meaningful gift can make the
                birthday celebration unforgettable.
            </p>
        `
    }

};


// ===============================
// LOAD RELATIONSHIPS FROM BACKEND
// ===============================

async function loadRelationships() {

    try {

        const response = await fetch(
            "http://localhost:8080/api/relationships"
        );

        if (!response.ok) {
            throw new Error("Failed to load relationships");
        }

        relationships = await response.json();

        console.log("Relationships loaded:", relationships);

        renderRelationships();

    } catch (error) {

        console.error(
            "Relationship loading error:",
            error
        );

    }
}

// ===============================
// RENDER RELATIONSHIPS
// ===============================

function renderRelationships() {

    const relationshipGrid =
        document.getElementById("relationshipGrid");

    if (!relationshipGrid) {
        return;
    }

    relationshipGrid.innerHTML = "";

    relationships.forEach(function (relationship) {

        const card = document.createElement("div");

        card.className = "relationship-card";

        card.innerHTML = `
            <img 
               src="${getImagePath(relationship.image)}"
                alt="${relationship.name}"
            >

            <div class="relationship-card-content">

                <h3>${relationship.name}</h3>

                <p>${relationship.description}</p>
      <button 
            class="view-product-btn"
            onclick="event.stopPropagation(); showRelationshipProducts('${relationship.id}')">
            View Products
        </button>
            </div>
        `;

        card.addEventListener("click", function () {

            showRelationshipProducts(relationship.id);

        });

        relationshipGrid.appendChild(card);

    });
}
// ===============================
// RELATIONSHIP CLICK
// ===============================

function showRelationshipProducts(relationshipId) {

    console.log("Selected relationship:", relationshipId);

    // Filter products coming from MySQL backend
    filteredProducts = products.filter(function(product) {

        return product.relationship &&
            String(product.relationship).toLowerCase() ===
            String(relationshipId).toLowerCase();

    });

    console.log("Relationship Products:", filteredProducts);

    const relationship = relationships.find(function(r) {

        return String(r.id).toLowerCase() ===
            String(relationshipId).toLowerCase();

    });

    const categoryTitle =
        document.getElementById("categoryTitle");

    if (categoryTitle) {

        if (relationship) {
            categoryTitle.textContent = relationship.name;
        } else {
            categoryTitle.textContent =
                "Gifts for " + relationshipId;
        }

    }

    populateFilters();

    showPage("category");

    applyFilters();

}

// ===============================
// LOAD RELATIONSHIPS
// ===============================

document.addEventListener("DOMContentLoaded", function () {

    loadRelationships();

});

/* =========================================================
   RISTHABOX LOGIN / SIGNUP SYSTEM
========================================================= */


/* =========================
   OPEN ACCOUNT
========================= */

function openAccount() {

    const loggedIn =
        localStorage.getItem("rishtaBoxLoggedIn");

    if (loggedIn === "true") {

        showPage("account");

        loadLoggedInUser();

    } else {

        showPage("login");

        showLogin();

    }
}


/* =========================
   SHOW LOGIN
========================= */

function showLogin() {

    const loginSection =
        document.getElementById("loginSection");

    const signupSection =
        document.getElementById("signupSection");

    if (!loginSection || !signupSection) {
        return;
    }

    loginSection.classList.remove("hidden");

    signupSection.classList.add("hidden");

    clearAuthMessages();
}


/* =========================
   SHOW SIGNUP
========================= */

function showSignup() {

    const loginSection =
        document.getElementById("loginSection");

    const signupSection =
        document.getElementById("signupSection");

    if (!loginSection || !signupSection) {
        return;
    }

    loginSection.classList.add("hidden");

    signupSection.classList.remove("hidden");

    clearAuthMessages();
}


/* =========================
   PASSWORD SHOW / HIDE
========================= */

function togglePassword(inputId, button) {

    const input =
        document.getElementById(inputId);

    if (!input) {
        return;
    }

    if (input.type === "password") {

        input.type = "text";

        button.textContent = "Hide";

    } else {

        input.type = "password";

        button.textContent = "Show";
    }
}


/* =========================
   SIGNUP
========================= */

const signupForm =
    document.getElementById("signupForm");


if (signupForm) {

    signupForm.addEventListener(
        "submit",
        function (event) {

            event.preventDefault();


            const name =
                document.getElementById("signupName")
                    .value
                    .trim();


            const mobile =
                document.getElementById("signupMobile")
                    .value
                    .trim();


            const email =
                document.getElementById("signupEmail")
                    .value
                    .trim();


            const password =
                document.getElementById("signupPassword")
                    .value;


            const confirmPassword =
                document.getElementById("confirmPassword")
                    .value;


            const message =
                document.getElementById("signupMessage");


            message.textContent = "";

            message.className =
                "auth-message";


            /* Name */

            if (name.length < 2) {

                showSignupError(
                    "Please enter your full name."
                );

                return;
            }


            /* Mobile */

            if (!/^[6-9]\d{9}$/.test(mobile)) {

                showSignupError(
                    "Please enter a valid 10-digit mobile number."
                );

                return;
            }


            /* Email */

            const emailPattern =
                /^[^\s@]+@[^\s@]+\.[^\s@]+$/;


            if (!emailPattern.test(email)) {

                showSignupError(
                    "Please enter a valid email address."
                );

                return;
            }


            /* Password */

            if (password.length < 6) {

                showSignupError(
                    "Password must contain at least 6 characters."
                );

                return;
            }


            /* Confirm Password */

            if (password !== confirmPassword) {

                showSignupError(
                    "Passwords do not match."
                );

                return;
            }


            /* Get users */

            const users =
                JSON.parse(
                    localStorage.getItem(
                        "rishtaBoxUsers"
                    )
                ) || [];


            /* Existing email */

            const emailExists =
                users.some(function (user) {

                    return user.email.toLowerCase() ===
                        email.toLowerCase();

                });


            if (emailExists) {

                showSignupError(
                    "This email is already registered."
                );

                return;
            }


            /* Existing mobile */

            const mobileExists =
                users.some(function (user) {

                    return user.mobile === mobile;

                });


            if (mobileExists) {

                showSignupError(
                    "This mobile number is already registered."
                );

                return;
            }


            /* Create user */

            const newUser = {

                id: Date.now(),

                name: name,

                mobile: mobile,

                email: email,

                password: password

            };


            users.push(newUser);


            localStorage.setItem(
                "rishtaBoxUsers",
                JSON.stringify(users)
            );


            /* Success */

            message.textContent =
                "Account created successfully! ❤️";

            message.className =
                "auth-message success";


            signupForm.reset();


            /* Open login */

            setTimeout(function () {

                showLogin();

                document.getElementById(
                    "loginEmail"
                ).value = email;

            }, 1200);

        }
    );

}


/* =========================
   LOGIN
========================= */

const loginForm =
    document.getElementById("loginForm");

//
// if (loginForm) {
//
//     loginForm.addEventListener(
//         "submit",
//         function (event) {
//
//             event.preventDefault();
//
//
//             const loginValue =
//                 document.getElementById("loginEmail")
//                 .value
//                 .trim();
//
//
//             const password =
//                 document.getElementById("loginPassword")
//                 .value;
//
//
//             const message =
//                 document.getElementById("loginMessage");
//
//
//             message.textContent = "";
//
//             message.className =
//                 "auth-message";
//
//
//             if (loginValue === "") {
//
//                 showLoginError(
//                     "Please enter your email or mobile number."
//                 );
//
//                 return;
//             }
//
//
//             if (password === "") {
//
//                 showLoginError(
//                     "Please enter your password."
//                 );
//
//                 return;
//             }
//
//
//             /* Get users */
//
//             const users =
//                 JSON.parse(
//                     localStorage.getItem(
//                         "rishtaBoxUsers"
//                     )
//                 ) || [];
//
//
//             if (users.length === 0) {
//
//                 showLoginError(
//                     "No account found. Please create an account first."
//                 );
//
//                 return;
//             }
//
//
//             /* Find user */
//
//             const user =
//                 users.find(function (item) {
//
//                     return (
//
//                         item.email.toLowerCase() ===
//                         loginValue.toLowerCase()
//
//                         ||
//
//                         item.mobile ===
//                         loginValue
//
//                     );
//
//                 });
//
//
//             if (!user) {
//
//                 showLoginError(
//                     "Account not found. Please check your details."
//                 );
//
//                 return;
//             }
//
//
//             /* Password */
//
//             if (user.password !== password) {
//
//                 showLoginError(
//                     "Incorrect password. Please try again."
//                 );
//
//                 return;
//             }
//
//
//             /* Login */
//
//             localStorage.setItem(
//                 "rishtaBoxLoggedIn",
//                 "true"
//             );
//
//
//             localStorage.setItem(
//                 "rishtaBoxCurrentUser",
//                 JSON.stringify(user)
//             );
//
//
//             /* Success */
//
//             message.textContent =
//                 "Welcome back, " +
//                 user.name +
//                 "! ❤️";
//
//             message.className =
//                 "auth-message success";
//
//
//             /* Update account */
//
//             updateAccountNav(user);
//
//
//             /* Go home */
//
//             setTimeout(function () {
//
//                 showPage("home");
//
//             }, 900);
//
//         }
//     );
//
// }


/* =========================
   UPDATE ACCOUNT NAV
========================= */

function updateAccountNav(user) {

    const accountText =
        document.getElementById(
            "accountNavText"
        );

    if (!accountText) {
        return;
    }


    if (user) {

        accountText.textContent =
            user.name;

    } else {

        accountText.textContent =
            "Account";
    }
}


/* =========================
   LOAD LOGGED USER
========================= */

function loadLoggedInUser() {

    const savedUser = localStorage.getItem("userData");

    console.log("Saved userData:", savedUser);

    if (!savedUser) {
        console.log("No user found in localStorage");
        return;
    }

    const user = JSON.parse(savedUser);

    console.log("Loaded user:", user);
    console.log("Loaded User ID:", user.id);

    currentUser = user;

    updateAccountNav(user);

    const name = document.getElementById("userName");
    const email = document.getElementById("userEmail");
    const phone = document.getElementById("userPhone");

    if (name) name.value = user.name || "";
    if (email) email.value = user.email || "";
    if (phone) phone.value = user.mobile || user.phone || "";
}


/* =========================
   LOGIN ERROR
========================= */

function showLoginError(message) {

    const element =
        document.getElementById(
            "loginMessage"
        );

    if (!element) {
        return;
    }

    element.textContent = message;

    element.className =
        "auth-message error";
}


/* =========================
   SIGNUP ERROR
========================= */

function showSignupError(message) {

    const element =
        document.getElementById(
            "signupMessage"
        );

    if (!element) {
        return;
    }

    element.textContent = message;

    element.className =
        "auth-message error";
}


/* =========================
   CLEAR MESSAGES
========================= */

function clearAuthMessages() {

    const loginMessage =
        document.getElementById(
            "loginMessage"
        );

    const signupMessage =
        document.getElementById(
            "signupMessage"
        );


    if (loginMessage) {

        loginMessage.textContent = "";

        loginMessage.className =
            "auth-message";
    }


    if (signupMessage) {

        signupMessage.textContent = "";

        signupMessage.className =
            "auth-message";
    }
}


/* =========================
   FORGOT PASSWORD
========================= */

function forgotPassword(event) {

    event.preventDefault();

    alert(
        "Password recovery will be connected to email/OTP later."
    );
}


/* =========================
   INITIAL LOGIN STATE
========================= */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        const loggedIn =
            localStorage.getItem(
                "rishtaBoxLoggedIn"
            );


        const user =
            JSON.parse(
                localStorage.getItem(
                    "rishtaBoxCurrentUser"
                )
            );


        if (
            loggedIn === "true"
            &&
            user
        ) {

            updateAccountNav(user);

        } else {

            updateAccountNav(null);

        }

    }
);async function payWithRazorpay(backendOrderId, orderData) {

    console.log("===== RAZORPAY PAYMENT START =====");
    console.log("Backend Order ID:", backendOrderId);

    // =====================================================
    // 1. CHECK RAZORPAY
    // =====================================================

    if (typeof Razorpay === "undefined") {

        alert(
            "Razorpay failed to load. Please check your internet connection."
        );

        return;
    }

    if (!backendOrderId) {

        alert("Order ID not found.");

        return;
    }


    // =====================================================
    // 2. TOKEN
    // =====================================================

    const token = localStorage.getItem("token");

    if (!token) {

        alert("Please login again.");

        return;
    }


    // =====================================================
    // 3. SAVE CART SNAPSHOT
    // =====================================================

    const paidCart = Array.isArray(cart)
        ? cart.map(item => ({ ...item }))
        : [];


    console.log("Paid Cart Snapshot:", paidCart);


    if (paidCart.length === 0) {

        alert("Cart is empty.");

        return;
    }


    try {

        // =====================================================
        // 4. CREATE RAZORPAY ORDER
        // =====================================================

        console.log(
            "Creating Razorpay order for DB Order:",
            backendOrderId
        );


        const paymentOrderResponse = await fetch(
            "http://localhost:8080/api/payments/create-order",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },

                body: JSON.stringify({
                    orderId: Number(backendOrderId)
                })
            }
        );


        const paymentOrderText =
            await paymentOrderResponse.text();


        let paymentOrder = {};


        try {

            paymentOrder =
                JSON.parse(paymentOrderText);

        } catch (e) {

            console.error(
                "Payment order response is not JSON:",
                paymentOrderText
            );
        }


        console.log(
            "Create Payment Status:",
            paymentOrderResponse.status
        );


        console.log(
            "Create Payment Response:",
            paymentOrder
        );


        if (!paymentOrderResponse.ok) {

            alert(
                "Could not create Razorpay payment: " +
                (
                    paymentOrder.message ||
                    paymentOrder.error ||
                    paymentOrderText ||
                    "Payment order creation failed."
                )
            );

            return;
        }


        // =====================================================
        // 5. GET RAZORPAY ORDER ID
        // =====================================================

        const razorpayOrderId =
            paymentOrder.razorpayOrderId;


        if (!razorpayOrderId) {

            alert(
                "Razorpay Order ID was not received from backend."
            );


            console.error(
                "Missing Razorpay Order ID:",
                paymentOrder
            );


            return;
        }


        console.log(
            "Razorpay Order ID:",
            razorpayOrderId
        );


        // =====================================================
        // 6. PAYMENT AMOUNT
        // =====================================================

        const razorpayAmount =
            Number(paymentOrder.amount);


        if (
            Number.isNaN(razorpayAmount) ||
            razorpayAmount <= 0
        ) {

            alert(
                "Invalid payment amount received from backend."
            );

            console.error(
                "Invalid Razorpay amount:",
                paymentOrder.amount
            );

            return;
        }


        console.log(
            "Razorpay Amount:",
            razorpayAmount
        );


        // =====================================================
        // 7. RAZORPAY CHECKOUT OPTIONS
        // =====================================================

        const options = {

            // Your TEST key
            key: "rzp_test_TcGIuj6KWkDynr",

            // Razorpay expects paise
            amount:
                Math.round(
                    razorpayAmount * 100
                ),

            currency: "INR",

            name: "RishtaBox",

            description:
                "RishtaBox Order",

            order_id:
            razorpayOrderId,


            // =================================================
            // 8. PAYMENT SUCCESS
            // =================================================

            handler: async function (response) {

                console.log(
                    "===== RAZORPAY PAYMENT SUCCESS ====="
                );


                console.log(
                    "Razorpay Response:",
                    response
                );


                // =============================================
                // CHECK RESPONSE
                // =============================================

                if (
                    !response ||
                    !response.razorpay_order_id ||
                    !response.razorpay_payment_id ||
                    !response.razorpay_signature
                ) {

                    console.error(
                        "Invalid Razorpay success response:",
                        response
                    );


                    alert(
                        "Payment response is incomplete. Please contact support."
                    );


                    return;
                }


                // =============================================
                // 9. VERIFY PAYMENT
                // =============================================

                try {

                    console.log(
                        "Sending payment verification to backend..."
                    );


                    const verifyResponse =
                        await fetch(
                            "http://localhost:8080/api/payments/verify",
                            {
                                method: "POST",

                                headers: {
                                    "Content-Type":
                                        "application/json",

                                    "Authorization":
                                        `Bearer ${token}`
                                },

                                body: JSON.stringify({

                                    orderId:
                                        Number(backendOrderId),

                                    razorpayOrderId:
                                    response.razorpay_order_id,

                                    razorpayPaymentId:
                                    response.razorpay_payment_id,

                                    razorpaySignature:
                                    response.razorpay_signature
                                })
                            }
                        );


                    const verifyText =
                        await verifyResponse.text();


                    let verifyData = {};


                    try {

                        verifyData =
                            JSON.parse(verifyText);

                    } catch (e) {

                        console.error(
                            "Verify response is not JSON:",
                            verifyText
                        );
                    }


                    console.log(
                        "Verify Status:",
                        verifyResponse.status
                    );


                    console.log(
                        "Verify Response:",
                        verifyData
                    );


                    // =========================================
                    // VERIFICATION FAILED
                    // =========================================

                    if (!verifyResponse.ok) {

                        console.error(
                            "BACKEND PAYMENT VERIFICATION FAILED"
                        );


                        alert(
                            "Payment verification failed: " +
                            (
                                verifyData.message ||
                                verifyData.error ||
                                verifyText ||
                                "Payment verification failed."
                            )
                        );


                        return;
                    }


                    // =========================================
                    // PAYMENT VERIFIED
                    // =========================================

                    console.log(
                        "===================================="
                    );


                    console.log(
                        "PAYMENT VERIFIED SUCCESSFULLY"
                    );


                    console.log(
                        "Database Order ID:",
                        backendOrderId
                    );


                    console.log(
                        "Razorpay Payment ID:",
                        response.razorpay_payment_id
                    );


                    console.log(
                        "===================================="
                    );


                    // =================================================
                    // 10. CREATE FRONTEND ORDER
                    // =================================================

                    console.log(
                        "Creating frontend order..."
                    );


                    const frontendOrderId =
                        "ORD" +
                        Date.now() +
                        Math.floor(
                            Math.random() * 1000
                        );


                    const orderDate =
                        new Date();


                    const deliveryDate =
                        new Date();


                    deliveryDate.setDate(
                        deliveryDate.getDate() + 7
                    );


                    // =================================================
                    // 11. CALCULATE TOTAL
                    // =================================================

                    const cartTotal =
                        paidCart.reduce(
                            (total, item) => {

                                const price =
                                    Number(item.price || 0);

                                const quantity =
                                    Number(item.quantity || 0);

                                return total +
                                    (price * quantity);
                            },

                            0
                        );


                    const deliveryCharges =
                        cartTotal >= 500
                            ? 0
                            : 50;


                    const finalTotal =
                        cartTotal +
                        deliveryCharges;


                    console.log(
                        "Cart Total:",
                        cartTotal
                    );


                    console.log(
                        "Delivery Charges:",
                        deliveryCharges
                    );


                    console.log(
                        "Final Total:",
                        finalTotal
                    );


                    // =================================================
                    // 12. CUSTOMER DETAILS SAFELY
                    // =================================================

                    const customerName =
                        currentUser?.name || "";

                    const customerEmail =
                        currentUser?.email || "";

                    const customerPhone =
                        currentUser?.phone || "";

                    const customerAddress =
                        currentUser?.address || "";


                    // =================================================
                    // 13. FRONTEND ORDER OBJECT
                    // =================================================

                    const frontendOrder = {

                        id:
                        frontendOrderId,

                        backendOrderId:
                        backendOrderId,

                        items:
                            paidCart.map(item => ({
                                ...item
                            })),

                        total:
                        cartTotal,

                        deliveryCharges:
                        deliveryCharges,

                        finalTotal:
                        finalTotal,

                        paymentMethod:
                            "Online Payment",

                        paymentStatus:
                            "PAID",

                        razorpayOrderId:
                        response.razorpay_order_id,

                        razorpayPaymentId:
                        response.razorpay_payment_id,

                        razorpaySignature:
                        response.razorpay_signature,

                        orderDate:
                            orderDate.toISOString(),

                        deliveryDate:
                            deliveryDate.toISOString(),

                        status:
                            "confirmed",

                        name:
                        customerName,

                        email:
                        customerEmail,

                        phone:
                        customerPhone,

                        address:
                        customerAddress
                    };


                    console.log(
                        "Frontend Order Object:",
                        frontendOrder
                    );


                    // =================================================
                    // 14. SAVE FRONTEND ORDER
                    // =================================================

                    try {

                        if (!Array.isArray(orders)) {

                            orders = [];
                        }


                        orders.push(
                            frontendOrder
                        );


                        if (
                            typeof saveOrdersData ===
                            "function"
                        ) {

                            saveOrdersData();

                        } else {

                            localStorage.setItem(
                                "orders",
                                JSON.stringify(orders)
                            );
                        }


                        console.log(
                            "Frontend order saved successfully."
                        );


                    } catch (orderSaveError) {

                        console.error(
                            "Frontend order save error:",
                            orderSaveError
                        );


                        // Direct localStorage backup
                        try {

                            localStorage.setItem(
                                "orders",
                                JSON.stringify(orders)
                            );


                            console.log(
                                "Order saved using localStorage backup."
                            );

                        } catch (backupError) {

                            console.error(
                                "localStorage order backup failed:",
                                backupError
                            );
                        }
                    }


                    // =================================================
                    // 15. CLEAR BACKEND CART
                    // =================================================

                    try {

                        if (
                            currentUser &&
                            currentUser.id
                        ) {

                            const clearCartResponse =
                                await fetch(
                                    `http://localhost:8080/api/cart/clear/${currentUser.id}`,
                                    {
                                        method: "DELETE",

                                        headers: {
                                            "Authorization":
                                                `Bearer ${token}`
                                        }
                                    }
                                );


                            console.log(
                                "Backend Cart Clear Status:",
                                clearCartResponse.status
                            );


                            if (!clearCartResponse.ok) {

                                console.warn(
                                    "Backend cart could not be cleared."
                                );
                            }


                        } else {

                            console.warn(
                                "Current user ID not available. Backend cart was not cleared."
                            );
                        }


                    } catch (cartError) {

                        console.error(
                            "Backend Cart Clear Error:",
                            cartError
                        );


                        // IMPORTANT:
                        // Payment is already verified.
                        // Cart error must not cancel success.
                    }


                    // =================================================
                    // 16. CLEAR FRONTEND CART
                    // =================================================

                    try {

                        cart = [];


                        if (
                            typeof saveCartData ===
                            "function"
                        ) {

                            saveCartData();

                        } else {

                            localStorage.setItem(
                                "cart",
                                JSON.stringify([])
                            );
                        }


                        if (
                            typeof updateCartCount ===
                            "function"
                        ) {

                            updateCartCount();
                        }


                        console.log(
                            "Frontend cart cleared."
                        );


                    } catch (cartSaveError) {

                        console.error(
                            "Frontend cart clear error:",
                            cartSaveError
                        );


                        // Force localStorage clear
                        try {

                            localStorage.setItem(
                                "cart",
                                JSON.stringify([])
                            );

                        } catch (e) {

                            console.error(
                                "Could not clear local cart:",
                                e
                            );
                        }
                    }


                    // =================================================
                    // 17. SHOW SUCCESS PAGE
                    // =================================================

                    console.log(
                        "Showing order success..."
                    );


                    try {

                        if (
                            typeof showOrderSuccess ===
                            "function"
                        ) {

                            showOrderSuccess(
                                frontendOrderId,
                                "Online Payment",
                                finalTotal,
                                deliveryDate
                            );


                            console.log(
                                "Order success page displayed."
                            );


                        } else {

                            console.error(
                                "showOrderSuccess() function not found."
                            );


                            alert(
                                "Payment successful! Order ID: " +
                                frontendOrderId
                            );
                        }


                    } catch (successError) {

                        console.error(
                            "showOrderSuccess error:",
                            successError
                        );


                        alert(
                            "Payment successful!\n\n" +
                            "Order ID: " +
                            frontendOrderId +
                            "\n\n" +
                            "Please check My Orders."
                        );
                    }


                    console.log(
                        "===== ORDER COMPLETED ====="
                    );
                }


                    // =================================================
                    // ERROR AFTER PAYMENT VERIFICATION
                    // =================================================

                catch (errorAfterPayment) {

                    console.error(
                        "ERROR AFTER PAYMENT VERIFICATION:",
                        errorAfterPayment
                    );


                    console.error(
                        "Error Name:",
                        errorAfterPayment?.name
                    );


                    console.error(
                        "Error Message:",
                        errorAfterPayment?.message
                    );


                    console.error(
                        "Error Stack:",
                        errorAfterPayment?.stack
                    );


                    /*
                     * IMPORTANT:
                     * At this point Razorpay payment was already
                     * verified by backend.
                     *
                     * Therefore DON'T show:
                     * "Payment verification failed"
                     *
                     * Show successful payment message instead.
                     */

                    alert(
                        "Payment successful!\n\n" +
                        "Order ID: " +
                        backendOrderId +
                        "\n\n" +
                        "Please check My Orders."
                    );
                }
            },


            // =====================================================
            // 18. CUSTOMER DETAILS
            // =====================================================

            prefill: {

                name:
                    currentUser?.name || "",

                email:
                    currentUser?.email || "",

                contact:
                    currentUser?.phone || ""
            },


            // =====================================================
            // 19. THEME
            // =====================================================

            theme: {

                color:
                    "#e35486"
            }
        };


        // =====================================================
        // 20. CREATE RAZORPAY INSTANCE
        // =====================================================

        console.log(
            "Creating Razorpay instance..."
        );


        const razorpay =
            new Razorpay(options);


        // =====================================================
        // 21. PAYMENT FAILED
        // =====================================================

        razorpay.on(
            "payment.failed",
            function (response) {

                console.error(
                    "Razorpay Payment Failed:",
                    response
                );


                const description =
                    response?.error?.description ||
                    "Unknown payment error.";


                alert(
                    "Payment failed: " +
                    description
                );
            }
        );


        // =====================================================
        // 22. OPEN RAZORPAY
        // =====================================================

        console.log(
            "Opening Razorpay checkout..."
        );


        razorpay.open();


        console.log(
            "Razorpay checkout opened."
        );


    } catch (error) {

        // =====================================================
        // 23. GENERAL ERROR
        // =====================================================

        console.error(
            "Razorpay Error:",
            error
        );


        console.error(
            "Error Message:",
            error?.message
        );


        console.error(
            "Error Stack:",
            error?.stack
        );


        alert(
            "Could not connect to the payment server.\n\n" +
            "Please make sure Spring Boot is running."
        );
    }
}function showOrderSuccess(
    orderId,
    paymentMethod,
    finalTotal,
    deliveryDate
) {

    console.log("===== ORDER SUCCESS PAGE =====");

    console.log("Order ID:", orderId);
    console.log("Payment Method:", paymentMethod);
    console.log("Total:", finalTotal);
    console.log("Delivery Date:", deliveryDate);


    // ==========================================
    // 1. ORDER ID
    // ==========================================

    const orderIdElement =
        document.getElementById("successOrderId");

    if (orderIdElement) {
        orderIdElement.textContent =
            orderId || "-";
    }


    // ==========================================
    // 2. PAYMENT METHOD
    // ==========================================

    const paymentMethodElement =
        document.getElementById("successPaymentMethod");

    if (paymentMethodElement) {

        paymentMethodElement.textContent =
            paymentMethod || "-";
    }


    // ==========================================
    // 3. TOTAL
    // ==========================================

    const totalElement =
        document.getElementById("successOrderTotal");

    if (totalElement) {

        totalElement.textContent =
            Number(finalTotal || 0).toFixed(2);
    }


    // ==========================================
    // 4. DELIVERY DATE
    // ==========================================

    const deliveryElement =
        document.getElementById("successDeliveryDate");

    if (deliveryElement) {

        const date =
            deliveryDate instanceof Date
                ? deliveryDate
                : new Date(deliveryDate);

        if (!isNaN(date.getTime())) {

            deliveryElement.textContent =
                date.toLocaleDateString(
                    "en-IN",
                    {
                        day: "numeric",
                        month: "long",
                        year: "numeric"
                    }
                );

        } else {

            deliveryElement.textContent =
                "To be confirmed";
        }
    }


    // ==========================================
    // 5. PAYMENT STATUS
    // ==========================================

    const paymentStatusElement =
        document.getElementById("successPaymentStatus");

    if (paymentStatusElement) {

        if (
            paymentMethod &&
            paymentMethod
                .toLowerCase()
                .includes("cash")
        ) {

            paymentStatusElement.textContent =
                "Payment will be collected on delivery";

        } else {

            paymentStatusElement.textContent =
                "Payment Successful";
        }
    }


    // ==========================================
    // 6. HIDE ALL PAGES
    // ==========================================

    document
        .querySelectorAll(".page")
        .forEach(function(page) {

            page.classList.add("hidden");
        });


    // ==========================================
    // 7. GET SUCCESS PAGE
    // ==========================================

    const successPage =
        document.getElementById(
            "orderSuccessPage"
        );


    if (!successPage) {

        console.error(
            "orderSuccessPage not found!"
        );

        alert(
            "Order placed successfully!\n\n" +
            "Order ID: " +
            orderId
        );

        return;
    }


    // ==========================================
    // 8. SHOW SUCCESS PAGE
    // ==========================================

    successPage.classList.remove("hidden");


    // ==========================================
    // 9. SCROLL TOP
    // ==========================================

    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });


    console.log(
        "Congratulations page opened successfully."
    );
}
function trackShipment() {

    const courier = document.getElementById("trackingCourier").value;

    if (courier === "shiprocket") {

        window.open(
            "https://www.shiprocket.in/shipment-tracking/",
            "_blank"
        );

    } else if (courier === "goswift") {

        alert(
            "GoSwift Tracking\n\n" +
            "Tracking ID: GS123456789\n\n" +
            "GoSwift shipment tracking will open here."
        );

    }

}

// =====================================================
// SIGNUP
// =====================================================

document.addEventListener("DOMContentLoaded", function () {

    const signupForm = document.getElementById("signupForm");

    if (signupForm) {

        signupForm.addEventListener("submit", async function (event) {

            event.preventDefault();

            console.log("=================================");
            console.log("SIGNUP FORM SUBMITTED");
            console.log("=================================");

            const name =
                document.getElementById("signupName").value.trim();

            const mobile =
                document.getElementById("signupMobile").value.trim();

            const email =
                document.getElementById("signupEmail").value.trim();

            const password =
                document.getElementById("signupPassword").value;

            const confirmPassword =
                document.getElementById("confirmPassword").value;

            const message =
                document.getElementById("signupMessage");

            // -----------------------------
            // VALIDATION
            // -----------------------------

            if (!name || !mobile || !email || !password) {

                message.textContent =
                    "Please fill all fields.";

                return;
            }

            if (password !== confirmPassword) {

                message.textContent =
                    "Passwords do not match.";

                return;
            }

            // -----------------------------
            // REQUEST DATA
            // -----------------------------

            const requestData = {
                name: name,
                email: email,
                phone: mobile,
                password: password
            };

            console.log(
                "REGISTER REQUEST:",
                requestData
            );

            try {

                const response = await fetch(
                    `${API_BASE_URL}/api/auth/register`,
                    {
                        method: "POST",

                        headers: {
                            "Content-Type": "application/json"
                        },

                        body: JSON.stringify(requestData)
                    }
                );

                console.log(
                    "REGISTER STATUS:",
                    response.status
                );

                const responseText =
                    await response.text();

                console.log(
                    "REGISTER RESPONSE:",
                    responseText
                );

                let data = {};

                try {
                    data = JSON.parse(responseText);
                } catch (e) {
                    console.log(
                        "Response is not JSON"
                    );
                }

                // -----------------------------
                // BACKEND ERROR
                // -----------------------------

                if (!response.ok) {

                    message.textContent =
                        data.message ||
                        responseText ||
                        "Registration failed.";

                    return;
                }

                // -----------------------------
                // SUCCESS
                // -----------------------------

                console.log(
                    "USER REGISTERED SUCCESSFULLY"
                );

                message.textContent =
                    "Account created successfully.";

                // Clear signup form
                signupForm.reset();

                // Go to login
                setTimeout(() => {

                    showLogin();

                }, 1000);

            } catch (error) {

                console.error(
                    "REGISTER ERROR:",
                    error
                );

                message.textContent =
                    "Unable to connect to backend.";

            }

        });

    } else {

        console.error(
            "ERROR: signupForm not found"
        );

    }


    // =====================================================
    // LOGIN
    // =====================================================

    const loginForm =
        document.getElementById("loginForm");

    if (loginForm) {

        loginForm.addEventListener("submit", async function (event) {

            event.preventDefault();

            console.log("=================================");
            console.log("LOGIN FORM SUBMITTED");
            console.log("=================================");

            const email =
                document.getElementById("loginEmail")
                    .value.trim();

            const password =
                document.getElementById("loginPassword")
                    .value;

            const message =
                document.getElementById("loginMessage");

            try {

                const response = await fetch(
                    `${API_BASE_URL}/api/auth/login`,
                    {
                        method: "POST",

                        headers: {
                            "Content-Type": "application/json"
                        },

                        body: JSON.stringify({
                            email: email,
                            password: password
                        })
                    }
                );

                console.log(
                    "LOGIN STATUS:",
                    response.status
                );

                const responseText =
                    await response.text();

                console.log(
                    "LOGIN RESPONSE:",
                    responseText
                );

                let data = {};

                try {
                    data = JSON.parse(responseText);
                } catch (e) {
                    console.log(
                        "Response is not JSON"
                    );
                }

                if (!response.ok) {

                    message.textContent =
                        data.message ||
                        responseText ||
                        "Invalid login.";

                    return;
                }

                // -----------------------------
                // SAVE JWT
                // -----------------------------

                localStorage.setItem(
                    "token",
                    data.token
                );

                // -----------------------------
                // CREATE USER DATA
                // -----------------------------

                const user = {

                    id: data.userId,

                    name: data.name,

                    email: data.email,

                    phone: "",

                    address: ""
                };

                // -----------------------------
                // SAVE USER DATA
                // -----------------------------

                localStorage.setItem(
                    "userData",
                    JSON.stringify(user)
                );

                // IMPORTANT
                localStorage.setItem(
                    "rishtaBoxLoggedIn",
                    "true"
                );

                currentUser = user;

                console.log(
                    "CURRENT USER:",
                    currentUser
                );

                console.log(
                    "CURRENT USER ID:",
                    currentUser.id
                );

                message.textContent =
                    "Login successful.";


                showPage("home");

            } catch (error) {

                console.error(
                    "LOGIN ERROR:",
                    error
                );

                message.textContent =
                    "Login error: " +
                    error.message;
            }

        });

    } else {

        console.error(
            "ERROR: loginForm not found"
        );

    }

});

function showFestival(festivalId) {

    console.log("Selected Festival ID:", festivalId);

    filteredProducts = products.filter(product =>
        product.festival !== null &&
        String(product.festival) === String(festivalId)
    );

    console.log("Festival Products:", filteredProducts);

    const festival = festivals.find(
        f => String(f.id) === String(festivalId)
    );

    if (festival) {
        const titleElement = document.getElementById("categoryTitle");

        if (titleElement) {
            titleElement.textContent = festival.name;
        }
    }

    populateFilters();
    showPage("category");
    applyFilters();
}


function renderRelationshipMenu() {

    const menu =
        document.getElementById("relationshipMenu");

    if (!menu) {
        return;
    }

    menu.innerHTML = "";

    if (
        !relationships ||
        relationships.length === 0
    ) {

        menu.innerHTML = `
            <div class="dropdown-empty">
                No relationships available
            </div>
        `;

        return;
    }


    relationships.forEach(function (relationship) {

        const link =
            document.createElement("a");

        link.href = "#";

        link.textContent =
            relationship.name;

        link.onclick = function (event) {

            event.preventDefault();

            showRelationshipProducts(relationship.id);


        };

        menu.appendChild(link);

    });

}
function renderFestivalMenu() {

    const menu =
        document.getElementById("festivalMenu");

    if (!menu) {
        return;
    }

    menu.innerHTML = "";

    if (
        !festivals ||
        festivals.length === 0
    ) {

        menu.innerHTML = `
            <div class="dropdown-empty">
                No festivals available
            </div>
        `;

        return;
    }


    festivals.forEach(function (festival) {

        const link =
            document.createElement("a");

        link.href = "#";

        link.textContent =
            festival.name;

        link.onclick = function (event) {

            event.preventDefault();

            showFestival(festival.id);

        };

        menu.appendChild(link);

    });

}
function renderCategoryMenu() {

    const menu =
        document.getElementById("categoryMenu");

    if (!menu) {
        return;
    }

    menu.innerHTML = "";

    if (
        !categories ||
        categories.length === 0
    ) {

        menu.innerHTML = `
            <div class="dropdown-empty">
                No categories available
            </div>
        `;

        return;
    }


    categories.forEach(function (category) {

        const link =
            document.createElement("a");

        link.href = "#";

        link.textContent =
            category.name;

        link.onclick = function (event) {

            event.preventDefault();

            showCategory(category.id);


        };

        menu.appendChild(link);

    });

}
function renderNavigationMenus() {

    console.log("Rendering navigation menus...");


    /* =====================================================
       RELATIONSHIPS
       ===================================================== */

    const relationshipMenu =
        document.getElementById("relationshipMenu");

    if (relationshipMenu) {

        relationshipMenu.innerHTML = "";

        if (
            typeof relationships !== "undefined" &&
            Array.isArray(relationships) &&
            relationships.length > 0
        ) {

            relationships.forEach(function (relationship) {

                const link =
                    document.createElement("a");

                link.href = "#";

                link.textContent =
                    relationship.name;

                link.addEventListener(
                    "click",
                    function (event) {

                        event.preventDefault();
                        event.stopPropagation();

                        console.log(
                            "RELATIONSHIP DROPDOWN CLICKED:",
                            relationship.id,
                            relationship.name
                        );

                        showRelationshipProducts(
                            relationship.id
                        );

                    }
                );

                relationshipMenu.appendChild(link);

            });

        } else {

            relationshipMenu.innerHTML =
                `<div class="dropdown-empty">
                    No relationships available
                </div>`;

        }
    }


    /* =====================================================
       FESTIVALS
       ===================================================== */

    const festivalMenu =
        document.getElementById("festivalMenu");

    if (festivalMenu) {

        festivalMenu.innerHTML = "";

        if (
            typeof festivals !== "undefined" &&
            Array.isArray(festivals) &&
            festivals.length > 0
        ) {

            festivals.forEach(function (festival) {

                const link =
                    document.createElement("a");

                link.href = "#";

                link.textContent =
                    festival.name;

                link.addEventListener(
                    "click",
                    function (event) {

                        event.preventDefault();
                        event.stopPropagation();

                        console.log(
                            "FESTIVAL DROPDOWN CLICKED:",
                            festival.id,
                            festival.name
                        );

                        showFestival(
                            festival.id
                        );

                    }
                );

                festivalMenu.appendChild(link);

            });

        } else {

            festivalMenu.innerHTML =
                `<div class="dropdown-empty">
                    No festivals available
                </div>`;

        }
    }


    /* =====================================================
       CATEGORIES
       ===================================================== */

    const categoryMenu =
        document.getElementById("categoryMenu");

    if (categoryMenu) {

        categoryMenu.innerHTML = "";

        if (
            typeof categories !== "undefined" &&
            Array.isArray(categories) &&
            categories.length > 0
        ) {

            categories.forEach(function (category) {

                const link =
                    document.createElement("a");

                link.href = "#";

                link.textContent =
                    category.name;

                link.addEventListener(
                    "click",
                    function (event) {

                        event.preventDefault();
                        event.stopPropagation();

                        console.log(
                            "CATEGORY DROPDOWN CLICKED:",
                            category.id,
                            category.name
                        );

                        showCategory(
                            category.id
                        );

                    }
                );

                categoryMenu.appendChild(link);

            });

        } else {

            categoryMenu.innerHTML =
                `<div class="dropdown-empty">
                    No categories available
                </div>`;

        }
    }


    console.log(
        "Navigation menus rendered successfully."
    );

}
function renderShopDropdowns() {

    // ================= RELATIONSHIPS =================

    const relationshipMenu =
        document.getElementById("relationshipMenu");

    if (relationshipMenu) {

        relationshipMenu.innerHTML = "";

        if (Array.isArray(relationships)) {

            relationships.forEach(function (relationship) {

                const link =
                    document.createElement("a");

                link.href = "#";

                link.textContent =
                    relationship.name;

                link.onclick = function (event) {

                    event.preventDefault();

                    console.log(
                        "Relationship selected:",
                        relationship
                    );

                    // Temporary: use your existing product page
                    showPage("category");

                    filteredProducts =
                        products.filter(function (product) {

                            return String(
                                product.relationshipId ??
                                product.relationship_id ??
                                product.relationship?.id
                            ) === String(
                                relationship.id
                            );

                        });

                    renderProducts(filteredProducts);
                };

                relationshipMenu.appendChild(link);
            });
        }
    }


    // ================= FESTIVALS =================

    const festivalMenu =
        document.getElementById("festivalMenu");

    if (festivalMenu) {

        festivalMenu.innerHTML = "";

        if (Array.isArray(festivals)) {

            festivals.forEach(function (festival) {

                const link =
                    document.createElement("a");

                link.href = "#";

                link.textContent =
                    festival.name;

                link.onclick = function (event) {

                    event.preventDefault();

                    console.log(
                        "Festival selected:",
                        festival
                    );

                    showPage("category");

                    filteredProducts =
                        products.filter(function (product) {

                            return String(
                                product.festivalId ??
                                product.festival_id ??
                                product.festival?.id
                            ) === String(
                                festival.id
                            );

                        });

                    renderProducts(filteredProducts);
                };

                festivalMenu.appendChild(link);
            });
        }
    }


    // ================= CATEGORIES =================

    const categoryMenu =
        document.getElementById("categoryMenu");

    if (categoryMenu) {

        categoryMenu.innerHTML = "";

        if (Array.isArray(categories)) {

            categories.forEach(function (category) {

                const link =
                    document.createElement("a");

                link.href = "#";

                link.textContent =
                    category.name;

                link.onclick = function (event) {

                    event.preventDefault();

                    console.log(
                        "Category selected:",
                        category
                    );

                    showPage("category");

                    filteredProducts =
                        products.filter(function (product) {

                            return String(
                                product.categoryId ??
                                product.category_id ??
                                product.category?.id
                            ) === String(
                                category.id
                            );

                        });

                    renderProducts(filteredProducts);
                };

                categoryMenu.appendChild(link);
            });
        }
    }
}

/* =========================================================
   SHOP NAVIGATION DROPDOWN MENUS
========================================================= */
function renderShopNavigationMenus() {

    /* ================= RELATIONSHIP ================= */

    const relationshipMenu =
        document.getElementById("relationshipMenu");

    if (
        relationshipMenu &&
        Array.isArray(relationships)
    ) {

        relationshipMenu.innerHTML = "";

        relationships.forEach(function (relationship) {

            const link =
                document.createElement("a");

            link.href = "#";

            link.textContent =
                relationship.name;

            link.onclick = function (event) {

                event.preventDefault();
                event.stopPropagation();

                console.log(
                    "Relationship clicked:",
                    relationship
                );

                showRelationshipProducts(
                    relationship.id
                );

            };

            relationshipMenu.appendChild(link);

        });
    }


    /* ================= FESTIVAL ================= */

    const festivalMenu =
        document.getElementById("festivalMenu");

    if (
        festivalMenu &&
        Array.isArray(festivals)
    ) {

        festivalMenu.innerHTML = "";

        festivals.forEach(function (festival) {

            const link =
                document.createElement("a");

            link.href = "#";

            link.textContent =
                festival.name;

            link.onclick = function (event) {

                event.preventDefault();
                event.stopPropagation();

                console.log(
                    "Festival clicked:",
                    festival
                );

                showFestival(
                    festival.id
                );

            };

            festivalMenu.appendChild(link);

        });
    }


    /* ================= CATEGORY ================= */

    const categoryMenu =
        document.getElementById("categoryMenu");

    if (
        categoryMenu &&
        Array.isArray(categories)
    ) {

        categoryMenu.innerHTML = "";

        categories.forEach(function (category) {

            const link =
                document.createElement("a");

            link.href = "#";

            link.textContent =
                category.name;

            link.onclick = function (event) {

                event.preventDefault();
                event.stopPropagation();

                console.log(
                    "Category clicked:",
                    category
                );

                showCategory(
                    category.id
                );

            };

            categoryMenu.appendChild(link);

        });
    }

}


/* =========================================================
   WAIT FOR EXISTING DATA
========================================================= */

const shopMenuLoader =
    setInterval(function () {

        if (
            Array.isArray(relationships) &&
            Array.isArray(festivals) &&
            Array.isArray(categories)
        ) {

            renderShopNavigationMenus();

            clearInterval(shopMenuLoader);

            console.log(
                "Shop navigation menus loaded"
            );
        }

    }, 500);
function openAllGifts() {

    console.log("========== ALL GIFTS CLICKED ==========");

    // Take ALL products directly
    const allProducts = Array.isArray(products)
        ? products.slice()
        : [];

    console.log("ALL PRODUCTS:", allProducts);
    console.log("ALL PRODUCT COUNT:", allProducts.length);

    if (allProducts.length === 0) {
        console.error("NO PRODUCTS AVAILABLE");
        return;
    }

    // Set filtered products to ALL products
    filteredProducts = allProducts;

    // Change title
    const categoryTitle =
        document.getElementById("categoryTitle");

    if (categoryTitle) {
        categoryTitle.textContent = "All Gifts";
    }

    // Open category page
    showPage("category");

    // Render ALL products
    renderProducts(allProducts);

    console.log("========== ALL GIFTS DONE ==========");
}
