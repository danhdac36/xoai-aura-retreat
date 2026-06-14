document.addEventListener("DOMContentLoaded", function() {
    const ratingGroup = document.querySelector('.star-rating');
    if (!ratingGroup) return;
    
    const stars = ratingGroup.querySelectorAll('.material-symbols-outlined');
    const ratingInput = document.getElementById('ratingInput');
    const submitBtn = document.getElementById('submitReviewBtn');
    
    stars.forEach(star => {
        star.addEventListener('click', () => {
            const value = parseInt(star.getAttribute('data-value'));
            
            // Update hidden input
            if (ratingInput) {
                ratingInput.value = value;
            }
            
            // Reset all stars in this group
            stars.forEach(s => {
                s.classList.remove('active');
                s.style.fontVariationSettings = "'FILL' 0";
            });

            // Set active stars up to the clicked value
            for (let i = 0; i < value; i++) {
                stars[i].classList.add('active');
                stars[i].style.fontVariationSettings = "'FILL' 1";
            }
        });

        // Hover effects
        star.addEventListener('mouseenter', () => {
            const value = parseInt(star.getAttribute('data-value'));
            stars.forEach((s, idx) => {
                if (idx < value) s.classList.add('opacity-70');
            });
        });

        star.addEventListener('mouseleave', () => {
            stars.forEach(s => s.classList.remove('opacity-70'));
        });
    });

    if (submitBtn) {
        submitBtn.addEventListener('click', function(e) {
            // Check if rating is selected
            if (!ratingInput || ratingInput.value === "0" || ratingInput.value === "") {
                e.preventDefault();
                alert("Vui lòng chọn số sao đánh giá trước khi gửi!");
                return;
            }
            
            const originalText = submitBtn.innerText;
            submitBtn.innerText = "Đang gửi...";
            submitBtn.classList.add('opacity-50', 'pointer-events-none');
            
            // Form will submit normally since it's inside a <form> and this is a submit button.
        });
    }
});
