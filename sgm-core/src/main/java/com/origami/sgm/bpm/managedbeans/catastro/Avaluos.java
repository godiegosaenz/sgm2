/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;


import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatPredioAvalHistorico;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.Faces;
import util.JsfUti;

/**
 *
 * @author root
 */
@Named
@ViewScoped
public class Avaluos extends PredioUtil implements Serializable {

    private static final long serialVersionUID = 8799656478674716638L;
    @Inject
    protected ServletSession ss;
    protected List<CatPredioAvalHistorico> avaluosHistoricosPredio;
    private BigDecimal valorMetro2;

    public void load() {

        this.cargarDatos();

    }

    protected void cargarDatos() {
        try {
            if (sess != null) {
                Long numeroPredio = null;
                Long id = null;
                if (ss.getParametros() == null) {
                    JsfUti.redirectFaces2(SisVars.urlbase);
                }
                if ((ss.getParametros().get("numPredio") != null || ss.getParametros().get("idPredio") != null)) {
                    if (ss.getParametros().get("numPredio") != null) {
                        numeroPredio = Long.parseLong(ss.getParametros().get("numPredio").toString());
                    } else {
                        id = Long.parseLong(ss.getParametros().get("idPredio").toString());
                    }
                }
                if (numeroPredio != null) {
                    predio = catas.getPredioNumPredio(numeroPredio);
                } else {
                    if (id == null) {
                        JsfUti.redirectFaces("/vistaprocesos/catastro/prediosSV.xhtml");
                    } else {
                        predio = catas.getPredioId(id);
                    }
                }
            }
            if (predio != null) {
                getAvalHistorico();
            } else {
                JsfUti.redirectFaces("/vistaprocesos/catastro/predios.xhtml");
            }
        } catch (NumberFormatException e) {
            log.log(Level.SEVERE, null, e);
        }
    }

    public void getAvalHistorico() {
        CatPredioAvalHistorico valorSuelo = null;
        avaluosHistoricosPredio = em.findAll(Querys.getAvaluosHistoricosPorPredios, new String[]{"predio"}, new Object[]{predio.getId()});
        if (!avaluosHistoricosPredio.isEmpty()) {
            if (avaluosHistoricosPredio.size() >= 1) {
                valorSuelo = avaluosHistoricosPredio.get(avaluosHistoricosPredio.size() - 1);
                if (valorSuelo != null) {
                    if (valorSuelo.getValorBaseM2() != null) {
                        valorMetro2 = valorSuelo.getValorBaseM2();
                    }
                }
            }

        }

    }
    
    public void reporteDetalleCalculos(CatPredioAvalHistorico avalHistorico) {
        try {
            if (predio != null) {
                ss.borrarDatos();
                ss.instanciarParametros();
                String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
                ss.setTieneDatasource(Boolean.TRUE);
                ss.setNombreSubCarpeta("catastro/avaluos");
                if (dataBaseConnect()) {
                    ss.setNombreReporte("detalleCalculoEmision");
                } else {
                    ss.setNombreReporte("detalleCalculoEmisionOracle");
                    ss.agregarParametro("ANIO_INICIO", avalHistorico.getAnioInicio());
                    ss.agregarParametro("ANIO_FIN", avalHistorico.getAnioFin());
                }

                ss.agregarParametro("LOGO_FOOTER", path + SisVars.sisLogo1);
                ss.agregarParametro("LOGO", path + SisVars.logoReportes);
                ss.agregarParametro("LOGO2", path + SisVars.sisLogo1);
                ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/"));
                ss.agregarParametro("ID_AVAL_HISTORICO", avalHistorico.getId());
                ss.agregarParametro("USUARIO", sess.getName_user());
                JsfUti.redirectNewTab("/sgmEE/Documento");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
    }

    public List<CatPredioAvalHistorico> getAvaluosHistoricosPredio() {
        return avaluosHistoricosPredio;
    }

    public void setAvaluosHistoricosPredio(List<CatPredioAvalHistorico> avaluosHistoricosPredio) {
        this.avaluosHistoricosPredio = avaluosHistoricosPredio;
    }

    public BigDecimal getValorMetro2() {
        return valorMetro2;
    }

    public void setValorMetro2(BigDecimal valorMetro2) {
        this.valorMetro2 = valorMetro2;
    }

}
