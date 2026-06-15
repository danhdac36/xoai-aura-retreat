// Javascript functions for Receptionist Check-in Modal

function openCheckInModal(bookingId, totalGuests) {
    const modal = document.getElementById("checkInModal");
    const bookingIdInput = document.getElementById("modalBookingId");
    
    if (modal && bookingIdInput) {
        bookingIdInput.value = bookingId;
        
        // Dynamically filter villas by capacity (totalGuests) and status (AVAILABLE)
        const select = document.getElementById("villaId");
        if (select) {
            const options = select.querySelectorAll("option");
            options.forEach(opt => {
                if (opt.value === "") return; // Skip placeholder option
                const limit = parseInt(opt.getAttribute("data-limit") || "0", 10);
                const status = (opt.getAttribute("data-status") || "").toUpperCase();
                
                // Show if capacity matches AND status is AVAILABLE or AVAILABLE-equivalent (like TRỐNG or AVAILABLE)
                if (limit < totalGuests || (status !== "AVAILABLE" && status !== "TRỐNG" && status !== "")) {
                    opt.style.display = "none";
                    opt.disabled = true;
                } else {
                    opt.style.display = "";
                    opt.disabled = false;
                }
            });
            select.value = ""; // Reset selection
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
