// Javascript functions for Receptionist Check-in Modal

function openCheckInModal(bookingId) {
    const modal = document.getElementById("checkInModal");
    const bookingIdInput = document.getElementById("modalBookingId");
    
    if (modal && bookingIdInput) {
        bookingIdInput.value = bookingId;
        modal.classList.remove("hidden");
    }
}

function closeCheckInModal() {
    const modal = document.getElementById("checkInModal");
    if (modal) {
        modal.classList.add("hidden");
    }
}
