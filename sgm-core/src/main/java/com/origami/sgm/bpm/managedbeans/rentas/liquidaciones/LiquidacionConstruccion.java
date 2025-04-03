package com.origami.sgm.bpm.managedbeans.rentas.liquidaciones;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.managedbeans.component.Busquedas;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import util.Faces;
import util.JsfUti;
import util.MessagesRentas;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class LiquidacionConstruccion extends Busquedas implements Serializable {

    private static final Logger LOG = Logger.getLogger(LiquidacionConstruccion.class.getName());

    @javax.inject.Inject
    private RentasServices services;

    @javax.inject.Inject
    private PermisoConstruccionServices permisoServices;

    @Inject
    private UserSession session;
    @Inject
    private ServletSession ss;

    private Integer tipo = 1;
    private Integer tipoCons = 2;
    private Boolean esMatriz = false;
    private Boolean seccion1 = false;
    private Boolean seccion2 = false;
    private Boolean exonerar = false;
    private Boolean alcabala = false;

    private CatPredio predio;
    private CatPredioPropietario vendedor;
    private RenLiquidacion liquidacion;
    private RenTipoLiquidacion tipoLiquidacion;
    private List<RenTipoLiquidacion> tiposLiquidacions;
    private List<RenRubrosLiquidacion> rubrosLiquidacion;

    @PostConstruct
    public void initView() {
        iniciarDatos();
    }

    public void iniciarDatos() {
        try {
            tiposLiquidacions = services.gettiposLiquidacionByCodTitRep(3);
            predio = new CatPredio();
            initLiquidacion();
            consultarRubros();
            vendedor = new CatPredioPropietario();
        } catch (Exception e) {
            LOG.log(Level.OFF, "Iniciar vista", e);
        }
    }

    public void consultarRubros() {
        rubrosLiquidacion = new ArrayList<>();
        if (tipoLiquidacion != null) {
            rubrosLiquidacion = services.getRubrosPorLiquidacion(tipoLiquidacion.getId());
            initLiquidacion();
        }
    }

    public void initLiquidacion() {
        liquidacion = new RenLiquidacion();
        liquidacion.setTotalPago(new BigDecimal(0));
        liquidacion.setCuantia(BigDecimal.ZERO);
    }

    @Override
    public void seleccionarPredios(SelectEvent event) {
        List<CatPredio> predios = (List<CatPredio>) event.getObject();
        if (predios != null) {
            predio = predios.get(0);
        }
        setEsPersonaComp(getComprador().getEsPersona());
    }

    public void consultar() {
        if (tipoLiquidacion == null) {
            JsfUti.messageError(null, "Advertencia", "Debe seleccionar el tipo de liquidación a realizar");
            return;
        }
        try {
            CatPredio temp = consultar(tipoCons, predio);

            if (temp != null) {
                predio = temp;
                esMatriz = predio.getPhh() != 0 && predio.getPhv() != 0;
                if (predio.getCatPredioPropietarioCollection() != null && predio.getCatPredioPropietarioCollection().size() == 1) {
                    vendedor = Utils.get(predio.getCatPredioPropietarioCollection(), 0);
                }
                seccion1 = true;
            } else {
                seccion1 = false;
                JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.predioNoEncontrado);
            }
            JsfUti.update("frmLiquidaciones");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
        }
    }

    public String getNombreVendedor(CatEnte ente) {
        if (ente != null) {
            if (ente.getEsPersona()) {
                return Utils.isEmpty(ente.getApellidos()) + " " + Utils.isEmpty(ente.getNombres());
            } else {
                return Utils.isEmpty(ente.getRazonSocial());
            }
        }
        return "";
    }

    public void valorTotal() {
        try {
            BigDecimal total = BigDecimal.ZERO;
            for (RenRubrosLiquidacion rb : rubrosLiquidacion) {
                if (rb.getCobrar()) {
                    if (rb.getValorTotal() != null) {
                        total = total.add(rb.getValorTotal());
                    }
                }
            }
            liquidacion.setTotalPago(total.setScale(2, RoundingMode.HALF_UP));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "valorTotal", e);
        }
    }

    public void procesar() {
        if (this.getComprador() == null) {
            JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.faltaIngresar.concat("nombre del comprador"));
            return;
        }
        if (getComprador().getId() == null) {
            JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.faltaIngresar.concat("nombre del comprador"));
            return;
        }
        if (liquidacion.getTotalPago().doubleValue() <= 0) {
            JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.total);
            return;
        }

        try {
            liquidacion.setComprador(getComprador());
            liquidacion.setFechaIngreso(new Date());
            liquidacion.setUsuarioIngreso(session.getName_user());
            liquidacion.setEstadoLiquidacion(services.getEstadoLiquidacionByDesc(2L));
            liquidacion.setTipoLiquidacion(tipoLiquidacion);
            if (predio.getNumPredio() != null) {
                liquidacion.setPredio(predio);
            }
            liquidacion.setCoactiva(false);
            liquidacion = services.guardarLiquidacion(liquidacion, rubrosLiquidacion, tipoLiquidacion.getPrefijo(), null);
            if (liquidacion != null) {
                if (services.generarNumLiquidacion(liquidacion, tipoLiquidacion.getPrefijo())) {
                    JsfUti.executeJS("PF('dlgIdLiquidacion').show()");
                    JsfUti.executeJS("PF('obs').hide()");
                    JsfUti.update("numLiquidacion:dlgDilLiq");
                } else {
                    JsfUti.messageError(null, "Error", "Ocurrio un error al intentar guardar.");
                }
            } else {
                JsfUti.messageError(null, "Error", "Ocurrio un error al intentar guardar.");
                JsfUti.executeJS("PF('obs').hide()");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void borrarDatos() {
        JsfUti.redirectFaces("/faces/rentas/liquidaciones/liquidacionConstruccion.xhtml");
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public Integer getTipoCons() {
        return tipoCons;
    }

    public void setTipoCons(Integer tipoCons) {
        this.tipoCons = tipoCons;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public RenLiquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RenLiquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    public List<RenRubrosLiquidacion> getRubrosLiquidacion() {
        return rubrosLiquidacion;
    }

    public void setRubrosLiquidacion(List<RenRubrosLiquidacion> rubrosLiquidacion) {
        this.rubrosLiquidacion = rubrosLiquidacion;
    }

    public CatPredioPropietario getVendedor() {
        return vendedor;
    }

    public void setVendedor(CatPredioPropietario vendedor) {
        this.vendedor = vendedor;
    }

    public void setServices(RentasServices services) {
        this.services = services;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public RenTipoLiquidacion getTipoLiquidacion() {
        return tipoLiquidacion;
    }

    public void setTipoLiquidacion(RenTipoLiquidacion tipoLiquidacion) {
        this.tipoLiquidacion = tipoLiquidacion;
    }

    public List<RenTipoLiquidacion> getTiposLiquidacions() {
        return tiposLiquidacions;
    }

    public void setTiposLiquidacions(List<RenTipoLiquidacion> tiposLiquidacions) {
        this.tiposLiquidacions = tiposLiquidacions;
    }

    public Boolean getEsMatriz() {
        return esMatriz;
    }

    public void setEsMatriz(Boolean esMatriz) {
        this.esMatriz = esMatriz;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public Boolean getSeccion1() {
        return seccion1;
    }

    public void setSeccion1(Boolean seccion1) {
        this.seccion1 = seccion1;
    }

    public Boolean getSeccion2() {
        return seccion2;
    }

    public void setSeccion2(Boolean seccion2) {
        this.seccion2 = seccion2;
    }

    public Boolean getExonerar() {
        return exonerar;
    }

    public void setExonerar(Boolean exonerar) {
        this.exonerar = exonerar;
    }

    public Boolean getAlcabala() {
        return alcabala;
    }

    public void setAlcabala(Boolean alcabala) {
        this.alcabala = alcabala;
    }

}
