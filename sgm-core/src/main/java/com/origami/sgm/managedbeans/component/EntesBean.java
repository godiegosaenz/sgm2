/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import com.origami.sgm.services.interfaces.solicitudServico.SolicitudServicosServices;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import util.JsfUti;
import util.PhoneUtils;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class EntesBean implements Serializable {

    private static final Logger LOG = Logger.getLogger(EntesBean.class.getName());

// variables de validaciones
    private Integer tipoEnte = 1;
    private Boolean tipoSelect;
    private Long idEnte;
    private Boolean nuevo = false;
    private String verTemp;
    private Boolean ver;
    private String esPersona;
    private String ciRuc;

// variables general
    private CatEnte ente;
    private CatEnte entidadTemp;
    private CatEnte representanteLegal;
    private CatEnte conyuge;

// Inject
    @Inject
    private CatastroServices serv;
    @Inject
    private PropiedadHorizontalServices phServ;
    @javax.inject.Inject
    private SolicitudServicosServices service;

    // LAZY 
    private CatEnteLazy entes;

    public void load() {
        if (!JsfUti.isAjaxRequest()) {
            if (idEnte == null) { // nuevo
                System.out.println("Es persona " + esPersona);
                Boolean persona = null;
                persona = Boolean.valueOf(esPersona == null ? Boolean.TRUE.toString() : esPersona);
                ente = new CatEnte(null, persona, "A");
                if (persona) {
                    tipoEnte = 1;
                } else {
                    tipoEnte = 2;
                }
                ente.setCiRuc(ciRuc);
                ente.setExcepcionales(false);
            } else {
                ente = phServ.getFichaServices().getCatEnteById(idEnte);
            }
            if (ente.getEsPersona()) {
                conyuge = new CatEnte();
            } else {
                representanteLegal = new CatEnte();
            }
            this.cargarDefault();
        }
    }

    private void cargarDefault() {
        if (ente.getTipoDocumento() == null) {
            ente.setTipoDocumento(serv.getDefaultItem("ente.tipo_documento"));
        }
        if (ente.getEstadoCivil() == null) {
            ente.setEstadoCivil(serv.getDefaultItem("ente.estado_civil"));
        }
        if (ente.getDiscapacidad() == null) {
            ente.setDiscapacidad(serv.getDefaultItem("ente.discapacidad"));
        }
    }

    public void buscarEnte() {
        try {
            if (ente.getCiRuc() == null) {
                return;
            }
            if (ente.getCiRuc().length() == 0) {
                return;
            }
            CatEnte temp = phServ.getPermiso().getCatEnteByCiRuc(ente.getCiRuc());
            if (temp == null) {
                JsfUti.messageError(null, "Error!", "Numero de identificacion invalido.");
            } else {
                ente = temp;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void guardarEnte() {
        if (ente.getEsPersona() && !ente.getExcepcionales()) {
            if (ente.getCiRuc() == null || ente.getCiRuc().length() == 0) {
                JsfUti.messageError(null, "Error!", "Debe ingresar el numero de Identificacion.");
                return;
            }
            if (ente.getNombres() == null || ente.getNombres().length() == 0) {
                JsfUti.messageError(null, "Error!", "Debe ingresar los Nombres.");
                return;
            }
            if (ente.getApellidos() == null || ente.getApellidos().length() == 0) {
                JsfUti.messageError(null, "Error!", "Debe ingresar los Apellidos.");
                return;
            }
        } else if (!ente.getEsPersona() && !ente.getExcepcionales()) {
            if (ente.getCiRuc() == null || ente.getCiRuc().length() == 0) {
                JsfUti.messageError(null, "Error!", "Debe ingresar el numero de Identificacion.");
                return;
            }
            if (ente.getRazonSocial() == null || ente.getRazonSocial().length() == 0) {
                JsfUti.messageError(null, "Error!", "Debe ingresar los Nombres.");
                return;
            }
        }
        if(!ente.getEsPersona()){
            
        }
        Long ide = serv.existeEnteByCiRuc(new String[]{"ciRuc"}, new Object[]{ente.getCiRuc()});
        if (ide != null) {
            if (ente.getId() != null) {
                if (ide.compareTo(ente.getId()) == 0) {
                } else {
                    JsfUti.messageError(null, "Advertencia", "Ya existe un cliente con el mismo número de documento.");
                }
            } else {
                JsfUti.messageError(null, "Advertencia", "Ya existe un cliente con el mismo número de documento.");
            }
        }
        ente = phServ.guardarOActualizarEnteCorreosTlfns(ente);
        RequestContext.getCurrentInstance().closeDialog(ente);
    }

    public void cerrar() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void agregarCorreo() {
        if (ente.getCorreo() == null) {
            JsfUti.messageInfo(null, "Debe Ingresar Correo.", "");
            return;
        }
        if (!Utils.validarEmailConExpresion(ente.getCorreo())) {
            JsfUti.messageInfo(null, "Correo Ingresado es invalido.", "");
            return;
        }
        try {
            EnteCorreo correo = new EnteCorreo();
            correo.setEnte(ente);
            ente.getEnteCorreoCollection().add(correo);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void agregarCorreoProvicional() {
        try {
            EnteCorreo correoProvicional = new EnteCorreo();
            correoProvicional.setEmail("no_ingreso_correo@hotmail.com");
            correoProvicional.setEnte(ente);
            ente.getEnteCorreoCollection().add(correoProvicional);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }

    }

    public void eliminarCorreo(EnteCorreo corr) {
        int index = 0;
        if (corr.getId() != null) {
            index = ente.getEnteCorreoCollection().indexOf(corr);
        } else {
            for (EnteCorreo ec : ente.getEnteCorreoCollection()) {
                if (ec.getEmail().equals(corr.getEmail())) {
                    break;
                }
                index++;
            }
        }
        ente.getEnteCorreoCollection().remove(index);
    }

    public void agregarTelefono() {
        if (ente.getTelefono() == null) {
            JsfUti.messageInfo(null, "Debe Ingresar Número de Telefonico.", "");
            return;
        }
        if (!Utils.validateNumberPattern(ente.getTelefono())) {
            JsfUti.messageInfo(null, "solo debe Ingresar Números", "");
            return;
        }

        if (!PhoneUtils.getValidNumber(ente.getTelefono(), "EC")) {
            JsfUti.messageInfo(null, "Número Telefonico invalido", "");
            return;
        }
        try {
            EnteTelefono telefono = new EnteTelefono();
            telefono.setEnte(ente);
            ente.getEnteTelefonoCollection().add(telefono);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        JsfUti.update("formEditEnt:dtTel");
    }

    public void eliminarTelefono(EnteTelefono tel) {
        int index = 0;
        if (tel.getId() != null) {
            index = ente.getEnteTelefonoCollection().indexOf(tel);
        } else {
            for (EnteTelefono ec : ente.getEnteTelefonoCollection()) {
                if (ec.getTelefono().equals(tel.getTelefono())) {
                    break;
                }
                index++;
            }
        }
        ente.getEnteTelefonoCollection().remove(index);
    }

    public void setPersona() {
        switch (tipoEnte) {
            case 3:
                ente.setExcepcionales(true);
                break;
            case 2:
                ente.setEsPersona(false);
                break;
            default:
                ente.setEsPersona(true);
                break;
        }
    }

    public String getTipoIdentificacionPersona() {
        switch (tipoEnte) {
            case 3:
                return "(*)Identificación:";
            case 2:
                return "(*)R.U.C.:";
            default:
                return "(*)Cédula:";
        }
    }

    public void buscarRes(Boolean tipoSelect) {
        this.tipoSelect = tipoSelect;
        entes = new CatEnteLazy(true);
        JsfUti.executeJS("PF('dlgEntes').show()");
    }

    public void seleccionarComprador(CatEnte ente) {
        if (tipoSelect) {
            ente.setConyuge(BigInteger.valueOf(ente.getId()));
            conyuge = ente;
        } else {
            ente.setRepresentanteLegal(BigInteger.valueOf(ente.getId()));
            representanteLegal = ente;
        }
    }

    public void onItemSelect(SelectEvent event) {
        ente = entidadTemp;
        RequestContext.getCurrentInstance().closeDialog(ente);
    }

    public List<CatEnte> completarEntidades(String query) {
        if (query != null) {
            ente.setRazonSocial(query);
            List<CatEnte> tempEntes = service.getEntesByRazonSocial(query);
            if (!tempEntes.isEmpty()) {
                return tempEntes;
            }
        }
        return null;
    }

    public List<CtlgItem> getListado(String catalogo) {
        return serv.getItemsByCatalogo(catalogo);
    }

    public Integer getTipoEnte() {
        return tipoEnte;
    }

    public void setTipoEnte(Integer tipoEnte) {
        this.tipoEnte = tipoEnte;
    }

    public Long getIdEnte() {
        return idEnte;
    }

    public void setIdEnte(Long idEnte) {
        this.idEnte = idEnte;
    }

    public Boolean getVer() {
        return ver;
    }

    public void setVer(Boolean ver) {
        this.ver = ver;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    public CatEnte getRepresentanteLegal() {
        return representanteLegal;
    }

    public void setRepresentanteLegal(CatEnte representanteLegal) {
        this.representanteLegal = representanteLegal;
    }

    public CatEnte getConyuge() {
        return conyuge;
    }

    public void setConyuge(CatEnte conyuge) {
        this.conyuge = conyuge;
    }

    public CatEnteLazy getEntes() {
        return entes;
    }

    public void setEntes(CatEnteLazy entes) {
        this.entes = entes;
    }

    public String getEsPersona() {
        return esPersona;
    }

    public void setEsPersona(String esPersona) {
        this.esPersona = esPersona;
    }

    public String getCiRuc() {
        return ciRuc;
    }

    public void setCiRuc(String ciRuc) {
        this.ciRuc = ciRuc;
    }

    public String getVerTemp() {
        return verTemp;
    }

    public void setVerTemp(String verTemp) {
        this.verTemp = verTemp;
    }

    public CatEnte getEntidadTemp() {
        return entidadTemp;
    }

    public void setEntidadTemp(CatEnte entidadTemp) {
        this.entidadTemp = entidadTemp;
    }

}
