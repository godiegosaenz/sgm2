/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.inspeccionFinal;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.HistoricoArchivo;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.PeInspeccionFinal;
import com.origami.sgm.services.bpm.BpmBaseEngine;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.activiti.engine.task.Attachment;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import util.ApplicationContextUtils;
import util.CmisUtil;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@SessionScoped
public class EditarFotosViewIF implements Serializable{
    public static final Long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(InspeccionFinalConsulta.class.getName());
    
    @javax.inject.Inject
    protected BpmBaseEngine engine;
    @javax.inject.Inject
    private Entitymanager services;
    
    private String nomResponsable, observacion, rutaFotoSel, tipoFoto, idFoto;
    private Long idInspeccion, idTramite;
    private CmisUtil alfrescoUtils;
    private HistoricoTramites ht;
    private PeInspeccionFinal inspeccion;
    private List<Attachment> listaArchivos;
    private StreamedContent f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, fotoSel;
    private Folder carpetaPadre;
    private ContentStream is1, is2, is3, is4, is5, is6, is7, is8, is9, is10, is11, is12;
    private HashMap<String, String> datos;
    private Integer pos;
    
    private Integer cont1=0, cont2=0, cont3=0, cont4=0;
    
    
    public void initView() throws IOException{
        try{
            this.borrarDatos();
            ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteByIdNew, new String[]{"id"}, new Object[]{idTramite});
            observacion = ht.getObservacion();
            datos = new HashMap<String, String>();
            if(observacion!=null && observacion.equals("Trámite migrado"))
                this.cargarFotosMigrados();
            else
                this.cargarFotos(ht);
        }catch(Exception e){
            LOG.log(Level.SEVERE, "Error", e);
        }
    }
    
    public void borrarDatos(){
        observacion = null;
        alfrescoUtils = null;
        ht = null;
        listaArchivos = null;
        f1 = f2 = f3 = f4 = f5 = f6 = f7 = f8 = f9 = f10 = f11 = f12 = null;
        is1 = is2 = is3 = is4 = is5 = is6 = is7 = is8 = is9 = is10 = is11 = is12 = null;
        cont1=cont2=cont3=cont4=0;
        datos = null;
    }
    
    public void borrarTodo(){
        this.borrarDatos();
        nomResponsable = null;
        idInspeccion = idTramite = null;
        inspeccion = null;
    }
    
    public void cargarFotos(HistoricoTramites ht) throws IOException{
        List<HistoricoArchivo> archivos = services.findAll(Querys.getHistoricoArchivosList, new String[]{"tramiteId", "carpeta"}, new Object[]{ht.getIdTramite(), "fotosInspeccion-"+ht.getId()});
        HistoricoArchivo temp;
        alfrescoUtils = (CmisUtil) ApplicationContextUtils.getBean("cmisUtil");
        
        if (archivos == null) {
            JsfUti.messageInfo(null, "Info", "La inspección no tiene fotos");
            return;
        }else {
            if(archivos.size()>0)
                temp = archivos.get(archivos.size() - 1);
            else{
                JsfUti.messageInfo(null, "Info", "La inspección no tiene fotos");
                return;
            }
        }
        carpetaPadre = alfrescoUtils.getFolder(temp.getCarpetaContenedora());
        
        this.llenarImagenes();
    }
    
    

    public void llenarImagenes() throws IOException {
        InputStream is = null;
        int centinela;
        String url;
        String s[] = null;
        cont1=0; cont2=0; cont3=0; cont4=0;
        
        try{
            if(carpetaPadre==null){
                JsfUti.messageInfo(null, "Info", "La inspección no tiene fotos que editar");
                return;
            }
            ItemIterable<CmisObject> it = carpetaPadre.getChildren();
            for(CmisObject temp : it){
                if(temp.getName().contains("Fachada_Posterior")){
                    switch(cont1){ 
                        case 0:

                            is1 = alfrescoUtils.getDocument(temp.getId());
                            f1 = new DefaultStreamedContent(is1.getStream());
                            break;
                        case 1:
                            is2 = alfrescoUtils.getDocument(temp.getId());
                            f2 = new DefaultStreamedContent(is2.getStream());
                            break;
                        case 2:
                            is3 = alfrescoUtils.getDocument(temp.getId());
                            f3 = new DefaultStreamedContent(is3.getStream());
                            break;
                    }
                    cont1++;
                    datos.put(cont1+"", temp.getId());
                }
                if(temp.getName().contains("Fachada_Frontal")){
                    switch(cont2){
                        case 0:
                            is4 = alfrescoUtils.getDocument(temp.getId());
                            f4 = new DefaultStreamedContent(is4.getStream());
                            break;
                        case 1:
                            is5 = alfrescoUtils.getDocument(temp.getId());
                            f5 = new DefaultStreamedContent(is5.getStream());
                            break;
                        case 2:
                            is6 = alfrescoUtils.getDocument(temp.getId());
                            f6 = new DefaultStreamedContent(is6.getStream());
                            break;
                    }
                    cont2++;
                    datos.put(cont2+3+"", temp.getId());
                }
                if(temp.getName().contains("Fachada_Izquierda")){
                    switch(cont3){
                        case 0:
                            is7 = alfrescoUtils.getDocument(temp.getId());
                            f7 = new DefaultStreamedContent(is7.getStream());
                            break;
                        case 1:
                            is8 = alfrescoUtils.getDocument(temp.getId());
                            f8 = new DefaultStreamedContent(is8.getStream());
                            break;
                        case 2:
                            is9 = alfrescoUtils.getDocument(temp.getId());
                            f9 = new DefaultStreamedContent(is9.getStream());
                            break;
                    }
                    cont3++;
                    datos.put(cont3+6+"", temp.getId());
                }
                if(temp.getName().contains("Fachada_Derecha")){
                    switch(cont4){
                        case 0:
                            is10 = alfrescoUtils.getDocument(temp.getId());
                            f10 = new DefaultStreamedContent(is10.getStream());
                            break;
                        case 1:
                            is11 = alfrescoUtils.getDocument(temp.getId());
                            f11 = new DefaultStreamedContent(is11.getStream());
                            break;
                        case 2:
                            is12 = alfrescoUtils.getDocument(temp.getId());
                            f12 = new DefaultStreamedContent(is12.getStream());
                            break;
                    }
                    cont4++;
                    datos.put(cont4+9+"", temp.getId());
                }
            }
            if(cont1+cont2+cont3+cont4 == 0){
                JsfUti.messageInfo(null, "Info", "La inpección final no tiene fotos");
            }
            /*
            for (Attachment att : listaArchivos) {
                url = att.getUrl();
                s = url.split("nodeRef=");
                
                if (att.getDescription().equals("tipo1")) {
                    switch(cont1){ 
                        case 0:
                            is1 = alfrescoUtils.getDocument(s[1]);
                            f1 = new DefaultStreamedContent(is1.getStream());
                            break;
                        case 1:
                            is2 = alfrescoUtils.getDocument(s[1]);
                            f2 = new DefaultStreamedContent(is2.getStream());
                            break;
                        case 2:
                            is3 = alfrescoUtils.getDocument(s[1]);
                            f3 = new DefaultStreamedContent(is3.getStream());
                            break;
                    }
                    cont1++;
                    datos.put(cont1+"", s[1]);
                }
                if (att.getDescription().equals("tipo2")) {
                    switch(cont2){
                        case 0:
                            is4 = alfrescoUtils.getDocument(s[1]);
                            f4 = new DefaultStreamedContent(is4.getStream());
                            break;
                        case 1:
                            is5 = alfrescoUtils.getDocument(s[1]);
                            f5 = new DefaultStreamedContent(is5.getStream());
                            break;
                        case 2:
                            is6 = alfrescoUtils.getDocument(s[1]);
                            f6 = new DefaultStreamedContent(is6.getStream());
                            break;
                    }
                    cont2++;
                    datos.put(cont2+3+"", s[1]);
                }
                if (att.getDescription().equals("tipo3")) {
                    switch(cont3){
                        case 0:
                            is7 = alfrescoUtils.getDocument(s[1]);
                            f7 = new DefaultStreamedContent(is7.getStream());
                            break;
                        case 1:
                            is8 = alfrescoUtils.getDocument(s[1]);
                            f8 = new DefaultStreamedContent(is8.getStream());
                            break;
                        case 2:
                            is9 = alfrescoUtils.getDocument(s[1]);
                            f9 = new DefaultStreamedContent(is9.getStream());
                            break;
                    }
                    cont3++;
                    datos.put(cont3+6+"", s[1]);
                }
                if (att.getDescription().equals("tipo4")) {
                    switch(cont4){
                        case 0:
                            is10 = alfrescoUtils.getDocument(s[1]);
                            f10 = new DefaultStreamedContent(is10.getStream());
                            break;
                        case 1:
                            is11 = alfrescoUtils.getDocument(s[1]);
                            f11 = new DefaultStreamedContent(is11.getStream());
                            break;
                        case 2:
                            is12 = alfrescoUtils.getDocument(s[1]);
                            f12 = new DefaultStreamedContent(is12.getStream());
                            break;
                    }
                    cont4++;
                    datos.put(cont4+9+"", s[1]);
                }
            }
            if(cont1+cont2+cont3+cont4 == 0){
                JsfUti.messageInfo(null, "Info", "La inpección final no tiene fotos");
            }*/
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
        }finally{
            alfrescoUtils = null;
        }

    }
    
    public void cargarFotosMigrados() throws IOException {
        
        int cont=0;
        ContentStream doc;
        HistoricoArchivo ha;
        //f1 = f2 = f3 = f4 = f5 = f6 = f7 = f8 = f9 = f10 = f11 = f12 = new DefaultStreamedContent();
        
        try{
            alfrescoUtils = (CmisUtil) ApplicationContextUtils.getBean("cmisUtil");
            carpetaPadre = alfrescoUtils.getFolder("migracion-"+idInspeccion);
            if(carpetaPadre==null){
                JsfUti.messageInfo(null, "Info", "La inspección no tiene fotos que editar");
                return;
            }
            ItemIterable<CmisObject> it = carpetaPadre.getChildren();
            for(CmisObject temp : it){
                ha = (HistoricoArchivo) services.find(Querys.getHistoricoArchivoByArchivoId, new String[]{"idArchivo","carpeta"}, new Object[]{temp.getId(), "migracion-"+idInspeccion});
                if(ha.getEstado()){
                    if(temp.getName().contains("Fachada_Posterior")){
                        switch(cont1){ 
                            case 0:
                                
                                is1 = alfrescoUtils.getDocument(temp.getId());
                                f1 = new DefaultStreamedContent(is1.getStream());
                                break;
                            case 1:
                                is2 = alfrescoUtils.getDocument(temp.getId());
                                f2 = new DefaultStreamedContent(is2.getStream());
                                break;
                            case 2:
                                is3 = alfrescoUtils.getDocument(temp.getId());
                                f3 = new DefaultStreamedContent(is3.getStream());
                                break;
                        }
                        cont1++;
                        datos.put(cont1+"", temp.getId());
                    }
                    if(temp.getName().contains("Fachada_Frontal")){
                        switch(cont2){
                            case 0:
                                is4 = alfrescoUtils.getDocument(temp.getId());
                                f4 = new DefaultStreamedContent(is4.getStream());
                                break;
                            case 1:
                                is5 = alfrescoUtils.getDocument(temp.getId());
                                f5 = new DefaultStreamedContent(is5.getStream());
                                break;
                            case 2:
                                is6 = alfrescoUtils.getDocument(temp.getId());
                                f6 = new DefaultStreamedContent(is6.getStream());
                                break;
                        }
                        cont2++;
                        datos.put(cont2+3+"", temp.getId());
                    }
                    if(temp.getName().contains("Fachada_Izquierda")){
                        switch(cont3){
                            case 0:
                                is7 = alfrescoUtils.getDocument(temp.getId());
                                f7 = new DefaultStreamedContent(is7.getStream());
                                break;
                            case 1:
                                is8 = alfrescoUtils.getDocument(temp.getId());
                                f8 = new DefaultStreamedContent(is8.getStream());
                                break;
                            case 2:
                                is9 = alfrescoUtils.getDocument(temp.getId());
                                f9 = new DefaultStreamedContent(is9.getStream());
                                break;
                        }
                        cont3++;
                        datos.put(cont3+6+"", temp.getId());
                    }
                    if(temp.getName().contains("Fachada_Derecha")){
                        switch(cont4){
                            case 0:
                                is10 = alfrescoUtils.getDocument(temp.getId());
                                f10 = new DefaultStreamedContent(is10.getStream());
                                break;
                            case 1:
                                is11 = alfrescoUtils.getDocument(temp.getId());
                                f11 = new DefaultStreamedContent(is11.getStream());
                                break;
                            case 2:
                                is12 = alfrescoUtils.getDocument(temp.getId());
                                f12 = new DefaultStreamedContent(is12.getStream());
                                break;
                        }
                        cont4++;
                        datos.put(cont4+9+"", temp.getId());
                    }                    
                }
            }
            if(cont1+cont2+cont3+cont4 == 0){
                JsfUti.messageInfo(null, "Info", "La inpección final no tiene fotos");
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            alfrescoUtils = null;
        }
    }
    
    public void handleFileUpload(FileUploadEvent event) throws IOException { 
        try{
            InputStream buffer = event.getFile().getInputstream();
            HistoricoArchivo ha = new HistoricoArchivo();
            alfrescoUtils = (CmisUtil) ApplicationContextUtils.getBean("cmisUtil");
            Document temp;
            //byte[] buffer=  new byte[(int)event.getFile().getSize()];  
            
            switch(tipoFoto){
                case "Fachada_Posterior":
                    cont1++;
                    if(cont1==1){ 
                        temp = alfrescoUtils.createDocument(carpetaPadre, "Fachada_Posterior1", "image/jpeg", IOUtils.toByteArray(buffer));
                        ha.setIdArchivo(temp.getId());
                    }
                    if(cont1==2){
                        temp = alfrescoUtils.createDocument(carpetaPadre, "Fachada_Posterior2", "image/jpeg", IOUtils.toByteArray(buffer));
                        ha.setIdArchivo(temp.getId());
                    }
                    if(cont1==3){
                        temp = alfrescoUtils.createDocument(carpetaPadre, "Fachada_Posterior3", "image/jpeg", IOUtils.toByteArray(buffer));
                        ha.setIdArchivo(temp.getId());
                    }
                    break;
                case "Fachada_Frontal":
                    cont2++;
                    if(cont2==1){
                        temp = alfrescoUtils.createDocument(carpetaPadre, "Fachada_Frontal1", "image/jpeg", IOUtils.toByteArray(buffer));
                        ha.setIdArchivo(temp.getId());
                    }
                    if(cont2==2){
                        temp = alfrescoUtils.createDocument(carpetaPadre, "Fachada_Frontal2", "image/jpeg", IOUtils.toByteArray(buffer));
                        ha.setIdArchivo(temp.getId());
                    }
                    if(cont2==3){
                        temp = alfrescoUtils.createDocument(carpetaPadre, "Fachada_Frontal3", "image/jpeg", IOUtils.toByteArray(buffer));
                        ha.setIdArchivo(temp.getId());
                    }
                    break;
                case "Fachada_Izquierda":
                    cont3++;
                    if(cont3==1){
                        temp = alfrescoUtils.createDocument(carpetaPadre, "Fachada_Izquierda1", "image/jpeg", IOUtils.toByteArray(buffer));
                        ha.setIdArchivo(temp.getId());
                    }
                    if(cont3==2){
                        temp = alfrescoUtils.createDocument(carpetaPadre, "Fachada_Izquierda2", "image/jpeg", IOUtils.toByteArray(buffer));
                        ha.setIdArchivo(temp.getId());
                    }
                    if(cont3==3){
                        temp = alfrescoUtils.createDocument(carpetaPadre, "Fachada_Izquierda3", "image/jpeg", IOUtils.toByteArray(buffer));
                        ha.setIdArchivo(temp.getId());
                    }
                    break;
                case "Fachada_Derecha":
                    cont4++;
                    if(cont4==1){
                        temp = alfrescoUtils.createDocument(carpetaPadre, "Fachada_Derecha1", "image/jpeg", IOUtils.toByteArray(buffer));
                        ha.setIdArchivo(temp.getId());
                    }
                    if(cont4==2){
                        temp = alfrescoUtils.createDocument(carpetaPadre, "Fachada_Derecha2", "image/jpeg", IOUtils.toByteArray(buffer));
                        ha.setIdArchivo(temp.getId());
                    }
                    if(cont4==3){
                        temp = alfrescoUtils.createDocument(carpetaPadre, "Fachada_Derecha3", "image/jpeg", IOUtils.toByteArray(buffer));
                        ha.setIdArchivo(temp.getId());
                    }
                    break;
            }            
            ha.setCarpetaContenedora("migracion-"+idInspeccion);
            ha.setEstado(Boolean.TRUE);
            ha.setFechaCreacion(new Date());
            services.persist(ha);
            this.borrarDatos();
            this.initView();
            JsfUti.redirectFaces("/vistaprocesos/edificaciones/inspeccionFinal/editarFotosInspeccion.xhtml");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            alfrescoUtils = null;
        }
    }
    
    public void handleFileUploadEdit(FileUploadEvent event) throws IOException { 
        HistoricoArchivo ha;
        ha = (HistoricoArchivo) services.find(Querys.getHistoricoArchivoByArchivoId, new String[]{"idArchivo","carpeta"}, new Object[]{idFoto, "migracion-"+idInspeccion});
        try{
               
            byte[] buffer=  new byte[(int)event.getFile().getSize()];  
            
            alfrescoUtils = (CmisUtil) ApplicationContextUtils.getBean("cmisUtil");
            alfrescoUtils.deleteDocument(idFoto);
            
            switch(tipoFoto){
                case "Fachada_Frontal":
                    if(pos==1){
                        ha.setIdArchivo(alfrescoUtils.createDocument(carpetaPadre, "Fachada_Frontal1", "image/jpeg", buffer).getId());
                    }
                    if(pos==2){
                        ha.setIdArchivo(alfrescoUtils.createDocument(carpetaPadre, "Fachada_Frontal2", "image/jpeg", buffer).getId());
                    }
                    if(pos==3){
                        ha.setIdArchivo(alfrescoUtils.createDocument(carpetaPadre, "Fachada_Frontal3", "image/jpeg", buffer).getId());
                    }
                    break;
                case "Fachada_Derecha":
                    if(pos==1){
                        ha.setIdArchivo(alfrescoUtils.createDocument(carpetaPadre, "Fachada_Derecha1", "image/jpeg", buffer).getId());
                    }
                    if(pos==2){
                        ha.setIdArchivo(alfrescoUtils.createDocument(carpetaPadre, "Fachada_Derecha2", "image/jpeg", buffer).getId());
                    }
                    if(pos==3){
                        ha.setIdArchivo(alfrescoUtils.createDocument(carpetaPadre, "Fachada_Derecha3", "image/jpeg", buffer).getId());
                    }
                    break;
                case "Fachada_Izquierda":
                    if(pos==1){
                        ha.setIdArchivo(alfrescoUtils.createDocument(carpetaPadre, "Fachada_Izquierda1", "image/jpeg", buffer).getId());
                    }
                    if(pos==2){
                        ha.setIdArchivo(alfrescoUtils.createDocument(carpetaPadre, "Fachada_Izquierda2", "image/jpeg", buffer).getId());
                    }
                    if(pos==3){
                        ha.setIdArchivo(alfrescoUtils.createDocument(carpetaPadre, "Fachada_Izquierda3", "image/jpeg", buffer).getId());
                    }
                    break;
                case "Fachada_Posterior":
                    if(pos==1){
                        ha.setIdArchivo(alfrescoUtils.createDocument(carpetaPadre, "Fachada_Posterior1", "image/jpeg", buffer).getId());
                    }
                    if(pos==2){
                        ha.setIdArchivo(alfrescoUtils.createDocument(carpetaPadre, "Fachada_Posterior2", "image/jpeg", buffer).getId());
                    }
                    if(pos==3){
                        ha.setIdArchivo(alfrescoUtils.createDocument(carpetaPadre, "Fachada_Posterior3", "image/jpeg", buffer).getId());
                    }
                    break;                   
            }
            ha.setFechaCreacion(new Date());
            services.update(ha);
            this.borrarDatos();
            this.initView();
            JsfUti.redirectFaces("/vistaprocesos/edificaciones/inspeccionFinal/editarFotosInspeccion.xhtml");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            alfrescoUtils = null;
        }
    }
    
    public void editFachadaFrontal(int i){
        pos = i;
        tipoFoto = "Fachada_Frontal";
        idFoto = (String) datos.remove((i+3)+"");        
    }
    
    public void editFachadaPosterior(int i){
        pos = i;
        tipoFoto = "Fachada_Posterior";
        idFoto = (String) datos.remove(i+"");
    }
    
    public void editFachadaIzquierda(int i){
        pos = i;
        tipoFoto = "Fachada_Izquierda";
        idFoto = (String) datos.remove((i+6)+"");
    }
    
    public void editFachadaDerecha(int i){
        pos = i;
        tipoFoto = "Fachada_Derecha";
        idFoto = (String) datos.remove((i+9)+"");   
    }
    
    public void eliminarFachadaPosterior(int i){
        try{
            alfrescoUtils = (CmisUtil) ApplicationContextUtils.getBean("cmisUtil");
            String id;
            tipoFoto = "Fachada_Posterior";
            id = (String) datos.remove(i+"");
            HistoricoArchivo ha;
            ha = (HistoricoArchivo) services.find(Querys.getHistoricoArchivoByArchivoId, new String[]{"idArchivo","carpeta"}, new Object[]{id, "migracion-"+idInspeccion});
            if(ha!=null){
                ha.setEstado(Boolean.FALSE);
                services.update(ha);
            }
            alfrescoUtils.deleteDocument(id);
            this.borrarDatos();
            this.initView();
            JsfUti.redirectFaces("/vistaprocesos/edificaciones/inspeccionFinal/editarFotosInspeccion.xhtml");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            alfrescoUtils = null;
        }
    }
    
    public void eliminarFachadaFrontal(int i){
        try{
            alfrescoUtils = (CmisUtil) ApplicationContextUtils.getBean("cmisUtil");
            String id;
            tipoFoto = "Fachada_Frontal";
            id = (String) datos.remove(i+3+"");
            HistoricoArchivo ha;
            ha = (HistoricoArchivo) services.find(Querys.getHistoricoArchivoByArchivoId, new String[]{"idArchivo","carpeta"}, new Object[]{id, "migracion-"+idInspeccion});
            if(ha!=null){
                ha.setEstado(Boolean.FALSE);
                services.update(ha);
            }
            alfrescoUtils.deleteDocument(id);
            this.borrarDatos();
            this.initView();
            JsfUti.redirectFaces("/vistaprocesos/edificaciones/inspeccionFinal/editarFotosInspeccion.xhtml");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            alfrescoUtils = null;
        }
    }
    
    public void eliminarFachadaIzquierda(int i){
        try{
            alfrescoUtils = (CmisUtil) ApplicationContextUtils.getBean("cmisUtil");
            String id;
            tipoFoto = "Fachada_Izquierda";
            id = (String) datos.remove(i+6+"");
            HistoricoArchivo ha;
            ha = (HistoricoArchivo) services.find(Querys.getHistoricoArchivoByArchivoId, new String[]{"idArchivo","carpeta"}, new Object[]{id, "migracion-"+idInspeccion});
            if(ha!=null){
                ha.setEstado(Boolean.FALSE);
                services.update(ha);
            }
            alfrescoUtils.deleteDocument(id);
            this.borrarDatos();
            this.initView();
            JsfUti.redirectFaces("/vistaprocesos/edificaciones/inspeccionFinal/editarFotosInspeccion.xhtml");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            alfrescoUtils = null;
        }
    }
    
    public void eliminarFachadaDerecha(int i){
        try{
            alfrescoUtils = (CmisUtil) ApplicationContextUtils.getBean("cmisUtil");
            String id;
            tipoFoto = "Fachada_Derecha";
            id = (String) datos.remove(i+9+"");
            HistoricoArchivo ha;
            ha = (HistoricoArchivo) services.find(Querys.getHistoricoArchivoByArchivoId, new String[]{"idArchivo","carpeta"}, new Object[]{id, "migracion-"+idInspeccion});
            if(ha!=null){
                ha.setEstado(Boolean.FALSE);
                services.update(ha);
            }
            alfrescoUtils.deleteDocument(id);
            this.borrarDatos();
            this.initView();
            JsfUti.redirectFaces("/vistaprocesos/edificaciones/inspeccionFinal/editarFotosInspeccion.xhtml");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            alfrescoUtils = null;
        }
        
    }
    
    public void subirFachadaFrontal(){
        if(cont2<3){
            tipoFoto = "Fachada_Frontal";
            JsfUti.update("frmNewFoto");
            JsfUti.executeJS("PF('nuevoDlgfoto').show()");
        }else{
            JsfUti.messageInfo(null, "Info", "No se puede seguir agregando imágenes");
        }
    }
    
    public void subirFachadaPosterior(){
        if(cont1<3){
            tipoFoto = "Fachada_Posterior";
            JsfUti.update("frmNewFoto");
            JsfUti.executeJS("PF('nuevoDlgfoto').show()");
        }else{
            JsfUti.messageInfo(null, "Info", "No se puede seguir agregando imágenes");
        }
    }
    
    public void subirFachadaIzquierda(){
        if(cont3<3){
            tipoFoto = "Fachada_Izquierda";
            JsfUti.update("frmNewFoto");
            JsfUti.executeJS("PF('nuevoDlgfoto').show()");
        }else{
            JsfUti.messageInfo(null, "Info", "No se puede seguir agregando imágenes");
        }
    }
    
    public void subirFachadaDerecha(){
        if(cont4<3){
            tipoFoto = "Fachada_Derecha";
            JsfUti.update("frmNewFoto");
            JsfUti.executeJS("PF('nuevoDlgfoto').show()");
        }else{
            JsfUti.messageInfo(null, "Info", "No se puede seguir agregando imágenes");
        }
    }

    public Integer getCont1() {
        return cont1+1;
    }

    public void setCont1(Integer cont1) {
        this.cont1 = cont1;
    }

    public Integer getCont2() {
        return cont2+1;
    }

    public void setCont2(Integer cont2) {
        this.cont2 = cont2;
    }

    public Integer getCont3() {
        return cont3+1;
    }

    public void setCont3(Integer cont3) {
        this.cont3 = cont3;
    }

    public Integer getCont4() {
        return cont4+1;
    }

    public void setCont4(Integer cont4) {
        this.cont4 = cont4;
    }

    public String getNomResponsable() {
        return nomResponsable;
    }

    public void setNomResponsable(String nomResponsable) {
        this.nomResponsable = nomResponsable;
    }

    public StreamedContent getF1() {
        return f1;
    }

    public void setF1(StreamedContent f1) {
        this.f1 = f1;
    }

    public StreamedContent getF2() {
        return f2;
    }

    public void setF2(StreamedContent f2) {
        this.f2 = f2;
    }

    public StreamedContent getF3() {
        return f3;
    }

    public void setF3(StreamedContent f3) {
        this.f3 = f3;
    }

    public StreamedContent getF4() {
        return f4;
    }

    public void setF4(StreamedContent f4) {
        this.f4 = f4;
    }

    public StreamedContent getF5() {
        return f5;
    }

    public void setF5(StreamedContent f5) {
        this.f5 = f5;
    }

    public StreamedContent getF6() {
        return f6;
    }

    public void setF6(StreamedContent f6) {
        this.f6 = f6;
    }

    public StreamedContent getF7() {
        return f7;
    }

    public void setF7(StreamedContent f7) {
        this.f7 = f7;
    }

    public StreamedContent getF8() {
        return f8;
    }

    public void setF8(StreamedContent f8) {
        this.f8 = f8;
    }

    public StreamedContent getF9() {
        return f9;
    }

    public void setF9(StreamedContent f9) {
        this.f9 = f9;
    }

    public StreamedContent getF10() {
        return f10;
    }

    public void setF10(StreamedContent f10) {
        this.f10 = f10;
    }

    public StreamedContent getF11() {
        return f11;
    }

    public void setF11(StreamedContent f11) {
        this.f11 = f11;
    }

    public StreamedContent getF12() {
        return f12;
    }

    public void setF12(StreamedContent f12) {
        this.f12 = f12;
    }

    public String getRutaFotoSel() {
        return rutaFotoSel;
    }

    public void setRutaFotoSel(String rutaFotoSel) {
        this.rutaFotoSel = rutaFotoSel;
    }

    public StreamedContent getFotoSel() {
        return fotoSel;
    }

    public void setFotoSel(StreamedContent fotoSel) {
        this.fotoSel = fotoSel;
    }

    public Long getIdInspeccion() {
        return idInspeccion;
    }

    public void setIdInspeccion(Long idInspeccion) {
        this.idInspeccion = idInspeccion;
    }

    public Long getIdTramite() {
        return idTramite;
    }

    public void setIdTramite(Long idTramite) {
        this.idTramite = idTramite;
    }

    public String getTipoFoto() {
        return tipoFoto;
    }

    public void setTipoFoto(String tipoFoto) {
        this.tipoFoto = tipoFoto;
    }

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }
    
}
