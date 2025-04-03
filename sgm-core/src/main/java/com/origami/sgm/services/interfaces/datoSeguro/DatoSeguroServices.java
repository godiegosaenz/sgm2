/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.datoSeguro;

import com.origami.sgm.bpm.models.DatoSeguro;
import com.origami.sgm.entities.CatEnte;
import java.net.URLConnection;
import javax.ejb.Local;

/**
 *
 * @author CarlosLoorVargas
 */
@Local
public interface DatoSeguroServices {
    
    public DatoSeguro getDatos(String cedula, boolean empresa, Integer intentos);

    public URLConnection configureConnection(URLConnection con);
    
    /**
     * Retorna los datos del ente persistido si el numero de identificacion son validos
     * @param data
     * @return 
     */
    public CatEnte getEnteFromDatoSeguro(DatoSeguro data);

    /**
     * Llena los campos de catEnte,retorna el ente sin persistir
     * @param data
     * @param ente
     * @param cabiarCiRuc
     * @return 
     */
    public CatEnte llenarEnte(DatoSeguro data, CatEnte ente, Boolean cabiarCiRuc);
    
    public String getData();
    
}
