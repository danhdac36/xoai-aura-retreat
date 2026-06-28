// Dashboard specific JS for Housekeeping (UC28)
document.addEventListener("DOMContentLoaded", function() {
    console.log("Housekeeping Dashboard loaded successfully.");
    
    // Add any necessary interactive logic here in the future
    // e.g. confirming assignments or rejections
    const rejectButtons = document.querySelectorAll("form[action$='/reject'] button");
    rejectButtons.forEach(btn => {
        btn.addEventListener('click', function(e) {
            if(!confirm("Bạn có chắc chắn muốn TỪ CHỐI và yêu cầu dọn lại Villa này không?")) {
                e.preventDefault();
            }
        });
    });
});

function promptReportMaintenance(villaId) {
    document.getElementById('modalVillaId').value = villaId;
    document.getElementById('modalMaintenanceNote').value = '';
    document.getElementById('maintenanceModal').classList.remove('hidden');
}

function closeMaintenanceModal() {
    document.getElementById('maintenanceModal').classList.add('hidden');
}
