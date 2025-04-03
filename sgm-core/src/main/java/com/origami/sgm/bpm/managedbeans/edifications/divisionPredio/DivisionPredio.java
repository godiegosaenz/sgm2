/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.divisionPredio;

import com.origami.config.SisVars;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.DivisionPredioServices;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;
import util.PhoneUtils;
import util.Utils;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class DivisionPredio extends BpmManageBeanBaseRoot implements Serializable {

    @Inject
    private UserSession uSession;

    @javax.inject.Inject
    protected DivisionPredioServices servicesDP;

    @javax.inject.Inject
    private Entitymanager services;

    private HistoricoTramites ht;
    private CatPredio datosPredio, dpTemp;
    private Integer numDivision;
    private List<CatPredio> prediosList, prediosListTemp;
    private AclUser userCreador;
    private Boolean validarPrediosList;
    private String cedulaRuc;
    private List<CatEnte> enteList, enteListTemp;
    private List<CatPredioPropietario> lisPropietarios;
    private CatPredioPropietario catpp;
    private List<CtlgItem> propietarioTiposList;
    private EnteCorreo email = new EnteCorreo();
    private EnteTelefono telefono = new EnteTelefono();
    private String correo, telf;
    private CatEnte persona;
    private HashMap<String, Object> paramsActiviti;
    private Observaciones obs;

    @PostConstruct
    public void init() {
        try {
            if (uSession.getTaskID() != null) {
                this.setTaskId(uSession.getTaskID());
                enteListTemp = new ArrayList();
                propietarioTiposList = servicesDP.obtenerCtlgItemListByNombreDeCatalogo("predio.propietario.tipo");
                userCreador = servicesDP.obtenerAclUserPorQuery(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{uSession.getName_user()});
                //ht = (HistoricoTramites) services.find(HistoricoTramites.class, Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString()));
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                if (ht != null) {
                    //datosPredio = (CatPredio) services.find(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{ht.getNumPredio()});
                    datosPredio = servicesDP.obtenerCatPredioPorQuery(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{ht.getNumPredio()});
                }
                prediosListTemp = servicesDP.obtenerCatPrediosHijos(Querys.getPredioHijosByFatherID, new String[]{"numPredio"}, new Object[]{datosPredio.getId()});

                if (prediosListTemp != null) {
                    if (!prediosListTemp.isEmpty()) {
                        JsfUti.redirectFaces("/vistaprocesos/edificaciones/divisionPredio/agregarCatPredioS4S6.xhtml");
                    }
                }
            } else {
                datosPredio = new CatPredio();
                if (datosPredio != null && datosPredio.getId() != null) {
                    prediosListTemp = servicesDP.obtenerCatPrediosHijos(Querys.getPredioHijosByFatherID, new String[]{"numPredio"}, new Object[]{datosPredio.getId()});

                    if (prediosListTemp != null) {
                        if (!prediosListTemp.isEmpty()) {
                            JsfUti.redirectFaces("/vistaprocesos/edificaciones/divisionPredio/agregarCatPredioS4S6.xhtml");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Guarda cada uno de los predios generados.
     */
    public void guardarDivision() {
        if (prediosList == null || prediosList.isEmpty()) {
            JsfUti.messageError(null, "Error", "No se ha creado ninguna División de Predio. Proceda a hacerlo antes de continuar.");
            return;
        }
        for (CatPredio cp : prediosList) {
            if (cp.getCatPredioPropietarioCollection() == null || cp.getCatPredioPropietarioCollection().isEmpty()) {
                JsfUti.messageError(null, "Error", "Uno o más predios no tienen propietarios. Asegúrese de que cada predio tenga al menos un propietario.");
                return;
            }
            if (cp.getNomCompPago() == null || cp.getNomCompPago().equals("")) {
                JsfUti.messageError(null, "Error", "Uno o más predios no tienen el nombre del comprobante de pago. Asegúrese de que cada predio tenga un nombre para el comprobante de pago.");
                return;
            }
        }
        if (servicesDP.guardarCatPredioDivisionPredio(prediosList)) {
            JsfUti.redirectFaces("/vistaprocesos/edificaciones/divisionPredio/agregarCatPredioS4S6.xhtml");
        }

    }

    /**
     * Permite editar el ente de un propietario.
     *
     * @param ente
     */
    public void editarPropietario(CatEnte ente) {
        for (CatPredioPropietario cpp : lisPropietarios) {
            if (cpp.getEnte().equals(ente)) {
                catpp = cpp;
                return;
            }
        }
    }

    /**
     * Permite eliminar un propietario de un predio.
     *
     * @param ente
     */
    public void eliminarPropietario(CatEnte ente) {
        for (CatPredioPropietario cpp : lisPropietarios) {
            if (cpp.getEnte().equals(ente)) {
                if (cpp.getId() != null) {
                    cpp.setEstado("I");
                    servicesDP.actualizarPredioPropietario(cpp);
                }
                lisPropietarios.remove(cpp);
                enteList.remove(ente);
                return;
            }
        }

    }

    /**
     * Agrega un propietario a un predio.
     *
     * @param ente
     */
    public void agregarPropietario(CatEnte ente) {
        CatPredioPropietario propTemp = new CatPredioPropietario();
        CtlgItem item = new CtlgItem();

        try {
            for (CatPredioPropietario cpp : lisPropietarios) {
                if (cpp.getEnte().equals(ente) && cpp.getEstado().equals("A")) {
                    JsfUti.messageError(null, "Error", "El usuario ya ha sido agregado anteriormente. No lo puede volver a agregar.");
                    return;
                }
            }

            if (lisPropietarios != null) {
                propTemp.setPredio(datosPredio);
                propTemp.setTipo(servicesDP.obtenerCtlgItemPorID(new Long(56)));
                propTemp.setEsResidente(true);
                propTemp.setEstado("A");
                propTemp.setModificado("");
                propTemp.setEnte(ente);
                lisPropietarios.add(propTemp);
                //enteList.remove(0);
                cedulaRuc = "";
            }
            actualizarListaPropietarios();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Setea la lista de propietarios del predio seleccionado para ser guardada
     * posteriormente.
     */
    public void guardarDatosPropietario() {
        dpTemp.setCatPredioPropietarioCollection(lisPropietarios);
    }

    /**
     * Setea una nueva lista de propietarios al predio.
     *
     * @param cp
     */
    public void ingresoPropietarios(CatPredio cp) {
        dpTemp = cp;
        lisPropietarios = (List<CatPredioPropietario>) cp.getCatPredioPropietarioCollection();

        if (lisPropietarios == null) {
            lisPropietarios = new ArrayList();
        }

        actualizarListaPropietarios();
    }

    /**
     * Actualiza la lista de propietarios de un predio.
     */
    public void actualizarListaPropietarios() {
        enteList = new ArrayList();
        enteListTemp = new ArrayList();
        if (lisPropietarios != null) {
            for (CatPredioPropietario prop : lisPropietarios) {
                enteList.add(prop.getEnte());
            }
        }
    }

    /**
     * Busca un ente y lo carga en memoria.
     */
    public void buscarEnte() {
        if (this.cedulaRuc.equals("")) {
            return;
        }

        CatEnte tempEnte = servicesDP.obtenerCatEntePorQuery(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
        try {
            if (tempEnte != null) {
                enteListTemp.add(tempEnte);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Si catastro encuentra alguna anomalía puede rechazar la división del
     * predio
     */
    public void rechazarPredio() {
        paramsActiviti = new HashMap<>();
        paramsActiviti.put("tdocs", false);
        paramsActiviti.put("aprobado", false);
        this.completeTask(this.getTaskId(), paramsActiviti);
        this.continuar();
    }

    /**
     * Genera las divisiones del predio y les asigna un código predial a cada
     * uno.
     */
    public void generarDivisionPredio() {
        if (numDivision < 2) {
            JsfUti.messageError(null, "Error", "Se debe ingresar un número mayor a 2 para iniciar la división.");
            return;
        }
        /*
        if(numDivision>9){
            JsfUti.messageError(null, "Error", "El número ingresado es muy grande para iniciar la división.");
            return;
        }*/

        CatPredio predio;
        prediosList = new ArrayList();

        for (int i = 1; i <= numDivision; i++) {

            predio = new CatPredio();

            predio.setSector(datosPredio.getSector());
            predio.setMz(datosPredio.getMz());
            predio.setCdla(datosPredio.getCdla());
            predio.setCiudadela(datosPredio.getCiudadela());
            predio.setMzdiv(datosPredio.getMzdiv());
            predio.setSolar(datosPredio.getSolar());
            predio.setDiv1(datosPredio.getDiv1());
            predio.setDiv2(datosPredio.getDiv2());
            predio.setDiv3(datosPredio.getDiv3());
            predio.setDiv4(datosPredio.getDiv4());
            predio.setDiv5(datosPredio.getDiv5());
            predio.setDiv6(datosPredio.getDiv6());
            predio.setDiv7(datosPredio.getDiv7());
            predio.setDiv8(datosPredio.getDiv8());
            predio.setDiv9(datosPredio.getDiv9());
            predio.setPhh(datosPredio.getPhh());
            predio.setPhv(datosPredio.getPhv());
            predio.setTipoConjunto(datosPredio.getTipoConjunto());
            predio.setNombreUrb(datosPredio.getNombreUrb());
            predio.setUrbSec(datosPredio.getUrbSec());
            predio.setNumPredio(null);
            predio.setPropiedad(datosPredio.getPropiedad());
            predio.setTenencia(datosPredio.getTenencia());
            predio.setSoportaHipoteca(datosPredio.getSoportaHipoteca());
            predio.setUrbMz(datosPredio.getUrbMz());
            predio.setNumeroFicha(null);
            predio.setUrbSolarnew(datosPredio.getUrbSolarnew());
            predio.setUrbSecnew(datosPredio.getUrbSecnew());
            predio.setNomCompPago("");
            predio.setEstado("T");
            predio.setUsuarioCreador(this.userCreador);
            predio.setInstCreacion(new Date());
            predio.setPredioRaiz(BigInteger.valueOf(datosPredio.getId()));

            if (datosPredio.getDiv1() == 0) {
                predio.setDiv1((short) i);
            } else if (datosPredio.getDiv2() == 0) {
                predio.setDiv2((short) i);
            } else if (datosPredio.getDiv3() == 0) {
                predio.setDiv3((short) i);
            } else if (datosPredio.getDiv4() == 0) {
                predio.setDiv4((short) i);
            } else if (datosPredio.getDiv5() == 0) {
                predio.setDiv5((short) i);
            } else if (datosPredio.getDiv6() == 0) {
                predio.setDiv6((short) i);
            } else if (datosPredio.getDiv7() == 0) {
                predio.setDiv7((short) i);
            } else if (datosPredio.getDiv8() == 0) {
                predio.setDiv8((short) i);
            } else if (datosPredio.getDiv9() == 0) {
                predio.setDiv9((short) i);
            } else {
                JsfUti.messageInfo(null, "Info!", "Este predio ya no se puede seguir dividiendo. El sistema no lo soporta.");
            }
            if (!servicesDP.existePredio(predio)) {
                prediosList.add(predio);
            } else {
                JsfUti.messageInfo(null, "Info!", "Ya existe una Propiedad Almacenada con NumPredio=" + predio.getCodigoPredial() + ". Revisar en la Edición.");
            }
        }
        if (!prediosList.isEmpty()) {
            this.validarPrediosList = true;
        } else {
            this.validarPrediosList = false;
            JsfUti.messageError(null, "Error", "No se pudo hacer ninguna división del predio");
        }
    }

    /**
     * Agrega un email a la lista de emails de un ente.
     *
     */
    public void agregarEmail() {
        String emailNew;
        if (correo != null) {
            emailNew = correo.trim();
            Boolean flag = true;
            for (EnteCorreo c : catpp.getEnte().getEnteCorreoCollection()) {
                if (c.getEmail().equals(emailNew)) {
                    flag = false;
                }
            }
            if (flag) {
                if (Utils.validarEmailConExpresion(emailNew)) {
                    email = new EnteCorreo();
                    email.setEmail(emailNew);
                    catpp.getEnte().getEnteCorreoCollection().add(email);
                    emailNew = "";
                } else {
                    JsfUti.messageInfo(null, Messages.correoInvalido, "");
                }
            } else {
                JsfUti.messageInfo(null, Messages.elementoRepetido, "");
            }
        } else {
            JsfUti.messageInfo(null, Messages.campoVacio, "");
        }
    }

    /**
     * Elimina/inactiva un email de la lista de emails de un ente.
     *
     * @param c
     */
    public void eliminarEmail(EnteCorreo c) {
        if (c.getId() != null) {
            catpp.getEnte().getEnteCorreoCollection().remove(c);
            acl.delete(c);
        } else {
            int ind = -1;
            int cont = 0;
            for (EnteCorreo co : catpp.getEnte().getEnteCorreoCollection()) {
                if (co.getEmail().equals(c.getEmail())) {
                    ind = cont;
                }
                cont++;
            }
            if (ind >= 0) {
                catpp.getEnte().getEnteCorreoCollection().remove(ind);
            }
        }
    }

    /**
     * Agrega un número de teléfono a la lista de teléfonos de un ente.
     *
     */
    public void agregarTlfn() {
        if (telf != null) {
            telf = telf.trim();
            Boolean flag = true;
            for (EnteTelefono t : catpp.getEnte().getEnteTelefonoCollection()) {
                if (t.getTelefono().equals(telf)) {
                    flag = false;
                }
            }
            if (flag) {
                if (Utils.validateNumberPattern(telf)) {
                    if (PhoneUtils.getValidNumber(telf, SisVars.region)) {
                        telefono = new EnteTelefono();
                        telefono.setTelefono(telf);
                        catpp.getEnte().getEnteTelefonoCollection().add(telefono);
                        telf = "";
                    } else {
                        JsfUti.messageInfo(null, Messages.tlfnInvalido, "");
                    }
                } else {
                    JsfUti.messageInfo(null, Messages.tlfnInvalido, "");
                }
            } else {
                JsfUti.messageInfo(null, Messages.elementoRepetido, "");
            }
        } else {
            JsfUti.messageInfo(null, Messages.campoVacio, "");
        }
    }

    /**
     * Elimina/inactiva un teléfono de la lista de teléfonos de un ente.
     *
     * @param t
     */
    public void eliminarTlfn(EnteTelefono t) {
        if (t.getId() != null) {
            catpp.getEnte().getEnteTelefonoCollection().remove(t);
            acl.delete(t);
        } else {
            int ind = -1;
            int cont = 0;
            for (EnteTelefono te : catpp.getEnte().getEnteTelefonoCollection()) {
                if (te.getTelefono().equals(t.getTelefono())) {
                    ind = cont;
                }
                cont++;
            }
            if (ind >= 0) {
                catpp.getEnte().getEnteTelefonoCollection().remove(ind);
            }
        }
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public CatPredio getDatosPredio() {
        return datosPredio;
    }

    public void setDatosPredio(CatPredio datosPredio) {
        this.datosPredio = datosPredio;
    }

    public Integer getNumDivision() {
        return numDivision;
    }

    public void setNumDivision(Integer numDivision) {
        this.numDivision = numDivision;
    }

    public List<CatPredio> getPrediosList() {
        return prediosList;
    }

    public void setPrediosList(List<CatPredio> prediosList) {
        this.prediosList = prediosList;
    }

    public AclUser getUserCreador() {
        return userCreador;
    }

    public void setUserCreador(AclUser userCreador) {
        this.userCreador = userCreador;
    }

    public Boolean getValidarPrediosList() {
        return validarPrediosList;
    }

    public void setValidarPrediosList(Boolean validarPrediosList) {
        this.validarPrediosList = validarPrediosList;
    }

    public String getCedulaRuc() {
        return cedulaRuc;
    }

    public void setCedulaRuc(String cedulaRuc) {
        this.cedulaRuc = cedulaRuc;
    }

    public List<CatEnte> getEnteList() {
        return enteList;
    }

    public void setEnteList(List<CatEnte> enteList) {
        this.enteList = enteList;
    }

    public List<CatPredioPropietario> getLisPropietarios() {
        return lisPropietarios;
    }

    public void setLisPropietarios(List<CatPredioPropietario> lisPropietarios) {
        this.lisPropietarios = lisPropietarios;
    }

    public CatPredio getDpTemp() {
        return dpTemp;
    }

    public void setDpTemp(CatPredio dpTemp) {
        this.dpTemp = dpTemp;
    }

    public List<CatEnte> getEnteListTemp() {
        return enteListTemp;
    }

    public void setEnteListTemp(List<CatEnte> enteListTemp) {
        this.enteListTemp = enteListTemp;
    }

    public CatPredioPropietario getCatpp() {
        return catpp;
    }

    public void setCatpp(CatPredioPropietario catpp) {
        this.catpp = catpp;
    }

    public List<CtlgItem> getPropietarioTiposList() {
        return propietarioTiposList;
    }

    public void setPropietarioTiposList(List<CtlgItem> propietarioTiposList) {
        this.propietarioTiposList = propietarioTiposList;
    }

    public EnteCorreo getEmail() {
        return email;
    }

    public void setEmail(EnteCorreo email) {
        this.email = email;
    }

    public EnteTelefono getTelefono() {
        return telefono;
    }

    public void setTelefono(EnteTelefono telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelf() {
        return telf;
    }

    public void setTelf(String telf) {
        this.telf = telf;
    }

    public CatEnte getPersona() {
        return persona;
    }

    public void setPersona(CatEnte persona) {
        this.persona = persona;
    }

    public List<CatPredio> getPrediosListTemp() {
        return prediosListTemp;
    }

    public void setPrediosListTemp(List<CatPredio> prediosListTemp) {
        this.prediosListTemp = prediosListTemp;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

}
