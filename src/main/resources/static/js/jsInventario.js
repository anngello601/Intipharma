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

function llenarModalEditar(boton) {
    // Captura los valores
    document.getElementById('editId').value = boton.getAttribute('data-id');
    document.getElementById('editNombre').value = boton.getAttribute('data-nombre');
    document.getElementById('editCodigo').value = boton.getAttribute('data-codigo');
    document.getElementById('editLote').value = boton.getAttribute('data-lote');
    document.getElementById('editLaboratorio').value = boton.getAttribute('data-laboratorio');
    document.getElementById('editPrecio').value = boton.getAttribute('data-precio');
    document.getElementById('editCantidad').value = boton.getAttribute('data-cantidad');
    document.getElementById('editVencimiento').value = boton.getAttribute('data-vencimiento');
    document.getElementById('editEstado').value = boton.getAttribute('data-estado');
    //Nuevo
    document.getElementById('editDescripcion').value = boton.getAttribute('data-descripcion');
    document.getElementById('editRegistro').value = boton.getAttribute('data-registro');
    // Ubicación
    document.getElementById('editCategoria').value = boton.getAttribute('data-categoria');
    document.getElementById('editUbicacion').value = boton.getAttribute('data-ubicacion');
    document.getElementById('editUnidad').value = boton.getAttribute('data-unidad');
    // FORZAR SELECCIÓN:
    let catId = boton.getAttribute('data-categoria');
    let selectCat = document.getElementById('editCategoria');

    if (catId) {
        selectCat.value = catId;
    } else {
        selectCat.value = ""; // Si no tiene categoría, selecciona la opción vacía
    }
    // Cambiar título
    document.querySelector('#modalEditar .modal-title').innerText = "Editar Producto";

    // Abrir el modal
    var myModal = new bootstrap.Modal(document.getElementById('modalEditar'));
    myModal.show();

}
// Función para cerrar el modal de eliminar
function cerrarModalEliminar() {
    document.getElementById('modalDelete').style.display = 'none';
}

// Función para abrir el modal (debe ser llamada por el botón de la tabla)
function prepararEliminacion(boton) {
    const id = boton.getAttribute('data-id');
    document.getElementById('idProductoAEliminar').value = id;
    document.getElementById('modalDelete').style.display = 'block';
}

// Función para ejecutar la eliminación
function ejecutarEliminacion() {
    const id = document.getElementById('idProductoAEliminar').value;
    if (id) {
        window.location.href = '/eliminarInventario/' + id;
    }
}

function prepararFormularioAgregar() {
    // 1. Limpiar todos los campos del formulario
    document.getElementById('editId').value = ""; // Si el ID está vacío, Spring entiende que es un INSERT
    document.getElementById('editNombre').value = "";
    document.getElementById('editCodigo').value = "";
    document.getElementById('editDescripcion').value = "";
    document.getElementById('editLote').value = "";
    document.getElementById('editLaboratorio').value = "";
    document.getElementById('editRegistro').value = "";
    document.getElementById('editPrecio').value = "";
    document.getElementById('editCantidad').value = "";
    document.getElementById('editUnidad').value = "";
    document.getElementById('editVencimiento').value = "";
    document.getElementById('editEstado').value = "Activo"; // Valor por defecto
    document.getElementById('editCategoria').value = "";
    document.getElementById('editUbicacion').value = "";

    // 2. Cambiar título (opcional pero recomendado)
    document.querySelector('#modalEditar .modal-title').innerText = "Nuevo Producto";

    // 3. Abrir el modal usando Bootstrap
    var myModal = new bootstrap.Modal(document.getElementById('modalEditar'));
    myModal.show();
}