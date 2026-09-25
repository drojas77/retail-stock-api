package com.tienda.GestionStock.service;

import com.tienda.GestionStock.dto.AltaRepoRequest;
import com.tienda.GestionStock.model.CajaStock;

import java.util.ArrayList;

public interface IStockService {


    public ArrayList<CajaStock> registrarRepoStock(AltaRepoRequest request);

}
