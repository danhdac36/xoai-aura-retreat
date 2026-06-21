document.addEventListener('DOMContentLoaded', () => {
    // Subtle micro-interaction for the inputs
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

    // Background parallax effect
    document.addEventListener('mousemove', (e) => {
        const img = document.querySelector('img');
        if (img) {
            const x = (window.innerWidth - e.pageX * 2) / 100;
            const y = (window.innerHeight - e.pageY * 2) / 100;
            img.style.transform = `scale(1.05) translate(${x}px, ${y}px)`;
        }
    });
});
