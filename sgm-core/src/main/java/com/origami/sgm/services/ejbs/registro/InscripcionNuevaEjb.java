/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.registro;

import com.origami.sgm.bpm.models.InscripcionNuevaModel;
import com.origami.sgm.bpm.models.MovimientoModel;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CtlgCargo;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.CtlgTipoParticipacion;
import com.origami.sgm.entities.RegActo;
import com.origami.sgm.entities.RegCapital;
import com.origami.sgm.entities.RegCatPapel;
import com.origami.sgm.entities.RegEnteInterviniente;
import com.origami.sgm.entities.RegEnteJudiciales;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegFichaPropietarios;
import com.origami.sgm.entities.RegLibro;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoCapital;
import com.origami.sgm.entities.RegMovimientoCliente;
import com.origami.sgm.entities.RegMovimientoFicha;
import com.origami.sgm.entities.RegMovimientoReferencia;
import com.origami.sgm.entities.RegMovimientoRepresentante;
import com.origami.sgm.entities.RegMovimientoSocios;
import com.origami.sgm.enums.ActividadesTransaccionales;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.auditoria.BitacoraServices;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import com.origami.sgm.services.interfaces.registro.InscripcionNuevaServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.hibernate.Hibernate;
import util.EntityBeanCopy;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Stateless(name = "inscripcionNueva")
@Interceptors(value = {HibernateEjbInterceptor.class})
public class InscripcionNuevaEjb implements InscripcionNuevaServices {

    @javax.inject.Inject
    private Entitymanager serv;

    @javax.inject.Inject
    private SeqGenMan sec;

    @javax.inject.Inject
    private FichaIngresoNuevoServices ficha;

    @javax.inject.Inject
    private Entitymanager manager;

    @javax.inject.Inject
    private BitacoraServices bitacora;

    public String getValorUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * Busca los movimientos por el id y retorna el registro si existe
     *
     * @param id - Id del Movimiento
     * @return RegMovientos
     */
    @Override
    public RegMovimiento getRegMovimientoById(Long id) {
        return (RegMovimiento) manager.find(RegMovimiento.class, id);
    }

    /**
     * Consulta en la tabla RegMovimientos por el Id de la Tarea
     *
     * @param idTarea Id de la tarea
     * @return RegMovimiento
     */
    @Override
    public RegMovimiento getRegMovimientoByRegpCertificadoInscripcion(Long idTarea) {
        return (RegMovimiento) manager.find(Querys.getRegMovimientoByRegpCertificadoInscripcion, new String[]{"idTarea"}, new Object[]{idTarea});
    }

    /**
     * Busca todos los Libros en la tabla RegLibro que esten con estado true y
     * los ordena por el nombre
     *
     * @return Lista RegLibro
     */
    @Override
    public List<RegLibro> getRegLibroList() {
        return manager.findAllEntCopy(Querys.getRegLibroList);
    }

    /**
     * Realiza la busqueda por el id del libro, para buscar la lista de actos y
     * retornar la lista
     *
     * @param idLibro id del RegLibro
     * @return Lista de actos
     */
    @Override
    public List<RegActo> getActosByLibro(Long idLibro) {
        List<RegActo> actos = new ArrayList<>();
        try {
            RegLibro libro = (RegLibro) serv.find(Querys.getRegLibroById, new String[]{"idlibro"}, new Object[]{idLibro});
            for (RegActo acc : libro.getRegActoCollection()) {
                if (acc.getEstado()) {
                    actos.add(acc);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return actos;
    }

    /**
     * Busca en RegMovimientoReferencia por el id del movimiento y retorna lista
     * de Movimientos
     *
     * @param idMov - Id del Movimiento
     * @return Lista de RegMovimientos
     */
    @Override
    public List<RegMovimiento> getMovReferenciaByMov(Long idMov) {
        List<RegMovimientoReferencia> movref = serv.findAll(Querys.getRegMovimientoReferencia, new String[]{"idMov"}, new Object[]{idMov});
        List<RegMovimiento> referencias = new ArrayList<>();
        if (movref != null) {
            for (RegMovimientoReferencia m : movref) {
                referencias.add(m.getMovimientoReff());
            }
        }

        return referencias;
    }

    /**
     * Optiene la lista de cantones
     *
     * @return Lista de cantones
     */
    @Override
    public List<CatCanton> getCatCantonList() {
        return manager.findAllEntCopy(Querys.getCatCantonList);
    }

    /**
     * Hace una consulta de un registro del entiti RegCatPapel por el id
     *
     * @param id
     * @return
     */
    @Override
    public RegCatPapel getRegCatPapelById(Long id) {
        try {
            return (RegCatPapel) manager.find(RegCatPapel.class, id);
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    /**
     * Busca por el id del acto en la tabla RegActo Y filtra todos los papeles
     * correspondientes a ese acto
     *
     * @param idacto Id del acto
     * @return Lista de RegCatPapel
     */
    @Override
    public Collection<RegCatPapel> getRegCatPapelByActo(Long idacto) {
        Collection<RegCatPapel> papels;
        RegActo acto;
        try {
            acto = (RegActo) serv.find(Querys.getRegCatPapelByActo, new String[]{"idacto"}, new Object[]{idacto});
            acto.getRegCatPapelCollection().size();
            papels = (Collection<RegCatPapel>) EntityBeanCopy.clone(acto.getRegCatPapelCollection());
            Hibernate.initialize(papels);
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return papels;
    }

    /**
     * Buscar en la tabla RegEnteInterviniente por el NÃºmero de Documento
     *
     * @param cedula NÃºmero de documento
     * @return Interviniente si existe
     */
    @Override
    public RegEnteInterviniente getInterviniente(String cedula) {
        return (RegEnteInterviniente) serv.find(Querys.getRegEnteIntervinienteByCedRuc, new String[]{"cadena", "variable"}, new Object[]{cedula, cedula});
    }

    /**
     * Buscar En la tabla CtlgItem por el nombre del catalogo
     *
     * @param catalogo Nombre del Catalogo
     * @return Lista de Catalogo de la CtlgItem
     */
    @Override
    public List<CtlgItem> lisCtlgItems(String catalogo) {
        return serv.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{catalogo});
    }

    /**
     * Obtiene La lista de cargos de la tabla CtlgCargo que tengan el estado
     * true
     *
     * @return Lista de Cargos
     */
    @Override
    public List<CtlgCargo> ctlgCargos() {
        return manager.findAllEntCopy(Querys.getCtlgCargo);
    }

    /**
     * Obtienen la lista de tipo de participacion de la tabla
     * CtlgTipoParticipacion
     *
     * @return lista de CtlgTipoParticipacion
     */
    @Override
    public List<CtlgTipoParticipacion> getListParticipante() {
        return serv.findAll(Querys.CtlgTipoParticipacionOrberByNombre, new String[]{}, new Object[]{});
    }

    /**
     * Obtiene la lista de capital de la tabla RegCapital
     *
     * @return lista de RegCapital
     */
    @Override
    public List<RegCapital> getListRegCapital() {
        return serv.findAll(Querys.getRegCapital, new String[]{}, new Object[]{});
    }

    /**
     * Buaca en la tabla RegInterviniente si existe el registro
     *
     * @param cedRuc CÃ©dula o Ruc
     * @param nombre Nombre del Intervinientre
     * @param tipoInterv Tipo de Interviniente
     * @return RegInterviniente
     */
    @Override
    public RegEnteInterviniente bucarRegInterv(String cedRuc, String nombre, String tipoInterv) {
        return (RegEnteInterviniente) serv.find(Querys.getRenIntervinienteByCedRucByNombreByTipoInt, new String[]{"cedula", "nomb", "tipointr"}, new Object[]{cedRuc, nombre, tipoInterv});
    }

    /**
     * Buscar el nÃºmero maximo de la secuencia en la columna secuencia de la
     * tabla RegEnteInterviniente si existe devuelve el
     *
     * @param cedula NÃºmero de CÃ©dula
     * @param tipoInterv Tipo de Interviniente
     * @return El nÃºmero de la secuencia
     */
    @Override
    public Integer getMaxInterviniente(String cedula, String tipoInterv) {
//        public static String getMaxRegEnteInterviniente = "select max(e.secuencia) from RegEnteInterviniente e where e.cedRuc=:cedula and e.tipoInterv=:tipointr";
        return (Integer) sec.getSequences(Querys.getMaxRegEnteInterviniente, new String[]{"cedula", "tipointr"}, new Object[]{cedula, tipoInterv});
    }

    /**
     * Primero obtiene el ultimo nÃºmero de la secuencia y despues verifica si
     * el ente ingresado en persona natural para concatenar el nombre y apellido
     * en uno solo campo nombreenvia guardar la entity RegEnteInterviniente
     *
     * @param interv entity
     * @return RegEnteInterviniente
     */
    @Override
    public RegEnteInterviniente guardaRegEnteInterviniente(RegEnteInterviniente interv) {
        try {
            if ("N".equalsIgnoreCase(interv.getTipoInterv())) {
                interv.setNombre(interv.getApellidos() + " " + interv.getNombre());
            }
            interv = sec.getMaxInterviniente(interv);
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return interv;
    }

    /**
     * Metodo que se encarga de crear un registro en CatEnte si es que la cedula
     * que contiene el objeto RegEnteInterviniente no existe aun en
     *
     * @param interv
     * @return
     */
    @Override
    public Boolean creaEnteDeInterviniente(RegEnteInterviniente interv) {
        CatEnte ente;
        try {
            if (interv.getCedRuc().length() == 10 || interv.getCedRuc().length() == 13) {
                ente = ficha.getCatEnte(interv.getCedRuc());
                if (ente == null) {
                    ente = new CatEnte();
                    ente.setCiRuc(interv.getCedRuc());
                    if (interv.getTipoInterv().equalsIgnoreCase("J")) {
                        ente.setEsPersona(false);
                        ente.setRazonSocial(interv.getNombre());
                    } else {
                        ente.setEsPersona(true);
                        ente.setApellidos(interv.getApellidos());
                        ente.setNombres(interv.getNombre());
                    }
                    manager.persist(ente);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    /**
     * Primero obtiene el ultimo nÃºmero de la secuencia y despues envia guardar
     * la entity RegEnteInterviniente
     *
     * @param interv entity
     * @return RegEnteInterviniente
     */
    @Override
    public RegEnteInterviniente guardarRegEnteInterviniente(RegEnteInterviniente interv) {
        try {
            return sec.getMaxInterviniente(interv);
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    /**
     * Si no ingresa el RUC del interviniente ruc de 15 digitos y se envia a
     * actualizar la entity RegEnteInterviniente
     *
     * @param interv RegEnteInterviniente
     * @return RegEnteInterviniente
     */
    @Override
    public RegEnteInterviniente updateRegEnteInterviniente(RegEnteInterviniente interv) {
        if (interv.getCedRuc() == null) {
            String GenererRuc = Utils.completarCadenaConCeros(interv.getId().toString(), 14);
            interv.setCedRuc(("9" + GenererRuc));
        }
        try {
            return (RegEnteInterviniente) serv.persist(interv);
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    /**
     * Primero obtiene la lista que se le pasaron la collection que fueron
     * pasadas y despues se obtiene el la secuencia del NÃºmero de Inscripcion
     * filtado por Libro , el Valor UUID que es un valor aleatorio y se envia a
     * guardar en RegMovimiento con la entity persistida se envia a persistir el
     * resto de lista RegMovimientoCliente, RegMovimientoRepresentante,
     * RegMovimientoSocios, RegMovimientoCapital, RegMovimientoFicha, y por
     * ultimo la Lista de referencias de RegMovimiento si no hayningun error
     * durante este proceso retorna la entity
     *
     * @param movimiento Entity RegMovimiento
     * @param movimientoReferenciaList Lista RegMovimiento
     * @return RegMovimiento
     */
    @Override
    public RegMovimiento guardarMovimientoNuevo(RegMovimiento movimiento, List<RegMovimientoReferencia> movimientoReferenciaList) {
        List<RegMovimientoFicha> movimientoFichas = (List<RegMovimientoFicha>) movimiento.getRegMovimientoFichaCollection();
        List<RegMovimientoCliente> movimientoCliente = (List<RegMovimientoCliente>) movimiento.getRegMovimientoClienteCollection();
        List<RegMovimientoCapital> movimientoCapitals = (List<RegMovimientoCapital>) movimiento.getRegMovimientoCapitalCollection();
        List<RegMovimientoRepresentante> movimientoRepresentantes = (List<RegMovimientoRepresentante>) movimiento.getRegMovimientoRepresentanteCollection();
        List<RegMovimientoSocios> movimientoSocios = (List<RegMovimientoSocios>) movimiento.getRegMovimientoSociosCollection();
        Calendar cl = Calendar.getInstance();
        cl.setTime(movimiento.getFechaInscripcion());
        BigInteger periodo = BigInteger.valueOf(cl.get(Calendar.YEAR));
        RegMovimiento mov = null;
        movimiento.setRegMovimientoFichaCollection(null);
        movimiento.setRegMovimientoClienteCollection(null);
        movimiento.setRegMovimientoCapitalCollection(null);
        movimiento.setRegMovimientoRepresentanteCollection(null);
        movimiento.setRegMovimientoSociosCollection(null);
        //RegMovimientoReferencia referencia;
        try {
            movimiento.setMostrarRelacion(Boolean.FALSE);
            movimiento.setRazonImpresa(Boolean.FALSE);
            movimiento.setInscripcionImpresa(Boolean.FALSE);
            movimiento.setEstado("AC");
            movimiento.setValorUuid(getValorUuid());
            mov = sec.getNumInscripcionRegmovimientobyAnioLibro(cl.get(Calendar.YEAR), movimiento.getLibro().getId(), movimiento);
            bitacora.registrarMovimiento(null, mov, ActividadesTransaccionales.INGRESO_INSCRIPCION, periodo);
            if (!movimientoCliente.isEmpty()) {
                this.guardarMovimientoClientes(movimientoCliente, mov);
            }

            if (!movimientoCapitals.isEmpty()) {
                this.guardarMovimientoCapital(movimientoCapitals, mov);
            }

            if (!movimientoRepresentantes.isEmpty()) {
                this.guardarMovimientoRepresentante(movimientoRepresentantes, mov);
            }

            if (!movimientoSocios.isEmpty()) {
                this.guardarMovimientosSocios(movimientoSocios, mov);
            }

            if (!movimientoFichas.isEmpty()) {
                this.guardarMovimientoFicha(movimientoFichas, mov);
                List<RegFicha> temp = new ArrayList<>();
                for (RegMovimientoFicha mf : movimientoFichas) {
                    temp.add(mf.getFicha());
                }
                bitacora.registrarMovFichas(mov, temp, ActividadesTransaccionales.AGREGAR_REFERENCIA, periodo);
                if (mov.getLibro().getEstadoFicha() != null) {
                    CtlgItem item = manager.find(CtlgItem.class, mov.getLibro().getEstadoFicha());
                    for (RegFicha f : temp) {
                        f.setEstado(item);
                        manager.update(f);
                    }
                }
            }

            if (!movimientoReferenciaList.isEmpty()) {
                for (RegMovimientoReferencia acc : movimientoReferenciaList) {
                    acc.setMovimiento(mov.getId());
                    serv.persist(acc);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mov;
    }

    /**
     * Metodo que se encarga de guardar un registro en el entiti RegMovimiento
     * este registro es diferente por se encarga de guardar una inscripcion que
     * ya fue realizada y lo que hace es registrarla en el sistema
     *
     * @param movimiento
     * @param movimientoReferenciaList
     * @param movimientoFichas
     * @param movimientoClientes
     * @return
     */
    @Override
    public Boolean guardarInscripcionAntigua(RegMovimiento movimiento, List<RegMovimiento> movimientoReferenciaList,
            List<RegFicha> movimientoFichas, List<RegMovimientoCliente> movimientoClientes) {
        Calendar cl = Calendar.getInstance();
        RegMovimientoReferencia referencia;
        RegMovimientoFicha mf;
        BigInteger periodo = BigInteger.valueOf(cl.get(Calendar.YEAR));
        try {
            movimiento = (RegMovimiento) manager.persist(movimiento);
            bitacora.registrarMovimiento(null, movimiento, ActividadesTransaccionales.INGRESO_INSCRIPCION, periodo);
            if (!movimientoClientes.isEmpty()) {
                this.guardarMovimientoClientes(movimientoClientes, movimiento);
            }
            if (!movimientoFichas.isEmpty()) {
                for (RegFicha acc : movimientoFichas) {
                    mf = new RegMovimientoFicha();
                    mf.setMovimiento(movimiento);
                    mf.setFicha(acc);
                    serv.persist(mf);
                }
                bitacora.registrarMovFichas(movimiento, movimientoFichas, ActividadesTransaccionales.AGREGAR_REFERENCIA, periodo);
            }
            if (!movimientoReferenciaList.isEmpty()) {
                for (RegMovimiento acc : movimientoReferenciaList) {
                    referencia = new RegMovimientoReferencia();
                    referencia.setMovimiento(movimiento.getId());
                    referencia.setMovimientoReff(acc);
                    serv.persist(referencia);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    /**
     * Guarda una lista de tipo RegMovimientoCliente que representa los
     * intervinientes relacionados con una inscripcion o movimiento
     *
     * @param list
     * @param mov
     * @return
     */
    @Override
    public List<RegMovimientoCliente> guardarMovsClientes(List<RegMovimientoCliente> list, RegMovimiento mov) {
        List<RegMovimientoCliente> temp = new ArrayList<>();
        try {
            for (RegMovimientoCliente cn : list) {
                if (cn.getId() == null) {
                    cn.setMovimiento(mov);
                }
                cn = (RegMovimientoCliente) serv.persist(cn);
                temp.add(cn);
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return temp;
    }

    /**
     * Guarda una lista de tipo RegMovimientoFicha que representa el conjunto de
     * fichas asociadas a una inscripcion o movimiento, cada ficha representa un
     * bien inmueble
     *
     * @param list
     * @param mov
     * @return
     */
    @Override
    public List<RegMovimientoFicha> guardarMovsFichas(List<RegMovimientoFicha> list, RegMovimiento mov) {
        List<RegMovimientoFicha> temp = new ArrayList<>();
        try {
            for (RegMovimientoFicha cn : list) {
                if (cn.getId() == null) {
                    cn.setMovimiento(mov);
                    cn = (RegMovimientoFicha) serv.persist(cn);
                }
                temp.add(cn);
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return temp;
    }

    /**
     * Guarda una lista de tipo RegMovimientoReferencia que representa las otras
     * inscripciones o movimientos relacionados con una inscripcion nueva
     *
     * @param list
     * @param mov
     * @return
     */
    @Override
    public List<RegMovimientoReferencia> guardarMovsReferencia(List<RegMovimientoReferencia> list, RegMovimiento mov) {
        List<RegMovimientoReferencia> temp = new ArrayList<>();
        try {
            for (RegMovimientoReferencia cn : list) {
                if (cn.getId() == null) {
                    cn.setMovimiento(mov.getId());
                }
                cn = (RegMovimientoReferencia) serv.persist(cn);
                temp.add(cn);
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return temp;
    }

    /**
     * Recibe como parametros listas de RegMovimientoCliente,
     * RegMovimientoRepresentante, RegMovimientoSocios luego Realiza un
     * recorrido por la listas y si hay un ente nuevo lo persiste en la tabla
     * CatEnte
     *
     * @param movimientoClienteList RegMovimientoCliente
     * @param movimientoRepresentanteList RegMovimientoRepresentante
     * @param movimientoSocioList RegMovimientoSocios
     */
    @Override
    public void guadarEntesMovimientos(List<RegMovimientoCliente> movimientoClienteList,
            List<RegMovimientoRepresentante> movimientoRepresentanteList, List<RegMovimientoSocios> movimientoSocioList) {
        RegEnteInterviniente interv;
        CatEnte ente;
        try {
            if (!movimientoClienteList.isEmpty()) {
                for (RegMovimientoCliente cliente : movimientoClienteList) {
                    interv = cliente.getEnteInterv();
                    if (interv.getCedRuc().length() == 10 || interv.getCedRuc().length() == 13) {
                        ente = ficha.getCatEnte(interv.getCedRuc());
                        if (ente == null) {
                            ente = new CatEnte();
                            ente.setCiRuc(interv.getCedRuc());
                            if (interv.getTipoInterv().equalsIgnoreCase("J")) {
                                ente.setEsPersona(false);
                                ente.setRazonSocial(interv.getNombre());
                            } else {
                                ente.setEsPersona(true);
                                ente.setApellidos(interv.getApellidos());
                                ente.setNombres(interv.getNombre());
                            }
                            manager.persist(ente);
                        }
                    }
                }
            }
            if (!movimientoRepresentanteList.isEmpty()) {
                for (RegMovimientoRepresentante cliente : movimientoRepresentanteList) {
                    interv = cliente.getEnteInterv();
                    if (interv.getCedRuc().length() == 10 || interv.getCedRuc().length() == 13) {
                        ente = ficha.getCatEnte(interv.getCedRuc());
                        if (ente == null) {
                            ente = new CatEnte();
                            ente.setCiRuc(interv.getCedRuc());
                            if (interv.getTipoInterv().equalsIgnoreCase("J")) {
                                ente.setEsPersona(false);
                                ente.setRazonSocial(interv.getNombre());
                            } else {
                                ente.setEsPersona(true);
                                ente.setApellidos(interv.getApellidos());
                                ente.setNombres(interv.getNombre());
                            }
                            manager.persist(ente);
                        }
                    }
                }
            }
            if (!movimientoSocioList.isEmpty()) {
                for (RegMovimientoSocios cliente : movimientoSocioList) {
                    interv = cliente.getEnteInterv();
                    if (interv.getCedRuc().length() == 10 || interv.getCedRuc().length() == 13) {
                        ente = ficha.getCatEnte(interv.getCedRuc());
                        if (ente == null) {
                            ente = new CatEnte();
                            ente.setCiRuc(interv.getCedRuc());
                            if (interv.getTipoInterv().equalsIgnoreCase("J")) {
                                ente.setEsPersona(false);
                                ente.setRazonSocial(interv.getNombre());
                            } else {
                                ente.setEsPersona(true);
                                ente.setApellidos(interv.getApellidos());
                                ente.setNombres(interv.getNombre());
                            }
                            manager.persist(ente);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Actualiza el objeto RegMovimiento despues de actualizar las listas y
     * borrar los elementos seleccionados
     *
     * @param movimiento RegMovimiento entity
     * @param inscripcion InscripcionNuevaModel modelo de datos
     * @param movimientoModel
     * @return RegMovimiento
     */
    @Override
    public RegMovimiento guardarMovimientoEdidicion(RegMovimiento movimiento, InscripcionNuevaModel inscripcion, MovimientoModel movimientoModel) {
        BigInteger periodo = new BigInteger(new Date().getYear() + 1900 + "");
        Collection<RegMovimientoCliente> movClieList = movimiento.getRegMovimientoClienteCollection();
        Collection<RegMovimientoRepresentante> movRepList = movimiento.getRegMovimientoRepresentanteCollection();
        Collection<RegMovimientoSocios> movSocList = movimiento.getRegMovimientoSociosCollection();
        Collection<RegMovimientoCapital> movCapList = movimiento.getRegMovimientoCapitalCollection();
        Collection<RegMovimientoFicha> movFichaList = movimiento.getRegMovimientoFichaCollection();
        Collection<RegMovimientoReferencia> movRefList = movimiento.getRegMovimientoReferenciaCollection();
        movimiento.setRegMovimientoClienteCollection(null);
        movimiento.setRegMovimientoRepresentanteCollection(null);
        movimiento.setRegMovimientoSociosCollection(null);
        movimiento.setRegMovimientoCapitalCollection(null);
        movimiento.setRegMovimientoFichaCollection(null);
        movimiento.setRegMovimientoReferenciaCollection(null);
        try {
            this.deleteListsOfMovimiento(inscripcion.getListClientsDell(), inscripcion.getRepresentanteDell(), inscripcion.getCapitalDell(),
                    inscripcion.getSociosDell(), inscripcion.getListaFichasDell(), inscripcion.getListMovsRefDel());
            manager.update(movimiento);
            this.saveOrUpdateEnteOfMovimientosList(movimiento, movClieList, movRepList, movSocList, movCapList, movFichaList, movRefList);
            bitacora.registrarMovimiento(movimientoModel, movimiento, ActividadesTransaccionales.MODIFICACION_INSCRIPCION, periodo);
            if (!inscripcion.getFichasBorradas().isEmpty()) {
                bitacora.registrarMovFichas(movimiento, inscripcion.getFichasBorradas(), ActividadesTransaccionales.ELIMINAR_REFERENCIA, periodo);
            }
            if (!inscripcion.getFichasAgregadas().isEmpty()) {
                bitacora.registrarMovFichas(movimiento, inscripcion.getFichasAgregadas(), ActividadesTransaccionales.AGREGAR_REFERENCIA, periodo);
            }
        } catch (Exception ex) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return movimiento;
    }

    @Override
    public void inactivaPropieatariosFichas(List<RegMovimientoFicha> movsFich) {
        List<RegFichaPropietarios> list;
        try {
            for (RegMovimientoFicha mf : movsFich) {
                list = manager.findAll(Querys.getRegPropietariosByFicha, new String[]{"idficha"}, new Object[]{mf.getFicha().getId()});
                if (!list.isEmpty()) {
                    for (RegFichaPropietarios fp : list) {
                        fp.setEstado(Boolean.FALSE);
                        manager.update(fp);
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void saveFichaPropietarios(List<RegMovimientoCliente> movsCli, List<RegMovimientoFicha> movsFich, RegMovimiento mov) {
        CatEnte ente;
        try {
            for (RegMovimientoFicha mf : movsFich) {
                for (RegMovimientoCliente mc : movsCli) {
                    if (mc.getPropietario()) {
                        RegFichaPropietarios fp = new RegFichaPropietarios();
                        fp.setFicha(mf.getFicha());
                        fp.setMovimiento(mov);
                        fp.setInterviniente(mc.getEnteInterv());
                        ente = ficha.getCatEnte(mc.getEnteInterv().getCedRuc());
                        if (ente != null) {
                            fp.setEnte(ente);
                        }
                        manager.saveAll(fp);
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Guarda o actualiza las listas relacionadas con los intervinientes en una
     * inscripcion
     *
     * @param mov
     * @param movimientoClienteList
     * @param movimientoRepresentanteList
     * @param movimientoSocioList
     * @param movCapList
     * @param movFichaList
     * @param movRefList
     * @return
     */
    @Override
    public Boolean saveOrUpdateEnteOfMovimientosList(RegMovimiento mov, Collection<RegMovimientoCliente> movimientoClienteList,
            Collection<RegMovimientoRepresentante> movimientoRepresentanteList, Collection<RegMovimientoSocios> movimientoSocioList,
            Collection<RegMovimientoCapital> movCapList, Collection<RegMovimientoFicha> movFichaList, Collection<RegMovimientoReferencia> movRefList) {
        RegEnteInterviniente interv;
        CatEnte ente;
        try {
            if (!movimientoClienteList.isEmpty()) {
                for (RegMovimientoCliente cliente : movimientoClienteList) {
                    interv = cliente.getEnteInterv();
                    ente = ficha.getCatEnte(interv.getCedRuc());
                    if (ente != null) {
                        cliente.setEnte(ente);
                    }
                    if (cliente.getId() == null) {
                        cliente.setMovimiento(mov);
                        manager.persist(cliente);
                    } else {
                        manager.update(cliente);
                    }
                }
            }
            if (!movimientoRepresentanteList.isEmpty()) {
                for (RegMovimientoRepresentante cliente : movimientoRepresentanteList) {
                    interv = cliente.getEnteInterv();
                    ente = ficha.getCatEnte(interv.getCedRuc());
                    if (ente != null) {
                        cliente.setEnte(ente);
                    }
                    if (cliente.getId() == null) {
                        cliente.setMovimiento(mov);
                        manager.persist(cliente);
                    } else {
                        manager.update(cliente);
                    }
                }
            }
            if (!movimientoSocioList.isEmpty()) {
                for (RegMovimientoSocios cliente : movimientoSocioList) {
                    interv = cliente.getEnteInterv();
                    ente = ficha.getCatEnte(interv.getCedRuc());
                    if (ente != null) {
                        cliente.setEnte(ente);
                    }
                    if (cliente.getId() == null) {
                        cliente.setMovimiento(mov);
                        manager.persist(cliente);
                    } else {
                        manager.update(cliente);
                    }
                }
            }
            if (!movCapList.isEmpty()) {
                for (RegMovimientoCapital mc : movCapList) {
                    if (mc.getId() == null) {
                        mc.setMovimiento(mov);
                        manager.persist(mc);
                    } else {
                        manager.update(mc);
                    }
                }
            }
            if (!movFichaList.isEmpty()) {
                for (RegMovimientoFicha mf : movFichaList) {
                    if (mf.getId() == null) {
                        mf.setMovimiento(mov);
                        manager.persist(mf);
                    }
                }
            }
            if (!movRefList.isEmpty()) {
                for (RegMovimientoReferencia mr : movRefList) {
                    if (mr.getId() == null) {
                        mr.setMovimiento(mov.getId());
                        manager.persist(mr);
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    /**
     * Borra registros de las tablas que recibe como parametros, estos registros
     * pertenecen a un movimiento o inscripcion es decir se esta editando un
     * movimiento del entiti RegMovimiento
     *
     * @param movCliList
     * @param movRepList
     * @param movCapList
     * @param movSocioList
     * @param movFichaList
     * @param movRefList
     * @return
     */
    @Override
    public Boolean deleteListsOfMovimiento(List<RegMovimientoCliente> movCliList, List<RegMovimientoRepresentante> movRepList,
            List<RegMovimientoCapital> movCapList, List<RegMovimientoSocios> movSocioList, List<RegMovimientoFicha> movFichaList,
            List<RegMovimientoReferencia> movRefList) {
        try {
            if (!movCliList.isEmpty()) {
                manager.deleteList(movCliList);
            }
            if (!movRepList.isEmpty()) {
                manager.deleteList(movRepList);
            }
            if (!movSocioList.isEmpty()) {
                manager.deleteList(movSocioList);
            }
            if (!movCapList.isEmpty()) {
                manager.deleteList(movCapList);
            }
            if (!movFichaList.isEmpty()) {
                manager.deleteList(movFichaList);
            }
            if (!movRefList.isEmpty()) {
                manager.deleteList(movRefList);
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    /**
     * Obtiene la lista de referencias por el id de Moviento
     *
     * @param id id movimiento
     * @return lista de RegMovimientoReferencia
     */
    @Override
    public List<RegMovimientoReferencia> listMovimientoReferenciaByMov(Long id) {
        return serv.findAll(Querys.getRegMovimientoReferenciaByIdMov, new String[]{"idmov"}, new Object[]{id});
    }

    /**
     * Recibe la lista de movimientos de clientes para realizar el guardo o
     * update Verifica si el id es nulo realiza el marge caso contrario el
     * update de la entity RegMovimientoCliente
     *
     * @param clientesNew lista RegMovimientoCliente
     * @param movimiento RegMovimiento
     */
    @Override
    public void guardarMovimientoClientes(List<RegMovimientoCliente> clientesNew, RegMovimiento movimiento) {
        CatEnte ente;
        for (RegMovimientoCliente cn : clientesNew) {
            if (cn.getId() == null) {
                cn.setMovimiento(movimiento);
            }
            ente = ficha.getCatEnte(cn.getEnteInterv().getCedRuc());
            if (ente != null) {
                cn.setEnte(ente);
            }
            serv.persist(cn);
        }
    }

    /**
     * Verifica si existe el id es nulo para realizar un merge caso contrario
     * realiza un update de la entity RegMovimientoFicha
     *
     * @param fichasNew RegMovimientoFicha
     * @param movimiento RegMovimiento
     */
    @Override
    public void guardarMovimientoFicha(List<RegMovimientoFicha> fichasNew, RegMovimiento movimiento) {
        for (RegMovimientoFicha cn : fichasNew) {
            if (cn.getId() == null) {
                cn.setMovimiento(movimiento);
            }
            serv.persist(cn);
        }
    }

    /**
     * Recibe los RegMovimiento nuevos y los RegMovimientoReferencia y verifica
     * si no existe para realizar el merge a la entity RegMovimientoReferencia
     *
     * @param listadoMovimientosRef RegMovimiento
     * @param listRefOld RegMovimientoReferencia
     * @param movimiento RegMovimiento
     */
    @Override
    public void guardarMovimientoReferencia(List<RegMovimiento> listadoMovimientosRef, List<RegMovimientoReferencia> listRefOld, RegMovimiento movimiento) {
        RegMovimientoReferencia referencia;
        Boolean guardar;
        Integer cont = 0;
        for (RegMovimiento m : listadoMovimientosRef) {
            guardar = true;
            for (RegMovimientoReferencia mr : listRefOld) {
                if (m.getId().equals(mr.getMovimientoReff().getId())) {
                    guardar = false;
                }
            }
            if (guardar) {
                referencia = new RegMovimientoReferencia();
                referencia.setMovimiento(movimiento.getId());
                referencia.setMovimientoReff(m);
                referencia.setSecuencia(new BigInteger(cont.toString()));
                serv.persist(referencia);
                cont++;
            }
        }
    }

    /**
     * Verifica si es nulo el id para realizar un merge caso contrario realizar
     * un update a la entity RegMovimientoRepresentante
     *
     * @param representante RegMovimientoRepresentante
     * @param movimiento RegMovimiento
     */
    @Override
    public void guardarMovimientoRepresentante(List<RegMovimientoRepresentante> representante, RegMovimiento movimiento) {
        for (RegMovimientoRepresentante rn : representante) {
            if (rn.getId() == null) {
                rn.setMovimiento(movimiento);
                serv.persist(rn);
            } else {
                serv.persist(rn);
            }
        }
    }

    /**
     * Verifica si es nulo el id para realizar un merge caso contrario realizar
     * un update a la entity RegMovimientoSocios
     *
     * @param sociosNew RegMovimientoSocios
     * @param movimiento RegMovimiento
     */
    @Override
    public void guardarMovimientosSocios(List<RegMovimientoSocios> sociosNew, RegMovimiento movimiento) {
        for (RegMovimientoSocios sn : sociosNew) {
            if (sn.getId() == null) {
                sn.setMovimiento(movimiento);
                serv.persist(sn);
            } else {
                serv.persist(sn);
            }
        }
    }

    /**
     * Verifica si es nulo el id para realizar un merge caso contrario realizar
     * un update a la entity RegMovimientoCapital
     *
     * @param capitalNew RegMovimientoCapital
     * @param movimiento RegMovimiento
     */
    @Override
    public void guardarMovimientoCapital(List<RegMovimientoCapital> capitalNew, RegMovimiento movimiento) {
        for (RegMovimientoCapital cpn : capitalNew) {
            if (cpn.getId() == null) {
                cpn.setMovimiento(movimiento);
                serv.persist(cpn);
            } else {
                serv.update(cpn);
            }
        }
    }

    /**
     * Busca en la tabla RegMovimientoReferencia
     *
     * @param idReff Id del Moviento de referencia
     * @param idMov Id del movimiento que se esta actualizando
     * @return Entity RegMovimientoReferencia
     */
    @Override
    public RegMovimientoReferencia getMovimientoReferenciaByMovReff(Long idReff, Long idMov) {
        return (RegMovimientoReferencia) serv.find(Querys.getRegMovimientoReferenciaByMovReffByIdMov, new String[]{"movReff", "idMov"}, new Object[]{idReff, idMov});
    }

    /**
     * Obtiene el ultimo nÃºmero de la secuencia del campo numRepertorio
     * consultando por el aÃ±o y si isMercantil, retorna la secuencia y se le
     * asigan al campo al mismo campo de la tabla RegMovimiento para crear un
     * nuevo registro envia a guardar y retorna la entity
     *
     * @param anio aÃ±o
     * @param movimiento Entity RegMovimiento
     * @return RegMovimiento
     */
    @Override
    public RegMovimiento asignarNumRepertorioByAnioYporTipoLibro(int anio, RegMovimiento movimiento) {
        try {
            movimiento = sec.getMaxNumRepertRegMovimiento(movimiento, anio);
            //bitacora.registrarMovimiento(null, movimiento, ActividadesTransaccionales.INGRESO_INSCRIPCION, BigInteger.valueOf(anio));
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return movimiento;
    }

    /**
     * Consulta los libros que contengan el mismo de Id de libros y que sean del
     * aÃ±on ingresado como parametro, retorna los libros desde la cantidad
     * obtenidad por el conteo de los mismo el el mismo aÃ±o.
     *
     * @param anio AÃ±o
     * @param idLibro Id de Libro
     * @return Lista RegMovimiento con un maximo de 10 registro
     */
    @Override
    public List<RegMovimiento> getRegMovimientosPorLibroAnio(Integer anio, Long idLibro) {
        List<RegMovimiento> regMovimientos;
        BigInteger cant = this.cantidadMovimientosXanioYlibro(anio, idLibro);
        int valor;
        if (cant.compareTo(BigInteger.TEN) > 0) {
            valor = cant.subtract(BigInteger.TEN).intValue();
        } else {
            valor = 0;
        }
        try {
            regMovimientos = manager.findFirstAndMaxResult(Querys.getRegMovimientosPorLibroAnio, new String[]{"anio", "libroid"}, new Object[]{anio.toString(), idLibro}, valor, 10);
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return regMovimientos;
    }

    /**
     * Realiza un conteo de los libros que contengan el mismo id y que sean del
     * mismo aÃ±o
     *
     * @param anio AÃ±o
     * @param libroId Id de Libro
     * @return La Cantidad de Libros Ingresados
     */
    @Override
    public BigInteger cantidadMovimientosXanioYlibro(Integer anio, Long libroId) {
        BigInteger cantidad;
        try {
            cantidad = (BigInteger) manager.getNativeQuery("SELECT count(*) FROM  sgm_app.reg_movimiento m WHERE m.estado = 'AC' and m.libro=" + libroId + " and to_char(m.fecha_inscripcion, 'yyyy')='" + anio + "'");
            if (cantidad == null) {
                cantidad = BigInteger.ZERO;
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
            return BigInteger.ZERO;
        }
        return cantidad;
    }

    /**
     * Retorna un objeto de tipo RegActo del cual se consulta por el campo
     * abreviatura
     *
     * @param abrev
     * @return
     */
    @Override
    public RegActo getActoByAbrev(String abrev) {
        RegActo acto = new RegActo();
        try {
            acto = (RegActo) manager.findUnique(Querys.getActobyAbreviatura, new String[]{"abrev"}, new Object[]{abrev});
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return acto;
    }

    /**
     * Retorna un objeto de tipo RegEnteJudiciales que se consulta por el campo
     * abreviatura
     *
     * @param abrev
     * @return
     */
    @Override
    public RegEnteJudiciales getRegEnteJudicialByAbrev(String abrev) {
        RegEnteJudiciales ente = new RegEnteJudiciales();
        try {
            ente = (RegEnteJudiciales) manager.findUnique(Querys.getRegEnteJudicialByAbrev, new String[]{"abrev"}, new Object[]{abrev});
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return ente;
    }

}
