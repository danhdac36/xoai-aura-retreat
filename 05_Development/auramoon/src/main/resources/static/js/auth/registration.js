document.addEventListener('DOMContentLoaded', () => {
    // Micro-interactions for form inputs (label coloring on focus)
    document.querySelectorAll('input').forEach(input => {
        input.addEventListener('focus', () => {
            const label = input.parentElement.querySelector('label');
            if (label) {
                label.style.color = '#566342'; // secondary color (sage green)
            }
        });
        input.addEventListener('blur', () => {
            const label = input.parentElement.querySelector('label');
            if (label) {
                label.style.color = '';
            }
        });
    });

    // Password visibility toggle logic
    const toggleBtn = document.getElementById('togglePassword');
    if (toggleBtn) {
        toggleBtn.addEventListener('click', () => {
            const passwordInput = document.getElementById('password');
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            const icon = toggleBtn.querySelector('.material-symbols-outlined');
            if (icon) {
                icon.textContent = type === 'password' ? 'visibility_off' : 'visibility';
            }
        });
    }

    // Image Slideshow Transition Effect
    const bgImages = document.querySelectorAll('.brand-bg-img');
    let currentIndex = 0;
    if (bgImages.length > 1) {
        setInterval(() => {
            // Fade out current image
            bgImages[currentIndex].classList.replace('opacity-100', 'opacity-0');
            // Move to next image
            currentIndex = (currentIndex + 1) % bgImages.length;
            // Fade in next image
            bgImages[currentIndex].classList.replace('opacity-0', 'opacity-100');
        }, 5000); // Transition every 5 seconds
    }

    // Background parallax effect
    document.addEventListener('mousemove', (e) => {
        const bgContainer = document.querySelector('.brand-bg-container');
        if (bgContainer) {
            const x = (window.innerWidth - e.pageX * 2) / 120;
            const y = (window.innerHeight - e.pageY * 2) / 120;
            bgContainer.style.transform = `scale(1.05) translate(${x}px, ${y}px)`;
        }
    });
});
