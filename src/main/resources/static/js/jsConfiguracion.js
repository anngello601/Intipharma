function eliminarUsuario(btn) {
    btn.closest("tr").remove();
}

function abrirModalEditar(id, nombre, correo) {
    document.getElementById('editId').value = id;
    document.getElementById('editNombre').value = nombre;
    document.getElementById('editCorreo').value = correo;

    // Abre el modal usando Bootstrap
    var myModal = new bootstrap.Modal(document.getElementById('editarUsuarioModal'));
    myModal.show();
}

function prepararEdicion(boton) {
    // Obtenemos los datos del botón que se presionó
    const id = boton.getAttribute('data-id');
    const nombre = boton.getAttribute('data-nombre');
    const correo = boton.getAttribute('data-correo');

    // Asignamos los valores al formulario del modal
    document.getElementById('editId').value = id;
    document.getElementById('editNombre').value = nombre;
    document.getElementById('editCorreo').value = correo;

    // Inicializamos y mostramos el modal
    var myModal = new bootstrap.Modal(document.getElementById('editarUsuarioModal'));
    myModal.show();
}

function llenarModal(boton) {
    // Obtenemos los valores desde el botón
    const id = boton.getAttribute('data-id');
    const dni = boton.getAttribute('data-dni');
    const nombre = boton.getAttribute('data-nombre');
    const apellido = boton.getAttribute('data-apellido');
    const correo = boton.getAttribute('data-correo');

    // Asignamos los valores a los inputs del modal
    // Usamos el ID del elemento para buscar el input
    document.getElementById('editId').value = id || '';
    document.getElementById('editDni').value = dni || '';
    document.getElementById('editNombre').value = nombre || '';
    document.getElementById('editApellido').value = apellido || '';
    document.getElementById('editCorreo').value = correo || '';

    // Limpiamos el password por seguridad
    document.getElementById('editPassword').value = '';
}

function mostrarSeccion(id) {
    let secciones = document.querySelectorAll(".seccion");
    secciones.forEach(sec => sec.classList.add("d-none"));

    document.getElementById(id).classList.remove("d-none");

    let opciones = document.querySelectorAll(".config-item");
    opciones.forEach(op => op.classList.remove("active-option"));

    event.currentTarget.classList.add("active-option");
}

console.log("El archivo configuracion.js se ha cargado correctamente.");

function probarConexion() {
    alert("¡La conexión entre HTML y JS funciona correctamente!");
    console.log("Función ejecutada exitosamente.");
}