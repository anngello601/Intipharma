function filtrarTabla() {

    let texto = document
        .getElementById("buscarProducto")
        .value
        .toLowerCase();

    let categoria = document
        .getElementById("filtroCategoria")
        .value
        .toLowerCase();

    let filas = document.querySelectorAll(
        "#tablaProductos tbody tr"
    );

    filas.forEach(fila => {

        let nombre =
            fila.cells[0].innerText.toLowerCase();

        let coincideNombre =
            nombre.includes(texto);

        let coincideCategoria = true;

        if (categoria !== "") {

            coincideCategoria =
                fila.dataset.categoria.toLowerCase()
                === categoria;
        }

        fila.style.display =
            (coincideNombre && coincideCategoria)
            ? ""
            : "none";
    });
}

document
.getElementById("buscarProducto")
.addEventListener("keyup", filtrarTabla);

document
.getElementById("filtroCategoria")
.addEventListener("change", filtrarTabla);
