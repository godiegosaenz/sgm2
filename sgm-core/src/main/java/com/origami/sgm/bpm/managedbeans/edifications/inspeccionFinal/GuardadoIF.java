/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.inspeccionFinal;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.entities.PeInspeccionFinal;
import com.origami.sgm.entities.PePermiso;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class GuardadoIF implements Serializable {
    
    public static final Long serialVersionUID = 1L;
    
    @Inject
    private ServletSession servletSession;
    
    @Inject
    private UserSession uSession;
    
    public void guardarDatosReporteCertificado(PeInspeccionFinal peInspeccionFinalV, List<CatPredioPropietario> lisPropietarios, CatEnte responsableTec,
            CatPredio datosPredio, PePermiso pp, PeFirma firma, HistoricoTramites ht, HistoricoReporteTramite hrt){
        try{
            servletSession.instanciarParametros();
            servletSession.agregarParametro("numReporte", peInspeccionFinalV.getNumReporte()+"-"+peInspeccionFinalV.getAnioInspeccion());
            /*
            if (solicitante.getEsPersona()) {
                servletSession.agregarParametro("nomPropietario", solicitante.getNombres() + " " + solicitante.getApellidos());
            } else {
                servletSession.agregarParametro("nomPropietario", solicitante.getNombreComercial());
            }*/
            
            servletSession.agregarParametro("numeroTramiteInspeccion", peInspeccionFinalV.getId()+"-"+peInspeccionFinalV.getAnioInspeccion());
            servletSession.agregarParametro("dia",new SimpleDateFormat("dd").format(peInspeccionFinalV.getFechaInspeccion()));
            servletSession.agregarParametro("mes",new SimpleDateFormat("MM").format(peInspeccionFinalV.getFechaInspeccion()));
            servletSession.agregarParametro("anio",new SimpleDateFormat("yyyy").format(peInspeccionFinalV.getFechaInspeccion()));
            if(ht!=null)
                servletSession.agregarParametro("numTramite",ht.getId()+"-"+peInspeccionFinalV.getAnioInspeccion());
            
            if(peInspeccionFinalV.getPropietario() != null){
                if(peInspeccionFinalV.getPropietario().getEsPersona()){
                    servletSession.agregarParametro("nomPropietario", peInspeccionFinalV.getPropietario().getNombres()+" "+peInspeccionFinalV.getPropietario().getApellidos());
                }else{
                    servletSession.agregarParametro("nomPropietario", peInspeccionFinalV.getPropietario().getRazonSocial());                        
                }
                servletSession.agregarParametro("ciRuc", peInspeccionFinalV.getPropietario().getCiRuc());
            }else{
                if(lisPropietarios!=null && !lisPropietarios.isEmpty()){
                    if(lisPropietarios.get(0).getEnte().getEsPersona()){
                        servletSession.agregarParametro("nomPropietario", lisPropietarios.get(0).getEnte().getNombres()+" "+lisPropietarios.get(0).getEnte().getApellidos());
                    }else{
                        servletSession.agregarParametro("nomPropietario", lisPropietarios.get(0).getEnte().getRazonSocial());                        
                    }
                    servletSession.agregarParametro("ciRuc", lisPropietarios.get(0).getEnte().getCiRuc());
                }
            }
            /*
            if(peInspeccionFinalV.getSolicitante() != null){
                if (peInspeccionFinalV.getSolicitante().getEsPersona()) {
                    servletSession.agregarParametro("nomPropietario", peInspeccionFinalV.getSolicitante().getNombres() + " " + peInspeccionFinalV.getSolicitante().getApellidos());
                } else {
                    servletSession.agregarParametro("nomPropietario", peInspeccionFinalV.getSolicitante().getRazonSocial());
                }
                servletSession.agregarParametro("ciRuc", peInspeccionFinalV.getSolicitante().getCiRuc());
            }  */          
            
            servletSession.agregarParametro("nombreTecnico", responsableTec.getNombreCompleto());
            servletSession.agregarParametro("ciTecnico", responsableTec.getCiRuc());
            servletSession.agregarParametro("canton","Samborondón");
            servletSession.agregarParametro("sector", "La Puntilla");
            servletSession.agregarParametro("calle", "Vehicular");
            servletSession.agregarParametro("manzana", datosPredio.getUrbMz());
            servletSession.agregarParametro("solar", datosPredio.getUrbSolarnew());
            servletSession.agregarParametro("codigoNuevo", datosPredio.getCodigoPredialCompleto());
            servletSession.agregarParametro("codigoAnterior", datosPredio.getPredialant());
            servletSession.agregarParametro("urbanizacion", datosPredio.getCiudadela().getNombre());
            servletSession.agregarParametro("observacion", "");
            servletSession.agregarParametro("validador", "");
            servletSession.agregarParametro("areaSolar", pp.getAreaSolar());
            servletSession.agregarParametro("areaConstruccion", pp.getAreaConstruccion());
            servletSession.agregarParametro("areaSolarIns", peInspeccionFinalV.getAreaSolar());
            servletSession.agregarParametro("areaConstIns", peInspeccionFinalV.getAreaConst());
            servletSession.agregarParametro("descPermiso", pp.getDescFamiliar());
            servletSession.agregarParametro("descInspeccion", peInspeccionFinalV.getDescEdificacion());
            servletSession.agregarParametro("retFron",pp.getRetiroFrontal());
            servletSession.agregarParametro("retl1", pp.getRetiroLateral1());
            servletSession.agregarParametro("retl2", pp.getRetiroLateral2());
            servletSession.agregarParametro("retPost", pp.getRetiroPosterior());
            servletSession.agregarParametro("retFronIns", peInspeccionFinalV.getRetiroFrontal());
            servletSession.agregarParametro("retl1In", peInspeccionFinalV.getRetiroLateral1());
            servletSession.agregarParametro("retl2Ins",peInspeccionFinalV.getRetiroLateral2());
            servletSession.agregarParametro("retPostIns",peInspeccionFinalV.getRetiroPosterior());
            servletSession.agregarParametro("nombreIng", firma.getNomCompleto());
            servletSession.agregarParametro("cargoIng", firma.getCargo().toUpperCase() + " " + firma.getDepartamento().toUpperCase());
            if(uSession.getNombrePersonaLogeada()!="")
                servletSession.agregarParametro("responsable", uSession.getNombrePersonaLogeada());
            else
                servletSession.agregarParametro("responsable", uSession.getName_user());
            servletSession.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
            servletSession.agregarParametro("inspeccionId", peInspeccionFinalV.getId());
            servletSession.agregarParametro("permisoId", pp.getId());
            servletSession.agregarParametro("regProfTec", responsableTec.getRegProf());
            servletSession.agregarParametro("registroPermiso", pp.getNumReporte()+"-"+pp.getAnioPermiso());
            servletSession.agregarParametro("firmaImg", JsfUti.getRealPath("//css//firmas//lilianaGuerrero.jpg"));
            servletSession.agregarParametro("SUBREPORT_DIR",JsfUti.getRealPath("//reportes//inspeccionFinal//"));
            if(hrt!=null)
                servletSession.agregarParametro("codigoQR",SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + hrt.getCodValidacion());
            servletSession.agregarParametro("SUBREPORT_DIR2",JsfUti.getRealPath("//reportes//permisoConstruccion//"));           
            servletSession.setNombreReporte("CertificadoInspeccionFinal");
            servletSession.setNombreSubCarpeta("inspeccionFinal");
            servletSession.setTieneDatasource(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void generarDatosReporteLiquidacion(PeInspeccionFinal peInspeccionFinalV, List<CatPredioPropietario> lisPropietarios, CatPredio datosPredio, CatEnte responsableTec, HistoricoTramites ht, PeFirma firma, HistoricoReporteTramite hrt){
        try{
            servletSession.instanciarParametros();
            
            servletSession.agregarParametro("numReporte", peInspeccionFinalV.getNumReporte()+"-"+peInspeccionFinalV.getAnioInspeccion());
            
            if(lisPropietarios!=null && !lisPropietarios.isEmpty()){
                if(lisPropietarios.get(0).getEnte().getEsPersona()){
                    servletSession.agregarParametro("nomPropietario", lisPropietarios.get(0).getEnte().getNombres()+" "+lisPropietarios.get(0).getEnte().getApellidos());
                }else{
                    servletSession.agregarParametro("nomPropietario", lisPropietarios.get(0).getEnte().getRazonSocial());                        
                }
                servletSession.agregarParametro("ciRuc", lisPropietarios.get(0).getEnte().getCiRuc());
            }
            /*if(peInspeccionFinalV.getSolicitante() != null){
                if (peInspeccionFinalV.getSolicitante().getEsPersona()) {
                    servletSession.agregarParametro("nomPropietario", peInspeccionFinalV.getSolicitante().getNombres() + " " + peInspeccionFinalV.getSolicitante().getApellidos());
                } else {
                    servletSession.agregarParametro("nomPropietario", peInspeccionFinalV.getSolicitante().getRazonSocial());
                }
                servletSession.agregarParametro("ciRuc", peInspeccionFinalV.getSolicitante().getCiRuc());
            }  */
            servletSession.agregarParametro("canton", "Samborondón");
            servletSession.agregarParametro("observacion", peInspeccionFinalV.getDescEdificacion());
            servletSession.agregarParametro("sector", "La Puntilla");
            servletSession.agregarParametro("inspeccion", peInspeccionFinalV.getInspeccion());
            servletSession.agregarParametro("mz",datosPredio.getUrbMz());
            servletSession.agregarParametro("solar",datosPredio.getUrbSolarnew());
            servletSession.agregarParametro("nombreResponsable", responsableTec.getNombreCompleto());
            servletSession.agregarParametro("regProf", responsableTec.getRegProf());
            servletSession.agregarParametro("ciResp", responsableTec.getCiRuc());
            servletSession.agregarParametro("calle", "Vehicular");
            servletSession.agregarParametro("urbanizacion", datosPredio.getCiudadela().getNombre());
            servletSession.agregarParametro("permisoConst", peInspeccionFinalV.getNumPermisoConstruc());
            servletSession.agregarParametro("codigoNuevo", datosPredio.getCodigoPredial());
            servletSession.agregarParametro("areaEdif", peInspeccionFinalV.getAreaConst());
            servletSession.agregarParametro("areaSolar", peInspeccionFinalV.getAreaSolar());
            servletSession.agregarParametro("codigoAnterior", datosPredio.getPredialant());
            servletSession.agregarParametro("imsadc",peInspeccionFinalV.getImpuesto());
            servletSession.agregarParametro("revYAprobPlanos",peInspeccionFinalV.getRevicion());
            servletSession.agregarParametro("noAdeudarMunicipio",peInspeccionFinalV.getNoAdeudar());
            servletSession.agregarParametro("verificacionAreaEdificada", 0);
            servletSession.agregarParametro("totalAPagar", ht.getValorLiquidacion());
            if(hrt!=null)
                servletSession.agregarParametro("codigoQR", SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + hrt.getCodValidacion());
            servletSession.agregarParametro("nombreIng",firma.getNomCompleto());
            servletSession.agregarParametro("cargoIng", firma.getCargo());
            servletSession.agregarParametro("responsable", uSession.getNombrePersonaLogeada());
            servletSession.agregarParametro("avaluoConstruccion", peInspeccionFinalV.getEvaluoLiquidacion());
            servletSession.agregarParametro("dia", new SimpleDateFormat("dd").format(peInspeccionFinalV.getFechaInspeccion()));
            servletSession.agregarParametro("mes", new SimpleDateFormat("MM").format(peInspeccionFinalV.getFechaInspeccion()));
            servletSession.agregarParametro("anio", new SimpleDateFormat("yyyy").format(peInspeccionFinalV.getFechaInspeccion()));
            servletSession.agregarParametro("numTramiteSeq", Long.parseLong(peInspeccionFinalV.getNumReporte().toString())+"-"+peInspeccionFinalV.getAnioInspeccion());
            servletSession.agregarParametro("numTramite", ht.getId()+"-"+peInspeccionFinalV.getAnioInspeccion());
            servletSession.agregarParametro("logoImg", JsfUti.getRealPath(SisVars.logoReportes));
            servletSession.agregarParametro("firmaImg", JsfUti.getRealPath("//css//firmas//lilianaGuerrero.jpg"));
            servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("//reportes//inspeccionFinal//"));          
            servletSession.agregarParametro("inspeccionId", peInspeccionFinalV.getId());
            servletSession.agregarParametro("seleccionadoImg", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
            servletSession.setNombreReporte("LiquidacionInspeccionFinal");
            servletSession.setNombreSubCarpeta("inspeccionFinal");
            servletSession.setTieneDatasource(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }
    
}
