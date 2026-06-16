document.addEventListener('DOMContentLoaded', () => {
    // Simple micro-interaction for table rows
    document.querySelectorAll('tbody tr').forEach(row => {
        row.addEventListener('click', () => {
            // Potential expansion logic here
            row.classList.toggle('bg-surface-container-high');
        });
    });

    // Search bar focus interaction if exists
    const searchInput = document.querySelector('input[type="text"]');
    if (searchInput) {
        searchInput.addEventListener('focus', () => {
            searchInput.parentElement.classList.add('ring-1', 'ring-primary');
        });
        searchInput.addEventListener('blur', () => {
            searchInput.parentElement.classList.remove('ring-1', 'ring-primary');
        });
    }
});
