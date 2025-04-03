/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.geoapi.predios;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;

/**
 *
 * @author Fernando
 */
@ApplicationScoped
public class FeatureSourceFactory {
    
    @Inject
    protected CatastroGeoConfigs catastroGeoConfigs;
    
    public SimpleFeatureSource predioSource() throws IOException {
        String getCapabilities = catastroGeoConfigs.getWfsUrl() + "?service=wfs&version=" + catastroGeoConfigs.getWfsVersion() + "&request=GetCapabilities";
        Map connectionParameters = new HashMap();
        connectionParameters.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", getCapabilities);
        WFSDataStoreFactory  dsf = new WFSDataStoreFactory();
        
        WFSDataStore dataStore = dsf.createDataStore(connectionParameters);
        SimpleFeatureSource source = dataStore.getFeatureSource(catastroGeoConfigs.getPredioLayer());
        return source;
    }
    
}
