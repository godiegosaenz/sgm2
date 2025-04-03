/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.registro;

import java.io.IOException;
import javax.ejb.Local;

/**
 *
 * @author Anyelo
 */
@Local
public interface AnexosRegistroPropiedadServices {
    
    public void anexoDatoPublico(String fechaInicio, String nombreReporte) throws IOException;
    
    public void anexoMercantilContratos(String fechaInicio, String fechaFin) throws IOException;
    
    public void anexoMercantilSociedad(String fechaInicio, String fechaFin) throws IOException;
    
}
