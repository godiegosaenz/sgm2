/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioS4;
import com.origami.sgm.entities.CatPredioS6;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.GeDocumentos;
import com.origami.sgm.entities.avaluos.SectorValorizacion;
import com.origami.sgm.events.CreacionPredioPost;
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
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import util.EntityBeanCopy;
import util.Faces;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author CarlosLoorVargas
 */
@Named(value = "gestionFicha")
@ViewScoped
public class GestionFicha extends PredioUtil implements Serializable {

    private static final Long serialVersionUID = 1L;
    private static final Logger logx = Logger.getLogger(GestionFicha.class.getName());
    @Inject
    private UserSession sess;
    @javax.inject.Inject
    private Entitymanager manager;
    @Inject
    private ServletSession ss;
    private Boolean ph = false, busqueda = false;
    private List<CatPredio> horizontales, verticales, resultado;
    private List<SectorValorizacion> subsectores;
    private Integer maxh = 0, maxv = 0, bod = 0, pq = 0, otros = 0, locComer = 0;
    protected AclUser usr;

    @javax.inject.Inject
    protected CatastroServices catast;
    
    @Inject
    private UploadDocumento documentoBean;
    @Inject
    private OmegaUploader fserv;
    private Long idDoc = null;

    private Boolean isNuevaManzana = Boolean.FALSE;
    
    @Inject
    protected Event<CreacionPredioPost> eventCrearPredio;

    @PostConstruct
    protected void load() {
        this.init();
        predio = new CatPredio();
        subsectores = manager.findAllEntCopy(SectorValorizacion.class);
        usr = (AclUser) manager.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{sess.getName_user()});
    }

    public void habPh() {
        if (ph) {
            verticales = new ArrayList<>();
            horizontales = new ArrayList<>();
            busqueda = true;
        } else {
            busqueda = false;
        }
    }

    public void registrar() {
        if (usr != null) {
            if (estaDibujado()) {

                
                if (predio.getTipoConjunto() == null || predio.getCiudadela() == null) {
                    Faces.messageInfo(null, "Datos Invalidos", "Los datos marcados como (*) son Obligatorios");
                    JsfUti.update("frmMain");
                    return;
                } else {
                    if (predio.getProvincia() > 0 && predio.getCanton() > 0 && predio.getParroquia() > 0
                            && predio.getZona() > 0 && predio.getSector() > 0 && predio.getMz() > 0
                            && predio.getSolar() > 0) {
                        predio.setUsuarioCreador(usr);
                        predio.setInstCreacion(new Date());
                        predio.setEstado("A");
                        predio.setEsAvaluoVerificado(Boolean.FALSE);
                        predio.setTipoPredio("U");
                        predio.setLote(predio.getSolar());
                        predio.setCdla(new Short("0"));
                        predio.setMzdiv(new Short("0"));
                        predio.setDiv1(new Short("0"));
                        predio.setDiv2(new Short("0"));
                        predio.setDiv3(new Short("0"));
                        predio.setDiv4(new Short("0"));
                        predio.setDiv5(new Short("0"));
                        predio.setDiv6(new Short("0"));
                        predio.setDiv7(new Short("0"));
                        predio.setDiv8(new Short("0"));
                        predio.setDiv9(new Short("0"));
                        predio.setPhv(new Short("0"));
                        predio.setPhh(new Short("0"));
                        predio.setPropiedadHorizontal(false);
                        this.setNamePredioByCiudadela();    
                        if (getCatas().existePredio(predio) == true) {
                            predio = new CatPredio();
                            Faces.messageInfo(null, "Predio ya ha sido anteriormente registrado", "");
                            JsfUti.update("frmMain");
                            return;
                        }
                        CatPredio p = new CatPredio();
                        p = predio;
                        predio = this.registrarPredio();
                        if (predio != null && predio.getId() != null) {
                            if (predio.getNumPredio() != null || predio.getNumPredio().compareTo(BigInteger.ZERO) <= 0) {
                                this.predio = getCatas().generarNumPredio(predio);
                            }
                            Faces.messageInfo(null, "Nota!", "Matricula Inmobiliaria registrada satisfactoriamente ");
                            JsfUti.executeJS("PF('dlgMatricula').show()");
                        } else {
                            predio = p;
                            JsfUti.update("frmMain");
                        }
                        CreacionPredioPost ev = new CreacionPredioPost();
                        ev.setCodPredio(predio.getClaveCat());
                        eventCrearPredio.fire(ev);
                    } else {
                        Faces.messageInfo(null, "Datos Invalidos", "Los datos marcados como (*) son Obligatorios");
                    }
                }
            } else {
                Faces.messageInfo(null, "No dibujado !", " predio no se ecuentra dibujado.");
            }
        } else {
            Faces.messageInfo(null, "Datos Invalidos", "Pacman :");
        }
    }

    public boolean estaDibujado() {
        return true;
    }

    public void continuar() {
        if (predio.getId() != null) {
            ss.instanciarParametros();
            ss.agregarParametro("idPredio", predio.getId());
            ss.agregarParametro("edit", true);
            ss.agregarParametro("numPredio", predio.getNumPredio());
            Faces.redirectFacesNewTab("/faces/vistaprocesos/catastro/fichaPredial/fichaPredial.xhtml");
            predio = new CatPredio();
        } else {
            Faces.messageWarning(null, "Advertencia!", "El predio no registra ninguna matricula inmobiliaria, revise que los datos ingresados sean correctos");
        }
    }

    public void salirPH() {
        JsfUti.executeJS("PF('dlgResultado').hide()");
        JsfUti.redirectFaces("/vistaprocesos/catastro/gestionPredios.xhtml");
    }

    public void listarPredios() {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        options.put("width", "70%");
        options.put("closable", true);
        options.put("contentWidth", "100%");
        RequestContext.getCurrentInstance().openDialog("/resources/dialog/predios", options, null);
    }

    public void seleccionarPredio(SelectEvent event) {
        if (event != null) {
            if (event.getObject() == null) {
                Faces.messageWarning(null, "Advertencia!", "Debe seleccionar el predio...");
                return;
            }
            predio = (CatPredio) ((ArrayList) event.getObject()).get(0);
            if (predio.getAreaSolar() != null && predio.getAreaSolar().compareTo(BigDecimal.ZERO) > 0) {
                if (predio.getRegFicha() != null) {
                    predio.setNumeroFicha(new BigInteger(predio.getRegFicha().getNumFicha() + ""));
                }
            } else {
                Faces.messageWarning(null, "Advertencia!", "El predio matriz debe tener area de solar y ficha registral");
            }
        }
    }

    public void crearAlicuotas() {
        verticales = new ArrayList<>();
        horizontales = new ArrayList<>();
        if (maxh == null) {
            JsfUti.messageError(null, "Debe Ingresar el número de pisos", "Error");
            return;
        }
        if (maxh == null) {
            JsfUti.messageError(null, "Debe Ingresar el número de divisiones por piso", "Error");
            return;
        }
        if (maxv > 0 && maxh > 0) {
            int length = 0;
            for (int i = 1; i <= maxv; i++) {
                for (int j = 1; j <= maxh; j++) {
                    agregarDatos(i, j, "Departamento");
                    length = j;
                }
                if (bod > 0) {
                    for (int j = 1; j <= bod; j++) {
                        agregarDatos(i, j + length, "Bodega");
                        length = j + length;
                    }
                }
                if (pq > 0) {
                    for (int j = 1; j <= pq; j++) {
                        agregarDatos(i, j + length, "Parqueadero");
                        length = j + length;
                    }
                }
                if (locComer > 0) {
                    for (int j = 1; j <= locComer; j++) {
                        agregarDatos(i, j + length, "Local Comercial");
                        length = j + length;
                    }
                }
                if (otros > 0) {
                    for (int j = 1; j <= otros; j++) {
                        agregarDatos(i, j + length, "Otros");
                        length = j + length;
                    }
                }
            }
        }
    }
    
    public void eventManzanaBySector(CatPredio p){
        System.out.println("isNuevaManzana" + isNuevaManzana);
        if(isNuevaManzana){
            getLastManzanaInSector(p);
        }
    }
    
    public void getLastManzanaInSector(CatPredio p){
        Integer mz = 0;
        System.out.println("Predio" + p.getProvincia() + p.getCanton() + p.getParroquia() + p.getZona() +  p.getSector() + p.getMz() );
        mz = catast.getManzanaMaxPredio(p);
        if(mz!= null){
            this.predio.setMz((short) (mz + 1));
            p.setMz((short) (mz + 1));
        }else{
            this.predio.setMz((short) 1);
            p.setMz((short)1);
        }
        
        System.out.println("this.predio.setMz" + this.predio.getMz());
        getLastSolarPredioInManzana(p);
    }
    
    public void getLastSolarPredioInManzana(CatPredio p){
        Integer solar = 0;
        System.out.println("Predio" + p.getProvincia() + p.getCanton() + p.getParroquia() + p.getZona() +  p.getSector() + p.getMz() );
        solar = catast.getSolarMaxPredio(p);
        if(solar!= null){
            this.predio.setSolar((short) (solar + 1));
        }else{
            this.predio.setSolar((short) 1);
        }
        
    }

    protected void agregarDatos(int i, int j, String nombrePredio) {
        CatPredio vi = new CatPredio();
        vi.setPredioRaiz(new BigInteger(predio.getId().toString()));
        vi.setProvincia(predio.getProvincia());
        vi.setCanton(predio.getCanton());
        vi.setParroquia(predio.getParroquia());
        vi.setZona(predio.getZona());
        vi.setSector(predio.getSector());
        vi.setMz(predio.getMz());
        vi.setSolar(predio.getSolar());
        vi.setLote(predio.getSolar());
        vi.setCdla(new Short("0"));
        vi.setMzdiv(new Short("0"));
        vi.setDiv1(new Short("0"));
        vi.setDiv2(new Short("0"));
        vi.setDiv3(new Short("0"));
        vi.setDiv4(new Short("0"));
        vi.setDiv5(new Short("0"));
        vi.setDiv6(new Short("0"));
        vi.setDiv7(new Short("0"));
        vi.setDiv8(new Short("0"));
        vi.setDiv9(new Short("0"));
        vi.setPhv(new Short("0"));
        vi.setPhh(new Short("0"));
        vi.setBloque(predio.getBloque());
        vi.setPiso(new Short("" + i));
        vi.setUnidad(new Short("" + j));
        vi.setTipoConjunto(predio.getTipoConjunto());
        vi.setInstCreacion(new Date());
        vi.setFormaSolar(predio.getFormaSolar());
        vi.setTopografiaSolar(predio.getTopografiaSolar());
        vi.setTipoSuelo(predio.getTipoSuelo());
        vi.setTenencia(predio.getTenencia());
        vi.setPropiedad(predio.getPropiedad());
        vi.setTipovia(predio.getTipovia());
        vi.setOtroTipovia(predio.getOtroTipovia());
        vi.setSubsector(predio.getSubsector());
        vi.setUnidadMedida(predio.getUnidadMedida());
        vi.setAvaluoSolar(predio.getAvaluoSolar());
        vi.setAvaluoConstruccion(predio.getAvaluoConstruccion());
        vi.setNombreEdificio(predio.getNombreEdificio());
        vi.setEstado("A");
        vi.setCalle(predio.getCalle());
        vi.setCalleAv(predio.getCalleAv());
        vi.setCalleS(predio.getCalleS());
        vi.setClaveCat(predio.getClaveCat());
        vi.setUsuarioCreador(usr);
        vi.setUrbMz(predio.getUrbMz());
        vi.setUrbSec(predio.getUrbSec());
        vi.setUrbSecnew(predio.getUrbSecnew());
        vi.setUrbSolarnew(predio.getUrbSolarnew());
        vi.setDivisionUrb(predio.getDivisionUrb());
        vi.setSubsector(predio.getSubsector());
        vi.setCiudadela(predio.getCiudadela());
        vi.setSector(predio.getSector());
        vi.setMz(predio.getMz());
        vi.setCdla(predio.getCdla());
        vi.setMzdiv(predio.getMzdiv());
        vi.setSolar(predio.getSolar());
        vi.setPhv(new Short("" + i));
        vi.setPhh(new Short(j + ""));
        vi.setCrear(Boolean.FALSE);
        vi.setPropiedadHorizontal(true);
        vi.setAreaSolar(predio.getAreaSolar());
        if (predio.getCatPredioS4() != null) {
            try {
                vi.setCatPredioS4(new CatPredioS4());
                EntityBeanCopy.cloneClass(predio.getCatPredioS4(), vi.getCatPredioS4());
            } catch (Exception ex) {
                Logger.getLogger(GestionFicha.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (predio.getCatPredioPropietarioCollection() != null) {
            try {
                predio.getCatPredioPropietarioCollection().size();
                vi.setCatPredioPropietarioCollection(EntityBeanCopy.cloneClass(predio.getCatPredioPropietarioCollection()));
            } catch (Exception ex) {
                Logger.getLogger(GestionFicha.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (predio.getCatPredioS6() != null) {
            try {
                vi.setCatPredioS6(new CatPredioS6());
                EntityBeanCopy.cloneClass(predio.getCatPredioS6(), vi.getCatPredioS6());
                vi.getCatPredioS6().setCtlgItemCollection(EntityBeanCopy.cloneClass(predio.getCatPredioS6().getCtlgItemCollection()));
                vi.getCatPredioS6().setCtlgItemCollectionInstalacionEspecial(EntityBeanCopy.cloneClass(predio.getCatPredioS6().getCtlgItemCollectionInstalacionEspecial()));
            } catch (Exception ex) {
                Logger.getLogger(GestionFicha.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (predio.getCatEscrituraCollection() != null) {
            try {
                vi.setCatEscrituraCollection(EntityBeanCopy.cloneClass(predio.getCatEscrituraCollection()));
            } catch (Exception ex) {
                Logger.getLogger(GestionFicha.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (predio.getUsoSolar() != null) {
            vi.setUsoSolar(predio.getUsoSolar());
        }
        vi.setNombreEdificio(nombrePredio);
        if (Utils.isNotEmpty((List<?>) predio.getCatPredioEdificacionCollection())) {
            predio.getCatPredioEdificacionCollection().stream().filter((ed) -> (ed.getNoEdificacion() == 1)).forEachOrdered((ed) -> {
                List<CtlgItem> itemsByCatalogo = this.getCatas().getItemsByCatalogo("edif.uso_constructivo_piso");
                itemsByCatalogo.forEach((ctlgItem) -> {
                    if (nombrePredio.equalsIgnoreCase("Otros")) {
                        ed.setUsoConstructivoPiso(ctlgItem);
                    } else if (nombrePredio.equalsIgnoreCase("Bodega")) {
                        ed.setUsoConstructivoPiso(ctlgItem);
                    } else if (nombrePredio.equalsIgnoreCase("Departamento")) {
                        ed.setUsoConstructivoPiso(ctlgItem);
                    } else if (nombrePredio.equalsIgnoreCase("Parqueadero")) {
                        ed.setUsoConstructivoPiso(ctlgItem);
                    } else if (nombrePredio.equalsIgnoreCase("Local Comercial")) {
                        ed.setUsoConstructivoPiso(ctlgItem);
                    }
                });
                vi.setCatPredioEdificacionCollection(new ArrayList<>());
                vi.getCatPredioEdificacionCollection().add(ed);
            });

        }
        verticales.add(vi);
    }

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        if (newValue == null) {
            Faces.messageWarning(null, "Advertencia!", "El dato ingresado es incorrecto");
        }
    }

    public void registrarPH() {
        if (this.idDoc == null) {
            JsfUti.messageError(null, "ERROR AL CREAR PHS", "DEBE SELECCIONAR EL PDF DE 'DECLARATORIA DE PROPIEDAD HORINZONTAL' PARA CONTINUAR.");
            return;
        }
        resultado = IngresarPHs(verticales, horizontales);
        if (resultado != null) {
            Faces.messageInfo(null, "Nota!", "Se crearon " + resultado.size() + ", propiedados horizontales");
            Faces.executeJS("PF('dlgSubirDocumento').hide(); PF('dlgResultado').show()");
        } else {
            Faces.messageWarning(null, "Advertencia!", "No ha sido posiblre crear las propiedades, verifique que los datos sean correctos, o el predio matriz tenga la informacion respectiva");
        }

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
            documentoBean.setRaiz(predio.getId());
            documentoBean.setContentType(event.getFile().getContentType());
            documentoBean.setDocumentoId(documentoId);
            documentoBean.setIdentificacion("Propiedad Horizontal");
            GeDocumentos saveDocumento = documentoBean.saveDocumento();
            if (saveDocumento != null) {
                this.idDoc = saveDocumento.getId();
            }
            is.close();
            out.close();
            Faces.messageInfo(null, "Nota1", "Archivo cargado Satisfactoriamente");
        } catch (IOException e) {
            Logger.getLogger(FichaPredial.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public Boolean getPh() {
        return ph;
    }

    public void setPh(Boolean ph) {
        this.ph = ph;
    }

    public List<SectorValorizacion> getSubsectores() {
        return subsectores;
    }

    public void setSubsectores(List<SectorValorizacion> subsectores) {
        this.subsectores = subsectores;
    }

    public Boolean getBusqueda() {
        return busqueda;
    }

    public void setBusqueda(Boolean busqueda) {
        this.busqueda = busqueda;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public List<CatPredio> getHorizontales() {
        return horizontales;
    }

    public void setHorizontales(List<CatPredio> horizontales) {
        this.horizontales = horizontales;
    }

    public List<CatPredio> getVerticales() {
        return verticales;
    }

    public void setVerticales(List<CatPredio> verticales) {
        this.verticales = verticales;
    }

    public Integer getMaxh() {
        return maxh;
    }

    public void setMaxh(Integer maxh) {
        this.maxh = maxh;
    }

    public Integer getMaxv() {
        return maxv;
    }

    public void setMaxv(Integer maxv) {
        this.maxv = maxv;
    }

    public List<CatPredio> getResultado() {
        return resultado;
    }

    public void setResultado(List<CatPredio> resultado) {
        this.resultado = resultado;
    }

    public Integer getBod() {
        return bod;
    }

    public void setBod(Integer bod) {
        this.bod = bod;
    }

    public Integer getPq() {
        return pq;
    }

    public void setPq(Integer pq) {
        this.pq = pq;
    }

    public Integer getOtros() {
        return otros;
    }

    public void setOtros(Integer otros) {
        this.otros = otros;
    }

    public Integer getLocComer() {
        return locComer;
    }

    public void setLocComer(Integer locComer) {
        this.locComer = locComer;
    }

    public Boolean getIsNuevaManzana() {
        return isNuevaManzana;
    }

    public void setIsNuevaManzana(Boolean isNuevaManzana) {
        this.isNuevaManzana = isNuevaManzana;
    }
    
    

}
