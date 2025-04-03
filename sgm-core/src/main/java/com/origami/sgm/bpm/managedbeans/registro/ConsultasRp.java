/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.RegActo;
import com.origami.sgm.entities.RegEnteInterviniente;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegLibro;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoCapital;
import com.origami.sgm.entities.RegMovimientoCliente;
import com.origami.sgm.entities.RegMovimientoFicha;
import com.origami.sgm.entities.RegMovimientoRepresentante;
import com.origami.sgm.entities.RegMovimientoSocios;
import com.origami.sgm.entities.RegpCertificadosInscripciones;
import com.origami.sgm.lazymodels.RegEnteIntervinienteLazy;
import com.origami.sgm.lazymodels.RegFichaLazy;
import com.origami.sgm.lazymodels.RegMovimientosLazy;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;
import util.Utils;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class ConsultasRp extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    @javax.inject.Inject
    private Entitymanager serv;

    @Inject
    private ServletSession servletSession;

    @Inject
    private ReportesView reportes;

    protected List<RegMovimientoCliente> movimientosInterv = new ArrayList<>();
    protected List<RegFicha> fichasInterv = new ArrayList<>();
    protected RegEnteIntervinienteLazy intervinientesLazy;
    protected RegMovimiento movimiento;
    protected RegFicha ficha;

    protected List<RegMovimientoRepresentante> representantes = new ArrayList<>();
    protected List<RegMovimientoSocios> socios = new ArrayList<>();
    protected List<RegMovimientoCapital> capitales = new ArrayList<>();
    protected List<RegMovimientoCliente> clientes = new ArrayList<>();
    protected List<RegFicha> fichas = new ArrayList<>();
    protected List<RegMovimientoFicha> movimientosFichas = new ArrayList<>();

    protected String linderos = "";
    protected RegMovimientosLazy movimientosLazy;
    protected RegFichaLazy fichasLazy;

    protected Integer tipoConsulta = 1;
    protected String valorConsulta = "";
    protected Boolean showMovs = false;
    protected Boolean showFichas = false;
    protected RegMovimientosLazy lazyMovs;
    protected RegFichaLazy lazyFichas;

    protected Boolean realizarTarea = false;
    protected Boolean showInterv = false;
    protected Boolean showBtn = false;
    protected Long idTarea;
    protected RegpCertificadosInscripciones cert;
    protected RegEnteInterviniente interviniente;

    protected RegEnteInterviniente select = new RegEnteInterviniente();
    protected Integer cantMovs = 0;
    protected Integer cantFich = 0;

    protected String cadena = "";
    protected Date desde = new Date();
    protected Date hasta = new Date();

    protected RegLibro libroConsEsp;
    protected RegActo actoConsEsp;
    protected Long inscripcionConsEsp;
    protected Long repertorioConsEsp;
    protected Date desdeConsEsp;
    protected Date hastaConsEsp;
    protected List<RegActo> listActos;
    protected List<RegMovimiento> movimientosSeleccionados;
    protected String urlDownload = "";
    protected Integer anio;
    protected Calendar cal = Calendar.getInstance();

    protected RegLibro libroIndice;
    protected Date desdeIndice;
    protected Date hastaIndice;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            if (session.getTaskID() != null && idTarea != null) {
                this.setTaskId(session.getTaskID());
                realizarTarea = true;
                cert = (RegpCertificadosInscripciones) acl.find(RegpCertificadosInscripciones.class, idTarea);
            }
            intervinientesLazy = new RegEnteIntervinienteLazy();
        } catch (Exception e) {
            Logger.getLogger(ConsultasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public List<RegActo> complete(String query) {//////////////mejorar consulta
        List<RegActo> listaActos = serv.findAll(Querys.getRegActoList);
        listActos = new ArrayList<>();
        if (!query.equals("*")) {
            for (RegActo a : listaActos) {
                if (a.getAbreviatura().toUpperCase().contains(query.toUpperCase())) {
                    listActos.add(a);
                }
                if (a.getNombre().toUpperCase().contains(query.toUpperCase())) {
                    listActos.add(a);
                }
            }
            if (listActos.size() >= 10) {
                return listActos.subList(0, 10);
            }
        } else {
            listActos.addAll(listaActos.subList(0, 10));
        }
        return listActos;
    }

    public void imprimirInscripcionesPorFecha() {
        try {
            if (hasta.after(desde) || hasta.equals(desde)) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date limite = sdf.parse("01-01-2016");
                //ESTA FECHA ESTA QUEMADA EN EL CODIGO POR QUE
                //DESDE AQUI LAS INSCRIPCIONES SE HICIERON EN SGM
                if (hasta.after(limite) || hasta.equals(limite)) {
                    limite = Utils.sumarRestarDiasFecha(hasta, 1);
                } else {
                    limite = hasta;
                }
                servletSession.instanciarParametros();
                servletSession.setTieneDatasource(true);
                servletSession.setNombreReporte("RegConsultaEspecificaFechas");
                servletSession.setNombreSubCarpeta("registroPropiedad");
                servletSession.agregarParametro("DESDE", desde);
                servletSession.agregarParametro("HASTA", limite);
                servletSession.agregarParametro("USERNAME", session.getName_user());
                JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");
            } else {
                JsfUti.messageWarning(null, "Fecha Hasta debe ser mayor o igual a Fecha Desde.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(ConsultasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void imprimirReporte(RegEnteInterviniente interv) {
        try {
            if (realizarTarea) {
                showInterv = true;
                interviniente = interv;
                JsfUti.update("mainForm");
            } else {
                Integer i = 0;
                Collection col1 = reg.getListIdMovsByCedRucInterv(interv.getCedRuc());
                if (col1 != null) {
                    i = col1.size();
                }
                servletSession.instanciarParametros();
                servletSession.setTieneDatasource(true);
                servletSession.setNombreReporte("RegConsultaEspecificaPersona");
                servletSession.setNombreSubCarpeta("registroPropiedad");
                //servletSession.agregarParametro("ENTE", interv.getId());
                servletSession.agregarParametro("ENTE", interv.getCedRuc());
                servletSession.agregarParametro("NOMBRE", interv.getNombre());
                servletSession.agregarParametro("USERNAME", session.getName_user());
                servletSession.agregarParametro("TOTAL", "TOTAL   :   " + i + "  Movimientos.");
                JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(ConsultasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void imprimirBitacoraMov() {
        try {
            servletSession.instanciarParametros();
            servletSession.setTieneDatasource(true);
            servletSession.setNombreSubCarpeta("registroPropiedad");
            servletSession.setNombreReporte("bitacoraSgm");
            servletSession.agregarParametro("codMovimiento", movimiento.getId());
            servletSession.agregarParametro("numFicha", null);
            servletSession.agregarParametro("titulo", Messages.bitacoraMovimiento);
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, e.getMessage());
            Logger.getLogger(ConsultasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void imprimirBitacoraFicha() {
        try {
            servletSession.instanciarParametros();
            servletSession.setTieneDatasource(true);
            servletSession.setNombreSubCarpeta("registroPropiedad");
            servletSession.setNombreReporte("bitacoraSgm");
            servletSession.agregarParametro("codMovimiento", null);
            servletSession.agregarParametro("numFicha", ficha.getNumFicha());
            servletSession.agregarParametro("titulo", Messages.bitacoraFicha);
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, e.getMessage());
            Logger.getLogger(ConsultasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void imprimirCompletandoTarea() {
        try {
            Integer i = 0;
            if (interviniente.getRegMovimientoClienteCollection() != null) {
                i = interviniente.getRegMovimientoClienteCollection().size();
            }
            servletSession.instanciarParametros();
            servletSession.setTieneDatasource(true);
            servletSession.setNombreReporte("RegConsultaEspecificaPersona");
            servletSession.setNombreSubCarpeta("registroPropiedad");
            servletSession.agregarParametro("ENTE", interviniente.getId());
            servletSession.agregarParametro("USERNAME", session.getName_user());
            servletSession.agregarParametro("TOTAL", "TOTAL   :   " + i + "  Movimientos.");
            JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");
            showBtn = true;
            JsfUti.update("mainForm:panelInterviniente");
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(ConsultasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void imprimirInscripcionesEspecificas() {
        try {
            servletSession.instanciarParametros();
            servletSession.setTieneDatasource(true);
            servletSession.setNombreReporte("RegConsultaEspecifica");
            servletSession.setNombreSubCarpeta("registroPropiedad");
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date limite = sdf.parse("01-01-2016");
            Date hasta2016 = hastaConsEsp;
            //ESTA FECHA ESTA QUEMADA EN EL CODIGO POR QUE
            //DESDE AQUI LAS INSCRIPCIONES SE HICIERON EN SGM
            if (hastaConsEsp != null) {
                if (hastaConsEsp.before(limite)) {
                    limite = hastaConsEsp;
                } else {
                    limite = Utils.sumarRestarDiasFecha(hastaConsEsp, 1);
                }
            } else {
                limite = reg.getFechaInscripcionMayor();
                hasta2016 = limite;
            }
            servletSession.agregarParametro("LIBRO", libroConsEsp != null ? libroConsEsp.getId() : 0L);
            servletSession.agregarParametro("LIBRO_NAME", libroConsEsp != null ? libroConsEsp.getNombre() : "TODOS LOS LIBROS");
            servletSession.agregarParametro("ACTO", actoConsEsp != null ? actoConsEsp.getId() : 0L);
            servletSession.agregarParametro("ACTO_NAME", actoConsEsp != null ? actoConsEsp.getAbreviatura() + " | " + actoConsEsp.getNombre() : "TODOS LOS ACTOS");
            servletSession.agregarParametro("INSCRIPCION", inscripcionConsEsp != null ? inscripcionConsEsp : 0L);
            servletSession.agregarParametro("REPERTORIO", repertorioConsEsp != null ? repertorioConsEsp : 0L);
            servletSession.agregarParametro("DESDE", desdeConsEsp != null ? desdeConsEsp : reg.getFechaInscripcionMenor());
            servletSession.agregarParametro("HASTA", limite);
            servletSession.agregarParametro("HASTA2016", hasta2016);
            servletSession.agregarParametro("USERNAME", session.getName_user());

            JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");

        } catch (Exception e) {
            Logger.getLogger(ConsultasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void reporteIndices() {
        try {
            if (desdeIndice != null && hastaIndice != null) {
                if (desdeIndice.before(hastaIndice)) {
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    servletSession.instanciarParametros();
                    servletSession.setTieneDatasource(true);
                    servletSession.setNombreReporte("IndicesEspecificos");
                    servletSession.setNombreSubCarpeta("registroPropiedad");
                    if (libroIndice == null) {
                        servletSession.agregarParametro("LIBRO", 0L);
                        servletSession.agregarParametro("LIBRO_NAME", "TODOS LOS LIBROS");
                    } else {
                        servletSession.agregarParametro("LIBRO", libroIndice.getId());
                        servletSession.agregarParametro("LIBRO_NAME", libroIndice.getNombre());
                    }
                    servletSession.agregarParametro("DESDE", desdeIndice);
                    servletSession.agregarParametro("HASTA", Utils.sumarRestarDiasFecha(hastaIndice, 1));
                    servletSession.agregarParametro("CADENA_HASTA", df.format(hastaIndice));
                    servletSession.agregarParametro("USER_NAME", session.getName_user());
                    servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/registroPropiedad/");
                    JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");
                } else {
                    JsfUti.messageWarning(null, "Fecha desde debe ser menor a fecha hasta.", "");
                }
            } else {
                JsfUti.messageWarning(null, "Debe ingresar fecha desde y hasta.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(ConsultasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void actualizarTarea() {
        Calendar cale = Calendar.getInstance();
        try {
            cert.setRealizado(true);
            cert.setFechaFin(cale.getTime());
            acl.persist(cert);
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(InscripcionNueva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void terminarTarea() {
        this.actualizarTarea();
        this.continuar();
    }

    public void consultaInterviniente(RegEnteInterviniente interv) {
        try {
            select = interv;
            cantMovs = 0;
            cantFich = 0;
            //Collection col1 = reg.getListIdMovsByInterv(interv.getId());
            Collection col1 = reg.getListIdMovsByCedRucInterv(interv.getCedRuc());
            if (col1 != null) {
                if (!col1.isEmpty()) {
                    movimientosLazy = new RegMovimientosLazy(col1);
                    //Collection col2 = reg.getListIdFichasByInterv(interv.getId());
                    Collection col2 = reg.getListIdFichasByDocInterv(interv.getCedRuc());
                    if (col2.isEmpty()) {
                        fichasLazy = null;
                    } else {
                        fichasLazy = new RegFichaLazy(col2);
                    }
                    cantMovs = col1.size();
                    cantFich = col2.size();
                    JsfUti.update("formConsulta");
                    JsfUti.executeJS("PF('dlgConsultaInterv').show();");
                } else {
                    JsfUti.messageInfo(null, "El cliente no registra Movimientos.", "");
                }
            } else {
                JsfUti.messageInfo(null, "El cliente no registra Movimientos.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(ConsultasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgMovSelect(RegMovimiento mov) {
        try {
            movimiento = mov;
            movimiento.setRegMovimientoReferenciaCollection(serv.findAll(Querys.getRegMovimientoReferenciaByIdMov, new String[]{"idmov"}, new Object[]{movimiento.getId()}));
            fichas = reg.getRegFichaByIdRegMov(mov.getId());
            capitales = reg.getRegMovCapitalByIdMov(mov.getId());
            clientes = reg.getRegMovClienteByIdMov(mov.getId());
            representantes = reg.getRegMovRepresentByIdMov(mov.getId());
            socios = reg.getRegMovSociosByIdMov(mov.getId());
            cal.setTime(movimiento.getFechaInscripcion());
            anio = cal.get(Calendar.YEAR);
            urlDownload = "/pages" + SisVars.urlbase + "descarga.jsf?nombreLibro=" + movimiento.getLibro().getNombreCarpeta()
                    + "&anioInscripcion=" + anio + "&numeroTomo=" + movimiento.getNumTomo() + "&numeroInscripcion="
                    + movimiento.getNumInscripcion() + "&folioInicial=" + movimiento.getFolioInicio()
                    + "&folioFinal=" + movimiento.getFolioFin();
            JsfUti.update("formMovRegSelec");
            JsfUti.executeJS("PF('dlgMovRegSelec').show();");
        } catch (Exception e) {
            Logger.getLogger(ConsultasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showLinderos(RegFicha f) {
        linderos = f.getLinderos();
    }

    public void showDlgFichaSelect(RegFicha f) {
        try {
            ficha = f;
            ficha.setDescripcionTemp(f.getObsvEstado(f.getEstado()));
            movimientosFichas = reg.getRegMovByIdFicha(f.getId());
            if (ficha.getTipoPredio() != null) {
                if (ficha.getTipoPredio().equalsIgnoreCase("U")) {
                    ficha.setTipoPredioTemp("Urbano");
                } else if (ficha.getTipoPredio().equalsIgnoreCase("R")) {
                    ficha.setTipoPredioTemp("Rural");
                } else if (ficha.getTipoPredio().equalsIgnoreCase("I")) {
                    ficha.setTipoPredioTemp("IIIIIIIIII");
                }
            }
            JsfUti.executeJS("PF('dlgMovRegSelec').hide();");
            JsfUti.update("formFichaSelect");
            JsfUti.executeJS("PF('dlgFichaSelect').show();");
        } catch (Exception e) {
            Logger.getLogger(ConsultasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void otrasConsultas() {
        try {
            switch (tipoConsulta) {
                case 1:
                    if (this.validaCampo()) {
                        lazyFichas = new RegFichaLazy(valorConsulta, 1);
                        showMovs = false;
                        showFichas = true;
                        JsfUti.update("mainForm:tabConsultas:pnlGrpTables");
                    }
                    break;
                case 2:
                    if (this.validaCampo()) {
                        if (Utils.validateNumberPattern(valorConsulta)) {
                            lazyFichas = new RegFichaLazy(valorConsulta, 2);
                            showMovs = false;
                            showFichas = true;
                            JsfUti.update("mainForm:tabConsultas:pnlGrpTables");
                        } else {
                            JsfUti.messageError(null, "Solo debe ingresar numeros.", "");
                        }
                    }

                    break;
                case 3:
                    if (this.validaCampo()) {
                        if (Utils.validateNumberPattern(valorConsulta)) {
                            lazyMovs = new RegMovimientosLazy(valorConsulta, 1);
                            showMovs = true;
                            showFichas = false;
                            JsfUti.update("mainForm:tabConsultas:pnlGrpTables");
                        } else {
                            JsfUti.messageError(null, "Solo debe ingresar numeros.", "");
                        }
                    }
                    break;
                case 4:
                    if (this.validaCampo()) {
                        if (Utils.validateNumberPattern(valorConsulta)) {
                            lazyMovs = new RegMovimientosLazy(valorConsulta, 2);
                            showMovs = true;
                            showFichas = false;
                            JsfUti.update("mainForm:tabConsultas:pnlGrpTables");
                        } else {
                            JsfUti.messageError(null, "Solo debe ingresar numeros.", "");
                        }
                    }
                    break;
                case 5:
                    JsfUti.update("formFechas");
                    JsfUti.executeJS("PF('consultaFechas').show();");
                    break;
                case 6:
                    if (this.validaCampo()) {
                        lazyFichas = new RegFichaLazy("numFicha", valorConsulta, 6);
                        showMovs = false;
                        showFichas = true;
                        JsfUti.update("mainForm:tabConsultas:pnlGrpTables");
                    }
                    break;
                case 7:
                    JsfUti.executeJS("PF('consultaEspecifica').show();");
                    break;
                case 8:
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    String fecha = sdf.format(new Date());
                    libroIndice = null;
                    desdeIndice = sdf.parse(fecha);
                    hastaIndice = sdf.parse(fecha);
                    JsfUti.update("formIndices");
                    JsfUti.executeJS("PF('indicesEspecificos').show();");
                    break;
                default:
                    showMovs = false;
                    showFichas = false;
                    break;
            }
        } catch (Exception e) {
            Logger.getLogger(ConsultasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public boolean validaCampo() {
        if (valorConsulta == null) {
            JsfUti.messageError(null, Messages.campoVacio, "");
            return false;
        } else {
            return true;
        }
    }

    public void buscarInscripFechas() {
        try {
            if (hasta.after(desde) || hasta.equals(desde)) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date limite = sdf.parse("01-01-2016");
                //ESTA FECHA ESTA QUEMADA EN EL CODIGO POR QUE
                //DESDE AQUI LAS INSCRIPCIONES SE HICIERON EN SGM
                if (hasta.after(limite) || hasta.equals(limite)) {
                    limite = Utils.sumarRestarDiasFecha(hasta, 1);
                } else {
                    limite = hasta;
                }
                lazyMovs = new RegMovimientosLazy(desde, limite);
                showMovs = true;
                showFichas = false;
                JsfUti.update("mainForm:tabConsultas:pnlGrpTables");
                JsfUti.executeJS("PF('consultaFechas').hide();");
            } else {
                JsfUti.messageWarning(null, "Fecha Hasta debe ser mayor o igual a Fecha Desde.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(ConsultasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void buscarInterv() {
        try {
            if (cadena != null) {
                intervinientesLazy = new RegEnteIntervinienteLazy(cadena);
            } else {
                JsfUti.messageError(null, Messages.campoVacio, "");
            }
        } catch (Exception e) {
            Logger.getLogger(ConsultasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void consultaEspecifica() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date limite = sdf.parse("01-01-2016");
            //ESTA FECHA ESTA QUEMADA EN EL CODIGO POR QUE
            //DESDE AQUI LAS INSCRIPCIONES SE HICIERON EN SGM
            if (hastaConsEsp != null) {
                if (hastaConsEsp.before(limite)) {
                    limite = hastaConsEsp;
                } else {
                    limite = Utils.sumarRestarDiasFecha(hastaConsEsp, 1);
                }
            } else {
                limite = reg.getFechaInscripcionMayor();
            }
            lazyMovs = new RegMovimientosLazy(libroConsEsp, actoConsEsp, inscripcionConsEsp, repertorioConsEsp, desdeConsEsp != null ? desdeConsEsp : reg.getFechaInscripcionMenor(), limite);
            showMovs = true;
            showFichas = false;
            JsfUti.update("mainForm:tabConsultas:pnlGrpTables");
            JsfUti.executeJS("PF('consultaEspecifica').hide();");
        } catch (Exception e) {
            Logger.getLogger(ConsultasRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public String getPapelByMovimientoInterviniente(Long mov, Long inter) {
        return reg.getPapelByMovimientoInterviniente(mov, inter);
    }

    public String getPapelByMovAndCodInterv(Long mov, String doc) {
        return reg.getPapelByMovAndDocumentoInterv(mov, doc);
    }

    public List<RegLibro> getLibros() {
        return serv.findAll(Querys.getRegLibroList);
    }

    public RegEnteInterviniente getInterviniente() {
        return interviniente;
    }

    public void setInterviniente(RegEnteInterviniente interviniente) {
        this.interviniente = interviniente;
    }

    public ReportesView getReportes() {
        return reportes;
    }

    public void setReportes(ReportesView reportes) {
        this.reportes = reportes;
    }

    public Integer getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(Integer tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public String getValorConsulta() {
        return valorConsulta;
    }

    public void setValorConsulta(String valorConsulta) {
        this.valorConsulta = valorConsulta;
    }

    public RegEnteIntervinienteLazy getIntervinientesLazy() {
        return intervinientesLazy;
    }

    public void setIntervinientesLazy(RegEnteIntervinienteLazy intervinientesLazy) {
        this.intervinientesLazy = intervinientesLazy;
    }

    public List<RegFicha> getFichas() {
        return fichas;
    }

    public void setFichas(List<RegFicha> fichas) {
        this.fichas = fichas;
    }

    public RegFicha getFicha() {
        return ficha;
    }

    public void setFicha(RegFicha ficha) {
        this.ficha = ficha;
    }

    public RegMovimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(RegMovimiento movimiento) {
        this.movimiento = movimiento;
    }

    public List<RegMovimientoFicha> getMovimientosFichas() {
        return movimientosFichas;
    }

    public void setMovimientosFichas(List<RegMovimientoFicha> movimientosFichas) {
        this.movimientosFichas = movimientosFichas;
    }

    public List<RegMovimientoCliente> getMovimientosInterv() {
        return movimientosInterv;
    }

    public void setMovimientosInterv(List<RegMovimientoCliente> movimientosInterv) {
        this.movimientosInterv = movimientosInterv;
    }

    public List<RegFicha> getFichasInterv() {
        return fichasInterv;
    }

    public void setFichasInterv(List<RegFicha> fichasInterv) {
        this.fichasInterv = fichasInterv;
    }

    public List<RegMovimientoRepresentante> getRepresentantes() {
        return representantes;
    }

    public void setRepresentantes(List<RegMovimientoRepresentante> representantes) {
        this.representantes = representantes;
    }

    public List<RegMovimientoSocios> getSocios() {
        return socios;
    }

    public void setSocios(List<RegMovimientoSocios> socios) {
        this.socios = socios;
    }

    public List<RegMovimientoCapital> getCapitales() {
        return capitales;
    }

    public void setCapitales(List<RegMovimientoCapital> capitales) {
        this.capitales = capitales;
    }

    public List<RegMovimientoCliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<RegMovimientoCliente> clientes) {
        this.clientes = clientes;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public String getLinderos() {
        return linderos;
    }

    public void setLinderos(String linderos) {
        this.linderos = linderos;
    }

    public RegMovimientosLazy getMovimientosLazy() {
        return movimientosLazy;
    }

    public void setMovimientosLazy(RegMovimientosLazy movimientosLazy) {
        this.movimientosLazy = movimientosLazy;
    }

    public RegFichaLazy getFichasLazy() {
        return fichasLazy;
    }

    public void setFichasLazy(RegFichaLazy fichasLazy) {
        this.fichasLazy = fichasLazy;
    }

    public Boolean getShowMovs() {
        return showMovs;
    }

    public void setShowMovs(Boolean showMovs) {
        this.showMovs = showMovs;
    }

    public Boolean getShowFichas() {
        return showFichas;
    }

    public void setShowFichas(Boolean showFichas) {
        this.showFichas = showFichas;
    }

    public RegMovimientosLazy getLazyMovs() {
        return lazyMovs;
    }

    public void setLazyMovs(RegMovimientosLazy lazyMovs) {
        this.lazyMovs = lazyMovs;
    }

    public RegFichaLazy getLazyFichas() {
        return lazyFichas;
    }

    public void setLazyFichas(RegFichaLazy lazyFichas) {
        this.lazyFichas = lazyFichas;
    }

    public Boolean getRealizarTarea() {
        return realizarTarea;
    }

    public void setRealizarTarea(Boolean realizarTarea) {
        this.realizarTarea = realizarTarea;
    }

    public Boolean getShowInterv() {
        return showInterv;
    }

    public void setShowInterv(Boolean showInterv) {
        this.showInterv = showInterv;
    }

    public Boolean getShowBtn() {
        return showBtn;
    }

    public void setShowBtn(Boolean showBtn) {
        this.showBtn = showBtn;
    }

    public Long getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Long idTarea) {
        this.idTarea = idTarea;
    }

    public Integer getCantMovs() {
        return cantMovs;
    }

    public void setCantMovs(Integer cantMovs) {
        this.cantMovs = cantMovs;
    }

    public Integer getCantFich() {
        return cantFich;
    }

    public void setCantFich(Integer cantFich) {
        this.cantFich = cantFich;
    }

    public RegEnteInterviniente getSelect() {
        return select;
    }

    public void setSelect(RegEnteInterviniente select) {
        this.select = select;
    }

    public String getCadena() {
        return cadena;
    }

    public void setCadena(String cadena) {
        this.cadena = cadena;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    public RegLibro getLibroConsEsp() {
        return libroConsEsp;
    }

    public void setLibroConsEsp(RegLibro libroConsEsp) {
        this.libroConsEsp = libroConsEsp;
    }

    public Long getInscripcionConsEsp() {
        return inscripcionConsEsp;
    }

    public void setInscripcionConsEsp(Long inscripcionConsEsp) {
        this.inscripcionConsEsp = inscripcionConsEsp;
    }

    public Long getRepertorioConsEsp() {
        return repertorioConsEsp;
    }

    public void setRepertorioConsEsp(Long repertorioConsEsp) {
        this.repertorioConsEsp = repertorioConsEsp;
    }

    public Date getDesdeConsEsp() {
        return desdeConsEsp;
    }

    public void setDesdeConsEsp(Date desdeConsEsp) {
        this.desdeConsEsp = desdeConsEsp;
    }

    public Date getHastaConsEsp() {
        return hastaConsEsp;
    }

    public void setHastaConsEsp(Date hastaConsEsp) {
        this.hastaConsEsp = hastaConsEsp;
    }

    public RegActo getActoConsEsp() {
        return actoConsEsp;
    }

    public void setActoConsEsp(RegActo actoConsEsp) {
        this.actoConsEsp = actoConsEsp;
    }

    public List<RegMovimiento> getMovimientosSeleccionados() {
        return movimientosSeleccionados;
    }

    public void setMovimientosSeleccionados(List<RegMovimiento> movimientosSeleccionados) {
        this.movimientosSeleccionados = movimientosSeleccionados;
    }

    public String getUrlDownload() {
        return urlDownload;
    }

    public void setUrlDownload(String urlDownload) {
        this.urlDownload = urlDownload;
    }

    public RegLibro getLibroIndice() {
        return libroIndice;
    }

    public void setLibroIndice(RegLibro libroIndice) {
        this.libroIndice = libroIndice;
    }

    public Date getDesdeIndice() {
        return desdeIndice;
    }

    public void setDesdeIndice(Date desdeIndice) {
        this.desdeIndice = desdeIndice;
    }

    public Date getHastaIndice() {
        return hastaIndice;
    }

    public void setHastaIndice(Date hastaIndice) {
        this.hastaIndice = hastaIndice;
    }

}
