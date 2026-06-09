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

function filtrarPorNombre() {
    // Obtener el texto escrito y convertirlo a minúsculas
    let input = document.getElementById("inputBuscarNombre");
    let filtro = input.value.toLowerCase();

    // Obtener la tabla y todas sus filas
    let tabla = document.getElementById("tablaUsuarios");
    let filas = tabla.getElementsByTagName("tr");

    // Recorrer las filas (empezando desde la primera)
    for (let i = 0; i < filas.length; i++) {
        // La columna 0 es donde está el nombre (según tu código)
        let celdaNombre = filas[i].getElementsByTagName("td")[0];

        if (celdaNombre) {
            let texto = celdaNombre.textContent || celdaNombre.innerText;
            // Si el texto coincide, mostramos la fila, si no, la ocultamos
            if (texto.toLowerCase().indexOf(filtro) > -1) {
                filas[i].style.display = "";
            } else {
                filas[i].style.display = "none";
            }
        }
    }
}