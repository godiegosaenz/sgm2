/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.servicetask;

import com.origami.sgm.bpm.models.DepartamentoSolicitudServicio;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import util.EmailUtil;

/**
 *
 * @author origami
 */
public class MessengerSolicitud implements JavaDelegate {

    protected EmailUtil mail;

    @Override
    public void execute(DelegateExecution de) throws Exception {
        try {
            mail = new EmailUtil();
            if (de.getVariable("departamento") != null && de.getVariable("subject") != null && de.getVariable("message") != null) {
                DepartamentoSolicitudServicio dss = (DepartamentoSolicitudServicio) de.getVariable("departamento");
                String to = dss.getCorreoDirector();
                if (dss.getAccion() != null && dss.getAccion().equals(2L)) {
                    to = dss.getCorreoResponsable();
                }
                if (de.getVariable("bcc") != null && de.getVariable("cc") != null) {
                    mail.sendEmail(to, de.getVariable("subject").toString(), de.getVariable("message").toString(), de.getVariable("bcc").toString(), de.getVariable("cc").toString());
                } else {
                    mail.sendEmail(to, de.getVariable("subject").toString(), de.getVariable("message").toString(), null, null);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Messenger.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
