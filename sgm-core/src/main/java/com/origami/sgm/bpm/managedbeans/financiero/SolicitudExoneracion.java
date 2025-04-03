package com.origami.sgm.bpm.managedbeans.financiero;

import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.FnEstadoExoneracion;
import com.origami.sgm.entities.FnExoneracionClase;
import com.origami.sgm.entities.FnExoneracionTipo;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenTipoValor;
import com.origami.sgm.managedbeans.component.Busquedas;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.activiti.engine.runtime.ProcessInstance;
import org.primefaces.event.SelectEvent;
import util.EntityBeanCopy;
import util.JsfUti;
import util.MessagesRentas;
import util.Utils;

/**
 *
 * @author Angel Navarro
 * @Date 19/05/2016
 */
@Named
@ViewScoped
public class SolicitudExoneracion extends Busquedas implements Serializable {

    public static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(SolicitudExoneracion.class.getName());

    @javax.inject.Inject
    private Entitymanager services;

    @Inject
    private UserSession uSession;
    @javax.inject.Inject
    private CatastroServices catastroServices;

    private FnExoneracionClase exoneracion;
    private FnExoneracionTipo exoneracionTipo;
    private List<CatPredio> predios;
    private List<CatPredioRustico> prediosRusticos;

    private Map<String, Object> entradas;
    private Integer tipoPredio = 1;
    private Boolean mostrar = false;
    private Boolean esCatastro = false;
    private Boolean usuarioAutorizado;

    protected GeDepartamento departamento;
    protected List<AclUser> directores;
    ///se lo utiliza para la asignacion de una exoneracion al jefe de rentas
    private String jefeRentas;

    @PostConstruct
    public void initView() {

        if (uSession.esLogueado()) {
            if (uSession.getName_user() != null) {
                if (uSession.getRoles().contains(99L) || uSession.getDepts().contains(2L) || uSession.getDepts().contains(12L)) {
                    if (uSession.getRoles().contains(99L) || uSession.getDepts().contains(12L)) {
                        esCatastro = false;
                    } else {
                        esCatastro = true;
                    }
                    jefeRentas = "";
                    departamento = services.find(GeDepartamento.class, 12L);
                    if (departamento != null) {
                        directores = new ArrayList<>();
                        directores.addAll(catastroServices.getUser(departamento));
                        for (AclRol a : departamento.getAclRolCollection()) {
                            if (a.getIsDirector() && a.getEstado()) {
                                directores.addAll(a.getAclUserCollection());
                            }
                        }
                    }
                    if (!directores.isEmpty()) {
                        AclUser user = directores.get(directores.size() - 1);
                        if (user != null) {
                            jefeRentas = user.getUsuario();
                        }
                    }
                    usuarioAutorizado = false;
                    JsfUti.messageInfo(null, "Info", "Usuario no autorizado");
                    usuarioAutorizado = true;
                    borrarDatos();
                    JsfUti.update("frmSolExo");
                }
            } else {
                usuarioAutorizado = false;
            }
        }
    }

    public void consultarSolicitante() {
        try {
            String cedulaRuc = ((CatEnte) entradas.get("ente")).getCiRuc();
            if (cedulaRuc != null && cedulaRuc.trim().length() > 0) {
                setComprador(getServices().consultarEnte(cedulaRuc, (cedulaRuc.trim().length() == 13), session.getName_user()));
                setEsPersonaComp(getComprador().getEsPersona());
                switch (tipoPredio) {
                    case 1:// Urbano
                        predios = new ArrayList<>();
//                      List<CatPredio> p = new ArrayList<>();
                        List<CatPredio> p = getServices().getPredios(getComprador());
                        entradas.put("prediosEncontrados", p);
                        predios.addAll(p);
                        break;
                    case 2: // Rustico
                        prediosRusticos = new ArrayList<>();
                        List<CatPredioRustico> pr = new ArrayList<>();
                        for (CatPredioRustico rust : getComprador().getCatPredioRusticos()) {
                            if (rust.getEstado()) {
                                pr.add((CatPredioRustico) EntityBeanCopy.clone(rust));
                            }
                        }
                        entradas.put("prediosEncontrados", pr);
                        prediosRusticos.addAll(pr);
                        break;
                }

                mostrar = true;

            }
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }

    public void actualizarFrm() {
        mostrar = getComprador() != null;
    }

    public void mostrarPredios() {
        try {
            if (tipoPredio == 1) {
                this.mostrarDialog("/resources/dialog/predios");
            } else {
                this.mostrarDialog("/resources/dialog/prediosRustico");
            }
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }

    public void seleccionarObject(SelectEvent event) {
        try {
            if (tipoPredio == 1) {
                entradas.put("prediosEncontrados", (List<CatPredio>) event.getObject());
                predios = (List<CatPredio>) event.getObject();
            } else {
                entradas.put("prediosEncontrados", (List<CatPredio>) event.getObject());
                prediosRusticos = (List<CatPredioRustico>) event.getObject();
            }
            mostrar = true;
            JsfUti.update("frmSolExo:panel");
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }

    public void seleccionarObjectAdd(SelectEvent event) {
        try {
            List<CatPredio> pds = new ArrayList<>();
            pds = Utils.verificarRepetidos(((List<CatPredio>) entradas.get("prediosEncontrados")), pds, ((List<CatPredio>) event.getObject()), 0);
            pds.addAll((List<CatPredio>) event.getObject());
            entradas.put("prediosEncontrados", pds);
            predios.addAll((List<CatPredio>) event.getObject());
            JsfUti.update("@all");
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }

    public void eliminarPredio(CatPredio predio) {
        try {
            List<CatPredio> pds = (List<CatPredio>) entradas.get("prediosEncontrados");
            pds.remove(predio);
            entradas.put("prediosEncontrados", pds);
            predios.remove(predio);
            JsfUti.update("@all");
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }

    public void seleccionarObjectAddRustico(SelectEvent event) {
        try {
            List<CatPredioRustico> pds = new ArrayList<>();
            pds = Utils.verificarRepetidos(((List<CatPredioRustico>) entradas.get("prediosEncontrados")), pds, ((List<CatPredioRustico>) event.getObject()), 0);
            entradas.put("prediosEncontrados", pds);
            prediosRusticos.addAll(pds);
            JsfUti.update("frmSolExo:panel");
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }

    public void eliminarPredioRustico(CatPredioRustico predio) {
        try {
            List<CatPredioRustico> pds = (List<CatPredioRustico>) entradas.get("prediosEncontrados");
            pds.remove(predio);
            entradas.put("prediosEncontrados", pds);
            prediosRusticos.remove(predio);
            JsfUti.update("@all");
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }

    public void actualizarTipoPredio() {
        if (getComprador() != null) {
            getExoneraciones();
            actualizarTipo();
            setEsPersonaComp(getComprador().getEsPersona());
            mostrar = true;
        } else {
            mostrar = false;
        }
    }

    public List<FnExoneracionClase> getExoneraciones() {
        List<FnExoneracionClase> l = getServices().FnExoneraciones(true, tipoPredio);
        return l;
    }

    public void actualizarTipo() {
        getTiposExonracion();
    }

    public List<FnExoneracionTipo> getTiposExonracion() {
        if (exoneracion != null) {
            return getServices().FnExoneracionesTipo(Boolean.TRUE, exoneracion.getId());
        }
        return null;
    }

    public List<RenTipoValor> getRenTipoValores() {
        Set<Long> ids = new HashSet<>();
        ids.add(2L);
        ids.add(4L);
        return getServices().tipoValorList(ids);
    }

    public void escuchar() {
        if ((((FnSolicitudExoneracion) entradas.get("solicitud")).getTipoValor().getId() == 2)) {
            entradas.put("valorPorcentaje", true);
        } else if ((((FnSolicitudExoneracion) entradas.get("solicitud")).getTipoValor().getId() == 4)) {
            entradas.put("valorPorcentaje", false);
        }
    }

    public void mostrarObs() {
        if (exoneracion == null) {
            JsfUti.messageError(null, MessagesRentas.advert, "Debe seleccionar la clase de exoneración.");
            return;
        }
        if (exoneracionTipo == null) {
            JsfUti.messageError(null, MessagesRentas.advert, "Debe seleccionar el tipo de exoneracón.");
            return;
        }
        if ((Integer.valueOf(entradas.get("tipo").toString())) == 1) {
            if (getComprador() == null) {
                JsfUti.messageError(null, MessagesRentas.advert, "Debe seleccionar al solicitante.");
                return;
            }
            if (getComprador().getEstado().equalsIgnoreCase("F")) {
                JsfUti.messageError(null, MessagesRentas.advert, "Solicitante tiene condicion de fallecido, debe Cambiar de propietario el predio.");
                return;
            }
            if (exoneracionTipo.getAplica() != null && exoneracionTipo.getAplica().equalsIgnoreCase("T")) {
                if (getComprador().getEdad() != null && Utils.validateNumberPattern(getComprador().getEdad())) {
                    Integer edad = Integer.valueOf(getComprador().getEdad());
                    if (edad < 65) {
                        JsfUti.messageError(null, MessagesRentas.advert, "Solicitante debe cumplir con la edad estipulada por la ley.");
                        return;
                    }
                }
            }
        }

        if (((FnSolicitudExoneracion) entradas.get("solicitud")).getAnioFin() < ((FnSolicitudExoneracion) entradas.get("solicitud")).getAnioInicio()) {
            JsfUti.messageError(null, MessagesRentas.advert, "Error al ingresar los años de inicio y fin.");
            return;
        }
        FnSolicitudExoneracion SolExo = (FnSolicitudExoneracion) entradas.get("solicitud");

        List<Long> existeExoneracion = new ArrayList<>();
        List<Long> prediosPagados = new ArrayList<>();
        if (tipoPredio == 1) {
            if (predios == null || predios.isEmpty()) {
                JsfUti.messageError(null, "Advertencia", "Ingresar por lo menos un predio.");
                return;
            }
            for (CatPredio prop1 : predios) {
                // Verificar si existe Solicitud
                List<FnSolicitudExoneracion> solicitud = getServices().verficarSolicitudExoneracion(exoneracionTipo, SolExo.getAnioInicio(), SolExo.getAnioFin(), prop1);
                if (solicitud != null && solicitud.size() > 0) {
                    existeExoneracion.add(prop1.getId());
                    prop1.add("solicitudes", solicitud);
                }
                // Verificar que el predio no no tenga ningun pago
                List<RenLiquidacion> liquidPag = getServices().getPagosPredio(prop1, SolExo, Boolean.TRUE);
                if (liquidPag != null && liquidPag.size() > 0) {
                    prediosPagados.add(prop1.getId());
                    prop1.add(tipo.LIQUIDACION.name(), liquidPag);
                }
            }
        } else {
            if (prediosRusticos == null || prediosRusticos.isEmpty()) {
                JsfUti.messageError(null, "Advertencia", "Ingresar por lo menos un predio.");
                return;
            }
            for (CatPredioRustico rustico : prediosRusticos) {
                // Verificar sis existe solicitudes ingresadas.
                List<FnSolicitudExoneracion> solicitudes = getServices().verficarSolicitudExoneracionRust(exoneracionTipo, SolExo.getAnioInicio(), SolExo.getAnioFin(), rustico);
                if (solicitudes != null && solicitudes.size() > 0) {
                    existeExoneracion.add(rustico.getId());
                    rustico.add("solicitudes", solicitudes);
                }
                // Verificar si la emision anual esta pagada.
                List<RenLiquidacion> liquidPag = getServices().getPagosPredioRusticos(rustico, SolExo);
                if (liquidPag != null && liquidPag.size() > 0) {
                    prediosPagados.add(rustico.getId());
                    rustico.add(tipo.LIQUIDACION.name(), liquidPag);
                }
            }
        }
        if (existeExoneracion.size() > 0) {
            entradas.put("header", "Predios con solicitud registrada");
            entradas.put("desc", "Los siguientes predios no pueder ser ingresados debido a que tiene una solicitud en proceso.");
            entradas.put("numSol", armarMensaje(existeExoneracion, false));
            JsfUti.executeJS("PF('dlgIdLiquidacion').show()");
            JsfUti.update("menSol:dlgIdLiquidacion");
            JsfUti.update("menSol:dlgDilLiq");
            return;
        }
        if (prediosPagados.size() > 0) {
            entradas.put("header", "Predios con emisión anual pagados");
            entradas.put("desc", "Los siguientes predios no pueden ser ingresados debido a que la emisión ya fue pagada");
            entradas.put("numSol", armarMensaje(prediosPagados, true));
            JsfUti.executeJS("PF('dlgIdLiquidacion').show()");
            JsfUti.update("menSol:dlgIdLiquidacion");
            JsfUti.update("menSol:dlgDilLiq");
            return;
        }

        JsfUti.executeJS("PF('obs').show()");
        JsfUti.update("ComObs:frmObsCor");
    }

    private String armarMensaje(List<Long> list, boolean esPago) {
        String mensaje = "";
        if (list != null) {
            if (!esPago) { // Solicitud

                if (tipoPredio == 1) {
                    for (CatPredio urbano : predios) {
                        if (urbano.get("solicitudes") != null) {
                            mensaje += "Predio Número " + urbano.getNumPredio() + " &#8629; ";
                        }
                    }
                } else {
                    for (CatPredioRustico rustico : prediosRusticos) {
                        if (rustico.get("solicitudes") != null) {
                            List<FnSolicitudExoneracion> sol = rustico.get("solicitudes");
                            String numSol = null;
                            for (FnSolicitudExoneracion sol1 : sol) {
                                numSol = (numSol == null ? "" : numSol + ",") + sol1.getId();
                            }
                            mensaje += "El Predio " + rustico.getRegCatastral() + "-" + rustico.getIdPredial() + " tiene una solicitud " + numSol + " &#8629; ";
                        }
                    }
                }
            } else {
                // Pagos
                if (tipoPredio == 1) {
                    for (CatPredio urbano : predios) {
                        if (urbano.get(tipo.LIQUIDACION.name()) != null) {
                            String pago = null;
                            List<RenLiquidacion> pagos = urbano.get(tipo.LIQUIDACION.name());
                            for (RenLiquidacion p : pagos) {
                                pago = (pago == null ? "" : pago + ",") + p.getAnio();
                            }
                            mensaje += "Predio Número " + urbano.getNumPredio() + " año(s) " + pago + " &#8629; ";
                        }
                    }
                } else {
                    for (CatPredioRustico rustico : prediosRusticos) {
                        if (rustico.get(tipo.LIQUIDACION.name()) != null) {
                            String pago = null;
                            List<RenLiquidacion> pagos = rustico.get(tipo.LIQUIDACION.name());
                            for (RenLiquidacion p : pagos) {
                                pago = (pago == null ? "" : pago + ",") + p.getAnio();
                            }
                            mensaje += "Predio " + rustico.getIdPredial() + " año " + pago + " &#8629; ";
                        }
                    }
                }
            }
        }
        return mensaje;
    }

    public void registrarSolicitud() {
        String obs = (String) entradas.get("obs");
        if (obs == null) {
            JsfUti.messageError(null, MessagesRentas.advert, "Debe seleccionar la clase de exoneración.");
            return;
        }
        if (obs.trim().length() <= 0) {
            JsfUti.messageError(null, MessagesRentas.advert, "Debe seleccionar la clase de exoneración.");
            return;
        }
        if (exoneracion == null) {
            JsfUti.messageError(null, MessagesRentas.advert, "Debe seleccionar la clase de exoneración.");
            return;
        }
        if (exoneracionTipo == null) {
            JsfUti.messageError(null, MessagesRentas.advert, "Debe seleccionar el tipo de exoneracón.");
            return;
        }

        FnSolicitudExoneracion SolExo = (FnSolicitudExoneracion) entradas.get("solicitud");

        SolExo.setExoneracionTipo(exoneracionTipo);
        SolExo.setFechaIngreso(new Date());
        SolExo.setEstado(new FnEstadoExoneracion(2L));
        SolExo.setUsuarioCreacion(session.getName_user());
        SolExo.setSolicitante(this.getComprador());
        SolExo.setSolicitudGrupal((Integer.valueOf(entradas.get("tipo").toString())) == 1);
        if (tipoPredio == 1) {
            SolExo = this.getServices().registraSolicitudExoneracion(SolExo, session.getUserId(), predios, obs);
        } else {
            SolExo = this.getServices().registraSolicitudExoneracionRust(SolExo, session.getUserId(), prediosRusticos, obs);
        }
        if (SolExo != null) {

            HashMap<String, Object> paramt = new HashMap<>();

            paramt.put("es_catastro", Boolean.FALSE);
            paramt.put("carpeta", SolExo.getTramite().getTipoTramite().getCarpeta());
            paramt.put("listaArchivos", this.getFiles());
            paramt.put("listaArchivosFinal", new ArrayList<>());
            paramt.put("descripcion", SolExo.getTramite().getTipoTramiteNombre());
            paramt.put("tramite", SolExo.getTramite().getId());
            paramt.put("digitalizador", uSession.getName_user());
            ///VARIABLES DE FACELETS QUEMADITAS CORTESIA DE ANDY :V 
            paramt.put("rutaFormulario", "/vistaprocesos/financiero/generarResolucion.xhtml");
            paramt.put("frmAprobar", "/vistaprocesos/financiero/aprobarResolucion.xhtml");
            paramt.put("secretaria", uSession.getName_user());
            paramt.put("director", uSession.getName_user());
            if (!jefeRentas.equals("")) {
                paramt.put("renta", jefeRentas);
            }

            paramt.put("prioridad", 50);

            for (ParametrosDisparador parm : SolExo.getTramite().getTipoTramite().getParametrosDisparadorCollection()) {
                if (parm.getEstado()) {
                    if (parm.getVarInterfaz() != null) {
                        paramt.put(parm.getVarInterfaz(), parm.getInterfaz());
                    }
                    if (parm.getVarResp() != null && parm.getResponsable() != null) {
                        paramt.put(parm.getVarResp(), parm.getResponsable().getUsuario());
                    }
                }
            }
            if (esCatastro) {
                //    paramt.put("catastro", uSession.getName_user());
            } else {

            }
            paramt.put("user_financiero", uSession.getName_user());
            ProcessInstance pro = this.startProcessByDefinitionKey(SolExo.getTramite().getTipoTramite().getActivitykey(), paramt);
            if (pro != null) {
                SolExo.getTramite().setIdProcesoTemp(pro.getId());
                SolExo.getTramite().setIdProceso(pro.getId());
                entradas.put("header", "Se ha registrado la solicitud");
                entradas.put("desc", "Solicitud número: ");
                entradas.put("numSol", SolExo.getId() + " - Nº Tramite:" + SolExo.getTramite().getId());
                if (getServices().actualizarsolicitudExoneracion(SolExo)) {
                    JsfUti.executeJS("PF('obs').hide()");
                    JsfUti.executeJS("PF('dlgIdLiquidacion').show()");
                    JsfUti.update("menSol:dlgIdLiquidacion");
                    JsfUti.update("menSol:dlgDilLiq");
                    JsfUti.messageInfo(null, MessagesRentas.info, MessagesRentas.guardadoCorrecto);
                }
            }
        } else {
            JsfUti.messageError(null, MessagesRentas.advert, MessagesRentas.ErrorGuardar);
        }
//        borrarDatos();
    }

    public void borrarDatos() {
        exoneracion = null;
        exoneracionTipo = null;

        entradas = new HashMap<>();
        entradas.put("tipo", 1);
        entradas.put("obs", "");
        entradas.put("ente", new CatEnte());
        entradas.put("esPersona", Boolean.TRUE);
        entradas.put("claseExon", new FnExoneracionClase());
        entradas.put("solicitud", new FnSolicitudExoneracion(Utils.getAnio(new Date()), Utils.getAnio(new Date()), 0, new RenTipoValor(1L)));
        mostrar = false;
        entradas.put("predProp", new CatPredioPropietario());
        entradas.put("fechaLimit", new Date());
        tipoPredio = 1;
        entradas.put("prediosEncontrados", new ArrayList<>());
        setComprador(null);
        predios = null;
        prediosRusticos = null;
    }

    public void ocultar() {
        borrarDatos();
        JsfUti.executeJS("PF('dlgIdLiquidacion').hide()");
        JsfUti.update("frmSolExo");
    }

    public SolicitudExoneracion() {
    }

    public Map<String, Object> getEntradas() {
        return entradas;
    }

    public void setEntradas(Map<String, Object> entradas) {
        this.entradas = entradas;
    }

    public FnExoneracionClase getExoneracion() {
        return exoneracion;
    }

    public void setExoneracion(FnExoneracionClase exoneracion) {
        this.exoneracion = exoneracion;
    }

    public FnExoneracionTipo getExoneracionTipo() {
        return exoneracionTipo;
    }

    public void setExoneracionTipo(FnExoneracionTipo exoneracionTipo) {
        this.exoneracionTipo = exoneracionTipo;
    }

    public List<CatPredio> getPredios() {
        return predios;
    }

    public void setPredios(List<CatPredio> predios) {
        this.predios = predios;
    }

    public List<CatPredioRustico> getPrediosRusticos() {
        return prediosRusticos;
    }

    public void setPrediosRusticos(List<CatPredioRustico> prediosRusticos) {
        this.prediosRusticos = prediosRusticos;
    }

    public Integer getTipoPredio() {
        return tipoPredio;
    }

    public void setTipoPredio(Integer tipoPredio) {
        this.tipoPredio = tipoPredio;
    }

    public Boolean getMostrar() {
        return mostrar;
    }

    public void setMostrar(Boolean mostrar) {
        this.mostrar = mostrar;
    }

    enum tipo {

        LIQUIDACION,
        PREDIOS
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public Boolean getUsuarioAutorizado() {
        return usuarioAutorizado;
    }

    public void setUsuarioAutorizado(Boolean usuarioAutorizado) {
        this.usuarioAutorizado = usuarioAutorizado;
    }

}
