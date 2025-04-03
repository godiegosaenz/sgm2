/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBase;
import com.origami.sgm.bpm.models.DatoSeguro;
import com.origami.sgm.bpm.models.ModelDepuracionEntes;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.lazymodels.CatEnteLazyJoinCatPredio;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.sgm.services.interfaces.datoSeguro.DatoSeguroServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import util.Utils;
import util.VerCedulaUtils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class EdicionEntes implements Serializable {

    private static final Logger LOG = Logger.getLogger(EdicionEntes.class.getName());

    private static final long serialVersionUID = 1L;

    @Inject
    private UserSession session;
    @Inject
    private BpmManageBeanBase base;

    @javax.inject.Inject
    private CatastroServices services;
    @javax.inject.Inject
    private Entitymanager manager;

    @javax.inject.Inject
    private DatoSeguroServices datoSeguroSeguro;

    private Boolean esPersona = true;
    private Boolean seleccionado = false;
    private Boolean unificado = false;
    private Boolean edicion = false;
    private Integer tipoBusqueda = 2;
    private String entry;
    private String ciRucTemp;

    private CatEnteLazyJoinCatPredio entesLazy;
    private List<CatEnte> listEntes;
    private List<CatEnte> listEntesSel;
    private List<ModelDepuracionEntes> modificarLista;
    private CatEnte enteValido;
    private CatEnte valido;
    private CatEnte enteRepetido;
    private CatEnte datos;

    /**
     * ****Detalle del predio ***
     */
    private List<CatPredio> predios;

    /**
     * Inicializa las variables de la vista.
     */
    @PostConstruct
    public void initView() {
        cambioTipoPersona();
    }

    /**
     * Actualiza el formulario renderizando los componentes asociados al tipo de
     * busqueda selecccionado.
     */
    public void cambioTipoPersona() {
        if (tipoBusqueda.compareTo(1) == 0) {

        } else if (tipoBusqueda.compareTo(2) == 0) {
            entesLazy = new CatEnteLazyJoinCatPredio(3, esPersona, "A");
        }
        listEntes = new ArrayList<>();
        listEntesSel = new ArrayList<>();
        valido = new CatEnte();
        unificado = false;
        JsfUti.update("frmCorCli");
        JsfUti.update("frmCorCli:pnlBusqNomb");
        JsfUti.update("frmCorCli:dtuser");
    }

    /**
     * Envia renderizar los paneles para el tipo de busqueda seleccionado.
     */
    public void cambioBusqueda() {
        if (tipoBusqueda.compareTo(1) == 0) {

        } else if (tipoBusqueda.compareTo(2) == 0) {
            entesLazy = new CatEnteLazyJoinCatPredio(3, esPersona, "A");
        } else if (tipoBusqueda.compareTo(3) == 0) {
            entesLazy = new CatEnteLazyJoinCatPredio(2, "A");
        } else if (tipoBusqueda.compareTo(4) == 0) {
            entesLazy = new CatEnteLazyJoinCatPredio(1, "A");
        }
        JsfUti.update("frmCorCli");
        JsfUti.update("frmCorCli:pnlBusqNomb");
        JsfUti.update("frmCorCli:dtuser");
    }

    /**
     * Realiza la busqueda de todos los elemento que coincidan con los datos
     * ingresado en la variable entry, despues de tener todos los elementos que
     * retorna la busqueda se envia a eliminar los elementos repetidos con la
     * ayuda del metodo {@link limpiarLista(List<CatEnte>)} que retorna una
     * lista limpia.
     */
    public void buscarEntes() {
        if (entry == null) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar los nombres o apellidos");
            return;
        }
        List<CatEnte> busqueda;
        if (esPersona) {
            busqueda = services.getCatEnteByNombresApellidos(entry, esPersona);
        } else {
            busqueda = services.getEntesByRazonSocial(entry);
        }
        if (busqueda != null) {
            listEntesSel = limpiarLista(busqueda);
        } else {
            JsfUti.messageError(null, "Advertencia", "No se encontraron registros en la busqueda.");
        }
        agregarEntes();
    }

    /**
     * Agrega los entes seleccionados en el Lazy a lista final de entes que
     * seran unificados e inactivados en la base de datos.
     */
    public void agregarEntes() {
        if (Utils.isNotEmpty(listEntes)) {
            listEntesSel.addAll(listEntes);
            listEntesSel = limpiarLista(listEntesSel);
        }
        JsfUti.update("frmCorCli:dtSel");
    }

    /**
     * Remueve de la lista el ente que fue seleccionado en la tabla
     *
     * @param del Entity {@link CatEnte} a remover de la lista.
     */
    public void eliminar(CatEnte del) {
        if (Utils.isNotEmpty(listEntesSel)) {
            listEntesSel.remove(del);
        }
        JsfUti.update("frmCorCli:dtSel");
    }

    /**
     * Muestra el dialog para realizar la edición del ente
     */
    public void editar() {
        if (valido == null) {
            JsfUti.messageError(null, "Debe Seleccionar un cliente", "");
            return;
        }
        JsfUti.update("frmEdicionEnte");
        JsfUti.executeJS("PF('edicionEnte').show()");
    }

    /**
     * Muestra el dialog para la edición del ente
     *
     * @param ente {@link CatEnte} a Editar.
     */
    public void edicion(CatEnte ente) {
        if (listEntesSel.size() > 0) {
            JsfUti.messageError(null, "Error", "Tiene clientes por unificar.");
            return;
        }
        edicion = true;
        this.valido = ente;
        this.ciRucTemp = ente.getCiRuc();
        JsfUti.update("frmEdicionEnte");
        JsfUti.executeJS("PF('edicionEnte').show()");
    }

    /**
     * Cambia El estado del a X
     *
     * @param ente {@link CatEnte} a Actualizar.
     */
    public void inactivar(CatEnte ente) {
        ente.setEstadoCorrecion("X");
//        ente.setEstado("I");
        ente.setFechaMod(new Date());
        ente.setUserMod(session.getName_user());
        services.actualizarEnte(ente);
        JsfUti.messageError(null, "Advertencia", "Cliente se registro como invalido.");
    }

    /**
     * Cambia el estado del ente a 'I'
     *
     * @param ente {@link CatEnte} a Inactivar
     */
    public void eliminarEnte(CatEnte ente) {
        ente.setEstadoCorrecion("C");
        ente.setEstado("I");
        ente.setFechaMod(new Date());
        ente.setUserMod(session.getName_user());
        services.actualizarEnte(ente);
        JsfUti.messageError(null, "Advertencia", "Cliente se registro como invalido.");
    }

    /**
     * Muestra el Dialog de los predios que tiene el {@link CatEnte}
     *
     * @param ente {@link CatEnte} a mostrar predios.
     */
    public void detallePredios(CatEnte ente) {
        predios = new ArrayList<>();
        for (CatPredioPropietario p : ente.getCatPredioPropietarioCollection()) {
            predios.add(p.getPredio());
        }
        datos = ente;
        JsfUti.update("frmdet");
        JsfUti.executeJS("PF('detallePredios').show()");
    }

    /**
     * Verifica si existe el número de documento de que se ha ingresado.
     */
    public void existeCedula() {
        validarDocumento(valido);
        final String numDocTemp = ciRucTemp;
        final Boolean esPer = valido.getEsPersona();
        DatoSeguro datSeg = null;
        if (esPer) {
            datSeg = verificarEnte(valido.getCiRuc(), false);
        }

        Long temp = services.existeEnteByCiRuc(new String[]{"ciRuc"}, new Object[]{valido.getCiRuc()});
        if (temp != null) {
            if (temp.compareTo(valido.getId()) == 0) {
                if (datSeg != null) {
                    datoSeguroSeguro.llenarEnte(datSeg, valido, false);
                }
            } else {

                enteRepetido = new CatEnte();
                listEntesSel = new ArrayList<>();

                Map<String, Object> par = new HashMap<>();
                par.put("ciRuc", valido.getCiRuc());
                enteRepetido = services.propiedadHorizontal().getCatEnteByParemt(par);

                valido.setCiRuc(numDocTemp);
                if (datSeg != null) {
                    datoSeguroSeguro.llenarEnte(datSeg, enteRepetido, true);
                    datoSeguroSeguro.llenarEnte(datSeg, valido, false);
                }
                listEntesSel.add(valido);
                listEntesSel.add(enteRepetido);

                enteRepetido = new CatEnte();
                valido = new CatEnte();
            }
        }
        JsfUti.update("frmEdicionEnte");
    }

    /**
     * Valida si el número de documento el valido
     *
     * @param verificar
     */
    public void validarDocumento(CatEnte verificar) {
        if (verificar.getCiRuc() == null || verificar.getCiRuc().trim().length() <= 0) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar el número de identificación.");
            return;
        }
        if (Utils.validateNumberPattern(verificar.getCiRuc().trim())) {
            if (verificar.getCiRuc().trim().length() == 10 || verificar.getCiRuc().trim().length() == 13) {
                VerCedulaUtils validarCiRuc = new VerCedulaUtils();
                if (!validarCiRuc.comprobarDocumento(verificar.getCiRuc())) {
                    JsfUti.messageError(null, "Advertencia", "Número de identificación invalidó.");
//                    return;
                }
            } else {
                JsfUti.messageError(null, "Advertencia", "Número de identificación invalidó.");
            }
        } else {
            JsfUti.messageError(null, "Número de identificación invalidó", "Solo debe ingresar números.");
            return;
        }
    }

    /**
     * Guarda los cambios que se realizaron sobre el ente
     */
    public void modificar() {
        if (unificado) {
            JsfUti.messageError(null, "Advertencia.", "Debe completar el proceso de Unificación.");
            return;
        }
        try {
            validarDocumento(valido);

            Long temp = services.existeEnteByCiRuc(new String[]{"ciRuc"}, new Object[]{valido.getCiRuc()});
            if (temp != null) {
                if (temp.compareTo(valido.getId()) == 0) {

                } else {
                    JsfUti.messageError(null, "Advertencia", "Ya existe un cliente con el mismo número de documento.");
                    return;
                }
            }
            valido.setFechaMod(new Date());
            valido.setUserMod(session.getName_user());
            valido.setEstadoCorrecion("C");
            if (services.actualizarEnte(valido)) {
                JsfUti.messageInfo(null, "Información", "Datos actualizados Correctamente.");
                JsfUti.executeJS("PF('edicionEnte').hide()");
                JsfUti.update("frmCorCli");
                if (edicion) {
                    valido = new CatEnte();
                    edicion = false;
                }
            } else {
                JsfUti.messageFatal(null, "Error", "Ocurrio un error al intentar actualizar los datos del cliente.");
            }
            limpiarDatos();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al Guardar CatEnte", e);
        }

    }

    public void limpiarDatosMod() {
        listEntes = new ArrayList<>();
        listEntesSel = new ArrayList<>();
        unificado = false;
    }

    public void limpiarDatos() {
        listEntes = new ArrayList<>();
        listEntesSel = new ArrayList<>();
        valido = new CatEnte();
        unificado = false;
    }

    /**
     * Realiza el cambio del ente a todas las lista que esten asociados a cada
     * uno de los entes que seran inactivados.
     */
    public void unificar() {
        if (enteValido == null) {
            JsfUti.messageError(null, "Advertencia.", "Debe seleccionar un cliente para poder eliminar los repetidos.");
            return;
        }
        if (Utils.isNotEmpty(listEntesSel)) {
            if (listEntesSel.size() <= 1) {
                JsfUti.messageError(null, "Advertencia.", "Debe haber por lo menos 2 registros para poder realizar la unificación de clientes.");
                return;
            }
            valido = enteValido;
            listEntesSel.remove(enteValido);
            modificarLista = new ArrayList<>();
            //modificarLista = services.getObjectosAsociados(listEntesSel);
        }
        //unificado = services.actualizarEntesDepurado(modificarLista, enteValido);
        JsfUti.update("frmCorCli");
        JsfUti.update("frmCorCli:cliSeleccionados");
        JsfUti.update("frmCorCli:menuSel");
        JsfUti.update("frmCorCli:pnlDatosEnte");
        JsfUti.update("frmCorCli:dtSel");
        JsfUti.update("frmEdicionEnte");
    }

    /**
     * Inactiva en la base todos los registro que contenaga la lista
     * listEntesSel
     */
    public void eliminarRepetidos() {
        final int borrar = listEntesSel.size();
        for (CatEnte s : listEntesSel) {
            s.setEstadoCorrecion("C");
        }
        if (services.actualizarEnteAndCollection(listEntesSel, modificarLista, session.getName_user())) {
            if (edicion) {
                limpiarDatosMod();
            } else {
                limpiarDatos();
            }

            modificarLista = new ArrayList<>();
            JsfUti.messageInfo(null, "", "Se han eliminado " + borrar + " registro(s) de cliente(s).");
        }

        JsfUti.update("frmCorCli");
        JsfUti.update("frmCorCli:cliSeleccionados");
        JsfUti.update("frmCorCli:menuSel");
        JsfUti.update("frmCorCli:pnlDatosEnte");
        JsfUti.update("frmCorCli:dtSel");
        JsfUti.update("frmEdicionEnte");
    }

    public void salir() {
        if (unificado) {
            JsfUti.messageError(null, "Advertencia.", "No termino de Unificar los clientes repetidos.");
        }
        limpiarDatos();
        JsfUti.executeJS("PF('edicionEnte').hide();");
        JsfUti.update("frmCorCli");
    }

    public void buscarCoincidencias() {
        if (Utils.isEmpty(listEntes)) {
            JsfUti.messageError(null, "Error", "No se seleccionado ningun registro.");
        }
        int procesados = 0;
        try {
            int count = 1;
            for (CatEnte ente : listEntes) {
                if (ente.getNombres() == null && ente.getApellidos() == null && ente.getRazonSocial() == null) {
                    DatoSeguro data = verificarEnte(ente.getCiRuc(), false);
                    if(data != null){
                        datoSeguroSeguro.llenarEnte(data, ente, false);
                        manager.persist(ente);
                    }
                }
                listEntesSel = new ArrayList<>();
                String q = "SELECT p FROM CatEnte p WHERE p.id <> :id AND COALESCE(TRIM(UPPER(p.nombres)), '') = :nombres AND COALESCE(TRIM(UPPER(p.apellidos)), '') = :apellidos AND COALESCE(TRIM(UPPER(p.razonSocial)), '') = :razonSocial AND p.estado = 'A'";
                List<CatEnte> list = manager.findAll(q, new String[]{"id", "nombres", "apellidos", "razonSocial"},
                        new Object[]{ente.getId(), (ente.getNombres() == null ? "" : ente.getNombres().trim().toUpperCase()), (ente.getApellidos() == null ? "" : ente.getApellidos().trim().toUpperCase()), (ente.getRazonSocial() == null ? "" : ente.getRazonSocial().trim().toUpperCase())});
                /*String q2 = "SELECT p FROM CatEnte p WHERE p.id <> :id AND COALESCE(TRIM(UPPER(p.nombres)), '') = :nombres AND p.apellidos IS NULL AND COALESCE(TRIM(UPPER(p.razonSocial)), '') = :razonSocial AND p.estado = 'A'";
                List<CatEnte> list2 = manager.findAll(q2, new String[]{"id", "nombres", "razonSocial"},
                        new Object[]{ente.getId(), ((ente.getApellidos() == null ? "" : ente.getApellidos().trim().toUpperCase()) + " " + (ente.getNombres() == null ? "" : ente.getNombres().trim().toUpperCase())), (ente.getRazonSocial() == null ? "" : ente.getRazonSocial().trim().toUpperCase())});
                System.out.println("Segunda consulta: " + count + " - " + Utils.isNotEmpty(list2));*/
                count++;
                if (Utils.isNotEmpty(list)) {
                    System.out.println("Datos a procesar: " + ente.getId() + " - " + ente.getNombres() + " " + ente.getApellidos() + " " + ente.getRazonSocial());
                    Boolean encontrado = false;
                    for (CatEnte temp : list) {
                        System.out.print("No. Identificacion: " + temp.getCiRuc());
                        System.out.println(" >> resultado: " + temp.getId() + " - " + ente.getNombres() + " " + ente.getApellidos() + " " + ente.getRazonSocial());
                        // verificar cuando hay dos cedulas con 10 digitos usar metodo para validar
                        if (list.size() > 1) {
                            if (Utils.validateCCRuc(temp.getCiRuc()) && !encontrado) {
                                enteValido = temp;
                                listEntesSel.add(temp);
                                encontrado = true;
                            } else {
                                if (Utils.validateCCRuc(temp.getCiRuc()) && encontrado) {

                                } else {
                                    listEntesSel.add(temp);
                                    if (enteValido == null) {
                                        enteValido = ente;
                                    }
                                }
                            }
                        } else {
                            // Cuando halla cedula y ruc con los mismos datos de la cedula no eliminar ninguna de las 2
                            if (temp.getCiRuc() != null && (temp.getCiRuc().length() == 10 || temp.getCiRuc().length() == 13)) {
                                enteValido = temp;
                            } else {
                                if (enteValido == null) {
                                    enteValido = ente;
                                }
                            }
                            listEntesSel.add(temp);
                        }
                    }
                    System.out.println("Ente con cedula valido: " + enteValido.getCiRuc());
                    listEntesSel.add(ente);
                    unificar();
                    elimiarRepetidosBucle();
                    procesados++;
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Procesar...", e);
        }
        listEntes = null;
        System.out.print("------ Procesados " + procesados);
        System.out.println(" Fin de Bucle...");
    }

    private void elimiarRepetidosBucle() {
        if (Utils.isNotEmpty(listEntesSel)) {
            int borrar = listEntesSel.size();
            for (CatEnte s : listEntesSel) {
                s.setEstadoCorrecion("C");
            }
            if (services.actualizarEnteAndCollection(listEntesSel, modificarLista, session.getName_user())) {
                System.out.println(" Borrados: " + borrar);
            }
            if (enteValido.getEstado().equals("I")) {
                enteValido.setEstado("A");
                manager.persist(enteValido);
            }
            enteValido = null;
        }
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public CatEnteLazyJoinCatPredio getEntesLazy() {
        return entesLazy;
    }

    public void setEntesLazy(CatEnteLazyJoinCatPredio entesLazy) {
        this.entesLazy = entesLazy;
    }

    public Boolean getEsPersona() {
        return esPersona;
    }

    public void setEsPersona(Boolean esPersona) {
        this.esPersona = esPersona;
    }

    public List<CatEnte> getListEntes() {
        return listEntes;
    }

    public void setListEntes(List<CatEnte> listEntes) {
        this.listEntes = listEntes;
    }

    public CatEnte getEnteValido() {
        return enteValido;
    }

    public void setEnteValido(CatEnte enteValido) {
        this.enteValido = enteValido;
    }

    public Boolean getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(Boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public BpmManageBeanBase getBase() {
        return base;
    }

    public void setBase(BpmManageBeanBase base) {
        this.base = base;
    }

    public List<CatEnte> getListEntesSel() {
        return listEntesSel;
    }

    public void setListEntesSel(List<CatEnte> listEntesSel) {
        this.listEntesSel = listEntesSel;
    }

    public Boolean getUnificado() {
        return unificado;
    }

    public void setUnificado(Boolean unificado) {
        this.unificado = unificado;
    }

    public Integer getTipoBusqueda() {
        return tipoBusqueda;
    }

    public void setTipoBusqueda(Integer tipoBusqueda) {
        this.tipoBusqueda = tipoBusqueda;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public CatEnte getValido() {
        return valido;
    }

    public void setValido(CatEnte valido) {
        this.valido = valido;
    }

    /**
     * Elimina los elementos repetidos en la lista de {@link CatEnte}.
     *
     * @param busqueda Lista de {@link  CatEnte}.
     * @return Lista sin elementos repetidos.
     */
    private List<CatEnte> limpiarLista(List<CatEnte> busqueda) {
        List<CatEnte> listaFinal = new ArrayList<>();
        Map<Long, Object> object = new HashMap<Long, Object>();
        for (CatEnte elemento : busqueda) {
            object.put(elemento.getId(), elemento);
        }
        for (Map.Entry<Long, Object> entrySet : object.entrySet()) {
            Object value = entrySet.getValue();
            listaFinal.add((CatEnte) value);
        }
        return listaFinal;
    }

    public List<CatPredio> getPredios() {
        return predios;
    }

    public void setPredios(List<CatPredio> predios) {
        this.predios = predios;
    }

    public CatEnte getEnteRepetido() {
        return enteRepetido;
    }

    public void setEnteRepetido(CatEnte enteRepetido) {
        this.enteRepetido = enteRepetido;
    }

    private DatoSeguro verificarEnte(String CiRuc, Boolean empresa) {
        DatoSeguro data;
        try {
            data = datoSeguroSeguro.getDatos(CiRuc, empresa, 0);
            if (data == null) {
                return null;
            }
            return data;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error al validar", e);
        }
        return null;
    }

    public CatEnte getDatos() {
        return datos;
    }

    public void setDatos(CatEnte datos) {
        this.datos = datos;
    }

}
