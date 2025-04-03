/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces;

import com.origami.sgm.services.bpm.BpmBaseEngine;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.util.concurrent.Future;
import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Anyelo
 */
//@Local
public interface VentanillaPublica {
    
    public Boolean activarCuenta(Long id, String codigo);
    
    public Future<Integer> uploadFile(HttpServletRequest request, String carpetaTramite, String carpetaRepo, Long id, String idProcess);

    public BpmBaseEngine getEngine();

    public Entitymanager getManager();
    
}
