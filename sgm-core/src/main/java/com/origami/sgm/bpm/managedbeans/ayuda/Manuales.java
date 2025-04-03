/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.ayuda;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author origami
 */
@Named
@ViewScoped
public class Manuales implements Serializable{

    public void descargarDocumento(String url) {
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "DescargarDocsRepositorio?id=" + url);
    }
}
