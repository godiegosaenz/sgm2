/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.RegCertificado;
import com.origami.sgm.entities.RegRegistrador;
import com.origami.sgm.entities.RegTipoCertificado;
import com.origami.sgm.entities.RegpCertificadosInscripciones;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class CertificadoNuevoRp extends BpmManageBeanBaseRoot implements Serializable {

    @Inject
    private ServletSession servletSession;

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    protected RegCertificado certificado;
    protected RegTipoCertificado tipo;
    protected HistoricoTramites ht;
    protected RegpCertificadosInscripciones cert;
    protected Long idTarea;
    protected List<RegTipoCertificado> listCertf;
    protected Date fecha = new Date();
    protected Calendar cal = Calendar.getInstance();
    private RegRegistrador registrador = new RegRegistrador();

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            if (idTarea != null) {
                if (session.getTaskID() != null) {
                    this.setTaskId(session.getTaskID());
                    Long id = (Long) this.getVariable(this.getTaskId(), "tramite");
                    ht = reg.getHistoricoTramiteById(id);
                }
                cert = (RegpCertificadosInscripciones) acl.find(RegpCertificadosInscripciones.class, idTarea);
                listCertf = reg.getListTipoCertificado();
                certificado = reg.getCertificadoByIdTarea(idTarea);
                if (certificado == null) {
                    certificado = new RegCertificado();
                    registrador = (RegRegistrador) acl.find(Querys.getRegRegistrador);
                } else {
                    tipo = certificado.getTipoCertificado();
                    fecha = certificado.getFechaEmision();
                }
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            Logger.getLogger(CertificadoNuevoRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void cargarTexto() {
        if (tipo != null) {
            certificado.setObservacion(tipo.getPlantilla());
            JsfUti.update("mainForm");
        }
    }

    public void saveCertificado() {
        try {
            if (tipo != null) {
                if (certificado.getBeneficiario() != null) {
                    if (certificado.getObservacion() != null) {
                        certificado.setTipoCertificado(tipo);
                        if (certificado.getId() == null) {
                            if (registrador != null) {
                                certificado.setRegistrador(registrador);
                            }
                            certificado.setFechaEmision(fecha);
                            if (this.getTaskId() != null) {
                                certificado.setTaskId(this.getTaskId());
                                certificado.setNumCertificado(cert.getLiquidacion().getNumTramiteRp()); // numero de tramite del registro
                                certificado.setNumTramite(BigInteger.valueOf(ht.getId())); // numero de seguimiento del tramite
                                certificado.setSecuencia(reg.getMaxNumeroIndiceCertificado(certificado.getNumTramite()));
                            } else {
                                certificado.setSecuencia(reg.getMaxNumeroIndiceCertificadoSine(cal.get(Calendar.YEAR)));
                            }
                            certificado.setRegpCertificadoInscripciones(cert);
                            certificado.setUserCreador(new AclUser(session.getUserId()));
                            certificado = (RegCertificado) acl.persist(certificado);
                        } else {
                            acl.persist(certificado);
                        }
                        JsfUti.messageInfo(null, "Guardado Exitoso.", "");
                    } else {
                        JsfUti.messageError(null, "Falta ingresar el contenido del Certificado.", "");
                    }
                } else {
                    JsfUti.messageError(null, "Falta ingresar el nombre del beneficiario.", "");
                }
            } else {
                JsfUti.messageError(null, "Debe seleccionar el tipo de certificado.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(CertificadoNuevoRp.class.getName()).log(Level.SEVERE, null, e);
            JsfUti.messageError(null, Messages.error, "");
        }
    }

    public void generarReporte() {
        try {
            if (certificado.getId() != null) {
                servletSession.instanciarParametros();
                servletSession.setTieneDatasource(true);
                servletSession.setEncuadernacion(Boolean.TRUE);
                //servletSession.setNombreReporte("CertificadoGeneral");
                servletSession.setNombreReporte("CertificadoWord");
                servletSession.setNombreSubCarpeta("registroPropiedad");
                servletSession.agregarParametro("ID_CERTIFICADO", certificado.getId());
                servletSession.agregarParametro("USER", certificado.getUserCreador().getUsuario());
                servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "reportes/registroPropiedad/");
                servletSession.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                servletSession.agregarParametro("ESCUDO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                servletSession.agregarParametro("IMG_FOOTER", JsfUti.getRealPath(SisVars.sisLogo1));
                JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");
                certificado.setCertificadoImpreso(Boolean.TRUE);
                acl.persist(certificado);
            } else {
                JsfUti.messageError(null, "Primero debe guardar el certificado.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(CertificadoNuevoRp.class.getName()).log(Level.SEVERE, null, e);
            JsfUti.messageError(null, Messages.error, "");
        }
    }

    public void actualizarTarea() {
        try {
            if (certificado.getId() != null) {
                cert.setRealizado(true);
                cert.setFechaFin(new Date());
                cert.setAclUser(session.getUserId());
                if (this.getTaskId() != null) {
                    cert.setObservacion("Certificado Word: " + certificado.getNumCertificado() + "-" + certificado.getSecuencia());
                } else {
                    cert.setObservacion("Certificado Word: Dinardap-" + cal.get(Calendar.YEAR) + "-" + certificado.getSecuencia());
                }
                acl.persist(cert);
                this.continuar();
            } else {
                JsfUti.messageError(null, "Primero debe guardar el certificado.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(CertificadoNuevoRp.class.getName()).log(Level.SEVERE, null, e);
            JsfUti.messageError(null, Messages.error, "");
        }
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public RegpCertificadosInscripciones getCert() {
        return cert;
    }

    public void setCert(RegpCertificadosInscripciones cert) {
        this.cert = cert;
    }

    public Long getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Long idTarea) {
        this.idTarea = idTarea;
    }

    public RegCertificado getCertificado() {
        return certificado;
    }

    public void setCertificado(RegCertificado certificado) {
        this.certificado = certificado;
    }

    public RegTipoCertificado getTipo() {
        return tipo;
    }

    public void setTipo(RegTipoCertificado tipo) {
        this.tipo = tipo;
    }

    public List<RegTipoCertificado> getListCertf() {
        return listCertf;
    }

    public void setListCertf(List<RegTipoCertificado> listCertf) {
        this.listCertf = listCertf;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

}
