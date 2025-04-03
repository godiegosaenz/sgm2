/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.geo;

import com.origami.sgm.database.DbFunctions;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author Fernando
 */
@Singleton
@Lock(LockType.READ)
@ApplicationScoped
public class GeodataIdentifiers {
    
    @javax.inject.Inject
    private DbFunctions dbFunc;
    
    protected String geodataSchema = "geodata";
    protected String tblGeoPredio = "geo_predio";

    public String getGeoPredio(){
        return dbFunc.schemaTableIdentifier(getGeodataSchema(), getTblGeoPredio());
    }
    
    public GeodataIdentifiers() {
    }

    public String getGeodataSchema() {
        return geodataSchema;
    }

    public String getTblGeoPredio() {
        return tblGeoPredio;
    }
    
     
    
    
}
