
        // Micro-interactions for the Inspect buttons
        document.querySelectorAll('button').forEach(btn => {
            btn.addEventListener('mousedown', function() {
                this.style.transform = 'scale(0.97)';
            });
            btn.addEventListener('mouseup', function() {
                this.style.transform = 'scale(1)';
            });
        });

        // Simple row highlight logic
        const rows = document.querySelectorAll('.data-table-row');
        rows.forEach(row => {
            row.addEventListener('click', () => {
                rows.forEach(r => r.classList.remove('bg-secondary-container/10'));
                row.classList.add('bg-secondary-container/10');
            });
        });
    
