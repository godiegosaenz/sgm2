/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.geoapi.predios;

import com.origami.geoapi.utils.Bbox;
import java.util.logging.Logger;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

/**
 *
 * @author Fernando
 */
@Alternative
public class PredioMockService extends PrediosService{

    @Override
    public Bbox predioBboxEnvelope(String claveCatastral) {
        Logger.getLogger(PredioMockService.class.getName()).info(" USING ALTERNATIVES ");
        return super.predioBboxEnvelope(claveCatastral); 
        
    }
    
    
    
}
