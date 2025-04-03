package com.origami.sgm.bpm.managedbeans.edifications;

import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBase;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.EntityBeanCopy;
import util.GroovyUtil;
import util.JsfUti;
import util.Utils;

/**
 * Edita los valores de la tasa de liquidacón de los trámites de División de
 * Predio, Propiedad Horizontal, Otros Tramites, Resellado de Planos y Permisos
 * Adicionales.
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
@SuppressWarnings("serial")
public class verLiquidacionHTD implements Serializable {

    public static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(verLiquidacionHTD.class.getName());

    @javax.inject.Inject
    protected PropiedadHorizontalServices services;
    @Inject
    private ServletSession ss;
    @Inject
    private UserSession session;
    @Inject
    private BpmManageBeanBase base;

    protected Boolean isPropiedadHorizontal = true;
    protected Boolean impresion = false;
    protected Boolean guardar = true;
    protected Boolean impresoLiquidacion = false;
    protected Boolean dashBoard = false;
    protected Boolean areaEdi = true;
    protected Boolean avaluoConst = true;
    protected Boolean avaluoSolar = true;
    protected Boolean avaluoProp = true;
    protected String representanteLegal;
    protected String observacion;
    protected String realizadorPor;
    protected Long idHtd;

    protected MsgFormatoNotificacion ms;
    protected AclUser user;
    protected HistoricoTramites ht;
    protected GeTipoTramite tramite;
    protected CatPredio predio;
    protected CatEnte propietarioNuevo;
    protected HistoricoTramiteDet liquidacion;
    protected CatCanton canton;
    protected CatParroquia parroquia;
    protected List<CatPredioPropietario> listaPropietarios;
    protected List<CatPredioPropietario> listaPropietariosEliminar;
    protected List<CatCanton> catCantonsList;
    protected List<CatParroquia> catParroquiaList;
    protected CatEnteLazy enteLazy;

    /*
     Estas dos variables son para obtener la formulas de calculo.
     */
    protected GroovyUtil groovyUtil;
    protected MatFormulaTramite formulas;

    @PostConstruct
    public void initView() {
        if (ss != null && ss.getParametros() != null) {
            if (ss.getParametros().get("tramite") == null) {
                JsfUti.messageError(null, "", "No se ha encontrado información de Liquidación.");
                ss.borrarParametros();
                base.continuar();
            }
            idHtd = (Long) ss.getParametros().get("tramite");
            try {
                iniciarVariables();
                llenarVariables();
                if (liquidacion != null) {
                    predio = liquidacion.getPredio();
                    if (predio != null) {
                        listaPropietarios = llenarPropietarios(predio.getCatPredioPropietarioCollection());
                        llenarParroquia();
                    }
                }
                llenarRealizado();
                formulas = services.getPermiso().getMatFormulaTramite(tramite.getId());
                groovyUtil = new GroovyUtil(formulas.getFormula());
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "", e);
            }
        } else {
            base.continuar();
        }
    }

    /**
     * Inicializa las variables.
     */
    private void iniciarVariables() {
        catCantonsList = new ArrayList<>();
        catParroquiaList = new ArrayList<>();
        listaPropietarios = new ArrayList<>();
        listaPropietariosEliminar = new ArrayList<>();
        predio = new CatPredio();
        ht = new HistoricoTramites();
        liquidacion = new HistoricoTramiteDet();
        tramite = new GeTipoTramite();
        canton = new CatCanton();
        parroquia = new CatParroquia();
        user = new AclUser();
    }

    /**
     * Llena los lista y consulta los datos de la tabla
     * {@link HistoricoTramiteDet}
     */
    private void llenarVariables() {
        canton = services.getCatCantonById(1L);
        catCantonsList = services.getInscripcion().getCatCantonList();
        catParroquiaList = services.getFichaServices().getCatPerroquiasListByCanton(canton.getId());
        getListPeFirma();
        /*if (session != null) {
            user = 
        (AclUser) EntityBeanCopy.clone(services.getPermiso().getAclUserByUser(session.getName_user()));
        }*/
        
        liquidacion = services.getHistoricoTramiteDetById(idHtd);
        realizadorPor = liquidacion.getResponsable();
        ht = services.getPermiso().getHistoricoTramiteById(liquidacion.getTramite().getId());
        tramite = services.getPermiso().getGeTipoTramiteById(ht.getTipoTramite().getId());
    }

    /**
     * Llena al responsable de edición
     */
    private void llenarRealizado() {
        if (user.getEnte() != null) {
            realizadorPor = ((user.getEnte().getApellidos() == null) ? "" : user.getEnte().getApellidos())
                    + " " + ((user.getEnte().getNombres() == null) ? "" : user.getEnte().getNombres());
        } else {
            realizadorPor = user.getUsuario();
        }
    }

    /**
     * Carga la lista de propietarios activos del predio.
     *
     * @param propietarioCollection lista de Propietarios.
     * @return Lista de Propietarios Activos.
     */
    private List<CatPredioPropietario> llenarPropietarios(Collection<CatPredioPropietario> propietarioCollection) {
        List<CatPredioPropietario> propietariosTemp = new ArrayList<>();
        if (Utils.isNotEmpty((List<?>) propietarioCollection)) {
            for (CatPredioPropietario temp : propietarioCollection) {
                if ("A".equals(temp.getEstado())) {
                    propietariosTemp.add(temp);
                }
            }
        }
        return propietariosTemp;
    }

    private void llenarParroquia() {
        if (predio.getCiudadela() != null) {
            parroquia = (CatParroquia) EntityBeanCopy.clone(predio.getCiudadela().getCodParroquia());
        } else {
            parroquia = (CatParroquia) EntityBeanCopy.clone(services.getCatParroquia(1L));
        }
        if (parroquia != null) {
            canton = (CatCanton) EntityBeanCopy.clone(predio.getCiudadela().getCodParroquia().getIdCanton());
        } else {
            parroquia = (CatParroquia) EntityBeanCopy.clone(Utils.get(canton.getCatParroquiaCollection(), canton.getCatParroquiaCollection().size() - 1));
        }
    }

    public List<PeFirma> getListPeFirma() {
        return services.getListPeFirma();
    }

    public void retornar() {
        switch (tramite.getId().intValue()) {
            case 6:
                JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/permisosAdicionales/permisosAdicionalesConsulta.xhtml");
                break;
            case 7:
                JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/divisionPredio/divisionPredioConsulta.xhtml");
                break;
            case 9:
                JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/propiedadHorizontal/propiedadHorizontalConsulta.xhtml");
                break;
            case 14:
                JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/otrosTramites/otrosTramitesConsulta.xhtml");
                break;
            case 16:
                JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/reselladoPlanos/reselladoPlanosConsulta.xhtml");
                break;
            default:
                base.continuar();
                break;
        }
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public List<CatPredioPropietario> getListaPropietarios() {
        return listaPropietarios;
    }

    public void setListaPropietarios(List<CatPredioPropietario> listaPropietarios) {
        this.listaPropietarios = listaPropietarios;
    }

    public Boolean getIsPropiedadHorizontal() {
        return isPropiedadHorizontal;
    }

    public void setIsPropiedadHorizontal(Boolean isPropiedadHorizontal) {
        this.isPropiedadHorizontal = isPropiedadHorizontal;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public GeTipoTramite getTramite() {
        return tramite;
    }

    public void setTramite(GeTipoTramite tramite) {
        this.tramite = tramite;
    }

    public CatEnte getPropietarioNuevo() {
        return propietarioNuevo;
    }

    public void setPropietarioNuevo(CatEnte propietarioNuevo) {
        this.propietarioNuevo = propietarioNuevo;
    }

    public String getRepresentanteLegal() {
        return representanteLegal;
    }

    public void setRepresentanteLegal(String representanteLegal) {
        this.representanteLegal = representanteLegal;
    }

    public CatEnteLazy getEnteLazy() {
        return enteLazy;
    }

    public void setEnteLazy(CatEnteLazy enteLazy) {
        this.enteLazy = enteLazy;
    }

    public verLiquidacionHTD() {
    }

    public CatCanton getCanton() {
        return canton;
    }

    public void setCanton(CatCanton canton) {
        this.canton = canton;
    }

    public List<CatCanton> getCatCantonsList() {
        return catCantonsList;
    }

    public void setCatCantonsList(List<CatCanton> catCantonsList) {
        this.catCantonsList = catCantonsList;
    }

    public List<CatParroquia> getCatParroquiaList() {
        return catParroquiaList;
    }

    public void setCatParroquiaList(List<CatParroquia> catParroquiaList) {
        this.catParroquiaList = catParroquiaList;
    }

    public CatParroquia getParroquia() {
        return parroquia;
    }

    public void setParroquia(CatParroquia parroquia) {
        this.parroquia = parroquia;
    }

    public Boolean getDashBoard() {
        return dashBoard;
    }

    public void setDashBoard(Boolean dashBoard) {
        this.dashBoard = dashBoard;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public Boolean getImpresion() {
        return impresion;
    }

    public void setImpresion(Boolean impresion) {
        this.impresion = impresion;
    }

    public Boolean getGuardar() {
        return guardar;
    }

    public void setGuardar(Boolean guardar) {
        this.guardar = guardar;
    }

    public HistoricoTramiteDet getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(HistoricoTramiteDet liquidacion) {
        this.liquidacion = liquidacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getRealizadorPor() {
        return realizadorPor;
    }

    public void setRealizadorPor(String realizadorPor) {
        this.realizadorPor = realizadorPor;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public BpmManageBeanBase getBase() {
        return base;
    }

    public void setBase(BpmManageBeanBase base) {
        this.base = base;
    }

    public Boolean getAreaEdi() {
        return areaEdi;
    }

    public void setAreaEdi(Boolean areaEdi) {
        this.areaEdi = areaEdi;
    }

    public Boolean getAvaluoConst() {
        return avaluoConst;
    }

    public void setAvaluoConst(Boolean avaluoConst) {
        this.avaluoConst = avaluoConst;
    }

    public Boolean getAvaluoSolar() {
        return avaluoSolar;
    }

    public void setAvaluoSolar(Boolean avaluoSolar) {
        this.avaluoSolar = avaluoSolar;
    }

    public Boolean getAvaluoProp() {
        return avaluoProp;
    }

    public void setAvaluoProp(Boolean avaluoProp) {
        this.avaluoProp = avaluoProp;
    }

}
