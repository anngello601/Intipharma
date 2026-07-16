// ============================================
// 1. GESTIÓN DE USUARIOS
// ============================================

function abrirModalAgregarUsuario() {
    // Limpiar campos del modal
    document.getElementById('editId').value = '';
    document.getElementById('editDni').value = '';
    document.getElementById('editNombre').value = '';
    document.getElementById('editApellido').value = '';
    document.getElementById('editCorreo').value = '';
    document.getElementById('editPassword').value = '';
    // Cambiar título del modal
    document.querySelector('#editarUsuarioModal .modal-title').textContent = 'Nuevo Usuario';
    // Abrir modal
    var modal = new bootstrap.Modal(document.getElementById('editarUsuarioModal'));
    modal.show();
}

function llenarModal(button) {
    // Editar usuario existente
    document.querySelector('#editarUsuarioModal .modal-title').textContent = 'Editar Usuario';
    document.getElementById('editId').value = button.getAttribute('data-id');
    document.getElementById('editDni').value = button.getAttribute('data-dni');
    document.getElementById('editNombre').value = button.getAttribute('data-nombre');
    document.getElementById('editApellido').value = button.getAttribute('data-apellido');
    document.getElementById('editCorreo').value = button.getAttribute('data-correo');
    document.getElementById('editRol').value = button.getAttribute('data-rol');
    document.getElementById('editPassword').value = '';
}

function filtrarPorNombre() {
    const input = document.getElementById('inputBuscarNombre');
    const filtro = input.value.toLowerCase();
    const filas = document.querySelectorAll('#tablaUsuarios tr');
    filas.forEach(fila => {
        const nombre = fila.querySelector('td:first-child')?.textContent.toLowerCase() || '';
        fila.style.display = nombre.includes(filtro) ? '' : 'none';
    });
    // Reiniciar paginación después del filtro
    reiniciarPaginacion('tablaProductos', 5);
}

// ============================================
// 2. CONFIGURACIÓN DE ALERTAS
// ============================================

function guardarConfiguracionAlertas() {
    const dias = document.getElementById('diasAlerta').value;
    const prioridad = document.getElementById('prioridadAlerta').value;
    
    // Enviar al backend (ejemplo con fetch)
    fetch('/configuracion/alertas', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ dias: dias, prioridad: prioridad })
    })
    .then(response => response.json())
    .then(data => {
        mostrarToast('Configuración de alertas guardada exitosamente', 'success');
    })
    .catch(error => {
        mostrarToast('Error al guardar configuración', 'danger');
    });
}

// ============================================
// 3. CORREOS AUTOMÁTICOS
// ============================================

function guardarConfiguracionCorreos() {
    const correo = document.getElementById('correoPrincipal').value;
    const alertasAuto = document.getElementById('alertasAutomaticas').checked;
    
    fetch('/configuracion/correos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ correo: correo, alertasAuto: alertasAuto })
    })
    .then(response => response.json())
    .then(data => {
        mostrarToast('Configuración de correos guardada', 'success');
    })
    .catch(error => {
        mostrarToast('Error al guardar configuración de correos', 'danger');
    });
}

// ============================================
// 4. SEGURIDAD (Cambio de contraseña)
// ============================================

function cambiarPassword() {
    const nueva = document.getElementById('nuevaPassword').value;
    const confirmar = document.getElementById('confirmarPassword').value;
    
    if (nueva !== confirmar) {
        mostrarToast('Las contraseñas no coinciden', 'danger');
        return;
    }
    if (nueva.length < 6) {
        mostrarToast('La contraseña debe tener al menos 6 caracteres', 'danger');
        return;
    }
    
    fetch('/configuracion/cambiar-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ password: nueva })
    })
    .then(response => response.json())
    .then(data => {
        mostrarToast('Contraseña actualizada correctamente', 'success');
        document.getElementById('nuevaPassword').value = '';
        document.getElementById('confirmarPassword').value = '';
    })
    .catch(error => {
        mostrarToast('Error al cambiar contraseña', 'danger');
    });
}

// ============================================
// 5. NOTIFICACIONES
// ============================================

function guardarNotificaciones() {
    const sistema = document.getElementById('notifSistema').checked;
    const sonido = document.getElementById('notifSonido').checked;
    
    fetch('/configuracion/notificaciones', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sistema: sistema, sonido: sonido })
    })
    .then(response => response.json())
    .then(data => {
        mostrarToast('Preferencias de notificaciones guardadas', 'success');
    })
    .catch(error => {
        mostrarToast('Error al guardar preferencias', 'danger');
    });
}

// ============================================
// 6. COPIA DE SEGURIDAD (BACKUP)
// ============================================

function exportarExcel() {
    // Redirigir al endpoint que genera el Excel
    window.location.href = '/configuracion/exportar-excel';
    mostrarToast('Descargando archivo Excel...', 'info');
}

function exportarPDF() {
    window.location.href = '/configuracion/exportar-pdf';
    mostrarToast('Descargando archivo PDF...', 'info');
}

function generarBackupCompleto() {
    if (confirm('¿Estás seguro de generar un respaldo completo de la base de datos?')) {
        fetch('/configuracion/backup', {
            method: 'POST'
        })
        .then(response => response.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `backup-${new Date().toISOString().slice(0,10)}.sql`;
            document.body.appendChild(a);
            a.click();
            a.remove();
            mostrarToast('Backup generado exitosamente', 'success');
            // Actualizar fecha de última copia
            const now = new Date();
            document.getElementById('ultimaCopia').textContent = now.toLocaleDateString('es-PE');
        })
        .catch(error => {
            mostrarToast('Error al generar backup', 'danger');
        });
    }
}

// ============================================
// 7. TOAST (notificaciones emergentes)
// ============================================

function mostrarToast(mensaje, tipo = 'success') {
    const container = document.getElementById('toastContainer');
    if (!container) {
        // Crear contenedor si no existe
        const div = document.createElement('div');
        div.id = 'toastContainer';
        div.className = 'toast-container position-fixed bottom-0 end-0 p-3';
        document.body.appendChild(div);
    }
    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${tipo} border-0`;
    toast.role = 'alert';
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">${mensaje}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;
    document.getElementById('toastContainer').appendChild(toast);
    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();
    setTimeout(() => {
        toast.remove();
    }, 5000);
}

// ============================================
// 8. NAVEGACIÓN ENTRE SECCIONES
// ============================================

function mostrarSeccion(id) {
    document.querySelectorAll('.seccion').forEach(el => el.classList.add('d-none'));
    document.getElementById(id).classList.remove('d-none');
    
    // Resaltar botón del menú
    document.querySelectorAll('.config-item').forEach(el => el.classList.remove('active-option'));
    document.querySelector(`.config-item[onclick*="${id}"]`)?.classList.add('active-option');
}