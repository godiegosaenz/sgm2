/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.coactiva;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.Faces;
import util.JsfUti;

/**
 *
 * @author origami
 */
@Named(value = "prediosACoactivar")
@ViewScoped
public class PrediosACoactivar implements Serializable{
    public static final Long serialVersionUID = 1L;    
    @javax.inject.Inject
    private Entitymanager manager;
    
    @Inject
    private ServletSession ss;
    
    protected CatPredioModel predioModel = new CatPredioModel();
    protected List<CatCiudadela> ciudadelas;
    protected CatEnte contribuyenteConsulta;
    protected List<CatPredio> prediosUrbanos;
    protected CatPredio predioUrbanoConsulta;
    protected List<CatPredio> prediosUrbanosConsultaSeleccionados;
    protected List<CatPredio> prediosUrbanosConsulta;
    private Map<String, Object> parametros;
    private CatEnteLazy contribuyentes;
    private String path ;
    
    
    @PostConstruct
    public void initView() {
        try{
//            ciudadelas = manager.findAllObjectOrder(CatCiudadela.class, new String[]{"nombre"}, Boolean.TRUE); 
            ciudadelas = manager.findAll(Querys.getCatCiudadelasByCanton);
            prediosUrbanos= new ArrayList<>();
            contribuyentes = new CatEnteLazy();
            path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        }catch (Exception e) {
            Logger.getLogger(PrediosACoactivar.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void consultarPredioUrbano(){
        predioUrbanoConsulta=null;
        prediosUrbanosConsultaSeleccionados=null;
        prediosUrbanosConsulta = new ArrayList<>();
        parametros = new HashMap<>();
        try {
            switch (predioModel.getTipoConsultaUrbano().intValue()) {
                case 1://NUMERO PREDIAL
                    if (predioModel.getNumPredio() != null && predioModel.getNumPredio().compareTo(BigInteger.ZERO) > 0) {
                        parametros.put("numPredio", predioModel.getNumPredio());
                        predioUrbanoConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, parametros);
                    }
                    break;
                case 2://CONTRIBUYENTE
                    if (contribuyenteConsulta != null) {
                        if (contribuyenteConsulta.getCatPredioPropietarioCollection() != null && !contribuyenteConsulta.getCatPredioPropietarioCollection().isEmpty()) {
                            if (contribuyenteConsulta.getCatPredioPropietarioCollection().size() == 1) {
                                parametros.put("numPredio", contribuyenteConsulta.getCatPredioPropietarioCollection().get(0).getPredio().getNumPredio());
                                predioUrbanoConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, parametros);
                            } else {
                                for (CatPredioPropietario pp : contribuyenteConsulta.getCatPredioPropietarioCollection()) {
                                    prediosUrbanosConsulta.add(pp.getPredio());
                                }
                            }
                        } else {
                            JsfUti.messageInfo(null, "Contribuyente no posee Predios", "");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Realice la busqueda del Contribuyente.");
                    }
                    break;
                case 3://CODIGO PREDIAL
                    if (predioModel.getSector() > 0 || predioModel.getMz() > 0 || predioModel.getCdla() > 0 || predioModel.getMzDiv() > 0 || predioModel.getSolar() > 0 || predioModel.getDiv1() > 0 || predioModel.getDiv2() > 0 || predioModel.getDiv3() > 0 || predioModel.getDiv4() > 0 || predioModel.getDiv5() > 0 || predioModel.getDiv6() > 0 || predioModel.getDiv7() > 0 || predioModel.getDiv8() > 0 || predioModel.getDiv9() > 0 || predioModel.getPhv() > 0 || predioModel.getPhh() > 0) {
                        parametros.put("estado", "A");
                        if(predioModel.getSector()>0)
                            parametros.put("sector", predioModel.getSector());
                        if(predioModel.getMz()>0)
                            parametros.put("mz", predioModel.getMz());
                        if(predioModel.getCdla()>0)
                            parametros.put("cdla", predioModel.getCdla());
                        if(predioModel.getMzDiv()>0)
                            parametros.put("mzdiv", predioModel.getMzDiv());
                        if(predioModel.getSolar()>0)
                            parametros.put("solar", predioModel.getSolar());
                        if(predioModel.getDiv1()>0)
                            parametros.put("div1", predioModel.getDiv1());
                        if(predioModel.getDiv2()>0)
                            parametros.put("div2", predioModel.getDiv2());
                        if(predioModel.getDiv3()>0)
                            parametros.put("div3", predioModel.getDiv3());
                        if(predioModel.getDiv4()>0)
                            parametros.put("div4", predioModel.getDiv4());
                        if(predioModel.getDiv5()>0)
                            parametros.put("div5", predioModel.getDiv5());
                        if(predioModel.getDiv6()>0)
                            parametros.put("div6", predioModel.getDiv6());
                        if(predioModel.getDiv7()>0)
                            parametros.put("div7", predioModel.getDiv7());
                        if(predioModel.getDiv8()>0)
                            parametros.put("div8", predioModel.getDiv8());
                        if(predioModel.getDiv9()>0)
                            parametros.put("div9", predioModel.getDiv9());
                        if(predioModel.getPhv()>0)
                            parametros.put("phv", predioModel.getPhv());
                        if(predioModel.getPhh()>0)
                            parametros.put("phh", predioModel.getPhh());
                        prediosUrbanosConsulta = manager.findObjectByParameterList(CatPredio.class, parametros);
                    } else {
                        JsfUti.messageError(null, "Error", "Codigo Predial no es valido.");
                    }
                    break;
                case 4://UBICACION
                    if(predioModel.getCiudadela()!=null || predioModel.getMzUrb()!=null || predioModel.getSlUrb()!=null){
                        if(predioModel.getCiudadela()!=null)
                            parametros.put("ciudadela", predioModel.getCiudadela());
                        if(predioModel.getMzUrb()!=null)
                            parametros.put("urbMz", predioModel.getMzUrb());
                        if(predioModel.getSlUrb()!=null)
                            parametros.put("urbSolarnew", predioModel.getSlUrb());
                        prediosUrbanosConsulta = manager.findObjectByParameterList(CatPredio.class, parametros);
                    } else {
                        JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");
                    }
                    break;
                case 5:
                    if(predioModel.getCodAnt1()!=null && predioModel.getCodAnt2()!=null && predioModel.getCodAnt3()!=null && predioModel.getCodAnt5()!=null && predioModel.getCodAnt6()!=null && predioModel.getCodAnt7()!=null && predioModel.getCodAnt8()!=null){
                        parametros = new HashMap<>();
                        parametros.put("estado", "A");
                        parametros.put("predialant", predioModel.getCodAnt1()+"-"+predioModel.getCodAnt2()+"-"+predioModel.getCodAnt3()+"-"+predioModel.getCodAnt4()+"-"+predioModel.getCodAnt5()+"-"+predioModel.getCodAnt6()+"-"+predioModel.getCodAnt7()+"-"+predioModel.getCodAnt8());
                        prediosUrbanosConsulta = manager.findObjectByParameterList(CatPredio.class, parametros);                        
                    } else {
                        JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");
                    }
                    break;
                default:
                    break;
            }
            if (prediosUrbanosConsulta != null && !prediosUrbanosConsulta.isEmpty() && prediosUrbanosConsulta.size() == 1) {
                parametros = new HashMap<>();
                parametros.put("numPredio", prediosUrbanosConsulta.get(0).getNumPredio());
                predioUrbanoConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, parametros);
            }
            if(predioUrbanoConsulta!=null){
                if(!this.prediosUrbanos.contains(predioUrbanoConsulta)){
                    this.prediosUrbanos.add(predioUrbanoConsulta);
                }else{
                    JsfUti.messageInfo(null, "Mensaje", "Predio ya se encuentra agregado.");
                }
            }else{
                if (prediosUrbanosConsulta != null && prediosUrbanosConsulta.size() > 1) {
                    JsfUti.update("frmPredios");
                    JsfUti.executeJS("PF('dlgPrediosConsulta').show();");
                }else{
                    JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(PrediosACoactivar.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void seleccionarPredio(Long tipoPredio) {
        try {
            parametros = new HashMap<>();
            switch (tipoPredio.intValue()) {
                case 1:
                    if (prediosUrbanosConsultaSeleccionados != null && !prediosUrbanosConsultaSeleccionados.isEmpty()) {
                        for (CatPredio pucs : prediosUrbanosConsultaSeleccionados) {
                            parametros.put("numPredio", pucs.getNumPredio());
                            predioUrbanoConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, parametros);
                            if(!this.prediosUrbanos.contains(predioUrbanoConsulta)){
                                this.prediosUrbanos.add(predioUrbanoConsulta);
                            }
                        }
                        JsfUti.executeJS("PF('dlgPrediosConsulta').hide();");
                    } else {
                        JsfUti.messageInfo(null, "Mensaje", "Seleccione un predio, luego clic en Seleccionar");
                    }
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            Logger.getLogger(PrediosACoactivar.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void generarReporte(Long tipoReporte){
        try{
            
            List<Long> predios= new ArrayList<>();
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.agregarParametro("LOGO",  path + SisVars.logoReportes);
            ss.agregarParametro("LOGO2",  path + SisVars.logoReportes);
            ss.agregarParametro("LOGO_FOOTER",  path + SisVars.logoReportes);
            ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/"));
            
            switch (tipoReporte.intValue()) {
                case 1:
                    ss.setNombreSubCarpeta("coactiva");
                    ss.setNombreReporte("prediosACoactivar");
                    for (CatPredio prediosUrbano : prediosUrbanos) {
                        predios.add(prediosUrbano.getId());
                    }
                    break;
                default:
                    break;
            }
            ss.agregarParametro("FECHA", new Date());
            ss.agregarParametro("PREDIOS", (Collection) predios);
            if (!predios.isEmpty()) {
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            }else{
                JsfUti.messageInfo(null, "Mensaje", "La lista de Predios no debe estar Vacia");
            }
        } catch (Exception e) {
            Logger.getLogger(PrediosACoactivar.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void eliminarPredioUrbano(CatPredio urbano){
        this.prediosUrbanos.remove(urbano);
    }

    public CatPredioModel getPredioModel() {
        return predioModel;
    }

    public void setPredioModel(CatPredioModel predioModel) {
        this.predioModel = predioModel;
    }

    public List<CatCiudadela> getCiudadelas() {
        return ciudadelas;
    }

    public void setCiudadelas(List<CatCiudadela> ciudadelas) {
        this.ciudadelas = ciudadelas;
    }

    public CatEnte getContribuyenteConsulta() {
        return contribuyenteConsulta;
    }

    public void setContribuyenteConsulta(CatEnte contribuyenteConsulta) {
        this.contribuyenteConsulta = contribuyenteConsulta;
    }

    public List<CatPredio> getPrediosUrbanos() {
        return prediosUrbanos;
    }

    public void setPrediosUrbanos(List<CatPredio> prediosUrbanos) {
        this.prediosUrbanos = prediosUrbanos;
    }

    public List<CatPredio> getPrediosUrbanosConsultaSeleccionados() {
        return prediosUrbanosConsultaSeleccionados;
    }

    public void setPrediosUrbanosConsultaSeleccionados(List<CatPredio> prediosUrbanosConsultaSeleccionados) {
        this.prediosUrbanosConsultaSeleccionados = prediosUrbanosConsultaSeleccionados;
    }

    public List<CatPredio> getPrediosUrbanosConsulta() {
        return prediosUrbanosConsulta;
    }

    public void setPrediosUrbanosConsulta(List<CatPredio> prediosUrbanosConsulta) {
        this.prediosUrbanosConsulta = prediosUrbanosConsulta;
    }

    public CatEnteLazy getContribuyentes() {
        return contribuyentes;
    }

    public void setContribuyentes(CatEnteLazy contribuyentes) {
        this.contribuyentes = contribuyentes;
    }
    
}
