/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.acl;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Fernando
 */
public abstract class AclUtils {
    
    public static String getFacesSubdir(){
        return "/faces";
    }
    
    public static String getRelativeUrl(){
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        // devuelve /myaplicacion/faces + /page.xhtml
        HttpServletRequest servletRequest = (HttpServletRequest) ctx.getExternalContext().getRequest();
        
        return servletRequest.getRequestURI().substring(servletRequest.getContextPath().length());
    }
    
    public static String getRelativeUrl(HttpServletRequest servletRequest){
        return servletRequest.getRequestURI().substring(servletRequest.getContextPath().length());
    }
    
    /**
     * Devuelve la direccion interna del recurso web (sin /faces, sin /context, sin domain)
     * Ejm: "/admin/usuarios/xhtml"
     * @param servletRequest
     * @return 
     */
    public static String getUnifiedUrl(HttpServletRequest servletRequest){
        String relUrl = getRelativeUrl(servletRequest);
        String facesSubdir = getFacesSubdir();
        int pos = relUrl.indexOf(facesSubdir + "/");
        if(pos==0){
            return relUrl.substring(facesSubdir.length());
        }
        else {
            return relUrl;
        }
    }
    
}
