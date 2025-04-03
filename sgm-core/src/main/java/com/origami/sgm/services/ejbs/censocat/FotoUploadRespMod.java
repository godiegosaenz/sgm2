/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.censocat;

import java.io.Serializable;

/**
 *
 * @author Fernando
 */
public class FotoUploadRespMod implements Serializable{
    
    private Boolean ok;
    private Long fotoId;

    public FotoUploadRespMod() {
    }

    public FotoUploadRespMod(Boolean ok, Long fotoId) {
        this.ok = ok;
        this.fotoId = fotoId;
    }
    

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public Long getFotoId() {
        return fotoId;
    }

    public void setFotoId(Long fotoId) {
        this.fotoId = fotoId;
    }

    

    
    
    
    
    
}
