
document.addEventListener('DOMContentLoaded', () => {
    const buttons = document.querySelectorAll('button');
    buttons.forEach(btn => {
        if (btn.innerText.includes('Hide')) {
            btn.addEventListener('click', function(e) {
                e.preventDefault();
                const reviewId = this.getAttribute('data-id');
                if(reviewId) {
                    fetch('/manager/reviews/hide/' + reviewId, { method: 'POST' })
                    .then(res => {
                        if(res.ok) {
                            const row = this.closest('.bg-white, .md\\\\:bg-transparent');
                            row.style.opacity = '0';
                            row.style.transform = 'translateY(10px)';
                            row.style.transition = 'all 0.4s ease';
                            setTimeout(() => {
                                row.style.display = 'none';
                            }, 400);
                        }
                    });
                }
            });
        }
    });
});

