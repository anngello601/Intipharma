function applyFilters() {
    // 1. Obtener valores de todos los filtros
    const searchTerm = document.getElementById('tableSearch').value.toLowerCase();
    const ubFilter = document.getElementById('filterUbicacion').value.toLowerCase();
    const estFilter = document.getElementById('filterEstado').value.toLowerCase();
    const soloAlertas = document.getElementById('soloAlertas').checked;

    const rows = document.querySelectorAll('#tableBody tr');

    rows.forEach(row => {
        // 2. Extraer datos de la fila
        const nombre = row.cells[0].innerText.toLowerCase();
        const lote = row.cells[1].innerText.toLowerCase();
        const rowUb = row.cells[3].innerText.toLowerCase().trim();
        const rowEst = row.cells[5].innerText.toLowerCase().trim();

        // 3. Validar filtros
        // Buscar por nombre o lote
        const matchesSearch = nombre.includes(searchTerm) || lote.includes(searchTerm);

        // Filtrar por ubicación (si el select está vacío, incluye todo)
        const matchesUb = ubFilter === "" || rowUb.includes(ubFilter);

        // Filtrar por estado (si el select está vacío, incluye todo)
        const matchesEst = estFilter === "" || rowEst.includes(estFilter);

        // Filtrar por checkbox "Solo alertas"
        // Si está marcado, solo mostrar si el estado contiene 'bajo stock' o 'agotado'
        const matchesAlert = !soloAlertas || (rowEst.includes('bajo stock') || rowEst.includes('agotado'));

        // 4. Aplicar visibilidad
        if (matchesSearch && matchesUb && matchesEst && matchesAlert) {
            row.style.display = "";
        } else {
            row.style.display = "none";
        }
    });
}

// Variable global para guardar el ID que se va a eliminar
let idProductoAEliminar = null;



function saveForm() {
    // 1. Recopilar datos del formulario
    const formData = {
        idProducto: document.getElementById('id_producto').value,
        nombreProducto: document.getElementById('nombre_producto').value,
        lote: document.getElementById('lote').value,
        cantidad: document.getElementById('cantidad').value,
        // ... agrega todos los campos que tengas en tu formulario
    };

    // 2. Enviar datos al controlador
    fetch('/producto/guardar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
    })
        .then(response => {
            if (response.ok) {
                closeModal('modalForm');
                location.reload(); // Recarga simple para mostrar el cambio
            } else {
                alert('Error al guardar');
            }
        });
}

// --- 2. LÓGICA DE EDICIÓN CON MODAL ---
const listaProductos = /*[[${listaProductos}]]*/[];

// Pon solo esto en tu archivo .js
modalEditar.addEventListener('show.bs.modal', function (event) {
    const button = event.relatedTarget;

    document.getElementById('editId').value = button.getAttribute('data-id');
    document.getElementById('editNombre').value = button.getAttribute('data-nombre');
    document.getElementById('editStock').value = button.getAttribute('data-stock');
    document.getElementById('editCodigo').value = button.getAttribute('data-codigo');
    document.getElementById('editCategoria').value = button.getAttribute('data-id-cat');
    document.getElementById('editLote').value = button.getAttribute('data-lote');

    // BORRA ESTA LÍNEA TAMBIÉN:
    // document.getElementById('editIdUsuario').value = button.getAttribute('data-id-user');
});
// Asegúrate de que esta línea esté en tu archivo JS


function confirmDelete() {
    // Lógica para enviar eliminación al controlador
    window.location.href = '/producto/eliminar/' + idProductoAEliminar;
}
function abrirMiModal(btn) {
    // 1. Cargamos los datos del botón al formulario
    document.getElementById('editId').value = btn.getAttribute('data-id');
    document.getElementById('editNombre').value = btn.getAttribute('data-nombre');
    document.getElementById('editStock').value = btn.getAttribute('data-stock');

    // 2. FORZAMOS la apertura del modal con el objeto Bootstrap
    var elementoModal = document.getElementById('modalEditar');
    var modalInstancia = new bootstrap.Modal(elementoModal);
    modalInstancia.show();
}