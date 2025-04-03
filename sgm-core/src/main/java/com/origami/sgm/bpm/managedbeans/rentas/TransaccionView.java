/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas;

import com.origami.session.UserSession;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.RenTipoTransaccion;
import com.origami.sgm.lazymodels.TipoLiquidacionLazy;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import util.EntityBeanCopy;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class TransaccionView implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(TransaccionView.class.getName());

    @Inject
    private UserSession uSession;

    @javax.inject.Inject
    protected Entitymanager services;

    @javax.inject.Inject
    protected RentasServices servicesRentas;

    private Boolean tienePadre;
    private List<RenTipoTransaccion> tiposTransaccion;
    private RenTipoLiquidacion transaccion, transaccionPadre;
    private TreeNode root;
    private List<RenTipoLiquidacion> tipoLiqSel, tipoLiqHijos, tiposLiquidaciones;
    private TipoLiquidacionLazy tiposLazy;

    @PostConstruct
    public void initView() {

        try {
            if (uSession != null) {
                tiposTransaccion = new ArrayList<>();
                for (Object temp : services.findAll(QuerysFinanciero.getTipoTransacciones, new String[]{}, new Object[]{})) {
                    tiposTransaccion.add((RenTipoTransaccion) EntityBeanCopy.clone(temp));
                }
                tiposLiquidaciones = services.findAll(QuerysFinanciero.getRenTipoLiquidacionList, new String[]{}, new Object[]{});

                tiposLazy = new TipoLiquidacionLazy(2, true);

                root = new DefaultTreeNode();
                llenarArbol();

                //root.getChildren().add(root)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void nuevoTipoLiquidacion() {
        transaccion = new RenTipoLiquidacion();
    }

    public void llenarArbol() {
        try {
            List<RenTipoLiquidacion> raices;
            raices = services.findAll(QuerysFinanciero.getRenTransaccionesPadres, new String[]{"idPadre"}, new Object[]{0L});

            for (RenTipoLiquidacion temp : raices) {
                if (!temp.getTomado()) {
                    temp.setTomado(true);
                    TreeNode node = new DefaultTreeNode(temp.getNombreTransaccion(), root);
                    llenarHijosArbol(temp, node);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void llenarHijosArbol(RenTipoLiquidacion hoja, TreeNode padre) {
        try {
            List<RenTipoLiquidacion> hijos;

            hijos = services.findAll(QuerysFinanciero.getRenTransaccionesPadres, new String[]{"idPadre"}, new Object[]{hoja.getId()});
            if (hijos == null || hijos.isEmpty()) {
                return;
            }

            for (RenTipoLiquidacion temp2 : hijos) {
                if (!temp2.getTomado()) {
                    TreeNode node = new DefaultTreeNode(temp2.getNombreTransaccion(), padre);
                    temp2.setTomado(true);
                    llenarHijosArbol(temp2, node);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void guardarTransaccionesHijas() {
        try {
            for (RenTipoLiquidacion temp : tipoLiqSel) {
                if (!temp.equals(transaccionPadre)) {
                    temp.setTransaccionPadre(transaccionPadre.getId());
                    //services.updateAndPersistEntity(temp);
                    services.persist(temp);
                }
            }
            JsfUti.messageInfo(null, "Info", "Se guardaron las subtransacciones correctamente");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void eliminarTransaccion(RenTipoLiquidacion rtl) {
        try {
            rtl.setTransaccionPadre(0L);
            services.persist(rtl);
            this.tipoLiqHijos.remove(rtl);
            JsfUti.messageInfo(null, "Info", "Se quitó la transacción");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selTransaccionEdit(RenTipoLiquidacion rtl) {
        transaccion = rtl;
        if (transaccion.getTransaccionPadre() != null) {
            transaccionPadre = (RenTipoLiquidacion) services.find(RenTipoLiquidacion.class, transaccion.getId());
        }
    }

    public void guardarNuevoTitulo() {
        try {
            if (transaccion.getPrefijo().trim().length() > 3) {
                JsfUti.messageError(null, "Error", "Prefijo debe contener maximo 3 caracteres.");
            }
            transaccion.setNombreTitulo(transaccion.getNombreTitulo().toUpperCase());
            transaccion.setPrefijo(transaccion.getPrefijo().trim().toUpperCase());

            Long temp = (Long) services.find(QuerysFinanciero.getCodigoTituloReporte, new String[]{"prefijo", "nomTitulo"}, new Object[]{transaccion.getPrefijo(), transaccion.getNombreTitulo()});
            if (temp == null) {
                transaccion.setFechaIngreso(new Date());
                transaccion.setUsuarioIngreso(uSession.getName_user());
                transaccion.setEstado(true);
                transaccion.setMostrarTransaccion(true);
                transaccion.setTransaccionPadre(0L);
                transaccion.setCodigoTituloReporte(servicesRentas.generarCodTitRep());
                transaccion = (RenTipoLiquidacion) services.persist(transaccion);
                if (transaccion != null && transaccion.getId() != null) {
                    JsfUti.messageInfo(null, "Info", "Título de reporte creado correctamente");
                    tiposLazy = new TipoLiquidacionLazy(2, true);
                    tiposLiquidaciones = services.findAll(QuerysFinanciero.getRenTipoLiquidacionList, new String[]{}, new Object[]{});
                } else {

                }
            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }

    }

    public void nuevaTransaccion() {
        transaccion = new RenTipoLiquidacion();
    }

    public void guardarEdicionTransaccion() {
        try {
            if (servicesRentas.editarRenTipoLiquidacion(transaccion)) {
                JsfUti.messageInfo(null, "Info", "Se actualizó la transacción correctamente");
            } else {
                JsfUti.messageError(null, "Error", "Ocurrió un error al actualizar los datos. Inténtelo nuevamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarTitulo(RenTipoLiquidacion rtl) {
        try {
            rtl.setEstado(false);
            if (services.update(rtl)) {
                tiposLiquidaciones = services.findAll(QuerysFinanciero.getRenTipoLiquidacionList, new String[]{}, new Object[]{});
                JsfUti.messageInfo(null, "Info", "Se actualizó la transacción correctamente");
            } else {
                JsfUti.messageError(null, "Error", "Ocurrió un error al actualizar los datos. Inténtelo nuevamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public Boolean getTienePadre() {
        return tienePadre;
    }

    public void setTienePadre(Boolean tienePadre) {
        this.tienePadre = tienePadre;
    }

    public List<RenTipoTransaccion> getTiposTransaccion() {
        return tiposTransaccion;
    }

    public void setTiposTransaccion(List<RenTipoTransaccion> tiposTransaccion) {
        this.tiposTransaccion = tiposTransaccion;
    }

    public List<RenTipoLiquidacion> getTipoLiqSel() {
        return tipoLiqSel;
    }

    public void setTipoLiqSel(List<RenTipoLiquidacion> tipoLiqSel) {
        this.tipoLiqSel = tipoLiqSel;
    }

    public RenTipoLiquidacion getTransaccionPadre() {
        return transaccionPadre;
    }

    public void setTransaccionPadre(RenTipoLiquidacion transaccionPadre) {
        try {
            this.transaccionPadre = transaccionPadre;
            tipoLiqHijos = (List<RenTipoLiquidacion>) services.findAll(QuerysFinanciero.getRenTransaccionesHijos, 
                    new String[]{"idPadre"}, new Object[]{this.transaccionPadre.getId()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<RenTipoLiquidacion> getTipoLiqHijos() {
        return tipoLiqHijos;
    }

    public void setTipoLiqHijos(List<RenTipoLiquidacion> tipoLiqHijos) {
        this.tipoLiqHijos = tipoLiqHijos;
    }

    public RenTipoLiquidacion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(RenTipoLiquidacion transaccion) {
        this.transaccion = transaccion;
    }

    public List<RenTipoLiquidacion> getTiposLiquidaciones() {
        return tiposLiquidaciones;
    }

    public void setTiposLiquidaciones(List<RenTipoLiquidacion> tiposLiquidaciones) {
        this.tiposLiquidaciones = tiposLiquidaciones;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TipoLiquidacionLazy getTiposLazy() {
        return tiposLazy;
    }

    public void setTiposLazy(TipoLiquidacionLazy tiposLazy) {
        this.tiposLazy = tiposLazy;
    }

}
