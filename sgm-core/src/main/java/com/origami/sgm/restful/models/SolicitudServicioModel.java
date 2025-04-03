/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.restful.models;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.VuCatalogo;
import com.origami.sgm.entities.VuItems;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.util.EjbsCaller;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Joao Sanga
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SolicitudServicioModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<TipoSolicitud> tiposSolicitud;
    private List<Parroquia> parroquias;

    public void llenarListas(List<VuCatalogo> catalogoList, List<CatParroquia> parroquias) {
        this.tiposSolicitud = new ArrayList();
        this.parroquias = new ArrayList();
        if (catalogoList != null) {
            for (VuCatalogo temp : catalogoList) {
                TipoSolicitud t = new TipoSolicitud();
                t.setId(temp.getId());
                t.setDescripcion(temp.getNombre());
                t.setCatalogo(temp.getId());
                t.llenarItems();
                this.tiposSolicitud.add(t);
            }
        }

        for (CatParroquia temp : parroquias) {
            Parroquia t = new Parroquia();
            t.setCodigoParroquia(temp.getCodigoParroquia() == null ? null : temp.getCodigoParroquia().longValue());
            t.setId(temp.getId());
            t.setDescripcion(temp.getDescripcion());
            t.llenarCiudadelas();
            this.parroquias.add(t);
        }

    }

    public List<TipoSolicitud> getTiposSolicitud() {
        return tiposSolicitud;
    }

    public void setTiposSolicitud(List<TipoSolicitud> tiposSolicitud) {
        this.tiposSolicitud = tiposSolicitud;
    }

    public List<Parroquia> getParroquias() {
        return parroquias;
    }

    public void setParroquias(List<Parroquia> parroquias) {
        this.parroquias = parroquias;
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public class TipoSolicitud implements Serializable {

        private static final long serialVersionUID = 1L;

        private Long id;
        private String descripcion;
        private Long catalogo;
        private List<Item> items;

        public void llenarItems() {
            this.items = new ArrayList();
            List<VuItems> itemsVU;
            VuCatalogo c;

            if (catalogo == null) {
                return;
            } else {
                c = (VuCatalogo) EjbsCaller.getTransactionManager().find(VuCatalogo.class, catalogo);
            }

            itemsVU = EjbsCaller.getTransactionManager().findAll(Querys.getVuItemsByCatalogo, new String[]{"catalogo"}, new Object[]{c});

            if (itemsVU == null) {
                return;
            }

            for (VuItems temp : itemsVU) {
                Item t = new Item();
                t.setId(temp.getId());
                t.setNombre(temp.getNombre());
                t.setCatalogo(temp.getCatalogo() == null ? null : temp.getCatalogo().getId());
                t.setClasificacion(temp.getClasificacion());
                this.items.add(t);
            }
        }

        public Long getCatalogo() {
            return catalogo;
        }

        public void setCatalogo(Long catalogo) {
            this.catalogo = catalogo;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    class Parroquia implements Serializable {

        private static final long serialVersionUID = 1L;
        private Long id;
        private String descripcion;
        private Long codigoParroquia;
        private List<Ciudadela> ciudadelas;

        public void llenarCiudadelas() {
            this.ciudadelas = new ArrayList();
            List<CatCiudadela> ciudadelas;

            if (codigoParroquia == null) {
                return;
            }

            ciudadelas = EjbsCaller.getTransactionManager().findAll(Querys.getCatCiudadelaByCodigoParroquia, new String[]{"codigo"}, new Object[]{codigoParroquia});

            for (CatCiudadela temp : ciudadelas) {
                Ciudadela t = new Ciudadela();
                t.setId(temp.getId());
                t.setDescripcion(temp.getNombre());
                t.setCodigo(temp.getCodigo() == null ? null : Long.parseLong(temp.getCodigo() + ""));
                t.setCodigoParroquia(temp.getCodParroquia() == null ? null : temp.getCodParroquia().getId());
                this.ciudadelas.add(t);
            }
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public Long getCodigoParroquia() {
            return codigoParroquia;
        }

        public void setCodigoParroquia(Long codigoParroquia) {
            this.codigoParroquia = codigoParroquia;
        }

        public List<Ciudadela> getCiudadelas() {
            return ciudadelas;
        }

        public void setCiudadelas(List<Ciudadela> ciudadelas) {
            this.ciudadelas = ciudadelas;
        }

    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    private class Ciudadela implements Serializable {

        private static final long serialVersionUID = 1L;
        private Long id;
        private String descripcion;
        private Long codigoParroquia;
        private Long codigo;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public Long getCodigoParroquia() {
            return codigoParroquia;
        }

        public void setCodigoParroquia(Long codigoParroquia) {
            this.codigoParroquia = codigoParroquia;
        }

        public Long getCodigo() {
            return codigo;
        }

        public void setCodigo(Long codigo) {
            this.codigo = codigo;
        }

    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    private class Item implements Serializable {

        private static final long serialVersionUID = 1L;
        private Long id;
        private Long catalogo;
        private String nombre;
        private String clasificacion;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getCatalogo() {
            return catalogo;
        }

        public void setCatalogo(Long catalogo) {
            this.catalogo = catalogo;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getClasificacion() {
            return clasificacion;
        }

        public void setClasificacion(String clasificacion) {
            this.clasificacion = clasificacion;
        }

    }
}
