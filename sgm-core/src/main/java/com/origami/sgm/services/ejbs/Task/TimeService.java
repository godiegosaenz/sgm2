/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.Task;

import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.RenPermisosFuncionamientoLocalComercial;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.NormasConstruccionServices;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import util.EmailUtil;

/**
 *
 * @author Joao Sanga
 */
@Singleton
@Lock(LockType.READ)
public class TimeService {
    
    @javax.inject.Inject
    private Entitymanager services;
    
    /*@Schedule(minute="30", hour="7", persistent=false)
    public void doWork(){
        notificarUsuarioPermisoLocal();
    }*/
    
    public void notificarUsuarioPermisoLocal(){
        try{
            System.out.println("Notificaciones: "+new Date());
            MsgFormatoNotificacion msg;
            msg = (MsgFormatoNotificacion)services.find(MsgFormatoNotificacion.class, 4L);
            
            List<CatEnte> propietarios;
            EmailUtil mail = new EmailUtil();;
            CatEnte propietario;
            Calendar calendar = Calendar.getInstance(); // this would default to now
            calendar.add(Calendar.DAY_OF_MONTH, -7);
            Date fecha = calendar.getTime();
            
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            propietarios = (List<CatEnte>)services.findAll(QuerysFinanciero.getFechasCaducidadPermisosLC, new String[]{"hoy"}, new Object[]{calendar.getTime()});
            String mensaje = msg.getHeader()
                    + "Tiene 7 días antes de que su permiso de funcionamiento caduque."
                    + msg.getFooter();
            
            for(CatEnte temp : propietarios){
                mail.sendEmail(temp.getEmails(), "Notificación permiso de funcionamiento", mensaje, null, null);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
