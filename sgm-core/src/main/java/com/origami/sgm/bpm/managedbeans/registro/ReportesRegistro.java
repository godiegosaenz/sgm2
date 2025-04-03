/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.ReporteTramitesRp;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class ReportesRegistro implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;
    @Inject
    private ServletSession ss;
    @Inject
    private UserSession sess;

    protected String mensaje = "";
    protected Integer code = 1;
    protected Integer tipoReporte = 1;
    protected Integer tipoConsulta = 0;
    protected Date fecha = new Date();
    protected Date desde = new Date();
    protected Date hasta = new Date();
    protected Integer resultado;
    protected List<ReporteTramitesRp> list = new ArrayList<>();
    protected SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    protected List<AclUser> users;
    protected AclUser user;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        desde = Utils.sumarRestarDiasFecha(new Date(), -1);
        users = reg.getUsuariosByRolId(80L);
    }

    public void actualizaForm() {
        hasta = new Date();
        desde = Utils.sumarRestarDiasFecha(new Date(), -1);
        JsfUti.update("mainForm");
    }

    public void cargarTramitesRp() {
        try {
            if (fecha != null) {
                list = reg.tramitesAsignadosRegistro(sdf.format(fecha), tipoReporte);
                if (list.isEmpty()) {
                    JsfUti.messageInfo(null, "Lista de tramites vacia.", "");
                }
            } else {
                list = new ArrayList<>();
                JsfUti.messageError(null, "Error", "Debe ingresar la fecha de busqueda.");
            }
            JsfUti.update("mainForm:dtTramitesAsignados");
        } catch (Exception e) {
            Logger.getLogger(ReportesRegistro.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void reporteCertificadosRealizados() {
        try {
            if (desde.before(hasta) || desde.equals(hasta)) {
                ss.instanciarParametros();
                ss.setTieneDatasource(Boolean.TRUE);
                ss.setNombreSubCarpeta("registroPropiedad");
                ss.setNombreReporte("certificadosRealizados");
                ss.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/registroPropiedad/");
                ss.agregarParametro("USUARIO", sess.getName_user());
                ss.agregarParametro("DESDE", sdf.format(desde));
                ss.agregarParametro("FECHA_HASTA", sdf.format(hasta));
                ss.agregarParametro("HASTA", sdf.format(Utils.sumarRestarDiasFecha(hasta, 1)));
                JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");
            } else {
                JsfUti.messageError(null, "Error", "La fecha desde debe ser menor a fecha hasta.");
            }
        } catch (Exception e) {
            Logger.getLogger(ReportesRegistro.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void generarDocumento() {
        try {
            if (!list.isEmpty()) {
                ss.instanciarParametros();
                ss.setTieneDatasource(Boolean.FALSE);
                ss.setNombreSubCarpeta("registroPropiedad");
                ss.setNombreReporte("reporteTramitesAsignadosRp");
                ss.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                ss.agregarParametro("CANTIDAD", list.size());
                ss.agregarParametro("TITULO", this.tituloReporte());
                ss.agregarParametro("USUARIO", sess.getName_user());
                ss.agregarParametro("TIPO", tipoReporte);
                ss.setDataSource(list);
                JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");
            } else {
                JsfUti.messageError(null, "Error", "La lista de elemetos esta vacia.");
            }
        } catch (Exception e) {
            Logger.getLogger(ReportesRegistro.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public String tituloReporte() {
        String temp = " DEL " + sdf.format(fecha);
        switch (tipoReporte) {
            case 1:
                temp = "TRAMITES CERTIFICADOS" + temp;
                break;
            case 2:
                temp = "TRAMITES CONTRATOS" + temp;
                break;
            default:
                JsfUti.messageError(null, "Error", "Opcion no valida.");
                break;
        }
        return temp;
    }

    public void consultaCantidades() {
        try {
            if (tipoConsulta > 0) {
                switch (tipoConsulta) {
                    case 1:
                        resultado = reg.getCantidadCertificadosRealizados(sdf.format(desde), sdf.format(Utils.sumarRestarDiasFecha(hasta, 1)));
                        mensaje = "CANTIDAD DE CERTIFICADOS REALIZADOS:";
                        break;
                    case 2:
                        resultado = reg.getCantSolicitantes(1, sdf.format(desde), sdf.format(Utils.sumarRestarDiasFecha(hasta, 1)));
                        mensaje = "CANTIDAD DE SOLICITANTES DE CERTIFICADOS:";
                        break;
                    case 3:
                        resultado = reg.getCantSolicitantes(2, sdf.format(desde), sdf.format(Utils.sumarRestarDiasFecha(hasta, 1)));
                        mensaje = "CANTIDAD DE SOLICITANTES DE INSCRIPCIONES PROPIEDAD:";
                        break;
                    case 4:
                        resultado = reg.getCantSolicitantes(3, sdf.format(desde), sdf.format(Utils.sumarRestarDiasFecha(hasta, 1)));
                        mensaje = "CANTIDAD DE SOLICITANTES DE INSCRIPCIONES DE GRAVAMENES:";
                        break;
                    case 5:
                        resultado = reg.getCantSolicitantes(4, sdf.format(desde), sdf.format(Utils.sumarRestarDiasFecha(hasta, 1)));
                        mensaje = "CANTIDAD DE SOLICITANTES DE INSCRIPCIONES POR RESOLUCION:";
                        break;
                    default:
                        break;
                }
            } else {
                JsfUti.messageError(null, "Error", "Debe seleccionar el tipo de reporte.");
            }
        } catch (Exception e) {
            Logger.getLogger(ReportesRegistro.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void reporteTareasUsuario (){
        try {
            if(user == null){
                JsfUti.messageError(null, "Error", "Debe seleccionar el usuario.");
                return;
            }
            if (desde.before(hasta) || desde.equals(hasta)) {
                ss.instanciarParametros();
                ss.setTieneDatasource(Boolean.TRUE);
                ss.setNombreSubCarpeta("registroPropiedad");
                ss.setNombreReporte("tareasRealizadasPorUsuario");
                ss.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/registroPropiedad/");
                ss.agregarParametro("USER_NAME", sess.getName_user());
                ss.agregarParametro("USER_ID", user.getId());
                ss.agregarParametro("DESDE", sdf.format(desde));
                ss.agregarParametro("FECHA_HASTA", sdf.format(hasta));
                ss.agregarParametro("HASTA", sdf.format(Utils.sumarRestarDiasFecha(hasta, 1)));
                JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");
            } else {
                JsfUti.messageError(null, "Error", "La fecha desde debe ser menor a fecha hasta.");
            }
        } catch (Exception e) {
            Logger.getLogger(ReportesRegistro.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Integer getResultado() {
        return resultado;
    }

    public void setResultado(Integer resultado) {
        this.resultado = resultado;
    }

    public Integer getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(Integer tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public Integer getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(Integer tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public List<ReporteTramitesRp> getList() {
        return list;
    }

    public void setList(List<ReporteTramitesRp> list) {
        this.list = list;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public List<AclUser> getUsers() {
        return users;
    }

    public void setUsers(List<AclUser> users) {
        this.users = users;
    }

    public AclUser getUser() {
        return user;
    }

    public void setUser(AclUser user) {
        this.user = user;
    }

}
