/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CtlgCatalogo;
import com.origami.sgm.entities.CtlgDescuentoEmision;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.lazymodels.CtlgCatalogoLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.jboss.logging.Logger;
import util.JsfUti;

/**
 *
 * @author Mariuly
 */
@Named
@ViewScoped
public class CatalogoMantenimiento implements Serializable {

    public static final long serialVersionUID = 1l;

    @javax.inject.Inject
    private Entitymanager manager;
    protected CtlgCatalogoLazy catalogos;
    protected CtlgCatalogo catalogo;
    protected CtlgItem item;
    protected CtlgItem itemHijo;
    protected List<CtlgItem> items;

    @PostConstruct
    public void init() {
        try {
            catalogos = new CtlgCatalogoLazy();
        } catch (Exception e) {
            Logger.getLogger(CatalogoMantenimiento.class.getName()).log(Logger.Level.FATAL, null, e);
        }
    }

    public void generarOrdenItems() {
        for (CtlgCatalogo ct : (List<CtlgCatalogo>) catalogos.getWrappedData()) {
            Object maxItem = manager.find("SELECT MAX(i.orden) FROM CtlgItem i WHERE i.catalogo=:catalogo", new String[]{"catalogo"}, new Object[]{ct});
            Integer max = Integer.valueOf(maxItem == null ? "1" : maxItem.toString());
            for (CtlgItem it : ct.getCtlgItemCollection()) {
                if (it.getOrden() == null) {
                    it.setOrden(max);
                    manager.persist(it);
                    max++;
                }
            }
        }
    }

    public void editar(CtlgCatalogo editar) {
        try {
            this.catalogo = editar;
        } catch (Exception e) {
            Logger.getLogger(CatalogoMantenimiento.class.getName()).log(Logger.Level.FATAL, null, e);
        }
    }

    public void editarItem(CtlgItem editar) {
        try {
            this.item = editar;
        } catch (Exception e) {
            Logger.getLogger(CatalogoMantenimiento.class.getName()).log(Logger.Level.FATAL, null, e);
        }
    }

    public void nuevo() {
        catalogo = new CtlgCatalogo();
    }

    public void nuevoItem() {
        if (catalogo == null) {
            JsfUti.messageError(null, "Error", "Debe Ingresar Catalogo");
            return;
        }
        if (catalogo.getId() == null) {
            JsfUti.messageError(null, "error", "Debe ingresar Catalogo");
            return;
        }
        item = new CtlgItem();
        JsfUti.executeJS("PF('dlgItemGuardar').show()");
    }

    public void guardar() {
        try {
            catalogo.setCtlgItemCollection(null);
            this.manager.persist(catalogo);
        } catch (Exception e) {
            Logger.getLogger(CatalogoMantenimiento.class.getName()).log(Logger.Level.FATAL, null, e);
        }
    }

    public void guardaritems() {
        if (itemHijo == null) {
            guardarItem();
        } else {
            guardarItemHijos();
        }
    }

    public void guardarItem() {
        try {
            if (item.getId() == null) {
                item.setCatalogo(catalogo);
            }
            item = (CtlgItem) this.manager.persist(item);
            if (!catalogo.getCtlgItemCollection().contains(item)) {
                catalogo.getCtlgItemCollection().add(item);
            }
        } catch (Exception e) {
            Logger.getLogger(CatalogoMantenimiento.class.getName()).log(Logger.Level.FATAL, null, e);
        }
    }

    public void eliminar(CtlgDescuentoEmision delete) {

        try {
            this.manager.delete(delete);
        } catch (Exception e) {
            Logger.getLogger(CatalogoMantenimiento.class.getName()).log(Logger.Level.FATAL, null, e);
        }
    }

    public void eliminarItem(CtlgItem delete) {

        try {
            this.catalogo.getCtlgItemCollection().remove(delete);
            this.manager.delete(delete);
        } catch (Exception e) {
            Logger.getLogger(CatalogoMantenimiento.class.getName()).log(Logger.Level.FATAL, null, e);
        }
    }

    public void nuevoItemHijos() {
        if (itemHijo == null) {
            JsfUti.messageError(null, "Error", "Debe Ingresar Catalogo");
            return;
        }
        item = new CtlgItem();
        JsfUti.executeJS("PF('dlgItemGuardar').show()");
    }

    public void hijos(CtlgItem item) {
        this.itemHijo = item;
        if (itemHijo == null) {
            return;
        }
        items = manager.findAllEntCopy(Querys.getCtlgItemByParent, new String[]{"padre"}, new Object[]{itemHijo.getId()});
    }

    public void guardarItemHijos() {
        try {
            if (itemHijo == null) {
                JsfUti.messageError(null, "Error", "Debe Guardar Item padre primero");
                return;
            }
            if (item.getId() == null) {
                item.setPadre(BigInteger.valueOf(itemHijo.getId()));
            }
            this.manager.persist(item);
            hijos(itemHijo);
        } catch (Exception e) {
            Logger.getLogger(CatalogoMantenimiento.class.getName()).log(Logger.Level.FATAL, null, e);
        }
    }

    public void eliminarItemHijos(CtlgItem delete) {
        try {
            this.items.remove(delete);
            this.manager.delete(delete);
        } catch (Exception e) {
            Logger.getLogger(CatalogoMantenimiento.class.getName()).log(Logger.Level.FATAL, null, e);
        }
    }

    public CtlgCatalogoLazy getCatalogos() {
        return catalogos;
    }

    public void setCatalogos(CtlgCatalogoLazy catalogos) {
        this.catalogos = catalogos;
    }

    public CtlgCatalogo getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(CtlgCatalogo catalogo) {
        this.catalogo = catalogo;
    }

    public CtlgItem getItem() {
        return item;
    }

    public void setItem(CtlgItem item) {
        this.item = item;
    }

    public CtlgItem getItemHijo() {
        return itemHijo;
    }

    public void setItemHijo(CtlgItem itemHijo) {
        this.itemHijo = itemHijo;
    }

    public List<CtlgItem> getItems() {
        return items;
    }

    public void setItems(List<CtlgItem> items) {
        this.items = items;
    }

}
