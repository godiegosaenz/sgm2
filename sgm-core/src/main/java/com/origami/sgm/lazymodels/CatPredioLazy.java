/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import util.Utils;

/**
 *
 * @author Angel Navarro, CarlosLoorVargas
 */
public class CatPredioLazy extends BaseLazyDataModel<CatPredio> {

    private String estado;
    private Boolean tieneAvaluoPropiedad = false;
    private BigInteger numPredio;
    private List<CatPredioPropietario> propietarios;
    private CatPredioModel model;
    private Boolean esPropiedadHorizontal = false;
    private Boolean esPropiedadHorizontalMatriz = false;
    private String tipoPredio = null;

    /*
        Lista para distinguir entre predis fentistas y generales uso exclusivos para 
        la seleccion de predios en las mejoras
     */
    private ArrayList<CatPredio> prediosMejoras;

    public CatPredioLazy() {
        super(CatPredio.class, "numPredio", "ASC");
    }
    
    public CatPredioLazy(String estado, Boolean esPhMatriz) {
        super(CatPredio.class, "numPredio", "ASC");
        this.estado = estado;
        this.esPropiedadHorizontalMatriz = esPhMatriz;
    }

//    public CatPredioLazy() {
//        super(CatPredio.class, "parroquia", "ASC", "zona", "ASC", "sector", "ASC", "mz", "ASC");
//    }
//
    public CatPredioLazy(String estado) {
        super(CatPredio.class);
        this.estado = estado;
    }

    public CatPredioLazy(Boolean tieneAvaluoPropiedad) {
        super(CatPredio.class);
        this.tieneAvaluoPropiedad = tieneAvaluoPropiedad;
    }

    public CatPredioLazy(BigInteger numPredio, String estado) {
        super(CatPredio.class);
        this.estado = estado;
        this.numPredio = numPredio;
    }

    public CatPredioLazy(List propietarios) {
        super(CatPredio.class);
        this.propietarios = propietarios;
    }

    public CatPredioLazy(ArrayList prediosMejoras) {
        super(CatPredio.class);
        this.prediosMejoras = prediosMejoras;
    }

    public CatPredioLazy(CatPredioModel model) {
        super(CatPredio.class, "numPredio", "DESC");
        this.model = model;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("numPredio")) {
            if (Utils.validateNumberPattern(filters.get("numPredio").toString().trim())) {
                crit.add(Restrictions.eq("numPredio", new BigInteger(filters.get("numPredio").toString().trim())));
            }
        }
        if (filters.containsKey("parroquia")) {
            if (Utils.validateNumberPattern(filters.get("parroquia").toString().trim())) {
                crit.add(Restrictions.eq("parroquia", new Short(filters.get("parroquia").toString().trim())));
            }
        }
        if (filters.containsKey("sector")) {
            if (Utils.validateNumberPattern(filters.get("sector").toString().trim())) {
                crit.add(Restrictions.eq("sector", new Short(filters.get("sector").toString().trim())));
            }
        }
        if (filters.containsKey("mz")) {
            if (Utils.validateNumberPattern(filters.get("mz").toString().trim())) {
                crit.add(Restrictions.eq("mz", new Short(filters.get("mz").toString().trim())));
            }
        }
        if (filters.containsKey("calle")) {
            crit.add(Restrictions.ilike("calle", "%" + filters.get("calle").toString().trim() + "%"));
        }
        if (filters.containsKey("cdla")) {
            if (Utils.validateNumberPattern(filters.get("cdla").toString().trim())) {
                crit.add(Restrictions.eq("cdla", new Short(filters.get("cdla").toString().trim())));
            }
        }
        if (filters.containsKey("mzdiv")) {
            if (Utils.validateNumberPattern(filters.get("mzdiv").toString().trim())) {
                crit.add(Restrictions.eq("mzdiv", new Short(filters.get("mzdiv").toString().trim())));
            }
        }
        if (filters.containsKey("solar")) {
            if (Utils.validateNumberPattern(filters.get("solar").toString().trim())) {
                crit.add(Restrictions.eq("solar", new Short(filters.get("solar").toString().trim())));
            }
        }
        if (filters.containsKey("div1")) {
            if (Utils.validateNumberPattern(filters.get("div1").toString().trim())) {
                crit.add(Restrictions.eq("div1", new Short(filters.get("div1").toString().trim())));
            }
        }
        if (filters.containsKey("nomCompPago")) {
            crit.add(Restrictions.ilike("nomCompPago", "%" + filters.get("nomCompPago").toString().trim() + "%"));
        }
        if (filters.containsKey("ciudadela.nombre")) {
            crit.createCriteria("ciudadela").add(Restrictions.ilike("nombre", "%" + filters.get("ciudadela.nombre").toString().trim() + "%"));
        }
        if (filters.containsKey("propiedad.nombre")) {
            crit.createCriteria("propiedad").add(Restrictions.ilike("nombre", "%" + filters.get("propiedad.nombre").toString().trim() + "%"));
        }
        if (filters.containsKey("urbMz")) {
            crit.add(Restrictions.ilike("urbMz", "%" + filters.get("urbMz").toString().trim() + "%"));
        }
        if (filters.containsKey("urbSolarnew")) {
            crit.add(Restrictions.ilike("urbSolarnew", "%" + filters.get("urbSolarnew").toString().trim() + "%"));
        }
        if (filters.containsKey("nombreEdificio")) {
            crit.add(Restrictions.ilike("nombreEdificio", "%" + filters.get("nombreEdificio").toString().trim() + "%"));
        }
        if (filters.containsKey("numDepartamento")) {
            crit.add(Restrictions.ilike("numDepartamento", "%" + filters.get("numDepartamento").toString().trim() + "%"));
        }
        if (filters.containsKey("divisionUrb")) {
            crit.add(Restrictions.ilike("divisionUrb", "%" + filters.get("divisionUrb").toString().trim() + "%"));
        }
        if (filters.containsKey("urbSecnew")) {
            crit.add(Restrictions.ilike("urbSecnew", "%" + filters.get("urbSecnew").toString().trim() + "%"));
        }
        if (filters.containsKey("estado")) {
            crit.add(Restrictions.eq("estado", filters.get("estado").toString().trim()));
        } //
        if (estado != null) {
            crit.add(Restrictions.eq("estado", estado));
            crit.add(Restrictions.ne("estado", "X"));
        } else {
            crit.add(Restrictions.ne("estado", "R"));
            crit.add(Restrictions.ne("estado", "X"));
        }
        if (tieneAvaluoPropiedad == true) {
            crit.add(Restrictions.isNotNull("avaluoMunicipal"));
            crit.add(Restrictions.ne("avaluoMunicipal", new BigDecimal("0.00")));
        }
        if (filters.containsKey("ciudadela.id")) {
            crit.createCriteria("ciudadela").add(Restrictions.eq("id", Long.parseLong(filters.get("ciudadela.id").toString().trim())));
        }
        if (numPredio != null) {
            crit.add(Restrictions.eq("numPredio", numPredio));
        }
        if (prediosMejoras != null) {
            if (!prediosMejoras.isEmpty()) {
                for (CatPredio p : prediosMejoras) {
                    if (p != null) {
                        if (p.getNumPredio() != null) {
                            crit.add(Restrictions.ne("numPredio", p.getNumPredio()));
                        }
                    }

                }
            }
        }
        if (propietarios != null && !propietarios.isEmpty()) {
            crit.createCriteria("catPredioPropietarioCollection").add(Restrictions.in("id", getIdPropietarios(propietarios)));
//            crit.add(Restrictions.in("catPredioPropietarioCollection", propietarios));
        }
        if (filters.containsKey("avaluoSolar")) {
            if (Utils.validateDecimalPattern(filters.get("avaluoSolar").toString().trim())) {
                crit.add(Restrictions.eq("avaluoSolar", new BigDecimal(filters.get("avaluoSolar").toString().trim())));
            }
        }
        if (filters.containsKey("avaluoConstruccion")) {
            if (Utils.validateDecimalPattern(filters.get("avaluoConstruccion").toString().trim())) {
                crit.add(Restrictions.eq("avaluoConstruccion", new BigDecimal(filters.get("avaluoConstruccion").toString().trim())));
            }
        }
        if (filters.containsKey("avaluoMunicipal")) {
            if (Utils.validateDecimalPattern(filters.get("avaluoMunicipal").toString().trim())) {
                crit.add(Restrictions.eq("avaluoMunicipal", new BigDecimal(filters.get("avaluoMunicipal").toString().trim())));
            }
        }
        if (filters.containsKey("catPredioS6.areaSolar")) {
            crit.createCriteria("catPredioS6").add(Restrictions.eq("areaSolar", Long.parseLong(filters.get("catPredioS6.areaSolar").toString().trim())));
        }
        if (filters.containsKey("regFicha.alicuotaEscritura")) {
            crit.createCriteria("regFicha").add(Restrictions.eq("alicuotaEscritura", Long.parseLong(filters.get("regFicha.alicuotaEscritura").toString().trim())));
        }
        if (filters.containsKey("regFicha.numFicha")) {
            crit.createCriteria("regFicha").add(Restrictions.eq("numFicha", Long.parseLong(filters.get("regFicha.numFicha").toString().trim())));
        }
        if (filters.containsKey("predialant")) {
            crit.add(Restrictions.ilike("predialant", "%" + filters.get("predialant").toString() + "%"));
        }
        ///////
        if (filters.containsKey("zona")) {
            crit.add(Restrictions.eq("zona", new Short(filters.get("zona").toString().trim())));
        }
        if (filters.containsKey("lote")) {
            crit.add(Restrictions.eq("lote", new Short(filters.get("lote").toString().trim())));
        }
        if (filters.containsKey("bloque")) {
            crit.add(Restrictions.eq("bloque", new Short(filters.get("bloque").toString().trim())));
        }
        if (filters.containsKey("piso")) {
            crit.add(Restrictions.eq("piso", new Short(filters.get("piso").toString().trim())));
        }
        if (filters.containsKey("unidad")) {
            crit.add(Restrictions.eq("unidad", new Short(filters.get("unidad").toString().trim())));
        }

        try {
            if (esPropiedadHorizontal) {
                crit.add(Restrictions.gt("bloque", new Short("0")));
            }
            if (esPropiedadHorizontalMatriz) {
                crit.add(Restrictions.eq("propiedadHorizontal", esPropiedadHorizontalMatriz));
                crit.add(Restrictions.eq("bloque", new Short("0")));
            }
            
            if (model != null) {
                if (model.getNumPredio() != null && model.getNumPredio().compareTo(BigInteger.ZERO) > 0) {
                    crit.add(Restrictions.eq("numPredio", model.getNumPredio()));
                }
                if (model.getCdla() > 0) {
                    crit.add(Restrictions.eq("cdla", model.getCdla()));
                }
                if (model.getMzDiv() > 0) {
                    crit.add(Restrictions.eq("mzdiv", model.getMzDiv()));
                }
                if (model.getMzUrb() != null) {
                    crit.add(Restrictions.eq("urbMz", model.getMzUrb()));
                }
                if (model.getSlUrb() != null) {
                    crit.add(Restrictions.eq("urbSolarnew", model.getSlUrb()));
                }
                if (model.getDivisionUrb() != null) {
                    crit.add(Restrictions.eq("divisionUrb", model.getDivisionUrb()));
                }
                if (model.getNumDepartamento() != null) {
                    crit.add(Restrictions.eq("numDepartamento", model.getNumDepartamento()));
                }
                if (model.getCiudadela() != null) {
                    crit.createCriteria("ciudadela").add(Restrictions.eq("id", model.getCiudadela().getId()));
                }
                if (model.getPredialAnterior() != null) {
                    crit.add(Restrictions.ilike("predialant", "%" + model.getPredialAnterior() + "%"));
                }
                if (model.getPredialAnt() != null) {
                    crit.add(Restrictions.ilike("predialantAnt", "%" + model.getPredialAnt() + "%"));
                }
                ///////

                if (model.getParroquiaShort() > 0) {
                    crit.add(Restrictions.eq("parroquia", model.getParroquiaShort()));
                }
                if (model.getZona() > 0) {
                    crit.add(Restrictions.eq("zona", model.getZona()));
                }
                if (model.getSector() > 0) {
                    crit.add(Restrictions.eq("sector", model.getSector()));
                }
                if (model.getMz() > 0) {
                    crit.add(Restrictions.eq("mz", model.getMz()));
                }
                if (model.getSolar() > 0) {
                    crit.add(Restrictions.eq("solar", model.getSolar()));
                }
                if (model.getPiso() > 0) {
                    crit.add(Restrictions.eq("piso", model.getPiso()));
                }
                if (model.getBloque() > 0) {
                    crit.add(Restrictions.eq("bloque", model.getBloque()));
                }
                if (model.getUnidad() > 0) {
                    crit.add(Restrictions.eq("unidad", model.getUnidad()));
                }
                if (model.getLote() > 0) {
                    crit.add(Restrictions.eq("lote", model.getLote()));
                }
                if (model.getCodigoPredial() != null && model.getCodigoPredial().trim().length() > 0) {
                    crit.add(Restrictions.ilike("claveCat", "%" + model.getCodigoPredial() + "%"));
                }
            }
            if (this.tipoPredio != null) {
                crit.add(Restrictions.ilike("tipoPredio", "%" + this.tipoPredio + "%"));
            }
        } catch (HibernateException e) {
            System.out.println("PrediosLazy --> " + e.getMessage());
        }
    }

    public BigInteger getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(BigInteger numPredio) {
        this.numPredio = numPredio;
    }

    public List<CatPredioPropietario> getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(List<CatPredioPropietario> propietarios) {
        this.propietarios = propietarios;
    }

    public CatPredioModel getModel() {
        return model;
    }

    public void setModel(CatPredioModel model) {
        this.model = model;
    }

    public Boolean getEsPropiedadHorizontal() {
        return esPropiedadHorizontal;
    }

    public void setEsPropiedadHorizontal(Boolean esPropiedadHorizontal) {
        this.esPropiedadHorizontal = esPropiedadHorizontal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipoPredio() {
        return tipoPredio;
    }

    public void setTipoPredio(String tipoPredio) {
        this.tipoPredio = tipoPredio;
    }

    private Collection getIdPropietarios(List<CatPredioPropietario> propietarios) {
        Collection c = new ArrayList<>();
        for (CatPredioPropietario propietario : propietarios) {
            c.add(propietario.getId());
        }
        return c;
    }

    public Boolean getTieneAvaluoPropiedad() {
        return tieneAvaluoPropiedad;
    }

    public void setTieneAvaluoPropiedad(Boolean tieneAvaluoPropiedad) {
        this.tieneAvaluoPropiedad = tieneAvaluoPropiedad;
    }

    public ArrayList<CatPredio> getPrediosMejoras() {
        return prediosMejoras;
    }

    public void setPrediosMejoras(ArrayList<CatPredio> prediosMejoras) {
        this.prediosMejoras = prediosMejoras;
    }

}
