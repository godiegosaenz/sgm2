<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Carbon\Carbon;
use Illuminate\Support\Facades\DB;
use App\Models\FirmaElectronica;
use App\Models\Area;
use Illuminate\Support\Facades\Crypt;
use App\Models\User;
use Exception;
use GuzzleHttp\Client;
use SimpleSoftwareIO\QrCode\Facades\QrCode;


class ParteDiarioController extends Controller
{
    private $clienteFirmador = null;

    public function __construct(){
        try{
           
            $ip2="http://192.168.0.42:82/";

            $this->clienteFirmador = new Client([
                'base_uri' =>$ip2,
                'verify' => false,
            ]);

        }catch(Exception $e){
            Log::error($e->getMessage());
        }
    }

    public function index(){
        if(!Auth()->user()->hasPermissionTo('Parte Diario'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('parte_diario.index');
    }

    public function consultar($fecha){
        $response = $this->clienteFirmador->request('GET', "/soporte-desarrollo/pdf-parte-diario/{$fecha}", [
            'headers' => [
                'Accept' => 'application/pdf',
            ],
        ]);

        $responseBody = json_decode($response->getBody(), true);
        if($responseBody['error']==true){
            return ['error' => true, 'mensaje' => $responseBody['mensaje']];
        }

        $response = $this->clienteFirmador->request('GET', "/soporte-desarrollo/documento-parte/{$responseBody['pdf']}", [
            'headers' => [
                'Accept' => 'application/pdf',
            ],
        ]);

        if ($response->getStatusCode() === 200) {
            $contenidoPDF = $response->getBody()->getContents();

            // Guardar en disco local del Proyecto A
            \Storage::disk('local')->put("public/{$responseBody['pdf']}", $contenidoPDF);
            
            return ['error' => false, 'mensaje' => 'Archivo generado exitosamente.', 'pdf'=>$responseBody['pdf']];
        } else {
            return ['error' => true, 'mensaje' => 'Error al obtener el PDF.'];
        }
    }

    public function pdfParte($fecha_ini){
        try{
            $consultaInfo=$this->generarParte($fecha_ini);
            if($consultaInfo['error']==true){
                return response()->json([
                    'error'=>true,
                    'mensaje'=>'Ocurrió un error'
                ]);
            }
            
            #agrupamos
            $listado_final=[];
            foreach ($consultaInfo['resultado'] as $key => $item){                
                if(!isset($listado_final[$item->tipo])) {
                    $listado_final[$item->tipo]=array($item);
            
                }else{
                    array_push($listado_final[$item->tipo], $item);
                }
            } 
            // dd($listado_final);
           
            $nombrePDF="reporte_parte_diario.pdf";

            $pdf=\PDF::LoadView('gestion_reporte_sv.reporte_parte_diario',['datos'=>$listado_final,'fecha'=>$consultaInfo['fecha_actual'] ]);
            $pdf->setPaper("A4", "portrait");

            // return $pdf->stream("aa.pdf");
            
            $estadoarch = $pdf->stream();

            //lo guardamos en el disco temporal
            \Storage::disk('public')->put(str_replace("", "",$nombrePDF), $estadoarch);
            $exists_destino = \Storage::disk('public')->exists($nombrePDF); 
            if($exists_destino){ 
                return response()->json([
                    'error'=>false,
                    'pdf'=>$nombrePDF
                ]);
            }else{
                return response()->json([
                    'error'=>true,
                    'mensaje'=>'No se pudo crear el documento'
                ]);
            }

        }catch (\Throwable $e) {
            \Log::error('ParteDiarioController => pdfParte => mensaje => '.$e->getMessage().  ' linea => '.$e->getLine());
            return response()->json([
                'error'=>true,
                'mensaje'=>'Ocurrió un error'.$e
            ]);
            
        }
    }

    public function generarParte($fecha_ini){
        try{
            $fecha_ini_ = new \DateTime($fecha_ini);          
            
            $anio_seleccionado = explode($fecha_ini,"-");
            $anio_seleccionado=$anio_seleccionado[0];                       
            

            $datosPagoPredialUrb = DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_detalle as rpd', 'rpd.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', 'pago.liquidacion')
            ->where('rpr.rubro', 2)
            ->whereIn('rpd.tipo_pago',[1,2,3,4])
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
            
           
            $datosPagosDesc = DB::connection('pgsql')
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
            
             $datosPagosRecargo = DB::connection('pgsql')
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
            

            $datosPagosInteres = DB::connection('pgsql')
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

            //PREDIAL RUSTICO...
           
            // $datosPagoPredialRust = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            // ->leftJoin('CARTERA_VENCIDA as cv', function($join) use ($fecha_ini) {
            //     $join->on('cv.Pre_CodigoCatastral', '=', 'pago.Pre_CodigoCatastral')
            //         ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
            //         ->where('cv.CarVe_Estado', 'C'); // Mover la condición aquí
            // })
            // ->whereRaw("CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?", [$fecha_ini])
            // ->where('pago.TitPr_Estado', 'C')
            // ->selectRaw("
            //     '0' as orden,
            //     '3' as ordenlista,
            //     'PREDIAL RUSTICO' as tipo,
            //     '11.02.02.' AS codigo,                    
            //     SUM(pago.TItPr_IPU) as total_pago_anio_actual,
            //     SUM(cv.CarVe_IPU) as total_pago_anteriores,
            //     'IMPUESTO PREDIAL RUSTICO' AS detalle_imp
            // ")
            // ->get();

            $datosPagoPredialRust = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->whereRaw("CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?", [$fecha_ini])
            ->where('pago.TitPr_Estado', 'C')
            ->selectRaw("
                '0' as orden,
                '3' as ordenlista,
                'PREDIAL RUSTICO' as tipo,
                '11.02.02.' AS codigo,                    
                SUM(pago.TItPr_IPU) as total_pago_anio_actual,
                
                'IMPUESTO PREDIAL RUSTICO' AS detalle_imp
            ")
            ->get();

            $datosPagoPredialRustAnt = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
            ->where('cv.CarVe_Estado', 'C')
            ->selectRaw("
                SUM(cv.CarVe_IPU) as total_pago_anteriores
            ")
            ->get();

            // Suponiendo que ambos resultados son colecciones con un solo elemento cada uno:
            $total_ant = $datosPagoPredialRustAnt[0]->total_pago_anteriores ?? 0;

            // Recorres el array principal y le agregas el campo adicional
            foreach ($datosPagoPredialRust as $item) {
                $item->total_pago_anteriores = $total_ant;
            }
            
            

            $datosPagosDescRust = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
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
            foreach ($datosPagosDescRust as $item) {
                $item->total_pago_anteriores = $total_ant;
            };

          
            $datosPagosRecargoRust = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
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
            foreach ($datosPagosRecargoRust as $item) {
                $item->total_pago_anteriores = $total_ant;
            };


            $datosPagosInteresRust = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
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
            foreach ($datosPagosInteresRust as $item) {
                $item->total_pago_anteriores = $total_ant;
            };

            //otros ingresos  tributarios

            $datosUtilidad = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',2212222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '0' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '11.01.02' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'UTILIDAD EN LA VENTA DE PREDIOS URBANOS' AS detalle_imp
            ")
            ->get();

            $datosImpuestoRodaje = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',2212222223)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '1' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '11.02.05' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'IMPUESTO AL RODAJE DEL VEHICULO' AS detalle_imp
            ")
            ->get();

            $datosAlcabala = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',22122222234)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '2' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '11.02.06' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'ALCABALAS' AS detalle_imp
            ")
            ->get();

            $datos15ActivosTotales = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',22122222234)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '3' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '11.02.07' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                '1.5 A LOS ACTIVOS TOTALES' AS detalle_imp
            ")
            ->get();

            $datos5XCientoMuni = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',22122222235)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '4' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '11.02.99' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                '5% IMP. MUNICIPAL Y 1. MIL EDUCACION' AS detalle_imp
            ")
            ->get();

            $datos5XAdicional = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',221222222356)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '5' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '11.02.99' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                '1,2,3 % ADICIONAL Y EDUCAC. ELEMENTAL' AS detalle_imp
            ")
            ->get();

            $datosMedicinaRural = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',22122222235644)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '6' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '11.02.99' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'MEDICINA RURAL' AS detalle_imp
            ")
            ->get();

            $datosPatente = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',221)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '7' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '11.07.04' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'PATENTE MUNICIPAL' AS detalle_imp
            ")
            ->get();

            $datosPermisoEspectaculo = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',2233333331)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '8' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.01.12' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'PERMISO DE ESPECTACULOS PUBLICOS' AS detalle_imp
            ")
            ->get();

            $datosPermisoLetreros = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',2233333331)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '9' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.01.12' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'PERMISO DE LETREROS Y AVISOS PUBLICITARIOS' AS detalle_imp
            ")
            ->get();

            $datosPermisoConstruccion = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',2233333331)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '10' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.01.12' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'PERMISO DE CONSTRUCCION' AS detalle_imp
            ")
            ->get();

            $datosPermisoFuncionamiento = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',2233333331)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '11' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.01.12' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'PERMISO DE FUNCIONAMIENTO' AS detalle_imp
            ")
            ->get();

            $datosPermisoConexion = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',2233333331)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '12' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.01.20' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'PERMISO DE CONEXION RED ALCANTARILLLADO' AS detalle_imp
            ")
            ->get();

            $datosPermisoImplatacion = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',2233333331)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '13' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.01.99.005' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'PERMISO DE IMPLATACION ESTRUCTURAS METALICAS' AS detalle_imp
            ")
            ->get();

            $datosTasaInspecciones = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',2233333331)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '14' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.01.99.006' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'TASA POR INSPECCIONES' AS detalle_imp
            ")
            ->get();

            $datosTasaServMant = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',2233333331)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '15' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.01.99.008' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'TASA POR SERV. MANT. Y LIMP. AA.SS Y PP.' AS detalle_imp
            ")
            ->get();

            $datosTasaServCaracterSocial = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',2233333331)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '16' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.01.99.014' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'TASA POR SERVICIOS DE CARACTER SOCIAL' AS detalle_imp
            ")
            ->get();

            $datosTasaTramiteAdministrativo = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',2233333331)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '17' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.01.99.007' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'TASA POR TRAMITE ADMINISTRATIVO' AS detalle_imp
            ")
            ->get();
           

            $datosPagosServiciosAdm = DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_detalle as rpd', 'rpd.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', 'pago.liquidacion')
            ->where('rpr.rubro', 3)
            ->whereIn('rpd.tipo_pago',[1,2,3,4])
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

           
           

            $datosServiciosAdmRurales = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->whereRaw("CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?", [$fecha_ini])
            ->whereIn('pago.TitPr_Estado', ['C','Q'])
            ->selectRaw("
                '2' as orden,
                '19' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.01.99.011' AS codigo, 
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
            foreach ($datosServiciosAdmRurales as $item) {
                $item->total_pago_anteriores = $total_ant;
            };

            $datosServiciosAdmRuralesAnt = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
            ->where('cv.CarVe_Estado', 'C')
            ->selectRaw("
                SUM(cv.CarVe_TasaAdministrativa) as total_pago_anteriores
            ")
            ->get();

            // Suponiendo que ambos resultados son colecciones con un solo elemento cada uno:
            $total_ant = $datosServiciosAdmRuralesAnt[0]->total_pago_anteriores ?? 0;

            // Recorres el array principal y le agregas el campo adicional
            foreach ($datosServiciosAdmRurales as $item) {
                $item->total_pago_anteriores = $total_ant;
            }
            
            
            $datosServiciosOOPP = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',2233333331)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '20' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.01.99.015' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'SERVICIOS TECNICOS -OO.PP Y PLANIFICACION' AS detalle_imp
            ")
            ->get();
            
            $datosServiciosGestioRiesgo = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',2233333331)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '21' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.01.99.016' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'SERVICIOS TECNICOS - GESTION DE RIEGO' AS detalle_imp
            ")
            ->get();
            
            $datosServBD = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',2233333331)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '22' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.01.99.017' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'SERVICIO DE INFOR. CERTIF. DE BASE DATOS CATAS' AS detalle_imp
            ")
            ->get();
            
            $datosConstEspecMejoras = DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',2233333331)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '23' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.04.99' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'CONTRIBUCION ESPEC. MEJORAS (AÑOS ANTER.)' AS detalle_imp
            ")
            ->get();
            

            $datosPagosCemParquePlaza = DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_detalle as rpd', 'rpd.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->where('rpr.rubro', 640)
            ->whereIn('rpd.tipo_pago',[1,2,3,4])
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

            $datosCEMAlcantarilladoVias = DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_detalle as rpd', 'rpd.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->where('rpr.rubro', 641)
            ->whereIn('rpd.tipo_pago',[1,2,3,4])
            ->where('pago.estado', true)
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->selectRaw("
                '2' as orden,
                '25' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '13.04.09.' AS codigo,                
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
 
            
            $datosPagosCemMercadoMu = DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_detalle as rpd', 'rpd.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->where('rpr.rubro', 706)
            ->whereIn('rpd.tipo_pago',[1,2,3,4])
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
                'CEM-MERCADO MUNICIPAL SV' as detalle_imp
            ")
            ->get();
           

            $datosCEMAlcantarilladoStaMartha= DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_detalle as rpd', 'rpd.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', 'pago.liquidacion')
            ->where('rpr.rubro', 707)
            ->whereIn('rpd.tipo_pago',[1,2,3,4])
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

         
            $datosCEMAreaRecreacional = DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_detalle as rpd', 'rpd.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->where('rpr.rubro', 705)
            ->whereIn('rpd.tipo_pago',[1,2,3,4])
            ->where('pago.estado', true)
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->selectRaw("
                '2' as orden,
                '25' as ordenlista,
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
                'CEM-AREA RECREACIONAL CANOA' as detalle_imp
            ")
            ->get();
                       

            $datosPagosCemrRegeneracionMalecon= DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_detalle as rpd', 'rpd.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->where('rpr.rubro', 709)
            ->whereIn('rpd.tipo_pago',[1,2,3,4])
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


            $datosPagosCemPavimentMallaUrbana = DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_detalle as rpd', 'rpd.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->where('rpr.rubro', 710)
            ->whereIn('rpd.tipo_pago',[1,2,3,4])
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

            $datosPagosCemAAlcantarilladoSanit= DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_detalle as rpd', 'rpd.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', 'pago.liquidacion')
            ->where('rpr.rubro', 711)
            ->whereIn('rpd.tipo_pago',[1,2,3,4])
            ->where('pago.estado', true)
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->selectRaw("
                '2' as orden,
                '18' as ordenlista,
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
                'CEM-ALCANTARILLADO SANITARIO PLUVIAL SV' as detalle_imp
            ")
            ->get();

            $datosPagosIntereMora =  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',71055555555)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '2' as orden,
                '32' as ordenlista,
                'OTROS INGRESOS TRIBUTARIOS' as tipo,
                '17.03.01.' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'INTERES POR MORA TRIBUTARIA (OTROS IMP.)' AS detalle_imp
            ")
            ->get();

           
           
            //FONDOS AJENOS.

            $datosPagosAlcabalasOtroCantones=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',7111111111)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '3' as orden,
                '0' as ordenlista,
                'FONDOS AJENOS' as tipo,
                '' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'ALCABALAS OTROS CANTONES' AS detalle_imp
            ")
            ->get();

            $datosPagoSolarNoEdificado=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',7111111111111111)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '3' as orden,
                '1' as ordenlista,
                'FONDOS AJENOS' as tipo,
                '' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'SOLAR NO EDIFICADO (EPM-VCUVCSV)' AS detalle_imp
            ")
            ->get();

            $datosPagosColegioManabitas=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',711111111111)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '3' as orden,
                '2' as ordenlista,
                'FONDOS AJENOS' as tipo,
                '' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'COLEGIOS MANABITAS' AS detalle_imp
            ")
            ->get();

            $datosPagosCentroSaludPecuaria=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',711111111111)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '3' as orden,
                '3' as ordenlista,
                'FONDOS AJENOS' as tipo,
                '' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                '5% CEN. SALUD PECUARIA' AS detalle_imp
            ")
            ->get();

            // $datosPagosBomberoUrbano=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            // ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            // ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            // ->whereDate('pago.fecha_pago', $fecha_ini)
            // ->where('det_liq.rubro',7)
            // ->where('det_liq.estado',true)
            // ->where('liq.estado_liquidacion',1)
            // ->where('pago.estado', true)
            // ->selectRaw("
            //     '3' as orden,
            //     '4' as ordenlista,
            //     'FONDOS AJENOS' as tipo,
            //     '' AS codigo,                    
            //     SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
            //     SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
            //     'CUERPO DE BOMBEROS URBANOS' AS detalle_imp
            // ")
            // ->get();

            $datosPagosBomberoUrbano= DB::connection('pgsql')
            ->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_pago_detalle as rpd', 'rpd.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_pago_rubro as rpr', 'rpr.pago', '=', 'pago.id')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->where('rpr.rubro', 7)
            ->whereIn('rpd.tipo_pago',[1,2,3,4])
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
                'CUERPO DE BOMBEROS URBANOS' as detalle_imp
            ")
            ->get();

            
            // $datosPagosBomberoRural=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            // ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            // ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            // ->whereDate('pago.fecha_pago', $fecha_ini)
            // ->where('det_liq.rubro',711111111111)
            // ->where('det_liq.estado',true)
            // ->where('liq.estado_liquidacion',1)
            // ->where('pago.estado', true)
            // ->selectRaw("
            //     '3' as orden,
            //     '5' as ordenlista,
            //     'FONDOS AJENOS' as tipo,
            //     '' AS codigo,                    
            //     SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
            //     SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
            //     'CUERPO DE BOMBEROS RURALES' AS detalle_imp
            // ")
            // ->get();

            
            $datosPagosBomberoRural = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->whereRaw("CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?", [$fecha_ini])
            ->whereIn('pago.TitPr_Estado', ['C','Q'])
            ->selectRaw("
                '3' as orden,
                '5' as ordenlista,
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
            foreach ($datosPagosBomberoRural as $item) {
                $item->total_pago_anteriores = $total_ant;
            };
            // dd($datosPagosBomberoRural);

            $datosPagosViviendaRural=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',7)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '3' as orden,
                '6' as ordenlista,
                'FONDOS AJENOS' as tipo,
                '' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'VIVIENDA RURAL' AS detalle_imp
            ")
            ->get();

            // INGRESOS NO TRIBUTARIOS
            $datosPagoOcupacionPublica=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '4' as orden,
                '0' as ordenlista,
                'INGRESOS NO TRIBUTARIOS' as tipo,
                '13.01.03' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'OCUPACION DE LA VIA PUBICA' AS detalle_imp
            ")
            ->get();


            $datosServicioCamales=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '4' as orden,
                '1' as ordenlista,
                'INGRESOS NO TRIBUTARIOS' as tipo,
                '13.01.14' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'SERVICIOS DE CAMALES' AS detalle_imp
            ")
            ->get();

            $datosPagoAdjudicacionObras=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '4' as orden,
                '2' as ordenlista,
                'INGRESOS NO TRIBUTARIOS' as tipo,
                '13.01.99.001' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'TASA POR ADJUDICACIONES DE OBRAS' AS detalle_imp
            ")
            ->get();

            $datosPagoServAlcant=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '4' as orden,
                '3' as ordenlista,
                'INGRESOS NO TRIBUTARIOS' as tipo,
                '14.03.03' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'POR EL SERVICIO DE ALCANTARILLADO' AS detalle_imp
            ")
            ->get();

            $datosPagoArriendoMercadoLetras=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '4' as orden,
                '4' as ordenlista,
                'INGRESOS NO TRIBUTARIOS' as tipo,
                '17.02.02' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'ARRIENDOS DE MERCADOS Y EDIFICIOS -LETRAS C' AS detalle_imp
            ")
            ->get();

            $datosPagoArriendoMercadoCCMM=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '4' as orden,
                '5' as ordenlista,
                'INGRESOS NO TRIBUTARIOS' as tipo,
                '17.02.02' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'ARRIENDOS DE MERCADOS Y EDIFICIOS -CCM-SV' AS detalle_imp
            ")
            ->get();

            $datosPagoMultaInfraccionOrdenanza=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '4' as orden,
                '6' as ordenlista,
                'INGRESOS NO TRIBUTARIOS' as tipo,
                '17.04.02' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'MULTAS INFRACCION ORDENANZAS MCPAL' AS detalle_imp
            ")
            ->get();

            $datosPagoOtrasMultas=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '4' as orden,
                '7' as ordenlista,
                'INGRESOS NO TRIBUTARIOS' as tipo,
                '17.04.99' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'OTRAS MULTAS' AS detalle_imp
            ")
            ->get();

            $datosPagoOtrosNoEspecificado=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '4' as orden,
                '8' as ordenlista,
                'INGRESOS NO TRIBUTARIOS' as tipo,
                '19.04.99.001' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'OTROS INGRESOS NO ESPECIFICADOS' AS detalle_imp
            ")
            ->get();

            $datosPagoDepositoIntermediacion=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '4' as orden,
                '9' as ordenlista,
                'INGRESOS NO TRIBUTARIOS' as tipo,
                '212.01' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'DEPOSITOS DE INTERMEDIACION' AS detalle_imp
            ")
            ->get();

            $datosPagoGarantiaRecibida=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '4' as orden,
                '10' as ordenlista,
                'INGRESOS NO TRIBUTARIOS' as tipo,
                '212.11' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'GARANTIAS RECIBIDAS' AS detalle_imp
            ")
            ->get();



            //MATRICULACION Y REVISION VEHICULAR
            $datosPagoPermisoOperacion=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '0' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '13.01.12.17' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'PERMISO DE OPERACION / RENOVACION' AS detalle_imp
            ")
            ->get();


            $datosContratoOperacion=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '1' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '13.01.12.18' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'CONTRATO DE OPERACION / RENOVACION' AS detalle_imp
            ")
            ->get();

            $datosPagoIncrementoGrupo=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '2' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '13.01.11.24' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'INCREMENTO DE CUPO' AS detalle_imp
            ")
            ->get();

            $datosPagoInscripcionGravamen=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '3' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '13.01.06.01' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'INSCRIPCION DE GRAVAMEN' AS detalle_imp
            ")
            ->get();

            $datosPagolevantamientoGravamen=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '4' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '13.01.06.02' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'LEVANTAMIENTO DE GRAVAMEN' AS detalle_imp
            ")
            ->get();

            $datosPagoTraspasoDominio=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '5' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '13.01.11.03' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'TRASPASO DE DOMINIO VEHICULAR' AS detalle_imp
            ")
            ->get();

            $datosPagoResolHabilitacion=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '6' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '13.01.11.04' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'RESOLUCION-ADENDA POR HABILITACION' AS detalle_imp
            ")
            ->get();

            $datosPagoResolDesHabilitacion=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '7' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '13.01.11.05' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'RESOLUCION-ADENDA POR DESHABILITACION' AS detalle_imp
            ")
            ->get();

            $datosPagoResolCambioSocio=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '8' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '13.01.11.06' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'RESOLUCION-ADENDA POR CAMBIO DE SOCIO' AS detalle_imp
            ")
            ->get();

            $datosPagoModifCaractVehiculo=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '9' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '13.01.11.13' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'MODIFICACION DE CARACTERISTICAS DEL VEHICULO' AS detalle_imp
            ")
            ->get();

            $datosPagoBloqueoDesbloqueoSys=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '10' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '13.01.11.16' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'BLOQUEO Y DESBLOQUEO DEL SISTEMA' AS detalle_imp
            ")
            ->get();

            $datosPagoResolucionFactibilidad=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '11' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '13.01.11.18' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'RESOLUCION FACTIBILIDAD (CONSTRUCCION JURIDICA)' AS detalle_imp
            ")
            ->get();


            $datosCertUnicoVehi=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '12' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '13.01.11.22' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'CERTIFICADO UNICO VEHICULAR (CUV)' AS detalle_imp
            ")
            ->get();

            $datosPagoCertPoseeVehic=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '13' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '13.01.11.23' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'CERTIFICADO DE POSEER VEHICULO (CVP)' AS detalle_imp
            ")
            ->get();

          

            $datosPagoEmisionCertSalvoConducto=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '14' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '13.01.11.25' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'EMISIONES DE CERTIFICACIONES Y SALVOCONDUCTO' AS detalle_imp
            ")
            ->get();

            $datosPagoDuplicadoMatricula=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '15' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '13.01.11.01' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'DUPLICADO DE MATRICULA' AS detalle_imp
            ")
            ->get();

            $datosPagoStickerRevVehic=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '16' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '13.01.11.02' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'STICKER REVISION VEHICULAR' AS detalle_imp
            ")
            ->get();

            $datosPagoDuplicadoRevVehic=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '17' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '13.01.11.03' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'DUPLICADO DE REVISION VEHICULAR' AS detalle_imp
            ")
            ->get();

            $datosPagoRecargoMatVehPart=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '18' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '17.04.99.22' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'RECARGO POR RETRASO MATRICULACION VEHICULAR-PARTICULARES' AS detalle_imp
            ")
            ->get();

            $datosPagoRecargoMatVehPubli=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '5' as orden,
                '19' as ordenlista,
                'MATRICULACION Y REVISION VEHICULAR' as tipo,
                '17.04.99.03' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'RECARGO POR RETRASO MATRICULACION VEHICULAR-PUBLICOS' AS detalle_imp
            ")
            ->get();

            //INGRESOS DE CAPITAL

            $datosPagoVentaMunicipal=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '6' as orden,
                '0' as ordenlista,
                'INGRESOS DE CAPITAL' as tipo,
                '24.02.01' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'VENTA DE TERRRENO MUNICIPAL' AS detalle_imp
            ")
            ->get();

            $datosPagoVentaLoteCementerio=  DB::connection('pgsql')->table('sgm_financiero.ren_pago as pago')
            ->join('sgm_financiero.ren_liquidacion as liq', 'liq.id', '=', 'pago.liquidacion')
            ->join('sgm_financiero.ren_det_liquidacion as det_liq', 'liq.id', '=', 'det_liq.liquidacion')
            ->whereDate('pago.fecha_pago', $fecha_ini)
            ->where('det_liq.rubro',722222222)
            ->where('det_liq.estado',true)
            ->where('liq.estado_liquidacion',1)
            ->where('pago.estado', true)
            ->selectRaw("
                '6' as orden,
                '1' as ordenlista,
                'INGRESOS DE CAPITAL' as tipo,
                '24.02.01' AS codigo,                    
                SUM(CASE WHEN liq.anio = 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anio_actual,
                SUM(CASE WHEN liq.anio < 2025 THEN det_liq.valor ELSE 0 END) as total_pago_anteriores,
                'VENTA DE LOTE EN EL CEMENTERIO' AS detalle_imp
            ")
            ->get();

            

            // dd($datosPagosBomberoUrbano);

            
            $datosPagos = $datosPagoPredialUrb
            ->merge($datosPagosDesc)
            ->merge($datosPagosRecargo)
            ->merge($datosPagosInteres)
            ->merge($datosPagoPredialRust)
            ->merge($datosPagosDescRust)

          
            ->merge($datosPagosRecargoRust)
            ->merge($datosPagosInteresRust)
            ->merge($datosPagosCemMercadoMu)
            ->merge($datosPagosCemParquePlaza)
            ->merge($datosPagosCemPavimentMallaUrbana)
            ->merge($datosPagosCemrRegeneracionMalecon)
            ->merge($datosPagosBomberoUrbano)
            ->merge($datosPagosServiciosAdm)
            ->merge($datosCEMAreaRecreacional)
            ->merge($datosCEMAlcantarilladoVias)
            ->merge($datosCEMAlcantarilladoStaMartha)
            ->merge($datosPatente)
            ->merge($datosMedicinaRural)
            ->merge($datos5XAdicional)
            ->merge($datos5XCientoMuni)
            ->merge($datos15ActivosTotales)
            ->merge($datosAlcabala)
            ->merge($datosImpuestoRodaje)
            ->merge($datosUtilidad)

            ->merge($datosTasaTramiteAdministrativo)
            ->merge($datosTasaServCaracterSocial)
            ->merge($datosTasaServMant)
            ->merge($datosTasaInspecciones)
            ->merge($datosPermisoImplatacion)
            ->merge($datosPermisoConexion)
            ->merge($datosPermisoFuncionamiento)
            ->merge($datosPermisoConstruccion)
            ->merge($datosPermisoLetreros)
            ->merge($datosPermisoEspectaculo)

            ->merge($datosServiciosAdmRurales)
            ->merge($datosServiciosOOPP)
            ->merge($datosServiciosGestioRiesgo)
            ->merge($datosConstEspecMejoras)
            ->merge($datosServBD)

            ->merge($datosPagosCemAAlcantarilladoSanit)
            ->merge($datosPagosIntereMora)

            ->merge($datosPagosAlcabalasOtroCantones)
            ->merge($datosPagoSolarNoEdificado)
            ->merge($datosPagosColegioManabitas)
            ->merge($datosPagosCentroSaludPecuaria)
            // ->merge($datosPagosCemAAlcantarilladoSanit)
            ->merge($datosPagosBomberoRural)

            ->merge($datosPagoOcupacionPublica)
            ->merge($datosServicioCamales)
            ->merge($datosPagoAdjudicacionObras)
            ->merge($datosPagoServAlcant)
            ->merge($datosPagoArriendoMercadoLetras)
            ->merge($datosPagoArriendoMercadoCCMM)
            ->merge($datosPagoMultaInfraccionOrdenanza)
            ->merge($datosPagoOtrasMultas)
            ->merge($datosPagoOtrosNoEspecificado)
            ->merge($datosPagoDepositoIntermediacion)
            ->merge($datosPagoGarantiaRecibida)
            
            ->merge($datosPagoPermisoOperacion)
            ->merge($datosContratoOperacion)
            ->merge($datosPagoIncrementoGrupo)
            ->merge($datosPagoInscripcionGravamen)
            ->merge($datosPagolevantamientoGravamen)
            ->merge($datosPagoTraspasoDominio)
            ->merge($datosPagoResolHabilitacion)
            ->merge($datosPagoResolDesHabilitacion)
            ->merge($datosPagoResolCambioSocio)
            ->merge($datosPagoModifCaractVehiculo)
            ->merge($datosPagoBloqueoDesbloqueoSys)
            ->merge($datosPagoResolucionFactibilidad)
            ->merge($datosCertUnicoVehi)

            ->merge($datosPagoCertPoseeVehic)
            ->merge($datosPagoEmisionCertSalvoConducto)
            ->merge($datosPagoDuplicadoMatricula)
            ->merge($datosPagoStickerRevVehic)
            ->merge($datosPagoDuplicadoRevVehic)
            ->merge($datosPagoRecargoMatVehPart)
            ->merge($datosPagoRecargoMatVehPubli)

            ->merge($datosPagoVentaMunicipal)
            ->merge($datosPagoVentaLoteCementerio)
            
          
            // ->merge($datosUtilidad);
            // ->orderBy('orden')
            // ->orderBy('ordenlista','asc')
            // ->get();

            // ->get() // Trae los datos antes de ordenar
            
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
            dd($e);
            Log::error('ParteDiarioController => buscarPagos => mensaje => '.$e->getMessage(). ' linea => '.$e->getLine());
            return[
                'error'=>true,
                'mensaje'=>'Ocurrió un error'
            ];
            
        }
    }

    public function descargarPdf($archivo){
        try{

            $exists_destino = \Storage::disk('public')->exists($archivo);

            if($exists_destino){
                return response()->download( storage_path('app/public/'.$archivo))->deleteFileAfterSend(true);
            }else{
                dd("a");
                return back()->with(['error'=>'Ocurrió un error','estadoP'=>'danger']);
            }

        } catch (\Throwable $th) {
            dd($th);
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return back()->with(['error'=>'Ocurrió un error','estadoP'=>'danger']);
        }
    }
}