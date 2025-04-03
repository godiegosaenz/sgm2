package com.origami.sgm.bpm.servicetask;

import com.origami.sgm.services.ejbs.censocat.OmegaUploader;
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

public class CargarDocumentosAlfrescoTask implements JavaDelegate {

    private LectorArchivos lectorArchivos;
    private TaskService service;

    @Override
    public void execute(DelegateExecution arg0) throws Exception {
        try {
            service = arg0.getEngineServices().getTaskService();
            List<Archivo> archivosSubidos = (List<Archivo>) arg0.getVariable("listaArchivos");
            String nombre, tipo;
            byte[] data = null;
            if (arg0.getVariable("archivo") != null) {
                data = (byte[]) arg0.getVariable("archivo");
            }
            if (data != null) {

                nombre = (String) arg0.getVariable("nombreArchivoByteArray");
                tipo = (String) arg0.getVariable("tipoArchivoByteArray");
                //System.out.println(carpeta + " - " + nombre);
                arg0.setVariable("archivo", null);
                if (!archivosSubidos.isEmpty()) {
                    List<Archivo> archivosDefinitivos = (List<Archivo>) arg0.getVariable("listaArchivosFinal");

                    for (Archivo e : archivosSubidos) {
                        arg0.setVariable("rutaArchivo", e.getRuta());
                        data = this.lectorArchivos.leerArchivo(e.getRuta());
                        nombre = e.getNombre();
                        String descripcionAdjunto = "Archivo Adjunto de tarea " + nombre + "(" + arg0.getId() + ")";
                        Long id = null;
                        System.out.println("Nombre Archivo: " + e.getNombre() + " tipo: " + e.getTipo());
                        if (e.getTipo() != null) {
                            id = getUploader().uploadFile(new ByteArrayInputStream(data), nombre, e.getTipo());
                            if (id != null) {
                                service.createAttachment(e.getTipo(), null, arg0.getProcessInstanceId(), nombre, descripcionAdjunto, id.toString());
                            }
                        } else {
                            id = getUploader().uploadFile(new ByteArrayInputStream(data), nombre, "url");
                            if (id != null) {
                                service.createAttachment("url", null, arg0.getProcessInstanceId(), nombre, descripcionAdjunto, id.toString());
                            }
                        }
                        if (id != null) {
                            EjbsCaller.getHistTramRep(arg0.getVariable("idReporte"), id.toString());
                        }
                    }
                    for (Archivo arc : archivosSubidos) {
                        archivosDefinitivos.add(arc);
                    }
                    arg0.setVariable("listaArchivosFinal", archivosDefinitivos);
                    archivosSubidos = new ArrayList<>();
                    arg0.setVariable("listaArchivos", archivosSubidos);
                }
            } else if (archivosSubidos != null) {
                if (archivosSubidos.isEmpty() != true) {
                    List<Archivo> archivosDefinitivos = (List<Archivo>) arg0.getVariable("listaArchivosFinal");

                    for (Archivo e : archivosSubidos) {
                        arg0.setVariable("rutaArchivo", e.getRuta());
                        data = this.lectorArchivos.leerArchivo(e.getRuta());
                        nombre = e.getNombre() + "( " + arg0.getProcessInstanceId() + " )";
                        String descripcionAdjunto = "Archivo Adjunto de tarea " + nombre + "(" + arg0.getId() + ")";
                        Long id = null;
                        if (e.getTipo() != null) {
                            id = getUploader().uploadFile(new ByteArrayInputStream(data), nombre, e.getTipo());
                            if (id != null) {
                                service.createAttachment(e.getTipo(), null, arg0.getProcessInstanceId(), nombre, descripcionAdjunto, id.toString());
                            }
                        } else {
                            id = getUploader().uploadFile(new ByteArrayInputStream(data), nombre, "url");
                            if (id != null) {
                                service.createAttachment("url", null, arg0.getProcessInstanceId(), nombre, descripcionAdjunto, id.toString());
                            }
                        }
                    }
                    for (Archivo arc : archivosSubidos) {
                        archivosDefinitivos.add(arc);
                    }
                    arg0.setVariable("listaArchivosFinal", archivosDefinitivos);
                    archivosSubidos = new ArrayList<>();
                    arg0.setVariable("listaArchivos", archivosSubidos);
                    //System.out.println("////fin subidos");
                }
            }

        } catch (Exception e) {
            Logger.getLogger(CargarDocumentosAlfrescoTask.class.getName()).log(Level.SEVERE, null, e);
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
