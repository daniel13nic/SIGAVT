package com.taqueria.sigavt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reportes")
public class ReporteWebController {

    @GetMapping
    public String mostrarReportes() {
        return "reportes/reportes";
    }
}