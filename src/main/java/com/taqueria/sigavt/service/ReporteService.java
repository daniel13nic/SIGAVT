package com.taqueria.sigavt.service;

import com.taqueria.sigavt.repository.VentaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final VentaRepository ventaRepository;

    public ReporteService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }



    // MÉTRICAS SUPERIORES DEL DASHBOARD

    public Map<String, Object> obtenerMetricasPrincipales(LocalDate inicio, LocalDate fin, Integer idCategoria) {
        Map<String, Object> metricas = new HashMap<>();

        BigDecimal totalVendido = ventaRepository.sumarIngresosPorRango(inicio, fin, idCategoria);
        Long ventasRegistradas = ventaRepository.contarVentasPorRango(inicio, fin, idCategoria);
        BigDecimal ventaPromedio = ventaRepository.obtenerTicketPromedioPorRango(inicio, fin, idCategoria);

        List<Object[]> categorias = ventaRepository.agruparVentasPorCategoria(inicio, fin, idCategoria);
        String categoriaTop = categorias.isEmpty() ? "N/A" : (String) categorias.get(0)[0];

        metricas.put("totalVendido", totalVendido != null ? totalVendido : BigDecimal.ZERO);
        metricas.put("ventasRegistradas", ventasRegistradas != null ? ventasRegistradas : 0L);
        metricas.put("ventaPromedio", ventaPromedio != null ? ventaPromedio.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        metricas.put("categoriaTop", categoriaTop);

        return metricas;
    }

    // DATOS PARA GRÁFICA DE BARRAS
    public Map<String, Object> obtenerDatosGraficaBarras(LocalDate inicio, LocalDate fin, Integer idCategoria) {
        List<Object[]> datosCrudos = ventaRepository.agruparVentasPorDia(inicio, fin, idCategoria);
        List<String> labels = datosCrudos.stream().map(obj -> obj[0].toString()).collect(Collectors.toList());
        List<BigDecimal> data = datosCrudos.stream().map(obj -> (BigDecimal) obj[1]).collect(Collectors.toList());

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("labels", labels);
        resultado.put("data", data);
        return resultado;
    }

    // DATOS PARA GRÁFICA DE DONA
    public Map<String, Object> obtenerDatosGraficaDona(LocalDate inicio, LocalDate fin, Integer idCategoria) {
        List<Object[]> datosCrudos = ventaRepository.agruparVentasPorCategoria(inicio, fin, idCategoria);
        List<String> labels = datosCrudos.stream().map(obj -> (String) obj[0]).collect(Collectors.toList());
        List<BigDecimal> data = datosCrudos.stream().map(obj -> (BigDecimal) obj[1]).collect(Collectors.toList());

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("labels", labels);
        resultado.put("data", data);
        return resultado;
    }

    // TABLAS DE RANKINGS INFERIORES
    public List<Object[]> obtenerRankingProductos(LocalDate inicio, LocalDate fin, Integer idCategoria) {
        return ventaRepository.obtenerTopProductos(inicio, fin, idCategoria, PageRequest.of(0, 5));
    }

    public List<Object[]> obtenerRankingUsuarios(LocalDate inicio, LocalDate fin, Integer idCategoria) {
        List<Object[]> usuarios = ventaRepository.obtenerTopUsuarios(inicio, fin, idCategoria);
        return usuarios.stream().limit(5).collect(Collectors.toList());
    }
}