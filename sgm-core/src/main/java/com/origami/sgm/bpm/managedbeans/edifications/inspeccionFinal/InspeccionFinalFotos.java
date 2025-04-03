/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.inspeccionFinal;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBase;
import com.origami.sgm.bpm.models.FotosInspeccionMigracion;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.HistoricoArchivo;
import com.origami.sgm.entities.PeInspeccionFinal;
import com.origami.sgm.lazymodels.PeInspeccionFinalMigradoLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.hibernate.SQLQuery;
import util.ApplicationContextUtils;
import util.CmisUtil;
import util.JsfUti;
import utils.ejb.interfaces.DatabaseLocal;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class InspeccionFinalFotos implements Serializable {
    public static final Long serialVersionUID = 1L;
    @javax.inject.Inject
    private Entitymanager services;
    @Inject
    protected BpmManageBeanBase manageBeanBase;
    @javax.inject.Inject
    private DatabaseLocal ds;
    
    private Connection conn, conn2;
    private ComboPooledDataSource dsc;
    private PreparedStatement ps;
    private ResultSet rs;
    private Map parametros;
    private SQLQuery query;
    private PeInspeccionFinalMigradoLazy inspeccionlazy;
    private CmisUtil alfrescoUtils;
    
    @PostConstruct
    public void initView() {
        inspeccionlazy = new PeInspeccionFinalMigradoLazy();
        //inspecciones = services.findAll(Querys.getPeInspeccionList, new String[]{}, new Object[]{});
        //path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        //firmaDir = new AclUser();
    }
    
    public void migrarFotos(PeInspeccionFinal inspeccion) throws SQLException{
        Long idIns = inspeccion.getId();
        HistoricoArchivo ha;
        BigInteger idInspeccionAnt;
        FotosInspeccionMigracion foto;
        Document doc;
        Integer cont1, cont2, cont3, cont4;
        Folder carpetaPadre, carpetaContenedora = null;
        int cont = 0;
        
        try {
            cont1 = cont2 = cont3 = cont4 = 1;
            alfrescoUtils = (CmisUtil) ApplicationContextUtils.getBean("cmisUtil");
            conn = ds.getDbOldDataSource().getConnection();
            
            query = services.getSession().createSQLQuery("Select inspeccion_ant from migracion_permisos.inspecciones where inspeccion_new = " + inspeccion.getId());
            idInspeccionAnt = (BigInteger) query.uniqueResult();
                        
            if (conn != null) {
                ps = conn.prepareStatement(Querys.getPeInspeccionFotosByInspeccion);
                ps.setLong(1, idInspeccionAnt.longValue());
                rs = ps.executeQuery();
                if(alfrescoUtils!=null){
                    carpetaPadre = alfrescoUtils.getFolder("fotosInspeccionMigracion");
                    if(carpetaPadre!=null){
                        if(alfrescoUtils.getFolder("migracion-"+idIns)!=null){
                            JsfUti.messageError(null, "Error", "La inspección final ya tiene fotos migradas.");
                            return;
                        }else{
                            carpetaContenedora = alfrescoUtils.createFolder(carpetaPadre, "migracion-"+idIns);    
                        }
                    }
                }else{
                    JsfUti.messageInfo(null, "Info", "Se produjo un error en el sistema. Vuelva a intentarlo.");
                    return;
                }
                while (rs.next()) {
                    foto = new FotosInspeccionMigracion();
                    ha = new HistoricoArchivo();
                    if (rs.getString(1) != null) {
                        if(rs.getString(1).toLowerCase().contains("frontal")){
                            foto.setImagen_nombre("Fachada_Frontal"+cont1);
                            cont1 = cont1 + 1;
                        }
                        if(rs.getString(1).toLowerCase().contains("izquierda")){
                            foto.setImagen_nombre("Fachada_Izquierda"+cont2);
                            cont2 = cont2 + 1;
                        }
                        if(rs.getString(1).toLowerCase().contains("derecha")){
                            foto.setImagen_nombre("Fachada_Derecha"+cont3);
                            cont3 = cont3 + 1;
                        }
                        if(rs.getString(1).toLowerCase().contains("posterior")){
                            foto.setImagen_nombre("Fachada_Posterior"+cont4);
                            cont4 = cont4 + 1;
                        }
                    }
                    if (rs.getBytes(2) != null) {
                        foto.setImagen(rs.getBytes(2));
                    }
                    if (rs.getString(3) != null) {
                        foto.setIdInspeccion(Long.valueOf(rs.getString(3)));
                    }
                    cont++;
                    doc = alfrescoUtils.createDocument(carpetaContenedora, foto.getImagen_nombre(), "image/jpeg", foto.getImagen());
                    
                    ha.setCarpetaContenedora("migracion-"+idIns);
                    ha.setIdArchivo(doc.getId());
                    ha.setEstado(Boolean.TRUE);
                    ha.setFechaCreacion(new Date());
                    services.persist(ha);
                }
                JsfUti.messageInfo(null, "Info", "Se subieron: "+cont+" fotos");
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            rs.close();
            ps.close();
            conn.close();
            alfrescoUtils = null;
        }
    }

    public PeInspeccionFinalMigradoLazy getInspeccionlazy() {
        return inspeccionlazy;
    }

    public void setInspeccionlazy(PeInspeccionFinalMigradoLazy inspeccionlazy) {
        this.inspeccionlazy = inspeccionlazy;
    }

    public BpmManageBeanBase getManageBeanBase() {
        return manageBeanBase;
    }

    public void setManageBeanBase(BpmManageBeanBase manageBeanBase) {
        this.manageBeanBase = manageBeanBase;
    }
    
    
}
