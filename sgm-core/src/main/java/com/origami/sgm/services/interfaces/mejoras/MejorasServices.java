/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.mejoras;

import com.origami.sgm.entities.CatUbicacion;
import com.origami.sgm.entities.MejObra;
import com.origami.sgm.entities.MejValoresObraUbicacion;
import java.util.List;

/**
 *
 * @author xndyxnchez :v
 */
public interface MejorasServices {
    
    public MejObra saveObra(MejObra obra, List<CatUbicacion> catUbicaciones);
    
    public List<CatUbicacion> saveUbicaciones(List<CatUbicacion> catUbicaciones);
    
    public List<MejValoresObraUbicacion> saveMejValoresObraUbicacion(MejObra obra, List<CatUbicacion> catUbicaciones);
}
