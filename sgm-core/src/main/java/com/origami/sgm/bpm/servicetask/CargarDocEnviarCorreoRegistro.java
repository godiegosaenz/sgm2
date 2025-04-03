/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.servicetask;

import com.origami.sgm.services.ejbs.censocat.OmegaUploader;
import com.origami.config.SisVars;
import com.origami.sgm.bpm.managedbeans.catastro.lbrtd.FichaPredialLowValidation;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.util.EjbsCaller;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import util.Archivo;
import util.CmisUtil;

/**
 *
 * @author Anyelo
 */
public class CargarDocEnviarCorreoRegistro implements JavaDelegate {

    private LectorArchivos lectorArchivos;
    private TaskService service;
    private byte[] data = null;

    @Override
    public void execute(DelegateExecution de) throws Exception {
        try {
            service = de.getEngineServices().getTaskService();

            if (de.getVariable("listaArchivos") != null && de.getVariable("tramite") != null) {
                List<Archivo> archivosSubidos = (List<Archivo>) de.getVariable("listaArchivos");
                if (!archivosSubidos.isEmpty()) {

                    for (Archivo e : archivosSubidos) {
                        data = this.lectorArchivos.leerArchivo(e.getRuta());
                        String nombre = e.getNombre();
                        String descripcionAdjunto = "Archivo Adjunto de tarea " + nombre + "(" + de.getId() + ")";
                        Long id = getUploader().uploadFile(new ByteArrayInputStream(data), nombre, e.getTipo());
                        e.setUrl(id.toString());
                        if (id != null) {
                            service.createAttachment(e.getTipo(), null, de.getProcessInstanceId(), nombre, descripcionAdjunto, id.toString());
                        }
                        
                    }
                    de.setVariable("listaArchivos", new ArrayList<>());
                }
            }
           
        } catch (Exception e) {
            Logger.getLogger(CargarDocEnviarCorreoRegistro.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public LectorArchivos getLectorArchivos() {
        return lectorArchivos;
    }

    public void setLectorArchivos(LectorArchivos lectorArchivos) {
        this.lectorArchivos = lectorArchivos;
    }

    public OmegaUploader getUploader() {
        return BeanProvider.getContextualReference(OmegaUploader.class, false);
    }
}
