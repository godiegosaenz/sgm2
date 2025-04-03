/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.edificaciones;

import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.OtrosTramites;
import com.origami.sgm.entities.OtrosTramitesHasPermiso;
import java.util.List;

/**
 *
 * @author Joao Sanga
 */
public interface OtrosTramitesServices {
    
    public Boolean guardarOtrosTramitesEdicion(HistoricoReporteTramite hrt, HistoricoTramiteDet htd, Observaciones obs, OtrosTramitesHasPermiso ohtp, OtrosTramites oTramite, HistoricoTramites ht, List<HistoricoTramiteDet> htdList, List<HistoricoReporteTramite> hrtList);
    
    public Boolean guardarOtrosTramites(HistoricoReporteTramite hrt, HistoricoTramiteDet htd, Observaciones obs, OtrosTramitesHasPermiso ohtp, OtrosTramites oTramite, HistoricoTramites ht);
    
}
