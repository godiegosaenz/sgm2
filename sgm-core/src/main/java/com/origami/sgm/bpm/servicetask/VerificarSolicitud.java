/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.servicetask;

import com.origami.sgm.bpm.models.DepartamentoSolicitudServicio;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.SvSolicitudDepartamento;
import com.origami.sgm.entities.SvSolicitudServicios;
import com.origami.sgm.services.interfaces.solicitudServico.SolicitudServicosServices;
import com.origami.sgm.util.EjbsCaller;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 *
 * @author Henry Pilco
 *
 * Verificar la respuesta de los departamentos a los cuales se le asigno la
 * atencion de la Solicitud.
 */
public class VerificarSolicitud implements JavaDelegate {

    @Override
    public void execute(DelegateExecution de) throws Exception {
        try {
            SolicitudServicosServices service = EjbsCaller.getSolicitudServicosServices();
            Boolean rechazar = Boolean.FALSE;
            HistoricoTramites historicoTramites = service.getPropiedadHorizontalServices().getPermiso().getHistoricoTramiteById((Long) de.getVariable("tramite"));
            SvSolicitudServicios solicitud = service.getSolicitudServicioByTramite(historicoTramites.getIdTramite());
            // RECORER LOS DEPARTAMENTOS A LOS CUALES FUERON ASIGNADA LA SOLICITUD Y VERIFICAR LA ACCION SETEAR TRUE EN RECHAR SI UNA ES 3
            List<DepartamentoSolicitudServicio> departamentosModel = (List<DepartamentoSolicitudServicio>) de.getVariable("departamentos");
            if (departamentosModel != null) {
                for (DepartamentoSolicitudServicio dm : departamentosModel) {
                    // Se compara con 3 debido que es la opcion cuando una solicitud fue rechazada por una de las direcciones asignadas.                    
                    if (dm.getAccion() != null && dm.getAccion().equals(3L)) {
                        rechazar = Boolean.TRUE;
                        break;
                    }
                }
            }
            //Se actualiza el tramite.
            String informeSolicitud = "";
            for (SvSolicitudDepartamento depSolicitud : solicitud.getSvSolicitudDepartamentoCollection()) {
                if (!depSolicitud.getAccion().equals(3L)) {
                    informeSolicitud = informeSolicitud + "Departamento: " + depSolicitud.getDepartamento().getNombre() + "\nInforme: " + depSolicitud.getInforme() + ". \n\n";
                }
            }
            solicitud.setInforme(informeSolicitud);
            informeSolicitud = informeSolicitud.replaceAll("\n", "<br/>");
            if (!rechazar) {
                MsgFormatoNotificacion ms = service.getPropiedadHorizontalServices().getMsgFormatoNotificacionByTipo(2L);
                String mensaje = ms.getHeader() + "Notificacion Solicitud Servicios Online.<br/><br/>"
                        + "Tramite : " + historicoTramites.getId()
                        + " Su solicitud fue atendida.<br/><br/> "
                        + " El detalle de la misma se encuentra a continuacion.<br/> "
                        + informeSolicitud + "<br/><br/> "
                        + "<br/><br/>" + ms.getFooter();
                de.setVariable("to", historicoTramites.getCorreos());
                de.setVariable("subject", "Servicio Solicitado " + historicoTramites.getId());
                de.setVariable("message", mensaje);
                historicoTramites.setEstado("Finalizado");
            }

            de.setVariable("rechazar", rechazar);
            service.getPropiedadHorizontalServices().actualizarHistoricoTramites(historicoTramites);
            service.guardarOActualizarSolicitudServicos(solicitud);
        } catch (Exception e) {
            Logger.getLogger(VerificarSolicitud.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
