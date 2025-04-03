
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatPredioCultivo;
import java.math.BigInteger;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
/**
 *
 * @author elcid
 */
public class CatPredioCultivoLazy extends BaseLazyDataModel<CatPredioCultivo>{
    private Boolean estado = false;
    private static final Logger log = Logger.getLogger(CatPredioCultivoLazy.class.getName());
    
    public CatPredioCultivoLazy(Boolean estado) {
        super(CatPredioCultivo.class);
        this.estado = estado;
    }
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        try {
            if (filters.containsKey("predio.numPredio")) {
                if (NumberUtils.isNumber(filters.get("predio.numPredio").toString())) {
                    crit.createCriteria("predio").add(Restrictions.eq("numPredio", new BigInteger(filters.get("numPredio").toString().trim())));
                }
            }
            if (filters.containsKey("cultivo.tipo")) {
                crit.createCriteria("cultivo").add(Restrictions.ilike("tipo", "%" + filters.get("cultivo.tipo").toString().trim() + "%"));
            }
            if (filters.containsKey("cultivo.plantacion")) {
                crit.createCriteria("cultivo").add(Restrictions.ilike("plantacion", "%" + filters.get("cultivo.plantacion").toString().trim() + "%"));
            }
            if (filters.containsKey("cultivo.area")) {
                crit.createCriteria("cultivo").add(Restrictions.ilike("area", "%" + filters.get("cultivo.area").toString().trim() + "%"));
            }
            if (filters.containsKey("cultivo.cantidad")) {
                crit.createCriteria("cultivo").add(Restrictions.ilike("cantidad", "%" + filters.get("cultivo.cantidad").toString().trim() + "%"));
            }
            if (filters.containsKey("cultivo.valor")) {
                crit.createCriteria("cultivo").add(Restrictions.ilike("valor", "%" + filters.get("cultivo.valor").toString().trim() + "%"));
            }
            if (filters.containsKey("cultivo.edad")) {
                crit.createCriteria("cultivo").add(Restrictions.ilike("edad", "%" + filters.get("cultivo.edad").toString().trim() + "%"));
            }
            if (filters.containsKey("cultivo.conservacion")) {
                crit.createCriteria("cultivo").add(Restrictions.ilike("conservacion", "%" + filters.get("cultivo.conservacion").toString().trim() + "%"));
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
    }    
}
