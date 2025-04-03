/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import com.origami.config.SisVars;
import javax.annotation.PostConstruct;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import util.Faces;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 * @date 29/08/2016
 */
@FacesComponent(value = "detalleTareasCont")
public class detalleTareasControler extends UINamingContainer {

    @PostConstruct
    public void init() {

    }

    public void descargarDocumento(String url) {
        String s[] = url.split("nodeRef=");
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "DescargarDocsRepositorio?idDoc=" + s[1]);
    }

    public void showDocument(String url) {
        Faces.redirectNewTab(url);
    }

    public void descargarDocumento(String url, String type) {
        if (url != null && url.trim().length() > 0) {
            JsfUti.redirectNewTab(SisVars.urlbase + "DescargarDocsRepositorio?idDoc=" + url + "&type=" + type);
        }
    }

    public void showDocument1(String url, String type) {
        if (url != null && url.trim().length() > 0) {
            JsfUti.redirectNewTab(SisVars.urlbase + "showDocuments?idDoc=" + url + "&type=" + type);
        }
    }

}
