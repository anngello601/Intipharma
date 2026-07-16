/**
 * Sidebar Hamburguesa - Toggle
 */
function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.querySelector('.sidebar-overlay');
    if (sidebar) {
        sidebar.classList.toggle('open');
        if (overlay) {
            overlay.classList.toggle('active');
        }
        // Opcional: bloquear scroll del body cuando el sidebar está abierto
        document.body.style.overflow = sidebar.classList.contains('open') ? 'hidden' : '';
    }
}

// Cerrar al hacer clic en overlay
document.addEventListener('DOMContentLoaded', function() {
    const overlay = document.querySelector('.sidebar-overlay');
    if (overlay) {
        overlay.addEventListener('click', function() {
            toggleSidebar();
        });
    }

    // Cerrar al hacer clic en un enlace del menú (solo en móvil)
    const navLinks = document.querySelectorAll('.sidebar-nav a, .btn-logout');
    navLinks.forEach(link => {
        link.addEventListener('click', function() {
            if (window.innerWidth <= 768) {
                toggleSidebar();
            }
        });
    });

    // Cerrar al redimensionar la ventana a > 768px (modo PC)
    window.addEventListener('resize', function() {
        if (window.innerWidth > 768) {
            const sidebar = document.querySelector('.sidebar');
            const overlay = document.querySelector('.sidebar-overlay');
            if (sidebar) sidebar.classList.remove('open');
            if (overlay) overlay.classList.remove('active');
            document.body.style.overflow = '';
        }
    });
});