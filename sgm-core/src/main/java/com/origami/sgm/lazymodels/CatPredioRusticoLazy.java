/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatPredioRustico;
import java.math.BigInteger;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import util.Utils;

/**
 *
 * @author Angel Navarro
 * @Date 22/06/2016
 */
public class CatPredioRusticoLazy extends BaseLazyDataModel<CatPredioRustico> {

    //variable se puso porque habian predios rusticos que no tenian propietarios y cuando se crgarba
    //en el facelet aparecian registrros en fila vacias =O 
    //Opcion Solo Disponible en el Canton San Vicente D:
    private Boolean propietariosExist;

    public CatPredioRusticoLazy(Boolean propietariosExist) {
        super(CatPredioRustico.class);
        this.propietariosExist = propietariosExist;
    }

    public CatPredioRusticoLazy() {
        super(CatPredioRustico.class);
    }

    public CatPredioRusticoLazy(String defaultSorted) {
        super(CatPredioRustico.class, defaultSorted);
    }

    public CatPredioRusticoLazy(String defaultSorted, String defaultSortOrder) {
        super(CatPredioRustico.class, defaultSorted, defaultSortOrder);
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
//        Criteria prop = crit.createCriteria("propietario");
        if(propietariosExist){
            crit.add(Restrictions.isNotNull("propietario"));
        }
        if (filters.containsKey("regCatastral")) {
            crit.add(Restrictions.ilike("regCatastral", "%" + filters.get("regCatastral").toString().trim() + "%"));
        }
        if (filters.containsKey("idPredial")) {
            if (Utils.validateNumberPattern(filters.get("idPredial").toString())) {
                crit.add(Restrictions.eq("idPredial", filters.get("idPredial").toString().trim()));
            }
        }
        if (filters.containsKey("nombrePredio")) {
            crit.add(Restrictions.ilike("nombrePredio", "%" + filters.get("nombrePredio").toString().trim() + "%"));
        }
        if (filters.containsKey("sitio")) {
            crit.add(Restrictions.ilike("sitio", "%" + filters.get("sitio").toString().trim() + "%"));
        }
        if (filters.containsKey("numPredioRustico")) {
            if (Utils.validateNumberPattern(filters.get("numPredioRustico").toString())) {
                crit.add(Restrictions.eq("numPredioRustico", new BigInteger(filters.get("numPredioRustico").toString().trim())));
            }
        }
        if (filters.containsKey("propietario.ciRuc")) {
            crit.createCriteria("propietario").add(Restrictions.ilike("ciRuc", "%" + filters.get("propietario.ciRuc").toString().trim() + "%"));
        }
        if (filters.containsKey("propietario.nombres")) {
            crit.createCriteria("propietario").add(Restrictions.ilike("nombres", "%" + filters.get("propietario.nombres").toString().trim() + "%"));
        }
        if (filters.containsKey("propietario.apellidos")) {
            crit.createCriteria("propietario").add(Restrictions.ilike("apellidos", "%" + filters.get("propietario.apellidos").toString().trim() + "%"));
        }
        if (filters.containsKey("propietario.nombresCompletos")) {
            Criterion c1 = Restrictions.ilike("nombres", "%" + filters.get("propietario.nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
            Criterion c2 = Restrictions.ilike("apellidos", "%" + filters.get("propietario.nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
            Criterion c3 = Restrictions.ilike("razonSocial", "%" + filters.get("propietario.nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
            Criterion c4 = Restrictions.ilike("nombreComercial", "%" + filters.get("propietario.nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
            crit.add(Restrictions.or(c1, c2, c3, c4));
        }
        if (filters.containsKey("parroquia")) {
            if (Utils.validateNumberPattern(filters.get("parroquia").toString())) {
                crit.add(Restrictions.eq("parroquia", new BigInteger(filters.get("parroquia").toString().trim())));
            }
        }

        crit.add(Restrictions.eq("estado", true));
    }

    public Boolean getPropietariosExist() {
        return propietariosExist;
    }

    public void setPropietariosExist(Boolean propietariosExist) {
        this.propietariosExist = propietariosExist;
    }

}
