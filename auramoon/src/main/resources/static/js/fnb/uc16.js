let allMenuItems = [];
let selectedItems = [];
let currentPage = 1;
const itemsPerPage = 10;

document.addEventListener("DOMContentLoaded", () => {
    loadPersonalizedMenu();
});

// Load menu items from static data
function loadPersonalizedMenu() {
    const userEl = document.getElementById("input-user-id") || document.getElementById("input-guest-id");
    const guestId = userEl ? userEl.value : "";
    const bookingId = document.getElementById("input-booking-id").value;

    if (!guestId || !bookingId) {
        showToast("Vui lòng nhập đầy đủ User ID và Booking ID", "error");
        return;
    }

    // Read directly from static menuData
    allMenuItems = menuData || [];
    currentPage = 1;
    renderMenu();
    renderPagination();
    renderSelectedPanel();
    showToast("Tải thực đơn cá nhân thành công", "success");
}


// Render menu cards for current page
function renderMenu() {
    const grid = document.getElementById("menu-grid");
    grid.innerHTML = "";

    if (allMenuItems.length === 0) {
        grid.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: var(--text-muted); font-style: italic;">Không có món ăn nào khả dụng.</p>';
        return;
    }

    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = Math.min(startIndex + itemsPerPage, allMenuItems.length);
    const pageItems = allMenuItems.slice(startIndex, endIndex);

    pageItems.forEach(item => {
        const imageUrl = getMenuImageUrl(item);
        const isSelected = selectedItems.some(i => i.id === item.id);

        const card = document.createElement("article");
        card.className = "menu-card";
        card.innerHTML = `
            <div class="card-img-wrap">
                <img src="${imageUrl}" alt="${item.itemName}" class="card-img" onerror="this.src='/images/fnb/menu/default-food.jpg'">
            </div>
            <div class="card-body">
                <h3 class="card-title">${item.itemName}</h3>
                <div class="card-price">${formatVND(item.price)}</div>
                <p class="card-ingredients">${item.ingredient || "Thành phần tự nhiên từ vườn Aura"}</p>
                <button type="button" class="btn-select ${isSelected ? 'selected' : ''}" onclick="toggleItemSelection(${item.id})">
                    ${isSelected ? 'ĐÃ CHỌN ✓' : 'CHỌN MÓN'}
                </button>
            </div>
        `;
        grid.appendChild(card);
    });
}

// Render Pagination Controls
function renderPagination() {
    const container = document.getElementById("pagination-controls");
    if (allMenuItems.length <= itemsPerPage) {
        container.style.display = "none";
        return;
    }

    container.style.display = "flex";
    const totalPages = Math.ceil(allMenuItems.length / itemsPerPage);
    document.getElementById("page-info").textContent = `Trang ${currentPage} / ${totalPages}`;
    document.getElementById("btn-prev").disabled = currentPage === 1;
    document.getElementById("btn-next").disabled = currentPage === totalPages;
}

function changePage(direction) {
    const totalPages = Math.ceil(allMenuItems.length / itemsPerPage);
    const newPage = currentPage + direction;
    if (newPage >= 1 && newPage <= totalPages) {
        currentPage = newPage;
        renderMenu();
        renderPagination();
    }
}

// Manage Selection (Add / Remove)
function toggleItemSelection(itemId) {
    const item = allMenuItems.find(i => i.id === itemId);
    if (!item) return;

    const existingIndex = selectedItems.findIndex(i => i.id === itemId);
    if (existingIndex > -1) {
        // Remove item
        selectedItems.splice(existingIndex, 1);
    } else {
        // Add item
        selectedItems.push({
            id: item.id,
            itemName: item.itemName,
            price: item.price,
            quantity: 1
        });
    }

    renderMenu();
    renderSelectedPanel();
}

function updateItemQuantity(itemId, delta) {
    const item = selectedItems.find(i => i.id === itemId);
    if (!item) return;

    item.quantity += delta;
    if (item.quantity <= 0) {
        const index = selectedItems.indexOf(item);
        selectedItems.splice(index, 1);
    }

    renderMenu();
    renderSelectedPanel();
}

// Render selected items sidebar list
function renderSelectedPanel() {
    const listContainer = document.getElementById("selected-items-list");
    const emptyMsg = document.getElementById("empty-cart-msg");
    const badge = document.getElementById("selected-badge");

    listContainer.innerHTML = "";

    badge.textContent = selectedItems.reduce((acc, i) => acc + i.quantity, 0);

    if (selectedItems.length === 0) {
        listContainer.appendChild(emptyMsg);
        updateCostSummary(0);
        return;
    }

    selectedItems.forEach(item => {
        const fullItem = allMenuItems.find(i => i.id === item.id) || item;
        const imageUrl = getMenuImageUrl(fullItem);

        const row = document.createElement("div");
        row.className = "cart-item";
        row.innerHTML = `
            <img src="${imageUrl}" alt="${item.itemName}" class="cart-item-img" onerror="this.src='/images/fnb/menu/default-food.jpg'">
            <div class="cart-item-details">
                <div class="cart-item-name">${item.itemName}</div>
                <div class="cart-item-price">${formatVND(item.price)}</div>
                <div class="cart-item-qty-row">
                    <button class="qty-btn" onclick="updateItemQuantity(${item.id}, -1)">-</button>
                    <span>${item.quantity}</span>
                    <button class="qty-btn" onclick="updateItemQuantity(${item.id}, 1)">+</button>
                </div>
            </div>
            <button class="cart-item-remove" onclick="toggleItemSelection(${item.id})">Xóa</button>
        `;
        listContainer.appendChild(row);
    });

    const subtotal = selectedItems.reduce((acc, i) => acc + (i.price * i.quantity), 0);
    updateCostSummary(subtotal);
}

// Update Subtotal, 5% Service fee, and Total amounts
function updateCostSummary(subtotal) {
    const serviceFee = subtotal * 0.05;
    const total = subtotal + serviceFee;

    document.getElementById("price-subtotal").textContent = formatVND(subtotal);
    document.getElementById("price-service-charge").textContent = formatVND(serviceFee);
    document.getElementById("price-total").textContent = formatVND(total);
}

// Submit Order (POST Request to API)
function submitOrder() {
    if (selectedItems.length === 0) {
        showToast("Vui lòng chọn ít nhất 1 món ăn trước khi xác nhận", "error");
        return;
    }

    const userEl = document.getElementById("input-user-id") || document.getElementById("input-guest-id");
    const guestId = userEl ? parseInt(userEl.value, 10) : 1;
    const bookingId = parseInt(document.getElementById("input-booking-id").value, 10);
    const placeOrder = document.getElementById("order-place").value;
    const note = document.getElementById("order-note").value;

    const requestBody = {
        bookingId: bookingId,
        guestId: guestId,
        placeOrder: placeOrder || "Villa V101",
        note: note || "",

        items: selectedItems.map(item => ({
            menuItemId: item.id,
            quantity: item.quantity
        }))
    };

    fetch("/api/v1/fnb/meal-orders", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(requestBody)
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => {
                    throw new Error(err.error?.message || "Lỗi khi đặt món");
                });
            }
            return response.json();
        })
        .then(data => {
            showToast("Đặt món ăn cá nhân thành công!", "success");
            selectedItems = [];
            renderMenu();
            renderSelectedPanel();
            document.getElementById("order-note").value = "";
        })
        .catch(err => {
            console.error(err);
            showToast(err.message, "error");
        });
}

// Helper: format number to VND currency
function formatVND(value) {
    if (value === undefined || value === null) return "0đ";
    return value.toLocaleString('vi-VN') + "đ";
}

// Helper: Toast Notifications
function showToast(message, type) {
    const toast = document.getElementById("toast-notification");
    toast.textContent = message;
    toast.className = `toast show ${type}`;

    setTimeout(() => {
        toast.className = "toast";
    }, 3000);
}
