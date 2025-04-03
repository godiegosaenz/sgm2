/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.mail.ent;

import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Fernando
 */
@Entity
@Table(name = "om_mail", schema = SchemasConfig.APP1)
public class OmMail implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    protected Long id;
    
    @Column(name = "to_")
    protected String to;
    
    @Column(name = "subject")
    protected String subject;
    
    @Column(name = "content")
    protected String content;
    
    @Column(name = "bcc")
    protected String bcc;
    
    @Column(name = "cc")
    protected String cc;
    
    @Column(name = "inst_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date instCreacion = new Date();
    
    @Basic(optional = false)
    @Column(name = "oportunidades")
    protected Integer oportunidades = 3;
    
    @Basic(optional = false)
    @Column(name = "enviado")
    protected Boolean enviado = false;
    
    @Column(name = "inst_enviado")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date instEnviado;

    public OmMail() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public Date getInstCreacion() {
        return instCreacion;
    }

    public void setInstCreacion(Date instCreacion) {
        this.instCreacion = instCreacion;
    }

    public Integer getOportunidades() {
        return oportunidades;
    }

    public void setOportunidades(Integer oportunidades) {
        this.oportunidades = oportunidades;
    }

    public Boolean getEnviado() {
        return enviado;
    }

    public void setEnviado(Boolean enviado) {
        this.enviado = enviado;
    }

    public Date getInstEnviado() {
        return instEnviado;
    }

    public void setInstEnviado(Date instEnviado) {
        this.instEnviado = instEnviado;
    }

    
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OmMail other = (OmMail) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    
    
}
