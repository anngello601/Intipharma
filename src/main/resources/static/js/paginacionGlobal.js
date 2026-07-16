/**
 * Sistema de paginación global para tablas
 * 
 * Uso:
 * 1. En tu tabla, asigna un ID único (ej: 'tablaProductos')
 * 2. En el contenedor de paginación, usa los mismos IDs (pagInfo, pagBtns)
 * 3. Llama a inicializarPaginacion(idTabla, filasPorPagina)
 * 
 * Ejemplo:
 *   inicializarPaginacion('tablaProductos', 10);
 */

// Almacena el estado de paginación por tabla
const paginacionEstado = {};

function inicializarPaginacion(idTabla, filasPorPagina = 10) {
    const tabla = document.getElementById(idTabla);
    if (!tabla) {
        console.warn(`Tabla con ID "${idTabla}" no encontrada`);
        return;
    }

    const tbody = tabla.querySelector('tbody');
    if (!tbody) {
        console.warn(`No se encontró <tbody> en la tabla "${idTabla}"`);
        return;
    }

    // Obtener filas visibles (considerando filtros)
    const filas = Array.from(tbody.querySelectorAll('tr')).filter(fila => fila.style.display !== 'none');
    const totalFilas = filas.length;

    if (totalFilas === 0) {
        // No hay datos visibles
        const pagInfo = document.getElementById('pagInfo');
        if (pagInfo) pagInfo.textContent = 'No hay registros';
        const pagBtns = document.getElementById('pagBtns');
        if (pagBtns) pagBtns.innerHTML = '';
        return;
    }

    // Guardamos el estado
    paginacionEstado[idTabla] = {
        filas: filas,
        total: totalFilas,
        porPagina: filasPorPagina,
        paginaActual: 1,
        totalPaginas: Math.ceil(totalFilas / filasPorPagina)
    };

    // Mostrar la primera página
    mostrarPagina(idTabla, 1);
}

function mostrarPagina(idTabla, pagina) {
    const estado = paginacionEstado[idTabla];
    if (!estado) return;

    const { filas, porPagina, total, totalPaginas } = estado;

    // Validar página
    if (pagina < 1) pagina = 1;
    if (pagina > totalPaginas) pagina = totalPaginas;
    estado.paginaActual = pagina;

    const inicio = (pagina - 1) * porPagina;
    const fin = Math.min(inicio + porPagina, total);

    // Ocultar todas las filas
    filas.forEach(fila => fila.style.display = 'none');

    // Mostrar las filas de la página actual
    for (let i = inicio; i < fin; i++) {
        if (filas[i]) {
            filas[i].style.display = '';
        }
    }

    // Actualizar información
    const pagInfo = document.getElementById('pagInfo');
    if (pagInfo) {
        pagInfo.textContent = `Mostrando ${inicio + 1} - ${fin} de ${total} registros`;
    }

    // Generar botones de paginación
    generarBotones(idTabla, pagina, totalPaginas);
}

function generarBotones(idTabla, paginaActual, totalPaginas) {
    const contenedor = document.getElementById('pagBtns');
    if (!contenedor) return;

    let html = '';

    // Botón "Anterior"
    html += `<button class="btn-pag" onclick="cambiarPagina('${idTabla}', ${paginaActual - 1})" 
                    ${paginaActual <= 1 ? 'disabled' : ''}>
                &laquo;
            </button>`;

    // Números de página (máximo 5 a la vez)
    let inicio = Math.max(1, paginaActual - 2);
    let fin = Math.min(totalPaginas, paginaActual + 2);

    // Ajustar para mostrar siempre 5 si es posible
    if (fin - inicio < 4) {
        if (inicio === 1) {
            fin = Math.min(totalPaginas, inicio + 4);
        } else if (fin === totalPaginas) {
            inicio = Math.max(1, fin - 4);
        }
    }

    // Primera página si no está visible
    if (inicio > 1) {
        html += `<button class="btn-pag" onclick="cambiarPagina('${idTabla}', 1)">1</button>`;
        if (inicio > 2) html += `<span class="btn-pag disabled">…</span>`;
    }

    for (let i = inicio; i <= fin; i++) {
        const activo = i === paginaActual ? 'active' : '';
        html += `<button class="btn-pag ${activo}" onclick="cambiarPagina('${idTabla}', ${i})">${i}</button>`;
    }

    // Última página si no está visible
    if (fin < totalPaginas) {
        if (fin < totalPaginas - 1) html += `<span class="btn-pag disabled">…</span>`;
        html += `<button class="btn-pag" onclick="cambiarPagina('${idTabla}', ${totalPaginas})">${totalPaginas}</button>`;
    }

    // Botón "Siguiente"
    html += `<button class="btn-pag" onclick="cambiarPagina('${idTabla}', ${paginaActual + 1})"
                    ${paginaActual >= totalPaginas ? 'disabled' : ''}>
                &raquo;
            </button>`;

    contenedor.innerHTML = html;
}

function cambiarPagina(idTabla, pagina) {
    const estado = paginacionEstado[idTabla];
    if (!estado) return;
    const { totalPaginas } = estado;
    if (pagina < 1 || pagina > totalPaginas) return;
    mostrarPagina(idTabla, pagina);
}

// Re-inicializar paginación después de filtrar
function reiniciarPaginacion(idTabla, filasPorPagina = 10) {
    // Limpiar estado anterior
    delete paginacionEstado[idTabla];
    // Inicializar de nuevo
    inicializarPaginacion(idTabla, filasPorPagina);
}

// Escuchar cambios en filtros para reiniciar paginación
function agregarListenerFiltros(idTabla) {
    // Buscar campos de filtro comunes en la página
    const filtros = document.querySelectorAll('#buscarProducto, #filtroEstado, #filtroCategoria, .filtro-tabla');
    filtros.forEach(filtro => {
        filtro.addEventListener('input', function() {
            aplicarFiltrosYReiniciar(idTabla);
        });
        filtro.addEventListener('change', function() {
            aplicarFiltrosYReiniciar(idTabla);
        });
    });
}

function aplicarFiltrosYReiniciar(idTabla) {
    // Aplicar filtros personalizados (si existen)
    if (typeof aplicarFiltrosTabla === 'function') {
        aplicarFiltrosTabla();
    }
    // Reiniciar paginación después de un breve delay para que los filtros actualicen el DOM
    setTimeout(() => {
        reiniciarPaginacion(idTabla, 10);
    }, 50);
}

// Función de filtro global que puede ser sobrescrita
function aplicarFiltrosTabla() {
    // Esta función se puede sobrescribir en cada página
    // Por defecto, no hace nada
}

// Inicialización automática cuando el DOM está listo
document.addEventListener('DOMContentLoaded', function() {
    // Buscar todas las tablas con la clase 'tabla-paginable' o un atributo data-paginable
    const tablas = document.querySelectorAll('table[data-paginable]');
    tablas.forEach(tabla => {
        const id = tabla.id;
        const filasPorPagina = parseInt(tabla.getAttribute('data-filas') || 10);
        if (id) {
            inicializarPaginacion(id, filasPorPagina);
            agregarListenerFiltros(id);
        }
    });

    // También permitir inicialización manual con un atributo personalizado
    // Si no usas data-paginable, puedes llamar manualmente desde tu script
});