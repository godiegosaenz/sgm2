/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.acl;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Fernando
 */
public class AccessLevelRequest implements Serializable{
    
    private String urlAcceso;
    
    private Long idUser;
    private String usuario;
    private List<Long> rolesIds;
    private Boolean esSuperUser = false;
    
    public AccessLevelRequest(){
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public List<Long> getRolesIds() {
        return rolesIds;
    }

    public void setRolesIds(List<Long> rolesIds) {
        this.rolesIds = rolesIds;
    }

    public String getUrlAcceso() {
        return urlAcceso;
    }

    public void setUrlAcceso(String urlAcceso) {
        this.urlAcceso = urlAcceso;
    }

    public Boolean getEsSuperUser() {
        return esSuperUser;
    }

    public void setEsSuperUser(Boolean esSuperUser) {
        if(esSuperUser==null) this.esSuperUser = false;
        this.esSuperUser = esSuperUser;
    }
    
    
    
}
