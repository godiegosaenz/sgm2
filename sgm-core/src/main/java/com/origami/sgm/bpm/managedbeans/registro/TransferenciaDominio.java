/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.models.ConsultaMovimientoModel;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioS6;
import com.origami.sgm.entities.CatTransferenciaDominio;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegFichaPropietarios;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoFicha;
import com.origami.sgm.entities.RegpCertificadosInscripciones;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.managedbeans.ClienteTramite;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Angel Navarro,CarlosLoorVargas
 */
@Named
@ViewScoped
public class TransferenciaDominio extends ClienteTramite implements Serializable {

    @javax.inject.Inject
    protected FichaIngresoNuevoServices fichaServices;

    @javax.inject.Inject
    protected RegistroPropiedadServices propiedadServices;

    @Inject
    protected ServletSession servletSession;

    protected Boolean natural = true;
    protected Boolean esPersona = true;
    protected Boolean residente = true;
    protected Boolean guardado = false;
    protected Boolean realizado = false;
    protected Boolean impreso = false;
    protected Boolean codPredial = false;
    protected String documento;
    protected Long idTarea;
    protected CatEnte ente = new CatEnte();
    protected RegFicha ficha = new RegFicha();
    protected CatPredio predio = new CatPredio();
    protected CatPredioS6 predio6 = new CatPredioS6();
    protected CatPredioLazy predioLazy;
    protected CatEnteLazy enteLazy;
    protected CtlgItem tipoPropietario;
    protected CtlgItem item;
    protected RegMovimiento movimiento = new RegMovimiento();
    protected CatPredioPropietario propietario = new CatPredioPropietario();
    protected ConsultaMovimientoModel modelo = new ConsultaMovimientoModel();
    protected List<RegMovimientoFicha> listMovFich = new ArrayList<>();
    protected List<CatPredioPropietario> propietarios = new ArrayList<>();
    protected List<CatPredioPropietario> propietariosNew = new ArrayList<>();
    protected List<String> nombresUrbanList = new ArrayList<>();
    protected List<CtlgItem> listTipoProp = new ArrayList<>();

    protected List<RegFichaPropietarios> listPropsFicha = new ArrayList<>();
    protected RegpCertificadosInscripciones tareaCatastro;
    protected CatTransferenciaDominio td;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            initView();
        }
    }

    private void initView() {
        try {
            if (idTarea != null && session.getTaskID() != null) {
                tareaCatastro = (RegpCertificadosInscripciones) acl.find(RegpCertificadosInscripciones.class, idTarea);
                ficha = fichaServices.getFichaByNumFichaByTipo(tareaCatastro.getNumFicha(), 1L);
                if (ficha != null) {
                    td = (CatTransferenciaDominio) acl.find(Querys.getCatTransferenciaByTarea, new String[]{"idTarea"}, new Object[]{idTarea});
                    if (td != null) {
                        guardado = true;
                        realizado = true;
                    }
                    this.setTaskId(session.getTaskID());
                    listMovFich = fichaServices.getRegMovimientoFichasList(ficha.getId());
                    if (ficha.getPredio() != null) {
                        predio = ficha.getPredio();
                        if (predio.getCatPredioS6() != null) {
                            predio6 = predio.getCatPredioS6();
                        }
                        if (predio.getCatPredioPropietarioCollection() != null) {
                            propietarios = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();
                        }
                    }
                    propietariosNew = fichaServices.getNewsPropietariosPredioByFicha(ficha.getId());
                    nombresUrbanList = fichaServices.getListNombresCdla();
                    listTipoProp = propiedadServices.lisCtlgItems("predio.propietario.tipo");
                    tipoPropietario = (CtlgItem) acl.find(CtlgItem.class, 56L); // tipo propietario por defecto
                    item = (CtlgItem) acl.find(CtlgItem.class, 56L); // tipo propietario por defecto
                } else {
                    this.continuar();
                }
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            Logger.getLogger(TransferenciaDominio.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgMovs(RegMovimiento mov) {
        try {
            movimiento = mov;
            modelo = propiedadServices.getConsultaMovimiento(mov.getId());
            if (modelo != null) {
                JsfUti.update("formMovRegistralSelec");
                JsfUti.executeJS("PF('dlgMovRegistralSelec').show();");
            } else {
                JsfUti.messageError(null, "No se pudo hacer la consulta.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(TransferenciaDominio.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgPredios() {
        predioLazy = new CatPredioLazy("A");
        JsfUti.update("formSelecPredio");
        JsfUti.executeJS("PF('dlgSelectPredio').show();");
    }

    public SelectItem[] getLisUrbanizaciones() {
        int cantRegis = nombresUrbanList.size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            options[i + 1] = new SelectItem(nombresUrbanList.get(i), nombresUrbanList.get(i));
        }
        return options;
    }

    public void selectPredio(CatPredio pred) {
        RegFicha temp = fichaServices.getRegFichaByPredio(pred.getId());
        if (temp == null) {
            predio = pred;
            if (pred.getCatPredioS6() != null) {
                predio6 = pred.getCatPredioS6();
            }
            if (pred.getCatPredioPropietarioCollection() != null) {
                propietarios = (List<CatPredioPropietario>) pred.getCatPredioPropietarioCollection();
            }
            JsfUti.update("formTransfDomin:accPanelTD:pnlGrpPredio");
            JsfUti.executeJS("PF('dlgSelectPredio').hide()");
        } else {
            JsfUti.messageInfo(null, "No se puede seleccionar", "El predio " + pred.getNumPredio() + " esta relacionado con la ficha registral " + temp.getNumFicha());
        }
    }

    public void showDlgEntes() {
        enteLazy = new CatEnteLazy(natural, "A");
        JsfUti.update("formSelectInterv");
        JsfUti.executeJS("PF('dlgSelectReprest').show();");
    }

    public void cambioLazyEnte() {
        enteLazy = new CatEnteLazy(natural, "A");
    }

    public void buscarEnte() {
        try {
            if (predio.getId() != null) {
                //if (documento != null && vcu.comprobarDocumento(documento)) {
                if (documento != null) {
                    ente = fichaServices.getCatEnte(documento);
                    if (ente != null) {
                        if (!this.existeEnte()) {
                            propietario = new CatPredioPropietario();
                            propietario.setEnte(ente);
                            //propietario.setPredio(predio);
                            propietario.setTipo(item);
                            propietario.setEstado("A");
                            propietario.setEsResidente(true);
                            propietariosNew.add(propietario);
                        } else {
                            ente = new CatEnte();
                            JsfUti.messageInfo(null, "Ya fue ingresado el Propietario.", "");
                        }
                    } else {
                        if (vcu.comprobarDocumento(documento)) {
                            ente = new CatEnte();
                            this.inicializarVariables();
                            persona.setCiRuc(documento);
                            esPersona = documento.length() == 10;
                            persona.setEsPersona(esPersona);
                            JsfUti.messageInfo(null, Messages.enteNoExiste, "");
                            JsfUti.update("formInterv");
                            JsfUti.executeJS("PF('dlgInterv').show();");
                        } else {
                            JsfUti.messageError(null, "Documento ingresado invalido.", "");
                        }
                    }
                } else {
                    JsfUti.messageError(null, "Documento ingresado invalido.", "");
                }
            } else {
                JsfUti.messageError(null, "Debe seleccionar primero el predio.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(TransferenciaDominio.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean existeEnte() {
        for (CatPredioPropietario p : propietariosNew) {
            if (p.getEnte().getId().equals(ente.getId())) {
                return true;
            }
        }
        return false;
    }

    public void agregarRepresentante(CatEnte e) {
        if (predio.getId() != null) {
            ente = e;
            if (!this.existeEnte()) {
                propietario = new CatPredioPropietario();
                propietario.setEnte(e);
                ///propietario.setPredio(predio);
                propietario.setTipo(item);
                propietario.setEsResidente(true);
                propietario.setEstado("A");
                propietariosNew.add(propietario);
                JsfUti.update("formTransfDomin:accPanelTD:dtNewProp");
            } else {
                ente = new CatEnte();
                JsfUti.messageInfo(null, "Ya fue ingresado el Propietario.", "");
            }
        } else {
            JsfUti.messageError(null, "Debe seleccionar primero el predio.", "");
        }
    }

    public void eliminarPropietario(int indice) {
        propietariosNew.remove(indice);
    }

    public void editPropietario(CatPredioPropietario pro) {
        persona = pro.getEnte();
        esPersona = persona.getEsPersona();
        tipoPropietario = pro.getTipo();
        residente = pro.getEsResidente();
        JsfUti.update("formInterv");
        JsfUti.executeJS("PF('dlgInterv').show();");
    }

    public void guardarInterviniente() {
        Boolean flag;
        Boolean nuevo;
        persona.setEsPersona(esPersona);
        if (persona.getId() == null) {
            flag = guardarClienteSinCorreo();
            nuevo = true;
        } else {
            flag = editarClienteSinCorreo();
            nuevo = false;
        }
        if (flag) {
            CatEnte e = (CatEnte) acl.find(CatEnte.class, persona.getId());
            propietario = new CatPredioPropietario();
            propietario.setEnte(e);
            //propietario.setPredio(predio);
            propietario.setTipo(tipoPropietario);
            propietario.setEsResidente(residente);
            propietario.setEstado("A");
            if (nuevo) {
                propietariosNew.add(propietario);
            } else {
                int ind = this.returnIndex(persona);
                propietariosNew.set(ind, propietario);
            }
            JsfUti.executeJS("PF('dlgInterv').hide();");
            JsfUti.update("formTransfDomin:accPanelTD:dtNewProp");
        }
    }

    public int returnIndex(CatEnte e) {
        int temp = 0;
        int ind = -1;
        for (CatPredioPropietario i : propietariosNew) {
            if (i.getEnte() != null) {
                if (i.getEnte().getId().equals(e.getId())) {
                    ind = temp;
                }
            }
            temp++;
        }
        return ind;
    }

    public void dialogConfirmacion() {
        JsfUti.update("formConfirm");
        JsfUti.executeJS("PF('dlgConfirm').show();");
    }

    public void guardar() {
        try {
            if (predio.getId() != null) {
                if (!propietariosNew.isEmpty()) {
                    if (tareaCatastro.getIdMovimiento() != null) {
                        RegMovimiento mov = (RegMovimiento) acl.find(RegMovimiento.class, tareaCatastro.getIdMovimiento());
                        if (mov != null) {
                            ficha.setPredio(predio);
                            acl.persist(ficha);
                            td = new CatTransferenciaDominio();
                            td.setMovimiento(mov);
                            td.setPredio(predio);
                            td.setFecha(new Date());
                            td.setUsuario(session.getName_user());
                            td.setTaskId(this.getTaskId());
                            td.setNumTramite(mov.getNumTramite());
                            td.setTareaRegistro(tareaCatastro);
                            td = propiedadServices.registrarTransferencia(td, propietarios);
                            guardado = propiedadServices.saveTransferenciaDominio(predio, propietarios, propietariosNew);
                            if (guardado) {
                                JsfUti.messageInfo(null, "Transferencia Guardada con Exito.", "");
                                JsfUti.update("formTransfDomin");
                                JsfUti.executeJS("PF('dlgConfirm').hide();");
                            }
                        }
                    }
                } else {
                    JsfUti.messageError(null, "Debe ingresar el(los) propietario(s) para la transferencia de dominio.", "");
                }
            } else {
                JsfUti.messageError(null, "Debe seleccionar el predio para la transferencia de dominio.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(TransferenciaDominio.class.getName()).log(Level.SEVERE, null, e);
            JsfUti.messageError(null, Messages.error, "");
        }
    }

    public void imprimirPdf() {
        try {
            if (guardado && td.getId() != null) {
                servletSession.instanciarParametros();
                servletSession.setNombreReporte("transferenciaDominio");
                servletSession.setNombreSubCarpeta("registroPropiedad");
                servletSession.setTieneDatasource(true);
                servletSession.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.sisLogo1));
                servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "reportes/registroPropiedad/");
                servletSession.agregarParametro("USUARIO", session.getName_user());
                servletSession.agregarParametro("IDTRANSFERENCIA", td.getId());
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
                tareaCatastro.setRealizado(true);
                tareaCatastro.setFechaFin(new Date());
                acl.persist(tareaCatastro);
            } else {
                JsfUti.messageError(null, "Debe de guardar la Transferencia de Dominio.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(TransferenciaDominio.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CatPredioLazy getPredioLazy() {
        return predioLazy;
    }

    public void setPredioLazy(CatPredioLazy predioLazy) {
        this.predioLazy = predioLazy;
    }

    public List<String> getNombresUrbanList() {
        return nombresUrbanList;
    }

    public void setNombresUrbanList(List<String> nombresUrbanList) {
        this.nombresUrbanList = nombresUrbanList;
    }

    public List<CatPredioPropietario> getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(List<CatPredioPropietario> propietarios) {
        this.propietarios = propietarios;
    }

    public List<CatPredioPropietario> getPropietariosNew() {
        return propietariosNew;
    }

    public void setPropietariosNew(List<CatPredioPropietario> propietariosNew) {
        this.propietariosNew = propietariosNew;
    }

    public Boolean getResidente() {
        return residente;
    }

    public void setResidente(Boolean residente) {
        this.residente = residente;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    public CtlgItem getTipoPropietario() {
        return tipoPropietario;
    }

    public void setTipoPropietario(CtlgItem tipoPropietario) {
        this.tipoPropietario = tipoPropietario;
    }

    public List<CtlgItem> getListTipoProp() {
        return listTipoProp;
    }

    public void setListTipoProp(List<CtlgItem> listTipoProp) {
        this.listTipoProp = listTipoProp;
    }

    public CatEnteLazy getEnteLazy() {
        return enteLazy;
    }

    public void setEnteLazy(CatEnteLazy enteLazy) {
        this.enteLazy = enteLazy;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public RegFicha getFicha() {
        return ficha;
    }

    public void setFicha(RegFicha ficha) {
        this.ficha = ficha;
    }

    public List<RegMovimientoFicha> getListMovFich() {
        return listMovFich;
    }

    public void setListMovFich(List<RegMovimientoFicha> listMovFich) {
        this.listMovFich = listMovFich;
    }

    public RegMovimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(RegMovimiento movimiento) {
        this.movimiento = movimiento;
    }

    public ConsultaMovimientoModel getModelo() {
        return modelo;
    }

    public void setModelo(ConsultaMovimientoModel modelo) {
        this.modelo = modelo;
    }

    public CatPredioS6 getPredio6() {
        return predio6;
    }

    public void setPredio6(CatPredioS6 predio6) {
        this.predio6 = predio6;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public CatPredioPropietario getPropietario() {
        return propietario;
    }

    public void setPropietario(CatPredioPropietario propietario) {
        this.propietario = propietario;
    }

    public Boolean getNatural() {
        return natural;
    }

    public void setNatural(Boolean natural) {
        this.natural = natural;
    }

    public Boolean getEsPersona() {
        return esPersona;
    }

    public void setEsPersona(Boolean esPersona) {
        this.esPersona = esPersona;
    }

    public Boolean getCodPredial() {
        return codPredial;
    }

    public void setCodPredial(Boolean codPredial) {
        this.codPredial = codPredial;
    }

    public List<RegFichaPropietarios> getListPropsFicha() {
        return listPropsFicha;
    }

    public void setListPropsFicha(List<RegFichaPropietarios> listPropsFicha) {
        this.listPropsFicha = listPropsFicha;
    }

    public Long getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Long idTarea) {
        this.idTarea = idTarea;
    }

    public Boolean getGuardado() {
        return guardado;
    }

    public void setGuardado(Boolean guardado) {
        this.guardado = guardado;
    }

    public Boolean getRealizado() {
        return realizado;
    }

    public void setRealizado(Boolean realizado) {
        this.realizado = realizado;
    }

}
