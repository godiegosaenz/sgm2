/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RegActo;
import com.origami.sgm.entities.RegLibro;
import com.origami.sgm.entities.RegMovimiento;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anyelo
 */
public class RegMovimientosLazy extends BaseLazyDataModel<RegMovimiento> {

    private Date desde, hasta;
    private Collection list;
    private String valor;
    private int tipo = 0;
    private RegLibro libro;
    private RegActo acto;
    private Long inscripcion;
    private Long repertorio;

    public RegMovimientosLazy() {
        super(RegMovimiento.class, "fechaInscripcion", "DESC");
    }

    public RegMovimientosLazy(String valor, int tipo) {
        super(RegMovimiento.class, "fechaInscripcion", "DESC");
        this.valor = valor;
        this.tipo = tipo;
    }

    public RegMovimientosLazy(Collection values) {
        super(RegMovimiento.class, "fechaInscripcion", "DESC");
        list = values;
    }

    public RegMovimientosLazy(Date desde, Date hasta) {
        super(RegMovimiento.class, "fechaInscripcion", "ASC");
        this.desde = desde;
        this.hasta = hasta;
    }
    
    public RegMovimientosLazy(RegLibro libro, Date desde, Date hasta) {
        super(RegMovimiento.class, "folioFin", "ASC");
        this.libro=libro;
        this.desde = desde;
        this.hasta = hasta;
    }
    
    public RegMovimientosLazy(RegLibro libro,RegActo acto, Long inscripcion, Long repertorio, Date desde, Date hasta) {
        super(RegMovimiento.class);
        this.libro=libro;
        this.acto=acto;
        this.inscripcion=inscripcion;
        this.repertorio=repertorio;
        this.desde=desde;
        this.hasta=hasta;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {

        crit.add(Restrictions.in("estado", new Object[]{"AC", "AN"}));
        
        if (filters.containsKey("libro.nombre")) {
            crit.createCriteria("libro").add(Restrictions.ilike("nombre", "%" + filters.get("libro.nombre").toString().trim() + "%"));
        }

        if (filters.containsKey("acto.nombre")) {
            crit.createCriteria("acto").add(Restrictions.ilike("nombre", "%" + filters.get("acto.nombre").toString().trim() + "%"));
        }

        if (filters.containsKey("numInscripcion")) {
            crit.add(Restrictions.eq("numInscripcion", new Integer(filters.get("numInscripcion").toString().trim())));
        }

        if (filters.containsKey("numRepertorio")) {
            crit.add(Restrictions.eq("numRepertorio", new Integer(filters.get("numRepertorio").toString().trim())));
        }

        if (filters.containsKey("numTramite")) {
            crit.add(Restrictions.eq("numTramite", new Long(filters.get("numTramite").toString().trim())));
        }

        if (filters.containsKey("numTomo")) {
            crit.add(Restrictions.ilike("numTomo", "%" + filters.get("numTomo").toString().trim() + "%"));
        }
        
        if (filters.containsKey("folioInicio")) {
            crit.add(Restrictions.eq("folioInicio", new Integer(filters.get("folioInicio").toString().trim())));
        }

        if (filters.containsKey("folioFin")) {
            crit.add(Restrictions.eq("folioFin", new Integer(filters.get("folioFin").toString().trim())));
        }
        
        if (filters.containsKey("indice")) {
            crit.add(Restrictions.eq("indice", new Integer(filters.get("indice").toString().trim())));
        }


        if (tipo != 0) {
            if (tipo == 1) {
                crit.add(Restrictions.eq("numInscripcion", new Integer(valor.trim())));
            } else if (tipo == 2) {
                crit.add(Restrictions.eq("numRepertorio", new Integer(valor.trim())));
            }
        }

        if (list != null) {
            if (!list.isEmpty()) {
                crit.add(Restrictions.in("id", list));
            }
        }

        if (desde != null && hasta != null) {
            crit.add(Restrictions.between("fechaInscripcion", desde, hasta));
        }
        
        if(this.libro!=null){
            crit.add(Restrictions.eq("libro", this.libro));
        }
        
        if(this.acto!=null){
            crit.add(Restrictions.eq("acto", this.acto));
        }
        
        if(this.inscripcion!=null){
            crit.add(Restrictions.eq("numInscripcion", new Integer(this.inscripcion+"")));
        }
        
        if(this.repertorio!=null){
            crit.add(Restrictions.eq("numRepertorio", new Integer(this.repertorio+"")));
        }

    }

}
