/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.samdoc.controller;

import com.origami.config.SamdocVars;
import com.origami.config.SisVars;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author Fernando
 */
@Named
@ApplicationScoped
public class SamdocUploadCompUtil {
    
    public String getSamdocUrl(){
        return SamdocVars.url;
    }
    
    public String formUploadUrl(String nombreLibro, String anioInscripcion, String numeroTomo, String numeroInscripcion
        , String folioInicial, String folioFinal, String clientId){
        StringBuilder urlBuild = new StringBuilder(SamdocVars.url);
        try {
            urlBuild.append("/pages" + SisVars.urlbase + "subida.jsf?")
                    .append("nombreLibro=")
                    .append(nombreLibro)
                    .append("&anioInscripcion=")
                    .append(anioInscripcion)
                    .append("&numeroTomo=")
                    .append(numeroTomo)
                    .append("&numeroInscripcion=")
                    .append(numeroInscripcion)
                    .append("&folioInicial=")
                    .append(folioInicial)
                    .append("&folioFinal=")
                    .append(folioFinal)
                    .append("&clientId=")
                    .append( URLEncoder.encode(clientId, "UTF-8") );
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SamdocUploadCompUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return urlBuild.toString();
    }
    
    public SamdocUploadCompUtil() {
    }
    
    @PostConstruct
    public void init(){
    }
    
    
    
}
