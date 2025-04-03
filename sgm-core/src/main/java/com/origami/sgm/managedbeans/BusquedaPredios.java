/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.config.SisVars;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBase;
import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.bpm.models.PagoTituloReporteModel;
import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.FnConvenioPago;
import com.origami.sgm.entities.FnConvenioPagoDetalle;
import com.origami.sgm.entities.FnExoneracionLiquidacion;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.lazymodels.CatPredioRusticoLazy;
import com.origami.sgm.lazymodels.PropietariosLazy;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import util.Archivo;
import util.JsfUti;
import util.MessagesRentas;
import util.Utils;

/**
 *
 * @author Joao Sanga
 */
public class BusquedaPredios {

    @javax.inject.Inject
    private Entitymanager entityManager;
    @javax.inject.Inject
    private RecaudacionesService recaudacionServices;

    @javax.inject.Inject
    private RentasServices services;

    protected List<RenLiquidacion> emisionesPrediales;
    protected CatPredio predioConsulta;
    private List<CatPredio> prediosConsulta;
    private PagoTituloReporteModel modelPago;
    private Boolean pagoRealizado = Boolean.FALSE;
    private BigDecimal totalEmisionesGeneral;
    protected BigDecimal totalEmisiones;
    private Long tipoConsulta = 6L;
    protected CatPredioModel predioModel;
    private Map<String, Object> paramtr;
    private FnSolicitudExoneracion exoneracion;
    protected String mensajeExoneracion, tipoExoneracionParametro;
    private Boolean variosPagos = Boolean.FALSE;
    private CatEnte contribuyenteConsulta;

    private PropietariosLazy propietarios;
    private CatPredioRusticoLazy propietariosRustico;

    protected Long tipoConsultaRural = 1L;
    private List<CatPredioRustico> prediosRusticoConsulta;
    private CatPredioRustico predioRuralConsulta;

    private Boolean controlDocumento = Boolean.FALSE;

    private String tabName;

    protected BigDecimal sumaTotalConv;
    protected List<FnConvenioPagoDetalle> convenidos;
    protected FnConvenioPagoDetalle cpd;
    protected List<RenLiquidacion> cuotasPredios;
    protected Boolean tieneConvenio;

    /**
     * PARA SAN MIGUEL DEBE ESTAR EN isSanMiguel = Boolean.TRUE selectionMode =
     * "multiple";
     *
     */
    private Boolean isSanMiguel = Boolean.FALSE;
    private String selectionMode = "single";

    public BusquedaPredios() {
        propietarios = new PropietariosLazy(Boolean.TRUE);
        predioModel = new CatPredioModel();
        modelPago = new PagoTituloReporteModel();
    }

    public void consultarEmisiones() {
        RenLiquidacion liq;

        emisionesPrediales = null;
        predioConsulta = null;
        prediosConsulta = null;
        //modelPago = new PagoTituloReporteModel();
        modelPago = new PagoTituloReporteModel(new BigDecimal("0.00"), this.variosPagos, this.modelPago.getPagoNotaCredio(), this.modelPago.getPagoCheque(), this.modelPago.getPagoTarjetaCredito(), this.modelPago.getPagoTransferencia());

        pagoRealizado = Boolean.FALSE;
        totalEmisionesGeneral = null;
        totalEmisiones = null;
        try {
            switch (tipoConsulta.intValue()) {
                case 1://NUMERO PREDIAL
                    if (predioModel.getNumPredio() != null && predioModel.getNumPredio().compareTo(BigInteger.ZERO) > 0) {
                        paramtr = new HashMap<>();
                        paramtr.put("numPredio", predioModel.getNumPredio());
                        paramtr.put("estado", "A");
                        predioConsulta = (CatPredio) entityManager.findObjectByParameter(CatPredio.class, paramtr);
                    } else {
                        JsfUti.messageError(null, "Error", "Numero de Predio no es valido.");
                    }
                    break;
                case 2://CONTRIBUYENTE
                    if (contribuyenteConsulta != null) {
                        if (contribuyenteConsulta.getCatPredioPropietarioCollection() != null && !contribuyenteConsulta.getCatPredioPropietarioCollection().isEmpty()) {
                            if (contribuyenteConsulta.getCatPredioPropietarioCollection().size() == 1) {
                                paramtr = new HashMap<>();
                                paramtr.put("numPredio", contribuyenteConsulta.getCatPredioPropietarioCollection().get(0).getPredio().getNumPredio());
                                paramtr.put("estado", "A");
                                predioConsulta = (CatPredio) entityManager.findObjectByParameter(CatPredio.class, paramtr);
                            } else {
                                prediosConsulta = new ArrayList<>();
                                for (CatPredioPropietario p : contribuyenteConsulta.getCatPredioPropietarioCollection()) {
                                    if (p.getPredio().getPropiedadHorizontal() == null || !p.getPredio().getPropiedadHorizontal()) //PARA Q NO APARESCAN LAS PROPIEDADES HORIZONTALES
                                    {
                                        prediosConsulta.add(p.getPredio());
                                    }
                                }
                            }
                        } else {
                            JsfUti.messageInfo(null, "Contribuyente no posee Predios, o son Rurales", "");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Realice la busqueda del Contribuyente.");
                    }
                    break;
                case 3://CODIGO PREDIAL
                    if (predioModel.getSector() > 0 || predioModel.getMz() > 0 || predioModel.getProvincia() > 0 || predioModel.getCanton() > 0
                            || predioModel.getParroquiaShort() > 0 || predioModel.getZona() > 0 || predioModel.getSolar() > 0 || predioModel.getPiso() >= 0
                            || predioModel.getUnidad() >= 0 || predioModel.getBloque() >= 0) {
                        paramtr = new HashMap<>();
                        paramtr.put("estado", "A");
                        paramtr.put("sector", predioModel.getSector());
                        paramtr.put("mz", predioModel.getMz());
                        paramtr.put("provincia", predioModel.getProvincia());
                        paramtr.put("canton", predioModel.getCanton());
                        paramtr.put("parroquia", predioModel.getParroquiaShort());
                        paramtr.put("zona", predioModel.getZona());
                        paramtr.put("solar", predioModel.getSolar());
                        paramtr.put("piso", predioModel.getPiso());
                        paramtr.put("unidad", predioModel.getUnidad());
                        paramtr.put("bloque", predioModel.getBloque());

                        prediosConsulta = entityManager.findObjectByParameterList(CatPredio.class, paramtr);
                        if (prediosConsulta != null && !prediosConsulta.isEmpty()) {
                            if (prediosConsulta.size() == 1) {
                                predioConsulta = prediosConsulta.get(0);
                            }
                        } else {
                            JsfUti.messageInfo(null, "Predio no encontrado.", "");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Codigo Predial no es valido.");
                    }
                    break;
                case 4:
                    if (predioModel.getCiudadela() != null && predioModel.getMzUrb() != null && predioModel.getSlUrb() != null) {
                        paramtr = new HashMap<>();
                        paramtr.put("estado", "A");
                        paramtr.put("ciudadela", predioModel.getCiudadela());
                        paramtr.put("urbMz", predioModel.getMzUrb());
                        paramtr.put("urbSolarnew", predioModel.getSlUrb());
                        prediosConsulta = entityManager.findObjectByParameterList(CatPredio.class, paramtr);
                        if (prediosConsulta != null && !prediosConsulta.isEmpty()) {
                            if (prediosConsulta.size() == 1) {
                                predioConsulta = prediosConsulta.get(0);
                            }
                        } else {
                            JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");
                    }
                    break;
                case 5://CODIGO ANTERIOR
                    if (predioModel.getPredialAnt() != null) {
                        paramtr = new HashMap<>();
                        paramtr.put("estado", "A");
                        paramtr.put("predialant", predioModel.getPredialAnt());
                        prediosConsulta = entityManager.findObjectByParameterList(CatPredio.class, paramtr);
                        if (prediosConsulta != null && !prediosConsulta.isEmpty()) {
                            if (prediosConsulta.size() == 1) {
                                predioConsulta = prediosConsulta.get(0);
                            }
                        } else {
                            JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");
                    }
                    break;
                case 6:
                    if (predioModel.getClaveCat() != null) {
                        paramtr = new HashMap<>();
                        paramtr.put("claveCat", predioModel.getClaveCat());
                        paramtr.put("estado", "A");
                        prediosConsulta = entityManager.findObjectByParameterList(CatPredio.class, paramtr);
                        if (prediosConsulta == null && prediosConsulta.isEmpty()) {
                            JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
                        } else {
                            predioConsulta = prediosConsulta.get(0);
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");
                    }
                    break;
                default:
                    break;
            }
            if (predioConsulta != null) {
//                paramt = new HashMap<>();
//                paramt.put("tipoLiquidacion", new RenTipoLiquidacion(13L));
//                paramt.put("predio", predioConsulta);
//                emisionesPredialesList = entityManager.findObjectByParameterOrderList(RenLiquidacion.class, paramt, new String[]{"anio"}, Boolean.TRUE);
                if (!isSanMiguel) {
                    emisionesPrediales = entityManager.findAll(QuerysFinanciero.obtenerLiquidacionesPrediales, new String[]{"tipoLiquidacion", "predio"}, new Object[]{new RenTipoLiquidacion(13L), predioConsulta});
                } else {
                    emisionesPrediales = entityManager.findAll(QuerysFinanciero.obtenerLiquidacionesPredialesPendientes, new String[]{"tipoLiquidacion", "predio"}, new Object[]{new RenTipoLiquidacion(13L), predioConsulta});
                }

                // BUSCA SI TIENE SOLICITUD PENDIENTE DE EXONERACION --JOAO
                // BUSCA SOLICITUDES DE EXONERACION POR PREDIO EN ESTADO 1 o 2, DE LEY DEL ANCIANO O DISCAPACIDAD, (LA MAS ATUAL) --HENRY
                //exoneracion = (FnSolicitudExoneracion) entityManager.find(QuerysFinanciero.buscarExoneracionTerceraEdadYDiscapacitadoPorPredio, new String[]{"predio"}, new Object[]{predioConsulta});
                exoneracion = (FnSolicitudExoneracion) entityManager.find(QuerysFinanciero.buscarExoneracionPorPredio, new String[]{"predio"}, new Object[]{predioConsulta});

                if (exoneracion != null) {
                    //LA CONSULTA ANTERIOR VERIFICA QUE LOS ESTADOS SEAN (1,2)
                    if (exoneracion.getEstado().getId() == 3L || exoneracion.getEstado().getId() == 4L) {
                        exoneracion = null;
                        mensajeExoneracion = null;
                        tipoExoneracionParametro = null;
                    } else {
                        mensajeExoneracion = "Tiene una exoneración de: " + exoneracion.getExoneracionTipo().getDescripcion().toUpperCase()
                                + "\nNúmero de resolución: " + exoneracion.getNumResolucionSac();
                        this.setTipoExoneracionParametro("TIENE UNA EXONERACIÒN : " + exoneracion.getExoneracionTipo().getDescripcion().toUpperCase());
                        //LA CONSULTA ANTERIOR VERIFICA QUE LOS TIPO SON (17,18,37,44)
                        //if (exoneracion.getExoneracionTipo().getId() == 17L || exoneracion.getExoneracionTipo().getId() == 18L || 
//                                exoneracion.getExoneracionTipo().getId() == 37L || exoneracion.getExoneracionTipo().getId() == 44L) {
                        //if(exoneracion.getExoneracionTipo().getId() == 17L || exoneracionSolicitud.getExoneracionTipo().getId() == 37L){
                        JsfUti.update("formMensajeExo");
                        JsfUti.executeJS("PF('dlgMensajeExo').show()");
//                        } else {
//                            exoneracion = null;
//                            mensajeExoneracion = null;
//                        }
                    }
                }
                calculoTotalPago(emisionesPrediales, null);
                cuotasConvenio();
                totalEmisionesGeneral = new BigDecimal(totalEmisiones.toString());
                // ENVIA MOSTRAR ADVERTENCIA SI EL PAGO YA FUE REALIZADO POR MEDIO DEL BANCO
//                if (tipoLiquidacion.getId().equals(13L)) {
                try {
                    if (totalEmisionesGeneral.compareTo(BigDecimal.ZERO) > 0) {
                        if (recaudacionServices.verificarPagoBanco(predioConsulta)) {
                            JsfUti.executeJS("PF('dlgPagoBanco').show()");
                        }
                    }
                } catch (Exception e) {
                    Logger.getLogger(e.toString());
                }
//                }

            } else {
                //DIALOGO DE SELECCION DE PREDIOS
                if (prediosConsulta == null && prediosConsulta.isEmpty()) {
                    JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
                }
                if (prediosConsulta != null && prediosConsulta.size() > 1) {
                    JsfUti.update("frmPredios");
                    JsfUti.executeJS("PF('dlgPrediosConsulta').show();");
                }
            }
            if (emisionesPrediales.isEmpty()) {
                JsfUti.messageInfo(null, "Mensaje", "Predio No posee Deuda");
            }

        } catch (Exception e) {
            Logger.getLogger(e.toString());
        }

    }

    public void onSelectCuota(SelectEvent event) {
        cpd = (FnConvenioPagoDetalle) event.getObject();
        cuotasPredios = new ArrayList<>();
        sumaTotalConv = new BigDecimal("0.00");
        for (FnConvenioPagoDetalle d : convenidos) {
            if (d.getMes() <= cpd.getMes()) {
                if (d.getLiquidacion().getEstadoLiquidacion().getId() == 8L) {
                    cuotasPredios.add(d.getLiquidacion());
                    sumaTotalConv = sumaTotalConv.add(d.getDeuda());
                }
            }
        }
        JsfUti.update("mainForm:tvRecaudaciones:tvRecaudacionesPredios:panelConvenios");
    }

    public void consultarEmisionesPendientesPago() {
        RenLiquidacion liq;

        emisionesPrediales = null;
        predioConsulta = null;
        prediosConsulta = null;
        //modelPago = new PagoTituloReporteModel();
        modelPago = new PagoTituloReporteModel(new BigDecimal("0.00"), this.variosPagos, this.modelPago.getPagoNotaCredio(), this.modelPago.getPagoCheque(), this.modelPago.getPagoTarjetaCredito(), this.modelPago.getPagoTransferencia());

        pagoRealizado = Boolean.FALSE;
        totalEmisionesGeneral = null;
        totalEmisiones = null;
        try {
            switch (tipoConsulta.intValue()) {
                case 1://NUMERO PREDIAL
                    if (predioModel.getNumPredio() != null && predioModel.getNumPredio().compareTo(BigInteger.ZERO) > 0) {
                        paramtr = new HashMap<>();
                        paramtr.put("numPredio", predioModel.getNumPredio());
                        paramtr.put("estado", "A");
                        predioConsulta = (CatPredio) entityManager.findObjectByParameter(CatPredio.class, paramtr);
                    } else {
                        JsfUti.messageError(null, "Error", "Numero de Predio no es valido.");
                    }
                    break;
                case 2://CONTRIBUYENTE
                    if (contribuyenteConsulta != null) {
                        if (contribuyenteConsulta.getCatPredioPropietarioCollection() != null && !contribuyenteConsulta.getCatPredioPropietarioCollection().isEmpty()) {
                            if (contribuyenteConsulta.getCatPredioPropietarioCollection().size() == 1) {
                                paramtr = new HashMap<>();
                                paramtr.put("numPredio", contribuyenteConsulta.getCatPredioPropietarioCollection().get(0).getPredio().getNumPredio());
                                paramtr.put("estado", "A");
                                predioConsulta = (CatPredio) entityManager.findObjectByParameter(CatPredio.class, paramtr);
                            } else {
                                prediosConsulta = new ArrayList<>();
                                for (CatPredioPropietario p : contribuyenteConsulta.getCatPredioPropietarioCollection()) {
                                    if (p.getPredio().getPropiedadHorizontal() == null || !p.getPredio().getPropiedadHorizontal()) //PARA Q NO APARESCAN LAS PROPIEDADES HORIZONTALES
                                    {
                                        prediosConsulta.add(p.getPredio());
                                    }
                                }
                            }
                        } else {
                            JsfUti.messageInfo(null, "Contribuyente no posee Predios", "");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Realice la busqueda del Contribuyente.");
                    }
                    break;
                case 3://CODIGO PREDIAL
                    if (predioModel.getSector() > 0 || predioModel.getMz() > 0 || predioModel.getProvincia() > 0 || predioModel.getCanton() > 0
                            || predioModel.getParroquiaShort() > 0 || predioModel.getZona() > 0 || predioModel.getSolar() > 0 || predioModel.getPiso() >= 0
                            || predioModel.getUnidad() >= 0 || predioModel.getBloque() >= 0) {
                        paramtr = new HashMap<>();
                        paramtr.put("estado", "A");
                        paramtr.put("sector", predioModel.getSector());
                        paramtr.put("mz", predioModel.getMz());
                        paramtr.put("provincia", predioModel.getProvincia());
                        paramtr.put("canton", predioModel.getCanton());
                        paramtr.put("parroquia", predioModel.getParroquiaShort());
                        paramtr.put("zona", predioModel.getZona());
                        paramtr.put("solar", predioModel.getSolar());
                        paramtr.put("piso", predioModel.getPiso());
                        paramtr.put("unidad", predioModel.getUnidad());
                        paramtr.put("bloque", predioModel.getBloque());

                        prediosConsulta = entityManager.findObjectByParameterList(CatPredio.class, paramtr);
                        if (prediosConsulta != null && !prediosConsulta.isEmpty()) {
                            if (prediosConsulta.size() == 1) {
                                predioConsulta = prediosConsulta.get(0);
                            }
                        } else {
                            JsfUti.messageInfo(null, "Predio no encontrado.", "");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Codigo Predial no es valido.");
                    }
                    break;
                case 4:
                    if (predioModel.getCiudadela() != null && predioModel.getMzUrb() != null && predioModel.getSlUrb() != null) {
                        paramtr = new HashMap<>();
                        paramtr.put("estado", "A");
                        paramtr.put("ciudadela", predioModel.getCiudadela());
                        paramtr.put("urbMz", predioModel.getMzUrb());
                        paramtr.put("urbSolarnew", predioModel.getSlUrb());
                        prediosConsulta = entityManager.findObjectByParameterList(CatPredio.class, paramtr);
                        if (prediosConsulta != null && !prediosConsulta.isEmpty()) {
                            if (prediosConsulta.size() == 1) {
                                predioConsulta = prediosConsulta.get(0);
                            }
                        } else {
                            JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");
                    }
                    break;
                case 5://CODIGO ANTERIOR
                    if (predioModel.getPredialAnt() != null) {
                        paramtr = new HashMap<>();
                        paramtr.put("estado", "A");
                        paramtr.put("predialant", predioModel.getPredialAnt());
                        prediosConsulta = entityManager.findObjectByParameterList(CatPredio.class, paramtr);
                        if (prediosConsulta != null && !prediosConsulta.isEmpty()) {
                            if (prediosConsulta.size() == 1) {
                                predioConsulta = prediosConsulta.get(0);
                            }
                        } else {
                            JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");
                    }
                    break;
                case 6:
                    if (predioModel.getClaveCat() != null) {
                        paramtr = new HashMap<>();
                        paramtr.put("claveCat", predioModel.getClaveCat());
                        paramtr.put("estado", "A");
                        prediosConsulta = entityManager.findObjectByParameterList(CatPredio.class, paramtr);
                        if (prediosConsulta == null && prediosConsulta.isEmpty()) {
                            JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
                        } else {
                            predioConsulta = prediosConsulta.get(0);
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");
                    }
                    break;
                default:
                    break;
            }
            if (predioConsulta != null) {

                emisionesPrediales = entityManager.findAll(QuerysFinanciero.obtenerLiquidacionesPredialesPendientes, new String[]{"tipoLiquidacion", "predio"}, new Object[]{new RenTipoLiquidacion(13L), predioConsulta});

                // BUSCA SI TIENE SOLICITUD PENDIENTE DE EXONERACION --JOAO
                // BUSCA SOLICITUDES DE EXONERACION POR PREDIO EN ESTADO 1 o 2, DE LEY DEL ANCIANO O DISCAPACIDAD, (LA MAS ATUAL) --HENRY
                exoneracion = (FnSolicitudExoneracion) entityManager.find(QuerysFinanciero.buscarExoneracionTerceraEdadYDiscapacitadoPorPredio, new String[]{"predio"}, new Object[]{predioConsulta});

                if (exoneracion != null) {
                    //LA CONSULTA ANTERIOR VERIFICA QUE LOS ESTADOS SEAN (1,2)
                    if (exoneracion.getEstado().getId() == 3L || exoneracion.getEstado().getId() == 4L) {
                        exoneracion = null;
                        mensajeExoneracion = null;
                    } else {
                        Boolean tieneExo = Boolean.FALSE;
                        for (FnExoneracionLiquidacion exo : exoneracion.getExoneracionLiquidacionCollection()) {
                            if (exo.getEstado()) {
                                tieneExo = Boolean.TRUE;
                            }
                        }
                        if (tieneExo) {
                            mensajeExoneracion = "Tiene una exoneración de: " + exoneracion.getExoneracionTipo().getDescripcion().toUpperCase()
                                    + "\nNúmero de resolución: " + exoneracion.getNumResolucionSac();
                            //LA CONSULTA ANTERIOR VERIFICA QUE LOS TIPO SON (17,18,37,44)
                            if (exoneracion.getExoneracionTipo().getId() == 17L || exoneracion.getExoneracionTipo().getId() == 18L || exoneracion.getExoneracionTipo().getId() == 37L || exoneracion.getExoneracionTipo().getId() == 44L) {
                                //if(exoneracion.getExoneracionTipo().getId() == 17L || exoneracionSolicitud.getExoneracionTipo().getId() == 37L){
                                JsfUti.update("formMensajeExo");
                                JsfUti.executeJS("PF('dlgMensajeExo').show()");
                            } else {
                                exoneracion = null;
                                mensajeExoneracion = null;
                            }
                        }

                    }
                }
                calculoTotalPago(emisionesPrediales, null);
                cuotasConvenio();
                if (tieneConvenio) {
                    mensajeExoneracion = mensajeExoneracion + "  PREDIO TIENE CUOTAS DE CONVENIO";
                    JsfUti.update("formMensajeExo");
                    JsfUti.executeJS("PF('dlgMensajeExo').show()");
                }
                totalEmisionesGeneral = new BigDecimal(totalEmisiones.toString());
                // ENVIA MOSTRAR ADVERTENCIA SI EL PAGO YA FUE REALIZADO POR MEDIO DEL BANCO
//                if (tipoLiquidacion.getId().equals(13L)) {
                try {
                    if (totalEmisionesGeneral.compareTo(BigDecimal.ZERO) > 0) {
                        if (recaudacionServices.verificarPagoBanco(predioConsulta)) {
                            JsfUti.executeJS("PF('dlgPagoBanco').show()");
                        }
                    }
                } catch (Exception e) {
                    Logger.getLogger(e.toString());
                }
//                }

            } else {
                //DIALOGO DE SELECCION DE PREDIOS
                if (prediosConsulta == null && prediosConsulta.isEmpty()) {
                    JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
                }
                if (prediosConsulta != null && prediosConsulta.size() > 1) {
                    JsfUti.update("frmPredios");
                    JsfUti.executeJS("PF('dlgPrediosConsulta').show();");
                }
            }
            if (emisionesPrediales.isEmpty()) {
                JsfUti.messageInfo(null, "Mensaje", "Predio No posee Deuda");
            }

        } catch (Exception e) {
            Logger.getLogger(e.toString());
        }

    }

    public void cuotasConvenio() {
        try {
            tieneConvenio = Boolean.FALSE;
            if (getPredioConsulta() != null) {
                if (getPredioConsulta().getId() != null) {
                    sumaTotalConv = BigDecimal.ZERO;
                    List<FnConvenioPago> convenioPagos = (List<FnConvenioPago>) entityManager.findAll(QuerysFinanciero.getConvenioByPredios, new String[]{"predio"},
                            new Object[]{getPredioConsulta().getId()});
                    if (Utils.isNotEmpty(convenioPagos)) {
                        convenidos = new ArrayList();
                        for (FnConvenioPago fcp : convenioPagos) {
                            if (fcp.getCuotasConvenio() != null) {
                                convenidos.addAll(fcp.getCuotasConvenio());
//                            if (Utils.isNotEmpty(fcp.getCuotasConvenio())) {
//                                convenidos.addAll(fcp.getCuotasConvenio());
                            } else {
                                JsfUti.messageError(null, "Info", "No se han Elaborado las Coutas de Convenios");
                            }
                        }
                        if (Utils.isNotEmpty(convenidos)) {
                            for (FnConvenioPagoDetalle fcpd : convenidos) {
                                if (fcpd.getLiquidacion().getEstadoLiquidacion().getId().equals(8L)) {
                                    if (fcpd.getFechaMaximaPago().before(new Date())) {
                                        sumaTotalConv = sumaTotalConv.add(fcpd.getDeuda());
                                    }
                                }
                            }
                            tieneConvenio = Boolean.TRUE;
                        }
                    }
                    totalEmisiones = totalEmisiones.add(sumaTotalConv);
                    JsfUti.update("mainForm:tvRecaudaciones:tvRecaudacionesPredios:panelConvenios");
                } else {
                    JsfUti.messageError(null, "Info", "Debe Seleccionar Un predio");
                }
            } else {
                JsfUti.messageError(null, "Info", "Debe Seleccionar Un predio");
            }

        } catch (Exception e) {

        }
    }

    public void calculoTotalPago(List<RenLiquidacion> listado, Date fechaPago) {
        Boolean flag = true;
        totalEmisiones = new BigDecimal("0.00");
        for (RenLiquidacion e : listado) {
            // Pregunta por el año actual, si ya fue exonerado y si se encontró la solicitud de exoneración en el anterior método.
            if (e.getAnio() == Utils.getAnio(new Date()) && e.getEstaExonerado() && exoneracion != null) {
                exoneracion = null;
            }
            //

            if (flag && e.getEstadoLiquidacion().getId().compareTo(2L) == 0) {
                if (e.getEstadoCoactiva() == 2) {
                    flag = false;
                    JsfUti.executeJS("PF('dlgMensaje').show();");
                }
            }
            if (e.getEstadoLiquidacion().getId().compareTo(2L) == 0) {
                try {
                    //CALCULO DE DESCUENTO-RECARGO-INTERES
                    e = recaudacionServices.realizarDescuentoRecargaInteresPredial(e, fechaPago);
                    e.calcularPagoConCoactiva();
                    totalEmisiones = totalEmisiones.add(e.getPagoFinal());
                    totalEmisiones = totalEmisiones.setScale(2, RoundingMode.HALF_UP);
                } catch (Exception ex) {
                    Logger.getLogger(ex.toString());
                }
            }
        }
    }

    public void onChangeTab(TabChangeEvent event) {
        this.tabName = event.getTab().getId().toString();
        if (tabName.equals("tabPagoPredial") || tabName.equals("pagoPredialRural")) {
            emisionesPrediales = null;
            predioModel = new CatPredioModel();
        } else {
            predioModel = new CatPredioModel();
        }
    }

    public void onChangeRadio() {

        //emisionesPrediales = null;
        predioModel = new CatPredioModel();
        predioConsulta = null;
        totalEmisiones = new BigDecimal("0.00");
    }

    public void consultarEmisionesRurales() {
        try {
            paramtr = new HashMap<>();
            //modelPago = new PagoTituloReporteModel();
            modelPago = new PagoTituloReporteModel(new BigDecimal("0.00"), this.variosPagos, this.modelPago.getPagoNotaCredio(), this.modelPago.getPagoCheque(), this.modelPago.getPagoTarjetaCredito(), this.modelPago.getPagoTransferencia());
            totalEmisionesGeneral = null;
            totalEmisiones = null;
            switch (tipoConsultaRural.intValue()) {
                //CODIGO PREDIAL
                case 1:
                    if (predioModel.getRegCatastral() != null) {
                        paramtr.put("regCatastral", predioModel.getRegCatastral().trim());
                        paramtr.put("estado", true);
                        predioRuralConsulta = (CatPredioRustico) entityManager.findObjectByParameter(CatPredioRustico.class, paramtr);
                    } else {
                        JsfUti.messageInfo(null, "Mensaje", "Ingrese los datos para realizar la Busqueda.");
                    }
                    break;
                //CONTRIBUYENTE
                case 2:
                    if (contribuyenteConsulta != null) {
                        if (contribuyenteConsulta.getCatPredioRusticos() != null && !contribuyenteConsulta.getCatPredioRusticos().isEmpty()) {
//                            System.out.println("LOS PREDIOS SON = " + contribuyenteConsulta.getCatPredioRusticos().get(0).getRegCatastral()
//                            + " RDH" + "  " + contribuyenteConsulta.getCatPredioRusticos().get(0).getIdPredial() + " GRRBR "+
//                                    contribuyenteConsulta.getCatPredioRusticos().get(0).getParroquia());
                            if (contribuyenteConsulta.getCatPredioRusticos().size() == 1) {
                                paramtr.put("regCatastral", contribuyenteConsulta.getCatPredioRusticos().get(0).getRegCatastral());
                                //paramtr.put("idPredial", contribuyenteConsulta.getCatPredioRusticos().get(0).getIdPredial());
                                paramtr.put("parroquia", contribuyenteConsulta.getCatPredioRusticos().get(0).getParroquia());
                                paramtr.put("estado", true);
                                predioRuralConsulta = (CatPredioRustico) entityManager.findObjectByParameter(CatPredioRustico.class, paramtr);
                            }
                        } else {
                            JsfUti.messageInfo(null, "Contribuyente no posee Predios", "");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Realice la busqueda del Contribuyente.");
                    }
                    break;
                //CODIGO ANTERIOR
                case 3:
                    if (predioModel != null) {
                        if (predioModel.getProvinciaAnt() != null && predioModel.getCantonAnt() != null && predioModel.getParroquiaAnt() != null
                                && predioModel.getManzanaAnt() != null && predioModel.getSolarAnt() != null && predioModel.getPropiedadHorizontalAnt() != null) {
                            paramtr.put("estado", true);
                            paramtr.put("regCatastral", predioModel.getProvinciaAnt() + predioModel.getCantonAnt() + predioModel.getParroquiaAnt()
                                    + predioModel.getManzanaAnt() + predioModel.getSolarAnt() + predioModel.getPropiedadHorizontalAnt());

                            predioRuralConsulta = (CatPredioRustico) entityManager.findObjectByParameter(CatPredioRustico.class, paramtr);

                        } else {
                            JsfUti.messageInfo(null, "Predio no encontrado", "");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Realice la busqueda del Contribuyente.");
                    }
                    break;
                default:
                    break;
            }
            if (predioRuralConsulta != null) {
                if (tabName.equals("pagoPredialRusticoTitulosPredios")) {
                    paramtr = new HashMap<>();
                    paramtr.put("tipoLiquidacion", new RenTipoLiquidacion(7L));
                    paramtr.put("predioRustico", predioRuralConsulta);
                    paramtr.put("estadoLiquidacion", new RenEstadoLiquidacion(2L));
                    emisionesPrediales = entityManager.findObjectByParameterOrderList(RenLiquidacion.class, paramtr, new String[]{"anio"}, Boolean.TRUE);
                } else {
                    if (isSanMiguel) {
                        emisionesPrediales = entityManager.findAll(QuerysFinanciero.obtenerLiquidacionesPredialesRusticosPendientes, new String[]{"tipoLiquidacion", "predioRustico"}, new Object[]{new RenTipoLiquidacion(7L), predioRuralConsulta});
                    } else {
                        emisionesPrediales = entityManager.findAll(QuerysFinanciero.obtenerLiquidacionesPredialesRusticos, new String[]{"tipoLiquidacion", "predioRustico"}, new Object[]{new RenTipoLiquidacion(7L), predioRuralConsulta});
                    }
                    calculoTotalPago(emisionesPrediales, null);
                    totalEmisionesGeneral = new BigDecimal(totalEmisiones.toString());
                }

            } else {
                //DIALOGO DE SELECCION DE PREDIOS
                //DIALOGO DE SELECCION DE PREDIOS
                if (contribuyenteConsulta != null && contribuyenteConsulta.getCatPredioRusticos() != null && contribuyenteConsulta.getCatPredioRusticos().size() > 1) {
                    JsfUti.update("frmPrediosRural");
                    JsfUti.executeJS("PF('dlgPrediosRuralConsulta').show();");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(e.toString());
        }
    }

    public List<CatPredio> consultar(Integer tipoCons, CatPredio predio) {
        List<CatPredio> tempList = new ArrayList<>();
        CatPredio temp = new CatPredio();
        switch (tipoCons) {
            case 1: // Codigo Anterior

                break;
            case 2: // Codigo Nuevo

                temp = services.permisoServices().getCatPredioByCodigoPredio(predio.getProvincia(),
                        predio.getCanton(), predio.getParroquia(), predio.getZona(), predio.getSector(),
                        predio.getMz(), predio.getLote(), predio.getBloque(), predio.getPiso(), predio.getUnidad(), "A");
                if (temp != null && temp.getId() != null) {
                    tempList.add(temp);
                }
                break;
            case 3:// Numero de Predio
                if (predio.getNumPredio() == null) {
                    JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.faltaNumPredio);
                    return null;
                }
                temp = services.permisoServices().getFichaServices().getPredioByNum(predio.getNumPredio().longValue());
                if (temp != null && temp.getId() != null) {
                    tempList.add(temp);
                }
                break;
            case 5:
                if (contribuyenteConsulta != null) {
                    if (contribuyenteConsulta.getCatPredioPropietarioCollection() != null && !contribuyenteConsulta.getCatPredioPropietarioCollection().isEmpty()) {
                        tempList = new ArrayList<>();
                        for (CatPredioPropietario p : contribuyenteConsulta.getCatPredioPropietarioCollection()) {
                            if (p.getPredio().getPropiedadHorizontal() == null || !p.getPredio().getPropiedadHorizontal()) //PARA Q NO APARESCAN LAS PROPIEDADES HORIZONTALES
                            {
                                tempList.add(p.getPredio());
                            }
                        }
                    } else {
                        JsfUti.messageInfo(null, "Contribuyente no posee Predios", "");
                    }
                } else {
                    JsfUti.messageError(null, "Error", "Realice la busqueda del Contribuyente.");
                }
                break;
            case 6:// Codigo Anterior
                if (predio.getPredialant() == null || predio.getPredialant().length() == 0) {
                    JsfUti.messageError(null, MessagesRentas.error, "Dato para la consulta");
                    return null;
                }
                temp = services.permisoServices().getFichaServices().getPredioByPredialant(predio.getPredialant());
                if (temp != null && temp.getId() != null) {
                    tempList.add(temp);
                }
                break;

        }
        if (!tempList.isEmpty()) {
            return tempList;
        } else {
            JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.predioNoEncontrado);
        }
        return null;
    }

    public void handleFileUpload(FileUploadEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            Date d = new Date();
            int numero = 0;
            String rutaArchivo = SisVars.rutaRepotiorioArchivo + d.getTime() + event.getFile().getFileName();
            File file = new File(rutaArchivo);
            InputStream is;
            is = event.getFile().getInputstream();
            OutputStream out = new FileOutputStream(file);
            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            is.close();
            out.close();
            Archivo documento = new Archivo();
            documento.setNombre(d.getTime() + "_" + event.getFile().getFileName());

            documento.setTipo(event.getFile().getContentType());
            documento.setRuta(rutaArchivo);

            controlDocumento = Boolean.TRUE;

        } catch (IOException e) {
            Logger.getLogger(BpmManageBeanBase.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean getIsSanMiguel() {
        return isSanMiguel;
    }

    public void setIsSanMiguel(Boolean isSanMiguel) {
        this.isSanMiguel = isSanMiguel;
    }

    public CatPredio getPredioConsulta() {
        return predioConsulta;
    }

    public void setPredioConsulta(CatPredio predioConsulta) {
        this.predioConsulta = predioConsulta;
    }

    public List<CatPredio> getPrediosConsulta() {
        return prediosConsulta;
    }

    public void setPrediosConsulta(List<CatPredio> prediosConsulta) {
        this.prediosConsulta = prediosConsulta;
    }

    public PagoTituloReporteModel getModelPago() {
        return modelPago;
    }

    public void setModelPago(PagoTituloReporteModel modelPago) {
        this.modelPago = modelPago;
    }

    public Boolean getPagoRealizado() {
        return pagoRealizado;
    }

    public void setPagoRealizado(Boolean pagoRealizado) {
        this.pagoRealizado = pagoRealizado;
    }

    public BigDecimal getTotalEmisionesGeneral() {
        return totalEmisionesGeneral;
    }

    public void setTotalEmisionesGeneral(BigDecimal totalEmisionesGeneral) {
        this.totalEmisionesGeneral = totalEmisionesGeneral;
    }

    public BigDecimal getTotalEmisiones() {
        return totalEmisiones;
    }

    public void setTotalEmisiones(BigDecimal totalEmisiones) {
        this.totalEmisiones = totalEmisiones;
    }

    public Long getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(Long tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public CatPredioModel getPredioModel() {
        return predioModel;
    }

    public void setPredioModel(CatPredioModel predioModel) {
        this.predioModel = predioModel;
    }

    public Map<String, Object> getParamtr() {
        return paramtr;
    }

    public void setParamtr(Map<String, Object> paramtr) {
        this.paramtr = paramtr;
    }

    public FnSolicitudExoneracion getExoneracion() {
        return exoneracion;
    }

    public void setExoneracion(FnSolicitudExoneracion exoneracion) {
        this.exoneracion = exoneracion;
    }

    public String getMensajeExoneracion() {
        return mensajeExoneracion;
    }

    public void setMensajeExoneracion(String mensajeExoneracion) {
        this.mensajeExoneracion = mensajeExoneracion;
    }

    public Boolean getVariosPagos() {
        return variosPagos;
    }

    public void setVariosPagos(Boolean variosPagos) {
        this.variosPagos = variosPagos;
    }

    public CatEnte getContribuyenteConsulta() {
        return contribuyenteConsulta;
    }

    public void setContribuyenteConsulta(CatEnte contribuyenteConsulta) {
        this.contribuyenteConsulta = contribuyenteConsulta;
    }

    public PropietariosLazy getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(PropietariosLazy propietarios) {
        this.propietarios = propietarios;
    }

    public CatPredioRusticoLazy getPropietariosRustico() {
        return propietariosRustico;
    }

    public void setPropietariosRustico(CatPredioRusticoLazy propietariosRustico) {
        this.propietariosRustico = propietariosRustico;
    }

    public List<CatPredioRustico> getPrediosRusticoConsulta() {
        return prediosRusticoConsulta;
    }

    public void setPrediosRusticoConsulta(List<CatPredioRustico> prediosRusticoConsulta) {
        this.prediosRusticoConsulta = prediosRusticoConsulta;
    }

    public List<RenLiquidacion> getEmisionesPrediales() {
        return emisionesPrediales;
    }

    public void setEmisionesPrediales(List<RenLiquidacion> emisionesPrediales) {
        this.emisionesPrediales = emisionesPrediales;
    }

    public Long getTipoConsultaRural() {
        return tipoConsultaRural;
    }

    public void setTipoConsultaRural(Long tipoConsultaRural) {
        this.tipoConsultaRural = tipoConsultaRural;
    }

    public CatPredioRustico getPredioRuralConsulta() {
        return predioRuralConsulta;
    }

    public void setPredioRuralConsulta(CatPredioRustico predioRuralConsulta) {
        this.predioRuralConsulta = predioRuralConsulta;
    }

    public Boolean getControlDocumento() {
        return controlDocumento;
    }

    public void setControlDocumento(Boolean controlDocumento) {
        this.controlDocumento = controlDocumento;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(String selectionMode) {
        this.selectionMode = selectionMode;
    }

    public String getTipoExoneracionParametro() {
        return tipoExoneracionParametro;
    }

    public void setTipoExoneracionParametro(String tipoExoneracionParametro) {
        this.tipoExoneracionParametro = tipoExoneracionParametro;
    }

    public BigDecimal getSumaTotalConv() {
        return sumaTotalConv;
    }

    public void setSumaTotalConv(BigDecimal sumaTotalConv) {
        this.sumaTotalConv = sumaTotalConv;
    }

    public List<FnConvenioPagoDetalle> getConvenidos() {
        return convenidos;
    }

    public void setConvenidos(List<FnConvenioPagoDetalle> convenidos) {
        this.convenidos = convenidos;
    }

    public FnConvenioPagoDetalle getCpd() {
        return cpd;
    }

    public void setCpd(FnConvenioPagoDetalle cpd) {
        this.cpd = cpd;
    }

    public List<RenLiquidacion> getCuotasPredios() {
        return cuotasPredios;
    }

    public void setCuotasPredios(List<RenLiquidacion> cuotasPredios) {
        this.cuotasPredios = cuotasPredios;
    }

    public Boolean getTieneConvenio() {
        return tieneConvenio;
    }

    public void setTieneConvenio(Boolean tieneConvenio) {
        this.tieneConvenio = tieneConvenio;
    }

}
