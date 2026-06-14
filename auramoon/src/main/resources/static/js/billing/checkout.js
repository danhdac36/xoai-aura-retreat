/**
 * Handle checkout modal logic for UC21 (Hóa đơn Gộp & Check-out)
 */

function handleCheckout() {
    const modal = document.getElementById('checkoutModal');
    const content = document.getElementById('modalContent');
    
    // Hiển thị modal
    modal.classList.remove('hidden');
    modal.classList.add('flex');
    
    // Thêm hiệu ứng fade in / scale up
    setTimeout(() => {
        content.classList.remove('scale-90', 'opacity-0');
        content.classList.add('scale-100', 'opacity-100');
    }, 10);
}

function closeModal() {
    const modal = document.getElementById('checkoutModal');
    const content = document.getElementById('modalContent');
    
    // Thêm hiệu ứng fade out / scale down
    content.classList.remove('scale-100', 'opacity-100');
    content.classList.add('scale-90', 'opacity-0');
    
    // Ẩn modal sau khi hoàn tất hiệu ứng (500ms khớp với duration-500 trong class)
    setTimeout(() => {
        modal.classList.add('hidden');
        modal.classList.remove('flex');
    }, 500);
}
