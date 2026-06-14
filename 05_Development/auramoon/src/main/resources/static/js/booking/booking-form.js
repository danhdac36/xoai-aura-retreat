// JS file for booking form enhancements and dynamic calculations
document.addEventListener("DOMContentLoaded", () => {
    const bookingForm = document.getElementById("bookingForm");
    if (!bookingForm) return;

    const checkinInput = document.getElementById("checkinDate");
    const checkoutDisplay = document.getElementById("checkoutDateDisplay");
    const surchargeDisplay = document.getElementById("surchargeDisplay");
    const totalPriceDisplay = document.getElementById("totalPriceDisplay");
    const villaRadios = document.querySelectorAll('input[name="villaTypeId"]');

    // Read parameters from form data attributes
    const durationDays = parseInt(bookingForm.dataset.duration) || 3;
    const basePrice = parseFloat(bookingForm.dataset.price) || 0;

    // Helper to format currency
    const formatCurrency = (value) => {
        return new Intl.NumberFormat("vi-VN").format(value) + " đ";
    };

    // 1. Set minimum check-in date to today
    if (checkinInput) {
        const today = new Date().toISOString().split("T")[0];
        checkinInput.min = today;

        // Listen for check-in date changes
        checkinInput.addEventListener("change", updateCheckoutDate);
        updateCheckoutDate();
    }

    // 2. Update checkout date dynamically
    function updateCheckoutDate() {
        if (!checkinInput || !checkinInput.value) return;

        const checkin = new Date(checkinInput.value);
        checkin.setDate(checkin.getDate() + durationDays);

        const yyyy = checkin.getFullYear();
        const mm = String(checkin.getMonth() + 1).padStart(2, "0");
        const dd = String(checkin.getDate()).padStart(2, "0");

        if (checkoutDisplay) {
            checkoutDisplay.textContent = `${dd}/${mm}/${yyyy}`;
        }
    }

    // 3. Calculate surcharge and total price dynamically
    function updatePricing() {
        let surchargePerDay = 0;
        const selectedRadio = document.querySelector('input[name="villaTypeId"]:checked');

        if (selectedRadio) {
            surchargePerDay = parseFloat(selectedRadio.getAttribute("data-price")) || 0;
        }

        const totalSurcharge = surchargePerDay * durationDays;
        const finalPrice = basePrice + totalSurcharge;

        if (surchargeDisplay) {
            surchargeDisplay.textContent = formatCurrency(totalSurcharge);
        }
        if (totalPriceDisplay) {
            totalPriceDisplay.textContent = formatCurrency(finalPrice);
        }
    }

    // Attach event listeners to villa selection radios
    villaRadios.forEach(radio => {
        radio.addEventListener("change", updatePricing);
    });

    // Run initial pricing calculation
    updatePricing();
});

