let cartItems = [];
let currentCategory = "ALL";

document.addEventListener("DOMContentLoaded", () => {
    bindLoadMenuButton();
    bindSelectButtons();
    bindOrderForm();
    bindCategoryTabs();
    applyMenuImages();
    renderCartPanel();
    updateHiddenFormInputs();

    // Check if there is an error message rendered on page load
    const toastEl = document.getElementById("toast-notification");
    if (toastEl && toastEl.classList.contains("error") && toastEl.textContent.trim()) {
        showErrorModal(toastEl.textContent.trim());
        toastEl.classList.remove("show", "error");
        toastEl.textContent = "";
    }
});

function bindLoadMenuButton() {
    const searchBtn = document.querySelector(".btn-search");
    if (!searchBtn) return;
    searchBtn.addEventListener("click", () => {
        const guestEl = document.getElementById("input-guest-id");
        const bookingEl = document.getElementById("input-booking-id");
        const guestId = guestEl ? guestEl.value : "";
        const bookingId = bookingEl ? bookingEl.value : "";

        if (!guestId || !bookingId) {
            showToast("Vui lòng nhập đầy đủ Guest ID và Booking ID", "error");
            return;
        }
        window.location.href = `/fnb/uc19-alacarte-order?guestId=${encodeURIComponent(guestId)}&bookingId=${encodeURIComponent(bookingId)}`;
    });
}

function bindSelectButtons() {
    const selectButtons = document.querySelectorAll(".btn-select");
    selectButtons.forEach(btn => {
        btn.addEventListener("click", (e) => {
            toggleCartItem(e.currentTarget);
        });
    });
}

function toggleCartItem(buttonEl) {
    const itemId = parseInt(buttonEl.getAttribute("data-id"), 10);
    const itemName = buttonEl.getAttribute("data-name");
    const price = parseFloat(buttonEl.getAttribute("data-price"));

    if (!itemId || !itemName || Number.isNaN(price)) {
        showToast("Dữ liệu món ăn không hợp lệ", "error");
        return;
    }

    const existingIndex = cartItems.findIndex(item => item.id === itemId);
    if (existingIndex > -1) {
        cartItems.splice(existingIndex, 1);
        buttonEl.classList.remove("selected");
        buttonEl.textContent = "CHỌN MÓN";
    } else {
        const imageUrl = buttonEl.getAttribute("data-image-url") || "/images/fnb/menu/default-food.jpg";
        cartItems.push({
            id: itemId,
            itemName: itemName,
            price: price,
            quantity: 1,
            imageUrl: imageUrl
        });
        buttonEl.classList.add("selected");
        buttonEl.textContent = "ĐÃ CHỌN (1)";
    }
    renderCartPanel();
    updateHiddenFormInputs();
}

function updateItemQuantity(itemId, delta) {
    const item = cartItems.find(item => item.id === itemId);
    if (!item) return;

    item.quantity += delta;

    const buttons = document.querySelectorAll(`.btn-select[data-id="${itemId}"]`);
    if (item.quantity <= 0) {
        const index = cartItems.indexOf(item);
        cartItems.splice(index, 1);
        buttons.forEach(btn => {
            btn.classList.remove("selected");
            btn.textContent = "CHỌN MÓN";
        });
    } else {
        buttons.forEach(btn => {
            btn.classList.add("selected");
            btn.textContent = `ĐÃ CHỌN (${item.quantity})`;
        });
    }
    renderCartPanel();
    updateHiddenFormInputs();
}

function removeCartItem(itemId) {
    const index = cartItems.findIndex(item => item.id === itemId);
    if (index > -1) {
        cartItems.splice(index, 1);
    }
    const buttons = document.querySelectorAll(`.btn-select[data-id="${itemId}"]`);
    buttons.forEach(btn => {
        btn.classList.remove("selected");
        btn.textContent = "CHỌN MÓN";
    });
    renderCartPanel();
    updateHiddenFormInputs();
}

function renderCartPanel() {
    const cartList = document.getElementById("cart-items-list") || document.getElementById("selected-items-list");
    const badge = document.getElementById("cart-badge");

    if (!cartList) return;
    cartList.innerHTML = "";

    const totalQuantity = cartItems.reduce((acc, item) => acc + item.quantity, 0);
    if (badge) {
        badge.textContent = totalQuantity;
    }

    if (cartItems.length === 0) {
        const emptyMsg = document.createElement("p");
        emptyMsg.className = "cart-empty";
        emptyMsg.textContent = "Chưa chọn món nào";
        cartList.appendChild(emptyMsg);
        updateCostSummary(0);
        return;
    }

    cartItems.forEach(item => {
        let imageUrl = item.imageUrl || "/images/fnb/menu/default-food.jpg";

        const row = document.createElement("div");
        row.className = "cart-item";
        row.innerHTML = `
            <img src="${imageUrl}" alt="${item.itemName}" class="cart-item-img" onerror="this.src='/images/fnb/menu/default-food.jpg'">
            <div class="cart-item-details">
                <div class="cart-item-name">${item.itemName}</div>
                <div class="cart-item-price">${formatVND(item.price)}</div>
                <div class="cart-item-qty-row">
                    <button type="button" class="qty-btn minus-btn">-</button>
                    <span>${item.quantity}</span>
                    <button type="button" class="qty-btn plus-btn">+</button>
                </div>
            </div>
            <button type="button" class="cart-item-remove">Xóa</button>
        `;

        row.querySelector(".minus-btn").addEventListener("click", () => {
            updateItemQuantity(item.id, -1);
        });
        row.querySelector(".plus-btn").addEventListener("click", () => {
            updateItemQuantity(item.id, 1);
        });
        row.querySelector(".cart-item-remove").addEventListener("click", () => {
            removeCartItem(item.id);
        });

        cartList.appendChild(row);
    });

    const subtotal = cartItems.reduce((acc, item) => acc + item.price * item.quantity, 0);
    updateCostSummary(subtotal);
}

function updateCostSummary(subtotal) {
    const serviceFee = subtotal * 0.05;
    const total = subtotal + serviceFee;

    const subtotalEl = document.getElementById("price-subtotal");
    const serviceChargeEl = document.getElementById("price-service-charge");
    const totalEl = document.getElementById("price-total");

    if (subtotalEl) subtotalEl.textContent = formatVND(subtotal);
    if (serviceChargeEl) serviceChargeEl.textContent = formatVND(serviceFee);
    if (totalEl) totalEl.textContent = formatVND(total);
}

function updateHiddenFormInputs() {
    const container = document.getElementById("hidden-items-container");
    if (!container) return;
    container.innerHTML = "";

    cartItems.forEach((item, index) => {
        const idInput = document.createElement("input");
        idInput.type = "hidden";
        idInput.name = `items[${index}].menuItemId`;
        idInput.value = item.id;

        const qtyInput = document.createElement("input");
        qtyInput.type = "hidden";
        qtyInput.name = `items[${index}].quantity`;
        qtyInput.value = item.quantity;

        container.appendChild(idInput);
        container.appendChild(qtyInput);
    });
}

function bindOrderForm() {
    const form = document.getElementById("alacarte-order-form");
    if (!form) return;
    form.addEventListener("submit", (e) => {
        if (cartItems.length === 0) {
            e.preventDefault();
            showToast("Vui lòng chọn ít nhất 1 món ăn trước khi xác nhận", "error");
            return;
        }

        const guestEl = document.getElementById("input-guest-id");
        const bookingEl = document.getElementById("input-booking-id");
        const placeEl = document.getElementById("order-place");
        const timeEl = document.getElementById("order-time");
        const noteEl = document.getElementById("order-note");

        const formGuestId = document.getElementById("form-guest-id");
        const formBookingId = document.getElementById("form-booking-id");
        const formPlaceOrder = document.getElementById("form-place-order");
        const formServingTime = document.getElementById("form-serving-time");
        const formNote = document.getElementById("form-note");

        if (formGuestId && guestEl) formGuestId.value = guestEl.value;
        if (formBookingId && bookingEl) formBookingId.value = bookingEl.value;
        if (formPlaceOrder) formPlaceOrder.value = placeEl ? placeEl.value : "Villa V101";
        if (formServingTime) formServingTime.value = timeEl ? timeEl.value : "";
        if (formNote) formNote.value = noteEl ? noteEl.value : "";

        updateHiddenFormInputs();
    });
}

function applyMenuImages() {
    const images = document.querySelectorAll(".card-img");
    images.forEach(img => {
        img.addEventListener("error", function onError() {
            if (!img.dataset.fallbackApplied) {
                img.dataset.fallbackApplied = "true";
                img.src = "/images/fnb/menu/default-food.jpg";
            }
        });
    });
}

function formatVND(value) {
    if (value === undefined || value === null || Number.isNaN(Number(value))) {
        return "0đ";
    }
    return Number(value).toLocaleString("vi-VN") + "đ";
}

function showToast(message, type) {
    if (type === "error") {
        showErrorModal(message);
        return;
    }
    const toast = document.getElementById("toast-notification");
    if (!toast) return;
    toast.textContent = message;
    toast.className = "toast show " + type;

    setTimeout(() => {
        toast.className = "toast";
        toast.textContent = "";
    }, 3000);
}

function showErrorModal(message) {
    const modal = document.getElementById("error-modal");
    const msgEl = document.getElementById("modal-error-message");
    if (modal && msgEl) {
        msgEl.textContent = message;
        modal.classList.add("show");
    }
}

function closeErrorModal() {
    const modal = document.getElementById("error-modal");
    if (modal) {
        modal.classList.remove("show");
    }
}

function bindCategoryTabs() {
    const tabs = document.querySelectorAll(".tab-btn");
    tabs.forEach(tab => {
        tab.addEventListener("click", () => {
            tabs.forEach(t => t.classList.remove("active"));
            tab.classList.add("active");
            currentCategory = tab.getAttribute("data-category");
            filterMenuItems();
        });
    });
}

function filterMenuItems() {
    const cards = document.querySelectorAll(".menu-card");
    cards.forEach(card => {
        const cat = card.getAttribute("data-category");
        if (currentCategory === "ALL" || cat === currentCategory) {
            card.classList.remove("hidden");
        } else {
            card.classList.add("hidden");
        }
    });
}
