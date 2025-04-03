/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatEnte;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anyelo
 */
public class CatEnteLazyJoinCatPredio extends BaseLazyDataModel<CatEnte> {

    private Boolean esPersona;
    private String estado;
    private Integer tipoFiltro;
    private Boolean excepcionales = false;

    public CatEnteLazyJoinCatPredio() {
        super(CatEnte.class);
    }

    public CatEnteLazyJoinCatPredio(Boolean esPersona) {
        super(CatEnte.class, "id", "DESC");
        this.esPersona = esPersona;
    }

    public CatEnteLazyJoinCatPredio(Boolean esPersona, String estado) {
        super(CatEnte.class, "id", "DESC");
        this.estado = estado;
        this.esPersona = esPersona;
    }

    public CatEnteLazyJoinCatPredio(Integer tipoFiltro, Boolean esPersona, String estado) {
        super(CatEnte.class, "id", "DESC");
        this.estado = estado;
        this.esPersona = esPersona;
        this.tipoFiltro = tipoFiltro;
    }

    public CatEnteLazyJoinCatPredio(Integer tipoFiltro, String estado) {
        super(CatEnte.class, "id", "DESC");
        this.estado = estado;
        this.tipoFiltro = tipoFiltro;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        try {

            if (filters.containsKey("ciRuc")) {
                crit.add(Restrictions.ilike("ciRuc", "%" + filters.get("ciRuc").toString().trim() + "%"));
            }
            if (filters.containsKey("nombres")) {
                crit.add(Restrictions.ilike("nombres", "%" + filters.get("nombres").toString().trim() + "%"));
            }
            if (filters.containsKey("apellidos")) {
                crit.add(Restrictions.ilike("apellidos", "%" + filters.get("apellidos").toString().trim() + "%"));
            }
            if (filters.containsKey("razonSocial")) {
                crit.add(Restrictions.ilike("razonSocial", "%" + filters.get("razonSocial").toString().trim() + "%"));
            }
            if (filters.containsKey("nombreComercial")) {
                crit.add(Restrictions.ilike("nombreComercial", "%" + filters.get("nombreComercial").toString().trim() + "%"));
            }
            if (esPersona != null) {
                crit.add(Restrictions.eq("esPersona", esPersona));
            }
            //if(estado != null){
            crit.add(Restrictions.eq("estado", "A"));
            //}
            if (filters.containsKey("excepcionales")) {
                crit.add(Restrictions.eq("excepcionales", Boolean.parseBoolean(filters.get("excepcionales").toString())));
            }

            if (tipoFiltro != null) {
                Criterion clausula = null;
//                System.out.println("TipoFiltro " + tipoFiltro + " Excepcional " + excepcionales);
                switch (tipoFiltro) {
                    case 1:
                        if (!excepcionales) {
                            clausula = Restrictions
                                    .sqlRestriction("{alias}.id IN (SELECT CPP.ENTE FROM  sgm_app.CAT_PREDIO_PROPIETARIO CPP WHERE ESTADO = 'A' GROUP BY CPP.ENTE)");
                        } else {
                            clausula = Restrictions
                                    .sqlRestriction("{alias}.id IN (SELECT CPP.ENTE FROM  sgm_app.CAT_PREDIO_PROPIETARIO CPP WHERE ESTADO = 'A' GROUP BY CPP.ENTE) AND CAST({alias}.ci_ruc AS BIGINT) >= 300000000000000 AND {alias}.ci_ruc IS DISTINCT FROM 'xxxxxxxxxx'");
                        }
                        crit.add(clausula);
                        break;
                    case 2:
                        if (excepcionales) {
                            clausula = Restrictions
                                    .sqlRestriction("{alias}.id IN (SELECT pr.propietario FROM  sgm_app.cat_predio_rustico pr GROUP BY pr.propietario) AND CAST({alias}.ci_ruc AS BIGINT) >= 300000000000000 AND {alias}.ci_ruc IS DISTINCT FROM 'xxxxxxxxxx'");
                        } else {
                            clausula = Restrictions
                                    .sqlRestriction("{alias}.id IN (SELECT pr.propietario FROM  sgm_app.cat_predio_rustico pr GROUP BY pr.propietario)");
                        }
                        crit.add(clausula);
                        break;
                    case 3:
                        if (excepcionales) {
                            clausula = Restrictions
                                    .sqlRestriction("CAST({alias}.ci_ruc AS BIGINT) >= 300000000000000 AND {alias}.ci_ruc IS DISTINCT FROM 'xxxxxxxxxx'");
                            crit.add(clausula);
                        }
                        break;
                }

            }
        } catch (HibernateException hibernateException) {
            LOG.log(Level.SEVERE, "ERROR ", hibernateException);
        }
    }
    private static final Logger LOG = Logger.getLogger(CatEnteLazyJoinCatPredio.class.getName());

    public Boolean getExcepcionales() {
        return excepcionales;
    }

    public void setExcepcionales(Boolean excepcionales) {
        this.excepcionales = excepcionales;
    }

    public Integer getTipoFiltro() {
        return tipoFiltro;
    }

    public void setTipoFiltro(Integer tipoFiltro) {
        this.tipoFiltro = tipoFiltro;
    }

}
