package com.tienda.GestionStock.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class SystemErrorController {

    @GetMapping("/403")
    public String accesoDenegado() {
        return "error/403"; // Apunta a src/main/resources/templates/error/403.html
    }
}