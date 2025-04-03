/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.config.SisVars;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.managedbeans.component.Busquedas;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import util.JsfUti;
import util.Messages;
import util.PhoneUtils;
import util.Utils;
import util.VerCedulaUtils;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class ClienteTramite extends BpmManageBeanBaseRoot implements Serializable {

    protected String emailNew = "";
    protected String tlfnNew = "";
    protected VerCedulaUtils vcu = new VerCedulaUtils();
    protected CatEnte persona = new CatEnte();
    protected EnteCorreo email = new EnteCorreo();
    protected EnteTelefono telefono = new EnteTelefono();
    
    public void inicializarVariables() {
        emailNew = "";
        tlfnNew = "";
        persona = new CatEnte();
    }

    public void agregarEmail() {
        if (emailNew != null) {
            emailNew = emailNew.trim();
            Boolean flag = true;
            for (EnteCorreo c : persona.getEnteCorreoCollection()) {
                if (c.getEmail().equals(emailNew)) {
                    flag = false;
                }
            }
            if (flag) {
                if (Utils.validarEmailConExpresion(emailNew)) {
                    email = new EnteCorreo();
                    email.setEmail(emailNew);
                    persona.getEnteCorreoCollection().add(email);
                    emailNew = "";
                } else {
                    JsfUti.messageInfo(null, Messages.correoInvalido, "");
                }
            } else {
                JsfUti.messageInfo(null, Messages.elementoRepetido, "");
            }
        } else {
            JsfUti.messageInfo(null, Messages.campoVacio, "");
        }
    }

    public void eliminarEmail(EnteCorreo c) {
        if (c.getId() != null) {
            persona.getEnteCorreoCollection().remove(c);
            acl.delete(c);
        } else {
            int ind = -1;
            int cont = 0;
            for (EnteCorreo co : persona.getEnteCorreoCollection()) {
                if (co.getEmail().equals(c.getEmail())) {
                    ind = cont;
                }
                cont++;
            }
            if (ind >= 0) {
                persona.getEnteCorreoCollection().remove(ind);
            }
        }
    }

    public void agregarTlfn() {
        if (tlfnNew != null) {
            tlfnNew = tlfnNew.trim();
            Boolean flag = true;
            for (EnteTelefono t : persona.getEnteTelefonoCollection()) {
                if (t.getTelefono().equals(tlfnNew)) {
                    flag = false;
                }
            }
            if (flag) {
                if (Utils.validateNumberPattern(tlfnNew)) {
                    if (PhoneUtils.getValidNumber(tlfnNew, SisVars.region)) {
                        telefono = new EnteTelefono();
                        telefono.setTelefono(tlfnNew);
                        persona.getEnteTelefonoCollection().add(telefono);
                        tlfnNew = "";
                    } else {
                        JsfUti.messageInfo(null, Messages.tlfnInvalido, "");
                    }
                } else {
                    JsfUti.messageInfo(null, Messages.tlfnInvalido, "");
                }
            } else {
                JsfUti.messageInfo(null, Messages.elementoRepetido, "");
            }
        } else {
            JsfUti.messageInfo(null, Messages.campoVacio, "");
        }
    }

    public void eliminarTlfn(EnteTelefono t) {
        if (t.getId() != null) {
            persona.getEnteTelefonoCollection().remove(t);
            acl.delete(t);
        } else {
            int ind = -1;
            int cont = 0;
            for (EnteTelefono te : persona.getEnteTelefonoCollection()) {
                if (te.getTelefono().equals(t.getTelefono())) {
                    ind = cont;
                }
                cont++;
            }
            if (ind >= 0) {
                persona.getEnteTelefonoCollection().remove(ind);
            }
        }
    }

    public Boolean existeEnte(String ciruc) {
        CatEnte temp = (CatEnte) acl.findNoProxy(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{ciruc});
        return temp != null;
    }

    public void agregarCorreoGenerico() {
        emailNew = SisVars.correoClienteGenerico;
        this.agregarEmail();
    }

    public Boolean validarEnte(CatEnte e) {
        if (e.getCiRuc() != null) {
            if (e.getEsPersona()) {
                return ((e.getApellidos() != null) && (e.getNombres() != null));
            } else {
                return e.getRazonSocial() != null;
            }
        } else {
            return false;
        }
    }

    public Boolean guardarCliente() {
        Boolean flag = false;
        try {
            if (this.validarEnte(persona)) {
                if (!persona.getEnteCorreoCollection().isEmpty()) {
                    persona.setUserCre(session.getName_user());
                    persona.setFechaCre(new Date());
                    persona = acl.guardarEnteCorreosTlfns(persona);
                    flag = persona.getId() != null;
                } else {
                    JsfUti.messageError(null, Messages.userSinCorreo, "");
                }
            } else {
                JsfUti.messageError(null, Messages.faltanCampos, "");
            }
        } catch (Exception e) {
            flag = false;
            Logger.getLogger(ClienteTramite.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }

    public Boolean guardarClienteSinCorreo() {
        Boolean flag = false;
        try {
            if (this.validarEnte(persona)) {
                persona.setUserCre(session.getName_user());
                persona.setFechaCre(new Date());
                persona = acl.guardarEnteCorreosTlfns(persona);
                flag = true;
            } else {
                JsfUti.messageError(null, Messages.faltanCampos, "");
            }
        } catch (Exception e) {
            flag = false;
            Logger.getLogger(ClienteTramite.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }

    public Boolean editarCliente() {
        Boolean flag = false;
        try {
            if (this.validarEnte(persona)) {
                if (!persona.getEnteCorreoCollection().isEmpty()) {
                    persona.setUserMod(session.getName_user());
                    persona.setFechaMod(new Date());
                    flag = acl.editarEnteCorreosTlfns(persona);
                } else {
                    JsfUti.messageError(null, Messages.userSinCorreo, "");
                }
            } else {
                JsfUti.messageError(null, Messages.faltanCampos, "");
            }
        } catch (Exception e) {
            flag = false;
            Logger.getLogger(ClienteTramite.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }

    public Boolean editarClienteSinCorreo() {
        Boolean flag = false;
        try {
            if (this.validarEnte(persona)) {
                persona.setUserMod(session.getName_user());
                persona.setFechaMod(new Date());
                flag = acl.editarEnteCorreosTlfns(persona);
            } else {
                JsfUti.messageError(null, Messages.faltanCampos, "");
            }
        } catch (Exception e) {
            flag = false;
            Logger.getLogger(ClienteTramite.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }

    public String getEmailNew() {
        return emailNew;
    }

    public void setEmailNew(String emailNew) {
        this.emailNew = emailNew;
    }

    public String getTlfnNew() {
        return tlfnNew;
    }

    public void setTlfnNew(String tlfnNew) {
        this.tlfnNew = tlfnNew;
    }

    public EnteCorreo getEmail() {
        return email;
    }

    public void setEmail(EnteCorreo email) {
        this.email = email;
    }

    public EnteTelefono getTelefono() {
        return telefono;
    }

    public void setTelefono(EnteTelefono telefono) {
        this.telefono = telefono;
    }

    public CatEnte getPersona() {
        return persona;
    }

    public void setPersona(CatEnte persona) {
        this.persona = persona;
    }

    public void buscarEnteDlg() {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        options.put("width", "75%");
        options.put("position", "center");
        options.put("closable", true);
        options.put("closeOnEscape", true);
        options.put("responsive", true);
        options.put("contentWidth", "100%");
        RequestContext.getCurrentInstance().openDialog("/resources/dialog/dialogEnte", options, null);
    }

}
