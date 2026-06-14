let allMenuItems = [];
let alacarteItems = [];
let cartItems = [];

document.addEventListener("DOMContentLoaded", () => {
    loadAlacarteMenu();
});

// Load menu items from static data and filter for European dishes
function loadAlacarteMenu() {
    const guestId = document.getElementById("input-guest-id").value;
    const bookingId = document.getElementById("input-booking-id").value;

    if (!guestId || !bookingId) {
        showToast("Vui lòng nhập đầy đủ Guest ID và Booking ID", "error");
        return;
    }

    // Filter exactly the 50 European dishes from static menuData
    const europeanDishes = (menuData || []).filter(item => 
        item.ingredient && item.ingredient.toLowerCase().includes("europe")
    );

    alacarteItems = europeanDishes.map(item => {
        return {
            ...item,
            isAllergic: false
        };
    });

    cartItems = [];
    renderMenu();
    renderCartPanel();
    showToast(`Tải ${alacarteItems.length} món A-La-Carte thành công`, "success");
}


// Render European menu cards
function renderMenu() {
    const grid = document.getElementById("menu-grid");
    grid.innerHTML = "";

    if (alacarteItems.length === 0) {
        grid.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: var(--text-muted); font-style: italic;">Không có món ăn A-La-Carte nào khả dụng cho khách hàng này.</p>';
        return;
    }

    alacarteItems.forEach(item => {
        const imageUrl = getMenuImageUrl(item);
        const cartItem = cartItems.find(i => i.id === item.id);
        const isSelected = !!cartItem;
        const isAllergic = item.isAllergic;
        
        const card = document.createElement("article");
        card.className = `menu-card ${isAllergic ? 'allergic-card' : ''}`;
        
        let buttonHtml = "";
        if (isAllergic) {
            buttonHtml = `
                <button type="button" class="btn-select disabled" disabled style="background-color: #E6D5D5; color: var(--danger-red); border: 1px solid #D9B3B3; cursor: not-allowed;">
                    CÓ THỂ GÂY DỊ ỨNG ⚠️
                </button>
            `;
        } else {
            buttonHtml = `
                <button type="button" class="btn-select ${isSelected ? 'selected' : ''}" onclick="addToCart(${item.id})">
                    ${isSelected ? `ĐÃ CHỌN (${cartItem.quantity})` : 'CHỌN MÓN'}
                </button>
            `;
        }

        card.innerHTML = `
            <div class="card-img-wrap" style="${isAllergic ? 'opacity: 0.65;' : ''}">
                <img src="${imageUrl}" alt="${item.itemName}" class="card-img" onerror="this.src='/images/fnb/menu/default-food.jpg'">
            </div>
            <div class="card-body" style="${isAllergic ? 'background-color: #FAF2F2;' : ''}">
                <h3 class="card-title" style="${isAllergic ? 'color: var(--danger-red);' : ''}">${item.itemName}</h3>
                <div class="card-price">${formatVND(item.price)}</div>
                <p class="card-ingredients">${item.ingredient || "Món ăn cao cấp chuẩn Âu"}</p>
                ${isAllergic ? `<p style="color: var(--danger-red); font-size: 0.8rem; font-weight: 600; margin-bottom: 0.75rem;">⚠️ Cảnh báo: Chứa nguyên liệu gây dị ứng trong hồ sơ của bạn!</p>` : ''}
                ${buttonHtml}
            </div>
        `;
        grid.appendChild(card);
    });
}


// Manage Cart
function addToCart(itemId) {
    const item = alacarteItems.find(i => i.id === itemId);
    if (!item) return;

    const existing = cartItems.find(i => i.id === itemId);
    if (existing) {
        existing.quantity += 1;
    } else {
        cartItems.push({
            id: item.id,
            itemName: item.itemName,
            price: item.price,
            quantity: 1
        });
    }

    renderMenu();
    renderCartPanel();
}

function updateCartQuantity(itemId, delta) {
    const item = cartItems.find(i => i.id === itemId);
    if (!item) return;

    item.quantity += delta;
    if (item.quantity <= 0) {
        const index = cartItems.indexOf(item);
        cartItems.splice(index, 1);
    }

    renderMenu();
    renderCartPanel();
}

function removeFromCart(itemId) {
    const index = cartItems.findIndex(i => i.id === itemId);
    if (index > -1) {
        cartItems.splice(index, 1);
    }
    renderMenu();
    renderCartPanel();
}

// Render cart list
function renderCartPanel() {
    const listContainer = document.getElementById("cart-items-list");
    const emptyMsg = document.getElementById("empty-cart-msg");
    const badge = document.getElementById("cart-badge");

    listContainer.innerHTML = "";
    badge.textContent = cartItems.reduce((acc, i) => acc + i.quantity, 0);

    if (cartItems.length === 0) {
        listContainer.appendChild(emptyMsg);
        updateCostSummary(0);
        return;
    }

    cartItems.forEach(item => {
        const fullItem = alacarteItems.find(i => i.id === item.id) || item;
        const imageUrl = getMenuImageUrl(fullItem);

        const row = document.createElement("div");
        row.className = "cart-item";
        row.innerHTML = `
            <img src="${imageUrl}" alt="${item.itemName}" class="cart-item-img" onerror="this.src='/images/fnb/menu/default-food.jpg'">
            <div class="cart-item-details">
                <div class="cart-item-name">${item.itemName}</div>
                <div class="cart-item-price">${formatVND(item.price)}</div>
                <div class="cart-item-qty-row">
                    <button class="qty-btn" onclick="updateCartQuantity(${item.id}, -1)">-</button>
                    <span>${item.quantity}</span>
                    <button class="qty-btn" onclick="updateCartQuantity(${item.id}, 1)">+</button>
                </div>
            </div>
            <button class="cart-item-remove" onclick="removeFromCart(${item.id})">Xóa</button>
        `;
        listContainer.appendChild(row);
    });

    const subtotal = cartItems.reduce((acc, i) => acc + (i.price * i.quantity), 0);
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

// Submit Alacarte Order
function submitAlacarteOrder() {
    if (cartItems.length === 0) {
        showToast("Vui lòng chọn ít nhất 1 món ăn trước khi xác nhận", "error");
        return;
    }

    const guestId = parseInt(document.getElementById("input-guest-id").value, 10);
    const bookingId = parseInt(document.getElementById("input-booking-id").value, 10);
    const placeOrder = document.getElementById("order-place").value;
    const note = document.getElementById("order-note").value;

    const requestBody = {
        bookingId: bookingId,
        guestId: guestId,
        placeOrder: placeOrder || "Villa V101",
        note: note || "",
        items: cartItems.map(item => ({
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
        showToast("Đặt món A-La-Carte thành công!", "success");
        cartItems = [];
        renderMenu();
        renderCartPanel();
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
