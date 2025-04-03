/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.edificaciones;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatEdfCategProp;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioS12;
import com.origami.sgm.entities.CatPredioS4;
import com.origami.sgm.entities.CatPredioS6;
import com.origami.sgm.entities.CatTiposDominio;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.entities.Resolucion;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.DivisionPredioServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import util.HiberUtil;

/**
 *
 * @author origami-idea
 */
@Stateless(name = "divisionPredioServices")
@Interceptors(value = {HibernateEjbInterceptor.class})
public class DivisionPredioEjb implements DivisionPredioServices{
    
    @javax.inject.Inject
    protected Entitymanager services;
    
    @javax.inject.Inject
    private SeqGenMan seqManager;
    
    @Override
    public HistoricoTramites obtenerHistoricoTramitePorID(Long id){
        HistoricoTramites ht;
        try{
            ht = (HistoricoTramites) services.find(HistoricoTramites.class, id);
        }catch(Exception e){
            e.printStackTrace();
            ht = null;
        }
        return ht;
    }
    
    @Override
    public List<CtlgItem> obtenerCtlgItemList(){
        List<CtlgItem> list;
        try{
            list = (List<CtlgItem>) services.findAll(Querys.getCtlgItemList, new String[]{}, new Object[]{});
        }catch(Exception e){
            e.printStackTrace();
            list = null;
        }
        return list;
    }
    
    @Override
    public CtlgItem obtenerCtlgItemPorID(Long id){
        CtlgItem ht;
        try{
            ht = (CtlgItem) services.find(CtlgItem.class, id);
        }catch(Exception e){
            e.printStackTrace();
            ht = null;
        }
        return ht;
    }
    
    @Override
    public Boolean guardarResolucion(Resolucion resolucion, Observaciones obs, HistoricoTramites ht){
        Boolean b;
        try{
            b = true;
            if(resolucion.getNumResolucion()!=null)
                services.persist(resolucion);
            services.persist(ht);
            services.persist(obs);
        }catch(Exception e){
            b=false;
            e.printStackTrace();
        }
        return b;
    }
    
    @Override
    public List<CtlgItem> obtenerCtlgItemListByNombreDeCatalogo(String nombreDeCatalogo){
        List<CtlgItem> itemsList;
        try{
            itemsList = (List<CtlgItem>) services.findNoProxy(Querys.getCtlgItemListByNombreDeCatalogo, new String[]{"catalogo"}, new Object[]{nombreDeCatalogo});
        }catch(Exception e){
            e.printStackTrace();
            itemsList = null;
        }
        return itemsList;
    }
    
    @Override
    public List<CatCanton> obtenerCantonesList(){
        List<CatCanton> list;
        try{
            list = (List<CatCanton>) services.findAll(Querys.getCantonList, new String[]{}, new Object[]{});
        }catch(Exception e){
            e.printStackTrace();
            list = null;
        }
        return list;
    }
    
    @Override
    public List<CatEdfCategProp> obtenerCatCategoriasPropConstruccion(){
        List<CatEdfCategProp> list;
        try{
            list = (List<CatEdfCategProp>) services.findAll(Querys.getCatCategoriasPropConstruccionList, new String[]{}, new Object[]{});
        }catch(Exception e){
            e.printStackTrace();
            list = null;
        }
        return list;
    }
    
    @Override
    public List<CatTiposDominio> obtenerTipoDominioList(){
        List<CatTiposDominio> list;
        try{
            list = (List<CatTiposDominio>) services.findAll(Querys.getTipoDominioList, new String[]{}, new Object[]{});
        }catch(Exception e){
            e.printStackTrace();
            list = null;
        }
        return list;
    }
    
    @Override
    public CatPredioPropietario obtenerCatPredioPropietarioByID(Long id){
        CatPredioPropietario cpp;
        try{
            cpp = (CatPredioPropietario) services.find(CatPredioPropietario.class, id);
        }catch(Exception e){
            e.printStackTrace();
            cpp = null;
        }
        return cpp;
    }
    
    @Override
    public CatPredio obtenerCatPredioPorQuery(String query, String[] parametros, Object[] valores){
        CatPredio predio;
        try{
            predio = (CatPredio) services.find(query, parametros, valores);
        }catch(Exception e){
            e.printStackTrace();
            predio = null;
        }
        return predio;
    }
    
    @Override
    public AclUser obtenerAclUserPorQuery(String query, String[] parametros, Object[] valores){
        AclUser user;
        try{
            user = (AclUser) services.find(query, parametros, valores);
        }catch(Exception e){
            e.printStackTrace();
            user = null;
        }
        return user;
    }
    
    @Override
    public List<PeFirma> obtenerPeFirmaListPorQuery(String query, String[] parametros, Object[] valores){
        List<PeFirma> firmaList;
        try{
            firmaList = (List<PeFirma>) services.findNoProxy(query, parametros, valores);
        }catch(Exception e){
            e.printStackTrace();
            firmaList = null;
        }
        return firmaList;
    }
    
    @Override
    public List<CatPredio> obtenerCatPrediosHijos(String query, String[] parametros, Object[] valores){
        List<CatPredio> prediosList;
        try{
            prediosList = (List<CatPredio>) services.findNoProxy(query, parametros, valores);
        }catch(Exception e){
            e.printStackTrace();
            prediosList = null;
        }
        return prediosList;
    }
    
    @Override
    public List<HistoricoReporteTramite> obtenerHistoricoReporteTramiteListPorQuery(String query, String[] parametros, Object[] valores){
        List<HistoricoReporteTramite> hrtList;
        try{
            hrtList = (List<HistoricoReporteTramite>) services.findNoProxy(query, parametros, valores);
        }catch(Exception e){
            e.printStackTrace();
            hrtList = null;
        }
        return hrtList;
    }
    
    @Override
    public CatEnte obtenerCatEntePorQuery(String query, String[] parametros, Object[] valores){
        CatEnte ente;
        try{
            ente = (CatEnte) services.find(query, parametros, valores);
        }catch(Exception e){
            e.printStackTrace();
            ente = null;
        }
        return ente;
    }
            
    @Override
    public HistoricoReporteTramite guardarHistoricoReporteTramite(HistoricoReporteTramite hrt){
        HistoricoReporteTramite h;
        try{
            h = (HistoricoReporteTramite)services.persist(hrt);
        }catch(Exception e){
            h = null;
            e.printStackTrace();
        }
        return h;
    }
    
    @Override
    public HistoricoTramiteDet guardarHistoricoTramiteDetalle(HistoricoTramiteDet htd){
        HistoricoTramiteDet h;
        try{
            h = (HistoricoTramiteDet) services.persist(htd);
        }catch(Exception e){
            e.printStackTrace();
            h = null;
        }
        return h;
    }
    
    @Override
    public CatPredioEdificacion guardarCatPredioEdificacion(CatPredioEdificacion cpe){
        CatPredioEdificacion h;
        try{
            h = (CatPredioEdificacion) services.persist(cpe);
        }catch(Exception e){
            e.printStackTrace();
            h = null;
        }
        return h;
    }
    
    @Override
    public HistoricoTramites guardarHistoricoTramite(HistoricoTramites ht){
        HistoricoTramites h;
        try{
            ht.setLiquidacionAprobada(Boolean.FALSE);
            h = (HistoricoTramites) services.persist(ht);
        }catch(Exception e){
            e.printStackTrace();
            h = null;
        }
        return h;
    }
    
    
    
    @Override
    public Observaciones guardarObservacion(Observaciones obs){
        Observaciones o;
        try{
            o = (Observaciones) services.persist(obs);
        }catch(Exception e){
            e.printStackTrace();
            o = null;
        }
        return o;
    }
    
    @Override
    public CatPredio guardarCatPredio(CatPredio predio){
        CatPredio cp;
        try{
            cp = (CatPredio) services.persist(predio);
        }catch(Exception e){
            Logger.getLogger(DivisionPredioEjb.class.getName()).log(Level.SEVERE, null, e);
            cp = null;
        }
        return cp;
    }
    
    @Override
    public Boolean guardarCatPredioDivisionPredio(List<CatPredio> predios){
        Boolean b;
        try{
            b = true;
            for(CatPredio cp : predios){
                if(cp.getId() == null){
                    cp = seqManager.generarNumPredioAndGuardarCatPredio(cp);
                    cp = this.guardarCatPredio(cp);
                }
                else
                    this.actualizarPredio(cp);
                
                if(cp.getCatPredioPropietarioCollection() == null || cp.getCatPredioPropietarioCollection().isEmpty()){
                    HiberUtil.rollback();
                    return false;
                }
                
                for(CatPredioPropietario cpp : cp.getCatPredioPropietarioCollection()){
                    cpp.setPredio(cp);
                    if(cpp.getId() == null)
                        this.guardarCatPredioPropietario(cpp);
                    else
                        this.actualizarPredioPropietario(cpp);
                }
                
            }
        }catch(Exception e){
            e.printStackTrace();
            b = false;
        }
        return b;
    }
    
    @Override
    public Boolean guardarTasaDeLiquidacion(Observaciones obs, CatPredio predio, HistoricoTramiteDet htd, List<CatPredioPropietario> propietariosList, List<HistoricoReporteTramite> hrtList, List<HistoricoTramiteDet> htdList, HistoricoReporteTramite hrt){
        Boolean b;
        try{
            b = true;
            services.persist(obs);
            
            if(htd.getId()==null)
                services.persist(htd);
            else
                services.persist(htd);
            
            for(CatPredioPropietario cpp : propietariosList){
                cpp.setPredio(predio);
                if(cpp.getId()==null)
                    services.persist(cpp); //services.persist(cpp);
                else
                    services.persist(cpp); //services.persist(cpp);
            }
            
            if(predio.getId()==null)
                services.persist(predio);
            else
                services.persist(predio);
            
            if(hrt.getId()==null)
                services.persist(hrt);
            else
                services.persist(hrt);
            
            for(HistoricoReporteTramite hrtTemp : hrtList){
                hrtTemp.setEstado(false);
                services.persist(hrtTemp);
            }
            for(HistoricoTramiteDet htdTemp : htdList){
                htdTemp.setEstado(false);
                services.persist(htdTemp);
            }
            
        }catch(Exception e){
            b = false;
            e.printStackTrace();
        }
        return b;
    }
    
    @Override
    public Boolean guardarEdificaciones(List<CatPredio> predios){
        Boolean b;
        try{
            b = true;
            for(CatPredio cp : predios){
                
                for(CatPredioEdificacion cpe : cp.getCatPredioEdificacionCollection()){
                    
                    if((cpe.getNoEdificacion()+"").equals("") || cpe.getAreaConsCenso() == null || cpe.getAreaConsPermiso() == null || cpe.getAreaConsLosa() == null || cpe.getEnConstruccionPorc() == null || cpe.getAnioCons() == null || 
                            cpe.getEstaRentado() == null || cpe.getNumPisos() == null || cpe.getEstadoConservacion() == null || cpe.getInstalacionesElectricas() == null ){
                        HiberUtil.rollback();
                        return false;
                    }
                    cpe.setEstado("A");
                    cpe.setPredio(cp);
                    cp.setPropiedadHorizontal(true);
                    if(cpe.getId() == null)
                        services.persist(cpe);
                    else
                        services.persist(cpe);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            b = false;
        }
        return b;
    }
    
    @Override
    public Boolean guardarCatPredioS12(List<CatPredio> predios){
        Boolean b;
        CatPredioS12 cps12;
        try{            
            b = true;
            for(CatPredio cp : predios){
                cps12 = cp.getCatPredioS12();
                if(cps12 == null){
                    HiberUtil.rollback();
                    return false;
                }
                if(cp.getTienePermiso()){
                    if(  cps12.getNumInspeccionFinal() == null || cps12.getAreaConsInspeccion() == null || cps12.getUsosList()== null || cps12.getFechaInspeccionFinal() == null || cps12.getFechaPermiso() == null || cps12.getNumPermisoConstruccion() == null || cps12.getResponsablePermiso() == null ){
                        HiberUtil.rollback();
                        return false;
                    }
                }
                cps12.setPredio(cp);
                services.persist(cps12);
            }
        }catch(Exception e){
            e.printStackTrace();
            b = false;
        }
        return b;
    }
    
    @Override
    public Boolean guardarCatPredioS4S6(List<CatPredio> predios, HistoricoTramites ht){
        Boolean b;
        CatPredioS4 cps4;
        CatPredioS6 cps6;
        List<CatEscritura> escrituras;
        try{
            b = true;
            for(CatPredio cp : predios){
                cps4 = cp.getCatPredioS4();
                cps6 = cp.getCatPredioS6();
                
                if(cps4 == null || cps6 == null){
                    HiberUtil.rollback();
                    return false;
                }
                if(cps4.getCerramientoCtlg() == null ){
                    HiberUtil.rollback();
                    return false;
                }
                if(cps4.getAccesibilidadList() == null || cps4.getAccesibilidadList().isEmpty()){
                    HiberUtil.rollback();
                    return false;
                }
                if(cps6.getNumMedidoresAgua()==null){
                    HiberUtil.rollback();
                    return false;
                }
                if(cps6.getNumMedElect()==null){
                    HiberUtil.rollback();
                    return false;
                }
                if(cps6.getTieneTelfFijo() && cps6.getTelfFijo()==null){
                    HiberUtil.rollback();
                    return false;
                }
                if(cps4.getFrente1()==null || cps4.getFrente2()==null || cps4.getFrente3()==null || cps4.getFrente4()==null || cps4.getFrenteTotal()==null || cps4.getFondo1()==null || cps4.getFondo2()==null || cps4.getAreaCalculada()==null){
                    HiberUtil.rollback();
                    return false;
                }
                if(cps6.getNotaria()==null || cps6.getFechaEscritura() == null || cps6.getFechaInscripcion()==null || cps6.getNumRegistro()==null || cps6.getNumRepertorio()==null || cps6.getFolioDesdeCad()==null || cps6.getFolioHastaCad()==null || cps6.getAreaSolar()==null || cps6.getAreaCons()==null){
                    HiberUtil.rollback();
                    return false;
                }
                if(cps6.getLindLevNorte()==null || cps6.getLindLevSur()==null || cps6.getLindLevEste()==null || cps6.getLindLevOeste()==null || cps6.getLindLevNorteCon()==null || cps6.getLindLevSurCon()==null || cps6.getLindLevEsteCon()==null || cps6.getLindLevOesteCon()==null){
                    HiberUtil.rollback();
                    return false;
                }
                if(cps6.getLindEscNorte()==null || cps6.getLindEscSur()==null || cps6.getLindEscEste()==null || cps6.getLindEscOeste()==null || cps6.getLindEscNorteCon()==null || cps6.getLindEscSurCon()==null || cps6.getLindEscEsteCon()==null || cps6.getLindEscOesteCon()==null){
                    HiberUtil.rollback();
                    return false;
                }
                /*
                CatEscritura temp = new CatEscritura();
                temp.setFecEscritura(cps6.getFechaEscritura());
                temp.setFecInscripcion(cps6.getFechaInscripcion());
                temp.setNotaria(BigInteger.valueOf(cps6.getNotaria()));
                temp.setCanton(cps6.getCanton());
                temp.setNumRegistro(cps6.getNumRegistro());
                temp.setNumRepertorio(cps6.getNumRepertorio());
                temp.setFolioDesde(cps6.getFolioDesdeCad());
                temp.setFolioHasta(cps6.getFolioHastaCad());
                temp.setAreaSolar(cps6.getAreaSolar());
                temp.setAreaConstruccion(cps6.getAreaCons());
                temp.setNumTramite(ht.getId().toString());
                temp.setAlicuota(cps6.getAlicuota());
                temp.setLindEscrEste(cps6.getLindEscEste());
                temp.setLindEscrEsteCon(cps6.getLindEscEsteCon());
                temp.setLindEscrNorte(cps6.getLindEscNorte());
                temp.setLindEscrNorteCon(cps6.getLindEscNorteCon());
                temp.setLindEscrOeste(cps6.getLindEscOeste());
                temp.setLindEscrOesteCon(cps6.getLindEscOesteCon());
                temp.setLindEscrSur(cps6.getLindEscSur());
                temp.setLindEscrSurCon(cps6.getLindEscSurCon());
                temp.setAnio(Long.parseLong(new SimpleDateFormat("yyyy").format(new Date())));
                temp.setEstado("A");
                temp.setPredio(cp);
                services.persist(temp);
                */ 
                cps4.setPredio(cp);
                cps6.setPredio(cp);
                services.persist(cps4);
                services.persist(cps6);
            }
        }catch(Exception e){
            e.printStackTrace();
            b = false;
        }
        return b;
    }
    
    @Override
    public CatPredioPropietario guardarCatPredioPropietario(CatPredioPropietario propietario){
        CatPredioPropietario c;
        try{
            c = (CatPredioPropietario) services.persist(propietario);
        }catch(Exception e){
            e.printStackTrace();
            c = null;
        }
        return c;
    }
    
    @Override
    public Boolean actualizarHistoricoReporteTramite(HistoricoReporteTramite hrt){
        Boolean b;
        try{
            b =  services.update(hrt);
        }catch(Exception e){
            e.printStackTrace();
            b = false;
        }
        return b;
    }
    
    @Override
    public Boolean actualizarCatPredioEdificacion(CatPredioEdificacion cpe){
        Boolean b;
        try{
            b =  services.update(cpe);
        }catch(Exception e){
            e.printStackTrace();
            b = false;
        }
        return b;
    }
    
    @Override
    public Boolean actualizarHistoricoTramiteDetalle(HistoricoTramiteDet htd){
        Boolean b;
        try{
            b =  services.update(htd);
        }catch(Exception e){
            e.printStackTrace();
            b = false;
        }
        return b;
    }
    
    @Override
    public Boolean actualizarPredio(CatPredio predio){
        Boolean b;
        try{
            b =  services.update(predio);
        }catch(Exception e){
            e.printStackTrace();
            b = false;
        }
        return b;
    }
    
    
    @Override
    public Boolean actualizarHistoricoTramite(HistoricoTramites ht){
        Boolean b;
        try{
            b =  services.update(ht);
        }catch(Exception e){
            e.printStackTrace();
            b = false;
        }
        return b;
    }
    
    @Override
    public Boolean actualizarPredioPropietario(CatPredioPropietario propietario){
        Boolean b;
        try{
            b =  services.update(propietario);
        }catch(Exception e){
            e.printStackTrace();
            b = false;
        }
        return b;
    }
    
    @Override
    public Boolean existePredio(CatPredio predio){
        Boolean b;
        
        try{
            List<CatPredio> pred = (List<CatPredio>)services.findAll(Querys.getPredioByCod,
                new String[]{"sectorp", "mzp", "cdlap", "mzdivp", "solarp", "div1p", "div2p", "div3p", "div4p", "div5p", "div6p", "div7p", "div8p", "div9p", "phhp", "phvp"},
                new Object[]{predio.getSector(), predio.getMz(), predio.getCdla(), predio.getMzdiv(), predio.getSolar(), predio.getDiv1(), predio.getDiv2(), predio.getDiv3(),
                    predio.getDiv4(), predio.getDiv5(), predio.getDiv6(), predio.getDiv7(), predio.getDiv8(), predio.getDiv9(), predio.getPhh(), predio.getPhv()});
            if(pred!=null && !pred.isEmpty())
                b = true;
            else
                b = false;
        }catch(Exception e){
            e.printStackTrace();
            b = null;
        }
        return b;
    }
}
