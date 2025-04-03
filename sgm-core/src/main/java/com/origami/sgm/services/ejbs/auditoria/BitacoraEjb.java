/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.auditoria;

import com.origami.sgm.bpm.models.MovimientoModel;
import com.origami.sgm.entities.RegBitacora;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoCliente;
import com.origami.sgm.entities.RegMovimientoReferencia;
import com.origami.sgm.enums.ActividadesTransaccionales;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.interfaces.SessionServiceLocal;
import com.origami.sgm.services.interfaces.auditoria.BitacoraServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

/**
 *
 * @author CarlosLoorVargas
 */
@Stateless(name = "bitacora")
@Interceptors(value = {HibernateEjbInterceptor.class})
@Dependent
public class BitacoraEjb implements BitacoraServices {

    @Inject
    private Entitymanager manager;

    @Inject
    private SessionServiceLocal session;

    private RegBitacora rb;

    @Override
    public Object registrarFicha(RegFicha f, ActividadesTransaccionales actividadTransaccional, BigInteger periodo, BigInteger orden) {
        BigInteger codUsu = BigInteger.valueOf(session.getSessionContext().getUserId());
        Object reg = null;
        String actividad = actividadTransaccion(actividadTransaccional, f, null, null);
        try {
            rb = new RegBitacora();
            rb.setCodUsuario(codUsu);
            rb.setIdUsuario(codUsu);
            rb.setActividad(actividad);
            rb.setFecha(new Date());
            rb.setFechaHora(new Date());
            rb.setCodPrograma("SGMReg");
            rb.setAnio(periodo);
            if (orden != null) {
                rb.setRepertorioOrdenTramite(orden);
            }
            rb.setNumFicha(new BigInteger(f.getNumFicha() + ""));
            rb.setIdFicha(new BigInteger(f.getId().toString()));
            rb.setTipServicio(new BigInteger("2"));
            reg = manager.persist(rb);
        } catch (Exception e) {
            Logger.getLogger(BitacoraEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return reg;
    }

    @Override
    public Object registrarMovimiento(MovimientoModel movimientoModel, RegMovimiento m, ActividadesTransaccionales actividadTransaccional, BigInteger periodo) {
        BigInteger codUsu = BigInteger.valueOf(session.getSessionContext().getUserId());
        Object reg = null;
        String actividad = this.actividadTransaccion(actividadTransaccional, null, movimientoModel, m);
        try {
            rb = new RegBitacora();
            rb.setCodUsuario(codUsu);
            rb.setIdUsuario(codUsu);
            rb.setActividad(actividad);
            rb.setFecha(new Date());
            rb.setFechaHora(new Date());
            rb.setCodPrograma("SGMReg");
            rb.setAnio(periodo);
            rb.setFechaInscripcion(m.getFechaInscripcion());
            rb.setIdMovimiento(new BigInteger(m.getId().toString()));
            rb.setTipServicio(new BigInteger("1"));
            rb.setIndice(new BigInteger(m.getIndice().toString()));
            rb.setNumInscripcion(new BigInteger(m.getNumInscripcion().toString()));
            rb.setRepertorioOrdenTramite(new BigInteger(m.getNumRepertorio().toString()));
            reg = manager.persist(rb);
        } catch (Exception e) {
            Logger.getLogger(BitacoraEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return reg;
    }

    @Override
    public boolean registrarFichaMov(RegFicha f, RegMovimiento mov, ActividadesTransaccionales actividadTransaccional, BigInteger periodo) {
        BigInteger codUsu = BigInteger.valueOf(session.getSessionContext().getUserId());
        boolean flag = false;
        String actividad;
        try {
            if (f != null) {
                actividad = actividadTransaccion(actividadTransaccional, f, null, mov);
                rb = new RegBitacora();
                rb.setCodUsuario(codUsu);
                rb.setIdUsuario(codUsu);
                rb.setActividad(actividad);
                rb.setFecha(new Date());
                rb.setFechaHora(new Date());
                rb.setCodPrograma("SGMReg");
                rb.setAnio(periodo);
                rb.setNumFicha(new BigInteger(f.getNumFicha() + ""));
                rb.setIdFicha(new BigInteger(f.getId().toString()));
                rb.setFechaInscripcion(mov.getFechaInscripcion());
                rb.setIdMovimiento(new BigInteger(mov.getId().toString()));
                rb.setTipServicio(new BigInteger("1"));
                rb.setIndice(new BigInteger(mov.getIndice().toString()));
                rb.setNumInscripcion(new BigInteger(mov.getNumInscripcion().toString()));
                rb.setRepertorioOrdenTramite(new BigInteger(mov.getNumRepertorio().toString()));
                manager.persist(rb);
            }
        } catch (Exception e) {
            Logger.getLogger(BitacoraEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }

    @Override
    public boolean registrarFichaMovs(RegFicha f, List<RegMovimiento> movs, ActividadesTransaccionales actividadTransaccional, BigInteger periodo) {
        BigInteger codUsu = BigInteger.valueOf(session.getSessionContext().getUserId());
        boolean flag = false;
        String actividad;
        try {
            if (f != null) {
                actividad = actividadTransaccion(actividadTransaccional, f, null, null);
                for (RegMovimiento m : movs) {
                    rb = new RegBitacora();
                    rb.setCodUsuario(codUsu);
                    rb.setIdUsuario(codUsu);
                    rb.setActividad(actividad);
                    rb.setFecha(new Date());
                    rb.setFechaHora(new Date());
                    rb.setCodPrograma("SGMReg");
                    rb.setAnio(periodo);
                    rb.setNumFicha(new BigInteger(f.getNumFicha() + ""));
                    rb.setIdFicha(new BigInteger(f.getId().toString()));
                    rb.setFechaInscripcion(m.getFechaInscripcion());
                    rb.setIdMovimiento(new BigInteger(m.getId().toString()));
                    rb.setTipServicio(new BigInteger("1"));
                    rb.setIndice(new BigInteger(m.getIndice().toString()));
                    rb.setNumInscripcion(new BigInteger(m.getNumInscripcion().toString()));
                    rb.setRepertorioOrdenTramite(new BigInteger(m.getNumRepertorio().toString()));
                    manager.persist(rb);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(BitacoraEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }

    @Override
    public boolean registrarMovFichas(RegMovimiento m, List<RegFicha> fs, ActividadesTransaccionales actividadTransaccional, BigInteger periodo) {
        BigInteger codUsu = BigInteger.valueOf(session.getSessionContext().getUserId());
        boolean flag = false;
        String actividad;
        try {
            //actividad = this.actividadTransaccion(actividadTransaccional, null, null, null);
            //MODIFICACION: 02-12-2016
            actividad = this.actividadTransaccion(actividadTransaccional, null, null, m);
            for (RegFicha f : fs) {
                rb = new RegBitacora();
                rb.setCodUsuario(codUsu);
                rb.setIdUsuario(codUsu);
                rb.setActividad(actividad);
                rb.setFecha(new Date());
                rb.setFechaHora(new Date());
                rb.setCodPrograma("SGMReg");
                rb.setAnio(periodo);
                rb.setNumFicha(new BigInteger(f.getNumFicha() + ""));
                rb.setIdFicha(new BigInteger(f.getId().toString()));
                rb.setTipServicio(new BigInteger("1"));
                rb.setFechaInscripcion(m.getFechaInscripcion());
                rb.setIdMovimiento(new BigInteger(m.getId().toString()));
                rb.setIndice(new BigInteger(m.getIndice().toString()));
                rb.setNumInscripcion(new BigInteger(m.getNumInscripcion().toString()));
                rb.setRepertorioOrdenTramite(new BigInteger(m.getNumRepertorio().toString()));
                manager.persist(rb);
            }
        } catch (Exception e) {
            Logger.getLogger(BitacoraEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }

    @Override
    public boolean registrarMovMovs(RegMovimiento m, List<RegMovimiento> movs, String actividad, BigInteger periodo) {
        BigInteger codUsu = BigInteger.valueOf(session.getSessionContext().getUserId());
        boolean flag = false;
        try {
            rb = new RegBitacora();
            rb.setCodUsuario(codUsu);
            rb.setIdUsuario(codUsu);
            rb.setActividad(actividad);
            rb.setFecha(new Date());
            rb.setFechaHora(new Date());
            rb.setCodPrograma("SGMReg");
            rb.setAnio(periodo);
            rb.setFechaInscripcion(m.getFechaInscripcion());
            rb.setIdMovimiento(new BigInteger(m.getId().toString()));
            rb.setTipServicio(new BigInteger("1"));
            rb.setIndice(new BigInteger(m.getIndice().toString()));
            rb.setNumInscripcion(new BigInteger(m.getNumInscripcion().toString()));
            rb.setRepertorioOrdenTramite(new BigInteger(m.getNumRepertorio().toString()));
            manager.persist(rb);
            for (RegMovimiento mv : movs) {
                rb = new RegBitacora();
                rb.setCodUsuario(codUsu);
                rb.setIdUsuario(codUsu);
                rb.setActividad(actividad);
                rb.setFecha(new Date());
                rb.setFechaHora(new Date());
                rb.setCodPrograma("SGMReg");
                rb.setAnio(periodo);
                rb.setFechaInscripcion(mv.getFechaInscripcion());
                rb.setIdMovimiento(new BigInteger(mv.getId().toString()));
                rb.setTipServicio(new BigInteger("1"));
                rb.setIndice(new BigInteger(mv.getIndice().toString()));
                rb.setNumInscripcion(new BigInteger(mv.getNumInscripcion().toString()));
                rb.setRepertorioOrdenTramite(new BigInteger(mv.getNumRepertorio().toString()));
                manager.persist(rb);
            }
        } catch (Exception e) {
            Logger.getLogger(BitacoraEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }

    @Override
    public String actividadTransaccion(ActividadesTransaccionales actividad, RegFicha ficha, MovimientoModel movimientoAnterior, RegMovimiento movimiento) {
        String detalleActividad = actividad.getDescripcion() + "\n";
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        try{
            switch (actividad) {
                case GENERACION_FICHA:
                    detalleActividad = detalleActividad
                            + (ficha.getCodigoPredial() == null ? "" : "Codigo: " + ficha.getCodigoPredial() + "\n")
                            + (ficha.getLinderos() == null ? "" : "Lindero: " + ficha.getLinderos() + "\n");
                    break;

                case DATOS_REGISTRALES:
                    detalleActividad = detalleActividad
                            + (ficha.getLinderos() == null ? "" : "Lindero: " + ficha.getLinderos() + "\n")
                            + (ficha.getObservacion() == null ? "" : "Observaciones: " + ficha.getObservacion() + "\n");
                    break;

                case MODIFICAR_FICHA:
                    RegFicha fichaAnterior = manager.find(RegFicha.class, ficha.getId());
                    if (!fichaAnterior.getEstado().equals(ficha.getEstado())) {
                        if (ficha.getEstado().getId().equals(134L)) {
                            detalleActividad = detalleActividad + ActividadesTransaccionales.ACTIVAR_FICHA.getDescripcion() + "\n";
                        } else {
                            detalleActividad = detalleActividad + ActividadesTransaccionales.INACTIVAR_FICHA.getDescripcion() + ficha.getEstado().getValor() + "\n";
                        }
                    }
                    if (fichaAnterior.getLinderos() != null && !fichaAnterior.getLinderos().equals(ficha.getLinderos())) {
                        detalleActividad = detalleActividad + "LINDERO ANTERIOR: " + fichaAnterior.getLinderos() + "\n";
                    }
                    if (fichaAnterior.getObservacion() != null && !fichaAnterior.getObservacion().equals(ficha.getObservacion())) {
                        detalleActividad = detalleActividad + "OBSERVACION ANTERIOR: " + fichaAnterior.getObservacion() + "\n";
                    }
                    if (fichaAnterior.getCodigoPredial() != null && !fichaAnterior.getCodigoPredial().equals(ficha.getCodigoPredial())) {
                        detalleActividad = detalleActividad + "CODIGO PREDIAL ANTERIOR: " + fichaAnterior.getCodigoPredial() + "\n";
                    }
                    break;

                case MODIFICACION_INSCRIPCION:
                    detalleActividad = detalleActividad + "DATOS ANTERIORES: \n";
                    if (!movimientoAnterior.getCodigoCan().equals(movimiento.getCodigoCan())) {
                        detalleActividad = detalleActividad + "CANTON: " + movimientoAnterior.getCodigoCan().getNombre() + "\n";
                    }
                    if (!movimientoAnterior.getNumTomo().equals(movimiento.getNumTomo())) {
                        detalleActividad = detalleActividad + "TOMO: " + movimientoAnterior.getNumTomo() + "\n";
                    }
                    if (movimientoAnterior.getActo() != null && movimiento.getActo() != null && !movimientoAnterior.getActo().getId().equals(movimiento.getActo().getId())) {
                        detalleActividad = detalleActividad + "ACTO: " + movimientoAnterior.getActo().getNombre() + "\n";
                    }
                    if (movimientoAnterior.getEnteJudicial() != null && movimiento.getEnteJudicial() != null && !movimientoAnterior.getEnteJudicial().getId().equals(movimiento.getEnteJudicial().getId())) {
                        detalleActividad = detalleActividad + "NOTARIA/JUZG: " + movimientoAnterior.getEnteJudicial().getNombre() + "\n";
                    }
                    if (movimientoAnterior.getFechaOto() != null && movimiento.getFechaOto() != null && movimientoAnterior.getFechaOto().getTime() != movimiento.getFechaOto().getTime()) {
                        detalleActividad = detalleActividad + "FECHA OTORGAMIENTO: " + fecha.format(movimientoAnterior.getFechaOto()) + "\n";
                    }
                    if (!movimientoAnterior.getFolioInicio().equals(movimiento.getFolioInicio()) || !movimientoAnterior.getFolioFin().equals(movimiento.getFolioFin())) {
                        detalleActividad = detalleActividad + "FOLIO INICIAL: " + movimientoAnterior.getFolioInicio() + " \tFOLIO FINAL: " + movimientoAnterior.getFolioFin() + "\n";
                    }
                    if (!movimientoAnterior.getOrdJud().equals(movimiento.getOrdJud())) {
                        detalleActividad = detalleActividad + "ORDEN JUDICIAL: " + movimientoAnterior.getOrdJud() + "\n";
                    }
                    if (movimientoAnterior.getEscritJuicProvResolucion() != null && !movimientoAnterior.getEscritJuicProvResolucion().equals(movimiento.getEscritJuicProvResolucion())) {
                        detalleActividad = detalleActividad + "Escrit/Juic/Provi/Resolucion: " + movimientoAnterior.getEscritJuicProvResolucion() + "\n";
                    }
                    if (movimientoAnterior.getObservacion() != null && !movimientoAnterior.getObservacion().equals(movimiento.getObservacion())) {
                        detalleActividad = detalleActividad + "OBSERVACION: " + movimientoAnterior.getObservacion() + "\n";
                    }
                    if (!movimientoAnterior.getMovRefList().isEmpty()) {
                        for (RegMovimientoReferencia mr : movimientoAnterior.getMovRefList()) {
                            if (mr.getId() == null) {
                                detalleActividad = detalleActividad + "- Se Agrega Referencia - Libro: " + mr.getMovimientoReff().getLibro().getNombre() + ", Num.Inscripcion: " + mr.getMovimientoReff().getNumInscripcion()
                                        + ", F.Inscrip.: " + fecha.format(mr.getMovimientoReff().getFechaInscripcion()) + ", Repertorio: " + mr.getMovimientoReff().getNumRepertorio() + "\n";
                            }
                        }
                    }
                    if (!movimientoAnterior.getMovRefListDel().isEmpty()) {
                        for (RegMovimientoReferencia mr : movimientoAnterior.getMovRefListDel()) {
                            detalleActividad = detalleActividad + "- Se Elimina Referencia - Libro: " + mr.getMovimientoReff().getLibro().getNombre() + ", Num.Inscripcion: " + mr.getMovimientoReff().getNumInscripcion()
                                    + ", F.Inscrip.: " + fecha.format(mr.getMovimientoReff().getFechaInscripcion()) + ", Repertorio: " + mr.getMovimientoReff().getNumRepertorio() + "\n";
                        }
                    }
                    if (movimientoAnterior.getMovCliList().isEmpty() && !movimientoAnterior.getMovCliListOld().isEmpty()) {
                        for (RegMovimientoCliente mco : movimientoAnterior.getMovCliListOld()) {
                            detalleActividad = detalleActividad + "Se Elimina Cliente: " + mco.getEnteInterv().getNombre() + ", Papel: " + mco.getPapel().getPapel() + "\n";
                        }
                    } else if (!movimientoAnterior.getMovCliList().isEmpty() && movimientoAnterior.getMovCliListOld().isEmpty()) {
                        for (RegMovimientoCliente mcn : movimientoAnterior.getMovCliList()) {
                            detalleActividad = detalleActividad + "Se Agrega Cliente: " + mcn.getEnteInterv().getNombre() + ", Papel: " + mcn.getPapel().getPapel() + "\n";
                        }
                    } else if (!movimientoAnterior.getMovCliList().isEmpty() && !movimientoAnterior.getMovCliListOld().isEmpty()) {
                        for (RegMovimientoCliente mco : movimientoAnterior.getMovCliListOld()) {
                            Boolean borrado = true;
                            for (RegMovimientoCliente mcn : movimientoAnterior.getMovCliList()) {
                                if (mcn.getId() == null) {
                                    detalleActividad = detalleActividad + "Se Agrega Cliente: " + mcn.getEnteInterv().getNombre() + ", Papel: " + mcn.getPapel().getPapel() + "\n";
                                } else if (mco.getId().equals(mcn.getId())) {
                                    borrado = false;
                                    if (!mco.getPapel().getId().equals(mcn.getPapel().getId())) {
                                        detalleActividad = detalleActividad + "Se Modifica Cliente: " + mcn.getEnteInterv().getNombre() + ", Papel anterior: " + mco.getPapel().getPapel() + "\n";
                                    }
                                    if (mco.getEstado() != null && !mco.getEstado().equals(mcn.getEstado())) {
                                        detalleActividad = detalleActividad + "Se Modifica Cliente: " + mcn.getEnteInterv().getNombre() + ", Estado anterior: " + mco.getEstado() + "\n";
                                    }
                                    if (mco.getCedula() != null && !mco.getCedula().equals(mcn.getCedula())) {
                                        detalleActividad = detalleActividad + "Se Modifica Cliente: " + mcn.getEnteInterv().getNombre() + ", Inf. Anterior: " + mco.getEstado() + " - " + mco.getNombres() + " - " + mco.getCedula() + "\n";
                                    }
                                    if (mco.getNombres() != null && !mco.getNombres().equals(mcn.getNombres())) {
                                        detalleActividad = detalleActividad + "Se Modifica Cliente: " + mcn.getEnteInterv().getNombre() + ", Inf. Anterior: " + mco.getEstado() + " - " + mco.getNombres() + " - " + mco.getCedula() + "\n";
                                    }
                                }
                            }
                            if (borrado) {
                                detalleActividad = detalleActividad + "Se Elimina Cliente: " + mco.getEnteInterv().getNombre() + ", Papel: " + mco.getPapel().getPapel() + "\n";
                            }
                        }
                    }
                    break;
                case ELIMINAR_REFERENCIA:
                    if (movimiento.getObservacionEliminacion() != null) {
                        detalleActividad = detalleActividad + "Observación: \n" + movimiento.getObservacionEliminacion() + "\n";
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Logger.getLogger(BitacoraEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return detalleActividad;
    }

}
