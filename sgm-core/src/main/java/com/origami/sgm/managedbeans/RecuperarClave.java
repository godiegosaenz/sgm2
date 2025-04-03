/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.config.SisVars;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.EmailUtil;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class RecuperarClave implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    protected Entitymanager acl;

    protected String action;
    protected Boolean accion1 = false;
    protected Boolean accion2 = false;
    protected String clave1;
    protected String clave2;
    protected Long fechalong;

    protected Boolean mostrar = false;
    protected Boolean mostrarlist = false;
    protected Integer tipo = 0;
    protected String valor;
    protected String nombres;
    protected String email;
    protected String clave;
    protected String user;
    protected SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    protected AclUser usuario = new AclUser();
    protected List<AclUser> listUsers = new ArrayList<>();
    protected Long iduser;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            if (action != null) {
                switch (action) {
                    case "changecode":
                        accion1 = true;
                        accion2 = false;
                        break;
                    case "newcode":
                        if (iduser != null && user != null && fechalong != null) {
                            Date fecha = new Date(fechalong);
                            if (fecha.after(new Date())) {
                                usuario = (AclUser) acl.find(AclUser.class, iduser);
                                if (usuario != null) {
                                    String temp = new String(user.getBytes("ISO-8859-1"), "UTF-8");
                                    if (usuario.getUsuario().equals(temp)) {
                                        accion1 = false;
                                        accion2 = true;
                                    }
                                }
                            }
                        }
                        break;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RecuperarClave.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlg() {
        JsfUti.update("formConfirm");
        JsfUti.executeJS("PF('dlgConfirm').show();");
    }

    public void buscarUsuarios() {
        if (tipo > 0) {
            if (valor != null) {
                switch (tipo) {
                    case 1:
                        buscarPorUser();
                        break;
                    case 2:
                        buscarPorCorreo();
                        break;
                }
            } else {
                JsfUti.messageError(null, Messages.faltanCampos, "");
            }
        } else {
            JsfUti.messageError(null, Messages.faltanCampos, "");
        }
    }

    public void buscarPorUser() {
        try {
            usuario = (AclUser) acl.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{valor});
            if (usuario == null) {
                JsfUti.messageError(null, Messages.sinCoincidencias, "");
                mostrar = false;
                mostrarlist = false;
                JsfUti.update("mainForm");
            } else {
                if (usuario.getEnte() != null) {
                    if (usuario.getEnte().getEnteCorreoCollection() != null) {
                        if (!usuario.getEnte().getEnteCorreoCollection().isEmpty()) {
                            if (usuario.getEnte().getEsPersona()) {
                                nombres = usuario.getEnte().getNombres() + " " + usuario.getEnte().getApellidos();
                            } else {
                                nombres = usuario.getEnte().getRazonSocial();
                            }
                            List<EnteCorreo> list = (List<EnteCorreo>) usuario.getEnte().getEnteCorreoCollection();
                            email = list.get(0).getEmail();
                            user = usuario.getUsuario();
                            clave = usuario.getPass();
                            iduser = usuario.getId();
                            mostrar = true;
                            mostrarlist = false;
                            JsfUti.update("mainForm");
                        } else {
                            JsfUti.messageError(null, Messages.userSinCorreo, "");
                        }
                    } else {
                        JsfUti.messageError(null, Messages.userSinCorreo, "");
                    }
                } else {
                    JsfUti.messageError(null, "Este usuario NO puede recuperar la clave. Comuníquese con Sistemas.", "");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RecuperarClave.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void buscarPorCorreo() {
        try {
            listUsers = new ArrayList<>();
            List<CatEnte> listEnte = (List<CatEnte>) acl.findAll(Querys.getListEnteByCorreo, new String[]{"email"}, new Object[]{valor});
            for (CatEnte ente : listEnte) {
                if (!ente.getAclUserCollection().isEmpty()) {
                    for (AclUser u : ente.getAclUserCollection()) {
                        listUsers.add(u);
                    }
                }
            }
            if (listUsers.isEmpty()) {
                JsfUti.messageError(null, Messages.sinCoincidencias, "");
                mostrarlist = false;
                mostrar = false;
            } else {
                mostrarlist = true;
            }
            JsfUti.update("mainForm");
        } catch (Exception e) {
            Logger.getLogger(RecuperarClave.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void mostrarDialogo(AclUser us) {
        if (us.getEnte().getEsPersona()) {
            nombres = us.getEnte().getNombres() + " " + us.getEnte().getNombres();
        } else {
            nombres = us.getEnte().getRazonSocial();
        }
        user = us.getUsuario();
        clave = us.getPass();
        iduser = us.getId();
        email = valor;
        JsfUti.update("formConfirm");
        JsfUti.executeJS("PF('dlgConfirm').show();");
    }

    public void enviarCorreo() {
        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 24);
            MsgFormatoNotificacion msg = (MsgFormatoNotificacion) acl.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{1L});
            if (msg != null) {
                String contenido = msg.getHeader() + "Para cambiar su clave presione <a href=\"" + SisVars.urlPublica + "/admin/users/recuperarClave.xhtml?action=newcode&code1=" + iduser + "&code2=" + user + "&code3=" + cal.getTimeInMillis() + "\" target=\"_new\">AQUI.</a> <br><br><br>"
                        + "Se solicitó recuperar su clave el día : " + ft.format(new Date()) + "<br>Si no lo hizo usted, por favor comuníquese con el Departamento de Sistemas.<br>"
                        + "El enlace tiene duración de 24 horas. Déspues de este tiempo solicite nuevamente Recuperar su Clave.<br>"
                        + "Es necesario realizar el cambio de su clave periódicamente por seguridad.<br><br>"
                        + msg.getFooter();
                //Email correo = new Email(email, "Solicitud de Recuperacion de Clave", contenido);
                EmailUtil mail = new EmailUtil();
                //boolean flag = correo.sendMail();
                boolean flag = mail.sendEmail(email, "Solicitud de Recuperacion de Clave", contenido, null, null);
                if (flag) {
                    JsfUti.redirectFaces("/");
                } else {
                    JsfUti.messageError(null, Messages.correoNoEnviado, "");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RecuperarClave.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void guardarClaveNew() {
        if (clave1 != null && clave2 != null) {
            if (clave1.length() > 7) {
                if (clave1.equals(clave2)) {
                    usuario.setPass(clave2);
                    Boolean flag = acl.update(usuario);
                    if (flag) {
                        JsfUti.redirectFaces("/");
                    } else {
                        JsfUti.messageError(null, Messages.problemaConexion, "");
                    }
                } else {
                    JsfUti.messageError(null, Messages.noCoincidenClaves, "");
                }
            } else {
                JsfUti.messageError(null, Messages.longitudClave, "");
            }
        } else {
            JsfUti.messageError(null, Messages.faltanCampos, "");
        }
    }

    public Long getIduser() {
        return iduser;
    }

    public void setIduser(Long iduser) {
        this.iduser = iduser;
    }

    public Boolean getMostrarlist() {
        return mostrarlist;
    }

    public void setMostrarlist(Boolean mostrarlist) {
        this.mostrarlist = mostrarlist;
    }

    public Boolean getMostrar() {
        return mostrar;
    }

    public void setMostrar(Boolean mostrar) {
        this.mostrar = mostrar;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public AclUser getUsuario() {
        return usuario;
    }

    public void setUsuario(AclUser usuario) {
        this.usuario = usuario;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<AclUser> getListUsers() {
        return listUsers;
    }

    public void setListUsers(List<AclUser> listUsers) {
        this.listUsers = listUsers;
    }

    public Boolean getAccion1() {
        return accion1;
    }

    public void setAccion1(Boolean accion1) {
        this.accion1 = accion1;
    }

    public Boolean getAccion2() {
        return accion2;
    }

    public void setAccion2(Boolean accion2) {
        this.accion2 = accion2;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getClave1() {
        return clave1;
    }

    public void setClave1(String clave1) {
        this.clave1 = clave1;
    }

    public String getClave2() {
        return clave2;
    }

    public void setClave2(String clave2) {
        this.clave2 = clave2;
    }

    public Long getFechalong() {
        return fechalong;
    }

    public void setFechalong(Long fechalong) {
        this.fechalong = fechalong;
    }
}
