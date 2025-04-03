/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.edificaciones;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEdifInspeccionValores;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioEdificacionProp;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.HistoricoArchivo;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeDetalleInspeccion;
import com.origami.sgm.entities.PeDetallePermiso;
import com.origami.sgm.entities.PeInspeccionCabEdificacion;
import com.origami.sgm.entities.PeInspeccionFinal;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.entities.PePermisoCabEdificacion;
import com.origami.sgm.entities.PeTipoPermiso;
import com.origami.sgm.entities.models.AvaluoInspeccion;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.InspeccionFinalServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 *
 * @author Joao Sanga
 */
@Stateless(name = "inspeccionFinalServices")
@Interceptors(value = {HibernateEjbInterceptor.class})
public class InspeccionFinalEjb implements InspeccionFinalServices {

    @javax.inject.Inject
    private Entitymanager services;
    
    @javax.inject.Inject
    private SeqGenMan secuencia;

    @Override
    public List<CtlgItem> obtenerCtlgItemListByNombreDeCatalogo(String nombreDeCatalogo) {
        List<CtlgItem> itemsList;
        try {
            itemsList = (List<CtlgItem>) services.findAll(Querys.getCtlgItemListByNombreDeCatalogo, new String[]{"catalogo"}, new Object[]{nombreDeCatalogo});
        } catch (Exception e) {
            e.printStackTrace();
            itemsList = null;
        }
        return itemsList;
    }

    @Override
    public PeTipoPermiso getTipoPermiso(String codigo) {
        PeTipoPermiso ptp;

        try {
            ptp = (PeTipoPermiso) services.find(Querys.getPeTipoPermisoCodigoAnt, new String[]{"codigo"}, new Object[]{codigo});
        } catch (Exception e) {
            ptp = null;
            e.printStackTrace();
        }
        return ptp;
    }

    @Override
    public Boolean guardarArchivosInspeccion(Observaciones obs, HistoricoArchivo ha) {
        Boolean b;
        try {
            b = true;
            services.persist(obs);
            services.persist(ha);
        } catch (Exception e) {
            b = false;
            e.printStackTrace();
        }
        return b;
    }
    
    @Override
    public Boolean actualizarDatosPredio(CatPredio predio, PeInspeccionFinal inspeccion){
        Boolean b;
        CatPredioEdificacion edificacion;
        CatPredioEdificacionProp propEdif;
        List<CatPredioEdificacionProp> propEdifList;
        PePermiso permiso;
        BigDecimal areaTotal = BigDecimal.ZERO;
        
        try{
            b = true;
            permiso = (PePermiso) services.find(PePermiso.class, inspeccion.getNumPermisoConstruc() == null ? null : inspeccion.getNumPermisoConstruc().longValue());
            predio.setAvaluoConstruccion(inspeccion.getEvaluoLiquidacion());
            predio.setAvaluoMunicipal(inspeccion.getEvaluoLiquidacion().add(predio.getAvaluoSolar()));
            
            predio.getCatPredioS12().setFechaInspeccionFinal(inspeccion.getFechaInspeccion());
            predio.getCatPredioS12().setNumInspeccionFinal(inspeccion.getId()+"");
            
            predio.getCatPredioS12().setFechaPermiso(permiso.getFechaEmision());
            predio.getCatPredioS12().setNumPermisoConstruccion(permiso.getId()+"");
            
            predio.getCatPredioS12().setResponsablePermiso(inspeccion.getRespTecnico().getNombreCompleto());
            
            //predio.setAvaluoSolar(inspeccion.get);
            services.update(predio);
            for(PeInspeccionCabEdificacion temp1 : inspeccion.getPeInspeccionCabEdificacionCollection()){
                edificacion = new CatPredioEdificacion();
                if(services.find(Querys.getCatPredioEdificacionByPredioAndNumEdif, new String[]{"predio", "noEdif"}, new Object[]{predio, temp1.getNumEdificacion()}) == null){
                    edificacion.setPredio(predio);
                    edificacion.setAnioCons(inspeccion.getAnioInspeccion().intValue());
                    edificacion.setNoEdificacion(Short.valueOf(temp1.getNumEdificacion()+""));
                    edificacion.setNumPisos(Short.valueOf(temp1.getCantidadPisos()+""));
                    edificacion.setAreaConsPermiso(temp1.getAreaConstruccion());
                    edificacion.setEstado("A");
                    edificacion.setEstadoConservacion(new CtlgItem(43L));
                    edificacion.setVidautil(20);
                    areaTotal = areaTotal.add(temp1.getAreaConstruccion());

                    edificacion = (CatPredioEdificacion) services.persist(edificacion);
                }
                
                for(PeDetalleInspeccion temp2 : temp1.getPeDetalleInspeccionCollection()){
                    if(services.find(Querys.getCatPredioEdificacionProp, new String[]{"edif", "prop"}, new Object[]{edificacion, temp2.getCaracteristica()}) == null){
                        propEdif = new CatPredioEdificacionProp();

                        propEdif.setEdificacion(edificacion);
                        propEdif.setEstado(temp2.getEstado());
                        propEdif.setPorcentaje(temp2.getPorcentaje());
                        propEdif.setProp(temp2.getCaracteristica());
                        services.persist(propEdif);
                    }
                }
                //edificacion.setCatPredioEdificacionPropCollection(propEdifList);
                //services.update(edificacion);
            }
            areaTotal = areaTotal.add(inspeccion.getAreaParqueos());
            predio.getCatPredioS12().setAreaConsInspeccion(areaTotal);
            services.update(predio);
        }catch(Exception e){            
            e.printStackTrace();
            return false;
        }
        return b;
    }
    
    @Override
    public BigDecimal valoresAvaluoOrdenanzaPermiso(List<PePermisoCabEdificacion> edificacionesList){
        return null;
    }
    
    @Override
    public BigDecimal valoresAvaluoOrdenanzaInspeccion(List<PeInspeccionCabEdificacion> edificacionesList){
        
        Integer tipo1, tipo2, tipo3, tipo4, tipo5, tipo6, tipo7, tipo8, tipo9, tipo10, tipo11, tipo12, tipo13, tipo14, tipo15, tipo16, tipo17;
        tipo1 = tipo2 = tipo3 = tipo4 = tipo5 = tipo6 = tipo7 = tipo8 = tipo9 = tipo10 = tipo11 = tipo12 = tipo13 = tipo14 = tipo15 = tipo16 = tipo17 = 0;
        List<AvaluoInspeccion> list = new ArrayList();
        list.add(new AvaluoInspeccion(1L)); list.add(new AvaluoInspeccion(2L)); list.add(new AvaluoInspeccion(3L)); list.add(new AvaluoInspeccion(4L)); 
        list.add(new AvaluoInspeccion(5L)); list.add(new AvaluoInspeccion(6L)); list.add(new AvaluoInspeccion(7L)); list.add(new AvaluoInspeccion(8L)); list.add(new AvaluoInspeccion(9L));
        list.add(new AvaluoInspeccion(10L)); list.add(new AvaluoInspeccion(11L)); list.add(new AvaluoInspeccion(12L)); list.add(new AvaluoInspeccion(13L)); 
        list.add(new AvaluoInspeccion(14L)); list.add(new AvaluoInspeccion(15L)); list.add(new AvaluoInspeccion(16L)); list.add(new AvaluoInspeccion(17L));
        
        CatEdifInspeccionValores valor;
        BigDecimal total = BigDecimal.ZERO;
        if(edificacionesList!=null){
            for(PeInspeccionCabEdificacion temp : edificacionesList){
                for(PeDetalleInspeccion temp2 : temp.getPeDetalleInspeccionCollection()){

                    if(temp2.getCaracteristica().getId().compareTo(1L) == 0){
                        list.get(13).setCantidad(list.get(13).getCantidad() + 1);
                        list.get(14).setCantidad(list.get(14).getCantidad() + 1); list.get(15).setCantidad(list.get(15).getCantidad() + 1); 
                        list.get(16).setCantidad(list.get(16).getCantidad() + 1); 
                        list.get(13).setCantidad(list.get(13).getCantidad() + 1);
                    }
                    //if(temp2.getCaracteristica().getId().compareTo(2L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(3L) == 0){
                        list.get(0).setCantidad(list.get(0).getCantidad() + 1); 
                        list.get(1).setCantidad(list.get(1).getCantidad() + 1); 
                        list.get(2).setCantidad(list.get(2).getCantidad() + 1); 
                        list.get(3).setCantidad(list.get(3).getCantidad() + 1); 
                        list.get(4).setCantidad(list.get(4).getCantidad() + 1); 
                        list.get(5).setCantidad(list.get(5).getCantidad() + 1); list.get(6).setCantidad(list.get(6).getCantidad() + 1); list.get(7).setCantidad(list.get(7).getCantidad() + 1); list.get(8).setCantidad(list.get(8).getCantidad() + 1); list.get(9).setCantidad(list.get(9).getCantidad() + 1); list.get(10).setCantidad(list.get(10).getCantidad() + 1); list.get(11).setCantidad(list.get(11).getCantidad() + 1); list.get(12).setCantidad(list.get(12).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(59L) == 0){
                        list.get(0).setCantidad(list.get(0).getCantidad() + 1); list.get(1).setCantidad(list.get(1).getCantidad() + 1); list.get(2).setCantidad(list.get(2).getCantidad() + 1); list.get(3).setCantidad(list.get(3).getCantidad() + 1); list.get(4).setCantidad(list.get(4).getCantidad() + 1); list.get(5).setCantidad(list.get(5).getCantidad() + 1); list.get(6).setCantidad(list.get(6).getCantidad() + 1); list.get(7).setCantidad(list.get(7).getCantidad() + 1); list.get(8).setCantidad(list.get(8).getCantidad() + 1); list.get(9).setCantidad(list.get(9).getCantidad() + 1); list.get(10).setCantidad(list.get(10).getCantidad() + 1); list.get(11).setCantidad(list.get(11).getCantidad() + 1); list.get(12).setCantidad(list.get(12).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(4L) == 0){
                        list.get(13).setCantidad(list.get(13).getCantidad() + 1); list.get(15).setCantidad(list.get(15).getCantidad() + 1); 
                        list.get(16).setCantidad(list.get(16).getCantidad() + 1); 
                        list.get(8).setCantidad(list.get(8).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(5L) == 0){
                        list.get(0).setCantidad(list.get(0).getCantidad() + 1); list.get(13).setCantidad(list.get(13).getCantidad() + 1); list.get(14).setCantidad(list.get(14).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(6L) == 0){
                        list.get(0).setCantidad(list.get(0).getCantidad() + 1); list.get(1).setCantidad(list.get(1).getCantidad() + 1); list.get(2).setCantidad(list.get(2).getCantidad() + 1); list.get(3).setCantidad(list.get(3).getCantidad() + 1); list.get(4).setCantidad(list.get(4).getCantidad() + 1); list.get(5).setCantidad(list.get(5).getCantidad() + 1); list.get(6).setCantidad(list.get(6).getCantidad() + 1); list.get(7).setCantidad(list.get(7).getCantidad() + 1); list.get(8).setCantidad(list.get(8).getCantidad() + 1); list.get(9).setCantidad(list.get(9).getCantidad() + 1); list.get(10).setCantidad(list.get(10).getCantidad() + 1); list.get(12).setCantidad(list.get(12).getCantidad() + 1); list.get(14).setCantidad(list.get(14).getCantidad() + 1); list.get(15).setCantidad(list.get(15).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(7L) == 0){
                        list.get(2).setCantidad(list.get(2).getCantidad() + 1); list.get(3).setCantidad(list.get(3).getCantidad() + 1); list.get(4).setCantidad(list.get(4).getCantidad() + 1); list.get(5).setCantidad(list.get(5).getCantidad() + 1); list.get(6).setCantidad(list.get(6).getCantidad() + 1); list.get(7).setCantidad(list.get(7).getCantidad() + 1); list.get(9).setCantidad(list.get(9).getCantidad() + 1); list.get(10).setCantidad(list.get(10).getCantidad() + 1); list.get(11).setCantidad(list.get(11).getCantidad() + 1); list.get(12).setCantidad(list.get(12).getCantidad() + 1); 
                    }
                    if(temp2.getCaracteristica().getId().compareTo(8L) == 0){
                        list.get(0).setCantidad(list.get(0).getCantidad() + 1); list.get(1).setCantidad(list.get(1).getCantidad() + 1); list.get(8).setCantidad(list.get(8).getCantidad() + 1); list.get(9).setCantidad(list.get(9).getCantidad() + 1); list.get(13).setCantidad(list.get(13).getCantidad() + 1); list.get(15).setCantidad(list.get(15).getCantidad() + 1); 
                        list.get(16).setCantidad(list.get(16).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(9L) == 0){
                        list.get(0).setCantidad(list.get(0).getCantidad() + 1); list.get(1).setCantidad(list.get(1).getCantidad() + 1); list.get(8).setCantidad(list.get(8).getCantidad() + 1); list.get(10).setCantidad(list.get(10).getCantidad() + 1); list.get(11).setCantidad(list.get(11).getCantidad() + 1); list.get(14).setCantidad(list.get(14).getCantidad() + 1); list.get(15).setCantidad(list.get(15).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(10L) == 0){
                        list.get(1).setCantidad(list.get(1).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(11L) == 0){
                        list.get(2).setCantidad(list.get(2).getCantidad() + 1); list.get(5).setCantidad(list.get(5).getCantidad() + 1); list.get(6).setCantidad(list.get(6).getCantidad() + 1); list.get(7).setCantidad(list.get(7).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(12L) == 0){
                        list.get(2).setCantidad(list.get(2).getCantidad() + 1);  list.get(3).setCantidad(list.get(3).getCantidad() + 1); list.get(5).setCantidad(list.get(5).getCantidad() + 1); list.get(6).setCantidad(list.get(6).getCantidad() + 1); list.get(7).setCantidad(list.get(7).getCantidad() + 1); list.get(12).setCantidad(list.get(12).getCantidad() + 1); 
                    }
                    if(temp2.getCaracteristica().getId().compareTo(13L) == 0){
                        list.get(3).setCantidad(list.get(3).getCantidad() + 1); list.get(4).setCantidad(list.get(4).getCantidad() + 1); list.get(5).setCantidad(list.get(5).getCantidad() + 1); list.get(6).setCantidad(list.get(6).getCantidad() + 1); list.get(7).setCantidad(list.get(7).getCantidad() + 1); list.get(12).setCantidad(list.get(12).getCantidad() + 1); 

                    }
                    if(temp2.getCaracteristica().getId().compareTo(14L) == 0){
                        list.get(3).setCantidad(list.get(3).getCantidad() + 1); list.get(4).setCantidad(list.get(4).getCantidad() + 1); list.get(5).setCantidad(list.get(5).getCantidad() + 1); list.get(6).setCantidad(list.get(6).getCantidad() + 1);  list.get(7).setCantidad(list.get(7).getCantidad() + 1); list.get(12).setCantidad(list.get(12).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(15L) == 0){
                        list.get(4).setCantidad(list.get(4).getCantidad() + 1); list.get(5).setCantidad(list.get(5).getCantidad() + 1); list.get(6).setCantidad(list.get(6).getCantidad() + 1); list.get(7).setCantidad(list.get(7).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(16L) == 0){
                        list.get(3).setCantidad(list.get(3).getCantidad() + 1); list.get(4).setCantidad(list.get(4).getCantidad() + 1); list.get(5).setCantidad(list.get(5).getCantidad() + 1); list.get(6).setCantidad(list.get(6).getCantidad() + 1); list.get(7).setCantidad(list.get(7).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(17L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(18L) == 0){
                        list.get(0).setCantidad(list.get(0).getCantidad() + 1); list.get(13).setCantidad(list.get(13).getCantidad() + 1); 
                        list.get(16).setCantidad(list.get(16).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(19L) == 0){
                        list.get(13).setCantidad(list.get(13).getCantidad() + 1); list.get(15).setCantidad(list.get(15).getCantidad() + 1); 
                        list.get(16).setCantidad(list.get(16).getCantidad() + 1); 
                        list.get(0).setCantidad(list.get(0).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(20L) == 0){
                        list.get(1).setCantidad(list.get(1).getCantidad() + 1); list.get(2).setCantidad(list.get(2).getCantidad() + 1); list.get(3).setCantidad(list.get(3).getCantidad() + 1); list.get(4).setCantidad(list.get(4).getCantidad() + 1); list.get(5).setCantidad(list.get(5).getCantidad() + 1); list.get(6).setCantidad(list.get(6).getCantidad() + 1); list.get(7).setCantidad(list.get(7).getCantidad() + 1); list.get(9).setCantidad(list.get(9).getCantidad() + 1); list.get(10).setCantidad(list.get(10).getCantidad() + 1); list.get(11).setCantidad(list.get(11).getCantidad() + 1); list.get(12).setCantidad(list.get(12).getCantidad() + 1); list.get(14).setCantidad(list.get(14).getCantidad() + 1); list.get(15).setCantidad(list.get(15).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(21L) == 0){
                        list.get(9).setCantidad(list.get(9).getCantidad() + 1); list.get(10).setCantidad(list.get(10).getCantidad() + 1); list.get(11).setCantidad(list.get(11).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(22L) == 0){
                        list.get(9).setCantidad(list.get(9).getCantidad() + 1); 
                        list.get(16).setCantidad(list.get(16).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(23L) == 0){
                        list.get(0).setCantidad(list.get(0).getCantidad() + 1); list.get(1).setCantidad(list.get(1).getCantidad() + 1); list.get(2).setCantidad(list.get(2).getCantidad() + 1); list.get(8).setCantidad(list.get(8).getCantidad() + 1); list.get(10).setCantidad(list.get(10).getCantidad() + 1); list.get(11).setCantidad(list.get(11).getCantidad() + 1); list.get(13).setCantidad(list.get(13).getCantidad() + 1); list.get(14).setCantidad(list.get(14).getCantidad() + 1); list.get(15).setCantidad(list.get(15).getCantidad() + 1); 
                        list.get(16).setCantidad(list.get(16).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(24L) == 0){
                        list.get(0).setCantidad(list.get(0).getCantidad() + 1); list.get(1).setCantidad(list.get(1).getCantidad() + 1); list.get(2).setCantidad(list.get(2).getCantidad() + 1); list.get(8).setCantidad(list.get(8).getCantidad() + 1); list.get(10).setCantidad(list.get(10).getCantidad() + 1); list.get(11).setCantidad(list.get(11).getCantidad() + 1); list.get(13).setCantidad(list.get(13).getCantidad() + 1); list.get(14).setCantidad(list.get(14).getCantidad() + 1); list.get(15).setCantidad(list.get(15).getCantidad() + 1); 
                        list.get(16).setCantidad(list.get(16).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(25L) == 0){
                        list.get(0).setCantidad(list.get(0).getCantidad() + 1); list.get(1).setCantidad(list.get(1).getCantidad() + 1); list.get(2).setCantidad(list.get(2).getCantidad() + 1); list.get(5).setCantidad(list.get(5).getCantidad() + 1); list.get(6).setCantidad(list.get(6).getCantidad() + 1); list.get(7).setCantidad(list.get(7).getCantidad() + 1); list.get(8).setCantidad(list.get(8).getCantidad() + 1); list.get(10).setCantidad(list.get(10).getCantidad() + 1); list.get(11).setCantidad(list.get(11).getCantidad() + 1); list.get(14).setCantidad(list.get(14).getCantidad() + 1); list.get(15).setCantidad(list.get(15).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(26L) == 0){
                        list.get(3).setCantidad(list.get(3).getCantidad() + 1); list.get(4).setCantidad(list.get(4).getCantidad() + 1); list.get(12).setCantidad(list.get(12).getCantidad() + 1); list.get(5).setCantidad(list.get(5).getCantidad() + 1); list.get(6).setCantidad(list.get(6).getCantidad() + 1); list.get(7).setCantidad(list.get(7).getCantidad() + 1); list.get(8).setCantidad(list.get(8).getCantidad() + 1); list.get(10).setCantidad(list.get(10).getCantidad() + 1); list.get(11).setCantidad(list.get(11).getCantidad() + 1); list.get(14).setCantidad(list.get(14).getCantidad() + 1); list.get(15).setCantidad(list.get(15).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(27L) == 0){
                        list.get(2).setCantidad(list.get(2).getCantidad() + 1); list.get(3).setCantidad(list.get(3).getCantidad() + 1); list.get(4).setCantidad(list.get(4).getCantidad() + 1);  list.get(5).setCantidad(list.get(5).getCantidad() + 1); list.get(6).setCantidad(list.get(6).getCantidad() + 1); list.get(7).setCantidad(list.get(7).getCantidad() + 1); list.get(12).setCantidad(list.get(12).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(28L) == 0){
                        list.get(0).setCantidad(list.get(0).getCantidad() + 1); list.get(10).setCantidad(list.get(10).getCantidad() + 1); list.get(11).setCantidad(list.get(11).getCantidad() + 1);  list.get(13).setCantidad(list.get(13).getCantidad() + 1); list.get(15).setCantidad(list.get(15).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(29L) == 0){
                        list.get(0).setCantidad(list.get(0).getCantidad() + 1); list.get(1).setCantidad(list.get(1).getCantidad() + 1); list.get(14).setCantidad(list.get(14).getCantidad() + 1); 
                    }
                    if(temp2.getCaracteristica().getId().compareTo(30L) == 0){
                        list.get(0).setCantidad(list.get(0).getCantidad() + 1); list.get(1).setCantidad(list.get(1).getCantidad() + 1); list.get(2).setCantidad(list.get(2).getCantidad() + 1); list.get(14).setCantidad(list.get(14).getCantidad() + 1); 
                    }
                    if(temp2.getCaracteristica().getId().compareTo(31L) == 0){
                        list.get(2).setCantidad(list.get(2).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(32L) == 0){
                        list.get(3).setCantidad(list.get(3).getCantidad() + 1); list.get(4).setCantidad(list.get(4).getCantidad() + 1); list.get(12).setCantidad(list.get(12).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(33L) == 0){
                        list.get(3).setCantidad(list.get(3).getCantidad() + 1); list.get(4).setCantidad(list.get(4).getCantidad() + 1); list.get(12).setCantidad(list.get(12).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(34L) == 0){
                        list.get(2).setCantidad(list.get(2).getCantidad() + 1); list.get(3).setCantidad(list.get(3).getCantidad() + 1); list.get(4).setCantidad(list.get(4).getCantidad() + 1); list.get(12).setCantidad(list.get(12).getCantidad() + 1); list.get(5).setCantidad(list.get(5).getCantidad() + 1); list.get(6).setCantidad(list.get(6).getCantidad() + 1); list.get(7).setCantidad(list.get(7).getCantidad() + 1); 
                    }
                    if(temp2.getCaracteristica().getId().compareTo(35L) == 0){
                        list.get(2).setCantidad(list.get(2).getCantidad() + 1); list.get(15).setCantidad(list.get(15).getCantidad() + 1); 
                        list.get(16).setCantidad(list.get(16).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(36L) == 0){
                        list.get(1).setCantidad(list.get(1).getCantidad() + 1); list.get(2).setCantidad(list.get(2).getCantidad() + 1); list.get(3).setCantidad(list.get(3).getCantidad() + 1); list.get(4).setCantidad(list.get(4).getCantidad() + 1); list.get(12).setCantidad(list.get(12).getCantidad() + 1); list.get(5).setCantidad(list.get(5).getCantidad() + 1); list.get(6).setCantidad(list.get(6).getCantidad() + 1); list.get(7).setCantidad(list.get(7).getCantidad() + 1); list.get(8).setCantidad(list.get(8).getCantidad() + 1); list.get(9).setCantidad(list.get(9).getCantidad() + 1); list.get(10).setCantidad(list.get(10).getCantidad() + 1); list.get(11).setCantidad(list.get(11).getCantidad() + 1); list.get(12).setCantidad(list.get(12).getCantidad() + 1); list.get(14).setCantidad(list.get(14).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(37L) == 0){
                        list.get(0).setCantidad(list.get(0).getCantidad() + 1); list.get(1).setCantidad(list.get(1).getCantidad() + 1); list.get(8).setCantidad(list.get(8).getCantidad() + 1); list.get(9).setCantidad(list.get(9).getCantidad() + 1); list.get(13).setCantidad(list.get(13).getCantidad() + 1); list.get(14).setCantidad(list.get(14).getCantidad() + 1); list.get(15).setCantidad(list.get(15).getCantidad() + 1); 
                        list.get(16).setCantidad(list.get(16).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(38L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(39L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(40L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(41L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(42L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(43L) == 0){
                        list.get(12).setCantidad(list.get(12).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(44L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(45L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(46L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(47L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(48L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(49L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(50L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(51L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(52L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(53L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(54L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(56L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(57L) == 0){
                        list.get(0).setCantidad(list.get(0).getCantidad() + 1); list.get(2).setCantidad(list.get(2).getCantidad() + 1); list.get(5).setCantidad(list.get(5).getCantidad() + 1); list.get(6).setCantidad(list.get(6).getCantidad() + 1); list.get(7).setCantidad(list.get(7).getCantidad() + 1); list.get(8).setCantidad(list.get(8).getCantidad() + 1);
                        list.get(10).setCantidad(list.get(10).getCantidad() + 1); list.get(14).setCantidad(list.get(14).getCantidad() + 1); list.get(15).setCantidad(list.get(15).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(58L) == 0){}                
                    if(temp2.getCaracteristica().getId().compareTo(60L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(61L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(62L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(63L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(64L) == 0){
                        list.get(1).setCantidad(list.get(1).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(65L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(66L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(67L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(68L) == 0){
                        list.get(0).setCantidad(list.get(0).getCantidad() + 1); list.get(1).setCantidad(list.get(1).getCantidad() + 1); list.get(2).setCantidad(list.get(2).getCantidad() + 1); list.get(3).setCantidad(list.get(3).getCantidad() + 1); list.get(4).setCantidad(list.get(4).getCantidad() + 1); list.get(5).setCantidad(list.get(5).getCantidad() + 1); list.get(6).setCantidad(list.get(6).getCantidad() + 1); list.get(7).setCantidad(list.get(7).getCantidad() + 1);
                        list.get(9).setCantidad(list.get(9).getCantidad() + 1); list.get(10).setCantidad(list.get(10).getCantidad() + 1); list.get(11).setCantidad(list.get(11).getCantidad() + 1); list.get(12).setCantidad(list.get(12).getCantidad() + 1); list.get(14).setCantidad(list.get(14).getCantidad() + 1); list.get(15).setCantidad(list.get(15).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(69L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(70L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(71L) == 0){
                        list.get(3).setCantidad(list.get(3).getCantidad() + 1); list.get(12).setCantidad(list.get(12).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(72L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(73L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(74L) == 0){
                        list.get(12).setCantidad(list.get(12).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(75L) == 0){
                        list.get(12).setCantidad(list.get(12).getCantidad() + 1);
                    }
                    if(temp2.getCaracteristica().getId().compareTo(76L) == 0){}
                    if(temp2.getCaracteristica().getId().compareTo(78L) == 0){}

                }

                Collections.sort(list);

                // MÉTODO EXHAUSTIVO QUE ME PERMITE TENER EL VALOR DE LA TABLA DE VALORES DE ACUERDO A UN CONJUNTO DE PARÁMETROS
                valor = buscarValor(temp, list, 0);

                total = total.add(valor.getValor().multiply(temp.getAreaConstruccion()));

            }
        }
        return total;
    }
    
    
    public CatEdifInspeccionValores buscarValor(PeInspeccionCabEdificacion temp, List<AvaluoInspeccion> list, int indice){
        CatEdifInspeccionValores valor;
        valor = (CatEdifInspeccionValores)services.find(Querys.getValorOrdenanzaNew, new String[]{"tipoId", "numPisos"}, new Object[]{new Long(list.get(indice).getId()), temp.getCantidadPisos()});
        if(list == null || list.isEmpty()){
            return new CatEdifInspeccionValores(new BigDecimal(600L));
        }else if(valor == null){
            buscarValor(temp, list, indice+1);
        }
        return valor;
    }
    
    public PeInspeccionFinal guardarInspeccionFinal(PeInspeccionFinal pif, List<CtlgItem> detallesList) {
        PeInspeccionFinal b;
        try {
            if(pif.getId()==null)
                b = (PeInspeccionFinal) services.persist(pif);
            else
                b = pif;
            b.setDetalleList(detallesList);
            services.update(b);

        } catch (Exception e) {
            b = null;
            e.printStackTrace();
        }
        return b;
    }
    
    public Boolean guardarEdicionInspeccionFinal(PeInspeccionFinal pif, List<CtlgItem> detallesList) {
        Boolean b;
        try {
            b = true;
            services.update(pif);
            
            pif.setDetalleList(detallesList);
            services.update(pif);

        } catch (Exception e) {
            b = false;
            e.printStackTrace();
        }
        return b;
    }

    @Override
    public PeInspeccionFinal guardarTasaDeLiquidacion(PePermiso permiso, PeInspeccionFinal inspeccionFinalV, List<CtlgItem> listaDetalle, Observaciones obs, HistoricoTramiteDet htd, HistoricoReporteTramite hrt, List<CatPredioPropietario> propietariosList, List<PePermisoCabEdificacion> pePermisoCabEdificacionList, List<PeDetallePermiso> peDetallePermisoList, List<HistoricoTramiteDet> htdList, HistoricoTramites ht) {
        Boolean b;
        BigInteger sec;
        try {
            Long anio = new Long(new SimpleDateFormat("yyyy").format(new Date()));
            sec = new BigInteger(secuencia.getMaxSecuenciaTipoTramite(Integer.valueOf(anio+""), ht.getTipoTramite().getId()).toString());
            
            inspeccionFinalV.setNumReporte(sec);
            
            hrt.setCodValidacion(sec + "" + hrt.getProceso());
            hrt.setSecuencia(sec);
            htd.setNumTasa(sec);
            inspeccionFinalV.setNumReporte(sec);
            
            services.persist(obs);
            
            inspeccionFinalV = this.guardarInspeccionFinal(inspeccionFinalV, listaDetalle);
            
            for(HistoricoTramiteDet temp : htdList){
                temp.setEstado(false);
                services.update(temp);
            }
            
            services.persist(htd);
            
            services.persist(hrt);
            
            services.update(ht);

            for (PePermisoCabEdificacion ppce : pePermisoCabEdificacionList) {
                PeInspeccionCabEdificacion cabEdificacion = new PeInspeccionCabEdificacion();

                cabEdificacion.setAreaConstruccion(ppce.getAreaConstruccion());
                if (ppce.getNumeroPisos() != null) {
                    cabEdificacion.setCantidadPisos(ppce.getNumeroPisos() + 0);
                }
                cabEdificacion.setDescEdificacion(ppce.getDescEdificacion());
                if (ppce.getNumEdificacion() != null) {
                    cabEdificacion.setNumEdificacion(ppce.getNumEdificacion() + 0);
                }
                cabEdificacion.setIdInspeccion(inspeccionFinalV);
                cabEdificacion.setEstado(true);
                cabEdificacion = (PeInspeccionCabEdificacion) services.persist(cabEdificacion);

                for (PeDetallePermiso pdp : ppce.getPeDetallePermisoCollection()) {
                    PeDetalleInspeccion pdi = new PeDetalleInspeccion();

                    pdi.setArea(pdp.getArea());
                    pdi.setCaracteristica(pdp.getIdCatEdfProp());
                    pdi.setPorcentaje(pdp.getPorcentaje());
                    pdi.setEdificacion(cabEdificacion);
                    pdi.setEstado(true);

                    services.persist(pdi);
                }
                
            }
            
            //inspeccionFinalV.setAvaluoInspeccion(this.valoresAvaluoOrdenanzaInspeccion((List)inspeccionFinalV.getPeInspeccionCabEdificacionCollection()));

            for (CatPredioPropietario cpp : propietariosList) {
                if (cpp.getId() == null) {
                    services.persist(cpp); //services.persist(cpp);
                } else {
                    services.update(cpp); //services.update(cpp);
                }
            }
            
            

        } catch (Exception e) {
            e.printStackTrace();
        }
        return inspeccionFinalV;
    }
    
    @Override
    public Boolean guardarTasaDeLiquidacionEdicionLocal(PeInspeccionFinal inspeccionFinalV, List<CtlgItem> listaDetalle, HistoricoTramiteDet htd, List<CatPredioPropietario> propietariosList, List<HistoricoTramiteDet> htdList, List<PeInspeccionCabEdificacion> peInspeccionCabEdificacionList){
        Boolean b;
        List<PeDetalleInspeccion> detalles;
        
        try {
            b = true;
            inspeccionFinalV = this.guardarInspeccionFinal(inspeccionFinalV, listaDetalle);
            
            if(htd!=null){
                if (htd.getId() == null) {
                    services.persist(htd);
                } else {
                    services.update(htd);
                }
            }

            for (PeInspeccionCabEdificacion pice : peInspeccionCabEdificacionList) {
                detalles = (List<PeDetalleInspeccion>) pice.getPeDetalleInspeccionCollection();
                pice.setEstado(true);
                pice.setIdInspeccion(inspeccionFinalV);
                if (pice.getId() == null) {                    
                    pice = (PeInspeccionCabEdificacion) services.persist(pice);
                } else {
                    services.update(pice);
                }

                if (detalles != null ) {
                    for (PeDetalleInspeccion pdi : detalles) {
                        pdi.setEstado(true);
                        pdi.setEdificacion(pice);
                        if (pdi.getId() == null) {
                            pdi = (PeDetalleInspeccion) services.persist(pdi);
                        } else {
                            services.update(pdi);
                        }
                    }
                }
            }
            
            //inspeccionFinalV.setAvaluoInspeccion(this.valoresAvaluoOrdenanzaInspeccion((List)inspeccionFinalV.getPeInspeccionCabEdificacionCollection()));

            for (CatPredioPropietario cpp : propietariosList) {
                if (cpp.getId() == null) {
                    services.persist(cpp); //services.persist(cpp);
                } else {
                    services.update(cpp); //services.update(cpp);
                }
            }
            
            if(htdList!=null){
                for (HistoricoTramiteDet htdTemp : htdList) {
                    htdTemp.setEstado(false);
                    services.update(htdTemp);
                }
            }
        } catch (Exception e) {
            b = false;
            e.printStackTrace();
        }
        return b;
    }

    @Override
    public Boolean guardarTasaDeLiquidacionEdicion(PeInspeccionFinal inspeccionFinalV, List<CtlgItem> listaDetalle, Observaciones obs,  HistoricoTramiteDet htd, List<CatPredioPropietario> propietariosList, List<HistoricoReporteTramite> hrtList, List<HistoricoTramiteDet> htdList, List<PeInspeccionCabEdificacion> peInspeccionCabEdificacionList) {
        Boolean b;
        List<PeDetalleInspeccion> detalles;

        try {
            b = true;
            services.persist(obs);
            inspeccionFinalV = this.guardarInspeccionFinal(inspeccionFinalV, listaDetalle);

            if (htd.getId() == null) {
                services.persist(htd);
            } else {
                services.update(htd);
            }

            for (PeInspeccionCabEdificacion pice : peInspeccionCabEdificacionList) {
                detalles = (List<PeDetalleInspeccion>) pice.getPeDetalleInspeccionCollection();
                pice.setEstado(true);
                pice.setIdInspeccion(inspeccionFinalV);
                if (pice.getId() == null) {                    
                    pice = (PeInspeccionCabEdificacion) services.persist(pice);
                } else {
                    services.update(pice);
                }

                if (detalles != null) {
                    for (PeDetalleInspeccion pdi : detalles) {
                        pdi.setEstado(true);
                        pdi.setEdificacion(pice);
                        if (pdi.getId() == null) {
                            pdi = (PeDetalleInspeccion) services.persist(pdi);
                        } else {
                            services.update(pdi);
                        }
                    }
                }
            }
            
            //inspeccionFinalV.setAvaluoInspeccion(this.valoresAvaluoOrdenanzaInspeccion((List)inspeccionFinalV.getPeInspeccionCabEdificacionCollection()));

            for (CatPredioPropietario cpp : propietariosList) {
                if (cpp.getId() == null) {
                    services.persist(cpp); //services.persist(cpp);
                } else {
                    services.update(cpp); //services.update(cpp);
                }
            }

            for (HistoricoReporteTramite hrtTemp : hrtList) {
                hrtTemp.setEstado(false);
                services.update(hrtTemp);
            }

            for (HistoricoTramiteDet htdTemp : htdList) {
                htdTemp.setEstado(false);
                services.update(htdTemp);
            }

        } catch (Exception e) {
            b = false;
            e.printStackTrace();
        }
        return b;
    }

}
