/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.servicetask;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import util.Archivo;

/**
 *
 * @author Max
 */
public class AtacharDocumetoListener implements TaskListener {

    @Override
    public void notify(DelegateTask dt) {
        try {
            List<Archivo> archivoDefinitivo = (List<Archivo>) dt.getVariable("listaArchivosFinal");
            TaskService taskService = dt.getExecution().getEngineServices().getTaskService();
            for (Archivo item : archivoDefinitivo) {
                String taskId = dt.getId();
                String processId = dt.getProcessInstanceId();
                String nombreAdjunto = item.getNombre();

                String descripcionAdjunto = "Archivo Adjunto de tarea "
                        + dt.getName() + "(" + dt.getId() + ")";
                if (item.getDescripcion() != null) {
                    descripcionAdjunto = item.getDescripcion() + "(" + dt.getId() + ")";
                }
                taskService.createAttachment(item.getTipo(), taskId, processId, nombreAdjunto, descripcionAdjunto, item.getUrl());
            }
        } catch (Exception e) {
            Logger.getLogger(AtacharDocumetoListener.class.getName()).log(Level.SEVERE, null, e);
        }

    }
}
