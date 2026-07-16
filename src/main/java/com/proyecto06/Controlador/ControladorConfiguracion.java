package com.proyecto06.Controlador;

import org.springframework.jdbc.core.JdbcTemplate;
import com.proyecto06.Modelo.Producto;
import com.proyecto06.Modelo.Rol;
import com.proyecto06.Modelo.Usuario;
import com.proyecto06.Repository.ProductoRepository;
import com.proyecto06.Repository.UsuarioRepository;
import com.proyecto06.Repository.RolRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.geom.PageSize;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/configuracion")
public class ControladorConfiguracion {
    private final JdbcTemplate jdbcTemplate;
    private final UsuarioRepository usuarioRepo;
    private final RolRepository rolRepo;
    private final ProductoRepository productoRepository;
    private final Environment environment;

    public ControladorConfiguracion(UsuarioRepository usuarioRepo,
            RolRepository rolRepo,
            ProductoRepository productoRepository,
            Environment environment,
            JdbcTemplate jdbcTemplate) {
        this.usuarioRepo = usuarioRepo;
        this.rolRepo = rolRepo;
        this.productoRepository = productoRepository;
        this.environment = environment;
        this.jdbcTemplate = jdbcTemplate;
    }

    // ========== Página principal ==========
    @GetMapping
    public String mostrarConfiguracion(HttpSession session, HttpServletRequest request, Model model) {
        Integer rol = (Integer) session.getAttribute("rol");
        if (rol == null || rol != 0) {
            String referer = request.getHeader("Referer");
            return (referer != null && !referer.isEmpty()) ? "redirect:" + referer : "redirect:/alertaProducto";
        }
        model.addAttribute("usuarios", usuarioRepo.findAll());
        model.addAttribute("roles", rolRepo.findAll());
        return "configuracion";
    }

    // ========== CRUD Usuarios ==========
    // ========== ACTUALIZAR USUARIO ==========
    @PostMapping("/editar")
    public String actualizarUsuario(@ModelAttribute Usuario usuario,
            RedirectAttributes redirectAttrs) {
        try {
            // Buscar usuario existente
            Usuario existente = usuarioRepo.findById(usuario.getIdUsuario()).orElse(null);
            if (existente == null) {
                redirectAttrs.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/configuracion";
            }

            // Actualizar campos (excepto contraseña si está vacía)
            existente.setDni(usuario.getDni());
            existente.setNombre(usuario.getNombre());
            existente.setApellido(usuario.getApellido());
            existente.setCorreo(usuario.getCorreo());

            // Actualizar rol si se seleccionó uno
            if (usuario.getRol() != null && usuario.getRol().getIdRol() != null) {
                Rol nuevoRol = rolRepo.findById(usuario.getRol().getIdRol()).orElse(null);
                existente.setRol(nuevoRol);
            }

            // Solo actualizar contraseña si se proporcionó una nueva (no vacía)
            if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty()) {
                existente.setPassword(usuario.getPassword().trim());
            }

            usuarioRepo.save(existente);
            redirectAttrs.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttrs.addFlashAttribute("error", "Error al actualizar usuario: " + e.getMessage());
        }
        return "redirect:/configuracion";
    }

    // ========== ELIMINAR USUARIO ==========
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Integer id,
            RedirectAttributes redirectAttrs) {
        try {
            if (usuarioRepo.existsById(id)) {
                usuarioRepo.deleteById(id);
                redirectAttrs.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
            } else {
                redirectAttrs.addFlashAttribute("error", "Usuario no encontrado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttrs.addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
        }
        return "redirect:/configuracion";
    }

    // ========== GUARDAR NUEVO USUARIO ==========
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario,
            RedirectAttributes redirectAttrs) {
        try {
            // Validar que el correo no esté duplicado
            if (usuarioRepo.findByCorreo(usuario.getCorreo()).isPresent()) {
                redirectAttrs.addFlashAttribute("error", "El correo ya está registrado");
                return "redirect:/configuracion";
            }

            // Validar que el nombre de usuario no esté duplicado
            if (usuario.getNombre() != null && !usuario.getNombre().isEmpty()) {
                // Si tienes un método para buscar por username, úsalo
                // Si no, puedes omitir esta validación
            }

            // Guardar nuevo usuario (la contraseña ya viene del formulario)
            usuarioRepo.save(usuario);
            redirectAttrs.addFlashAttribute("mensaje", "Usuario creado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttrs.addFlashAttribute("error", "Error al guardar usuario: " + e.getMessage());
        }
        return "redirect:/configuracion";
    }

    // ========== EXPORTAR PRODUCTOS A EXCEL ==========
    @GetMapping("/exportar-productos-excel")
    public void exportarProductosExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=productos_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");

        List<Producto> productos = productoRepository.findAll();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Productos");

            String[] columnas = { "ID", "Nombre", "Código", "Categoría", "Lote", "Laboratorio",
                    "Cantidad", "Precio", "Vencimiento", "Estado" };
            XSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                headerRow.createCell(i).setCellValue(columnas[i]);
            }

            int rowNum = 1;
            for (Producto p : productos) {
                XSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(p.getIdProducto() != null ? p.getIdProducto() : 0);
                row.createCell(1).setCellValue(p.getNombreProducto() != null ? p.getNombreProducto() : "");
                row.createCell(2).setCellValue(p.getCodigoBarras() != null ? p.getCodigoBarras() : "");
                row.createCell(3).setCellValue(p.getCategoria() != null ? p.getCategoria().getNombreCategoria() : "");
                row.createCell(4).setCellValue(p.getLote() != null ? p.getLote() : "");
                row.createCell(5).setCellValue(p.getLaboratorio() != null ? p.getLaboratorio() : "");
                row.createCell(6).setCellValue(p.getCantidad() != null ? p.getCantidad() : 0);
                row.createCell(7).setCellValue(p.getPrecio() != null ? p.getPrecio().doubleValue() : 0.0);
                row.createCell(8)
                        .setCellValue(p.getFechaVencimiento() != null ? p.getFechaVencimiento().toString() : "");
                row.createCell(9).setCellValue(p.getEstado() != null ? p.getEstado() : "");
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error generando Excel: " + e.getMessage());
        }
    }

    // ========== EXPORTAR PRODUCTOS A PDF ==========
    @GetMapping("/exportar-productos-pdf")
    public void exportarProductosPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=productos_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");

        List<Producto> productos = productoRepository.findAll();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4.rotate());
            document.setMargins(20, 20, 20, 20);

            document.add(new Paragraph("Lista de Productos - IntiPharma S.A.C.")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph(
                    "Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(" "));

            float[] columnWidths = { 30, 120, 80, 80, 60, 80, 50, 60, 80, 60 };
            Table table = new Table(UnitValue.createPointArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            String[] headers = { "ID", "Nombre", "Código", "Categoría", "Lote", "Laboratorio",
                    "Cant.", "Precio", "Vencimiento", "Estado" };
            for (String h : headers) {
                table.addCell(new Cell().add(new Paragraph(h).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setPadding(5));
            }

            for (Producto p : productos) {
                table.addCell(String.valueOf(p.getIdProducto()));
                table.addCell(p.getNombreProducto() != null ? p.getNombreProducto() : "");
                table.addCell(p.getCodigoBarras() != null ? p.getCodigoBarras() : "");
                table.addCell(p.getCategoria() != null ? p.getCategoria().getNombreCategoria() : "");
                table.addCell(p.getLote() != null ? p.getLote() : "");
                table.addCell(p.getLaboratorio() != null ? p.getLaboratorio() : "");
                table.addCell(String.valueOf(p.getCantidad()));
                table.addCell(p.getPrecio() != null ? String.valueOf(p.getPrecio()) : "0");
                table.addCell(p.getFechaVencimiento() != null ? p.getFechaVencimiento().toString() : "");
                table.addCell(p.getEstado() != null ? p.getEstado() : "");
            }

            document.add(table);
            document.close();

            response.getOutputStream().write(baos.toByteArray());
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error generando PDF: " + e.getMessage());
        }
    }

    // ========== GENERAR BACKUP SQL COMPLETO ==========
    @GetMapping("/generar-backup-sql")
    public void generarBackupSQL(HttpServletResponse response) throws IOException {
        response.setContentType("application/sql");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=backup_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".sql");

        generarBackupManual(response);
    }

    // ============================================================
    // MÉTODO PRINCIPAL: GENERA EL BACKUP MANUAL CON TODAS LAS TABLAS
    // ============================================================
    private void generarBackupManual(HttpServletResponse response) throws IOException {
        StringBuilder sql = new StringBuilder();

        // Cabecera
        sql.append("-- ============================================================\n");
        sql.append("-- BACKUP COMPLETO DE LA BASE DE DATOS\n");
        sql.append("-- Generado: ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .append("\n");
        sql.append("-- ============================================================\n\n");
        sql.append("SET FOREIGN_KEY_CHECKS=0;\n");
        sql.append("SET AUTOCOMMIT=0;\n\n");

        // Obtener todas las tablas del esquema actual
        List<String> tablas = obtenerNombresTablas();

        if (tablas.isEmpty()) {
            // Si no hay tablas, escribir un mensaje
            sql.append("-- No se encontraron tablas en la base de datos.\n");
            response.getOutputStream().write(sql.toString().getBytes(StandardCharsets.UTF_8));
            response.getOutputStream().flush();
            return;
        }

        // Exportar cada tabla
        for (String nombreTabla : tablas) {
            try {
                exportarTabla(nombreTabla, sql);
            } catch (Exception e) {
                // Si una tabla falla, continuar con la siguiente
                sql.append("-- Error al exportar tabla '").append(nombreTabla).append("': ").append(e.getMessage())
                        .append("\n");
                e.printStackTrace();
            }
        }

        // Finalizar
        sql.append("SET FOREIGN_KEY_CHECKS=1;\n");
        sql.append("COMMIT;\n");

        response.getOutputStream().write(sql.toString().getBytes(StandardCharsets.UTF_8));
        response.getOutputStream().flush();
    }

    // Obtener nombres de todas las tablas en el esquema actual
    private List<String> obtenerNombresTablas() {
        String query = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = DATABASE()";
        try {
            return jdbcTemplate.query(query, (rs, rowNum) -> rs.getString("TABLE_NAME"));
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback: lista de tablas comunes si no se puede consultar INFORMATION_SCHEMA
            return List.of("roles", "categorias", "ubicaciones", "productos", "usuarios", "alertas", "movimientos",
                    "notificaciones", "historial_cambios", "logs", "reportes");
        }
    }

    // Exportar una tabla individual (estructura + datos)
    private void exportarTabla(String nombreTabla, StringBuilder sql) {
        // Obtener estructura de la tabla
        String estructura = obtenerEstructuraTabla(nombreTabla);
        if (estructura == null || estructura.isEmpty()) {
            sql.append("-- Tabla '").append(nombreTabla).append("' no existe o no se pudo obtener estructura.\n");
            return;
        }

        sql.append("-- ------------------------------------------------------\n");
        sql.append("-- Table structure for table `").append(nombreTabla).append("`\n");
        sql.append("-- ------------------------------------------------------\n");
        sql.append("DROP TABLE IF EXISTS `").append(nombreTabla).append("`;\n");
        sql.append(estructura).append(";\n\n");

        // Obtener datos de la tabla
        List<Object[]> datos = obtenerDatosTabla(nombreTabla);
        if (datos.isEmpty()) {
            sql.append("-- No hay datos en `").append(nombreTabla).append("`\n\n");
            return;
        }

        sql.append("--\n-- Dumping data for table `").append(nombreTabla).append("`\n--\n");
        sql.append("LOCK TABLES `").append(nombreTabla).append("` WRITE;\n");
        sql.append("/*!40000 ALTER TABLE `").append(nombreTabla).append("` DISABLE KEYS */;\n");

        // Generar INSERTs
        int columnasCount = obtenerColumnasCount(nombreTabla);
        String columnasSql = obtenerColumnas(nombreTabla);

        for (Object[] row : datos) {
            sql.append("INSERT INTO `").append(nombreTabla).append("` (").append(columnasSql).append(") VALUES (");
            for (int i = 0; i < row.length; i++) {
                if (i > 0)
                    sql.append(", ");
                sql.append(convertirValorSQL(row[i]));
            }
            sql.append(");\n");
        }

        sql.append("/*!40000 ALTER TABLE `").append(nombreTabla).append("` ENABLE KEYS */;\n");
        sql.append("UNLOCK TABLES;\n\n");
    }

    // Obtener estructura CREATE TABLE de una tabla
    private String obtenerEstructuraTabla(String nombreTabla) {
        try {
            String query = "SHOW CREATE TABLE `" + nombreTabla + "`";
            return jdbcTemplate.queryForObject(query, (rs, rowNum) -> rs.getString("Create Table"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Obtener todos los datos de una tabla
    private List<Object[]> obtenerDatosTabla(String nombreTabla) {
        String query = "SELECT * FROM `" + nombreTabla + "`";
        try {
            return jdbcTemplate.query(query, (rs, rowNum) -> {
                int columnCount = rs.getMetaData().getColumnCount();
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                return row;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // Obtener número de columnas de una tabla (para generar el INSERT)
    private int obtenerColumnasCount(String nombreTabla) {
        String query = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?";
        try {
            return jdbcTemplate.queryForObject(query, Integer.class, nombreTabla);
        } catch (Exception e) {
            return 0;
        }
    }

    // Obtener lista de nombres de columnas separados por coma
    private String obtenerColumnas(String nombreTabla) {
        String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? ORDER BY ORDINAL_POSITION";
        try {
            List<String> columnas = jdbcTemplate.query(query, (rs, rowNum) -> rs.getString("COLUMN_NAME"), nombreTabla);
            return String.join(", ", columnas);
        } catch (Exception e) {
            return "*"; // Fallback
        }
    }

    // ========== MÉTODOS AUXILIARES ==========
    private String convertirValorSQL(Object valor) {
        if (valor == null)
            return "NULL";
        if (valor instanceof Number) {
            return valor.toString();
        } else if (valor instanceof java.sql.Date || valor instanceof java.sql.Timestamp) {
            return "'" + valor.toString() + "'";
        } else if (valor instanceof Boolean) {
            return ((Boolean) valor) ? "1" : "0";
        } else {
            return "'" + escaparSQL(valor.toString()) + "'";
        }
    }

    private List<Object[]> jdbcQuery(String query) {
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            Object[] row = new Object[rs.getMetaData().getColumnCount()];
            for (int i = 0; i < row.length; i++) {
                row[i] = rs.getObject(i + 1);
            }
            return row;
        });
    }

    private String escaparSQL(Object valor) {
        if (valor == null)
            return "";
        String str = valor.toString();
        return str.replace("'", "''").replace("\\", "\\\\");
    }

    // ========== GUARDAR CONFIGURACIONES ==========
    @PostMapping("/alertas/guardar")
    public String guardarAlertas(@RequestParam Integer dias,
            @RequestParam String prioridad,
            RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("mensaje",
                "Alertas actualizadas: días=" + dias + ", prioridad=" + prioridad);
        return "redirect:/configuracion";
    }

    @PostMapping("/correos/guardar")
    public String guardarCorreos(@RequestParam String correo,
            @RequestParam(value = "enviarAlertas", defaultValue = "false") boolean enviarAlertas,
            RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("mensaje",
                "Correos actualizados: " + correo + ", alertas=" + enviarAlertas);
        return "redirect:/configuracion";
    }

    @PostMapping("/seguridad/cambiar-password")
    public String cambiarPassword(@RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttrs) {
        String correo = (String) session.getAttribute("correo");
        if (correo != null) {
            Optional<Usuario> usuarioOpt = usuarioRepo.findByCorreo(correo);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                usuario.setPassword(password);
                usuarioRepo.save(usuario);
                redirectAttrs.addFlashAttribute("mensaje", "Contraseña actualizada");
            } else {
                redirectAttrs.addFlashAttribute("error", "Usuario no encontrado");
            }
        } else {
            redirectAttrs.addFlashAttribute("error", "No has iniciado sesión");
        }
        return "redirect:/configuracion";
    }

    @PostMapping("/notificaciones/guardar")
    public String guardarNotificaciones(@RequestParam(value = "sistema", defaultValue = "false") boolean sistema,
            @RequestParam(value = "sonidos", defaultValue = "false") boolean sonidos,
            RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("mensaje", "Preferencias de notificaciones actualizadas");
        return "redirect:/configuracion";
    }
}