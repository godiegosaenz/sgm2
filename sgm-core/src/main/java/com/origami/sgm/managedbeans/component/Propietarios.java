/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import com.origami.config.MainConfig;
import com.origami.config.SisVars;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.catastro.FichaPredial;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatNacionalidad;
import com.origami.sgm.entities.CatPais;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.GeDocumentos;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.CatPredioPropietarioLazy;
import com.origami.sgm.services.ejbs.censocat.OmegaUploader;
import com.origami.sgm.services.ejbs.censocat.UploadDocumento;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import util.EntityBeanCopy;
import util.Faces;
import util.JsfUti;
import util.Messages;
import util.PhoneUtils;
import util.Utils;

/**
 *
 * @author Angel Navarro, CarlosLoorVargas
 * @date 03/11/2016
 */
@Named
@ViewScoped
public class Propietarios implements Serializable {

    protected static final Logger LOG = Logger.getLogger(Propietarios.class.getName());

    protected String correo;
    protected String telefono;
    protected Boolean nuevo;
    protected Boolean esAnterior = Boolean.FALSE;
    protected Boolean edicion;
    protected CatPredioPropietario pro;
    protected List<EnteTelefono> eliminarTelefono;
    protected List<EnteCorreo> eliminarCorreo;
    // Parametros
    protected String idCatPredioPro;
    protected String idPredio;
    protected String esNuevo;
    protected String editar;
    protected String anterior;
    protected CatPredioPropietarioLazy propietarios;
    protected List<CatPredioPropietario> seleccionados;
    protected CatEnte representanteLegal;
    protected CatEnte conyuge;
    protected CatEnteLazy entes;
    protected Boolean tipoSelect;
    protected String mensajeDoc;

    @javax.inject.Inject
    protected CatastroServices ejb;
    @Inject
    protected UserSession us;
    @javax.inject.Inject
    protected Entitymanager manager;

    @Inject
    protected UploadDocumento documentoBean;
    @Inject
    protected OmegaUploader fserv;
    protected GeDocumentos saveDocumento;
    protected Boolean closeDialog = false;
    protected Boolean closeDialog1 = false;

    protected Integer tipo;

    protected MainConfig config;

//    @PostConstruct
    public void initView() {
        try {
            if (!JsfUti.isAjaxRequest()) {
                propietarios = new CatPredioPropietarioLazy(Boolean.TRUE);
                nuevo = Boolean.valueOf(esNuevo);
                edicion = Boolean.valueOf(editar);
                esAnterior = Boolean.valueOf(anterior);
                if (nuevo) {
                    if (idPredio == null) {
                        return;
                    }
                    pro = new CatPredioPropietario();
                    pro.setPredio(new CatPredio(Long.valueOf(idPredio)));
                    pro.setEnte(new CatEnte());
                    pro.setEsResidente(false);
                    pro.setUsuario(us.getName_user());
                    pro.setEstado("A");
                    conyuge = new CatEnte();
                    representanteLegal = new CatEnte();
                } else {
                    if (idCatPredioPro == null) {
                        return;
                    }
                    pro = ejb.getPredioPropietarioById(Long.valueOf(idCatPredioPro));
                    if (pro.getEnte().getConyuge() != null) {
                        conyuge = manager.find(CatEnte.class, pro.getEnte().getConyuge().longValue());
                    } else {
                        conyuge = new CatEnte();
                    }
                    if (pro.getEnte().getRepresentanteLegal() != null) {
                        representanteLegal = manager.find(CatEnte.class, pro.getEnte().getRepresentanteLegal().longValue());
                    } else {
                        representanteLegal = new CatEnte();
                    }
                }

                config = new MainConfig();
            }
        } catch (NumberFormatException ne) {
            LOG.log(Level.SEVERE, null, ne);
        }
    }

    public void buscarEnte() {
        if (pro.getEnte().getCiRuc() != null) {
            try {
                Map paramt = new HashMap<>();
                paramt.put("ciRuc", pro.getEnte().getCiRuc());
                paramt.put("esPersona", pro.getEnte().getEsPersona());
                CatEnte newEnt = ejb.propiedadHorizontal().getCatEnteByParemt(paramt);
                if (newEnt != null) {
                    if (ejb.existePropietarioPredio(pro.getPredio(), newEnt.getId())) {
                        JsfUti.messageInfo(null, "Cliente ya fue agregado al predio", "");
                        return;
                    }
                    pro.setEnte(newEnt);
                } else {
                    JsfUti.messageInfo(null, Messages.cedulaCIinvalida, "");
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, null, e);
            }
        }
    }

    public void agregarPropietario() {
        try {
            if (pro == null) {
                JsfUti.messageError(null, "Error", "No se encontro registro a guardar");
                return;
            }
            if (pro.getEnte().getCiRuc() == null) {
                JsfUti.messageError(null, "Error", "No se encontro registro a guardar");
                return;
            }
            //if (existeDocumento()) {
            pro.setFecha(new Date());
            pro = ejb.guardarPropietario(pro, us.getName_user());
            RequestContext.getCurrentInstance().closeDialog(pro);
//            } else {
//            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void agregarPropietarioUnSave() {
        try {
            if (pro == null) {
                JsfUti.messageError(null, "Error", "No se encontro registro a guardar");
                return;
            }
            if (pro.getEnte().getCiRuc() == null) {
                JsfUti.messageError(null, "Error", "No se encontro registro a guardar");
                return;
            }

            pro.setFecha(new Date());
            //pro = ejb.guardarPropietario(pro, us.getName_user());
            RequestContext.getCurrentInstance().closeDialog(pro);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    private Boolean existeDocumento() {
        if (tipo == null) {
            return true;
        }
        switch (tipo) {
            case 1:
                if (saveDocumento == null) {
                    JsfUti.executeJS("PF('dlgSubirDocumento').show()");
                    closeDialog1 = true;
                    return false;
                }
                break;
            case 2:
                if (!pro.getEnte().getDiscapacidad().getValor().equalsIgnoreCase("Ninguna")) {
                    if (saveDocumento == null) {
                        JsfUti.executeJS("PF('dlgSubirDocumento').show()");
                        closeDialog1 = true;
                        return false;
                    }
                }
                break;
            case 3:
                if (saveDocumento == null) {
                    JsfUti.executeJS("PF('dlgSubirDocumento').show()");
                    closeDialog1 = true;
                    return false;
                }
                break;
        }
        return true;
    }

    public void cerrar() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void modificarPropietario() {
        try {
            if (pro == null) {
                JsfUti.messageError(null, "Error", "No se encontro registro a actualizar");
                return;
            }
            if (pro.getEnte().getEsTerceraEdad()) {
                if (saveDocumento == null) {
                    JsfUti.executeJS("PF('dlgSubirDocumento').show()");
                    closeDialog = true;
                    return;
                }
            }
            if (pro.getEnte().getDiscapacidad() != null) {
                if (!pro.getEnte().getDiscapacidad().getValor().equalsIgnoreCase("Ninguna")) {
                    if (saveDocumento == null) {
                        JsfUti.executeJS("PF('dlgSubirDocumento').show()");
                        closeDialog = true;
                        return;
                    }
                }
            }
            pro.setModificado(us.getName_user());
            pro = ejb.guardarPropietario(pro, us.getName_user());
            RequestContext.getCurrentInstance().closeDialog(pro);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void modificarPropietarioUnsave() {
        try {
            if (pro == null) {
                JsfUti.messageError(null, "Error", "No se encontro registro a actualizar");
                return;
            }
            if (pro.getEnte().getEsTerceraEdad()) {
                if (saveDocumento == null) {
                    JsfUti.executeJS("PF('dlgSubirDocumento').show()");
                    closeDialog = true;
                    return;
                }
            }
            if (pro.getEnte().getDiscapacidad() != null) {
                if (!pro.getEnte().getDiscapacidad().getValor().equalsIgnoreCase("Ninguna")) {
                    if (saveDocumento == null) {
                        JsfUti.executeJS("PF('dlgSubirDocumento').show()");
                        closeDialog = true;
                        return;
                    }
                }
            }
            pro.setModificado(us.getName_user());
            pro = ejb.guardarPropietario(pro, us.getName_user());
            RequestContext.getCurrentInstance().closeDialog(pro);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public Boolean verificarPorcentajeParticipacion() {
        BigDecimal totalPorcentaje = BigDecimal.ZERO;
        if (pro.getPorcentajePosecion().doubleValue() == 0) {
            JsfUti.messageInfo(null, "Nota!", "El porcentaje de posesión no puede ser igual a Cero");
            return false;
        }
        if (pro.getPredio().getId() != null) {
            List<CatPredioPropietario> props = (List<CatPredioPropietario>) EntityBeanCopy.clone(ejb.propiedadHorizontal().getFichaServices().getPropietariosByPredio(pro.getPredio().getId()));
            for (CatPredioPropietario prop : props) {
                if (prop.getCopropietario() != null && prop.getCopropietario()) {
                    if (prop.getPorcentajePosecion() == null) {
                        prop.setPorcentajePosecion(BigDecimal.ZERO);
                    }
                    totalPorcentaje = totalPorcentaje.add(prop.getPorcentajePosecion());
                }
            }
            if (pro.getPorcentajePosecion() == null) {
                pro.setPorcentajePosecion(BigDecimal.ZERO);
            }
            totalPorcentaje = totalPorcentaje.add(pro.getPorcentajePosecion());
            if (totalPorcentaje == null) {
                totalPorcentaje = (BigDecimal.ZERO);
            }
            if (totalPorcentaje.doubleValue() > 100) {
                JsfUti.messageInfo(null, "Nota!", "La suma del porcentaje de parcipacion de los coopropietarios no debe exceder el 100%, la suma es: " + totalPorcentaje);
                pro.setPorcentajePosecion(BigDecimal.ZERO);
                return false;
            }
        }
        return true;
    }

    public void selecPropietarios() {
        if (seleccionados != null) {
            RequestContext.getCurrentInstance().closeDialog(seleccionados);
        }
    }

    public void agregarTelefono() {
        try {
            if (telefono == null) {
                JsfUti.messageInfo(null, "Debe Ingresar Número de Telefonico.", "");
                return;
            }
            if (!Utils.validateNumberPattern(telefono)) {
                JsfUti.messageInfo(null, "solo debe Ingresar Números", "");
                return;
            }

            if (!PhoneUtils.getValidNumber(telefono, "EC")) {
                JsfUti.messageInfo(null, "Número Telefonico invalido", "");
                return;
            }
            if (pro.getEnte().getEnteTelefonoCollection() == null) {
                pro.getEnte().setEnteTelefonoCollection(new ArrayList<EnteTelefono>());
            }
            EnteTelefono c = new EnteTelefono();
            c.setTelefono(telefono);
            c.setEnte(pro.getEnte());
            pro.getEnte().getEnteTelefonoCollection().add(c);
            telefono = null;

        } catch (Exception e) {
            Logger.getLogger(Propietarios.class
                    .getName()).log(Level.SEVERE, null, e);
        }
    }

    public void agregarCorreo() {
        try {
            if (correo == null) {
                JsfUti.messageInfo(null, "Debe Ingresar Correo.", "");
                return;
            }
            if (!Utils.validarEmailConExpresion(correo)) {
                JsfUti.messageInfo(null, "Correo Ingresado es invalido.", "");
                return;
            }
            if (pro.getEnte().getEnteCorreoCollection() == null) {
                pro.getEnte().setEnteCorreoCollection(new ArrayList<EnteCorreo>());
            }
            EnteCorreo c = new EnteCorreo();
            c.setEmail(correo);
            c.setEnte(pro.getEnte());
            pro.getEnte().getEnteCorreoCollection().add(c);
            correo = null;

        } catch (Exception e) {
            Logger.getLogger(Propietarios.class
                    .getName()).log(Level.SEVERE, null, e);
        }
    }

    public void eliminarTelefono(EnteTelefono tel) {
        try {
            if (eliminarTelefono == null) {
                eliminarTelefono = new ArrayList<>();
            }
            if (tel.getId() != null) {
                eliminarTelefono.add(tel);
            }
            pro.getEnte().getEnteTelefonoCollection().remove(tel);

        } catch (Exception e) {
            Logger.getLogger(Propietarios.class
                    .getName()).log(Level.SEVERE, null, e);
        }
    }

    public void eliminarCorreo(EnteCorreo corr) {
        try {
            if (eliminarCorreo == null) {
                eliminarCorreo = new ArrayList<>();
            }
            if (corr.getId() != null) {
                eliminarCorreo.add(corr);
            }
            pro.getEnte().getEnteCorreoCollection().remove(corr);

        } catch (Exception e) {
            Logger.getLogger(Propietarios.class
                    .getName()).log(Level.SEVERE, null, e);
        }
    }

    public List<CtlgItem> getTipoPro() {
        return ejb.propiedadHorizontal().getCtlgItem("predio.propietario.tipo");
    }

    /**
     * Obtiene la lista del catalogo ctlgItem
     *
     * @param argumento
     * @return
     */
    public List<CtlgItem> getListado(String argumento) {
        return manager.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{argumento});
    }

    public void buscarRes(Boolean tipoSelect) {
        this.tipoSelect = tipoSelect;
        entes = new CatEnteLazy(true);
        JsfUti.executeJS("PF('dlgEntes').show()");
    }

    public void seleccionarComprador(CatEnte ente) {
        if (tipoSelect) {
            pro.getEnte().setConyuge(BigInteger.valueOf(ente.getId()));
            conyuge = ente;
        } else {
            pro.getEnte().setRepresentanteLegal(BigInteger.valueOf(ente.getId()));
            representanteLegal = ente;
        }
    }

    public List<CatNacionalidad> getNacionalidades() {
        return ejb.getNacionalidades();
    }

    public List<CatPais> getPaises() {
        return ejb.getPaises();
    }

    public List<CtlgItem> getDiscapacidades() {
        return ejb.getItemsByCatalogo("ente.discapacidad");
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public CatPredioPropietario getPro() {
        return pro;
    }

    public void setPro(CatPredioPropietario pro) {
        this.pro = pro;
    }

    public String getIdCatPredioPro() {
        return idCatPredioPro;
    }

    public void setIdCatPredioPro(String idCatPredioPro) {
        this.idCatPredioPro = idCatPredioPro;
    }

    public String getIdPredio() {
        return idPredio;
    }

    public void setIdPredio(String idPredio) {
        this.idPredio = idPredio;
    }

    public Boolean getNuevo() {
        return nuevo;
    }

    public void setNuevo(Boolean nuevo) {
        this.nuevo = nuevo;
    }

    public String getEsNuevo() {
        return esNuevo;
    }

    public void setEsNuevo(String esNuevo) {
        this.esNuevo = esNuevo;
    }

    public CatPredioPropietarioLazy getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(CatPredioPropietarioLazy propietarios) {
        this.propietarios = propietarios;
    }

    public List<CatPredioPropietario> getSeleccionados() {
        return seleccionados;
    }

    public void setSeleccionados(List<CatPredioPropietario> seleccionados) {
        this.seleccionados = seleccionados;
    }

    public Boolean getEdicion() {
        return edicion;
    }

    public void setEdicion(Boolean edicion) {
        this.edicion = edicion;
    }

    public String getEditar() {
        return editar;
    }

    public void setEditar(String editar) {
        this.editar = editar;
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

    public void handleFileDocumentBySave(FileUploadEvent event) {
        try {
            Date d = new Date();
            File file = new File(SisVars.rutaRepotiorioArchivo + d.getTime() + event.getFile().getFileName());

            InputStream is;
            is = event.getFile().getInputstream();
            OutputStream out = new FileOutputStream(file);
            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            Long documentoId = fserv.uploadFile(event.getFile().getInputstream(), event.getFile().getFileName(), event.getFile().getContentType());
            documentoBean.setFechaCreacion(new Date());
            documentoBean.setNombre(event.getFile().getFileName());
            try {
                if (this.pro.getEnte().getId() == null) {
                    documentoBean.setRaiz(this.pro.getEnte().getId());
                } else {
                    documentoBean.setRaiz(0L);
                }
            } catch (Exception e) {
            }
            documentoBean.setContentType(event.getFile().getContentType());
            documentoBean.setDocumentoId(documentoId);
            switch (tipo) {
                case 1:
                    documentoBean.setIdentificacion("Tercera Edad");
                    break;
                case 2:
                    documentoBean.setIdentificacion("Discapacidad");
                    break;
                case 3:
                    documentoBean.setIdentificacion("Coopropietarios");
                    break;
            }
            saveDocumento = documentoBean.saveDocumento();
            if (closeDialog1) {
                agregarPropietario();
            }
            if (closeDialog) {
                modificarPropietario();
            }
            is.close();
            out.close();
            Faces.messageInfo(null, "Nota1", "Archivo cargado Satisfactoriamente");
        } catch (IOException e) {
            Logger.getLogger(FichaPredial.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
        switch (tipo) {
            case 1:
                mensajeDoc = "Ingrese pdf del 'DOCUMENTO DE IDENTIDAD'";
                break;
            case 2:
                mensajeDoc = "Ingrese pdf del 'CARNET DE DISCAPACIDAD'";
                break;
            case 3:
                mensajeDoc = "Ingrese pdf de la 'DECLARATORIA DE PROPIEDAD HORINZONTAL'";
                break;
        }
    }

    public String getMensajeDoc() {
        return mensajeDoc;
    }

    public void setMensajeDoc(String mensajeDoc) {
        this.mensajeDoc = mensajeDoc;
    }

    public MainConfig getConfig() {
        return config;
    }

    public void setConfig(MainConfig config) {
        this.config = config;
    }

    public Boolean getEsAnterior() {
        return esAnterior;
    }

    public void setEsAnterior(Boolean esAnterior) {
        this.esAnterior = esAnterior;
    }

    public String getAnterior() {
        return anterior;
    }

    public void setAnterior(String anterior) {
        this.anterior = anterior;
    }
    
    
    
    

}
