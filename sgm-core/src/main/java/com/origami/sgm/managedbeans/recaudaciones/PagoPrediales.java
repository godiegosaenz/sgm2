/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.recaudaciones;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.bpm.models.DatoSeguro;
import com.origami.sgm.bpm.models.PagoTituloReporteModel;
import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.CtlgSalario;
import com.origami.sgm.entities.EmisionesRuralesExcel;
import com.origami.sgm.entities.FnConvenioPago;
import com.origami.sgm.entities.FnConvenioPagoObservacion;
import com.origami.sgm.entities.FnExoneracionLiquidacion;
import com.origami.sgm.entities.FnRemisionSolicitud;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.RenEntidadBancaria;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenPago;
import com.origami.sgm.entities.RenTipoEntidadBancaria;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.historic.ValoracionPredial;
import com.origami.sgm.entities.models.NombreContribuyenteModel;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.CatPredioRusticoLazy;
import com.origami.sgm.lazymodels.EmisionesRuralesExcelLazy;
import com.origami.sgm.lazymodels.PropietariosLazy;
import com.origami.sgm.managedbeans.BusquedaPredios;
import com.origami.sgm.managedbeans.rentas.RemisionIntereses;
import com.origami.sgm.services.interfaces.catastro.AvaluosServices;
import com.origami.sgm.services.interfaces.datoSeguro.DatoSeguroServices;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.sgm.services.interfaces.financiero.RemisionInteresServices;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.lucene.util.fst.Util;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.UnselectEvent;
import util.Faces;
import util.JsfUti;
import util.Messages;
import util.Utils;
import util.VerCedulaUtils;

/**
 *
 * @author HenryPilco
 */
@Named
@ViewScoped
public class PagoPrediales extends BusquedaPredios implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    protected Entitymanager manager;
    @javax.inject.Inject
    protected RecaudacionesService recaudacion;
    @Inject
    protected ServletSession ss;
    @javax.inject.Inject
    private RentasServices servicesRentas;
    @Inject
    protected UserSession session;

    protected Long anioEmision;
    protected CtlgSalario salario;
    protected List<RenDetLiquidacion> rubrosEmision = new ArrayList<>();
    protected RenDetLiquidacion rubro;
    protected RenTipoLiquidacion tipoLiquidacion;

    protected RenLiquidacion emisionSeleccionada;
    protected AclUser usuario;
    protected List<RenLiquidacion> emisionesACobrar;
    protected List<RenEntidadBancaria> bancos;
    protected List<RenEntidadBancaria> tarjetas;
    protected RenPago pagoCoactiva;
    protected List<CatParroquia> parroquiasRurales;
    private CatPredioRustico predioRustico;

    protected List<CatCiudadela> ciudadelas;

    protected String numCertificado;
    protected Long tipoCertificado;
    protected String nombreComprador;
    private String nombreContribuyente;
    private List<NombreContribuyenteModel> modelNombresList;
    protected Boolean liquidador = false;
    private CatPredioPropietario propietario;
    private CatEnte ente;

    protected Map<String, Object> paramt;

    private List<RenLiquidacion> emisionesPredialesTemp;

    private Boolean renderContextMenu = Boolean.FALSE;

    @javax.inject.Inject
    protected AvaluosServices avaluos;
    protected ValoracionPredial valoracion;
    protected Boolean normal, tipoCert;

    protected String nombresFox, ubicacion, codCatastralRural;
    protected EmisionesRuralesExcel predioUrbanoFox;
    protected Boolean isFox = Boolean.FALSE;
    private List<EmisionesRuralesExcel> prediosUrbanosFox;
    protected EmisionesRuralesExcelLazy emisionesRuralesExcelLazy;

    protected CatEnte registroEnte = new CatEnte();
    protected CatEnte comprador;

    private Integer tipoEnte;
    protected CatEnteLazy solicitantes;

    @javax.inject.Inject
    private DatoSeguroServices datoSeguroSeguro;

    protected Boolean excepcionalEmpresa = Boolean.FALSE;

    /**
     * VARIABLES PARA CONSULTAR EN EL AME
     *
     */
    private String claveCatastralAnterior = "132250";
    private String nombresPropietariosAME;

    private String detalle, nombresTesorera, nombresPropietarios;
    private Boolean esPagoCuota, esPagoCuotaCoactiva;

    private FnConvenioPago convenioPago;

    @Inject
    protected RemisionInteresServices remisionInteresServices;

    @PostConstruct
    public void initView() {
        try {
            if (session != null) {
                setPropietarios(new PropietariosLazy());
                setPropietariosRustico(new CatPredioRusticoLazy(Boolean.TRUE));
                paramt = new HashMap<>();
                paramt.put("estado", Boolean.TRUE);
                paramt.put("tipo", new RenTipoEntidadBancaria(1L));
                bancos = manager.findObjectByParameterList(RenEntidadBancaria.class, paramt);
                paramt.put("tipo", new RenTipoEntidadBancaria(2L));
                tarjetas = manager.findObjectByParameterList(RenEntidadBancaria.class, paramt);
                parroquiasRurales = manager.findAllEntCopy(Querys.parroquiasRurales);
                anioEmision = new Long(Calendar.getInstance().get(Calendar.YEAR));
                paramt = new HashMap<>();
                paramt.put("usuario", session.getName_user());
                usuario = (AclUser) manager.findObjectByParameter(AclUser.class, paramt);
                ciudadelas = manager.findAllObjectOrder(CatCiudadela.class, new String[]{"nombre"}, Boolean.TRUE);
                emisionesRuralesExcelLazy = new EmisionesRuralesExcelLazy();
                predioUrbanoFox = new EmisionesRuralesExcel();
                habilitarRecalculo();
            }
        } catch (Exception e) {
            Logger.getLogger(PagoPrediales.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void generateCertByContribuyente() {
        GeDepartamento tesoreria;
        try {
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.FALSE);
            ss.setNombreSubCarpeta("certificados");
            ss.setNombreReporte("certificadoNoAdeudarImpuestoPredial");
            ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
            ss.agregarParametro("LOGO2", JsfUti.getRealPath(SisVars.sisLogo1));
            ss.agregarParametro("LOGO_FOOTER", JsfUti.getRealPath(SisVars.sisLogo1));
            ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/"));
            ss.agregarParametro("NOMBRE_EMPRESA", SisVars.NOMBREMUNICIPIO);
            ss.agregarParametro("CERTIFICADO", numCertificado);
            tesoreria = manager.find(GeDepartamento.class, 20L);//ID: 20 Departamento de Tesoreria
            for (AclRol rol : tesoreria.getAclRolCollection()) {
                if (rol.getEsSubDirector()) {//ROL DE TESORERA
                    for (AclUser user : rol.getAclUserCollection()) {
                        ss.agregarParametro("TESORERA", user.getEnte() == null ? "REGISTRAR DATOS" : (Utils.isEmpty(user.getEnte().getTituloProf()) + " " + user.getEnte().getNombres() + " " + user.getEnte().getApellidos()));
                        break;
                    }
                    break;
                }
            }
            if (tipoCertificado.equals(1L)) {
                for (CatPredioPropietario p : getPredioConsulta().getCatPredioPropietarioCollection()) {
                    ss.agregarParametro("PROPIETARIO", p.getEnte().getEsPersona() ? p.getEnte().getApellidos() + " " + p.getEnte().getNombres() : p.getEnte().getRazonSocial());
                    break;
                }
                ss.agregarParametro("NUM_PREDIO", getPredioConsulta().getNumPredio().toString());
                ss.agregarParametro("CODIGO_CATASTRAL", getPredioConsulta().getCodigoPredialCompleto());
                ss.agregarParametro("CIUDADELA", getPredioConsulta().getCiudadela().getNombre());
                ss.agregarParametro("TITULO_REPORTE", 13L);
            }
            if (tipoCertificado.equals(2L)) {
                ss.agregarParametro("PROPIETARIO", getPredioRuralConsulta().getPropietario() != null ? (getPredioRuralConsulta().getPropietario().getEsPersona() ? getPredioRuralConsulta().getPropietario().getApellidos() + " " + getPredioRuralConsulta().getPropietario().getNombres() : getPredioRuralConsulta().getPropietario().getRazonSocial()) : "");
                ss.agregarParametro("CODIGO_CATASTRAL", getPredioRuralConsulta().getRegCatastral() + "-" + getPredioRuralConsulta().getIdPredial() + "-" + getPredioRuralConsulta().getParroquia().getDescripcion());
                ss.agregarParametro("TITULO_REPORTE", 7L);
            }

            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "/Documento");
        } catch (Exception e) {
            Logger.getLogger(PagoPrediales.class.getName()).log(Level.OFF, null, e);
        }
    }

    public void seleccionar() {
        if (this.comprador == null) {

            Faces.messageWarning(null, "Advertencia", "Debe seleccionar un solicitante del listado");

        } else {
            openDialogCertificadoContribuyenteNoRegistrado();
            Faces.messageInfo(null, "Mensaje", "Contribuyente seleccionado.");
            JsfUti.executeJS("PF('dlgSolicitanteCertificado').hide();");
        }
    }

    public void seleccionarRural() {
        if (this.comprador == null) {
            Faces.messageWarning(null, "Advertencia", "Debe seleccionar un solicitante del listado");

        } else {
            openDialogCertificadoRural();
            Faces.messageInfo(null, "Mensaje", "Contribuyente seleccionado.");
            JsfUti.executeJS("PF('dlgSolicitanteCertificado').hide();");
        }
    }

    public void setNombreContribuyente() {
        String[] nombres;
        Integer cont = 0;
        NombreContribuyenteModel model = new NombreContribuyenteModel();
        modelNombresList = new ArrayList();

        if (emisionSeleccionada.getComprador() != null) {
            nombreContribuyente = emisionSeleccionada.getComprador().getNombreCompleto();
        } else {
            nombreContribuyente = emisionSeleccionada.getNombreComprador();
        }

        if (emisionSeleccionada.getNombreCompradorHistoric() != null) {
            nombres = emisionSeleccionada.getNombreCompradorHistoric().split(";");

            for (String temp : nombres) {
                if (cont % 3 == 0) {
                    model = new NombreContribuyenteModel();
                    model.setUsername(temp);
                }
                if (cont % 3 == 1) {
                    model.setFecha(temp);
                }
                if (cont % 3 == 2) {
                    model.setNombre(temp);
                    modelNombresList.add(model);
                }
                cont++;
            }
        }
    }

    public void actualizarContribuyente() {
        try {

            String nom = emisionSeleccionada.getComprador() == null ? emisionSeleccionada.getNombreComprador() == null ? "" : emisionSeleccionada.getNombreComprador().toUpperCase() : emisionSeleccionada.getComprador().getNombreCompleto().toUpperCase();
            if (nombreContribuyente != null) {

                emisionSeleccionada.setComprador(null);
                emisionSeleccionada.setNombreComprador(nombreContribuyente.toUpperCase());
                if (emisionSeleccionada.getNombreCompradorHistoric() == null) {
                    emisionSeleccionada.setNombreCompradorHistoric(session.getName_user() + ";" + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()) + ";" + nom + ";");
                } else {
                    emisionSeleccionada.setNombreCompradorHistoric(emisionSeleccionada.getNombreCompradorHistoric() + session.getName_user() + ";" + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()) + ";" + nombreContribuyente.toUpperCase() + ";");
                }
                emisionSeleccionada = (RenLiquidacion) manager.persist(emisionSeleccionada);
                for (RenPago p : emisionSeleccionada.getRenPagoCollection()) {
                    p.setContribuyente(null);
                    p.setNombreContribuyente(nombreContribuyente.toUpperCase());
                }
                setControlDocumento(Boolean.FALSE);
            } else {
                JsfUti.messageInfo(null, "Info", "No ha ingresado el nombre del contribuyente o Subido el Documento");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void seleccionarPredio(Long tipoPredio) {
        try {
            switch (tipoPredio.intValue()) {
                case 1: // PREDIO URBANO INDIVIDUAL 
                    if (getPredioConsulta() != null) {
                        paramt = new HashMap<>();
                        paramt.put("tipoLiquidacion", new RenTipoLiquidacion(13L));
                        paramt.put("predio", getPredioConsulta());
                        paramt.put("estadoLiquidacion", new RenEstadoLiquidacion(2L));
                        emisionesPredialesTemp = manager.findObjectByParameterOrderList(RenLiquidacion.class, paramt, new String[]{"anio"}, Boolean.TRUE);
                        setEmisionesPrediales(emisionesPredialesTemp);
                        calculoTotalPago(getEmisionesPrediales(), null);
                        JsfUti.executeJS("PF('dlgPrediosConsulta').hide();");
                    } else {
                        JsfUti.messageInfo(null, "Mensaje", "Seleccione un predio, luego clic en Seleccionar");
                    }
                    break;
                case 2: // PREDIO RURAL INDIVIDUAL
                    if (getPredioRuralConsulta() != null) {
                        paramt = new HashMap<>();
                        paramt.put("tipoLiquidacion", new RenTipoLiquidacion(7L));
                        paramt.put("predioRustico", getPredioRuralConsulta());
                        paramt.put("estadoLiquidacion", new RenEstadoLiquidacion(2L));
                        emisionesPredialesTemp = manager.findObjectByParameterOrderList(RenLiquidacion.class, paramt, new String[]{"anio"}, Boolean.TRUE);
                        setEmisionesPrediales(emisionesPredialesTemp);
                        calculoTotalPago(getEmisionesPrediales(), null);
                        JsfUti.executeJS("PF('dlgPrediosRuralConsulta').hide();");
                    } else {
                        JsfUti.messageInfo(null, "Mensaje", "Seleccione un predio, luego clic en Seleccionar");
                    }
                    break;
                case 3: // TODAS LA EMISIONES URBANAS QUE TENGA EL CONTRIBUYENTE 
                    List<RenLiquidacion> tempEmisiones = new ArrayList<RenLiquidacion>();
                    for (CatPredio catPredio : this.getPrediosConsulta()) {
                        paramt = new HashMap<>();
                        paramt.put("tipoLiquidacion", new RenTipoLiquidacion(13L));
                        paramt.put("predio", catPredio);
                        paramt.put("estadoLiquidacion", new RenEstadoLiquidacion(2L));
                        emisionesPredialesTemp = manager.findObjectByParameterOrderList(RenLiquidacion.class, paramt, new String[]{"anio"}, Boolean.TRUE);
                        if (Utils.isNotEmpty(emisionesPredialesTemp)) {
                            tempEmisiones.addAll(emisionesPredialesTemp);
                        }
                    }
                    setEmisionesPrediales(tempEmisiones);
                    calculoTotalPago(getEmisionesPrediales(), null);
                    JsfUti.executeJS("PF('dlgPrediosConsulta').hide();");
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            Logger.getLogger(PagoPrediales.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void openDialogCertificado() {
        getDataTesorera();
        getNombresPropietarios();
        this.detalle = "<div style=\"text-align: justify;\"> <b> " + this.nombresTesorera + ", TESORERO(A) DEL GOBIERNO AUTÓNOMO DESCENTRALIZADO MUNICIPAL DEL CANTÓN SAN VICENTE, </b> EN LEGAL USO DE SUS FUNCIONES, <b>CERTIFICA </b> QUE EL SEÑOR(A) <b> " + this.nombresPropietarios + "</b>, NO ES DEUDOR (A) DE ESTA MUNICIPALIDAD POR CONCEPTO DE IMPUESTOS PREDIALES. </div>";
        JsfUti.executeJS("PF('dlgCertificadoSv').show();");
        JsfUti.update("formCertificadoSv");
    }

    public void openDialogCertificadoContribuyenteNoRegistrado() {
        getDataTesorera();
        getNombresContribuyenteNoAfiliado();
        this.detalle = "<div style=\"text-align: justify;\"> <b> " + this.nombresTesorera + ", TESORERO(A) DEL GOBIERNO AUTÓNOMO DESCENTRALIZADO MUNICIPAL DEL CANTÓN SAN VICENTE, </b> EN LEGAL USO DE SUS FUNCIONES, <b>CERTIFICA </b> QUE EL SEÑOR(A) <b> " + this.nombresPropietarios + "</b>, NO REGISTRA EN EL SISTEMA COMO CONTRIBUYENTE ALGUNO, POR TANTO NO ES DEUDOR (A) DE ESTA MUNICIPALIDAD. </div>";
        JsfUti.executeJS("PF('dlgCertificadoSv').show();");
        JsfUti.update("formCertificadoSv");
        this.tipoCert = Boolean.TRUE;
    }

    public void openDialogCertificadoRural() {
        ubicacion = "";
        codCatastralRural = "";
        getDataTesorera();
        getNombresContribuyenteNoAfiliado();
        this.detalle = "<div style=\"text-align: justify;\"> <b> " + this.nombresTesorera + ", TESORERO(a) DEL GOBIERNO AUTÓNOMO DESCENTRALIZADO MUNICIPAL DEL CANTÓN SAN VICENTE, </b> EN LEGAL USO DE SUS FUNCIONES, <b>CERTIFICA </b> QUE EL SEÑOR(A) <b> " + this.nombresPropietarios + "</b>, NO ES DEUDOR (A) DE ESTA MUNICIPALIDAD POR CONCEPTO DE IMPUESTOS PREDIALES. </div>";
        JsfUti.executeJS("PF('dlgCertificadoRuralSv').show();");
        JsfUti.update("formCertificadoRuralSv");
        this.tipoCert = Boolean.TRUE;
    }

    public void generarCertificadoNoContribuyente() {
        try {
            GeDepartamento tesoreria;
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("certificados");
            ss.setNombreReporte("certificadoNoAdeudaImpuestoPredialSanVicenteNoContribuyente");
            ss.agregarParametro("NOMBRE_EMPRESA", SisVars.NOMBREMUNICIPIO);
            ss.agregarParametro("CERTIFICADO", numCertificado);
            ss.agregarParametro("DETALLE", this.detalle);
            tesoreria = manager.find(GeDepartamento.class, 20L);//ID: 20 Departamento de Tesoreria
            for (AclRol rol : tesoreria.getAclRolCollection()) {
                if (rol.getEsSubDirector()) {//ROL DE TESORERA
                    for (AclUser user : rol.getAclUserCollection()) {
                        ss.agregarParametro("TESORERA", user.getEnte() == null ? "REGISTRAR DATOS" : (Utils.isEmpty(user.getEnte().getTituloProf()) + " " + user.getEnte().getNombres() + " " + user.getEnte().getApellidos()));
                        break;
                    }
                    break;
                }
            }
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "/Documento");
        } catch (Exception e) {
            Logger.getLogger(PagoPrediales.class.getName()).log(Level.OFF, null, e);
        }
    }

    public void generateCertRural() {
        GeDepartamento tesoreria;
        try {
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("certificados");
            ss.setNombreReporte("certificadoNoAdeudaImpuestoPredialSanVicenteRural");
            ss.agregarParametro("NOMBRE_EMPRESA", SisVars.NOMBREMUNICIPIO);
            ss.agregarParametro("CERTIFICADO", numCertificado);
            ss.agregarParametro("DETALLE", this.detalle);
            tesoreria = manager.find(GeDepartamento.class, 20L);//ID: 20 Departamento de Tesoreria
            for (AclRol rol : tesoreria.getAclRolCollection()) {
                if (rol.getEsSubDirector()) {//ROL DE TESORERA
                    for (AclUser user : rol.getAclUserCollection()) {
                        ss.agregarParametro("TESORERA", user.getEnte() == null ? "REGISTRAR DATOS" : (Utils.isEmpty(user.getEnte().getTituloProf()) + " " + user.getEnte().getNombres() + " " + user.getEnte().getApellidos()));
                        break;
                    }
                    break;
                }
            }
            ss.agregarParametro("CODIGO_CATASTRAL", this.codCatastralRural);
            ss.agregarParametro("UBICACION", this.ubicacion);
            ss.agregarParametro("TITULO_REPORTE", 13L);

            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "/Documento");
        } catch (Exception e) {
            Logger.getLogger(PagoPrediales.class.getName()).log(Level.OFF, null, e);
        }
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public void aplicarExoneracion() {
        List<FnExoneracionLiquidacion> exoneraciones = null;
        List parametros;
        RenLiquidacion liqPost;
        BigDecimal posterior = BigDecimal.ZERO, original = BigDecimal.ZERO;
        try {
            //CONULTA SI EL PREDIO YA FUE EXONERADO (ESTADO LIQ: 4) --HENRY
            if (manager.find(QuerysFinanciero.getRenLiquidacionesByPredioYTipoLiquidacionExon, new String[]{"predio", "anio", "idTipoLiquidacion"}, new Object[]{getExoneracion().getPredio(), Utils.getAnio(new Date()), manager.find(RenTipoLiquidacion.class, 13L)}) == null) {
                //A PARTIR DEL 2014 SE APLICAN LAS EXONERACIONES O UN MAXIMO DE 3 ANIOS???
                getExoneracion().setAnioInicio(getExoneracion().getAnioInicio() < 2014 ? 2014 : getExoneracion().getAnioInicio());
                getExoneracion().setAnioFin(Utils.getAnio(new Date())); //SE ACTUALIZA LA SOLICITUD ORIGINAL CON EL ANIO ACTUAL
                //METODO QUE REALIZA LA EXONERACION (EJB)
                exoneraciones = servicesRentas.aplicarExoneracion(null, this.getExoneracion(), session.getName_user());
            }
            //SI NO INGRESA EN LA CONDICION ANTERIOR TAMPOCO INGRESA A ESTA --HENRY
            if (exoneraciones != null && !exoneraciones.isEmpty()) {
                FnExoneracionLiquidacion exo = exoneraciones.get(exoneraciones.size() - 1);
                parametros = new ArrayList<>();

                for (FnExoneracionLiquidacion t : exoneraciones) {
                    original = original.add(t.getLiquidacionOriginal().getTotalPago());
                    posterior = posterior.add(t.getLiquidacionPosterior().getTotalPago());
                    t.getLiquidacionPosterior().setComprador(t.getLiquidacionOriginal().getComprador());
                    t.getLiquidacionPosterior().setNombreComprador(t.getLiquidacionOriginal().getNombreComprador());
                    t.getLiquidacionPosterior().setObservacion(getMensajeExoneracion());
                    t.getLiquidacionPosterior().setTotalPago(t.getLiquidacionPosterior().getTotalPago().setScale(2, RoundingMode.HALF_UP));
                    t.getLiquidacionPosterior().setCoactiva(false);
                    t.getLiquidacionPosterior().setUsuarioIngreso(session.getName_user());
                    this.emisionSeleccionada = t.getLiquidacionPosterior();
                    manager.persist(t.getLiquidacionPosterior());
                }

                ss.instanciarParametros();

                ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
                ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/recaudaciones/").concat("/"));
                ss.agregarParametro("FECHA", exo.getFechaIngreso());
                ss.agregarParametro("IMP_ORIG", original.setScale(2, BigDecimal.ROUND_HALF_UP));
                ss.agregarParametro("DIFERENCIA", original.subtract(posterior).setScale(2, BigDecimal.ROUND_HALF_UP));
                ss.agregarParametro("IMP_NEW", posterior.setScale(2, BigDecimal.ROUND_HALF_UP));
                ss.agregarParametro("ID_SOLICITUD", exo.getId());
                ss.setTieneDatasource(Boolean.TRUE);
                ss.setNombreSubCarpeta("recaudaciones");
                ss.setNombreReporte("formulario_exoneracion_master");
            } else {
                JsfUti.messageInfo(null, "Info", "Ya tiene aplicada una exoneración");
                return;
            }
            setExoneracion(null);
            Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChangeTab(TabChangeEvent event) {
        setTabName(event.getTab().getId().toString());
        emisionesPredialesTemp = new ArrayList<>();
        if (event.getTab().getId().equals("tabPagoPredial") || event.getTab().getId().equals("pagoPredialRural")) {
            setEmisionesPrediales(null);
            setPredioModel(new CatPredioModel());
        } else {
            setPredioModel(new CatPredioModel());
        }
    }

    @Override
    public void onChangeRadio() {
        setEmisionesPrediales(null);
        setPredioModel(new CatPredioModel());
        setPredioConsulta(null);
        setTotalEmisiones(new BigDecimal("0.00"));
    }

    /**
     * Tipo 1: Urbano, Tipo 2: Rural
     *
     * @param tipo
     */
    public void seleccionarEmision(Long tipo) {
        if (emisionesPredialesTemp == null || emisionesPredialesTemp.isEmpty()) {
            calculoTotalPago(getEmisionesPrediales(), null);
        } else {

            emisionesACobrar = new ArrayList<>();
            emisionesACobrar.clear();

            paramt = new HashMap<>();
            if (tipo == 1L) {
                for (RenLiquidacion liq : emisionesPredialesTemp) {
                    paramt.put("anio", liq.getAnio());
                    for (RenLiquidacion l : recaudacion.getEmisionesByPredio(liq.getPredio(), paramt)) {
                        if (l != null) {
                            emisionesACobrar.add(l);
                        }
                    }
                }
            }
            if (tipo == 2L) {
                for (RenLiquidacion liq : emisionesPredialesTemp) {
                    paramt.put("anio", liq.getAnio());
                    for (RenLiquidacion l : recaudacion.getEmisionesByPredioRustico(liq.getPredioRustico(), paramt)) {
                        if (l != null) {
                            emisionesACobrar.add(l);
                        }
                    }
                }
            }
            if (tipo == 3L) {
                List<RenLiquidacion> liquidacions;
                for (RenLiquidacion liq : emisionesPredialesTemp) {
                    paramt.put("anio", liq.getAnio());
                    liquidacions = recaudacion.getEmisionesByPredioRustico2017(liq.getRuralExcel(), paramt);
                    System.out.println("liquidacions" + liquidacions);
                    if (liquidacions != null) {
                        if (!liquidacions.isEmpty()) {
                            for (RenLiquidacion l : liquidacions) {
                                System.out.println("RenLiquidacion" + l);
                                if (l != null) {
                                    emisionesACobrar.add(l);
                                }
                            }
                        }
                    }

                }
            }
            if (tipo == 4L) {
                List<RenLiquidacion> liquidacions;
                for (RenLiquidacion liq : emisionesPredialesTemp) {
                    liquidacions = recaudacion.getEmisionesByAME(liq.getClaveAME(), liq.getAnio());
                    if (liquidacions != null) {
                        if (!liquidacions.isEmpty()) {
                            for (RenLiquidacion l : liquidacions) {
                                if (l != null) {
                                    emisionesACobrar.add(l);
                                }
                            }
                        }
                    }

                }
            }

            if (emisionesPredialesTemp.size() == 1) {
                this.setEmisionSeleccionada(new RenLiquidacion());
                this.setEmisionSeleccionada(emisionesPredialesTemp.get(0));
                //System.out.println("EMISION SELECCIONADA  " + emisionSeleccionada);
                renderContextMenu = true;
            }
            if (emisionesPredialesTemp.size() != 1) {
                this.setEmisionSeleccionada(null);
                renderContextMenu = false;
            }

            //elimina valores repetidos
            Set<RenLiquidacion> liquidacionCobro = new HashSet<>();
            liquidacionCobro.addAll(emisionesACobrar);
            emisionesACobrar.clear();
            emisionesACobrar.addAll(liquidacionCobro);

            //  Collections.sort((ArrayList) emisionesACobrar);
            calculoTotalPago(emisionesACobrar, null);

            JsfUti.update("mainForm");

        }
    }

    public void openDlgConvenio() {

        Map<String, List<String>> params = new HashMap<>();
        List<String> p = new ArrayList<>();

        p = new ArrayList<>();
        p.add("1");
        params.put("nuevo", p);

        p = new ArrayList<>();
        p.add(totalEmisiones.toString());
        params.put("deudaInicial", p);
        p = new ArrayList<>();
        p.add("1");
        params.put("calculaInteres", p);
        if (!emisionesPredialesTemp.isEmpty()) {
            p = new ArrayList<>();
            if (emisionesPredialesTemp.get(emisionesPredialesTemp.size() - 1).getComprador() != null) {
                p.add(emisionesPredialesTemp.get(emisionesPredialesTemp.size() - 1).getComprador().getId().toString());
            } else {
                JsfUti.messageError(null, "Error", "Debe Actualizar los Datos del Conribuyente de los Años Anteriores");
                return;
            }
            params.put("contribuyente", p);
        }

        p = new ArrayList<>();
        p.add(remisionInteresServices.aplicaRemisionPagoCuotaInicial(emisionesPredialesTemp).toString());
        params.put("aplicaRemision", p);

        Map<String, Object> options = new HashMap<>();
        options.put("resizable", true);
        options.put("draggable", true);
        options.put("modal", true);
        options.put("closable", true);
        RequestContext.getCurrentInstance().openDialog("/resources/dialog/dlgConvenioPago", options, params);

    }

    public void procesarPago() {
        try {
            if (getEmisionesPrediales() != null) {
                ///COMPARA LOS AVALUOS DEL ANIO ACTUAL VSEL ANIO PASADO 
                //EL OBJETIVO DE ESTO ES PORQUE SE SUPONE QUE CADA ANIO L AVALUO DE LA TIERRA AUMENTA
                BigDecimal avaluoAnterior = BigDecimal.ZERO, avaluoActual = BigDecimal.ZERO;
                Integer anioActual = Utils.getAnio(new Date());
                Integer anioPasado = anioActual - 1;
                if (emisionesPredialesTemp != null) {
                    if (!emisionesPredialesTemp.isEmpty()) {
                        for (RenLiquidacion l : emisionesPredialesTemp) {
                            if (l.getAnio() > Utils.getAnio(new Date())) {
                                JsfUti.messageError(null, "Error", "Solo se puede realizar pago de amisión hasta el año actual.");
                                return;
                            }

                        }
                        for (RenLiquidacion l : getEmisionesPrediales()) {
                            if (Objects.equals(l.getAnio(), anioActual)) {
                                avaluoActual = l.getAvaluoMunicipal();
                            }
                            if (Objects.equals(l.getAnio(), anioPasado)) {
                                avaluoAnterior = l.getAvaluoMunicipal();
                            }
                        }
                        if (avaluoAnterior.compareTo(BigDecimal.ZERO) == 0) {

                        } else {
                            if (avaluoActual.compareTo(avaluoAnterior) == -1 && !emisionesPredialesTemp.get(0).getPredio().getEsAvaluoVerificado()) {
                                JsfUti.messageError(null, "Advertencia", "El avaluo del año Anterior fue mayor al año Actual, Verificar datos en el Departamento de Catastro");
                                return;
                            }
                        }

                        emisionesACobrar = new ArrayList<>();
                        paramt = new HashMap<>();
                        for (RenLiquidacion liq : emisionesPredialesTemp) {
                            paramt.put("anio", liq.getAnio());

                            if (liq.getTipoLiquidacion().getId() == 13L) {
                                if (liq.getRuralExcel() != null) {
                                    isFox = Boolean.TRUE;
                                    for (RenLiquidacion l : recaudacion.getEmisionesByPredioRustico2017(liq.getRuralExcel(), paramt)) {
                                        if (l != null) {
                                            emisionesACobrar.add(l);
                                        }
                                    }

                                } else {
                                    if (liq.getPredio() != null) {
                                        for (RenLiquidacion l : recaudacion.getEmisionesByPredio(liq.getPredio(), paramt)) {
                                            if (l != null) {
                                                emisionesACobrar.add(l);
                                            }
                                        }
                                    } else {
                                        for (RenLiquidacion l : recaudacion.getEmisionesByAME(liq.getClaveAME(), liq.getAnio())) {
                                            if (l != null) {
                                                emisionesACobrar.add(l);
                                            }
                                        }
                                    }

                                }

                            }

                            if (liq.getTipoLiquidacion().getId() == 7L) {
                                for (RenLiquidacion l : recaudacion.getEmisionesByPredioRustico(liq.getPredioRustico(), paramt)) {
                                    if (l != null) {
                                        emisionesACobrar.add(l);
                                    }
                                }
                            }
                        }
                        if (emisionesACobrar != null && !emisionesACobrar.isEmpty()) {
                            Set<RenLiquidacion> liquidacionCobro = new HashSet<>();
                            liquidacionCobro.addAll(emisionesACobrar);
                            emisionesACobrar.clear();
                            emisionesACobrar.addAll(liquidacionCobro);

                            calculoTotalPago(emisionesACobrar, null);
                            JsfUti.update("formProcesar");
                            JsfUti.executeJS("PF('dlgProcesar').show();");
                        } else {
                            JsfUti.messageInfo(null, "Mensaje", "No posee deuda.");

                        }
                    }
                } else {
                    JsfUti.messageInfo(null, "Mensaje", "Debe seleccionar la emision a cancelar.");
                }
            } else {
                JsfUti.messageInfo(null, "Mensaje", "Debe realizar la Busqueda.");
            }
        } catch (Exception e) {
            Logger.getLogger(PagoPrediales.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void realizarPago() {
        List<RenPago> pagos = new ArrayList<>();
        RenPago pago;
        pagoCoactiva = null;
        try {
            if (emisionesACobrar.size() > 1) {
                //VERIFICAR QUE SOLO SE REALIZE UN TIPO DE PAGO DE LOS 5 QUE EXISTEN                
                if (getModelPago().cantidadTipoPagos() == 1) {//VERIFICAR QUE SE REALIZE TOTOAL PAGO DE LaS EMISIONES SELECCIONADAS
                    if (getModelPago().getValorTotal().compareTo(getTotalEmisiones()) == 0) {
                        RenLiquidacion temp = recaudacion.realizarPagosCoactiva(emisionesACobrar, session.getName_user());
                        if (temp != null) {
                            temp.setPagoFinal(temp.getTotalPago());
                            /*pagoCoactiva = recaudacion.realizarPago(temp, modelPago.realizarPago(temp), usuario);
                             if (pagoCoactiva != null) {
                             this.comprobanteCoactiva();
                             }*/
                            pagoCoactiva = recaudacion.realizarPago(temp, getModelPago().realizarPago(temp), usuario, true);
                        }
                        for (RenLiquidacion e : emisionesACobrar) {
                            //e = recaudacion.realizarDescuentoRecargaInteresPredial(e,null);
                            e.calcularPago();
                            //e = recaudacion.realizarPago(e, modelPago.realizarPago(e),usuario);
                            pago = recaudacion.realizarPago(e, getModelPago().realizarPago(e), usuario, true);
                            if (pago != null) {
                                pagos.add(pago);
                            }
                        }
                        if (pagoCoactiva != null) {
                            pagos.add(pagoCoactiva);
                        }
                    } else {
                        JsfUti.messageInfo(null, "Verifique el valor a cobrar", "El cobro de Emisiones multiples debe ser cancelado Totalmente");
                    }
                } else {
                    JsfUti.messageInfo(null, "Verifique Tipo de Pagos", "El cobro de Emisiones multiples solo permite un Tipo de Pago");
                }
            } else {
                if (getModelPago().getValorTotal().compareTo(getTotalEmisiones()) <= 0) {
                    if (getModelPago().getValorTotal().compareTo(BigDecimal.ZERO) >= 0) {
                        for (RenLiquidacion l : emisionesACobrar) {
                            //l = recaudacion.realizarDescuentoRecargaInteresPredial(l,getModelPago().getPagoTransferencia().getFecha());
                            l.calcularPago();
                            //l=recaudacion.realizarPago(l, getModelPago().realizarPago(l),usuario);
                            if (getModelPago().getValorTotal().compareTo(BigDecimal.ZERO) > 0) {
                                if (l.calculoMinimoPago(getModelPago().getValorTotal())) {
                                    break;
                                }
                            }

                            if (l.getEstadoCoactiva() != null && l.getEstadoCoactiva() == 2) {
                                //modelPago.calculoValoresPago();
                                //RenLiquidacion temp = recaudacion.realizarUnPagoCoactiva(l, modelPago.getValorCoactiva(), session.getName_user());
                                RenLiquidacion temp = recaudacion.realizarUnPagoCoactiva(l, recaudacion.valorRecaudarCoactiva(getModelPago().getValorTotal()), session.getName_user());
                                if (temp != null) {
                                    temp.setPagoFinal(temp.getTotalPago());
                                    /*pagoCoactiva = recaudacion.realizarPago(temp, getModelPago().realizarPago(temp), usuario);
                                     if (pagoCoactiva != null) {
                                     this.comprobanteCoactiva();
                                     }*/
                                    pagoCoactiva = recaudacion.realizarPago(temp, getModelPago().realizarPago(temp), usuario, true);
                                }
                            }
                            pago = recaudacion.realizarPago(l, getModelPago().realizarPago(l), usuario, true);
                            if (pago != null) {
                                pagos.add(pago);
                            }
                            if (pagoCoactiva != null) {
                                pagos.add(pagoCoactiva);
                            }
                        }
                        if (pagos.isEmpty()) {
                            JsfUti.messageInfo(null, "Verifique el valor a cobrar", "Los valores ingresados debe ser mayor al Recargo+Interes");
                        }
                    } else {
                        JsfUti.messageInfo(null, "Verifique el valor a cobrar", "Los valores ingresados debe ser mayor a 0.00");
                    }
                } else {
                    JsfUti.messageInfo(null, "Verifique el valor a cobrar", "Los valores ingresados no deben ser mayor al de la Recaudación");
                }
            }
            if (!pagos.isEmpty()) {
                setPagoRealizado(Boolean.TRUE);
                System.out.println("isFoxMaster" + isFox);
                System.out.println("TIPO_EXONERACION NOextras" + this.tipoExoneracionParametro);
                if (isFox) {
                    generarComprobanteFox(pagos);
                } else {
                    generarComprobante(pagos);
                }

            }
        } catch (Exception e) {
            Logger.getLogger(PagoPrediales.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void realizarPagoCuotas() {
        List<RenPago> pagos = new ArrayList<>();
        RenPago pago;
        boolean terminado = emisionesACobrar.size() == convenidos.size();
        try {
            if (emisionesACobrar.size() > 1) {
                //VERIFICAR QUE SOLO SE REALIZE UN TIPO DE PAGO DE LOS 5 QUE EXISTEN                
                if (getModelPago().cantidadTipoPagos() == 1) {//VERIFICAR QUE SE REALIZE TOTOAL PAGO DE LaS EMISIONES SELECCIONADAS        
                    if (getModelPago().getValorTotal().compareTo(sumaTotalConv) == 0) {

                        for (RenLiquidacion e : emisionesACobrar) {
                            //e = recaudacion.realizarDescuentoRecargaInteresPredial(e,null);
                            e.calcularPago();
                            //e = recaudacion.realizarPago(e, modelPago.realizarPago(e),usuario);
                            pago = recaudacion.realizarPago(e, getModelPago().realizarPago(e), usuario, true);
                            if (pago != null) {
                                pagos.add(pago);
                            }
                        }

                    } else {
                        JsfUti.messageInfo(null, "Verifique el valor a cobrar", "El cobro de Emisiones multiples debe ser cancelado Totalmente");
                    }
                } else {
                    JsfUti.messageInfo(null, "Verifique Tipo de Pagos", "El cobro de Emisiones multiples solo permite un Tipo de Pago");
                }
            } else {
                if (getModelPago().getValorTotal().compareTo(sumaTotalConv) <= 0) {
                    if (getModelPago().getValorTotal().compareTo(BigDecimal.ZERO) > 0) {
                        for (RenLiquidacion l : emisionesACobrar) {
                            //l = recaudacion.realizarDescuentoRecargaInteresPredial(l,getModelPago().getPagoTransferencia().getFecha());
                            l.calcularPago();

                            pago = recaudacion.realizarPago(l, getModelPago().realizarPago(l), usuario, true);
                            if (pago != null) {
                                pagos.add(pago);
                            }
                        }
                        if (pagos.isEmpty()) {
                            JsfUti.messageInfo(null, "Verifique el valor a cobrar", "Los valores ingresados debe ser mayor al Recargo+Interes");
                        }
                    } else {
                        JsfUti.messageInfo(null, "Verifique el valor a cobrar", "Los valores ingresados debe ser mayor a 0.00");
                    }
                } else {
                    JsfUti.messageInfo(null, "Verifique el valor a cobrar", "Los valores ingresados no deben ser mayor al de la Recaudación");
                }
            }
            if (!pagos.isEmpty()) {
                if (terminado) {
                    convenioPago.setEstado((short) 6);
                    FnConvenioPagoObservacion observacionConvenio = new FnConvenioPagoObservacion();
                    observacionConvenio.setConvenio(convenioPago);
                    observacionConvenio.setEstado(Boolean.TRUE);
                    observacionConvenio.setEstadoConvenio(convenioPago.getEstado());
                    observacionConvenio.setObservacion("Completado.");
                    observacionConvenio.setUsuarioIngreso(session.getName_user());
                    observacionConvenio.setFechaIngreso(new Date());
                    manager.persist(convenioPago);
                    manager.persist(observacionConvenio);
                }

                setPagoRealizado(Boolean.TRUE);
                generarComprobanteConvenio(pagos);
            }
        } catch (Exception e) {
            Logger.getLogger(PagoPrediales.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void procesarPagosConv() {
        try {
            if (cuotasPredios.isEmpty() || cpd == null) {
                JsfUti.messageWarning(null, "Advertencia", "No ha seleccionado deudas para procesar.");
            } else {
                procesarPagosCuotasCoactiva();
            }
        } catch (Exception e) {
            JsfUti.messageError(null, "Error", "Error al procesar pago.");
        }
    }

    public void procesarPagosConvCoactiva() {
        if (cuotasPredios.isEmpty()) {
            JsfUti.messageWarning(null, "Advertencia", "No ha seleccionado deudas para procesar.");
        } else {
            procesarPagosCuotasCoactiva();
        }
    }

    public void procesarPagosCuotasCoactiva() {
        emisionesACobrar = new ArrayList<>();
        for (RenLiquidacion rl : cuotasPredios) {
            //VERIFIA SI EL PAGO QUESE HARA ES POR CUOTAS DE COACTIVA
            if (rl.getTipoLiquidacion().getId().equals(281L)) {
                esPagoCuotaCoactiva = Boolean.TRUE;
            }
            emisionesACobrar.add(rl);
        }
        if (cuotasPredios.size() == 1) {
            this.setEmisionSeleccionada(new RenLiquidacion());
            this.setEmisionSeleccionada(cuotasPredios.get(0));
            renderContextMenu = true;
        }
        if (cuotasPredios.size() != 1) {
            this.setEmisionSeleccionada(null);
            renderContextMenu = false;
        }
        Set<RenLiquidacion> liquidacionCobro = new HashSet<>();
        liquidacionCobro.addAll(emisionesACobrar);
        emisionesACobrar.clear();
        emisionesACobrar.addAll(liquidacionCobro);

        esPagoCuota = Boolean.TRUE;
        totalEmisiones = sumaTotalConv;
        this.getModelPago().setValorCobrar(sumaTotalConv);

        if (emisionesACobrar != null && !emisionesACobrar.isEmpty()) {
            JsfUti.update("formProcesar");
            JsfUti.executeJS("PF('dlgProcesar').show();");
        } else {
            JsfUti.messageInfo(null, "Mensaje", "No posee deuda.");

        }

    }

    public void generarComprobanteConvenio(List<RenPago> pagos) {
        try {
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            ss.borrarParametros();
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/"));
            ss.agregarParametro("LOGO", path + SisVars.sisLogo);
            ss.setNombreReporte("sComprobanteConvenioPagosPredios");
            ss.agregarParametro("liquidaciones", pagos);
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Comprobantes");
        } catch (Exception e) {
            System.out.println("e: " + e.getStackTrace());
        }
    }

    public void procesarConvenio(SelectEvent event) {
        convenioPago = (FnConvenioPago) event.getObject();
        convenioPago.setPredio(emisionesPredialesTemp.get(0).getPredio());
        convenioPago = (FnConvenioPago) manager.persist(convenioPago);
        emisionesPredialesTemp.stream().map((l) -> {
            l.setUsuarioValida(session.getName_user());
            return l;
        }).map((l) -> {
            l.setConvenioPago(convenioPago);
            return l;
        }).map((l) -> {
            l.setEstadoLiquidacion(new RenEstadoLiquidacion(7L));
            return l;
        }).forEachOrdered((l) -> {
            manager.persist(l);
        });
        emisionesPrediales = new ArrayList();
        emisionesPredialesTemp = new ArrayList();
        JsfUti.messageInfo(null, "Info", "El convenio se ha elaborado con exito.");

    }

    public void generarComprobanteFox(List<RenPago> pagos) {
        try {
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            ss.borrarParametros();
            ss.instanciarParametros();
            ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/"));
            ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreReporte("sCobroGeneralPredioRural");
            ss.agregarParametro("liquidaciones", pagos);
            Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "Comprobantes");
            setPagoRealizado(Boolean.FALSE);
        } catch (Exception e) {
            Logger.getLogger(PagoPrediales.class.getName()).log(Level.OFF, null, e);
        }
    }

    /*METODO QUE FUE MODIFICADO LOGO DEL REPORTE*/
    public void generarComprobante(List<RenPago> pagos) {
        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        ss.borrarParametros();
        ss.instanciarParametros();
        ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/"));
        ss.agregarParametro("LOGO", path + SisVars.sisLogo);
        ss.setTieneDatasource(Boolean.TRUE);
        ss.setNombreReporte(pagos.get(0).getLiquidacion().getTipoLiquidacion().getNombreReporte());
        if (pagos.get(0).getLiquidacion().getTipoLiquidacion().getId() == 7L) {
            ss.setNombreReporte("emisionPredioRuralFormatoAme");
        }
        if (pagos.get(0).getLiquidacion().getTipoLiquidacion().getId() == 13L && pagos.get(0).getLiquidacion().getPredio() == null
                && pagos.get(0).getLiquidacion().getClaveAME() != null) {
            ss.setNombreReporte("emisionPredioUrbanoSanVicenteSinPredioAME");
        }

        if (pagos.get(0).getLiquidacion().getEstadoLiquidacion().getId() == 2L) {
            ss.setNombreReporte("comprobanteAbonoUrbano");
        }
        ss.agregarParametro("TIPO_EXONERACION", this.getTipoExoneracionParametro());
        ss.agregarParametro("liquidaciones", pagos);
        Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "Comprobantes");
        setPagoRealizado(Boolean.FALSE);
    }

    public void generarCertificado() {
        try {
            GeDepartamento tesoreria;
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("certificados");
            ss.setNombreReporte("certificadoNoAdeudaImpuestoPredialSanVicente");
            ss.agregarParametro("NOMBRE_EMPRESA", SisVars.NOMBREMUNICIPIO);
            ss.agregarParametro("CERTIFICADO", numCertificado);
            ss.agregarParametro("DETALLE", this.detalle);
            tesoreria = manager.find(GeDepartamento.class, 20L);//ID: 20 Departamento de Tesoreria
            for (AclRol rol : tesoreria.getAclRolCollection()) {
                if (rol.getEsSubDirector()) {//ROL DE TESORERA
                    for (AclUser user : rol.getAclUserCollection()) {
                        ss.agregarParametro("TESORERA", user.getEnte() == null ? "REGISTRAR DATOS" : (Utils.isEmpty(user.getEnte().getTituloProf()) + " " + user.getEnte().getNombres() + " " + user.getEnte().getApellidos()));
                        break;
                    }
                    break;
                }
            }
            if (tipoCertificado.equals(1L)) {
                ss.agregarParametro("NUM_PREDIO", getPredioConsulta().getNumPredio().toString());
                ss.agregarParametro("CODIGO_CATASTRAL", getPredioConsulta().getCodigoPredialCompleto());
                ss.agregarParametro("CIUDADELA", getPredioConsulta().getCiudadela().getNombre());
                ss.agregarParametro("TITULO_REPORTE", 13L);
                ss.agregarParametro("CALVE_ANTERIOR", getPredioConsulta().getPredialant());
            }
            if (tipoCertificado.equals(2L)) {
                ss.agregarParametro("CODIGO_CATASTRAL", getPredioRuralConsulta().getRegCatastral() + "-" + getPredioRuralConsulta().getIdPredial() + "-" + getPredioRuralConsulta().getParroquia().getDescripcion());
                ss.agregarParametro("TITULO_REPORTE", 7L);
            }

            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "/Documento");
        } catch (Exception e) {
            Logger.getLogger(PagoPrediales.class.getName()).log(Level.OFF, null, e);
        }
    }

    public void getDataTesorera() {
        GeDepartamento tesoreria;
        tesoreria = manager.find(GeDepartamento.class, 20L);//ID: 20 Departamento de Tesoreria
        for (AclRol rol : tesoreria.getAclRolCollection()) {
            if (rol.getEsSubDirector()) {//ROL DE TESORERA
                for (AclUser user : rol.getAclUserCollection()) {
                    this.nombresTesorera = user.getEnte() == null ? "REGISTRAR DATOS" : (Utils.isEmpty(user.getEnte().getTituloProf()) + " " + user.getEnte().getNombres() + " " + user.getEnte().getApellidos());
                    break;
                }
                break;
            }
        }
    }

    public void getNombresPropietarios() {
        RenLiquidacion liq = this.getEmisionesPrediales().get(this.getEmisionesPrediales().size() - 1);
        if (liq != null) {
            if (liq.getComprador() != null) {
                this.nombresPropietarios = liq.getComprador().getEsPersona() ? liq.getComprador().getApellidos() + " " + liq.getComprador().getNombres() : liq.getComprador().getRazonSocial();
            } else {
                this.nombresPropietarios = liq.getNombreComprador();
            }
        }
//        if (tipoCertificado.equals(1L)) {
//            for (CatPredioPropietario p : getPredioConsulta().getCatPredioPropietarioCollection()) {
//                this.nombresPropietarios = p.getEnte().getEsPersona() ? p.getEnte().getApellidos() + " " + p.getEnte().getNombres() : p.getEnte().getRazonSocial();
//                break;
//            }
//        }
//        if (tipoCertificado.equals(2L)) {
//            this.nombresPropietarios = getPredioRuralConsulta().getPropietario() != null ? (getPredioRuralConsulta().getPropietario().getEsPersona() ? getPredioRuralConsulta().getPropietario().getApellidos() + " " + getPredioRuralConsulta().getPropietario().getNombres() : getPredioRuralConsulta().getPropietario().getRazonSocial()) : "";
//
//        }
    }

    public void getNombresContribuyenteNoAfiliado() {
        this.nombresPropietarios = comprador.getEsPersona() ? comprador.getApellidos() + " " + comprador.getNombres() : comprador.getRazonSocial();
    }

    public void comprobanteCoactiva() {
        try {
            if (pagoCoactiva.getId() != null) {
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Recibos?codigo=" + pagoCoactiva.getId());
                /*ss.instanciarParametros();
                 ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/Emision/").concat("/"));
                 ss.agregarParametro("RUTA_FIRMAS", Faces.getRealPath("/css/firmas/").concat("/"));
                 ss.setTieneDatasource(Boolean.TRUE);
                 ss.setNombreSubCarpeta("Emision");
                 ss.setNombreReporte("reciboCompleto");
                 ss.agregarParametro("ID_PAGO", pagoCoactiva.getId());
                 JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");*/
            }
        } catch (Exception e) {
            Logger.getLogger(PagoPrediales.class.getName()).log(Level.OFF, null, e);
        }
    }

    public void consultarEmisionesRurales2017() {
        try {
            paramt = new HashMap<>();
            //modelPago = new PagoTituloReporteModel();
            this.setModelPago(new PagoTituloReporteModel(new BigDecimal("0.00"), this.getVariosPagos(), this.getModelPago().getPagoNotaCredio(), this.getModelPago().getPagoCheque(), this.getModelPago().getPagoTarjetaCredito(), this.getModelPago().getPagoTransferencia()));
            this.setTotalEmisionesGeneral(null);
            this.setTotalEmisiones(null);

            if (predioUrbanoFox != null) {
                List<EmisionesRuralesExcel> prediosFoxsTemp = manager.findAll(QuerysFinanciero.getAllEmisionesFox,
                        new String[]{"codigoCatastralFox"},
                        new Object[]{predioUrbanoFox.getCodigoCatastral()});
                if (prediosFoxsTemp != null) {
                    if (!prediosFoxsTemp.isEmpty()) {
                        List<RenLiquidacion> tempEmisiones = new ArrayList<RenLiquidacion>();
                        RenLiquidacion liquidacion;
                        for (EmisionesRuralesExcel fox : prediosFoxsTemp) {
                            liquidacion = new RenLiquidacion();
                            liquidacion = (RenLiquidacion) manager.find(
                                    QuerysFinanciero.obtenerLiquidacionesPredialesRusticosExcel2017,
                                    new String[]{"tipoLiquidacion", "ruralExcel"},
                                    new Object[]{new RenTipoLiquidacion(13L), fox});
                            tempEmisiones.add(liquidacion);
                            System.out.println("fox" + fox);
                        }
                        this.setEmisionesPrediales(tempEmisiones);
                        calculoTotalPago(this.getEmisionesPrediales(), null);
                        this.setTotalEmisionesGeneral(new BigDecimal(this.getTotalEmisiones().toString()));
                    }
                }

                //paramt = new HashMap<>();
                //paramt.put("tipoLiquidacion", new RenTipoLiquidacion(7L));
                //paramt.put("estadoLiquidacion", new RenEstadoLiquidacion(2L));
                //paramt.put("estadoLiquidacion", new RenEstadoLiquidacion(1L));
                // paramt.put("ruralExcel", predioRural2017);
            } else {
                JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
            }
        } catch (Exception e) {
            Logger.getLogger(PagoPrediales.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void consultarEmisionesAME() {
        try {
            paramt = new HashMap<>();
            //modelPago = new PagoTituloReporteModel();
            this.setModelPago(new PagoTituloReporteModel(new BigDecimal("0.00"), this.getVariosPagos(), this.getModelPago().getPagoNotaCredio(), this.getModelPago().getPagoCheque(), this.getModelPago().getPagoTarjetaCredito(), this.getModelPago().getPagoTransferencia()));
            this.setTotalEmisionesGeneral(null);
            this.setTotalEmisiones(null);
            if (claveCatastralAnterior != null && !claveCatastralAnterior.equals("")) {
                List<RenLiquidacion> tempEmisiones = new ArrayList<RenLiquidacion>();
                tempEmisiones = manager.findAll(QuerysFinanciero.getRenLiquidacionByClaveAME, new String[]{"claveAnterior"},
                        new Object[]{claveCatastralAnterior});
                this.setEmisionesPrediales(tempEmisiones);
                calculoTotalPago(this.getEmisionesPrediales(), null);
                this.setTotalEmisionesGeneral(new BigDecimal(this.getTotalEmisiones().toString()));
            } else {
                JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
            }
        } catch (Exception e) {
            Logger.getLogger(PagoPrediales.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void consultarPorNombreComprador(Long tipo) {
        try {
            if (nombreComprador != null) {
                setEmisionesPrediales(null);
                String[] nombreCompradorSegmentado = nombreComprador.trim().replaceAll(" ", "%").split("%");
                String nuevoNombre = "";
                if (nombreCompradorSegmentado.length > 2) {
                    for (int i = 2; i < nombreCompradorSegmentado.length; i++) {
                        nuevoNombre = nuevoNombre + "%" + nombreCompradorSegmentado[i];
                    }
                    for (int i = 0; i < 2; i++) {
                        nuevoNombre = nuevoNombre + "%" + nombreCompradorSegmentado[i];
                    }
                } else {
                    for (int i = nombreCompradorSegmentado.length; i > 0; i--) {
                        nuevoNombre = nuevoNombre + "%" + nombreCompradorSegmentado[i - 1];
                    }
                }
                if (tipo == 1L) {

                    setPrediosConsulta(manager.findAll(QuerysFinanciero.prediosUrbanosEnLiquidacionPorNombreContribuyente, new String[]{"nombreComprador"}, new Object[]{"%" + nombreComprador.trim().replaceAll(" ", "%") + "%"}));
                    /*System.out.println("NOMBRE COMPRADOR : " + nombreComprador + " SEGMENTADO: " + nombreCompradorSegmentado 
                    + " GET " + getPrediosConsulta() + " qeury " + 
                            manager.findAll(QuerysFinanciero.prediosUrbanosEnLiquidacionPorNombreContribuyente, new String[]{"nombreComprador"}, new Object[]{"%" + nombreComprador.trim().replaceAll(" ", "%") + "%"}));
                     */ if (getPrediosConsulta() != null && !getPrediosConsulta().isEmpty()) {
                        if (getPrediosConsulta().size() == 1) {
                            //LLAMA AL PREDIO
                            paramt = new HashMap<>();
                            paramt.put("tipoLiquidacion", new RenTipoLiquidacion(13L));
                            paramt.put("predio", getPrediosConsulta().get(0));
                            setPredioConsulta(getPrediosConsulta().get(0));
                            emisionesPredialesTemp = manager.findObjectByParameterOrderList(RenLiquidacion.class, paramt, new String[]{"anio"}, Boolean.TRUE);
                            setEmisionesPrediales(emisionesPredialesTemp);
                            calculoTotalPago(getEmisionesPrediales(), null);
                            setTotalEmisionesGeneral(new BigDecimal(getTotalEmisiones().toString()));
                        } else {
                            //ABRIR CATALOGO PARA VARIOS PREDIOS LO HACE EL OTRO METODO
                            JsfUti.update("frmPredios");
                            JsfUti.executeJS("PF('dlgPrediosConsulta').show();");
                        }
                    } else {
                        //NUEVO INTENTO
                        setPrediosConsulta(manager.findAll(QuerysFinanciero.prediosUrbanosEnLiquidacionPorNombreContribuyente, new String[]{"nombreComprador"}, new Object[]{"%" + nuevoNombre + "%"}));
                        if (getPrediosConsulta() != null && !getPrediosConsulta().isEmpty()) {
                            if (getPrediosConsulta().size() == 1) {
                                //LLAMA AL PREDIO
                                paramt = new HashMap<>();
                                paramt.put("tipoLiquidacion", new RenTipoLiquidacion(13L));
                                paramt.put("predio", getPrediosConsulta().get(0));
                                setPredioConsulta(getPrediosConsulta().get(0));
                                emisionesPredialesTemp = manager.findObjectByParameterOrderList(RenLiquidacion.class, paramt, new String[]{"anio"}, Boolean.TRUE);
                                setEmisionesPrediales(emisionesPredialesTemp);
                                calculoTotalPago(getEmisionesPrediales(), null);
                                setTotalEmisionesGeneral(new BigDecimal(getTotalEmisiones().toString()));
                            } else {
                                //ABRIR CATALOGO PARA VARIOS PREDIOS LO HACE EL OTRO METODO
                                JsfUti.update("frmPredios");
                                JsfUti.executeJS("PF('dlgPrediosConsulta').show();");
                            }
                        } else {
                            JsfUti.messageInfo(null, "Mensaje", "No se encontraron coincidencias.");
                        }
                    }
                }
                if (tipo == 2L) {
                    emisionesPredialesTemp = new ArrayList<>();
                    setPrediosRusticoConsulta(manager.findAll(QuerysFinanciero.prediosRuralesEnLiquidacionPorNombreContribuyente, new String[]{"nombreComprador"}, new Object[]{"%" + nombreComprador.trim().replaceAll(" ", "%") + "%"}));

                    if (getPrediosRusticoConsulta() != null && !getPrediosRusticoConsulta().isEmpty()) {
                        if (getPrediosRusticoConsulta().size() == 1) {
                            //LLAMA AL PREDIO
                            paramt = new HashMap<>();
                            paramt.put("tipoLiquidacion", new RenTipoLiquidacion(7L));
                            paramt.put("predioRustico", getPrediosRusticoConsulta().get(0));
                            setPredioRuralConsulta(getPrediosRusticoConsulta().get(0));
                            emisionesPredialesTemp = manager.findObjectByParameterOrderList(RenLiquidacion.class, paramt, new String[]{"anio"}, Boolean.TRUE);
                            setEmisionesPrediales(emisionesPredialesTemp);
                            calculoTotalPago(getEmisionesPrediales(), null);
                            setTotalEmisionesGeneral(new BigDecimal(getTotalEmisiones().toString()));
                        }
                    } else {
                        //NUEVO INTENTO

                        setPrediosRusticoConsulta(manager.findAll(QuerysFinanciero.prediosRuralesEnLiquidacionPorNombreContribuyente, new String[]{"nombreComprador"}, new Object[]{"%" + nuevoNombre + "%"}));

                        if (getPrediosRusticoConsulta() != null && !getPrediosRusticoConsulta().isEmpty()) {
                            if (getPrediosRusticoConsulta().size() == 1) {
                                //LLAMA AL PREDIO
                                paramt = new HashMap<>();
                                paramt.put("tipoLiquidacion", new RenTipoLiquidacion(7L));
                                paramt.put("predioRustico", getPrediosRusticoConsulta().get(0));
                                setPredioRuralConsulta(getPrediosRusticoConsulta().get(0));
                                emisionesPredialesTemp = manager.findObjectByParameterOrderList(RenLiquidacion.class, paramt, new String[]{"anio"}, Boolean.TRUE);
                                setEmisionesPrediales(emisionesPredialesTemp);
                                calculoTotalPago(getEmisionesPrediales(), null);
                                setTotalEmisionesGeneral(new BigDecimal(getTotalEmisiones().toString()));
                            }
                        } else {
                            JsfUti.messageInfo(null, "Mensaje", "No se encontraron coincidencias.");
                        }
                    }
                }
                if (tipo == 3L) {
                    predioUrbanoFox = new EmisionesRuralesExcel();
                    emisionesPredialesTemp = new ArrayList<>();
                    prediosUrbanosFox = manager.findAll(QuerysFinanciero.prediosUrbanosEnLiquidacionPorNombreContribuyente, new String[]{"nombreComprador"}, new Object[]{"%" + nombreComprador.trim().replaceAll(" ", "%") + "%"});
                    /*System.out.println("NOMBRE COMPRADOR : " + nombreComprador + " SEGMENTADO: " + nombreCompradorSegmentado 
                    + " GET " + getPrediosConsulta() + " qeury " + 
                            manager.findAll(QuerysFinanciero.prediosUrbanosEnLiquidacionPorNombreContribuyente, new String[]{"nombreComprador"}, new Object[]{"%" + nombreComprador.trim().replaceAll(" ", "%") + "%"}));
                     */ if (prediosUrbanosFox != null) {
                        if (!prediosUrbanosFox.isEmpty()) {
                            if (prediosUrbanosFox.size() == 1) {
                                //LLAMA AL PREDIO
                                paramt = new HashMap<>();
                                paramt.put("tipoLiquidacion", new RenTipoLiquidacion(13L));
                                paramt.put("ruralExcel", prediosUrbanosFox.get(0));
                                predioUrbanoFox = prediosUrbanosFox.get(0);
                                emisionesPredialesTemp = manager.findObjectByParameterOrderList(RenLiquidacion.class, paramt, new String[]{"anio"}, Boolean.TRUE);
                                setEmisionesPrediales(emisionesPredialesTemp);
                                calculoTotalPago(getEmisionesPrediales(), null);
                                setTotalEmisionesGeneral(new BigDecimal(getTotalEmisiones().toString()));
                            } else {

                            }
                        }

                    }
                }
            } else {

            }
        } catch (Exception e) {
            Logger.getLogger(PagoPrediales.class.getName()).log(Level.OFF, null, e);
        }
    }

    public void onRowSelect(SelectEvent event) {
        calculoTotalPago(emisionesPredialesTemp, null);
    }

    public void onRowUnselect(UnselectEvent event) {
        calculoTotalPago(emisionesPredialesTemp, null);
    }

    public void onRowToggle() {
        calculoTotalPago(emisionesPredialesTemp, null);
    }

    public void cleanValues() {
        emisionesPredialesTemp = new ArrayList();
        consultarEmisionesPendientesPago();
    }

    public Boolean habilitarProcesar() {
        try {
            return session.getRoles().contains(73L) || session.getRoles().contains(201L) || session.getRoles().contains(75L)
                    || session.getRoles().contains(9L);
        } catch (Exception e) {
            Logger.getLogger(PagoPrediales.class.getName()).log(Level.OFF, null, e);
            return false;
        }
    }

    protected void habilitarRecalculo() {
        if (session != null && session.getRoles() != null) {
            for (Long r : session.getRoles()) {
                if (r.equals(204L)) {
                    liquidador = true;
                }
            }
        }
        tipoCert = Boolean.FALSE;
        solicitantes = new CatEnteLazy();
        codCatastralRural = "132250";
    }

    public void preCalculo(Boolean normalx) {
        try {
            normal = normalx;
            valoracion = avaluos.getEmisionPredial(session.getName_user(), emisionSeleccionada.getAnio(), emisionSeleccionada.getPredio().getNumPredio(), normalx).get();
            if (valoracion != null) {
                Faces.messageInfo(null, "Nota!", "Predio recalculado!");
            }
        } catch (InterruptedException | ExecutionException e) {
            Logger.getLogger(PagoPrediales.class.getName()).log(Level.OFF, null, e);
        }
    }

    public void recalcular() {
        try {
            setExoneracion((FnSolicitudExoneracion) manager.find(QuerysFinanciero.buscarExoneracionTerceraEdadYDiscapacitadoPorPredio, new String[]{"predio"}, new Object[]{getPredioConsulta()}));
            if (getExoneracion() != null) {
                if (getExoneracion().getEstado().getId() == 3L || getExoneracion().getEstado().getId() == 4L || getExoneracion().getEstado().getId() == 2L) {
                    setExoneracion(null);
                    setMensajeExoneracion(null);
                } else {
                    setMensajeExoneracion("Tiene una exoneración de: " + getExoneracion().getExoneracionTipo().getDescripcion().toUpperCase()
                            + "\nNúmero de resolución: " + getExoneracion().getNumResolucionSac());
                    if (getExoneracion().getExoneracionTipo().getId() == 17L || getExoneracion().getExoneracionTipo().getId() == 18L || getExoneracion().getExoneracionTipo().getId() == 37L || getExoneracion().getExoneracionTipo().getId() == 44L) {
                        JsfUti.update("formMensajeExo");
                        JsfUti.executeJS("PF('dlgMensajeExo').show()");
                        return;
                    } else {
                        setExoneracion(null);
                        setMensajeExoneracion(null);
                    }
                }
            }
            if (avaluos.recalcular(session.getName_user(), emisionSeleccionada.getId(), normal) != null) {
                Faces.messageInfo(null, "Nota!", "Liquidacion de predios urbanos recalculada satisfactoriamente");
            } else {
                Faces.messageWarning(null, "Advertencia!", "La emision actual esta exonerada, o realizo anticipos");
            }
        } catch (Exception e) {
            Logger.getLogger(PagoPrediales.class.getName()).log(Level.OFF, null, e);
        }
    }

    /**
     * @param @param tipo 1: No Contribuyente - 2: Certificado Rural
     *
     */
    public void guardarEnte(Boolean tipo) {
        try {
            VerCedulaUtils validacion = new VerCedulaUtils();
            paramt = new HashMap<>();
            Boolean esExcepcional = false;

            if (tipoEnte == 1) {
                if (registroEnte.getCiRuc() == null || registroEnte.getApellidos() == null || registroEnte.getNombres() == null) {
                    JsfUti.messageInfo(null, Messages.faltanCampos, "");
                    return;
                }
                paramt.put("ciRuc", registroEnte.getCiRuc());
                if (manager.findObjectByParameter(CatEnte.class, paramt) != null) {
                    JsfUti.messageInfo(null, Messages.ciRucExiste, "");
                }
                if (!validacion.isCIValida(registroEnte.getCiRuc())) {
                    JsfUti.messageInfo(null, Messages.cedulaCIinvalida, "");
                    return;
                }
                registroEnte.setEsPersona(Boolean.TRUE);
            }
            if (tipoEnte == 2) {
                if (registroEnte.getCiRuc() == null || registroEnte.getRazonSocial() == null) {
                    JsfUti.messageInfo(null, Messages.faltanCampos, "");
                    return;
                }
                paramt.put("ciRuc", registroEnte.getCiRuc());
                if (manager.findObjectByParameter(CatEnte.class, paramt) != null) {
                    JsfUti.messageInfo(null, Messages.ciRucExiste, "");
                }
                if (!validacion.isRucValido(registroEnte.getCiRuc())) {
                    JsfUti.messageInfo(null, Messages.cedulaCIinvalida, "");
                    return;
                }
                registroEnte.setEsPersona(Boolean.FALSE);
            }
            if (tipoEnte == 3) {
                esExcepcional = true;
                if (excepcionalEmpresa) {
                    if (registroEnte.getRazonSocial() == null) {
                        JsfUti.messageInfo(null, Messages.faltanCampos, "");
                        return;
                    }
                    //liqSelect.setNombreComprador(registroEnte.getRazonSocial());
                    //cobrosGenerales.setNombreComprador(registroEnte.getRazonSocial());
                } else {
                    if (registroEnte.getApellidos() == null || registroEnte.getNombres() == null) {
                        JsfUti.messageInfo(null, Messages.faltanCampos, "");
                        return;
                    }
                    //liqSelect.setNombreComprador(registroEnte.getNombres() + " " + registroEnte.getApellidos());
                    //cobrosGenerales.setNombreComprador(registroEnte.getNombres() + " " + registroEnte.getApellidos());
                }
                registroEnte.setEsPersona(!excepcionalEmpresa);
                registroEnte.setExcepcionales(Boolean.TRUE);
                registroEnte.setUserCre(session.getName_user());
                registroEnte.setFechaCre(new Date());
                JsfUti.messageInfo(null, "Seleccionado correctamente", "");
                JsfUti.executeJS("PF('dlgNewClient').hide();");
                JsfUti.update("mainForm");
                JsfUti.executeJS("PF('dlgSolicitanteCertificado').hide();");
                if (tipo) {
                    seleccionar();
                } else {
                    seleccionarRural();
                }
            }

            if (registroEnte != null && registroEnte.getId() == null && !esExcepcional) {
                registroEnte.setEsPersona(!excepcionalEmpresa);
                registroEnte.setUserCre(session.getName_user());
                registroEnte.setFechaCre(new Date());
                registroEnte = (CatEnte) manager.persist(registroEnte);
            }
            if (registroEnte != null && !esExcepcional) {
                JsfUti.messageInfo(null, "Registro Grabado", "");
                comprador = (CatEnte) manager.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{registroEnte.getCiRuc()});
                JsfUti.update("mainForm");
                JsfUti.executeJS("PF('dlgSolicitanteCertificado').hide();");
                JsfUti.executeJS("PF('dlgNewClient').hide();");
                if (tipo) {
                    seleccionar();
                } else {
                    seleccionarRural();
                }
            }
        } catch (Exception e) {
        }
    }

    public void existeCedula() {
        VerCedulaUtils validacion = new VerCedulaUtils();
        String identificacion = registroEnte.getCiRuc();
        if (registroEnte.getCiRuc() != null && registroEnte.getCiRuc().length() > 0) {
            registroEnte = (CatEnte) manager.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{identificacion});
            if (registroEnte == null) {
                registroEnte = new CatEnte();
                registroEnte.setCiRuc(identificacion);
                if (tipoEnte == 1) {
                    if (validacion.isCIValida(identificacion)) {
                        DatoSeguro ds = datoSeguroSeguro.getDatos(identificacion, false, 0);
                        registroEnte = datoSeguroSeguro.llenarEnte(ds, registroEnte, false);
                    }
                }
            }
        } else {
            registroEnte = new CatEnte();
        }
    }

    public void actualizarEnte() {
        registroEnte = new CatEnte();
    }

    public void actualizarValorPago() {
        calculoTotalPago(emisionesACobrar, getModelPago().getPagoTransferencia().getFecha());
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public Long getAnioEmision() {
        return anioEmision;
    }

    public void setAnioEmision(Long anioEmision) {
        this.anioEmision = anioEmision;
    }

    public CtlgSalario getSalario() {
        return salario;
    }

    public void setSalario(CtlgSalario salario) {
        this.salario = salario;
    }

    public List<RenDetLiquidacion> getRubrosEmision() {
        return rubrosEmision;
    }

    public void setRubrosEmision(List<RenDetLiquidacion> rubrosEmision) {
        this.rubrosEmision = rubrosEmision;
    }

    public RenDetLiquidacion getRubro() {
        return rubro;
    }

    public void setRubro(RenDetLiquidacion rubro) {
        this.rubro = rubro;
    }

    public RenTipoLiquidacion getTipoLiquidacion() {
        return tipoLiquidacion;
    }

    public void setTipoLiquidacion(RenTipoLiquidacion tipoLiquidacion) {
        this.tipoLiquidacion = tipoLiquidacion;
    }

    public RenLiquidacion getEmisionSeleccionada() {
        return emisionSeleccionada;
    }

    public void setEmisionSeleccionada(RenLiquidacion emisionSeleccionada) {
        this.emisionSeleccionada = emisionSeleccionada;
    }

    public AclUser getUsuario() {
        return usuario;
    }

    public void setUsuario(AclUser usuario) {
        this.usuario = usuario;
    }

    public List<RenLiquidacion> getEmisionesACobrar() {
        return emisionesACobrar;
    }

    public void setEmisionesACobrar(List<RenLiquidacion> emisionesACobrar) {
        this.emisionesACobrar = emisionesACobrar;
    }

    public List<RenEntidadBancaria> getBancos() {
        return bancos;
    }

    public void setBancos(List<RenEntidadBancaria> bancos) {
        this.bancos = bancos;
    }

    public List<RenEntidadBancaria> getTarjetas() {
        return tarjetas;
    }

    public void setTarjetas(List<RenEntidadBancaria> tarjetas) {
        this.tarjetas = tarjetas;
    }

    public RenPago getPagoCoactiva() {
        return pagoCoactiva;
    }

    public void setPagoCoactiva(RenPago pagoCoactiva) {
        this.pagoCoactiva = pagoCoactiva;
    }

    public List<CatParroquia> getParroquiasRurales() {
        return parroquiasRurales;
    }

    public void setParroquiasRurales(List<CatParroquia> parroquiasRurales) {
        this.parroquiasRurales = parroquiasRurales;
    }

    public List<CatCiudadela> getCiudadelas() {
        return ciudadelas;
    }

    public void setCiudadelas(List<CatCiudadela> ciudadelas) {
        this.ciudadelas = ciudadelas;
    }

    public String getNumCertificado() {
        return numCertificado;
    }

    public void setNumCertificado(String numCertificado) {
        this.numCertificado = numCertificado;
    }

    public Long getTipoCertificado() {
        return tipoCertificado;
    }

    public void setTipoCertificado(Long tipoCertificado) {
        this.tipoCertificado = tipoCertificado;
    }

    public String getNombreComprador() {
        return nombreComprador;
    }

    public void setNombreComprador(String nombreComprador) {
        this.nombreComprador = nombreComprador;
    }

    public String getNombreContribuyente() {
        return nombreContribuyente;
    }

    public void setNombreContribuyente(String nombreContribuyente) {
        this.nombreContribuyente = nombreContribuyente;
    }

    public List<NombreContribuyenteModel> getModelNombresList() {
        return modelNombresList;
    }

    public void setModelNombresList(List<NombreContribuyenteModel> modelNombresList) {
        this.modelNombresList = modelNombresList;
    }

    public Boolean getLiquidador() {
        return liquidador;
    }

    public void setLiquidador(Boolean liquidador) {
        this.liquidador = liquidador;
    }

    public Map<String, Object> getParamt() {
        return paramt;
    }

    public void setParamt(Map<String, Object> paramt) {
        this.paramt = paramt;
    }

    public ValoracionPredial getValoracion() {
        return valoracion;
    }

    public void setValoracion(ValoracionPredial valoracion) {
        this.valoracion = valoracion;
    }

    public Boolean getNormal() {
        return normal;
    }

    public void setNormal(Boolean normal) {
        this.normal = normal;
    }

    public void setPropietario(CatPredioPropietario propietario) {
        try {
            if (propietario != null) {
                this.propietario = propietario;
                setContribuyenteConsulta(propietario.getEnte());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPredioRustico(CatPredioRustico predioRustico) {
        try {
            if (predioRustico != null) {
                this.predioRustico = predioRustico;
                setContribuyenteConsulta(predioRustico.getPropietario());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CatPredioRustico getPredioRustico() {
        return predioRustico;
    }

    public CatPredioPropietario getPropietario() {
        return propietario;
    }

    public void emisionPredial() {
        if (getIsSanMiguel()) {
            getEmisionesPredialesTemp();
        } else {
            getEmisionSeleccionada();
        }
    }

    public List<RenLiquidacion> getEmisionesPredialesTemp() {
        return emisionesPredialesTemp;
    }

    public void setEmisionesPredialesTemp(List<RenLiquidacion> emisionesPredialesTemp) {
        this.emisionesPredialesTemp = emisionesPredialesTemp;
    }

    public Boolean getRenderContextMenu() {
        return renderContextMenu;
    }

    public void setRenderContextMenu(Boolean renderContextMenu) {
        this.renderContextMenu = renderContextMenu;
    }

    public String getNombresFox() {
        return nombresFox;
    }

    public void setNombresFox(String nombresFox) {
        this.nombresFox = nombresFox;
    }

    public EmisionesRuralesExcel getPredioUrbanoFox() {
        if (predioUrbanoFox == null) {
            predioUrbanoFox = new EmisionesRuralesExcel();
        }

        return predioUrbanoFox;
    }

    public void setPredioUrbanoFox(EmisionesRuralesExcel predioUrbanoFox) {
        try {
            if (predioUrbanoFox != null) {
                this.predioUrbanoFox = new EmisionesRuralesExcel();
                this.predioUrbanoFox = predioUrbanoFox;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<EmisionesRuralesExcel> getPrediosUrbanosFox() {
        return prediosUrbanosFox;
    }

    public void setPrediosUrbanosFox(List<EmisionesRuralesExcel> prediosUrbanosFox) {
        this.prediosUrbanosFox = prediosUrbanosFox;
    }

    public EmisionesRuralesExcelLazy getEmisionesRuralesExcelLazy() {
        return emisionesRuralesExcelLazy;
    }

    public void setEmisionesRuralesExcelLazy(EmisionesRuralesExcelLazy emisionesRuralesExcelLazy) {
        this.emisionesRuralesExcelLazy = emisionesRuralesExcelLazy;
    }

    public Integer getTipoEnte() {
        return tipoEnte;
    }

    public void setTipoEnte(Integer tipoEnte) {
        this.tipoEnte = tipoEnte;
    }

    public CatEnteLazy getSolicitantes() {
        return solicitantes;
    }

    public void setSolicitantes(CatEnteLazy solicitantes) {
        this.solicitantes = solicitantes;
    }

    public Boolean getExcepcionalEmpresa() {
        return excepcionalEmpresa;
    }

    public void setExcepcionalEmpresa(Boolean excepcionalEmpresa) {
        this.excepcionalEmpresa = excepcionalEmpresa;
    }

    public CatEnte getRegistroEnte() {
        return registroEnte;
    }

    public void setRegistroEnte(CatEnte registroEnte) {
        this.registroEnte = registroEnte;
    }

    public CatEnte getComprador() {
        return comprador;
    }

    public void setComprador(CatEnte comprador) {
        this.comprador = comprador;
    }

    public Boolean getTipoCert() {
        return tipoCert;
    }

    public void setTipoCert(Boolean tipoCert) {
        this.tipoCert = tipoCert;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getCodCatastralRural() {
        return codCatastralRural;
    }

    public void setCodCatastralRural(String codCatastralRural) {
        this.codCatastralRural = codCatastralRural;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        setContribuyenteConsulta(ente);
        this.ente = ente;
    }

    public String getClaveCatastralAnterior() {
        return claveCatastralAnterior;
    }

    public void setClaveCatastralAnterior(String claveCatastralAnterior) {
        this.claveCatastralAnterior = claveCatastralAnterior;
    }

    public String getNombresPropietariosAME() {
        return nombresPropietariosAME;
    }

    public void setNombresPropietariosAME(String nombresPropietariosAME) {
        this.nombresPropietariosAME = nombresPropietariosAME;
    }

    /*
    *
    *CALCULO DE REMISION DE INTERESES
     */
    public void openDlgRemisionInteres() {
        if (emisionesPrediales.get(emisionesPrediales.size() - 1).getComprador() != null) {

            Map<String, List<String>> params = new HashMap<>();
            List<String> p = new ArrayList<>();

            p = new ArrayList<>();
            p.add(remisionInteresServices.saveFnRemisionSolicitudProceso(emisionesPrediales).getId().toString());
            params.put("fnRemisionSolicitudId", p);

            Map<String, Object> options = new HashMap<>();
            options.put("resizable", true);
            options.put("draggable", true);
            options.put("modal", true);
            options.put("closable", true);
            RequestContext.getCurrentInstance().openDialog("/resources/dialog/dlgRemisionInteres", options, params);
        } else {
            JsfUti.messageError(null, "Debe Actualizar los Datos del Propietario", null);
        }
    }

    public void procesarRemision(SelectEvent event) {
        FnRemisionSolicitud frs = (FnRemisionSolicitud) event.getObject();
        if (frs != null) {
            if (frs.getId() != null) {
                JsfUti.messageInfo(null, "Info", "Datos Guardados Correctamente");
                // RequestContext.getCurrentInstance().closeDialog(frs);
            } else {
                JsfUti.messageError(null, "Info", "Ocurrio un Problema Mientras Se persistian los datos");
            }
        } else {
            JsfUti.messageError(null, "Info", "Ocurrio un Problema Mientras Se persistian los datos");
        }
    }

    public void generarSolicitudReimisionInteres() {
        try {
            if (Utils.isNotEmpty(emisionesPrediales)) {
                if (emisionesPrediales.size() >= 0) {
                    if (emisionesPrediales.get(emisionesPrediales.size() - 1).getPredio() != null) {
                        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
                        ss.borrarDatos();
                        ss.borrarParametros();
                        ss.instanciarParametros();
                        ss.setTieneDatasource(Boolean.TRUE);
                        ss.setNombreSubCarpeta("Financiero/ReimisionInteres");
                        ss.agregarParametro("LOGO", Faces.getRealPath("/").concat(SisVars.sisLogo));
                        ss.agregarParametro("NUMPREDIO", emisionesPrediales.get(emisionesPrediales.size() - 1).getPredio().getNumPredio());
                        ss.agregarParametro("CODIGO_CATASTRAL", emisionesPrediales.get(emisionesPrediales.size() - 1).getPredio().getClaveCat());
                        ss.setNombreReporte("sReporteSolicirtud");
                        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
                    }
                } else {
                    JsfUti.messageError(null, "Info", "Debe Seleccionar un Predio");
                }
            } else {
                JsfUti.messageError(null, "Info", "Debe Seleccionar un Predio");
            }
        } catch (Exception e) {
            Logger.getLogger(RemisionIntereses.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean getEsPagoCuota() {
        return esPagoCuota;
    }

    public void setEsPagoCuota(Boolean esPagoCuota) {
        this.esPagoCuota = esPagoCuota;
    }

    public Boolean getEsPagoCuotaCoactiva() {
        return esPagoCuotaCoactiva;
    }

    public void setEsPagoCuotaCoactiva(Boolean esPagoCuotaCoactiva) {
        this.esPagoCuotaCoactiva = esPagoCuotaCoactiva;
    }
    
    

}
