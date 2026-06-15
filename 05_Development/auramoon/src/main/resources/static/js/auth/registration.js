document.addEventListener('DOMContentLoaded', () => {
    // Micro-interactions for form inputs
    document.querySelectorAll('.luxury-input').forEach(input => {
        input.addEventListener('focus', () => {
            const label = input.parentElement.querySelector('label');
            if (label) {
                label.classList.add('text-secondary');
            }
        });
        input.addEventListener('blur', () => {
            const label = input.parentElement.querySelector('label');
            if (label) {
                label.classList.remove('text-secondary');
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
});
