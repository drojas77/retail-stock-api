package com.tienda.GestionStock.controller;


import com.tienda.GestionStock.model.CajaStock;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class IndexViewController {

    @GetMapping("/index")
    public String mostrarInicio() {

        System.out.println("Entro en el index");

        return "index";
    }
}
