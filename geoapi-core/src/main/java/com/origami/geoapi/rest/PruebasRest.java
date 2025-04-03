/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.geoapi.rest;

import com.origami.geoapi.utils.Bbox;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
@ApplicationScoped
@Path("/pruebas")
public class PruebasRest {
    
    @GET
    @Path("/testWFS")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Bbox testWFS(){
        String getCapabilities = "http://app.orbis/geoserver/ows?service=wfs&version=1.1.0&request=GetCapabilities";
        Map connectionParameters = new HashMap();
        connectionParameters.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", getCapabilities);
        WFSDataStoreFactory  dsf = new WFSDataStoreFactory();
        
        try {
            WFSDataStore dataStore = dsf.createDataStore(connectionParameters);
            SimpleFeatureSource source = dataStore.getFeatureSource("catastro:geo_predio");
            // cod_catast = 04-027-048-16
            Filter flt = CQL.toFilter("cod_catast = '04-027-048-16' ");
            SimpleFeatureCollection fc = source.getFeatures();
            if(!fc.isEmpty()){
                SimpleFeature sf = fc.features().next(); 
                BoundingBox boundResult = sf.getBounds();
                Bbox bbx = new Bbox(boundResult.getMinX(), boundResult.getMaxX(), boundResult.getMinY(), boundResult.getMaxY());
                return bbx;
            }
//            while(fc.features().hasNext()){
//                SimpleFeature sf = fc.features().next();
//                System.out.println(sf.getAttribute("myname"));
//            }
        } catch (IOException ex) {
            Logger.getLogger(PruebasRest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CQLException ex) {
            Logger.getLogger(PruebasRest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
