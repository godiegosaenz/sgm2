/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.session.UserSession;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.CroppedImage;
import util.JsfUti;
import util.Messages;
import util.Utils;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class ActualizaDatos implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private Entitymanager acl;

    @Inject
    protected UserSession session;

    private String image = "/image/usuarios/avatar2.jpg";
    private String imageTemp;
    private CroppedImage croppedImage;
    private CatEnte ente;
    private AclUser aclUser;
    private Boolean foto = false;

    private String username = "";
    private String pass;
    private String passOne;
    private String passTwo;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        image = session.getPerfil();
        username = session.getName_user();
        aclUser = (AclUser) acl.find(AclUser.class, session.getUserId());
        if (aclUser != null) {
            if (aclUser.getEnte() != null) {
                ente = aclUser.getEnte();
            } else {
                ente = new CatEnte();
            }
        } else {
            JsfUti.redirectFaces("/");
        }
    }

    public void showDlgClave() {
        JsfUti.update("formChangePass");
        JsfUti.executeJS("PF('dlgChangePass').show();");
    }

    public void cambioClave() {
        try {
            if (passOne != null && passTwo != null && pass != null) {
                if (Utils.encriptSHAHex(pass).equals(aclUser.getPass())) {
                    if (passOne.length() > 7) {
                        if (passOne.equals(passTwo)) {
                            aclUser.setPass(Utils.encriptSHAHex(passOne));
                            boolean flag = acl.update(aclUser);
                            if (flag) {
                                JsfUti.messageInfo(null, "Datos actualizados con exito. Debe salir e iniciar sesion nuevamente para constatar los cambios.", "");
                                JsfUti.executeJS("PF('dlgChangePass').hide();");
                                JsfUti.update("mainForm");
                            } else {
                                JsfUti.messageError(null, Messages.problemaConexion, "");
                            }
                        } else {
                            JsfUti.messageError(null, Messages.noCoincidenClaves, "");
                        }
                    } else {
                        JsfUti.messageError(null, "La clave debe tener minimo 8 caracteres.", "");
                    }
                } else {
                    JsfUti.messageError(null, "La clave actual ingresada no coincide.", "");
                }
            } else {
                JsfUti.messageError(null, Messages.faltanCampos, "");
            }
        } catch (Exception e) {
            Logger.getLogger(ActualizaDatos.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void uploadFile(FileUploadEvent event) {
        try {
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/image/usuarios");
            String archivo = path + File.separator + event.getFile().getFileName();
            FileOutputStream fileOutputStream = new FileOutputStream(archivo);
            byte[] buffer = new byte[6124];
            int bulk;
            InputStream inputStream = event.getFile().getInputstream();
            while (true) {
                bulk = inputStream.read(buffer);
                if (bulk < 0) {
                    break;
                }
                fileOutputStream.write(buffer, 0, bulk);
                fileOutputStream.flush();
            }
            fileOutputStream.close();
            inputStream.close();
            this.setImageTemp(event.getFile().getFileName());
            foto = true;
        } catch (Exception e) {
            foto = false;
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(ActualizaDatos.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void crop() {

    }

    public Boolean getFoto() {
        return foto;
    }

    public void setFoto(Boolean foto) {
        this.foto = foto;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageTemp() {
        return imageTemp;
    }

    public void setImageTemp(String imageTemp) {
        this.imageTemp = imageTemp;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    public CroppedImage getCroppedImage() {
        return croppedImage;
    }

    public void setCroppedImage(CroppedImage croppedImage) {
        this.croppedImage = croppedImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassOne() {
        return passOne;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setPassOne(String passOne) {
        this.passOne = passOne;
    }

    public String getPassTwo() {
        return passTwo;
    }

    public void setPassTwo(String passTwo) {
        this.passTwo = passTwo;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

}
