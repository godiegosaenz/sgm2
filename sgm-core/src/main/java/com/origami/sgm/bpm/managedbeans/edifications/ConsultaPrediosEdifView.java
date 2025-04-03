/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications;

import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.managedbeans.recaudaciones.PagoPrediales;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
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
public class ConsultaPrediosEdifView implements Serializable {

    public static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ConsultaPrediosEdifView.class.getName());
    
    @Inject
    private ServletSession ss;
    @Inject
    private UserSession session;
    @javax.inject.Inject
    private Entitymanager manager;
    
    protected Long tipoConsulta = 1L;
    protected CatPredioModel predioModel = new CatPredioModel();
    protected CatEnte contribuyenteConsulta;
    private Map<String, Object> paramt;
    protected CatPredio predioConsulta;
    private List<CatPredio> prediosConsulta;
    private List<CatEnte> lisPropietarios;
    protected List<CatCiudadela> ciudadelas;
    
    @PostConstruct
    public void init(){
        ciudadelas = manager.findAllObjectOrder(CatCiudadela.class, new String[]{"nombre"}, Boolean.TRUE);
    }
    
    public void consultarEmisiones() {
        try {
            switch (tipoConsulta.intValue()) {
                case 1://NUMERO PREDIAL
                    if (predioModel.getNumPredio() != null && predioModel.getNumPredio().compareTo(BigInteger.ZERO) > 0) {
                        paramt = new HashMap<>();
                        paramt.put("numPredio", predioModel.getNumPredio());
                        paramt.put("estado", "A");
                        predioConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, paramt);
                    } else {
                        JsfUti.messageError(null, "Error", "Numero de Predio no es valido.");
                    }
                    break;
                case 3://CODIGO PREDIAL
                    if (predioModel.getSector() > 0 || predioModel.getMz() > 0 || predioModel.getCdla() > 0 || predioModel.getMzDiv() > 0 || predioModel.getSolar() > 0 || predioModel.getDiv1() > 0 || predioModel.getDiv2() > 0 || predioModel.getDiv3() > 0 || predioModel.getDiv4() > 0 || predioModel.getDiv5() > 0 || predioModel.getDiv6() > 0 || predioModel.getDiv7() > 0 || predioModel.getDiv8() > 0 || predioModel.getDiv9() > 0 || predioModel.getPhv() > 0 || predioModel.getPhh() > 0) {
                        paramt = new HashMap<>();
                        paramt.put("estado", "A");
                        paramt.put("sector", predioModel.getSector());
                        paramt.put("mz", predioModel.getMz());
                        paramt.put("cdla", predioModel.getCdla());
                        paramt.put("mzdiv", predioModel.getMzDiv());
                        paramt.put("solar", predioModel.getSolar());
                        paramt.put("div1", predioModel.getDiv1());
                        paramt.put("div2", predioModel.getDiv2());
                        paramt.put("div3", predioModel.getDiv3());
                        paramt.put("div4", predioModel.getDiv4());
                        paramt.put("div5", predioModel.getDiv5());
                        paramt.put("div6", predioModel.getDiv6());
                        paramt.put("div7", predioModel.getDiv7());
                        paramt.put("div8", predioModel.getDiv8());
                        paramt.put("div9", predioModel.getDiv9());
                        paramt.put("phv", predioModel.getPhv());
                        paramt.put("phh", predioModel.getPhh());
                        prediosConsulta = manager.findObjectByParameterList(CatPredio.class, paramt);
                        if (prediosConsulta != null && !prediosConsulta.isEmpty()) {
                            if (prediosConsulta.size() == 1) {
                                paramt = new HashMap<>();
                                paramt.put("numPredio", prediosConsulta.get(0).getNumPredio());
                                paramt.put("estado", "A");
                                predioConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, paramt);
                            }
                        } else {
                            JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Codigo Predial no es valido.");
                    }
                    break;
                case 4:
                    if(predioModel.getCiudadela()!=null && predioModel.getMzUrb()!=null && predioModel.getSlUrb()!=null){
                        paramt = new HashMap<>();
                        paramt.put("estado", "A");
                        paramt.put("ciudadela", predioModel.getCiudadela());
                        paramt.put("urbMz", predioModel.getMzUrb());
                        paramt.put("urbSolarnew", predioModel.getSlUrb());
                        prediosConsulta = manager.findObjectByParameterList(CatPredio.class, paramt);
                        if (prediosConsulta != null && !prediosConsulta.isEmpty()) {
                            if (prediosConsulta.size() == 1) {
                                paramt = new HashMap<>();
                                paramt.put("numPredio", prediosConsulta.get(0).getNumPredio());
                                paramt.put("estado", "A");
                                predioConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, paramt);
                            }
                        } else {
                            JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");
                    }
                    break;
                default:
                    break;
            }
            if (predioConsulta != null) {
                lisPropietarios = new ArrayList();
                for(CatPredioPropietario cpp : predioConsulta.getCatPredioPropietarioCollection()){
                    lisPropietarios.add(cpp.getEnte());
                }
                
                paramt = new HashMap<>();
                paramt.put("tipoLiquidacion", new RenTipoLiquidacion(13L));
                paramt.put("predio", predioConsulta);
            } else {
                //DIALOGO DE SELECCION DE PREDIOS
                if (prediosConsulta == null && (prediosConsulta==null || prediosConsulta.isEmpty()))
                    JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
                if (prediosConsulta != null && prediosConsulta.size() > 1) {
                    JsfUti.update("frmPredios");
                    JsfUti.executeJS("PF('dlgPrediosConsulta').show()");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(PagoPrediales.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public Long getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(Long tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public CatPredioModel getPredioModel() {
        return predioModel;
    }

    public void setPredioModel(CatPredioModel predioModel) {
        this.predioModel = predioModel;
    }

    public CatEnte getContribuyenteConsulta() {
        return contribuyenteConsulta;
    }

    public void setContribuyenteConsulta(CatEnte contribuyenteConsulta) {
        this.contribuyenteConsulta = contribuyenteConsulta;
    }

    public Map<String, Object> getParamt() {
        return paramt;
    }

    public void setParamt(Map<String, Object> paramt) {
        this.paramt = paramt;
    }

    public CatPredio getPredioConsulta() {
        return predioConsulta;
    }

    public void setPredioConsulta(CatPredio predioConsulta) {
        this.predioConsulta = predioConsulta;
    }

    public List<CatPredio> getPrediosConsulta() {
        return prediosConsulta;
    }

    public void setPrediosConsulta(List<CatPredio> prediosConsulta) {
        this.prediosConsulta = prediosConsulta;
    }

    public List<CatEnte> getLisPropietarios() {
        return lisPropietarios;
    }

    public void setLisPropietarios(List<CatEnte> lisPropietarios) {
        this.lisPropietarios = lisPropietarios;
    }

    public List<CatCiudadela> getCiudadelas() {
        return ciudadelas;
    }

    public void setCiudadelas(List<CatCiudadela> ciudadelas) {
        this.ciudadelas = ciudadelas;
    }
}
