/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.app;

import com.origami.config.MainConfig;
import com.origami.config.SisVars;
import com.origami.sgm.predio.models.ModelLockPredio;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author Fernando
 */
@Named
@ApplicationScoped
public class AppConfig {

    private Map<String, ModelLockPredio> prediosedicion = new HashMap<>();
    Map<String, String> camposficha = new HashMap<>();
    private MainConfig mainConfig = new MainConfig();

    public AppConfig() {
    }

    public String getSystemLogo() {
        return SisVars.sisLogo;
    }

    public String getUrlbase() {
        return SisVars.urlbase;
    }

    public String getFacesUrl() {
        return SisVars.facesUrl;
    }

    public String getUrlbaseFaces() {
        return SisVars.urlbaseFaces;
    }

    public String getGeoserverUrl() {
        return SisVars.geoserverUrl;
    }

    public String getGeoserverProxyUrl() {
        return SisVars.geoserverProxyUrl;
    }

    public String getFormatoArchivos() {
        return SisVars.formatoArchivos;
    }

    public String getBackgroundVar() {
        return SisVars.varbackground;
    }

    public Map<String, ModelLockPredio> getPrediosedicion() {
        return prediosedicion;
    }

    public void setPrediosedicion(Map<String, ModelLockPredio> prediosedicion) {
        this.prediosedicion = prediosedicion;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public void setMainConfig(MainConfig mainConfig) {
        this.mainConfig = mainConfig;
    }

    /**
     * Envia a bloquear el predio.
     *
     * @param user
     * @param idPredio
     */
    public void lockPredio(String user, Long idPredio) {
        ModelLockPredio lock = new ModelLockPredio();
        lock.setFechaBloqueo(new Date());
        lock.setIdpredio(idPredio);
        this.prediosedicion.put(user, lock);
    }

    /**
     * Devuelve <code>True</code> si esta bloqueado, caso contrario
     * <code>False</code>
     *
     * @param user
     * @param idPredio
     * @return
     */
    public boolean isLocked(String user, Long idPredio) {
        ModelLockPredio lock = new ModelLockPredio();
        lock.setFechaBloqueo(new Date());
        lock.setIdpredio(idPredio);
        ModelLockPredio get = this.prediosedicion.get(user);
        if (get != null) {
            if (get.equals(lock)) {
                this.lockPredio(user, idPredio);
                return false;
            }
        }

        return this.prediosedicion.containsValue(lock);
    }

    public List<Entry<String, ModelLockPredio>> getEntrys() {
        return new ArrayList(prediosedicion.entrySet());
    }

    public void cargarCampos() {
        this.camposficha.put("provincia", "Provincia");
        this.camposficha.put("sector", "Sector");
        this.camposficha.put("canton", "Cantón");
        this.camposficha.put("parroquia", "Parroquia");
        this.camposficha.put("mz", "Manzana ");
        this.camposficha.put("solar", "Lote");
        this.camposficha.put("zona", "Zona");
        this.camposficha.put("nombre", "Nombre del espacio urbano / rural"); ///CTLLG ITEM REVISAR EN ESOS CASOS, no reflejo cambio propietario, area grafica,frente1,fondo,estadoSolar no se cargan en el primer cambio
        this.camposficha.put("nombreEdificio", "Nombre del Predio o Edificacion");//no cambios en linderos,avaluoPlusmunicipal
        this.camposficha.put("tipoPredio", "Tipo de predio");
        this.camposficha.put("estado", "Estado Predio");
        this.camposficha.put("calle", "Calle Principal");
        this.camposficha.put("calleS", "Calle Secundaria");
        this.camposficha.put("numeroVivienda", "Placa Domiciliaria");
        this.camposficha.put("numDepartamento", "Descripcion (PH)");
        this.camposficha.put("coordx", "Coordenadas X (Latitud)");
        this.camposficha.put("coordy", "Coordenada Y (Longitud)");
        this.camposficha.put("urbMz", "Mz (Plano Aprobado)");
        this.camposficha.put("urbSolarnew", "Lote (Plano Aprobado)");
        this.camposficha.put("piso", "Planta (Piso PH)");
        this.camposficha.put("claveCat", "Clave Catastral");
        this.camposficha.put("usosList", "Uso del Predio");
        this.camposficha.put("formaAdquisicion", "Forma de Adquisición o Tenencia");
        this.camposficha.put("requierePerfeccionamiento", "Requiere Perfeccionamiento ");
        this.camposficha.put("aniosPosesion", "Años en Posesión");
        this.camposficha.put("nombrePuebloEtnia", "Nombre Pueblo/Etnia");
        this.camposficha.put("aniosSinPerfeccionamiento", "Años sin Perfeccionamiento");
        this.camposficha.put("tipoPoseedor", "Tipo de Poseedor");
        this.camposficha.put("catEscrituraCollection", "Escrituras");
        this.camposficha.put("areaGrafica", "Área Gráfica");
        this.camposficha.put("frente1", "Frente");
        this.camposficha.put("fondo1", "Fondo Relativo");
        this.camposficha.put("locManzana", "Localizacion en Mz");
        this.camposficha.put("estadoSolar", "Ocupacion ");
        this.camposficha.put("tipoSuelo", "Caract. Suelo");
        this.camposficha.put("clasificacionSuelo", "Clasificación del suelo");
        this.camposficha.put("topografiaSolar", "Topografia");
        this.camposficha.put("formaSolar", "Forma del predio");
        this.camposficha.put("constructividad", "Constructividad");
        this.camposficha.put("usoVia", "Uso de Vía");
        this.camposficha.put("tipovia", "Tipo de Vía");
        this.camposficha.put("abastAgua", "Agua Potable");
        this.camposficha.put("abastEnergia", "Energia Electrica");
        this.camposficha.put("rodadura", "Material de Rodadura");
        this.camposficha.put("tieneAguaPotable", "Medidor de Agua Potable");
        this.camposficha.put("medidorEe", "Medidor energia");
        this.camposficha.put("evacAguasServ", "Alcantarillado Sanitario");
        this.camposficha.put("numMedidoresAgua", "No. Medidor agua ");
        this.camposficha.put("numMedElect", "No. Medidor");
        this.camposficha.put("tpublico", "Transporte Público");
        this.camposficha.put("tieneTelfFijo", "Red Telefónica");
        this.camposficha.put("tieneInternet", "Internet");
        this.camposficha.put("recoleccionbasura", "Recolección Basura");
        this.camposficha.put("telefoniasatelital", "Telefonía Satelital");
        this.camposficha.put("tieneBordillo", "Bordillos");
        this.camposficha.put("alarmas_comunitarias", "Alarmas Comunitarias");
        this.camposficha.put("alumbrado", "Alumbrado Público");
        this.camposficha.put("aseoCalles", "Aseo calles");
        this.camposficha.put("tvPag", "Tv. Pagada");
        this.camposficha.put("tieneAceras", "Aceras");
        this.camposficha.put("catPredioCultivoCollection", "Cultivos");
        this.camposficha.put("catPredioBloqueCollection", "Bloques Constructivos");
        this.camposficha.put("catPredioEdificacionCollection", "Bloques Constructivos");
        this.camposficha.put("catPredioEdificacionPropCollection", "Caracteristicas del Bloques Constructivos");
        this.camposficha.put("catEdificacionPisosDetCollection", "Nivel del Bloques Constructivos");
        this.camposficha.put("catPredioPropietarioCollection", "Propietario");
        this.camposficha.put("areaDeclaradaConst", "Area Declarada Construccion");
        this.camposficha.put("catPredioObraInternaCollection", "Obras Internas o Complementarias");
        this.camposficha.put("clasificacionVivienda", "Clasificacion Vivienda");
        this.camposficha.put("condicionVivienda", "Condicion Vivienda");
        this.camposficha.put("tipoVivienda", "Tipo Vivienda");
        this.camposficha.put("tenenciaVivienda", "Tenencia de Vivienda");
        this.camposficha.put("numHabitaciones", "Numero de Habitaciones");
        this.camposficha.put("numDormitorios", "Numero de Dormitorios");
        this.camposficha.put("numEspaciosBanios", "Espacios Para Bañarse o Duchas");
        this.camposficha.put("habitantes", "Numero de Habitantes");
        this.camposficha.put("numHogares", "Numero de Hogares");
        this.camposficha.put("numCelulares", "Numero de Telefonos Celulares");
        this.camposficha.put("vivCencalPoseeTelfConvencional", "Posee Telefono Convencional");
        this.camposficha.put("vivCencalServInternet", "Servicio de Internet");
        this.camposficha.put("ciuHorizontal", "CI/RUC Jefe de Hogar");
        this.camposficha.put("ciuNombresHorizontal", "Nombres Completos Jefe de Hogar");
        this.camposficha.put("ciRucInformante", "CI/RUC INFORMANTE");
        this.camposficha.put("nombreInformante", "Nombre Informante");
        this.camposficha.put("apellidosInformante", "Apellidos Informante");
        this.camposficha.put("observaciones", "Observaciones");
        this.camposficha.put("ciuActualizador", "CI/RUC Actualizador");
        this.camposficha.put("ciuNombresActualizador", "Nombres Completos Actualizador");
        this.camposficha.put("ciuFiscalizador", "CI/RUC Fiscalizador");
        this.camposficha.put("ciuNombresFiscalizador", "Nombres Completos Fiscalizador");
        this.camposficha.put("numPredio", "Codigo Unico");
        this.camposficha.put("areaCultivos", "Area Cultivos");
        this.camposficha.put("areaSolar", "Area Terreno");
        this.camposficha.put("areaObras", "Area Obras Complementarias");
        this.camposficha.put("subsector", "Eje de Valor");
        this.camposficha.put("areaAccesoPriv", "Area de acceso privado");
        this.camposficha.put("riesgo", "Tipo de Riesgos");
        this.camposficha.put("disponiblidadRiego", "Disponibilidad de Riego");
        this.camposficha.put("metodoRiego", "Metodo de Riego");
        this.camposficha.put("vivCencalAcabadoPiso", "Acabado Piso");
        this.camposficha.put("vivCencalEstadoAcabadoPiso", "Estado Acabado Piso");
        this.camposficha.put("abastAguaRecibe", "Agua Recibe");
        this.camposficha.put("recolBasura", "Eliminacion de basura");
        this.camposficha.put("abasteElectrico", "Energia Elecrtica Proviene");
        this.camposficha.put("catPredioClasificRuralCollection", "Clasificacion de Suelo ");
        this.camposficha.put("avaluoSolar", "Avaluo Terreno ");
        this.camposficha.put("avaluoObraComplement", "Avaluo Obras Complementarias");
        this.camposficha.put("avaluoMunicipal", "Valor de la Propiedad");
        this.camposficha.put("avaluoConstruccion", "Avaluo Construcción");
        this.camposficha.put("avaluoCultivos", "Avaluo Cultivos ");
        this.camposficha.put("baseImponible", "Base Imponible");
        this.camposficha.put("avaluoPlussolar", "Avaluo Plsuvalia Terreno");
        this.camposficha.put("avaluoPluObraComplement", "Avaluo Plsuvalia Obras Complementarias");
        this.camposficha.put("avaluoPlusmunicipal", "Avaluo Plsuvalia Valor de la Propiedad");
        this.camposficha.put("avaluoPlusconstruccion", "Avaluo Plsuvalia Construccion");
        this.camposficha.put("avaluoPluscultivos", "Avaluo Plsuvalia Cultivos");
        this.camposficha.put("tipoConjunto", "Espacio Urbano / Rural");
        this.camposficha.put("propiedad", "Tenencia");
        this.camposficha.put("constructividad", "Constructividad");
        this.camposficha.put("nombreUrb", "Nombre del espacio urbano / rural");
        this.camposficha.put("predialant", "Clave Anterior");
        this.camposficha.put("fecEscritura", "Fecha Escritura (Fecha Protocolizacion)");
        this.camposficha.put("", "");

    }

    public String retornarNombreCampo(String campo) {
        String nombre;
        this.cargarCampos();
        nombre = this.camposficha.get(campo);
        if (nombre == null) {
            return campo;
        }

        return nombre;
    }

}
