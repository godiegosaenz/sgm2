/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.RenPago;
import java.math.BigInteger;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

/**
 *
 * @author Origami
 */
public class RenPagoLazy extends BaseLazyDataModel<RenPago>{
    
    private Criteria liquidacion;
    private AclUser cajero;
    private Criteria contribuyente;

    public RenPagoLazy() {
        super(RenPago.class, "id", "DESC");
    }
    
    public RenPagoLazy(AclUser cajero) {
        super(RenPago.class, "id", "DESC");
        this.cajero=cajero;
    }
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        liquidacion = crit.createCriteria("liquidacion");
         
        if (filters.containsKey("numComprobante")) {
            crit.add(Restrictions.eq("numComprobante", new Long(filters.get("numComprobante").toString().trim())));
        }
        /*AUN NO ESTA FILTRANDO POR EL CODIGO CATASTRAL YA QUE EN REN_LIQUIDACION NO SE ENCUENTRA  LA COLUMNA*/
         
//        if (filters.containsKey("liquidacion.predio.claveCat")) {
//                 liquidacion.add(Restrictions.ilike("predio.claveCat", "%" + filters.get("liquidacion.predio.claveCat").toString().trim().replaceAll(" ", "%") + "%"));
//          }
        
        
        
        /*PARA EL FILTRO POR ID_LIQUIDACION*/        
        if (filters.containsKey("liquidacion.idLiquidacion")) {
            liquidacion.add(Restrictions.ilike("idLiquidacion", "%" + filters.get("liquidacion.idLiquidacion").toString().trim().replaceAll(" ", "%") + "%"));
        }
      
        
        if (filters.containsKey("liquidacion.numLiquidacion")) {
            liquidacion.add(Restrictions.eq("numLiquidacion", new BigInteger(filters.get("liquidacion.numLiquidacion").toString().trim())));
        }
        if (filters.containsKey("liquidacion.anio")) {
            liquidacion.add(Restrictions.eq("anio", new Integer(filters.get("liquidacion.anio").toString().trim())));
        }
        if(this.cajero!=null){
            crit.add(Restrictions.eq("cajero", this.cajero));
        }
        
        if(this.cajero!=null){
            crit.add(Restrictions.eq("cajero", this.cajero));
        }
        
        if (filters.containsKey("nombreContribuyente")) {
            contribuyente = 
            crit.add(Restrictions.or(Restrictions.ilike("nombreContribuyente", "%" + filters.get("nombreContribuyente").toString().trim().replaceAll(" ", "%") + "%"))
                .add(Restrictions.or(Restrictions.isNotNull("contribuyente")))
            ).createCriteria("contribuyente", JoinType.LEFT_OUTER_JOIN);
            contribuyente.add(Restrictions.or(Restrictions.ilike("apellidos", "%" + filters.get("nombreContribuyente").toString().trim().replaceAll(" ", "%") + "%"))
                    .add(Restrictions.or(Restrictions.isNull("id")))
            );
            //Restrictions.ilike("nombreContribuyente", "%" + filters.get("nombreContribuyente").toString().trim().replaceAll(" ", "%") + "%")
                /*.add(Restrictions.or(
                    Restrictions.isNotNull("contribuyente")
                ));*/
            //contribuyente.add(Restrictions.or(Restrictions.ilike("apellidos", "%" + filters.get("nombreContribuyente").toString().trim().replaceAll(" ", "%") + "%")));
            //papel.add(Restrictions.ilike("papel", "%" + filters.get("papel").toString().trim().replaceAll(" ", "%") + "%"));
        }
        
    }
}
