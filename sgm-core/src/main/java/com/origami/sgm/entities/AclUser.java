/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.OrdenTrabajo;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "acl_user", schema = SchemasConfig.APP1)
@NamedQueries({
    @NamedQuery(name = "AclUser.findAll", query = "SELECT a FROM AclUser a")
    ,
@NamedQuery(name = "AclUser.findAllByRol", query = "SELECT a FROM AclUser a INNER JOIN a.aclRolCollection r WHERE r.id = :idRol AND a.sisEnabled = TRUE ORDER BY a.usuario")})
public class AclUser implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "es_super_user", nullable = false)
    private Boolean esSuperUser = Boolean.FALSE;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuarioIngreso", fetch = FetchType.LAZY)
    private Collection<CatUbicacion> catUbicacionCollection;
    @OneToMany(mappedBy = "usuarioModificacion", fetch = FetchType.LAZY)
    private Collection<CatUbicacion> catUbicacionCollection1;
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Collection<CatCategoriasConstruccion> catCategoriasConstruccionCollection;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;
    @Column(name = "usuario")
    @Expose
    private String usuario;
    @Column(name = "pass")
    private String pass;
    @Basic(optional = false)
    @NotNull
    @Column(name = "sis_enabled", nullable = false)
    private Boolean sisEnabled;
    @Column(name = "user_is_director")
    private Boolean userIsDirector;
    @Size(max = 250)
    @Column(name = "ruta_imagen", length = 250)
    private String rutaImagen;
    @Size(max = 500)
    @Column(name = "imagen_perfil", length = 500)
    private String imagenPerfil;
    @JoinTable(name = "acl_user_has_rol", joinColumns = {
        @JoinColumn(name = "acl_user", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "acl_rol", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<AclRol> aclRolCollection;
    @JoinColumn(name = "ente", referencedColumnName = "id")
    @ManyToOne
    @Expose
    private CatEnte ente;
    @OneToMany(mappedBy = "responsable", fetch = FetchType.LAZY)
    private Collection<ParametrosDisparador> parametrosDisparadorCollection;
    @OneToMany(mappedBy = "userCreador", fetch = FetchType.LAZY)
    private Collection<CertificadoExoneracionLocalActivos> certificadoExoneracionLocal1;
    @OneToMany(mappedBy = "userModificacion", fetch = FetchType.LAZY)
    private Collection<CertificadoExoneracionLocalActivos> certificadoExoneracionLocal2;
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Collection<GeTareaUsuario> geTareaUsuarioCollection;

    @JoinColumn(name = "firma", referencedColumnName = "id")
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private PeFirma firma;

    @OneToMany(mappedBy = "usuarioCreador", fetch = FetchType.LAZY)
    private Collection<CatPredio> catPredioCollection;
    @OneToMany(mappedBy = "usuarioCreador", fetch = FetchType.LAZY)
    private Collection<PePermiso> pePermisoCollection;
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Collection<PeInspeccionFinal> peInspeccionFinalCollection;

    @OneToMany(mappedBy = "responsable", fetch = FetchType.LAZY)
    private Collection<OrdenTrabajo> responsableCollection;
    @OneToMany(mappedBy = "supervisor", fetch = FetchType.LAZY)
    private Collection<OrdenTrabajo> supervisorCollection;

    public AclUser() {
    }

    public AclUser(Long id) {
        this.id = id;
    }

    public AclUser(Long id, String usuario, Boolean sisEnabled) {
        this.id = id;
        this.usuario = usuario;
        this.sisEnabled = sisEnabled;
    }

    public String getRoles() {
        if (aclRolCollection == null) {
            return "";
        }
        String s = "";
        Integer i = 0;

        for (AclRol temp : aclRolCollection) {
            i++;
            if (i != aclRolCollection.size()) {
                s = s + temp.getNombre().toUpperCase() + ", ";
            } else {
                s = s + temp.getNombre().toUpperCase();
            }
        }
        return s;
    }

    public String getDepartamentos() {
        if (aclRolCollection == null) {
            return "";
        }
        String s = "";
        Integer i = 0;
        List<String> nombres = new ArrayList();

        for (AclRol temp : aclRolCollection) {
            if (!nombres.contains(temp.getDepartamento().getNombre())) {
                s = s + "Dep. " + temp.getDepartamento().getNombre() + " ";
                nombres.add(temp.getDepartamento().getNombre());
            }
        }
        return s;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Boolean getSisEnabled() {
        return sisEnabled;
    }

    public void setSisEnabled(Boolean sisEnabled) {
        this.sisEnabled = sisEnabled;
    }

    public Boolean getUserIsDirector() {
        return userIsDirector;
    }

    public void setUserIsDirector(Boolean userIsDirector) {
        this.userIsDirector = userIsDirector;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }

    public Collection<AclRol> getAclRolCollection() {
        return aclRolCollection;
    }

    public void setAclRolCollection(Collection<AclRol> aclRolCollection) {
        this.aclRolCollection = aclRolCollection;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<ParametrosDisparador> getParametrosDisparadorCollection() {
        return parametrosDisparadorCollection;
    }

    public void setParametrosDisparadorCollection(Collection<ParametrosDisparador> parametrosDisparadorCollection) {
        this.parametrosDisparadorCollection = parametrosDisparadorCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<GeTareaUsuario> getGeTareaUsuarioCollection() {
        return geTareaUsuarioCollection;
    }

    public void setGeTareaUsuarioCollection(Collection<GeTareaUsuario> geTareaUsuarioCollection) {
        this.geTareaUsuarioCollection = geTareaUsuarioCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AclUser)) {
            return false;
        }
        AclUser other = (AclUser) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "" + id + "";
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredio> getCatPredioCollection() {
        return catPredioCollection;
    }

    public void setCatPredioCollection(Collection<CatPredio> catPredioCollection) {
        this.catPredioCollection = catPredioCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PePermiso> getPePermisoCollection() {
        return pePermisoCollection;
    }

    public void setPePermisoCollection(Collection<PePermiso> pePermisoCollection) {
        this.pePermisoCollection = pePermisoCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeInspeccionFinal> getPeInspeccionFinalCollection() {
        return peInspeccionFinalCollection;
    }

    public void setPeInspeccionFinalCollection(Collection<PeInspeccionFinal> peInspeccionFinalCollection) {
        this.peInspeccionFinalCollection = peInspeccionFinalCollection;
    }

    public PeFirma getFirma() {
        return firma;
    }

    public void setFirma(PeFirma firma) {
        this.firma = firma;
    }

    public Boolean getEsSuperUser() {
        return esSuperUser;
    }

    public void setEsSuperUser(Boolean esSuperUser) {
        this.esSuperUser = esSuperUser;
    }

    public Collection<OrdenTrabajo> getResponsableCollection() {
        return responsableCollection;
    }

    public void setResponsableCollection(Collection<OrdenTrabajo> responsableCollection) {
        this.responsableCollection = responsableCollection;
    }

    public Collection<OrdenTrabajo> getSupervisorCollection() {
        return supervisorCollection;
    }

    public void setSupervisorCollection(Collection<OrdenTrabajo> supervisorCollection) {
        this.supervisorCollection = supervisorCollection;
    }

    public Collection<CertificadoExoneracionLocalActivos> getCertificadoExoneracionLocal1() {
        return certificadoExoneracionLocal1;
    }

    public void setCertificadoExoneracionLocal1(Collection<CertificadoExoneracionLocalActivos> certificadoExoneracionLocal1) {
        this.certificadoExoneracionLocal1 = certificadoExoneracionLocal1;
    }

    public Collection<CertificadoExoneracionLocalActivos> getCertificadoExoneracionLocal2() {
        return certificadoExoneracionLocal2;
    }

    public void setCertificadoExoneracionLocal2(Collection<CertificadoExoneracionLocalActivos> certificadoExoneracionLocal2) {
        this.certificadoExoneracionLocal2 = certificadoExoneracionLocal2;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatUbicacion> getCatUbicacionCollection() {
        return catUbicacionCollection;
    }

    public void setCatUbicacionCollection(Collection<CatUbicacion> catUbicacionCollection) {
        this.catUbicacionCollection = catUbicacionCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatUbicacion> getCatUbicacionCollection1() {
        return catUbicacionCollection1;
    }

    public void setCatUbicacionCollection1(Collection<CatUbicacion> catUbicacionCollection1) {
        this.catUbicacionCollection1 = catUbicacionCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatCategoriasConstruccion> getCatCategoriasConstruccionCollection() {
        return catCategoriasConstruccionCollection;
    }

    public void setCatCategoriasConstruccionCollection(Collection<CatCategoriasConstruccion> catCategoriasConstruccionCollection) {
        this.catCategoriasConstruccionCollection = catCategoriasConstruccionCollection;
    }
}
