
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatBloqueObraEspecial;
import java.math.BigInteger;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class CatBloqueObraEspecialLazy extends BaseLazyDataModel<CatBloqueObraEspecial>{
    private Boolean estado = false;
    private static final Logger log = Logger.getLogger(CatBloqueObraEspecialLazy.class.getName());
    
    public CatBloqueObraEspecialLazy(Boolean estado){
        super(CatBloqueObraEspecial.class);
        this.estado = estado;
    }
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        try {
            if (filters.containsKey("bloque.numBloque")) {
                if (NumberUtils.isNumber(filters.get("bloque.numBloque").toString())) {
                    crit.createCriteria("bloque").add(Restrictions.eq("numBloque", new BigInteger(filters.get("numBloque").toString().trim())));
                }
            }
            if (filters.containsKey("CatBloqueObraEspecial.tipo")) {
                crit.createCriteria("CatBloqueObraEspecial").add(Restrictions.ilike("tipo", "%" + filters.get("CatBloqueObraEspecial.tipo").toString().trim() + "%"));
            }
            if (filters.containsKey("CatBloqueObraEspecial.orden")) {
                crit.createCriteria("CatBloqueObraEspecial").add(Restrictions.ilike("orden", "%" + filters.get("CatBloqueObraEspecial.orden").toString().trim() + "%"));
            }
            if (filters.containsKey("CatBloqueObraEspecial.cantidad")) {
                crit.createCriteria("CatBloqueObraEspecial").add(Restrictions.ilike("cantidad", "%" + filters.get("CatBloqueObraEspecial.cantidad").toString().trim() + "%"));
            }
            if (filters.containsKey("CatBloqueObraEspecial.valorUnitario")) {
                crit.createCriteria("CatBloqueObraEspecial").add(Restrictions.ilike("valorUnitario", "%" + filters.get("CatBloqueObraEspecial.valorUnitario").toString().trim() + "%"));
            }            
            if (filters.containsKey("CatBloqueObraEspecial.conservacion")) {
                crit.createCriteria("CatBloqueObraEspecial").add(Restrictions.ilike("conservacion", "%" + filters.get("CatPredioObraInterna.conservacion").toString().trim() + "%"));
            }
            if (filters.containsKey("CatBloqueObraEspecial.edad")) {
                crit.createCriteria("CatBloqueObraEspecial").add(Restrictions.ilike("edad", "%" + filters.get("CatBloqueObraEspecial.edad").toString().trim() + "%"));
            }            
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
    }
}
