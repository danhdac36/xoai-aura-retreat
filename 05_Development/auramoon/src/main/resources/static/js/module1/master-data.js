// Modal opening and closing helpers
function openCreateModal() {
    const modal = document.getElementById('createModal');
    if (modal) {
        modal.classList.remove('hidden', 'opacity-0', 'pointer-events-none');
        modal.classList.add('flex');
    }
}

function closeCreateModal() {
    const modal = document.getElementById('createModal');
    if (modal) {
        modal.classList.add('hidden', 'opacity-0', 'pointer-events-none');
        modal.classList.remove('flex');
    }
}

function openEditModalFromButton(button) {
    const modal = document.getElementById('editModal');
    if (modal) {
        const id = button.dataset.id;
        const category = button.dataset.category;
        
        // Reset all inputs in form first
        modal.querySelectorAll('input:not([type="hidden"]), select, textarea').forEach(el => {
            if (el.type === 'checkbox') {
                el.checked = false;
            } else {
                el.value = '';
            }
        });

        // Populate values by name matching camelCase dataset properties
        for (const [key, val] of Object.entries(button.dataset)) {
            const input = modal.querySelector(`[name="${key}"]`);
            if (input) {
                if (input.type === 'checkbox') {
                    input.checked = (val === 'true');
                } else {
                    input.value = val;
                }
            }
        }

        // Set action url
        const form = modal.querySelector('form');
        if (form && category && id) {
            form.action = `/admin/master-data/${category}/${id}/update`;
        }

        modal.classList.remove('hidden', 'opacity-0', 'pointer-events-none');
        modal.classList.add('flex');
    }
}

function closeEditModal() {
    const modal = document.getElementById('editModal');
    if (modal) {
        modal.classList.add('hidden', 'opacity-0', 'pointer-events-none');
        modal.classList.remove('flex');
    }
}

function confirmDelete(id) {
    const activeTab = getActiveTabCategory();
    const modal = document.getElementById('deleteModal');
    if (modal) {
        const form = document.getElementById('deleteForm');
        form.action = `/admin/master-data/${activeTab}/${id}/delete`;
        
        modal.classList.remove('hidden', 'opacity-0', 'pointer-events-none');
        modal.classList.add('flex');
        void modal.offsetWidth;
        modal.classList.remove('opacity-0', 'pointer-events-none');
    }
}

function confirmDeleteFromButton(button) {
    const modal = document.getElementById('deleteModal');
    if (modal) {
        const id = button.dataset.id;
        const category = button.dataset.category;
        const form = document.getElementById('deleteForm');
        if (form && category && id) {
            form.action = `/admin/master-data/${category}/${id}/delete`;
        }
        
        modal.classList.remove('hidden', 'opacity-0', 'pointer-events-none');
        modal.classList.add('flex');
        void modal.offsetWidth;
        modal.classList.remove('opacity-0', 'pointer-events-none');
    }
}

function closeDeleteModal() {
    const modal = document.getElementById('deleteModal');
    if (modal) {
        modal.classList.add('opacity-0', 'pointer-events-none');
        setTimeout(() => {
            modal.classList.add('hidden');
            modal.classList.remove('flex');
        }, 300);
    }
}

function switchTab(category, element) {
    fetch(`/admin/master-data/${category}`, {
        headers: { 'X-Requested-With': 'XMLHttpRequest' }
    })
    .then(res => res.text())
    .then(html => {
        const temp = document.createElement('div');
        temp.innerHTML = html;
        const newContent = temp.querySelector('#main-content') || temp.firstElementChild;
        const oldContent = document.getElementById('main-content');
        if (oldContent && newContent) {
            oldContent.replaceWith(newContent);
            
            // Update active tab styles
            document.querySelectorAll('.tab-link').forEach(btn => {
                btn.classList.remove('border-primary', 'text-primary');
                btn.classList.add('border-transparent', 'text-secondary/70');
            });
            element.classList.remove('border-transparent', 'text-secondary/70');
            element.classList.add('border-primary', 'text-primary');
        }
    })
    .catch(err => console.error('Error switching tab:', err));
}

// Quick status toggle with CSRF validation
function toggleStatus(id) {
    const activeTab = getActiveTabCategory();
    const csrfToken = document.querySelector('input[name="_csrf"]')?.value;
    const headers = { 'X-Requested-With': 'XMLHttpRequest' };
    if (csrfToken) {
        headers['X-CSRF-TOKEN'] = csrfToken;
    }

    fetch(`/admin/master-data/${activeTab}/${id}/toggle-status`, {
        method: 'POST',
        headers: headers
    })
    .then(res => res.text())
    .then(html => {
        const temp = document.createElement('div');
        temp.innerHTML = html;
        const newContent = temp.querySelector('#main-content') || temp.firstElementChild;
        const oldContent = document.getElementById('main-content');
        if (oldContent && newContent) {
            oldContent.replaceWith(newContent);
        }
    })
    .catch(err => console.error('Error toggling status:', err));
}

function getActiveTabCategory() {
    const activeBtn = document.querySelector('.tab-link.border-primary');
    if (activeBtn) {
        const onclickAttr = activeBtn.getAttribute('onclick');
        if (onclickAttr) {
            const match = onclickAttr.match(/switchTab\('([^']+)'/);
            if (match && match[1]) {
                return match[1];
            }
        }
    }
    return 'retreat-packages';
}
