// Micro-interaction for booking cards on tap
document.querySelectorAll('.organic-shadow').forEach(function(card) {
    card.addEventListener('mousedown', function() {
        card.style.transform = 'scale(0.98)';
        card.style.transition = 'transform 0.1s ease-out';
    });
    card.addEventListener('mouseup', function() {
        card.style.transform = 'scale(1)';
    });
    card.addEventListener('mouseleave', function() {
        card.style.transform = 'scale(1)';
    });
});
