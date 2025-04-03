/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CtlgItem;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import util.EntityBeanCopy;

/**
 *
 * @author dfcalderio
 */
@Named(value = "compView")
@ApplicationScoped
public class ComponenteView implements Serializable {

    @Inject
    private Entitymanager manager;

    private Integer itemBind;

    @PostConstruct
    public void init() {

    }

    public Integer valorItem(Object obj) {

        if (obj instanceof CtlgItem) {
            if (obj != null) {
                return ((CtlgItem) obj).getOrden();
            }
        }

        return null;
    }

    public List<CtlgItem> valoresByCatalogo(String catalogo) {
        String catag = catalogo.replace('-', '.');
        List<CtlgItem> ctlgItem = (List<CtlgItem>) EntityBeanCopy.clone(manager.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{catag}));
        return ctlgItem;
    }

    public List<SelectItem> valoresItemSelect(String catalogo) {
        List<SelectItem> items = new ArrayList<>();
        List<CtlgItem> ctlgItems = valoresByCatalogo(catalogo);
        ctlgItems.stream().map((v) -> new SelectItem("com.origami.sgm.entities.CtlgItem:" + v.getId() + ":java.lang.Long", v.getValor())).forEachOrdered((i) -> {
            items.add(i);

        });

        return items;
    }

    public String rangoOrdenes(String catalogo) {

        String rango = "";
        List<CtlgItem> items = valoresByCatalogo(catalogo);
        if (!items.isEmpty()) {
            if (items.size() == 1) {
                rango += items.get(0).getId() + ";" + items.get(0).getOrden();
            } else {
                for (int i = 0; i < items.size() - 1; i++) {
                    rango += items.get(i).getId() + ";" + items.get(i).getOrden() + "-";
                }
                rango += items.get(items.size() - 1).getId() + ";" + items.get(items.size() - 1).getOrden();
            }
        }
        return rango;
    }

    public List<Integer> ordenes(String catalago) {
        List<Integer> ordenes = new ArrayList<>();
        List<CtlgItem> items = valoresByCatalogo(catalago);
        if (!items.isEmpty()) {
            ordenes.add(items.get(0).getOrden());
        }
        return ordenes;
    }

    public void update() {

    }

    public Entitymanager getManager() {
        return manager;
    }

    public void setManager(Entitymanager manager) {
        this.manager = manager;
    }

    public Integer getItemBind() {
        itemBind = 0;
        return itemBind;
    }

    public void setItemBind(Integer itemBind) {
        this.itemBind = itemBind;
    }

}
