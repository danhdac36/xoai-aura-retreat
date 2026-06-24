let selectedItems = [];
let currentPage = 1;
const itemsPerPage = 12;
let currentCategory = "ALL";

document.addEventListener("DOMContentLoaded", function () {
    bindLoadMenuButton();
    bindSelectButtons();
    bindOrderForm();
    bindPaginationButtons();
    bindCategoryTabs();
    applyMenuImages();
    renderPagination();
    renderSelectedPanel();
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

    if (!searchBtn) {
        return;
    }

    searchBtn.addEventListener("click", function () {
        const userEl = document.getElementById("input-user-id") || document.getElementById("input-guest-id");
        const bookingEl = document.getElementById("input-booking-id");

        const guestId = userEl ? userEl.value : "";
        const bookingId = bookingEl ? bookingEl.value : "";

        if (!guestId || !bookingId) {
            showToast("Vui lòng nhập đầy đủ User ID và Booking ID", "error");
            return;
        }

        window.location.href = "/fnb/meal-selection?guestId="
            + encodeURIComponent(guestId)
            + "&bookingId="
            + encodeURIComponent(bookingId);
    });
}

function bindSelectButtons() {
    const selectButtons = document.querySelectorAll(".btn-select");

    selectButtons.forEach(function (button) {
        button.addEventListener("click", function () {
            toggleItemSelection(button);
        });
    });
}

function bindOrderForm() {
    const form = document.getElementById("order-submit-form");

    if (!form) {
        return;
    }

    form.addEventListener("submit", function (event) {
        if (selectedItems.length === 0) {
            event.preventDefault();
            showToast("Vui lòng chọn ít nhất 1 món ăn trước khi xác nhận", "error");
            return;
        }

        const guestInput = document.getElementById("input-user-id") || document.getElementById("input-guest-id");
        const bookingInput = document.getElementById("input-booking-id");
        const placeOrderInput = document.getElementById("order-place");
        const noteInput = document.getElementById("order-note");
        const timeInput = document.getElementById("order-time");

        const formGuestId = document.getElementById("form-guest-id");
        const formBookingId = document.getElementById("form-booking-id");
        const formPlaceOrder = document.getElementById("form-place-order");
        const formNote = document.getElementById("form-note");
        const formServingTime = document.getElementById("form-serving-time");

        if (formGuestId && guestInput) {
            formGuestId.value = guestInput.value;
        }

        if (formBookingId && bookingInput) {
            formBookingId.value = bookingInput.value;
        }

        if (formPlaceOrder) {
            formPlaceOrder.value = placeOrderInput && placeOrderInput.value
                ? placeOrderInput.value
                : "Villa V101";
        }

        if (formServingTime) {
            formServingTime.value = timeInput && timeInput.value
                ? timeInput.value
                : "";
        }

        if (formNote) {
            formNote.value = noteInput && noteInput.value
                ? noteInput.value
                : "";
        }

        updateHiddenFormInputs();
    });
}

function bindPaginationButtons() {
    const prevBtn = document.getElementById("btn-prev");
    const nextBtn = document.getElementById("btn-next");

    if (prevBtn) {
        prevBtn.addEventListener("click", function () {
            changePage(-1);
        });
    }

    if (nextBtn) {
        nextBtn.addEventListener("click", function () {
            changePage(1);
        });
    }
}

function applyMenuImages() {
    const images = document.querySelectorAll(".card-img");

    images.forEach(function (img) {
        img.addEventListener("error", function () {
            if (!img.dataset.fallbackApplied) {
                img.dataset.fallbackApplied = "true";
                img.src = "/images/fnb/menu/default-food.jpg";
            }
        });
    });
}

function toggleItemSelection(buttonEl) {
    const itemId = parseInt(buttonEl.getAttribute("data-id"), 10);
    const itemName = buttonEl.getAttribute("data-name");
    const price = parseFloat(buttonEl.getAttribute("data-price"));

    if (!itemId || !itemName || Number.isNaN(price)) {
        showToast("Dữ liệu món ăn không hợp lệ", "error");
        return;
    }

    const existingIndex = selectedItems.findIndex(function (item) {
        return item.id === itemId;
    });

    if (existingIndex > -1) {
        selectedItems.splice(existingIndex, 1);
        buttonEl.classList.remove("selected");
        buttonEl.textContent = "CHỌN MÓN";
    } else {
        const imageUrl = buttonEl.getAttribute("data-image-url") || "/images/fnb/menu/default-food.jpg";
        selectedItems.push({
            id: itemId,
            itemName: itemName,
            price: price,
            quantity: 1,
            imageUrl: imageUrl
        });

        buttonEl.classList.add("selected");
        buttonEl.textContent = "ĐÃ CHỌN ✓";
    }

    renderSelectedPanel();
    updateHiddenFormInputs();
}

function updateItemQuantity(itemId, delta) {
    const item = selectedItems.find(function (selectedItem) {
        return selectedItem.id === itemId;
    });

    if (!item) {
        return;
    }



    item.quantity += delta;

    if (item.quantity <= 0) {
        removeItemSelection(itemId);
        return;
    }

    renderSelectedPanel();
    updateHiddenFormInputs();
}

function removeItemSelection(itemId) {
    const index = selectedItems.findIndex(function (item) {
        return item.id === itemId;
    });

    if (index > -1) {
        selectedItems.splice(index, 1);
    }

    const buttons = document.querySelectorAll('.btn-select[data-id="' + itemId + '"]');
    buttons.forEach(function (button) {
        button.classList.remove("selected");
        button.textContent = "CHỌN MÓN";
    });

    renderSelectedPanel();
    updateHiddenFormInputs();
}

function updateHiddenFormInputs() {
    const container = document.getElementById("hidden-items-container");

    if (!container) {
        return;
    }

    container.innerHTML = "";

    selectedItems.forEach(function (item, index) {
        const idInput = document.createElement("input");
        idInput.type = "hidden";
        idInput.name = "items[" + index + "].menuItemId";
        idInput.value = item.id;

        const qtyInput = document.createElement("input");
        qtyInput.type = "hidden";
        qtyInput.name = "items[" + index + "].quantity";
        qtyInput.value = item.quantity;

        container.appendChild(idInput);
        container.appendChild(qtyInput);
    });
}

function renderSelectedPanel() {
    const listContainer = document.getElementById("selected-items-list");
    const badge = document.getElementById("selected-badge");

    if (!listContainer) {
        return;
    }

    listContainer.innerHTML = "";

    const totalQuantity = selectedItems.reduce(function (total, item) {
        return total + item.quantity;
    }, 0);

    if (badge) {
        badge.textContent = totalQuantity;
    }

    if (selectedItems.length === 0) {
        const emptyMessage = document.createElement("p");
        emptyMessage.className = "cart-empty";
        emptyMessage.textContent = "Chưa chọn món nào";
        listContainer.appendChild(emptyMessage);

        updateCostSummary(0);
        return;
    }

    selectedItems.forEach(function (item) {
        const row = createSelectedItemRow(item);
        listContainer.appendChild(row);
    });

    const subtotal = selectedItems.reduce(function (total, item) {
        return total + item.price * item.quantity;
    }, 0);

    updateCostSummary(subtotal);
}

function createSelectedItemRow(item) {
    const row = document.createElement("div");
    row.className = "cart-item";

    const image = document.createElement("img");
    image.className = "cart-item-img";
    image.alt = item.itemName;
    image.src = item.imageUrl || "/images/fnb/menu/default-food.jpg";

    image.addEventListener("error", function () {
        if (!image.dataset.fallbackApplied) {
            image.dataset.fallbackApplied = "true";
            image.src = "/images/fnb/menu/default-food.jpg";
        }
    });

    const details = document.createElement("div");
    details.className = "cart-item-details";

    const name = document.createElement("div");
    name.className = "cart-item-name";
    name.textContent = item.itemName;

    const qtyRow = document.createElement("div");
    qtyRow.className = "cart-item-qty-row";

    const minusBtn = document.createElement("button");
    minusBtn.type = "button";
    minusBtn.className = "qty-btn";
    minusBtn.textContent = "-";
    minusBtn.addEventListener("click", function () {
        updateItemQuantity(item.id, -1);
    });

    const qtyText = document.createElement("span");
    qtyText.textContent = item.quantity;

    const plusBtn = document.createElement("button");
    plusBtn.type = "button";
    plusBtn.className = "qty-btn";
    plusBtn.textContent = "+";
    plusBtn.addEventListener("click", function () {
        updateItemQuantity(item.id, 1);
    });

    qtyRow.appendChild(minusBtn);
    qtyRow.appendChild(qtyText);
    qtyRow.appendChild(plusBtn);

    details.appendChild(name);
    details.appendChild(qtyRow);

    const removeBtn = document.createElement("button");
    removeBtn.type = "button";
    removeBtn.className = "cart-item-remove";
    removeBtn.textContent = "Xóa";
    removeBtn.addEventListener("click", function () {
        removeItemSelection(item.id);
    });

    row.appendChild(image);
    row.appendChild(details);
    row.appendChild(removeBtn);

    return row;
}

function updateCostSummary(subtotal) {
    const serviceFee = subtotal * 0.05;
    const total = subtotal + serviceFee;

    const subtotalEl = document.getElementById("price-subtotal");
    const serviceFeeEl = document.getElementById("price-service-charge");
    const totalEl = document.getElementById("price-total");

    if (subtotalEl) {
        subtotalEl.textContent = formatVND(subtotal);
    }

    if (serviceFeeEl) {
        serviceFeeEl.textContent = formatVND(serviceFee);
    }

    if (totalEl) {
        totalEl.textContent = formatVND(total);
    }
}

function renderPagination() {
    const allCards = Array.from(document.querySelectorAll(".menu-card"));
    const paginationContainer = document.getElementById("pagination-controls");
    const pageInfo = document.getElementById("page-info");
    const prevBtn = document.getElementById("btn-prev");
    const nextBtn = document.getElementById("btn-next");

    if (!paginationContainer) {
        return;
    }

    // Filter cards based on current category
    const cards = allCards.filter(function (card) {
        if (currentCategory === "ALL" || !currentCategory) {
            return true;
        }
        return card.getAttribute("data-category") === currentCategory;
    });

    // Hide all cards first
    allCards.forEach(function (card) {
        card.classList.add("hidden");
    });

    if (cards.length === 0) {
        paginationContainer.classList.add("hidden");
        if (pageInfo) {
            pageInfo.textContent = "Trang 0 / 0";
        }
        return;
    }

    if (cards.length <= itemsPerPage) {
        paginationContainer.classList.add("hidden");
        cards.forEach(function (card) {
            card.classList.remove("hidden");
        });
        return;
    }

    paginationContainer.classList.remove("hidden");

    const totalPages = Math.ceil(cards.length / itemsPerPage);

    if (currentPage < 1) {
        currentPage = 1;
    }

    if (currentPage > totalPages) {
        currentPage = totalPages;
    }

    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;

    cards.forEach(function (card, index) {
        if (index >= startIndex && index < endIndex) {
            card.classList.remove("hidden");
        }
    });

    if (pageInfo) {
        pageInfo.textContent = "Trang " + currentPage + " / " + totalPages;
    }

    if (prevBtn) {
        prevBtn.disabled = currentPage === 1;
    }

    if (nextBtn) {
        nextBtn.disabled = currentPage === totalPages;
    }
}

function changePage(direction) {
    const cards = document.querySelectorAll(".menu-card");
    const totalPages = Math.ceil(cards.length / itemsPerPage);

    if (totalPages <= 1) {
        return;
    }

    const newPage = currentPage + direction;

    if (newPage < 1 || newPage > totalPages) {
        return;
    }

    currentPage = newPage;
    renderPagination();
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

    if (!toast) {
        return;
    }

    toast.textContent = message;
    toast.className = "toast show " + type;

    setTimeout(function () {
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
    tabs.forEach(function (tab) {
        tab.addEventListener("click", function () {
            tabs.forEach(function (t) {
                t.classList.remove("active");
            });
            tab.classList.add("active");
            currentCategory = tab.getAttribute("data-category");
            currentPage = 1;
            renderPagination();
        });
    });
}