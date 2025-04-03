/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs;

import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.RenTipoTransaccion;
import com.origami.sgm.managedbeans.Recaudaciones;
import com.origami.sgm.services.interfaces.TitulosReporteCacheLocal;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import util.EntityBeanCopy;

/**
 *
 * @author Joao Sanga
 */
@Singleton(name = "titulosReporteCache")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)

public class TitulosReporteCache implements TitulosReporteCacheLocal {

    @javax.inject.Inject
    private Entitymanager manager;

    protected ConcurrentMap<String, TreeNode> treeMap;
    protected ConcurrentMap<String, String> lockerMap = new ConcurrentHashMap<>();

    @Override
    public void clearCache() {
        treeMap.remove("arbol_recaudaciones");
    }

    @Override
    public TreeNode getTree() {
        TreeNode root = treeMap.get("arbol_recaudaciones");
        if (root == null) {
            generarArbol();
            return getTree();
        }

        return (TreeNode) EntityBeanCopy.clone(root);
    }

    private TreeNode createTree() {
        TreeNode root = new DefaultTreeNode("Titulos", null);
        this.llenarArbol(root);
        return root;
    }

    private void llenarArbol(TreeNode root) {
        try {
            List<RenTipoLiquidacion> raices = manager.findAll(QuerysFinanciero.getRenTransaccionesPadres, new String[]{"idPadre"}, new Object[]{0L});
            for (RenTipoLiquidacion temp : raices) {
                if (!temp.getTomado()) {
                    temp.setTomado(true);
                    TreeNode node = new DefaultTreeNode(temp, root);
                    llenarHijosArbol(temp, node);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void llenarHijosArbol(RenTipoLiquidacion hoja, TreeNode padre) {
        try {
            List<RenTipoLiquidacion> hijos;
            RenTipoTransaccion t = (RenTipoTransaccion) manager.find(RenTipoTransaccion.class, 3L);
            hijos = manager.findAll(QuerysFinanciero.getRenTransaccionesHijos, new String[]{"idPadre", "tTransaccion"}, new Object[]{hoja.getId(), t});
            
            if (hijos == null || hijos.isEmpty()) {
                return;
            }

            for (RenTipoLiquidacion temp2 : hijos) {
                if (!temp2.getTomado()) {
                    TreeNode node = new DefaultTreeNode(temp2, padre);
                    temp2.setTomado(true);
                    llenarHijosArbol(temp2, node);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Recaudaciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    protected void generarArbol() {

        synchronized (getLockerObject()) {

            // comprobar si no se entró en espera y ya existe el "arbol_recaudaciones" mapeado:
            TreeNode tree = treeMap.get("arbol_recaudaciones");
            if (tree == null) {
                this.loadTree();
            }

        }
    }

    private void loadTree() {
        //TreeNode tree = this.getTree();

        TreeNode tree = createTree();
        // si se encontro menubar, realizar la clonacion
        if (tree != null) {
            treeMap.putIfAbsent("arbol_recaudaciones", tree);
        }
    }

    protected String getLockerObject() {
        lockerMap.putIfAbsent("arbol_recaudaciones", "arbol_recaudaciones");

        return lockerMap.get("arbol_recaudaciones");
    }

    /**
     * Inicializa el map de menubars en vacio
     */
    protected void initMenubarsMap() {
        this.treeMap = new ConcurrentHashMap<>();
    }

    /**
     * Inicializa el singleton ejb
     */
    @PostConstruct
    protected void init() {
        this.initMenubarsMap();
    }

}
