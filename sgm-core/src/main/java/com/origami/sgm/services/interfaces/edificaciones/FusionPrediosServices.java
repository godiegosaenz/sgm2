/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.edificaciones;

import com.origami.sgm.entities.HistoricoTramiteDet;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author CarlosLoorVargas
 */
@Local
public interface FusionPrediosServices {
    
    public boolean fusionarPredios(List<HistoricoTramiteDet> det);
    
}
