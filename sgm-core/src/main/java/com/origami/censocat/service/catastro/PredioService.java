/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.censocat.service.catastro;

import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioS6;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author CarlosLoorVargas
 */
@Local
public interface PredioService {

    public CatPredio guardarPredio(CatPredio p, String ref);

    public CatPredio getDatosPredio(String json);

    public boolean editarPredio(CatPredio p);

    public boolean guardarPropietario(List<CatEnte> c, CatPredio p, String usuario);

    public CatEscritura guardarEscritura(CatEscritura pe);

    public CatPredioS6 guardarServicio(CatPredio p, CatPredioS6 ps);

    public CatPredio guardarPredioNuevo(CatPredio p);

}
