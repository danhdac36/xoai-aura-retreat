
        document.addEventListener('DOMContentLoaded', () => {
            const hideButtons = document.querySelectorAll('button:has(span:not(.material-symbols-outlined)), button:contains("Hide")');
            // Using a broader selector for the hide buttons
            const buttons = document.querySelectorAll('button');
            buttons.forEach(btn => {
                if (btn.innerText.includes('Hide')) {
                    btn.addEventListener('click', function() {
                        const row = this.closest('.bg-white, .md:bg-transparent');
                        row.style.opacity = '0';
                        row.style.transform = 'translateY(10px)';
                        row.style.transition = 'all 0.4s ease';
                        setTimeout(() => {
                            row.style.display = 'none';
                        }, 400);
                    });
                }
            });
        });
    
