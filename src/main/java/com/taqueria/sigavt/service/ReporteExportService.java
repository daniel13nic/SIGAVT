package com.taqueria.sigavt.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.taqueria.sigavt.model.Venta;
import com.taqueria.sigavt.repository.VentaRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReporteExportService {

    private final VentaRepository ventaRepository;

    public ReporteExportService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    public byte[] generarExcel(LocalDate inicio, LocalDate fin) throws Exception {
        // Obtenemos las ventas (asegúrate de tener este método o uno similar)
        List<Venta> ventas = ventaRepository.findByFechaVentaBetween(inicio, fin);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Ventas");

            // Encabezados
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Fecha");
            headerRow.createCell(1).setCellValue("Usuario");
            headerRow.createCell(2).setCellValue("Producto");
            headerRow.createCell(3).setCellValue("Cantidad");
            headerRow.createCell(4).setCellValue("Total");

            // Datos
            int rowIdx = 1;
            for (Venta venta : ventas) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(venta.getFechaVenta().toString());
                row.createCell(1).setCellValue(venta.getUsuario().getNombre());
                row.createCell(2).setCellValue(venta.getProducto().getNombre());
                row.createCell(3).setCellValue(venta.getCantidad().doubleValue());
                row.createCell(4).setCellValue(venta.getTotalVenta().doubleValue());
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generarPdf(LocalDate inicio, LocalDate fin) throws Exception {
        List<Venta> ventas = ventaRepository.findByFechaVentaBetween(inicio, fin);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Reporte de Ventas SIGAVT"));
            document.add(new Paragraph("Periodo: " + inicio + " a " + fin));
            document.add(new Paragraph(" "));

            for (Venta venta : ventas) {
                document.add(new Paragraph(
                        venta.getFechaVenta() + " | " +
                                venta.getUsuario().getNombre() + " | " +
                                venta.getProducto().getNombre() + " | " +
                                "$" + venta.getTotalVenta()
                ));
            }

            document.close();
            return out.toByteArray();
        }
    }
}