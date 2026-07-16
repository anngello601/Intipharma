/**
 * jsProductos.js - Lógica para la gestión de productos
 */
// Después de DOMContentLoaded o al inicio
document.addEventListener('DOMContentLoaded', function() {
    const cantidadInput = document.getElementById('editCantidad');
    const selectEstado = document.getElementById('editEstado');
    const hiddenEstado = document.getElementById('editEstadoHidden');

    if (cantidadInput) {
        cantidadInput.addEventListener('input', function() {
            const cant = parseInt(this.value) || 0;
            const nuevoEstado = cant <= 10 ? 'Bajo stock' : 'Activo';
            selectEstado.value = nuevoEstado;
            hiddenEstado.value = nuevoEstado;
        });
    }
});
// ==========================================
// FILTRO DE BÚSQUEDA Y CATEGORÍA
// ==========================================
document.getElementById('buscarProducto')?.addEventListener('keyup', filtrarTabla);
document.getElementById('filtroCategoria')?.addEventListener('change', filtrarTabla);

function filtrarTabla() {
    const texto = document.getElementById('buscarProducto')?.value.toLowerCase() || '';
    const catFiltro = document.getElementById('filtroCategoria')?.value || '';
    const filas = document.querySelectorAll('#tablaProductos tbody tr');

    filas.forEach(fila => {
        // Ajusta los índices según tu tabla (0: ID, 1: Nombre, 2: Código, 3: Categoría, etc.)
        const nombre = fila.cells[1]?.innerText.toLowerCase() || '';
        const cat = fila.cells[3]?.innerText || '';
        const coincideTexto = nombre.includes(texto);
        const coincideCat = (catFiltro === "" || cat === catFiltro);
        fila.style.display = (coincideTexto && coincideCat) ? "" : "none";
    });
}

// ==========================================
// MODAL EDITAR / AGREGAR
// ==========================================
function llenarModalEditar(boton) {
    // Captura los valores desde los atributos data-
    document.getElementById('editId').value = boton.getAttribute('data-id') || '';
    document.getElementById('editNombre').value = boton.getAttribute('data-nombre') || '';
    document.getElementById('editCantidad').value = boton.getAttribute('data-cantidad') || '';
    document.getElementById('editCodigo').value = boton.getAttribute('data-codigo') || '';
    document.getElementById('editDescripcion').value = boton.getAttribute('data-descripcion') || '';
    document.getElementById('editCategoria').value = boton.getAttribute('data-categoria') || '';

    // Ubicación: obtener el valor y forzarlo en el select
    const ubicacionVal = boton.getAttribute('data-ubicacion');
    const selectUbicacion = document.getElementById('editUbicacion');
    const cantidad = parseInt(boton.getAttribute('data-cantidad')) || 0;
    document.getElementById('editCantidad').value = cantidad;
    // Calcular estado según cantidad
    const estado = cantidad <= 10 ? 'Bajo stock' : 'Activo';
    const selectEstado = document.getElementById('editEstado');
    const hiddenEstado = document.getElementById('editEstadoHidden');

    // Forzar el valor en el select (deshabilitado)
    selectEstado.value = estado;
    hiddenEstado.value = estado;
    //Ubicacion 
    if (selectUbicacion) {
        // Buscar la opción con ese valor y seleccionarla
        const optionExists = Array.from(selectUbicacion.options).some(opt => opt.value === ubicacionVal);
        selectUbicacion.value = optionExists ? ubicacionVal : '';
        // Disparar evento change para actualizar cualquier listener
        selectUbicacion.dispatchEvent(new Event('change'));
    }

    document.getElementById('editLote').value = boton.getAttribute('data-lote') || '';
    document.getElementById('editLaboratorio').value = boton.getAttribute('data-laboratorio') || '';
    document.getElementById('editRegistro').value = boton.getAttribute('data-registro') || '';
    document.getElementById('editPrecio').value = boton.getAttribute('data-precio') || '';
    document.getElementById('editUnidad').value = boton.getAttribute('data-unidad') || 'Caja';
    document.getElementById('editVencimiento').value = boton.getAttribute('data-vencimiento') || '';
    document.getElementById('editEstado').value = boton.getAttribute('data-estado') || 'Activo';

    // Cambiar título del modal
    document.querySelector('#modalEditar .modal-title').innerText = "Editar Producto";
}

function prepararFormularioAgregar() {
    // Limpiar todos los campos
    document.getElementById('editId').value = '';
    document.getElementById('editNombre').value = '';
    document.getElementById('editCodigo').value = '';
    document.getElementById('editDescripcion').value = '';
    document.getElementById('editCategoria').value = '';

    // Ubicación: seleccionar la primera opción o dejar vacío
    const selectUbicacion = document.getElementById('editUbicacion');
    if (selectUbicacion) {
        selectUbicacion.value = ''; // o la primera opción
    }

    document.getElementById('editLote').value = '';
    document.getElementById('editLaboratorio').value = '';
    document.getElementById('editRegistro').value = '';
    document.getElementById('editPrecio').value = '';
    document.getElementById('editCantidad').value = '';
    document.getElementById('editUnidad').value = 'Caja';
    document.getElementById('editVencimiento').value = '';
    document.getElementById('editEstado').value = 'Activo';

    document.querySelector('#modalEditar .modal-title').innerText = "Nuevo Producto";

    // Abrir modal con Bootstrap 5
    const myModal = new bootstrap.Modal(document.getElementById('modalEditar'));
    myModal.show();
}

// ==========================================
// MODAL ELIMINAR
// ==========================================
function prepararEliminacion(boton) {
    const id = boton.getAttribute('data-id');
    document.getElementById('idProductoAEliminar').value = id;
    document.getElementById('modalDelete').style.display = 'block';
}

function cerrarModalEliminar() {
    document.getElementById('modalDelete').style.display = 'none';
}

function ejecutarEliminacion() {
    const id = document.getElementById('idProductoAEliminar').value;
    if (id) {
        window.location.href = '/eliminarProducto/' + id;
    }
}

// ==========================================
// CONFIGURACIÓN DEL MODAL DE ELIMINACIÓN (si usas el de Bootstrap)
// ==========================================
document.addEventListener('DOMContentLoaded', function () {
    const modalEliminar = document.getElementById('modalEliminar');
    if (modalEliminar) {
        modalEliminar.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const url = button.getAttribute('data-url');
            const btnConfirmar = document.getElementById('btnConfirmarEliminar');
            if (btnConfirmar) {
                btnConfirmar.setAttribute('href', url);
            }
        });
    }
});