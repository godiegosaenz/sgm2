/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.lazymodels.AclUsersLazy;
import com.origami.sgm.services.interfaces.solicitudServico.SolicitudServicosServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import util.JsfUti;
import util.Messages;
import util.Utils;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class Usuarios implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private Entitymanager aclServices;

    @javax.inject.Inject
    private SolicitudServicosServices service;

    @Inject
    private ServletSession servletSession;

    private LazyDataModel<AclUser> usuariosLazy;
    private AclUser acluser;
    private AclRol rol;
    private CatEnte ente;
    private List<AclRol> rolsUser;
    private List<EnteCorreo> listCorreos;
    private List<EnteTelefono> listTlfns;
    private String passOne;
    private String passTwo;
    private String cedula;
    private String usuario;

    //REPORTE
    protected List<GeDepartamento> departamentos;
    protected GeDepartamento departamento;
    protected Long estadoDepartamento;
    protected Long tipoDepartamento;
    protected Long tipoUsuario;
    protected Long estadoUsuario;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        usuariosLazy = new AclUsersLazy();
        acluser = new AclUser();

        departamentos = service.getDepartamentos();
    }

    public void inicializarVariables() {
        acluser = new AclUser();
        ente = new CatEnte();
        rolsUser = new ArrayList<>();
        listCorreos = new ArrayList<>();
        listTlfns = new ArrayList<>();
        passOne = "";
        passTwo = "";
        cedula = "";
        usuario = "";
    }

    public void showDlgNewUser() {
        this.inicializarVariables();
        JsfUti.update("frmNewUsr");
        JsfUti.executeJS("PF('dlgNUsr').show();");
    }

    public void showDlgEditUser(AclUser u) {
        acluser = u;
        passOne = "";
        passTwo = "";
        JsfUti.update("formCambioClave");
        JsfUti.executeJS("PF('dlgCambioClave').show();");
    }

    public void cambioClave() {
        try {
            if (acluser.getUsuario() != null) {
                if (passOne != null && passTwo != null) {
                    if (passOne.equals(passTwo)) {
                        acluser.setPass(Utils.encriptSHAHex(passOne));
//                        acluser.setPass(passOne);
                        boolean flag = aclServices.update(acluser);
                        if (flag) {
                            JsfUti.redirectFaces("/admin/users/usersList.xhtml");
                        } else {
                            JsfUti.messageError(null, Messages.problemaConexion, "");
                        }
                    } else {
                        JsfUti.messageError(null, Messages.noCoincidenClaves, "");
                    }
                } else {
                    JsfUti.messageError(null, Messages.faltanCampos, "");
                }
            } else {
                JsfUti.messageError(null, Messages.faltanCampos, "");
            }
        } catch (Exception e) {
            Logger.getLogger(Usuarios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgEditarRoles(AclUser u) {
        this.inicializarVariables();
        acluser = u;
        if (acluser.getAclRolCollection() != null) {
            for (AclRol r : acluser.getAclRolCollection()) {
                rolsUser.add(r);
            }
        }
        JsfUti.update("formCambioRol");
        JsfUti.executeJS("PF('dlgRoles').show();");
    }

    public void agregarRol() {
        if (rol == null) {
            JsfUti.messageInfo(null, Messages.campoVacio, "");
        } else {
            if (rolsUser.contains(rol)) {
                JsfUti.messageInfo(null, Messages.elementoRepetido, "");
            } else {
                rolsUser.add(rol);
            }
        }
    }

    public void eliminarRol(AclRol r) {
        rolsUser.remove(r);
    }

    public void editarRoles() {
        try {
            if (rolsUser.isEmpty()) {
                JsfUti.messageInfo(null, Messages.noListaVacia, "");
            } else {
                acluser.setAclRolCollection(rolsUser);
                boolean flag = aclServices.update(acluser);
                if (flag) {
                    JsfUti.redirectFaces("/admin/users/usersList.xhtml");
                } else {
                    JsfUti.messageError(null, Messages.problemaConexion, "");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Usuarios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgDetalleUser(AclUser u) {
        if (u.getEnte() == null) {
            JsfUti.messageError(null, Messages.noAsignadoPersona, "");
        } else {
            this.inicializarVariables();
            ente = u.getEnte();
            listCorreos = u.getEnte().getEnteCorreoCollection();
            listTlfns = u.getEnte().getEnteTelefonoCollection();
            JsfUti.update("formDetalleCliente");
            JsfUti.executeJS("PF('dlgDetalleClient').show();");
        }
    }

    public void buscarEnte() {
        if (cedula != null) {
            ente = (CatEnte) aclServices.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedula});
            if (ente != null) {
                if (ente.getEnteCorreoCollection() != null) {
                    listCorreos = ente.getEnteCorreoCollection();
                }
                if (ente.getEnteTelefonoCollection() != null) {
                    listTlfns = ente.getEnteTelefonoCollection();
                }
            } else {
                ente = new CatEnte();
                JsfUti.messageError(null, Messages.sinCoincidencias, "");
            }
        } else {
            JsfUti.messageError(null, Messages.campoVacio, "");
        }
    }

    public Boolean userDisponible() {
        AclUser u = (AclUser) aclServices.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{usuario});
        return u == null;
    }

    public void comprobarUsuario() {
        if (usuario != null) {
            if (this.userDisponible()) {
                JsfUti.messageInfo(null, Messages.userDisponible, "");
            } else {
                JsfUti.messageError(null, Messages.enteExiste, "");
            }
        } else {
            JsfUti.messageError(null, Messages.campoVacio, "");
        }
    }

    public void guardarNuevoUser() {
        try {
            if (ente.getId() != null) {
                if (!listCorreos.isEmpty()) {
                    if (usuario != null) {
                        if (this.userDisponible()) {
                            if (!rolsUser.isEmpty()) {
                                if (passOne != null && passTwo != null) {
                                    if (passOne.equals(passTwo)) {
                                        acluser.setSisEnabled(true);
                                        acluser.setUserIsDirector(false);
                                        acluser.setUsuario(usuario);
                                        acluser.setEsSuperUser(Boolean.FALSE);
                                        acluser.setPass(Utils.encriptSHAHex(passOne));
//                                        acluser.setPass(passOne);
                                        acluser.setEnte(ente);
                                        acluser = (AclUser) aclServices.persist(acluser);
                                        if (acluser.getId() != null) {
                                            this.editarRoles();
                                        } else {
                                            JsfUti.messageError(null, Messages.problemaConexion, "");
                                        }
                                    } else {
                                        JsfUti.messageError(null, Messages.noCoincidenClaves, "");
                                    }
                                } else {
                                    JsfUti.messageError(null, Messages.noCoincidenClaves, "");
                                }
                            } else {
                                JsfUti.messageInfo(null, "El usuario debe tener ingresado mínimo un Rol.", "");
                            }
                        } else {
                            JsfUti.messageError(null, Messages.enteExiste, "");
                        }
                    } else {
                        JsfUti.messageError(null, "Debe ingresar el nombre de Usuario.", "");
                    }
                } else {
                    JsfUti.messageError(null, Messages.userSinCorreo, "");
                }
            } else {
                JsfUti.messageError(null, Messages.noAsignadoPersona, "");
            }
        } catch (Exception e) {
            Logger.getLogger(Usuarios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void generarReporteUsuarios() {
        try {
            servletSession.instanciarParametros();
            servletSession.setTieneDatasource(true);
            servletSession.setNombreReporte("usuarios");
            servletSession.setNombreSubCarpeta("administracion");
            servletSession.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.sisLogo1));
            servletSession.agregarParametro("DEPARTAMENTO", departamento != null ? departamento.getId() : null);
            servletSession.agregarParametro("TIPO_DEP", tipoDepartamento);
            servletSession.agregarParametro("ESTADO_DEP", estadoDepartamento);
            servletSession.agregarParametro("USUARIOS", tipoUsuario);
            servletSession.agregarParametro("ESTADO_USUARIOS", estadoUsuario);
            JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");

        } catch (Exception e) {
            Logger.getLogger(Usuarios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public List<AclRol> getRoles() {
        if (departamento != null) {
            return aclServices.findAll(Querys.getIdAclRolByDepts, new String[]{"dept"}, new Object[]{departamento});
        } else {
            return null;
        }
    }

    public LazyDataModel<AclUser> getUsuariosLazy() {
        return usuariosLazy;
    }

    public void setUsuariosLazy(LazyDataModel<AclUser> usuariosLazy) {
        this.usuariosLazy = usuariosLazy;
    }

    public AclUser getAcluser() {
        return acluser;
    }

    public void setAcluser(AclUser acluser) {
        this.acluser = acluser;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    public List<EnteCorreo> getListCorreos() {
        return listCorreos;
    }

    public void setListCorreos(List<EnteCorreo> listCorreos) {
        this.listCorreos = listCorreos;
    }

    public List<EnteTelefono> getListTlfns() {
        return listTlfns;
    }

    public void setListTlfns(List<EnteTelefono> listTlfns) {
        this.listTlfns = listTlfns;
    }

    public String getPassOne() {
        return passOne;
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

    public AclRol getRol() {
        return rol;
    }

    public void setRol(AclRol rol) {
        this.rol = rol;
    }

    public List<AclRol> getRolsUser() {
        return rolsUser;
    }

    public void setRolsUser(List<AclRol> rolsUser) {
        this.rolsUser = rolsUser;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public List<GeDepartamento> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(List<GeDepartamento> departamentos) {
        this.departamentos = departamentos;
    }

    public GeDepartamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(GeDepartamento departamento) {
        this.departamento = departamento;
    }

    public Long getEstadoDepartamento() {
        return estadoDepartamento;
    }

    public void setEstadoDepartamento(Long estadoDepartamento) {
        this.estadoDepartamento = estadoDepartamento;
    }

    public Long getTipoDepartamento() {
        return tipoDepartamento;
    }

    public void setTipoDepartamento(Long tipoDepartamento) {
        this.tipoDepartamento = tipoDepartamento;
    }

    public Long getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(Long tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Long getEstadoUsuario() {
        return estadoUsuario;
    }

    public void setEstadoUsuario(Long estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

}
