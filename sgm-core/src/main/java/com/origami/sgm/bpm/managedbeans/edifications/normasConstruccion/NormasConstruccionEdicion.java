/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.normasConstruccion;

import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatNormasConstruccion;
import com.origami.sgm.entities.CatNormasConstruccionHasRetirosAumento;
import com.origami.sgm.services.interfaces.edificaciones.NormasConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import util.EntityBeanCopy;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class NormasConstruccionEdicion implements Serializable {

    private static final Long serialVersionUID = 1L;
    @javax.inject.Inject
    protected NormasConstruccionServices normasServices;

    @javax.inject.Inject
    protected FichaIngresoNuevoServices fichaServices;

    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;

    @Inject
    private ServletSession ss;
    @Inject
    protected UserSession session;

    protected Long idNorma;
    protected Integer codigo;
    protected String retiros;
    protected Boolean hayImagen = false;
    protected byte[] buffer;

    protected CatNormasConstruccion normaConstruccion;
    protected CatCiudadela ciudadela;
    protected CatNormasConstruccionHasRetirosAumento retirosAumentos;
    protected List<CatCiudadela> ciudadelaList;
    protected List<CatNormasConstruccionHasRetirosAumento> retirosAumentosEliminar;
    protected List<CatNormasConstruccionHasRetirosAumento> retiroLateral;
    protected List<CatNormasConstruccionHasRetirosAumento> retiroPosterior;
    protected List<CatNormasConstruccionHasRetirosAumento> retiroFrontal;
    protected List<CatNormasConstruccionHasRetirosAumento> alturaMaxEdificación;
    protected List<CatNormasConstruccionHasRetirosAumento> alturaAntepechosVentanas;
    protected List<CatNormasConstruccionHasRetirosAumento> alturaMaxCerramiento;
    protected List<CatNormasConstruccionHasRetirosAumento> disposicionGeneral;

    /**
     * Inicio de parametros del facelet
     */
    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            initView();
        }
    }

    /**
     * Inicio de variables.
     */
    public void initView() {
        if (idNorma != null) {
            ciudadelaList = new ArrayList<>();
            ciudadelaList = fichaServices.getCiudadelas();
            normaConstruccion = new CatNormasConstruccion();
            normaConstruccion = normasServices.getCatNormasConstruccion(idNorma);
            retirosAumentosEliminar = new ArrayList<>();
            retirosAumentos = new CatNormasConstruccionHasRetirosAumento();
            retiroFrontal = new ArrayList<>();
            retiroLateral = new ArrayList<>();
            retiroPosterior = new ArrayList<>();
            alturaAntepechosVentanas = new ArrayList<>();
            alturaMaxCerramiento = new ArrayList<>();
            alturaMaxEdificación = new ArrayList<>();
            disposicionGeneral = new ArrayList<>();
            if (normaConstruccion != null) {
                if (normaConstruccion.getIdCiudadela() != null) {
                    ciudadela = new CatCiudadela();
                    ciudadela = (CatCiudadela) EntityBeanCopy.clone(normaConstruccion.getIdCiudadela());
                }

                List<CatNormasConstruccionHasRetirosAumento> todos = (List<CatNormasConstruccionHasRetirosAumento>) normaConstruccion.getCatNormasConstruccionHasRetirosAumentoCollection();
                for (CatNormasConstruccionHasRetirosAumento todo : todos) {
                    switch (todo.getCodigo().intValue()) {
                        case 1:
                            todo.setNombreCodigo("Retiro Frontal");
                            retiroFrontal.add(todo);
                            break;
                        case 2:
                            todo.setNombreCodigo("Retiro Lateral");
                            retiroLateral.add(todo);
                            break;
                        case 3:
                            todo.setNombreCodigo("Retiro Posterior");
                            retiroPosterior.add(todo);
                            break;
                        case 4:
                            todo.setNombreCodigo("Altura Max. Edificación");
                            alturaMaxEdificación.add(todo);
                            break;
                        case 5:
                            todo.setNombreCodigo("Altura Max. Cerramiento");
                            alturaMaxCerramiento.add(todo);
                            break;
                        case 6:
                            todo.setNombreCodigo("Altura Antepechos de Ventanas");
                            alturaAntepechosVentanas.add(todo);
                            break;
                        case 7:
                            todo.setNombreCodigo("Disposición General");
                            disposicionGeneral.add(todo);
                            break;
                    }
                }
                hayImagen = normaConstruccion.getImafoto() != null;
            }
        }
//         else {
//            JsfUti.redirectFaces("/vistaprocesos/dashBoard.xhtml");
//        }
    }

    /**
     * Agrega El nombre del tipo de Retiro o Altura de la norma, despues muestra
     * el Dialog de Ingreso.
     *
     * @param cod Código de Retiros o Alturas de la norma de Construcción
     */
    public void mostrarIngreso(Integer cod) {
        retirosAumentos = new CatNormasConstruccionHasRetirosAumento();
        codigo = cod;
        retirosAumentos.setCodigo(new BigInteger(codigo.toString()));
        retiros = new String();
        switch (codigo) {
            case 1:
                retirosAumentos.setNombreCodigo("Retiro Frontal");
                break;
            case 2:
                retirosAumentos.setNombreCodigo("Retiro Lateral");
                break;
            case 3:
                retirosAumentos.setNombreCodigo("Retiro Posterior");
                break;
            case 4:
                retirosAumentos.setNombreCodigo("Altura Max. Edificación");
                break;
            case 5:
                retirosAumentos.setNombreCodigo("Altura Max. Cerramiento");
                break;
            case 6:
                retirosAumentos.setNombreCodigo("Altura Antepechos de Ventanas");
                break;
            case 7:
                retirosAumentos.setNombreCodigo("Disposición General");
                break;
        }
        JsfUti.update("frmObsCor");
        JsfUti.executeJS("PF('agregar').show();");

    }

    /**
     * Agrega a los diferentes de lista dependiendo del codigo
     */
    public void agregarRetiros() {
        if (retirosAumentos.getDescripcion() == null || "".equals(retirosAumentos.getDescripcion())) {
            JsfUti.messageInfo(null, "Llene el campo retiro.", "");
            return;
        }
        retirosAumentos.setEstado("A");
        retirosAumentos.setCodigo(new BigInteger(codigo.toString()));
        switch (retirosAumentos.getCodigo().intValue()) {
            case 1:
                retiroFrontal.add(retirosAumentos);
                JsfUti.update("formNormaConstIngr:inf:dtRetFront");
                break;
            case 2:
                retiroLateral.add(retirosAumentos);
                JsfUti.update("formNormaConstIngr:inf:dtRetLat");
                break;
            case 3:
                retiroPosterior.add(retirosAumentos);
                JsfUti.update("formNormaConstIngr:inf:dtRetPost");
                break;
            case 4:
                alturaMaxEdificación.add(retirosAumentos);
                JsfUti.update("formNormaConstIngr:inf:dtAltEdif");
                break;
            case 5:
                alturaMaxCerramiento.add(retirosAumentos);
                JsfUti.update("formNormaConstIngr:inf:dtAltCerr");
                break;
            case 6:
                alturaAntepechosVentanas.add(retirosAumentos);
                JsfUti.update("formNormaConstIngr:inf:dtAltVent");
                break;
            case 7:
                disposicionGeneral.add(retirosAumentos);
                JsfUti.update("formNormaConstIngr:inf:dtDisposiciones");
                break;
        }

        retiros = retirosAumentos.getNombreCodigo();
        retirosAumentos = new CatNormasConstruccionHasRetirosAumento();
        retirosAumentos.setNombreCodigo(retiros);
        JsfUti.update("formNormaConstIngr");
        JsfUti.update("frmObsCor");

    }

    /**
     * Elimina de las diferentes lista
     * {@link CatNormasConstruccionHasRetirosAumento} dependiendo del código
     *
     * @param ra Entity {@link CatNormasConstruccionHasRetirosAumento}
     */
    public void eliminarRetirosAumentos(CatNormasConstruccionHasRetirosAumento ra) {
        switch (ra.getCodigo().intValue()) {
            case 1:
                for (int i = 0; i < retiroFrontal.size(); i++) {
                    if (retiroFrontal.get(i).getDescripcion().equals(ra.getDescripcion()) && retiroFrontal.get(i).equals(ra)) {
                        retiroFrontal.remove(ra);
                        break;
                    }
                }
                JsfUti.update("formNormaConstIngr:inf:dtRetFront");
                break;
            case 2:
                for (int i = 0; i < retiroLateral.size(); i++) {
                    if (retiroLateral.get(i).getDescripcion().equals(ra.getDescripcion()) && retiroLateral.get(i).equals(ra)) {
                        retiroLateral.remove(i);
                    }
                }
                JsfUti.update("formNormaConstIngr:inf:dtRetLat");
                break;
            case 3:
                for (int i = 0; i < retiroPosterior.size(); i++) {
                    if (retiroPosterior.get(i).getDescripcion().equals(ra.getDescripcion()) && retiroPosterior.get(i).equals(ra)) {
                        retiroPosterior.remove(i);
                    }
                }
                JsfUti.update("formNormaConstIngr:inf:dtRetPost");
                break;
            case 4:
                for (int i = 0; i < alturaMaxEdificación.size(); i++) {
                    if (alturaMaxEdificación.get(i).getDescripcion().equals(ra.getDescripcion()) && alturaMaxEdificación.get(i).equals(ra)) {
                        alturaMaxEdificación.remove(i);
                    }
                }
                JsfUti.update("formNormaConstIngr:inf:dtAltEdif");
                break;
            case 5:
                for (int i = 0; i < alturaMaxCerramiento.size(); i++) {
                    if (alturaMaxEdificación.get(i).getDescripcion().equals(ra.getDescripcion()) && alturaMaxEdificación.get(i).equals(ra)) {
                        alturaMaxCerramiento.remove(i);
                    }
                }
                JsfUti.update("formNormaConstIngr:inf:dtAltCerr");
                break;
            case 6:
                for (int i = 0; i < alturaAntepechosVentanas.size(); i++) {
                    if (alturaAntepechosVentanas.get(i).getDescripcion().equals(ra.getDescripcion()) && alturaAntepechosVentanas.get(i).equals(ra)) {
                        alturaAntepechosVentanas.remove(i);
                    }
                }
                JsfUti.update("formNormaConstIngr:inf:dtAltVent");
                break;
            case 7:
                for (int i = 0; i < disposicionGeneral.size(); i++) {
                    if (disposicionGeneral.get(i).getDescripcion().equals(ra.getDescripcion()) && disposicionGeneral.get(i).equals(ra)) {
                        disposicionGeneral.remove(i);
                    }
                }
                JsfUti.update("formNormaConstIngr:inf:dtDisposiciones");
                break;
        }

        if (ra.getId() != null) {
            retirosAumentosEliminar.add(ra);
        }

        JsfUti.update("formNormaConstIngr");
    }

    public void handleFileUpload(FileUploadEvent event) {
        try (InputStream fi = event.getFile().getInputstream()) {
            buffer = new byte[(int) event.getFile().getSize()];

            fi.read(buffer);
//            normaConstruccion.setImafoto(buffer);
            JsfUti.messageInfo(null, "Exitosamente", event.getFile().getFileName() + " fue subida.");
            hayImagen = false;
        } catch (IOException ex) {
            Logger.getLogger(NormasConstruccionEdicion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void guadarNormaConstruccion() {
        List<CatNormasConstruccionHasRetirosAumento> listaNormas = new ArrayList<>();
        for (CatNormasConstruccionHasRetirosAumento retiroFrontal1 : retiroFrontal) {
            listaNormas.add(retiroFrontal1);
        }
        for (CatNormasConstruccionHasRetirosAumento retiroLateral1 : retiroLateral) {
            listaNormas.add(retiroLateral1);
        }
        for (CatNormasConstruccionHasRetirosAumento retiroPosterior1 : retiroPosterior) {
            listaNormas.add(retiroPosterior1);
        }
        for (CatNormasConstruccionHasRetirosAumento alturaMaxEdificación1 : alturaMaxEdificación) {
            listaNormas.add(alturaMaxEdificación1);
        }
        for (CatNormasConstruccionHasRetirosAumento alturaMaxCerramiento1 : alturaMaxCerramiento) {
            listaNormas.add(alturaMaxCerramiento1);
        }
        for (CatNormasConstruccionHasRetirosAumento alturaAntepechosVentana : alturaAntepechosVentanas) {
            listaNormas.add(alturaAntepechosVentana);
        }
        for (CatNormasConstruccionHasRetirosAumento disposicionGeneral1 : disposicionGeneral) {
            listaNormas.add(disposicionGeneral1);
        }
        if (ciudadela != null) {
            normaConstruccion.setIdCiudadela(ciudadela);
        }
//        normaConstruccion.setEstado("I");
        Boolean ok = normasServices.actualizarNormasConstrauccion(normaConstruccion, listaNormas, retirosAumentosEliminar, buffer);
        if (ok) {
            JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/normasConstruccion/normasConstruccionConsulta.xhtml");
        }
    }

    public void volver() {
        JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/normasConstruccion/normasConstruccionConsulta.xhtml");
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public CatCiudadela getCiudadela() {
        return ciudadela;
    }

    public void setCiudadela(CatCiudadela ciudadela) {
        this.ciudadela = ciudadela;
    }

    public List<CatCiudadela> getCiudadelaList() {
        return ciudadelaList;
    }

    public void setCiudadelaList(List<CatCiudadela> ciudadelaList) {
        this.ciudadelaList = ciudadelaList;
    }

    public List<CatNormasConstruccionHasRetirosAumento> getRetiroLateral() {
        return retiroLateral;
    }

    public void setRetiroLateral(List<CatNormasConstruccionHasRetirosAumento> retiroLateral) {
        this.retiroLateral = retiroLateral;
    }

    public List<CatNormasConstruccionHasRetirosAumento> getRetiroPosterior() {
        return retiroPosterior;
    }

    public void setRetiroPosterior(List<CatNormasConstruccionHasRetirosAumento> retiroPosterior) {
        this.retiroPosterior = retiroPosterior;
    }

    public List<CatNormasConstruccionHasRetirosAumento> getRetiroFrontal() {
        return retiroFrontal;
    }

    public void setRetiroFrontal(List<CatNormasConstruccionHasRetirosAumento> retiroFrontal) {
        this.retiroFrontal = retiroFrontal;
    }

    public List<CatNormasConstruccionHasRetirosAumento> getAlturaMaxEdificación() {
        return alturaMaxEdificación;
    }

    public void setAlturaMaxEdificación(List<CatNormasConstruccionHasRetirosAumento> alturaMaxEdificación) {
        this.alturaMaxEdificación = alturaMaxEdificación;
    }

    public List<CatNormasConstruccionHasRetirosAumento> getAlturaAntepechosVentanas() {
        return alturaAntepechosVentanas;
    }

    public void setAlturaAntepechosVentanas(List<CatNormasConstruccionHasRetirosAumento> alturaAntepechosVentanas) {
        this.alturaAntepechosVentanas = alturaAntepechosVentanas;
    }

    public List<CatNormasConstruccionHasRetirosAumento> getAlturaMaxCerramiento() {
        return alturaMaxCerramiento;
    }

    public void setAlturaMaxCerramiento(List<CatNormasConstruccionHasRetirosAumento> alturaMaxCerramiento) {
        this.alturaMaxCerramiento = alturaMaxCerramiento;
    }

    public List<CatNormasConstruccionHasRetirosAumento> getdisposicionGeneral() {
        return disposicionGeneral;
    }

    public void setdisposicionGeneral(List<CatNormasConstruccionHasRetirosAumento> disposicionGeneral) {
        this.disposicionGeneral = disposicionGeneral;
    }

    public NormasConstruccionEdicion() {
    }

    public Long getIdNorma() {
        return idNorma;
    }

    public void setIdNorma(Long idNorma) {
        this.idNorma = idNorma;
    }

    public List<CatNormasConstruccionHasRetirosAumento> getDisposicionGeneral() {
        return disposicionGeneral;
    }

    public void setDisposicionGeneral(List<CatNormasConstruccionHasRetirosAumento> disposicionGeneral) {
        this.disposicionGeneral = disposicionGeneral;
    }

    public String getRetiros() {
        return retiros;
    }

    public void setRetiros(String retiros) {
        this.retiros = retiros;
    }

    public CatNormasConstruccionHasRetirosAumento getRetirosAumentos() {
        return retirosAumentos;
    }

    public void setRetirosAumentos(CatNormasConstruccionHasRetirosAumento retirosAumentos) {
        this.retirosAumentos = retirosAumentos;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public CatNormasConstruccion getNormaConstruccion() {
        return normaConstruccion;
    }

    public void setNormaConstruccion(CatNormasConstruccion normaConstruccion) {
        this.normaConstruccion = normaConstruccion;
    }

    public Boolean getHayImagen() {
        return hayImagen;
    }

    public void setHayImagen(Boolean hayImagen) {
        this.hayImagen = hayImagen;
    }

}
