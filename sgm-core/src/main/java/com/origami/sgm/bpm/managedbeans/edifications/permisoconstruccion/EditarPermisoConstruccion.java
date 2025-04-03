/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.permisoconstruccion;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEdfCategProp;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeDetallePermiso;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.entities.PePermisoCabEdificacion;
import com.origami.sgm.entities.PeTipoPermiso;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.DivisionPredioServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import util.EntityBeanCopy;
import util.GroovyUtil;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class EditarPermisoConstruccion implements Serializable {

    public static final Long serialVersionUID = 1L;

    @Inject
    private ReportesView generarReportes;
    @Inject
    private UserSession sess;
    @Inject
    private ServletSession servletSession;

    @javax.inject.Inject
    private Entitymanager service;
    @javax.inject.Inject
    protected DivisionPredioServices divisonServices;
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;
    @javax.inject.Inject
    protected FichaIngresoNuevoServices fichaServices;

    protected Long idPermiso;
    protected Long IdTramite;
    private Boolean ListGuardado = false;
    private Boolean listoImpr = false;
    private Boolean impreso = false;
    private Boolean impresoPermiso = false;
    private Observaciones ob;

    private HashMap<String, Object> paramt;
    private String nombreTec;
    private String representanteLegal;
    private String observacion;
    private Boolean imprimir = false;
    private Boolean esRenovacion = false;
    private Boolean impresoLiquidacion = false;
    private Short numEdificacion;

    protected AclUser tecnico = new AclUser();
    protected PdfReporte reporte = new PdfReporte();
    protected GeTipoTramite tramite;
    protected CatPredio predio;
    protected PePermiso permisoNuevo;
    protected CatEnte respTec = new CatEnte();
    protected CatEnte respTecNuevo = new CatEnte();
    protected CatEnte propietarioNuevo = new CatEnte();
    protected PePermisoCabEdificacion nuevDetalleEdif;
    protected PePermisoCabEdificacion permisoSelect;
    protected PeDetallePermiso detallePermiso = new PeDetallePermiso();
    protected CatEdfCategProp categoria = new CatEdfCategProp();
    protected CatEdfProp carateristicas = new CatEdfProp();
    private MsgFormatoNotificacion ms;

    protected List<CatEdfCategProp> listCat = new ArrayList<>();
    protected List<PeTipoPermiso> listRequisTra = new ArrayList<>();
    protected List<CatPredioPropietario> listaPropietarios;
    protected List<CatPredioPropietario> listaPropietariosEliminar = new ArrayList<>();
    protected List<PePermisoCabEdificacion> detallesEdific;
    protected List<CatEdfProp> lisCatEdfProp = new ArrayList<>();
    protected List<PeDetallePermiso> lisPeDetallePermisos = new ArrayList<>();
    protected List<PePermisoCabEdificacion> detallesEdificEliminar = new ArrayList<>();
    protected List<PeDetallePermiso> peDetallePermisoEliminar = new ArrayList<>();
    protected CatEnteLazy enteLazy;

    private HistoricoTramites ht = new HistoricoTramites();

    /*
     Estas dos variables son para obtener la formulas de calculo.
     */
    protected MatFormulaTramite formulas;
    protected GroovyUtil groovyUtil;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            initView();
        }
    }

//    @PostConstruct
    public void initView() {
        if (idPermiso != null) {
            listaPropietarios = new ArrayList<>();
            permisoNuevo = new PePermiso();
            nuevDetalleEdif = new PePermisoCabEdificacion();
            permisoSelect = new PePermisoCabEdificacion();
            detallesEdific = new ArrayList<>();
            listaPropietarios = new ArrayList<>();
            PePermiso p;
            p = permisoServices.getPePermisoById(idPermiso);

            if (p != null) {
                tecnico = p.getUsuarioCreador();
                if (p.getTramite() != null) {
                    ht = permisoServices.getHistoricoTramiteById(p.getTramite().getId());
                    tramite = permisoServices.getGeTipoTramiteById(ht.getTipoTramite().getId());
                }

                listRequisTra = (List<PeTipoPermiso>) EntityBeanCopy.clone(permisoServices.getPeTipoPermisoList());
                predio = new CatPredio();

                permisoNuevo = p;
                permisoNuevo.setTipoPermiso((PeTipoPermiso) EntityBeanCopy.clone(p.getTipoPermiso()));
                predio = p.getIdPredio();
                if (predio.getNumPredio() != null) {
                    consultarNumPredio();
                } else {
                    consultarIdPredio();
                }

                respTec = p.getResponsablePersona();

                if (p.getPePermisoCabEdificacionCollection().size() > 0) {
                    detallesEdific = (List<PePermisoCabEdificacion>) p.getPePermisoCabEdificacionCollection();
                    detallePermiso.setPorcentaje(new BigDecimal("0.00"));
                }

                if (detallesEdific.size() > 0) {
                    permisoSelect = detallesEdific.get(0);
                }
                ob = new Observaciones();
                permisoNuevo.setPropietarioPersona(p.getPropietarioPersona());

                permisoNuevo.setResponsablePersona(respTec);
                listCat = permisoServices.getCatEdfCategPropList();
                if (tramite == null) {
                    tramite = (GeTipoTramite) service.find(GeTipoTramite.class, 2L);
                }
                formulas = formulas = permisoServices.getMatFormulaTramite(tramite.getId());

            } else {
                JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/permisoConstruccion/generarLiquidacionPC.xhtml");
                JsfUti.messageError(null, "No hay información del Tramite Solicitado", "");
            }
        }

    }

    public void modificarPermiso() {

        if (permisoNuevo.getIdPredio() == null) {
            JsfUti.messageError(null, "Debe Buscar el predio", "");
            return;
        }
        if (permisoNuevo.getResponsablePersona() == null) {
            JsfUti.messageError(null, "Falta Seleccionar al Responsable Tecnico", "");
            return;
        }
        if (permisoNuevo.getDescFamiliar() == null) {
            JsfUti.messageError(null, "Debe llenar Descripcion de Edificacion. por 1 RESIDENCIA UNIFAMILIAR o 1 RESIDENCIA MULTIFAMILIAR o 1 RESIDENCIA BIFAMILIAR", "");
            return;
        }
        if (permisoNuevo.getFechaEmision() == null && permisoNuevo.getFechaCaducidad() == null && permisoNuevo.getTramite() == null) {
            JsfUti.messageError(null, "Faltan llenar algunos campos en la secciÃ³n Datos de Permiso", "");
            return;
        }
        if (permisoNuevo.getRetiroLateral1() == null) {
            JsfUti.messageError(null, "Faltan llenar algunos campos: Retiro lateral 1", "");
            return;
        }
        if (permisoNuevo.getRetiroLateral2() == null) {
            JsfUti.messageError(null, "Faltan llenar algunos campos: Retiro Lateral 2", "");
            return;
        }
        if (listaPropietarios.isEmpty() || listaPropietarios.size() == 0) {
            JsfUti.messageError(null, "Debe Seleccionar un Propietario", "");
            return;
        }
        for (PePermisoCabEdificacion de : detallesEdific) {
            permisoSelect = de;
            de.setPeDetallePermisoCollection(permisoSelect.getPeDetallePermisoCollection());
        }
        permisoNuevo.setFechaModificacion(new Date());
        permisoNuevo.setUsuarioModificacion(sess.getName_user());
        PePermiso p = permisoServices.modificarLiquidacion(listaPropietariosEliminar, listaPropietarios, permisoNuevo,
                detallesEdific, detallesEdificEliminar, peDetallePermisoEliminar, formulas);
        if (p != null) {
            permisoNuevo = p;
            groovyUtil = new GroovyUtil(formulas.getFormula());
            groovyUtil.setProperty("permiso", p);
            permisoServices.actualizarHistoricoTramitesAndValorLiquidacion(
                    (BigDecimal) groovyUtil.getExpression("getValorLiquidacion", new Object[]{}), ht);
            JsfUti.messageInfo(null, "Información Actualizada con exito", "");
            listoImpr = true;
        }
        JsfUti.update("forGenLiq");
    }

    public void imprimirLiquidacion() {
        paramt = new HashMap<>();
        AclUser firmaDirector = permisoServices.getAclUserByUser(tramite.getUserDireccion());
        AclUser firmaDir = (AclUser) EntityBeanCopy.clone(firmaDirector);

        servletSession.instanciarParametros();
        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        servletSession.agregarParametro("permiso", permisoNuevo.getId());
        servletSession.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
        servletSession.agregarParametro("SUBREPORT_DIR", path + "reportes/permisoConstruccion/");
        servletSession.agregarParametro("firmaDirec", path + "css/firmas/" + firmaDir.getRutaImagen() + ".jpg");
        servletSession.agregarParametro("firmaTecni", path + "css/firmas/" + tecnico.getRutaImagen() + ".jpg");
        servletSession.agregarParametro("idUser", sess.getUserId());
        servletSession.setNombreReporte("LiquidacionTasasPermiso");
        servletSession.setTieneDatasource(true);
        servletSession.setNombreSubCarpeta("permisoConstruccion");

        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public void bandejaTareas() {
        JsfUti.redirectFaces("/vistaprocesos/edificaciones/permisoConstruccion/permisoConstruccionConsulta.xhtml");
    }

    public void guadar() {
        permisoNuevo.setResponsablePersona(respTec);
    }

    public void renderPersNat() {
        propietarioNuevo = new CatEnte();
        JsfUti.update("formNuProp");
        JsfUti.executeJS("PF('dlgNuevPro').show()");
    }

    public void consultarCodPredio() {
        listaPropietarios = new ArrayList<>();
        CatPredio pred = permisoServices.getCatPredioByCodigoPredio(predio.getZona(),predio.getSector(), predio.getMz(), predio.getSolar());
        if (pred != null) {
            predio = pred;
            List<CatPredioPropietario> propietariosTemp = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();
            if (!propietariosTemp.isEmpty()) {
                for (CatPredioPropietario temp : propietariosTemp) {
                    if ("A".equals(temp.getEstado())) {
                        listaPropietarios.add(temp);
                    }
                }
            }
            permisoNuevo.setIdPredio(predio);

            //MODIFICADO 26-09-2016 hpilco
            //INICIO
            permisoNuevo.setNombUrbanizacionImpresa(pred.getCiudadela() == null ? "" : pred.getCiudadela().getNombre());
            permisoNuevo.setUrbmz(pred.getUrbMz());
            permisoNuevo.setUrbsolar(pred.getUrbSolarnew());
            //FIN
            JsfUti.update("forGenLiq:panelCons");
        } else {
            JsfUti.messageError(null, "No hay registro con el Código de predio ingresado ir al DEPARTAMENTO DE CATASTRO PARA QUE INGRESEN EL PREDIO", "");
        }
    }

    public void consultarNumPredio() {
        if (predio.getNumPredio() == null) {
            JsfUti.messageError(null, "Debe Ingresar el Número de Predio", "");
            return;
        }
        CatPredio pred1 = fichaServices.getPredioByNum(predio.getNumPredio().longValue());
        if (pred1 == null) {
            JsfUti.messageError(null, "No hay registro con el Número de predio ingresado", "");
            return;
        }
        predio = pred1;
        List<CatPredioPropietario> propietariosTemp = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();
        if (!propietariosTemp.isEmpty()) {
            for (CatPredioPropietario temp : propietariosTemp) {
                if ("A".equals(temp.getEstado())) {
                    listaPropietarios.add(temp);
                }
            }
        }
        permisoNuevo.setIdPredio(predio);
        permisoNuevo.setNombUrbanizacionImpresa(pred1.getCiudadela() == null ? "" : pred1.getCiudadela().getNombre());
        permisoNuevo.setUrbmz(pred1.getUrbMz());
        permisoNuevo.setUrbsolar(pred1.getUrbSolarnew());
    }

    public void buscarTecnico() {
        if (respTec.getCiRuc() == null) {
            JsfUti.messageError(null, "Debe Ingresar Número de Cédula", "");
            return;
        }
        CatEnte ente = permisoServices.getCatEnteByCiRucByEsPersona(respTec.getCiRuc(), true);
        if (ente != null) {
            nombreTec = ente.getTituloProf() + " " + ente.getApellidos() + " " + ente.getNombres();
            respTec = ente;
            permisoNuevo.setResponsablePersona((CatEnte) EntityBeanCopy.clone(respTec));
            JsfUti.update("forGenLiq:resT");
            JsfUti.update("forGenLiq:regP");
        } else {
            respTecNuevo.setCiRuc(respTec.getCiRuc());
            JsfUti.executeJS("PF('dlgNuevRespTec').show()");
        }

    }

    public void guardarResTecn() {
        if (respTecNuevo.getCiRuc() != null && respTecNuevo.getApellidos() != null && respTecNuevo.getNombres() != null) {
            respTecNuevo.setEsPersona(true);
            respTecNuevo.setEstado("A");
            respTec = new CatEnte();
            respTec = fichaServices.guardarCatEnte(respTecNuevo);
        } else {
            respTecNuevo.setEstado("A");
            fichaServices.guardarCatEnte(respTec);
        }
        permisoNuevo.setResponsablePersona((CatEnte) EntityBeanCopy.clone(respTec));
        permisoNuevo.setResponsable(nombreTec);
        nombreTec = respTec.getTituloProf() + ". " + respTec.getApellidos() + " " + respTec.getNombres();
        JsfUti.update("forGenLiq:resT");
        JsfUti.update("forGenLiq:regP");
        JsfUti.update("forGenLiq:ciIde");
        JsfUti.executeJS("PF('dlgEditRespTec').hide()");
    }

    public void buscarPropietario() {
        if (propietarioNuevo.getCiRuc() != null) {
            CatEnte nuwEnt = fichaServices.getCatEnte(propietarioNuevo.getCiRuc());
            if (nuwEnt != null) {
                for (CatPredioPropietario listCat1 : listaPropietarios) {
                    if (listCat1.getEnte().getId().compareTo(nuwEnt.getId()) == 0) {
                        JsfUti.messageInfo(null, "Cliente ya fue agregado al predio", "");
                        return;
                    }
                }
                CatPredioPropietario entP = new CatPredioPropietario();
                propietarioNuevo = nuwEnt;
                entP.setEnte(propietarioNuevo);
                entP.setPredio(predio);
                entP.setEstado("A");
                listaPropietarios.add(entP);
                permisoNuevo.setPropietarioPersona(propietarioNuevo);
                propietarioNuevo = new CatEnte();
                JsfUti.update("forGenLiq:dtProp");
                JsfUti.executeJS("PF('dlgNuevPro').hide()");
            }
        }
    }

    public void agregarPropietario() {
        if (propietarioNuevo != null) {
            if (!propietarioNuevo.getEsPersona()) {
                if (representanteLegal == null) {
                    JsfUti.messageInfo(null, "Debe seleccionar el Representante Legal", "");
                    return;
                }
            }
            CatEnte nuwEnt = null;
            if (propietarioNuevo.getCiRuc() != null) {
                nuwEnt = fichaServices.getCatEnte(propietarioNuevo.getCiRuc());
            }
            if (nuwEnt != null) {
                for (CatPredioPropietario listCat1 : listaPropietarios) {
                    if (listCat1.getEnte().getCiRuc().compareTo(propietarioNuevo.getCiRuc()) == 0) {
                        JsfUti.messageInfo(null, "Ya fue agregado un propietario con el mismo número de documento", "");
                        return;
                    }
                }
            }
            permisoNuevo.setPropietarioPersona(propietarioNuevo);
            CatPredioPropietario entP = new CatPredioPropietario();
            entP.setEstado("A");
            entP.setEnte(propietarioNuevo);
            entP.setPredio(predio);
            entP.setModificado("edificacion");
            listaPropietarios.add(entP);
            propietarioNuevo = null;
            representanteLegal = "";
            JsfUti.executeJS("PF('dlgNuevPro').hide()");
            JsfUti.update("forGenLiq:dtProp");
        }
    }

    public void eliminarProp(CatPredioPropietario prop) {
        if (listaPropietarios.size() == 1) {
            JsfUti.messageError(null, "No puede Eliminar todos los propietarios", "");
            return;
        }
        prop.setModificado("edificacion");
        prop.setEstado("I");
        int index = 0;
        int i = 0;
        for (CatPredioPropietario listCat1 : listaPropietarios) {
            if (prop.getId() != null) {
                if (listCat1.getId().compareTo(prop.getId()) == 0) {
                    index = i;
                    break;
                }

            } else {
                if (listCat1.getEnte().getCiRuc().compareTo(prop.getEnte().getCiRuc()) == 0) {
                    index = i;
                    break;
                }
            }
            i++;
        }
        if (prop.getId() != null) {
            listaPropietariosEliminar.add(listaPropietarios.get(index));
        }
        listaPropietarios.remove(index);
        permisoNuevo.setPropietarioPersona(listaPropietarios.get(0).getEnte());
        JsfUti.update("forGenLiq:dtProp");
    }

    public void seleccionarReprest() {
        enteLazy = new CatEnteLazy(true);
        JsfUti.update("formSelectInterv");
        JsfUti.executeJS("PF('dlgSelectReprest').show();");
    }

    public void agregarRepresentante(CatEnte represt) {
        propietarioNuevo.setRepresentanteLegal(new BigInteger(represt.getId().toString()));
        if (represt.getApellidos() != null && represt.getNombres() != null) {
            representanteLegal = represt.getApellidos() + " " + represt.getNombres();
        } else if (represt.getApellidos() != null) {
            representanteLegal = represt.getApellidos();
        } else if (propietarioNuevo.getNombres() != null) {
            representanteLegal = represt.getNombres();
        }
        JsfUti.update("formNuProp");
        JsfUti.executeJS("PF('dlgSelectReprest').hide()");
    }

    public void agregarEdifi() {
        Boolean edf = true;
        if (!Utils.validateNumberPattern(numEdificacion.toString())) {
            JsfUti.messageInfo(null, "Solo Debe ingresar Números", "");
            return;
        }
        if (!Utils.validateNumberPattern(numEdificacion.toString())) {
            JsfUti.messageInfo(null, "Solo Debe ingresar Números", "");
            return;
        }
        nuevDetalleEdif.setNumEdificacion(numEdificacion);
        if (nuevDetalleEdif.getNumEdificacion() != null) {
            if (nuevDetalleEdif.getNumEdificacion() == 0) {
                nuevDetalleEdif.setDescripcion("Edificacion Principal");
            } else {
                nuevDetalleEdif.setDescripcion("Anexo " + nuevDetalleEdif.getNumEdificacion());
            }
            for (PePermisoCabEdificacion list : detallesEdific) {
                if (list.getNumEdificacion().compareTo(nuevDetalleEdif.getNumEdificacion()) == 0) {
                    edf = false;
                }
            }
            if (edf) {
                nuevDetalleEdif.setIdPermiso(permisoNuevo);
                detallesEdific.add(nuevDetalleEdif);
                nuevDetalleEdif = new PePermisoCabEdificacion();
                detallePermiso.setPorcentaje(new BigDecimal("0.00"));
            } else {
                JsfUti.messageError(null, "Edificacion Ya fue agregada", "");
            }
            calculoAreaConstruccion();
            JsfUti.update("forGenLiq:dtEdif");//dtEdif, edif
            JsfUti.update("forGenLiq:ad");
            JsfUti.update("forGenLiq:ac");
        }
    }

    public void eliminarEdif(PePermisoCabEdificacion edf) {
        permisoSelect = edf;
        int index = 0;
        int i = 0;
        for (PePermisoCabEdificacion list : detallesEdific) {
            if (list.getNumEdificacion().compareTo(edf.getNumEdificacion()) == 0) {
                index = i;
            }
            i++;
        }
        if (edf.getId() != null) {
            detallesEdificEliminar.add(detallesEdific.get(index));
        }
        detallesEdific.remove(index);
        if (permisoSelect.getPeDetallePermisoCollection() != null) {
            for (PeDetallePermiso listCat1 : permisoSelect.getPeDetallePermisoCollection()) {
                peDetallePermisoEliminar.add(listCat1);
            }
            permisoSelect = null;
            permisoSelect = new PePermisoCabEdificacion();
        }
        calculoAreaConstruccion();
        JsfUti.update("forGenLiq:ad");
        JsfUti.update("forGenLiq:ac");
        JsfUti.update("forGenLiq:dtEdif");
        JsfUti.update("forGenLiq:dtCartEdif");
    }

    public void agregarDetalleEdif() {
        Boolean detPer = true;
        if (permisoSelect == null) {
            JsfUti.messageError(null, "Debe Seleccionar una Edificación", "");
            return;
        }
        if (carateristicas == null && categoria == null) {
            JsfUti.messageError(null, "Debe Seleccionar las caracteristicas", "");
            return;
        }
        for (PeDetallePermiso dp : permisoSelect.getPeDetallePermisoCollection()) {
            if (dp.getIdCatEdfProp().getId().compareTo(carateristicas.getId()) == 0) {
                detPer = false;
            }
        }
        if (detPer) {
            detallePermiso.setIdCatEdfProp(carateristicas);
            detallePermiso.setIdPermisoEdificacion(permisoSelect);
            permisoSelect.getPeDetallePermisoCollection().add(detallePermiso);
            carateristicas = new CatEdfProp();
            categoria = new CatEdfCategProp();
            detallePermiso = new PeDetallePermiso();
            detallePermiso.setPorcentaje(new BigDecimal("0.00"));
        } else {
            JsfUti.messageError(null, "Caracteristica ya fue agregada", "");
        }
        calculoAreaConstruccion();
        JsfUti.update("forGenLiq:lisCartg");
        JsfUti.update("forGenLiq:lisCaract");
        JsfUti.update("forGenLiq:por");
        JsfUti.update("forGenLiq:dtCartEdif");
    }

    public void eliminarDetalleEdif(PeDetallePermiso dpermiso) {
        List<PeDetallePermiso> d = new ArrayList<>();
        for (PeDetallePermiso listCat1 : permisoSelect.getPeDetallePermisoCollection()) {
            if (listCat1.getIdCatEdfProp().getId().compareTo(dpermiso.getIdCatEdfProp().getId()) != 0) {
                d.add(listCat1);
            } else {
                if (dpermiso.getId() != null) {
                    peDetallePermisoEliminar.add(dpermiso);
                }
            }
        }
        permisoSelect.setPeDetallePermisoCollection(new ArrayList<PeDetallePermiso>());
        permisoSelect.getPeDetallePermisoCollection().addAll((Collection<PeDetallePermiso>) d);
        permisoSelect.getPeDetallePermisoCollection().size();
        JsfUti.update("forGenLiq:dtCartEdif");
    }

    public void actulizarLista() {
        if (categoria.getId() != null) {
            lisCatEdfProp = permisoServices.getCatEdfPropList(categoria.getId());
            JsfUti.update("forGenLiq:lisCaract");
        }
    }

    public void calculoAreaConstruccion() {
        BigDecimal totArea = new BigDecimal(0);
        for (PePermisoCabEdificacion det : detallesEdific) {
            totArea = totArea.add(det.getAreaConstruccion());
        }
        permisoNuevo.setAreaEdificaciones(totArea);
        if (permisoNuevo.getAreaParqueos() != null) {
            totArea = totArea.add((permisoNuevo.getAreaParqueos()));
        }
        permisoNuevo.setAreaConstruccion(totArea);
        JsfUti.update("forGenLiq:ac");
    }

    public void onRowSelect(SelectEvent event) {
        lisPeDetallePermisos = (List<PeDetallePermiso>) permisoSelect.getPeDetallePermisoCollection();
        JsfUti.update("forGenLiq:dtCartEdif");
    }

    private void consultarIdPredio() {
        if (!ht.getHistoricoTramiteDetCollection().isEmpty()) {
            List<HistoricoTramiteDet> htd = (List<HistoricoTramiteDet>) ht.getHistoricoTramiteDetCollection();
            HistoricoTramiteDet histdet = htd.get(0);
            predio = histdet.getPredio();
        }
        List<CatPredioPropietario> propietariosTemp = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();
        if (!propietariosTemp.isEmpty()) {
            for (CatPredioPropietario temp : propietariosTemp) {
                if ("A".equals(temp.getEstado())) {
                    listaPropietarios.add(temp);
                }
            }
        }
        permisoNuevo.setIdPredio(predio);
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public HashMap<String, Object> getParamt() {
        return paramt;
    }

    public void setParamt(HashMap<String, Object> paramt) {
        this.paramt = paramt;
    }

    public GeTipoTramite getTramite() {
        return tramite;
    }

    public void setTramite(GeTipoTramite tramite) {
        this.tramite = tramite;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public List<PeTipoPermiso> getListRequisTra() {
        return listRequisTra;
    }

    public void setListRequisTra(List<PeTipoPermiso> listRequisTra) {
        this.listRequisTra = listRequisTra;
    }

    public PePermiso getPermisoNuevo() {
        return permisoNuevo;
    }

    public void setPermisoNuevo(PePermiso permisoNuevo) {
        this.permisoNuevo = permisoNuevo;
    }

    public List<CatPredioPropietario> getListaPropietarios() {
        return listaPropietarios;
    }

    public void setListaPropietarios(List<CatPredioPropietario> listaPropietarios) {
        this.listaPropietarios = listaPropietarios;
    }

    public CatEnte getRespTec() {
        return respTec;
    }

    public void setRespTec(CatEnte respTec) {
        this.respTec = respTec;
    }

    public CatEnte getRespTecNuevo() {
        return respTecNuevo;
    }

    public void setRespTecNuevo(CatEnte respTecNuevo) {
        this.respTecNuevo = respTecNuevo;
    }

    public String getNombreTec() {
        return nombreTec;
    }

    public void setNombreTec(String nombreTec) {
        this.nombreTec = nombreTec;
    }

    public CatEnte getPropietarioNuevo() {
        return propietarioNuevo;
    }

    public void setPropietarioNuevo(CatEnte propietarioNuevo) {
        this.propietarioNuevo = propietarioNuevo;
    }

    public List<PePermisoCabEdificacion> getDetallesEdific() {
        return detallesEdific;
    }

    public void setDetallesEdific(List<PePermisoCabEdificacion> detallesEdific) {
        this.detallesEdific = detallesEdific;
    }

    public PePermisoCabEdificacion getNuevDetalleEdif() {
        return nuevDetalleEdif;
    }

    public void setNuevDetalleEdif(PePermisoCabEdificacion nuevDetalleEdif) {
        this.nuevDetalleEdif = nuevDetalleEdif;
    }

    public List<CatEdfCategProp> getListCat() {
        return listCat;
    }

    public void setListCat(List<CatEdfCategProp> listCat) {
        this.listCat = listCat;
    }

    public PeDetallePermiso getDetallePermiso() {
        return detallePermiso;
    }

    public void setDetallePermiso(PeDetallePermiso detallePermiso) {
        this.detallePermiso = detallePermiso;
    }

    public CatEdfCategProp getCategoria() {
        return categoria;
    }

    public void setCategoria(CatEdfCategProp categoria) {
        this.categoria = categoria;
    }

    public List<CatEdfProp> getLisCatEdfProp() {
        return lisCatEdfProp;
    }

    public void setLisCatEdfProp(List<CatEdfProp> lisCatEdfProp) {
        this.lisCatEdfProp = lisCatEdfProp;
    }

    public PePermisoCabEdificacion getPermisoSelect() {
        return permisoSelect;
    }

    public void setPermisoSelect(PePermisoCabEdificacion permisoSelect) {
        this.permisoSelect = permisoSelect;
    }

    public CatEdfProp getCarateristicas() {
        return carateristicas;
    }

    public void setCarateristicas(CatEdfProp carateristicas) {
        this.carateristicas = carateristicas;
    }

    public List<PeDetallePermiso> getLisPeDetallePermisos() {
        return lisPeDetallePermisos;
    }

    public void setLisPeDetallePermisos(List<PeDetallePermiso> lisPeDetallePermisos) {
        this.lisPeDetallePermisos = lisPeDetallePermisos;
    }

    public Boolean getImprimir() {
        return imprimir;
    }

    public void setImprimir(Boolean imprimir) {
        this.imprimir = imprimir;
    }

    public Boolean getEsRenovacion() {
        return esRenovacion;
    }

    public void setEsRenovacion(Boolean esRenovacion) {
        this.esRenovacion = esRenovacion;
    }

    public PdfReporte getReporte() {
        return reporte;
    }

    public void setReporte(PdfReporte reporte) {
        this.reporte = reporte;
    }

    public MsgFormatoNotificacion getMs() {
        return ms;
    }

    public void setMs(MsgFormatoNotificacion ms) {
        this.ms = ms;
    }

    public List<CatPredioPropietario> getListaPropietariosEliminar() {
        return listaPropietariosEliminar;
    }

    public void setListaPropietariosEliminar(List<CatPredioPropietario> listaPropietariosEliminar) {
        this.listaPropietariosEliminar = listaPropietariosEliminar;
    }

    public Boolean getImpresoLiquidacion() {
        return impresoLiquidacion;
    }

    public void setImpresoLiquidacion(Boolean impresoLiquidacion) {
        this.impresoLiquidacion = impresoLiquidacion;
    }

    public Short getNumEdificacion() {
        return numEdificacion;
    }

    public void setNumEdificacion(Short numEdificacion) {
        this.numEdificacion = numEdificacion;
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

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public ReportesView getGenerarReportes() {
        return generarReportes;
    }

    public void setGenerarReportes(ReportesView generarReportes) {
        this.generarReportes = generarReportes;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public Boolean getListGuardado() {
        return ListGuardado;
    }

    public void setListGuardado(Boolean ListGuardado) {
        this.ListGuardado = ListGuardado;
    }

    public Boolean getListoImpr() {
        return listoImpr;
    }

    public void setListoImpr(Boolean listoImpr) {
        this.listoImpr = listoImpr;
    }

    public Boolean getImpreso() {
        return impreso;
    }

    public void setImpreso(Boolean impreso) {
        this.impreso = impreso;
    }

    public Observaciones getOb() {
        return ob;
    }

    public void setOb(Observaciones ob) {
        this.ob = ob;
    }

    public Long getIdPermiso() {
        return idPermiso;
    }

    public void setIdPermiso(Long idPermiso) {
        this.idPermiso = idPermiso;
    }

    public Long getIdTramite() {
        return IdTramite;
    }

    public void setIdTramite(Long IdTramite) {
        this.IdTramite = IdTramite;
    }

}
