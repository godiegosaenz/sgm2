package com.origami.sgm.bpm.managedbeans;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.activiti.engine.repository.ProcessDefinition;
import util.JsfUti;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class ProcessManagement extends BpmManageBeanBaseRoot implements Serializable {

    public static final Long serialVerisonUID = 1L;

    @javax.inject.Inject
    private Entitymanager manager;
    private List<GeTipoTramite> procedures = null, selProcedures;
    private List<ProcessDefinition> process = null;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    private void iniView() {
        procedures = manager.findAll(Querys.getGeTipoTramitesByState, new String[]{"estado"}, new Object[]{true});
    }

    public List<ProcessDefinition> getProcessDeployments() {
        try {
            if (procedures != null) {
                process = new ArrayList<>();
                List<ProcessDefinition> pro = this.getProcesosDesplegados();
                for (ProcessDefinition p : pro) {
                    for (GeTipoTramite t : procedures) {
                        if (p.getKey().equals(t.getActivitykey().trim())) {
                            process.add(p);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ProcessManagement.class.getName()).log(Level.SEVERE, null, e);
        }
        return process;
    }

    public void loadProcess() {
        try {
            if (selProcedures.isEmpty() != true) {
                for (GeTipoTramite p : selProcedures) {
                    this.loadProcessByClassPath(p.getArchivoBpmn());
                }
                JsfUti.update("formMain:tdatos:dtprocessDep");
                JsfUti.messageInfo(null, "Nota", "Se despleagaron " + selProcedures.size() + " proceso(s) satisfactoriamente.");
            } else {
                JsfUti.messageWarning(null, "Advertencia", "Debe elegir al menos un proceso del listado");
            }

        } catch (Exception e) {
            Logger.getLogger(ProcessManagement.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public List<GeTipoTramite> getProcedures() {
        return procedures;
    }

    public void setProcedures(List<GeTipoTramite> procedures) {
        this.procedures = procedures;
    }

    public List<GeTipoTramite> getSelProcedures() {
        return selProcedures;
    }

    public void setSelProcedures(List<GeTipoTramite> selProcedures) {
        this.selProcedures = selProcedures;
    }

}
