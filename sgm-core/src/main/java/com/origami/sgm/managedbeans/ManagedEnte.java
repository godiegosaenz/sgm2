/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.app.wscliente.Cedulas;
import com.origami.app.wscliente.CedulaJama;
import com.origami.app.wscliente.PersonaModel;
import com.origami.app.wscliente.Service1;
import com.origami.config.SisVars;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.DatoSeguro;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.lazymodels.CatEnteLazy;

import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.sgm.services.interfaces.datoSeguro.DatoSeguroServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import util.EntityBeanCopy;
import util.JsfUti;
import util.Messages;
import util.PhoneUtils;
import util.Utils;
import util.VerCedulaUtils;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author
 * Administrador
 */
@Named
@ViewScoped
public class ManagedEnte implements Serializable {

    public static final Long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(ManagedEnte.class.getName());

    @javax.inject.Inject
    private Entitymanager acl;

    @javax.inject.Inject
    private SeqGenMan seqServices;

    @javax.inject.Inject
    private CatastroServices services;

    @javax.inject.Inject
    private DatoSeguroServices datoSeguroSeguro;

    @Inject
    private UserSession session;

    
    private String emailNew;
    private String tlfnNew;
    private VerCedulaUtils vcu;
    private CatEnte enteNew;
    private CatEnte enteEdit;
    private List<EnteCorreo> listCorreos;
    private List<EnteTelefono> listTlfs;
    private Boolean esPersona = true;
    private Boolean esNatural = true;
    private Boolean autorizado = false;
    private Boolean tieneDiscapacidad = false;
    private EnteCorreo email;
    private EnteTelefono telefono;
    private Long id;

    private Integer tipoEnte;
    private CatEnteLazy entesLazy;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        if (request.getParameter("param") != null) {
            id = Long.valueOf(request.getParameter("param"));
        } else {
            this.inicializarVariables();
        }
        if (id != null) {
            enteEdit = (CatEnte) acl.find(CatEnte.class, id);
            listCorreos = enteEdit.getEnteCorreoCollection();
            listTlfs = enteEdit.getEnteTelefonoCollection();
        } else {
            enteEdit = new CatEnte();
            this.inicializarVariables();
        }
        entesLazy = new CatEnteLazy(esPersona);
        if (session.esLogueado()) {
            autorizado = session.getName_user().equals("admin");
        }
        tipoEnte = 1;
        enteNew = new CatEnte();
    }

    /**
     * Verifica si
     * existe el
     * número de
     * documento de
     * que se ha
     * ingresado.
     */
    public void existeCedula() {
        BigInteger RepLegal = null;
        Boolean esPer = enteNew.getEsPersona();
        switch (tipoEnte) {
            case 1:
                esPer = true;
                break;
            case 2:
                esPer = false;
                break;
            default:
                esPer = false;
                break;
        }
        final String identificacion = enteNew.getCiRuc();
        CatEnte enteTemp;
        try {
            DatoSeguro datSeg = null;
            if (enteNew.getCiRuc() == null || identificacion.trim().length() == 0) {
                return;
            }
            if (esPer) {
                //saveByDatoSeguro();
                datSeg = verificarEnte(enteNew.getCiRuc(), false);
                //enteNew = datoSeguroSeguro.llenarEnte(datSeg, enteNew, false);
                //getInformationEnte();
            }

            if (enteNew.getRepresentanteLegal() != null && enteNew.getRepresentanteLegal().compareTo(BigInteger.ZERO) > 0) {
                RepLegal = enteNew.getRepresentanteLegal();
            }

            enteTemp = (CatEnte) acl.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{identificacion});
            //Long temp = services.existeEnteByCiRuc(new String[]{"ciRuc"}, new Object[]{enteNew.getCiRuc()});
            if (enteTemp == null && datSeg != null) {
                enteNew = datoSeguroSeguro.llenarEnte(datSeg, enteNew, false);
                enteNew.setCiRuc(identificacion);
            } else {
                if (enteTemp != null) {
                    enteNew = enteTemp;
                    listCorreos = enteTemp.getEnteCorreoCollection();
                    listTlfs = enteTemp.getEnteTelefonoCollection();
                } else {
                    enteNew = new CatEnte();
                    enteNew.setCiRuc(identificacion);
                    enteNew.setEsPersona(esPer);
                }
            }
            if (RepLegal != null) {
                enteNew.setRepresentanteLegal(RepLegal);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        JsfUti.update("formNewClient:tabNewEnte");
    }
    
    

    public void saveByDatoSeguro() {
        DatoSeguro datSeg = null;

        List<CedulaJama> enteList =  acl.findAll(Querys.getDataEntes);
        PersonaModel personaModel;
        
        System.out.println("enteList" + enteList.size());
        Integer count = 0;
        for (CedulaJama e : enteList) {
            datSeg = null;

            System.out.println("count" + count);
            if (e != null) {
                if (e.getCedula() != null) {
                    System.out.println("Ente" + e.getCedula());
                    datSeg = verificarEnte(e.getCedula(), false);
                    if (datSeg != null) {
                        enteNew = datoSeguroSeguro.llenarEnte(datSeg, enteNew, false);
                        e.setApellidoDatSeg(enteNew.getApellidos());
                        e.setNombreDatSeg(enteNew.getNombres());
                        e.setCedula(e.getCedula());
                        e.setDireccionDatSeg(enteNew.getDireccion());
                        e.setFechanacDatSeg(enteNew.getFechaNacimiento());
                        e.setDiscapacidadDatSeg(datSeg.getCondicion());
                        e.setEstadoCivilDatSeg(datSeg.getEstadoCivil());
                        e.setEstado("DATO SEGURO");
                        acl.persist(e);
                    }

                }
                count = count + 1;
            }
        }
    }

    public List<CtlgItem> getDiscapacidades() {
        return services.getItemsByCatalogo("ente.discapacidad");
    }

    private DatoSeguro verificarEnte(String CiRuc, Boolean empresa) {
        DatoSeguro data;
        try {
            data = datoSeguroSeguro.getDatos(CiRuc, empresa, 0);
            if (data == null) {
                return null;
            }
            return data;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al validar", e);
        }
        return null;
    }

    public void cambioTipoPersona() {
        if (tipoEnte == 1) {
            esPersona = true;
        }
        if (tipoEnte == 2) {
            esPersona = false;
        }
        entesLazy = new CatEnteLazy(esPersona);
        JsfUti.update("formEnte:dtuser");
    }

    public void inicializarVariables() {
        emailNew = "";
        tlfnNew = "";
        listCorreos = new ArrayList<>();
        listTlfs = new ArrayList<>();
    }

    public void cargarListas(CatEnte e) {
        if (e.getEnteCorreoCollection() != null) {
            for (EnteCorreo c : e.getEnteCorreoCollection()) {
                listCorreos.add(c);
            }
        }
        if (e.getEnteTelefonoCollection() != null) {
            for (EnteTelefono t : e.getEnteTelefonoCollection()) {
                listTlfs.add(t);
            }
        }
    }

    public void openDlgNew() {
        enteNew = new CatEnte();
        esNatural = true;
        this.inicializarVariables();
        JsfUti.update("formNewClient");
        JsfUti.executeJS("PF('dlgNewClient').show();");
    }

    public void openDlgEdit(CatEnte e) {
        enteEdit = e;
        if (e.getEsPersona()) {
            tipoEnte = 1;
            enteEdit.setExcepcionales(false);
        }
        if (!e.getEsPersona()) {
            tipoEnte = 2;
            enteEdit.setExcepcionales(false);
        }
        if (e.getCiRuc().length() > 14) {
            tipoEnte = 3;
            enteEdit.setExcepcionales(true);
        }
        this.inicializarVariables();
        this.cargarListas(e);
        JsfUti.update("formEditUser");
        JsfUti.executeJS("PF('dlgEditClient').show();");
    }

    public void openDlgDetalle(CatEnte e) {
        listCorreos = new ArrayList<>();
        listTlfs = new ArrayList<>();
        enteEdit = e;
        this.cargarListas(e);
        JsfUti.update("formDetalleCliente");
        JsfUti.executeJS("PF('dlgDetalleClient').show();");
    }

    public void agregarEmail() {
        if (emailNew != null) {
            emailNew = emailNew.trim();
            Boolean flag = true;
            for (EnteCorreo c : listCorreos) {
                if (c.getEmail().equals(emailNew)) {
                    flag = false;
                }
            }
            if (flag) {
                if (Utils.validarEmailConExpresion(emailNew)) {
                    email = new EnteCorreo();
                    email.setEmail(emailNew);
                    listCorreos.add(email);
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

    public void getInformationEnte() {
        List<Cedulas> enteList = acl.findAll(Querys.getDataEntes);
        PersonaModel personaModel;
        Integer count = 0;
        for (Cedulas e : enteList) {
            count = count + 1;
            System.out.println("count" + count);
            if (e != null) {
                if (e.getCedula() != null) {
                    System.out.println("Ente" + e.getCedula());
                    personaModel = new PersonaModel();
                    personaModel = getPersonaByCiRuc(e.getCedula());
                    e.setApellidosWs(personaModel.getApellidos());
                    e.setNombresWs(personaModel.getNombres());
                    e.setCedulaWS(personaModel.getCedularuc());
                    e.setDireccion(personaModel.getDireccion());
                    e.setFechanac(personaModel.getFechanac());
                    e.setObligadocontabilidad(personaModel.getObligadocontabilidad());

                    System.out.println("personaModel" + personaModel.toString());

                    acl.persist(e);
                }
            }

        }
    }

    public static PersonaModel getPersonaByCiRuc(String documento) {
        try {

            Service1 serv = new Service1();
            String xml = serv.getService1Soap().persona(documento);
            System.out.println(xml);
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));
            System.out.println("docElement -" + doc.getDocumentElement().getElementsByTagName("DocumentElement").toString());
            NodeList nl = doc.getDocumentElement().getElementsByTagName("DocumentElement").item(0).getChildNodes();
            JAXBContext jaxbContext = JAXBContext.newInstance(PersonaModel.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            return (PersonaModel) jaxbUnmarshaller.unmarshal(doc.getDocumentElement().getElementsByTagName("MyTable").item(0));
        } catch (Exception e) {
            PersonaModel model = new PersonaModel();
            model.setNombres("EN BLANCO");
            return model;
        }
    }

    public void eliminarEmail(EnteCorreo email) {
        if (email.getId() != null) {
            listCorreos.remove(email);
            acl.delete(email);
        } else {
            int ind = -1;
            int cont = 0;
            for (EnteCorreo co : listCorreos) {
                if (co.getEmail().equals(email.getEmail())) {
                    ind = cont;
                }
                cont++;
            }
            if (ind >= 0) {
                listCorreos.remove(ind);
            }
        }
    }

    public void agregarTlfn() {
        if (tlfnNew != null) {
            tlfnNew = tlfnNew.trim();
            Boolean flag = true;
            for (EnteTelefono t : listTlfs) {
                if (t.getTelefono().equals(tlfnNew)) {
                    flag = false;
                }
            }
            if (flag) {
                if (Utils.validateNumberPattern(tlfnNew)) {
                    if (PhoneUtils.getValidNumber(tlfnNew, SisVars.region)) {
                        telefono = new EnteTelefono();
                        telefono.setTelefono(tlfnNew);
                        listTlfs.add(telefono);
                        tlfnNew = "";
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

    public void eliminarTlfn(EnteTelefono tlfn) {
        if (tlfn.getId() != null) {
            listTlfs.remove(tlfn);
            acl.delete(tlfn);
        } else {
            int ind = -1;
            int cont = 0;
            for (EnteTelefono te : listTlfs) {
                if (te.getTelefono().equals(tlfn.getTelefono())) {
                    ind = cont;
                }
                cont++;
            }
            if (ind >= 0) {
                listTlfs.remove(ind);
            }
        }
    }

    public Boolean validarEnte(CatEnte e) {
        if (e.getCiRuc() != null) {
            Long ide = services.existeEnteByCiRuc(new String[]{"ciRuc"}, new Object[]{e.getCiRuc()});
            if (ide != null) {
                if (e.getId() != null) {
                    if (ide.compareTo(e.getId()) == 0) {

                    } else {
                        JsfUti.messageError(null, "Advertencia", "Ya existe un cliente con el mismo número de documento.");
                        return false;
                    }
                } else {
                    JsfUti.messageError(null, "Advertencia", "Ya existe un cliente con el mismo número de documento.");
                    return false;
                }
            }
            if (e.getEsPersona()) {
                return ((e.getApellidos() != null) && (e.getNombres() != null));
            } else {
                return e.getRazonSocial() != null;
            }

        } else {
            return false;
        }
    }

    public void editarEnte() {
        CatEnte temp;

        if (tipoEnte == 1) {
            esNatural = true;
            enteEdit.setExcepcionales(false);
        }
        if (tipoEnte == 2) {
            esNatural = false;
            enteEdit.setExcepcionales(false);
        }
        if (tipoEnte == 3) {
//            enteEdit.setEsPersona(true);
            enteEdit.setExcepcionales(true);
            if ((temp = seqServices.guardarOActualizarEnte(enteEdit)) != null) {
                temp.setEnteCorreoCollection(listCorreos);
                temp.setEnteTelefonoCollection(listTlfs);
                acl.editarEnteCorreosTlfns(temp);
                JsfUti.redirectFaces("/generic/entefaces.xhtml");
            } else {
                JsfUti.messageError(null, "Error", "No se pudo guardar el ente");
            }
            return;
        }

        enteEdit.setEsPersona(esNatural);

        if (this.validarEnte(enteEdit)) {
            try {
                enteEdit.setUserMod(session.getName_user());
                enteEdit.setFechaMod(new Date());
                enteEdit.setEnteCorreoCollection(listCorreos);
                enteEdit.setEnteTelefonoCollection(listTlfs);
                Boolean b = acl.editarEnteCorreosTlfns(enteEdit);
                if (b) {
                    JsfUti.redirectFaces("/generic/entefaces.xhtml");
                } else {
                    JsfUti.messageInfo(null, Messages.problemaConexion, "");
                }
            } catch (Exception e) {
                Logger.getLogger(ManagedEnte.class.getName()).log(Level.SEVERE, null, e);
            }
        } else {
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        }
    }

    public void guardarEnte() {
        CatEnte temp;
        if (tipoEnte == 1) {
            enteNew.setEsPersona(true);
        } else if (tipoEnte == 2) {
            enteNew.setEsPersona(false);
        }
        if (tipoEnte == 3) {
            enteNew.setExcepcionales(true);
            setearValoresEnteNew();
            if ((temp = seqServices.guardarOActualizarEnte(enteNew)) != null) {
//                temp.setEnteCorreoCollection(listCorreos);
//                temp.setEnteTelefonoCollection(listTlfs);
//                acl.editarEnteCorreosTlfns(temp);
                JsfUti.redirectFaces("/generic/entefaces.xhtml");
            } else {
                JsfUti.messageError(null, "Error", "No se pudo guardar el ente");
            }
            return;
        }

        if (this.validarEnte(enteNew)) {
            try {
                if (this.existeEnte(enteNew.getCiRuc())) {
                    JsfUti.messageInfo(null, Messages.ciRucExiste, "");
                    enteEdit = enteNew;
                    editarEnte();
                } else {
                    vcu = new VerCedulaUtils();
                    if (vcu.comprobarDocumento(enteNew.getCiRuc())) { // verificar 
                        this.setearValoresEnteNew();
                    } else {
                        JsfUti.messageInfo(null, Messages.cedulaCIinvalida, "");
                    }
                }
            } catch (Exception e) {
                Logger.getLogger(ManagedEnte.class.getName()).log(Level.SEVERE, null, e);
            }
        } else {
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        }
    }

    public void setearValoresEnteNew() {
        enteNew.setUserCre(session.getName_user());
        enteNew.setFechaCre(new Date());
        if (!listCorreos.isEmpty()) {
            enteNew.setEnteCorreoCollection(listCorreos);
        }
        if (!listTlfs.isEmpty()) {
            enteNew.setEnteTelefonoCollection(listTlfs);
        }
        enteNew = acl.guardarEnteCorreosTlfns(enteNew);
        if (enteNew.getId() != null) {
            JsfUti.redirectFaces("/generic/entefaces.xhtml");
        } else {
            JsfUti.messageInfo(null, Messages.problemaConexion, "");
        }
    }

    public Boolean getEsPeronalNat() {
        switch (tipoEnte) {
            case 1:
                return true;
            case 2:
                return false;
            default:
                if (enteEdit != null && enteEdit.getId() != null) {
                    return enteEdit.getEsPersona();
                } else {
                    return enteNew.getEsPersona();
                }
        }
    }

    public Boolean existeEnte(String ciruc) {
        CatEnte temp = (CatEnte) acl.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{ciruc});
        return temp != null;
    }

    public void buscarRes() {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("modal", true);
        options.put("width", "75%");
        options.put("contentWidth", "100%");
        RequestContext.getCurrentInstance().openDialog("/resources/dialog/dialogEnte", options, null);
    }

    public void representanteLegal(SelectEvent event) {
        enteNew.setRepresentanteLegal(BigInteger.valueOf(((CatEnte) EntityBeanCopy.clone(event.getObject())).getId()));
    }

    public void representanteLegalEdit(SelectEvent event) {
        enteEdit.setRepresentanteLegal(BigInteger.valueOf(((CatEnte) EntityBeanCopy.clone(event.getObject())).getId()));
    }

    public String nombreRepresentante(BigInteger idRepLagal) {
        try {
            if (idRepLagal != null) {
                CatEnte temp = (CatEnte) EntityBeanCopy.clone(acl.find(CatEnte.class, idRepLagal.longValue()));
                if (temp != null) {
                    return temp.getNombreCompleto();
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Nombre Representante", e);
        }
        return null;

    }

    public CatEnteLazy getEntesLazy() {
        return entesLazy;
    }

    public void setEntesLazy(CatEnteLazy entesLazy) {
        this.entesLazy = entesLazy;
    }

    public Boolean getEsPersona() {
        return esPersona;
    }

    public void setEsPersona(Boolean esPersona) {
        this.esPersona = esPersona;
    }

    public CatEnte getEnteEdit() {
        return enteEdit;
    }

    public void setEnteEdit(CatEnte enteEdit) {
        this.enteEdit = enteEdit;
    }

    public String getEmailNew() {
        return emailNew;
    }

    public void setEmailNew(String emailNew) {
        this.emailNew = emailNew;
    }

    public String getTlfnNew() {
        return tlfnNew;
    }

    public void setTlfnNew(String tlfnNew) {
        this.tlfnNew = tlfnNew;
    }

    public Boolean getEsNatural() {
        return esNatural;
    }

    public void setEsNatural(Boolean esNatural) {
        this.esNatural = esNatural;
    }

    public CatEnte getEnteNew() {
        return enteNew;
    }

    public void setEnteNew(CatEnte enteNew) {
        this.enteNew = enteNew;
    }

    public Boolean getAutorizado() {
        return autorizado;
    }

    public void setAutorizado(Boolean autorizado) {
        this.autorizado = autorizado;
    }

    public List<EnteCorreo> getListCorreos() {
        return listCorreos;
    }

    public void setListCorreos(List<EnteCorreo> listCorreos) {
        this.listCorreos = listCorreos;
    }

    public List<EnteTelefono> getListTlfs() {
        return listTlfs;
    }

    public void setListTlfs(List<EnteTelefono> listTlfs) {
        this.listTlfs = listTlfs;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public Integer getTipoEnte() {
        return tipoEnte;
    }

    public void setTipoEnte(Integer tipoEnte) {
        this.tipoEnte = tipoEnte;
    }

    public Boolean getTieneDiscapacidad() {
        return tieneDiscapacidad;
    }

    public void setTieneDiscapacidad(Boolean tieneDiscapacidad) {
        this.tieneDiscapacidad = tieneDiscapacidad;
    }

}
