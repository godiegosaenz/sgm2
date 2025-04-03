/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.SolicitudCorreccionPredio;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author origami
 */
public class SolicitudCorreccionPredioLazy extends BaseLazyDataModel<SolicitudCorreccionPredio>{
    private AclUser user;
    private Long tipoUsuario;
    
    public SolicitudCorreccionPredioLazy() {
        super(SolicitudCorreccionPredio.class, "id", "DESC");
    }

    public SolicitudCorreccionPredioLazy(AclUser user, Long tipoUsuario) {
        super(SolicitudCorreccionPredio.class, "id", "DESC");
        this.user = user;
        this.tipoUsuario=tipoUsuario;
    }
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if(this.user!=null && this.tipoUsuario!=null){
            if (this.tipoUsuario.equals(1L)) {
                crit.add(Restrictions.eq("solicitante", this.user));
            }else if(this.tipoUsuario.equals(2L)){
                crit.add(Restrictions.eq("directorCatastro", this.user));
            }else if(this.tipoUsuario.equals(3L)){
                crit.add(Restrictions.eq("tecnicoCatastro", this.user));
            }
        }
    }
    
}
