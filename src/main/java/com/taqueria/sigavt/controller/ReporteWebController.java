package com.taqueria.sigavt.controller;

import com.taqueria.sigavt.repository.CategoriaRepository;
import com.taqueria.sigavt.repository.UsuarioRepository;
import com.taqueria.sigavt.service.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.taqueria.sigavt.service.ReporteExportService;


import java.time.LocalDate;

@Controller
@RequestMapping("/reportes")
public class ReporteWebController {

    private final ReporteService reporteService;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReporteExportService reporteExportService;



    public ReporteWebController(ReporteService reporteService,
                                CategoriaRepository categoriaRepository,
                                UsuarioRepository usuarioRepository,
                                ReporteExportService reporteExportService) {
        this.reporteService = reporteService;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.reporteExportService = reporteExportService;
    }

    @GetMapping
    public String mostrarReportes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Integer idCategoria,
            Model model) {

        LocalDate fechaFin = (hasta != null) ? hasta : LocalDate.now();
        LocalDate fechaInicio = (desde != null) ? desde : fechaFin.minusMonths(3);

        model.addAttribute("listaCategorias", categoriaRepository.findAll());

        model.addAttribute("metricas", reporteService.obtenerMetricasPrincipales(fechaInicio, fechaFin, idCategoria));
        model.addAttribute("datosBarras", reporteService.obtenerDatosGraficaBarras(fechaInicio, fechaFin, idCategoria));
        model.addAttribute("datosDona", reporteService.obtenerDatosGraficaDona(fechaInicio, fechaFin, idCategoria));
        model.addAttribute("rankingProductos", reporteService.obtenerRankingProductos(fechaInicio, fechaFin, idCategoria));
        model.addAttribute("rankingUsuarios", reporteService.obtenerRankingUsuarios(fechaInicio, fechaFin, idCategoria));

        model.addAttribute("fechaDesde", fechaInicio);
        model.addAttribute("fechaHasta", fechaFin);
        model.addAttribute("idCategoriaSeleccionada", idCategoria);

        return "reportes/reportes";
    }


    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) throws Exception {

        LocalDate fechaFin = (hasta != null) ? hasta : LocalDate.now();
        LocalDate fechaInicio = (desde != null) ? desde : fechaFin.minusMonths(3);

        byte[] excelContent = reporteExportService.generarExcel(fechaInicio, fechaFin);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Ventas_" + fechaInicio + ".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelContent);
    }

    @GetMapping("/exportar/pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) throws Exception {

        LocalDate fechaFin = (hasta != null) ? hasta : LocalDate.now();
        LocalDate fechaInicio = (desde != null) ? desde : fechaFin.minusMonths(3);

        byte[] pdfContent = reporteExportService.generarPdf(fechaInicio, fechaFin);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Ventas_" + fechaInicio + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }


}