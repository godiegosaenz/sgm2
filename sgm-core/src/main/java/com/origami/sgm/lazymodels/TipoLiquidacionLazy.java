/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RenTipoLiquidacion;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Joao Sanga
 */
public class TipoLiquidacionLazy extends BaseLazyDataModel<RenTipoLiquidacion>{

    private Integer tipo;
    private Boolean soloPadres = false;
    private Boolean activos = true;
    
    public TipoLiquidacionLazy(Integer tipo) {        
        super(RenTipoLiquidacion.class, "nombreTransaccion", "ASC");
        this.tipo = tipo;
    }
    
    public TipoLiquidacionLazy(Integer tipo, Boolean soloPadres) {        
        super(RenTipoLiquidacion.class, "nombreTransaccion", "ASC");
        this.tipo = tipo;
        this.soloPadres = soloPadres;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        //ctp = crit.createCriteria("tipo", JoinType.LEFT_OUTER_JOIN);
        
        if (filters.containsKey("nombreTitulo")) {
            crit.add(Restrictions.ilike("nombreTitulo", "%" + filters.get("nombreTitulo").toString().trim() + "%"));
        }
        if (filters.containsKey("nombreTransaccion")) {
            crit.add(Restrictions.ilike("nombreTransaccion", "%" + filters.get("nombreTransaccion").toString().trim() + "%"));
        }
        if (filters.containsKey("prefijo")) {
            crit.add(Restrictions.ilike("prefijo", "%" + filters.get("prefijo").toString().trim() + "%"));
        }
        
        if(tipo == 1){
            crit.add(Restrictions.isNotNull("nombreTitulo"));
        }
        if(tipo == 2){
            crit.add(Restrictions.isNotNull("nombreTransaccion"));            
        }
        if(soloPadres){
            crit.add(Restrictions.eq("transaccionPadre", 0L));
        }
        crit.add(Restrictions.eq("estado", activos));
    }

    public Boolean getActivos() {
        return activos;
    }

    public void setActivos(Boolean activos) {
        this.activos = activos;
    }
    
}
