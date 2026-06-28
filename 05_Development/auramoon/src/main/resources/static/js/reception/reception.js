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
        
        // Smart filtering for Villa Assignment
        const requestedType = button.getAttribute("data-requested-villa-type");
        const villaSelect = document.getElementById("villaId");
        if (villaSelect) {
            villaSelect.value = ""; // reset selection
            Array.from(villaSelect.options).forEach(option => {
                if (option.value === "") return; // keep the placeholder
                const optionType = option.getAttribute("data-villa-type");
                if (requestedType && requestedType !== "Không xác định") {
                    if (optionType === requestedType) {
                        option.style.display = "block";
                    } else {
                        option.style.display = "none";
                    }
                } else {
                    option.style.display = "block"; // Show all if requested type is unknown
                }
            });
        }
        
        modal.classList.remove("hidden");
    }
}

function closeCheckInModal() {
    const modal = document.getElementById("checkInModal");
    if (modal) {
        modal.classList.add("hidden");
    }
}
