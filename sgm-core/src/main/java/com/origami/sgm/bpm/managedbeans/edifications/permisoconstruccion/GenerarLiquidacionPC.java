/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.permisoconstruccion;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEdfCategProp;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoReporteTramite;
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
import com.origami.sgm.services.interfaces.edificaciones.DivisionPredioServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.hibernate.Hibernate;
import org.primefaces.event.SelectEvent;
import util.EntityBeanCopy;
import util.GroovyUtil;
import util.JsfUti;
import util.Messages;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class GenerarLiquidacionPC extends BpmManageBeanBaseRoot implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    protected DivisionPredioServices divisonServices;
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;
    @javax.inject.Inject
    protected FichaIngresoNuevoServices fichaServices;

    private HashMap<String, Object> paramt;
    private String nombreTec;
    private String representanteLegal;
    private String observacion;
    private Boolean imprimir = false;
    private Boolean guardado = true;
    private Boolean banTarea = false;
//    private Boolean esRenovacion = false;
    private Boolean impresoLiquidacion = false;
    private Short numEdificacion;

    protected PdfReporte reporte = new PdfReporte();
    protected GeTipoTramite tramite = new GeTipoTramite();
    protected HistoricoTramites histTramite = new HistoricoTramites();
    protected Observaciones observ = new Observaciones();
    protected CatPredio predio;
    protected PePermiso permisoNuevo = new PePermiso();
    protected CatEnte respTec = new CatEnte();
    protected CatEnte respTecNuevo = new CatEnte();
    protected CatEnte propietarioNuevo = new CatEnte();
    protected PePermisoCabEdificacion nuevDetalleEdif;
    protected PePermisoCabEdificacion permisoSelect = new PePermisoCabEdificacion();
    protected PeDetallePermiso detallePermiso = new PeDetallePermiso();
    protected CatEdfCategProp categoria = new CatEdfCategProp();
    protected CatEdfProp carateristicas = new CatEdfProp();
    private MsgFormatoNotificacion ms;

    protected List<CatEdfCategProp> listCat = new ArrayList<>();
    protected List<PeTipoPermiso> listRequisTra = new ArrayList<>();
    protected List<CatPredioPropietario> listaPropietarios;
    protected List<CatPredioPropietario> listaPropietariosEliminar = new ArrayList<>();
    protected List<PePermisoCabEdificacion> DetallesEdific;
    protected List<CatEdfProp> lisCatEdfProp = new ArrayList<>();
    protected List<PeDetallePermiso> lisPeDetallePermisos = new ArrayList<>();
    protected CatEnteLazy enteLazy;

    @Inject
    private UserSession sess;
    @Inject
    private ServletSession ss;
    @Inject
    private ReportesView reportes;

    /*
     Estas dos variables son para obtener la formulas de calculo.
     */
    protected MatFormulaTramite formulas;
    protected GroovyUtil groovyUtil;

    @PostConstruct
    public void initView() {
        if (sess != null && sess.getTaskID() != null) {
//            permisoNuevo = new PePermiso();
            predio = new CatPredio();
            nuevDetalleEdif = new PePermisoCabEdificacion();
            DetallesEdific = new ArrayList<>();
            listaPropietarios = new ArrayList<>();
            this.setTaskId(sess.getTaskID());
            Calendar cl = Calendar.getInstance();
            histTramite = permisoServices.getHistoricoTramiteById(Long.parseLong(this.getVariable(sess.getTaskID(), "tramite").toString()));
            tramite = permisoServices.getGeTipoTramiteById(histTramite.getTipoTramite().getId());
            listRequisTra = (List<PeTipoPermiso>) EntityBeanCopy.clone(permisoServices.getPeTipoPermisoByCodigo());
            listCat = permisoServices.getCatEdfCategPropList();

            predio.setNumPredio(histTramite.getNumPredio());
            permisoNuevo = new PePermiso();
            permisoNuevo.setFechaEmision(cl.getTime());
            permisoNuevo.setAnioTramite(Short.parseShort(Integer.toString(cl.get(Calendar.YEAR))));
            permisoNuevo.setTramite(histTramite);
            permisoNuevo.setCalle("Vehicular");
            permisoNuevo.setAreaEdificaciones(BigDecimal.ZERO);
            permisoNuevo.setEsMacroLote(false);
            if (histTramite.getIdProceso() != null) {
                permisoNuevo.setIdprocess(new BigInteger(histTramite.getIdProceso()));
            }
            permisoNuevo.setDescFamiliar("1 RESIDENCIA UNIFAMILIAR");
            if (predio.getNumPredio() != null) {
                consultarNumPredio();
            } else {
                consultarIdPredio();
            }

            cl.add(Calendar.YEAR, 1);
            permisoNuevo.setFechaCaducidad(cl.getTime());

            formulas = formulas = permisoServices.getMatFormulaTramite(tramite.getId());
        } else {
            this.continuar();
        }
    }

    public void actualizarTipoPermiso() {
//        esRenovacion = "RENOVACION".equals(histTramite.getTipoTramiteNombre());
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
        try {
            CatPredio pred = permisoServices.getCatPredioByCodigoPredio(predio.getZona(), predio.getSector(), predio.getMz(), predio.getSolar());
            if (pred != null) {
                predio = pred;
                agregarAListProp((List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection());

                permisoNuevo.setIdPredio(predio);

                permisoNuevo.setUrbmz(predio.getUrbMz());
                permisoNuevo.setUrbsolar(predio.getUrbSolarnew());
                permisoNuevo.setNombUrbanizacionImpresa(predio.getCiudadela().getNombre());
            } else {
                JsfUti.messageError(null, "No hay registro con el Código de predio ingresado ir al DEPARTAMENTO DE CATASTRO PARA QUE INGRESEN EL PREDIO", "");
            }
        } catch (Exception e) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void consultarNumPredio() {
        try {
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
            agregarAListProp((List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection());
            permisoNuevo.setIdPredio(predio);
            permisoNuevo.setUrbmz(predio.getUrbMz());
            permisoNuevo.setUrbsolar(predio.getUrbSolarnew());
            if (predio.getCiudadela() != null) {
                permisoNuevo.setNombUrbanizacionImpresa(predio.getCiudadela().getNombre());
            } else {
                permisoNuevo.setNombUrbanizacionImpresa(null);
            }

        } catch (Exception e) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void agregarAListProp(List<CatPredioPropietario> propietarios) {
        if (!propietarios.isEmpty()) {
            listaPropietarios = new ArrayList<>();
            for (CatPredioPropietario temp : propietarios) {
                if ("A".equals(temp.getEstado())) {
                    listaPropietarios.add(temp);
                }
            }
        }
    }

    public void buscarTecnico() {
        try {
            if (respTec.getCiRuc() == null) {
                JsfUti.messageError(null, "Debe Ingresar Número de Cédula", "");
                return;
            }
            CatEnte ente = permisoServices.getCatEnteByCiRucByEsPersona(respTec.getCiRuc(), true);
            if (ente != null) {
                nombreTec = Utils.isEmpty(ente.getTituloProf()) + " " + Utils.isEmpty(ente.getApellidos()) + " " + (ente.getNombres());
                respTec = ente;
                permisoNuevo.setResponsablePersona((CatEnte) EntityBeanCopy.clone(respTec));
                this.validarRegProf(ente);
                JsfUti.update("forGenLiq:resT");
                JsfUti.update("forGenLiq:regP");
            } else {
                respTecNuevo.setCiRuc(respTec.getCiRuc());
                JsfUti.executeJS("PF('dlgNuevRespTec').show()");
            }
        } catch (Exception e) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public void guardarResTecn() {
        try {
            if (!Utils.validateCCRuc(respTecNuevo.getCiRuc())) {
                JsfUti.messageError(null, "", "Número de documento es invalido.");
                return;
            }
            if (respTecNuevo.getCiRuc() != null && respTecNuevo.getApellidos() != null && respTecNuevo.getNombres() != null) {
                respTecNuevo.setEsPersona(true);
                respTecNuevo.setEstado("A");
                respTec = new CatEnte();
                if (fichaServices.getCatEnte(respTecNuevo.getCiRuc()) == null) {
                    respTec = fichaServices.guardarCatEnte(respTecNuevo);
                } else {
                    JsfUti.messageError(null, nombreTec, "Ya existe un cliente registrado con el mismo número de cédula");
                }
            } else {
                respTecNuevo.setEstado("A");
                fichaServices.guardarCatEnte(respTec);
            }
            permisoNuevo.setResponsablePersona((CatEnte) EntityBeanCopy.clone(respTec));
            permisoNuevo.setResponsable(nombreTec);
            nombreTec = Utils.isEmpty(respTec.getTituloProf()) + ". " + Utils.isEmpty(respTec.getApellidos()) + " " + Utils.isEmpty(respTec.getNombres());
            JsfUti.update("forGenLiq:resT");
            JsfUti.update("forGenLiq:regP");
            JsfUti.update("forGenLiq:ciIde");
            JsfUti.executeJS("PF('dlgEditRespTec').hide()");
        } catch (Exception e) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void editarResTecn() {
        if (respTec == null) {
            JsfUti.messageError(null, "No hay seleccionado ningún Responzable Técnico", nombreTec);
            return;
        }
        fichaServices.actualizarCatEnteTelefEmails(respTec, null, null);
        JsfUti.update("forGenLiq:resT");
        JsfUti.update("forGenLiq:regP");
        JsfUti.update("forGenLiq:ciIde");
        JsfUti.executeJS("PF('dlgEditRespTec').hide()");
    }

    public void buscarPropietario() {
        try {
            if (Utils.isEmpty(listaPropietarios)) {
                listaPropietarios = new ArrayList<>();
            }
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
        } catch (Exception e) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void agregarPropietario() {
        if (!Utils.validateCCRuc(propietarioNuevo.getCiRuc())) {
            JsfUti.messageError(null, "", "Número de documento es invalido.");
            return;
        }
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
        try {
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
        } catch (Exception e) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
        }
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
//        if (!Utils.validateNumberPattern(nuevDetalleEdif.getNumeroPisos().toString())) {
//            JsfUti.messageInfo(null, "Solo Debe ingresar Números", "");
//            return;
//        }
        if (!Utils.validateNumberPattern(numEdificacion.toString())) {
            JsfUti.messageInfo(null, "Solo Debe ingresar Números", "");
            return;
        }
        if (nuevDetalleEdif.getNumeroPisos() != null) {
            if (!Utils.validateNumberPattern(nuevDetalleEdif.getNumeroPisos().toString())) {
                JsfUti.messageInfo(null, "Debe ingresar solo Números", "");
                return;
            }
        }
        if (numEdificacion != null) {
            try {
                nuevDetalleEdif.setNumEdificacion(numEdificacion);
                if (numEdificacion == 0) {
                    nuevDetalleEdif.setDescripcion("Edificacion Principal");
                } else {
                    nuevDetalleEdif.setDescripcion("Anexo " + numEdificacion);
                }
                for (PePermisoCabEdificacion list : DetallesEdific) {
                    if (list.getNumEdificacion().compareTo(numEdificacion) == 0) {
                        edf = false;
                    }
                }
                if (edf) {
                    nuevDetalleEdif.setIdPermiso(permisoNuevo);
                    DetallesEdific.add(nuevDetalleEdif);
                    nuevDetalleEdif = new PePermisoCabEdificacion();
                    detallePermiso.setPorcentaje(new BigDecimal("0.00"));
                } else {
                    JsfUti.messageError(null, "Edificacion Ya fue agregada", "");
                }
                numEdificacion = null;
                calculoAreaConstruccion();
                JsfUti.update("forGenLiq:dtEdif");//dtEdif, edif
                JsfUti.update("forGenLiq:ad");
                JsfUti.update("forGenLiq:ac");
            } catch (Exception e) {
                Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public void eliminarEdif(PePermisoCabEdificacion edf) {
        try {
            permisoSelect = edf;
            int index = 0;
            int i = 0;
            for (PePermisoCabEdificacion list : DetallesEdific) {
                if (list.getNumEdificacion().compareTo(edf.getNumEdificacion()) == 0) {
                    index = i;
                }
                i++;
            }
            DetallesEdific.remove(index);
            if (permisoSelect.getPeDetallePermisoCollection() != null) {
                permisoSelect = null;
            }
            calculoAreaConstruccion();
        } catch (Exception e) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
        }
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
        try {
            if (detPer) {
                detallePermiso.setIdCatEdfProp(carateristicas);
                detallePermiso.setIdPermisoEdificacion(permisoSelect);
                PeDetallePermiso p = new PeDetallePermiso();
                p.setArea(detallePermiso.getArea());
                p.setIdCatEdfProp(detallePermiso.getIdCatEdfProp());
                p.setIdPermisoEdificacion(permisoSelect);
                p.setPorcentaje(detallePermiso.getPorcentaje());
                p.setEstado(true);
                permisoSelect.getPeDetallePermisoCollection().add(p);
//            lisPeDetallePermisos.add(p);
                carateristicas = new CatEdfProp();
                categoria = new CatEdfCategProp();
                detallePermiso = new PeDetallePermiso();
                detallePermiso.setPorcentaje(new BigDecimal("0.00"));
            } else {
                JsfUti.messageError(null, "Caracteristica ya fue agregada", "");
            }
            calculoAreaConstruccion();
        } catch (Exception e) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
        }
        JsfUti.update("forGenLiq:lisCartg");
        JsfUti.update("forGenLiq:lisCaract");
        JsfUti.update("forGenLiq:por");
        JsfUti.update("forGenLiq:dtCartEdif");
    }

    public void eliminarDetalleEdif(PeDetallePermiso dpermiso) {
        try {
            List<PeDetallePermiso> d = new ArrayList<>();
            for (PeDetallePermiso listCat1 : permisoSelect.getPeDetallePermisoCollection()) {
                if (listCat1.getIdCatEdfProp().getId().compareTo(dpermiso.getIdCatEdfProp().getId()) != 0) {
                    d.add(listCat1);
                }
            }
            permisoSelect.setPeDetallePermisoCollection(new ArrayList<PeDetallePermiso>());
            permisoSelect.getPeDetallePermisoCollection().addAll((Collection<PeDetallePermiso>) d);
            permisoSelect.getPeDetallePermisoCollection().size();
        } catch (Exception e) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
        }
        JsfUti.update("forGenLiq:dtCartEdif");
    }

    public void actulizarLista() {
        if (categoria.getId() != null) {
            lisCatEdfProp = permisoServices.getCatEdfPropList(categoria.getId());
            JsfUti.update("forGenLiq:lisCaract");
        }
    }

    public void calculoAreaConstruccion() {
        try {
            BigDecimal totArea = new BigDecimal(0);
            for (PePermisoCabEdificacion det : DetallesEdific) {
                totArea = totArea.add(det.getAreaConstruccion());
            }
            permisoNuevo.setAreaEdificaciones(totArea);
            if (permisoNuevo.getAreaParqueos() != null) {
                totArea = totArea.add((permisoNuevo.getAreaParqueos()));
            }
            permisoNuevo.setAreaConstruccion(totArea);
        } catch (Exception e) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
        }
        JsfUti.update("forGenLiq:ac");
    }

    public void onRowSelect(SelectEvent event) {
        lisPeDetallePermisos = (List<PeDetallePermiso>) permisoSelect.getPeDetallePermisoCollection();
        JsfUti.update("forGenLiq:dtCartEdif");
    }

    public void validar() {
//        if (!esRenovacion) {
            if (this.getFiles().isEmpty() || this.getFiles() == null) {
                JsfUti.messageError(null, "Debe Seleccionar el Plano de la Edificación", "");
                return;
            }
//        }
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
            JsfUti.messageError(null, "Faltan llenar algunos campos en la sección Datos de Permiso", "");
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
        if (listaPropietarios.isEmpty()) {
            JsfUti.messageError(null, "Debe Seleccionar un Propietario", "");
            return;
        }
//        if (permisoNuevo.getObservacion() == null) {
//            JsfUti.messageError(null, "La Observacion es Obligatorio", "");
//            return;
//        }
        
        JsfUti.update("frmConf");
        JsfUti.executeJS("PF('confCartaAdo').show()");

    }

    public void guardarPermiso() {
        if (observacion == null || observacion.trim().length() <= 0) {
            JsfUti.messageError(null, "La Observacion es Obligatorio", "");
            return;
        }
        permisoServices.actualizarHistoricoTramites(histTramite);
        histTramite = permisoServices.getHistoricoTramiteById(histTramite.getId());
        try {
//            if (!esRenovacion) {
                if (this.getFiles().isEmpty() || this.getFiles() == null) {
                    JsfUti.messageError(null, "Debe Seleccionar el Plano de la Edificación", "");
                    return;
                }
//            }
            if (permisoNuevo.getIdPredio().getId() == null) {
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
                JsfUti.messageError(null, "Faltan llenar algunos campos en la sección Datos de Permiso", "");
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
            if (listaPropietarios.isEmpty()) {
                JsfUti.messageError(null, "Debe Seleccionar un Propietario", "");
                return;
            }
//            if (permisoNuevo.getObservacion() == null) {
//                JsfUti.messageError(null, "La Observacion es Obligatorio", "");
//                return;
//            }
            if (predio.getCatPredioS4() == null) {
                JsfUti.messageError(null, "Predio sin datos de Caracteristicas Fisicas del Solar, Editar Predio", "");
                return;
            }
            if (predio.getCatPredioS4().getLocManzana() == null) {
                JsfUti.messageError(null, "Predio no tiene Localización en Manzana", "");
                return;
            }
            PeTipoPermiso tipoPermiso = permisoServices.getPeTipoPermisoByDesc(histTramite.getTipoTramiteNombre());
            if (tipoPermiso == null) {
                JsfUti.messageError(null, "Debe seleccionar el tipo de Permiso.", "");
                return;
            }

            observ = new Observaciones();

            observ = permisoServices.guardarActualizarObservacionProp(listaPropietariosEliminar, listaPropietarios,
                    histTramite, sess.getName_user(), observacion, this.getTaskDataByTaskID().getName());
            if (observ != null) {

                histTramite = permisoServices.getHistoricoTramiteById(histTramite.getId());

                if (permisoNuevo.getPropietarioPersona() == null) {
                    permisoNuevo.setPropietarioPersona(listaPropietarios.get(0).getEnte());
                }
                permisoNuevo.setEstado("A");
                permisoNuevo.setCedulaResponsableTecnico(respTec.getCiRuc());
                permisoNuevo.setIdPredio(predio);
                permisoNuevo.setTipoPermiso(tipoPermiso);
                permisoNuevo.setUsuarioCreador(new AclUser(sess.getUserId()));
                permisoNuevo.setFechaCreacion(new Date());
                permisoNuevo.setResponsable(nombreTec);
                permisoNuevo.setTecnicoRegistroProfesional(respTec.getRegProf());
                permisoNuevo.setMostrarCertificado(true);

                Calendar c = Calendar.getInstance();
                c.setTime(permisoNuevo.getFechaEmision());
                permisoNuevo.setAnioPermiso((short) c.get(Calendar.YEAR));
                permisoNuevo.setMostrarCertificado(false);
                permisoNuevo.setTramite(histTramite);
                permisoNuevo.setCartaAdosamiento(Utils.isNotEmpty(this.getCartaAdosamiento()));
                PePermiso p = permisoServices.guardarPePermiso(permisoNuevo, formulas);
                if (p != null) {
                    permisoNuevo = p;
                    permisoServices.guardarPePermisoCabEdificacionAndPeDetallePermiso(p, DetallesEdific, permisoSelect);

                    groovyUtil = new GroovyUtil(formulas.getFormula());
                    groovyUtil.setProperty("permiso", permisoNuevo);
                    histTramite.setValorLiquidacion(((BigDecimal) groovyUtil.getExpression("getValorLiquidacion", new Object[]{})));

                    if (divisonServices.actualizarHistoricoTramite(histTramite)) {
                        Hibernate.initialize(histTramite);
                        imprimir = true;
                    } else {
                        JsfUti.messageError(null, Messages.transacError, "");
                    }
                } else {
                    JsfUti.messageError(null, Messages.error, "");
                    return;
                }
            }
            paramt = new HashMap<>();
            guardado = false;
            JsfUti.update("forGenLiq");
        } catch (Exception ex) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void imprimirLiquidacion() {
        if (!permisoNuevo.getMostrarCertificado()) {
            try {
                HistoricoTramites ht = permisoServices.getHistoricoTramiteById(histTramite.getId());
                ms = new MsgFormatoNotificacion();
                Calendar cl = Calendar.getInstance();

                HistoricoReporteTramite vd = new HistoricoReporteTramite();
                vd.setCodValidacion(permisoNuevo.getNumReporte() + ht.getIdProceso());
                vd.setEstado(true);
                vd.setFecCre(cl.getTime());
                vd.setNombreReporte("LiquidacionTasasPermiso" + ht.getSolicitante().getCiRuc());
                vd.setNombreTarea(this.getTaskDataByTaskID().getTaskDefinitionKey());
                vd.setProceso(ht.getIdProceso());
                vd.setSecuencia(permisoNuevo.getNumReporte());
                vd.setTramite(ht);
                String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
                vd = divisonServices.guardarHistoricoReporteTramite(vd);

                AclUser firmaDirector = permisoServices.getAclUserByUser(tramite.getUserDireccion());
                AclUser firmaDir = (AclUser) EntityBeanCopy.clone(firmaDirector);

                if (vd != null) {
                    String codigoQR = SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + vd.getCodValidacion();
                    ss.instanciarParametros();
                    ss.agregarParametro("permiso", permisoNuevo.getId());
                    ss.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
                    ss.agregarParametro("SUBREPORT_DIR", path + "reportes/permisoConstruccion/");
                    ss.agregarParametro("firmaDirec", path + "css/firmas/" + firmaDir.getRutaImagen() + ".jpg");
                    ss.agregarParametro("firmaTecni", path + "css/firmas/" + new AclUser(sess.getUserId()).getRutaImagen() + ".jpg");
                    ss.agregarParametro("idUser", sess.getUserId());
                    ss.agregarParametro("validador", vd.getId().toString());
                    ss.agregarParametro("codigoQR", codigoQR);
                    ss.setNombreReporte("LiquidacionTasasPermiso");
                    ss.setTieneDatasource(true);
                    paramt.put("idReporte", vd.getId());
                    ss.setNombreSubCarpeta("permisoConstruccion");
                }

                ss.setReportePDF(reporte.generarPdf("reportes/permisoConstruccion/LiquidacionTasasPermiso.jasper", ss.getParametros()));

                paramt.put("prioridad", 50);
                paramt.put("archivo", ss.getReportePDF());
                paramt.put("carpeta", ht.getCarpetaRep());
                paramt.put("nombreArchivoByteArray", new Date().getTime() + ss.getNombreReporte() + ht.getSolicitante().getCiRuc() + ".pdf");
                paramt.put("tipoArchivoByteArray", "application/pdf");
                agregarCartaAdosamieto();
                paramt.put("listaArchivos", this.getFiles());
                paramt.put("listaArchivosFinal", new ArrayList<>());

                paramt.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
                paramt.put("to", ht.getCorreos());
                paramt.put("from", SisVars.correo);
                paramt.put("idProcess", session.getTaskID());
                paramt.put("tipo_PC", ht.getTipoTramiteNombre());
                permisoNuevo.setMostrarCertificado(true);

                if (!impresoLiquidacion) {
                    if (permisoServices.actualizarPePermiso(permisoNuevo)) {
                        this.completeTask(this.getTaskId(), paramt);
                    }
                }
                impresoLiquidacion = true;
                banTarea = true;
                imprimir = false;
//            ss.borrarDatos();
            } catch (Exception e) {
                Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        JsfUti.update("forGenLiq");
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public Observaciones getObserv() {
        return observ;
    }

    public void setObserv(Observaciones observ) {
        this.observ = observ;
    }

    public HashMap<String, Object> getParamt() {
        return paramt;
    }

    public void setParamt(HashMap<String, Object> paramt) {
        this.paramt = paramt;
    }

    public HistoricoTramites getHistTramite() {
        return histTramite;
    }

    public void setHistTramite(HistoricoTramites histTramite) {
        this.histTramite = histTramite;
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
        return DetallesEdific;
    }

    public void setDetallesEdific(List<PePermisoCabEdificacion> DetallesEdific) {
        this.DetallesEdific = DetallesEdific;
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

//    public Boolean getEsRenovacion() {
//        return esRenovacion;
//    }
//
//    public void setEsRenovacion(Boolean esRenovacion) {
//        this.esRenovacion = esRenovacion;
//    }

    public PdfReporte getReporte() {
        return reporte;
    }

    public void setReporte(PdfReporte reporte) {
        this.reporte = reporte;
    }

    public ReportesView getReportes() {
        return reportes;
    }

    public void setReportes(ReportesView reportes) {
        this.reportes = reportes;
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

    private void consultarIdPredio() {
        if (!histTramite.getHistoricoTramiteDetCollection().isEmpty()) {
            List<HistoricoTramiteDet> htd = (List<HistoricoTramiteDet>) histTramite.getHistoricoTramiteDetCollection();
            HistoricoTramiteDet histdet = htd.get(0);
            predio = permisoServices.getCatPredioById(histdet.getPredio().getId());
        }
        List<CatPredioPropietario> propietariosTemp = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();
        if (propietariosTemp != null) {
            if (!propietariosTemp.isEmpty()) {
                for (CatPredioPropietario temp : propietariosTemp) {
                    if ("A".equals(temp.getEstado())) {
                        listaPropietarios.add(temp);
                    }
                }
            }
        }
        permisoNuevo.setIdPredio(predio);
        if (predio.getCiudadela() != null) {
            permisoNuevo.setNombUrbanizacionImpresa(predio.getCiudadela().getNombre());
        } else {
            permisoNuevo.setNombUrbanizacionImpresa(null);
        }
    }

    public Boolean getBanTarea() {
        return banTarea;
    }

    public void setBanTarea(Boolean banTarea) {
        this.banTarea = banTarea;
    }

    public Boolean getGuardado() {
        return guardado;
    }

    public void setGuardado(Boolean guardado) {
        this.guardado = guardado;
    }

    public List<CatCiudadela> getCiudadelas() {
        return permisoServices.getFichaServices().getCiudadelas();
    }

}
