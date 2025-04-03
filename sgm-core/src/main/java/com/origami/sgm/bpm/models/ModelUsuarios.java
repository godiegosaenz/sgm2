/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeDepartamento;
import java.io.Serializable;

/**
 *
 * @author Angel Navarro
 */
public class ModelUsuarios implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private AclUser usuario;
    private GeDepartamento departamento;    

    public GeDepartamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(GeDepartamento departamento) {
        this.departamento = departamento;
    }

    public AclUser getUsuario() {
        return usuario;
    }

    public void setUsuario(AclUser usuario) {
        this.usuario = usuario;
    }
    
    
}
