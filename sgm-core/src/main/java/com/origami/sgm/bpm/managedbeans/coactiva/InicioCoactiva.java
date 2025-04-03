/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.coactiva;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CoaAbogado;
import com.origami.sgm.entities.CoaEstadoJuicio;
import com.origami.sgm.entities.CoaJuicio;
import com.origami.sgm.entities.CoaJuicioPredio;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import org.activiti.engine.runtime.ProcessInstance;
import util.Faces;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class InicioCoactiva extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    protected FichaIngresoNuevoServices fs;

    @javax.inject.Inject
    protected RecaudacionesService rs;

    @javax.inject.Inject
    protected Entitymanager manager;

    @Inject
    private ServletSession ss;

    private List<String> ciudadelas;
    protected Boolean urbanistico = true;
    protected Boolean guardado = false;
    protected Boolean showTable = false;
    protected BigDecimal totalJuicio = new BigDecimal("0.00");
    protected CatPredioLazy predios;
    protected CoaJuicio juicio = new CoaJuicio();
    protected Integer estado = 1;
    protected String observacion;
    protected String formatoArchivos;
    protected HistoricoTramites ht = new HistoricoTramites();
    protected List<AclUser> users = new ArrayList<>();
    protected List<CoaJuicioPredio> detalleJuicio = new ArrayList<>();
    protected List<CoaAbogado> listAbogados = new ArrayList<>();
    protected List<CoaEstadoJuicio> listEstados = new ArrayList<>();
    protected List<RenLiquidacion> impuestos = new ArrayList<>();
    protected CatPredioModel predioModel = new CatPredioModel();
    protected List<CatCiudadela> ciudadelasEntidad;
    protected Map<String, Object> parametros;
    protected CatPredio predioUrbanoConsulta;
    protected List<CatPredio> prediosUrbanosConsultaSeleccionados;
    protected List<CatPredio> prediosUrbanosConsulta;
    protected List<CatPredio> prediosUrbanos = new ArrayList<>();

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            ciudadelasEntidad = manager.findAllObjectOrder(CatCiudadela.class, new String[]{"nombre"}, Boolean.TRUE);
            juicio.setFechaJuicio(new Date());
            ciudadelas = fs.getListNombresCdla();
            listAbogados = rs.getListAbogadosJuicios();
            listEstados = rs.getListEstadosJuicios();
            users = rs.getUsuariosByRolId(200L); // ROL ASISTENTE_ADMINISTRATIVO_TESORERIA
            formatoArchivos = SisVars.formatoArchivos;
        } catch (Exception e) {
            Logger.getLogger(InicioCoactiva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public SelectItem[] getLisUrbanizaciones() {
        int cantRegis = ciudadelas.size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            options[i + 1] = new SelectItem(ciudadelas.get(i), ciudadelas.get(i));
        }
        return options;
    }

    public void showDlgPredios() {
        predios = new CatPredioLazy("A");
        JsfUti.update("formPredSel");
        JsfUti.executeJS("PF('selPredio').show();");
    }

    public void actualizaForm() {
        if (this.validaCampos()) {
            showTable = true;
            JsfUti.update("mainForm");
        }
    }

    public void closeDialog() {
        JsfUti.messageInfo(null, "Correcto", "Archivo Agregados Correctamente");
    }

    public void consultarPredioUrbano() {
        predioUrbanoConsulta = null;
        impuestos = new ArrayList<>();
        prediosUrbanosConsultaSeleccionados = null;
        prediosUrbanosConsulta = new ArrayList<>();
        parametros = new HashMap<>();
        prediosUrbanos = new ArrayList<>();
        try {
            switch (predioModel.getTipoConsultaUrbano().intValue()) {
                case 1://NUMERO PREDIAL
                    if (predioModel.getNumPredio() != null && predioModel.getNumPredio().compareTo(BigInteger.ZERO) > 0) {
                        parametros.put("numPredio", predioModel.getNumPredio());
                        predioUrbanoConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, parametros);
                    }
                    break;
                case 3://CODIGO PREDIAL
                    if (predioModel.getSector() > 0 || predioModel.getMz() > 0 || predioModel.getProvincia() > 0 || predioModel.getCanton() > 0
                            || predioModel.getParroquiaShort() > 0 || predioModel.getZona() > 0 || predioModel.getSolar() > 0 || predioModel.getPiso() >= 0
                            || predioModel.getUnidad() >= 0 || predioModel.getBloque() >= 0) {
                        System.out.println("pilas D: ");
                        parametros = new HashMap<>();
                        parametros.put("estado", "A");
                        parametros.put("sector", predioModel.getSector());
                        parametros.put("mz", predioModel.getMz());
                        parametros.put("provincia", predioModel.getProvincia());
                        parametros.put("canton", predioModel.getCanton());
                        parametros.put("parroquia", predioModel.getParroquiaShort());
                        parametros.put("zona", predioModel.getZona());
                        parametros.put("solar", predioModel.getSolar());
                        parametros.put("piso", predioModel.getPiso());
                        parametros.put("unidad", predioModel.getUnidad());
                        parametros.put("bloque", predioModel.getBloque());

                        prediosUrbanosConsulta = manager.findObjectByParameterList(CatPredio.class, parametros);
                        System.out.println("pilas D: prediosUrbanosConsulta " + prediosUrbanosConsulta.toString());
                    } else {
                        JsfUti.messageError(null, "Error", "Codigo Predial no es valido.");
                    }
                    break;
                case 4://UBICACION
                    if (predioModel.getCiudadela() != null || predioModel.getMzUrb() != null || predioModel.getSlUrb() != null) {
                        if (predioModel.getCiudadela() != null) {
                            parametros.put("ciudadela", predioModel.getCiudadela());
                        }
                        if (predioModel.getMzUrb() != null) {
                            parametros.put("urbMz", predioModel.getMzUrb());
                        }
                        if (predioModel.getSlUrb() != null) {
                            parametros.put("urbSolarnew", predioModel.getSlUrb());
                        }
                        prediosUrbanosConsulta = manager.findObjectByParameterList(CatPredio.class, parametros);
                    } else {
                        JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");
                    }
                    break;
                default:
                    break;
            }
            if (prediosUrbanosConsulta != null && !prediosUrbanosConsulta.isEmpty() && prediosUrbanosConsulta.size() == 1) {
                parametros = new HashMap<>();
                parametros.put("numPredio", prediosUrbanosConsulta.get(0).getNumPredio());
                predioUrbanoConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, parametros);
            }
            if (predioUrbanoConsulta != null) {
                if (!this.prediosUrbanos.contains(predioUrbanoConsulta)) {
                    if (predioSeleccionado(predioUrbanoConsulta)) {
                        this.prediosUrbanos.add(predioUrbanoConsulta);
                    }
                } else {
                    JsfUti.messageInfo(null, "Mensaje", "Predio ya se encuentra agregado.");
                }
            } else {
                if (prediosUrbanosConsulta != null && prediosUrbanosConsulta.size() > 1) {
                    JsfUti.update("frmPredios");
                    JsfUti.executeJS("PF('dlgPrediosConsulta').show();");
                } else {
                    JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(InicioCoactiva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void seleccionarPredio(Long tipoPredio) {
        try {
            parametros = new HashMap<>();
            switch (tipoPredio.intValue()) {
                case 1:
                    if (prediosUrbanosConsultaSeleccionados != null && !prediosUrbanosConsultaSeleccionados.isEmpty()) {
                        for (CatPredio pucs : prediosUrbanosConsultaSeleccionados) {
                            parametros.put("numPredio", pucs.getNumPredio());
                            predioUrbanoConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, parametros);
                            if (!this.prediosUrbanos.contains(predioUrbanoConsulta)) {
                                this.prediosUrbanos.add(predioUrbanoConsulta);
                                predioSeleccionado(predioUrbanoConsulta);
                            }
                        }
                        JsfUti.executeJS("PF('dlgPrediosConsulta').hide();");
                    } else {
                        JsfUti.messageInfo(null, "Mensaje", "Seleccione un predio, luego clic en Seleccionar");
                    }
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            Logger.getLogger(InicioCoactiva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean predioSeleccionado(CatPredio pr) {
        try {
            if (this.validaPredioExistente(pr.getId())) {
                List<RenLiquidacion> temp = new ArrayList<>();
                if (estado == 1) {
                    // OPCION PARA JUICIOS NUEVOS
                    // LAS EMISIONES PREDIALES DEBEN TENER MAS DE UN ANIO DE NO SER PAGADAS
                    temp = rs.getPagoAnualByPredioPendientesCoactiva(pr.getId(), 2L); // ID ESTADO PENDIENTE DE PAGO 2L
                } else if (estado == 2) {
                    // OPCION PARA JUICIOS ANTIGUOS
                    // LAS EMISIONES PREDIALES PUEDEN O NO ESTAR PAGADAS
                    //temp = rs.getPagoAnualByPredioPendientesCoactiva(pr.getId(), null);
                    temp = rs.getEmisionesCoactivaAntigua(pr);
                }
                if (temp != null && !temp.isEmpty()) {
                    for (RenLiquidacion r : temp) {
                        impuestos.add(r);
                    }
                    this.updateValorJuicio();
                    JsfUti.update("mainForm:groupCoactiva");
                    return true;
                } else {
                    JsfUti.messageInfo(null, "Mensaje", "No se encontraron titulos de credito pendientes de pago o ya estan en coactiva para el predio " + pr.getNumPredio() + " .");
                }
            } else {
                JsfUti.messageInfo(null, "Mensaje", "Ya se ingresaron titulos de credito del predio " + pr.getNumPredio() + " .");
            }
        } catch (Exception e) {
            Logger.getLogger(InicioCoactiva.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public void deleteLiquidacion(int indice) {
        impuestos.remove(indice);
        this.updateValorJuicio();
        JsfUti.update("mainForm:groupCoactiva");
    }

    public void updateValorJuicio() {
        totalJuicio = new BigDecimal("0.00");
        for (RenLiquidacion l : impuestos) {
            totalJuicio = totalJuicio.add(l.getTotalPago());
        }
    }

    public Boolean validaPredioExistente(Long idPredio) {
        for (RenLiquidacion l : impuestos) {
            if (l.getPredio().getId().equals(idPredio)) {
                return false;
            }
        }
        return true;
    }

    public void showDetallePredio(CatPredio pr) {
        ss.instanciarParametros();
        ss.agregarParametro("numPredio", pr.getNumPredio());
        ss.agregarParametro("idPredio", pr.getId());
        ss.agregarParametro("edit", false);
        JsfUti.redirectFacesNewTab("/faces/vistaprocesos/catastro/verPredio.xhtml");
    }

    public void showDlgDetalleLiquidacion(RenLiquidacion liq) {

    }

    public void guardarJuicioCoactivo() {
        try {
            if (this.validaEmisiones()) {
                juicio.setFechaIngreso(new Date());
                juicio.setTotalDeuda(totalJuicio);
                juicio.setUsuarioIngreso(session.getName_user());
                if (estado == 1) {//JUICIO NUEVO
                    juicio.setEstadoJuicio(new CoaEstadoJuicio(1L));
                    AclUser user = (AclUser) acl.find(AclUser.class, session.getUserId());
                    ht = new HistoricoTramites();
                    if (user.getEnte() != null) {
                        ht.setSolicitante(user.getEnte());
                    }
                    ht.setNombrePropietario("MUNICIPIO DE " + SisVars.NOMBRECANTON);
                    ht.setUserCreador(session.getUserId());
                    ht = rs.saveJuicioCoactivoNuevo(ht, juicio, impuestos);
                    RenLiquidacion liq = new RenLiquidacion();
                    if (impuestos.size() > 0) {
                        liq = impuestos.get(impuestos.size() - 1);
                    } else {
                        liq = impuestos.get(0);
                    }
                    if (liq != null) {
                        if (liq.getPredio() != null) {
                            ht.setSolar(liq.getPredio().getSolar().toString());
                            ht.setMz(liq.getPredio().getMz().toString());
                        }
                    }
                    this.saveObservacion();
                    this.ingresarTramite();
                } else if (estado == 2) {//JUICIO FINALIZADO
                    juicio.setEstadoJuicio(new CoaEstadoJuicio(4L));
                    if (rs.guardarJuicioCoactivoAntiguo(juicio, impuestos)) {
                        guardado = true;
                        JsfUti.messageInfo(null, "Juicio guardado correctamente!!!", "");
                    }
                }
                JsfUti.update("mainForm");
                if (estado == 1) {
                    this.generarTitulosJuicio(ht.getCoaJuicio());
                }
            }
        } catch (Exception e) {
            JsfUti.messageError(null, "Error", Messages.error);
            Logger.getLogger(InicioCoactiva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean validaCampos() {
        if (juicio.getAbogadoJuicio() == null) {
            JsfUti.messageError(null, "Error", "Debe seleccionar un abogado para el juicio.");
            return false;
        }
        if (juicio.getNumeroJuicio() == null || juicio.getNumeroJuicio() < 1) {
            JsfUti.messageError(null, "Error", "Debe digitar el numero de juicio a ingresar.");
            return false;
        }
        if (juicio.getAnioJuicio() == null || juicio.getAnioJuicio() < 1) {
            JsfUti.messageError(null, "Error", "Debe digitar el anio de juicio a ingresar.");
            return false;
        }
        if (juicio.getFechaJuicio().after(new Date())) {
            JsfUti.messageError(null, "Error", "La fecha de juicio debe ser menor a la fecha de hoy.");
            return false;
        }
        if (juicio.getUsuarioAsignado() == null) {
            JsfUti.messageError(null, "Error", "Debe seleccionar el juncionario de tesoreria.");
            return false;
        }
        if (rs.consultaJuicioByNumeroYanio(juicio.getNumeroJuicio(), juicio.getAnioJuicio())) {
            JsfUti.messageError(null, "Error", "Ya se encuentra ingresado un juicio coactivo con el mismo numero y mismo anio.");
            return false;
        }
        return true;
    }

    public Boolean validaEmisiones() {
        if (impuestos.isEmpty()) {
            JsfUti.messageError(null, "Error", "No hay titulos de credito ingresados para el juicio.");
            return false;
        }
        return true;
    }

    public void saveObservacion() {
        try {
            Observaciones ob = new Observaciones();
            ob.setFecCre(new Date());
            ob.setIdTramite(ht);
            ob.setObservacion((observacion != null && !observacion.trim().isEmpty()) ? observacion : "INGRESO DE JUICIO");
            ob.setEstado(Boolean.TRUE);
            ob.setUserCre(session.getName_user());
            ob.setTarea("Ingreso de Juicio Coactivo " + juicio.getNumeroJuicio() + "-" + juicio.getAnioJuicio());
            acl.persist(ob);
        } catch (Exception e) {
            Logger.getLogger(InicioCoactiva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void ingresarTramite() {
        try {

            HashMap par = new HashMap<>();
            par.put("carpeta", ht.getTipoTramite().getCarpeta());
            par.put("tramite", ht.getId());
            par.put("listaArchivos", this.getFiles());
            par.put("tarea", 1);
            par.put("devolutiva", Boolean.FALSE);
            par.put("prioridad", 50);
            // SE MODIFICO PARA QUE LA TAREA LE LLEGUE AL USUARIO SELECCIONADO
            // YA NO AL JEFE DE TESORERIA, UN MISMO USUARIO SIGUE TODO EL PROCESO
            //par.put("juezcoactiva", rs.getAclUserByRol(104L).getUsuario()); // ID ROL JEFE TESORERIA
            par.put("juezcoactiva", juicio.getUsuarioAsignado().getUsuario());
            // SE MODIFICO PARA QUE LA TAREA LE LLEGUE AL USUARIO SELECCIONADO
            // YA NO AL ABOGADO QUE TIENE A CARGO EL JUICIO
            //par.put("abogado", juicio.getAbogadoJuicio().getAclUser().getUsuario());
            par.put("abogado", juicio.getUsuarioAsignado().getUsuario());

            ProcessInstance p = this.startProcessByDefinitionKey(ht.getTipoTramite().getActivitykey(), par);
            if (p != null) {
                this.setVariableByProcessInstance(p.getId(), "subCarpeta", ht.getId() + "-" + p.getId());

                ht.setCarpetaRep(ht.getId() + "-" + p.getId());
                ht.setIdProceso(p.getId());
                ht.setIdProcesoTemp(p.getId());
                acl.persist(ht);
                guardado = true;
                JsfUti.messageInfo(null, "Juicio guardado correctamente!!!", "");
            } else {
                JsfUti.messageError(null, Messages.error, "");
            }
        } catch (Exception e) {
            Logger.getLogger(InicioCoactiva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void redirectIngresoJuicio() {
        //MODIFICADO 26/11/2016 : MANTENER DATOS DEL FORMULARIO FECHA-AB-FUNC
        //JsfUti.redirectFaces("/vistaprocesos/coactiva/ingresoJuicioCoactiva.xhtml");
        Date fechaJuicio;
        Integer anioJuicio;
        CoaAbogado abJuicio;
        AclUser userJuicio;
        if (juicio != null) {
            fechaJuicio = juicio.getFechaJuicio();
            anioJuicio = juicio.getAnioJuicio();
            abJuicio = juicio.getAbogadoJuicio();
            userJuicio = juicio.getUsuarioAsignado();
            juicio = new CoaJuicio();
            juicio.setFechaJuicio(fechaJuicio);
            juicio.setAnioJuicio(anioJuicio);
            juicio.setAbogadoJuicio(abJuicio);
            juicio.setUsuarioAsignado(userJuicio);
            guardado = false;
            showTable = false;
            estado = 1;
            totalJuicio = new BigDecimal("0.00");
            predioModel = new CatPredioModel();
            impuestos = new ArrayList<>();
            prediosUrbanos = new ArrayList<>();
        }

    }

    public void showDlgDocumento() {
        if (estado == 1) {
            JsfUti.update("formDoc");
            JsfUti.executeJS("PF('dlgDocumento').show();");
        } else {
            JsfUti.messageError(null, "Solo se pueden adjuntar docuementos a los juicios nuevos.", "");
        }
    }

    public void generarTitulosJuicio(CoaJuicio ju) {
        try {
            ss.borrarParametros();
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreReporte("masterTituloCredito");
            ss.setNombreSubCarpeta("coactiva");
            ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
            ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/coactiva/"));
            
            ss.agregarParametro("ID_JUICIO", ju.getId());
            ss.agregarParametro("TRAMITE", ju.getTramite() == null ? null : ju.getTramite().getId());
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
            Logger.getLogger(InicioCoactiva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getFormatoArchivos() {
        return formatoArchivos;
    }

    public void setFormatoArchivos(String formatoArchivos) {
        this.formatoArchivos = formatoArchivos;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Boolean getUrbanistico() {
        return urbanistico;
    }

    public void setUrbanistico(Boolean urbanistico) {
        this.urbanistico = urbanistico;
    }

    public Boolean getGuardado() {
        return guardado;
    }

    public void setGuardado(Boolean guardado) {
        this.guardado = guardado;
    }

    public CatPredioLazy getPredios() {
        return predios;
    }

    public void setPredios(CatPredioLazy predios) {
        this.predios = predios;
    }

    public List<String> getCiudadelas() {
        return ciudadelas;
    }

    public void setCiudadelas(List<String> ciudadelas) {
        this.ciudadelas = ciudadelas;
    }

    public CoaJuicio getJuicio() {
        return juicio;
    }

    public void setJuicio(CoaJuicio juicio) {
        this.juicio = juicio;
    }

    public List<CoaAbogado> getListAbogados() {
        return listAbogados;
    }

    public void setListAbogados(List<CoaAbogado> listAbogados) {
        this.listAbogados = listAbogados;
    }

    public List<CoaEstadoJuicio> getListEstados() {
        return listEstados;
    }

    public void setListEstados(List<CoaEstadoJuicio> listEstados) {
        this.listEstados = listEstados;
    }

    public List<CoaJuicioPredio> getDetalleJuicio() {
        return detalleJuicio;
    }

    public void setDetalleJuicio(List<CoaJuicioPredio> detalleJuicio) {
        this.detalleJuicio = detalleJuicio;
    }

    public List<RenLiquidacion> getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(List<RenLiquidacion> impuestos) {
        this.impuestos = impuestos;
    }

    public BigDecimal getTotalJuicio() {
        return totalJuicio;
    }

    public void setTotalJuicio(BigDecimal totalJuicio) {
        this.totalJuicio = totalJuicio;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public Boolean getShowTable() {
        return showTable;
    }

    public void setShowTable(Boolean showTable) {
        this.showTable = showTable;
    }

    public List<AclUser> getUsers() {
        return users;
    }

    public void setUsers(List<AclUser> users) {
        this.users = users;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public CatPredioModel getPredioModel() {
        return predioModel;
    }

    public void setPredioModel(CatPredioModel predioModel) {
        this.predioModel = predioModel;
    }

    public List<CatCiudadela> getCiudadelasEntidad() {
        return ciudadelasEntidad;
    }

    public void setCiudadelasEntidad(List<CatCiudadela> ciudadelasEntidad) {
        this.ciudadelasEntidad = ciudadelasEntidad;
    }

    public List<CatPredio> getPrediosUrbanosConsultaSeleccionados() {
        return prediosUrbanosConsultaSeleccionados;
    }

    public void setPrediosUrbanosConsultaSeleccionados(List<CatPredio> prediosUrbanosConsultaSeleccionados) {
        this.prediosUrbanosConsultaSeleccionados = prediosUrbanosConsultaSeleccionados;
    }

    public List<CatPredio> getPrediosUrbanosConsulta() {
        return prediosUrbanosConsulta;
    }

    public void setPrediosUrbanosConsulta(List<CatPredio> prediosUrbanosConsulta) {
        this.prediosUrbanosConsulta = prediosUrbanosConsulta;
    }

}
