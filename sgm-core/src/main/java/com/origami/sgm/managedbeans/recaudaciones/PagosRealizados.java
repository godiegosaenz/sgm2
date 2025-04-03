/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.recaudaciones;

import com.origami.config.RolesEspeciales;
import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.catastro.FichaPredial;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeDocumentos;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenPago;
import com.origami.sgm.lazymodels.RenPagoLazy;
import com.origami.sgm.services.ejbs.censocat.OmegaUploader;
import com.origami.sgm.services.ejbs.censocat.UploadDocumento;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import util.Faces;
import util.JsfUti;

/**
 *
 * @author origami
 */
@Named(value = "pagosRealizados")
@ViewScoped
public class PagosRealizados implements Serializable {

    @javax.inject.Inject
    private RecaudacionesService recaudacion;
    protected RenPagoLazy pagos;
    protected RenPago pago;
    protected AclUser usuario;
    @javax.inject.Inject
    private Entitymanager manager;
    @Inject
    private ServletSession ss;
    @Inject
    private UserSession session;

    private Map<String, Object> parametros;

    private Boolean renderAnular;
    private String observacion;

    @Inject
    private UploadDocumento documentoBean;
    @Inject
    private OmegaUploader fserv;
    private Long idDoc = null;

    @PostConstruct
    public void initView() {
        try {
            parametros = new HashMap<>();
            parametros.put("usuario", session.getName_user());
            usuario = (AclUser) manager.findObjectByParameter(AclUser.class, parametros);
            if (usuario != null) {
                AclRol rol = new AclRol();
                rol.setId(RolesEspeciales.ADMINISTRADOR);
                AclRol supCaj = new AclRol();
                supCaj.setId(RolesEspeciales.SUPERVISOR_CAJA);
                if (usuario.getAclRolCollection().contains(rol) || usuario.getAclRolCollection().contains(supCaj)) {//JEFA DE CAJA
                    pagos = new RenPagoLazy();
                } else {
                    pagos = new RenPagoLazy(usuario);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(PagosRealizados.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public void verPago(RenPago p) {
        this.pago = p;
    }

    public void reversarPago() {
        //SOLO SE PUEDE REVERSAR EL ULTIMO PAGO
        try {
            if (this.pago != null && this.pago.getEstado()) {
                if (this.pago.equals(recaudacion.ultimoPago(this.pago.getLiquidacion())) && recaudacion.ultimaEspecie(this.pago.getLiquidacion())) {
                    this.pago.setObservacion(this.pago.getObservacion() + " El pago fue anulado: " + observacion);
                    this.pago = recaudacion.reversarPago(this.pago);
                    if (this.pago != null) {
                        RenLiquidacion liquidacion = manager.find(RenLiquidacion.class, this.pago.getLiquidacion().getId());
                        Boolean sinAbonos = Boolean.TRUE;
                        for (RenPago p : liquidacion.getRenPagoCollection()) {
                            if (p.getEstado()) {
                                sinAbonos = Boolean.FALSE;
                                break;
                            }
                        }
                        //INACTIVACION DE RENLIQUIDACION POR TIPO
                        Long tipoLiquidacion = liquidacion.getId();
                        if (sinAbonos && tipoLiquidacion != 7L && tipoLiquidacion != 13L) {
                            System.out.println("::: INACTIVAR");
                        }
                        JsfUti.messageInfo(null, "Mensaje", "PAGO ANULADO EXITOSAMENTE");
                    } else {
                        JsfUti.messageError(null, "PROBLEMAS AL ANULAR EL PAGO", "VUELVA A REALIZAR LA ACCION");
                    }
                } else {
                    JsfUti.messageInfo(null, "Mensaje", "SOLO SE PUEDE ANULAR EL ULTIMO PAGO PROCESADO");
                }
            } else {
                JsfUti.messageInfo(null, "Mensaje", "EL PAGO YA SE ENCUNETRA ANULADO");
            }
        } catch (Exception e) {
            Logger.getLogger(PagosRealizados.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void imprimirComprobante(RenPago p) {
        this.pago = p;
        this.generarComprobante(true);
    }

    /*METODO MODIFICADO PARA QUE SE PUEDA VIZUALIZAR EL PARAMETRO DEL LOGO EN EL REPORTE EL PARAMETRO DE COPIA CETIFICADA*/
    public void generarComprobante(Boolean copia) {
        List<RenPago> pagoss = new ArrayList<>();
        try {
            if (this.pago != null && this.pago.getEstado()) {
                String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
                ss.borrarParametros();
                ss.instanciarParametros();
                ss.agregarParametro("COPIA", copia);
                ss.agregarParametro("LOGO", Faces.getRealPath("/").concat(SisVars.sisLogo));
                
                ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes").concat("/Emision/"));
                ss.agregarParametro("SUBREPORT_DIR_COACTIVA", Faces.getRealPath("/reportes/Emision"));
                ss.setTieneDatasource(Boolean.TRUE);

                //ESTA NOTA ESTA ASI QUEMADA PORQUE JUAN CARLOS SUBIO ASI EL REPORTE -_- JUAN CARLOS SI LEES ESTO .I. JAAJJA :V
                /// U SEA EL REPORTE FUNCA Y TODO BELEN PERO ES PARA UN FORMATO DE LA BASE DE DATOS DEL AME 
                // Y ES CHEVERE :D LO DEJO AQUI PORQUE EN  MONTECRISTI - JAMA -SANVICENTE - SAN MIGUEL USAN EL AME Y HAN DE HABER VISTO REPORTES ASI 
                if (this.pago.getLiquidacion().getTipoLiquidacion().getId() == 13L && this.pago.getLiquidacion().getPredio() != null) {
                    ss.setNombreReporte("emisionPredioUrbanoJama");
                }
                if (this.pago.getLiquidacion().getTipoLiquidacion().getId() == 7L) {
                    ss.setNombreReporte("emisionPredioRuralFormatoAme");
                }

                if (this.pago.getLiquidacion().getTipoLiquidacion().getNombreReporte() == null
                        || this.pago.getLiquidacion().getTipoLiquidacion().getNombreReporte().trim().length() == 0) {
                    ss.setNombreReporte("sCobrosGenerales");
                } else {
                    ss.setNombreReporte(this.pago.getLiquidacion().getTipoLiquidacion().getNombreReporte());
                    // ss.setNombreReporte("sCobrosGeneralesSanVicente");
                }
                if (this.pago.getLiquidacion().getTipoLiquidacion().getId() == 13L && this.pago.getLiquidacion().getPredio() == null
                        && this.pago.getLiquidacion().getClaveAME() != null) {
                    ss.setNombreReporte("emisionPredioUrbanoSanVicenteSinPredioAME");
                }
                pagoss.add(this.pago);
                ss.setTieneDatasource(Boolean.TRUE);
                ss.agregarParametro("liquidaciones", pagoss);
                Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "Comprobantes");
            } else {
                JsfUti.messageInfo(null, "Mensaje", "SOLO SE PUEDE REIMPRIMIR PAGOS PROCESADOS");
            }
        } catch (Exception e) {
            Logger.getLogger(PagosRealizados.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void handleFileDocumentBySave(FileUploadEvent event) {
        try {
            Date d = new Date();
            File file = new File(SisVars.rutaRepotiorioArchivo + d.getTime() + event.getFile().getFileName());

            InputStream is;
            is = event.getFile().getInputstream();
            OutputStream out = new FileOutputStream(file);
            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            Long documentoId = fserv.uploadFile(event.getFile().getInputstream(), event.getFile().getFileName(), event.getFile().getContentType());
            documentoBean.setFechaCreacion(new Date());
            documentoBean.setNombre(event.getFile().getFileName());
            documentoBean.setRaiz(pago.getId());
            documentoBean.setContentType(event.getFile().getContentType());
            documentoBean.setDocumentoId(documentoId);
            documentoBean.setIdentificacion("Anulacion Pago");
            GeDocumentos saveDocumento = documentoBean.saveDocumento();
            if (saveDocumento != null) {
                this.idDoc = saveDocumento.getId();
            }
            is.close();
            out.close();
            Faces.messageInfo(null, "Nota1", "Archivo cargado Satisfactoriamente");
        } catch (IOException e) {
            Logger.getLogger(FichaPredial.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public RenPagoLazy getPagos() {
        return pagos;
    }

    public void setPagos(RenPagoLazy pagos) {
        this.pagos = pagos;
    }

    public RenPago getPago() {
        return pago;
    }

    public void setPago(RenPago pago) {
        this.pago = pago;
    }

    public Boolean getRenderAnular() {
        session.getRoles();
        for (Long rol : session.getRoles()) {
            if (rol == RolesEspeciales.SUPERVISOR_CAJA) {
                renderAnular = true;
            } else if (rol == RolesEspeciales.ADMINISTRADOR) {
                renderAnular = true;
            }
        }
        return renderAnular;
    }

    public void setRenderAnular(Boolean renderAnular) {
        this.renderAnular = renderAnular;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

}
