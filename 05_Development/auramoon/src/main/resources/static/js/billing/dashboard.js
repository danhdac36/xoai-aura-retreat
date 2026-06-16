document.addEventListener('DOMContentLoaded', () => {
    const bars = document.querySelectorAll('.chart-bar');
    bars.forEach(bar => {
        const height = bar.style.height;
        bar.style.height = '0px';
        setTimeout(() => {
            bar.style.height = height;
        }, 200);
    });
});
