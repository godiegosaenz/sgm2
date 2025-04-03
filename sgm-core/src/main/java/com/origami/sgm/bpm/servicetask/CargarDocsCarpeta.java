package com.origami.sgm.bpm.servicetask;

import com.origami.sgm.services.ejbs.censocat.OmegaUploader;
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

/**
 *
 * @author CarlosLoorVargas
 */

public class CargarDocsCarpeta implements JavaDelegate {

    private LectorArchivos lectorArchivos;
    private TaskService service;
    private byte[] data = null;
    
    @Override
    public void execute(DelegateExecution de) throws Exception {
        try {
            service = de.getEngineServices().getTaskService();
            if (de.getVariable("listaArchivos") != null && de.getVariable("tramite") != null) {
                List<Archivo> archivosDefinitivos = (List<Archivo>) de.getVariable("listaArchivosFinal");
                List<Archivo> archivosSubidos = (List<Archivo>) de.getVariable("listaArchivos");
                if (archivosSubidos.isEmpty() != true) {

                    String sc = de.getVariable("tramite").toString() + "-" + de.getProcessInstanceId();

                    for (Archivo e : archivosSubidos) {
                        de.setVariable("rutaArchivo", e.getRuta());
                        data = this.lectorArchivos.leerArchivo(e.getRuta());
                        String nombre = e.getNombre();
                        String descripcionAdjunto = "Archivo Adjunto de tarea "
                                + nombre + "(" + de.getId() + ")";

                        Long id = null;
                        System.out.println("Nombre Archivo: " + e.getNombre() + " tipo: " + e.getTipo());
                        if (e.getTipo() != null) {
                            id = getUploader().uploadFile(new ByteArrayInputStream(data), nombre, e.getTipo());
                            if (id != null) {
                                service.createAttachment(e.getTipo(), null, de.getProcessInstanceId(), nombre, descripcionAdjunto, id.toString());
                            }
                        } else {
                            id = getUploader().uploadFile(new ByteArrayInputStream(data), nombre, "url");
                            if (id != null) {
                                service.createAttachment("url", null, de.getProcessInstanceId(), nombre, descripcionAdjunto, id.toString());
                            }
                        }
                    }
                    for (Archivo arc : archivosSubidos) {
                        archivosDefinitivos.add(arc);
                    }
                    //de.setVariable("subCarpeta", sc);
                    de.setVariable("listaArchivosFinal", archivosDefinitivos);
                    archivosSubidos = new ArrayList<>();
                    de.setVariable("listaArchivos", archivosSubidos);
                }
            }
            de.setVariable("idTarea", null);
        } catch (Exception e) {
            Logger.getLogger(CargarDocsCarpeta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public LectorArchivos getLectorArchivos() {
        return lectorArchivos;
    }

    public void setLectorArchivos(LectorArchivos lectorArchivos) {
        this.lectorArchivos = lectorArchivos;
    }
    
    public OmegaUploader getUploader(){
        return BeanProvider.getContextualReference(OmegaUploader.class, false);
    }

}
