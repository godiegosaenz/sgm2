package com.origami.sgm.managedbeans.component;

import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioObraInterna;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.lazymodels.CatPredioObraInternaLazy;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import util.Faces;
import util.JsfUti;

@Named
@ViewScoped
public class ObrasInternas implements Serializable {

    private static final Logger LOG = Logger.getLogger(ObrasInternas.class.getName());
    private Boolean nuevo;
    private CatPredioObraInterna obrInterna;
    private String idCatPredioObraInterna;
    private String idPredio;
    private String esNuevo;
    private String ver;
    private Boolean esVer;

    private List<CtlgItem> materiales;

    private CatPredioObraInternaLazy obrasInternas;
    private List<CatPredioObraInterna> seleccionados;
    @javax.inject.Inject
    private CatastroServices ejb;
    @Inject
    private UserSession us;
    @javax.inject.Inject
    private Entitymanager manager;

    public void initView() {
        try {
            if (!JsfUti.isAjaxRequest()) {
                obrasInternas = new CatPredioObraInternaLazy(Boolean.TRUE);
                nuevo = Boolean.valueOf(esNuevo);
                esVer = Boolean.valueOf(ver);
                if (nuevo) {
                    if (idPredio == null) {
                        return;
                    }
                    obrInterna = new CatPredioObraInterna();
                    obrInterna.setPredio(new CatPredio(Long.valueOf(idPredio)));
                    obrInterna.setUsuario(us.getName_user());
                    obrInterna.setEstado("A");
                } else {
                    if (idCatPredioObraInterna == null) {
                        return;
                    }
                    if (Long.valueOf(idCatPredioObraInterna) > 0l) {
                        obrInterna = ejb.getPredioObraInternaById(Long.valueOf(idCatPredioObraInterna));
                    } else {
                        obrInterna = (CatPredioObraInterna) Faces.getSessionBean("obraInterna");
                        Faces.setSessionBean("obraInterna", null);
                        if (obrInterna == null) {
                            return;
                        }
                    }

                }
            }
        } catch (Exception ne) {
            LOG.log(Level.SEVERE, null, ne);
        }
    }

    public void referenciarMaterial(CtlgItem prot) {
        if (obrInterna.getTipo() != null) {
            System.out.println(obrInterna.getTipo());
        }
        if (prot != null) {
            materiales = manager.findAllEntCopy(Querys.getCtlgItemByParent, new String[]{"padre"}, new Object[]{prot.getId()});
        } else {
            Faces.messageWarning(null, "Advertencia!", "Elija el tipo de prototipo arquitectonico");
        }
    }

    private Boolean validarCampos() {
//        En caso de necesitar controlar el material de las obras internas
//        if (obrInterna.getMaterial() == null) {
//             JsfUti.messageError(null, "Error", "Debe seleccionar el material de la obra interna");
//             return false;
//        }
        if (obrInterna.getConservacion() == null) {
            JsfUti.messageError(null, "Error", "Debe seleccionar el estado de conservación");
            return false;
        }
        return true;
    }

    public void agregarObraInterna() {
        try {
            if (obrInterna == null) {
                JsfUti.messageError(null, "Error", "No se encontro registro a guardar");
                return;
            }
            if (!validarCampos()) {
                return;
            }
            obrInterna.setFecha(new Date());
            RequestContext.getCurrentInstance().closeDialog(ejb.guardarObraInterna(obrInterna, us.getName_user()));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void modificarObraInterna() {
        try {
            if (obrInterna == null) {
                JsfUti.messageError(null, "Error", "No se encontro registro a actualizar");
                return;
            }
            if (!validarCampos()) {
                return;
            }
            obrInterna.setModificado(us.getName_user());
            obrInterna = ejb.guardarObraInterna(obrInterna, us.getName_user());
            RequestContext.getCurrentInstance().closeDialog(obrInterna);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void selecObrasInternas() {
        if (seleccionados != null) {
            RequestContext.getCurrentInstance().closeDialog(seleccionados);
        }
    }

    public Boolean getNuevo() {
        return nuevo;
    }

    public void setNuevo(Boolean nuevo) {
        this.nuevo = nuevo;
    }

    public CatPredioObraInterna getObrInterna() {
        return obrInterna;
    }

    public void setObrInterna(CatPredioObraInterna obrInterna) {
        this.obrInterna = obrInterna;
    }

    public String getIdCatPredioObraInterna() {
        return idCatPredioObraInterna;
    }

    public void setIdCatPredioObraInterna(String idCatPredioObraInterna) {
        this.idCatPredioObraInterna = idCatPredioObraInterna;
    }

    public String getIdPredio() {
        return idPredio;
    }

    public void setIdPredio(String idPredio) {
        this.idPredio = idPredio;
    }

    public String getEsNuevo() {
        return esNuevo;
    }

    public void setEsNuevo(String esNuevo) {
        this.esNuevo = esNuevo;
    }

    public CatPredioObraInternaLazy getObrasInternas() {
        return obrasInternas;
    }

    public void setObrasInternas(CatPredioObraInternaLazy obrasInternas) {
        this.obrasInternas = obrasInternas;
    }

    public List<CatPredioObraInterna> getSeleccionados() {
        return seleccionados;
    }

    public void setSeleccionados(List<CatPredioObraInterna> seleccionados) {
        this.seleccionados = seleccionados;
    }

    public CatastroServices getEjb() {
        return ejb;
    }

    public void setEjb(CatastroServices ejb) {
        this.ejb = ejb;
    }

    public UserSession getUs() {
        return us;
    }

    public void setUs(UserSession us) {
        this.us = us;
    }

    public List<CtlgItem> getMateriales() {
        return materiales;
    }

    public void setMateriales(List<CtlgItem> materiales) {
        this.materiales = materiales;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public Boolean getEsVer() {
        return esVer;
    }

    public void setEsVer(Boolean esVer) {
        this.esVer = esVer;
    }
}
