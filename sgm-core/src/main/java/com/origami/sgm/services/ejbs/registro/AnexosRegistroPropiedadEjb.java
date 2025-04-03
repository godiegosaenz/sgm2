/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.registro;

import com.origami.config.SisVars;
import com.origami.sgm.bpm.models.DatoMercantilContrato;
import com.origami.sgm.bpm.models.DatoMercantilSocietario;
import com.origami.sgm.bpm.models.DatoPublicoRegistroPropiedad;
import com.origami.sgm.services.interfaces.registro.AnexosRegistroPropiedadServices;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import util.Utils;

/**
 *
 * @author Anyelo
 */
@Stateless(name = "anexosRegistro")
public class AnexosRegistroPropiedadEjb implements AnexosRegistroPropiedadServices {

    private String noCorresponde = "";
    private File archivo;
    private FileWriter fw;
    private BufferedWriter bw;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String formatoFecha() {
        Calendar cal = Calendar.getInstance();
        String fecha = cal.get(Calendar.YEAR) + "_" + (cal.get(Calendar.MONTH) + 1) + "_" + cal.get(Calendar.DAY_OF_MONTH);
        return fecha;
    }

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    /**
     * Este metodo del Ejb se encarga de generar los archivos de texto plano que
     * son los reportes que se envian a Diario a la Dinardap, del tipo de
     * inscripciones de Propiedades en el registro
     *
     * @param fechaInicio
     * @param nombreReporte
     * @throws IOException
     */
    @Override
    public void anexoDatoPublico(String fechaInicio, String nombreReporte) throws IOException {
        List<DatoPublicoRegistroPropiedad> list;
        String name = "RP_" + nombreReporte + "_SAMBORONDON";
        String line;
        try {
            //list = reg.consultaDinardapAnexoUno(fechaInicio, fechaFin);
            list = reg.consultaDinardapAnexoUno(fechaInicio);
            if (list != null) {
                archivo = new File(SisVars.rutaReportesDinardap + name + ".txt");
                if (archivo.exists()) {
                    archivo.delete();
                }

                //fw = new FileWriter(archivo, true);
                //bw = new BufferedWriter(fw);
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivo), "Cp1252"));

                line = "Apellidos|Nombres|Numero_Identificacion|Tipo_Compareciente|Razon_Social|Tipo_Contrato|"
                        + "Numero_Inscripcion|Fecha_Inscripcion|Clave_Catastral|Descripcion_Bien|Libro|Provincia|"
                        + "Zona|Superficie|Lindero_Orientacion|Lindero_Descripcion|Parroquia|Canton|Cuantia|"
                        + "Unidad_Cuantia|Identificador_Unico|Numero_Juicio|Estado|Ubicacion_Dato|Ultima_Modificacion|"
                        + "Notaria|Canton_Notaria|Fecha_Escritura";
                bw.write(line);
                bw.write("\r\n");
                //bw.newLine();
                for (DatoPublicoRegistroPropiedad d : list) {
                    if (d.getNombres() != null) {
                        d.setNombres(d.getApellidos());
                    }
                    if (d.getClavecatastral() == null) {
                        d.setClavecatastral(noCorresponde);
                    }
                    if (d.getTipopersona().equalsIgnoreCase("N")) {
                        d.setRazonsocial(noCorresponde);
                    } else {
                        d.setRazonsocial(d.getApellidos());
                    }
                    if (d.getZona() != null) {
                        if (d.getZona().equalsIgnoreCase("U")) {
                            d.setZona("Urbano");
                        } else if (d.getZona().equalsIgnoreCase("R")) {
                            d.setZona("Rural");
                        } else {
                            d.setZona(noCorresponde);
                        }
                    } else {
                        d.setZona(noCorresponde);
                    }
                    if (d.getSuperficie() != null) {
                    } else {
                        d.setSuperficie(noCorresponde);
                    }

                    if (d.getParroquia() != null) {
                    } else {
                        d.setParroquia(noCorresponde);
                    }
                    if (d.getLindero() != null) {
                        d.setLindero(Utils.quitarSaltos(d.getLindero()));
                        d.setLinderodescrip(d.getLindero());
                    } else {
                        d.setLindero(noCorresponde);
                        d.setLinderodescrip(noCorresponde);
                    }
                    if (d.getValoruuid() == null) {
                        d.setValoruuid(noCorresponde);
                    }
                    line = d.getApellidos() + "|" + d.getNombres() + "|" + d.getCi() + "|" + d.getTipocompareciente() + "|"
                            + d.getRazonsocial() + "|" + d.getTipocontrato() + "|" + d.getNuminscripcion() + "|"
                            + df.format(d.getFechainsripcion()) + "|" + d.getClavecatastral() + "|" + d.getDescripcionbien() + "|"
                            + d.getLibro() + "|" + d.getProvincia() + "|" + d.getZona() + "|" + d.getSuperficie() + "|"
                            + d.getLindero() + "|" + d.getLinderodescrip() + "|" + d.getParroquia() + "|" + d.getCanton() + "|"
                            + d.getCuantia() + "|" + d.getUnidad() + "|" + d.getValoruuid() + "|" + d.getNumjuicio() + "|"
                            + d.getEstado() + "|" + d.getUbicaciondato() + "|" + df.format(d.getUltimamodificacion()) + "|"
                            + d.getNotaria() + "|" + d.getCantonnotaria() + "|" + df.format(d.getFechaescritura());

                    line = Utils.quitarSaltos(line);
                    bw.write(line);
                    bw.write("\r\n");
                }
                bw.flush();
                bw.close();
            }
        } catch (Exception e) {
            Logger.getLogger(AnexosRegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Este metodo del Ejb se encarga de generar los archivos de texto plano que
     * son los reportes que se envian a Diario a la Dinardap, del tipo de
     * contratos del Registro Mercantil en el registro de la propiedad
     *
     * @param fechaInicio
     * @param fechaFin
     * @throws IOException
     */
    @Override
    public void anexoMercantilContratos(String fechaInicio, String fechaFin) throws IOException {
        List<DatoMercantilContrato> list;
        String name = "RM_MC_" + this.formatoFecha() + "_SAMBORONDON";
        String line;
        try {
            list = reg.consultaDinardapAnexoDos(fechaInicio, fechaFin);
            if (list != null) {
                archivo = new File(SisVars.rutaReportesDinardap + name + ".txt");
                if (archivo.exists()) {
                    archivo.delete();
                }
                fw = new FileWriter(archivo, true);
                bw = new BufferedWriter(fw);
                line = "Apellidos|Nombres|Numero_Identificacion|Tipo_Compareciente|Razon_Social|Tipo_Contrato|"
                        + "Fecha_Inscripcion|Numero_Inscripcion|Representante|Fecha_Cancelacion|Chasis_Serie|"
                        + "Motor|Marca|Modelo|Año_Fabricacion|Placa|Ubicacion_Dato|Ultima_Modificacion|"
                        + "Identifador_Unico|Notaria|Canton_Notaria|Fecha_Escritura|Estado_Inscripcion";
                bw.write(line);
                bw.newLine();
                for (DatoMercantilContrato d : list) {
                    if (d.getNombres() == null) {
                        d.setNombres(d.getApellidos());
                    }
                    if (d.getRazonsocial() == null) {
                        d.setRazonsocial(d.getApellidos());
                    }
                    if (d.getRepresentante() == null || d.getRepresentante().isEmpty()) {
                        d.setRepresentante(noCorresponde);
                    }
                    if (d.getTipobien() == null) {
                        d.setTipobien(noCorresponde);
                    }
                    if (d.getChasis() == null) {
                        d.setChasis(noCorresponde);
                    }
                    if (d.getEstado().trim().equalsIgnoreCase("AC")) {
                        d.setEstado("Vigente");
                    } else {
                        d.setEstado("No Vigente");
                    }
                    if (d.getValoruuid() == null) {
                        d.setValoruuid(noCorresponde);
                    }
                    line = d.getApellidos() + "|" + d.getNombres() + "|" + d.getCi() + "|" + d.getTipocompareciente() + "|"
                            + d.getRazonsocial() + "|" + d.getTipocontrato() + "|" + df.format(d.getFechainsripcion()) + "|"
                            + d.getNuminscripcion() + "|" + d.getRepresentante() + "|" + df.format(d.getFechacancelacion()) + "|"
                            + d.getTipobien() + "|" + d.getChasis() + "|" + d.getMotor() + "|" + d.getMarca() + "|" + d.getModelo() + "|"
                            + d.getAniofabrica() + "|" + d.getPlaca() + "|" + d.getUbicaciondato() + "|" + df.format(d.getUltimamodificacion()) + "|"
                            + d.getValoruuid() + "|" + d.getNotaria() + "|" + d.getCantonnotaria() + "|"
                            + df.format(d.getFechaescritura()) + "|" + d.getEstado();
                    line = Utils.quitarSaltos(line);
                    bw.write(line);
                    bw.newLine();
                }
                bw.flush();
                bw.close();
            }
        } catch (Exception e) {
            Logger.getLogger(AnexosRegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Este metodo del Ejb se encarga de generar los archivos de texto plano que
     * son los reportes que se envian a Diario a la Dinardap, del tipo de
     * inscripciones del Registro Mercantil en el registro de la propiedad
     *
     * @param fechaInicio
     * @param fechaFin
     * @throws IOException
     */
    @Override
    public void anexoMercantilSociedad(String fechaInicio, String fechaFin) throws IOException {
        List<DatoMercantilSocietario> list;
        String name = "RM_MS_" + this.formatoFecha() + "_SAMBORONDON";
        String line;
        try {
            list = reg.consultaDinardapAnexoTres(fechaInicio, fechaFin);
            if (list != null) {
                archivo = new File(SisVars.rutaReportesDinardap + name + ".txt");
                if (archivo.exists()) {
                    archivo.delete();
                }
                fw = new FileWriter(archivo, true);
                bw = new BufferedWriter(fw);
                line = "Nombre_Compañia|Numero_Identificacion|Especie_Compañia|Fecha_Inscripcion|"
                        + "Apellidos_Compareciente|Nombre_Compareciente|Cedula_Compareciente|Cargo|"
                        + "Disposicion|Autoridad_Emitio_Acto|Fecha_Disposicion|Numero_Disposicion|"
                        + "Fecha_Escritura|Notaria|Canton_Notaria|Tipo_Tramite|Ubicacion_Dato|"
                        + "Ultima_Modificacion|Identificador_Unico|Estado_Inscripcion";
                bw.write(line);
                bw.newLine();
                for (DatoMercantilSocietario d : list) {
                    if (d.getApellidoscompareciente() == null) {
                        d.setApellidoscompareciente(noCorresponde);
                    }
                    if (d.getNombrescompareciente() == null) {
                        d.setNombrescompareciente(noCorresponde);
                    }
                    if (d.getCicompareciente() == null) {
                        d.setCicompareciente(noCorresponde);
                    }
                    if (d.getCargo() == null) {
                        d.setCargo(noCorresponde);
                    }
                    if (d.getTipocompareciente() == null) {
                        d.setTipocompareciente(noCorresponde);
                    }
                    if (d.getEstado().trim().equalsIgnoreCase("AC")) {
                        d.setEstado("Vigente");
                    } else {
                        d.setEstado("No Vigente");
                    }
                    if (d.getValoruuid() == null) {
                        d.setValoruuid(noCorresponde);
                    }
                    line = d.getNombrecompania() + "|" + d.getCi() + "|" + d.getEspecie() + "|"
                            + d.getFechainscripcion() + "|" + d.getApellidoscompareciente() + "|"
                            + d.getNombrescompareciente() + "|" + d.getCicompareciente() + "|"
                            + d.getCargo() + "|" + d.getDisposicion() + "|" + d.getAutoridad() + "|"
                            + df.format(d.getFechadisposicion()) + "|" + d.getNumdisposicion() + "|"
                            + df.format(d.getFechaescritura()) + "|" + d.getNotaria() + "|"
                            + d.getCantonnotaria() + "|" + d.getTipotramite() + "|" + d.getUbicaciondato() + "|"
                            + df.format(d.getUltimamodificacion()) + "|" + d.getValoruuid() + "|" + d.getEstado();
                    line = Utils.quitarSaltos(line);
                    bw.write(line);
                    bw.newLine();
                }
                bw.flush();
                bw.close();
            }
        } catch (Exception e) {
            Logger.getLogger(AnexosRegistroPropiedadEjb.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
