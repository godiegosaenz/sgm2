/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.agenda;

import com.origami.sgm.bpm.models.DatosAgenda;
import com.origami.sgm.entities.Agenda;
import com.origami.sgm.entities.AgendaDet;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author CarlosLoorVargas
 */
@Local
public interface AgendaServ {
    
    public DatosAgenda guardarAgenda(DatosAgenda da);
    
    public boolean actIntervinientes(Agenda a, List<AgendaDet> d);
    
}
  
  