package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import java.math.BigInteger;
import java.util.Map;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.primefaces.component.datatable.DataTable;

/**
 *
 * @author Joao
 * Sanga
 */
public class RenLiquidacionesTitulosPredialesLazy extends BaseLazyDataModel<RenLiquidacion> {

    private Criteria pred;
    private Boolean estado;
    private String compName;
    private UIViewRoot root;
    private DataTable dt;
    private RenTipoLiquidacion tipoLiquidacion;
    private Criteria comprador;
    private Criteria predio;
    private Long numPredio;
    private Criteria tramite;

    public RenLiquidacionesTitulosPredialesLazy() {
        super(RenLiquidacion.class, "anio", "DESC");
    }

    public RenLiquidacionesTitulosPredialesLazy(RenTipoLiquidacion tipoLiquidacion) {
        super(RenLiquidacion.class, "numLiquidacion");
        this.tipoLiquidacion = tipoLiquidacion;
    }

    public RenLiquidacionesTitulosPredialesLazy(String compName, RenTipoLiquidacion tipoLiquidacion) {
        super(RenLiquidacion.class, "numLiquidacion");
        this.tipoLiquidacion = tipoLiquidacion;
        this.compName = compName;
    }

    public RenLiquidacionesTitulosPredialesLazy(RenTipoLiquidacion tipoLiquidacion, Long numPredio) {
        super(RenLiquidacion.class, "anio");
        this.tipoLiquidacion = tipoLiquidacion;
        this.numPredio = numPredio;
    }

    public RenLiquidacionesTitulosPredialesLazy(String compName) {
        super(RenLiquidacion.class, "fechaIngreso", "DESC");
        this.compName = compName;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (compName != null) {
            pred = crit.createCriteria("tipoLiquidacion");
            root = FacesContext.getCurrentInstance().getViewRoot();
            dt = (DataTable) root.findComponent(compName);
            if (dt.getSortColumn() != null) {
                if (dt.getSortColumn().getColumnKey() != null && dt.getSortColumn().getColumnKey().contains("tipoLiquidacion")) {
                    this.setOrderCrit(pred);
                    this.setOrderField("prefijo");
                }

                if (dt.getSortColumn().getColumnKey() != null && dt.getSortColumn().getColumnKey().contains("identificacion")) {
                    this.setOrderCrit(comprador);
                    this.setOrderField("ciRuc");
                }

                if (dt.getSortColumn().getColumnKey() != null && (/*dt.getSortColumn().getColumnKey().contains("mz")||dt.getSortColumn().getColumnKey().contains("sl")||*/dt.getSortColumn().getColumnKey().contains("numPredio"))) {
                    this.setOrderCrit(predio);
                    this.setOrderField("numPredio");
                    //this.setOrderField("urbMz");
                    //this.setOrderField("urbSolarnew");
                }
                if (dt.getSortColumn().getColumnKey() != null && (dt.getSortColumn().getColumnKey().contains("tramite") || dt.getSortColumn().getColumnKey().contains("id"))) {
                    this.setOrderCrit(tramite);
                    this.setOrderField("id");
                }
            }
        }
        if (filters.containsKey("numLiquidacion")) {
            crit.add(Restrictions.eq("numLiquidacion", new BigInteger(filters.get("numLiquidacion").toString().trim())));
        }
        if (filters.containsKey("anio")) {
            crit.add(Restrictions.eq("anio", new Integer(filters.get("anio").toString().trim())));
        }
        if (filters.containsKey("numComprobante")) {
            crit.add(Restrictions.eq("numComprobante", new BigInteger(filters.get("numComprobante").toString().trim())));
        }
        if (filters.containsKey("tramite.id")) {
            tramite = crit.createCriteria("tramite", JoinType.LEFT_OUTER_JOIN);
            tramite.add(Restrictions.eq("id", new Long(filters.get("tramite.id").toString().trim())));
        }
        if (filters.containsKey("comprador.ciRuc")) {
            comprador = crit.createCriteria("comprador", JoinType.LEFT_OUTER_JOIN);
            comprador.add(Restrictions.ilike("ciRuc", "%" + filters.get("comprador.ciRuc").toString().trim() + "%"));
        }
        if (filters.containsKey("tipoLiquidacion.prefijo")) {
            pred.add(Restrictions.ilike("prefijo", "%" + filters.get("tipoLiquidacion.prefijo").toString().trim() + "%"));
        }
        if (filters.containsKey("predio.numPredio")) {
            //if(predio==null)
            predio = crit.createCriteria("predio", JoinType.LEFT_OUTER_JOIN);
            predio.add(Restrictions.eq("numPredio", new BigInteger(filters.get("predio.numPredio").toString().trim())));
        }
        if (filters.containsKey("predio.urbMz")) {
            if (predio == null) {
                predio = crit.createCriteria("predio", JoinType.LEFT_OUTER_JOIN);
            }
            predio.add(Restrictions.ilike("urbMz", "%" + filters.get("predio.urbMz").toString().trim() + "%"));
        }
        if (filters.containsKey("predio.urbSolarnew")) {
            if (predio == null) {
                predio = crit.createCriteria("predio", JoinType.LEFT_OUTER_JOIN);
            }
            predio.add(Restrictions.ilike("urbSolarnew", "%" + filters.get("predio.urbSolarnew").toString().trim() + "%"));
        }

        crit.add(Restrictions.eq("tipoLiquidacion", new RenTipoLiquidacion(13L)));

        crit.add(Restrictions.ne("estadoLiquidacion", new RenEstadoLiquidacion(3L)));

    }

}
