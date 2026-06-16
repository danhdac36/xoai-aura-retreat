// Update current date in Vietnamese
function updateDate() {
    const dateInput = document.getElementById('selectedDateInput');
    const selectedDateStr = dateInput ? dateInput.value : null;
    
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    
    let targetDate;
    if (selectedDateStr) {
        targetDate = new Date(selectedDateStr);
    } else {
        targetDate = new Date();
    }
    
    const displayElement = document.getElementById('currentDate');
    if (displayElement) {
        displayElement.innerText = targetDate.toLocaleDateString('vi-VN', options);
    }
}

// Modal toggle logic
function toggleModal(id) {
    const modal = document.getElementById(id);
    if (!modal) return;
    
    if (modal.classList.contains('hidden')) {
        modal.classList.remove('hidden');
        document.body.style.overflow = 'hidden';
    } else {
        modal.classList.add('hidden');
        document.body.style.overflow = 'auto';
    }
}

// Run updateDate on load
document.addEventListener('DOMContentLoaded', function() {
    updateDate();
});
