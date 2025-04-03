/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RegpLiquidacionDerechosAranceles;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import util.Utils;

/**
 *
 * @author Anyelo
 */
public class RegpLiquidacionesRegistroLazy extends BaseLazyDataModel<RegpLiquidacionDerechosAranceles> {

    private Date date; 
    
    public RegpLiquidacionesRegistroLazy() {
        super(RegpLiquidacionDerechosAranceles.class, "fecha", "DESC");
    }

    public RegpLiquidacionesRegistroLazy(Date fecha) {
        super(RegpLiquidacionDerechosAranceles.class, "fecha", "DESC");
        date = fecha;
    }
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("numTramiteRp")) {
            if(Utils.validateNumberPattern(filters.get("numTramiteRp").toString().trim()))
                crit.add(Restrictions.eq("numTramiteRp", new BigInteger(filters.get("numTramiteRp").toString().trim())));
        }
        if (filters.containsKey("historicTramite.id")) {
            crit.createCriteria("historicTramite").add(
                    Restrictions.eq("id", new Long(filters.get("historicTramite.id").toString().trim())));
        }
        if (filters.containsKey("historicTramite.nombrePropietario")) {
            crit.createCriteria("historicTramite").add(Restrictions.ilike("nombrePropietario", 
                    "%" + filters.get("historicTramite.nombrePropietario").toString().trim() + "%"));
        }
        if(date != null){
            crit.add(Restrictions.between("fecha", date, Utils.sumarRestarDiasFecha(date, 1)));
        }
    }
    
}
