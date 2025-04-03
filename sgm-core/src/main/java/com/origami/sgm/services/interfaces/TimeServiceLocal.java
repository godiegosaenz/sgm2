/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces;

import javax.ejb.Local;

/**
 *
 * @author Joao Sanga
 */
@Local
public interface TimeServiceLocal {
    
    public void doWork();
    
    public void notificarUsuarioPermisoLocal();
    
}
