package com.origami.sgm.bpm.managedbeans.documentos;

import com.origami.config.SisVars;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import util.Archivo;
import util.Faces;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class GestionDocumentos extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    private Entitymanager manager;
    private HistoricoTramites tramite;
    private Long numTramite = null;
    private boolean existe = false, subcarpeta = false;
    private HashMap<String, Object> params;
    private String carpeta, url, instance;
    private Folder folder;
    private Document doc = null;
    private byte[] data = null;
    private static final long serialVersionUID = 1L;

    @PostConstruct
    protected void load() {
        params = new HashMap<>();
        url = SisVars.urlServidorAlfrescoPublica + "share/page/site/smbworkflow/document-details?nodeRef=";
    }

    public void buscar() {
        try {
            if (numTramite != null) {
                tramite = (HistoricoTramites) manager.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{numTramite});
                if (tramite == null) {
                    Faces.messageWarning(null, "Advertencia", "El tramite que desea buscar, no existe");
                    return;
                }
                if (tramite != null && !tramite.getEstado().equalsIgnoreCase("pendiente")) {
                    Faces.messageWarning(null, "Advertencia", "El tramite que desea buscar debe estar pendiente");
                } else {
                    existe = true;
                    if (tramite.getCarpetaRep() != null) {
                        carpeta = tramite.getCarpetaRep();
                        subcarpeta = true;
                    } else if (tramite.getTipoTramite().getCarpeta() != null) {
                        carpeta = tramite.getTipoTramite().getCarpeta();
                        subcarpeta = false;
                    }
                    if (tramite.getIdProcesoTemp() != null) {
                        instance = tramite.getIdProcesoTemp();
                    } else if (tramite.getIdProceso() != null) {
                        instance = tramite.getIdProceso();
                    } else {
                        instance = null;
                    }
                }
            } else {
                Faces.messageWarning(null, "Advertencia", "Debe ingresar un # Tramite valido");
            }

        } catch (Exception e) {
            Logger.getLogger(GestionDocumentos.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void cargarDocumento() {
        try {
            if (this.getFiles() != null && !this.getFiles().isEmpty()) {
                if (carpeta != null && instance != null) {
                    if (this.getCmis() != null) {
                        if (tramite.getTipoTramite().getDisparador() != null) {
                            folder = this.getCmis().getFolder(tramite.getTipoTramite().getDisparador().getCarpeta());
                        } else {
                            folder = this.getCmis().getFolder(tramite.getTipoTramite().getCarpeta());
                        }
                        if (subcarpeta) {
                            folder = this.getCmis().getFolder(tramite.getCarpetaRep());
//                            for (CmisObject c : folder.getChildren()) {
//                                if (c!=null && c.getType().getLocalName().equalsIgnoreCase("folder")) {
//                                    if (c.getName().equalsIgnoreCase(carpeta)) {
//                                        folder = this.getCmis().getFolderByID(c.getId());
//                                        break;
//                                    }
//                                }
//                            }
                        }
                        if (folder == null) {
                            Faces.messageWarning(null, "Advertencia", "El tramite no tiene una carpeta asociada para la carga de documentos");
                            return;
                        }
                        for (Archivo f : this.getFiles()) {
                            data = this.leerArchivo(f.getRuta());
                            doc = this.getCmis().createDocument(folder, f.getNombre(), f.getTipo(), data);
                            f.setUrl(url + doc.getId());
                            if (f.getTipo() != null) {
                                this.getProcessEngine().getTaskService().createAttachment(f.getTipo(), null, instance, f.getNombre(), "Archivo Adjunto de tarea " + (new Date()).getTime() + "(" + instance + ")" + f.getNombre(), f.getUrl());
                            } else {
                                this.getProcessEngine().getTaskService().createAttachment("url", null, instance, f.getNombre(), "Archivo Adjunto de tarea " + (new Date()).getTime() + "(" + instance + ")" + f.getNombre(), f.getUrl());
                            }
                        }
                        existe = false;
                        Faces.messageInfo(null, "Nota", "Se asociaron " + this.getFiles().size() + " documentos al tramite #" + tramite.getId());
                    } else {
                        Faces.messageWarning(null, "Advertencia", "No es posible cargar los documentos");
                    }
                } else {
                    Faces.messageWarning(null, "Advertencia", "El tramite no tiene una carpeta asociada para la carga de documentos");
                }
            } else {
                Faces.messageWarning(null, "Advertencia", "Debe ingresar un documento");
            }
        } catch (Exception e) {
            Logger.getLogger(GestionDocumentos.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public Long getNumTramite() {
        return numTramite;
    }

    public void setNumTramite(Long numTramite) {
        this.numTramite = numTramite;
    }

    public boolean getExiste() {
        return existe;
    }

    public void setExiste(boolean existe) {
        this.existe = existe;
    }

}
