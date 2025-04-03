/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.servicetask;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.util.EjbsCaller;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import util.Utils;

/**
 *
 * @author CarlosLoorVargas
 */
public class CatAsigAutomatica implements JavaDelegate {

    private Entitymanager manager;
    private HistoricoTramites tramite;
    private AclRol rol;
    private HashMap<Long, Object> params, sparams;

    @Override
    public void execute(DelegateExecution de) throws Exception {
        try {
            params = new HashMap<>();
            manager = (Entitymanager) EjbsCaller.getTransactionManager();
            if (de.getVariable("tramite") == null) {
                de.setVariable("auto", false);
                return;
            }
            if (de.getVariable("auto") != null && de.getVariable("auto").equals(true)) {
                return;
            }
            tramite = (HistoricoTramites) manager.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{de.getVariable("tramite")});
            if (tramite != null) {
                if (tramite.getTipoTramite().getRolesPerm() != null) {
                    String[] rls = tramite.getTipoTramite().getRolesPerm().split(",");
                    if (rls != null) {
                        de.setVariable("auto", true);
                    } else {
                        de.setVariable("auto", false);
                    }
                    for (String rl : rls) {
                        //obtener roles especificos
                        rol = manager.find(AclRol.class, Long.parseLong(rl));
                        //comprobar la cantidad de tareas que tenga cada usuario y asignar al de menor cargas de trabajo
                        for (AclUser u : rol.getAclUserCollection()) {
                            if (u.getSisEnabled()) {
                                params.put(EjbsCaller.getEngine().getTaskCounterByUser(u.getUsuario()), u.getUsuario());
                            }
                        }
                    }
                    if (!params.isEmpty()) {
                        sparams = Utils.sortByValues(params);
                        //asigna la tarea al tecnico con menor numero de tareas
                        for (Map.Entry<Long, Object> es : sparams.entrySet()) {
                            de.setVariable("tecnico", es.getValue());
                            break;
                        }
                    }
                } else {
                    de.setVariable("auto", false);
                }
            } else {
                de.setVariable("auto", false);
            }
        } catch (Exception e) {
            de.setVariable("auto", false);
            Logger.getLogger(CatAsigAutomatica.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
