/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.geoapi.predios;

import com.origami.geoapi.utils.Bbox;
import com.origami.geoapi.service.Servicio;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.geometry.BoundingBox;

/**
 *
 * @author Fernando
 */
@Singleton
@ApplicationScoped
@Lock(LockType.READ)
public class PrediosService extends Servicio{
    
    private static final Logger LOG = Logger.getLogger(PrediosService.class.getName());
    @Inject
    protected FeatureSourceFactory featureSf;
    @Inject
    protected CatastroGeoConfigs catConf;
    
    public SimpleFeature predioFeature(String claveCatastral){
        try {
            SimpleFeatureSource source = featureSf.predioSource();
            Filter flt = CQL.toFilter( catConf.getClaveAttrName() + " = '" + claveCatastral + "'");
            SimpleFeatureCollection fc = source.getFeatures(flt);
            if(!fc.isEmpty()){
                SimpleFeature sf = fc.features().next(); 
                return sf;
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, claveCatastral, ex);
        } catch (CQLException ex) {
            Logger.getLogger(PrediosService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SimpleFeature predioFeatureGid(Integer gid){
        try {
            SimpleFeatureSource source = featureSf.predioSource();
            Filter flt = CQL.toFilter(  "id = " + gid + "");
            //Filter flt = CQL.toFilter( catConf.getClaveAttrName() + " = " + gid + "");
            SimpleFeatureCollection fc = source.getFeatures(flt);
            if(!fc.isEmpty()){
                SimpleFeature sf = fc.features().next(); 
                return sf;
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, gid+"", ex);
        } catch (CQLException ex) {
            Logger.getLogger(PrediosService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Ejemplo via:
     * https://gis.stackexchange.com/questions/77639/geotools-wfs-feature-retrieval
     * @param claveCatastral
     * @return 
     */
    public Bbox predioBbox(String claveCatastral){
        SimpleFeature sf = predioFeature(claveCatastral);
        if(sf==null) return null;
        
        BoundingBox boundResult = sf.getBounds();
        Bbox bbx = new Bbox(boundResult.getMinX(), boundResult.getMaxX(), boundResult.getMinY(), boundResult.getMaxY());
        return bbx;
    }
    
    public Bbox predioBboxEnvelope(String claveCatastral){
        SimpleFeature sf = predioFeature(claveCatastral);
        if(sf==null) return null;
        
        BoundingBox boundResult = sf.getBounds();
        Bbox bbx = new Bbox(boundResult.getMinX() - catConf.getEnvelopeAdd(), boundResult.getMaxX() + catConf.getEnvelopeAdd(), 
                boundResult.getMinY() - catConf.getEnvelopeAdd(), boundResult.getMaxY() + catConf.getEnvelopeAdd());
        return bbx;
    }
    public Bbox predioBboxEnvelopeGid(Integer gid){
        SimpleFeature sf = predioFeatureGid(gid);
        if(sf==null) return null;
        
        BoundingBox boundResult = sf.getBounds();
        Bbox bbx = new Bbox(boundResult.getMinX() - catConf.getEnvelopeAdd(), boundResult.getMaxX() + catConf.getEnvelopeAdd(), 
                boundResult.getMinY() - catConf.getEnvelopeAdd(), boundResult.getMaxY() + catConf.getEnvelopeAdd());
        return bbx;
    }
    
}
