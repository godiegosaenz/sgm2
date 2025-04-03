/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces;

import com.origami.sgm.entities.RenActividadComercial;
import com.origami.sgm.entities.RenLocalCategoria;
import com.origami.sgm.entities.RenLocalUbicacion;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Angel Navarro
 */
@Local
public interface ServicesList {

    public List<RenLocalUbicacion> getLocalUbicacions();

    public List<RenActividadComercial> getActividadComercials();
    
    public List<RenLocalCategoria> getLocalCategorias();

}
