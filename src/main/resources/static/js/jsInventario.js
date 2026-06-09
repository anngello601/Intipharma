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

document.addEventListener("DOMContentLoaded", function () {
    const modalEditar = document.getElementById('modalEditar');

    if (modalEditar) {
        modalEditar.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;

            // Llenar inputs simples
            document.getElementById('editId').value = button.getAttribute('data-id');
            document.getElementById('editNombre').value = button.getAttribute('data-nombre');
            document.getElementById('editLote').value = button.getAttribute('data-lote');
            document.getElementById('editStock').value = button.getAttribute('data-cantidad');
            document.getElementById('editVencimiento').value = button.getAttribute('data-vencimiento');

            // Llenar Selects (el valor del atributo debe coincidir con el value de la option)
            document.getElementById('editUbicacion').value = button.getAttribute('data-ubicacion');
            document.getElementById('editEstado').value = button.getAttribute('data-estado');
        });
    }
});