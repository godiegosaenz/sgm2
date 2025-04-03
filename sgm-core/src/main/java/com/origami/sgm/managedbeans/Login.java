/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclLogin;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.imageio.stream.FileImageOutputStream;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;
import util.Utils;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class Login implements Serializable {

    private static final long serialVersionUID = 8799656478674716638L;
    
    @javax.inject.Inject
    private Entitymanager acl;
    private String user;
    private String pass;
    private String email;
    private List<Long> roles, depts;
    
    @Inject
    private UserSession sess;
    
    private AclLogin aclLogin;

    public void doLogin() {
        try {
            if (user != null && pass != null) {
                aclLogin = new AclLogin();
//                System.out.println(Utils.encriptSHAHex(pass));
                AclUser u = (AclUser) acl.find(Querys.getUsuariobyUserPass, new String[]{"user", "pass"}, new Object[]{user, Utils.encriptSHAHex(pass)});
                if (u != null) {
                    //sess = (UserSession) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userSession");
                    sess.setUserId(u.getId());
                    sess.setName_user(user);
                    sess.setTemp(true);
                    sess.setEsDirector(u.getUserIsDirector());
                    if (u.getEnte() != null) {
                        sess.setNombrePersonaLogeada(u.getEnte().getNombreCompleto());
                    } else {
                        sess.setNombrePersonaLogeada("");
                    }
                    roles = new ArrayList<>();
                    depts = new ArrayList<>();
                    if (!u.getAclRolCollection().isEmpty()) {
                        for (AclRol r : u.getAclRolCollection()) {
                            roles.add(r.getId());
                            if (r.getDepartamento() != null) {
                                depts.add(r.getDepartamento().getId());
                            }
                        }
                    }
                    sess.setRoles(roles);
                    sess.setDepts(depts);
                    sess.setEsSuperUser(u.getEsSuperUser());
                    sess.getDatosEquipo();
                    
                    aclLogin.setIpUserSession(sess.getDatosEquipo());
                    aclLogin.setUserSesionId(BigInteger.valueOf(u.getId()));
                    aclLogin.setUserSessionName(sess.getName_user());
                    aclLogin.setFechaDoLogin(new Date());
                    
                    try {
                        aclLogin = (AclLogin) acl.persist(aclLogin);
                        sess.setAclLogin(aclLogin);
                        sess.setIdAclLogin(aclLogin.getId());
                    } catch (Exception e) {
                        Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, e);
                    }
                    
                    if (u.getImagenPerfil() != null) {
                        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/image/usuarios") + File.separator + u.getId() + ".jpg";
                        File f = new File(u.getImagenPerfil());
                        if (f.length() != 0L) {
                            try {
                                byte[] resultado = new byte[(int) f.length()];
                                BufferedInputStream stream = new BufferedInputStream(new FileInputStream(f));
                                stream.read(resultado);
                                FileImageOutputStream imageOutput;
                                imageOutput = new FileImageOutputStream(new File(path));
                                imageOutput.write(resultado, 0, resultado.length);
                                imageOutput.flush();
                                imageOutput.close();
                                sess.setPerfil("/image/usuarios/" + u.getId() + ".jpg");
                            } catch (Exception e) {
                                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, e);
                            }
                        } else {
                            sess.setPerfil("/image/usuarios/avatar2.jpg");
                        }
                    } else {
                        sess.setPerfil("/image/usuarios/avatar2.jpg");
                    }
                    sess.redirectUrlSolicitada();
                } else {
                    JsfUti.messageError(null, Messages.credencialesInvalidas, "");
                }
            } else {
                JsfUti.messageError(null, Messages.credencialesInvalidas, "");
            }
        } catch (Exception e) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void redirectRecuperarClave() {
        JsfUti.redirectFaces("/faces/admin/users/recuperarClave.xhtml?action=changecode");
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}
