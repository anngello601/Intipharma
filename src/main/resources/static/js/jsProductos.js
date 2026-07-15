// Lógica de búsqueda y filtrado
document.getElementById('buscarProducto').addEventListener('keyup', filtrarTabla);
document.getElementById('filtroCategoria').addEventListener('change', filtrarTabla);

function filtrarTabla() {
    const texto = document.getElementById('buscarProducto').value.toLowerCase();
    const catFiltro = document.getElementById('filtroCategoria').value;
    const filas = document.querySelectorAll('#tablaProductos tbody tr');

    filas.forEach(fila => {
        const nombre = fila.cells[1].innerText.toLowerCase();
        const cat = fila.cells[3].innerText;
        const coincideTexto = nombre.includes(texto);
        const coincideCat = (catFiltro === "" || cat === catFiltro);
        fila.style.display = (coincideTexto && coincideCat) ? "" : "none";
    });
}
// 1. Llenado del Modal al hacer clic (Evento delegado o manual)
function llenarModalEditar(boton) {
    // Captura los valores desde los atributos data-
    document.getElementById('editId').value = boton.getAttribute('data-id');
    document.getElementById('editNombre').value = boton.getAttribute('data-nombre');
    document.getElementById('editCantidad').value = boton.getAttribute('data-cantidad');
    document.getElementById('editCodigo').value = boton.getAttribute('data-codigo');
    document.getElementById('editDescripcion').value = boton.getAttribute('data-descripcion');
    document.getElementById('editCategoria').value = boton.getAttribute('data-categoria');
    // En jsProductos.js
    document.getElementById('editUbicacion').value = boton.getAttribute('data-ubicacion');
    document.getElementById('editLote').value = boton.getAttribute('data-lote');
    document.getElementById('editLaboratorio').value = boton.getAttribute('data-laboratorio');
    document.getElementById('editRegistro').value = boton.getAttribute('data-registro');
    document.getElementById('editPrecio').value = boton.getAttribute('data-precio');
    document.getElementById('editUnidad').value = boton.getAttribute('data-unidad');
    document.getElementById('editVencimiento').value = boton.getAttribute('data-vencimiento');
    document.getElementById('editEstado').value = boton.getAttribute('data-estado');

    document.getElementById('editUbicacion').value = "1";

    console.log("Forzando el valor de ubicación a '1'");

    // Cambiar título
    document.querySelector('#modalEditar .modal-title').innerText = "Editar Producto";
}

// 2. Preparar el formulario para un NUEVO producto (limpiar campos)
function prepararFormularioAgregar() {
    document.getElementById('editId').value = "";
    document.getElementById('editNombre').value = "";
    document.getElementById('editCodigo').value = "";
    document.getElementById('editDescripcion').value = "";
    document.getElementById('editCategoria').value = "";
    document.getElementById('editUbicacion').value = "";
    document.getElementById('editLote').value = "";
    document.getElementById('editLaboratorio').value = "";
    document.getElementById('editRegistro').value = "";
    document.getElementById('editPrecio').value = "";
    document.getElementById('editCantidad').value = "";
    document.getElementById('editUnidad').value = "Caja";
    document.getElementById('editVencimiento').value = "";
    document.getElementById('editEstado').value = "Activo";

    document.querySelector('#modalEditar .modal-title').innerText = "Nuevo Producto";

    // Disparar apertura del modal usando Bootstrap 5
    var myModal = new bootstrap.Modal(document.getElementById('modalEditar'));
    myModal.show();
}

// 3. Funciones de Eliminación
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
        window.location.href = '/eliminarProducto/' + id; // Asegúrate que esta sea tu ruta correcta
    }
}
// Configurar el modal de eliminación dinámicamente
document.addEventListener('DOMContentLoaded', function () {
    const modalEliminar = document.getElementById('modalEliminar');
    modalEliminar.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        const url = button.getAttribute('data-url');
        const btnConfirmar = document.getElementById('btnConfirmarEliminar');
        btnConfirmar.setAttribute('href', url);
    });
});
const ubicacionVal = b.getAttribute('data-ubicacion');
const selectUbicacion = document.getElementById('editUbicacion');

// Forzamos el valor asegurando que sea string
selectUbicacion.value = ubicacionVal;

// Si aún así no cambia, podrías disparar un evento de cambio
selectUbicacion.dispatchEvent(new Event('change'));