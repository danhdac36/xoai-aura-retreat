// Javascript functions for Receptionist Check-in Modal

function openCheckInModal(button) {
    const modal = document.getElementById("checkInModal");
    const bookingIdInput = document.getElementById("modalBookingId");
    const fullNameInput = document.getElementById("fullName");
    const phoneInput = document.getElementById("phone");
    const genderInput = document.getElementById("gender");
    const dobInput = document.getElementById("dateOfBirth");
    
    if (modal && bookingIdInput) {
        bookingIdInput.value = button.getAttribute("data-booking-id");
        if (fullNameInput) fullNameInput.value = button.getAttribute("data-guest-name") || "";
        if (phoneInput) phoneInput.value = button.getAttribute("data-guest-phone") || "";
        if (genderInput) genderInput.value = button.getAttribute("data-guest-gender") || "Male";
        if (dobInput) dobInput.value = button.getAttribute("data-guest-dob") || "";
        
        modal.classList.remove("hidden");
    }
}

function closeCheckInModal() {
    const modal = document.getElementById("checkInModal");
    if (modal) {
        modal.classList.add("hidden");
    }
}
