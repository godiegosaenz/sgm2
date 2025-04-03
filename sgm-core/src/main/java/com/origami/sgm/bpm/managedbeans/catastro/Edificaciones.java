/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.censocat.restful.JsonUtils;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.CatEdfCategProp;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioEdificacionProp;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.historic.Predio;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class Edificaciones implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    private UserSession sess;
    @javax.inject.Inject
    private CatastroServices catas;
    @javax.inject.Inject
    private Entitymanager services;
    @Inject
    private ServletSession ss;

    protected Boolean editable, mostrar = false;
    protected CatPredio predio;
    protected CatPredioEdificacion edificacion;
    protected List<CatPredioEdificacion> edificacionEliminar;
    protected List<CatPredioEdificacionProp> listEdificaciones;
    protected CatPredioEdificacionProp propiedades;
    protected List<CatPredioEdificacionProp> propiedadesEliminar;
    protected List<CatEdfProp> catEdfProps;
    protected CatEdfCategProp caracteristica;
    private JsonUtils json;
    private String pant, pactual;
    private Predio pred;

    public Edificaciones() {
    }

    @PostConstruct
    private void initView() {
        if (sess != null) {
            json = new JsonUtils();
            if (ss.getParametros() != null) {
                edificacionEliminar = new ArrayList<>();
                propiedadesEliminar = new ArrayList<>();
                listEdificaciones = new ArrayList<>();
                predio = new CatPredio();
                if ((ss.getParametros().get("numPredio") != null || ss.getParametros().get("idPredio") != null) && ss.getParametros().get("edit") != null) {
                    editable = Boolean.parseBoolean(ss.getParametros().get("edit").toString());
                    if (editable) {
                        if (ss.getParametros().get("numPredio") != null) {
                            predio = catas.getPredioNumPredio(Long.parseLong(ss.getParametros().get("numPredio").toString()));
                            pant = json.generarJson(predio);
                        } else {
                            predio = catas.getPredioId(Long.parseLong(ss.getParametros().get("idPredio").toString()));
                            pant = json.generarJson(predio);
                        }
                    } else {
                        predio = catas.getPredioId(Long.parseLong(ss.getParametros().get("idPredio").toString()));
                        pant = json.generarJson(predio);
                    }

                }
            }
        }
    }

    public void agregarEdicicacion() {
        edificacion = new CatPredioEdificacion();

    }

    public void editarEdificacion(CatPredioEdificacion edif) {
        try {
            edificacion = new CatPredioEdificacion();
            propiedades = new CatPredioEdificacionProp();
            catEdfProps = new ArrayList<>();
            caracteristica = new CatEdfCategProp();
            mostrar = true;
            propiedades.setPorcentaje(BigDecimal.ZERO);
            listEdificaciones = new ArrayList<>();
            if (edif != null) {
                List<CatPredioEdificacionProp> list = new ArrayList<>();
                for (CatPredioEdificacionProp l : edif.getCatPredioEdificacionPropCollection()) {
                    if (l.getEstado()) {
                        list.add(l);
                    }
                }

                listEdificaciones.addAll(list);
                edificacion = edif;
            } else {

            }
        } catch (Exception e) {
            Logger.getLogger(Edificaciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public List<CtlgItem> getEstadoConservacion() {
        return catas.propiedadHorizontal().getCtlgItem("edif.estado_cons");
    }

    public List<CatEdfCategProp> getCatEdfCategProp() {
        return catas.propiedadHorizontal().getPermiso().getCatEdfCategPropList();
    }

    public void updateCaracteristica() {
        catEdfProps = getCatEdfProp();
        JsfUti.update("frmEdif:selec");
    }

    public List<CatEdfProp> getCatEdfProp() {
        if (caracteristica != null) {
            return catas.propiedadHorizontal().getPermiso().getCatEdfPropList(caracteristica.getId());
        }
        return null;
    }

    public void agregarCaracteristicaEdificion() {
        try {
            if (caracteristica == null) {
                JsfUti.messageInfo(null, "Debe seleccionar la Categoria.", "Campo Faltantes");
                return;
            }
            if (propiedades.getProp() == null) {
                JsfUti.messageInfo(null, "Debe seleccionar la Característica.", "Campo Faltantes");
                return;
            }
            if (edificacion.getCatPredioEdificacionPropCollection() != null) {
                for (CatPredioEdificacionProp d : edificacion.getCatPredioEdificacionPropCollection()) {
                    if (d.getProp().getId().compareTo(propiedades.getProp().getId()) == 0) {
                        JsfUti.messageInfo(null, "Característica ya fue agregada.", "Característica");
                        return;
                    }
                }
            }

            propiedades.setEstado(true);            
            listEdificaciones.add(propiedades);
            edificacion.setCatPredioEdificacionPropCollection(listEdificaciones);
            caracteristica = new CatEdfCategProp();
            propiedades = new CatPredioEdificacionProp();
            propiedades.setPorcentaje(BigDecimal.ZERO);
        } catch (Exception e) {
            Logger.getLogger(Edificaciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void eliminarCaracteristica(CatPredioEdificacionProp prop) {
        try {
            int index = 0;
            int contador = 0;
            for (CatPredioEdificacionProp c : listEdificaciones) {
                if (c.getProp().getId().compareTo(prop.getProp().getId()) == 0) {
                    index = contador;
                    break;
                }
                contador++;
            }
            if (prop.getId() != null) {
                prop.setEstado(false);
                services.update(prop);
            }
            listEdificaciones.remove(index);
            edificacion.getCatPredioEdificacionPropCollection().remove(index);
        } catch (Exception e) {
            Logger.getLogger(Edificaciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void guardarEdificacion() {
        try {
            if (edificacion.getNoEdificacion() != null) {
                predio.getCatPredioEdificacionCollection().size();
            } else {
                if (Utils.isEmpty((List<?>) predio.getCatPredioEdificacionCollection())) {
                    predio.setCatPredioEdificacionCollection(new ArrayList<CatPredioEdificacion>());
                }
                Integer numEdif = predio.getCatPredioEdificacionCollection().size();
                edificacion.setPredio(predio);
                edificacion.setNoEdificacion(numEdif.shortValue());
                //edificacion.setCatPredioEdificacionPropCollection(new ArrayList<CatPredioEdificacionProp>());
                //edificacion.setCatPredioEdificacionPropCollection(listEdificaciones);
                predio.getCatPredioEdificacionCollection().add(edificacion);
                edificacion = new CatPredioEdificacion();
            }
            mostrar = false;
        } catch (Exception e) {
            Logger.getLogger(Edificaciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void borrarEdificacion(CatPredioEdificacion edificacion) {
        try {
            if (predio.getCatPredioEdificacionCollection().size() == 1) {
                JsfUti.messageInfo(null, "Debe existir al menos 1 Edificacion", "");
                return;
            }
            edificacion.setEstado("I");
            if (edificacion.getId() != null) {
                //edificacionEliminar.add(edificacion);
                services.update(edificacion);
            }

            predio.getCatPredioEdificacionCollection().remove(edificacion);
        } catch (Exception e) {
            Logger.getLogger(Edificaciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void guardarAllEdificacion() {
        try {
            if (mostrar) {
                JsfUti.messageError(null, "Error", "Debe guardar la edificación que se esta editando.");
                return;
            }
            ss.instanciarParametros();
            ss.agregarParametro("idPredio", predio.getId());
            ss.agregarParametro("agregado", true);
            Boolean ok = catas.editarPrediosEdificaciones(predio, edificacionEliminar, propiedadesEliminar);
            if (ok) {                
                pactual = json.generarJson(predio);
                predio = catas.getAvaluoPredio(predio, Utils.getAnio(new Date()));
                if (pactual != null) {
//                    pred = new Predio();
//                    pred.setPredio(predio.getNumPredio().longValue());
//                    pred.setFecAct(new Date());
//                    pred.setFichaAnt(pant);
//                    pred.setFichaAct(pactual);
//                    pred.setUsuario(sess.getName_user());
//                    pred.setObservacion("Actualizacion 10. Edificaciones");
//                    catas.guardarHistoricoPredio(pred);
                    catas.guardarHistoricoPredio(predio.getNumPredio().longValue(), pant, pactual, sess.getName_user(), "Actualización 10. Edificaciones", "", "", "","", null);
                }
                if (editable) {
                    JsfUti.redirectFaces("/faces/vistaprocesos/catastro/editarPredio.xhtml");
                } else {
                    JsfUti.redirectFaces("/faces/vistaprocesos/catastro/agregarPredio.xhtml");
                }
            } else {
                JsfUti.messageError(null, "Ocurrio un error al intentar guardar los cambios.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(Edificaciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CatPredioEdificacion getEdificacion() {
        return edificacion;
    }

    public void setEdificacion(CatPredioEdificacion edificacion) {
        this.edificacion = edificacion;
    }

    public Boolean getMostrar() {
        return mostrar;
    }

    public void setMostrar(Boolean mostrar) {
        this.mostrar = mostrar;
    }

    public CatPredioEdificacionProp getPropiedades() {
        return propiedades;
    }

    public void setPropiedades(CatPredioEdificacionProp propiedades) {
        this.propiedades = propiedades;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public List<CatEdfProp> getCatEdfProps() {
        return catEdfProps;
    }

    public void setCatEdfProps(List<CatEdfProp> catEdfProps) {
        this.catEdfProps = catEdfProps;
    }

    public CatEdfCategProp getCaracteristica() {
        return caracteristica;
    }

    public void setCaracteristica(CatEdfCategProp caracteristica) {
        this.caracteristica = caracteristica;
    }

    public List<CatPredioEdificacionProp> getListEdificaciones() {
        return listEdificaciones;
    }

    public void setListEdificaciones(List<CatPredioEdificacionProp> listEdificaciones) {
        this.listEdificaciones = listEdificaciones;
    }

}
