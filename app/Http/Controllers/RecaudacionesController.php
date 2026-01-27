<?php

namespace App\Http\Controllers;
use \Log;
use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use DB;
use Carbon\Carbon;
use Illuminate\Support\Facades\Gate;

class RecaudacionesController extends Controller
{
    public function index(){
        if(!Auth()->user()->hasPermissionTo('Recaudacion Rural'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('recaudaciones.index');
    }

    public function pagosRecaudados(Request $request){
        try{
            if($request->area=='Rural'){
                $valoresRecaudado=$this->valoresRurales($request);
                if($valoresRecaudado['error']==true){
                    return [
                        'error'=>true,
                        'mensaje'=>'Ocurrió un error'
                    ];
                }
               
                return [
                    'error'=>false,
                    'data'=>$valoresRecaudado['resultado']
                ];
            }else if($request->area=='Urbano'){
                $valoresRecaudado=$this->valoresUrbanos($request);
                if($valoresRecaudado['error']==true){
                    return [
                        'error'=>true,
                        'mensaje'=>'Ocurrió un error'
                    ];
                }
              
                return [
                    'error'=>false,
                    'data'=>$valoresRecaudado['resultado']
                ];
            }
        }catch (\Throwable $e) {
            Log::error('RecaudacionesController => pagosRecaudados => mensaje => '.$e->getMessage().' linea => '.$e->getLine());
            return [
                'error'=>true,
                'mensaje'=>'Ocurrió un error' .$e
            ];
            
        }
    }

    public function valoresUrbanos(Request $request){
        try{
            $fecha_ini=$request->filtroDesde;
            $fecha_ini_ = new \DateTime($fecha_ini);  
            $datosPagosPredialUrb = DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', 'pago.liquidacion')
            ->where('rpr.rubro', 2)
            ->where('pago.estado', true)
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->selectRaw("
                '0' as orden,
                '0' as ordenlista,
                'PREDIAL URBANO' as tipo,
                '11.02.02.' AS codigo,                    
                SUM(
                    CASE 
                        WHEN liq.anio = EXTRACT(YEAR FROM CURRENT_DATE)
                        THEN rpr.valor 
                        ELSE 0 
                    END
                ) AS total_pago_anio_actual,
                SUM(
                    CASE 
                        WHEN liq.anio < EXTRACT(YEAR FROM CURRENT_DATE)
                        THEN rpr.valor 
                        ELSE 0 
                    END
                ) AS total_pago_anteriores,
                'IMPUESTO PREDIAL URBANO' as detalle_imp
            ")
            ->get();

            $datosPagosDescuentoUrb = DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', 'pago.liquidacion')
            ->where('pago.estado', true)
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->selectRaw("
                '0' as orden,
                '0' as ordenlista,
                'PREDIAL URBANO' as tipo,
                '11.02.02.' AS codigo,                    
                SUM(
                    CASE 
                        WHEN liq.anio = EXTRACT(YEAR FROM CURRENT_DATE)
                        THEN pago.descuento 
                        ELSE 0 
                    END
                ) AS total_pago_anio_actual,
                SUM(
                    CASE 
                        WHEN liq.anio < EXTRACT(YEAR FROM CURRENT_DATE)
                        THEN pago.descuento 
                        ELSE 0 
                    END
                ) AS total_pago_anteriores,
                'DESCUENTOS' as detalle_imp
            ")
            ->get();

            $datosPagosRecargoUrb = DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', 'pago.liquidacion')
            ->where('pago.estado', true)
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->selectRaw("
                '0' as orden,
                '0' as ordenlista,
                'PREDIAL URBANO' as tipo,
                '17.04.01.' AS codigo,                    
                SUM(
                    CASE 
                        WHEN liq.anio = EXTRACT(YEAR FROM CURRENT_DATE)
                        THEN pago.recargo 
                        ELSE 0 
                    END
                ) AS total_pago_anio_actual,
                SUM(
                    CASE 
                        WHEN liq.anio < EXTRACT(YEAR FROM CURRENT_DATE)
                        THEN pago.recargo 
                        ELSE 0 
                    END
                ) AS total_pago_anteriores,
                'RECARGO (MULTA TRIBUTARIA)' as detalle_imp
            ")
            ->get();

            $datosPagosinteresesUrb = DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', 'pago.liquidacion')
            ->where('pago.estado', true)
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->selectRaw("
                '0' as orden,
                '0' as ordenlista,
                'PREDIAL URBANO' as tipo,
                '17.03.01.' AS codigo,                    
                SUM(
                    CASE 
                        WHEN liq.anio = EXTRACT(YEAR FROM CURRENT_DATE)
                        THEN pago.interes 
                        ELSE 0 
                    END
                ) AS total_pago_anio_actual,
                SUM(
                    CASE 
                        WHEN liq.anio < EXTRACT(YEAR FROM CURRENT_DATE)
                        THEN pago.interes 
                        ELSE 0 
                    END
                ) AS total_pago_anteriores,
                'INTERES POR MORA TRIBUTARIA' as detalle_imp
            ")
            ->get();


            $datosPagosServAdmUrb = DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', 'pago.liquidacion')
            ->where('rpr.rubro', 3)
            ->where('pago.estado', true)
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->selectRaw("
                '2' as orden,
                '18' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.01.99.010' AS codigo,                
                SUM(
                    CASE 
                        WHEN liq.anio = EXTRACT(YEAR FROM CURRENT_DATE)
                        THEN rpr.valor 
                        ELSE 0 
                    END
                ) AS total_pago_anio_actual,
                SUM(
                    CASE 
                        WHEN liq.anio < EXTRACT(YEAR FROM CURRENT_DATE)
                        THEN rpr.valor 
                        ELSE 0 
                    END
                ) AS total_pago_anteriores,
                'SERVICIOS ADMINISTRATIVOS URBANOS' as detalle_imp
            ")
            ->get();


            $datosPagosCEMALCANTARILLADOSANITARIOPLUVIALAAPPMALLAURBANASV= DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', 'pago.liquidacion')
            ->where('rpr.rubro', 711)
            ->where('pago.estado', true)
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->selectRaw("
                '2' as orden,
                '18' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.04.09.' AS codigo,                
                SUM(
                    CASE 
                        WHEN liq.anio = EXTRACT(YEAR FROM CURRENT_DATE)
                        THEN rpr.valor 
                        ELSE 0 
                    END
                ) AS total_pago_anio_actual,
                SUM(
                    CASE 
                        WHEN liq.anio < EXTRACT(YEAR FROM CURRENT_DATE)
                        THEN rpr.valor 
                        ELSE 0 
                    END
                ) AS total_pago_anteriores,
                'CEM-ALCANTARILLADO SANITARIO PLUVIAL AAPP MALLA URBANA SV' as detalle_imp
            ")
            ->get();

            $datosPagosCEM_ALCANTARILLADO_SANTA_MARTHA= DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', 'pago.liquidacion')
            ->where('rpr.rubro', 707)
            ->where('pago.estado', true)
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->selectRaw("
                '2' as orden,
                '27' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.04.09' AS codigo,               
                SUM(
                    CASE 
                        WHEN liq.anio = EXTRACT(YEAR FROM CURRENT_DATE)
                        THEN rpr.valor 
                        ELSE 0 
                    END
                ) AS total_pago_anio_actual,
                SUM(
                    CASE 
                        WHEN liq.anio < EXTRACT(YEAR FROM CURRENT_DATE)
                        THEN rpr.valor 
                        ELSE 0 
                    END
                ) AS total_pago_anteriores,
                'CEM-ALCANTARILLADO SANTA MARTHA' as detalle_imp
            ")
            ->get();

            $datosPagosCEM_ALCANTARILLADOS_Y_VIAS = DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->where('rpr.rubro', 641)
            ->where('pago.estado', true)
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->selectRaw("
                '2' as orden,
                '25' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.04.9' AS codigo,                
                COALESCE(
                    SUM(
                        CASE 
                            WHEN liq.anio = EXTRACT(YEAR FROM CURRENT_DATE)
                            THEN rpr.valor 
                            ELSE 0 
                        END
                    ), 0
                ) AS total_pago_anio_actual,
                COALESCE(
                    SUM(
                        CASE 
                            WHEN liq.anio < EXTRACT(YEAR FROM CURRENT_DATE)
                            THEN rpr.valor 
                            ELSE 0 
                        END
                    ), 0
                ) AS total_pago_anteriores,
                'CEM-ALCANTARILLADOS Y VIAS' as detalle_imp
            ")
            ->get();

            $datosPagosCEM_AREA_RECREACIONAL = DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->where('rpr.rubro', 705)
            ->where('pago.estado', true)
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->selectRaw("
                '2' as orden,
                '25' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.04.9' AS codigo,                
                COALESCE(
                    SUM(
                        CASE 
                            WHEN liq.anio = EXTRACT(YEAR FROM CURRENT_DATE)
                            THEN rpr.valor 
                            ELSE 0 
                        END
                    ), 0
                ) AS total_pago_anio_actual,
                COALESCE(
                    SUM(
                        CASE 
                            WHEN liq.anio < EXTRACT(YEAR FROM CURRENT_DATE)
                            THEN rpr.valor 
                            ELSE 0 
                        END
                    ), 0
                ) AS total_pago_anteriores,
                'CEM-AREA RECREACIONAL' as detalle_imp
            ")
            ->get();

            $datosPagosCEM_MERCADO_MUNICIPAL = DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->where('rpr.rubro', 706)
            ->where('pago.estado', true)
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->selectRaw("
                '2' as orden,
                '26' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.04.13.' AS codigo,                            
                COALESCE(
                    SUM(
                        CASE 
                            WHEN liq.anio = EXTRACT(YEAR FROM CURRENT_DATE)
                            THEN rpr.valor 
                            ELSE 0 
                        END
                    ), 0
                ) AS total_pago_anio_actual,
                COALESCE(
                    SUM(
                        CASE 
                            WHEN liq.anio < EXTRACT(YEAR FROM CURRENT_DATE)
                            THEN rpr.valor 
                            ELSE 0 
                        END
                    ), 0
                ) AS total_pago_anteriores,
                'CEM-MERCADO MUNICIPAL' as detalle_imp
            ")
            ->get();

            $datosPagosPARQUES_Y_PLAZAS = DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->where('rpr.rubro', 640)
            ->where('pago.estado', true)
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->selectRaw("
                '2' as orden,
                '24' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.04.13.' AS codigo,                            
                COALESCE(
                    SUM(
                        CASE 
                            WHEN liq.anio = EXTRACT(YEAR FROM CURRENT_DATE)
                            THEN rpr.valor 
                            ELSE 0 
                        END
                    ), 0
                ) AS total_pago_anio_actual,
                COALESCE(
                    SUM(
                        CASE 
                            WHEN liq.anio < EXTRACT(YEAR FROM CURRENT_DATE)
                            THEN rpr.valor 
                            ELSE 0 
                        END
                    ), 0
                ) AS total_pago_anteriores,
                'CEM-PARQUES Y PLAZAS' as detalle_imp
            ")
            ->get();

            
            $datosPagosCEM_PAVIMENTACION_MALLA_URBANA_SV = DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->where('rpr.rubro', 710)
            ->where('pago.estado', true)
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->selectRaw("
                '2' as orden,
                '30' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.04.06.' AS codigo, 
                COALESCE(
                    SUM(
                        CASE 
                            WHEN liq.anio = EXTRACT(YEAR FROM CURRENT_DATE)
                            THEN rpr.valor 
                            ELSE 0 
                        END
                    ), 0
                ) AS total_pago_anio_actual,
                COALESCE(
                    SUM(
                        CASE 
                            WHEN liq.anio < EXTRACT(YEAR FROM CURRENT_DATE)
                            THEN rpr.valor 
                            ELSE 0 
                        END
                    ), 0
                ) AS total_pago_anteriores,
                'CEM-PAVIMENTACION MALLA URBANA SV' as detalle_imp
            ")
            ->get();

            $datosPagosCEM_REGENERACION_MALECON_SV= DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->where('rpr.rubro', 709)
            ->where('pago.estado', true)
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->selectRaw("
                '2' as orden,
                '29' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.04.13.' AS codigo,  
                COALESCE(
                    SUM(
                        CASE 
                            WHEN liq.anio = EXTRACT(YEAR FROM CURRENT_DATE)
                            THEN rpr.valor 
                            ELSE 0 
                        END
                    ), 0
                ) AS total_pago_anio_actual,
                COALESCE(
                    SUM(
                        CASE 
                            WHEN liq.anio < EXTRACT(YEAR FROM CURRENT_DATE)
                            THEN rpr.valor 
                            ELSE 0 
                        END
                    ), 0
                ) AS total_pago_anteriores,
                'CEM REGENERACION MALECON SV' as detalle_imp
            ")
            ->get();

            $datosPagosBomberos= DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->where('rpr.rubro', 7)
            ->where('pago.estado', true)
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->selectRaw("
                '3' as orden,
                '4' as ordenlista,
                'FONDOS AJENOS' as tipo,
                '' AS codigo, 
                COALESCE(
                    SUM(
                        CASE 
                            WHEN liq.anio = EXTRACT(YEAR FROM CURRENT_DATE)
                            THEN rpr.valor 
                            ELSE 0 
                        END
                    ), 0
                ) AS total_pago_anio_actual,
                COALESCE(
                    SUM(
                        CASE 
                            WHEN liq.anio < EXTRACT(YEAR FROM CURRENT_DATE)
                            THEN rpr.valor 
                            ELSE 0 
                        END
                    ), 0
                ) AS total_pago_anteriores,
                'CUERPO DE BOMBEROS' as detalle_imp
            ")
            ->get();

            $datosPagosSeguridadCiudadana= DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->where('rpr.rubro', 712)
            ->where('pago.estado', true)
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->selectRaw("
                '3' as orden,
                '4' as ordenlista,
                'FONDOS AJENOS' as tipo,
                '' AS codigo, 
                COALESCE(
                    SUM(
                        CASE 
                            WHEN liq.anio = EXTRACT(YEAR FROM CURRENT_DATE)
                            THEN rpr.valor 
                            ELSE 0 
                        END
                    ), 0
                ) AS total_pago_anio_actual,
                COALESCE(
                    SUM(
                        CASE 
                            WHEN liq.anio < EXTRACT(YEAR FROM CURRENT_DATE)
                            THEN rpr.valor 
                            ELSE 0 
                        END
                    ), 0
                ) AS total_pago_anteriores,
                'SEGURIDAD CIUDADANA' as detalle_imp
            ")
            ->get();

            $datosPagos = $datosPagosPredialUrb
            ->merge($datosPagosDescuentoUrb)
            ->merge($datosPagosRecargoUrb)
            ->merge($datosPagosinteresesUrb)
            ->merge($datosPagosServAdmUrb)
            ->merge($datosPagosCEMALCANTARILLADOSANITARIOPLUVIALAAPPMALLAURBANASV)
            ->merge($datosPagosCEM_ALCANTARILLADO_SANTA_MARTHA)
            ->merge($datosPagosCEM_ALCANTARILLADOS_Y_VIAS)
            ->merge($datosPagosCEM_AREA_RECREACIONAL)
            ->merge($datosPagosCEM_MERCADO_MUNICIPAL)
            ->merge($datosPagosPARQUES_Y_PLAZAS)
            ->merge($datosPagosCEM_PAVIMENTACION_MALLA_URBANA_SV)
            ->merge($datosPagosCEM_REGENERACION_MALECON_SV)
            ->merge($datosPagosBomberos)
            ->merge($datosPagosSeguridadCiudadana);

            setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');

            $fecha_timestamp = strtotime($fecha_ini);
    
            $fecha_formateada = strftime("%d de %B de %Y", $fecha_timestamp);
    
            $fecha_mayusculas = strtoupper($fecha_formateada);
            
            return[
                'error'=>false,
                'resultado'=>$datosPagos,
                'fecha_actual'=>$fecha_mayusculas,
                'anio'=>$fecha_ini
            ];


        }catch (\Throwable $e) {
            Log::error('RecaudacionesController => valoresUrbanos => mensaje => '.$e->getMessage().' linea => '.$e->getLine());
            return response()->json([
                'error'=>true,
                'mensaje'=>'Ocurrió un error' .$e
            ]);            
        }
    }

    public function valoresRurales(Request $request){
        try{
            $fecha_ini=$request->filtroDesde;
            $datosPagoPredialRust = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->whereRaw("CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?", [$fecha_ini])
            ->whereIn('pago.TitPr_Estado', ['C','Q'])
            ->selectRaw("
                '0' as orden,
                '3' as ordenlista,
                'PREDIAL RUSTICO' as tipo,
                '11.02.02.' AS codigo,                    
                CAST(SUM(pago.TItPr_IPU) AS DECIMAL(12,2)) AS total_pago_anio_actual,                
                'IMPUESTO PREDIAL RUSTICO' AS detalle_imp
            ")
            ->get();

           $datosPagoPredialRustAnt = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
            ->where('cv.CarVe_Estado', 'C')
            ->selectRaw("
                
                CAST(SUM(cv.CarVe_IPU) AS DECIMAL(12,2)) AS total_pago_anteriores    
            ")
            ->get();

            // Suponiendo que ambos resultados son colecciones con un solo elemento cada uno:
            $total_ant = $datosPagoPredialRustAnt[0]->total_pago_anteriores ?? 0;

            // Recorres el array principal y le agregas el campo adicional
            foreach ($datosPagoPredialRust as $item) {
                $item->total_pago_anteriores = $total_ant;
            };

            $datosPagoDescuentosRust = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->whereRaw("CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?", [$fecha_ini])
            ->whereIn('pago.TitPr_Estado', ['C','Q'])
            ->selectRaw("
                '0' as orden,
                '3' as ordenlista,
                'PREDIAL RUSTICO' as tipo,
                '11.02.02.' AS codigo,                    
               
                CAST(SUM(pago.TitPr_Descuento) AS DECIMAL(12,2)) AS total_pago_anio_actual,    
                
                'DESCUENTOS' AS detalle_imp
            ")
            ->get();

           $datosPagoDescuentoAnt = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
            ->where('cv.CarVe_Estado', 'C')
            ->selectRaw("
               
                CAST(SUM(cv.CarVe_Descuento) AS DECIMAL(12,2)) AS total_pago_anteriores    
            ")
            ->get();

            // Suponiendo que ambos resultados son colecciones con un solo elemento cada uno:
            $total_ant = $datosPagoDescuentoAnt[0]->total_pago_anteriores ?? 0;

            // Recorres el array principal y le agregas el campo adicional
            foreach ($datosPagoDescuentosRust as $item) {
                $item->total_pago_anteriores = $total_ant;
            };


            $datosPagoRecargosRust = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->whereRaw("CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?", [$fecha_ini])
            ->whereIn('pago.TitPr_Estado', ['C','Q'])
            ->selectRaw("
                '0' as orden,
                '3' as ordenlista,
                'PREDIAL RUSTICO' as tipo,
                '17.04.01.' AS codigo,                    
              
                CAST(SUM(pago.TitPr_Recargo) AS DECIMAL(12,2)) AS total_pago_anio_actual,    
                'RECARGO (MULTA TRIBUTARIA)' AS detalle_imp
            ")
            ->get();

           $datosPagoRecargosAnt = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
            ->where('cv.CarVe_Estado', 'C')
            ->selectRaw("
                
                CAST(SUM(cv.Carve_Recargo) AS DECIMAL(12,2)) AS total_pago_anteriores
            ")
            ->get();

            // Suponiendo que ambos resultados son colecciones con un solo elemento cada uno:
            $total_ant = $datosPagoRecargosAnt[0]->total_pago_anteriores ?? 0;

            // Recorres el array principal y le agregas el campo adicional
            foreach ($datosPagoRecargosRust as $item) {
                $item->total_pago_anteriores = $total_ant;
            };

            $datosPagoInteresesRust = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->whereRaw("CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?", [$fecha_ini])
            ->whereIn('pago.TitPr_Estado', ['C','Q'])
            ->selectRaw("
                '0' as orden,
                '3' as ordenlista,
                'PREDIAL RUSTICO' as tipo,
                '17.03.01.' AS codigo,                    
               
                CAST(SUM(pago.TitPr_Interes) AS DECIMAL(12,2)) AS total_pago_anio_actual,    
                'INTERES POR MORA TRIBUTARIA' AS detalle_imp
            ")
            ->get();

           $datosPagoInteresesAnt = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
            ->where('cv.CarVe_Estado', 'C')
            ->selectRaw("
              
                CAST(SUM(cv.CarVe_Interes) AS DECIMAL(12,2)) AS total_pago_anteriores
            ")
            ->get();

            // Suponiendo que ambos resultados son colecciones con un solo elemento cada uno:
            $total_ant = $datosPagoInteresesAnt[0]->total_pago_anteriores ?? 0;

            // Recorres el array principal y le agregas el campo adicional
            foreach ($datosPagoInteresesRust as $item) {
                $item->total_pago_anteriores = $total_ant;
            };

            $datosPagoSeguridadCiudadanaRust = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->whereRaw("CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?", [$fecha_ini])
            ->whereIn('pago.TitPr_Estado', ['C','Q'])
            ->selectRaw("
                '0' as orden,
                '3' as ordenlista,
                'OTROS INGRESOS TRIBUTARIO' as tipo,
                '17.03.01.' AS codigo,                    
               
                CAST(SUM(pago.TitPr_Valor1) AS DECIMAL(12,2)) AS total_pago_anio_actual,  
                'SEGURIDAD CIUDADANA' AS detalle_imp
            ")
            ->get();

           $datosPagoSeguridadCiudadanaAnt = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
            ->where('cv.CarVe_Estado', 'C')
            ->selectRaw("
               
                CAST(SUM(cv.Carve_Valor1) AS DECIMAL(12,2)) AS total_pago_anteriores
            ")
            ->get();

            // Suponiendo que ambos resultados son colecciones con un solo elemento cada uno:
            $total_ant = $datosPagoSeguridadCiudadanaAnt[0]->total_pago_anteriores ?? 0;

            // Recorres el array principal y le agregas el campo adicional
            foreach ($datosPagoSeguridadCiudadanaRust as $item) {
                $item->total_pago_anteriores = $total_ant;
            };

            $datosPagoBomberoRural = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->whereRaw("CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?", [$fecha_ini])
            ->whereIn('pago.TitPr_Estado', ['C','Q'])
            ->selectRaw("
                '0' as orden,
                '3' as ordenlista,
                'FONDOS AJENOS' as tipo,
                '' AS codigo,                    
              
                CAST(SUM(pago.TitPr_Bomberos) AS DECIMAL(12,2)) AS total_pago_anio_actual,
                'CUERPO DE BOMBEROS RURALES' AS detalle_imp
            ")
            ->get();

           $datosPagoBomberoRuralAnt = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
            ->where('cv.CarVe_Estado', 'C')
            ->selectRaw("
               
                CAST(SUM(cv.CarVe_Bomberos) AS DECIMAL(12,2)) AS total_pago_anteriores
            ")
            ->get();

            // Suponiendo que ambos resultados son colecciones con un solo elemento cada uno:
            $total_ant = $datosPagoBomberoRuralAnt[0]->total_pago_anteriores ?? 0;

            // Recorres el array principal y le agregas el campo adicional
            foreach ($datosPagoBomberoRural as $item) {
                $item->total_pago_anteriores = $total_ant;
            };

             $datosPagoServicioAdmRural = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->whereRaw("CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?", [$fecha_ini])
            ->whereIn('pago.TitPr_Estado', ['C','Q'])
            ->selectRaw("
                '0' as orden,
                '3' as ordenlista,
                'FONDOS AJENOS' as tipo,
                '3.01.99.011' AS codigo,                    
              
                CAST(SUM(pago.TitPr_TasaAdministrativa) AS DECIMAL(12,2)) AS total_pago_anio_actual,
                'SERVICIOS ADMINISTRATIVOS RURALES' AS detalle_imp
            ")
            ->get();

           $datosPagoServicioAdmAnt = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
            ->where('cv.CarVe_Estado', 'C')
            ->selectRaw("
               
                CAST(SUM(cv.CarVe_TasaAdministrativa) AS DECIMAL(12,2)) AS total_pago_anteriores
            ")
            ->get();

            // Suponiendo que ambos resultados son colecciones con un solo elemento cada uno:
            $total_ant = $datosPagoServicioAdmAnt[0]->total_pago_anteriores ?? 0;

            // Recorres el array principal y le agregas el campo adicional
            foreach ($datosPagoServicioAdmRural as $item) {
                $item->total_pago_anteriores = $total_ant;
            };
           

            
            $datosPagos = $datosPagoPredialRust
            ->merge($datosPagoDescuentosRust)
            ->merge($datosPagoServicioAdmRural)
            ->merge($datosPagoRecargosRust)
            ->merge($datosPagoInteresesRust)
            ->merge($datosPagoSeguridadCiudadanaRust)
            ->merge($datosPagoBomberoRural)
            ->sortBy('ordenlista') // Ordena por ordenlista
            ->sortBy('orden') 
            ->values(); // Restablece los índices del array

         
            // dd($datosPagos);
            setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');

            $fecha_timestamp = strtotime($fecha_ini);
    
            $fecha_formateada = strftime("%d de %B de %Y", $fecha_timestamp);
    
            $fecha_mayusculas = strtoupper($fecha_formateada);
            
            return[
                'error'=>false,
                'resultado'=>$datosPagos,
                'fecha_actual'=>$fecha_mayusculas,
                'anio'=>$fecha_ini
            ];
        }catch (\Throwable $e) {
            Log::error('RecaudacionesController => valoresRurales => mensaje => '.$e->getMessage().' linea => '.$e->getLine());
            return response()->json([
                'error'=>true,
                'mensaje'=>'Ocurrió un error'
            ]);            
        }
    }

    public function generarReporte(Request $request){
        try{
            $fecha_ini=$request->filtroDesde;

            setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');

            $fecha_timestamp = strtotime($fecha_ini);
    
            $fecha_formateada = strftime("%d de %B de %Y", $fecha_timestamp);
    
            $fecha_mayusculas = strtoupper($fecha_formateada);

            if($request->area=='Rural'){
               
                $datosPagoPredialRust = DB::connection('sqlsrv')
                ->table('TITULOS_PREDIO as pago')
                ->whereRaw(
                    "CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?",
                    [$fecha_ini]
                )
                ->whereIn('pago.TitPr_Estado', ['C', 'Q'])
                ->selectRaw("
                    'ANIO_ACTUAL' AS tipo_anio,
                    TitPr_NumTitulo as num_titulo,
                    CAST(pago.TItPr_IPU AS DECIMAL(12,2)) AS total_pago_anio_actual_ipr,
                    CAST(pago.TitPr_Descuento AS DECIMAL(12,2)) AS total_pago_anio_actual_desc,
                    CAST(pago.TitPr_Recargo AS DECIMAL(12,2)) AS total_pago_anio_actual_rec, 
                    CAST(pago.TitPr_Interes AS DECIMAL(12,2)) AS total_pago_anio_actual_int,
                    CAST(pago.TitPr_Valor1 AS DECIMAL(12,2)) AS total_pago_anio_actual_seguridad,
                    CAST(pago.TitPr_Bomberos AS DECIMAL(12,2)) AS total_pago_anio_actual_bombero,
                    CAST(pago.TitPr_TasaAdministrativa AS DECIMAL(12,2)) AS total_pago_anio_actual_tasa,
                    CAST(pago.TitPr_ValorTCobrado AS DECIMAL(12,2)) AS total_cobrado,
                    '' as tipo_pago
                ")
                ->get();

            
            $datosPagoPredialRustAnt = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
                ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
                ->where('cv.CarVe_Estado', 'C')
                ->selectRaw("
                    'ANIOS_ANTERIORES' AS tipo_anio,
                    CarVe_NumTitulo as num_titulo,
                    CAST(cv.CarVe_IPU AS DECIMAL(12,2)) AS total_pago_anio_actual_ipr,
                    CAST(cv.CarVe_Descuento AS DECIMAL(12,2)) AS total_pago_anio_actual_desc,
                    CAST(cv.Carve_Recargo AS DECIMAL(12,2)) AS total_pago_anio_actual_rec,  
                    CAST(cv.CarVe_Interes AS DECIMAL(12,2)) AS total_pago_anio_actual_int,  
                    CAST(cv.Carve_Valor1 AS DECIMAL(12,2)) AS total_pago_anio_actual_seguridad,
                    CAST(cv.CarVe_Bomberos AS DECIMAL(12,2)) AS total_pago_anio_actual_bombero,
                    CAST(cv.CarVe_TasaAdministrativa AS DECIMAL(12,2)) AS total_pago_anio_actual_tasa,
                    CAST(cv.CarVe_ValorTCobrado AS DECIMAL(12,2)) AS total_cobrado,
                    '' as tipo_pago
                    
                ")
                ->get();
                $resultado = $datosPagoPredialRust
                ->merge($datosPagoPredialRustAnt)->values();

                return [
                    'error'=>false,
                    'data'=>$resultado,
                    'fecha'=>$fecha_mayusculas
                ]; 

            }else if($request->area=='Urbano'){

                $datosPagosPredialUrb = DB::connection('pgsql')
                ->table('sgm_financiero.ren_pago as pago')
                ->join('sgm_financiero.ren_pago_detalle as rpd', 'rpd.pago', '=', 'pago.id')
                ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
                ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
                ->whereIn('rpr.rubro', [2])  
                ->where('liq.anio','=',date('Y'))
                ->where('pago.estado', true)
                ->whereDate('pago.fecha_pago', $fecha_ini)
                ->selectRaw("
                    'ANIOS_ACTUAL' AS tipo_anio,
                    liq.id_liquidacion as num_titulo,
                    pago.descuento as total_pago_anio_actual_desc,
                    pago.recargo as total_pago_anio_actual_rec,
                    pago.interes as total_pago_anio_actual_int,
                    CASE 
                        WHEN rpr.rubro = 2 THEN rpr.valor 
                        ELSE 0 
                    END AS total_pago_anio_actual_ipr,

                    (
                        SUM(CASE WHEN rpr.rubro = 2 THEN rpr.valor ELSE 0 END)
                        + SUM(pago.interes)
                        + SUM(pago.recargo)
                        - SUM(pago.descuento)
                    ) AS total_cobrado,
                    rpd.tipo_pago
                ")
                ->groupBy('liq.id_liquidacion','pago.descuento','pago.recargo','pago.interes','rpr.rubro','rpr.valor','rpd.tipo_pago')
                ->get();

                $datosPagosPredialUrbAnt = DB::connection('pgsql')
                ->table('sgm_financiero.ren_pago as pago')
                ->join('sgm_financiero.ren_pago_detalle as rpd', 'rpd.pago', '=', 'pago.id')
                ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
                ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
                ->whereIn('rpr.rubro', [2])  
                ->where('liq.anio','<',date('Y'))
                ->where('pago.estado', true)
                ->whereDate('pago.fecha_pago', $fecha_ini)
                ->selectRaw("
                    'ANIOS_ANTERIORES' AS tipo_anio,
                    liq.id_liquidacion as num_titulo,
                    pago.descuento as total_pago_anio_actual_desc,
                    pago.recargo as total_pago_anio_actual_rec,
                    pago.interes as total_pago_anio_actual_int,
                    CASE 
                        WHEN rpr.rubro = 2 THEN rpr.valor 
                        ELSE 0 
                    END AS total_pago_anio_actual_ipr,

                    (
                        SUM(CASE WHEN rpr.rubro = 2 THEN rpr.valor ELSE 0 END)
                        + SUM(pago.interes)
                        + SUM(pago.recargo)
                        - SUM(pago.descuento)
                    ) AS total_cobrado,
                    rpd.tipo_pago
                ")
                ->groupBy('liq.id_liquidacion','pago.descuento','pago.recargo','pago.interes','rpr.rubro','rpr.valor','rpd.tipo_pago')
                ->get();

                $datosPagosPredialUrbCEM = DB::connection('pgsql')
                ->table('sgm_financiero.ren_pago as pago')
                ->join('sgm_financiero.ren_pago_detalle as rpd', 'rpd.pago', '=', 'pago.id')
                ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
                ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
                ->whereIn('rpr.rubro', [711,707,641,705,706,640,710,709])
                ->where('liq.anio', date('Y'))
                ->where('pago.estado', true)
                ->whereDate('pago.fecha_pago', $fecha_ini)
                ->groupBy('liq.id_liquidacion','rpd.tipo_pago')
                ->selectRaw("
                    'ANIOS_ACTUAL' AS tipo_anio,
                    liq.id_liquidacion AS num_titulo,

                    SUM(CASE WHEN rpr.rubro = 711 THEN rpr.valor ELSE 0 END) AS cem_alcantarillado_sani,
                    SUM(CASE WHEN rpr.rubro = 707 THEN rpr.valor ELSE 0 END) AS cem_alcant_sta_marta,
                    SUM(CASE WHEN rpr.rubro = 641 THEN rpr.valor ELSE 0 END) AS cem_alcant_vias,
                    SUM(CASE WHEN rpr.rubro = 705 THEN rpr.valor ELSE 0 END) AS cem_area_recreacional,
                    SUM(CASE WHEN rpr.rubro = 706 THEN rpr.valor ELSE 0 END) AS cem_mercado_muni,
                    SUM(CASE WHEN rpr.rubro = 640 THEN rpr.valor ELSE 0 END) AS cem_parque_plaza,
                    SUM(CASE WHEN rpr.rubro = 710 THEN rpr.valor ELSE 0 END) AS cem_pavimentacion_malla,
                    SUM(CASE WHEN rpr.rubro = 709 THEN rpr.valor ELSE 0 END) AS cem_regeneracion,

                    (
                        SUM(CASE WHEN rpr.rubro = 711 THEN rpr.valor ELSE 0 END)
                    + SUM(CASE WHEN rpr.rubro = 707 THEN rpr.valor ELSE 0 END)
                    + SUM(CASE WHEN rpr.rubro = 641 THEN rpr.valor ELSE 0 END)
                    + SUM(CASE WHEN rpr.rubro = 705 THEN rpr.valor ELSE 0 END)
                    + SUM(CASE WHEN rpr.rubro = 706 THEN rpr.valor ELSE 0 END)
                    + SUM(CASE WHEN rpr.rubro = 640 THEN rpr.valor ELSE 0 END)
                    + SUM(CASE WHEN rpr.rubro = 710 THEN rpr.valor ELSE 0 END)
                    + SUM(CASE WHEN rpr.rubro = 709 THEN rpr.valor ELSE 0 END)
                    ) AS total_cobrado,
                    rpd.tipo_pago
                ")
                ->get();

                $datosPagosPredialUrbCEMAnt = DB::connection('pgsql')
                ->table('sgm_financiero.ren_pago as pago')
                ->join('sgm_financiero.ren_pago_detalle as rpd', 'rpd.pago', '=', 'pago.id')
                ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
                ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
                ->whereIn('rpr.rubro', [711,707,641,705,706,640,710,709])
                ->where('liq.anio','<', date('Y'))
                ->where('pago.estado', true)
                ->whereDate('pago.fecha_pago', $fecha_ini)
                ->groupBy('liq.id_liquidacion','rpd.tipo_pago')
                ->selectRaw("
                    'ANIOS_ANTERIORES' AS tipo_anio,
                    liq.id_liquidacion AS num_titulo,

                    SUM(CASE WHEN rpr.rubro = 711 THEN rpr.valor ELSE 0 END) AS cem_alcantarillado_sani,
                    SUM(CASE WHEN rpr.rubro = 707 THEN rpr.valor ELSE 0 END) AS cem_alcant_sta_marta,
                    SUM(CASE WHEN rpr.rubro = 641 THEN rpr.valor ELSE 0 END) AS cem_alcant_vias,
                    SUM(CASE WHEN rpr.rubro = 705 THEN rpr.valor ELSE 0 END) AS cem_area_recreacional,
                    SUM(CASE WHEN rpr.rubro = 706 THEN rpr.valor ELSE 0 END) AS cem_mercado_muni,
                    SUM(CASE WHEN rpr.rubro = 640 THEN rpr.valor ELSE 0 END) AS cem_parque_plaza,
                    SUM(CASE WHEN rpr.rubro = 710 THEN rpr.valor ELSE 0 END) AS cem_pavimentacion_malla,
                    SUM(CASE WHEN rpr.rubro = 709 THEN rpr.valor ELSE 0 END) AS cem_regeneracion,

                    (
                        SUM(CASE WHEN rpr.rubro = 711 THEN rpr.valor ELSE 0 END)
                    + SUM(CASE WHEN rpr.rubro = 707 THEN rpr.valor ELSE 0 END)
                    + SUM(CASE WHEN rpr.rubro = 641 THEN rpr.valor ELSE 0 END)
                    + SUM(CASE WHEN rpr.rubro = 705 THEN rpr.valor ELSE 0 END)
                    + SUM(CASE WHEN rpr.rubro = 706 THEN rpr.valor ELSE 0 END)
                    + SUM(CASE WHEN rpr.rubro = 640 THEN rpr.valor ELSE 0 END)
                    + SUM(CASE WHEN rpr.rubro = 710 THEN rpr.valor ELSE 0 END)
                    + SUM(CASE WHEN rpr.rubro = 709 THEN rpr.valor ELSE 0 END)
                    ) AS total_cobrado,
                    rpd.tipo_pago
                ")
                ->get();

                $datosPagosPredialUrbOtros = DB::connection('pgsql')
                ->table('sgm_financiero.ren_pago as pago')
                ->join('sgm_financiero.ren_pago_detalle as rpd', 'rpd.pago', '=', 'pago.id')
                ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
                ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
                ->whereIn('rpr.rubro', [712,7,3])
                ->where('liq.anio', date('Y'))
                ->where('pago.estado', true)
                ->whereDate('pago.fecha_pago', $fecha_ini)
                ->groupBy('liq.id_liquidacion','rpd.tipo_pago')
                ->selectRaw("
                    'ANIOS_ACTUAL' AS tipo_anio,
                    liq.id_liquidacion AS num_titulo,

                    SUM(CASE WHEN rpr.rubro = 712 THEN rpr.valor ELSE 0 END) AS total_seguridad,
                    SUM(CASE WHEN rpr.rubro = 7 THEN rpr.valor ELSE 0 END) AS total_bomberos,
                    SUM(CASE WHEN rpr.rubro = 3 THEN rpr.valor ELSE 0 END) AS total_adm,
                  

                    (
                        SUM(CASE WHEN rpr.rubro = 712 THEN rpr.valor ELSE 0 END)
                    + SUM(CASE WHEN rpr.rubro = 7 THEN rpr.valor ELSE 0 END)
                    + SUM(CASE WHEN rpr.rubro = 3 THEN rpr.valor ELSE 0 END)
                  
                    ) AS total_cobrado,
                    rpd.tipo_pago
                ")
                ->get();

                $datosPagosPredialUrbOtrosAnt = DB::connection('pgsql')
                ->table('sgm_financiero.ren_pago as pago')
                ->join('sgm_financiero.ren_pago_detalle as rpd', 'rpd.pago', '=', 'pago.id')
                ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
                ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
                ->whereIn('rpr.rubro', [712,7,3])
                ->where('liq.anio','<', date('Y'))
                ->where('pago.estado', true)
                ->whereDate('pago.fecha_pago', $fecha_ini)
                ->groupBy('liq.id_liquidacion','rpd.tipo_pago')
                ->selectRaw("
                    'ANIOS_ANTERIORES' AS tipo_anio,
                    liq.id_liquidacion AS num_titulo,

                    SUM(CASE WHEN rpr.rubro = 712 THEN rpr.valor ELSE 0 END) AS total_seguridad,
                    SUM(CASE WHEN rpr.rubro = 7 THEN rpr.valor ELSE 0 END) AS total_bomberos,
                    SUM(CASE WHEN rpr.rubro = 3 THEN rpr.valor ELSE 0 END) AS total_adm,
                  

                    (
                        SUM(CASE WHEN rpr.rubro = 712 THEN rpr.valor ELSE 0 END)
                    + SUM(CASE WHEN rpr.rubro = 7 THEN rpr.valor ELSE 0 END)
                    + SUM(CASE WHEN rpr.rubro = 3 THEN rpr.valor ELSE 0 END)
                  
                    ) AS total_cobrado,
                    rpd.tipo_pago
                ")
                ->get();
                

                $resultado = $datosPagosPredialUrb->merge($datosPagosPredialUrbAnt);

                return [
                    'error'=>false,
                    'data'=>$resultado,
                    'cem'=>$datosPagosPredialUrbCEM->merge($datosPagosPredialUrbCEMAnt),
                    'otros'=>$datosPagosPredialUrbOtros->merge($datosPagosPredialUrbOtrosAnt),
                    'fecha'=>$fecha_mayusculas
                ]; 
                
            }
            
           
        }catch (\Throwable $e) {
            Log::error('RecaudacionesController => generarReporte => mensaje => '.$e->getMessage().' linea => '.$e->getLine());
            return response()->json([
                'error'=>true,
                'mensaje'=>'Ocurrió un error'.$e->getMessage()
            ]);            
        }
    }
}