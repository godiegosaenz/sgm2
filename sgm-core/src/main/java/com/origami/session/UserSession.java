/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.session;

import com.origami.config.RolesEspeciales;
import com.origami.config.SisVars;
import com.origami.sgm.entities.AclLogin;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import jodd.util.StringUtil;
import util.Faces;

/**
 *
 * @author Origami
 */
@Named
@SessionScoped
public class UserSession implements WfUserSession {

    @javax.inject.Inject
    private Entitymanager acl;
    protected String name_user;
    protected String actKey;
    protected String taskID;
    protected Long userId = (long) -1;
    protected String name = "invitado";
    protected String urlSolicitada;
    protected Boolean temp = false;
    protected String departamento;
    protected String nombrePersonaLogeada;
    protected Boolean esDirector;
    protected String perfil = "/image/usuarios/avatar2.jpg";
    protected List<Long> roles = new ArrayList<>();
    protected List<Long> depts = new ArrayList<>();
    private String varTemp;
    protected String ipClient;
    private Map params = null;
    private static final long serialVersionUID = 1L;

    private Boolean esSuperUser;

    private AclLogin aclLogin = new AclLogin();
    private Long idAclLogin;
    
    private String sitioWebMunicipio;

    public String getNombreBienvenida() {
        if (this.esLogueado()) {
            return this.getNombrePersonaLogeada();
        }
        return "invitado";
    }

    public Boolean esLogueado() {
        return userId != null && userId > 0L;
    }

    public void redirectUrlSolicitada() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        if (this.getUrlSolicitada() != null || (this.getUrlSolicitada() != null && this.getUrlSolicitada().equals(ec.getRequestContextPath() + "/faces/login.xhtml"))) {
            try {
                ec.redirect(this.getUrlSolicitada());
                this.setUrlSolicitada(null);
            } catch (IOException ex) {
                Logger.getLogger(UserSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                ec.redirect(ec.getRequestContextPath() + "/");
            } catch (IOException ex) {
                Logger.getLogger(UserSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void redirectInvitadoOld() {
        if (temp) {
            System.out.println(" done!!! ");
            return;
        }
        this.persistReqUrl();
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        try {
            ec.redirect(ec.getRequestContextPath() + "/faces/login.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(UserSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void redirectInvitado() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpServletRequest servletRequest = (HttpServletRequest) ctx.getExternalContext().getRequest();
        String fullURI = servletRequest.getRequestURI();
        if (fullURI.equalsIgnoreCase(servletRequest.getContextPath() + "/faces/admin/users/recuperarClave.xhtml")
                || fullURI.equalsIgnoreCase(servletRequest.getContextPath() + "/faces/admin/users/cambioClave.xhtml")) {
            return;
        }
        if (temp) {
            //System.out.println(" done!!! ");
            return;
        }

        this.persistReqUrl();
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        try {
            ec.redirect(ec.getRequestContextPath() + "/faces/login.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(UserSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void persistReqUrl() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpServletRequest servletRequest = (HttpServletRequest) ctx.getExternalContext().getRequest();
        String fullURI = servletRequest.getRequestURI();
        if (StringUtil.isNotEmpty(servletRequest.getQueryString())) {
            fullURI = fullURI + "?" + servletRequest.getQueryString();
        }
        this.getDatosEquipo();
        this.setUrlSolicitada(fullURI);
    }

    public void logout() {
        if (aclLogin != null) {
            aclLogin.setFechaDoLogout(new Date());
            acl.update(aclLogin);
            aclLogin = null;
        }
        //System.out.println("logout");
        this.setUrlSolicitada(null);
        ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true)).invalidate();
        Faces.redirectFaces2(SisVars.urlbase + "/faces/login.xhtml");
    }

    public String getDatosEquipo() {
        String ip = "";
        try {

            FacesContext ctx = FacesContext.getCurrentInstance();
            HttpServletRequest sr = (HttpServletRequest) ctx.getExternalContext().getRequest();
            sr.getHeader("X-FORWARDED-FOR");

            System.out.println(sr.getRemoteHost() + " >> " + sr.getRemoteAddr());

            this.setIpClient(sr.getRemoteAddr());
            ip = (sr.getRemoteAddr().toString());
            return ip;
        } catch (Exception e) {
            Logger.getLogger(UserSession.class.getName()).log(Level.SEVERE, null, e.getMessage());
        }
        return null;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public Boolean getTemp() {
        return temp;
    }

    public void setTemp(Boolean temp) {
        this.temp = temp;
    }

    @Override
    public Boolean getEsDirector() {
        return esDirector;
    }

    public void setEsDirector(Boolean esDirector) {
        this.esDirector = esDirector;
    }

    @Override
    public String getNombrePersonaLogeada() {
        return nombrePersonaLogeada;
    }

    public void setNombrePersonaLogeada(String nombrePersonaLogeada) {
        this.nombrePersonaLogeada = nombrePersonaLogeada;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    @Override
    public String getUrlSolicitada() {
        return urlSolicitada;
    }

    public void setUrlSolicitada(String urlSolicitada) {
        this.urlSolicitada = urlSolicitada;
    }

    @Override
    public String getName_user() {
        return name_user;
    }

    public void setName_user(String name_user) {
        this.name_user = name_user;
    }

    @Override
    public List<Long> getRoles() {
        return roles;
    }

    public void setRoles(List<Long> roles) {
        this.roles = roles;
    }

    @Override
    public List<Long> getDepts() {
        return depts;
    }

    @Override
    public String getIpClient() {
        return ipClient;
    }

    public void setIpClient(String ipClient) {
        this.ipClient = ipClient;
    }

    public void setDepts(List<Long> depts) {
        this.depts = depts;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getActKey() {
        return actKey;
    }

    public void setActKey(String actKey) {
        this.actKey = actKey;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getVarTemp() {
        return varTemp;
    }

    public void setVarTemp(String varTemp) {
        this.varTemp = varTemp;
    }

    public Boolean getEsSuperUser() {
        return esSuperUser;
    }

    public void setEsSuperUser(Boolean esSuperUser) {
        this.esSuperUser = esSuperUser;
    }

    public Map getParams() {
        return params;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    public AclLogin getAclLogin() {
        return aclLogin;
    }

    public void setAclLogin(AclLogin aclLogin) {
        this.aclLogin = aclLogin;
    }

    public Long getIdAclLogin() {
        return idAclLogin;
    }

    public void setIdAclLogin(Long idAclLogin) {
        this.idAclLogin = idAclLogin;
    }

    public Boolean getIsAdmin() {
        for (Long role : roles) {
            return role.equals(RolesEspeciales.ADMINISTRADOR);
        }
        return false;
    }

    public String getSitioWebMunicipio() {
        return sitioWebMunicipio;
    }

    public void setSitioWebMunicipio(String sitioWebMunicipio) {
        this.sitioWebMunicipio = sitioWebMunicipio;
    }
    
    
}
