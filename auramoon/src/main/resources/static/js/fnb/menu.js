/**
 * menu.js — FnB Meal Selection Page
 * UC16 (Standard meal) & UC19 (À-la-carte)
 * Package: com.AuraMoon.auramoon.fnb
 */

'use strict';

/* ─── State ─────────────────────────────────────── */
let selectedItems = [];

/* ─── Tab Logic ──────────────────────────────────── */
const tabPersonalized = document.getElementById('tab-personalized');
const tabAlacarte     = document.getElementById('tab-alacarte');
const mealTypeInput   = document.getElementById('mealTypeInput');

function syncGridSelection() {
    const selectedIds = selectedItems.map(i => i.id);
    const allCardElements = document.querySelectorAll('.menu-grid .menu-card:not(.menu-card--disabled)');
    allCardElements.forEach(card => {
        const id = parseInt(card.getAttribute('data-id'), 10);
        const btn = card.querySelector('.select-btn');
        if (selectedIds.includes(id)) {
            card.classList.add('card-selected');
            if (btn) {
                btn.textContent = 'Đã chọn ✓';
                btn.classList.add('btn-selected');
            }
        } else {
            card.classList.remove('card-selected');
            if (btn) {
                btn.textContent = 'Chọn món';
                btn.classList.remove('btn-selected');
            }
        }
    });
}

function setActiveTab(activeEl, inactiveEl, mealTypeValue) {
    activeEl.classList.add('tab-active');
    inactiveEl.classList.remove('tab-active');
    if (mealTypeInput) mealTypeInput.value = mealTypeValue;

    const personalizedGrid = document.getElementById('menu-grid');
    const alacarteGrid = document.getElementById('menu-grid-alacarte');

    if (mealTypeValue === 'Standard') {
        if (personalizedGrid) {
            personalizedGrid.classList.remove('grid-tab-hidden');
            personalizedGrid.style.display = 'grid';
        }
        if (alacarteGrid) {
            alacarteGrid.classList.add('grid-tab-hidden');
            alacarteGrid.style.display = 'none';
        }
    } else {
        if (personalizedGrid) {
            personalizedGrid.classList.add('grid-tab-hidden');
            personalizedGrid.style.display = 'none';
        }
        if (alacarteGrid) {
            alacarteGrid.classList.remove('grid-tab-hidden');
            alacarteGrid.style.display = 'grid';
        }
    }

    syncGridSelection();
    renderSidebarItems();
    updatePriceSummary();
}

if (tabPersonalized) {
    tabPersonalized.addEventListener('click', () => {
        setActiveTab(tabPersonalized, tabAlacarte, 'Standard');
    });
}

if (tabAlacarte) {
    tabAlacarte.addEventListener('click', () => {
        setActiveTab(tabAlacarte, tabPersonalized, 'A-La-Carte');
    });
}

/* ─── Card Toggle ────────────────────────────────── */
/**
 * Toggle selection state for a menu card element.
 * @param {HTMLElement} cardEl
 */
function toggleSelectItem(cardEl) {
    const itemId    = parseInt(cardEl.getAttribute('data-id'), 10);
    const itemName  = cardEl.getAttribute('data-name') || '';
    const rawPrice  = parseFloat(cardEl.getAttribute('data-price')) || 0;
    const itemPrice = rawPrice < 5000 ? rawPrice * 1000 : rawPrice;
    const itemImage = cardEl.getAttribute('data-image') || '';

    const existingIdx = selectedItems.findIndex(i => i.id === itemId);

    if (existingIdx > -1) {
        // Deselect
        selectedItems.splice(existingIdx, 1);
    } else {
        // Select
        selectedItems.push({ id: itemId, name: itemName, price: itemPrice, imageUrl: itemImage });
    }

    // Sync visual state for ALL matching cards in both grids
    const allMatchingCards = document.querySelectorAll(`.menu-grid [data-id="${itemId}"]`);
    allMatchingCards.forEach(card => {
        const cardBtn = card.querySelector('.select-btn');
        if (existingIdx > -1) {
            card.classList.remove('card-selected');
            if (cardBtn) {
                cardBtn.textContent = 'Chọn món';
                cardBtn.classList.remove('btn-selected');
            }
        } else {
            card.classList.add('card-selected');
            if (cardBtn) {
                cardBtn.textContent = 'Đã chọn ✓';
                cardBtn.classList.add('btn-selected');
            }
        }
    });

    renderSidebarItems();
    updatePriceSummary();
}

/* ─── Remove item from sidebar ───────────────────── */
/**
 * Remove an item from selection by ID.
 * @param {number} itemId
 */
function removeSelectedItem(itemId) {
    // Deselect by triggering toggle on one matching card if found in either grid
    const cardEl = document.querySelector(`.menu-grid [data-id="${itemId}"]`);
    if (cardEl) {
        toggleSelectItem(cardEl);
        return;
    }
    // Fallback: remove from state only
    const idx = selectedItems.findIndex(i => i.id === itemId);
    if (idx > -1) {
        selectedItems.splice(idx, 1);
        renderSidebarItems();
        updatePriceSummary();
    }
}

/* ─── Sidebar Renderer ───────────────────────────── */
function renderSidebarItems() {
    const container = document.getElementById('selected-items-container');
    const badge     = document.getElementById('selected-count');

    if (!container) return;

    if (badge) {
        badge.textContent = selectedItems.length;
        if (selectedItems.length > 0) {
            badge.classList.add('has-items');
        } else {
            badge.classList.remove('has-items');
        }
    }

    if (selectedItems.length === 0) {
        container.innerHTML = '<p class="sidebar-empty" id="empty-selection-msg">Chưa chọn món nào</p>';
        return;
    }

    const isAlacarte = (mealTypeInput && mealTypeInput.value === 'A-La-Carte');
    let html = '';
    selectedItems.forEach(item => {
        const priceFormatted = isAlacarte ? formatVND(item.price) : '0đ';
        // Fallback image in case imageUrl is empty
        const imgSrc = item.imageUrl || '/img/pexels-alesiakozik-6544376.jpg';

        html += `
<div class="sidebar-item">
    <img
        class="sidebar-item-img"
        src="${escapeHtml(imgSrc)}"
        alt="${escapeHtml(item.name)}"
        onerror="this.src='/img/pexels-alesiakozik-6544376.jpg'">
    <div class="sidebar-item-info">
        <div class="sidebar-item-name">${escapeHtml(item.name)}</div>
        <div class="sidebar-item-price">${priceFormatted}</div>
    </div>
    <button
        class="sidebar-item-remove"
        type="button"
        onclick="removeSelectedItem(${item.id})"
        aria-label="Xóa ${escapeHtml(item.name)}">Xóa</button>
</div>`;
    });

    container.innerHTML = html;
}

/* ─── Price Calculator ───────────────────────────── */
/**
 * Recalculate and display subtotal / service charge / total.
 * DB stores price in thousands of VND (e.g. 420.00 = 420,000đ).
 */
function updatePriceSummary() {
    const subtotalEl  = document.getElementById('sidebar-subtotal');
    const feeEl       = document.getElementById('sidebar-service-charge');
    const totalEl     = document.getElementById('sidebar-total');

    const isAlacarte = (mealTypeInput && mealTypeInput.value === 'A-La-Carte');

    let subtotal = 0;
    if (isAlacarte) {
        selectedItems.forEach(item => { subtotal += item.price; });
    }

    // Service fee is always calculated at 5% on the UI
    const serviceFee = subtotal * 0.05;
    const total      = subtotal + serviceFee;

    if (subtotalEl) subtotalEl.textContent  = formatVND(subtotal);
    if (feeEl)      feeEl.textContent       = formatVND(serviceFee);
    if (totalEl)    totalEl.textContent     = formatVND(total);
}

/* ─── Form Submission ────────────────────────────── */
function submitSelectionForm() {
    if (selectedItems.length === 0) {
        alert('Vui lòng chọn ít nhất một món ăn trước khi xác nhận!');
        return;
    }

    const hiddenContainer = document.getElementById('hiddenItemsContainer');
    if (!hiddenContainer) return;

    hiddenContainer.innerHTML = '';
    selectedItems.forEach(item => {
        const input = document.createElement('input');
        input.type  = 'hidden';
        input.name  = 'menuItemIds';
        input.value = item.id;
        hiddenContainer.appendChild(input);
    });

    const form = document.getElementById('mealSelectionForm');
    if (form) form.submit();
}

/* ─── Helpers ────────────────────────────────────── */
/**
 * Format a price value to Vietnamese đồng string.
 * Example: 420000 → "420.000đ"
 * @param {number} val  — value in raw VND
 * @returns {string}
 */
function formatVND(val) {
    if (!val || val === 0) return '0đ';
    return Math.round(val).toLocaleString('vi-VN') + 'đ';
}

/**
 * Simple HTML entity escaper to prevent XSS in dynamic innerHTML.
 * @param {string} str
 * @returns {string}
 */
function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

/* ─── Init ───────────────────────────────────────── */
// Initial sidebar render (empty state)
renderSidebarItems();
updatePriceSummary();

// Force hide/show grids on load to ensure sync
const mealTypeValue = mealTypeInput ? mealTypeInput.value : 'Standard';
const personalizedGrid = document.getElementById('menu-grid');
const alacarteGrid = document.getElementById('menu-grid-alacarte');

if (mealTypeValue === 'Standard') {
    if (personalizedGrid) personalizedGrid.style.display = 'grid';
    if (alacarteGrid) alacarteGrid.style.display = 'none';
} else {
    if (personalizedGrid) personalizedGrid.style.display = 'none';
    if (alacarteGrid) alacarteGrid.style.display = 'grid';
}

// Support keyboard activation of cards (Enter / Space)
document.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' || e.key === ' ') {
        const target = e.target;
        if (target.classList.contains('menu-card') && !target.classList.contains('menu-card--disabled')) {
            e.preventDefault();
            toggleSelectItem(target);
        }
    }
});
