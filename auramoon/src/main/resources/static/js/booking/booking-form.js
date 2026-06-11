// JS file for booking form enhancements
document.addEventListener("DOMContentLoaded", () => {
    // Validate that check-in date is not in the past
    const checkinInput = document.getElementById("checkinDate");
    if (checkinInput) {
        const today = new Date().toISOString().split("T")[0];
        checkinInput.min = today;
    }
});
